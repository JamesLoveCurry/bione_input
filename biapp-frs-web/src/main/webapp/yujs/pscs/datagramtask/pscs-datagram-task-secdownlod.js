
function AfterInit(){
	JSPFree.createBillCard("d1","/biapp-pscs/freexml/pscs/datagramtask/pscs_datagram_task_datadate.xml",["下一步/onNext","取消/onCancel"]);
}

function onNext(){
	var jso_cardData = JSPFree.getBillCardFormValue(d1_BillCard);
	var str_date = jso_cardData.data_dt;
	if(str_date==null || str_date==""){
		JSPFree.alert("采集日期不能为空!");
		return;
	}

	JSPFree.openDialogAndCloseMe("一键压缩打包下载本机构【" + str_date + "】的所有加密报文","/yujs/pscs/datagramtask/pscs-datagram-task-sec.js",780,300,{data_dt:str_date});
}

function onCancel(){
	JSPFree.closeDialog();
}