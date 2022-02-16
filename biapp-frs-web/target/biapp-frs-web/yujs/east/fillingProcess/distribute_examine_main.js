//初始化界面,菜单配置路径是【/frs/yufreejs?js=/yujs/east/fillingProcess/distribute_examine.js】
function AfterInit(){
	JSPFree.createBillList("d1","/biapp-east/freexml/east/fillingProcess/east_filling_process_child_CODE1.xml",null,
			{isSwitchQuery:"N",querycontion:"status='1'",autocondition:"status='1'",
			list_btns:"[icon-p41]审核/examine;[icon-p80]处理日志/processReasonDetail;",
			orderbys:"data_dt,tab_name_en"});
	
	JSPFree.queryDataByConditon2(d1_BillList, getQueryCondition());
	JSPFree.setBillListForceSQLWhere(d1_BillList,getQueryCondition());

}

//查询列表加载默认查询条件，用于灵活查询
function getQueryCondition(){
	var condition = "";
	
	var jso_rt = JSPFree.doClassMethodCall("com.yusys.east.checkrule.rulesummary.service.ValidateQueryCondition",
			"getQueryConditionByUser",{"_loginUserOrgNo" : str_LoginUserOrgNo});
	
	if(jso_rt.msg == "ok"){
		condition = jso_rt.condition;
	}
	var whereSql = '';
	var jsn_result = JSPFree.doClassMethodCall("com.yusys.east.common.service.EastGetReportBS", "getReportList", null);
	if (jsn_result.code == 'success') {
		whereSql = jsn_result.data;
	}
	condition += " and " + whereSql;
	return condition;
}

// 审核:单条审核
// 判断是否是主任务下所有任务都完成，如果都完成，则更新主任务状态为完成
function examine(){
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
	JSPFree.openDialog("审核","/yujs/east/fillingProcess/distribute_examine_edit.js",1200,800,jso_Pars,function(_rtData){
		if (_rtData == "通过") {
			$.messager.alert('提示', '任务审核通过!', 'info');
		} else if (_rtData == "退回") {
			$.messager.alert('提示', '任务审核退回!', 'info');
		}

		JSPFree.queryDataByConditon(d1_BillList);
	});

}

// 批量审核
function batchExamine(){
	var json_data = d1_BillList.datagrid('getSelections');
	if (json_data == null || json_data == undefined || json_data.length == 0) {
		$.messager.alert('提示', '必须选择一条记录进行操作', 'warning');
		return;
	}
	
	var jso_allrids=[]; 
	for (var i=0;i<json_data.length;i++) {
		jso_allrids.push(json_data[i].rid);
	}

	// 修改数据状态：3：完成
	var jso_Pars = {allrids:jso_allrids,status:'3',type:'3',reason:"通过",userNo:str_LoginUserCode};
	var jsn_result = JSPFree.doClassMethodCall("com.yusys.east.process.service.EastCrProcessBSDMO", "updateDataByTaskByRids3", jso_Pars);
	if (jsn_result.msg == 'OK') {
		$.messager.alert('提示', '批量任务审核成功!', 'info');
		JSPFree.queryDataByConditon(d1_BillList);
	}
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
