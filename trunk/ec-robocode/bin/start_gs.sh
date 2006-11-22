#!/bin/bash

nice -n20 $GS_HOME/bin/gsServer.sh "/./space?groups=GPRobocode" "$GP_HOME/build/gp.jar" "-Dcom.gs.jini_lus.groups=GPRobocode" &