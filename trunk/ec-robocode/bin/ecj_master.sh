#!/bin/sh

if [ -z ""$GP_HOME ]
then
	echo "Must set GP_HOME"
	exit
fi

export CLASSPATH="${GP_HOME}"
for jar in `find ${GP_HOME}/build -name \*.jar | xargs`
do
	export CLASSPATH="${CLASSPATH}:${jar}"
done
export CLASSPATH="${CLASSPATH}"
echo $CLASSPATH

blah() {
    nohup java -Djava.security.policy=${GP_HOME}/bin/.java.policy \
	 -DGP_HOME="${GP_HOME}"  \
	 -server \
	 -cp "${CLASSPATH}" -jar ${GP_HOME}/build/gp.jar >$LOG 2>&1
}

if [[ $HOME = "/" ]]; then
	HOME="/tmp"
fi

echo "Starting ECJ Master..."
LOG=$HOME/ec_driver.log
blah &
echo "return status $?"
