//初始化界面
function AfterInit() {
	JSPFree.createTabb("d1", [ "规则任务", "表任务" ]); // 创建多页签

	JSPFree.createBillList("d1_1", "/biapp-crrs/freexml/crrs/engine/crrs_engine_task_CODE1.xml",null,{isSwitchQuery:"N"}); // 第1个页签
	JSPFree.createBillList("d1_2", "/biapp-crrs/freexml/crrs/engine/crrs_engine_task_CODE2.xml",null,{isSwitchQuery:"N"}); // 第2个页签
}


//规则任务--批量新增
function batchrulescreate(){
JSPFree.openDialog("选择来源日期的任务", "/yujs/crrs/engine/ruletaskdialog.js", 900, 500,{}, function(_rtdata) {
	if(typeof _rtdata != "undefined" && _rtdata!=null){  //的确有返回值!
		JSPFree.queryDataByConditon(d1_1_BillList, "task_id in (" + _rtdata.newids + ")");
		JSPFree.alert(_rtdata.msg + "<br>当前界面就是重新查询刚刚新复制的任务!");
	}
});
}


//表任务--批量新增
function batchtabscreate(){
	JSPFree.openDialog("选择来源日期的任务", "/yujs/crrs/engine/tabtaskdialog.js", 900, 500,{}, function(_rtdata) {
		if(typeof _rtdata != "undefined" && _rtdata!=null){  //的确有返回值!
		  JSPFree.queryDataByConditon(d1_2_BillList, "task_id in (" + _rtdata.newids + ")");
		  JSPFree.alert(_rtdata.msg + "<br>当前界面就是重新查询刚刚新复制的任务!");
	    }
	});
}


function start() {
	var selectData = d1_1_BillList.datagrid('getSelected');
	if (selectData == null) {
		$.messager.alert('提示', '必须选择一条记录进行操作', 'warning');
		return;
	}
	if (selectData.status == "ok") {
		$.messager.alert('提示', '当前任务已经启动！', 'warning');
		return;
	}
	var jso_par = {
		task_id : selectData.task_id
	};
	jso_par.rule_ids = selectData.rule_ids;
	var jso_data = JSPFree.doClassMethodCall("com.yusys.crrs.engine.service.CrrsEngineTaskBS","startTask", jso_par);
	if(jso_data.errorCode=='0000'){
		$.messager.alert('提示', '任务执行成功', 'warning');
	}else{
		$.messager.alert('提示', '任务执行失败，失败原因：'+jso_data.message, 'error');
	}
}

function log() {
	var selectData = d1_1_BillList.datagrid('getSelected');
	if (selectData == null) {
		$.messager.alert('提示', '必须选择一条记录进行操作', 'warning');
		return;
	}
	var jso_OpenPars = {};
	jso_OpenPars.task_id = selectData.task_id;
	JSPFree.openDialog("日志", "/yujs/crrs/engine/viewtasklog.js", 900, 600,
			jso_OpenPars, function(_rtdata) {

			});
}


function start2() {
	var selectData = d1_2_BillList.datagrid('getSelected');
	if (selectData == null) {
		$.messager.alert('提示', '必须选择一条记录进行操作', 'warning');
		return;
	}
	if (selectData.status == "ok") {
		$.messager.alert('提示', '当前任务已经启动！', 'warning');
		return;
	}
	var jso_par = {
		task_id : selectData.task_id
	};
	jso_par.table_ids = selectData.tab_names;
	var jso_data = JSPFree.doClassMethodCall("com.yusys.crrs.engine.service.CrrsEngineTaskBS","startTask", jso_par);
	
	if(jso_data.errorCode=='0000'){
		$.messager.alert('提示', '任务执行成功', 'warning');
	}else{
		$.messager.alert('提示', '任务执行失败，失败原因：'+jso_data.message, 'error');
	}
}

function log2() {
	var selectData = d1_2_BillList.datagrid('getSelected');
	if (selectData == null) {
		$.messager.alert('提示', '必须选择一条记录进行操作', 'warning');
		return;
	}
	var jso_OpenPars = {};
	jso_OpenPars.task_id = selectData.task_id;
	JSPFree.openDialog("日志", "/yujs/crrs/engine/viewtasklog.js", 900, 600,
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
