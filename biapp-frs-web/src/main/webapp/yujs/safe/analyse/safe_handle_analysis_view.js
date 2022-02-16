/**
 * 
 * <pre>
 * Title: 【统计与监控】-【报表处理统计】-【查看详细】
 * Description: 报表处理统计：查看辖内的具体填报明细数据
 * </pre>
 * @author liangzy5 
 * @version 1.00.00
   @date 2020年7月12日
 */

var tabName = "";
var taskId = "";
var report_type = "";
var str_className = null;

function AfterInit() {
	taskId = jso_OpenPars.taskId;
	tabName = jso_OpenPars.tabName;
	tabNameEn = jso_OpenPars.tabNameEn;
	dataDt = jso_OpenPars.dataDt;
	report_type = jso_OpenPars.report_type;
	
	
	str_className = "Class:com.yusys.safe.business.template.BusinessBuilderTemplate.getTemplet('" + tabName + "','" + tabNameEn + "','" + report_type + "','" + str_LoginUserOrgNo + "')";
	JSPFree.createBillList("d1", str_className, null, {list_btns:"$VIEW;",isSwitchQuery:"N",autoquery:"N",ishavebillquery:"N", ishavebillquery:"N"});
	
	FreeUtil.loadBillQueryData(d1_BillList, {data_dt:dataDt})
	JSPFree.queryDataByConditon2(d1_BillList, "task_id like '"+taskId+"%' and "+getOrgCondition());
	JSPFree.setBillListForceSQLWhere(d1_BillList,  "task_id like '"+taskId+"%' and "+getOrgCondition());
	
}

function getOrgCondition(){
	var jso_org = JSPFree.doClassMethodCall("com.yusys.safe.base.common.service.SafeValidateQueryConditionBS","getQueryCondition",{_loginUserOrgNo:str_LoginUserOrgNo,report_type:report_type});
	var orgCondition = " 1=2 ";
	if(jso_org.msg=="ok"){
		orgCondition = jso_org.condition;
	}
	return orgCondition;
}