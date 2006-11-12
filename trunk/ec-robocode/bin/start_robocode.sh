#!/bin/sh

if [[ -z ""{$GP_HOME} ]] 
then
	echo "Must set GP_HOME"
	exit
fi

export CLASSPATH="${GP_HOME}"

for JAR in 	gp.jar rc.jar jsk-dl.jar rio.jar jscience.jar \
			functionalj-0.8-jdk15.jar jsk-policy.jar jsk-lib.jar \
			jsk-platform.jar jsk-resources.jar junit-4.1.jar
do
	export CLASSPATH="${CLASSPATH}:${GP_HOME}/build/${JAR}"
done
echo $CLASSPATH

java -Djava.security.policy=/home/rbowers/.java.policy \
	-DGP_HOME="${GP_HOME}"  \
	-Dorg.jini.rio.groups="GPRobocode"  \
	-cp "${CLASSPATH}" -jar ${GP_HOME}/build/gp.jar -battle ${GP_HOME}/config/sample.battle
