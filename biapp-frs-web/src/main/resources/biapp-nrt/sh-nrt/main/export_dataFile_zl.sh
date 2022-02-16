#!/bin/bash
#***************************************************
#
#脚本名称 : export_dataFile_zl.sh
#脚本功能 : 检测是否具备卸载数据条件,具备就卸载数据
#创建日期 : 20190305
#作    者 : bwn
#版    本 : v_1.0
#
#***************************************************
#获取当前程序所在目录
shPath=$(cd "$(dirname "$0")"; pwd)
shDate=`date +"%Y%m%d"`
oDataDate=$1

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
DBUSER=`cat $shPath/sys.ini |grep @DBUSER |awk -F '=' '{printf $2}'`
DBPASSWD=`cat $shPath/sys.ini |grep @DBPASSWD |awk -F '=' '{printf $2}'`
ASUSER=`cat $shPath/sys.ini |grep @ASUSER |awk -F '=' '{printf $2}'`
CONNSTR="${DBUSER}/${DBPASSWD}@${ASUSER}"
FILE_PATH=`cat $shPath/sys.ini |grep @FILE_PATH |awk -F '=' '{printf $2}'`

sh ${shPath}/insert_ytj_log_zl.sh "export_dataFile_zl.sh" ${oDataDate} 2

#########################################################################################
#                                                                             ###########
#         获取数据日期                                                        ###########
#                                                                             ###########
#########################################################################################
data_dt=''
if [ $# -eq 1 ];then
    data_dt=$1
else
    while [ "${data_dt}_N" = '_N' ]
    do
        data_dt=`sqlplus -S "${CONNSTR}"<<EOF
                 set heading off;
				         set echo off;
				         set feedback off;
				         set term off;
				         set pagesize 0;
				         set linesize 1000;
				         SELECT ETL_DATE  FROM etl_date;
				         exit;
EOF`
        if [ "${data_dt}_N" = '_N' ];then
            log "获取当前数据日期失败等待30秒后继续获取。。。"
            sleep 30
        fi
    done
fi
log "当前数据日期:[${data_dt}]"

#########################################################################################
#                                                                             ###########
#         检测是否具备卸载数据条件                                            ###########
#                                                                             ###########
#########################################################################################
#检测具备不具备开始卸载数据条件 ACB_INDIV_BANK_ACCT_SUMMIT: Statistics_Dt
V_STATE=`sqlplus -S "${CONNSTR}"<<EOF
         set heading off;
				 set echo off;
				 set feedback off;
				 set term off;
				 set pagesize 0;
				 set linesize 1000;
				 SELECT to_char(T1.STATISTICS_DT,'yyyymmdd') FROM ACB_INDIV_BANK_ACCT_SUMMIT T1 WHERE T1.STATISTICS_DT=to_date('${data_dt}','yyyymmdd') AND ROWNUM=1;
				 exit;
EOF`
log "是否具备卸载数据条件: V_STATE:[${V_STATE}] = data_dt:[${data_dt}]"
if [ "${V_STATE}_N" != "${data_dt}_N" ]; then
   log " 卸载条件不具备"
   exit 1
fi

#########################################################################################
#                                                                             ###########
#         条件具备开始卸载数据                                                ###########
#                                                                             ###########
#########################################################################################
#一代个人账户报送
if [ $# -eq 1 ];then
    sqlstr="SELECT DEPOSIT_NAME AS "存款人名称",DEPOSIT_ADDR AS "地址",DEPOSIT_POST_CD AS "邮政编码",DEPOSIT_TEL AS "电话",DEPOSIT_IDENTITY_TYPE_CD AS "身份证件种类",DEPOSIT_IDENTITY_NUM AS "身份证件编号",OPEN_BANK_FIN_ORG_CD AS "开户银行机构代码",BANK_ACCT_NUM AS "账号",BANK_ACCT_TYPE_CD AS "账户类型",OPEN_DT AS "开户日期",CARD_DUE_DT AS "有效日期",DEPOSIT_CLASS_CD AS "存款人身份类别",ISSUE_ORG_AREA_CD AS "发证机关所在地区代码",INFO_TYPE_CD AS "信息类型" FROM ACB_INDIV_BANK_ACCT_SUMMIT WHERE STATISTICS_DT=TO_DATE("
    sqlstr="${sqlstr}$1"
    sqlstr="${sqlstr}, 'SYYYY-MM-DD HH24:MI:SS')"
else
log "jjjjjjjjjjj"
    sqlstr="select  to_char(\'存款人名称\') from dual union all select DEPOSIT_NAME from  ACB_INDIV_BANK_ACCT_SUMMIT"
fi
log "sqlstr:[${sqlstr}]"
#一代个人账户数据文件存放目录处理
vExpFileDIR="${FILE_PATH}/${shDate}"
if [ ! -d ${vExpFileDIR} ];then
	mkdir -p ${vExpFileDIR}
	log "创建一代个人账户目录[$vExpFileDIR]成功"
fi
vToday=`date +%H%M%S`
log "一代账户数据卸载开始"
log "${shPath}/sqluldr user=${CONNSTR}"
echo $1
${shPath}/sqluldr user="${CONNSTR}" sql=aa.sql field=0x7C record=0x0a size=480MB file="${vExpFileDIR}/ACCT_${shDate}_$1${vToday}.txt" head=yes charset=ZHS16GBK  safe=yes
returnVal=$?
if [ ${returnVal} -ne 0 ]; then
  log "一代账户拆分数据导出结果:[${returnVal}]"
  log "一代账户拆分数据卸载任务失败结束"
  exit 200
else
  log "一代账户拆分数据导出结果:[${returnVal}]"
  log "一代账户拆分数据卸载任务成功结束"
fi
upTaskTab=`sqlplus -S "${CONNSTR}"<<EOF
                       set heading off
                       set echo off
                       set feedback off
                       set term off
                       set pagesize 0
                       set linesize 1000
                       var ret number;
                       insert into RPT_HIS_FILE_DATA values(GRZH_SEQ.NEXTVAL,'${V_STATE}','ACCT_${shDate}_$1${vToday}.txt','${vExpFileDIR}/','9999','');
                       commit;
                       exit;
EOF`
log "插入历史数据下载表返回值为：[  ${upTaskTab} ]"
#组装卸载数据字段
if [ $# -eq 1 ];then
    sqlstr="SELECT DEPOSIT_NAME,DEPOSIT_IDENTITY_TYPE_CD,DEPOSIT_IDENTITY_NUM,IDENTITY_DUE_DT,ISSUE_ORG_AREA_CD,DEPOSIT_CLASS_CD,DEPOSIT_COUNTRY_CD,DEPOSIT_SEX_CD,DEPOSIT_POST_CD,DEPOSIT_ADDR,DEPOSIT_TEL,AGENT_NAME,AGENT_IDENTITY_TYPE_CD,AGENT_IDENTITY_NUM,AGENT_COUNTRY_CD,AGENT_TEL,OPEN_BANK_FIN_ORG_CD,BANK_ACCT_NUM,BANK_ACCT_KIND_CD,CARD_CD,CARD_DUE_DT,BANK_ACCT_MEDIA,EXPIRE_CARD_DT,CARD_STAT_CD,BANK_ACCT_TYPE_CD,BINDING_I_ACCT_NUM,BINDING_I_FIN_ORG_CD,OPEN_DT,EXPIRE_DT,ACCT_STAT_CD,CURRENCY_CD,ARMY_SECURITY_CARD,SOCIETY_SECURITY_CARD,AUDIT_RESULT_CD,NO_AUDIT_RESULT_DESC,DISPOSE_MODE,INFO_TYPE_CD,OPEN_CHANNAL_CD,REMARK,COLUMN1,COLUMN2,COLUMN3,COLUMN4,COLUMN5 FROM ACB_INDIV_BANK_ACCT_SUMMIT WHERE STATISTICS_DT=TO_DATE("
    sqlstr="${sqlstr}$1"
    sqlstr="${sqlstr}, 'SYYYY-MM-DD HH24:MI:SS')"
else
   sqlstr="SELECT DEPOSIT_NAME,DEPOSIT_IDENTITY_TYPE_CD,DEPOSIT_IDENTITY_NUM,IDENTITY_DUE_DT,ISSUE_ORG_AREA_CD,DEPOSIT_CLASS_CD,DEPOSIT_COUNTRY_CD,DEPOSIT_SEX_CD,DEPOSIT_POST_CD,DEPOSIT_ADDR,DEPOSIT_TEL,AGENT_NAME,AGENT_IDENTITY_TYPE_CD,AGENT_IDENTITY_NUM,AGENT_COUNTRY_CD,AGENT_TEL,OPEN_BANK_FIN_ORG_CD,BANK_ACCT_NUM,BANK_ACCT_KIND_CD,CARD_CD,CARD_DUE_DT,BANK_ACCT_MEDIA,EXPIRE_CARD_DT,CARD_STAT_CD,BANK_ACCT_TYPE_CD,BINDING_I_ACCT_NUM,BINDING_I_FIN_ORG_CD,OPEN_DT,EXPIRE_DT,ACCT_STAT_CD,CURRENCY_CD,ARMY_SECURITY_CARD,SOCIETY_SECURITY_CARD,AUDIT_RESULT_CD,NO_AUDIT_RESULT_DESC,DISPOSE_MODE,INFO_TYPE_CD,OPEN_CHANNAL_CD,REMARK,COLUMN1,COLUMN2,COLUMN3,COLUMN4,COLUMN5  FROM ACB_INDIV_BANK_ACCT_SUMMIT"
fi
log "sqlstr:[${sqlstr}]"

#文件导出存放目录
PUT_YTJ_PATH=`cat $shPath/sys.ini |grep @PUT_YTJ_PATH |awk -F '=' '{printf $2}'`
#vExpFileDIR="${PUT_YTJ_PATH}/${oDataDate}"
vExpFileDIR="${PUT_YTJ_PATH}"
#if [ -d ${vExpFileDIR} ];then
#	rm -rf  ${vExpFileDIR}
#	log "删除目录[${vExpFileDIR}]成功"
#fi

if [ ! -d ${vExpFileDIR} ];then
	mkdir -p ${vExpFileDIR}
	log "创建目录[${vExpFileDIR}]成功"
fi

vExpFileName="${vExpFileDIR}/[C1087964000012]"

#年月日时分秒14位
vExpFileName="${vExpFileName}[$1${vToday}].%b.tmp"
log "导出文件名:[${vExpFileName}]"

log "拆分数据卸载开始"
log "${shPath}/sqluldr user=${CONNSTR}"
${shPath}/sqluldr user="${CONNSTR}" query="${sqlstr}" field=0x7C record=0x0a size=480MB file="${vExpFileName}" charset=ZHS16GBK head=yes safe=yes
returnVal=$?
if [ ${returnVal} -ne 0 ]; then
  log "拆分数据导出结果:[${returnVal}]"
  log "拆分数据卸载任务失败结束"
  exit 200
else
  log "拆分数据导出结果:[${returnVal}]"
  log "拆分数据卸载任务成功结束"
fi
log "数据卸载处理完毕!!![${vExpFileName}]"

exit 0
