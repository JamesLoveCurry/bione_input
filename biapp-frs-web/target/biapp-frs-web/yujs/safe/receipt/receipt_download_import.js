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

	JSPFree.createSplitByBtn("d1", "上下", 120, [ "下载并导入/downLoadAndImport", "取消/onCancel" ], false);
	JSPFree.createBillCard("d1_A", className, null, {onlyItems:"data_dt"});
}

/**
 * 页面初始化后
 * @returns
 */
function AfterBodyLoad() {
	var dom_div = document.getElementById("d1_A_BillCardDiv");
	dom_div.style.overflow="hidden"; // 隐藏滚动框
}
function downLoadAndImport() {
	var form_vlue = JSPFree.getBillCardFormValue(d1_A_BillCard);
	var str_datestr_date = form_vlue.data_dt;
	if (str_datestr_date == null || str_datestr_date == "") {
		$.messager.alert('提示', '必须选择日期!', 'info');
		return;
	}
	var _rt = JSPFree.doClassMethodCall("com.yusys.safe.scheduled.service.ScheduledSafeDayTaskBS", "handLoadReceiptTask", {
		dataDt: str_datestr_date,
		reportType: reportType
	});
	JSPFree.closeDialog(_rt);
}
/**
 * 取消
 * @returns
 */
function onCancel() {
	JSPFree.closeDialog();
}