/**
 *
 * <pre>
 * Title:【任务触发器】
 * Description:任务触发器修改
 * </pre>
 * @author wangxy31
 * @version 1.00.00
 * @date 2021/9/15 16:08
 */

var template = ""; // 模板路径
var tabname = "";
var tabnameen = "";
var reporttype = "";
var rid = "";
var defaultValue = "";

function AfterInit() {
    template = jso_OpenPars.templetcode;
    tabname = jso_OpenPars.tabname;
    tabnameen = jso_OpenPars.tabnameen;
    reporttype = jso_OpenPars.reporttype;
    rid = jso_OpenPars.rid;
//	defaultValue = jso_OpenPars.defaultValue;

    JSPFree.createBillCard("d1", template, ["保存/onSave/icon-p21", "取消/onCancel/icon-undo"], null);

    // 赋值
	JSPFree.queryBillCardData(d1_BillCard, "rid = '"+rid+"'");
//	JSPFree.setBillCardValues(d1_BillCard, defaultValue);
	
	// 执行卡片默认值公式2，即修改时的defaultformula2
	JSPFree.execBillCardDefaultValueFormula(d1_BillCard, true); //参数带true，会去加载xml当中的defaultformula2，属于不同分支，所以不会把之前的UUID4冲掉

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
    var flag = JSPFree.doBillCardUpdate(d1_BillCard, null);
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
