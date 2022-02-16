//初始化界面,菜单配置路径是【/frs/yufreejs?js=/yujs/east/fillingProcess/distribute_main.js】
var rid = "";
var type = ""; // 新增Add、修改Edit
var template = ""; // 模板路径
var tabname = "";
var tabnameen = "";
var defaultValue = "";
function AfterInit(){
	rid = jso_OpenPars.rid;
	type = jso_OpenPars.type;
	template = jso_OpenPars.template;
	tabname = jso_OpenPars.tabname;
	tabnameen = jso_OpenPars.tabnameen;
	defaultValue = jso_OpenPars.defaultValue;
	
	JSPFree.createBillCard("d1",template,["保存/onSave/icon-p21","取消/onCancel/icon-undo"],null);
	
	if (type == "Add") {
		JSPFree.setBillCardValues(d1_BillCard,{rid:FreeUtil.getUUIDFromServer()});
	} else if (type == "Edit") {
		//赋值
		JSPFree.queryBillCardData(d1_BillCard, "rid = '"+rid+"'");
	}
	
	JSPFree.setBillCardValues(d1_BillCard, defaultValue);
}

/**
 * 保存
 * @return {[type]} [description]
 */
var saveFlag;
function onSave(){
	if(type == "Add" && !saveFlag){
		saveFlag = JSPFree.doBillCardInsert(d1_BillCard,null);
		if (saveFlag) {
			JSPFree.closeDialog("校验并保存成功");
		}
	} else {
		saveFlag = JSPFree.doBillCardUpdate(d1_BillCard,null);
		JSPFree.closeDialog("校验并保存成功");
		if (saveFlag) {
			JSPFree.closeDialog("校验并保存成功");
		}
	}
	
	/*if(type == "Add" && !saveFlag){
		var backValue = JSPFree.editTableCheckData(d1_BillCard, type, tabname, tabnameen.toUpperCase(),"","4");
		if (backValue == "" || "undifind" == backValue) {
			return;
		} else if (backValue == "OK") {
			saveFlag = JSPFree.doBillCardInsert(d1_BillCard,null);
			JSPFree.closeDialog("校验并保存成功");
		} else if (backValue == "Fail") {
			return;
		}
	} else {
		var backValue = JSPFree.editTableCheckData(d1_BillCard, type, tabname, tabnameen.toUpperCase(),"","4");
		if (backValue == "" || "undifind" == backValue) {
			return;
		} else if (backValue == "OK") {
			saveFlag = JSPFree.doBillCardUpdate(d1_BillCard,null);
			JSPFree.closeDialog("校验并保存成功");
		} else if (backValue == "Fail") {
			return;
		}
	}*/
}

/**
 * 取消
 * @return {[type]} [description]
 */
function onCancel(){
	JSPFree.closeDialog();
}
