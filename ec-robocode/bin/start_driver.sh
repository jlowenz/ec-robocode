#!/bin/sh


if [[ -e /tmp/ec_driver ]]; then
    rm -rf /tmp/ec_driver
fi

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

LOG=$HOME/ec_driver.log
java -Xmx800m \
	-Djava.security.policy=$GP_HOME/bin/.java.policy \
	-Djava.util.logging.config.file=$GP_HOME/config/jini.logging \
	-DGP_HOME="${GP_HOME}" \
	-Dorg.jini.rio.groups="GPRobocode" \
	-cp "${CLASSPATH}"  \
	com.imaginaryday.ec.main.Driver $* 1>>$LOG 2>>$LOG
