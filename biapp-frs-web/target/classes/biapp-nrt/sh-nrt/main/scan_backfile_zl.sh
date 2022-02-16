#!/bin/bash
#-----------------------------------------------------------------------------------------------
#
#脚本 sh scan_backfile_zl.sh
#流程: 检测当前数据库时间是不是在早上9:05 到 23:55之间,在就扫描一体机应答目录文件数
#
#      若文件数量无变化,认为一体机处理完毕,开始将数据下载数据并装数据
# 
#      若文件数量有变化,就等待设置的时间,然后再扫描,直到无变化,开始下载数据并装数据
#
#-----------------------------------------------------------------------------------------------
#获取当前程序所在目录
shPath=$(cd "$(dirname "$0")"; pwd)

dataRowId=$1
dataDate=$2
changeFileName=$3
shDate=$4

logPath=${shPath}/eaas/log/${shDate}
if [ ! -d ${logPath} ];then
    mkdir -p ${logPath}
fi
logFile=${logPath}/`basename $0 .sh`.log
log()
{
    echo `date +"%Y-%m-%d %H:%M:%S"` $$ $1 >> $logFile
}


DBUSER=`cat $shPath/sys.ini |grep @DBUSER |awk -F '=' '{printf $2}'`
DBPASSWD=`cat $shPath/sys.ini |grep @DBPASSWD |awk -F '=' '{printf $2}'`
ASUSER=`cat $shPath/sys.ini |grep @ASUSER |awk -F '=' '{printf $2}'`
CONNSTR="${DBUSER}/${DBPASSWD}@${ASUSER}"


FTP_IP=`cat $shPath/sys.ini |grep @FTP_IP |awk -F '=' '{printf $2}'`
FTP_USER=`cat $shPath/sys.ini |grep @USERNAME |awk -F '=' '{printf $2}'`
FTP_PASSWORD=`cat $shPath/sys.ini |grep @PASSWD |awk -F '=' '{printf $2}'`

#上传到一体机服务器目录
PUT_YTJ_GRZH=`cat $shPath/sys.ini |grep @PUT_YTJ_GRZH |awk -F '=' '{printf $2}'`
putYtjFilePath="${PUT_YTJ_GRZH}"

GET_YTJ_GRZH_BACK=`cat $shPath/sys.ini |grep @GET_YTJ_GRZH_BACK |awk -F '=' '{printf $2}'`
getYtjGrzhBack="${GET_YTJ_GRZH_BACK}"


log "dataRowId:[ ${dataRowId} ]"
log "dataDate:[ ${dataDate} ]"
log "changeFileName:[ ${changeFileName} ]"


taskUpdateData=`sqlplus -S "${CONNSTR}"<<EOF
                set heading off
                set echo off
                set feedback off
                set term off
                set pagesize 0
                set linesize 1000
                var ret number;
                update ytj_bigfile_detail t set t.status='running' where t.rowid='${dataRowId}';
                commit;
                exit;
EOF`

lineNum=0
shResult=1

while [ 1 ]
do                      
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
        if [ "${expDate}_N" = "_N" ]; then
                log "获取服务器时间失败,重新获取"
                shResult=1
        else
                log "服务器现在时间为expDate[ ${expDate} ]"
                nowDate=`echo ${expDate} |awk -F '-' '{printf $1}'`
                nowTime=`echo ${expDate} |awk -F '-' '{printf $2}'`
                shResult=0
        fi
        
        if [ ${shResult} -eq 0 ];then
            if [ ${nowTime} -gt 90500 ] && [ ${nowTime} -lt 235500 ] ; then
                
                log "扫描一体机返回数据目录/data/appdata/eaas/inter/fileback"
                 /usr/bin/expect ${shPath}/tmp_ytj_nameListStr.ex ${FTP_USER} ${FTP_PASSWORD} ${FTP_IP} > ${shPath}/eaas/log/tmp_ytj_nameListStr.log
    
                 cat ${shPath}/eaas/log/tmp_ytj_nameListStr.log | sed -ne '6,$p' | sed 's/.$//g' |awk '{printf "%s\n", $9}' >${shPath}/eaas/log/tmp_ytj_nameListStr.txt
                  log "要处理的文件列表存放在:[${shPath}/eaas/log]tmp_ytj_nameListStr.txt文件中"
                 
                  lineNumTmp=${lineNum}
                  lineNum=0
                  while read line
                  do
                     vfileNameTem=${line}
                     if [ "${vfileNameTem##*.}" = "txt" ]
                     then
                        lineNum=$((${lineNum}+1));
                   
			         fi
                  done < ${shPath}/eaas/log/tmp_ytj_nameListStr.txt
                  log "本次检测到一体机反馈的.txt文件文件数量是[${lineNum}], 上一次检测到的文件数量是[${lineNumTmp}]"
                   #  if [ ${lineNum} -eq "0" ]; then
                   #     exit 0;
                   #  fi
                     ResultNum=0
                     if [ ${lineNumTmp} -gt "0" ] && [ ${lineNumTmp} -eq ${lineNum} ]; then
                          log "获取一体机返回的校验文件信息完毕!!!"
                          ResultNum=1
                     fi
                    Step=1
                     if [ ${ResultNum} -eq 1 ]; then
                       lineNum2=0
                       
                        while [ 1 ]
                         do        

                
                         listFileNameTemp=`echo $changeFileName|awk -F\[ '{print $3}'|cut -b -14`
                         selNumber=`echo $changeFileName|awk -F\[ '{print $4}'|cut -b -2`
                         
                         scankeystr="[[]${listFileNameTemp}[]][[]${selNumber}[]]"
                         
                         log "扫描/data/appdata/eaas/inter/fileback中包含有 [${scankeystr}] 文件数量"
                         /usr/bin/expect ${shPath}/tmp_querybackfile.ex "${FTP_USER}" "${FTP_PASSWORD}" "${FTP_IP}" "${scankeystr}" "${getYtjGrzhBack}" >${shPath}/eaas/log/tmp_querybackfile.log
                         
                         
                         cat ${shPath}/eaas/log/tmp_querybackfile.log | sed -ne '6,$p' | sed 's/.$//g' | awk '{printf "%s\n", $9}' >${shPath}/eaas/log/tmp_querybackfile.txt
                         log "一体机的反馈目录文件列表存放在[ ${shPath}/eaas/log ]tmp_querybackfile.txt文件中"
                         
                         lineNumTmp2=${lineNum2}
                         lineNum2=0
                         while read line
                         do
                             vfileNameTem=${line}
                             if [ "${vfileNameTem##*.}" = "txt" ];then
                                 lineNum2=$((${lineNum2}+1));
                             fi
                         done < ${shPath}/eaas/log/tmp_querybackfile.txt
                             
                         log "本次检测到一体机反馈的txt文件文件数量是[${lineNum2}], 上一次检测到的文件数量是[${lineNumTmp2}]"
                         
                         if [ ${lineNumTmp2} -gt "0" ] && [ ${lineNumTmp2} -eq ${lineNum2} ]; then
                             log "一体机对本文件处理完毕,可以下载数据"
                             sh ${shPath}/getbackfile_load_zl.sh ${shDate} ${dataDate} ${listFileNameTemp} ${lineNumTmp2} ${changeFileName} ${selNumber}
                             TaskStep=$?
                             if [ ${TaskStep} -eq 0 ]; then
                              Step=2
                             else
                              Step=1
                             fi
                             break
                         fi
                          sleep 10
                         done
                     
                     fi
                     
                     if [ ${Step} -eq 2 ]; then
                      log "本文件数据下载完毕"
                      break
                     fi
                     
              ytjInterval=`sqlplus -S "${CONNSTR}"<<EOF
                         set heading off
                         set echo off
                         set feedback off
                         set term off
                         set pagesize 0
                         set linesize 1000
                         var ret number;
                         select  e.interval from east_ytj_scan_time e where e.name='ytj';
                         exit;
EOF`
                      if [ "${ytjInterval}_N" = "_N" ]; then
                              log "获取扫描时间失败,采用默认值600秒"
                              sleep 5
                      else
                              log "获取扫描时间为ytjInterval[ ${ytjInterval} ]秒"
                              sleep ${ytjInterval}
                      fi
            fi
         fi
        
done
 log "下载一体机数据完毕"

