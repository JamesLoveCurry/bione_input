#!/bin/bash
#***************************************************
#
#脚本名称 : export_changeFileName_zl.sh
#脚本功能 : 修改卸载数据文件名为一体机需要的格式
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

sh ${shPath}/insert_ytj_log_zl.sh "export_changeFileName_zl.sh" ${oDataDate} 3

DBUSER=`cat $shPath/sys.ini |grep @DBUSER |awk -F '=' '{printf $2}'`
DBPASSWD=`cat $shPath/sys.ini |grep @DBPASSWD |awk -F '=' '{printf $2}'`
ASUSER=`cat $shPath/sys.ini |grep @ASUSER |awk -F '=' '{printf $2}'`
CONNSTR="${DBUSER}/${DBPASSWD}@${ASUSER}"

PUT_YTJ_PATH=`cat $shPath/sys.ini |grep @PUT_YTJ_PATH |awk -F '=' '{printf $2}'`
#vExpFileDIR="${PUT_YTJ_PATH}/${oDataDate}"
vExpFileDIR="${PUT_YTJ_PATH}"
log "vExpFileDIR [ ${vExpFileDIR} ]"

if [ -d ${vExpFileDIR} ]; then
    cd ${vExpFileDIR}
    log "aaaaaaaaaaa"
    i=0;
    for element in `ls -lrt|awk '{ print $(NF) }'|grep ${oDataDate}`
    do
       fieldStr=`echo "${element}"|awk -F "." '{printf $1}'`
       i=$(($i+1));
       s=`printf "%02d" "${i}"`
       ytjFileName="${fieldStr}[${s}].tmp"
       log "element:[${element}]   ytjFileName:[${ytjFileName}]";
       if [ -f ${element} ]; then
          mv ${element} ${ytjFileName}
          log "${element} --> ${ytjFileName}更改成功"

#向数据库表中插入记录
    InsertTask=`sqlplus -S "${CONNSTR}"<<EOF
                           set heading off
                           set echo off
                           set feedback off
                           set term off
                           set pagesize 0
                           set linesize 1000
                           var ret number;
                           insert into ytj_record_detail(data_date, change_file_name, status, create_time) 
                           values('${oDataDate}', '${ytjFileName}', '0', sysdate);
                           commit;
                           exit;
EOF`
            log "InsertTask : [ $InsertTask ]"
       else
         log "[${element}]文件不存在,更改失败"
         exit 300
       fi
       
       sleep 10 
    done
fi

log "数据文件改名处理完毕!!!"

exit 0
