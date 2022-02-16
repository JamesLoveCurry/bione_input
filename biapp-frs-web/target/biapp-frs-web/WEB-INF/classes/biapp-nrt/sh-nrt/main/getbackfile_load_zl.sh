#!/bin/bash
#-----------------------------------------------------------------------------------------------
#
#脚本 sh getbackfile_load_zl.sh
#
#流程: 数据下载
#
#-----------------------------------------------------------------------------------------------
#获取当前程序所在目录
shPath=$(cd "$(dirname "$0")"; pwd)

shDate=$1
dataDate=$2
listFileNameTemp=$3
lineNum=$4
changeFileName=$5
selNumber=$6

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

FTP_IP=`cat $shPath/sys.ini |grep @FTP_IP |awk -F '=' '{printf $2}'`
FTP_USER=`cat $shPath/sys.ini |grep @USERNAME |awk -F '=' '{printf $2}'`
FTP_PASSWORD=`cat $shPath/sys.ini |grep @PASSWD |awk -F '=' '{printf $2}'`

DBUSER=`cat $shPath/sys.ini |grep @DBUSER |awk -F '=' '{printf $2}'`
DBPASSWD=`cat $shPath/sys.ini |grep @DBPASSWD |awk -F '=' '{printf $2}'`
ASUSER=`cat $shPath/sys.ini |grep @ASUSER |awk -F '=' '{printf $2}'`
CONNSTR="${DBUSER}/${DBPASSWD}@${ASUSER}"

GET_FILEBACK_PATH=`cat $shPath/sys.ini |grep @GET_FILEBACK_PATH |awk -F '=' '{printf $2}'`
vGetServerFiles=${GET_FILEBACK_PATH}/${shDate}
#if [  -d ${vGetServerFiles} ]; then
#   rm -rf ${vGetServerFiles}
#fi

if [ ! -d ${vGetServerFiles} ]; then
   mkdir -p ${vGetServerFiles}
fi

#上传到一体机服务器目录
PUT_YTJ_GRZH=`cat $shPath/sys.ini |grep @PUT_YTJ_GRZH |awk -F '=' '{printf $2}'`
putYtjFilePath="${PUT_YTJ_GRZH}"

GET_YTJ_GRZH_BACK=`cat $shPath/sys.ini |grep @GET_YTJ_GRZH_BACK |awk -F '=' '{printf $2}'`
getYtjGrzhBack="${GET_YTJ_GRZH_BACK}"

#########################################################################################
#                                                                             ###########
#  开始处理                                                                   ###########
#                                                                             ###########
#                                                                             ###########
#########################################################################################
log "shDate:[ ${shDate} ]"
log "listFileNameTemp:[ ${listFileNameTemp} ]"
log "lineNum:[ ${lineNum} ]"
log "changeFileName:[ ${changeFileName} ]"
log "要入库文件数量为lineNum:[${lineNum}]"
log "selNumber:[ ${selNumber} ]"

expData=`sqlplus -S "${CONNSTR}"<<EOF
                 set heading off
                 set echo off
                 set feedback off
                 set term off
                 set pagesize 0
                 set linesize 1000
                 var ret number;
                 delete ytj_fileback_detail where data_date='${dataDate}' and change_file_name='${changeFileName}';
                 commit;
                 exit;
EOF`


expData=`sqlplus -S "${CONNSTR}"<<EOF
                 set heading off
                 set echo off
                 set feedback off
                 set term off
                 set pagesize 0
                 set linesize 1000
                 var ret number;
                 delete ytj_fb_checkfile_detail where data_date='${dataDate}' and change_file_name='${changeFileName}';
                 commit;
                 exit;
EOF`

scankeystr="[[]${listFileNameTemp}[]][[]${selNumber}[]]"
log "开始获取服务器/data/appdata/eaas/inter/fileback 目录下的文件,包含 [ ${scankeystr} ]"
/usr/bin/expect tmp_sftp_getfile.ex ${FTP_USER} ${FTP_PASSWORD} ${FTP_IP} ${vGetServerFiles} ${scankeystr} ${getYtjGrzhBack}
log "/usr/bin/expect tmp_sftp_getfile.ex ${FTP_USER} ${FTP_PASSWORD} ${FTP_IP} ${vGetServerFiles} ${scankeystr} ${getYtjGrzhBack}"

        log "计算获取后的[ ${vGetServerFiles} ]文件数量"
        localFileNum=0
        #for element in `ls "${vGetServerFiles}"`
        for element in `ls "${vGetServerFiles}"|grep ${scankeystr}`
        do
              
              log "scankeystr:[  ${scankeystr}  ]"
              vfileNameTemFirst=`echo ${element}|grep ${scankeystr}`
              if [ "${vfileNameTemFirst##*.}" = "txt" ];then
                     expData=`sqlplus -S "${CONNSTR}"<<EOF
                              set heading off
                              set echo off
                              set feedback off
                              set term off
                              set pagesize 0
                              set linesize 1000
                              var ret number;
                                 insert into ytj_fileback_detail(data_date, change_file_name, fileback_name, create_time) 
                                 values('${dataDate}', '${changeFileName}', '${vfileNameTemFirst}', sysdate);
                              commit;
                              exit;
EOF`
                     log "expData:[ ${expData} ]"
                     localFileNum=$((${localFileNum}+1));
              fi
    
              vfileNameTem=`echo ${vfileNameTemFirst}|grep err`
              if [ "${vfileNameTem##*.}" = "txt" ];then
                      expDatafb=`sqlplus -S "${CONNSTR}"<<EOF
                                 set heading off
                                 set echo off
                                 set feedback off
                                 set term off
                                 set pagesize 0
                                 set linesize 1000
                                 var ret number;
                                         insert into ytj_fb_checkfile_detail(data_date, change_file_name, fileback_name, create_time) 
                                         values('${dataDate}', '${changeFileName}', '${vfileNameTem}', sysdate);
                                         commit;
                                 exit;
EOF`
                      log "expDatafb:[ ${expDatafb} ]"
              fi
        done

        log "下载下来的要入库的文件数量为localFileNum:[${localFileNum}]"

        if [ ${localFileNum} -gt "0" ] && [ ${localFileNum} -eq ${lineNum} ]; then
            log "从一体机下载处理结果文件完毕"
        else
            log "从一体机下载处理结果文件完毕,对应不上"
        fi

exit 0
