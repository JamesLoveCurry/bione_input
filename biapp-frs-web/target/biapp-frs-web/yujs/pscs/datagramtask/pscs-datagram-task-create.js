function AfterInit() {
	JSPFree.createSplitByBtn("d1", "上下", 370,
			[ "确定/onConfirm", "取消/onCancel" ], false);
	JSPFree.createBillList("d1_A", "/biapp-pscs/freexml/pscs/datagramconfig/pscs_datagram_config.xml",null,{isSwitchQuery:"N"});
	JSPFree.createBillCard("d1_B", "/biapp-pscs/freexml/pscs/datagramtask/pscs_datagram_task_datadate.xml");
}

function AfterBodyLoad(){
	var dom_div = document.getElementById("d1_B_BillCardDiv");
	dom_div.style.overflow="hidden";  //隐藏滚动框
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
	}else{
		var reportType = jsy_datas[0].report_type;
		if(reportType == "月报"){
			if(str_date != getLastMonthAndDay(str_date)){
				$.messager.alert('提示', '请选择月末日期!', 'info');
				return;
			}
		}else if(reportType == "季报"){
			if(str_date != (str_date.substring(0,4)+"0331") && str_date != (str_date.substring(0,4)+"0630")
				&& str_date != (str_date.substring(0,4)+"0930") && str_date != (str_date.substring(0,4)+"1231")){
				$.messager.alert('提示', '请选择季末日期!', 'info');
				return;
			}
		}else if(reportType == "半年报"){
			if(str_date != (str_date.substring(0,4)+"0630") && str_date != (str_date.substring(0,4)+"1231")){
				$.messager.alert('提示', '请选择半年年末日期!', 'info');
				return;
			}
		}else if(reportType == "年报"){
			if(str_date != (str_date.substring(0,4)+"1231")){
				$.messager.alert('提示', '请选择年末日期!', 'info');
				return;
			}
		}
	}

	var jso_par = {
		chooseTasks : jsy_datas,
		data_dt : str_date
	};

	var jso_rt = JSPFree.doClassMethodCall(
			"com.yusys.pscs.datagramtask.service.PscsDatagramTaskBS", "createDatagramTask",
			jso_par);

	JSPFree.closeDialog(jso_rt);
}

/**
 * 获取月底日期
 */
function getLastMonthAndDay(data_dt_1){
    //录入日期
    var year = data_dt_1.substring(0,4);
    var month = data_dt_1.substring(4,6);
    
    var lastDay = new Date(year,month,0);

	return year + month + lastDay.getDate();
}

function onCancel() {
	JSPFree.closeDialog(null);
}