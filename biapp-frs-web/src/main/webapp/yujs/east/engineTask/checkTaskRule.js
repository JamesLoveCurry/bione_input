//初始化界面,菜单配置路径是【/frs/yufreejs?js=/yujs/east/58table.js】
function AfterInit(){
	JSPFree.createBillList("d1","east_cr_task_CODE2");
	var str_sqlWhere = "tab_name is null";  //拼SQL条件
	JSPFree.queryDataByConditon(d1_BillList,str_sqlWhere);  //锁定规则表查询数据
}

function start(){
	var selectData = d1_BillList.datagrid('getSelected');
	if (selectData == null) {
		$.messager.alert('提示', '必须选择一条记录进行操作', 'warning');
		return;
	}
	if(selectData.status == "ok"){
		$.messager.alert('提示', '当前任务已经启动！', 'warning');
		return;
	}
	var jso_par = { task_id:selectData.task_id };
	var jso_data = JSPFree.doClassMethodCall("com.yusys.east.checkrule.detailmaintenance.service.EastCrRuleTaskBS","startTask",jso_par); 
}

function log(){
	var selectData = d1_BillList.datagrid('getSelected');
	if (selectData == null) {
		$.messager.alert('提示', '必须选择一条记录进行操作', 'warning');
		return;
	}
	var jso_OpenPars = {};
	jso_OpenPars.task_id = selectData.task_id;
	JSPFree.openDialog("日志","/yujs/east/viewtasklog.js", 900, 600, jso_OpenPars,function(_rtdata){
		
	});
}