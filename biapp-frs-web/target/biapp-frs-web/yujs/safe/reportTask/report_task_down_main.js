/**
 * 
 * <pre>
 * Title: 【报送管理】-【报文下载】
 * Description: 报文下载：主页面
 * </pre>
 * @author wangxy31 
 * @version 1.00.00
   @date 2020年7月3日
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
	// 获取【报文任务表】常量类
	tab_name = SafeFreeUtil.getTableNames().SAFE_CR_REPORT;
	// 获取英文表名
	var jso_data = JSPFree.doClassMethodCall(
			"com.yusys.safe.base.common.service.SafeCommonBS",
			"getTabNameByEn", {tab_name:tab_name, report_type:str_subfix});
	tab_name_en = jso_data.tab_name_en;

	str_className = "Class:com.yusys.safe.base.common.template.CommonBuilderTemplate.getTemplet('" + tab_name + "','" + tab_name_en + "','" + str_subfix + "','" + str_LoginUserOrgNo + "')";
	JSPFree.createBillList("d1", str_className, null, {list_btns:"[icon-p47]打包下载/onZipAndDownload;", isSwitchQuery:"N",list_ischeckstyle:"Y", list_ismultisel:"Y", autoquery:"N", orderbys:"org_no",card_size:"900*500"});
	
	JSPFree.queryDataByConditon2(d1_BillList, "status = '"+SafeFreeUtil.getProcessReportStatus().COMPLETE+"'");
	JSPFree.setBillListForceSQLWhere(d1_BillList, "status = '"+SafeFreeUtil.getProcessReportStatus().COMPLETE+"'");
}

/**
 * 打包下载报文
 * @returns
 */
function onZipAndDownload() {
	var jso_par = {className:str_className, reportType:str_subfix};
	JSPFree.openDialog("一键打包下载本机构某一时间的所有报文", "/yujs/safe/reportTask/report_task_choosedate.js", 500, 400, jso_par);
}