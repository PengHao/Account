APP_NAME=
NGINX_HOME=/home/nginx

export JAVA_HOME=/usr/lib/jvm/java-1.8.0-openjdk-amd64
export NGINX_CONF=/opt/nginx/nginx.conf
export CATALINA_HOME=/opt/tomcat7
export CATALINA_BASE=$APP_HOME/.default
export CATALINA_LOGS=$APP_HOME/logs/catalina
export CATALINA_OUT=$APP_HOME/logs/tomcat_stdout.log
export CATALINA_PID=$CATALINA_BASE/catalina.pid
export TOMCAT_PORT="7001"

CATALINA_OPTS="-server"
CATALINA_OPTS="${CATALINA_OPTS} -Xms4g -Xmx4g"
CATALINA_OPTS="${CATALINA_OPTS} -XX:PermSize=512m -XX:MaxPermSize=512m"
CATALINA_OPTS="${CATALINA_OPTS} -Xmn2g"
CATALINA_OPTS="${CATALINA_OPTS} -XX:MaxDirectMemorySize=1g"
CATALINA_OPTS="${CATALINA_OPTS} -XX:SurvivorRatio=10"
CATALINA_OPTS="${CATALINA_OPTS} -XX:+UseConcMarkSweepGC -XX:+UseCMSCompactAtFullCollection -XX:CMSMaxAbortablePrecleanTime=5000"
CATALINA_OPTS="${CATALINA_OPTS} -XX:+CMSClassUnloadingEnabled -XX:CMSInitiatingOccupancyFraction=80 -XX:+UseCMSInitiatingOccupancyOnly"
CATALINA_OPTS="${CATALINA_OPTS} -XX:+ExplicitGCInvokesConcurrent -Dsun.rmi.dgc.server.gcInterval=2592000000 -Dsun.rmi.dgc.client.gcInterval=2592000000"
CATALINA_OPTS="${CATALINA_OPTS} -XX:ParallelGCThreads=${CPU_COUNT}"
CATALINA_OPTS="${CATALINA_OPTS} -Xloggc:${MIDDLEWARE_LOGS}/gc.log -XX:+PrintGCDetails -XX:+PrintGCDateStamps"
CATALINA_OPTS="${CATALINA_OPTS} -XX:+HeapDumpOnOutOfMemoryError -XX:HeapDumpPath=${MIDDLEWARE_LOGS}/java.hprof"
CATALINA_OPTS="${CATALINA_OPTS} -Djava.awt.headless=true"
CATALINA_OPTS="${CATALINA_OPTS} -Dsun.net.client.defaultConnectTimeout=10000"
CATALINA_OPTS="${CATALINA_OPTS} -Dsun.net.client.defaultReadTimeout=30000"
CATALINA_OPTS="${CATALINA_OPTS} -DJM.LOG.PATH=${MIDDLEWARE_LOGS}"
CATALINA_OPTS="${CATALINA_OPTS} -DJM.SNAPSHOT.PATH=${MIDDLEWARE_SNAPSHOTS}"
CATALINA_OPTS="${CATALINA_OPTS} -Dfile.encoding=${JAVA_FILE_ENCODING}"
CATALINA_OPTS="${CATALINA_OPTS} -Dhsf.publish.delayed=true"
CATALINA_OPTS="${CATALINA_OPTS} -Xdebug -Xrunjdwp:transport=dt_socket,server=y,suspend=n,address=8000"

export CATALINA_OPTS


