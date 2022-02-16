function AfterInit(){
	JSPFree.createBillList("d1","/biapp-imas/freexml/imas/fillingProcess/imas_filling_process_child_CODE1.xml",null,
			{isSwitchQuery:"N",list_ispagebar:"Y",list_ischeckstyle:"N",list_ismultisel:"N",
			querycontion:"status in ('1','3')",autocondition:"status in ('1','3')",
			list_btns:"[icon-p41]查看/query;[icon-p80]处理日志/processReasonDetail;",
			orderbys:"data_dt,tab_name_en"});
	
	JSPFree.queryDataByConditon2(d1_BillList, getQueryCondition());
	JSPFree.setBillListForceSQLWhere(d1_BillList,getQueryCondition());
}

//查询列表加载默认查询条件，用于灵活查询
function getQueryCondition(){
	var condition = "";
	
	var jso_rt = JSPFree.doClassMethodCall("com.yusys.east.checkrule.rulesummary.service.ValidateQueryCondition",
			"getQueryCondition",{"_loginUserOrgNo" : str_LoginUserOrgNo});
	
	if(jso_rt.msg == "ok"){
		condition = jso_rt.condition;
	}
	
	return condition;
}

// 查看
function query(){
	var json_data = d1_BillList.datagrid('getSelections');
	if (json_data == null || json_data == undefined || json_data.length == 0) {
		$.messager.alert('提示', '必须选择一条记录进行操作', 'warning');
		return;
	}
	if (json_data.length>1) {
		$.messager.alert('提示', '只能选择一条记录进行操作', 'warning');
		return;
	}

	var jso_Pars = {data:json_data[0]};
	
	JSPFree.openDialog("查看","/yujs/east/fillingProcess/distribute_query_edit.js",1000,600,jso_Pars,function(_rtData){
		JSPFree.queryDataByConditon(d1_BillList);
		
		if (_rtData != null) {
			if ("dirclose" == _rtData.type) {
				return;
			}
		}
	});
}

//查看处理日志
function processReasonDetail(){
	var json_data = d1_BillList.datagrid('getSelections');
	if (json_data == null || json_data == undefined || json_data.length == 0) {
		$.messager.alert('提示', '必须选择一条记录进行操作', 'warning');
		return;
	}
	if (json_data.length>1) {
		$.messager.alert('提示', '只能选择一条记录进行操作', 'warning');
		return;
	}

	var jso_par = {child_task_id:json_data[0].rid};
	JSPFree.openDialog("处理日志信息","/yujs/east/fillingProcess/distribute_process_detail.js",900,600,jso_par,function(_rtData){
		if (_rtData != null) {
			if ("dirclose" == _rtData.type) {
				return;
			}
		}
	});
}
