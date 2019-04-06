package com.hwx.pe.valengine.akka


object MainLocal extends App {

  Controller.main(Array.empty)
  Valengine.main(Seq("8000").toArray)

}
