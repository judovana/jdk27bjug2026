#!/bin/bash
DIR="$(dirname "$(realpath "$0")")"
cd $DIR
if [[ "$1" == "clean" ]]; then
    rm -rf */build*
    exit
fi
if [[ "$1" == "print" ]]; then
    for x in  `find -maxdepth 1  -mindepth 1  -type d | sort` ; do 
      echo $x/Slave.java
      cat $x/Slave.java      
    done
    exit
fi
for x in  `find -maxdepth 1  -mindepth 1  -type d | sort` ; do 
  echo "$x";
  METHOD=""  sh generate.sh $x 2>&1 | tail -n 2
done
# we want to have it grouped
for x in  `find -maxdepth 1  -mindepth 1  -type d | sort` ; do 
  echo "$x with getter";
  METHOD=".getR()"  sh generate.sh $x 2>&1 | tail -n 2
done

