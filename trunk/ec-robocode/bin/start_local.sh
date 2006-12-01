#!/bin/sh

if [[ -z ""$NUM_WORKERS ]]; then
    NUM_WORKERS=2
fi

bin/start_gs.sh
bin/start_robocode.sh $NUM_WORKERS ooga
bin/start_driver.sh $* &