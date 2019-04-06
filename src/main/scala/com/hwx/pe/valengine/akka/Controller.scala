package com.hwx.pe.valengine.akka

import language.postfixOps
import akka.actor.{Actor, ActorLogging, ActorRef, ActorSystem, Props, Terminated}
import akka.cluster.Cluster
import akka.cluster.ClusterEvent._
import scala.collection.JavaConversions._


class Controller extends Actor with ActorLogging {

  var engines = IndexedSeq.empty[ActorRef]
  var reqCounter, jobCounter = 0

  val cluster = Cluster(context.system)

  override def preStart(): Unit = {
    cluster.subscribe(self, initialStateMode = InitialStateAsEvents, classOf[MemberEvent], classOf[UnreachableMember])
  }

  override def postStop(): Unit = cluster.unsubscribe(self)


  def receive = {

    case req: ValuationRequest if engines.isEmpty =>
      sender() ! ValuationFailed("Engines unavailable, try again later", req)

    case req: ValuationRequest =>
      if(engines.isEmpty){
        log.info(ValuationFailed("Engines unavailable", req).toString)
      } else {
        reqCounter += 1
        engines(reqCounter % engines.size) forward req
      }

    case job: ValuationJobRequest =>
      if(engines.isEmpty){
        log.info(ValuationJobFailed("Engines unavailable", job).toString)
      } else {
        jobCounter += 1
        log.info(s"Forwarding Job ${job.jobId} [${job.metric} | ${job.numTrades}] ...")
        engines(jobCounter % engines.size) forward job
      }


    case resp: ValuationResponse =>
      log.info("Computed {} [{}]: {}", resp.metric, resp.tradeId, resp.result)

    case resp: ValuationJobResponse =>
      log.info(s"Completed Job ${resp.jobId} [${resp.metric}|${resp.numTrades}] = ${resp.aggResult} in ${resp.duration} ms")

    case EngineRegistration if !engines.contains(sender()) =>
      context watch sender()
      engines = engines :+ sender()

    case Terminated(a) =>
      engines = engines.filterNot(_ == a)

    case MemberUp(member) => log.info(s"Cluster member up: ${member.address}")
    case UnreachableMember(member) => log.warning(s"Cluster member unreachable: ${member.address}")
    case MemberRemoved(member, previousStatus) => log.info(s"Cluster member removed: ${member.address}")
    case MemberExited(member) => log.info(s"Cluster member exited: ${member.address}")
    case _: MemberEvent =>
  }

}

object Controller {

  def main(args: Array[String]): Unit =
  {

    val config = ClusterConfig.config
    val system = ActorSystem(config.getString(Properties.VALENGINE_CLUSTER_NAME), config)
    system.log.info("Configured seed nodes: " + config.getStringList(Properties.AKKA_CLUSTER_SEED_NODES).mkString(", "))
    val controller = system.actorOf(Props[Controller], name = "ctrl")

    val httpService = new HttpService(system, controller, config)
    httpService.start()

    Runtime.getRuntime.addShutdownHook(new Thread(new Runnable {
      def run(): Unit = {
        httpService.stop()
      }
    }))

  }

}
