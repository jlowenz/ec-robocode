#!/bin/sh

PARALLEL=$1

if [ -z ""{$GP_HOME} ]
then
	echo "Must set GP_HOME"
	exit
fi

echo "Home is $HOME"
echo "GP_HOME is ${GP_HOME}"

export CLASSPATH="${GP_HOME}"

for jar in `find ${GP_HOME}/build -name \*.jar | xargs`
do
	export CLASSPATH="${CLASSPATH}:${jar}"
done
export CLASSPATH="${CLASSPATH}"
#echo $CLASSPATH

i=0
while [ $i -lt $PARALLEL ]; do
    echo "starting on $HOSTNAME..."
    nice -n20 java -Djava.security.policy=${GP_HOME}/bin/.java.policy \
        -Djava.util.logging.config.file=${GP_HOME}/config/jini.logging \
	    -DGP_HOME="${GP_HOME}"  \
	    -server \
	    -Dorg.jini.rio.groups="GPRobocode"  \
	    -cp "${CLASSPATH}" -jar ${GP_HOME}/build/gp.jar -id $2$i -battle ${GP_HOME}/config/sample.battle &
	i=$(($i + 1))
done
