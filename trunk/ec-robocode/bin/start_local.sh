#!/bin/sh

bin/start_gs.sh
bin/start_robocode.sh 2 ooga
bin/start_driver.sh $* &