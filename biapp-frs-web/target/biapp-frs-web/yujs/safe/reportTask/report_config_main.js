/**
 * 
 * <pre>
 * Title: 【报送管理】-【报文策略】
 * Description: 报文策略：主页面
 * </pre>
 * @author wangxy31 
 * @version 1.00.00
   @date 2020年5月22日
 */

var tab_name = "";
var tab_name_en = "";
var str_subfix = "";
var str_className = "";

function AfterInit() {
	// 获取路径参数
	if (jso_OpenPars != '') {
		if(jso_OpenPars.type != null){
			str_subfix = jso_OpenPars.type;
		}
	}
	// 获取【报文策略表】常量类
	tab_name = SafeFreeUtil.getTableNames().SAFE_CR_REPORT_CONFIG;
	// 获取英文表名
	var jso_data = JSPFree.doClassMethodCall(
			"com.yusys.safe.base.common.service.SafeCommonBS",
			"getTabNameByEn", {tab_name:tab_name, report_type:str_subfix});
	tab_name_en = jso_data.tab_name_en;

	str_className = "Class:com.yusys.safe.reporttask.template.TaskConfigBuilderTemplate.getTemplet('" + tab_name + "','" + tab_name_en + "','" + str_subfix + "')";
	JSPFree.createBillList("d1", str_className, null, {list_btns:"[icon-add]批量新增/onBatchInsert;[icon-p99]新增/onInsert;$UPDATE;$DELETE", isSwitchQuery:"N"});
}

/**
 * 批量新增
 * @returns
 */
function onBatchInsert() {
	var defaultVal = {tabname:tab_name, tabnameen:tab_name_en, reporttype:str_subfix};
	
	JSPFree.openDialog("批量新增", "/yujs/safe/reportTask/report_config_batch_add.js", 580, 500, defaultVal, function(_rtData) {
		if (_rtData != null) {
			if ("dirclose" == _rtData.type) {
				return;
			}
			
			var str_sql = _rtData.whereSQL;  // 返回的主键拼成的SQL
		       JSPFree.queryDataByConditon(d1_BillList, str_sql);
		       JSPFree.alert("批量新增数据成功!<br>当前页面数据是查询的新增数据!");
		}
	});
}

/**
 * 新增
 * @return {[type]} [description]
 */
function onInsert() {
	var defaultVal = {templetcode:str_className, tabname:tab_name, tabnameen:tab_name_en, reporttype:str_subfix};
	JSPFree.openDialog("新增", "/yujs/safe/reportTask/report_config_add.js", 900, 560, defaultVal, function(_rtdata) {
		if (_rtdata == "保存成功") {
			JSPFree.alert("保存成功!");
		}
		
		JSPFree.queryDataByConditon(d1_BillList);
	}, true);
}