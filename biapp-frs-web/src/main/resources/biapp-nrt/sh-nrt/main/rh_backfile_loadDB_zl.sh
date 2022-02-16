#!/bin/bash
# sh rh_backfile_loadDB_zl.sh

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
logPath=${shPath}/eaas/log/${shDate}/sqlldrlog_rh
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
log "人行返回数据装载开始"
#加载数据控制文件配置路径
loaddata_ctl_dir=${shPath}/ctl
if [ ! -d ${loaddata_ctl_dir} ]; then
    log "${loaddata_ctl_dir} 不存在"
    exit 100
fi

DBUSER=`cat $shPath/sys.ini |grep @DBUSER |awk -F '=' '{printf $2}'`
DBPASSWD=`cat $shPath/sys.ini |grep @DBPASSWD |awk -F '=' '{printf $2}'`
ASUSER=`cat $shPath/sys.ini |grep @ASUSER |awk -F '=' '{printf $2}'`
CONNSTR="${DBUSER}/${DBPASSWD}@${ASUSER}"

GET_RH_PATH=`cat $shPath/sys.ini |grep @GET_RH_PATH |awk -F '=' '{printf $2}'`
vGetServerFiles=${GET_RH_PATH}/${shDate}
#########################################################################################
#                                                                             ###########
#  加载数据/data/appdata/eaas/tlq/file/                                       ###########
#  rf:格式校验失败      -->   ACB_ERROR_RHG                                   ###########
#  rl:表示逻辑校验失败  -->   ACB_ERROR_RHL                                   ###########
#                                                                             ###########
#########################################################################################
job_rhg=acb_error_rhg
job_rhl=acb_error_rhl
log "vGetServerFiles: [ ${vGetServerFiles} ]"
for element in `ls "${vGetServerFiles}"`
do
    log "element:[ ${element} ]"
    
    vfileNameTemFirst=`echo ${element}|grep ^[\[]`
    log "vfileNameTemFirst: [ ${vfileNameTemFirst} ]"

##将人行返回报文名称入库报备
   data_date=`echo ${vfileNameTemFirst}|awk -F\[ '{print $4}'|cut -b -8`
   fileback_name=`echo ${vfileNameTemFirst}|awk -F . '{print $1}'`
   fileback_type=`echo ${fileback_name}|awk -F\] '{print $8}'`
   file_size=`ls -l ${vGetServerFiles}/${vfileNameTemFirst}|awk '{print $5}'`
       
     expData=`sqlplus -S "${CONNSTR}"<<EOF
                           set heading off
                           set echo off
                           set feedback off
                           set term off
                           set pagesize 0
                           set linesize 1000
                           var ret number;
                              insert into ytj_rh_backfile_detail(back_date, data_date, fileback_name, fileback_type,file_size,create_time,proc_flag) 
                              values('${shDate}', '${data_date}', '${fileback_name}', '${fileback_type}','${file_size}',sysdate,'ready');
                           commit;
                           exit;
EOF`
     log "expData:[ ${expData} ]"
   
##将人行返回报文入库  
   
    vfileNameTem=`echo ${vfileNameTemFirst}|grep rf`
    log "vfileNameTem: [ ${vfileNameTem} ] "
    if [ "${vfileNameTem##*.}" = "txt" ];then
       V_DATE_RFG=`echo $vfileNameTem|awk -F\[ '{print $4}'|cut -b -8`
       log "V_DATE_RFG:【${V_DATE_RFG}】"
       ##删除该日期已存在数据
       ##expData_rhg=`sqlplus -S "${CONNSTR}"<<EOF
       ##          set heading off
       ##          set echo off
       ##          set feedback off
       ##          set term off
       ##          set pagesize 0
       ##          set linesize 1000
       ##          var ret number;
       ##          delete from ACB_ERROR_RHG where data_date='${V_DATE_RFG}' ;
       ##          commit;
       ##          exit;
##EOF`
       ##log " expData_rhg [ ${expData_rhg} ]"
       
       formatstr="s/XXXXXXXX/${V_DATE_RFG}/g"
       sed  "${formatstr}" ${shPath}/ctl/acb_error_rhg_tmp.ctl >${shPath}/ctl/acb_error_rhg.ctl
       if [ $? = 0 ];then
           sqlldr userid=${CONNSTR} control=${loaddata_ctl_dir}/${job_rhg}.ctl data=${vGetServerFiles}/${vfileNameTem} skip=3 log=${logPath}/${job_rhg}_${vfileNameTem}.log bad=${logPath}/${job_rhg}_${vfileNameTem}.bad direct=true rows=10000 bindsize=10000 errors=10000000 >/dev/null
           SQLLDR=$?
           if [ "${SQLLDR}_N" != '_N'  ];then
                   if [ $SQLLDR -eq 0 ];then
                           log "[ ${element} ]数据加载成功sqlldr返回值为[ $? ]"
                           loadDataNum=$((${loadDataNum}+1));
                
                           ## ETL_RESULT=`sqlplus -S "${CONNSTR}"<<EOF
                           ##             set heading off;
                           ##             set echo off;
                           ##             set feedback off;
                           ##             set term off;
                           ##             set pagesize 0;
                           ##             set linesize 2048;
                           ##             var ora_return_code VARCHAR2(1000);
                           ##             exec PROC_ACB_ERROR_RHG(${V_DATE_RFG},:ora_return_code);
                           ##             select :ora_return_code from dual;
                           ##             exit;
##EOF`
                           ##
                           ## log "PROC_ACB_ERROR_RHG proc is result:[${ETL_RESULT}]";
                           ##log "PROC_ACB_ERROR_RHG处理完毕!!!"
                   else
                           log "[ ${element} ]数据加载失败sqlldr返回值为[ $? ]"
                   fi
           else
                   log "[ ${element} ]数据加载失败sqlldr返回值为[ $? ], 请检查加载文件数据"
           fi
       else
           log "sed fail 数据未加载[ ${vGetServerFiles}/${vfileNameTem} ]"
       fi
    fi
    
    
    vfileNameTem=`echo ${vfileNameTemFirst}|grep rl`
    if [ "${vfileNameTem##*.}" = "txt" ];then
       V_DATE_RHL=`echo $vfileNameTem|awk -F\[ '{print $4}'|cut -b -8`
       log "V_DATE_RHL:【${V_DATE_RHL}】"
       ##删除该日期已存在数据
       ##expData_rhl=`sqlplus -S "${CONNSTR}"<<EOF
       ##          set heading off
       ##          set echo off
       ##          set feedback off
       ##          set term off
       ##          set pagesize 0
       ##          set linesize 1000
       ##          var ret number;
       ##          delete from ACB_ERROR_RHL where data_date='${V_DATE_RHL}' ;
       ##          commit;
       ##          exit;
##EOF`
       ##log "expData_rhl : [ ${expData_rhl} ]"
       
       formatstr="s/XXXXXXXX/${V_DATE_RHL}/g"
       sed  "${formatstr}" ${shPath}/ctl/acb_error_rhl_tmp.ctl >${shPath}/ctl/acb_error_rhl.ctl
       if [ $? = 0 ];then
           sqlldr userid=${CONNSTR} control=${loaddata_ctl_dir}/${job_rhl}.ctl data=${vGetServerFiles}/${vfileNameTem} skip=3 log=${logPath}/${job_rhl}_${vfileNameTem}.log bad=${logPath}/${job_rhl}_${vfileNameTem}.bad direct=true rows=10000 bindsize=10000 errors=10000000 >/dev/null
           SQLLDR=$?
           if [ "${SQLLDR}_N" != '_N'  ];then
               if [ $SQLLDR -eq 0 ];then
                       log "[ ${element} ]数据加载成功sqlldr返回值为[ $? ]"
                       loadDataNum=$((${loadDataNum}+1));
                
                   #     ETL_RESULT=`sqlplus -S "${CONNSTR}"<<EOF
                   #                 set heading off;
                   #                 set echo off;
                   #                 set feedback off;
                   #                 set term off;
                   #                 set pagesize 0;
                   #                 set linesize 2048;
                   #                 var ora_return_code VARCHAR2(1000);
                   #                 exec PROC_ACB_ERROR_RHL(${V_DATE_RHL},:ora_return_code);
                   #                 select :ora_return_code from dual;
                   #                 exit;
#EOF`
 	 		             #
                   #     log "PROC_ACB_ERROR_RHL proc is result:[${ETL_RESULT}]";
                   #    log "PROC_ACB_ERROR_RHL处理完毕!!!"
               else
                       log "[ ${element} ]数据加载失败sqlldr返回值为[ $? ]"
               fi
           else
               log "[ ${element} ]数据加载失败sqlldr返回值为[ $? ], 请检查加载文件数据"
           fi
        
       else
          log "sed fail 数据未加载[ ${vGetServerFiles}/${vfileNameTem} ]"
       fi
    fi
done
log "数据全部加载完毕【success】"
log "!!!!!"
