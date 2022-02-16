#!/bin/bash

#获取当前程序所在目录
shPath=$(cd "$(dirname "$0")"; pwd)
DBUSER=`cat $shPath/sys.ini |grep @DBUSER |awk -F '=' '{printf $2}'`
DBPASSWD=`cat $shPath/sys.ini |grep @DBPASSWD |awk -F '=' '{printf $2}'`
ASUSER=`cat $shPath/sys.ini |grep @ASUSER |awk -F '=' '{printf $2}'`
CONNSTR="${DBUSER}/${DBPASSWD}@${ASUSER}"





step=0
while [ 1 ]
do
   
    #设置日志文件路径
    shDate=`date +"%Y%m%d"`
    shDate='20200331'
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
    log "1.get taskwork from ytj_maintain_work...${step}"
     TaskWork=`sqlplus -S "${CONNSTR}"<<EOF
                   set heading off
                   set echo off
                   set feedback off
                   set term off
                   set pagesize 0
                   set linesize 1000
                   var ret number;
                   select care_date || '|' || care_time_start || '|' || care_time_end from(select * from ytj_maintain_work order by care_date desc) where rownum = 1;
                   exit;
EOF`
        log "TaskWork  : [ ${TaskWork} ]"
        if [ "${TaskWork}_N" != "_N" ]; then
          care_date=`echo ${TaskWork} |awk -F '|' '{printf $1}'`
          care_time_start=`echo ${TaskWork} |awk -F '|' '{printf $2}'`
          care_time_end=`echo ${TaskWork} |awk -F '|' '{printf $3}'`
          step=1
	
        else
          step=2
	
        fi
echo ${care_date}
echo ${shDate}
      log "2.check today if have maintain work..${step}"  
        if [ ${step} -eq 1 ]; then
          if [ ${care_date} -eq ${shDate} ]; then
            log "today have maintain work!"
            step=1
          else
            log "today don't have maintain work!"
            step=3
          fi
        fi
        
        if [ ${step} -eq 1 ];then
        
	nowtime=`date +"%H%M%S"`
          log "check the time gt ${care_time_start} and lt ${care_time_end}-----[${nowtime}]"
          if [ ${nowtime} -gt ${care_time_start} ]&&[ ${nowtime} -lt ${care_time_end} ]; then
            log "nowtime:[${nowtime}] at the care_time,sleep 300"
            sleep 30
          else
            if [ ${nowtime} -gt 090000 ]; then
              
                log "【${shDate}】每隔60秒扫描一次人行一体机返回目录..."
                
                sh rh_scan_downFile_zl.sh ${shDate}
                sleep 60
            else
               log "不在扫描人行应答时间范围"
               sleep 30
            fi
          fi                          
        else
	
          nowtime=`date +"%H%M%S"`    
          log "check the time gt 17:00 [ ${nowtime} ]"
          if [ ${nowtime} -gt 090000 ]; then
            
              log "【${shDate}】每隔60秒扫描一次人行一体机返回目录..."
              
              sh rh_scan_downFile_zl.sh ${shDate}
              sleep 60
          else
             log "不在扫描人行应答时间范围"
             sleep 30
          fi
        fi
done
