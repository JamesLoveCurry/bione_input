#!/bin/bash
#-----------------------------------------------------------------------------------------------
#
#脚本 sh sqlldr_backfile_zl.sh
#
#流程: 装载一体机应答数据
#
#-----------------------------------------------------------------------------------------------
#获取当前程序所在目录
shPath=$(cd "$(dirname "$0")"; pwd)

shDate=$1
dataDate=$2
changeFileName=$3


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

loaddata_ctl_dir=${shPath}/ctl

DBUSER=`cat $shPath/sys.ini |grep @DBUSER |awk -F '=' '{printf $2}'`
DBPASSWD=`cat $shPath/sys.ini |grep @DBPASSWD |awk -F '=' '{printf $2}'`
ASUSER=`cat $shPath/sys.ini |grep @ASUSER |awk -F '=' '{printf $2}'`
CONNSTR="${DBUSER}/${DBPASSWD}@${ASUSER}"


GET_FILEBACK_PATH=`cat $shPath/sys.ini |grep @GET_FILEBACK_PATH |awk -F '=' '{printf $2}'`
vGetServerFiles=${GET_FILEBACK_PATH}/${shDate}


logPathSqlldr=${logPath}/sqlldrlog
if [  ! -d ${logPathSqlldr} ]; then
   mkdir -p ${logPathSqlldr}
fi


job_name=acb_error_ytj
loadDataNum=0

taskStep=1

log "shDate        [ ${shDate}         ]"
log "dataDate      [ ${dataDate}       ]"
log "changeFileName[ ${changeFileName} ]"

while [ 1 ]
do
        taskInsertData=`sqlplus -S "${CONNSTR}"<<EOF
                      set heading off
                      set echo off
                      set feedback off
                      set term off
                      set pagesize 0
                      set linesize 1000
                      var ret number;
                      select  rowid || '|'  || fileback_name || '|' || data_date from ytj_fb_checkfile_detail where status='ready' and data_date='${dataDate}' and rownum = 1;
                      exit;
EOF`
        log "taskInsertData[ ${taskInsertData} ]"
        if [ "${taskInsertData}_N" != "_N" ]; then
                 dataRowId=`echo ${taskInsertData} |awk -F '|' '{printf $1}'`
                 dataFilename=`echo ${taskInsertData} |awk -F '|' '{printf $2}'`
                 taskdatadate=`echo ${taskInsertData} |awk -F '|' '{printf $3}'`
                 taskStep=0
        else
                taskStep=2
        fi
         log "taskStep : [ $taskStep ]"

        if [ ${taskStep} -eq 0 ]; then
       
       taskInsertData=`sqlplus -S "${CONNSTR}"<<EOF
                      set heading off
                      set echo off
                      set feedback off
                      set term off
                      set pagesize 0
                      set linesize 1000
                      var ret number;
                      update ytj_fb_checkfile_detail t set t.status='running' where t.rowid='${dataRowId}';
                      commit;
                      exit;
EOF`
                    
		
		log "taskInsertData [ $taskInsertData ]"
                formatstr="s/XXXXXXXX/${taskdatadate}/g"
                sed  "${formatstr}" ${shPath}/ctl/acb_error_ytj_tmp.ctl >${shPath}/ctl/acb_error_ytj.ctl
                sqlldr userid=${CONNSTR} control=${loaddata_ctl_dir}/${job_name}.ctl data=${vGetServerFiles}/${dataFilename} skip=1 log=${logPathSqlldr}/${dataFilename}.log bad=${logPathSqlldr}/${dataFilename}.bad direct=true rows=10000 bindsize=10000 errors=10000000 >/dev/null
                SQLLDR=$?
		log "SQLLDR [ $SQLLDR ]"
		if [ "${SQLLDR}_N" != '_N'  ];then
		    if [ ${SQLLDR} -eq 0 ];then
                            log "[ ${dataFilename} ]数据加载成功sqlldr返回值为[ $? ]"
         
                            loadDate=`sqlplus -S "${CONNSTR}"<<EOF
                                      set heading off
                                      set echo off
                                      set feedback off
                                      set term off
                                      set pagesize 0
                                      set linesize 1000
                                      var ret number;
                                      update ytj_fb_checkfile_detail t set t.status='success' where t.rowid='${dataRowId}';
                                      commit;
                                      exit;
EOF`
                    else
                            log "[ ${dataFilename} ]数据加载失败sqlldr返回值为[ $? ]"
                            loadDate=`sqlplus -S "${CONNSTR}"<<EOF
                                      set heading off
                                      set echo off
                                      set feedback off
                                      set term off
                                      set pagesize 0
                                      set linesize 1000
                                      var ret number;
                                      update ytj_fb_checkfile_detail t set t.status='failed' where t.rowid='${dataRowId}';
                                      commit;
                                      exit;
EOF`
                    fi
                else
                    log "[ ${dataFilename} ]数据加载失败sqlldr返回值为[ $? ], 请检查加载文件数据"
                fi
        fi
        
        if [ ${taskStep} -eq 2 ];then
                insertfailed=`sqlplus -S "${CONNSTR}"<<EOF
                              set heading off
                              set echo off
                              set feedback off
                              set term off
                              set pagesize 0
                              set linesize 1000
                              var ret number;
                              select  count(1) from ytj_fb_checkfile_detail where status='failed' and data_date='${dataDate}';
                              exit;
EOF`
                log "insertfailed[ ${insertfailed} ]"
                if [ ${insertfailed} -eq 0 ]; then
                    break
                else
                    sleep 20
                fi
        fi
        sleep 1
done

log "任务数据处理成功"

exit 0
