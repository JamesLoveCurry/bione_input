//报表填报 初始化界面,菜单配置路径是【/frs/yufreejs?js=/yujs/wmp/distribute_fillin_main.js】
function AfterInit(){
	JSPFree.createBillList("d1","/biapp-wmp/freexml/wmp/wmp_filling_process_child_CODE1.xml",null,{isSwitchQuery:"N",orderbys:"data_dt,tab_name_en"});
	
	JSPFree.queryDataByConditon2(d1_BillList, getQueryCondition());
	JSPFree.setBillListForceSQLWhere(d1_BillList,getQueryCondition());
}

//查询列表加载默认查询条件，用于灵活查询
function getQueryCondition(){
	var condition = "";
	
	var jso_rt = JSPFree.doClassMethodCall("com.yusys.wmp.checkrule.rulesummary.service.ValidateQueryCondition",
			"getQueryCondition",{"_loginUserOrgNo" : str_LoginUserOrgNo});
	
	if(jso_rt.msg == "ok"){
		condition = jso_rt.condition;
	}
	
	return condition;
}

//填报
function created(){
	var json_data = d1_BillList.datagrid('getSelections');
	if (json_data == null || json_data == undefined || json_data.length == 0) {
		$.messager.alert('提示', '必须选择一条记录进行操作', 'warning');
		return;
	}
	if (json_data.length>1) {
		$.messager.alert('提示', '只能选择一条记录进行操作', 'warning');
		return;
	}

	var jso_Pars = {taskId:json_data[0].rid,tabNameEn:json_data[0].tab_name_en,tabName:json_data[0].tab_name,dataDt:json_data[0].data_dt};
	
	JSPFree.openDialog("填报","/yujs/wmp/distribute_detail_fillin1.js",1000,600,jso_Pars,function(_rtData){
		if (_rtData != null) {
			if ("dirclose" == _rtData.type) {
				return;
			}
		}
		
		if (_rtData == "提交") {
			$.messager.alert('提示', '任务提交成功!', 'info');
		} else if (_rtData == "强制提交") {
			$.messager.alert('提示', '任务强制提交成功!', 'info');
		}
		
		JSPFree.queryDataByConditon(d1_BillList);
	});
}

// 查看处理日志
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
	JSPFree.openDialog("处理日志信息","/yujs/wmp/distribute_process_detail.js",900,600,jso_par,function(_rtData){
		if (_rtData != null) {
			if ("dirclose" == _rtData.type) {
				return;
			}
		}
	});
}
