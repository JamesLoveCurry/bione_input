/**
 *
 * <pre>
 * Title:校验选择日期
 * Description:校验选择日期
 * </pre>
 * @author miaokx
 * @version 1.00.00
 * @date 2020/6/9 17:01
 */

var reportType = null;

function AfterInit() {
	reportType = jso_OpenPars.reportType;
	
	// 获取【检核日志】常量类
	tab_name = SafeFreeUtil.getTableNames().SAFE_ENGINE_LOG;
	// 获取英文表名
	var jso_data = JSPFree.doClassMethodCall(
		"com.yusys.safe.base.common.service.SafeCommonBS",
		"getTabNameByEn", {tab_name:tab_name});
	var tab_name_en = jso_data.tab_name_en;
	JSPFree.createSplitByBtn("d1", "上下", 130, ["确定/onConfirm","取消/onCancel"], false);
	
	var str_className = "Class:com.yusys.safe.base.common.template.CommonBuilderTemplate.getTemplet('" + tab_name + "','" + tab_name_en + "','" + reportType + "','" + str_LoginUserOrgNo + "')";
	JSPFree.createBillCard("d1_A", str_className, null, {onlyItems: "data_dt_start;data_dt_end"});
	
	$("#d1_A_form").append("<div style=\"text-indent:2em;width:100%;margin-top:-180px;\">执行数据校验，会清空校验结果数据，如果需要历史数据，请先导出校验结果。</div>");
	
	var tab_name_className = "Class:com.yusys.safe.base.common.template.TabBuilderTemplate.getTemplet('" + reportType + "')";
	JSPFree.createBillList("d1_B", tab_name_className, null, {isSwitchQuery:"N",ishavebillquery:"N",onlyItems:"tab_name;tab_name_en;report_code"});
}

function AfterBodyLoad() {
	document.getElementById("d1_A_form").style.overflowY = 'hidden';
}

/**
 * 确定
 * @returns
 */
function onConfirm(){
	var jso_cardData = JSPFree.getBillCardFormValue(d1_A_BillCard);
	var data_dt_start = jso_cardData.data_dt_start;
	if (!jso_cardData.data_dt_start) {
		$.messager.alert('提示', '必须选择数据开始日期!', 'info');
		return;
	}
	var data_dt_end = jso_cardData.data_dt_end;
	if (!jso_cardData.data_dt_end) {
		$.messager.alert('提示', '必须选择数据结束日期!', 'info');
		return;
	}
	if (data_dt_start > data_dt_end) {
		$.messager.alert('提示', '结束时间不能小于开始时间!', 'info');
		return;
	}
	
	var jsy_datas = JSPFree.getBillListSelectDatas(d1_B_BillList);
	if (!jsy_datas || jsy_datas.length == 0) {
		JSPFree.alert("请选择报表!");
		return;
	}
	var tabNames = "";
	for (var i = 0; i < jsy_datas.length; i++) {
		tabNames += jsy_datas[i].tab_name + ",";
	}
	tabNames = tabNames.substring(0, tabNames.lastIndexOf(","));
	JSPFree.doClassMethodCall2("com.yusys.safe.reportCheck.service.SafeEngineBS", "startEngineTask", {
		data_dt_start: jso_cardData.data_dt_start,
		data_dt_end: jso_cardData.data_dt_end,
		report_type: reportType,
		type: SafeFreeUtil.getSafeEngineType().MANUAL,
		tab_name: tabNames,
		user_org_no: str_LoginUserOrgNo
	},function (resultData) {
		if (resultData.code === 'error') {
			JSPFree.alert(resultData.msg);
			return;
		} else {
			$.messager.show({title: '消息提示', msg: '后台校验中，请稍后...', showType: 'show'});
			self.setInterval(getCheckEngineStatus, 5000);
		}
	});
}

/**
 * 定时获取获取检核引擎的状态
 */
function getCheckEngineStatus() {
	var jso_cardData = JSPFree.getBillCardFormValue(d1_A_BillCard);
	var data_dt_end = jso_cardData.data_dt_end;
	var json = {
		data_dt_end: data_dt_end,
		report_type: reportType
	}
	var result = JSPFree.doClassMethodCall("com.yusys.safe.reportCheck.service.SafeEngineBS",
		"getCheckEngineStatus", json);
	
	if (result.code === 'ok') {
		JSPFree.closeDialogToRoot({code: 'finish', msg: '校验完成！', showType: 'show'});
	}
}

/**
 * 取消
 * @returns
 */
function onCancel(){
	JSPFree.closeDialog(null);
}