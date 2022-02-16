/**
 *
 * <pre>
 * Title:报表校验
 * Description:
 * </pre>
 * @author miaokx
 * @version 1.00.00
 * @date 2020/6/9 14:52
 */

var str_data_dt = "";
var tab_name = "";
var tab_name_en = "";
var str_subfix = "";
var str_className = "";
function AfterInit(){
    // 获取路径参数
    if (jso_OpenPars != '') {
        if(jso_OpenPars.type != null) {
            str_subfix = jso_OpenPars.type;
        }
    }
    // 获取【错误补录表】常量类
    tab_name = SafeFreeUtil.getTableNames().SAFE_RESULT_DATA;
    // 获取英文表名
    var jso_data = JSPFree.doClassMethodCall(
        "com.yusys.safe.base.common.service.SafeCommonBS",
        "getTabNameByEn", {tab_name:tab_name});
    tab_name_en = jso_data.tab_name_en;
    str_className = "Class:com.yusys.safe.base.common.template.CommonBuilderTemplate.getTemplet('" + tab_name + "','" + tab_name_en + "','" + str_subfix + "','" + str_LoginUserOrgNo + "')";
    JSPFree.createBillList("d1", str_className, null, {list_btns:"[icon-search]校验/onValidate;[icon-p68]导出结果/onExportBySelect;[icon-p48]查看日志/onEngineLog;[icon-p09]查看规则/viewRule", isSwitchQuery:"N", autoquery:"N", orderbys:"reportdate,tablename_en"});
    // 获取最新一期的日期
    var jso_data = JSPFree.getHashVOs("SELECT max(reportdate) data_dt FROM safe_result_data  where report_type like '%"+str_subfix+"%'");
    if(jso_data != null && jso_data.length > 0){
        str_data_dt = jso_data[0].data_dt;
    }
    FreeUtil.loadBillQueryData(d1_BillList, {reportdate:str_data_dt});
    JSPFree.queryDataByConditon2(d1_BillList,"reportdate ='"+str_data_dt+"'");
}

/**
 * 导出excel文件
 */
function onExportBySelect() {
    var jsy_datas = JSPFree.getBillListSelectDatas(d1_BillList);
    var d1_sql = "select reportdate,rulerid, tablename,colname,colvalue,problemmsg,org_name,result_status from safe_result_data " +
        " where  ";
    if (jsy_datas==null || jsy_datas.length<=0) {
        // 全量下载
        var jsy_datas = JSPFree.getBillListAllDatas(d1_BillList);
        if(jsy_datas==null || jsy_datas.length<=0){
            $.messager.alert('提示', '当前无数据不可导出', 'warning');
            return;
        }
        var d1_sql_where = d1_BillList.CurrSQL.split("where")[1];
        d1_sql += d1_sql_where;
    } else{
        // 下载选中
        d1_sql += " 1=1 and rid in (";
        for(var i=0;i<jsy_datas.length;i++){
            if(i==jsy_datas.length-1){
                d1_sql += "'"+jsy_datas[i]["rid"]+"')";
            } else{
                d1_sql += "'"+jsy_datas[i]["rid"]+"',";
            }
        }
    }

    var report_date = jsy_datas[0]["reportdate"];
    JSPFree.downloadExcelBySQL(str_subfix+"-校验结果-"+report_date+".xls", d1_sql, "规范性校验","报告日期,校验id,表名,字段名,字段值,问题提示,经办机构,处理状态");
}

/**
 * 查看日志
 */
function onEngineLog() {
    JSPFree.openDialog("日志", "/yujs/safe/reportCheck/engine_log_view.js", 900, 600,
        {reportType:str_subfix}, function(_rtdata) {

        });
}

/**
 * 校验
 */
var onValidate = function () {
    var pars = { reportType:str_subfix };
    JSPFree.openDialog("选择日期","/yujs/safe/reportCheck/report_check_date.js",350,350,pars,function(_rtdata){
        if (_rtdata != null && _rtdata.code == "success") {
            $.messager.show({title:'消息提示',msg: '后台校验中，请稍后...',showType:'show'});
            JSPFree.queryDataByConditon(d1_BillList,null);  // 立即查询刷新数据
        }
    });
}

/**
 * 查看规则
 */
function viewRule() {
    var jso_par ={type: str_subfix};
    JSPFree.openDialog("校验规则","/yujs/safe/reportCheck/view_rule.js",960,600,jso_par);
}