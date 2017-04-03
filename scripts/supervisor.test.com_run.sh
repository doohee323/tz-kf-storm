#!/usr/bin/env bash

#cd /vagrant
export SERVERS=/home/vagrant/servers # for vagrant
#export SERVERS=/Users/dhong/Documents/workspace/etc/tz-kf-storm/servers # not for vagrant
#export JAVA_HOME=/Library/Java/JavaVirtualMachines/jdk1.8.0_73.jdk/Contents/Home
PATH=$PATH:.:$SERVERS/apache-storm-0.10.2/bin

echo ### [1. run zookeeper] ############################################################################################################
cd $SERVERS/zookeeper-3.4.8/bin
zkServer.sh start # zkServer.sh restart

echo ### [2. run apache-kafka] ############################################################################################################
cd $SERVERS/kafka
sudo mkdir -p /tmp/kafka-logs
#sudo chown -Rf mac:wheel /tmp/kafka-logs
sudo chown -Rf vagrant:vagrant /tmp/kafka-logs

sudo mkdir -p /tmp/zookeeper
sudo chown -Rf vagrant:vagrant /tmp/zookeeper
sudo echo 2 > /tmp/zookeeper/myid

sed -ie "s/broker.id=0/broker.id=2/g" ./config/server.properties
sed -ie "s/zookeeper.connect/#zookeeper.connect/g" ./config/server.properties
echo 'zookeeper.connect=nimbus.test.com:2181,supervisor.test.com:2181' >> ./config/server.properties

bin/kafka-server-start.sh ./config/server.properties &		
# bin/zookeeper-server-stop.sh
sleep 10
bin/kafka-topics.sh --create --topic logs --zookeeper nimbus.test.com:2181 --partitions 1 --replication-factor 1
#bin/kafka-topics.sh --delete --topic logs --zookeeper nimbus.test.com:2181
#Created topic "logs"
#bin/kafka-console-consumer.sh --topic logs --zookeeper nimbus.test.com:2181 
#bin/kafka-console-producer.sh --topic logs --broker nimbus.test.com:9092
#testaaa
#bin/kafka-topics.sh --zookeeper nimbus.test.com:2181 --list

echo ### [3. run apache-storm] ############################################################################################################
cd $SERVERS/apache-storm-0.10.2/bin
#storm nimbus &
storm supervisor &
#storm ui &
#storm logviewer &

#http://127.0.0.1:8080/index.html
#ll /Users/mac/apache-storm-0.10.2/logs
#http://127.0.0.1:8000/log?file=storm-kafka-topology-2-1456339787-worker-6700.log

echo ### [4. run logstash] ############################################################################################################
cp /vagrant/configs/logstash/log_list/derp.conf $SERVERS/logstash-2.2.2/log_list
$SERVERS/logstash-2.2.2/bin/logstash -f $SERVERS/logstash-2.2.2/log_list/derp.conf &

exit 0

