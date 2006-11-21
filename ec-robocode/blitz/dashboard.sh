SPACE_NAME=Blitz_JavaSpace

JAVA_HOME=/home/rbowers/Applications/jdk1.5.0_08/jre
JINI_LIB=/home/rbowers/Projects/jini+rio/jini2_1/lib/

CP=$JINI_LIB/jsk-lib.jar:lib/dashboard.jar:lib/stats.jar:$JINI_LIB/jsk-platform.jar:$JINI_LIB/sun-util.jar
POLICY=-Djava.security.policy=policy/policy.all

$JAVA_HOME/bin/java $POLICY -cp $CP org.dancres.blitz.tools.dash.StartDashBoard $SPACE_NAME
