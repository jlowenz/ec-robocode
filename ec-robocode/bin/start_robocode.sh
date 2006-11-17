#!/bin/sh

PARALLEL=$1

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
export CLASSPATH="${CLASSPATH}:${GP_HOME}/rc/classes/"
echo $CLASSPATH

i=0
while [ $i -lt $PARALLEL ]; do
    java -Djava.security.policy=/home/rbowers/.java.policy \
	    -DGP_HOME="${GP_HOME}"  \
	    -server \
	    -Dorg.jini.rio.groups="GPRobocode"  \
	    -cp "${CLASSPATH}" -jar ${GP_HOME}/build/gp.jar -id $2$i -battle ${GP_HOME}/config/sample.battle 2> /tmp/robo_$2$i &
	i=$(($i + 1))
done
