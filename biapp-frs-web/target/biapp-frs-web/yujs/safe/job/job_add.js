/**
 *
 * <pre>
 * Title:【任务触发器】
 * Description:任务触发器新增
 * </pre>
 * @author miaokx
 * @version 1.00.00
 * @date 2020/6/15 14:03
 */

var template = ""; // 模板路径
var tabname = "";
var tabnameen = "";
var reporttype = "";

function AfterInit() {
    template = jso_OpenPars.templetcode;
    tabname = jso_OpenPars.tabname;
    tabnameen = jso_OpenPars.tabnameen;
    reporttype = jso_OpenPars.reporttype;

    JSPFree.createBillCard("d1", template, ["保存/onSave/icon-p21", "取消/onCancel/icon-undo"], null);
}
/**
 * 保存
 * @return {[type]} [description]
 */
function onSave() {
	var classname = "";
	var jso_formData = JSPFree.getBillCardFormValue(d1_BillCard);
	var jobType = jso_formData.job_type;
	if (jobType == SafeFreeUtil.getSafeJobType().type1) {
		classname = SafeFreeUtil.getScheduledClassName();
	} else {
		classname = SafeFreeUtil.scheduledDownloadClassName();
	}
	
    JSPFree.setBillCardValues(d1_BillCard,{rid:FreeUtil.getUUIDFromServer(), 
    	report_type:reporttype, classname:classname});
    var flag = JSPFree.doBillCardInsert(d1_BillCard, null);
    if (flag) {
        JSPFree.closeDialog("OK");
    }
}

/**
 * 取消
 * @return {[type]} [description]
 */
function onCancel() {
    JSPFree.closeDialog();
}
