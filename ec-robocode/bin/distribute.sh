#!/bin/bash

# copy files to target machines
MACHINES=$1
NUMPROCS=$2

if [[ -z ""$GP_RSH ]]; then
    GP_RCP=krcp
    GP_RSH=krsh
fi

if [[ -z ""$2 ]]; then
    NUMPROCS=4
fi

if [ ! -d ecdist ]; then
    mkdir ecdist
else
    rm -rf ecdist
    mkdir ecdist
fi

cp -r bin build config ecdist
tar czf ecdist.tgz ecdist

for m in $MACHINES; do
    echo $m
    $GP_RCP ecdist.tgz $m:/tmp
    $GP_RSH $m "cd /tmp; tar xzf ecdist.tgz; cd ecdist; chmod +x bin/start_robocode.sh; GP_HOME=/tmp/ecdist nice -n20 bin/start_robocode.sh $NUMPROCS $m$USER" &
done
