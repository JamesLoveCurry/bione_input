//初始化界面,菜单配置路径是【/frs/yufreejs?js=/yujs/east/58table.js】
function AfterInit(){
	JSPFree.createBillCard("d1","/biapp-crrs/freexml/crrs/generateXml/crrs_report_xml_date2.xml",["确定/onConfirm","取消/onCancel"]);
}

// 创建任务
function onConfirm(){
	var jso_rt = null;
	
	var jso_cardData = JSPFree.getBillCardFormValue(d1_BillCard);
	if(jso_cardData.data_dt==null || jso_cardData.data_dt==""){
		JSPFree.alert("数据日期不能为空!");
		return;
	}

	jso_rt = JSPFree.doClassMethodCall("com.yusys.crrs.lock.service.CrrsLockBSDMO", "createLockData", {data_dt:jso_cardData.data_dt,operator:str_LoginUserCode,operator_name:str_LoginUserName});

	JSPFree.closeDialog(jso_rt);
}

function onCancel(){
	JSPFree.closeDialog(null);
}