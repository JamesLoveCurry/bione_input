/**
 * 
 * <pre>
 * Title: 【错误补录】
 * Description: 单一处理页面
 * </pre>
 * @author miaokx
 * @version 1.00.00
   @date 2020年8月24日
 */

var str_sqlCons = null;
var tablename = null;
var tablename_en = null;
var pkvalue = null;

function AfterInit() {
	pkvalue = jso_OpenPars.pkvalue;
	tablename = jso_OpenPars.tablename;
	tablename_en = jso_OpenPars.tablename_en;
	str_sqlCons = "rid = '"+ pkvalue +"'";

	//主表卡片
	var ary_CardBtn = ["保存/onSave/icon-ok", "隐藏提示/onHiddenErr", "显示提示/onShowErr"];
	var jso_CardConfig = {
			"afterloadclass": "com.yusys.bfd.errorRecord.service.AfterBfdErrorDataLoadBS",
			"isafterloadsetcolor": "Y"
		};
	var str_className = "Class:com.yusys.bfd.business.service.BfdModelTempletBuilder.getTemplet('" + tablename + "','" + tablename_en + "','" + str_LoginUserOrgNo + "')";
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
	var jso_check = JSPFree.doClassMethodCall("com.yusys.bfd.common.service.BfdCommonBS",
			"getBfdCheckProperties", {});
	var ischeck = jso_check.ischeck;
	// 根据表信息，查询数据源信息
	var jso_ds = JSPFree.doClassMethodCall("com.yusys.bfd.common.service.BfdCommonBS",
			"getDsByTabName", {tab_name:tablename});
	var str_ds = jso_ds.dsName;
	
	if ("Y" == ischeck) {
		var backValue = JSPFree.editTableCheckData(d1_BillCard, "Edit", tablename, tablename_en.toUpperCase(),str_ds,"BFD");
		if (backValue == "" || "undifind" == backValue) {
			return;
		} else if (backValue == "OK") {
			saveFlag = JSPFree.doBillCardUpdate(d1_BillCard, null);
			if (saveFlag) {
				var jso_rt = JSPFree.doClassMethodCall("com.yusys.bfd.errorRecord.service.BfdErrorRecordBS",
						"updateResultData", { tablename_en:tablename_en, pkvalue:pkvalue});
				
				JSPFree.closeDialog("OK");
			}
		} else if (backValue == "Fail") {
			return;
		}
	} else {
		saveFlag = JSPFree.doBillCardUpdate(d1_BillCard, null);
		if (saveFlag) {
			var jso_rt = JSPFree.doClassMethodCall("com.yusys.bfd.errorRecord.service.BfdErrorRecordBS",
					"updateResultData", {tablename_en:tablename_en, pkvalue:pkvalue});
			
			JSPFree.closeDialog("OK");
		}
	}
}