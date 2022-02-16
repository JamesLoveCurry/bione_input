//初始化界面,菜单配置路径是【/frs/yufreejs?js=/yujs/east/58table.js】
var currSQL = "";

function AfterInit(){
	currSQL = jso_OpenPars.currSQL;
	JSPFree.createBillCard("d1","/biapp-east/freexml/east/report/supervise_report_date.xml",["确定/onConfirm","取消/onCancel"]);
}

function onConfirm(){
	var jso_cardData = JSPFree.getBillCardFormValue(d1_BillCard);
	if(jso_cardData.data_dt==null || jso_cardData.data_dt==""){
		JSPFree.alert("采集日期不能为空!");
		return;
	}
	var jso_par ={"currSql":currSQL,"data_dt":jso_cardData.data_dt};
	JSPFree.openDialogAndCloseMe2("一键校验所有任务","/yujs/east/report/superviseYJH_start.js",750,350,{allTaskIds:null,data_dt:jso_cardData.data_dt});
}

function onCancel(){
	JSPFree.closeDialog(null);
}