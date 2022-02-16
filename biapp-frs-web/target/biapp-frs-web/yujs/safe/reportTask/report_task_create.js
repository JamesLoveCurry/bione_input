/**
 * 
 * <pre>
 * Title: 【报送管理】-【报文生成】
 * Description: 报文生成：生成报文
 * </pre>
 * @author wangxy31 
 * @version 1.00.00
   @date 2020年6月10日
 */
var reportType = null;

function AfterInit() {
	var className = jso_OpenPars.className;
	reportType = jso_OpenPars.reportType;
	
	JSPFree.createSplitByBtn("d1", "上下", 200, ["生成报文/onConfirm", "取消/onCancel" ], false);
	JSPFree.createBillCard("d1_A", className, null, {onlyItems:"org_no;report_frequency;data_dt_start;data_dt_end"});
	
	var str_className = "Class:com.yusys.safe.base.common.template.TabBuilderTemplate.getTempletOnlyShowCode('" + reportType + "')";
	JSPFree.createBillList("d1_B", str_className, null, {isSwitchQuery:"N",ishavebillquery:"N",onlyItems:"tab_name;tab_name_en;report_code"});
}

/**
 * 页面初始化后
 * @returns
 */
function AfterBodyLoad() {
	var dom_div = document.getElementById("d1_A_BillCardDiv");
	dom_div.style.overflow="hidden"; // 隐藏滚动框
	
	// 当前机构转换成报送机构
	var jso_data = JSPFree.doClassMethodCall(
			"com.yusys.safe.base.common.service.SafeValidateQueryConditionBS",
			"getReportOrgNoCondition", {_loginUserOrgNo:str_LoginUserOrgNo, report_type:reportType});
	var orgNo = jso_data.data;

	// 机构赋默认值并且置灰
	JSPFree.setBillCardItemValue(d1_A_BillCard, "org_no", orgNo);
	JSPFree.setBillCardItemEditable(d1_A_BillCard, "org_no", false);
	// 数据日期默认为本日
    JSPFree.setBillCardItemValue(d1_A_BillCard, "data_dt", new Date());
    JSPFree.setBillCardItemEditable(d1_A_BillCard, "data_dt", false);

}

/**
 * 全部生成
 */
function onAllConfirm() {
	var form_vlue = JSPFree.getBillCardFormValue(d1_A_BillCard);
    var str_org = form_vlue.org_no;
    if (str_org == null || str_org == "") {
        $.messager.alert('提示', '必须选择机构!', 'info');
        return;
    }
    
    var str_frequency = form_vlue.report_frequency;
    if (str_frequency == null || str_frequency == "") {
        $.messager.alert('提示', '必须选择报送频率!', 'info');
        return;
    }

    var str_date = getNowDate();
    // var str_date = form_vlue.data_dt;
    // if (str_date == null || str_date == "") {
    //     $.messager.alert('提示', '必须选择日期!', 'info');
    //     return;
    // }
    //
    var data_dt_start = form_vlue.data_dt_start;
    if (data_dt_start == null || data_dt_start == "") {
        $.messager.alert('提示', '必须选择数据开始日期!', 'info');
        return;
    }
    var data_dt_end = form_vlue.data_dt_end;
    if (data_dt_end == null || data_dt_end == "") {
        $.messager.alert('提示', '必须选择数据结束日期!', 'info');
        return;
    }
    if (data_dt_start > data_dt_end) {
        $.messager.alert('提示', '结束时间不能小于开始时间!', 'info');
        return;
    }
	var jso_par = {
		data_dt : str_date,
        report_type: reportType,
		str_org: str_org,
		report_frequency: str_frequency,
        data_dt_start:data_dt_start,
        data_dt_end:data_dt_end,
        status_sql_where: " and (data_status = '0' or data_status is null) and approval_status = '2' "
	};
    // 选择生成报文时，所选择的表都需要通过校验,才可生成报文，否则提示
    var checkResult = JSPFree.doClassMethodCall(
        "com.yusys.safe.reporttask.service.SafeCrReportBS", "getTableNamesByStatus", jso_par);
    if (checkResult.status === 'ok') {
        debugger;
        var tables = checkResult.tableNames;
        if (tables.length > 0) {
            $.messager.alert('提示', JSON.stringify(tables) + '表存在未校验通过数据,不能生成报文,请重新选择', 'info');
            return;
        }
    } else {
        $.messager.alert('创建报文失败！', 'info');
        return;
    }
	var jso_rt = JSPFree.doClassMethodCall(
		"com.yusys.safe.reporttask.service.SafeCrReportBS", "createReportTask",
		jso_par);
    jso_rt.isAllConfirm = true;
	JSPFree.closeDialog(jso_rt);
}

/**
 * 选择生成
 */
function onConfirm() {
	var jsy_datas = JSPFree.getBillListSelectDatas(d1_B_BillList);
	var form_vlue = JSPFree.getBillCardFormValue(d1_A_BillCard);
	if (jsy_datas.length <= 0) {
		$.messager.alert('提示', '必须选择一条数据!', 'info');
		return;
	}
	
    var str_org = form_vlue.org_no;
    if (str_org == null || str_org == "") {
        $.messager.alert('提示', '必须选择机构!', 'info');
        return;
    }
    
    var str_frequency = form_vlue.report_frequency;
    if (str_frequency == null || str_frequency == "") {
        $.messager.alert('提示', '必须选择报送频率!', 'info');
        return;
    }
    var str_date = getNowDate();
    // var str_date = form_vlue.data_dt;
	// if (str_date == null || str_date == "") {
	// 	$.messager.alert('提示', '必须选择日期!', 'info');
	// 	return;
	// }
    var data_dt_start = form_vlue.data_dt_start;
    if (data_dt_start == null || data_dt_start == "") {
        $.messager.alert('提示', '必须选择数据开始日期!', 'info');
        return;
    }
    var data_dt_end = form_vlue.data_dt_end;
    if (data_dt_end == null || data_dt_end == "") {
        $.messager.alert('提示', '必须选择数据结束日期!', 'info');
        return;
    }
    if (data_dt_start > data_dt_end) {
        $.messager.alert('提示', '结束时间不能小于开始时间!', 'info');
        return;
	}
    
	var jso_par = {
		chooseTasks : jsy_datas,
		data_dt : str_date,
        report_type: reportType,
        str_org: str_org,
        report_frequency: str_frequency,
        data_dt_start:data_dt_start,
        data_dt_end:data_dt_end,
        status_sql_where: " and (data_status = '0' or data_status is null) and approval_status = '2' "
	};
    // 选择生成报文时，所选择的表都需要通过校验,才可生成报文，否则提示
    var checkResult = JSPFree.doClassMethodCall(
        "com.yusys.safe.reporttask.service.SafeCrReportBS", "getTableNamesByStatus", jso_par);
    if (checkResult.status === 'ok') {
        var tables = checkResult.tableNames;
        if (tables.length > 0) {
            $.messager.alert('提示', JSON.stringify(tables) + '表存在未校验通过数据，请重新选择', 'info');
            return;
        }
    } else {
        $.messager.alert('创建报文失败！', 'info');
        return;
    }
    
	var jso_rt = JSPFree.doClassMethodCall(
			"com.yusys.safe.reporttask.service.SafeCrReportBS", "createReportTask",
			jso_par);
    jso_rt.isAllConfirm = false;
	JSPFree.closeDialog(jso_rt);
}

/**
 * 取消
 * @returns
 */
function onCancel() {
	JSPFree.closeDialog(null);
}

/**
 * 获取本日日期，格式：yyyyMMdd
 */
function getNowDate () {
    var now = new Date();
    var year = now.getFullYear();
    var month = now.getMonth() + 1;
    month = parseInt(month) > 9 ? month : "0" + month;
    var day = now.getDate() + "";
    day = parseInt(day) > 9 ? day : "0" + day;
    return year + "" + month + "" + day;

}