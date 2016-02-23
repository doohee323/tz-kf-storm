This is a Kafka-Storm-Esper example on vagrant.
=====================================

1. Make storm-kafka environment.

	1.1 install storm
	wget http://apache.arvixe.com/storm/apache-storm-0.10.0/apache-storm-0.10.0.zip
	unzip apache-storm-0.10.0.zip
	
	cd /Users/dhong/Documents/workspace/etc/tz-kf-storm/apache-storm-0.10.0
	vi config/storm.yaml
	storm.zookeeper.servers:
	    - "local1.test.com"
	    - "local2.test.com"
	nimbus.host: "127.0.0.1"
	
	1.2. install zookeeper
	wget http://apache.arvixe.com/zookeeper/stable/zookeeper-3.4.8.tar.gz
	tar xvzf zookeeper-3.4.8.tar.gz
	
	cd /Users/dhong/Documents/workspace/etc/tz-kf-storm/zookeeper-3.4.8
	vi config/zoo.cfg
	
	local1.test.com=zookeeper1:2888:3888
	local2.test.com=zookeeper2:2888:3888

	vi /etc/hosts
	127.0.0.1	local1.test.com
	127.0.0.1	local2.test.com
	
	1.3. set path
	export PATH=$PATH:/Users/dhong/Documents/workspace/etc/tz-kf-storm/apache-storm-0.10.0/bin:/Users/dhong/Documents/workspace/etc/tz-kf-storm/zookeeper-3.4.8/bin:/Users/dhong/Documents/workspace/etc/tz-kf-storm/kafka/bin
	
	1.4. run storm 
	storm nimbus
	storm supervisor
	storm ui
	
	1.5. run zookeper
	zkServer.sh start

	http://127.0.0.1:8080/index.html

2. build 
	
	mvn clean package
	
	4) kafka - storm(multiple bolts) example
		/tz-storm/src/main/java/example4/tz-storm/TestTopology4.java
		4.1 run zookeeper & kafka-server(broker)
		4.2 java  PopulateKafkaTopic
	
	5) storm + trident(unique data) + esper example
		/tz-storm/src/main/java/example5/tz-storm/TestTopology5.java

3. deploy jar to storm
	#storm jar /Users/dhong/Documents/workspace/etc/tz-kf-storm/target/tz-storm-0.0.1-SNAPSHOT-jar-with-dependencies.jar com.terry.storm.hellostorm.HelloTopology HelloTopology 
	
	storm jar /Users/dhong/Documents/workspace/etc/tz-kf-storm/target/tz-storm-0.0.1-SNAPSHOT-jar-with-dependencies.jar example4.tzstorm.TestTopology4 TestTopology4
	storm deactivate TestTopology4
	storm kill TestTopology4
	storm list
	
	storm jar /Users/dhong/Documents/workspace/etc/tz-kf-storm/target/tz-storm-0.0.1-SNAPSHOT-jar-with-dependencies.jar example5.tzstorm.TestTopology5 TestTopology5
	 
4. test
	Now you'll need to put some sentences into the sentence topic.
	You can either do it manually with:
	    $KAFKA_BIN/kafka-console-producer.sh --broker-list localhost:9092 --topic sentences
	    (and now type a bunch of sentences in)
	or use the example code I provide that puts in a bunch of sentences over a few minutes.
	    java -cp target/kstorm-1.0-SNAPSHOT-jar-with-dependencies.jar quux00.wordcount.kafka.PopulateKafkaSentenceTopic

	    
	    
# kafka install test
wget http://apache.tt.co.kr/kafka/0.8.2.0/kafka_2.10-0.8.2.0.tgz
tar -xzf kafka_2.10-0.8.2.0.tgz
mv kafka_2.10-0.8.2.0 kafka
cd kafka/bin

cd /Users/dhong/Documents/workspace/etc/tz-kf-storm/kafka/bin
1. run zookeeper & kafka-server(broker)
./zookeeper-server-start.sh ../config/zookeeper.properties &
./kafka-server-start.sh ../config/server.properties &

2. create topic
./kafka-topics.sh --create --zookeeper localhost:2181 --replication-factor 1 --partitions 1 --topic logs
./kafka-topics.sh --create --zookeeper localhost:2181 --replication-factor 1 --partitions 1 --topic logs1

server.properties
delete.topic.enable=true
./kafka-topics.sh --delete --zookeeper localhost:2181 --delete --topic logs1

./kafka-topics.sh --list --zookeeper localhost:2181

3. make producer
./kafka-console-producer.sh --broker-list localhost:9092 --topic logs

4. make consumer
./kafka-console-consumer.sh --zookeeper localhost:2181 --topic logs --from-beginning

5. typing "hello" on producer console

	    
	    
	    
	    
	    
	    
	    
	    
	    
	    
	    
	    
	    
	    
	    