package com.hwx.pe.valengine.akka

import akka.actor.{Actor, ActorLogging, ActorSystem, Props, RootActorPath}
import akka.cluster.{Cluster, Member, MemberStatus}
import akka.cluster.ClusterEvent.{CurrentClusterState, MemberUp}
import akka.pattern.pipe
import com.typesafe.config.ConfigFactory

import scala.concurrent.Future

class Valengine extends Actor with ActorLogging {

  import context.dispatcher

  val cluster = Cluster(context.system)

  // subscribe to cluster changes
  override def preStart(): Unit = cluster.subscribe(self, classOf[MemberUp])
  override def postStop(): Unit = cluster.unsubscribe(self)

  def receive = {

    case ValuationRequest(tradeId, metric) =>
      log.info("Computing {} for {} ...", metric, tradeId)
      Future(computeMetric(metric)) map { result =>
          log.debug("{} [{}] = {}", metric, tradeId, result)
          ValuationResponse(tradeId, metric, result)
      } pipeTo sender()

    case ValuationJobRequest(jobId, metric, numTrades) =>
      log.info("Processing job {} [{}|{}] ...", jobId, metric, numTrades)
      val start = System.currentTimeMillis()
      Future(computeAggMetric(metric, numTrades)) map { result =>
        //writeToO3(metric.name().toLowerCase(), jobId, (numTrades * result))
        val now = System.currentTimeMillis;
        writeToO3(metric, jobId, result, numTrades, (now - start))
        log.info(s"Wrote [${jobId}_${now} -> ($numTrades * $result)] to bucket ${metric.name().toLowerCase()} in ... ${now - start} ms")
        ValuationJobResponse(jobId, metric, numTrades, result, System.currentTimeMillis() - start)
      } pipeTo sender()

    case state: CurrentClusterState =>
      state.members.filter(_.status == MemberStatus.Up) foreach register

    case MemberUp(m) => register(m)
  }

  def computeMetric(metric: ValMetric): Double = {
    metric match {
      case ValMetric.FwdRate   => Pricer.computeFRASpot()
      case ValMetric.NPV       => Pricer.computeSwapNPV()
      case ValMetric.OptionPV  => Pricer.computeEquityOptionNPV()
      case _ => 0.0d
    }
  }

  def computeAggMetric(metric: ValMetric, numTrades: Int): Double = {
    var agg = 0.0d
    for (i <- 1 to numTrades){
      agg += computeMetric(metric)
    }
    if(numTrades == 0.0d) numTrades else agg / numTrades
  }

  def register(member: Member): Unit =
    if (member.hasRole("ctrl")) {
      context.actorSelection(RootActorPath(member.address) / "user" / "ctrl") ! EngineRegistration
    }

  def writeToO3(metric: String, batchId: String, result: Double): Unit = {
    Valengine.o3Sink.write(metric, batchId, String.valueOf(result))
  }

  def writeToO3(metric: ValMetric, batchId: String, result: Double, numTrades: Int, valTime: Long): Unit = {
    val trade = new Trade(batchId, metric, result, numTrades, valTime);
    Valengine.o3Sink.write(trade)
  }

}



object Valengine {

  var o3Sink: O3Sink = null

  def main(args: Array[String]): Unit =
  {
    if(args.length != 3) System.exit(1)

    val port = args(0)
    val omHost = args(1)
    var o3Bucket = args(2)

    val config = ConfigFactory.parseString(s"${Properties.AKKA_REMOTE_PORT}=$port")
                  .withFallback(ConfigFactory.parseString(s"${Properties.AKKA_CLUSTER_ROLES} = [valeng]"))
                  .withFallback(ClusterConfig.config)

    val system = ActorSystem(config.getString(Properties.VALENGINE_CLUSTER_NAME), config)
    system.actorOf(Props[Valengine], name = "valeng")

    o3Sink = O3Sink.instance(omHost, o3Bucket)
    o3Sink.info()
    
    Runtime.getRuntime.addShutdownHook(new Thread(new Runnable {
      def run(): Unit = {
        o3Sink.shutdown()
      }
    }))

  }

}
