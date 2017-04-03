# Kafka-Storm-Esper case on vagrant.
==========================================================================

* case 1:  kafka, storm (tzkfstorm.case4.Topology)
* case 2:  kafka, storm, esper (tzkfstorm.case9)

## workflow
```
	- install & run: zookeeper, kafka, storm, solr
	- deploy topology to storm: TestTopology4
	- test with kafka-console-consumer.sh & kafka-console-producer.sh
	- test with java producer
	- test with logstash kafaka output
```

## - run
```
	1. installations
		vagrant up # vagrant destroy -f # vagrant reload
	
	2. run servers
		vagrant ssh nimbus.test.com
		cd /vagrant/scripts
		bash nimbus.test.com_run.sh		# run servers (solr, kafka, logstash, storm)
		
		vagrant ssh supervisor.test.com
		cd /vagrant/scripts
		bash supervisor.test.com_run.sh		# run servers (kafka, logstash, storm)	
		
	* 192.168.82.170: vagrant's ip address.
```
http://192.168.82.170:8080	# storm ui
http://192.168.82.170:8983	# solr
	
## - deploy & test
```
	vagrant ssh nimbus.test.com
	cd /vagrant/scripts
	bash nimbus.test.com_test.sh
```
http://192.168.82.170:8983/solr/#/collection1/query
 	    
	    
	    