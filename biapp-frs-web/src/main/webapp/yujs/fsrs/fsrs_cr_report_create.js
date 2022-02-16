//初始化界面,菜单配置路径是【/frs/yufreejs?js=/yujs/east/east_cr_report_config.js】
function AfterInit() {
	JSPFree.createSplitByBtn("d1", "上下", 320,
			[ "确定/onConfirm", "取消/onCancel" ], false);
	JSPFree.createBillList("d1_A", "/biapp-fsrs/freexml/fsrs/fsrs_cr_report_config_ref.xml",null,{isSwitchQuery:"N"});
	JSPFree.createBillCard("d1_B", "/biapp-fsrs/freexml/fsrs/fsrs_cr_report_config_data_ref.xml");
}

function onConfirm() {
	var jsy_datas = JSPFree.getBillListSelectDatas(d1_A_BillList);
	var form_vlue = JSPFree.getBillCardFormValue(d1_B_BillCard);
	if (jsy_datas.length <= 0) {
		$.messager.alert('提示', '至少选择上面的一条策略!', 'info');
		return;
	}

	var str_date = form_vlue.data_dt;
	if (str_date == null || str_date == "") {
		$.messager.alert('提示', '必须选择日期!', 'info');
		return;
	}

	var jso_par = {
		chooseTasks : jsy_datas,
		data_dt : str_date
	};

	var jso_rt = JSPFree.doClassMethodCall(
			"com.yusys.fsrs.report.service.FsrsCrReportBSDMO", "createReportTask", jso_par);

	JSPFree.closeDialog(jso_rt);
}

function onCancel() {
	JSPFree.closeDialog(null);
}