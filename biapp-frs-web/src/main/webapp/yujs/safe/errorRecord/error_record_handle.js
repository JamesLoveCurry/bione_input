/**
 * 
 * <pre>
 * Title: 【错误补录】
 * Description: 单一处理页面
 * </pre>
 * @author wangxy31 
 * @version 1.00.00
   @date 2020年5月12日
 */

var str_sqlCons = null;
var tablename = null;
var tablename_en = null;
var pkvalue = null;
var report_type = null;

function AfterInit() {
	pkvalue = jso_OpenPars.pkvalue;
	tablename = jso_OpenPars.tablename;
	tablename_en = jso_OpenPars.tablename_en;
	report_type = jso_OpenPars.report_type;
	str_sqlCons = "rid = '"+ pkvalue +"'";

	//主表卡片
	var ary_CardBtn = ["保存/onSave/icon-ok", "隐藏提示/onHiddenErr", "显示提示/onShowErr"];
	var jso_CardConfig = {
			"afterloadclass": "com.yusys.safe.errorrecord.service.AfterSafeErrorDataLoadBS",
			"isafterloadsetcolor": "Y"
		};
	var str_className = "Class:com.yusys.safe.base.common.template.CommonBuilderTemplate.getTemplet('" + tablename + "','" + tablename_en + "','" + report_type + "','" + str_LoginUserOrgNo + "')";
	JSPFree.createBillCard("d1", str_className, ary_CardBtn, jso_CardConfig);
}

/**
 * 页面初始化后
 * @returns
 */
function AfterBodyLoad() {
	//查询主表卡片数据
	JSPFree.queryBillCardData(d1_BillCard, str_sqlCons, "Y");
}

/**
 * 隐藏提示
 * @returns
 */
function onHiddenErr() {
	JSPFree.setBillCardItemWarnMsgVisible(d1_BillCard,false);
}

/**
 * 显示提示
 * @returns
 */
function onShowErr() {
	JSPFree.setBillCardItemWarnMsgVisible(d1_BillCard, true);
}

/**
* 保存
 * 保存之前，先判断是否要进行单条数据检核操作
* @return {[type]} [description]
*/
var saveFlag = "";
function onSave() {
	// 是否进行单条数据检核操作
	var jso_check = JSPFree.doClassMethodCall("com.yusys.safe.base.common.service.SafeCommonBS", 
			"getSafeCheckProperties", {});
	var ischeck = jso_check.ischeck;
	
	// 根据表信息，查询数据源信息
	var jso_ds = JSPFree.doClassMethodCall("com.yusys.safe.base.common.service.SafeCommonBS",
			"getDsByTabName", {tab_name:tablename, report_type:report_type});
	var str_ds = jso_ds.dsName;
	
	if ("Y" == ischeck) {
		var backValue = JSPFree.editTableCheckData(d1_BillCard, "Edit", tablename, tablename_en.toUpperCase(),str_ds,"8");
		if (backValue == "" || "undifind" == backValue) {
			return;
		} else if (backValue == "OK") {
			saveFlag = JSPFree.doBillCardUpdate(d1_BillCard, null);
			if (saveFlag) {
				// 处理校验结果表的数据状态
				var tab_name = SafeFreeUtil.getTableNames().SAFE_RESULT_DATA;
				// 获取英文表名
				var jso_data = JSPFree.doClassMethodCall(
						"com.yusys.safe.base.common.service.SafeCommonBS",
						"getTabNameByEn", {tab_name:tab_name, report_type:report_type});
				var tab_name_en = jso_data.tab_name_en;
				
				var jso_rt = JSPFree.doClassMethodCall("com.yusys.safe.errorrecord.service.SafeErrorRecordBS", 
						"updateResultData", {tab:tab_name_en, tablename_en:tablename_en, pkvalue:pkvalue, report_type:report_type});
				
				JSPFree.closeDialog("OK");
			}
		} else if (backValue == "Fail") {
			return;
		}
	} else {
		saveFlag = JSPFree.doBillCardUpdate(d1_BillCard, null);
		if (saveFlag) {
			// 处理校验结果表的数据状态
			var tab_name = SafeFreeUtil.getTableNames().SAFE_RESULT_DATA;
			// 获取英文表名
			var jso_data = JSPFree.doClassMethodCall(
					"com.yusys.safe.base.common.service.SafeCommonBS",
					"getTabNameByEn", {tab_name:tab_name, report_type:report_type});
			var tab_name_en = jso_data.tab_name_en;
			
			var jso_rt = JSPFree.doClassMethodCall("com.yusys.safe.errorrecord.service.SafeErrorRecordBS", 
					"updateResultData", {tab:tab_name_en, tablename_en:tablename_en, pkvalue:pkvalue, report_type:report_type});
			
			JSPFree.closeDialog("OK");
		}
	}
}