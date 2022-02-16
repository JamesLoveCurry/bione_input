/**
 * 
 * <pre>
 * Title: 【报表处理】-【报表下发】
 * Description: 报表下发：主页面
 * 此页面提供了报表处理的下发管理功能，可以允许用户选择错误的明细数据进行下发
 * 对应的子任务会落在不同的机构上，由不同机构的人进行填报。
 * 此外还提供了撤回和查看子任务信息等操作
 * </pre>
 * @author wangxy31 
 * @version 1.00.00
   @date 2021年10月22日
 */
var org_no = "";
var maskUtil = "";
function AfterInit(){
	maskUtil = FreeUtil.getMaskUtil();
	org_no = CrrsFreeUtil.getOrgNo(str_LoginUserOrgNo);
	
	// 下发创建和查看只能查看当前机构的数据
	var condition = getQueryCondition();

	JSPFree.createBillList("d1", "/biapp-crrs/freexml/crrs/fillingProcess/crrs_filling_process_CODE1.xml", null, {
		querycontion: condition,
		autocondition:condition,
		isSwitchQuery: "N",
		orderbys: "data_dt desc,tab_name_en"
	});
}

/**
 * 增加查询条件
 * @returns
 */
function getQueryCondition(){
	var jso_rt = JSPFree.doClassMethodCall("com.yusys.crrs.customer.service.ValidateQueryOrgNoCondition","getQueryCondition",{"_loginUserOrgNo" : str_LoginUserOrgNo});
	var condition = "";
	if(jso_rt.msg == "ok"){
		condition = jso_rt.condition;
	}

	return condition;
}

/**
 * 选择数据日期和数据表，创建新增下发任务
 * @returns
 */
function insertTask(){
	JSPFree.openDialog("新增","/yujs/crrs/fillingProcess/crrs_distribute_main_insert.js",900,560,{org_no:org_no},function(_rtData){
		if (_rtData != null) {
			if ("dirclose" == _rtData.type) {
				return;
			}
			if (_rtData.msg == "OK") {
				var str_sql = _rtData.whereSQL;  //返回的主键拼成的SQL
			       JSPFree.queryDataByConditon(d1_BillList,str_sql);
			       JSPFree.alert("新增数据成功!<br>当前页面数据是查询的新增数据!");
			} else {
				JSPFree.alert("新增数据失败!<br>当前数据已存在/或者明细数据不存在!");
			}
		}
	});
}

/**
 * 编辑下发任务：选中一条任务，对下发说明进行编辑
 * @returns
 */
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
	
	JSPFree.openDialog("编辑","/yujs/crrs/fillingProcess/crrs_distribute_main_edit.js",700,560,jso_Pars,function(_rtData){
		if (_rtData == true) {
			JSPFree.refreshBillListCurrRow(d1_BillList);
		}
	});
}

/**
 * 删除下发任务：判断状态，删除下发任务和关联子任务
 * @returns
 */
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
			var jso_allrids=[]; // 存放所有选中表名和英文表名的数组
			var jso_alltabnames=[]; // 存放所有选中表名和英文表名的数组
			for(var i=0; i<json_data.length; i++){
				jso_allrids.push(json_data[i].rid)
			}

			var jsn_result = JSPFree.doClassMethodCall("com.yusys.crrs.process.service.CrrsCrProcessBSDMO", "deleteByTaskRid", {allrids:jso_allrids,alltabnameens:jso_alltabnames});
			if (jsn_result.msg == 'OK') {
				$.messager.alert('提示', '删除数据成功!', 'info');
				JSPFree.queryDataByConditon(d1_BillList);
			}
		}
	});
}

/**
 * 分发操作
 * 选择一条或多条任务，判断任务状态，对任务进行分发，根据上一步选择的明细数据的不同机构号，创建多个子任务
 * @returns
 */
function distributeTask(){
	var json_data = d1_BillList.datagrid('getSelections');
	if (json_data == null || json_data == undefined || json_data.length == 0) {
		$.messager.alert('提示', '必须至少选择一条记录进行操作', 'warning');
		return;
	}

	// 判断当前数据是否被锁定
	for (var i=0;i<json_data.length;i++) {
		var lock_data = JSPFree.doClassMethodCall("com.yusys.crrs.lock.service.CrrsLockBSDMO", "getStatus", {
			data_dt: json_data[i].data_dt
		});
		if(lock_data.status == "锁定"){
			$.messager.alert('提示', '当前日期的表数据已被锁定，无法操作！', 'warning');
			return;
		}
	}
	var orgArray = new Array();
	// 判断当前状态是否是分发和完成状态
	for (var i=0;i<json_data.length;i++) {
		if (json_data[i].status == '1' || json_data[i].status == '3') {
			$.messager.alert('提示', '当前状态下，不能进行分发操作！', 'warning');
			return;
		}
		orgArray.push(json_data[i].org_no);
	}

	maskUtil.mask();
	setTimeout(function (){
		var str_json = {jsonData:json_data,orgNo:orgArray} // 传参报送机构号
		var jsn_result = JSPFree.doClassMethodCall("com.yusys.crrs.process.service.CrrsCrProcessBSDMO", "beforeChildTasks", str_json);
		maskUtil.unmask();
		if (jsn_result.msg == 'OK') {
			$.messager.alert('提示', '任务分发成功!', 'info');
			JSPFree.refreshBillListCurrRows(d1_BillList);
		} else if (jsn_result.msg == 'Fail') {
			$.messager.alert('提示', jsn_result.data, 'warning');
		}
	},100);
}

/**
 * 撤回操作
 * 判断任务状态，对下发的任务进行撤回，只能撤回已分发的任务
 * @returns
 */
function withdrawTask(){
	var json_data = d1_BillList.datagrid('getSelections');
	if (json_data == null || json_data == undefined || json_data.length == 0) {
		$.messager.alert('提示', '必须至少选择一条记录进行操作', 'warning');
		return;
	}

	//判断当前数据是否被锁定
	for (var i=0;i<json_data.length;i++) {
		var lock_data = JSPFree.doClassMethodCall("com.yusys.crrs.lock.service.CrrsLockBSDMO", "getStatus", {data_dt:json_data[i].data_dt});
		if(lock_data.status == "锁定"){
			$.messager.alert('提示', '当前日期的表数据已被锁定，无法操作！', 'warning');
			return;
		}
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
			maskUtil.mask();
			setTimeout(function () {
				var jsn_result = JSPFree.doClassMethodCall("com.yusys.crrs.process.service.CrrsCrProcessBSDMO", "withdrawDataByTask", str_json);
				maskUtil.unmask();
				if (jsn_result.msg == 'OK') {
					$.messager.alert('提示', '任务撤回成功!', 'info');
					JSPFree.refreshBillListCurrRows(d1_BillList);
				}
			}, 100);
		}
	});
}

/**
 * 查看子任务信息
 * 选中一条主任务，点击按钮，按机构号查看子任务的填报情况统计
 * @returns
 */
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
		JSPFree.openDialog2("子任务信息","/yujs/crrs/fillingProcess/crrs_distribute_main_child.js",900,600,jso_Pars,function(_rtData){
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

/**
* 解锁
 */
function unlock() {
	var json_data = d1_BillList.datagrid('getSelections');
	if (json_data == null || json_data == undefined || json_data.length == 0) {
		$.messager.alert('提示', '必须选择一条记录进行操作', 'warning');
		return;
	}
	for(var i=0; i<json_data.length; i++){
		// 只能对已分发数据进行撤回处理
		if (json_data[i].status != "3") {
			$.messager.alert('提示', '只有完成状态下才可解锁重新下发', 'warning');
			return;
		}
	}
	var str_json = {jsonData:json_data}
	var jsn_result = JSPFree.doClassMethodCall("com.yusys.crrs.process.service.CrrsCrProcessBSDMO", "unlock", str_json);
	if (jsn_result.msg == 'OK') {
		$.messager.alert('提示', '任务解锁成功!', 'info');
		JSPFree.refreshBillListCurrRow(d1_BillList);
	}
}
