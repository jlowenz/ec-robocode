#!/bin/bash

PARALLEL=$1

if [ -z ""{$GP_HOME} ]
then
	echo "Must set GP_HOME"
	exit
fi

echo "Home is $HOME"

if [ -z ""$JAVA_HOME ]
then
	echo "JAVA_HOME is not set properly: $JAVA_HOME"
	echo "trying to source bashrc..."
	. $HOME/.bashrc
	if [ -z ""$JAVA_HOME ] 
	then
		echo "Nope, didn't work, trying path on system: $HOSTNAME"
		JAVA=`which java`
	fi
fi

export CLASSPATH="${GP_HOME}"

for jar in `find $GP_HOME/build -name \*.jar | xargs`
do
	export CLASSPATH="${CLASSPATH}:${jar}"
done
export CLASSPATH="${CLASSPATH}"
echo $CLASSPATH

if [ -z ""$JAVA ]; then
    JAVA=$JAVA_HOME/bin/java
fi

i=0
while [ $i -lt $PARALLEL ]; do
    nice -n20 $JAVA -Djava.security.policy=$GP_HOME/bin/.java.policy \
        -Djava.util.logging.config.file=$GP_HOME/config/jini.logging \
	    -DGP_HOME="${GP_HOME}"  \
	    -server \
	    -Dorg.jini.rio.groups="GPRobocode"  \
	    -cp "${CLASSPATH}" -jar ${GP_HOME}/build/gp.jar -id $2$i -battle ${GP_HOME}/config/sample.battle 1>/dev/null 2>/dev/null &
	i=$(($i + 1))
done
