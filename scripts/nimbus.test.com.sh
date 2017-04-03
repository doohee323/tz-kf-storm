#!/usr/bin/env bash

# change hosts
echo '' >> /etc/hosts
echo '# for vm' >> /etc/hosts
echo '192.168.82.170	nimbus.test.com' >> /etc/hosts
echo '192.168.82.171	supervisor.test.com' >> /etc/hosts

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

export PROJ_DIR=/home/vagrant
export SERVERS=/home/vagrant/servers

echo '' >> $PROJ_DIR/.bashrc
echo 'export SERVERS=/home/vagrant/servers' >> $PROJ_DIR/.bashrc
echo 'export PATH=$PATH:.:$SERVERS/apache-storm-0.10.2/bin' >> $PROJ_DIR/.bashrc
echo 'export JAVA_HOME='$JAVA_HOME >> $PROJ_DIR/.bashrc
echo 'export HADOOP_PREFIX=/home/vagrant/hadoop-2.7.2' >> $PROJ_DIR/.bashrc

cd /vagrant/scripts
bash tz-kf-storm_install.sh

echo ### [install apache solr] ############################################################################################################
cd $SERVERS
wget http://archive.apache.org/dist/lucene/solr/5.3.1/solr-5.3.1.zip
unzip solr-5.3.1.zip
cd solr-5.3.1
mkdir -p server/logs
mkdir -p server/solr/collection1
rm -Rf server/solr/collection1/conf
cp -r server/solr/configsets/basic_configs/conf/ server/solr/collection1/conf
cp -r $SERVERS/configs/solr/schema.xml server/solr/collection1/conf/schema.xml

#rm -Rf $SERVERS/*.tgz $SERVERS/*.zip $SERVERS/*.gz $SERVERS/*.tar.gz

chown -Rf vagrant:vagrant $SERVERS/zookeeper-3.4.8
chown -Rf vagrant:vagrant $SERVERS/kafka solr-5.3.1
chown -Rf vagrant:vagrant $SERVERS/apache-storm-0.10.2
chown -Rf vagrant:vagrant $SERVERS/solr-5.3.1

exit 0

cd /vagrant/scripts
bash nimbus.test.com_run.sh

# follow tz-kf-storm_test.sh guide


