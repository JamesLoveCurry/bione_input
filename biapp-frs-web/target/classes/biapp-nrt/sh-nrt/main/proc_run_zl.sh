#!/bin/bash
#***************************************************
#
#脚本名称 : proc_run_zl.sh
#脚本功能 : 执行存储过程
#创建日期 : 20190305
#作    者 : bwn
#版    本 : v_1.0
#
#***************************************************
#获取当前程序所在目录
shPath=$(cd "$(dirname "$0")"; pwd)
shDate=`date +"%Y%m%d"`

if [ $# -ne 2 ]; then
   log "参数传输错误！" >> ${shPath}/eaas/log/proc_run_err.log
   exit 1
fi
oDataDate=$1
procName=$2

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

log "获取的参数1:[${oDataDate}]  获取的参数2:[${procName}]"


DBUSER=`cat $shPath/sys.ini |grep @DBUSER |awk -F '=' '{printf $2}'`
DBPASSWD=`cat $shPath/sys.ini |grep @DBPASSWD |awk -F '=' '{printf $2}'`
ASUSER=`cat $shPath/sys.ini |grep @ASUSER |awk -F '=' '{printf $2}'`
CONNSTR="${DBUSER}/${DBPASSWD}@${ASUSER}"

sh ${shPath}/insert_ytj_log_zl.sh "proc_run_zl.sh" ${oDataDate} 1
#########################################################################################
#                                                                             ###########
#  检测O端数据是否装载完毕 ACB_INDIV_BANK_ACCT7                               ###########
#                                                                             ###########
##########################################################################################
#log "检测数据有没有加载完成 ready |running |success | failed"
#while [ 1 ]
#do
#    V_STATE=`sqlplus -S "${CONNSTR}"<<EOF
#             set heading off;
#				     set echo off;
#				     set feedback off;
#				     set term off;
#				     set pagesize 0;
#				     set linesize 1000;
#				     SELECT nvl(T2.STATE, '456') FROM ETL_WORK_DETAIL_GRZH T2  WHERE T2.JOB_NAME='acb_indiv_bank_acct7' and substr(t2.start_time,1,8)='${shDate}' AND ROWNUM=1;
#				     exit;
#EOF`
#    log "O端数据加载完成是success:V_STATE:[${V_STATE}]"
#    if [ "${V_STATE}_N" = "_N" ]; then
#        log "O端数据加载状态未获取到"
#    else
#        log "O端数据加载状态获取完毕[ ${V_STATE} ] !!!"
#        break;
#    fi
#    sleep 30;
#done
#
#if [ "${V_STATE}_N" != "success_N" ]; then
#    log "O端数据加载未成功, 程序退出"
#    exit 1
#fi

#########################################################################################
#                                                                             ###########
#  执行存储过程 PROC_ACB_ACCT_SUMMIT                                          ###########
#  ACB_INDIV_BANK_ACCT7    --> ACB_INDIV_BANK_ACCT_SUMMIT                     ###########
#                                                                             ###########
#########################################################################################
log "开始执行存储过程"
V_DATE=$1
if [ ${procName} = "ql" ]; then
    log "跑全量"
    ETL_RESULT=`sqlplus -S "${CONNSTR}"<<EOF
		 		        set heading off;
		 		        set echo off;
		 		        set feedback off;
		 		        set term off;
		 		        set pagesize 0;
		 		        set linesize 2048;
		 		        var ora_return_code VARCHAR2(1000);
		 	          exec PROC_ACB_ACCT_SUMMIT_QL(${V_DATE},:ora_return_code);
		 	          select :ora_return_code from dual;
		 		        exit;
EOF`
else
    log "跑增量"
	  ETL_RESULT=`sqlplus -S "${CONNSTR}"<<EOF
		 		        set heading off;
		 		        set echo off;
		 		        set feedback off;
		 		        set term off;
		 		        set pagesize 0;
		 		        set linesize 2048;
		 		        var ora_return_code VARCHAR2;
		            var p_i_date VARCHAR2;
		 	          exec PROC_ACB_ACCT_SUMMIT(${V_DATE},:ora_return_code);
		 	          select :ora_return_code from dual;
		 	          exit;
EOF`    
fi

log "proc is result:[${ETL_RESULT}]";
log "处理完毕!!!"
if [ ${ETL_RESULT} -eq 0 ]; then
    exit 0
else
    exit 1
fi

