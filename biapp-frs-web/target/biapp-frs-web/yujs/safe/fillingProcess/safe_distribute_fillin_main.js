/**
 * 
 * <pre>
 * Title: 【报表处理】-【报表填报】
 * Description: 报表填报：主页面
 * </pre>
 * @author wangxy31 
 * @version 1.00.00
   @date 2020年6月22日
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
	// 获取【填报流程任务子表】常量类
	tab_name = SafeFreeUtil.getTableNames().SAFE_FILLING_PROCESS_CHILD;
	// 获取英文表名
	var jso_data = JSPFree.doClassMethodCall(
			"com.yusys.safe.base.common.service.SafeCommonBS",
			"getTabNameByEn", {tab_name:tab_name, report_type:str_subfix});
	tab_name_en = jso_data.tab_name_en;
	var orgClass = getOrgClass();
	var buttons = "[icon-p41]填报/created;[icon-p77]处理日志/processReasonDetail";
	if ("总行" != orgClass) {
		buttons = "[icon-p90]校验/validateTask;[icon-p77]校验日志/onEngineLog;" + buttons;
	}
	str_className = "Class:com.yusys.safe.base.common.template.CommonBuilderTemplate.getTemplet('" + tab_name + "','" + tab_name_en + "','" + str_subfix + "','" + str_LoginUserOrgNo + "')";
	JSPFree.createBillList("d1", str_className, null, {list_btns:buttons,isSwitchQuery:"N", autoquery:"N", orderbys:"data_dt,tab_name_en"});
	JSPFree.queryDataByConditon2(d1_BillList, "status in ('"+SafeFreeUtil.getProcessChildStatus().DOWN+"', '"+SafeFreeUtil.getProcessChildStatus().RETURN+"')");
	JSPFree.setBillListForceSQLWhere(d1_BillList, "status in ('"+SafeFreeUtil.getProcessChildStatus().DOWN+"', '"+SafeFreeUtil.getProcessChildStatus().RETURN+"')");
}

function getOrgClass() {
	var orgClassVO = JSPFree.getHashVOs("select trim(ORG_CLASS) as org_class from rpt_org_info where ORG_TYPE = 'BOP' and MGR_ORG_NO = '" + str_LoginUserOrgNo + "'");
	if (orgClassVO && orgClassVO.length > 0) {
		var orgClass = orgClassVO[0].org_class;
		return orgClass;
	}
	return "";
}
/**
 * 校验
 * @returns
 */
function validateTask() {
	var pars = {reportType: str_subfix};
	JSPFree.openDialog("选择日期", "/yujs/safe/reportCheck/report_check_date.js", 700, 560, pars, function (_rtdata) {
		if (_rtdata != null && _rtdata.code == "success") {
			$.messager.show({title: '消息提示', msg: '后台校验中，请稍后...', showType: 'show'});
			JSPFree.queryDataByConditon(d1_BillList, null);  // 立即查询刷新数据
		}
	});
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
 * 填报
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
	
	//20200727 修复%传参问题 wangxy31
	var _tab_name = json_data[0].tab_name
    if (_tab_name.indexOf('%') != 0) {
    	_tab_name = _tab_name.replace(/%/, '%25');
       }
	var jso_Pars = {taskId:json_data[0].rid,tabNameEn:json_data[0].tab_name_en,tabName:_tab_name,
			dataDt:json_data[0].data_dt,report_type:str_subfix};
	
	JSPFree.openDialog("填报","/yujs/safe/fillingProcess/safe_distribute_detail_fillin1.js",1000,600,jso_Pars,function(_rtData){
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
