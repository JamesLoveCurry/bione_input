//初始化界面,菜单配置路径是【/frs/yufreejs?js=/yujs/east/checkquery.js】
var org_no = "";
var org_class = "";
var dataDt = null;
var whereSql = '';
var maskUtil = FreeUtil.getMaskUtil();
function AfterInit() {
	JSPFree.createTabb("d1", [ "按数据表查询", "按检核规则查询" ]);
	if (jso_OpenPars != '') {
		if(jso_OpenPars.data_dt!=null){
			dataDt = jso_OpenPars.data_dt;
		}
	}
	var jsn_result = JSPFree.doClassMethodCall("com.yusys.east.common.service.EastGetReportBS", "getReportCNList", null);
	if (jsn_result.code == 'success') {
		whereSql = jsn_result.data;
	}
	// 判断是否是总行，如果是总行则不禁用，如果是其他则禁用
	var jso_org = JSPFree.getHashVOs("SELECT org_no,org_class FROM rpt_org_info where org_type='04'  and mgr_org_no = '"+str_LoginUserOrgNo+"'");
	if(jso_org != null && jso_org.length > 0){
		org_no = jso_org[0].org_no;
		org_class = jso_org[0].org_class;
	}

	if (org_no!=null && org_no!="") {
		JSPFree.createBillList("d1_1", "/biapp-east/freexml/east/result/east_cr_summer_data1.xml",null,{isSwitchQuery:"N",autoquery:"N",list_ispagebar:"Y"}); // 按表查询
		d1_1_BillList.pagerType="2"; //第二种分页类型
		FreeUtil.loadBillQueryData(d1_1_BillList, {org_ot:org_no});
		var _sql = getSql(dataDt);
		JSPFree.queryDataBySQL(d1_1_BillList, _sql);
		JSPFree.billListBindCustQueryEvent(d1_1_BillList, onErrorSummary);
		
		JSPFree.createBillList("d1_2", "/biapp-east/freexml/east/result/east_cr_summer_QueryByRule1.xml",null,{isSwitchQuery:"N",autoquery:"N",list_ispagebar:"Y"}); // 按规则查询
		d1_2_BillList.pagerType="2";  //第二种分页类型
		FreeUtil.loadBillQueryData(d1_2_BillList, {org_ot1:org_no});
		var _sql2 = getSql1(dataDt);
		JSPFree.queryDataBySQL(d1_2_BillList, _sql2);
		JSPFree.billListBindCustQueryEvent(d1_2_BillList, onErrorSummary1);
	}
}

//页面加载结束后
function AfterBodyLoad(){

}

function getSql(str_data_dt) {
	var condition = "";
	var jso_rt = JSPFree.doClassMethodCall("com.yusys.east.checkrule.rulesummary.service.ValidateQueryCondition","getQueryCondition",{"_loginUserOrgNo" : str_LoginUserOrgNo});
	if(jso_rt.msg == "ok"){
		condition = jso_rt.condition;
	}
	// 判断是否是总行，如果是总行则不禁用，如果是其他则禁用
	var jso_org = JSPFree.getHashVOs("SELECT org_no,org_class FROM rpt_org_info where org_type='04'  and mgr_org_no = '"+str_LoginUserOrgNo+"'");
	if(jso_org != null && jso_org.length > 0){
		org_no = jso_org[0].org_no;
	}
	if (str_data_dt == "" || str_data_dt == null) {
		// 获取最新一期的日期
		var jso_data = JSPFree.getHashVOs("SELECT max(data_dt) data_dt FROM v_east_cr_summary1");
		if(jso_data != null && jso_data.length > 0){
			str_data_dt = jso_data[0].data_dt;
		}
	}
	
	FreeUtil.loadBillQueryData(d1_1_BillList, {data_dt:str_data_dt});
	
	// 如果是总行取busi_type是1_org开头的辖内
	// 如果是报送分行取busi_type是2_org开头的辖内
	// 如果是其他的取busi_type是1开头的辖内
	var _sql ="SELECT tab_name, SUM (sum_count) sum_count, SUM (fail_count) fail_count, " +
		"CASE WHEN SUM (sum_count) = 0 THEN '' ELSE TO_CHAR (ROUND (SUM (fail_count) / SUM (sum_count), 4) * 100, 'FM999990.00') || '%' END AS fail_rate,data_dt " +
		"FROM (SELECT tab_name, sum_count, fail_count, data_dt,org_no FROM V_EAST_CR_SUMMARY1 WHERE 1 = 1";

	_sql += " and " + whereSql+ " and " + condition;
	// if (org_class == '总行') {
	// 	_sql = _sql + " and busi_type like '1_"+org_no+"%'";
	// } else {
	// 	_sql = _sql + " and busi_type like '2_"+org_no+"%'" + " and " + condition;
	// }
	
	_sql = _sql + ") where 1=1";
	_sql = _sql + " and data_dt='" + str_data_dt +"' and org_no='" + org_no + "'";

	_sql += " group by tab_name, data_dt order by fail_count desc ";
	
	return _sql;
}

function getSql1(str_data_dt) {
	var condition = "";
	var jso_rt = JSPFree.doClassMethodCall("com.yusys.east.checkrule.rulesummary.service.ValidateQueryCondition","getQueryCondition",{"_loginUserOrgNo" : str_LoginUserOrgNo});
	if(jso_rt.msg == "ok"){
		condition = jso_rt.condition;
	}
	// 判断是否是总行，如果是总行则不禁用，如果是其他则禁用
	var jso_org = JSPFree.getHashVOs("SELECT org_no,org_class FROM rpt_org_info where org_type='04'  and mgr_org_no = '"+str_LoginUserOrgNo+"'");
	if(jso_org != null && jso_org.length > 0){
		org_no = jso_org[0].org_no;
	}
	if (str_data_dt == "" || str_data_dt == null) {
		// 获取最新一期的日期
		var jso_data = JSPFree.getHashVOs("SELECT max(data_dt) data_dt FROM v_east_cr_summary_rule");
		if(jso_data != null && jso_data.length > 0){
			str_data_dt = jso_data[0].data_dt;
		}
	}
	
	FreeUtil.loadBillQueryData(d1_2_BillList, {data_dt:str_data_dt});

	// 如果是总行取busi_type是1_org开头的辖内
	// 如果是报送分行取busi_type是2_org开头的辖内
	// 如果是其他的取busi_type是1开头的辖内
	var _sql ="select data_dt, rule_id, report_no, rule_name, tab_name, col_name,type_cd,disp_cd, sum(sum_count) sum_count, sum(fail_count) fail_count," +
	"CASE WHEN SUM (sum_count) = 0 THEN '' ELSE TO_CHAR (ROUND (SUM (fail_count) / SUM (sum_count), 4) * 100, 'FM999990.00') || '%' END AS fail_rate " +
	"from ( " +
	"select s.data_dt,s.rule_id,s.report_no,s.rule_name,s.tab_name,s.col_name,s.type_cd,s.disp_cd,s.weight,s.sum_count,s.fail_count,s.create_tm,s.rid,s.org_no,r.rule_source " +
	"from v_east_cr_summary_rule s left join east_cr_rule r on s.rule_id = r.id WHERE 1 = 1";
	_sql += " and " + whereSql.replace("tab_name", "s.tab_name")+ " and " + condition;
	// if (org_class == '总行') {
	// 	_sql = _sql + " and busi_type like '1_"+org_no+"%'";
	// } else {
	// 	_sql = _sql + " and busi_type like '2_"+org_no+"%'" + " and " + condition;
	// }
	
	_sql = _sql + ") where 1=1";
	_sql = _sql + " and data_dt='" + str_data_dt +"' and org_no='" + org_no + "'";

	_sql += "  group by data_dt, rule_id, report_no, rule_name, tab_name, col_name,type_cd,disp_cd ";
	_sql += " order by fail_count desc";
	
	return _sql;
}

function onErrorSummary(_condition){
	var org_no = JSPFree.getBillQueryFormValue(d1_1_BillQuery).org_ot;
	var jso_data = JSPFree.getHashVOs("select org_level from rpt_org_info where org_type='04'  and org_no = '"+org_no+"'");
	var org_level = '';
	if (jso_data != null && jso_data.length > 0) {
		org_level = jso_data[0].org_level;
	}

	var _sql ="SELECT tab_name, SUM (sum_count) sum_count, SUM (fail_count) fail_count, " +
			"CASE WHEN SUM (sum_count) = 0 THEN '' ELSE TO_CHAR (ROUND (SUM (fail_count) / SUM (sum_count), 4) * 100, 'FM999990.00') || '%' END AS fail_rate,data_dt " +
			"FROM (SELECT tab_name, sum_count, fail_count, data_dt, org_no FROM V_EAST_CR_SUMMARY1 WHERE 1 = 1";
	if(org_level>2){
		_sql ="SELECT tab_name, SUM (sum_count) sum_count, SUM (fail_count) fail_count, " +
			"CASE WHEN SUM (sum_count) = 0 THEN '' ELSE TO_CHAR (ROUND (SUM (fail_count) / SUM (sum_count), 4) * 100, 'FM999990.00') || '%' END AS fail_rate,data_dt " +
			"FROM (SELECT tab_name, sum_count, fail_count, data_dt, issued_no FROM V_EAST_CR_SUMMARY_ISSUED_NO WHERE 1 = 1";
	}
	_sql += " and " + whereSql;
	_sql = _sql + ") where 1=1";

	if(_condition!=""){
		_condition = _condition.replace('org_ot', 'org_no');
		if(org_level>2){
			_condition = _condition.replace('org_no', 'issued_no');
		}
		_sql += "and" + _condition;
	}
	_sql += " group by tab_name, data_dt order by fail_count desc";
	
	JSPFree.queryDataBySQL(d1_1_BillList, _sql);
	FreeUtil.resetToFirstPage(d1_1_BillList); //手工跳转到第一页
}

function onErrorSummary1(_condition){
	var org_no = JSPFree.getBillQueryFormValue(d1_2_BillQuery).org_ot1;
	var jso_data = JSPFree.getHashVOs("select org_level from rpt_org_info where org_type='04'  and org_no = '"+org_no+"'");
	var org_level = '';
	if (jso_data != null && jso_data.length > 0) {
		org_level = jso_data[0].org_level;
	}

	var _sql ="select data_dt, rule_id, report_no, rule_name, tab_name, col_name,type_cd,disp_cd, sum(sum_count) sum_count, sum(fail_count) fail_count, " +
			"CASE WHEN SUM (sum_count) = 0 THEN '' ELSE TO_CHAR (ROUND (SUM (fail_count) / SUM (sum_count), 4) * 100, 'FM999990.00') || '%' END AS fail_rate " +
			"from ( " +
			"select s.data_dt,s.rule_id,s.report_no,s.rule_name,s.tab_name,s.col_name,s.type_cd,s.disp_cd,s.weight,s.sum_count,s.fail_count,s.create_tm,s.rid,s.org_no,r.rule_source " +
			"from v_east_cr_summary_rule s left join east_cr_rule r on s.rule_id = r.id WHERE 1 = 1";
	if(org_level>2){
		_sql ="select data_dt, rule_id, report_no, rule_name, tab_name, col_name,type_cd,disp_cd, sum(sum_count) sum_count, sum(fail_count) fail_count, " +
			"CASE WHEN SUM (sum_count) = 0 THEN '' ELSE TO_CHAR (ROUND (SUM (fail_count) / SUM (sum_count), 4) * 100, 'FM999990.00') || '%' END AS fail_rate " +
			"from ( " +
			"select s.data_dt,s.rule_id,s.report_no,s.rule_name,s.tab_name,s.col_name,s.type_cd,s.disp_cd,s.weight,s.sum_count,s.fail_count,s.create_tm,s.rid,s.issued_no,r.rule_source " +
			"from v_east_cr_summary_rule_issu s left join east_cr_rule r on s.rule_id = r.id WHERE 1 = 1";
	}
	_sql += " and " + whereSql.replace("tab_name", "s.tab_name");
	_sql = _sql + ") where 1=1";
	if(_condition!=""){
		if(_condition!=""){
			_condition = _condition.replace('org_ot1', 'org_no');
			if(org_level>2){
				_condition = _condition.replace('org_no', 'issued_no');
			}
			_sql += "and" + _condition;
		}
	}
	_sql += " group by data_dt, rule_id, report_no, rule_name, tab_name, col_name,type_cd,disp_cd ";
	_sql += " order by fail_count desc";
	
	JSPFree.queryDataBySQL(d1_2_BillList, _sql);
	FreeUtil.resetToFirstPage(d1_2_BillList); //手工跳转到第一页
}

//按数据表查询：导出
function tabDownload() {
	if (d1_1_BillList.CurrSQL3 == null || "undefined" == d1_1_BillList.CurrSQL3) {
		JSPFree.alert("当前无记录！");
		return;
	}
	
	JSPFree.downloadBillListCurrSQL3AsExcel(null, d1_1_BillList);
}

//按检核规则查询：导出
function ruleExports() {
	if (d1_2_BillList.CurrSQL3 == null || "undefined" == d1_2_BillList.CurrSQL3) {
		JSPFree.alert("当前无记录！");
		return;
	}
	
	JSPFree.downloadBillListCurrSQL3AsExcel(null, d1_2_BillList);
}


//add by wangxy31
//按数据表查询：查看明细
function viewFailDetailByTab(_btn) {
	var _org_ot = JSPFree.getBillQueryFormValue(d1_1_BillQuery).org_ot;

	var org_no1 = "";
	var org_type = "";
	var org_level = '';
	// 判断是否存在报送机构查询框，如果存在，说明是报送机构，如果不存在，说明是非报送机构
	if (_org_ot != null && _org_ot != "") {
		// 根据中文表名，找对应的英文code
		var jso_org = JSPFree.getHashVOs("SELECT org_no,org_level FROM rpt_org_info where org_type='04'  and org_no = '"+_org_ot+"'");
		if(jso_org != null && jso_org.length > 0){
			org_no1 = jso_org[0].org_no;
			org_level = jso_org[0].org_level;
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

	// 判断，如果fail_count=0,则不能查看明细
	if ("0" == row.fail_count) {
		JSPFree.alert("当前错误记录数为0，无法查看错误明细数据！");
		return;
	}
	
	var jso_OpenPars = {tabName: row.tab_name,tabNameEn:row.tab_name_en,dataDt:row.data_dt,orgNo:org_no1,org_type:org_type,org_level:org_level};
	// 查看详情前，先确认表是否存在
	var jso_rt = JSPFree.doClassMethodCall("com.yusys.east.checkresult.summary.service.FailDetailBS", 
			"checkTab",jso_OpenPars);
	
	if (jso_rt.msg == "OK") {
		JSPFree.openDialog("错误明细","/yujs/east/result/failDetail.js", 950, 500, jso_OpenPars,function(_rtdata){});
	} else {
		JSPFree.alert("当前明细表不存在，无法查看明细数据!");
	}
}

//按数据表查询：导出明细
function exportFialDetailByTab(_btn){
	var _org_ot = JSPFree.getBillQueryFormValue(d1_1_BillQuery).org_ot;
	var data_dt = JSPFree.getBillQueryFormValue(d1_1_BillQuery).data_dt.replace(/-/g, "");
	var org_no1 = "";
	var org_type = "";
	var org_level = '';
	// 判断是否存在报送机构查询框，如果存在，说明是报送机构，如果不存在，说明是非报送机构
	if (_org_ot != null && _org_ot != "") {
		// 根据中文表名，找对应的英文code
		var jso_org = JSPFree.getHashVOs("SELECT org_no,org_level FROM rpt_org_info where org_type='04'  and org_no = '"+_org_ot+"'");
		if(jso_org != null && jso_org.length > 0){
			org_no1 = jso_org[0].org_no;
			org_level = jso_org[0].org_level;
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
	var queryType = '1';
	if(org_level>2){
		queryType = '3';
	}

	// 先确认表是否存在
	var jso_OpenPars = {tabName: row.tab_name,tabNameEn:row.tab_name_en,dataDt:row.data_dt,orgNo:org_no1,org_type:org_type};
	var jso_rt = JSPFree.doClassMethodCall("com.yusys.east.checkresult.summary.service.FailDetailBS",
		"checkTab",jso_OpenPars);

	if (jso_rt.msg != "OK") {
		JSPFree.alert("当前明细表不存在，无法导出明细数据!");

		return;
	}
	var errorTableName = jso_rt.tableName;
	var str_ds = jso_rt.dsName;
	var sql = " select * from " + errorTableName + " where org_no='" + _org_ot + "'";
	var new_sql = 'select count(1) c from ('+sql+') t';
	var jso_data = JSPFree.getHashVOsByDS(new_sql, str_ds);
	var c = jso_data[0].c;
	var param = {tabName: row.tab_name,
		tabNameEn: row.tab_name_en,
		dsName: str_ds,
		dataSql:sql,
		dataCount: c,
		orgNo: org_no1,
		dataDt: row.data_dt,
		type: '2',
		queryType:queryType,
		detailType: "EAST", //表类型
		downloadType: "2" ,  //导出类型：1、维护导出，2、错误明细导出，3、填报错误导出，4、填报全量导出，5、已修改记录导出
		fileType: "EXCEL",
		params: 'org_no=' + _org_ot + ' and cjrq=' + data_dt

	};
	var jso_rt = JSPFree.doClassMethodCall("com.yusys.bione.plugin.detail.download.service.DetailDownloadBS", "getMaxDataNumExcel",{type: "EAST"});
	exportFinal(c, jso_rt, param);
}
/**
 * 导出
 */
function exportFinal(c,jso_rt, param) {
	if (parseInt(c) > parseInt(jso_rt.downloadNum)) {
		var showMsg = JSPFree.doClassMethodCall("com.yusys.bione.plugin.detail.download.service.DetailDownloadBS", "showMsg",param);
		if (showMsg.code == 'success') {
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
//按检核规则查询：查看明细
function viewFailDetailByRule(_btn) {
	var _org_ot = JSPFree.getBillQueryFormValue(d1_2_BillQuery).org_ot1;

	var org_no1 = "";
	var org_type = "";
	var org_level = '';
	// 判断是否存在报送机构查询框，如果存在，说明是报送机构，如果不存在，说明是非报送机构
	if (_org_ot != null && _org_ot != "") {
		// 根据中文表名，找对应的英文code
		var jso_org = JSPFree.getHashVOs("SELECT org_no,org_level FROM rpt_org_info where org_type='04' and org_no = '" + _org_ot + "'");
		if (jso_org != null && jso_org.length > 0) {
			org_no1 = jso_org[0].org_no;
			org_level = jso_org[0].org_level;
		}
		org_type = "Y";
	} else {
		org_no1 = org_no;
		org_type = "N";
	}

	var dataset = _btn.dataset;
	var index = dataset.rowindex;
	var rows = d1_2_BillList.datagrid("getRows");
	var row = rows[index];//index为行号

	// 判断，如果fail_count=0,则不能查看明细
	if ("0" == row.fail_count) {
		JSPFree.alert("当前错误记录数为0，无法查看错误明细数据！");
		return;
	}

	// 常规校验、总分核对、跨监管一致性，记录数校验（无明细）
	var disp_cd = row.disp_cd;
	if (disp_cd.indexOf("常规") != -1) {
		var jso_OpenPars = {
			type: "1",
			tabName: row.tab_name,
			tabNameEn: row.tab_name_en,
			dataDt: row.data_dt,
			ruleId: row.rule_id,
			orgNo: org_no1,
			org_type: org_type,
			org_level: org_level,
			addr: ''
		};
		// 查看详情前，先确认表是否存在
		var jso_rt = JSPFree.doClassMethodCall("com.yusys.east.checkresult.summary.service.FailDetailBS",
			"checkTab", jso_OpenPars);

		if (jso_rt.msg == "OK") {
			JSPFree.openDialog("错误明细", "/yujs/east/result/failDetailByRule.js", 950, 600, jso_OpenPars, function (_rtdata) {
			});
		} else {
			JSPFree.alert("当前明细表不存在，无法查看明细数据!");
		}
	} else if (disp_cd.indexOf("总分核对") != -1) {
		var jso_OpenPars = {
			type: "2",
			tabName: row.tab_name,
			tabNameEn: row.tab_name_en,
			dataDt: row.data_dt,
			ruleId: row.rule_id,
			orgNo: org_no1,
			org_type: org_type,
			org_level: org_level,
			addr: ''
		};
		JSPFree.openDialog("错误明细", "/yujs/east/result/failDetailByRule.js", 950, 600, jso_OpenPars, function (_rtdata) {
		});
	} else if (disp_cd.indexOf("跨监管一致性") != -1) {
		var jso_OpenPars = {
			type: "3",
			tabName: row.tab_name,
			tabNameEn: row.tab_name_en,
			dataDt: row.data_dt,
			ruleId: row.rule_id,
			orgNo: org_no1,
			org_type: org_type,
			org_level: org_level,
			addr: ''
		};
		JSPFree.openDialog("错误明细", "/yujs/east/result/failDetailByRule.js", 950, 600, jso_OpenPars, function (_rtdata) {
		});
	} else if (disp_cd.indexOf("记录数") != -1) {
		JSPFree.alert("记录数校验，无明细数据!");
	}
}

//按数据表查询：导出明细
function exportFialDetailByRule(_btn){
	var _org_ot = JSPFree.getBillQueryFormValue(d1_2_BillQuery).org_ot1;

	var org_no1 = "";
	var org_type = "";
	var org_level = '';
	// 判断是否存在报送机构查询框，如果存在，说明是报送机构，如果不存在，说明是非报送机构
	if (_org_ot != null && _org_ot != "") {
		// 根据中文表名，找对应的英文code
		var jso_org = JSPFree.getHashVOs("SELECT org_no,org_level FROM rpt_org_info where org_type='04' and is_org_report='Y' and org_no = '"+_org_ot+"'");
		if(jso_org != null && jso_org.length > 0){
			org_no1 = jso_org[0].org_no;
			org_level = jso_org[0].org_level;
		}
		org_type = "Y";
	} else {
		org_no1 = org_no;
		org_type = "N";
	}

	var dataset = _btn.dataset;
	var index = dataset.rowindex;
	var rows = d1_2_BillList.datagrid("getRows");
	var row = rows[index];//index为行号

	// 判断，如果fail_count=0,则不能导出明细
	if ("0" == row.fail_count) {
		JSPFree.alert("当前错误记录数为0，无法导出错误明细数据！");
		return;
	}
	var jso_rt = JSPFree.doClassMethodCall("com.yusys.east.checkresult.summary.service.EastCheckResultBS", "getMaxDataNum",{});
	if (row.fail_count > jso_rt.download_num) {
		JSPFree.alert("当前生成数据量较大，请稍后在服务器上进行下载");
	} else {
		$.messager.confirm('提示', '是否要导出错误明细？', function (r) {
			if (r) {
				// 常规校验、总分核对、跨监管一致性，记录数校验（无明细）
				var disp_cd = row.disp_cd;
				if (disp_cd.indexOf("常规") != -1) {
					// 先确认表是否存在
					var jso_OpenPars = {
						tabName: row.tab_name,
						tabNameEn: row.tab_name_en,
						dataDt: row.data_dt,
						ruleId: row.rule_id,
						orgNo: org_no1,
						org_type: org_type
					};
					var jso_rt = JSPFree.doClassMethodCall("com.yusys.east.checkresult.summary.service.FailDetailBS",
						"checkTab", jso_OpenPars);

					if (jso_rt.msg == "OK") {
						var src = v_context + "/east/checkresult/data/exportCVSByRule?tab_name="
							+ row.tab_name + "&tab_name_en=" + row.tab_name_en + "&dataDt=" + row.data_dt
							+ "&ruleId=" + row.rule_id + "&orgNo=" + org_no1 + "&orgType=" + org_type;

						var download = null;
						download = $('<iframe id="download" style="display: none;"/>');
						$('body').append(download);

						download.attr('src', src);
					} else {
						JSPFree.alert("当前明细表不存在，无法导出明细数据!");
					}
				} else if (disp_cd.indexOf("总分核对") != -1) {
					var src = v_context + "/east/checkresult/data/exportCVSByRuleGl?tab_name="
						+ row.tab_name + "&dataDt=" + row.data_dt
						+ "&ruleId=" + row.rule_id + "&orgNo=" + org_no + "&orgType=" + org_type;

					var download = null;
					download = $('<iframe id="download" style="display: none;"/>');
					$('body').append(download);

					download.attr('src', src);
				} else if (disp_cd.indexOf("跨监管一致性") != -1) {
					var src = v_context + "/east/checkresult/data/exportCVSByRuleUprr?tab_name="
						+ row.tab_name + "&dataDt=" + row.data_dt
						+ "&ruleId=" + row.rule_id + "&orgNo=" + org_no + "&orgType=" + org_type;

					var download = null;
					download = $('<iframe id="download" style="display: none;"/>');
					$('body').append(download);

					download.attr('src', src);
				} else if (disp_cd.indexOf("记录数") != -1) {
					JSPFree.alert("记录数校验，无明细数据!");
				}
			}
		});
	}
}

function historyByRule(_btn){
	var _org_ot = JSPFree.getBillQueryFormValue(d1_2_BillQuery).org_ot1;

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

	var dataset = _btn.dataset;
	var index = dataset.rowindex;
	var rows = d1_2_BillList.datagrid("getRows");
	var row = rows[index];//index为行号

	var jso_OpenPars = {};
	jso_OpenPars.rule_name = row.rule_name;
	jso_OpenPars.data_dt = row.data_dt;
	jso_OpenPars.rule_id = row.rule_id;
	jso_OpenPars.org_no = org_no1;
	jso_OpenPars.org_type = org_type;
	jso_OpenPars.org_class = org_class1;

	JSPFree.openDialog("历史数据","/yujs/east/result/checkresultRuleHistory.js", 700, 450, jso_OpenPars,function(_rtdata){});
}
