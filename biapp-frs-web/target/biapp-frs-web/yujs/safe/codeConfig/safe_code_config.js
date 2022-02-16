/**
 *
 * <pre>
 * Title:【配置管理】【字典配置】
 * Description:字典配置：主页面
 * 在此界面对系统的数据字典进行维护，实现对数据字典的导入，导出操作
 * </pre>
 * @author liangzy5
 * @version 1.00.00
 * @date 2020/6/5 16:24
 */

var tab_name = "";
var tab_name_en = "";

function AfterInit(){
	// 获取"字典表"
	tab_name = SafeFreeUtil.getTableNames().RPT_STD_CODE_INFO;
	// 获取英文表名
	var jso_data = JSPFree.doClassMethodCall("com.yusys.safe.base.common.service.SafeCommonBS", "getTabNameByEn", {tab_name:tab_name});
	tab_name_en = jso_data.tab_name_en;
	
	var str_className = "Class:com.yusys.safe.codemanage.template.CodeManageBuilderTemplate.getTemplet('" + tab_name + "','" + tab_name_en + "')";
	JSPFree.createBillList("d1",str_className,null,{list_btns:"[icon-p68]导入/importRule;[icon-p69]导出/exportRule(this);[icon-remove]删除/onDelete;",isSwitchQuery:"N",list_ischeckstyle:"Y",list_ismultisel:"Y"});
}

/**
 * 批量删除选中的码值
 * @return {[type]} [description]
 */
function onDelete(){
	JSPFree.doBillListBatchDelete(d1_BillList);
}

/**
 * 【导入】
 * 点击按钮，上传excel模板文件，对数据字典进行导入
 * 
 * @returns
 */
function importRule() {
	var rptType = JSPFree.getBillQueryFormValue(d1_BillQuery).rpt_type;
	
	if(rptType==''){
		$.messager.alert('提示', '请在查询框中选择一种业务类型后再导入', 'warning');
		return;
	}
	
	JSPFree.confirm("提示","码值导入会增量添加系统中的码值字典，请谨慎操作，请确认是否执行导入操作！",function(_isOK){
		if(_isOK){JSPFree.openDialog("文件上传", "/yujs/safe/codeConfig/safe_input_code.js", 500, 240, {reportType:rptType},function(_rtdata) {
				if (_rtdata != null && _rtdata != undefined && _rtdata.type != "dirclose") { //不是直接关闭窗口
					JSPFree.alert(_rtdata.msg);
					// 导入之后立即查询导入之前业务类型的数据
					FreeUtil.loadBillQueryData(d1_BillList, {rpt_type:rptType});
					JSPFree.queryDataByConditon2(d1_BillList, "rpt_type ='"+rptType+"'"); // 立即查询刷新数据
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
	var rptType = JSPFree.getBillQueryFormValue(d1_BillQuery).rpt_type;
	
	if(rptType==''){
		$.messager.alert('提示', '请在查询框中选择一种业务类型后再导出', 'warning');
		return;
	}
	
	var download = null;
	download = $('<iframe id="download" style="display: none;"/>');
	$('body').append(download);

	var src = v_context + "/safe/code/export?rptType=" + rptType;
	download.attr('src', src);
}