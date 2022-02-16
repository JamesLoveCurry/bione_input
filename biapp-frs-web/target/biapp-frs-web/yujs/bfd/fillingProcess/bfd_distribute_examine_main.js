/**
 * 
 * <pre>
 * Title: 【报表处理】-【报表审核】
 * Description: 报表处理-报表审核：主页面
 * 此页面提供了报表处理的审核管理功能，可以允许用户单条或批量审核辖内机构的填报数据，并查看处理日志
 * </pre>
 * @author liangzy5 
 * @version 1.00.00
   @date 2020年8月18日
 */

var org_no = "";
function AfterInit(){
	//通过当前登录人所属内部机构获取报送机构号
	var jso_report_org = JSPFree.doClassMethodCall("com.yusys.bfd.common.service.BfdValidateQueryConditionBS","getReportOrgNoCondition",{_loginUserOrgNo:str_LoginUserOrgNo});
	if(jso_report_org.msg=="ok"){
		org_no = jso_report_org.data;
	}
	var jsoIsLine = JSPFree.doClassMethodCall("com.yusys.bfd.process.service.BfdCrProcessBSDMO", "getIsLineNo", {} );
	var templeteCode;
	if (jsoIsLine.msg == "OK") {
		templeteCode = "/biapp-bfd/freexml/fillingProcess/bfd_filling_process_child_CODE6.xml";
	} else {
		templeteCode = "/biapp-bfd/freexml/fillingProcess/bfd_filling_process_child_CODE3.xml";
	}
	JSPFree.createBillList("d1",templeteCode,null,
			{isSwitchQuery:"N",querycontion:"status='1'",autocondition:"status='1'",autoquery:"N",
			list_btns:"[icon-p41]审核/examine;[icon-ok1]批量审核/batchExamine;[icon-p80]处理日志/processReasonDetail;",
			orderbys:"data_dt,tab_name_en"});
	
	JSPFree.queryDataByConditon2(d1_BillList, getQueryCondition());
	JSPFree.setBillListForceSQLWhere(d1_BillList,getQueryCondition());
}

/**
 * 获取辖内机构号的过滤条件，作为sql的一部分
 * @returns
 */
function getQueryCondition(){
	var condition = "";
	var jso_rt = JSPFree.doClassMethodCall("com.yusys.bfd.process.service.BfdCrProcessBSDMO","getQueryConditionByUser",{"_loginUserOrgNo" : str_LoginUserOrgNo});
	
	if(jso_rt.msg == "ok"){
		condition = jso_rt.condition;
	}
	return condition;
}

/**
 * 审核:单条审核操作
 * 选择一条主任务记录，点击审核按钮，对主任务下面的所有子任务填报情况进行审核
 * 判断是否是主任务下所有任务都完成，如果都完成，则更新主任务状态为完成
 * @returns
 */
function examine(){
	var json_data = d1_BillList.datagrid('getSelections');
	if (json_data == null || json_data == undefined || json_data.length == 0) {
		$.messager.alert('提示', '必须选择一条记录进行操作', 'warning');
		return;
	}
	
	if (json_data.length>1) {
		$.messager.alert('提示', '只能选择一条记录进行操作', 'warning');
		return;
	}

	//判断当前数据是否被锁定
	var lock_data = JSPFree.doClassMethodCall("com.yusys.bfd.process.service.BfdLockData", "getStatus", {tab_name:json_data[0].tab_name,data_dt:json_data[0].data_dt,org_no:json_data[0].rpt_org_no,type:'3'});
	if(lock_data.status == "锁定"){
		$.messager.alert('提示', '当前日期的表数据已被锁定，无法操作！', 'warning');
		return;
	}
	
	var jso_Pars = {data:json_data[0]};
	
	JSPFree.openDialog("审核","/yujs/bfd/fillingProcess/bfd_distribute_examine_edit.js",900,600,jso_Pars,function(_rtData){
		if (_rtData == "通过") {
			// 从界面上删除行
			var p = d1_BillList.datagrid('getPager');
			$(p).pagination('select');
			$.messager.alert('提示', '任务审核通过!', 'info');
		} else if (_rtData == "退回") {
			// 从界面上删除行
			var p = d1_BillList.datagrid('getPager');
			$(p).pagination('select');
			$.messager.alert('提示', '任务审核退回!', 'info');
		}
		
		JSPFree.queryDataByConditon(d1_BillList);
	});
}

/**
 * 审核:批量审核操作
 * 选择一条或多条主任务记录，点击审核按钮，对主任务下面的所有子任务填报情况进行审核
 * 判断是否是主任务下所有任务都完成，如果都完成，则更新主任务状态为完成
 * @returns
 */
function batchExamine(){
	var json_data = d1_BillList.datagrid('getSelections');
	if (json_data == null || json_data == undefined || json_data.length == 0) {
		$.messager.alert('提示', '必须选择一条记录进行操作', 'warning');
		return;
	}

	//判断当前数据是否被锁定
	for (var i=0;i<json_data.length;i++) {
		var lock_data = JSPFree.doClassMethodCall("com.yusys.bfd.process.service.BfdLockData", "getStatus", {tab_name:json_data[i].tab_name,data_dt:json_data[i].data_dt,org_no:json_data[i].rpt_org_no,type:'3'});
		if(lock_data.status == "锁定"){
			$.messager.alert('提示', '当前日期的表数据已被锁定，无法操作！', 'warning');
			return;
		}
	}
	
	var jso_allrids=[]; 
	for (var i=0;i<json_data.length;i++) {
		jso_allrids.push(json_data[i].rid);
	}

	// 修改数据状态：3：完成
	var jso_Pars = {allrids:jso_allrids,status:'3',type:'3',reason:"通过",userNo:str_LoginUserCode};
	var jsn_result = JSPFree.doClassMethodCall("com.yusys.bfd.process.service.BfdCrProcessBSDMO", "updateDataByTaskByRids3", jso_Pars);
	if (jsn_result.msg == 'OK') {
		$.messager.alert('提示', '批量任务审核成功!', 'info');
		JSPFree.queryDataByConditon(d1_BillList);
	}
}

/**
 * 查看处理日志操作
 * 选择一条任务记录，点击查看处理日志按钮，查看这条任务记录的日志信息
 * @returns
 */
function processReasonDetail(){
	var json_data = d1_BillList.datagrid('getSelections');
	if (json_data == null || json_data == undefined || json_data.length == 0) {
		$.messager.alert('提示', '必须选择一条记录进行操作', 'warning');
		return;
	}
	if (json_data.length>1) {
		$.messager.alert('提示', '只能选择一条记录进行操作', 'warning');
		return;
	}

	var jso_par = {child_task_id:json_data[0].rid};
	JSPFree.openDialog("处理日志信息","/yujs/bfd/fillingProcess/bfd_distribute_process_detail.js",700,400,jso_par,function(_rtData){
		if (_rtData != null) {
			if ("dirclose" == _rtData.type) {
				return;
			}
		}
	});
}
