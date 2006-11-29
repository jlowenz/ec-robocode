#!/bin/sh

bin/start_gs.sh
bin/start_robocode.sh $1 ooga
bin/start_driver.sh &