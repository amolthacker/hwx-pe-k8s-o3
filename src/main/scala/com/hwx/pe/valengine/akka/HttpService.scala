package com.hwx.pe.valengine.akka

import akka.actor.{ActorRef, ActorSystem}
import akka.http.scaladsl.Http
import akka.http.scaladsl.model.StatusCodes
import akka.http.scaladsl.server.Directives._
import akka.stream.ActorMaterializer
import com.typesafe.config.Config

import scala.concurrent.{ExecutionContext, Future}


class HttpService(actorSystem: ActorSystem, ctrlActor: ActorRef, config: Config) {

  var bindingFuture: Future[Http.ServerBinding] = _

  implicit val system = actorSystem
  implicit val materializer     = ActorMaterializer()
  implicit val ec:ExecutionContext = system.dispatcher

  def start(): Unit =
  {
    val route =
      path("valQuery") {
        get {
          parameters('metric, 'numTrades.as[Int]) { (metric, numTrades) =>
            // submit job
            ctrlActor ! ValuationJobRequest(java.util.UUID.randomUUID().toString,
                                            ValMetric.valueOf(metric), numTrades)
            complete((StatusCodes.Accepted, "Job submitted\n"))
          }
        }
      }

    val hostname = config.getString(Properties.VALENGINE_HOST)
    val httpPort = config.getInt(Properties.VALENGINE_HTTP_PORT)
    bindingFuture = Http().bindAndHandle(route, hostname, httpPort)
    system.log.info(s"Controller HttpService [http://$hostname:$httpPort/] started")
  }

  def stop(): Unit =
  {
    system.log.info("Shutting down Controller HttpService ...")
    bindingFuture
      .flatMap(_.unbind())
      .onComplete(_ => actorSystem.terminate())
  }


}
