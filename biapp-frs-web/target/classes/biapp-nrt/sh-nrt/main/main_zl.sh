#!/bin/bash

#***************************************************
#
#脚本名称 : main_zl.sh
#脚本功能 : 主流程
#创建日期 : 20190305
#作    者 : bwn
#版    本 : v_1.0
#
#***************************************************
#########################################################################################
#                                                                             ###########
#  获取参数数据                                                               ###########
#        参数1: 日期                                                          ###########
#        参数2: 全量(ql)/增量(zl)                                             ###########
#                                                                             ###########
#########################################################################################
#获取当前程序所在目录
shPath=$(cd "$(dirname "$0")"; pwd)
shDate=`date +"%Y%m%d"`

parameterNum=$#
if [ ${parameterNum} -ne 2 ]; then
   echo "参数传输错误！" >> ${shPath}/eaas/log/main_err.log
   exit 1
fi
oDataDate=$1
procName=$2

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

sysTime=`date +"%Y%m%d%H%M%S"`
status=''
sh ${shPath}/insert_ytj_log_zl.sh "${sysTime}" "${oDataDate}" "main_zl.sh"  "0" "${status}" "1"
#########################################################################################
#                                                                             ###########
#  初始参数区                                                                 ###########
#                                                                             ###########
#                                                                             ###########
#########################################################################################
log "步骤一: [ 初始化参数... ]"
log "传输参数个数parameterNum[${parameterNum}]"
log "获取的参数1:[${oDataDate}]  获取的参数2:[${procName}]"


#########################################################################################
#                                                                             ###########
#  检测O端数据是否装载完毕 --> 执行存储过程 PROC_ACB_ACCT_SUMMIT              ###########
#  ACB_INDIV_BANK_ACCT7    --> ACB_INDIV_BANK_ACCT_SUMMIT                     ###########
#                                                                             ###########
#########################################################################################
log "  "
log "步骤二: [ 开始执行: proc_run_zl.sh... ]"

sh ${shPath}/proc_run_zl.sh ${oDataDate} ${procName}
shReturnCode=$?
if [ ${shReturnCode} -eq 0 ]; then
    log "O端数据装载并处理成功!!!shReturnCode:[${shReturnCode}]"
else
    log "O端数据装载并处理失败!!!shReturnCode:[${shReturnCode}]"
    exit 1
fi

#########################################################################################
#                                                                             ###########
#  导出ACB_INDIV_BANK_ACCT_SUMMIT表中数据                                     ###########
#                                                                             ###########
#########################################################################################
log "  "
log "步骤三: [ 开始执行: ${shPath}/export_dataFile_zl.sh ${oDataDate}... ]"
log "将数据从表[ ACB_INDIV_BANK_ACCT_SUMMIT ]中导出"
sh ${shPath}/export_dataFile_zl.sh ${oDataDate}
shReturnCode=$?
if [ ${shReturnCode} -eq 0 ]; then
    log "表[ACB_INDIV_BANK_ACCT_SUMMIT]数据导出处理成功!!!shReturnCode:[${shReturnCode}]"
else
    log "表[ACB_INDIV_BANK_ACCT_SUMMIT]数据导出处理失败!!!shReturnCode:[${shReturnCode}]"
   exit 1
fi

#########################################################################################
#                                                                             ###########
#  将导出数据文件名修改为一体机规定的格式                                     ###########
#                                                                             ###########
#########################################################################################
log "  "
log "步骤四: [ 开始执行: ${shPath}/export_changeFileName_zl.sh... ]"
log "将导出的数据文件进行名称修改"

sh ${shPath}/export_changeFileName_zl.sh ${oDataDate}
shReturnCode=$?
if [ ${shReturnCode} -eq 0 ]; then
    log "导出数据文件名称修改处理成功!!!shReturnCode:[${shReturnCode}]"
else
    log "导出数据文件名称修改处理失败!!!shReturnCode:[${shReturnCode}]"
    exit 1
fi

#########################################################################################
#                                                                             ###########
#  将文件传输到一体机                                                         ###########
#                                                                             ###########
#########################################################################################
#log "  "
#log "步骤五: [ 开始执行: ${shPath}/sftp_put_file_zl.sh... ]"
#log "将文件上传到一体机"
#if [ ${shDate} -ge 20190401 ]; then
#   sh ${shPath}/sftp_put_file_zl.sh ${oDataDate}
#   shReturnCode=$?
#   if [ ${shReturnCode} -eq 0 ]; then
#       log "文件上传到一体机处理成功!!!shReturnCode:[${shReturnCode}]"
#   else
#       log "文件上传到一体机处理失败!!!shReturnCode:[${shReturnCode}]"
#       exit 1
#   fi
#else
#   log "未到人行要求增量报送日期"
#   exit 0
#fi

#########################################################################################
#                                                                             ###########
#  让一体机开始处理                                                           ###########
#                                                                             ###########
#########################################################################################
#log "  "
#log "步骤六: [ 开始执行: ${shPath}/swap_remote_zl.sh... ]"
#log "让一体机开始处理..."
#sh ${shPath}/swap_remote_zl.sh ${oDataDate} yes
#shReturnCode=$?
#if [ ${shReturnCode} -eq 0 ]; then
#    log "一体机循环处理成功!!!shReturnCode:[${shReturnCode}]"
#else
#    log "一体机循环处理失败!!!shReturnCode:[${shReturnCode}]"
#    exit 1
#fi

#########################################################################################
#                                                                             ###########
#  从一体机下载处理结果文件                                                   ###########
#                                                                             ###########
#########################################################################################
#log "  "
#log "步骤七: [ 开始执行: ${shPath}/sftp_getfile.sh... ]"
#log "从一体机下载处理结果文件..."
#sh ${shPath}/sftp_getfile.sh ${oDataDate}
#shReturnCode=$?
#if [ ${shReturnCode} -eq 0 ]; then
#    log "从一体机下载结果文件处理成功!!!shReturnCode:[${shReturnCode}]"
#else
#    log "从一体机下载结果文件处理失败!!!shReturnCode:[${shReturnCode}]"
#    exit 600
#fi

#########################################################################################
#                                                                             ###########
#  将一体机返回的数据加载到数据库                                             ###########
#                                                                             ###########
#########################################################################################
#log "  "
#log "步骤八: [ 开始执行: ${shPath}/loaddata_ytj_back.sh ${oDataDate}... ]"
#log "将一体机返回的数据加载到数据库..."
#sh ${shPath}/loaddata_ytj_back.sh ${oDataDate}
#shReturnCode=$?
#if [ ${shReturnCode} -eq 0 ]; then
#    log "对一体机返回的数据加载到数据库等操作处理成功!!!shReturnCode:[${shReturnCode}]"
#else
#    log "对一体机返回的数据加载到数据库等操作处理失败!!!shReturnCode:[${shReturnCode}]"
#    exit 700
#fi

#########################################################################################
#                                                                             ###########
#  完毕                                                                       ###########
#                                                                             ###########
#########################################################################################

#sysTime=`date +"%Y%m%d%H%M%S"`
status='success'
sh ${shPath}/insert_ytj_log_zl.sh "${sysTime}" "${oDataDate}" "main.sh"  "0"  "${status}" "2"

log "全部处理成功！！！"
