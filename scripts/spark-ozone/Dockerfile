FROM amolthacker/spark:3.0.0

ADD core-site.xml /opt/hadoop/conf/core-site.xml
ADD core-site.xml /opt/spark/conf/core-site.xml

ADD ozone-site.xml /opt/hadoop/conf/ozone-site.xml
ADD ozone-site.xml /opt/spark/conf/ozone-site.xml

ENV HADOOP_CONF_DIR=/opt/hadoop/conf
ENV SPARK_EXTRA_CLASSPATH=/opt/hadoop/conf

ADD hadoop-ozone-filesystem-lib-current-0.4.0-SNAPSHOT.jar /opt/hadoop-ozone-filesystem-lib.jar
ADD hadoop-ozone-filesystem-lib-current-0.4.0-SNAPSHOT.jar /opt/spark/jars/hadoop-ozone-filesystem-lib.jar