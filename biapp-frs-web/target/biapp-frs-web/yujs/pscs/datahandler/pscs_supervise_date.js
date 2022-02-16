function AfterInit(){
	JSPFree.createBillCard("d1","/biapp-pscs/freexml/pscs/datahandler/pscs_supervise_date.xml",["确定/onConfirm","取消/onCancel"]);
	$("#d1_form").append("<div style=\"text-indent:2em;width:100%;margin-top:-130px;\">执行数据校验，会清空校验结果数据，如果需要历史数据，请先导出校验结果。</div>");
}

function onConfirm(){
	var jso_cardData = JSPFree.getBillCardFormValue(d1_BillCard);
	if(jso_cardData.data_dt==null || jso_cardData.data_dt==""){
		JSPFree.alert("校验日期不能为空!");
		return;
	}
	
	var jso_rt = JSPFree.doClassMethodCall("com.yusys.pscs.datahandler.service.PscsRuleHandler","startAllTask",{data_dt:jso_cardData.data_dt});
	if(jso_rt.errorCode == "0002" || jso_rt.errorCode == "0001"){
		return JSPFree.alert(jso_rt.message);
	}else{
		JSPFree.closeDialog(jso_rt);
	}
}

function onCancel(){
	JSPFree.closeDialog(null);
}