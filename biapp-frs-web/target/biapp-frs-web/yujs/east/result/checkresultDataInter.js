//初始化界面,菜单配置路径是【/frs/yufreejs?js=/yujs/east/checkresultDataInter.js】 检核结果表名排名
var org_no = "";
var org_class = "";
var whereSql = '';

function AfterInit(){
	// 判断是否是总行，如果是总行则不禁用，如果是其他则禁用
	var jso_org = JSPFree.getHashVOs("SELECT org_no,org_class FROM rpt_org_info where org_type='04' " +
			"and is_org_report='Y' and mgr_org_no = '"+str_LoginUserOrgNo+"'");
	if(jso_org != null && jso_org.length > 0){
		org_no = jso_org[0].org_no;
		org_class = jso_org[0].org_class;
	}
	var jsn_result = JSPFree.doClassMethodCall("com.yusys.east.common.service.EastGetReportBS", "getReportCNList", null);
	if (jsn_result.code == 'success') {
		whereSql = jsn_result.data;
	}
	if (org_no!=null && org_no!="") {
		JSPFree.createBillList("d1","/biapp-east/freexml/east/result/v_east_cr_summary1.xml",null,{isSwitchQuery:"N",autoquery:"N",list_ispagebar:"Y"});
		d1_BillList.pagerType="2"; //第二种分页类型
		FreeUtil.loadBillQueryData(d1_BillList, {org_ot:org_no});
		var _sql = getSql();
		JSPFree.queryDataBySQL(d1_BillList, _sql);
		JSPFree.billListBindCustQueryEvent(d1_BillList, onErrorSummary);
	} else {
		JSPFree.createBillList("d1","/biapp-east/freexml/east/result/v_east_cr_summary1_1.xml",null,{isSwitchQuery:"N",autoquery:"N",list_ispagebar:"Y"});
		d1_BillList.pagerType="2"; //第二种分页类型
		FreeUtil.loadBillQueryData(d1_BillList, {org_ot:org_no});
		var _sql = getSql();
		JSPFree.queryDataBySQL(d1_BillList, _sql);
		JSPFree.billListBindCustQueryEvent(d1_BillList, onErrorSummary);
	}
}

//页面加载结束后
function AfterBodyLoad(){
	// 判断是否是总行，如果是总行则不禁用，如果是其他则禁用
	if (org_class == '总行') {
		JSPFree.setBillQueryItemEditable("org_ot","下拉框",true);
	} else if (org_class == '分行') {
		JSPFree.setBillQueryItemEditable("org_ot","下拉框",false);
	} else {
		
	}
}

function getSql() {
	var condition = "";
	var jso_rt = JSPFree.doClassMethodCall("com.yusys.east.checkrule.rulesummary.service.ValidateQueryCondition",
			"getQueryCondition",{"_loginUserOrgNo" : str_LoginUserOrgNo});
	if(jso_rt.msg == "ok"){
		condition = jso_rt.condition;
	}
	
	// 获取最新一期的日期
	var str_data_dt = "";
	var jso_data = JSPFree.getHashVOs("SELECT max(data_dt) data_dt FROM v_east_cr_summary1");
	if(jso_data != null && jso_data.length > 0){
		str_data_dt = jso_data[0].data_dt;
	}
	
	FreeUtil.loadBillQueryData(d1_BillList, {data_dt:str_data_dt});
	
	var _sql ="SELECT data_dt, tab_name, SUM (sum_count) sum_count, SUM (fail_count) fail_count, " +
			"CASE WHEN SUM (sum_count) = 0 THEN '' " +
			"ELSE TO_CHAR (ROUND (SUM (fail_count) / SUM (sum_count), 4) * 100, 'FM999990.00') || '%' " +
			"END AS fail_rate " +
			"FROM V_EAST_CR_SUMMARY1 WHERE 1 = 1 ";
	_sql += " and " + whereSql;
	if (org_class == '总行') {
		_sql = _sql + " and data_dt='" + str_data_dt +"' and org_no = '"+org_no+"'";
	} else if (org_class == '分行') {
		_sql = _sql + " and data_dt='" + str_data_dt +"' and org_no = '"+org_no+"'" + " and " + condition;
	} else {
		_sql = _sql + " and data_dt='" + str_data_dt +"' and org_no = '"+org_no+"'" + " and " + condition;
	}
	
	_sql += " group by tab_name, data_dt order by fail_count desc ";

	return _sql;
}

function onErrorSummary(_condition){
	var condition = "";

	var jso_rt = JSPFree.doClassMethodCall("com.yusys.east.checkrule.rulesummary.service.ValidateQueryCondition",
			"getQueryCondition",{"_loginUserOrgNo" : str_LoginUserOrgNo});
	if(jso_rt.msg == "ok"){
		condition = jso_rt.condition;
	}

	var _sql ="SELECT data_dt, tab_name, SUM (sum_count) sum_count, SUM (fail_count) fail_count, " +
		"CASE WHEN SUM (sum_count) = 0 THEN '' ELSE TO_CHAR (ROUND (SUM (fail_count) / SUM (sum_count), 4) * 100, " +
		"'FM999990.00') || '%' END AS fail_rate " +
		"FROM V_EAST_CR_SUMMARY1 WHERE 1 = 1 ";

	_sql += " and " + whereSql;
	if(_condition!="") {
		if (_condition.indexOf('org_ot') != -1) {
			// 如果存在org_no，要获取org_no
			var org_no1 = _condition.substring(_condition.indexOf('org_ot')+10, _condition.length-2);
			var jso_org = JSPFree.doClassMethodCall("com.yusys.east.checkrule.rulesummary.service.ValidateQueryCondition","getQueryCondition",{"_loginUserOrgNo" : org_no1});
			
			var c = "";
			if(jso_org.msg == "ok"){
				c = jso_org.condition;
			}
		
			_condition = _condition.substring(0, _condition.indexOf('org_ot'));
			_condition = _condition + c;
			
			var cla = "";
			var jso_class = JSPFree.getHashVOs("SELECT org_class FROM rpt_org_info where org_type='04' and is_org_report='Y' and org_no = '"+org_no1+"'");
			if(jso_class != null && jso_class.length > 0){
				cla = jso_class[0].org_class;
			}
			
			if (cla == '总行') {
				_sql = _sql + " and org_no = '"+org_no1+"'"+ " and " + _condition;
			} else if (cla == '分行') {
				_sql = _sql + " and org_no =  '"+org_no1+"'"+ " and " + _condition;
			} else {
				_sql = _sql + " and org_no =  '"+org_no1+"'"+ " and " + _condition;
			}
		} else {
			var jso_org = JSPFree.doClassMethodCall("com.yusys.east.checkrule.rulesummary.service.ValidateQueryCondition","getQueryCondition",{"_loginUserOrgNo" : str_LoginUserOrgNo});
			var c = "";
			if(jso_org.msg == "ok"){
				c = jso_org.condition;
			}
			_sql = _sql + " and org_no = '"+org_no+"'"+" and " + _condition + " and " + c;
		}
	}

	_sql += " group by tab_name, data_dt order by fail_count desc ";
	
	JSPFree.queryDataBySQL(d1_BillList, _sql);
	FreeUtil.resetToFirstPage(d1_BillList); //手工跳转到第一页
}

// 导出
function exports(){
	if (d1_BillList.CurrSQL3 == null || "undefined" == d1_BillList.CurrSQL3) {
		JSPFree.alert("当前无记录！");
		return;
	}
	// 获取查询面板的日期
	var _str_data = JSPFree.getBillQueryFormValue(d1_BillQuery).data_dt;
	var data = _str_data.replace(/-/g, '');
//	JSPFree.downloadExcelBySQL("检核结果数据表排名-"+data+".xls",d1_BillList.CurrSQL3,"检核结果数据表排名-"+data,"数据日期,表名,总记录数,错误记录数,错误率,环比,同比");
	JSPFree.downloadBillListDataAsExcel1(d1_BillList, "检核结果数据表排名-"+data+".xls", "检核结果数据表排名-"+data+"");
}
/**
 * 历史
 * @returns
 */
function historyFn(_btn){
	var dataset = _btn.dataset;  //数据都在这个map中
	var index = dataset.rowindex;
	var rows = d1_BillList.datagrid("getRows");
	var row = rows[index];//index为行号

	var _org_ot = JSPFree.getBillQueryFormValue(d1_BillQuery).org_ot;
	
	var org_no1 = "";
	var org_type = "";
	var org_class1 = "";
	// 判断是否存在报送机构查询框，如果存在，说明是报送机构，如果不存在，说明是非报送机构
	if (_org_ot != null && _org_ot != "") {
		// 根据中文表名，找对应的英文code
		var jso_org = JSPFree.getHashVOs("SELECT org_no,org_class FROM rpt_org_info where org_type='04' and is_org_report='Y' and org_no = '"+_org_ot+"'");
		if(jso_org != null && jso_org.length > 0){
			org_no1 = jso_org[0].org_no;
			org_class1 = jso_org[0].org_class;
		}
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
	
	JSPFree.openDialog(row.tab_name +"(数据表排名)","/yujs/east/result/checkresultHistory.js", 700, 450, jso_OpenPars,function(_rtdata){});
}
/**
 * 机构
 * @returns
 */
function orgFn(_btn){
	var dataset = _btn.dataset;  //数据都在这个map中
	var index = dataset.rowindex;
	var rows = d1_BillList.datagrid("getRows");
	var row = rows[index];//index为行号

	var _org_ot = JSPFree.getBillQueryFormValue(d1_BillQuery).org_ot;
	
	var org_no1 = "";
	var org_type = "";
	var org_class1 = "";
	// 判断是否存在报送机构查询框，如果存在，说明是报送机构，如果不存在，说明是非报送机构
	if (_org_ot != null && _org_ot != "") {
		// 根据中文表名，找对应的英文code
		var jso_org = JSPFree.getHashVOs("SELECT org_no,org_class FROM rpt_org_info where org_type='04' and is_org_report='Y' and org_no = '"+_org_ot+"'");
		if(jso_org != null && jso_org.length > 0){
			org_no1 = jso_org[0].org_no;
			org_class1 = jso_org[0].org_class;
		}
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
	
	JSPFree.openDialog(row.tab_name +"(机构排名)","/yujs/east/result/checkresultDataorg.js", 900, 450, jso_OpenPars,function(_rtdata){});
}

/*
 * 暂去掉，不用
 * 
*//**
 * 部门
 * @returns
 *//*
function departFn(_btn){
	var dataset = _btn.dataset;  //数据都在这个map中
	var index = dataset.rowindex;
	var rows = d1_BillList.datagrid("getRows");
	var row = rows[index];//index为行号
	
	var jso_OpenPars = {};
	jso_OpenPars.tab_name = row.tab_name;
	jso_OpenPars.data_dt = row.data_dt;
	JSPFree.openDialog(row.tab_name +"(部门排名)","/yujs/east/checkresultDatadept.js", 900, 450, jso_OpenPars,function(_rtdata){});
}
*//**
 * 字段
 * @returns
 *//*
function colNmFn(_btn){
	var dataset = _btn.dataset;  //数据都在这个map中
	var index = dataset.rowindex;
	var rows = d1_BillList.datagrid("getRows");
	var row = rows[index];//index为行号
	
	var jso_OpenPars = {};
	jso_OpenPars.tab_name = row.tab_name;
	jso_OpenPars.data_dt = row.data_dt;
	JSPFree.openDialog(row.tab_name +"(字段排名)","/yujs/east/checkresultDataColumn.js", 900, 450, jso_OpenPars,function(_rtdata){});
}*/