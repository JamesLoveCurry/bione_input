#!/bin/bash
#
#***************************************************
#
#脚本名称 : insert_ytj_log_zl.sh
#脚本功能 : 将数据入库
#创建日期 : 20190305
#作    者 : zhurd
#版    本 : v_1.0
#
#***************************************************

#获取当前程序所在目录
shPath=$(cd "$(dirname "$0")"; pwd)
shDate=`date +"%Y%m%d"`
logPath=${shPath}/eaas/log/${shDate}

if [ ! -d ${logPath} ];then
mkdir -p ${logPath}
fi

logFile=${logPath}/`basename $0 .sh`.log
log()
{
msg=$1
echo `date +"%Y-%m-%d %H:%M:%S"` $$ $1 >> $logFile
}


DBUSER=`cat $shPath/sys.ini |grep @DBUSER |awk -F '=' '{printf $2}'`
DBPASSWD=`cat $shPath/sys.ini |grep @DBPASSWD |awk -F '=' '{printf $2}'`
ASUSER=`cat $shPath/sys.ini |grep @ASUSER |awk -F '=' '{printf $2}'`
CONNSTR="${DBUSER}/${DBPASSWD}@${ASUSER}"


sysTime=$1
dataDate=$2
sname=$3
runStep=$4
status=$5
activity=$6

if [ "${activity}_N" == "1_N" ];then
insertData=`sqlplus -S "${CONNSTR}"<<EOF
set heading off;
set echo off;
set feedback off;
set term off;
set pagesize 0;
set linesize 1000;
insert into ytj_log(sys_no, data_date, sname, run_step, start_time)
values('${sysTime}', '${dataDate}', '${sname}', '${runStep}', sysdate);
commit;
exit;
EOF`
else
insertData=`sqlplus -S "${CONNSTR}"<<EOF
set heading off;
set echo off;
set feedback off;
set term off;
set pagesize 0;
set linesize 1000;
update ytj_log set status='${status}', end_time=sysdate where sys_no='${sysTime}';
commit;
exit;
EOF`
fi
log "insertData:[ ${insertData} ]"
