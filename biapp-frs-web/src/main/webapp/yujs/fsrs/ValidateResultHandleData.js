var str_sqlCons = null;
var tablename = null;
var tablename_en = null;
var pkvalue = null;
function AfterInit(){
	pkvalue = jso_OpenPars.pkvalue;
	tablename = jso_OpenPars.tablename;
	tablename_en = jso_OpenPars.tablename_en;
	str_sqlCons = "rid = '"+ pkvalue +"'";

	//主表卡片
	var ary_CardBtn = ["保存/onSave/icon-ok","隐藏提示/onHiddenErr","显示提示/onShowErr"];
	var jso_CardConfig = {
			"afterloadclass": "com.yusys.fsrs.datahandle.service.AfterErrorDataLoadClass",
			"isafterloadsetcolor": "Y"
		};
	var str_className = "Class:com.yusys.fsrs.business.service.FsrsModelTempletBuilder.getTemplet('"+tablename+"','"+tablename_en+"')";
	
	JSPFree.createBillCard("d1", str_className,ary_CardBtn,jso_CardConfig);
}

function AfterBodyLoad(){
	//查询主表卡片数据
	JSPFree.queryBillCardData(d1_BillCard, str_sqlCons, "Y");
}

//隐藏提示
function onHiddenErr(){
	JSPFree.setBillCardItemWarnMsgVisible(d1_BillCard,false);
}

//显示提示
function onShowErr(){
	JSPFree.setBillCardItemWarnMsgVisible(d1_BillCard,true);
}

/**
* 保存
* @return {[type]} [description]
*/
function onSave(){
	// 保存前，先进行数据校验
	var backValue = JSPFree.editTableCheckData(d1_BillCard, "Edit", tablename, tablename_en.toUpperCase(),"","3");
	if (backValue == "" || "undifind" == backValue) {
		return;
	} else if (backValue == "OK") {
		var saveFlag = JSPFree.doBillCardUpdate(d1_BillCard,null);
		if (saveFlag) {
			// 处理主数据状态
			var jso_rt = JSPFree.doClassMethodCall("com.yusys.fsrs.business.service.FsrsResultBSDMO", 
					"updateResultData",{tab:"fsrs_result_standard",tablename_en:tablename_en,pkvalue:pkvalue});
			JSPFree.closeDialog("OK");
		}
	} else if (backValue == "Fail") {
		return;
	}
}