/**
 * 查看日志
 * @type {string}
 */
var tab_name = "";
var tab_name_en = "";
var reportType = "";
var str_className = "";
var str_sqlWhere = "";

function AfterInit() {
	reportType = jso_OpenPars.reportType;
	var taskId = jso_OpenPars.taskId; // 获取任务id
	 
    // 获取【检核日志表】常量类
    tab_name = SafeFreeUtil.getTableNames().SAFE_ENGINE_LOG;
    // 获取英文表名
    var jso_data = JSPFree.doClassMethodCall(
        "com.yusys.safe.base.common.service.SafeCommonBS",
        "getTabNameByEn", {tab_name:tab_name});
    tab_name_en = jso_data.tab_name_en;
    var str_className = "Class:com.yusys.safe.reportCheck.template.EngineLogTemplate.getTemplet('" + tab_name + "','" + tab_name_en + "')";
    JSPFree.createBillList("d1", str_className, null, {list_btns:"[icon-p02]刷新/onRefresh", isSwitchQuery:"N",list_ispagebar: "Y",ishavebillquery:"N", autoquery:"N", orderbys:"log_no"});
    
    str_sqlWhere = "task_id='"  + taskId + "'";  // 拼SQL条件
    JSPFree.queryDataByConditon2(d1_BillList, str_sqlWhere);
	JSPFree.setBillListForceSQLWhere(d1_BillList, str_sqlWhere);
}

/**
 * 刷新
 * @returns
 */
function onRefresh() {
    JSPFree.queryDataByConditon(d1_BillList, "");
}