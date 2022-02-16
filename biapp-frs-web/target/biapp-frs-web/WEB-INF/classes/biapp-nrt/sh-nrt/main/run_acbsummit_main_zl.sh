#!/bin/bash
#sh run_acbsummit_main_zl.sh

#获取当前程序所在目录
shPath=$(cd "$(dirname "$0")"; pwd)
DBUSER=`cat $shPath/sys.ini |grep @DBUSER |awk -F '=' '{printf $2}'`
DBPASSWD=`cat $shPath/sys.ini |grep @DBPASSWD |awk -F '=' '{printf $2}'`
ASUSER=`cat $shPath/sys.ini |grep @ASUSER |awk -F '=' '{printf $2}'`
CONNSTR="${DBUSER}/${DBPASSWD}@${ASUSER}"
stepResult=1

#上传到一体机服务器目录
PUT_YTJ_GRZH=`cat $shPath/sys.ini |grep @PUT_YTJ_GRZH |awk -F '=' '{printf $2}'`
putYtjFilePath="${PUT_YTJ_GRZH}"

GET_YTJ_GRZH_BACK=`cat $shPath/sys.ini |grep @GET_YTJ_GRZH_BACK |awk -F '=' '{printf $2}'`
getYtjGrzhBack="${GET_YTJ_GRZH_BACK}"


while [ 1 ]
do
  shDate=`date +"%Y%m%d"`

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
   
###获取当前时间点
   nowtime=`date +"%H%M%S"`
   
   log "check the time gt 9:10 and lt 16:00 [ ${nowtime} ]"
   if [ ${nowtime} -gt 91000 ] && [ ${nowtime} -lt 210000 ]; then
        acbsummit=`sqlplus -S "${CONNSTR}"<<EOF
                   set heading off
                   set echo off
                   set feedback off
                   set term off
                   set pagesize 0
                   set linesize 1000
                   var ret number;
                   select  rowid || '|' || data_date || '|' || proc_name from (select * from ytj_work_acbsummit t  where t.status='ready' order by t.data_date asc) where rownum = 1;
                   exit;
EOF`
        log "acbsummit[ ${acbsummit} ]"
        if [ "${acbsummit}_N" != "_N" ]; then
            dataRowid=`echo ${acbsummit} |awk -F '|' '{printf $1}'`
            dataDate=`echo ${acbsummit} |awk -F '|' '{printf $2}'`
            procName=`echo ${acbsummit} |awk -F '|' '{printf $3}'`
            stepResult=0
        else
            stepResult=1
        fi

        if [ ${stepResult} -eq 0 ];then
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
            sh ${shPath}/update_ytjworkacbsummit_zl.sh ${shDate} 'running' '执行中' ${dataRowid}
            sh ${shPath}/proc_run_acb_summit.sh ${dataDate}
##            ETL_RESULT=`sqlplus -S "${CONNSTR}"<<EOF
##		 		                set heading off;
##		 		                set echo off;
##		 		                set feedback off;
##		 		                set term off;
##		 		                set pagesize 0;
##		 		                set linesize 2048;
##		 		                var ora_return_code VARCHAR2;
##		 		                exec PROC_ACB_SUMMIT('${dataDate}',:ora_return_code);
##		 		                select :ora_return_code from dual;
##		 		                exit;
##EOF`
            ETL_RESULT=$?
            log "存储过程PROC_ACB_SUMMIT执行完毕--执行结果:[${ETL_RESULT}]"
            if [ ${ETL_RESULT} -eq 0 ]; then
                log "ok..."
                stepResult=0
            else
                log "存储过程[PROC_ACB_SUMMIT]执行失败,请检查原因"
                stepResult=2
            fi       
        fi

        if [ ${stepResult} -eq 0 ];then
                V_STATE=`sqlplus -S "${CONNSTR}"<<EOF
				                 set heading off;
				                 set echo off;
				                 set feedback off;
				                 set term off;
				                 set pagesize 0;
				                 set linesize 1000;
				                 SELECT to_char(T1.STATISTICS_DT,'yyyymmdd') FROM ACB_SUMMIT T1 WHERE T1.STATISTICS_DT=to_date('${dataDate}','yyyymmdd') AND ROWNUM=1;
				                 exit;
EOF`
                log "ACB_SUMMIT表是否具备卸载数据条件: V_STATE:[${V_STATE}]  dataDate:[${dataDate}]"
                if [ "${V_STATE}_N" != "${dataDate}_N" ]; then
                    log "没有需要卸载的数据"
                    stepResult=3
                else
                    stepResult=0
                fi
        fi

        if [ ${stepResult} -eq 0 ];then
            sqlstr="SELECT DEPOSIT_NAME,DEPOSIT_IDENTITY_TYPE_CD,DEPOSIT_IDENTITY_NUM,IDENTITY_DUE_DT,ISSUE_ORG_AREA_CD,DEPOSIT_CLASS_CD,DEPOSIT_COUNTRY_CD,DEPOSIT_SEX_CD,DEPOSIT_POST_CD,DEPOSIT_ADDR,DEPOSIT_TEL,AGENT_NAME,AGENT_IDENTITY_TYPE_CD,AGENT_IDENTITY_NUM,AGENT_COUNTRY_CD,AGENT_TEL,OPEN_BANK_FIN_ORG_CD,BANK_ACCT_NUM,BANK_ACCT_KIND_CD,CARD_CD,CARD_DUE_DT,BANK_ACCT_MEDIA,EXPIRE_CARD_DT,CARD_STAT_CD,BANK_ACCT_TYPE_CD,BINDING_I_ACCT_NUM,BINDING_I_FIN_ORG_CD,OPEN_DT,EXPIRE_DT,ACCT_STAT_CD,CURRENCY_CD,ARMY_SECURITY_CARD,SOCIETY_SECURITY_CARD,AUDIT_RESULT_CD,NO_AUDIT_RESULT_DESC,DISPOSE_MODE,INFO_TYPE_CD,OPEN_CHANNAL_CD,REMARK,COLUMN1,COLUMN2,COLUMN3,COLUMN4,COLUMN5  FROM ACB_SUMMIT  WHERE STATISTICS_DT=TO_DATE("
            sqlstr="${sqlstr}${dataDate}"
            sqlstr="${sqlstr}, 'SYYYY-MM-DD HH24:MI:SS')"
            
            putYtjPath=`cat $shPath/sys.ini |grep @PUT_YTJ_NEXT_PATH |awk -F '=' '{printf $2}'`
            expFileDir="${putYtjPath}/${shDate}"
            if [ -d ${expFileDir} ];then
	              rm -rf  ${expFileDir}
	              log "删除目录[${expFileDir}]成功"
            fi

            if [ ! -d ${expFileDir} ];then
	              mkdir -p ${expFileDir}
	              log "创建目录[${expFileDir}]成功"
            fi

            expFileName="${expFileDir}/[C1087964000012]"

            #年月日时分秒14位
            nowToday=`date +%H%M%S`
            expFileName="${expFileName}[${dataDate}${nowToday}].%b.tmp"
            log "导出文件名:[${expFileName}]"
            
            log "拆分数据卸载开始 sqluldr文件路径[ ${shPath}/sqluldr ]"
            ${shPath}/sqluldr user="${CONNSTR}" query="${sqlstr}" field=0x7C record=0x0a size=480MB file="${expFileName}" charset=ZHS16GBK head=yes safe=yes
            returnVal=$?
            if [ ${returnVal} -ne 0 ]; then
                log "拆分数据导出结果:[${returnVal}]"
                log "拆分数据卸载任务失败结束"
                stepResult=4
            else
                log "拆分数据导出结果:[${returnVal}]"
                log "拆分数据卸载任务成功结束"
                stepResult=0
            fi   
        fi

        if [ ${stepResult} -eq 0 ];then
            if [ -d ${expFileDir} ]; then
                cd ${expFileDir}
                i=0;
                for element in `ls -lrt|awk '{ print $(NF) }'|grep '.tmp'`
                do
                        fieldStr=`echo "${element}"|awk -F "." '{printf $1}'`
                        i=$(($i+1));
                        s=`printf "%02d" "${i}"`
                        ytjFileName="${fieldStr}[${s}].tmp"
                        log "二次导出数据文件后缀修改: element:[${element}]   ytjFileName:[${ytjFileName}]";
                        if [ -f ${element} ]; then
                            mv ${element} ${ytjFileName}
                            log "${element} --> ${ytjFileName}更改成功"
                        else
                            log "[${element}]文件不存在,不修改"
                        fi
                done
            fi
        fi

        if [ ${stepResult} -eq 0 ];then
                FTP_IP=`cat $shPath/sys.ini |grep @FTP_IP |awk -F '=' '{printf $2}'`
                FTP_USER=`cat $shPath/sys.ini |grep @USERNAME |awk -F '=' '{printf $2}'`
                FTP_PASSWORD=`cat $shPath/sys.ini |grep @PASSWD |awk -F '=' '{printf $2}'`

                log "开始调用${shPath}/tmp_sftp_putfile_second.ex"
                /usr/bin/expect ${shPath}/tmp_sftp_putfile_second.ex $FTP_USER $FTP_PASSWORD ${FTP_IP} ${expFileDir} ${putYtjFilePath}
                log "sftp 上传结果： [ ${?} ]"
        fi

        if [ ${stepResult} -eq 0 ];then
            log "执行 [swap_next_zl.sh] ${shDate} no"
            sh ${shPath}/swap_next_zl.sh ${shDate} no
        fi

        if [ ${stepResult} -eq 0 ];then
            log "aaaaa--执行 [update_ytjworkacbsummit_zl.sh] ${shDate} 'success' '成功' ${dataRowid}"
            sh ${shPath}/update_ytjworkacbsummit_zl.sh ${shDate} 'success' '成功' ${dataRowid}
        fi

        if [ ${stepResult} -eq 2 ];then
            log "bbbbb-- [update_ytjworkacbsummit_zl.sh] ${shDate} 'failed' '存储过程[PROC_ACB_SUMMIT]执行失败' ${dataRowid}"
            sh ${shPath}/update_ytjworkacbsummit_zl.sh ${shDate} 'failed' '存储过程[PROC_ACB_SUMMIT]执行失败' ${dataRowid}
        fi

        if [ ${stepResult} -eq 3 ];then
            log "ccccc-- [update_ytjworkacbsummit_zl.sh] ${shDate} 'success' '无需要卸载的数据' ${dataRowid}"
            sh ${shPath}/update_ytjworkacbsummit_zl.sh ${shDate} 'success' '无需要卸载的数据' ${dataRowid}   
        fi

        if [ ${stepResult} -eq 4 ];then
            log "ddddd-- [update_ytjworkacbsummit_zl.sh] ${shDate} 'failed' 'sqlstr数据卸载失败[${returnVal}]' ${dataRowid}"
            sh ${shPath}/update_ytjworkacbsummit_zl.sh ${shDate} 'failed' 'sqlstr数据卸载失败[${returnVal}]' ${dataRowid} 
        fi

        sleep 60
   else
      log "不在二次报送时间范围内"
      sleep 300
   fi
done
