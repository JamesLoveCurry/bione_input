#!/bin/bash
#***************************************************
#
#脚本名称 : proc_run_acb_summit.sh
#脚本功能 : 执行存储过程
#创建日期 : 20190305
#作    者 : bwn
#版    本 : v_1.0
#
#***************************************************
#获取当前程序所在目录
shPath=$(cd "$(dirname "$0")"; pwd)
shDate=`date +"%Y%m%d"`

if [ $# -ne 1 ]; then
   log "参数传输错误！" >> ${shPath}/eaas/log/proc_run_err.log
   exit 1
fi
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
}

log "获取的参数1:[${oDataDate}]"


DBUSER=`cat $shPath/sys.ini |grep @DBUSER |awk -F '=' '{printf $2}'`
DBPASSWD=`cat $shPath/sys.ini |grep @DBPASSWD |awk -F '=' '{printf $2}'`
ASUSER=`cat $shPath/sys.ini |grep @ASUSER |awk -F '=' '{printf $2}'`
CONNSTR="${DBUSER}/${DBPASSWD}@${ASUSER}"

sh ${shPath}/insert_ytj_log_zl.sh "proc_run_acb_summit.sh" ${oDataDate} 1

#########################################################################################
#                                                                             ###########
#  执行存储过程 PROC_ACB_SUMMIT                                               ###########
#  ACB_INDIV_BANK_ACCT_SUMMIT    --> ACB_SUMMIT                               ###########
#                                                                             ###########
#########################################################################################
log "开始执行存储过程"
            ETL_RESULT=`sqlplus -S "${CONNSTR}"<<EOF
		 		                set heading off;
		 		                set echo off;
		 		                set feedback off;
		 		                set term off;
		 		                set pagesize 0;
		 		                set linesize 2048;
		 		                var ora_return_code VARCHAR2;
		 		                exec PROC_ACB_SUMMIT('${oDataDate}',:ora_return_code);
		 		                select :ora_return_code from dual;
		 		                exit;
EOF`

echo "proc is result:[${ETL_RESULT}]"
log "处理完毕!!!"
if [ ${ETL_RESULT} -eq 0 ]; then
    exit 0
else
    exit 1
fi

