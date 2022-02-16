//业务数据查询【/frs/yufreejs?js=/yujs/cams/busidataquery.js】
function AfterInit(){
	JSPFree.createTabb("d1", [ "个人账户", "机构账户" ]);
	JSPFree.createBillList("d1_1","/biapp-cams/freexml/cams/cams_indv_acct_CODE1.xml",null,{isSwitchQuery:"N",autoquery:"Y",list_ispagebar:"Y"});
	JSPFree.createBillList("d1_2","/biapp-cams/freexml/cams/cams_corp_acct_CODE1.xml",null,{isSwitchQuery:"N",autoquery:"Y",list_ispagebar:"Y"});

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
 * 个人账户：新增
 */
function insert1(){
	JSPFree.openDialog("账户信息","/yujs/cams/collection/indv/addIndv.js", 950,600, null,function(_rtdata){
		JSPFree.queryDataByConditon(d1_1_BillList,null);  //立即查询刷新数据
	},true);
}

/**
 * 个人账户：修改
 */
function update1(){
	var json_data = d1_1_BillList.datagrid('getSelections');
	if (json_data == null || json_data == undefined || json_data.length ==0) {
		$.messager.alert('提示', '必须选择一条记录进行操作', 'warning');
		return;
	}
	if (json_data.length>1) {
		$.messager.alert('提示', '只能选择一条记录进行操作', 'warning');
		return;
	}
	// 只能修改状态为“初始化”、“退回”的数据
	if (json_data[0].status != '1' && json_data[0].status != '4') { // 1初始化2待复核3待审批4退回5完成
		var ss = "";
		if(json_data[0].status == '1'){
			ss = "初始化";
		}else if(json_data[0].status == '2'){
			ss = "待复核";
		}else if(json_data[0].status == '3'){
			ss = "待审批";
		}else if(json_data[0].status == '4'){
			ss = "退回";
		}else if(json_data[0].status == '5'){
			ss = "完成";
		}
		$.messager.alert('提示', '当前数据状态为【'+ss+'】，不可修改', 'warning');
		
		return;
	}
	
	JSPFree.openDialog2("账户信息","/yujs/cams/collection/indv/editIndv.js",950,600,{indv:json_data[0]},function(_rtData){
		if (_rtData != null) {
			if ("dirclose" == _rtData.type) {
				return;
			} 
			
			if (_rtData) {
				$.messager.alert('提示', '提交数据成功!', 'info');
				JSPFree.refreshBillListCurrPage(d1_1_BillList);
			}
		}
	},true);
}

/**
 * 个人账户：删除
 */
function delete1(){
	var json_data = d1_1_BillList.datagrid('getSelections');
	if (json_data == null || json_data == undefined || json_data.length ==0) {
		$.messager.alert('提示', '必须选择一条记录进行操作', 'warning');
		return;
	}
	
	var jso_allrids=[];
	for (var i=0;i<json_data.length;i++) {
		// 只能删除状态为“初始化”、“退回”的数据
		if (json_data[i].status != '1' && json_data[i].status != '4') { // 1初始化2待复核3待审批4退回5完成
			var ss = "";
			if(json_data[i].status == '1'){
				ss = "初始化";
			}else if(json_data[i].status == '2'){
				ss = "待复核";
			}else if(json_data[i].status == '3'){
				ss = "待审批";
			}else if(json_data[i].status == '4'){
				ss = "退回";
			}else if(json_data[i].status == '5'){
				ss = "完成";
			}
			$.messager.alert('提示', '当前数据包含状态为【'+ss+'】，不可删除，请重新选择', 'warning');
			
			return;
		}
		jso_allrids.push(json_data[i].rid);
	}

	JSPFree.confirm('提示', '你真的要删除选中的记录吗?', function(_isOK){
		if (_isOK){
			var jsn_result = JSPFree.doClassMethodCall("com.yusys.cams.indv.service.CamsIndvBSDMO", "deleteAllByIndvRid", {allrids:jso_allrids});
			if (jsn_result.msg == 'OK') {
				$.messager.alert('提示', '删除数据成功!', 'info');
				JSPFree.queryDataByConditon(d1_1_BillList);
			}
		}
	});
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
 * 机构账户：新增
 */
function insert2(){
	JSPFree.openDialog("账户信息","/yujs/cams/collection/corp/addCorp.js", 950,600, null,function(_rtdata){
		JSPFree.queryDataByConditon(d1_2_BillList,null);  //立即查询刷新数据
	},true);
}

/**
 * 机构账户：修改
 */
function update2(){
	var json_data = d1_2_BillList.datagrid('getSelections');
	if (json_data == null || json_data == undefined || json_data.length ==0) {
		$.messager.alert('提示', '必须选择一条记录进行操作', 'warning');
		return;
	}
	if (json_data.length>1) {
		$.messager.alert('提示', '只能选择一条记录进行操作', 'warning');
		return;
	}
	// 只能修改状态为“初始化”、“退回”的数据
	if (json_data[0].status != '1' && json_data[0].status != '4') { // 1初始化2待复核3待审批4退回5完成
		var ss = "";
		if(json_data[0].status == '1'){
			ss = "初始化";
		}else if(json_data[0].status == '2'){
			ss = "待复核";
		}else if(json_data[0].status == '3'){
			ss = "待审批";
		}else if(json_data[0].status == '4'){
			ss = "退回";
		}else if(json_data[0].status == '5'){
			ss = "完成";
		}
		$.messager.alert('提示', '当前数据状态为【'+ss+'】，不可修改', 'warning');
		
		return;
	}
	
	JSPFree.openDialog2("账户信息","/yujs/cams/collection/corp/editCorp.js",950,600,{corp:json_data[0]},function(_rtData){
		if (_rtData != null) {
			if ("dirclose" == _rtData.type) {
				return;
			} 
			
			if (_rtData) {
				$.messager.alert('提示', '提交数据成功!', 'info');
				JSPFree.refreshBillListCurrPage(d1_2_BillList);
			}
		}
	},true);
}

/**
 * 机构账户：删除
 */
function delete2(){
	var json_data = d1_2_BillList.datagrid('getSelections');
	if (json_data == null || json_data == undefined || json_data.length ==0) {
		$.messager.alert('提示', '必须选择一条记录进行操作', 'warning');
		return;
	}
	
	var jso_allacns=[];
	for (var i=0;i<json_data.length;i++) {
		// 只能删除状态为“初始化”、“退回”的数据
		if (json_data[i].status != '1' && json_data[i].status != '4') { // 1初始化2待复核3待审批4退回5完成
			var ss = "";
			if(json_data[i].status == '1'){
				ss = "初始化";
			}else if(json_data[i].status == '2'){
				ss = "待复核";
			}else if(json_data[i].status == '3'){
				ss = "待审批";
			}else if(json_data[i].status == '4'){
				ss = "退回";
			}else if(json_data[i].status == '5'){
				ss = "完成";
			}
			$.messager.alert('提示', '当前数据包含状态为【'+ss+'】，不可删除，请重新选择', 'warning');
			
			return;
		}
		jso_allacns.push(json_data[i].account_number);
	}

	JSPFree.confirm('提示', '你真的要删除选中的记录吗?', function(_isOK){
		if (_isOK){
			var jsn_result = JSPFree.doClassMethodCall("com.yusys.cams.corp.service.CamsCorpBSDMO", "deleteAllByCorpRid", {allacns:jso_allacns});
			if (jsn_result.msg == 'OK') {
				$.messager.alert('提示', '删除数据成功!', 'info');
				JSPFree.queryDataByConditon(d1_2_BillList);
			}
		}
	});
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