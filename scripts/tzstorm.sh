#!/usr/bin/env bash

# Get root up in here
sudo su

set -x

# change hosts
echo '' >> /etc/hosts
echo '# for vm' >> /etc/hosts
echo '192.168.82.150 tz-storm.test.com' >> /etc/hosts
echo '192.168.82.152 tz-storm2.test.com' >> /etc/hosts

echo "Reading config...." >&2
source /vagrant/setup.rc

PROJ_NAME=tz-storm
HOME=/home/vagrant
mkdir -p $HOME/$PROJ_NAME
cp /vagrant/target/version.ini $HOME/$PROJ_NAME
export VERSION=$(cat $HOME/$PROJ_NAME/version.ini)

#apt-get update
#apt-get install software-properties-common python-software-properties -y
#add-apt-repository ppa:openjdk-r/ppa -y

apt-get update
apt-get install wget curl unzip -y
#apt-get install openjdk-8-jdk -y
apt-get install openjdk-7-jdk -y

echo '' >> $HOME/.bashrc
echo 'export PATH=$PATH:.:$HOME/apache-storm-0.9.5/bin:$HOME/zookeeper-3.4.6/bin:$HOME/logstash-1.5.3/bin' >> $HOME/.bashrc
echo 'export PROJ_NAME='$PROJ_NAME >> $HOME/.bashrc
echo 'export VERSION='$VERSION >> $HOME/.bashrc

PATH=$PATH:.:$HOME/apache-storm-0.9.5/bin:$HOME/zookeeper-3.4.6/bin:$HOME/logstash-1.5.3/bin

### [install zookeeper] ############################################################################################################
cd $HOME
wget http://apache.arvixe.com/zookeeper/stable/zookeeper-3.4.6.tar.gz
tar xvzf zookeeper-3.4.6.tar.gz
cd zookeeper-3.4.6
cp conf/zoo_sample.cfg conf/zoo.cfg
echo '' >> conf/zoo.cfg
echo 'tz-storm.test.com=zookeeper1:2888:3888' >> conf/zoo.cfg
echo 'tz-storm2.test.com=zookeeper2:2888:3888' >> conf/zoo.cfg

cp /vagrant/etc/init/zookeeper.conf /etc/init
mkdir -p logs
zkServer.sh start &

### [install apache-storm] ############################################################################################################
cd $HOME
wget http://apache.arvixe.com/storm/apache-storm-0.9.5/apache-storm-0.9.5.zip
unzip apache-storm-0.9.5.zip
cd apache-storm-0.9.5
echo '' >> conf/storm.yaml
echo 'storm.zookeeper.servers:' >> conf/storm.yaml
echo '    - "tz-storm.test.com"' >> conf/storm.yaml
echo '    - "tz-storm2.test.com"' >> conf/storm.yaml
echo 'nimbus.host: "tz-storm.test.com"' >> conf/storm.yaml

cp /vagrant/etc/init/storm.conf /etc/init
storm nimbus &
storm supervisor &
storm ui &

# http://tz-storm.test.com:8080

### [install logstash] ############################################################################################################
cd $HOME
wget https://download.elastic.co/logstash/logstash/logstash-1.5.3.tar.gz
tar xvfz logstash-1.5.3.tar.gz
cd logstash-1.5.3
mkdir patterns
mkdir log_list
cp /vagrant/etc/logstash/patterns/$PROJ_NAME patterns
cp /vagrant/etc/logstash/log_list/$PROJ_NAME.conf log_list

# https://www.digitalocean.com/community/tutorials/how-to-map-user-location-with-geoip-and-elk-elasticsearch-logstash-and-kibana
curl -O "http://geolite.maxmind.com/download/geoip/database/GeoLiteCity.dat.gz"
gunzip GeoLiteCity.dat.gz
mkdir -p logs

cp /vagrant/etc/init/logstash.conf /etc/init
logstash -f log_list/$PROJ_NAME.conf -t &
#service logstash restart

### [launch example app] ############################################################################################################
cd $HOME
cp /vagrant/target/$PROJ_NAME-$VERSION.jar $HOME/$PROJ_NAME

storm jar $HOME/$PROJ_NAME/$PROJ_NAME-$VERSION.jar example.tzstorm.TestTopology TestTopology_tz-storm

#storm list
#storm deactivate TestTopology_tz-storm
#storm kill TestTopology_tz-storm


