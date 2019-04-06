package com.hwx.pe.valengine.akka

import com.typesafe.config.{Config, ConfigFactory, ConfigValueFactory}


object ClusterConfig {

  lazy val config = loadConfig()

  def loadConfig(): Config = {

    def getHostLocalAddress: Option[String] = {
      import java.net.NetworkInterface
      import scala.collection.JavaConversions._

      NetworkInterface.getNetworkInterfaces
        .find(_.getName equals "eth0")
        .flatMap { interface =>
          interface.getInetAddresses.find(_.isSiteLocalAddress).map(_.getHostAddress)
        }
    }

    def getSeedNodes(config: Config): Array[String] = {
      if (config.hasPath(Properties.VALENGINE_SEED_NODES)) {
        config.getString(Properties.VALENGINE_SEED_NODES).split(",").map(_.trim)
      } else {
        Array.empty
      }
    }

    def formatSeedNodesConfig(clusterName: String, seedNodeAddresses: Array[String], seedNodePort: String,
                              defaultSeedNodeAddress: String): String = {
      if (seedNodeAddresses.length > 0) {
        seedNodeAddresses.map { address =>
          s"""${Properties.AKKA_CLUSTER_SEED_NODES} += "akka.tcp://$clusterName@$address:$seedNodePort""""
        }.mkString("\n")
      } else {
        s"""${Properties.AKKA_CLUSTER_SEED_NODES} = ["akka.tcp://$clusterName@$defaultSeedNodeAddress:$seedNodePort"]"""
      }
    }

    val config = ConfigFactory.load()
    val clusterName = config.getString(Properties.VALENGINE_CLUSTER_NAME)
    val seedPort = config.getString(Properties.VALENGINE_SEED_PORT)

    val host = if (config.getString(Properties.VALENGINE_HOST) == "eth0-address-or-localhost") {
      getHostLocalAddress.getOrElse("127.0.0.1")
    } else {
      config.getString(Properties.VALENGINE_HOST)
    }

    ConfigFactory.parseString(formatSeedNodesConfig(clusterName, getSeedNodes(config), seedPort, host))
      .withValue(Properties.VALENGINE_HOST, ConfigValueFactory.fromAnyRef(host))
      .withValue(Properties.AKKA_REMOTE_HOST, ConfigValueFactory.fromAnyRef(host))
      .withFallback(ConfigFactory.parseString(s"${Properties.AKKA_CLUSTER_ROLES} = [ctrl]"))
      .withFallback(config)
      .resolve()
  }
}