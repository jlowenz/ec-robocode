JAVA_HOME=/home/rbowers/Applications/jdk1.5.0_08/jre
JINI_HOME=/home/rbowers/Projects/jini+rio/jini2_1/lib/
START_CONFIG=config/start-act-blitz.config

$JAVA_HOME/bin/java -Djava.security.policy=policy/policy.all -jar $JINI_HOME/start.jar $START_CONFIG
