var _TheArray = [
	["cams_indv_acct", "/biapp-cams/freexml/cams/cams_indv_acct_CODE1.xml"],
	["cams_indv_name", "/biapp-cams/freexml/cams/cams_indv_name_CODE1.xml"],
	["cams_indv_addr", "/biapp-cams/freexml/cams/cams_indv_addr_CODE1.xml"],
	["cams_indv_tin", "/biapp-cams/freexml/cams/cams_indv_tin_CODE1.xml"],
	["cams_corp_acct", "/biapp-cams/freexml/cams/cams_corp_acct_CODE1.xml"],
	["cams_corp_name", "/biapp-cams/freexml/cams/cams_corp_name_CODE1.xml"],
	["cams_corp_addr", "/biapp-cams/freexml/cams/cams_corp_addr_CODE1.xml"],
	["cams_corp_tin", "/biapp-cams/freexml/cams/cams_corp_tin_CODE1.xml"],
	["cams_corp_ctrl", "/biapp-cams/freexml/cams/cams_corp_ctrl_CODE1.xml"],
];
var str_sqlCons = null;
var tablename = null;
var tablename_en = null;
var pkvalue = null;
function AfterInit(){
	pkvalue = jso_OpenPars.pkvalue;
	tablename = jso_OpenPars.tablename;
	tablename_en = jso_OpenPars.tablename_en;
	str_sqlCons = "rid = '"+ pkvalue +"'";

	var arr_url = null; //遍历上面的数组，根据传过来的表名获取相应xml的路径
	for(var i=0;i<_TheArray.length;i++){
		if (tablename_en.toLowerCase() == _TheArray[i][0]) {
			arr_url = _TheArray[i][1];
		}
	}

	//主表卡片
	var ary_CardBtn = ["保存/onSave/icon-ok","隐藏提示/onHiddenErr","显示提示/onShowErr"];
	var jso_CardConfig = {
			"afterloadclass": "com.yusys.cams.revise.service.CamsAfterErrorDataLoadClass1",
			"isafterloadsetcolor": "Y"
		};
	
	JSPFree.createBillCard("d1", arr_url, ary_CardBtn, jso_CardConfig);
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
	var flag = JSPFree.doBillCardUpdate(d1_BillCard,null);
	if(flag){
		
		// 处理校验结果表的数据状态
		var jso_rt = JSPFree.doClassMethodCall("com.yusys.cams.revise.service.CamsReviseDMO", 
				"updateResultData",{tab:"cams_result_data",tablename_en:tablename_en,pkvalue:pkvalue});
		JSPFree.closeDialog("OK");
	}
}