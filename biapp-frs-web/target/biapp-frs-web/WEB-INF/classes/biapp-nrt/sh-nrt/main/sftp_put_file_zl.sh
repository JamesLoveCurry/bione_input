#!/bin/bash
#***************************************************
#
#脚本名称 : sftp_put_file_zl.sh
#脚本功能 : 将文件传送给一体机
#创建日期 : 20190305
#作    者 : bwn
#版    本 : v_1.0
#
#***************************************************
#获取当前程序所在目录
shPath=$(cd "$(dirname "$0")"; pwd)
shDate=`date +"%Y%m%d"`

oDataDate=$1

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

sh ${shPath}/insert_ytj_log_zl.sh "sftp_put_file_zl.sh" ${oDataDate} 4
#########################################################################################
#                                                                             ###########
#  开始上传数据                                                               ###########
#                                                                             ###########
#                                                                             ###########
#########################################################################################
FTP_IP=`cat $shPath/sys.ini |grep @FTP_IP |awk -F '=' '{printf $2}'`
FTP_USER=`cat $shPath/sys.ini |grep @USERNAME |awk -F '=' '{printf $2}'`
FTP_PASSWORD=`cat $shPath/sys.ini |grep @PASSWD |awk -F '=' '{printf $2}'`

DBUSER=`cat $shPath/sys.ini |grep @DBUSER |awk -F '=' '{printf $2}'`
DBPASSWD=`cat $shPath/sys.ini |grep @DBPASSWD |awk -F '=' '{printf $2}'`
ASUSER=`cat $shPath/sys.ini |grep @ASUSER |awk -F '=' '{printf $2}'`
CONNSTR="${DBUSER}/${DBPASSWD}@${ASUSER}"

#要上传的文件所在本地路径
PUT_YTJ_PATH=`cat $shPath/sys.ini |grep @PUT_YTJ_PATH |awk -F '=' '{printf $2}'`
putFilePath="${PUT_YTJ_PATH}"
#上传到一体机服务器目录
PUT_YTJ_GRZH=`cat $shPath/sys.ini |grep @PUT_YTJ_GRZH |awk -F '=' '{printf $2}'`
putYtjFilePath="${PUT_YTJ_GRZH}"

#获取当前时间点
nowtime=`date +"%H%M%S"`
log  "nowtime : [ ${nowtime} ]"
log "开始向一体机上传数据"
##i=0
##limit=3
##while [ $i -lt $limit ]
##do
ChangeFileName=`sqlplus -S "${CONNSTR}"<<EOF
set heading off
set echo off
set feedback off
set term off
set pagesize 0
set linesize 1000
var ret number;
select t.rowid|| '|' ||t.change_file_name from (select * from ytj_record_detail t where t.status='0'  order by t.create_time asc) t where rownum=1;
commit;
exit;
EOF`
if [ "${ChangeFileName}_N" != "_N" ];then
dataRowId=`echo $ChangeFileName | awk -F '|' '{printf $1}'`
PutFileName=`echo $ChangeFileName | awk -F '|' '{printf $2}'`
log "PutFileName : [ ${PutFileName} ]"
log "开始传输数据 putFilePath[${putFilePath}]  "
/usr/bin/expect tmp_sftp_putfile.ex $FTP_USER $FTP_PASSWORD ${FTP_IP} ${putFilePath} ${putYtjFilePath} ${PutFileName}

result=$?
if [ ${result} -eq 0 ];then
log "设置本任务为 :0->1"
taskExpdate=`sqlplus -S "${CONNSTR}"<<EOF
set heading off
set echo off
set feedback off
set term off
set pagesize 0
set linesize 1000
var ret number;
update ytj_record_detail t set t.status='1',t.update_time=sysdate where t.status='0' and t.rowid='${dataRowId}';
commit;
exit;
EOF`
fi
else
log "ytj_record_detail表中没有等待传输文件"
break
fi


#done
log "传输完毕[$?]"
exit 0

