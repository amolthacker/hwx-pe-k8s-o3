package com.hwx.pe.valengine.akka


object Properties {

  val AKKA_REMOTE_HOST        = "akka.remote.netty.tcp.hostname"
  val AKKA_REMOTE_PORT        = "akka.remote.netty.tcp.port"
  val AKKA_CLUSTER_SEED_NODES = "akka.cluster.seed-nodes"
  val AKKA_CLUSTER_ROLES      = "akka.cluster.roles"

  val VALENGINE_HOST          = "valengine.host"
  val VALENGINE_CLUSTER_NAME  = "valengine.cluster-name"
  val VALENGINE_SEED_NODES    = "valengine.seed-nodes"
  val VALENGINE_SEED_PORT     = "valengine.seed-port"
  val VALENGINE_HTTP_PORT     = "valengine.http-port"

}
