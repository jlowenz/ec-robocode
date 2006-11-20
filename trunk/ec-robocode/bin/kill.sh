#!/bin/bash

MACHINES=$1

echo "killer.sh $*"

for i in $MACHINES; do
    krsh $i killall -1 java
done