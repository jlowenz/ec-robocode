#!/bin/bash

LINCOLN=$1
if [[ $LINCOLN -ne 1 ]]; then
    echo "Don't forget to start lincoln and pass 1 as an argument to this script!"
    exit
fi

distribute.sh "erg pi daytona wind xon xoff" 4
distribute.sh "p1 p2 p3" 9
distribute.sh "wopr" 14
