#!/bin/sh

PARALLEL=$1

if [ -z ""$GP_HOME ]
then
	echo "Must set GP_HOME"
	exit
fi

if [ -z ""$GP_GROUP ]
then
	echo "Setting group to GPRobocode"
	GP_GROUP="GPRobocode"
fi

export CLASSPATH="${GP_HOME}"

for jar in `find ${GP_HOME}/build -name \*.jar | xargs`
do
	export CLASSPATH="${CLASSPATH}:${jar}"
done
export CLASSPATH="${CLASSPATH}"
echo $CLASSPATH

blah() {
    nohup nice -n20 java -Djava.security.policy=${GP_HOME}/bin/.java.policy \
         -Djava.util.logging.config.file=${GP_HOME}/config/jini.logging \
	 -DGP_HOME="${GP_HOME}"  \
	 -server \
	 -Dorg.jini.rio.groups="${GP_GROUP}"  \
	 -cp "${CLASSPATH}" -jar ${GP_HOME}/build/gp.jar -id $2$i -battle ${GP_HOME}/config/sample.battle >$NAME 2>&1
}

if [[ $HOME = "/" ]]; then
	HOME="/tmp"
fi

i=0
while [ $i -lt $PARALLEL ]; do
    echo "starting on $HOSTNAME..."
    NAME=$HOME/robo$2$i.log
    blah &
    echo "return status $?"
    i=$(($i + 1))
done


