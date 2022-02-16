/**
 *
 * <pre>
 * Title:【配置管理】-【校验配置】
 * Description:校验配置：主页面
 * 在此界面对系统的校验进行配置，实现启用，停用，导入，导出等操作
 * </pre>
 * @author liangzy5
 * @version 1.00.00
 * @date 2020年6月18日
 */

var tab_name = "";
var tab_name_en = "";
var str_report_type = "";
var str_className = "";

function AfterInit() {
	// 获取"规则总表"
    tab_name = SafeFreeUtil.getTableNames().SAFE_CR_RULE;
    // 获取英文表名
    var jso_data = JSPFree.doClassMethodCall("com.yusys.safe.base.common.service.SafeCommonBS","getTabNameByEn", {tab_name:tab_name});
    tab_name_en = jso_data.tab_name_en;
	str_className = "Class:com.yusys.safe.tabmanage.template.RuleConfigBuilderTemplate.getTemplet('" + tab_name + "','" + tab_name_en + "')";
	JSPFree.createBillList("d1", str_className,null,{list_btns:"[icon-p68]导入/importRule;[icon-p69]导出/exportRule(this);[icon-remove]删除/onDelete;[icon-ok1]启用/enable(this);[icon-cancel]停用/disable(this);",isSwitchQuery:"N",list_ischeckstyle:"Y",list_ismultisel:"Y",orderbys:"id"});
	
	//20200618 一进去默认不做查询
	// 获取其中一个业务类型
    /*var jso_data = JSPFree.getHashVOs("SELECT item FROM rpt_std_code_info where rpt_type='SAFE' and code_type='REPORT_TYPE'");
    if(jso_data != null && jso_data.length > 0){
    	str_report_type = jso_data[0].item; //这里直接获取到"ACC"就可以了，不需要拼接"ACC/外汇账户"，下面的loadBillQueryData方法只认item_name
    }
    FreeUtil.loadBillQueryData(d1_BillList, {report_type:str_report_type});
    JSPFree.queryDataByConditon2(d1_BillList,"report_type ='"+str_report_type+"'");*/
}

/**
 * 【导入】
 * 点击按钮，上传excel模板文件，对校验规则进行导入
 * 
 * @returns
 */
function importRule() {
	var reportType = JSPFree.getBillQueryFormValue(d1_BillQuery).rule_busi_type;
	if(reportType=='' || typeof reportType == "undefined"){
		// $.messager.alert('提示', '请在查询框中选择一种业务类型后再导入', 'warning');
		// return;
		reportType = "";
	}

	JSPFree.confirm("提示","规则导入会增量添加系统中的校验规则，请谨慎操作，请确认是否执行导入操作！",function(_isOK){
		if(_isOK){JSPFree.openDialog("文件上传", "/yujs/safe/ruleConfig/safe_input_rule.js", 500, 450, {reportType:reportType},function(_rtdata) {
				if (_rtdata != null && _rtdata != undefined && _rtdata.type != "dirclose") { // 不是直接关闭窗口
					JSPFree.alert(_rtdata.msg);
					// 导入之后立即查询导入之前业务类型的数据
					FreeUtil.loadBillQueryData(d1_BillList, {rule_busi_type:reportType});
					JSPFree.queryDataByConditon2(d1_BillList, "rule_busi_type ='"+reportType+"'"); // 立即查询刷新数据
				}
			});
		}
	});
}

/**
 * 【导出】
 * 点击按钮，系统下载excel文件，对校验规则进行导出
 * 
 * @returns
 */
function exportRule(_btn) {
	var ruleId = JSPFree.getBillQueryFormValue(d1_BillQuery).id;
	var tabName = JSPFree.getBillQueryFormValue(d1_BillQuery).tab_name;
	var ruleName = JSPFree.getBillQueryFormValue(d1_BillQuery).rule_name;
	var reportType = JSPFree.getBillQueryFormValue(d1_BillQuery).rule_busi_type;
	
	if(reportType=='') {
		$.messager.alert('提示', '请在查询框中选择一种业务类型后再导出', 'warning');
		return;
	}
	
	var download = null;
	download = $('<iframe id="download" style="display: none;"/>');
	$('body').append(download);

	var src = v_context + "/safe/rule/export?ruleId=" + ruleId
			+ "&tabName=" + tabName + "&ruleName=" + ruleName + "&reportType=" + reportType;
	download.attr('src', src);
}

/**
 * 启用规则
 * 
 * @param _btn
 * @returns
 */
function enable(_btn) {
	var selectDatas = d1_BillList.datagrid('getSelections');
	if (selectDatas.length == 0) {
		$.messager.alert('提示', '至少选择一条记录进行操作', 'warning');
		return;
	}
	var ids = "";
	for (var i=0; i<selectDatas.length; i++) {
		ids += selectDatas[i].id + ",";
	}
	if (ids != "") {
		ids = ids.substring(0,ids.length-1);
	}
	var jso_par = {
		"rule_sts" : "Y",
		"ids" : ids
	};
	var jso_data = JSPFree
			.doClassMethodCall(
					"com.yusys.safe.rule.service.SafeCrRuleBS",
					"updateRuleSts", jso_par);
	if (jso_data.success == 1) {
		$.messager.show({
			title : '消息提示',
			msg : '启用成功',
			showType : 'show'
		});
	}
	for (var j=0; j<selectDatas.length; j++) {
		var seleDate = selectDatas[j];
		FreeUtil.refreshBillListOneRowDataByUpdate(d1_BillList, seleDate._rownum, "id='" + seleDate.id + "'");
	}
}

/**
 * 批量删除选中的规则
 * @return {[type]} [description]
 */
function onDelete(){
	JSPFree.doBillListBatchDelete(d1_BillList);
}

/**
 * 停用规则
 * 
 * @param _btn
 * @returns
 */
function disable(_btn) {
	var selectDatas = d1_BillList.datagrid('getSelections');
	if (selectDatas.length == 0) {
		$.messager.alert('提示', '至少选择一条记录进行操作', 'warning');
		return;
	}
	var ids = "";
	for (var i=0; i<selectDatas.length; i++) {
		ids += selectDatas[i].id + ",";
	}
	if (ids != "") {
		ids = ids.substring(0,ids.length-1);
	}
	var jso_par = {
		"rule_sts" : "N",
		"ids" : ids
	};
	var jso_data = JSPFree
			.doClassMethodCall(
					"com.yusys.safe.rule.service.SafeCrRuleBS",
					"updateRuleSts", jso_par);
	if (jso_data.success == 1) {
		$.messager.show({
			title : '消息提示',
			msg : '停用成功',
			showType : 'show'
		});
	}
	for (var j=0; j<selectDatas.length; j++) {
		var seleDate = selectDatas[j];
		FreeUtil.refreshBillListOneRowDataByUpdate(d1_BillList, seleDate._rownum, "id='" + seleDate.id + "'");
	}
}

/**
 * 查看一条详细的规则
 * @param _this
 * @returns
 */
function onView(_this) {
	var dataset = _this.dataset;
	var index = dataset.rowindex;
	var rows = d1_BillList.datagrid("getRows");
	var row = rows[index]; // index是行号

	var jso_OpenPars = {
		id : row.id,
		type_cd : row.type_cd,
		tab_name : tab_name,
		tab_name_en : tab_name_en
	};
	JSPFree.openDialog("规则查看", "/yujs/safe/ruleConfig/safe_rule_detail_view.js", 750, 450,
			jso_OpenPars, function(_rtdata) {
	});
}