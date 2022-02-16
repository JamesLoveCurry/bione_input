#!/bin/bash
#------------------------------------------------------------------
#
#个人账户数据报送主调入口脚本 sh ytj_run_main_zl.sh
#
#------------------------------------------------------------------
#获取当前程序所在目录
shPath=$(cd "$(dirname "$0")"; pwd)
echo "=====****====="
echo $shPath
echo "=====****====="
DBUSER=`cat $shPath/sys.ini |grep @DBUSER |awk -F '=' '{printf $2}'`
DBPASSWD=`cat $shPath/sys.ini |grep @DBPASSWD |awk -F '=' '{printf $2}'`
ASUSER=`cat $shPath/sys.ini |grep @ASUSER |awk -F '=' '{printf $2}'`
CONNSTR="${DBUSER}/${DBPASSWD}@${ASUSER}"
echo "=====****====="
echo $DBUSER
echo "=====****====="
echo $DBPASSWD
echo "=====****====="
echo $ASUSER
echo "=====****====="
echo $CONNSTR
echo "=====****====="

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
echo ${expDate}
        datadate=`echo ${expDate} |awk -F '-' '{printf $1}'`
        nowTime=`echo ${expDate} |awk -F '-' '{printf $2}'`
echo "datadate[ ${datadate} ]    nowTime[ ${nowTime} ]"
