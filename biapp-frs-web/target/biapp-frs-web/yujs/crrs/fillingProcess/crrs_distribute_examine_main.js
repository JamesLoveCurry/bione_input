/**
 * 
 * <pre>
 * Title: 【报表处理】-【报表审核】
 * Description: 报表处理-报表审核：主页面
 * 此页面提供了报表处理的审核管理功能，可以允许用户单条或批量审核辖内机构的填报数据，并查看处理日志
 * </pre>
 * @author wangxy31 
 * @version 1.00.00
   @date 2020年8月18日
 */

var org_no = "";
function AfterInit(){
	org_no = CrrsFreeUtil.getOrgNo(str_LoginUserOrgNo);
	
	var condition = getQueryCondition() + " and status='1'";
	var templeteCode = "/biapp-crrs/freexml/crrs/fillingProcess/crrs_filling_process_child_CODE3.xml";
	
	JSPFree.createBillList("d1",templeteCode,null,
			{isSwitchQuery:"N",querycontion:condition,autocondition:condition,autoquery:"Y",
			list_btns:"[icon-p41]审核/examine;[icon-ok1]批量审核/batchExamine;[icon-p80]处理日志/processReasonDetail;",
			orderbys:"data_dt,tab_name_en"});
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
	var lock_data = JSPFree.doClassMethodCall("com.yusys.crrs.lock.service.CrrsLockBSDMO", "getStatus", {tab_name:json_data[0].tab_name,data_dt:json_data[0].data_dt});
	if(lock_data.status == "锁定"){
		$.messager.alert('提示', '当前日期的表数据已被锁定，无法操作！', 'warning');
		return;
	}

	// 表名传参需要将子表带过去
	var tabNameChilds = "1=2";
	var tabChildsData = JSPFree.doClassMethodCall("com.yusys.crrs.common.service.CrrsCommonBS", "getMainChildsTabEn", {tab_name: json_data[0].tab_name});
	if (tabChildsData.msg == "OK"){
		tabNameChilds = tabChildsData.tabChildsEn;
	}
	json_data[0].tab_name_en = tabNameChilds;
	var jso_Pars = {data:json_data[0],isFromModel:"2"};
	
	JSPFree.openDialog("审核","/yujs/crrs/fillingProcess/crrs_distribute_detail_fillin1.js",900,600,jso_Pars,function(_rtData){
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
		var lock_data = JSPFree.doClassMethodCall("com.yusys.crrs.lock.service.CrrsLockBSDMO", "getStatus", {tab_name:json_data[i].tab_name,data_dt:json_data[i].data_dt});
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
	var jsn_result = JSPFree.doClassMethodCall("com.yusys.crrs.process.service.CrrsCrProcessBSDMO", "updateDataByTaskByRids", jso_Pars);
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
	JSPFree.openDialog("处理日志信息","/yujs/crrs/fillingProcess/crrs_distribute_process_detail.js",700,400,jso_par,function(_rtData){
		if (_rtData != null) {
			if ("dirclose" == _rtData.type) {
				return;
			}
		}
	});
}
