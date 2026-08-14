#!/bin/bash
set -euo pipefail
DIR="$(dirname "$(realpath "$0")")"
cd $DIR

if [ "$#" -ne 1 ]; then
    echo "One argument expected:"
    find -maxdepth 1  -mindepth 1  -type d
    exit 1
fi
if [[ "$1" == "" ]]; then
    echo "argument must be non empty"
    exit 1
fi
r=0;
find -maxdepth 1  -mindepth 1  -type d | grep  -qe "${1}" || r=$?
if [[ $r -ne 0 ]]; then
    echo "argument be one of: "
    find -maxdepth 1  -mindepth 1  -type d
    exit 1
fi
set -x

targetName="${1}"
#METHOD=".getR()"
if [ "x${METHOD:-}" == "x" ] ; then
  METHOD=""
  target=$DIR/$targetName/build
else
  target=$DIR/$targetName/build-call
fi
srcs=$target/srcs
clss=$target/classes
master=$DIR/Master.java
slave=$DIR/$targetName/Slave.java
max=5000
if [ ! "${RUN:-}" == true ] ; then
  rm -rf $target ;
  mkdir $target;
  mkdir $srcs;
  set +x
  for x in `seq  1 $max` ; do 
     cat $slave | sed "s/Slave/Slave$x/g" >$srcs/Slave${x}.java
  done
  set -x
  cat $master | grep delimiter -B 100 > $srcs/Master.java
  set +x
  for x in `seq  1 $max` ; do 
     echo "new Slave$x()$METHOD;" >>$srcs/Master.java
  done
  set -x
  cat $master | grep delimiter -A 100 >> $srcs/Master.java
  mkdir $clss;
  /usr/lib/jvm/java-latest-openjdk/bin/javac  --enable-preview --release 26 -d $clss $srcs/*
fi
/usr/lib/jvm/java-latest-openjdk/bin/java -cp `readlink -m $clss`  --enable-preview Master
