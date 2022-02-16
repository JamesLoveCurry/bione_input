//报表下发 初始化界面,菜单配置路径是【/frs/yufreejs?js=/yujs/wmp/distribute_main.js】
function AfterInit(){
	JSPFree.createBillList("d1","/biapp-wmp/freexml/wmp/wmp_filling_process_CODE1.xml",null,{querycontion:"org_no='"+str_LoginUserOrgNo+"'",autocondition:"org_no='"+str_LoginUserOrgNo+"'",isSwitchQuery:"N",orderbys:"data_dt,tab_name_en"});
}

// 编辑
function updateTask(){
	//选择选中的数据..
	var json_data = d1_BillList.datagrid('getSelections');
	if (json_data == null || json_data == undefined || json_data.length == 0) {
		$.messager.alert('提示', '必须选择一条记录进行操作', 'warning');
		return;
	}
	if (json_data.length>1) {
		$.messager.alert('提示', '只能选择一条记录进行操作', 'warning');
		return;
	}
	// 根据状态判断，如果是分发，完成状态，则详细页面置灰，不可编辑分发说明
	var jso_Pars = {data:json_data[0]};
	
	JSPFree.openDialog("编辑","/yujs/wmp/distribute_main_edit.js",700,560,jso_Pars,function(_rtData){
		if (_rtData == true) {
			JSPFree.refreshBillListCurrRow(d1_BillList);
		}
	});
}

// 创建分发任务
function insertTask(){	
	JSPFree.openDialog("新增","/yujs/wmp/distribute_main_insert.js",700,560,null,function(_rtData){
		if (_rtData != null) {
			if ("dirclose" == _rtData.type) {
				return;
			}
			if (_rtData.msg == "OK") {
				var str_sql = _rtData.whereSQL;  //返回的主键拼成的SQL
			       JSPFree.queryDataByConditon(d1_BillList,str_sql);
			       JSPFree.alert("新增数据成功!<br>当前页面数据是查询的新增数据!");
			} else {
				JSPFree.alert("新增数据失败!<br>当前数据已存在!");
			}
		}
	});
}

// 删除分发任务
function deleteTask(){
	var json_data = d1_BillList.datagrid('getSelections');
	if (json_data == null || json_data == undefined || json_data.length == 0) {
		$.messager.alert('提示', '必须至少选择一条记录进行操作', 'warning');
		return;
	}

	// 判断当前状态是否是分发和完成状态
	for (var i=0;i<json_data.length;i++) {
		if (json_data[i].status == '1' || json_data[i].status == '3') {
			$.messager.alert('提示', '当前状态下，不能下进行删除操作！', 'warning');
			return;
		}
	}
	
	JSPFree.confirm('提示', '你真的要删除选中的记录吗?', function(_isOK){
		if (_isOK){
			var jso_allrids=[]; //存放所有选中表名和英文表名的数组
			var jso_alltabnames=[]; //存放所有选中表名和英文表名的数组
			for(var i=0; i<json_data.length; i++){
				jso_allrids.push(json_data[i].rid);
				jso_alltabnames.push(json_data[i].tab_name_en);
			}

			var jsn_result = JSPFree.doClassMethodCall("com.yusys.wmp.process.service.WmpCrProcessBS", "deleteByTaskRid", {allrids:jso_allrids,alltabnameens:jso_alltabnames});
			if (jsn_result.msg == 'OK') {
				$.messager.alert('提示', '删除数据成功!', 'info');
				JSPFree.queryDataByConditon(d1_BillList);
			}
		}
	});
}

// 维护错误明细
function maintainDetai(){
	var json_data = d1_BillList.datagrid('getSelections');
	if (json_data == null || json_data == undefined || json_data.length == 0) {
		$.messager.alert('提示', '必须选择一条记录进行操作', 'warning');
		return;
	}
	if (json_data.length>1) {
		$.messager.alert('提示', '只能选择一条记录进行操作', 'warning');
		return;
	}

	var jso_Pars = {taskId:json_data[0].rid,status:json_data[0].status,tabNameEn:json_data[0].tab_name_en,tabName:json_data[0].tab_name,dataDt:json_data[0].data_dt};
	
	JSPFree.openDialog("维护明细","/yujs/wmp/distribute_detail_info.js",1000,600,jso_Pars,function(_rtData){
		if (_rtData != null) {
			if ("dirclose" == _rtData.type) {
				return;
			}
		}
	});
}

// 分发
function distributeTask(){
	var json_data = d1_BillList.datagrid('getSelections');
	if (json_data == null || json_data == undefined || json_data.length == 0) {
		$.messager.alert('提示', '必须至少选择一条记录进行操作', 'warning');
		return;
	}
	// 判断当前状态是否是分发和完成状态
	for (var i=0;i<json_data.length;i++) {
		if (json_data[i].status == '1' || json_data[i].status == '3') {
			$.messager.alert('提示', '当前状态下，不能下进行分发操作！', 'warning');
			return;
		}
	}
	
	var str_json = {jsonData:json_data,orgNo:str_LoginUserOrgNo}
	var jsn_result = JSPFree.doClassMethodCall("com.yusys.wmp.process.service.WmpCrProcessBS", "createChildTasks", str_json);
	if (jsn_result.msg == 'OK') {
		$.messager.alert('提示', '任务分发成功!', 'info');
		JSPFree.queryDataByConditon(d1_BillList);
	} else if (jsn_result.msg == 'Fail') {
		$.messager.alert('提示', '当前分发的任务中包含空的明细数据，请确认后再分发！', 'warning');
	}
}

// 撤回
function withdrawTask(){
	var json_data = d1_BillList.datagrid('getSelections');
	if (json_data == null || json_data == undefined || json_data.length == 0) {
		$.messager.alert('提示', '必须至少选择一条记录进行操作', 'warning');
		return;
	}
	
	for(var i=0; i<json_data.length; i++){
		// 只能对已分发数据进行撤回处理
		if (json_data[i].status != "1") {
			$.messager.alert('提示', '只能对已分发数据进行撤回处理！', 'warning');
			return;
		}
	}
	
	var str_json = {jsonData:json_data}
	
	JSPFree.confirm('提示', '撤回操作会清空子任务数据，请确认是否撤回？', function(_isOK){
		if (_isOK){
			var jsn_result = JSPFree.doClassMethodCall("com.yusys.wmp.process.service.WmpCrProcessBS", "withdrawDataByTask", str_json);
			
			if (jsn_result.msg == 'OK') {
				$.messager.alert('提示', '任务撤回成功!', 'info');
				JSPFree.queryDataByConditon(d1_BillList);
			}
		}
	});
}

//子任务信息
function childTask(){
	var json_data = d1_BillList.datagrid('getSelections');
	if (json_data == null || json_data == undefined || json_data.length == 0) {
		$.messager.alert('提示', '必须选择一条记录进行操作', 'warning');
		return;
	}
	if (json_data.length>1) {
		$.messager.alert('提示', '只能选择一条记录进行操作', 'warning');
		return;
	}
	// 只能是分发和完成，两种状态才可以查看该功能
	var jso_Pars = {data:json_data[0]};
	
	if (json_data[0].status == '1' || json_data[0].status == '3') {
		JSPFree.openDialog2("子任务信息","/yujs/wmp/distribute_main_child.js",900,600,jso_Pars,function(_rtData){
			if (_rtData != null) {
				if ("dirclose" == _rtData.type) {
					return;
				}
			}
		});
	} else {
		$.messager.alert('提示', '当前状态下不可进行查看子任务信息操作', 'warning');
		return;
	}
}
