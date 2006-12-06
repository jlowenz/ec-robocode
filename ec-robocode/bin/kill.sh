#!/bin/bash

MACHINES=$1

echo "killer.sh $*"

if [[ -z ""$GP_RSH ]]; then
    GP_RCP=krcp
    GP_RSH=krsh
fi

for i in $MACHINES; do
    echo $i
    $GP_RSH $i killall java
done
