/**
 * 
 * <pre>
 * Title: 【引擎管理】
 * Description:【检核任务】
 * </pre>
 * @author wangxy31 
 * @version 1.00.00
   @date 2021年10月21日
 */

var org_no = "";
var org_class = "";
var condition = " 1=1 ";
function AfterInit() {
	JSPFree.createBillList("d1", "/biapp-crrs/freexml/crrs/engineTask/crrs_engine_task_CODE1.xml", null, {isSwitchQuery:"N"}); // 第1个页签
	JSPFree.queryDataByConditon2(d1_BillList, getCondition());
	JSPFree.setBillListForceSQLWhere(d1_BillList, getCondition());
	
	org_no = CrrsFreeUtil.getOrgNo(str_LoginUserOrgNo);
	org_class = CrrsFreeUtil.getOrgClass(org_no);
}

/**
 * 增加查询条件
 * @returns
 */
function getCondition() {
	// 获取当前用户查询权限  admin查询所有，其他条线只能查看自己创建的数据
	var whereSql = '';
	var jsn_result = JSPFree.doClassMethodCall("com.yusys.crrs.engineTask.service.CrrsEngineTaskBS", "getListByCreateBy", {field: 'busi_org_no'});
	if (jsn_result.code == 'success') {
		whereSql = jsn_result.data;
	}
	condition = condition + " and " + whereSql + " and edition_type = '2'";
	
	return condition;
}

/**
 * 新增
 * @return {[type]} [description]
 */
function tabCreate() {
	JSPFree.openDialog("引擎任务","/yujs/crrs/engineTask/crrs_engine_task_add.js", 700, 520, {org_no: org_no, org_class: org_class}, function(_rtdata){
		if (_rtdata == true) {
			JSPFree.queryDataByConditon(d1_BillList, null);  // 立即查询刷新数据
		}
	});
}

/**
 * 编辑
 * @return {[type]} [description]
 */
function tabUpdate() {
	var selectData = d1_BillList.datagrid('getSelections');
	if (selectData == 0) {
		$.messager.alert('提示', '必须选择一条记录进行操作', 'warning');
		
		return;
	}
	
	if (selectData.length > 1) {
		$.messager.alert('提示', '只能选择一条记录进行操作', 'warning');
		
		return;
	}
	
	var jso_par = {task_id : selectData[0].task_id};
	var jso_status = JSPFree.doClassMethodCall("com.yusys.crrs.engineTask.service.CrrsEngineTaskBS", "getTaskStatus", jso_par);
	if (jso_status.code == -999) {
		$.messager.alert('提示', '当前表任务正在进行中，请刷新状态或不能编辑！', 'warning');
		
		return;
	}
	
	JSPFree.openDialog("引擎任务", "/yujs/crrs/engineTask/crrs_engine_task_edit.js", 700, 520, {task_id:selectData[0].task_id,org_no: org_no, org_class: org_class}, function(_rtdata){
		if (_rtdata == true) {
			JSPFree.queryDataByConditon(d1_BillList, null);  // 立即查询刷新数据
		}
	});
}

/**
 * 删除
 * @return {[type]} [description]
 */
function tabDelete() {
	var selectData = d1_BillList.datagrid('getSelections');
	if (selectData == 0) {
		$.messager.alert('提示', '必须选择一条记录进行操作', 'warning');
		
		return;
	}
	
	if (selectData.length > 1) {
		$.messager.alert('提示', '只能选择一条记录进行操作', 'warning');
		
		return;
	}
	
	var jso_par = {task_id : selectData[0].task_id};
	var jso_status = JSPFree.doClassMethodCall("com.yusys.crrs.engineTask.service.CrrsEngineTaskBS", "getTaskStatus", jso_par);
	if (jso_status.code == -999) {
		$.messager.alert('提示', '当前表任务正在进行中，请刷新状态或不能删除！', 'warning');
		
		return;
	}
	
	JSPFree.doBillListBatchDelete(d1_BillList)
}

/**
 * 启动任务
 * @returns
 */
function tabStart () {
	var selectDatas = d1_BillList.datagrid('getSelections');
	if (selectDatas == null || selectDatas == undefined || selectDatas.length == 0) {
		$.messager.alert('提示', '至少选择一条记录进行操作', 'warning');
		return;
	}
	
	var jso_rids = new Array();
	for (var i=0; i<selectDatas.length; i++) {
		jso_rids.push(selectDatas[i].task_id);
	}
	
	var jsy_data = JSPFree.doClassMethodCall("com.yusys.crrs.engineTask.service.CrrsEngineTaskBS","getTaskStatusIsInConductByTaskIds", {allTaskIds: jso_rids});
	if (jsy_data.msg == "OK") {
		$.messager.alert('提示', '所选任务中存在进行或排队中的任务!', 'warning');
		return;
	}
	
	var jso_data = JSPFree.doClassMethodCall("com.yusys.crrs.engineTask.service.CrrsEngineTaskBS", "startAllTask", {allTaskIds: jso_rids});
	if (jso_data.errorCode == '0000') {
		tabRefresh();
		JSPFree.openDialog2("任务监控","/yujs/crrs/engineTask/crrs_engine_task_start.js",1000, 600,null);
	} else {
		$.messager.alert('提示', '任务执行失败，失败原因：' + jso_data.message, 'error');
	}
}

/**
 * 任务监控
 */
function showStatus() {
	JSPFree.openDialog2("任务监控","/yujs/crrs/engineTask/crrs_engine_task_start.js",1000, 600,null);
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
	var jso_status = JSPFree.doClassMethodCall("com.yusys.crrs.engineTask.service.CrrsEngineTaskBS", "getBreakTaskStatus", jso_par);
	if (jso_status.code != -999) {
		$.messager.alert('提示', '当前任务不在进行中，无需中断！', 'warning');
		return;
	}
	JSPFree.confirm('提示', '是否中断任务?', function (_isOK) {
		if (_isOK) {
			var _rt = JSPFree.doClassMethodCall("com.yusys.crrs.engineTask.service.CrrsEngineTaskBS", "forceCloseEngineTask", {task_id: selectData.task_id});
			if (_rt.status == "OK") {
				if (selectData.task_type == "表任务"){
					JSPFree.refreshBillListCurrRow(d1_BillList); // 刷新当前行
				}
				
				$.messager.alert('提示', '任务中断成功！', 'warning');
			}
		}
	});
}

/**
 * 中断任务
 */
function tabForceClose() {
	var selectDatas = d1_BillList.datagrid('getSelections');
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
	var jsy_data = JSPFree.doClassMethodCall("com.yusys.crrs.engineTask.service.CrrsEngineTaskBS","getQueueTaskStatus", {allTaskIds: jso_rids});
	if (jsy_data.code == "success") {
		$.messager.alert('提示', '只能选择排队中的任务,或任务已经启动引擎还未返回状态,请稍后!', 'warning');
		return;
	}

	var jso_data = JSPFree.doClassMethodCall("com.yusys.crrs.engineTask.service.CrrsEngineTaskBS", "removeQueue", {allTaskIds: jso_rids});
	if (jso_data.code == 'success') {
		$.messager.alert('提示', '取消排队任务成功！', 'warning');
		JSPFree.queryDataByConditon2(d1_BillList);
		JSPFree.queryDataByConditon2(d1_2_BillList);
	} else {
		$.messager.alert('提示', jso_data.msg, 'warning');
	}
}

/**
 * 取消排队
 */
function tabDequeue() {
	var selectDatas = d1_BillList.datagrid('getSelections');
	dequeue(selectDatas);
}

/**
 * 查看日志
 * @returns
 */
function tabLog() {
	var selectData = d1_BillList.datagrid('getSelections');
	if (selectData == null) {
		$.messager.alert('提示', '必须选择一条记录进行操作', 'warning');
		
		return;
	}
	
	if (selectData.length > 1) {
		$.messager.alert('提示', '只能选择一条记录进行操作', 'warning');
		return;
	}
	
	var jso_OpenPars = {task_id: selectData[0].task_id,tabNames:selectData[0].tab_names,dataDt:selectData[0].data_dt,status:selectData[0].status};
	JSPFree.openDialog("日志", "/yujs/crrs/engineTask/crrs_engine_task_log.js", 900, 600, jso_OpenPars, function(_rtdata) {});
}

/**
 * 表任务：刷新
 * @returns
 */
function tabRefresh() {
	JSPFree.queryDataByConditon2(d1_BillList);
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

/**
 * 首次校验完成后，请同步检核结果到历史表中
 * 注：
 * ① 当一次检核完成后，点击某一行的任务，进行历史结果数据迁移备份
 * ② 点击前判断，是否是首次数据备份
 * ③ 非首次请提示，是否确认同步：是-同步，否-不同步
 * ④ 同步涉及的表有三张：crrs_result_sure，crrs_result_consistency，crrs_result_warn
*/
function tabResultPush(_btn) {
	$.messager.alert('提示', '代码开发中', 'info');
}
