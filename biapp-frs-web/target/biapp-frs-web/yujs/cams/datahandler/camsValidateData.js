//数据校验
function AfterInit(){
	var jsy_btns = ["校验/onValidate/icon-p52","校验日志/onTaskLog/icon-p48","导出文件/onExportAll"];

	JSPFree.createBillList("d1","/biapp-cams/freexml/cams/cams_result_data_CODE1.xml",jsy_btns,{isSwitchQuery:"N"});
}

function onValidate(){
	if (true) {
		$.messager.alert('提示', '当前尚未实现该功能！', 'warning');
		return;
	}
	// 校验之前，先做判断
	var task_id = "88888888-8888-8888-8888-888888888888";
	var jsy_data = JSPFree.getHashVOs("select status from cams_engine_task where task_id ='" + task_id + "'");

	if (jsy_data !=null && jsy_data.length > 0) {
		var status = jsy_data[0].status;
		if ("进行中" == status) {
			$.messager.alert('提示', '当前任务正在进行校验，请勿重复操作！', 'warning');
			return;
		}
	} else {
		$.messager.alert('提示', '当前任务不存在！', 'warning');
		return;
	}
	JSPFree.openDialog("选择日期","/yujs/cams/datahandler/cams_supervise_date.js",350,350,null,function(_rtdata){
		if (_rtdata != null && _rtdata.code == 1) {
			$.messager.show({title:'消息提示',msg: '后台校验中，请稍后...',showType:'show'});
			JSPFree.queryDataByConditon(d1_BillList,null);  //立即查询刷新数据
		}
	});
}

function onExportAll(condition){
	var d1_sql_where = d1_BillList.CurrSQL.split("where")[1];
	
	var d1_sql = "select ruletype, tablename, colname, colvalue, problemcode, problemmsg, org_no, org_name, result_status from cams_result_data where " + d1_sql_where;

   JSPFree.downloadExcelBySQL("数据校验.xls", d1_sql, "数据校验","校验类型,表名,字段名,字段值,问题编号,问题提示,经办机构号,经办机构名称,错误处理状态");
}

function onTaskLog(){
	JSPFree.openDialog("日志", "/yujs/cams/datahandler/cams_viewtask_log.js", 900, 600, null, function(_rtdata) {
	});
}