#!/bin/bash
#------------------------------------------------------------------
#
#个人账户数据一体机应答检测入口脚本 sh run_backfile_main_zl.sh
#
#------------------------------------------------------------------
#获取当前程序所在目录
shPath=$(cd "$(dirname "$0")"; pwd)

DBUSER=`cat $shPath/sys.ini |grep @DBUSER |awk -F '=' '{printf $2}'`
DBPASSWD=`cat $shPath/sys.ini |grep @DBPASSWD |awk -F '=' '{printf $2}'`
ASUSER=`cat $shPath/sys.ini |grep @ASUSER |awk -F '=' '{printf $2}'`
CONNSTR="${DBUSER}/${DBPASSWD}@${ASUSER}"

step=1
while [ 1 ]
do
   shDate=`date +"%Y%m%d"`
   logPath=${shPath}/eaas/log/${shDate}
   if [ ! -d ${logPath} ];then
       mkdir -p ${logPath}
   fi
   logFile=${logPath}/`basename $0 .sh`.log
   log()
   {
       echo `date +"%Y-%m-%d %H:%M:%S"` $$ $1 >> $logFile
   }
    
    log "1. get sysdate date and time [ ${step} ]"
    expData=`sqlplus -S "${CONNSTR}"<<EOF
             set heading off
             set echo off
             set feedback off
             set term off
             set pagesize 0
             set linesize 1000
             var ret number;
             select  data_date || '|' || change_file_name from (select * from ytj_bigfile_detail where status='ready' order by data_date asc) where rownum = 1;
             exit;
EOF`
    log "    expData[ ${expData} ]"
    if [ "${expData}_N" != "_N" ]; then
        dataDatemust=`echo ${expData} |awk -F '|' '{printf $1}'`
        changeFileName=`echo ${expData} |awk -F '|' '{printf $2}'`
        step=1
    else
        step=2
    fi

    if [ ${step} -eq 1 ];then
        taskStep=1
        while [ 1 ]
        do
             taskExpData=`sqlplus -S "${CONNSTR}"<<EOF
                      set heading off
                      set echo off
                      set feedback off
                      set term off
                      set pagesize 0
                      set linesize 1000
                      var ret number;
                      select  rowid || '|' || data_date || '|' || change_file_name from ytj_bigfile_detail where status='ready' and data_date='${dataDatemust}' and rownum = 1;
                      exit;
EOF`
             log "    taskExpData[ ${taskExpData} ]"
             if [ "${taskExpData}_N" != "_N" ]; then
                 dataRowId=`echo ${taskExpData} |awk -F '|' '{printf $1}'`
                 dataDate=`echo ${taskExpData} |awk -F '|' '{printf $2}'`
                 changeFileName=`echo ${taskExpData} |awk -F '|' '{printf $3}'`
                 taskStep=0
             else
                 break
             fi
             
             
             if [ ${taskStep} -eq 0 ];then
                
                log "获取一体机文件"
                sh scan_backfile_zl.sh ${dataRowId} ${dataDate} ${changeFileName} ${shDate}
                
                 log "设置本任务为success"
		taskExpData=`sqlplus -S "${CONNSTR}"<<EOF
                                 set heading off
                                 set echo off
                                 set feedback off
                                 set term off
                                 set pagesize 0
                                 set linesize 1000
                                 var ret number;
                                 update ytj_bigfile_detail t set t.status='success',t.update_time=sysdate where t.rowid='${dataRowId}';
                                 commit;
                                 exit;
EOF`
             fi 
        done
        
        log "加载一体机数据 [  ${shDate} ${dataDatemust} ${changeFileName} ]"
	sh sqlldr_backfile_zl.sh ${shDate} ${dataDatemust} ${changeFileName}
       result_sqlldr=$?
       log "result_sqlldr [ $result_sqlldr ]"
	    

  loaddata_ctl_dir=${shPath}/ctl     
  
   BadfilePath=${logPath}/sqlldrlog
   job_name=ytj_badfile_record
   logPathSqlldr=${logPath}
    log "计算加载一体机数据[ ${BadfilePath} ]路径下bad文件数量"
	badfilenum=0
    for element in `ls ${BadfilePath}|grep bad`
      do             
              log "element:[  ${element}  ]"
              badfilename=`echo ${element}|grep bad`
              if [ "${badfilename}_N" != "_N" ];then
                     expData=`sqlplus -S "${CONNSTR}"<<EOF
                              set heading off
                              set echo off
                              set feedback off
                              set term off
                              set pagesize 0
                              set linesize 1000
                              var ret number;
                                 insert into ytj_badfile_detail(data_date, badfile_name, create_time) 
                                 values('${dataDate}', '${badfilename}', sysdate);
                              commit;
                              exit;
EOF`
                     log "expData:[ ${expData} ]"
                     badfilenum=$((${badfilenum}+1));
              fi
       done
  log "badfilenum:[  ${badfilenum}  ]"
   if [ ${badfilenum} -ne 0 ]; then
    taskStep=''
    while [ 1 ]
     do
     
     taskStep=`sqlplus -S "${CONNSTR}"<<EOF
                      set heading off
                      set echo off
                      set feedback off
                      set term off
                      set pagesize 0
                      set linesize 1000
                      var ret number;
                      select  rowid || '|'  || badfile_name || '|' || data_date from ytj_badfile_detail where status='ready' and data_date='${dataDate}' and rownum = 1;
                      exit;
EOF`
    log "taskStep [ ${taskStep} ]"
       if [ "${taskStep}_N" != "_N" ]; then
        dataRowId=`echo ${taskStep} |awk -F '|' '{printf $1}'`
        BadFilename=`echo ${taskStep} |awk -F '|' '{printf $2}'`
        sqlldr userid=${CONNSTR} control=${loaddata_ctl_dir}/${job_name}.ctl data=${BadfilePath}/${BadFilename} log=${logPathSqlldr}/${BadFilename}.log bad=${logPathSqlldr}/${BadFilename}.bad rows=1000 bindsize=1000 errors=-1 >/dev/null
        SQLLDR=$?
         if [ ${SQLLDR} -eq 0 ];then
         loadDate=`sqlplus -S "${CONNSTR}"<<EOF
                                         set heading off
                                         set echo off
                                         set feedback off
                                         set term off
                                         set pagesize 0
                                         set linesize 1000
                                         var ret number;
                                         update ytj_badfile_detail t set t.status='success' where t.rowid='${dataRowId}';
                                         commit;
                                         exit;
EOF`
         else
         loadDate=`sqlplus -S "${CONNSTR}"<<EOF
                                        set heading off
                                        set echo off
                                        set feedback off
                                        set term off
                                        set pagesize 0
                                        set linesize 1000
                                        var ret number;
                                        update ytj_badfile_detail t set t.status='failed' where t.rowid='${dataRowId}';
                                        commit;
                                        exit;
EOF`
         fi
       else
         break
       fi 
     done
   fi
 
   log "bad文件数据加载完毕！"  
 #fi       
        log "${data_date}日期数据全部应答完毕,开始二次调用"
        taskExpData=`sqlplus -S "${CONNSTR}"<<EOF
                                 set heading off
                                 set echo off
                                 set feedback off
                                 set term off
                                 set pagesize 0
                                 set linesize 1000
                                 var ret number;
                                 insert into ytj_work_acbsummit(run_date,data_date, proc_name, create_time) values('${shDate}','${dataDate}', 'PROC_ACB_SUMMIT', sysdate);
                                 commit;
                                 exit;
EOF`
       log "ok!!!!"
       log " " 
   fi
    
    
    sleep 60
done

