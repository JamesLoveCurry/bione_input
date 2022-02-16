//初始化界面,菜单配置路径是【/frs/yufreejs?js=/yujs/wmp/addTabDetail.js】
var tabname = "";
var tabnameen = "";
var ds = "";
function AfterInit(){
	var str_className = jso_OpenPars.templet;
	
	tabname = jso_OpenPars.tabname;
	tabnameen = jso_OpenPars.tabnameen;
	ds = jso_OpenPars.ds;
	
	JSPFree.createBillCard("d1",str_className,["保存/onSave/icon-p21","取消/onCancel/icon-undo"],null);
}

/**
 * 保存
 * @return {[type]} [description]
 */
function onSave(){
	var str_date = JSPFree.getBillCardItemValue(d1_BillCard,"stat_dt");
	var jso_data = JSPFree.getHashVOs("select status from frs_wmp_lock_data where data_dt='" + str_date + "' and status='锁定'");
	if(jso_data.length>0){  //如果的确有锁定,则提示不允许处理
		JSPFree.alert("日期[" + str_date + "]的数据已经被锁定,不能保存!<br>请在【数据处理->数据锁定】中进行设置");
		return;
	}
	
	JSPFree.setBillCardValues(d1_BillCard,{rid:FreeUtil.getUUIDFromServer()});
	
	var backValue = JSPFree.editTableCheckData(d1_BillCard, "Add", tabname, tabnameen.toUpperCase(),ds,"3");
	if (backValue == "" || "undifind" == backValue) {
		return;
	} else if (backValue == "OK") {
		saveFlag = JSPFree.doBillCardInsert(d1_BillCard,null);
		JSPFree.closeDialog("校验并保存成功");
	} else if (backValue == "Fail") {
		return;
	}
}
/**
 * 取消
 * @return {[type]} [description]
 */
function onCancel(){
	JSPFree.closeDialog();
}