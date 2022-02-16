/**
 * 
 * <pre>
 * Title: 【报表处理】-【报表下发】
 * Description: 报表下发：子任务页面
 * </pre>
 * @author wangxy31 
 * @version 1.00.00
   @date 2020年6月19日
 */

var task_id = "";
var report_type = "";

function AfterInit() {
	task_id = jso_OpenPars2.data.rid;
	report_type = jso_OpenPars2.data.report_type;
	
	// 获取【填报流程任务子表】常量类
	var tab_name = SafeFreeUtil.getTableNames().SAFE_FILLING_PROCESS_CHILD;
	// 获取英文表名
	var jso_data = JSPFree.doClassMethodCall(
			"com.yusys.safe.base.common.service.SafeCommonBS",
			"getTabNameByEn", {tab_name:tab_name, report_type:report_type});
	var tab_name_en = jso_data.tab_name_en;
	
	str_className = "Class:com.yusys.safe.base.common.template.CommonBuilderTemplate.getTemplet('" + tab_name + "','" + tab_name_en + "','" + report_type + "')";
	JSPFree.createBillList("d1", str_className, null, {isSwitchQuery:"N",ishavebillquery:"N",autoquery:"N", onlyItems:"ORG_NAME;DATA_DT;TOTAL_SUM;OK_HANDLE;NO_HANDLE;STATUS;DATA_TIME"});

	JSPFree.queryDataByConditon2(d1_BillList, getQueryCondition());
	JSPFree.setBillListForceSQLWhere(d1_BillList,getQueryCondition());
}

/**
 * 查询列表加载默认查询条件
 * @returns
 */
function getQueryCondition(){
	var condition = "task_id = '"+task_id+"' and report_type = '"+report_type+"'";
	
	return condition;
}