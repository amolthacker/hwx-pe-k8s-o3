FROM amolthacker/quantlib-java

ENTRYPOINT ["/usr/bin/java", "-jar", "/hwx-pe/compute/compute-engine-akka-0.1.0-ve.jar"]

ADD target/compute-engine-akka-0.1.0-ve.jar /hwx-pe/compute/compute-engine-akka-0.1.0-ve.jar