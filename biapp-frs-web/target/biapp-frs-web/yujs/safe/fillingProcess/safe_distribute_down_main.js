/**
 * 
 * <pre>
 * Title: 【报表处理】-【报表下发】
 * Description: 报表下发：主页面
 * </pre>
 * @author wangxy31 
 * @version 1.00.00
   @date 2020年6月17日
 */

var tab_name = "";
var tab_name_en = "";
var str_subfix = "";
var str_className = "";

function AfterInit() {	
	// 获取路径参数
	if (jso_OpenPars != '') {
		if(jso_OpenPars.type != null) {
			str_subfix = jso_OpenPars.type;
		}
	}
	// 获取【填报流程任务主表】常量类
	tab_name = SafeFreeUtil.getTableNames().SAFE_FILLING_PROCESS;
	// 获取英文表名
	var jso_data = JSPFree.doClassMethodCall(
			"com.yusys.safe.base.common.service.SafeCommonBS",
			"getTabNameByEn", {tab_name:tab_name, report_type:str_subfix});
	tab_name_en = jso_data.tab_name_en;
	
	str_className = "Class:com.yusys.safe.base.common.template.CommonBuilderTemplate.getTemplet('" + tab_name + "','" + tab_name_en + "','" + str_subfix + "','" + str_LoginUserOrgNo + "')";
	JSPFree.createBillList("d1", str_className, null, {list_btns:"[icon-p99]人工创建/insertTask;[icon-p79]编辑/updateTask;[icon-remove]删除/deleteTask;[icon-p87]人工下发/distributeTask;[icon-reset2]撤回/withdrawTask;[icon-p51]子任务信息/childTask;[icon-p20]解锁/onUnLock;",isSwitchQuery:"N",list_ischeckstyle:"Y", list_ismultisel:"Y", orderbys:"data_dt,tab_name_en"});
}

/**
 * 校验
 * @returns
 */
function validateTask(){
	var pars = { reportType:str_subfix };
    JSPFree.openDialog("选择日期","/yujs/safe/reportCheck/report_check_date.js",700,560,pars,function(_rtdata){
        if (_rtdata != null && _rtdata.code == "success") {
            $.messager.show({title:'消息提示',msg: '后台校验中，请稍后...',showType:'show'});
            JSPFree.queryDataByConditon(d1_BillList,null);  // 立即查询刷新数据
        }
    });
}

/**
 * 创建
 * @returns
 */
function insertTask(){
	var jso_Pars = {className:str_className,subfix:str_subfix};
	JSPFree.openDialog("新增","/yujs/safe/fillingProcess/safe_distribute_main_insert.js",700,560,jso_Pars,function(_rtData){
		if (_rtData != null) {
			if ("dirclose" == _rtData.type) {
				return;
			}
			if (_rtData.msg == "OK") {
				var str_sql = _rtData.whereSQL;  // 返回的主键拼成的SQL
				JSPFree.queryDataByConditon(d1_BillList, str_sql);
				if (_rtData.tabName) {
					$.messager.alert('提示', '数据表不需要创建下发任务 【' + _rtData.tabName +"】", 'warning');
				} else {
					JSPFree.alert("新增数据成功!<br>当前页面数据是查询的新增数据!");
				}
			} else {
				if (_rtData.tabName) {
					$.messager.alert('提示', '数据表不需要创建下发任务 【' + _rtData.tabName +"】", 'warning');
				} else {
					JSPFree.alert("新增数据失败!<br>当前数据已存在/或者明细数据不存在!");
				}
			}
		}
	});
 }

/**
 * 编辑
 * @returns
 */
function updateTask(){
	// 选择选中的数据..
	var json_data = d1_BillList.datagrid('getSelections');
	if (json_data == null || json_data == undefined || json_data.length == 0) {
		$.messager.alert('提示', '必须选择一条记录进行操作', 'warning');
		return;
	}
	if (json_data.length>1) {
		$.messager.alert('提示', '只能选择一条记录进行操作', 'warning');
		return;
	}
	
	// 根据状态判断，如果是1,3状态，则详细页面置灰，不可编辑下发说明
	var jso_Pars = {rid:json_data[0].rid,status:json_data[0].status,className:str_className,subfix:str_subfix};
	JSPFree.openDialog("编辑","/yujs/safe/fillingProcess/safe_distribute_main_edit.js",700,560,jso_Pars,function(_rtData){
		if (_rtData == true) {
			JSPFree.refreshBillListCurrRow(d1_BillList);
		}
	});
}

/**
 * 删除下发任务
 * @returns
 */
function deleteTask(){
	var json_data = d1_BillList.datagrid('getSelections');
	if (json_data == null || json_data == undefined || json_data.length == 0) {
		$.messager.alert('提示', '必须至少选择一条记录进行操作', 'warning');
		return;
	}

	// 判断当前状态是否是处理中和完成状态
	for (var i=0;i<json_data.length;i++) {
		var status = json_data[i].status;
		if (status == SafeFreeUtil.getProcessStatus().PROCESSING || status == SafeFreeUtil.getProcessStatus().COMPLETE) {
			$.messager.alert('提示', '当前状态下，不能下进行删除操作！', 'warning');
			return;
		}
	}
	
	JSPFree.confirm('提示', '你真的要删除选中的记录吗?', function(_isOK){
		if (_isOK){
			var str_json = { jsonData:json_data }
			var jsn_result = JSPFree.doClassMethodCall("com.yusys.safe.fillingProcess.service.SafeFillingProcessBS", "deleteByTaskRid", str_json);
			if (jsn_result.msg == 'OK') {
				$.messager.alert('提示', '删除数据成功!', 'info');
				JSPFree.queryDataByConditon(d1_BillList);
			}
		}
	});
}

/**
 * 下发
 * @returns
 */
function distributeTask() {
	var json_data = d1_BillList.datagrid('getSelections');
	if (json_data == null || json_data == undefined || json_data.length == 0) {
		$.messager.alert('提示', '必须至少选择一条记录进行操作', 'warning');
		return;
	}
	
	for (var i=0; i<json_data.length; i++) {
		// 判断当前状态是否是下发和完成状态
		var status = json_data[i].status;
		if (status == SafeFreeUtil.getProcessStatus().PROCESSING || status == SafeFreeUtil.getProcessStatus().COMPLETE) {
			$.messager.alert('提示', '当前状态下，不能下进行下发操作！', 'warning');
			return;
		}

		// 判断当前状总数是否为0，如果为0，提示不能进行下发
		var totalSum = json_data[i].total_sum;
		if (totalSum === 0) {
			$.messager.alert('提示', '当前下发的任务中包含空的明细数据，请确认后再下发！', 'warning');
			return;
		}
	}
		
	var str_json = { jsonData:json_data }
	var jsn_result = JSPFree.doClassMethodCall("com.yusys.safe.fillingProcess.service.SafeFillingProcessBS", 
			"createChildTasks", str_json);
	if (jsn_result.msg == 'OK' && jsn_result.flag == false ) {
		$.messager.alert('提示', '任务下发成功!', 'info');
		JSPFree.queryDataByConditon(d1_BillList);
	} else {
		$.messager.alert('提示', '任务下发存在部分异常（异常原因可能为无明细数据）!', 'info');
	}
}

/**
 * 撤回
 * @returns
 */
function withdrawTask() {
	var json_data = d1_BillList.datagrid('getSelections');
	if (json_data == null || json_data == undefined || json_data.length == 0) {
		$.messager.alert('提示', '必须至少选择一条记录进行操作', 'warning');
		return;
	}
	
	for(var i=0; i<json_data.length; i++){
		// 只能对处理中数据进行撤回处理
		if (json_data[i].status != SafeFreeUtil.getProcessStatus().PROCESSING && json_data[i].status != SafeFreeUtil.getProcessStatus().UNLOCK) {
			$.messager.alert('提示', '只能对【处理中】或【解锁】数据进行撤回处理！', 'warning');
			return;
		}
	}
	
	var str_json = {jsonData:json_data}
	JSPFree.confirm('提示', '撤回操作会清空子任务数据，请确认是否撤回？', function(_isOK){
		if (_isOK){
			var jsn_result = JSPFree.doClassMethodCall("com.yusys.safe.fillingProcess.service.SafeFillingProcessBS", 
					"withdrawDataByTask", str_json);
			
			if (jsn_result.msg == 'OK') {
				$.messager.alert('提示', '任务撤回成功!', 'info');
				JSPFree.queryDataByConditon(d1_BillList);
			}
		}
	});
}

/**
 * 子任务信息
 * @returns
 */
function childTask() {
	var json_data = d1_BillList.datagrid('getSelections');
	if (json_data == null || json_data == undefined || json_data.length == 0) {
		$.messager.alert('提示', '必须选择一条记录进行操作', 'warning');
		return;
	}
	if (json_data.length > 1) {
		$.messager.alert('提示', '只能选择一条记录进行操作', 'warning');
		return;
	}
	
	// 只能是下发和完成，两种状态才可以查看该功能
 	var jso_Pars = {data:json_data[0]};
	if (json_data[0].status == SafeFreeUtil.getProcessStatus().PROCESSING || json_data[0].status == SafeFreeUtil.getProcessStatus().COMPLETE ||json_data[0].status == SafeFreeUtil.getProcessStatus().UNLOCK) {
		JSPFree.openDialog2("子任务信息","/yujs/safe/fillingProcess/safe_distribute_main_child.js",900,600,jso_Pars,function(_rtData){
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
 * 查看日志
 */
function onEngineLog() {
    JSPFree.openDialog("日志", "/yujs/safe/reportCheck/engine_log_view.js", 900, 600,
        {reportType:str_subfix}, function(_rtdata) {

        });
}


/**
 * 解锁任务
 */
function onUnLock() {
	var json_data = d1_BillList.datagrid('getSelections');
	if (json_data == null || json_data == undefined || json_data.length == 0) {
		$.messager.alert('提示', '必须至少选择一条记录进行操作', 'warning');
		return;
	}

	for(var i=0; i<json_data.length; i++){
		// 只能对【完成】数据进行解锁处理
		if (json_data[i].status != SafeFreeUtil.getProcessStatus().COMPLETE) {
			$.messager.alert('提示', '只能对【完成】数据进行解锁处理！', 'warning');
			return;
		}
	}

	var str_json = {jsonData:json_data}
	JSPFree.confirm('提示', '请确认是否解锁？', function(_isOK){
		if (_isOK){
			var jsn_result = JSPFree.doClassMethodCall("com.yusys.safe.fillingProcess.service.SafeFillingProcessBS",
				"unlockTaskByTaskRid", str_json);

			if (jsn_result.msg == 'OK') {
				$.messager.alert('提示', '任务解锁成功!', 'info');
				JSPFree.queryDataByConditon(d1_BillList);
			}
		}
	});

}