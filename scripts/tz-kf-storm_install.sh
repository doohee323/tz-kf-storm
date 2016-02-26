#!/usr/bin/env bash

cd ..
export HOME=`pwd`
cd scripts

PATH=$PATH:.:$HOME/apache-storm-0.10.0/bin

rm -Rf $HOME/*.tgz $HOME/*.zip $HOME/*.gz $HOME/*.tar.gz
rm -Rf $HOME/zookeeper-3.4.8
rm -Rf $HOME/kafka
rm -Rf $HOME/apache-storm-0.10.0
rm -Rf $HOME/solr-5.3.1

echo ### [1. install zookeeper] ############################################################################################################
cd $HOME
wget http://apache.arvixe.com/zookeeper/stable/zookeeper-3.4.8.tar.gz
tar xvzf zookeeper-3.4.8.tar.gz
cd zookeeper-3.4.8
cp -r conf/zoo_sample.cfg conf/zoo.cfg
echo '' >> conf/zoo.cfg
echo 'local1.test.com=zookeeper1:2888:3888' >> conf/zoo.cfg
echo 'local2.test.com=zookeeper2:2888:3888' >> conf/zoo.cfg
sed -ie "s/dataDir/#dataDir/g" conf/zoo.cfg
echo 'dataDir='$HOME'/zookeeper-3.4.8/zookeeper' >> conf/zoo.cfg
mkdir -p logs

echo ### [2. install apache-kafka] ############################################################################################################
cd $HOME
wget http://apache.tt.co.kr/kafka/0.8.2.0/kafka_2.10-0.8.2.0.tgz
tar -xzf kafka_2.10-0.8.2.0.tgz
mv kafka_2.10-0.8.2.0 kafka
cd kafka
sed -ie "s/log.dirs/#log.dirs/g" config/server.properties
echo 'log.dirs='$HOME'/kafka/kafka-logs' >> config/server.properties

echo ### [3. install apache-storm] ############################################################################################################
cd $HOME
wget http://apache.arvixe.com/storm/apache-storm-0.10.0/apache-storm-0.10.0.zip
unzip apache-storm-0.10.0.zip
cd apache-storm-0.10.0
echo '' >> conf/storm.yaml
echo 'storm.zookeeper.servers:' >> conf/storm.yaml
echo '    - "local1.test.com"' >> conf/storm.yaml
echo '    - "local2.test.com"' >> conf/storm.yaml
echo 'nimbus.host: "127.0.0.1"' >> conf/storm.yaml

echo ### [4. install apache solr] ############################################################################################################
cd $HOME
curl -O http://apache.arvixe.com/lucene/solr/5.3.1/solr-5.3.1.zip
unzip solr-5.3.1.zip
cd solr-5.3.1
mkdir -p server/logs
cp -r server/solr/configsets/basic_configs/conf/ server/solr/collection1/conf
cp -r $HOME/etc/solr/schema.xml server/solr/collection1/conf/schema.xml

rm -Rf $HOME/*.tgz $HOME/*.zip $HOME/*.gz $HOME/*.tar.gz

echo #####################################################################################################################################
echo ### installation finished ###########################################################################################################
echo ### After execute 'vagrant ssh', run each of apps as follows. #######################################################################
echo #####################################################################################################################################

exit 0
