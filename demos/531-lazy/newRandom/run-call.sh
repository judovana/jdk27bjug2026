#!/bin/bash
set -o pipefail
DIR="$(dirname "$(realpath "$0")")"
METHOD=".getR()" RUN=true sh $DIR/generate.sh "$1"  2>&1 | tail -n 2
if [ $? -ne 0 ] ; then
  METHOD=".getR()" RUN=true sh $DIR/generate.sh "$1" 
fi
