#!/bin/bash
# sh rh_run_proc_rhg.sh

#获取当前程序所在目录
shPath=$(cd "$(dirname "$0")"; pwd)

#设置日志文件路径
shDate=$1
logPath=${shPath}/eaas/log/${shDate}/
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

nowtime=`date +"%H:%M:%S"`
log "The time is [ ${nowtime} ]"
while [ 1 ]
do
     TaskWork=`sqlplus -S "${CONNSTR}"<<EOF
                   set heading off
                   set echo off
                   set feedback off
                   set term off
                   set pagesize 0
                   set linesize 1000
                   var ret number;
                   select data_date || '|' || fileback_type from (select t.data_date, t.fileback_type from ytj_rh_backfile_detail t where t.fileback_type <> 'rr'and t.proc_flag = 'ready' group by t.data_date, t.fileback_type  order by t.data_date asc) where rownum = 1;
                   exit;
EOF`
        log "TaskWork  : [ ${TaskWork} ]"
        if [ "${TaskWork}_N" != "_N" ]; then
            dataDate=`echo ${TaskWork} |awk -F '|' '{printf $1}'`
            procFlag=`echo ${TaskWork} |awk -F '|' '{printf $2}'`              
               if [ "${procFlag}_N" = "rf_N" ]; then
                 log " run PROC_ACB_ERROR_RHG [ ${dataDate} ] "
                         ETL_RESULT=`sqlplus -S "${CONNSTR}"<<EOF
                                     set heading off;
                                     set echo off;
                                     set feedback off;
                                     set term off;
                                     set pagesize 0;
                                     set linesize 2048;
                                     var ora_return_code VARCHAR2(1000);
                                     exec PROC_ACB_ERROR_RHG(${dataDate},:ora_return_code);
                                     select :ora_return_code from dual;
                                     exit;
EOF`
                         
                           log "PROC_ACB_ERROR_RHG proc is result:[${ETL_RESULT}]";
                           log "PROC_ACB_ERROR_RHG处理完毕!!!"
                             if [ ${ETL_RESULT} -eq 0 ]; then
                                upTaskTab=`sqlplus -S "${CONNSTR}"<<EOF
                                           set heading off
                                           set echo off
                                           set feedback off
                                           set term off
                                           set pagesize 0
                                           set linesize 1000
                                           var ret number;
                                           update ytj_rh_backfile_detail set proc_flag='success' where data_date='${dataDate}' and fileback_type='${procFlag}';
                                           commit;
                                           exit;
EOF`
                                 log "    upTaskTab[  ${upTaskTab} ]"
                                 log "删除 [${dataDate}] ACB_ERROR_RHG 表数据"
                                deleteTask=`sqlplus -S "${CONNSTR}"<<EOF
                                           set heading off
                                           set echo off
                                           set feedback off
                                           set term off
                                           set pagesize 0
                                           set linesize 1000
                                           var ret number;
                                           alter table ACB_ERROR_RHG truncate partition for(to_date(${dataDate},'yyyymmdd'));
                                           exit;
EOF`                                 
                                   log "  deleteTask : [ ${deleteTask} ]"
                             else
                                upTaskTab=`sqlplus -S "${CONNSTR}"<<EOF
                                           set heading off
                                           set echo off
                                           set feedback off
                                           set term off
                                           set pagesize 0
                                           set linesize 1000
                                           var ret number;
                                           update ytj_rh_backfile_detail set proc_flag='failed' where data_date='${dataDate}' and fileback_type='${procFlag}';
                                           commit;
                                           exit;
EOF`
                                 log "    upTaskTab[  ${upTaskTab} ]"
                             fi 
               else
                 log " run PROC_ACB_ERROR_RHL [ ${dataDate} ] "
                         ETL_RESULT=`sqlplus -S "${CONNSTR}"<<EOF
                                     set heading off;
                                     set echo off;
                                     set feedback off;
                                     set term off;
                                     set pagesize 0;
                                     set linesize 2048;
                                     var ora_return_code VARCHAR2(1000);
                                     exec PROC_ACB_ERROR_RHL(${dataDate},:ora_return_code);
                                     select :ora_return_code from dual;
                                     exit;
EOF`
                         
                           log "PROC_ACB_ERROR_RHL proc is result:[${ETL_RESULT}]";
                           log "PROC_ACB_ERROR_RHL 处理完毕!!!"
                             if [ ${ETL_RESULT} -eq 0 ]; then
                                upTaskTab=`sqlplus -S "${CONNSTR}"<<EOF
                                           set heading off
                                           set echo off
                                           set feedback off
                                           set term off
                                           set pagesize 0
                                           set linesize 1000
                                           var ret number;
                                           update ytj_rh_backfile_detail set proc_flag='success' where data_date='${dataDate}' and fileback_type='${procFlag}';
                                           commit;
                                           exit;
EOF`
                                 log "    upTaskTab[  ${upTaskTab} ]"
                                 log "删除 [${dataDate}] ACB_ERROR_RHL 表数据"
                                deleteTask=`sqlplus -S "${CONNSTR}"<<EOF
                                           set heading off
                                           set echo off
                                           set feedback off
                                           set term off
                                           set pagesize 0
                                           set linesize 1000
                                           var ret number;
                                           alter table ACB_ERROR_RHL truncate partition for(to_date(${dataDate},'yyyymmdd'));
                                           commit;
                                           exit;
EOF`                                 
                                   log "  deleteTask : [ ${deleteTask} ]"                                 
                             else
                                upTaskTab=`sqlplus -S "${CONNSTR}"<<EOF
                                           set heading off
                                           set echo off
                                           set feedback off
                                           set term off
                                           set pagesize 0
                                           set linesize 1000
                                           var ret number;
                                           update ytj_rh_backfile_detail set proc_flag='failed' where data_date='${dataDate}' and fileback_type='${procFlag}';
                                           commit;
                                           exit;
EOF`
                                 log "    upTaskTab[  ${upTaskTab} ]"
                             fi
               fi
        else
           log "无跑数任务aaaaa" 
           break
        fi                                                                                                
done
nowtime2=`date +"%H:%M:%S"`
log " Now time is [ ${nowtime2} ]" 
