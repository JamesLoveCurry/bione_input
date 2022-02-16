#!/bin/bash
# sh rh_scan_downFile_zl.sh
# 格式校验 rf

#########################################################################################
#                                                                             ###########
#  初始参数区                                                                 ###########
#                                                                             ###########
#                                                                             ###########
#########################################################################################
#获取当前程序所在目录
shPath=$(cd "$(dirname "$0")"; pwd)

#设置日志文件路径
shDate=$1
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

vServerFileListPath=${shPath}/eaas/tmp/${shDate}
if [ ! -d ${vServerFileListPath} ]; then
    mkdir -p ${vServerFileListPath}
fi

FTP_IP=`cat $shPath/sys.ini |grep @FTP_IP |awk -F '=' '{printf $2}'`
FTP_USER=`cat $shPath/sys.ini |grep @USERNAME |awk -F '=' '{printf $2}'`
FTP_PASSWORD=`cat $shPath/sys.ini |grep @PASSWD |awk -F '=' '{printf $2}'`

#########################################################################################
#                                                                             ###########
#  检测人行返回目录下/data/appdata/eaas/tlq/file/是否存在文件                 ###########
#  rl:表示逻辑校验失败 rf:格式校验失败                                        ###########
#                                                                             ###########
#########################################################################################
lineNum=0
while [ 1 ]
do

    log "扫描人行返回数据目录/data/appdata/eaas/tlq/file"
   /usr/bin/expect ${shPath}/tmp_rh_nameListStr.ex ${FTP_USER} ${FTP_PASSWORD} ${FTP_IP} > ${shPath}/eaas/log/tmp_rhnameListStr.log
    
    cat ${shPath}/eaas/log/tmp_rhnameListStr.log | sed -ne '6,$p' | sed 's/.$//g' |awk '{printf "%s\n", $9}' >${shPath}/eaas/log/remoteRHtmp.txt
    log "要处理的文件列表存放在:[${shPath}/eaas/log]remoteRHtmp.txt文件中"

    lineNumTmp=${lineNum}
    lineNum=0
    while read line
    do
       vfileNameTem=${line}
       if [ "${vfileNameTem##*.}" = "txt" ]
       then
          lineNum=$((${lineNum}+1));
       fi
    done < ${shPath}/eaas/log/remoteRHtmp.txt

    log "本次检测到一体机反馈的rh.txt文件文件数量是[${lineNum}], 上一次检测到的文件数量是[${lineNumTmp}]"
    if [ ${lineNum} -eq "0" ]; then
       exit 0;
    fi


    if [ ${lineNumTmp} -gt "0" ] && [ ${lineNumTmp} -eq ${lineNum} ]; then
        log "获取人行返回的校验文件信息完毕!!!,本次应该获取的文件数量[${lineNum}]" 
        sleep 5
        sh rh_get_backfile_load_zl.sh ${lineNum}
        exit 0
    fi
  sleep 10
done
