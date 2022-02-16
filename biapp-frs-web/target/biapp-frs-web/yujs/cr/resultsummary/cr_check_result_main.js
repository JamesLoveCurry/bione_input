/**
 * 
 * <pre>
 * Title: 检核结果查询
 * Description:【按数据表查询】【按规则查询】
 * </pre>
 * @author wangxy31 
 * @version 1.00.00
   @date 2020年8月11日
 */

var org_no = "";
var org_class = "";
var data_dt = "";

function AfterInit() {
	JSPFree.createTabb("d1", [ "按数据表查询", "按检核规则查询" ]);
	
	// 判断是否是总行，如果是总行则不禁用，如果是其他则禁用
	var jso_data = JSPFree.doClassMethodCall("com.yusys.cr.business.service.CrBusinessBS","checkUserOrgNo",{str_LoginUserOrgNo: str_LoginUserOrgNo});
	var result = jso_data.result;
	if (result == "OK") {
		org_no = jso_data.orgNo;
		org_class = jso_data.orgClass;
	} 

	// 情况一：是报送行
	if (org_no != null && org_no != "") {
		JSPFree.createBillList("d1_1", "/biapp-cr/freexml/resultsummary/cr_result_tab_summary_v.xml",null,{isSwitchQuery:"N",autoquery:"N",list_ispagebar:"Y"}); // 按表查询
		d1_1_BillList.pagerType="2"; //第二种分页类型
		FreeUtil.loadBillQueryData(d1_1_BillList, {org_ot:org_no});
		JSPFree.queryDataBySQL(d1_1_BillList, getTabSummarySql(data_dt));
		JSPFree.billListBindCustQueryEvent(d1_1_BillList, onTabErrorSummary);
		
		JSPFree.createBillList("d1_2", "/biapp-cr/freexml/resultsummary/cr_result_rule_summary_v.xml",null,{isSwitchQuery:"N",autoquery:"N",list_ispagebar:"Y"}); // 按规则查询
		d1_2_BillList.pagerType="2";  //第二种分页类型
		FreeUtil.loadBillQueryData(d1_2_BillList, {org_ot1:org_no});
		JSPFree.queryDataBySQL(d1_2_BillList, getRuleSummarySql(data_dt));
		JSPFree.billListBindCustQueryEvent(d1_2_BillList, onRuleErrorSummary);
	}
	// 情况二：非报送行
	else {
		JSPFree.createBillList("d1_1", "/biapp-cr/freexml/resultsummary/cr_result_tab_summary1_v.xml",null,{isSwitchQuery:"N",autoquery:"N",list_ispagebar:"Y"}); // 按表查询
		d1_1_BillList.pagerType="2"; //第二种分页类型
		JSPFree.queryDataBySQL(d1_1_BillList, getTabSummarySql(data_dt));
		JSPFree.billListBindCustQueryEvent(d1_1_BillList, onTabErrorSummary);
		
		JSPFree.createBillList("d1_2", "/biapp-cr/freexml/resultsummary/cr_result_rule_summary1_v.xml",null,{isSwitchQuery:"N",autoquery:"N",list_ispagebar:"Y"}); // 按规则查询
		d1_2_BillList.pagerType="2";  //第二种分页类型
		JSPFree.queryDataBySQL(d1_2_BillList, getRuleSummarySql(data_dt));
		JSPFree.billListBindCustQueryEvent(d1_2_BillList, onRuleErrorSummary);
	}
}

/**
 * 页面加载结束后
 * @returns
 */
function AfterBodyLoad() {
	// 判断是否是总行，如果是总行则不禁用，如果是其他则禁用
	if (org_class == CrFreeUtil.getCrOrgClass().zh) {
		JSPFree.setBillQueryItemEditable("org_ot", "下拉框", true);
		JSPFree.setBillQueryItemEditable("org_ot1", "下拉框", true);
	} else if (org_class == CrFreeUtil.getCrOrgClass().fh) {
		JSPFree.setBillQueryItemEditable("org_ot", "下拉框", false);
		JSPFree.setBillQueryItemEditable("org_ot1", "下拉框", false);
	}
}

/**
 * 【按数据表查询】，拼接sql
 * @param str_data_dt
 * @returns
 */
function getTabSummarySql(str_data_dt) {
	if (str_data_dt == "" || str_data_dt == null) {
		// 获取最新一期的日期
		var jso_rt = JSPFree.doClassMethodCall("com.yusys.cr.result.service.CrResultSummaryBS","getSummaryMaxDate",{"from_tab":"v_cr_result_tab_summary", "_loginUserOrgNo" : str_LoginUserOrgNo});
		if (jso_rt.result == "OK") {
			str_data_dt = jso_rt.data_dt;
		}
	}
	
	FreeUtil.loadBillQueryData(d1_1_BillList, {data_dt:str_data_dt});
	
	var tab_sql = "";
	var defvalue = {"data_dt":str_data_dt, "org_class":org_class, "_loginUserOrgNo" : str_LoginUserOrgNo};
	var jso_sql = JSPFree.doClassMethodCall("com.yusys.cr.result.service.CrResultSummaryBS","getTabSummarySql", defvalue);
	if (jso_sql.result == "OK") {
		tab_sql = jso_sql.tab_sql;
	}
	
	return tab_sql;
}

/**
 * 【按规则查询】，拼接sql
 * @param str_data_dt
 * @returns
 */
function getRuleSummarySql(str_data_dt) {
	if (str_data_dt == "" || str_data_dt == null) {
		// 获取最新一期的日期
		var jso_rt = JSPFree.doClassMethodCall("com.yusys.cr.result.service.CrResultSummaryBS","getSummaryMaxDate",{"from_tab":"v_cr_result_rule_summary", "_loginUserOrgNo" : str_LoginUserOrgNo});
		if (jso_rt.result == "OK") {
			str_data_dt = jso_rt.data_dt;
		}
	}
	
	FreeUtil.loadBillQueryData(d1_2_BillList, {data_dt:str_data_dt});
	
	var rule_sql = "";
	var defvalue = {"data_dt":str_data_dt, "org_class":org_class, "_loginUserOrgNo" : str_LoginUserOrgNo};
	var jso_sql = JSPFree.doClassMethodCall("com.yusys.cr.result.service.CrResultSummaryBS","getRuleSummarySql", defvalue);
	if (jso_sql.result == "OK") {
		rule_sql = jso_sql.rule_sql;
	}
	
	return rule_sql;
}

/**
 * 【按数据表查询】，查询时候，拼接sql
 * @param str_data_dt
 * @returns
 */
function onTabErrorSummary(_condition) {
	var tab_sql = "";
	var defvalue = {"_condition":_condition, "_loginUserOrgNo" : str_LoginUserOrgNo};
	var jso_sql = JSPFree.doClassMethodCall("com.yusys.cr.result.service.CrResultSummaryBS","getTabSummaryQuerySql", defvalue);
	if (jso_sql.result == "OK") {
		tab_sql = jso_sql.tab_sql;
	}
	
	JSPFree.queryDataBySQL(d1_1_BillList, tab_sql);
	FreeUtil.resetToFirstPage(d1_1_BillList); // 手工跳转到第一页
}

/**
 * 【按规则查询】，查询时候，拼接sql
 * @param str_data_dt
 * @returns
 */
function onRuleErrorSummary(_condition) {
	var rule_sql = "";
	var defvalue = {"_condition":_condition, "_loginUserOrgNo" : str_LoginUserOrgNo};
	var jso_sql = JSPFree.doClassMethodCall("com.yusys.cr.result.service.CrResultSummaryBS","getRuleSummaryQuerySql", defvalue);
	if (jso_sql.result == "OK") {
		rule_sql = jso_sql.rule_sql;
	}
	
	JSPFree.queryDataBySQL(d1_2_BillList, rule_sql);
	FreeUtil.resetToFirstPage(d1_2_BillList); // 手工跳转到第一页
}

/**
 * 按数据表查询：导出
 * @returns
 */
function tabDownload() {
	if (d1_1_BillList.CurrSQL3 == null || "undefined" == d1_1_BillList.CurrSQL3) {
		JSPFree.alert("当前无记录！");
		return;
	}
	
	JSPFree.downloadBillListCurrSQL3AsExcel(null, d1_1_BillList);
}

/**
 * 按检核规则查询：导出
 * @returns
 */
function ruleDownload() {
	if (d1_2_BillList.CurrSQL3 == null || "undefined" == d1_2_BillList.CurrSQL3) {
		JSPFree.alert("当前无记录！");
		return;
	}
	
	JSPFree.downloadBillListCurrSQL3AsExcel(null, d1_2_BillList);
}

/**
 * 按数据表查询：查看明细
 * @param _btn
 * @returns
 */
function viewFailDetailByTab(_btn) {
	var _org_ot = JSPFree.getBillQueryFormValue(d1_1_BillQuery).org_ot;

	var org_no1 = "";
	var org_type = "";
	// 判断是否存在报送机构查询框，如果存在，说明是报送机构，如果不存在，说明是非报送机构
	if (_org_ot != null && _org_ot != "") {
		org_no1 = _org_ot;
		org_type = "Y";
	} else {
		org_no1 = org_no;
		org_type = "N";
	}
	
	var dataset = _btn.dataset;
	var index = dataset.rowindex;
	var rows = d1_1_BillList.datagrid("getRows");
	var row = rows[index]; // index为行号

	// 判断，如果fail_count=0, 则不能查看明细
	if ("0" == row.fail_count) {
		JSPFree.alert("当前错误记录数为0，无法查看错误明细数据！");
		return;
	}
	
	var jso_OpenPars = {tabName: row.tab_name,tabNameEn:row.tab_name_en,dataDt:row.data_dt,orgNo:org_no1,orgType:org_type};
	// 查看详情前，先确认表是否存在
	var jso_rt = JSPFree.doClassMethodCall("com.yusys.cr.result.service.CrResultSummaryBS", "checkFailTab", jso_OpenPars);
	
	if (jso_rt.msg == "OK") {
		JSPFree.openDialog("错误明细","/yujs/cr/resultsummary/cr_check_result_tab_fail.js", 950, 500, jso_OpenPars, function(_rtdata){});
	} else {
		JSPFree.alert("当前明细表不存在，无法查看明细数据!");
	}
}

/**
 * 按检核规则查询：查看明细
 * @param _btn
 * @returns
 */
function viewFailDetailByRule(_btn) {
	var _org_ot = JSPFree.getBillQueryFormValue(d1_2_BillQuery).org_ot1;
	
	var org_no1 = "";
	var org_type = "";
	// 判断是否存在报送机构查询框，如果存在，说明是报送机构，如果不存在，说明是非报送机构
	if (_org_ot != null && _org_ot != "") {
		org_no1 = _org_ot;
		org_type = "Y";
	} else {
		org_no1 = org_no;
		org_type = "N";
	}
	
	var dataset = _btn.dataset;
	var index = dataset.rowindex;
	var rows = d1_2_BillList.datagrid("getRows");
	var row = rows[index]; // index为行号

	// 判断，如果fail_count=0,则不能查看明细
	if ("0" == row.fail_count) {
		JSPFree.alert("当前错误记录数为0，无法查看错误明细数据！");
		return;
	}

	// 判断，如果当前字段名称为null，则弹出对话框提示
	if (row.col_name == null || row.col_name == "") {
		JSPFree.alert("当前规则校验：整表校验，无明细！");
		return;
	}
	
	var jso_OpenPars = {type:"1",tabName: row.tab_name,tabNameEn:row.tab_name_en,dataDt:row.data_dt,ruleId:row.rule_id,orgNo:org_no1,orgType:org_type};
	// 查看详情前，先确认表是否存在
	var jso_rt = JSPFree.doClassMethodCall("com.yusys.cr.result.service.CrResultSummaryBS", "checkFailTab", jso_OpenPars);
	
	if (jso_rt.msg == "OK") {
		JSPFree.openDialog("错误明细","/yujs/cr/resultsummary/cr_check_result_rule_fail.js", 950, 600, jso_OpenPars, function(_rtdata){});
	} else {
		JSPFree.alert("当前明细表不存在，无法查看明细数据!");
	}
}

/**
 * 按检核规则查询：查看历史
 * @param _btn
 * @returns
 */
function historyByRule(_btn) {
	var _org_ot = JSPFree.getBillQueryFormValue(d1_2_BillQuery).org_ot1;

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
	
	var dataset = _btn.dataset;
	var index = dataset.rowindex;
	var rows = d1_2_BillList.datagrid("getRows");
	var row = rows[index]; // index为行号

	var jso_OpenPars = {};
	jso_OpenPars.rule_name = row.rule_name;
	jso_OpenPars.data_dt = row.data_dt;
	jso_OpenPars.rule_id = row.rule_id;
	jso_OpenPars.org_no = org_no1;
	jso_OpenPars.org_type = org_type;
	jso_OpenPars.org_class = org_class1;
	
	JSPFree.openDialog("历史数据","/yujs/cr/resultsummary/cr_check_result_rule_history.js", 700, 450, jso_OpenPars,function(_rtdata){});
}

//按数据表查询：导出明细
function exportFialDetailByTab(_btn){
	var _org_ot = JSPFree.getBillQueryFormValue(d1_1_BillQuery).org_ot;

	var org_no1 = "";
	var org_type = "";
	// 判断是否存在报送机构查询框，如果存在，说明是报送机构，如果不存在，说明是非报送机构
	if (_org_ot != null && _org_ot != "") {
		// 根据中文表名，找对应的英文code
		var jso_org = JSPFree.getHashVOs("SELECT org_no FROM rpt_org_info where org_type='04' and is_org_report='Y' and org_no = '"+_org_ot+"'");
		if(jso_org != null && jso_org.length > 0){
			org_no1 = jso_org[0].org_no;
		}
		org_type = "Y";
	} else {
		org_no1 = org_no;
		org_type = "N";
	}

	var dataset = _btn.dataset;
	var index = dataset.rowindex;
	var rows = d1_1_BillList.datagrid("getRows");
	var row = rows[index];//index为行号

	// 判断，如果fail_count=0,则不能导出明细
	if ("0" == row.fail_count) {
		JSPFree.alert("当前错误记录数为0，无法导出错误明细数据！");
		return;
	}

	var src = v_context + "/cr/checkresult/data/exportCSV?tabName="
		+ row.tab_name + "&tabNameEn=" + row.tab_name_en + "&dataDt="
		+ row.data_dt + "&orgNo=" + org_no1 + "&orgType=" + org_type + "&failCount=" + row.fail_count;

	// 先确认表是否存在
	var jso_OpenPars = {tabName: row.tab_name,tabNameEn:row.tab_name_en,dataDt:row.data_dt, orgNo: org_no1};
	var jso_rt = JSPFree.doClassMethodCall("com.yusys.cr.result.service.CrResultSummaryBS", "checkFailTab", jso_OpenPars);

	if (jso_rt.msg != "OK") {
		JSPFree.alert("当前明细表不存在，无法导出明细数据!");

		return;
	}

	var jso_rt = JSPFree.doClassMethodCall("com.yusys.cr.business.service.CrBusinessBS", "getMaxDataNum",{});
	if (parseInt(row.fail_count) > parseInt(jso_rt.downloadNum)) {
		// 先去服务器上指定路径下查看，是否有已生成的文件，如果有则提示是否下载
		var params = {org_no:org_no1,tab_name:row.tab_name,data_dt:row.data_dt};
		var jso_eb = JSPFree.doClassMethodCall("com.yusys.cr.result.service.CrCheckResultBS", "getDownZipFile", params);
		if ("Ok" == jso_eb.msg) {
			$.messager.confirm('提示', '当前服务器已有压缩文件，是否重新生成？', function(r) {
				if (r) {
					JSPFree.alert("当前生成数据量较大，请稍后在服务器上进行下载");
					var xmlhttp = new XMLHttpRequest();
					xmlhttp.open("GET", src);
					xmlhttp.send();
				} else {
					JSPFree.alert("请到服务器上进行下载");
				}
			});
		} else {
			JSPFree.alert("当前生成数据量较大，请稍后在服务器上进行下载");
			var xmlhttp = new XMLHttpRequest();
			xmlhttp.open("GET", src);
			xmlhttp.send();
		}

		return;
	}
	$.messager.confirm('提示', '是否下载所选表的错误信息？', function(r) {
		if (r) {
			var download = $('<iframe id="download" style="display: none;"/>');
			$('body').append(download);

			download.attr('src', src);
		}
	})

}
/*
 * 导出excel 超过上限禁止导出
 */
function exportFialDetailByTabExcel(_btn) {
	var _org_ot = JSPFree.getBillQueryFormValue(d1_1_BillQuery).org_ot;
	var org_no1 = "";
	var org_type = "";
	// 判断是否存在报送机构查询框，如果存在，说明是报送机构，如果不存在，说明是非报送机构
	if (_org_ot != null && _org_ot != "") {
		// 根据中文表名，找对应的英文code
		var jso_org = JSPFree.getHashVOs("SELECT org_no FROM rpt_org_info where org_type='04' and is_org_report='Y' and org_no = '"+_org_ot+"'");
		if(jso_org != null && jso_org.length > 0){
			org_no1 = jso_org[0].org_no;
		}
		org_type = "Y";
	} else {
		org_no1 = org_no;
		org_type = "N";
	}
	var dataset = _btn.dataset;
	var index = dataset.rowindex;
	var rows = d1_1_BillList.datagrid("getRows");
	var row = rows[index];//index为行号
	// 判断，如果fail_count=0,则不能导出明细
	if ("0" == row.fail_count) {
		JSPFree.alert("当前错误记录数为0，无法导出错误明细数据！");
		return;
	}
	// 先确认表是否存在
	var jso_OpenPars = {tabName: row.tab_name,tabNameEn:row.tab_name_en,dataDt:row.data_dt, orgNo: org_no1};
	var jso_rt = JSPFree.doClassMethodCall("com.yusys.cr.result.service.CrResultSummaryBS", "checkFailTab", jso_OpenPars);

	if (jso_rt.msg != "OK") {
		JSPFree.alert("当前明细表不存在，无法导出明细数据!");

		return;
	}
	var jso_rt1 = JSPFree.doClassMethodCall("com.yusys.cr.result.service.CrCheckResultBS", "getDownloaErrordNum", jso_OpenPars);
	if (parseInt(row.fail_count) > parseInt(jso_rt1.data) ) {
		JSPFree.alert("当前错误记录数超出导出上限，无法导出错误明细数据！当前导出上限为:" +jso_rt1.data +"条");
		return;
	}
	var src = v_context + "/cr/checkresult/data/exportExcel?tabName="
		+ row.tab_name + "&tabNameEn=" + row.tab_name_en + "&dataDt="
		+ row.data_dt + "&orgNo=" + org_no1 + "&orgType=" + org_type ;
	$.messager.confirm('提示', '是否下载所选表的错误信息？', function(r) {
		if (r) {
			var download = $('<iframe id="download" style="display: none;"/>');
			$('body').append(download);

			download.attr('src', src);
		}
	})

}
