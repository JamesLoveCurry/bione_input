var currSQL = "";

function AfterInit(){
	currSQL = jso_OpenPars2.currSQL;
	JSPFree.createBillCard("d1","/biapp-pscs/freexml/pscs/datagramtask/pscs_datagram_task_datadate.xml",["确定/onConfirm","取消/onCancel"]);
}

function onConfirm(){
	var jso_cardData = JSPFree.getBillCardFormValue(d1_BillCard);
	if(jso_cardData.data_dt==null || jso_cardData.data_dt==""){
		JSPFree.alert("采集日期不能为空!");
		return;
	}
	var jso_par ={"currSql":currSQL,"data_dt":jso_cardData.data_dt};
	JSPFree.openDialogAndCloseMe2("一键启动所有任务","/yujs/pscs/datagramtask/pscs-datagram-task-start.js",750,350,{allTaskIds:null,data_dt:jso_cardData.data_dt});
}

function onCancel(){
	JSPFree.closeDialog(null);
}