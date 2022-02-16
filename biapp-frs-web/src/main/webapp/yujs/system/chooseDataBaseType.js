//初始化界面,菜单配置路径是【/frs/yufreejs?js=/yujs/east/58table.js】
var jso_cardData = null;
function AfterInit(){
	JSPFree.createBillCard("d1","/biapp-east/freexml/east/busiModel/choose_database_type.xml",["确定/onConfirm","取消/onCancel"]);
}

//确认
function onConfirm(){
	jso_cardData = JSPFree.getBillCardFormValue(d1_BillCard);
	if(jso_cardData.database_type==null || jso_cardData.database_type==""){
		JSPFree.alert("数据库类型不能为空!");
		return;
	}
	var returnObj = new Object();
	returnObj.database_type = jso_cardData.database_type;
	JSPFree.closeDialog(returnObj);
	
	$("#d1_BillCardBtn1").attr("disabled",true);
}

function onCancel(){
	JSPFree.closeDialog(null);
}