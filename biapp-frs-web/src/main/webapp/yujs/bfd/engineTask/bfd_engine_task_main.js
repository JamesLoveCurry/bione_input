/**
 * 
 * <pre>
 * Title: 【引擎管理】
 * Description:【检核任务】
 * </pre>
 * @author wangxy31 
 * @version 1.00.00
   @date 2020年8月13日
 */

var org_no = "";
var org_class = "";
var condition = " 1=1 ";
function AfterInit() {
	JSPFree.createTabb("d1", ["表任务", "规则任务"]); // 创建多页签

	JSPFree.createBillList("d1_1", "/biapp-bfd/freexml/engineTask/bfd_engine_tab_task_CODE1.xml", null, {isSwitchQuery:"N"}); // 第1个页签
	JSPFree.createBillList("d1_2", "/biapp-bfd/freexml/engineTask/bfd_engine_rule_task_CODE1.xml", null, {isSwitchQuery:"N"}); // 第2个页签

	JSPFree.queryDataByConditon2(d1_1_BillList, getCondition());
	JSPFree.queryDataByConditon2(d1_2_BillList, getCondition());
	
	JSPFree.setBillListForceSQLWhere(d1_1_BillList, getCondition());
	JSPFree.setBillListForceSQLWhere(d1_2_BillList, getCondition());
}

/**
 * 增加查询条件
 * @returns
 */
function getCondition() {
	// 获取当前用户查询权限  admin查询所有，其他条线只能查看自己创建的数据
	var whereSql = '';
	var jsn_result = JSPFree.doClassMethodCall("com.yusys.bfd.engineTask.service.BfdEngieTaskBS", "getListByCreateBy", {field: 'busi_org_no'});
	if (jsn_result.code == 'success') {
		whereSql = jsn_result.data;
	}
	condition = condition + " and " + whereSql;
	
	return condition;
}

/**
 * 表任务：新增
 * @return {[type]} [description]
 */
function tabCreate() {
	JSPFree.openDialog("引擎任务","/yujs/bfd/engineTask/bfd_engine_task_tab_add.js", 700, 520, {org_no: org_no, org_class: org_class}, function(_rtdata){
		if (_rtdata == true) {
			JSPFree.queryDataByConditon(d1_1_BillList, null);  // 立即查询刷新数据
		}
	});
}

/**
 * 规则任务：新增
 * @return {[type]} [description]
 */
function ruleCreate() {
	JSPFree.openDialog("引擎任务","/yujs/bfd/engineTask/bfd_engine_task_rule_add.js", 700, 520, {org_no: org_no, org_class: org_class}, function(_rtdata){
		if (_rtdata == true) {
			JSPFree.queryDataByConditon(d1_2_BillList, null);  // 立即查询刷新数据
		}
	});
}

/**
 * 表任务：编辑
 * @return {[type]} [description]
 */
function tabUpdate() {
	var selectData = d1_1_BillList.datagrid('getSelections');
	if (selectData == null) {
		$.messager.alert('提示', '必须选择一条记录进行操作', 'warning');
		
		return;
	}
	
	if (selectData.length > 1) {
		$.messager.alert('提示', '只能选择一条记录进行操作', 'warning');
		
		return;
	}
	
	var jso_par = {task_id : selectData[0].task_id};
	var jso_status = JSPFree.doClassMethodCall("com.yusys.bfd.engineTask.service.BfdEngieTaskBS", "getTaskStatus", jso_par);
	if (jso_status.code == -999) {
		$.messager.alert('提示', '当前表任务正在进行中，请刷新状态或不能编辑！', 'warning');
		
		return;
	}
	
	JSPFree.openDialog("引擎任务", "/yujs/bfd/engineTask/bfd_engine_task_tab_edit.js", 700, 520, {task_id:selectData[0].task_id,org_no: org_no, org_class: org_class}, function(_rtdata){
		if (_rtdata == true) {
			JSPFree.queryDataByConditon(d1_1_BillList, null);  // 立即查询刷新数据
		}
	});
}

/**
 * 规则任务：编辑
 * @return {[type]} [description]
 */
function ruleUpdate() {
	var selectData = d1_2_BillList.datagrid('getSelections');
	if (selectData == null) {
		$.messager.alert('提示', '必须选择一条记录进行操作', 'warning');
		
		return;
	}
	
	if (selectData.length > 1) {
		$.messager.alert('提示', '只能选择一条记录进行操作', 'warning');
		
		return;
	}
	
	var jso_par = {task_id : selectData[0].task_id};
	var jso_status = JSPFree.doClassMethodCall("com.yusys.bfd.engineTask.service.BfdEngieTaskBS", "getTaskStatus", jso_par);
	if (jso_status.code == -999) {
		$.messager.alert('提示', '当前规则任务正在进行中，请刷新状态或不能编辑！', 'warning');
		
		return;
	}
	
	JSPFree.openDialog("引擎任务", "/yujs/bfd/engineTask/bfd_engine_task_rule_edit.js", 700, 520, {task_id:selectData[0].task_id,org_no: org_no, org_class: org_class}, function(_rtdata){
		if (_rtdata == true) {
			JSPFree.queryDataByConditon(d1_2_BillList, null);  // 立即查询刷新数据
		}
	});
}

/**
 * 表任务：删除
 * @return {[type]} [description]
 */
function tabDelete() {
	var selectData = d1_1_BillList.datagrid('getSelected');
	if (selectData == null) {
		$.messager.alert('提示', '必须选择一条记录进行操作', 'warning');
		
		return;
	}
	
	var jso_par = {task_id : selectData.task_id};
	var jso_status = JSPFree.doClassMethodCall("com.yusys.bfd.engineTask.service.BfdEngieTaskBS", "getTaskStatus", jso_par);
	if (jso_status.code == -999) {
		$.messager.alert('提示', '当前表任务正在进行中，请刷新状态或不能删除！', 'warning');
		
		return;
	}
	
	JSPFree.doBillListBatchDelete(d1_1_BillList)
}

/**
 * 规则任务：删除
 * @return {[type]} [description]
 */
function ruleDelete() {
	var selectData = d1_2_BillList.datagrid('getSelected');
	if (selectData == null) {
		$.messager.alert('提示', '必须选择一条记录进行操作', 'warning');
		
		return;
	}
	
	var jso_par = {task_id : selectData.task_id};
	var jso_status = JSPFree.doClassMethodCall("com.yusys.bfd.engineTask.service.BfdEngieTaskBS", "getTaskStatus", jso_par);
	if (jso_status.code == -999) {
		$.messager.alert('提示', '当前规则任务正在进行中，请刷新状态或不能删除！', 'warning');
		
		return;
	}
	
	JSPFree.doBillListBatchDelete(d1_2_BillList)
}

/**
 * 表任务：启动任务
 * @returns
 */
function tabStart () {
	var selectDatas = d1_1_BillList.datagrid('getSelections');
	if (selectDatas == null || selectDatas == undefined || selectDatas.length == 0) {
		$.messager.alert('提示', '至少选择一条记录进行操作', 'warning');
		return;
	}
	var jso_rids = new Array();
	for (var i=0; i<selectDatas.length; i++) {
		jso_rids.push(selectDatas[i].task_id);
	}
	var jsy_data = JSPFree.doClassMethodCall("com.yusys.bfd.engineTask.service.BfdEngieTaskBS","getTaskStatusIsInConductByTaskIds", {allTaskIds: jso_rids});
	if (jsy_data.msg == "OK") {
		$.messager.alert('提示', '所选任务中存在进行或排队中的任务!', 'warning');
		return;
	}
	var jso_data = JSPFree.doClassMethodCall("com.yusys.bfd.engineTask.service.BfdEngieTaskBS", "startAllTask", {allTaskIds: jso_rids});
	if (jso_data.errorCode == '0000') {
		tabRefresh();
		JSPFree.openDialog2("任务监控","/yujs/bfd/engineTask/bfd_engine_task_start.js",1000, 600,null);
	} else {
		$.messager.alert('提示', '任务执行失败，失败原因：' + jso_data.message, 'error');
	}
}

/**
 * 任务监控
 */
function showStatus() {
	JSPFree.openDialog2("任务监控","/yujs/bfd/engineTask/bfd_engine_task_start.js",1000, 600,null);
}
/**
 * 规则任务：启动任务
 * @returns
 */
function ruleStart() {
	var selectDatas = d1_2_BillList.datagrid('getSelections');
	if (selectDatas == null || selectDatas == undefined || selectDatas.length == 0) {
		$.messager.alert('提示', '至少选择一条记录进行操作', 'warning');
		return;
	}
	var jso_rids = new Array();
	for (var i=0; i<selectDatas.length; i++) {
		jso_rids.push(selectDatas[i].task_id);
	}
	var jsy_data = JSPFree.doClassMethodCall("com.yusys.bfd.engineTask.service.BfdEngieTaskBS","getTaskStatusIsInConductByTaskIds", {allTaskIds: jso_rids});
	if (jsy_data.msg == "OK") {
		$.messager.alert('提示', '所选任务中存在进行或排队中的任务!', 'warning');
		return;
	}
	
	var jso_data = JSPFree.doClassMethodCall("com.yusys.bfd.engineTask.service.BfdEngieTaskBS", "startAllTask", {allTaskIds: jso_rids});
	if (jso_data.errorCode=='0000') {
		JSPFree.openDialog2("任务监控","/yujs/bfd/engineTask/bfd_engine_task_start.js",1000,600,null);
		ruleRefresh();
	} else {
		$.messager.alert('提示', '任务执行失败，失败原因：'+jso_data.message, 'error');
	}
}

/**
 * 中断任务公共方法
 * @param selectData
 */
function extracted(selectData) {
	if (selectData == null || selectData == undefined || selectData.length == 0) {
		$.messager.alert('提示', '必须选择一条记录进行操作', 'warning');
		return;
	}
	if (selectData.length>1) {
		$.messager.alert('提示', '只能选择一条记录进行操作', 'warning');
		return;
	}
	var jso_par = {task_id: selectData.task_id};
	var jso_status = JSPFree.doClassMethodCall("com.yusys.bfd.engineTask.service.BfdEngieTaskBS", "getBreakTaskStatus", jso_par);
	if (jso_status.code != -999) {
		$.messager.alert('提示', '当前任务不在进行中，无需中断！', 'warning');
		return;
	}
	JSPFree.confirm('提示', '是否中断任务?', function (_isOK) {
		if (_isOK) {
			var _rt = JSPFree.doClassMethodCall("com.yusys.bfd.engineTask.service.BfdEngieTaskBS", "forceCloseEngineTask", {task_id: selectData.task_id});
			if (_rt.status == "OK") {
				if (selectData.task_type == "表任务"){
					JSPFree.refreshBillListCurrRow(d1_1_BillList); // 刷新当前行
				}else {
					JSPFree.refreshBillListCurrRow(d1_2_BillList); // 刷新当前行
				}
				$.messager.alert('提示', '任务中断成功！', 'warning');
			}
		}
	});
}

/**
 * 表中断任务
 */
function tabForceClose() {
	var selectDatas = d1_1_BillList.datagrid('getSelections');
	extracted(selectDatas[0]);
}

/**
 * 规则中断任务
 */
function ruleForceClose() {
	var selectDatas = d1_2_BillList.datagrid('getSelections');
	extracted(selectDatas[0]);
}

/**
 * 取消排队
 */
function dequeue(selectDatas) {
	if (selectDatas == null || selectDatas == undefined || selectDatas.length == 0) {
		$.messager.alert('提示', '至少选择一条记录进行操作', 'warning');
		return;
	}
	var jso_rids = new Array();
	for (var i=0; i<selectDatas.length; i++) {
		jso_rids.push(selectDatas[i].task_id);
	}
	var jsy_data = JSPFree.doClassMethodCall("com.yusys.bfd.engineTask.service.BfdEngieTaskBS","getQueueTaskStatus", {allTaskIds: jso_rids});
	if (jsy_data.code == "success") {
		$.messager.alert('提示', '只能选择排队中的任务,或任务已经启动引擎还未返回状态,请稍后!', 'warning');
		return;
	}

	var jso_data = JSPFree.doClassMethodCall("com.yusys.bfd.engineTask.service.BfdEngieTaskBS", "removeQueue", {allTaskIds: jso_rids});
	if (jso_data.code == 'success') {
		$.messager.alert('提示', '取消排队任务成功！', 'warning');
		JSPFree.queryDataByConditon2(d1_1_BillList);
		JSPFree.queryDataByConditon2(d1_2_BillList);
	} else {
		$.messager.alert('提示', jso_data.msg, 'warning');
	}
}

/**
 * 表取消排队
 */
function tabDequeue() {
	var selectDatas = d1_1_BillList.datagrid('getSelections');
	dequeue(selectDatas);
}

/**
 * 规则取消排队
 */
function ruleDequeue() {
	var selectDatas = d1_2_BillList.datagrid('getSelections');
	dequeue(selectDatas);
}

/**
 * 表任务：查看日志
 * @returns
 */
function tabLog() {
	var selectData = d1_1_BillList.datagrid('getSelections');
	if (selectData == null) {
		$.messager.alert('提示', '必须选择一条记录进行操作', 'warning');
		
		return;
	}
	
	if (selectData.length > 1) {
		$.messager.alert('提示', '只能选择一条记录进行操作', 'warning');
		return;
	}
	
	var jso_OpenPars = {task_id: selectData[0].task_id,tabNames:selectData[0].tab_names,dataDt:selectData[0].data_dt,status:selectData[0].status};
	JSPFree.openDialog("日志", "/yujs/bfd/engineTask/bfd_engine_task_log.js", 900, 600, jso_OpenPars, function(_rtdata) {});
}

/**
 * 规则任务：查看日志
 * @returns
 */
function ruleLog() {
	var selectData = d1_2_BillList.datagrid('getSelected');
	if (selectData == null) {
		$.messager.alert('提示', '必须选择一条记录进行操作', 'warning');
		
		return;
	}
	
	if (selectData.length > 1) {
		$.messager.alert('提示', '只能选择一条记录进行操作', 'warning');
		return;
	}
	
	var jso_OpenPars = {task_id: selectData[0].task_id,tabNames:selectData[0].tab_names,dataDt:selectData[0].data_dt,status:selectData[0].status};
	JSPFree.openDialog("日志", "/yujs/bfd/engineTask/bfd_engine_task_log.js", 900, 600, jso_OpenPars, function(_rtdata) {});
}


/**
 * 表任务：刷新
 * @returns
 */
function tabRefresh() {
	JSPFree.queryDataByConditon2(d1_1_BillList);
}

/**
 * 规则任务：刷新
 * @returns
 */
function ruleRefresh() {
	JSPFree.queryDataByConditon2(d1_2_BillList);
}

/**
 * 查看命令模板
 * @param _btn
 * @returns
 */
function onLookCommand1(_btn) {
	var str_cmd = _btn.dataset.itemvalue;
	FreeUtil.openHtmlMsgBox("查看命令模板", 800, 300, "<span style='font-size:20px;color:#9B0000'>" + str_cmd + "</span>");
}

/**
 * 查看实际命令
 * @param _btn
 * @returns
 */
function onLookCommand2(_btn) {
	var str_cmd = _btn.dataset.itemvalue;
	FreeUtil.openHtmlMsgBox("查看实际命令", 800, 500, "<span style='font-size:14px;color:blue'>" + str_cmd + "</span>");
}
