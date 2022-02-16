//初始化界面,菜单配置路径是【/frs/yufreejs?js=/yujs/east/58table.js】
var currSQL = "";

function AfterInit(){
	currSQL = jso_OpenPars.currSQL;
	JSPFree.createBillCard("d1","/biapp-crrs/freexml/crrs/generateXml/crrs_report_xml_date.xml",["确定/onConfirm","取消/onCancel"]);
}

function onConfirm(){
	var jso_cardData = JSPFree.getBillCardFormValue(d1_BillCard);
	if(jso_cardData.data_dt==null || jso_cardData.data_dt==""){
		JSPFree.alert("数据日期不能为空!");
		return;
	}
	
	if(jso_cardData.seq==null || jso_cardData.seq==""){
		JSPFree.alert("批次不能为空!");
		return;
	}
	
	var jso_par ={"currSql":currSQL,"data_dt":jso_cardData.data_dt,"seq":jso_cardData.seq};
	JSPFree.openDialogAndCloseMe2("启动任务","/yujs/crrs/report/XmlGenerateReportStart.js",750,350,{ary_allTaskIds:null,data_dt:jso_cardData.data_dt,seq:jso_cardData.seq});
}

function onCancel(){
	JSPFree.closeDialog(null);
}