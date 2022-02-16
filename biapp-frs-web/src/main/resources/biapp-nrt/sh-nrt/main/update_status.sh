#!/bin/bash
#***************************************************
#
#脚本名称 : export_dataFile_zl.sh
#脚本功能 : 检测是否具备卸载数据条件,具备就卸载数据
#创建日期 : 20190305
#作    者 : bwn
#版    本 : v_1.0
#
#***************************************************
export ORACLE_HOME=/u01/app/oracle/product/19.0.0/client_1
export PATH=$ORACLE_HOME/bin:$PATH
export LD_LIBRARY_PATH=$LD_LIBRARY_PATH:$ORACLE_HOME/lib

#获取当前程序所在目录
#shPath=$(cd "$(dirname "$0")"; pwd)
shPath="/webshare/ytj_zl/main"
shDate=`date +"%Y%m%d"`

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
#########################################################################################
#                                                                             ###########
#         获取数据日期                                                        ###########
#                                                                             ###########
#########################################################################################
data_dt=''
if [ $# -eq 1 ];then
    data_dt=$1
else
    while [ "${data_dt}_N" = '_N' ]
    do
        data_dt=`sqlplus -S "${CONNSTR}"<<EOF
                 set heading off;
				         set echo off;
				         set feedback off;
				         set term off;
				         set pagesize 0;
				         set linesize 1000;
				         SELECT to_char(max(DATA_DATE),'yyyymmdd') FROM east_ytj_work_run;
				         exit;
EOF`
        if [ "${data_dt}_N" = '_N' ];then
             log "获取当前数据日期失败[${data_dt}]。。。"
            #sleep 30
            exit 2;
        fi
    done
fi
log "当前数据日期:[${data_dt}]"
#########################################################################################
#                                                                             ###########
#         检测是否具备修改数据条件                                            ###########
#                                                                             ###########
#########################################################################################
#
V_STATE=`sqlplus -S "${CONNSTR}"<<EOF
         set heading off;
				 set echo off;
				 set feedback off;
				 set term off;
				 set pagesize 0;
				 set linesize 1000;
				 SELECT to_char(T1.STATISTICS_DT,'yyyymmdd') FROM ACB_INDIV_BANK_ACCT_SUMMIT T1 WHERE T1.STATISTICS_DT=to_date('${data_dt}','yyyymmdd') AND ROWNUM=1;
				 exit;
EOF`
echo $V_STATE

upTaskTab1=`sqlplus -S "${CONNSTR}"<<EOF
                       set heading off
                       set echo off
                       set feedback off
                       set term off
                       set pagesize 0
                       set linesize 1000
                       var ret number;
                       UPDATE YTJ_WORK_ACBSUMMIT SET STATUS = 'ready'  WHERE DATA_DATE = '$V_STATE' AND PROC_NAME = 'PROC_ACB_SUMMIT';
                       commit;
                       exit;
EOF`
