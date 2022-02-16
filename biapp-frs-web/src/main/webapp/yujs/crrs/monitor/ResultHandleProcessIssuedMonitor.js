//以填报机构维度，进行统计
var org_no = "";
function AfterInit() {
	org_no = CrrsFreeUtil.getOrgNo(str_LoginUserOrgNo);
	
	JSPFree.createSplit("d1","左右", 235);
	JSPFree.createBillList("d1","/biapp-crrs/freexml/crrs/summary/v_crrs_result_issued.xml",null,{list_btns:"[icon-p69]导出/onExport;",autoquery:"N",isSwitchQuery:"N"});

	d1_BillList.pagerType = "2"; //第二种分页类型
    JSPFree.billListBindCustQueryEvent(d1_BillList, onSingleSummary);
}

/**
 * 获取查询sql
 * @returns
 */
function onSingleSummary(_condition) {
	var _sql = "";
	var dataDt = JSPFree.getBillQueryFormValue(d1_BillQuery).data_dt;
	var issuedNo = JSPFree.getBillQueryFormValue(d1_BillQuery).issued_no;
	var checkStatus = JSPFree.getBillQueryFormValue(d1_BillQuery).check_status;
	var reportStatus = JSPFree.getBillQueryFormValue(d1_BillQuery).report_status;
	var defvalue = {"_dataDt": dataDt, "_issuedNo": issuedNo, "_checkStatus": checkStatus, "_reportStatus": reportStatus};
	var jso_sql = JSPFree.doClassMethodCall("com.yusys.crrs.monitor.service.CrrsMonitorBSDMO","getResultSql", defvalue);
	if (jso_sql.result == "OK") {
		_sql = jso_sql.tab_sql;
	}
	
    JSPFree.queryDataBySQL(d1_BillList, _sql);
	FreeUtil.resetToFirstPage(d1_BillList); // 手工跳转到第一页
}

/**
 * 导出操作
 * @returns
 */
function onExport(){
	if (d1_BillList.CurrSQL3 == null || "undefined" == d1_BillList.CurrSQL3) {
		JSPFree.alert("当前无记录！");
		return;
	}
	
	JSPFree.downloadBillListCurrSQL3AsExcel(null, d1_BillList);
}