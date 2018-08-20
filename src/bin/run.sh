#!/bin/sh

BASE_DIR=$(pwd)
LIB="${BASE_DIR}/lib/"
JAVA_OPTS=" -Xmsm -Xmx2048m -XX:PermSize=64m -XX:MaxPermSize=512m -server -Dfile.encoding=UTF-8"
START_CLASS="net.oliver.Application"

echo ${LIB}

for libfile in ${LIB}/*.jar ; do
if [ -f $libfile ] ; then
    CLASSPATH=$libfile:${CLASSPATH}
fi
done

for libfile in ${BASE_DIR}/*.jar ; do
if [ -f $libfile ] ; then
    CLASSPATH=$libfile:${CLASSPATH}
fi
done

CLASSPATH=${BASE_DIR}:${CLASSPATH}
nohup "java" ${JAVA_OPTS} -server -cp ${CLASSPATH} ${START_CLASS} &