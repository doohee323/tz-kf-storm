# Kafka-Storm-Esper example on vagrant.
==========================================================================

* case 1:  kafka, storm (tzkfstorm.example4)
* case 2:  kafka, storm, esper (tzkfstorm.example9)

# workflow
```
	- install & run: zookeeper, kafka, storm, solr
	- deploy topology to storm: TestTopology4
	- test with kafka-console-consumer.sh & kafka-console-producer.sh
	- test with java producer
```

## - Requirements
vagrant box add precise64 http://files.vagrantup.com/precise64.box
	
## - run
```
	vagrant up # vagrant destroy -f # vagrant reload
	vagrant ssh
	cd /vagrant/scripts
	su vagrant
	./tz-kf-storm_run.sh
	
	* 192.168.82.150: vagrant's ip address.
```
http://192.168.82.150:8080
http://192.168.82.150:8983
	
## - deploy & test
```
	follow ~/scripts/tz-kf-storm_test.sh
```
http://192.168.82.150:8983/solr/#/collection1/query
 	    
	    
	    