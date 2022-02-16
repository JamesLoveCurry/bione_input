//初始化界面,菜单配置路径是【/frs/yufreejs?js=/yujs/east/58table.js】
function AfterInit(){
	JSPFree.createBillCard("d1","/biapp-crrs/freexml/crrs/generateXml/crrs_report_xml_date.xml",["下一步/onNext","取消/onCancel"]);
}

function onNext(){
	var jso_cardData = JSPFree.getBillCardFormValue(d1_BillCard);
	if(jso_cardData.data_dt==null || jso_cardData.data_dt==""){
		JSPFree.alert("数据日期不能为空!");
		return;
	}
	
	if(jso_cardData.seq==null || jso_cardData.seq==""){
		JSPFree.alert("批次不能为空!");
		return;
	}
	
	JSPFree.openDialogAndCloseMe("一键压缩打包下载【" + jso_cardData.data_dt + "】的所有报文",
			"/yujs/crrs/report/XmlGenerateReportZip.js",780,300,{data_dt:jso_cardData.data_dt,seq:jso_cardData.seq});
}

function onCancel(){
	JSPFree.closeDialog();
}