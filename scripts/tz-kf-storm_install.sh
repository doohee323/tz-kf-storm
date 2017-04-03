#!/usr/bin/env bash

cd ..
export SERVERS=/home/vagrant/servers # for vagrant
#export SERVERS=/Users/dhong/Documents/workspace/etc/tz-kf-storm/servers # not for vagrant
cd scripts

export PROJ_DIR=/home/vagrant
export SRC_DIR=/vagrant/resources
export SERVERS=/home/vagrant/servers # for vagrant

echo '' >> $PROJ_DIR/.bashrc
echo 'export PATH=$PATH:.' >> $PROJ_DIR/.bashrc
echo 'export PROJ_DIR='$PROJ_DIR >> $PROJ_DIR/.bashrc
echo 'export SRC_DIR='$SRC_DIR >> $PROJ_DIR/.bashrc
echo 'export SERVERS='$SERVERS >> $PROJ_DIR/.bashrc
source $PROJ_DIR/.bashrc

# change hosts
echo '' >> /etc/hosts
echo '# for vm' >> /etc/hosts
echo '192.168.82.170	nimbus.test.com' >> /etc/hosts
echo '192.168.82.171	supervisor.test.com' >> /etc/hosts

ssh-keygen -t dsa -P '' -f ~/.ssh/id_dsa
cat ~/.ssh/id_dsa.pub >> ~/.ssh/authorized_keys
echo '' >> /etc/ssh/ssh_config
echo '    ForwardX11 no' >> /etc/ssh/ssh_config
echo '    StrictHostKeyChecking no' >> /etc/ssh/ssh_config
sudo service ssh restart
# sudo launchctl unload  /System/Library/LaunchDaemons/ssh.plist 
# sudo launchctl load -w /System/Library/LaunchDaemons/ssh.plist
# ssh localhost

PATH=$PATH:.:$SERVERS/apache-storm-0.10.2/bin

#rm -Rf $SERVERS/*.tgz $SERVERS/*.zip $SERVERS/*.gz $SERVERS/*.tar.gz
rm -Rf $SERVERS/zookeeper-3.4.8
rm -Rf $SERVERS/kafka
rm -Rf $SERVERS/apache-storm-0.10.2
rm -Rf $SERVERS/solr-5.3.1
rm -Rf $SERVERS/logstash-2.2.2

echo ### [1. install zookeeper] ############################################################################################################
cd $SERVERS
wget http://apache.claz.org/zookeeper/zookeeper-3.4.8/zookeeper-3.4.8.tar.gz
tar xvzf zookeeper-3.4.8.tar.gz
cd zookeeper-3.4.8
cp -r conf/zoo_sample.cfg conf/zoo.cfg
echo '' >> conf/zoo.cfg
echo 'nimbus.test.com=zookeeper1:2888:3888' >> conf/zoo.cfg
echo 'supervisor.test.com=zookeeper2:2888:3888' >> conf/zoo.cfg
sed -ie "s/dataDir/#dataDir/g" conf/zoo.cfg
echo 'dataDir='$SERVERS'/zookeeper-3.4.8/zookeeper' >> conf/zoo.cfg
mkdir -p logs

echo ### [2. install apache-kafka] ############################################################################################################
cd $SERVERS
wget http://apache.tt.co.kr/kafka/0.8.2.0/kafka_2.10-0.8.2.0.tgz
tar -xzf kafka_2.10-0.8.2.0.tgz
mv kafka_2.10-0.8.2.0 kafka

echo ### [3. install apache-storm] ############################################################################################################
cd $SERVERS
wget http://apache.claz.org/storm/apache-storm-0.10.2/apache-storm-0.10.2.tar.gz
tar -xzf apache-storm-0.10.2.tar.gz
cd apache-storm-0.10.2
echo '' >> conf/storm.yaml
echo 'storm.zookeeper.servers:' >> conf/storm.yaml
echo '    - "nimbus.test.com"' >> conf/storm.yaml
echo '    - "supervisor.test.com"' >> conf/storm.yaml
echo 'nimbus.host: "nimbus.test.com"' >> conf/storm.yaml

echo ### [4. install logstash] ############################################################################################################
cd $SERVERS
wget https://download.elastic.co/logstash/logstash/logstash-2.2.2.tar.gz
tar xvfz logstash-2.2.2.tar.gz
mkdir $SERVERS/logstash-2.2.2/patterns
mkdir $SERVERS/logstash-2.2.2/log_list
#cp $SERVERS/configs/logstash/log_list/derp.conf $SERVERS/logstash-2.2.2/log_list
#$SERVERS/logstash-2.2.2/bin/logstash -f $SERVERS/logstash-2.2.2/log_list/derp.conf &

echo #####################################################################################################################################
echo ### installation finished ###########################################################################################################
echo ### After execute 'vagrant ssh', run each of apps as follows. #######################################################################
echo #####################################################################################################################################

