/**
 * 
 * <pre>
 * Title: 【报表处理】-【报表复核】
 * Description: 报表复核：主页面
 * </pre>
 * @author wangxy31 
 * @version 1.00.00
   @date 2020年6月22日
 */

var tab_name = "";
var tab_name_en = "";
var str_subfix = "";
var str_className = "";

function AfterInit(){
	// 获取路径参数
	if (jso_OpenPars != '') {
		if(jso_OpenPars.type != null) {
			str_subfix = jso_OpenPars.type;
		}
	}
	// 获取【填报流程任务子表】常量类
	tab_name = SafeFreeUtil.getTableNames().SAFE_FILLING_PROCESS_CHILD;
	// 获取英文表名
	var jso_data = JSPFree.doClassMethodCall(
			"com.yusys.safe.base.common.service.SafeCommonBS",
			"getTabNameByEn", {tab_name:tab_name, report_type:str_subfix});
	tab_name_en = jso_data.tab_name_en;

	str_className = "Class:com.yusys.safe.base.common.template.CommonBuilderTemplate.getTemplet('" + tab_name + "','" + tab_name_en + "','" + str_subfix + "','" + str_LoginUserOrgNo + "')";
	JSPFree.createBillList("d1", str_className, null, {list_btns:"[icon-p41]复核/review;[icon-ok1]批量复核/batchReview;[icon-p77]处理日志/processReasonDetail;",isSwitchQuery:"N", list_ismultisel:"Y", autoquery:"N", list_ischeckstyle:"Y", orderbys:"data_dt,tab_name_en"});
	JSPFree.queryDataByConditon2(d1_BillList, "status = '"+SafeFreeUtil.getProcessChildStatus().REVIEWED+"'");
	JSPFree.setBillListForceSQLWhere(d1_BillList, "status = '"+SafeFreeUtil.getProcessChildStatus().REVIEWED+"'");
}

/**
 * 复核单条审核
 * 判断是否是主任务下所有任务都完成，如果都完成，则更新主任务状态为完成
 * @returns
 */
function review() {
	var json_data = d1_BillList.datagrid('getSelections');
	if (json_data == null || json_data == undefined || json_data.length == 0) {
		$.messager.alert('提示', '必须选择一条记录进行操作', 'warning');
		return;
	}
	
	if (json_data.length>1) {
		$.messager.alert('提示', '只能选择一条记录进行操作', 'warning');
		return;
	}
	
	var jso_Pars = {data:json_data[0],reportType:str_subfix};
	
	JSPFree.openDialog("复核","/yujs/safe/fillingProcess/safe_distribute_review_edit.js",900,600,jso_Pars,function(_rtData){
		if (_rtData == "通过") {
			$.messager.alert('提示', '任务复核通过!', 'info');
		} else if (_rtData == "退回") {
			$.messager.alert('提示', '任务复核退回!', 'info');
		}
		
		JSPFree.queryDataByConditon(d1_BillList);
	});
}

/**
 * 批量复核
 * @returns
 */
function batchReview() {
	var json_data = d1_BillList.datagrid('getSelections');
	if (json_data == null || json_data == undefined || json_data.length == 0) {
		$.messager.alert('提示', '必须选择一条记录进行操作', 'warning');
		return;
	}
	
	var taskIds = []; 
	for (var i=0;i<json_data.length;i++) {
		taskIds.push(json_data[i].rid);
	}

	var jso_Pars = {taskIds:taskIds,status:SafeFreeUtil.getProcessChildStatus().COMPLETE,logType:SafeFreeUtil.getFillingReasonType().APPROVED,reason:"复核通过",userNo:str_LoginUserCode,reportType:str_subfix};
	var jsn_result = JSPFree.doClassMethodCall("com.yusys.safe.fillingProcess.service.SafeFillingProcessBS", "updateDataByTaskByTaskid", jso_Pars);
	if (jsn_result.msg == 'OK') {
		$.messager.alert('提示', '批量任务复核成功!', 'info');
		JSPFree.queryDataByConditon(d1_BillList);
	}
}

/**
 * 查看处理日志
 * @returns
 */
function processReasonDetail() {
	var json_data = d1_BillList.datagrid('getSelections');
	if (json_data == null || json_data == undefined || json_data.length == 0) {
		$.messager.alert('提示', '必须选择一条记录进行操作', 'warning');
		return;
	}
	if (json_data.length>1) {
		$.messager.alert('提示', '只能选择一条记录进行操作', 'warning');
		return;
	}

	var jso_par = {child_task_id:json_data[0].rid,report_type:str_subfix};
	JSPFree.openDialog("处理日志信息","/yujs/safe/fillingProcess/safe_distribute_process_detail.js",900,600,jso_par,function(_rtData){
		if (_rtData != null) {
			if ("dirclose" == _rtData.type) {
				return;
			}
		}
	});
}