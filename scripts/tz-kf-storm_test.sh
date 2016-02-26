#!/usr/bin/env bash

#export HOME=/vagrant/servers # for vagrant
#export HOME=`pwd` # not for vagrant
PATH=$PATH:.:$HOME/apache-storm-0.10.0/bin

echo ### [deploy test topology] ############################################################################################################
cd /vagrant
mvn clean package

storm jar target/tz-kf-storm-0.0.1-SNAPSHOT.jar tzkfstorm.example4.Topology
#storm deactivate TestTopology4
#storm kill TestTopology4
#storm list

# http://127.0.0.1:8080/index.html

echo ### [test topology] ############################################################################################################
# 1) with  kafka-console-producer.sh
cd $HOME/kafka
bin/kafka-console-consumer.sh --zookeeper 127.0.0.1:2181 --topic logs --from-beginning

bin/kafka-console-producer.sh --topic logs --broker 127.0.0.1:9092
#{id:1,timestamp:1441411710347,hostname:ruleset33.xdn.com,client_ip:10.115.74.54}

#solr -> http://127.0.0.1:8983/solr/#/collection1/query

# 2) with java
cd /vagrant
vi pom.xml
comment like this
<!-- 			<scope>provided</scope> -->
cd /vagrant
mvn clean package

java -cp target/tz-kf-storm-0.0.1-SNAPSHOT.jar tzkfstorm.example4.kafka.ProducerTestKafka

# remove data / logs
rm -Rf $HOME/apache-storm-0.10.0/logs/*
rm -Rf $HOME/solr-5.3.1/server/solr/collection1/data/*
rm -Rf $HOME/zookeeper-3.4.8/zookeeper
rm -Rf /tmp/kafka-logs/*

