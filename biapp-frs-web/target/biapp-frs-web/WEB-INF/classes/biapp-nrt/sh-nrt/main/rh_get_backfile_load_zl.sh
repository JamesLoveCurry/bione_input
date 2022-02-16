#!/bin/bash
#########################################################################################
#                                                                             ###########
#  初始参数区                                                                 ###########
#                                                                             ###########
#                                                                             ###########
#########################################################################################
#获取当前程序所在目录
shPath=$(cd "$(dirname "$0")"; pwd)

shDate=`date +"%Y%m%d"`
lineNum=$1

DBUSER=`cat $shPath/sys.ini |grep @DBUSER |awk -F '=' '{printf $2}'`
DBPASSWD=`cat $shPath/sys.ini |grep @DBPASSWD |awk -F '=' '{printf $2}'`
ASUSER=`cat $shPath/sys.ini |grep @ASUSER |awk -F '=' '{printf $2}'`
CONNSTR="${DBUSER}/${DBPASSWD}@${ASUSER}"

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

FTP_IP=`cat $shPath/sys.ini |grep @FTP_IP |awk -F '=' '{printf $2}'`
FTP_USER=`cat $shPath/sys.ini |grep @USERNAME |awk -F '=' '{printf $2}'`
FTP_PASSWORD=`cat $shPath/sys.ini |grep @PASSWD |awk -F '=' '{printf $2}'`

GET_RH_PATH=`cat $shPath/sys.ini |grep @GET_RH_PATH |awk -F '=' '{printf $2}'`
###下载人行返回报文本地临时路径#####
vGetServerFiles=${GET_RH_PATH}/${shDate}
if [  -d ${vGetServerFiles} ]; then
   rm -rf ${vGetServerFiles}
fi

if [ ! -d ${vGetServerFiles} ]; then
   mkdir -p ${vGetServerFiles}
fi

#服务器文件列表信息本地存放路径
vServerFileListPath=${shPath}/eaas/tmp/${shDate}
if [ ! -d ${vServerFileListPath} ]; then
   mkdir -p ${vServerFileListPath}
fi
#########################################################################################
#                                                                             ###########
#  开始处理                                                                   ###########
#                                                                             ###########
#########################################################################################

log "要下载文件数量为lineNum:[${lineNum}]"

log "从服务器上/data/appdata/eaas/tlq/file获取数据"
/usr/bin/expect ${shPath}/tmp_mgettxt.ex ${FTP_USER} ${FTP_PASSWORD} ${FTP_IP} ${vGetServerFiles}


#计算获取后的文件数量
localFileNum=0
for element in `ls "${vGetServerFiles}"`
do
    vfileNameTem=${element}
    if [ "${vfileNameTem##*.}" = "txt" ]
    then
       localFileNum=$((${localFileNum}+1));
    fi
done

log "实际下载下来的文件数量为localFileNum:[${localFileNum}]"

if [ ${localFileNum} -gt "0" ] && [ ${localFileNum} -eq ${lineNum} ]; then
   log "从一体机下载处理结果文件成功完毕"
else
   log "从一体机下载处理结果文件完毕,对应不上"
fi


#########################################################################################
#                                                                             ###########
#  更改服务器上的文件名                                                       ###########
#                                                                             ###########
#                                                                             ###########
#########################################################################################
log "开始修改一体机人行应答目录/data/appdata/eaas/tlq/file服务器上的文件名为bak"
for element in `ls "${vGetServerFiles}"`
do
    /usr/bin/expect ${shPath}/tmp_rhpchangename.ex ${FTP_USER} ${FTP_PASSWORD} ${FTP_IP} ${element} ${element}.bak
done


#########################################################################################
#                                                                             ###########
#  开始入库                                                                   ###########
#                                                                             ###########
#########################################################################################
log "数据入库【${shDate}】"
sh rh_backfile_loadDB_zl.sh ${shDate}

#########################################################################################
#                                                                             ###########
#  开始跑存储过程                                                             ###########
#   rf:格式校验  PROC_ACB_ERROR_RHG                                           ###########
#   rl:逻辑校验  PROC_ACB_ERROR_RHL                                           ###########                          
#                                                                             ###########
#########################################################################################
i='C'
  log " aaaaaaaa--${i}"
     while [ "${i}_N" = "C_N" ]   
      do
         i=`ps -ef | grep proc_run_zl.sh | grep -v grep | awk '{ print  $2 }'`
         if [ "${i}" = "" ];then
            break
            log "i : [ $i ]"
         else
           log " i=`ps -ef | grep proc_run_zl.sh | grep -v grep | awk '{ print  $2 }'`"   
           log "等待[proc_run_zl.sh]执行进程60s :[$i]"
           sleep 60
           i=C        
         fi
      done 
m='B'
  log " aaaaaaaa--${m}"
     while [ "${m}_N" = "B_N" ]   
      do
         m=`ps -ef | grep proc_run_acb_summit.sh | grep -v grep | awk '{ print  $2 }'`
         if [ "${m}" = "" ];then
            break
            log "m : [ $m ]"
         else
           log " m=`ps -ef | grep proc_run_acb_summit.sh | grep -v grep | awk '{ print  $2 }'`"   
           log "等待[proc_run_acb_summit.sh]执行进程60s :[$m]"
           sleep 60
           m=B        
         fi
      done
log "跑存储过程【${shDate}】"
sh rh_run_proc_rhg.sh ${shDate}

#########################################################################################
#                                                                             ###########
#  结束                                                                       ###########
#                                                                             ###########
#########################################################################################

log "全部处理完毕!!!"


