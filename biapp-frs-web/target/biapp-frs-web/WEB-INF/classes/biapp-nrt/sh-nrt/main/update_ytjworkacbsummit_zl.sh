#!/bin/bash

#获取当前程序所在目录
shPath=$(cd "$(dirname "$0")"; pwd)

shDate=$1
workStatus=$2
workRemark=$3
workRowid=$4


#设置日志文件路径
logPath=${shPath}/eaas/log/${shDate}
if [ ! -d ${logPath} ];then
	mkdir -p ${logPath}
fi
logFile=${logPath}/`basename $0 .sh`.log
log()
{
	msg=$1
	echo `date +"%Y-%m-%d %H:%M:%S"` $$ $1 >> $logFile
	echo `date +"%Y-%m-%d %H:%M:%S"` $$ $1 
}

DBUSER=`cat $shPath/sys.ini |grep @DBUSER |awk -F '=' '{printf $2}'`
DBPASSWD=`cat $shPath/sys.ini |grep @DBPASSWD |awk -F '=' '{printf $2}'`
ASUSER=`cat $shPath/sys.ini |grep @ASUSER |awk -F '=' '{printf $2}'`
CONNSTR="${DBUSER}/${DBPASSWD}@${ASUSER}"

log "shDate    : [${shDate}]"
log "workStatus: [${workStatus}]"
log "workRemark: [${workRemark}]"
log "workRowid : [${workRowid}]"

upTaskTab=`sqlplus -S "${CONNSTR}"<<EOF
                       set heading off
                       set echo off
                       set feedback off
                       set term off
                       set pagesize 0
                       set linesize 1000
                       var ret number;
                       update ytj_work_acbsummit set status='${workStatus}', remark='${workRemark}' where rowid='${workRowid}';
                       commit;
                       exit;
EOF`
log "upTaskTab[  ${upTaskTab} ]"

exit 0
