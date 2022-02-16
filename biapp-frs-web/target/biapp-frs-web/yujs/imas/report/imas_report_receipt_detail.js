var rid;
function AfterInit(){
	rid = jso_OpenPars.rid;


	JSPFree.createBillCard("d1","/biapp-imas/freexml/report/imas_report_receipt_detail.xml",["返回/onCancel/icon-ok"],null);
	
	//赋值
	JSPFree.queryBillCardData(d1_BillCard, "rid = '"+rid+"'");
}

function AfterBodyLoad(){
	JSPFree.setBillCardItemEditable(d1_BillCard,"*",false);
	var detail = "";
	let jso = JSPFree.doClassMethodCall("com.yusys.imas.report.service.ImasCrReportBS", "getDetail",{rid:rid});
	if(jso!=null){
		detail = jso.result;
	}
	if(!detail){
		JSPFree.setBillCardItemVisible(d1_BillCard, "detail", false);
	}else{
		JSPFree.setBillCardItemWarnMsg(d1_BillCard, "detail", detail);
	}
}

function onCancel(){
	JSPFree.closeDialog();
}