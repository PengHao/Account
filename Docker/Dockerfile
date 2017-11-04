FROM wolfpeng/tomcat7:1.0.0
MAINTAINER WolfPeng penghao345@gmail.com

ENV APP_NAME=account
ENV APP_HOME=/home/admin/$APP_NAME
ENV APP_PATH=$APP_HOME/target/$APP_NAME.war
#ENV CATALINA_BASE=$APP_HOME/.default
ENV CATALINA_LOGS=$APP_HOME/logs/catalina
ENV CATALINA_OUT=$APP_HOME/logs/tomcat_stdout.log
#ENV CATALINA_PID=$CATALINA_BASE/catalina.pid
RUN mkdir -p $APP_HOME
#RUN mkdir -p $CATALINA_BASE
RUN mkdir -p CATALINA_LOGS

ADD $APP_NAME.war  $APP_HOME/target/

VOLUME /home/admin/logs $APP_HOME/logs

#WORKDIR $APP_HOME/bin
ENTRYPOINT /home/admin/start.sh
