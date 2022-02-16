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
function AfterInit() {
	JSPFree.createTabb("d1", ["表任务", "规则任务"]); // 创建多页签

	JSPFree.createBillList("d1_1", "/biapp-cr/freexml/engineTask/cr_engine_tab_task_CODE1.xml", null, {isSwitchQuery:"N"}); // 第1个页签
	JSPFree.createBillList("d1_2", "/biapp-cr/freexml/engineTask/cr_engine_rule_task_CODE1.xml", null, {isSwitchQuery:"N"}); // 第2个页签

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
	var condition = " 1=1 ";
	
	// 判断当前用户是总行，还是分行
	var jso_data = JSPFree.doClassMethodCall("com.yusys.cr.business.service.CrBusinessBS","checkUserOrgNo",{str_LoginUserOrgNo: str_LoginUserOrgNo});
	var result = jso_data.result;
	if (result == "OK") {
		org_no = jso_data.orgNo;
		org_class = jso_data.orgClass;
	}

	if (org_class == "总行") {

	} else if (org_class == "分行") {
		// 如果是分行，则进行置灰，并赋值
		condition = " busi_org_no = '"+ org_no +"' ";
	}
	// 获取当前用户查询权限  admin查询所有，其他条线只能查看自己创建的数据
	var whereSql = '';
	var jsn_result = JSPFree.doClassMethodCall("com.yusys.cr.engineTask.service.CrEngieTaskBS", "getListByCreateBy", null);
	if (jsn_result.code == 'success') {
		whereSql = jsn_result.data;
	}
	condition = condition + " and " + whereSql;
	return condition;
}

/**
 * 表任务：复制任务
 * @returns
 */
function batchTabsCreate() {
	JSPFree.openDialog("选择来源日期的任务", "/yujs/cr/engineTask/cr_engine_task_tab_create.js", 900, 500, {org_no: org_no, org_class: org_class}, function(_rtdata) {
		if (typeof _rtdata != "undefined" && _rtdata!=null && _rtdata.type!="dirclose") { // 的确有返回值!
		  JSPFree.queryDataByConditon(d1_1_BillList, "task_id in (" + _rtdata.newids + ")");
		  JSPFree.alert(_rtdata.msg);
	    }
	});
}

/**
 * 规则任务：复制任务
 * @returns
 */
function batchRulesCreate() {
	JSPFree.openDialog("选择来源日期的任务", "/yujs/cr/engineTask/cr_engine_task_rule_create.js", 900, 500, {org_no: org_no, org_class: org_class}, function(_rtdata) {
		if (typeof _rtdata != "undefined" && _rtdata!=null && _rtdata.type!="dirclose") { // 的确有返回值!
			JSPFree.queryDataByConditon(d1_2_BillList, "task_id in (" + _rtdata.newids + ")");
			JSPFree.alert(_rtdata.msg);
		}
	});
}

/**
 * 表任务：新增
 * @return {[type]} [description]
 */
function tabCreate() {
	JSPFree.openDialog("引擎任务","/yujs/cr/engineTask/cr_engine_task_tab_add.js", 700, 520, {org_no: org_no, org_class: org_class}, function(_rtdata){
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
	JSPFree.openDialog("引擎任务","/yujs/cr/engineTask/cr_engine_task_rule_add.js", 700, 520, {org_no: org_no, org_class: org_class}, function(_rtdata){
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
	var selectData = d1_1_BillList.datagrid('getSelected');
	if (selectData == null) {
		$.messager.alert('提示', '必须选择一条记录进行操作', 'warning');
		
		return;
	}
	
	var jso_par = {task_id : selectData.task_id};
	var jso_status = JSPFree.doClassMethodCall("com.yusys.cr.engineTask.service.CrEngieTaskBS", "getTaskStatus", jso_par);
	if (jso_status.code == -999) {
		$.messager.alert('提示', '当前表任务正在进行中，请刷新状态或不能编辑！', 'warning');
		
		return;
	}
	
	JSPFree.openDialog("引擎任务", "/yujs/cr/engineTask/cr_engine_task_tab_edit.js", 700, 520, {task_id:selectData.task_id,org_no: org_no, org_class: org_class}, function(_rtdata){
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
	var selectData = d1_2_BillList.datagrid('getSelected');
	if (selectData == null) {
		$.messager.alert('提示', '必须选择一条记录进行操作', 'warning');
		return;
	}
	
	var jso_par = {task_id : selectData.task_id};
	var jso_status = JSPFree.doClassMethodCall("com.yusys.cr.engineTask.service.CrEngieTaskBS", "getTaskStatus", jso_par);
	if (jso_status.code == -999) {
		$.messager.alert('提示', '当前规则任务正在进行中，请刷新状态或不能编辑！', 'warning');
		return;
	}
	
	JSPFree.openDialog("引擎任务", "/yujs/cr/engineTask/cr_engine_task_rule_edit.js", 700, 520, {task_id:selectData.task_id,org_no: org_no, org_class: org_class}, function(_rtdata){
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
	var jso_status = JSPFree.doClassMethodCall("com.yusys.cr.engineTask.service.CrEngieTaskBS", "getTaskStatus", jso_par);
	if (jso_status.code == -999) {
		$.messager.alert('提示', '当前表任务正在进行中，请刷新状态或不能删除！', 'warning');
		return;
	}
	JSPFree.doBillListDelete(d1_1_BillList)
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
	var jso_status = JSPFree.doClassMethodCall("com.yusys.cr.engineTask.service.CrEngieTaskBS", "getTaskStatus", jso_par);
	if (jso_status.code == -999) {
		$.messager.alert('提示', '当前规则任务正在进行中，请刷新状态或不能删除！', 'warning');
		return;
	}
	JSPFree.doBillListDelete(d1_2_BillList)
}

/**
 * 表任务：启动任务
 * @returns
 */
function tabStart () {
	var selectData = d1_1_BillList.datagrid('getSelected');
	if (selectData == null) {
		$.messager.alert('提示', '必须选择一条记录进行操作', 'warning');
		return;
	}

	// 判断，如果有进行中的任务，则不能点击启动
	var jsy_data = JSPFree.doClassMethodCall("com.yusys.cr.engineTask.service.CrEngieTaskBS","getTaskStatusIsInConduct", {});
	if (jsy_data.msg == "OK") {
		$.messager.alert('提示', '当前存在任务正在进行校验，请勿重复操作！', 'warning');
		return;
	}
	
	var jso_par = {
		task_id : selectData.task_id,
		busi_type : selectData.busi_type,
		busi_org_no : selectData.busi_org_no,
		report_type : selectData.report_type,
		table_names : selectData.tab_names
	};
	
	var jso_data = JSPFree.doClassMethodCall("com.yusys.cr.engineTask.service.CrEngieTaskBS", "startTask", jso_par);
	if (jso_data.errorCode == '0000') {
		$.messager.alert('提示', '已启动后台校验任务，可通过【查看日志】按钮了解任务执行情况.', 'warning');
	} else {
		$.messager.alert('提示', '任务执行失败，失败原因：' + jso_data.message, 'error');
	}
}

/**
 * 规则任务：启动任务
 * @returns
 */
function ruleStart() {
	var selectData = d1_2_BillList.datagrid('getSelected');
	if (selectData == null) {
		$.messager.alert('提示', '必须选择一条记录进行操作', 'warning');
		return;
	}
	
	// 判断，如果有进行中的任务，则不能点击启动
	var jsy_data = JSPFree.doClassMethodCall("com.yusys.cr.engineTask.service.CrEngieTaskBS","getTaskStatusIsInConduct", {});
	if (jsy_data.msg == "OK") {
		$.messager.alert('提示', '当前存在任务正在进行校验，请勿重复操作！', 'warning');
		return;
	}
	
	var jso_par = {
		task_id : selectData.task_id,
		busi_type : selectData.busi_type,
		busi_org_no : selectData.busi_org_no,
		report_type : selectData.report_type,
		rule_ids : selectData.rule_ids
	};
	
	var jso_data = JSPFree.doClassMethodCall("com.yusys.cr.engineTask.service.CrEngieTaskBS", "startTask", jso_par);
	if (jso_data.errorCode=='0000') {
		$.messager.alert('提示', '已启动后台校验任务，可通过【查看日志】按钮了解任务执行情况.', 'warning');
	} else {
		$.messager.alert('提示', '任务执行失败，失败原因：'+jso_data.message, 'error');
	}
}

/**
 * 表任务：查看日志
 * @returns
 */
function tabLog() {
	var selectData = d1_1_BillList.datagrid('getSelected');
	if (selectData == null) {
		$.messager.alert('提示', '必须选择一条记录进行操作', 'warning');
		return;
	}
	
	var jso_OpenPars = {task_id: selectData.task_id};
	JSPFree.openDialog("日志", "/yujs/cr/engineTask/cr_engine_task_log.js", 900, 600, jso_OpenPars, function(_rtdata) {});
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
	
	var jso_OpenPars = {task_id: selectData.task_id};
	JSPFree.openDialog("日志", "/yujs/cr/engineTask/cr_engine_task_log.js", 900, 600, jso_OpenPars, function(_rtdata) {});
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
