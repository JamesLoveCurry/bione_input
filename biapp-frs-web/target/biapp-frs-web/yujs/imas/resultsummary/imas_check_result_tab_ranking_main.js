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

function AfterInit() {
	JSPFree.createBillList("d1","/biapp-imas/freexml/resultsummary/imas_result_tab_summary_ranking_v.xml", null, {isSwitchQuery:"N",autoquery:"N",list_ispagebar:"Y"});
	d1_BillList.pagerType="2"; // 第二种分页类型
	JSPFree.billListBindCustQueryEvent(d1_BillList, onTabErrorSummary);
}

/**
 * 查询时候，拼接sql
 * @param _condition
 * @returns
 */
function onTabErrorSummary(_condition) {
	var tab_sql = "";
	var defvalue = {"_condition":_condition, "_loginUserOrgNo" : str_LoginUserOrgNo};
	var jso_sql = JSPFree.doClassMethodCall("com.yusys.imas.result.service.ImasResultSummaryBS","getTabSummaryQuerySql", defvalue);
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
		var jso_userorg = JSPFree.doClassMethodCall("com.yusys.imas.common.service.ImasValidateQueryConditionBS", "getUserOrgNoCondition", {"orgNo" : _org_ot});
		if (jso_userorg.msg == "ok") {
			userNo = jso_userorg.data;
		}
		
		var jso_org = JSPFree.doClassMethodCall("com.yusys.imas.business.service.ImasBusinessBS", "checkUserOrgNo", {"str_LoginUserOrgNo" : userNo});
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
	
	JSPFree.openDialog(row.tab_name +"(数据表排名)","/yujs/imas/resultsummary/imas_check_result_tab_ranking_history.js", 700, 450, jso_OpenPars, function(_rtdata){});
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
		var jso_userorg = JSPFree.doClassMethodCall("com.yusys.imas.common.service.ImasValidateQueryConditionBS", "getUserOrgNoCondition", {"orgNo" : _org_ot});
		if (jso_userorg.msg == "ok") {
			userNo = jso_userorg.data;
		}
		
		var jso_org = JSPFree.doClassMethodCall("com.yusys.imas.business.service.ImasBusinessBS", "checkUserOrgNo", {"str_LoginUserOrgNo" : userNo});
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
	
	JSPFree.openDialog(row.tab_name +"(机构排名)", "/yujs/imas/resultsummary/imas_check_result_tab_ranking_org.js", 900, 450, jso_OpenPars, function(_rtdata){});
}