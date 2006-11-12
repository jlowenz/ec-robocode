

set -x

java -Djava.security.policy=/home/rbowers/.java.policy	\
     -jar ${JINI_HOME}/lib/start.jar	\
     config/start-services.config
