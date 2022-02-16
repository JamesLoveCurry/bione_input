var condition = '1=1';
var type="1";
//初始化界面
function AfterInit() {
	JSPFree.createTabb("d1", ["表任务","规则任务"]); // 创建多页签
	JSPFree.createBillList("d1_1", "east/engineTask/east_cr_task_CODE1",null,{isSwitchQuery:"N"}); // 第1个页签
	JSPFree.createBillList("d1_2", "east/engineTask/east_cr_task_CODE2",null,{isSwitchQuery:"N"}); // 第2个页签
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
	// 获取当前用户查询权限  admin查询所有，
	var whereSql = '';
	var jsn_result = JSPFree.doClassMethodCall("com.yusys.east.checkrule.detailmaintenance.service.EastCrRuleTaskBS", "getListByCreateBy", {field: 'busi_org_no'});
	if (jsn_result.code == 'success') {
		whereSql = jsn_result.data;
	}
	condition = condition + " and " + whereSql;

	return condition;
}
//表任务--复制任务
function batchtabscreate(){
	JSPFree.openDialog("选择来源日期的任务", "/yujs/east/engineTask/tabtaskdialog.js", 900, 500,{}, function(_rtdata) {
		if(typeof _rtdata != "undefined" && _rtdata!=null && _rtdata.type!="dirclose"){  //的确有返回值!
		  JSPFree.queryDataByConditon(d1_1_BillList, "task_id in (" + _rtdata.newids + ")");
		  JSPFree.alert(_rtdata.msg);
	    }
	});
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
	var jso_status = JSPFree.doClassMethodCall("com.yusys.east.checkrule.detailmaintenance.service.EastCrRuleTaskBS", "getBreakTaskStatus", jso_par);
	if (jso_status.code != -999) {
		$.messager.alert('提示', '当前任务不在进行中，无需中断！', 'warning');
		return;
	}
	JSPFree.confirm('提示', '是否中断任务?', function (_isOK) {
		if (_isOK) {
			var _rt = JSPFree.doClassMethodCall("com.yusys.east.checkrule.detailmaintenance.service.EastCrRuleTaskBS", "forceCloseEngineTask", {task_id: selectData.task_id});
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
/*
** 任务监控
 */
function showStatus() {
	JSPFree.openDialog2("任务监控","/yujs/east/engineTask/east_engine_task_start.js",1000, 600,null);
}

//规则任务--复制任务
function batchrulescreate(){
	JSPFree.openDialog("选择来源日期的任务", "/yujs/east/engineTask/ruletaskdialog.js", 900, 500,{}, function(_rtdata) {
		if(typeof _rtdata != "undefined" && _rtdata!=null){  //的确有返回值!
			JSPFree.queryDataByConditon(d1_2_BillList, "task_id in (" + _rtdata.newids + ")");
			JSPFree.alert(_rtdata.msg);
		}
	});
}

function start() {
	var selectData = d1_2_BillList.datagrid('getSelections');
	if (selectData == null) {
		$.messager.alert('提示', '必须选择一条记录进行操作', 'warning');
		return;
	}
	var jso_rids = new Array();
	for (var i=0; i<selectData.length; i++) {
		jso_rids.push(selectData[i].task_id);
	}
	var jsy_data = JSPFree.doClassMethodCall("com.yusys.east.checkrule.detailmaintenance.service.EastCrRuleTaskBS","getTaskStatusIsInConductByTaskIds", {allTaskIds: jso_rids});
	if (jsy_data.msg == "OK") {
		$.messager.alert('提示', '所选任务中存在进行或排队中的任务!', 'warning');
		return;
	}
	var result = JSPFree.doClassMethodCall("com.yusys.east.checkrule.detailmaintenance.service.EastCrRuleTaskBS","checkStart", {allTaskIds: jso_rids, type:'1', isRule: 'Y'});
	if (result.code == 'error') {
		$.messager.alert({
			title:'系统提示',
			msg:'<div  style="height:270px;overflow-x: hidden; overflow-y: auto;white-space:pre-wrap;">' + result.msg + '</div>',
			width:520,
			top: 150,
			icon:'info'
		});
		return;
	}
	var jso_data = JSPFree.doClassMethodCall("com.yusys.east.checkrule.detailmaintenance.service.EastCrRuleTaskBS", "startTaskAll", {allTaskIds: jso_rids});
	if (jso_data.errorCode == '0000') {
		refresh2();
		JSPFree.openDialog2("任务监控","/yujs/east/engineTask/east_engine_task_start.js",1000, 600,null);
	} else {
		$.messager.alert('提示', '任务执行失败，失败原因：' + jso_data.message, 'error');
	}
}

function log() {
	var selectData = d1_2_BillList.datagrid('getSelected');
	if (selectData == null) {
		$.messager.alert('提示', '必须选择一条记录进行操作', 'warning');
		return;
	}
	var jso_OpenPars = {};
	jso_OpenPars.task_id = selectData.task_id;
	JSPFree.openDialog("日志", "/yujs/east/engineTask/viewtasklog.js", 900, 600,
			jso_OpenPars, function(_rtdata) {

			});
}

function start2() {
	var selectData = d1_1_BillList.datagrid('getSelections');
	if (selectData == null) {
		$.messager.alert('提示', '必须选择一条记录进行操作', 'warning');
		return;
	}
	var jso_rids = new Array();
	for (var i=0; i<selectData.length; i++) {
		jso_rids.push(selectData[i].task_id);
	}
	var jsy_data = JSPFree.doClassMethodCall("com.yusys.east.checkrule.detailmaintenance.service.EastCrRuleTaskBS","getTaskStatusIsInConductByTaskIds", {allTaskIds: jso_rids});
	if (jsy_data.msg == "OK") {
		$.messager.alert('提示', '所选任务中存在进行或排队中的任务!', 'warning');
		return;
	}
	var result = JSPFree.doClassMethodCall("com.yusys.east.checkrule.detailmaintenance.service.EastCrRuleTaskBS","checkStart", {allTaskIds: jso_rids, type:'1'});
	if (result.code == 'error') {
		$.messager.alert({
			title:'系统提示',
			msg:'<div  style="height:270px;overflow-x: hidden; overflow-y: auto;white-space:pre-wrap;">' + result.msg + '</div>',
			width:520,
			top: 150,
			icon:'info'
		});
		return;
	}
	var jso_data = JSPFree.doClassMethodCall("com.yusys.east.checkrule.detailmaintenance.service.EastCrRuleTaskBS", "startTaskAll", {allTaskIds: jso_rids});
	if (jso_data.errorCode == '0000') {
		refresh1();
		JSPFree.openDialog2("任务监控","/yujs/east/engineTask/east_engine_task_start.js",1000, 600,null);
	} else {
		$.messager.alert('提示', '任务执行失败，失败原因：' + jso_data.message, 'error');
	}
}

function log2() {
	var selectData = d1_1_BillList.datagrid('getSelected');
	if (selectData == null) {
		$.messager.alert('提示', '必须选择一条记录进行操作', 'warning');
		return;
	}
	var jso_OpenPars = {};
	jso_OpenPars.task_id = selectData.task_id;
	JSPFree.openDialog("日志", "/yujs/east/engineTask/viewtasklog.js", 900, 600,
			jso_OpenPars, function(_rtdata) {

			});
}

//点击查看命令
function onLookCommand1(_btn){
  var str_cmd = _btn.dataset.itemvalue;
  FreeUtil.openHtmlMsgBox("查看命令模板",800,300,"<span style='font-size:20px;color:#9B0000'>" + str_cmd + "</span>");
}

//点击查看命令
function onLookCommand2(_btn){
  var str_cmd = _btn.dataset.itemvalue;
  FreeUtil.openHtmlMsgBox("查看实际命令",800,500,"<span style='font-size:14px;color:blue'>" + str_cmd + "</span>");
}

//表任务：刷新
function refresh1(){
	JSPFree.queryDataByConditon2(d1_1_BillList);
}
//规则任务：刷新
function refresh2(){
	JSPFree.queryDataByConditon2(d1_2_BillList);
}

/**
 * 新增
 * @return {[type]} [description]
 */
function create1(){
	JSPFree.openDialog("引擎任务","/yujs/east/engineTask/addTaskTab.js", 700, 520, {type: type},function(_rtdata){
		if (_rtdata == true) {
			JSPFree.queryDataByConditon(d1_1_BillList, null);  //立即查询刷新数据
		}
	});
}

/**
 * 新增
 * @return {[type]} [description]
 */
function create2(){
	JSPFree.openDialog("引擎任务","/yujs/east/engineTask/addTaskRule.js", 700, 520, {type: type},function(_rtdata){
		if (_rtdata == true) {
			JSPFree.queryDataByConditon(d1_2_BillList, null);  //立即查询刷新数据
		}
	});
}

/**
 * 编辑
 * @return {[type]} [description]
 */
function update1(){
	var selectData = d1_1_BillList.datagrid('getSelected');
	if (selectData == null) {
		$.messager.alert('提示', '必须选择一条记录进行操作', 'warning');
		return;
	}
	
	var jso_par = {task_id : selectData.task_id};
	var jso_status = JSPFree.doClassMethodCall("com.yusys.east.checkrule.detailmaintenance.service.EastCrRuleTaskBS","getTaskStatus", jso_par);
	
	if (jso_status.Code == -999) {
		$.messager.alert('提示', '当前表任务正在进行中，请刷新状态或不能编辑！', 'warning');
		return;
	}
	
	JSPFree.openDialog("引擎任务","/yujs/east/engineTask/editTaskTab.js", 700, 520, {task_id:selectData.task_id, type: type},function(_rtdata){
		if (_rtdata == true) {
			JSPFree.queryDataByConditon(d1_1_BillList, null);  //立即查询刷新数据
		}
	});
}

/**
 * 编辑
 * @return {[type]} [description]
 */
function update2(){
	var selectData = d1_2_BillList.datagrid('getSelected');
	if (selectData == null) {
		$.messager.alert('提示', '必须选择一条记录进行操作', 'warning');
		return;
	}
	
	var jso_par = {task_id : selectData.task_id};
	var jso_status = JSPFree.doClassMethodCall("com.yusys.east.checkrule.detailmaintenance.service.EastCrRuleTaskBS","getTaskStatus", jso_par);
	
	if (jso_status.Code == -999) {
		$.messager.alert('提示', '当前规则任务正在进行中，请刷新状态或不能编辑！', 'warning');
		return;
	}
	
	JSPFree.openDialog("引擎任务","/yujs/east/engineTask/editTaskRule.js", 700, 520, {task_id:selectData.task_id, type: type},function(_rtdata){
		if (_rtdata == true) {
			JSPFree.queryDataByConditon(d1_2_BillList, null);  //立即查询刷新数据
		}
	});
}

/**
 * 删除
 * @return {[type]} [description]
 */
function delete1(){
	var selectData = d1_1_BillList.datagrid('getSelected');
	if (selectData == null) {
		$.messager.alert('提示', '必须选择一条记录进行操作', 'warning');
		return;
	}
	
	var jso_par = {task_id : selectData.task_id};
	var jso_status = JSPFree.doClassMethodCall("com.yusys.east.checkrule.detailmaintenance.service.EastCrRuleTaskBS","getTaskStatus", jso_par);
	
	if (jso_status.Code == -999) {
		$.messager.alert('提示', '当前表任务正在进行中，请刷新状态或不能删除！', 'warning');
		return;
	}
	
	JSPFree.doBillListDelete(d1_1_BillList)
}

/**
 * 删除
 * @return {[type]} [description]
 */
function delete2(){
	var selectData = d1_2_BillList.datagrid('getSelected');
	if (selectData == null) {
		$.messager.alert('提示', '必须选择一条记录进行操作', 'warning');
		return;
	}
	
	var jso_par = {task_id : selectData.task_id};
	var jso_status = JSPFree.doClassMethodCall("com.yusys.east.checkrule.detailmaintenance.service.EastCrRuleTaskBS","getTaskStatus", jso_par);
	
	if (jso_status.Code == -999) {
		$.messager.alert('提示', '当前规则任务正在进行中，请刷新状态或不能删除！', 'warning');
		return;
	}
	
	JSPFree.doBillListDelete(d1_2_BillList)
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
	var jsy_data = JSPFree.doClassMethodCall("com.yusys.east.checkrule.detailmaintenance.service.EastCrRuleTaskBS","getQueueTaskStatus", {allTaskIds: jso_rids});
	if (jsy_data.code == "success") {
		$.messager.alert('提示', '只能选择排队中的任务,或任务已经启动引擎还未返回状态,请稍后!', 'warning');
		return;
	}

	var jso_data = JSPFree.doClassMethodCall("com.yusys.east.checkrule.detailmaintenance.service.EastCrRuleTaskBS", "removeQueue", {allTaskIds: jso_rids});
	if (jso_data.code == 'success') {
		$.messager.alert('提示', '取消排队任务成功！', 'warning');
		JSPFree.queryDataByConditon2(d1_1_BillList);
		JSPFree.queryDataByConditon2(d1_2_BillList);
	} else {
		$.messager.alert('提示', jso_data.msg, 'warning');
	}
}

/**
 * 表中断任务
 */
function tabDequeue() {
	var selectDatas = d1_1_BillList.datagrid('getSelections');
	dequeue(selectDatas);
}

/**
 * 规则中断任务
 */
function ruleDequeue() {
	var selectDatas = d1_2_BillList.datagrid('getSelections');
	dequeue(selectDatas);
}