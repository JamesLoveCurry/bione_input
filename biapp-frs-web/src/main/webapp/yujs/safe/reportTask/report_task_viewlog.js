/**
 *
 * <pre>
 * Title: 【报送管理】-【报文生成】
 * Description: 报文生成：查看日志页面
 * </pre>
 * @author miaokx
 * @version 1.00.00
 @date 2020年6月18日
 */

var str_rid = null;
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
		str_rid = jso_OpenPars.rid;
	}
	// 获取【报文任务日志表】常量类
	tab_name = SafeFreeUtil.getTableNames().SAFE_CR_REPORT_LOG;
	// 获取英文表名
	var jso_data = JSPFree.doClassMethodCall(
		"com.yusys.safe.base.common.service.SafeCommonBS",
		"getTabNameByEn", {tab_name:tab_name, report_type:str_subfix});
	tab_name_en = jso_data.tab_name_en;

	str_className = "Class:com.yusys.safe.reporttask.template.TaskBuilderLogTemplate.getTemplet('" + tab_name + "','" + tab_name_en + "','" + str_subfix + "','" + str_rid  + "')";


	JSPFree.createBillList("d1", str_className, null, {list_btns:"[icon-p02]刷新/onRefresh;", isSwitchQuery:"N", list_ismultisel:"Y", ishavebillquery: "N"});
}

/**
 * 手动刷新
 */
function onRefresh(){
	JSPFree.createBillList("d1", str_className, null, {list_btns:"[icon-p02]刷新/onRefresh;", isSwitchQuery:"N", list_ismultisel:"Y",ishavebillquery: "N"});
}
