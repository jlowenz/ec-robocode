

set -x

java -Djava.security.policy=/home/rbowers/.java.policy	\
	-DGP_HOME=${GP_HOME} \
     -jar ${GP_HOME}/gp/lib/jini/start.jar	\
     ${GP_HOME}/config/start-services.config
