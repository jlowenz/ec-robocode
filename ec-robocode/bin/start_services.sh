

set -x

java -Djava.security.policy=/home/rbowers/.java.policy	\
    -Xmx1G \
	-DGP_HOME=${GP_HOME} \
	-Dcom.sun.management.jmxremote -Dcom.sun.management.jmxremote.authenticate=false \
	-Djava.util.logging.config.file=${GP_HOME}/config/jini.logging \
     -jar ${GP_HOME}/gp/lib/jini/start.jar	\
     ${GP_HOME}/config/start-services.config
