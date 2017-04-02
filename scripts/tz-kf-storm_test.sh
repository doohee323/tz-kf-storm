#!/usr/bin/env bash

export PROJ_DIR=/home/vagrant
export SRC_DIR=/vagrant/resources
export SERVERS=/vagrant/servers # for vagrant
#export SERVERS=/Users/dhong/Documents/workspace/etc/tz-kf-storm/servers # not for vagrant
PATH=$PATH:.:$SERVERS/apache-storm-0.10.0/bin

echo ### [deploy test topology] ############################################################################################################
cd /vagrant
# cd /Users/dhong/Documents/workspace/etc/tz-kf-storm
#vi pom.xml
#	<scope>provided</scope>
mvn clean package

storm jar target/tz-kf-storm-0.0.1-SNAPSHOT.jar tzkfstorm.case4.Topology
#storm deactivate TestTopology4
#storm kill TestTopology4
#storm list
storm jar target/tz-kf-storm-0.0.1-SNAPSHOT.jar tzkfstorm.case7.Topology

# http://127.0.0.1:8080/index.html

echo ### [test topology] ############################################################################################################
# 1) with  kafka-console-producer.sh
cd $SERVERS/kafka
bin/kafka-console-consumer.sh --zookeeper 127.0.0.1:2181 --topic logs --from-beginning

bin/kafka-console-producer.sh --topic logs --broker 127.0.0.1:9092
#{id:1,timestamp:1441411710347,hostname:ruleset33.xdn.com,client_ip:10.115.74.54}

#solr -> http://127.0.0.1:8983/solr/#/collection1/query

# 2) with java producer
cd /vagrant
vi pom.xml
comment like this
<!-- 			<scope>provided</scope> -->
cd /vagrant
mvn clean package

java -cp target/tz-kf-storm-0.0.1-SNAPSHOT.jar tzkfstorm.case4.kafka.ProducerTestKafka

# 3) with logstash producer
mkdir -p $PROJ_DIR/data
cp $SRC_DIR/data/stats-2016-01-22.log $PROJ_DIR/data

# remove data / logs
rm -Rf $SERVERS/apache-storm-0.10.0/logs/*
rm -Rf $SERVERS/solr-5.3.1/server/solr/collection1/data/*
rm -Rf $SERVERS/zookeeper-3.4.8/zookeeper
rm -Rf /tmp/kafka-logs/*

