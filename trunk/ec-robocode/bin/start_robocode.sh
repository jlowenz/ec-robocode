#!/bin/sh

PARALLEL=$1

if [ -z ""{$GP_HOME} ]
then
	echo "Must set GP_HOME"
	exit
fi

export CLASSPATH="${GP_HOME}"

for jar in `find $GP_HOME/build -name \*.jar | xargs`
do
	export CLASSPATH="${CLASSPATH}:${GP_HOME}/build/${JAR}"
done
export CLASSPATH="${CLASSPATH}:${GP_HOME}/rc/classes/"
echo $CLASSPATH

i=0
while [ $i -lt $PARALLEL ]; do
    nice -n20 java -Djava.security.policy=$GP_HOME/bin/.java.policy \
        -Djava.util.logging.config.file=$GP_HOME/config/jini.logging \
	    -DGP_HOME="${GP_HOME}"  \
	    -server \
	    -Dorg.jini.rio.groups="GPRobocode"  \
	    -cp "${CLASSPATH}" -jar ${GP_HOME}/build/gp.jar -id $2$i -battle ${GP_HOME}/config/sample.battle 2> /tmp/robo_$2$i &
	i=$(($i + 1))
done
