#!/bin/bash

# copy files to target machines
MACHINES=$1
NUMPROCS=$2

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
    krcp ecdist.tgz $m:/tmp
    krsh $m "cd /tmp; tar xzf ecdist.tgz; cd ecdist; chmod +x bin/start_robocode.sh; source ~/.bashrc; GP_HOME=/tmp/ecdist nice -n20 bin/start_robocode.sh $NUMPROCS $m$USER" &
done