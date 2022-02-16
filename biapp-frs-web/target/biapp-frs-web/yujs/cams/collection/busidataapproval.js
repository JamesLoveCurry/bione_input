//业务数据查询【/frs/yufreejs?js=/yujs/cams/busidataquery.js】
function AfterInit(){
	JSPFree.createTabb("d1", [ "个人账户", "机构账户" ]);
	JSPFree.createBillList("d1_1","/biapp-cams/freexml/cams/cams_indv_acct_CODE1.xml",null,{isSwitchQuery:"N",autoquery:"Y",list_ispagebar:"Y",autocondition:"status='3'",querycontion:"status='3'",list_btns:"[icon-ok]审批/approval1;[icon-p80]批量审批/batchapproval1;[icon-p27]查看日志/proclogs1"});
	JSPFree.createBillList("d1_2","/biapp-cams/freexml/cams/cams_corp_acct_CODE1.xml",null,{isSwitchQuery:"N",autoquery:"Y",list_ispagebar:"Y",autocondition:"status='3'",querycontion:"status='3'",list_btns:"[icon-ok]审批/approval2;[icon-p80]批量审批/batchapproval2;[icon-p27]查看日志/proclogs2"});

	JSPFree.queryDataByConditon2(d1_1_BillList, getQueryCondition());
	JSPFree.setBillListForceSQLWhere(d1_1_BillList,getQueryCondition());

	JSPFree.queryDataByConditon2(d1_2_BillList, getQueryCondition());
	JSPFree.setBillListForceSQLWhere(d1_2_BillList,getQueryCondition());
}

//查询列表加载默认查询条件，用于灵活查询
function getQueryCondition(){
	var jso_rt = JSPFree.doClassMethodCall("com.yusys.cams.validate.service.ValidateQueryCondition","getQueryCondition",{"_loginUserOrgNo" : str_LoginUserOrgNo});
	var condition = "";
	if(jso_rt.msg == "ok"){
		condition = jso_rt.condition;
	}

	return condition;
}

/**
 * 个人账户：审批
 */
function approval1(){
	var json_data = d1_1_BillList.datagrid('getSelections');
	if (json_data == null || json_data == undefined || json_data.length ==0) {
		$.messager.alert('提示', '必须选择一条记录进行操作', 'warning');
		return;
	}
	if (json_data.length>1) {
		$.messager.alert('提示', '只能选择一条记录进行操作', 'warning');
		return;
	}

	JSPFree.openDialog2("账户信息","/yujs/cams/collection/indv/approvalIndv.js", 950,600, {indv:json_data[0]},function(_rtdata){
		JSPFree.queryDataByConditon(d1_1_BillList,null);  //立即查询刷新数据
	},true);
}

/**
 * 个人账户：批量审批
 */
function batchapproval1(){
	var json_data = d1_1_BillList.datagrid('getSelections');
	if (json_data == null || json_data == undefined || json_data.length ==0) {
		$.messager.alert('提示', '必须选择一条记录进行操作', 'warning');
		return;
	}

	var rids = [];
	for (var i=0;i<json_data.length;i++) {
		rids.push(json_data[i].rid);
	}

	var jso_par = {rids:rids,user_no:str_LoginUserCode,data_type:"indv"};
	var jsn_result = JSPFree.doClassMethodCall("com.yusys.cams.indv.service.CamsIndvBSDMO","batchDataApproval",jso_par);

	if (jsn_result.msg == 'OK') {
		$.messager.alert('提示', '数据批量审批成功！', 'info');
		JSPFree.queryDataByConditon(d1_1_BillList,null);  //立即查询刷新数据
	}
}

/**
 * 个人账户：查看日志
 */
function proclogs1(){
	var json_data = d1_1_BillList.datagrid('getSelections');
	if (json_data == null || json_data == undefined || json_data.length == 0) {
		$.messager.alert('提示', '必须选择一条记录进行操作', 'warning');
		return;
	}
	if (json_data.length>1) {
		$.messager.alert('提示', '只能选择一条记录进行操作', 'warning');
		return;
	}

	var jso_par = {data_id:json_data[0].rid,data_type:"indv"};
	JSPFree.openDialog("处理日志信息","/yujs/cams/collection/processlogs.js",900,600,jso_par,function(_rtData){
		if (_rtData != null) {
			if ("dirclose" == _rtData.type) {
				return;
			}
		}
	});
}

/**
 * 机构账户：审批
 */
function approval2(){
	var json_data = d1_2_BillList.datagrid('getSelections');
	if (json_data == null || json_data == undefined || json_data.length ==0) {
		$.messager.alert('提示', '必须选择一条记录进行操作', 'warning');
		return;
	}
	if (json_data.length>1) {
		$.messager.alert('提示', '只能选择一条记录进行操作', 'warning');
		return;
	}

	JSPFree.openDialog2("账户信息","/yujs/cams/collection/corp/approvalCorp.js", 950,600, {corp:json_data[0]},function(_rtdata){
		JSPFree.queryDataByConditon(d1_2_BillList,null);  //立即查询刷新数据
	},true);
}

/**
 * 个人账户：批量审批
 */
function batchapproval2(){
	var json_data = d1_2_BillList.datagrid('getSelections');
	if (json_data == null || json_data == undefined || json_data.length ==0) {
		$.messager.alert('提示', '必须选择一条记录进行操作', 'warning');
		return;
	}

	var rids = [];
	for (var i=0;i<json_data.length;i++) {
		rids.push(json_data[i].rid);
	}
	
	var jso_par = {rids:rids,user_no:str_LoginUserCode,data_type:"corp"};
	var jsn_result = JSPFree.doClassMethodCall("com.yusys.cams.corp.service.CamsCorpBSDMO","batchDataApproval",jso_par);
	
	if (jsn_result.msg == 'OK') {
		$.messager.alert('提示', '数据批量审批成功！', 'info');
		JSPFree.queryDataByConditon(d1_2_BillList,null);  //立即查询刷新数据
	}
}

/**
 * 机构账户：查看日志
 */
function proclogs2(){
	var json_data = d1_2_BillList.datagrid('getSelections');
	if (json_data == null || json_data == undefined || json_data.length == 0) {
		$.messager.alert('提示', '必须选择一条记录进行操作', 'warning');
		return;
	}
	if (json_data.length>1) {
		$.messager.alert('提示', '只能选择一条记录进行操作', 'warning');
		return;
	}

	var jso_par = {data_id:json_data[0].rid,data_type:"corp"};
	JSPFree.openDialog("处理日志信息","/yujs/cams/collection/processlogs.js",900,600,jso_par,function(_rtData){
		if (_rtData != null) {
			if ("dirclose" == _rtData.type) {
				return;
			}
		}
	});
}