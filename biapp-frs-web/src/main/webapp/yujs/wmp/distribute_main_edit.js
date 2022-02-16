//初始化界面,菜单配置路径是【/frs/yufreejs?js=/yujs/wmp/distribute_main.js】
var status = "";
function AfterInit(){
	status = jso_OpenPars.data.status;
	if (status == '1' || status == '3') {
		JSPFree.createBillCard("d1","/biapp-wmp/freexml/wmp/wmp_filling_process_CODE1_1.xml",["取消/onCancel/icon-undo"],null);
	} else {
		JSPFree.createBillCard("d1","/biapp-wmp/freexml/wmp/wmp_filling_process_CODE1.xml",["保存/onSave/icon-p21"],null);
	}
	//赋值
	JSPFree.queryBillCardData(d1_BillCard, "rid = '"+jso_OpenPars.data.rid+"'");
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
