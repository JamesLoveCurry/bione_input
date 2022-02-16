//初始化界面,菜单配置路径是【/frs/yufreejs?js=/yujs/east/east_cr_report_config.js】
function AfterInit() {
	JSPFree.createSplitByBtn("d1", "上下", 370,
			[ "确定/onConfirm", "取消/onCancel" ], false);
	JSPFree.createBillList("d1_A", "/biapp-east/freexml/east/report/east_cr_report_config_ref.xml",null,{isSwitchQuery:"N",autoquery:"N"});
	JSPFree.createBillCard("d1_B", "/biapp-east/freexml/east/report/east_cr_report_config_data_ref.xml");
	
	JSPFree.queryDataByConditon2(d1_A_BillList, getCondition());
}

function AfterBodyLoad(){
	var dom_div = document.getElementById("d1_B_BillCardDiv");
	dom_div.style.overflow="hidden";  //隐藏滚动框
}

function getCondition() {
	var condition = "";
	var jso_rt = JSPFree.doClassMethodCall("com.yusys.east.checkrule.rulesummary.service.ValidateQueryCondition","getQueryCondition",{"_loginUserOrgNo" : str_LoginUserOrgNo});
	if(jso_rt.msg == "ok"){
		condition = jso_rt.condition;
	}
	
	return condition;
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

	var jso_par = {
		chooseTasks : jsy_datas,
		data_dt : str_date
	};

	var jso_rt = JSPFree.doClassMethodCall(
			"com.yusys.east.business.report.service.EastCrReportBSDMO", "createReportTask",
			jso_par);

	JSPFree.closeDialog(jso_rt);
}

function onCancel() {
	JSPFree.closeDialog(null);
}