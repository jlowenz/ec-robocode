#!/bin/bash
if [ -z ""{$GP_GROUP} ]
then
	echo "Setting group to GPRobocode"
	GP_GROUP="GPRobocode"
fi

nice -n20 $GS_HOME/bin/gsServer.sh "/./space?groups=${GP_GROUP}" "$GP_HOME/build/gp.jar" "-Dcom.gs.jini_lus.groups=${GP_GROUP}" &
