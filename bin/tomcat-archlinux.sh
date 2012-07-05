#!/bin/bash

[ -f /etc/conf.d/tomcat ] && . /etc/conf.d/tomcat

. /etc/rc.conf
. /etc/rc.d/functions

case "$1" in
  start)
    stat_busy "Starting Tomcat"
    /opt/tomcat/bin/jsvc \
      -user $USER \
      -home /usr/lib/jvm/java-6-openjdk \
      -Dcatalina.home=/opt/tomcat \
      -Dcatalina.base=$BEEN_TOMCAT_DIR \
      -Djava.io.tmpdir=$BEEN_TOMCAT_DIR/temp \
      -wait 10 \
      -pidfile $BEEN_TOMCAT_DIR/tomcat.pid \
      -errfile $BEEN_TOMCAT_DIR/logs/catalina.log \
      $CATALINA_OPTS \
      -cp /usr/lib/jvm/java-6-openjdk/lib/tools.jar:/opt/tomcat/bin/commons-daemon.jar:/opt/tomcat/bin/bootstrap.jar \
      org.apache.catalina.startup.Bootstrap

    if [ $? -gt 0 ]; then
      stat_fail
    else
      stat_done
    fi
    ;;
  stop)
    stat_busy "Stopping Tomcat"
    /opt/tomcat/bin/jsvc \
      -stop \
      -pidfile $BEEN_TOMCAT_DIR/tomcat.pid \
      org.apache.catalina.startup.Bootstrap

    if [ $? -gt 0 ]; then
      stat_fail
    else
      rm -f $BEEN_TOMCAT_DIR/tomcat.pid	
      stat_done
    fi
    ;;
  restart)
    $0 stop
    sleep 1
    $0 start
    ;;
  *)
    echo "usage: $0 {start|stop|restart}"
esac
exit 0
