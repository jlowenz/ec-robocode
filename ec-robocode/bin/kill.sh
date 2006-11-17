#!/bin/bash

MACHINES=$1

for i in $MACHINES; do
    krsh $i killall -1 java
done