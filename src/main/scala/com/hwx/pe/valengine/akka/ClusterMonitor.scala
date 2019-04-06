package com.hwx.pe.valengine.akka

import akka.actor.{Actor, ActorLogging, ActorSystem, Props}
import akka.cluster.Cluster
import akka.cluster.ClusterEvent._
import scala.collection.JavaConversions._


class ClusterMonitor extends Actor with ActorLogging {

  val cluster = Cluster(context.system)

  override def preStart(): Unit = {
    cluster.subscribe(self, initialStateMode = InitialStateAsEvents, classOf[MemberEvent], classOf[UnreachableMember])
  }

  override def postStop(): Unit = cluster.unsubscribe(self)

  def receive = {
    case MemberUp(member) => log.info(s"Cluster member up: ${member.address}")
    case UnreachableMember(member) => log.warning(s"Cluster member unreachable: ${member.address}")
    case MemberRemoved(member, previousStatus) => log.info(s"Cluster member removed: ${member.address}")
    case MemberExited(member) => log.info(s"Cluster member exited: ${member.address}")
    case _: MemberEvent =>
  }

}

object ClusterMonitor {
  def main(args: Array[String]): Unit =
  {
    val config = ClusterConfig.config
    val system = ActorSystem(config.getString(Properties.VALENGINE_CLUSTER_NAME), config)
    system.log.info("Configured seed nodes: " + config.getStringList(Properties.AKKA_CLUSTER_SEED_NODES).mkString(", "))
    system.actorOf(Props[ClusterMonitor], "cluster-monitor")
  }
}
