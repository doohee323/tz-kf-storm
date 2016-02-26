#!/usr/bin/env bash

echo ### [1. run zookeeper] ############################################################################################################
cd $HOME/zookeeper-3.4.8/bin
zkServer.sh start # zkServer.sh restart

echo ### [2. run apache-kafka] ############################################################################################################
cd $HOME/kafka
bin/kafka-server-start.sh ./config/server.properties &		# bin/zookeeper-server-stop.sh
bin/kafka-topics.sh --create --topic logs --zookeeper 127.0.0.1:2181 --partitions 1 --replication-factor 1
#bin/kafka-topics.sh --delete --topic logs --zookeeper 127.0.0.1:2181
#Created topic "logs"
#bin/kafka-console-consumer.sh --topic logs --zookeeper 127.0.0.1:2181 
#bin/kafka-console-producer.sh --topic logs --broker 127.0.0.1:9092
#testaaa
#bin/kafka-topics.sh --zookeeper 127.0.0.1:2181 --list

echo ### [3. run apache-storm] ############################################################################################################
cd $HOME/apache-storm-0.10.0/bin
storm nimbus &
storm supervisor &
storm ui &
storm logviewer &

#http://127.0.0.1:8080/index.html
#ll /Users/dhong/apache-storm-0.10.0/logs
#http://127.0.0.1:8000/log?file=storm-kafka-topology-2-1456339787-worker-6700.log

echo ### [4. run apache solr] ############################################################################################################
cd $HOME/solr-5.3.1
bin/solr start # bin/solr restart
mkdir -p server/solr/collection1
bin/solr create -c collection1

#http://127.0.0.1:8983/solr/#/collection1
#http://127.0.0.1:8983/solr/#/collection1/schema-browser?field=value

exit 0

# rebuild
rm -Rf $HOME/kafka/kafka-logs/*
rm -Rf $HOME/zookeeper-3.4.8/zookeeper/*
rm -Rf $HOME/apache-storm-0.10.0/logs/*
#ps -ef | grep zookeeper
#ps -ef | grep storm
#ps -ef | grep solr
#ps -ef | grep kafka

cd $HOME/zookeeper-3.4.8/bin
zkServer.sh restart
cd $HOME/kafka
bin/zookeeper-server-stop.sh 
bin/kafka-server-start.sh ./config/server.properties &
bin/kafka-topics.sh --delete --topic logs --zookeeper 127.0.0.1:2181
bin/kafka-topics.sh --create --topic logs --zookeeper 127.0.0.1:2181 --partitions 1 --replication-factor 1
bin/kafka-topics.sh --zookeeper 127.0.0.1:2181 --list

cd $HOME
mvn clean package
storm kill TestTopology4
sleep 30

storm jar target/tz-kf-storm-0.0.1-SNAPSHOT-jar-with-dependencies.jar example4.tzkfstorm.Topology
storm list
