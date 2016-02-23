#!/bin/bash
#colors
export red='\033[0;31m'
export green='\033[0;32m'
export NC='\033[0m' # No Color

set -x 

PGNAME=$(basename $0)
PGDIR=$(cd $(dirname $0); pwd)

#####################################
# CHANGE YOUR PROJ NAME & DESC HERE #
#####################################
PROJ_NAME="tz-storm"
PROJ_DESC="FortiDirector storm"

# get version from pom.xml
VERSION=$(grep -m 1 "<version>" $PGDIR/pom.xml | sed 's/.*<version>//;s/<\/version>.*//' | tail -1)

# jar file using artificat id and version - with all dependencies packaged into one jar file
JARFILE="target/${PROJ_NAME}-$VERSION.jar"

echo "making Vagrant VM"

function makeVagrant() {
	vagrant destroy -f
	vagrant up
}

function execMaven() {
	mvn clean package -DskipTests=true
	echo $VERSION > target/version.ini
}

###############################
# Main 
###############################

execMaven
sleep 1
makeVagrant

exit 0
