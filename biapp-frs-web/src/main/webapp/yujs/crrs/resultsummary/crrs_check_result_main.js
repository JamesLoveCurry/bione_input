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
var data_dt = "";
var maskUtil = "";

function AfterInit() {
    maskUtil = FreeUtil.getMaskUtil();
	org_no = CrrsFreeUtil.getOrgNo(str_LoginUserOrgNo);

	JSPFree.createTabb("d1", ["按数据表查询", "按规则查询"]);
    JSPFree.createBillList("d1_1", "/biapp-crrs/freexml/crrs/resultsummary/crrs_result_tab_summary_v.xml", null, {isSwitchQuery: "N", autoquery: "N", list_ispagebar: "Y"}); // 按表查询
    JSPFree.createBillList("d1_2", "/biapp-crrs/freexml/crrs/resultsummary/crrs_result_rule_summary_v.xml", null, {isSwitchQuery: "N", autoquery: "N", list_ispagebar: "Y"}); // 按规则查询
    
    d1_1_BillList.pagerType = "2"; //第二种分页类型
    JSPFree.billListBindCustQueryEvent(d1_1_BillList, onTabErrorSummary);
    
    d1_2_BillList.pagerType = "2"; //第二种分页类型
    JSPFree.billListBindCustQueryEvent(d1_2_BillList, onRuleErrorSummary);
}

/**
 * 【按数据表查询】，查询时候，拼接sql
 * @param str_data_dt
 * @returns
 */
function onTabErrorSummary(_condition) {
	var tab_sql = "";
	var defvalue = {"_condition":_condition, "_orgno" : org_no};
	var jso_sql = JSPFree.doClassMethodCall("com.yusys.crrs.result.service.CrrsResultSummaryBS","getTabSummaryQuerySql", defvalue);
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
	var defvalue = {"_condition":_condition, "_orgno" : org_no};
	var jso_sql = JSPFree.doClassMethodCall("com.yusys.crrs.result.service.CrrsResultSummaryBS","getRuleSummaryQuerySql", defvalue);
	if (jso_sql.result == "OK") {
		rule_sql = jso_sql.rule_sql;
	}
	
    JSPFree.queryDataBySQL(d1_2_BillList, rule_sql);
	FreeUtil.resetToFirstPage(d1_2_BillList); // 手工跳转到第一页
}

/**
 * 按数据表查询：查看明细
 * @param _btn
 * @returns
 */
function viewFailDetailByTab(_btn) {
    var dataset = _btn.dataset;
    var index = dataset.rowindex;
    var rows = d1_1_BillList.datagrid("getRows");
    var row = rows[index]; // index为行号

    var issuedNo = row.issued_no;
    // 判断，如果fail_count=0, 则不能查看明细
    if ("0" == row.fail_sum) {
        JSPFree.alert("当前错误记录数为0，无法查看错误明细数据！");
        return;
    }

    var jso_OpenPars = {
        tabName: row.tablename,
        tabNameEn: row.tablename_en,
        dataDt: row.reportdate,
        orgNo: issuedNo
    };

    JSPFree.openDialog("错误明细", "/yujs/crrs/resultsummary/crrs_check_result_tab_fail.js", 950, 500, jso_OpenPars, function (_rtdata) {
    });
}

/**
 * 按规则查询：查看明细
 * @param _btn
 * @returns
 */
function viewFailDetailByRule(_btn) {
    var issuedNo = JSPFree.getBillQueryFormValue(d1_2_BillQuery).issued_no;

    var dataset = _btn.dataset;
    var index = dataset.rowindex;
    var rows = d1_2_BillList.datagrid("getRows");
    var row = rows[index]; // index为行号

    // 判断，如果fail_count=0, 则不能查看明细
    if ("0" == row.fail_sum) {
        JSPFree.alert("当前错误记录数为0，无法查看错误明细数据！");
        return;
    }

    var jso_OpenPars = {
        tabName: row.tablename,
        tabNameEn: row.tablename_en,
        dataDt: row.reportdate,
        problemcode: row.problemcode,
        orgNo: issuedNo
    };

    JSPFree.openDialog("错误明细", "/yujs/crrs/resultsummary/crrs_check_result_tab_fail.js", 950, 500, jso_OpenPars, function (_rtdata) {
    });
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
 * 按数据表查询：导出明细
 * @param _btn
 * @returns
 */
function exportFialDetailByTab(_btn){
	var issuedNo = JSPFree.getBillQueryFormValue(d1_1_BillQuery).issued_no;

	var dataset = _btn.dataset;
	var index = dataset.rowindex;
	var rows = d1_1_BillList.datagrid("getRows");
	var row = rows[index];//index为行号

	// 判断，如果fail_count=0,则不能导出明细
	if ("0" == row.fail_count) {
		JSPFree.alert("当前错误记录数为0，无法导出错误明细数据！");
		return;
	}

    var jso_OpenPars = {
        tabName: row.tablename,
        tabNameEn: row.tablename_en,
        dataDt: row.reportdate,
        problemcode: row.problemcode,
        orgNo: issuedNo
    };
	JSPFree.confirm('提示', '当前导出数据量较大,可能导出时间很长,是否导出?', function(_isOK){
		if (_isOK) {
			maskUtil.mask();
			setTimeout(function () {
				var json = JSPFree.doClassMethodCall("com.yusys.crrs.result.service.CrrsCheckResultBS", "getBusinessDataByCVS",jso_OpenPars);
				if (json.code='success') {
					var filepath = json.data;
					var src = v_context + "/crrs/checkresult/data/export?filepath=" + filepath;
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

/**
 * 导出excel 超过上限禁止导出
 */
function exportFialDetailByTabExcel(_btn) {
	var issuedNo = JSPFree.getBillQueryFormValue(d1_1_BillQuery).issued_no;
	
	var dataset = _btn.dataset;
	var index = dataset.rowindex;
	var rows = d1_1_BillList.datagrid("getRows");
	var row = rows[index];//index为行号

	// 判断，如果fail_count=0,则不能导出明细
	if ("0" == row.fail_count) {
		JSPFree.alert("当前错误记录数为0，无法导出错误明细数据！");
		return;
	}
	
	var jso_OpenPars = {
        tabName: row.tablename,
        tabNameEn: row.tablename_en,
        dataDt: row.reportdate,
        problemcode: row.problemcode,
        orgNo: issuedNo
    };
	
	$.messager.confirm('提示', '数据量越大,导出等待时间越长,是否导出？', function(r) {
		if (r) {
			maskUtil.mask();
			setTimeout(function () {
				var json = JSPFree.doClassMethodCall("com.yusys.crrs.result.service.CrrsCheckResultBS", "getBusinessDataByExcel",jso_OpenPars);
				if (json.code='success') {
					var filepath = json.data;
					var src = v_context + "/crrs/checkresult/data/export?filepath=" + filepath;
					var download = $('<iframe id="download" style="display: none;"/>');
					$('body').append(download);
					download.attr('src', src);
				} else {
					$.messager.alert('提示', json.msg, 'warning');
				}
				maskUtil.unmask();
			}, 100);
		}
	})
}