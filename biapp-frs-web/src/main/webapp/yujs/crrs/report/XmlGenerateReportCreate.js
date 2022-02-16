//初始化界面,菜单配置路径是【/frs/yufreejs?js=/yujs/east/east_cr_report_config.js】
function AfterInit() {
	JSPFree.createSplitByBtn("d1", "上下", 280, [ "确定/onConfirm", "取消/onCancel" ], false);
	JSPFree.createBillList("d1_A", "/biapp-crrs/freexml/crrs/generateXml/crrs_report_xml_config_CODE1.xml",null,{list_btns:"",ishavebillquery:"N",list_ischeckstyle:"Y",list_ismultisel:"Y"});
	JSPFree.createBillCard("d1_B", "/biapp-crrs/freexml/crrs/generateXml/crrs_report_xml_date.xml");
}

function onConfirm() {
	var jsy_datas = JSPFree.getBillListSelectDatas(d1_A_BillList);
	var form_vlue = JSPFree.getBillCardFormValue(d1_B_BillCard);
	if (jsy_datas.length <= 0) {
		$.messager.alert('提示', '必须选择一条数据!', 'info');
		return;
	}

	var str_date = form_vlue.data_dt;
	if (str_date == null || str_date == "") {
		$.messager.alert('提示', '必须选择日期!', 'info');
		return;
	}

	var seq = form_vlue.seq;
	if (seq == null || seq == "") {
		$.messager.alert('提示', '必须选择批次!', 'info');
		return;
	}

	var jso_par = {
		chooseTasks : jsy_datas,
		data_dt : str_date,
		seq : seq
	};

	var jso_rt = JSPFree.doClassMethodCall("com.yusys.crrs.report.service.CrrsReportBSDMO", "createReportTask",jso_par);

	JSPFree.closeDialog(jso_rt);
}

function onCancel() {
	JSPFree.closeDialog(null);
}