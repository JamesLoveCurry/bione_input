//业务数据查询【/frs/yufreejs?js=/yujs/cams/busidataquery.js】
function AfterInit(){
	JSPFree.createTabb("d1", [ "个人账户", "机构账户" ]);
	JSPFree.createBillList("d1_1","/biapp-cams/freexml/cams/cams_indv_acct_CODE1.xml",null,{isSwitchQuery:"N",autoquery:"Y",list_ispagebar:"Y",list_ischeckstyle:"N",list_ismultisel:"N",autocondition:"status='2'",querycontion:"status='2'",list_btns:"[icon-ok]复核/review1;[icon-p27]查看日志/proclogs1"});
	JSPFree.createBillList("d1_2","/biapp-cams/freexml/cams/cams_corp_acct_CODE1.xml",null,{isSwitchQuery:"N",autoquery:"Y",list_ispagebar:"Y",list_ischeckstyle:"N",list_ismultisel:"N",autocondition:"status='2'",querycontion:"status='2'",list_btns:"[icon-ok]复核/review2;[icon-p27]查看日志/proclogs2"});

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
 * 个人账户：复核
 */
function review1(){
	var json_data = d1_1_BillList.datagrid('getSelected');
	if (json_data == null || json_data == undefined || json_data.length == 0) {
		$.messager.alert('提示', '必须选择一条记录进行操作', 'warning');
		return;
	}

	JSPFree.openDialog2("账户信息","/yujs/cams/collection/indv/reviewIndv.js", 950,600, {indv:json_data},function(_rtdata){
		JSPFree.queryDataByConditon(d1_1_BillList,null);  //立即查询刷新数据
	},true);
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
 * 机构账户：复核
 */
function review2(){
	var json_data = d1_2_BillList.datagrid('getSelected');
	if (json_data == null || json_data == undefined || json_data.length == 0) {
		$.messager.alert('提示', '必须选择一条记录进行操作', 'warning');
		return;
	}

	JSPFree.openDialog2("账户信息","/yujs/cams/collection/corp/reviewCorp.js", 950,600, {corp:json_data},function(_rtdata){
		JSPFree.queryDataByConditon(d1_2_BillList,null);  //立即查询刷新数据
	},true);
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