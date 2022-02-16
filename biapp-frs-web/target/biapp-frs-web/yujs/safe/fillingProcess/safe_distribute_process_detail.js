/**
 * 
 * <pre>
 * Title: 【报表处理】-【报表填报】
 * Description: 报表填报：处理日志页面
 * </pre>
 * @author wangxy31 
 * @version 1.00.00
   @date 2020年6月22日
 */

var child_task_id = "";
var tab_name = "";
var tab_name_en = "";
var report_type = "";

function AfterInit(){
	child_task_id = jso_OpenPars.child_task_id;
	report_type = jso_OpenPars.report_type;
	
	// 获取【填报流程任务表原因记录】常量类
	tab_name = SafeFreeUtil.getTableNames().SAFE_FILLING_REASON;
	// 获取英文表名
	var jso_data = JSPFree.doClassMethodCall(
			"com.yusys.safe.base.common.service.SafeCommonBS",
			"getTabNameByEn", {tab_name:tab_name, report_type:report_type});
	tab_name_en = jso_data.tab_name_en;
	
	var str_className = "Class:com.yusys.safe.base.common.template.CommonBuilderTemplate.getTemplet('" + tab_name + "','" + tab_name_en + "','" + report_type + "')";
	JSPFree.createBillList("d1",str_className,null,{isSwitchQuery:"N",autoquery:"N",ishavebillquery:"N"});
	
	JSPFree.queryDataByConditon2(d1_BillList, "child_task_id = '"+child_task_id+"'");
}