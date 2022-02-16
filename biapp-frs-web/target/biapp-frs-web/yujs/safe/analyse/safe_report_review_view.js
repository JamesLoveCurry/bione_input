/**
 * 
 * <pre>
 * Title: 【统计与监控】-【审核情况统计】-【查看详细】
 * Description: 报表处理统计：查看辖内的具体填报明细数据
 * </pre>
 * @author baifk 
   @date 2021年03月01日
 */

var tabName = "";
var tabNameEn = "";
var taskId = "";
var report_type = "";
var str_className = null;
var dataStatus = "";
var str_ds = "";
var org_no = "";
function AfterInit() {
	tabName = jso_OpenPars.str_tab_name;
	tabNameEn = jso_OpenPars.tab_name_en;
	dataDt = jso_OpenPars.data_dt;
	report_type = jso_OpenPars.report_type;
	dataStatus = jso_OpenPars.data_status;
	str_ds = jso_OpenPars.ds;
	org_no = jso_OpenPars.org_no;
	str_className = "Class:com.yusys.safe.business.template.BusinessBuilderTemplate.getTemplet('" + tabName + "','" + tabNameEn + "','" + report_type + "','" + str_LoginUserOrgNo + "','2')";
	JSPFree.createBillList("d1", str_className, null, {list_btns:"[icon-p41]查看/query;",isSwitchQuery:"N",autoquery:"N"});
	var whereSql = "";
	if (dataStatus) {
		whereSql = " org_no ='"+org_no+"' and data_dt = '" + dataDt +"' and approval_status = '" + dataStatus + "' and " + getOrgCondition();
	} else {
		whereSql = " org_no ='"+org_no+"' and data_dt = '" + dataDt + "' and " + getOrgCondition();
	}
	JSPFree.queryDataByConditon2(d1_BillList, whereSql);
	JSPFree.setBillListForceSQLWhere(d1_BillList, whereSql);
	
}
// 查看
function query(){
	var json_rowdata = d1_BillList.datagrid('getSelected'); // 先得到数据

	if (json_rowdata == null || json_rowdata == undefined) {
		$.messager.alert('提示', '至少选择一条记录进行操作', 'warning');
		return;
	}

	var defaultVal = {type:"View",tabname:tabName,tabnameen:tabNameEn,str_ds:str_ds,report_type:report_type};
	defaultVal["_BillCardData"] = json_rowdata; // 列表数据,这样设置就是从前端取数

	JSPFree.openDialog3("查看","/yujs/safe/busidata/safe_check_data_edit.js",null,null,defaultVal,function(_rtdata) {
		
	},true);
}
function getOrgCondition(){
	var jso_org = JSPFree.doClassMethodCall("com.yusys.safe.base.common.service.SafeValidateQueryConditionBS","getQueryCondition",{_loginUserOrgNo:str_LoginUserOrgNo,report_type:report_type});
	var orgCondition = " 1=2 ";
	if(jso_org.msg=="ok"){
		orgCondition = jso_org.condition;
	}
	return orgCondition;
}