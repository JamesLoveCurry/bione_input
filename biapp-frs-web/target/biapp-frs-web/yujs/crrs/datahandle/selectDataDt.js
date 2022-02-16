//初始化界面,菜单配置路径是【/frs/yufreejs?js=/yujs/east/58table.js】
function AfterInit(){
	JSPFree.createBillCard("d1","/biapp-crrs/freexml/crrs/rule/supervise_date.xml",["确定/onConfirm","取消/onCancel"]);
}

function onConfirm(){
	var jso_cardData = JSPFree.getBillCardFormValue(d1_BillCard);
	if(jso_cardData.data_dt==null || jso_cardData.data_dt==""){
		JSPFree.alert("采集日期不能为空!");
		return;
	}
	var jso_rt = JSPFree.doClassMethodCall("com.yusys.crrs.rulevalidate.service.CrrsRuleHandler","startAllTask",{data_dt:jso_cardData.data_dt});
	JSPFree.closeDialog(jso_rt);
}

function onCancel(){
	JSPFree.closeDialog(null);
}