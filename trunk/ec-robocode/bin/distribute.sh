#!/bin/bash

BLAH[server]=1
echo $BLAH[server]
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

echo "GP_RSH=$GP_RSH"
echo "GP_RCP=$GP_RCP"

for m in $MACHINES; do
    echo $m
    $GP_RCP ecdist.tgz $m:/tmp
    $GP_RSH $m ". ~/.bashrc; cd /tmp; tar xzf ecdist.tgz; cd ecdist; chmod +x bin/start_robocode.sh; export GP_HOME=/tmp/ecdist; sh bin/start_robocode.sh $NUMPROCS $m$USER" &
done
