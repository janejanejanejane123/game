#!/bin/bash

cd `dirname $0`
BIN_DIR=`pwd`
cd ..
DEPLOY_DIR=`pwd`

TEMP_DIRECTORY=$DEPLOY_DIR/upload_tmp
if [ ! -d $TEMP_DIRECTORY ]; then
    mkdir $TEMP_DIRECTORY
fi

SERVER_NAME=`basename \`pwd\``
if [ -z "$SERVER_NAME" ]; then
    SERVER_NAME=`hostname`
fi

CONF_DIR=$DEPLOY_DIR/config

# SERVER_PORT=`sed '/server.port/!d;s/.*=//' config/application.properties | tr -d '\r'`
SERVER_PORT=`sed -nr '/port: [0-9]+/ s/.*port: +([0-9]+).*/\1/p' config/application.yml`

PIDS=`ps -ef | grep java | grep "$CONF_DIR" |awk '{print $2}'`
if [ "$1" = "status" ]; then
    if [ -n "$PIDS" ]; then
        echo "The $SERVER_NAME is running...!"
        echo "PID: $PIDS"
        exit 0
    else
        echo "The $SERVER_NAME is stopped"
        exit 0
    fi
fi

if [ -n "$PIDS" ]; then
    echo "ERROR: The $SERVER_NAME already started!"
    echo "PID: $PIDS"
    exit 1
fi

if [ -n "$SERVER_PORT" ]; then
    SERVER_PORT_COUNT=`netstat -tln | grep $SERVER_PORT | wc -l`
    if [ $SERVER_PORT_COUNT -gt 0 ]; then
        echo "ERROR: The $SERVER_NAME port $SERVER_PORT already used!"
        exit 1
    fi
fi

LOGS_FILE=$CONF_DIR/logback.xml
LOGS_DIR=$DEPLOY_DIR/logs
if [ ! -d $LOGS_DIR ]; then
    mkdir $LOGS_DIR
fi
STDOUT_FILE=$LOGS_DIR/stdout.log

LIB_DIR=$DEPLOY_DIR/lib
LIB_JARS=`ls $LIB_DIR|grep .jar|awk '{print "'$LIB_DIR'/"$0}'|tr "\n" ":"`
ALL_LIB_JARS=$LIB_JARS

JAVA_OPTS=" -Djava.security.egd=file:/dev/./urandom -Djava.awt.headless=true -Djava.net.preferIPv4Stack=true -Djava.io.tmpdir=$TEMP_DIRECTORY "
LOGS_OPTS=" -Dlog4j2.contextSelector=org.apache.logging.log4j.core.async.AsyncLoggerContextSelector -DAsyncLogger.RingBufferSize=262188 -DAsyncLoggerConfig.RingBufferSize=262188 -Dlog4j2.AsyncQueueFullPolicy=Discard "
JAVA_DEBUG_OPTS=""
if [ "$1" = "debug" ]; then
    JAVA_DEBUG_OPTS=" -Ddebug -agentlib:jdwp=transport=dt_socket,server=y,suspend=n,address=5005 "
fi
JAVA_JMX_OPTS=""
if [ "$1" = "jmx" ]; then
    JAVA_JMX_OPTS=" -Dcom.sun.management.jmxremote.port=1099 -Dcom.sun.management.jmxremote.ssl=false -Dcom.sun.management.jmxremote.authenticate=false "
fi
JAVA_MEM_OPTS=""
BITS=`java -version 2>&1 | grep -i 64-bit`
if [ "$java_env" == "prod" ] || [ "$java_env" == "beta" ]; then
    JAVA_MEM_OPTS=" -server -Xms1g -Xmx2g -XX:PermSize=64m -Xss256k -XX:+DisableExplicitGC -XX:+UseConcMarkSweepGC -XX:+CMSParallelRemarkEnabled -XX:+UseCMSCompactAtFullCollection -XX:LargePageSizeInBytes=128m -XX:+UseFastAccessorMethods -XX:+UseCMSInitiatingOccupancyOnly -XX:CMSInitiatingOccupancyFraction=70 -XX:+UnlockExperimentalVMOptions -XX:+UseCGroupMemoryLimitForHeap "
elif [ "$java_env" == "test" ]; then
    JAVA_MEM_OPTS=" -server -Xms1g -Xmx1g -XX:PermSize=32m -Xss256k -XX:+DisableExplicitGC -XX:+UseConcMarkSweepGC -XX:+CMSParallelRemarkEnabled -XX:+UseCMSCompactAtFullCollection -XX:LargePageSizeInBytes=128m -XX:+UseFastAccessorMethods -XX:+UseCMSInitiatingOccupancyOnly -XX:CMSInitiatingOccupancyFraction=70 -XX:+UnlockExperimentalVMOptions -XX:+UseCGroupMemoryLimitForHeap "
else
    JAVA_MEM_OPTS=" -server -Xms256m -Xmx512m -XX:PermSize=32m -Xss256k -XX:+DisableExplicitGC -XX:+UseConcMarkSweepGC -XX:+CMSParallelRemarkEnabled -XX:+UseCMSCompactAtFullCollection -XX:LargePageSizeInBytes=128m -XX:+UseFastAccessorMethods -XX:+UseCMSInitiatingOccupancyOnly -XX:CMSInitiatingOccupancyFraction=70 -XX:+UnlockExperimentalVMOptions -XX:+UseCGroupMemoryLimitForHeap "
fi
DUMP_DATE=`date +%Y%m%d%H%M%S`
HEAP_DUMP_OPTS=" -XX:+HeapDumpOnOutOfMemoryError -XX:HeapDumpPath=$DEPLOY_DIR/logs/heap_dump_$DUMP_DATE.log "

echo -e "Starting the $SERVER_NAME ...\c"

JAVA_PRINT_GC_OPTS=""
if [ "$1" = "printGc" ] || [ "$2" == "printGc" ];then
    JAVA_DEBUG_OPTS=" -Xloggc:$LOGS_DIR/gc.log -XX:+PrintGC -XX:+PrintGCDetails -XX:+PrintHeapAtGC -XX:+PrintTenuringDistribution -XX:+PrintGCTimeStamps -XX:+PrintGCDateStamps -XX:+PrintGCApplicationStoppedTime "
fi


if [ ! -f $BIN_DIR/libdecrypt.jar ]; then
    nohup java $JAVA_OPTS $LOGS_OPTS $JAVA_PRINT_GC_OPTS $JAVA_MEM_OPTS $JAVA_DEBUG_OPTS $JAVA_JMX_OPTS $HEAP_DUMP_OPTS $JAVA_AGENT -classpath $CONF_DIR:$LIB_JARS com.ruoyi.RuoYiApplication > $STDOUT_FILE 2>&1 &
else
    nohup java $JAVA_OPTS $LOGS_OPTS $JAVA_PRINT_GC_OPTS $JAVA_MEM_OPTS $JAVA_DEBUG_OPTS $JAVA_JMX_OPTS $HEAP_DUMP_OPTS $JAVA_AGENT -classpath $CONF_DIR -agentpath:$BIN_DIR/libdecrypt.jar=show -cp $CONF_DIR:$ALL_LIB_JARS com.ruoyi.RuoYiApplication > $STDOUT_FILE 2>&1 &
fi

COUNT=0
while [ $COUNT -lt 1 ]; do
    echo -e ".\c"
    sleep 1
    if [ -n "$SERVER_PORT" ]; then
        COUNT=`netstat -an | grep $SERVER_PORT | wc -l`
    else
    	COUNT=`ps -ef | grep java | grep "$DEPLOY_DIR" | awk '{print $2}' | wc -l`
    fi
    if [ $COUNT -gt 0 ]; then
        break
    fi
done

echo "OK!"
PIDS=`ps -ef | grep java | grep "$DEPLOY_DIR" | awk '{print $2}'`
echo "PID: $PIDS"
echo "STDOUT: $STDOUT_FILE"
