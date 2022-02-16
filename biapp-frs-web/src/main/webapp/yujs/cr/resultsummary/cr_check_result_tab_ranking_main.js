/**
 * 
 * <pre>
 * Title: 检核结果数据表排名
 * Description:
 * </pre>
 * @author wangxy31 
 * @version 1.00.00
   @date 2020年8月12日
 */

var org_no = "";
var org_class = "";

function AfterInit() {
	// 判断是否是总行，如果是总行则不禁用，如果是其他则禁用
	var jso_data = JSPFree.doClassMethodCall("com.yusys.cr.business.service.CrBusinessBS", "checkUserOrgNo", {str_LoginUserOrgNo: str_LoginUserOrgNo});
	var result = jso_data.result;
	if (result == "OK") {
		org_no = jso_data.orgNo;
		org_class = jso_data.orgClass;
	}

	// 情况一：是报送行
	if (org_no != null && org_no != "") {
		JSPFree.createBillList("d1","/biapp-cr/freexml/resultsummary/cr_result_tab_summary_ranking_v.xml", null, {isSwitchQuery:"N",autoquery:"N",list_ispagebar:"Y"});
		d1_BillList.pagerType="2"; // 第二种分页类型
		FreeUtil.loadBillQueryData(d1_BillList, {org_ot: org_no});
		
		JSPFree.queryDataBySQL(d1_BillList, getTabSummarySql());
		JSPFree.billListBindCustQueryEvent(d1_BillList, onTabErrorSummary);
	}
	// 情况二：非报送行
	else {
		JSPFree.createBillList("d1","/biapp-cr/freexml/resultsummary/cr_result_tab_summary_ranking1_v.xml", null, {isSwitchQuery:"N",autoquery:"N",list_ispagebar:"Y"});
		d1_BillList.pagerType="2"; // 第二种分页类型
		FreeUtil.loadBillQueryData(d1_BillList, {org_ot: org_no});
		
		JSPFree.queryDataBySQL(d1_BillList, getTabSummarySql());
		JSPFree.billListBindCustQueryEvent(d1_BillList, onTabErrorSummary);
	}
}

/**
 * 页面加载结束后
 * @returns
 */
function AfterBodyLoad(){
	// 判断是否是总行，如果是总行则不禁用，如果是其他则禁用
	if (org_class == CrFreeUtil.getCrOrgClass().zh) {
		JSPFree.setBillQueryItemEditable("org_ot", "下拉框", true);
	} else if (org_class == CrFreeUtil.getCrOrgClass().fh) {
		JSPFree.setBillQueryItemEditable("org_ot", "下拉框", false);
	}
}

/**
 * 拼接sql
 * @returns
 */
function getTabSummarySql() {
	// 获取最新一期的日期
	var str_data_dt = "";
	var jso_rt = JSPFree.doClassMethodCall("com.yusys.cr.result.service.CrResultSummaryBS","getSummaryMaxDate",{"from_tab":"v_cr_result_tab_summary", "_loginUserOrgNo" : str_LoginUserOrgNo});
	if (jso_rt.result == "OK") {
		str_data_dt = jso_rt.data_dt;
	}
	
	FreeUtil.loadBillQueryData(d1_BillList, {data_dt : str_data_dt});
	
	var tab_sql = "";
	var defvalue = {"data_dt":str_data_dt, "org_class":org_class, "_loginUserOrgNo" : str_LoginUserOrgNo};
	var jso_sql = JSPFree.doClassMethodCall("com.yusys.cr.result.service.CrResultSummaryBS","getTabSummarySql", defvalue);
	if (jso_sql.result == "OK") {
		tab_sql = jso_sql.tab_sql;
	}
	
	return tab_sql;
}

/**
 * 查询时候，拼接sql
 * @param _condition
 * @returns
 */
function onTabErrorSummary(_condition) {
	var tab_sql = "";
	var defvalue = {"_condition":_condition, "_loginUserOrgNo" : str_LoginUserOrgNo};
	var jso_sql = JSPFree.doClassMethodCall("com.yusys.cr.result.service.CrResultSummaryBS","getTabSummaryQuerySql", defvalue);
	if (jso_sql.result == "OK") {
		tab_sql = jso_sql.tab_sql;
	}
	
	JSPFree.queryDataBySQL(d1_BillList, tab_sql);
	FreeUtil.resetToFirstPage(d1_BillList); // 手工跳转到第一页
}

/**
 * 导出
 * @returns
 */
function exports() {
	if (d1_BillList.CurrSQL3 == null || "undefined" == d1_BillList.CurrSQL3) {
		JSPFree.alert("当前无记录！");
		
		return;
	}
	
	// 获取查询面板的日期
	var _str_data = JSPFree.getBillQueryFormValue(d1_BillQuery).data_dt;
	var data = _str_data.replace(/-/g, '');
	
	JSPFree.downloadExcelBySQL("检核结果数据表排名-" + data + ".xls", d1_BillList.CurrSQL3, "检核结果数据表排名-"+data, "数据日期,表名,总记录数,错误记录数,错误率");
}

/**
 * 历史
 * @returns
 */
function historyFn(_btn) {
	var dataset = _btn.dataset; // 数据都在这个map中
	var index = dataset.rowindex;
	var rows = d1_BillList.datagrid("getRows");
	var row = rows[index]; // index为行号

	var _org_ot = JSPFree.getBillQueryFormValue(d1_BillQuery).org_ot;
	
	var org_no1 = "";
	var org_type = "";
	var org_class1 = "";

	// 判断是否存在报送机构查询框，如果存在，说明是报送机构，如果不存在，说明是非报送机构
	if (_org_ot != null && _org_ot != "") {
		// 报送机构号转换成用户所属机构号
		var userNo = "";
		var jso_userorg = JSPFree.doClassMethodCall("com.yusys.cr.common.service.CrValidateQueryConditionBS", "getUserOrgNoCondition", {"orgNo" : _org_ot});
		if (jso_userorg.msg == "ok") {
			userNo = jso_userorg.data;
		}
		
		var jso_org = JSPFree.doClassMethodCall("com.yusys.cr.business.service.CrBusinessBS", "checkUserOrgNo", {"str_LoginUserOrgNo" : userNo});
		org_class1 = jso_org.orgClass;
		org_no1 = _org_ot;
		org_type = "Y";
	} else {
		org_no1 = org_no;
		org_type = "N";
	}
	
	var jso_OpenPars = {};
	jso_OpenPars.tab_name = row.tab_name;
	jso_OpenPars.data_dt = row.data_dt;
	jso_OpenPars.org_no = org_no1;
	jso_OpenPars.org_type = org_type;
	jso_OpenPars.org_class = org_class1;
	
	JSPFree.openDialog(row.tab_name +"(数据表排名)","/yujs/cr/resultsummary/cr_check_result_tab_ranking_history.js", 700, 450, jso_OpenPars, function(_rtdata){});
}

/**
 * 机构
 * @returns
 */
function orgFn(_btn) {
	var dataset = _btn.dataset; // 数据都在这个map中
	var index = dataset.rowindex;
	var rows = d1_BillList.datagrid("getRows");
	var row = rows[index]; // index为行号

	var _org_ot = JSPFree.getBillQueryFormValue(d1_BillQuery).org_ot;
	
	var org_no1 = "";
	var org_type = "";
	var org_class1 = "";
	// 判断是否存在报送机构查询框，如果存在，说明是报送机构，如果不存在，说明是非报送机构
	if (_org_ot != null && _org_ot != "") {
		// 报送机构号转换成用户所属机构号
		var userNo = "";
		var jso_userorg = JSPFree.doClassMethodCall("com.yusys.cr.common.service.CrValidateQueryConditionBS", "getUserOrgNoCondition", {"orgNo" : _org_ot});
		if (jso_userorg.msg == "ok") {
			userNo = jso_userorg.data;
		}
		
		var jso_org = JSPFree.doClassMethodCall("com.yusys.cr.business.service.CrBusinessBS", "checkUserOrgNo", {"str_LoginUserOrgNo" : userNo});
		org_class1 = jso_org.orgClass;
		org_no1 = _org_ot;
		org_type = "Y";
	} else {
		org_no1 = org_no;
		org_type = "N";
	}
	
	var jso_OpenPars = {};
	jso_OpenPars.tab_name = row.tab_name;
	jso_OpenPars.data_dt = row.data_dt;
	jso_OpenPars.org_no = org_no1;
	jso_OpenPars.org_type = org_type;
	jso_OpenPars.org_class = org_class1;
	
	JSPFree.openDialog(row.tab_name +"(机构排名)", "/yujs/cr/resultsummary/cr_check_result_tab_ranking_org.js", 900, 450, jso_OpenPars, function(_rtdata){});
}