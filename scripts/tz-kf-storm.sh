#!/usr/bin/env bash

# change hosts
echo '' >> /etc/hosts
echo '# for vm' >> /etc/hosts
echo '127.0.0.1	local1.test.com' >> /etc/hosts
echo '127.0.0.1	local2.test.com' >> /etc/hosts

echo "Reading config...." >&2
source /vagrant/setup.rc

apt-get -y -q update 
apt-get install software-properties-common python-software-properties -y
add-apt-repository ppa:webupd8team/java -y 
apt-get -y -q update 
echo oracle-java8-installer shared/accepted-oracle-license-v1-1 select true | /usr/bin/debconf-set-selections 
apt-get -y -q install oracle-java8-installer 
apt-get purge openjdk* -y
apt-get install oracle-java8-set-default
apt-get install wget curl unzip -y
export JAVA_HOME=/usr/lib/jvm/java-8-oracle

apt-get install maven -y

echo '' >> $SERVERS/.bashrc
echo 'export SERVERS=/vagrant/servers' >> $SERVERS/.bashrc
echo 'export PATH=$PATH:.:$SERVERS/apache-storm-0.10.0/bin' >> $SERVERS/.bashrc
echo 'export JAVA_HOME='$JAVA_HOME >> $SERVERS/.bashrc
echo 'export HADOOP_PREFIX=/home/vagrant/hadoop-2.7.2' >> $SERVERS/.bashrc

export SERVERS=/vagrant/servers
cd /vagrant/scripts
./tz-kf-storm_install.sh

chown -Rf vagrant:vagrant $SERVERS/zookeeper-3.4.8
chown -Rf vagrant:vagrant $SERVERS/kafka solr-5.3.1
chown -Rf vagrant:vagrant $SERVERS/apache-storm-0.10.0
chown -Rf vagrant:vagrant $SERVERS/solr-5.3.1

exit 0

cd /vagrant/scripts
su vagrant
./tz-kf-storm_run.sh

# follow tz-kf-storm_test.sh guide


