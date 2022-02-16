/**
 * 
 * <pre>
 * Title: 【报表处理】-【报表填报】
 * Description: 报表处理-报表填报：主页面
 * 此页面提供了报表处理的填报管理功能，可以允许用户选择具体的子任务，点击填报
 * 列表展示子任务中的具体错误明细数据，选择一条错误明细数据，即可对数据进行填报
 * </pre>
 * @author wangxy31 
 * @version 1.00.00
   @date 2021年10月26日
 */

var org_no = "";
var maskUtil = "";
function AfterInit(){
	maskUtil = FreeUtil.getMaskUtil();
	org_no = CrrsFreeUtil.getOrgNo(str_LoginUserOrgNo);
	
	JSPFree.createBillList("d1","/biapp-crrs/freexml/crrs/fillingProcess/crrs_filling_process_child_CODE1.xml",null,{isSwitchQuery:"N",autoquery:"N",orderbys:"data_dt,tab_name_en"});
	
	JSPFree.queryDataByConditon2(d1_BillList, getQueryCondition()); //获取辖内机构的填报子任务
	JSPFree.setBillListForceSQLWhere(d1_BillList,getQueryCondition());
}

/**
 * 获取辖内机构号的过滤条件，作为sql的一部分
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
 * 填报按钮操作
 * 选择一条子任务，点击填报按钮，展示这条子任务对应的错误明细数据，并进行下一步操作
 * 只能查看到登录机构辖内机构的错误明细数据
 * @returns
 */
function created() {
	var json_data = d1_BillList.datagrid('getSelections');
	if (json_data == null || json_data == undefined || json_data.length == 0) {
		$.messager.alert('提示', '必须选择一条记录进行操作', 'warning');
		return;
	}
	if (json_data.length>1) {
		$.messager.alert('提示', '只能选择一条记录进行操作', 'warning');
		return;
	}
	// 判断当前数据是否被锁定
	for (var i=0;i<json_data.length;i++) {
		var lock_data = JSPFree.doClassMethodCall("com.yusys.crrs.lock.service.CrrsLockBSDMO", "getStatus", {data_dt: json_data[i].data_dt});
		
		if (lock_data.status == "锁定"){
			$.messager.alert('提示', '当前日期的表数据已被锁定，无法操作！', 'warning');
			return;
		}
	}
	
	// 表名传参需要将子表带过去
	var tabNameChilds = "1=2";
	var tabChildsData = JSPFree.doClassMethodCall("com.yusys.crrs.common.service.CrrsCommonBS", "getMainChildsTabEn", {tab_name: json_data[0].tab_name});
	if (tabChildsData.msg == "OK"){
		tabNameChilds = tabChildsData.tabChildsEn;
	}

	var jso_Pars = {taskId:json_data[0].rid,orgNo:json_data[0].org_no,tabNameEn:tabNameChilds,tabName:json_data[0].tab_name,tabCode:json_data[0].tab_code,dataDt:json_data[0].data_dt};

	JSPFree.openDialog("填报","/yujs/crrs/fillingProcess/crrs_distribute_detail_fillin1.js",1000,600,jso_Pars,function(_rtData){
		if (_rtData != null) {
			if ("dirclose" == _rtData.type) {
				return;
			}
		}
		
		if (_rtData == "提交") {
			$.messager.alert('提示', '任务提交成功!', 'info');
		} else if (_rtData == "强制提交") {
			$.messager.alert('提示', '任务强制提交成功!', 'info');
		}
		
		JSPFree.queryDataByConditon(d1_BillList);
	});
}

/**
 * 查看处理日志
 * 选择一条子任务记录，点击按钮，即可查看这条子任务的处理日志
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
