#!/usr/bin/env bash

#cd /vagrant
export SERVERS=/vagrant/servers # for vagrant
#export SERVERS=/Users/dhong/Documents/workspace/etc/tz-kf-storm/servers # not for vagrant
#export JAVA_HOME=/Library/Java/JavaVirtualMachines/jdk1.8.0_73.jdk/Contents/Home
PATH=$PATH:.:$SERVERS/apache-storm-0.10.0/bin

echo ### [1. run zookeeper] ############################################################################################################
cd $SERVERS/zookeeper-3.4.8/bin
zkServer.sh start # zkServer.sh restart

echo ### [2. run apache-kafka] ############################################################################################################
cd $SERVERS/kafka
sudo mkdir -p /tmp/kafka-logs
#sudo chown -Rf mac:wheel /tmp/kafka-logs
sudo chown -Rf vagrant:vagrant /tmp/kafka-logs
bin/kafka-server-start.sh ./config/server.properties &		
# bin/zookeeper-server-stop.sh
sleep 10
bin/kafka-topics.sh --create --topic logs --zookeeper 127.0.0.1:2181 --partitions 1 --replication-factor 1
#bin/kafka-topics.sh --delete --topic logs --zookeeper 127.0.0.1:2181
#Created topic "logs"
#bin/kafka-console-consumer.sh --topic logs --zookeeper 127.0.0.1:2181 
#bin/kafka-console-producer.sh --topic logs --broker 127.0.0.1:9092
#testaaa
#bin/kafka-topics.sh --zookeeper 127.0.0.1:2181 --list

echo ### [3. run apache-storm] ############################################################################################################
cd $SERVERS/apache-storm-0.10.0/bin
storm nimbus &
storm supervisor &
storm ui &
storm logviewer &

#http://127.0.0.1:8080/index.html
#ll /Users/mac/apache-storm-0.10.0/logs
#http://127.0.0.1:8000/log?file=storm-kafka-topology-2-1456339787-worker-6700.log

echo ### [4. run apache solr] ############################################################################################################
cd $SERVERS/solr-5.3.1
bin/solr start # bin/solr restart
mkdir -p server/solr/collection1
bin/solr create -c collection1

#http://127.0.0.1:8983/solr/#/collection1
#http://127.0.0.1:8983/solr/#/collection1/schema-browser?field=value

echo ### [5. run logstash] ############################################################################################################
cp $SERVERS/configs/logstash/log_list/derp.conf $SERVERS/logstash-2.2.2/log_list
$SERVERS/logstash-2.2.2/bin/logstash -f $SERVERS/logstash-2.2.2/log_list/derp.conf &

exit 0

# rebuild
rm -Rf $SERVERS/kafka/kafka-logs/*
rm -Rf $SERVERS/zookeeper-3.4.8/zookeeper/*
rm -Rf $SERVERS/apache-storm-0.10.0/logs/*
#ps -ef | grep zookeeper
#ps -ef | grep storm
#ps -ef | grep solr
#ps -ef | grep kafka

cd $SERVERS/zookeeper-3.4.8/bin
zkServer.sh restart
cd $SERVERS/kafka
bin/zookeeper-server-stop.sh 
bin/kafka-server-start.sh ./config/server.properties &
bin/kafka-topics.sh --delete --topic logs --zookeeper 127.0.0.1:2181
bin/kafka-topics.sh --create --topic logs --zookeeper 127.0.0.1:2181 --partitions 1 --replication-factor 1
bin/kafka-topics.sh --zookeeper 127.0.0.1:2181 --list

cd $SERVERS
mvn clean package
storm kill TestTopology4
sleep 30

storm jar target/tz-kf-storm-0.0.1-SNAPSHOT.jar tzkfstorm.example4.Topology
storm list
