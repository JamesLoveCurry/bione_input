/**
 * 
 * <pre>
 * Title: 【报送管理】-【上传报文】
 * Description: 报文生成：上传报文
 * </pre>
 * @author wangxy31
 * @version 1.00.00
   @date 2020年6月10日
 */
var reportType = null;

function AfterInit() {
	var className = jso_OpenPars.className;
	reportType = jso_OpenPars.reportType;

	JSPFree.createSplitByBtn("d1", "上下", 120, [ "上传报文/sendMessage", "取消/onCancel" ], false);
	JSPFree.createBillCard("d1_A", className, null, {onlyItems:"org_no;data_dt"});
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
}

function sendMessage(){
	var form_vlue = JSPFree.getBillCardFormValue(d1_A_BillCard);
	var str_datestr_date = form_vlue.data_dt;
	if (str_datestr_date == null || str_datestr_date == "") {
		$.messager.alert('提示', '必须选择日期!', 'info');
		return;
	}


	var par={dataDt:str_datestr_date, reportType:reportType, str_LoginUserOrgNo:str_LoginUserOrgNo};
	var _rt=JSPFree.doClassMethodCall("com.yusys.safe.scheduled.service.ScheduledSafeDayTaskBS","handSendTask",par);
	JSPFree.closeDialog(_rt);
}
/**
 * 取消
 * @returns
 */
function onCancel() {
	JSPFree.closeDialog();
}