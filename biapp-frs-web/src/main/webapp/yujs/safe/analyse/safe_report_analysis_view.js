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
var dataStatus = "";
function AfterInit() {
	tabName = jso_OpenPars.str_tab_name;
	tabNameEn = jso_OpenPars.tab_name_en;
	dataDt = jso_OpenPars.data_dt;
	report_type = jso_OpenPars.report_type;
	dataStatus = jso_OpenPars.data_status;
	str_className = "Class:com.yusys.safe.business.template.BusinessBuilderTemplate.getTemplet('" + tabName + "','" + tabNameEn + "','" + report_type + "','" + str_LoginUserOrgNo + "','2')";
	JSPFree.createBillList("d1", str_className, null, {list_btns:"$VIEW;",isSwitchQuery:"N",autoquery:"N"});
	var whereSql = "";
	if (dataStatus) {
		whereSql = "data_dt = '" + dataDt +"' and data_status = '" + dataStatus + "' and " + getOrgCondition();
	} else {
		whereSql = "data_dt = '" + dataDt + "' and " + getOrgCondition();
	}

	JSPFree.queryDataByConditon2(d1_BillList, whereSql);
	JSPFree.setBillListForceSQLWhere(d1_BillList, whereSql);
	
}

function getOrgCondition(){
	var jso_org = JSPFree.doClassMethodCall("com.yusys.safe.base.common.service.SafeValidateQueryConditionBS","getQueryCondition",{_loginUserOrgNo:str_LoginUserOrgNo,report_type:report_type});
	var orgCondition = " 1=2 ";
	if(jso_org.msg=="ok"){
		orgCondition = jso_org.condition;
	}
	return orgCondition;
}