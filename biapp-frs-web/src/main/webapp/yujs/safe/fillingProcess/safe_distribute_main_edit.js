/**
 * 
 * <pre>
 * Title: 【报表处理】-【报表下发】
 * Description: 报表下发：编辑页面
 * </pre>
 * @author wangxy31 
 * @version 1.00.00
   @date 2020年6月18日
 */

var status = "";
var className = null;
var subfix = null;
var rid = null;

function AfterInit() {
	status = jso_OpenPars.status;
	className = jso_OpenPars.className;
	subfix = jso_OpenPars.subfix;
	rid = jso_OpenPars.rid;
	
	// 根据状态判断，如果是1,3状态，则详细页面置灰，不可编辑分发说明
	if (status == SafeFreeUtil.getProcessStatus().PROCESSING || status == SafeFreeUtil.getProcessStatus().COMPLETE) {
		JSPFree.createBillCard("d1", className, ["取消/onCancel/icon-undo"], null);
	} else {
		JSPFree.createBillCard("d1", className, ["保存/onSave/icon-p21"], null);
	}
	//赋值
	JSPFree.queryBillCardData(d1_BillCard, "rid = '"+rid+"'");
}

/**
 * 页面初始化后
 * @returns
 */
function AfterBodyLoad() {
	// 根据状态判断，如果是1,3状态，则详细页面置灰，不可编辑分发说明
	if (status == SafeFreeUtil.getProcessStatus().PROCESSING || status == SafeFreeUtil.getProcessStatus().COMPLETE) {
		JSPFree.setBillCardItemEditable(d1_BillCard, "*", false);
	}
}

/**
 * 保存
 * @return {[type]} [description]
 */
function onSave(){
	var flag = JSPFree.doBillCardUpdate(d1_BillCard,null);
	if(flag){
		JSPFree.alert("保存成功");
	}
	JSPFree.closeDialog(flag);
}

/**
 * 取消
 * @return {[type]} [description]
 */
function onCancel(){
	JSPFree.closeDialog();
}
