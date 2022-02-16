//初始化界面,菜单配置路径是【/frs/yufreejs?js=/yujs/east/58table.js】

function AfterInit(){
	JSPFree.createBillCard("d1","/biapp-east/freexml/east/report/report_list_date.xml",["确定/onConfirm","取消/onCancel"]);
}

function onConfirm(){
	var jso_cardData = JSPFree.getBillCardFormValue(d1_BillCard);
	if(jso_cardData.data_dt==null || jso_cardData.data_dt==""){
		JSPFree.alert("采集日期不能为空!");
		return;
	}
	var jso_statu = JSPFree.doClassMethodCall("com.yusys.east.business.report.service.EastCrReportBSDMO","checkReportListStatu",{Data_dt:jso_cardData.data_dt});
	if(jso_statu.code == -999){
		JSPFree.alert(jso_statu.msg);
		return;
	}
	var jso_par = JSPFree.doClassMethodCall("com.yusys.east.business.report.service.EastCrReportBSDMO","createReportList",{DataDt:jso_cardData.data_dt});
	JSPFree.alert(jso_par.msg);
}

function onCancel(){
	JSPFree.closeDialog(null);
}