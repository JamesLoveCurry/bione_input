//初始化界面,菜单配置路径是【/frs/yufreejs?js=/yujs/east/58table.js】
var currSQL = "";

function AfterInit(){
	currSQL = jso_OpenPars.currSQL;
	JSPFree.createBillCard("d1","/biapp-fsrs/freexml/fsrs/supervise_report_download.xml",["下一步/onNext","取消/onCancel"]);
}

function onNext(){
	var jso_cardData = JSPFree.getBillCardFormValue(d1_BillCard);
	if(jso_cardData.data_dt==null || jso_cardData.data_dt==""){
		JSPFree.alert("采集日期不能为空!");
		return;
	}
	JSPFree.openDialog("一键导出","/yujs/fsrs/superviseReportDownLoad1.js",350,350,{org_no:str_LoginUserOrgNo,data_dt:jso_cardData.data_dt},function(_rtdata){
	});
}

function onCancel(){
	JSPFree.closeDialog();
}