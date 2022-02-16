//初始化界面,菜单配置路径是【/frs/yufreejs?js=/yujs/crrs/xxx/xxx.js】
function AfterInit(){
	JSPFree.createSplit("d1","左右",235);

	JSPFree.createBillList("d1","/biapp-crrs/freexml/crrs/summary/v_crrs_result_CODE1.xml",null,{list_btns:"[icon-p69]导出/onExport;",autoquery:"N",isSwitchQuery:"N"});
	d1_BillList.pagerType="1";
	JSPFree.billListBindCustQueryEvent(d1_BillList, onSingleSummary);

}

function onSingleSummary(_condition){
	var _sql = null;

	var jso_rt = JSPFree.doClassMethodCall("com.yusys.crrs.monitor.service.CrrsMonitorBSDMO",
		"getSql",{condition:_condition});
	if ("OK" == jso_rt.status) {
		_sql = jso_rt.allsql;
	}

	JSPFree.queryDataBySQL(d1_BillList, _sql);
}

function onExport(){
	var str_sql = d1_BillList.CurrSQL;
	if (str_sql == null) {
		JSPFree.alert("当前无记录！");
		return;
	}
	JSPFree.downloadExcelBySQL("处理进度监控.xls", str_sql, "处理进度监控","机构编号,机构名称,表名,总数,已分发数,分发失败数,退回数,完成数");
}