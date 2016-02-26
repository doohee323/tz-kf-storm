#!/usr/bin/env bash

PATH=$PATH:.:$HOME/apache-storm-0.10.0/bin

echo ### [deploy test topology] ############################################################################################################
#cd /vagrant
cd $HOME
mvn clean package

storm jar target/tz-kf-storm-0.0.1-SNAPSHOT-jar-with-dependencies.jar example4.tzkfstorm.Topology
#storm deactivate TestTopology4
#storm kill TestTopology4
#storm list

# http://127.0.0.1:8080/index.html

echo ### [test topology] ############################################################################################################
# 1) with  kafka-console-producer.sh
cd $HOME/kafka
bin/kafka-console-consumer.sh --zookeeper 127.0.0.1:2181 --topic logs --from-beginning

bin/kafka-console-producer.sh --topic logs --broker 127.0.0.1:9092
solr id:1 value:solr_message
solr id:2 value:solr_message
hdfs this message goes to hdfs
mongo id:1 value:mongodb_message

#solr -> http://127.0.0.1:8983/solr/#/collection1/query

# 2) with java
cd /vagrant
vi pom.xml
comment like this
<!-- 			<scope>provided</scope> -->
cd /vagrant
mvn clean package

java -cp target/tz-kf-storm-0.0.1-SNAPSHOT.jar example4.tzkfstorm.kafka.ProducerTestKafka solr
java -cp target/tz-kf-storm-0.0.1-SNAPSHOT.jar example4.tzkfstorm.kafka.ProducerTestKafka hdfs


