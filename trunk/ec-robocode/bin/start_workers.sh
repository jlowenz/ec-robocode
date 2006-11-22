#!/bin/bash

LINCOLN=$1
if [[ $LINCOLN -ne 1 ]]; then
    echo "Don't forget to start lincoln and pass 1 as an argument to this script!"
    exit
fi

bin/distribute.sh "erg daytona wind xon xoff baal trinity" 4
bin/distribute.sh "p1 p2 p3" 9
bin/distribute.sh "wopr" 14
