This is a Kafka-Storm-Esper example on vagrant.
=====================================

case 1) 
	- install & run: zookeeper, kafka, storm, solr
	- deploy topology to storm: TestTopology4
	- test with kafka-console-consumer.sh & kafka-console-producer.sh
	- test with java producer
	
- required
- run
	vagrant up # vagrant destroy -f # vagrant reload
	vagrant ssh
	cd /vagrant/scripts
	su vagrant
	./tz-kf-storm_run.sh
	
	http://192.168.82.150:8080
	http://192.168.82.150:8983
	
- deploy & test
	follow ~/scripts/tz-kf-storm_test.sh
	
	http://192.168.82.150:8983/solr/#/collection1/query
 	    
	    
	    