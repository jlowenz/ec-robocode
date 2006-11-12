#!/bin/sh


export CLASSPATH=".:build/gp.jar"

for JAR in 	rc.jar jsk-dl.jar rio.jar jscience.jar \
			functionalj-0.8-jdk15.jar jsk-policy.jar jsk-lib.jar \
			jsk-platform.jar jsk-resources.jar junit-4.1.jar
do
	export CLASSPATH="${CLASSPATH}:build/${JAR}"
done
echo $CLASSPATH

java -Djava.security.policy=/home/rbowers/.java.policy -Dorg.jini.rio.groups="GPRobocode" -cp "${CLASSPATH}" -jar build/gp.jar -battle config/sample.battle
