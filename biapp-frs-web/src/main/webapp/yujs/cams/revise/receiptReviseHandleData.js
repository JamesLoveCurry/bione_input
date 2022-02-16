var _TheArray = [
	["cams_indv_acct_m", "/biapp-cams/freexml/cams/revise/cams_indv_acct_m_CODE1.xml"],
	["cams_indv_name_m", "/biapp-cams/freexml/cams/revise/cams_indv_name_m_CODE1.xml"],
	["cams_indv_addr_m", "/biapp-cams/freexml/cams/revise/cams_indv_addr_m_CODE1.xml"],
	["cams_indv_tin_m", "/biapp-cams/freexml/cams/revise/cams_indv_tin_m_CODE1.xml"],
	["cams_corp_acct_m", "/biapp-cams/freexml/cams/revise/cams_corp_acct_m_CODE1.xml"],
	["cams_corp_name_m", "/biapp-cams/freexml/cams/revise/cams_corp_name_m_CODE1.xml"],
	["cams_corp_addr_m", "/biapp-cams/freexml/cams/revise/cams_corp_addr_m_CODE1.xml"],
	["cams_corp_tin_m", "/biapp-cams/freexml/cams/revise/cams_corp_tin_m_CODE1.xml"],
	["cams_corp_ctrl_m", "/biapp-cams/freexml/cams/revise/cams_corp_ctrl_m_CODE1.xml"],
];
var str_sqlCons = null;
var tablename_en = null;
var docrefid = null;
var rid = null;
var type = ""; //用于区分是否是机构控制人信息那边传过来的，2表示是，其他页面不传此参数。主要区别在于保存之后的提示

//机构控制人列表层次多，页面展示不同于其他表，所以除了机构控制人列表之外，所有编辑按钮都走这里！！
//展示逻辑都是一致的，查找错误，允许编辑错误。只有保存逻辑onSave()在判断一条数据是否已经完成的逻辑不一样
//如果是机构控制人下面的三种子表，在保存的时候需要查找4张表中是否还有待处理的。其他表只需要查找自己表里面是否还有待处理的。
function AfterInit(){
	docrefid = jso_OpenPars.docrefid;
	tablename_en = jso_OpenPars.tablename_en;
	rid = jso_OpenPars.rid; //子表比主表多传一个参数，rid，用于在子表中确定唯一一条数据
	type = jso_OpenPars.type;
	
	str_sqlCons = "docrefid = '"+ docrefid +"'";
	
	if(rid != null && rid != ""){
		str_sqlCons = str_sqlCons + " and rid = '"+rid+"' ";
	}
	
	var arr_url = null; //遍历上面的数组，根据传过来的表名获取相应xml的路径
	for(var i=0;i<_TheArray.length;i++){
		if (tablename_en.toLowerCase() == _TheArray[i][0]) {
			arr_url = _TheArray[i][1];
		}
	}

	//主表卡片
	var ary_CardBtn = ["保存/onSave/icon-ok","隐藏提示/onHiddenErr","显示提示/onShowErr"];
	var jso_CardConfig = {
			"afterloadclass": "com.yusys.cams.revise.service.CamsAfterErrorDataLoadClass2",
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
	JSPFree.setBillCardItemValue(d1_BillCard,"revise_status","2"); //更新此条数据的状态为已处理
	var flag = JSPFree.doBillCardUpdate(d1_BillCard,null);
	if(flag){
		if("2"==type){ //保存机构控制人信息的子表走这里
			// 处理主数据状态
			var jso_rt = JSPFree.doClassMethodCall("com.yusys.cams.revise.service.CamsReviseDMO", 
					"updateResultData4",{tab:"cams_receipt_data",tablename_en:tablename_en,docrefid:docrefid});
			if("OK"==jso_rt.msg){
				JSPFree.closeDialog("OK");
			}else{
				JSPFree.closeDialog("No");
			}	
		}else{
			// 处理主数据状态
			var jso_rt = JSPFree.doClassMethodCall("com.yusys.cams.revise.service.CamsReviseDMO", 
					"updateResultData3",{tab:"cams_receipt_data",tablename_en:tablename_en,docrefid:docrefid});
			if("OK"==jso_rt.msg){
				JSPFree.closeDialog("OK");
			}else{
				JSPFree.closeDialog("No");
			}
		}
	}
}