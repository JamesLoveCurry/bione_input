var tabName = "";
var ruleId = "";
var tabNameEn = "";
var dataDt = "";
var orgNo = "";
var orgType = "";
var org_level = '';
function AfterInit(){
	tabName = jso_OpenPars.tabName;
	ruleId = jso_OpenPars.ruleId;
	tabNameEn = jso_OpenPars.tabNameEn;
	dataDt = jso_OpenPars.dataDt;
	orgNo = jso_OpenPars.orgNo;
	orgType = jso_OpenPars.org_type;
	org_level = jso_OpenPars.org_level;
	var queryType = '1';
	if(org_level>2){
		queryType = '3';
	}
	var str_className = "Class:com.yusys.east.checkresult.summary.service.FailDetailTempletBuilder.getTemplet('"+tabName+"','"+tabNameEn+"','"+dataDt+"','"+orgNo +"','"+orgType +"','"+queryType+"')";
	JSPFree.createBillList("d1", str_className);
	/*d1_BillList.pagerType="1"; //第一种分页类型
	JSPFree.queryDataBySQL(d1_BillList, getTabTemplateSql());*/
}

/**
 * 【查看错误明细数据】，拼接sql
 * @param str_data_dt
 * @returns
 */
function getTabTemplateSql(str_data_dt) {
	var _sql = "";
	var defvalue = {"tabName":tabName, "tabNameEn":tabNameEn, "dataDt" : dataDt, "orgNo" : orgNo, "orgType" : orgType};
	var jso_sql = JSPFree.doClassMethodCall("com.yusys.east.checkresult.summary.service.FailDetailTempletBuilder","getTabTemplateSql", defvalue);
	if (jso_sql.result == "OK") {
		_sql = jso_sql._sql;
	}

	return _sql;
}