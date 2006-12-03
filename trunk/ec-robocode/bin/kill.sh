#!/bin/bash

MACHINES=$1

echo "killer.sh $*"

for i in $MACHINES; do
    $GP_RSH $i killall -1 java
done
