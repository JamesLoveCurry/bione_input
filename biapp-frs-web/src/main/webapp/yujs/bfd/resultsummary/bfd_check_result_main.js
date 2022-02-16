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
var maskUtil = "";

function AfterInit() {
	maskUtil = FreeUtil.getMaskUtil();
	
	JSPFree.createTabb("d1", [ "按数据表查询", "按检核规则查询" ]);
	
	JSPFree.createBillList("d1_1", "/biapp-bfd/freexml/resultsummary/bfd_result_tab_summary_v.xml",null,{isSwitchQuery:"N",autoquery:"N",list_ispagebar:"Y"}); // 按表查询
	d1_1_BillList.pagerType="2"; //第二种分页类型
	JSPFree.billListBindCustQueryEvent(d1_1_BillList, onTabErrorSummary);
	
	JSPFree.createBillList("d1_2", "/biapp-bfd/freexml/resultsummary/bfd_result_rule_summary_v.xml",null,{isSwitchQuery:"N",autoquery:"N",list_ispagebar:"Y"}); // 按规则查询
	d1_2_BillList.pagerType="2";  //第二种分页类型
	JSPFree.billListBindCustQueryEvent(d1_2_BillList, onRuleErrorSummary);
}

/**
 * 【按数据表查询】，查询时候，拼接sql
 * @param str_data_dt
 * @returns
 */
function onTabErrorSummary(_condition) {
	var tab_sql = "";
	var defvalue = {"_condition":_condition, "_loginUserOrgNo" : str_LoginUserOrgNo};
	var jso_sql = JSPFree.doClassMethodCall("com.yusys.bfd.result.service.BfdResultSummaryBS","getTabSummaryQuerySql", defvalue);
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
	var jso_sql = JSPFree.doClassMethodCall("com.yusys.bfd.result.service.BfdResultSummaryBS","getRuleSummaryQuerySql", defvalue);
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
	var jso_rt = JSPFree.doClassMethodCall("com.yusys.bfd.result.service.BfdResultSummaryBS", "checkFailTab", jso_OpenPars);
	
	if (jso_rt.msg == "OK") {
		JSPFree.openDialog("错误明细","/yujs/bfd/resultsummary/bfd_check_result_tab_fail.js", 950, 500, jso_OpenPars, function(_rtdata){});
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
	var jso_rt = JSPFree.doClassMethodCall("com.yusys.bfd.result.service.BfdResultSummaryBS", "checkFailTab", jso_OpenPars);
	
	if (jso_rt.msg == "OK") {
		JSPFree.openDialog("错误明细","/yujs/bfd/resultsummary/bfd_check_result_rule_fail.js", 950, 600, jso_OpenPars, function(_rtdata){});
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
		var jso_userorg = JSPFree.doClassMethodCall("com.yusys.bfd.common.service.BfdValidateQueryConditionBS", "getUserOrgNoCondition", {"orgNo" : _org_ot});
		if (jso_userorg.msg == "ok") {
			userNo = jso_userorg.data;
		}
		
		var jso_org = JSPFree.doClassMethodCall("com.yusys.bfd.business.service.BfdBusinessBS", "checkUserOrgNo", {"str_LoginUserOrgNo" : userNo});
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
	
	JSPFree.openDialog("历史数据","/yujs/bfd/resultsummary/bfd_check_result_rule_history.js", 700, 450, jso_OpenPars,function(_rtdata){});
}

//按数据表查询：导出明细
function exportFialDetailByTab(_btn){
	var _org_ot = JSPFree.getBillQueryFormValue(d1_1_BillQuery).org_ot;

	var org_no1 = "";
	var org_type = "";
	// 判断是否存在报送机构查询框，如果存在，说明是报送机构，如果不存在，说明是非报送机构
	if (_org_ot != null && _org_ot != "") {
		// 根据中文表名，找对应的英文code
		var jso_org = JSPFree.getHashVOs("SELECT org_no FROM rpt_org_info where org_type='BFD' and is_org_report='Y' and org_no = '"+_org_ot+"'");
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

	// 获取错误表名
	// 计算错误明细表：表名，如Z191031_IBANKDPST
	var result= JSPFree.doClassMethodCall("com.yusys.imas.common.template.ImasCommonTemplate","getZTable",{orgNo: _org_ot, dataDt: row.data_dt, tableNameEn: row.tab_name_en});
	var tabNameEnZ = result.data;
	var whereSql = '';
	var jsn_result = JSPFree.doClassMethodCall("com.yusys.imas.common.service.ImasGetReportBS", "getObjList", null);
	if (jsn_result.code == 'success') {
		whereSql = jsn_result.data;
	}
	var param = {
		type: '2',
		tabNameEn: row.tab_name_en,
		errorTableName: tabNameEnZ,
		detailType: "BFD",
		whereSql: whereSql,
		orgNo: _org_ot
	}
	// 获取数量和sql
	var data= JSPFree.doClassMethodCall("com.yusys.bione.plugin.detail.download.service.DetailDownloadBS","getErrorSql",param);
	var jso_OpenPars = {tabName: row.tab_name,
		tabNameEn:row.tab_name_en,
		dataDt:row.data_dt,
		orgNo: _org_ot,
		dataSql: data.dataSql,
		dataCount: data.count,
		type: '2',
		detailType: "BFD", //表类型
		downloadType: "2" ,  //导出类型：1、维护导出，2、错误明细导出，3、填报错误导出，4、填报全量导出，5、已修改记录导出
		fileType: "CSV",
		dsName: data.dsName,
		params: 'rpt_org_no= '+ _org_ot + '  and data_dt=' + row.data_dt + ' and ' + whereSql
	};
	var jso_rt = JSPFree.doClassMethodCall("com.yusys.bione.plugin.detail.download.service.DetailDownloadBS", "getMaxDataNumCsv",{type: "BFD"});
	exportFinal(data.count, jso_rt, jso_OpenPars);
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
		var jso_org = JSPFree.getHashVOs("SELECT org_no FROM rpt_org_info where org_type='BFD' and is_org_report='Y' and org_no = '"+_org_ot+"'");
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
	var jso_OpenPars = {tabName: row.tab_name,tabNameEn:row.tab_name_en,dataDt:row.data_dt,orgNo:org_no1, pathType: 'result'};
	var jso_rt = JSPFree.doClassMethodCall("com.yusys.bfd.result.service.BfdResultSummaryBS", "checkFailTab", jso_OpenPars);

	if (jso_rt.msg != "OK") {
		JSPFree.alert("当前明细表不存在，无法导出明细数据!");

		return;
	}
	// 获取错误表名
	// 计算错误明细表：表名，如Z191031_IBANKDPST
	var result= JSPFree.doClassMethodCall("com.yusys.imas.common.template.ImasCommonTemplate","getZTable",{orgNo: _org_ot, dataDt: row.data_dt, tableNameEn: row.tab_name_en});
	var tabNameEnZ = result.data;
	var whereSql = '';
	var jsn_result = JSPFree.doClassMethodCall("com.yusys.imas.common.service.ImasGetReportBS", "getObjList", null);
	if (jsn_result.code == 'success') {
		whereSql = jsn_result.data;
	}
	var param = {
		type: '2',
		tabNameEn: row.tab_name_en,
		errorTableName: tabNameEnZ,
		detailType: "BFD",
		whereSql: whereSql,
		orgNo: _org_ot
	}
	// 获取数量和sql
	var data= JSPFree.doClassMethodCall("com.yusys.bione.plugin.detail.download.service.DetailDownloadBS","getErrorSql",param);
	var jso_OpenPars = {tabName: row.tab_name,
		tabNameEn:row.tab_name_en,
		dataDt:row.data_dt,
		orgNo: _org_ot,
		dataSql: data.dataSql,
		dataCount: data.count,
		type: '2',
		detailType: "BFD", //表类型
		downloadType: "2" ,  //导出类型：1、维护导出，2、错误明细导出，3、填报错误导出，4、填报全量导出，5、已修改记录导出
		fileType: "EXCEL",
		dsName: data.dsName,
		params: 'rpt_org_no= '+ _org_ot + '  and data_dt=' + row.data_dt + ' and ' + whereSql
	};
	var jso_rt = JSPFree.doClassMethodCall("com.yusys.bione.plugin.detail.download.service.DetailDownloadBS", "getMaxDataNumExcel",{type: "BFD"});
	exportFinal(data.count, jso_rt, jso_OpenPars);
}
/**
 * 导出
 */
function exportFinal(c,jso_rt, param) {
	if (parseInt(c) > parseInt(jso_rt.downloadNum)) {
		var showMsg = JSPFree.doClassMethodCall("com.yusys.bione.plugin.detail.download.service.DetailDownloadBS", "showMsg",param);
		if (showMsg.code == 'success') {
			JSPFree.confirm('提示', '当前导出数据量较大,可能导出时间很长,是否导出?', function(_isOK){
				if (_isOK) {
					param.code = 'success';
					maskUtil.mask();
					setTimeout(function () {
						// 调用线程启动类
						var json = JSPFree.doClassMethodCall("com.yusys.bione.plugin.detail.download.service.DetailDownloadBS", "exportDataThread", param);
						if (json.code='success') {
							$.messager.alert('提示', '导出数据较大，后台异步生成文件中，请稍后前往下载列表下载。', 'warning');
						} else {
							$.messager.alert('提示', json.msg, 'warning');
						}
						maskUtil.unmask();
					}, 100);
				}
			})

		}else if (showMsg.code == "warn") {
			JSPFree.confirm('提示', '当前导出数据已存在,是否重新生成?', function(_isOK) {
				if (_isOK) {
					param.code = 'warn';
					param.rid = showMsg.rid;
					maskUtil.mask();
					setTimeout(function () {
						// 调用线程启动类
						var json = JSPFree.doClassMethodCall("com.yusys.bione.plugin.detail.download.service.DetailDownloadBS", "exportDataThread", param);
						if (json.code='success') {
							$.messager.alert('提示', '导出数据较大，后台异步生成文件中，请稍后前往下载列表下载。', 'warning');
						} else {
							$.messager.alert('提示', json.msg, 'warning');
						}
						maskUtil.unmask();
					}, 100);
				}
			});
		} else {
			$.messager.alert('提示', showMsg.msg, 'warning');
		}

	} else {
		JSPFree.confirm('提示', '是否导出?', function(_isOK){
			if (_isOK) {
				maskUtil.mask();
				setTimeout(function () {
					var json = JSPFree.doClassMethodCall("com.yusys.bione.plugin.detail.download.service.DetailDownloadBS", "exportData",param);
					if (json.code='success') {
						var filepath = json.data;
						var src = v_context + "/detail/download/data/downloadData?filepath=" + filepath;
						var download = $('<iframe id="download" style="display: none;"/>');
						$('body').append(download);
						download.attr('src', src);
					} else {
						$.messager.alert('提示', json.msg, 'warning');
					}
					maskUtil.unmask();
				}, 100);

			}
		});
	}
}