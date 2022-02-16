#!/bin/bash
#------------------------------------------------------------------
#
#个人账户数据报送主调入口脚本 sh ytj_run_main_zl.sh
#
#------------------------------------------------------------------
#获取当前程序所在目录
shPath=$(cd "$(dirname "$0")"; pwd)


for i in `ps -ef | grep "ytj_run_main_zl.sh" | grep -v grep | awk '{ print  $2 }'`
do
if [ $$ -ne $i ]; then
echo "kill process number: [ $i ]"
kill -9 $i;
fi
done

DBUSER=`cat $shPath/sys.ini |grep @DBUSER |awk -F '=' '{printf $2}'`
DBPASSWD=`cat $shPath/sys.ini |grep @DBPASSWD |awk -F '=' '{printf $2}'`
ASUSER=`cat $shPath/sys.ini |grep @ASUSER |awk -F '=' '{printf $2}'`
CONNSTR="${DBUSER}/${DBPASSWD}@${ASUSER}"
step=1
while [ 1 ]
do
##设置日志路径
shDate=`date +"%Y%m%d"`
logPath=${shPath}/eaas/log/${shDate}
if [ ! -d ${logPath} ];then
mkdir -p ${logPath}
fi
##设置日志文件
logFile=${logPath}/`basename $0 .sh`.log
log()
{
echo `date +"%Y-%m-%d %H:%M:%S"` $$ $1 >> $logFile
}

log "1. 获取系统日期和时间, [ 第${step}步! ]"
#echo "步骤1 "
expDate=`sqlplus -S "${CONNSTR}"<<EOF
set heading off
set echo off
set feedback off
set term off
set pagesize 0
set linesize 1000
var ret number;
select  to_char(sysdate,'yyyymmdd-hh24miss') from dual;
exit;
EOF`
#echo "步骤1"
log "    expDate[ ${expDate} ]"
if [ "${expDate}_N" != "_N" ]; then
expDate="20200331-120000"
datadate=`echo ${expDate} |awk -F '-' '{printf $1}'`
nowTime=`echo ${expDate} |awk -F '-' '{printf $2}'`
echo "datadate[ ${datadate} ]    nowTime[ ${nowTime} ]"
step=1
#echo "步骤1.1"
else
step=2
fi
log "2. check the time gt 1:00 [ ${step} ]"
if [ ${step} -eq 1 ];then
if [ ${nowTime} -gt 10000 ]; then
step=1
else
step=2
fi
fi

log "3. check today's sftptask is yes/ro [ ${step} ]"
if [ ${step} -eq 1 ];then
if [ ${datadate} -ge 20190401 ]; then
log "check the time gt 9:10 and lt 16:00 ${nowTime}"
if [ ${nowTime} -gt 91000 ] && [ ${nowTime} -lt 230000 ]; then
taskNumber=`sqlplus -S "${CONNSTR}"<<EOF
set heading off
set echo off
set feedback off
set term off
set pagesize 0
set linesize 1000
var ret number;
select count(*) from ytj_record_detail t where to_char(t.update_time,'yyyymmdd')='${datadate}';
exit;
EOF`
log "check the taskNumber lt 3 [ ${taskNumber} ]"
if [ ${taskNumber} -lt 3 ]; then

log " 开始执行: ${shPath}/sftp_put_file_zl.sh... "
log "将文件上传到一体机"
sh ${shPath}/sftp_put_file_zl.sh ${datadate}
shReturnCode=$?
if [ ${shReturnCode} -eq 0 ]; then
log "文件上传到一体机处理成功!!!shReturnCode:[${shReturnCode}]"
else
log "文件上传到一体机处理失败!!!shReturnCode:[${shReturnCode}]"
fi
log "  "
log " 开始执行: ${shPath}/swap_remote_zl.sh... "
log "让一体机开始处理..."
sh ${shPath}/swap_remote_zl.sh ${datadate} yes
shReturnCode=$?
if [ ${shReturnCode} -eq 0 ]; then
log "一体机循环处理成功!!!shReturnCode:[${shReturnCode}]"
else
log "一体机循环处理失败!!!shReturnCode:[${shReturnCode}]"
fi
fi
else
log "不在人行接收时间范围内"
fi
else
log "未到人行要求增量报送日期"
fi
fi
log "4. check today's Task is yes/ro  [ ${step} ]"
if [ ${step} -eq 1 ];then
dataTemp=`sqlplus -S "${CONNSTR}"<<EOF
set heading off
set echo off
set feedback off
set term off
set pagesize 0
set linesize 1000
var ret number;
select  count(1) from east_ytj_work_run where now_date=to_date('${datadate}','yyyymmdd') and status='0';
exit;
EOF`
log "    dataTemp[ ${dataTemp} ]"
if [ ${dataTemp} -eq 1 ]; then
step=1
else
step=2
fi
fi

log "5. if [ ${step} ] = 1 today's Task is yes now start"
if [ ${step} -eq 1 ];then
vFlag=`sqlplus -S "${CONNSTR}"<<EOF
set heading off
set echo off
set feedback off
set term off
set pagesize 0
set linesize 1000
var ret number;
select to_char(data_date,'yyyymmdd') || '|' || flag || '|' || rowid from east_ytj_work_run
where now_date=to_date('${datadate}', 'yyyymmdd')  and status='0';
exit;
EOF`
log "    vFlag[ ${vFlag} ]"
if [ "${vFlag}_N" != "_N" ];then
proc_datadate=`echo ${vFlag} |awk -F '|' '{printf $1}'`
proc_flag=`echo ${vFlag} |awk -F '|' '{printf $2}'`
rowid=`echo ${vFlag} |awk -F '|' '{printf $3}'`
echo "proc_datadate[ ${proc_datadate} ]  proc_flag[ ${proc_flag} ] rowid [ ${rowid} ]"
step=1
else
step=2
fi
fi

log "6. if [ ${step} ] = 1 now update today's Task status 1"
if [ ${step} -eq 1 ];then
upTaskTab=`sqlplus -S "${CONNSTR}"<<EOF
set heading off
set echo off
set feedback off
set term off
set pagesize 0
set linesize 1000
var ret number;
update east_ytj_work_run set status='1' where rowid='${rowid}';
commit;
exit;
EOF`
log "    upTaskTab[  ${upTaskTab} ]"
fi

log "7. if [ ${step} ] = 1 now running today's Task [ ${proc_flag} ]..."
flag=1
if [ ${step} -eq 1 ];then
if [ ${proc_flag} = "ql" ];then
flag=ql
else
flag=zl
fi
#echo "调用main函数"
sh ${shPath}/main_zl.sh ${proc_datadate} ${flag}
shReturnCode=$?
if [ ${shReturnCode} -eq 0 ]; then
sh ${shPath}/insert_ytj_log_zl.sh "ytj_run_main_zl.sh" ${proc_datadate} 6
else
sh ${shPath}/insert_ytj_log_zl.sh "ytj_run_main_zl.sh" ${proc_datadate} 7
fi
fi
log "ok!!!!"
log " "

sleep 5
done



