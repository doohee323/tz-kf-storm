# Kafka-Storm-Esper case on vagrant.
==========================================================================

* case 1:  kafka, storm (tzkfstorm.case4.Topology)
* case 2:  kafka, storm, esper (tzkfstorm.case9)

# workflow
```
	- install & run: zookeeper, kafka, storm, solr
	- deploy topology to storm: TestTopology4
	- test with kafka-console-consumer.sh & kafka-console-producer.sh
	- test with java producer
	- test with logstash kafaka output
```

## - run
```
	vagrant up # vagrant destroy -f # vagrant reload
	vagrant ssh
	cd /vagrant/scripts
	bash tz-kf-storm_run.sh		# run servers (solr, kafka, logstash, storm)
	
	* 192.168.82.170: vagrant's ip address.
```
http://192.168.82.170:8080	# storm ui
http://192.168.82.170:8983	# solr
	
## - deploy & test
```
	cd /vagrant/scripts
	bash tz-kf-storm_test.sh
```
http://192.168.82.170:8983/solr/#/collection1/query
 	    
	    
	    