#!/bin/bash
#***************************************************
#
#脚本名称 : swap_remote_zl.sh
#脚本功能 : 修改一体机上文件名由tmp为txt,将本次上传的
#           文件名信息存入ytj_bigfile_detail中
#创建日期 : 20190305
#作    者 : bwn
#版    本 : v_1.0
#
#***************************************************
#获取当前程序所在目录
shPath=$(cd "$(dirname "$0")"; pwd)
shDate=`date +"%Y%m%d"`

oDataDate=$1
inBFTable=$2

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

#服务器文件列表信息本地存放路径
vServerFileListPath=${shPath}/eaas/tmp/${oDataDate}
if [ ! -d ${vServerFileListPath} ]; then
   mkdir -p ${vServerFileListPath}
fi

FTP_IP=`cat $shPath/sys.ini |grep @FTP_IP |awk -F '=' '{printf $2}'`
FTP_USER=`cat $shPath/sys.ini |grep @USERNAME |awk -F '=' '{printf $2}'`
FTP_PASSWORD=`cat $shPath/sys.ini |grep @PASSWD |awk -F '=' '{printf $2}'`

DBUSER=`cat $shPath/sys.ini |grep @DBUSER |awk -F '=' '{printf $2}'`
DBPASSWD=`cat $shPath/sys.ini |grep @DBPASSWD |awk -F '=' '{printf $2}'`
ASUSER=`cat $shPath/sys.ini |grep @ASUSER |awk -F '=' '{printf $2}'`
CONNSTR="${DBUSER}/${DBPASSWD}@${ASUSER}"

#上传到一体机服务器目录
PUT_YTJ_GRZH=`cat $shPath/sys.ini |grep @PUT_YTJ_GRZH |awk -F '=' '{printf $2}'`
putYtjFilePath="${PUT_YTJ_GRZH}"


sh ${shPath}/insert_ytj_log_zl.sh "swap_remote_zl.sh" ${oDataDate} 5

#########################################################################################
#                                                                             ###########
#  删除一体机应答目录文件                                                     ###########
#                                                                             ###########
#                                                                             ###########
#########################################################################################
#log "删除一体机应答目录下的文件开始..."
#/usr/bin/expect ${shPath}/tmp_dbacklist.ex ${FTP_USER} ${FTP_PASSWORD} ${FTP_IP}

#nameListStr=`ftp -i -n ${FTP_IP}<<EOF
#user $FTP_USER $FTP_PASSWORD
#cd /data/appdata/eaas/inter/fileback
#mdelete *
#bye
#EOF`

#log "删除一体机应答目录下的文件完毕!!!"

#########################################################################################
#                                                                             ###########
#  修改服务器上的文件名称                                                     ###########
#                                                                             ###########
#                                                                             ###########
#########################################################################################
while [ 1 ]
do
    log "获取远程服务器上[ /data/appdata/eaas/inter/bigfile ] 的需要修改名称的文件列表..."
    /usr/bin/expect ${shPath}/tmp_nameListStr.ex ${FTP_USER} ${FTP_PASSWORD} ${FTP_IP} ${putYtjFilePath} > ${shPath}/eaas/log/tmp_nameListStr.log
    
    cat ${shPath}/eaas/log/tmp_nameListStr.log |sed -ne '6,$p' |sed 's/.$//g' |awk '{printf "%s\n", $9}' >${shPath}/eaas/log/remoteStringtmp.txt
    log "要处理的文件列表存放在:[ ${shPath}/eaas/log ]的remoteStringtmp.txt文件中"

    log "检查有没有后缀为txt文件"
    flename=''
    while read line
    do
        fileNameTem=${line}
        if [ "${fileNameTem##*.}" = "txt" ];then
            flename=${line}
            log "txt文件flename:[${flename}]"
            break;
        fi
    done < ${shPath}/eaas/log/remoteStringtmp.txt

    g_filenameTemp=${flename}
    if [ "${g_filenameTemp##*.}" != "txt" ];then
            log "服务器上不存在txt文件"
            flename=''
            while read line
            
		    do
                fileNameTem=${line}
            if [ "${fileNameTem##*.}" = "tmp" ];then
                    flename=${line}
                    log "将要处理的文件flename:[${flename}]"
            break; 
	        fi
            done < ${shPath}/eaas/log/remoteStringtmp.txt
            v_RemoteFileName=${flename}
            v_RemoteFileNameTmp=${flename}
            v_changeSFileName=''
            if [ "${flename##*.}" = "tmp" ];then
                v_changeSFileName="${v_RemoteFileNameTmp%.*}.txt"
                log "将一体机上的文件[${v_RemoteFileName}]修改为[${v_changeSFileName}]"
            else
                log "服务器上没有要处理的文件, 退出循环"
                break
            fi
            log "修改/data/appdata/eaas/inter/bigfile下的[${v_RemoteFileName}] --> [ ${v_changeSFileName}] "
            /usr/bin/expect ${shPath}/tmp_pchangename.ex ${FTP_USER} ${FTP_PASSWORD} ${FTP_IP} ${v_RemoteFileName} ${v_changeSFileName} ${putYtjFilePath}> ${shPath}/eaas/log/tmp_pchangename.log
            
            sleep 1
            ##获取数据文件对应数据日期
                newDataDate=`echo ${v_changeSFileName}|awk -F "[" '{print $3}'|cut -b -8`
                log "文件名信息数据入库:[ ${v_changeSFileName} ]"
                upTaskTab=`sqlplus -S "${CONNSTR}"<<EOF
                           set heading off
                           set echo off
                           set feedback off
                           set term off
                           set pagesize 0
                           set linesize 1000
                           var ret number;
                           insert into ytj_bigfile_detail(data_date, up_file_name, change_file_name, status, create_time) 
                           values('${newDataDate}', '${v_RemoteFileName}', '${v_changeSFileName}', 'ready', sysdate);
                           commit;
                           exit;
EOF`
    else
        log "服务器上存在txt文件 [ ${flename} ]"
    fi
    log "-------------"
    sleep 20
done

log "所有文件都处理完毕!!!"
exit 0
