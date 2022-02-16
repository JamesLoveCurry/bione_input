var _TheArray = [["crrs_group_group_client1","/biapp-crrs/freexml/crrs/group/crrs_group_group_client_CODE1.xml"],
	["crrs_group_group_client2","/biapp-crrs/freexml/crrs/group/crrs_group_group_client_CODE2.xml"],
	["crrs_group_actualcontroller","/biapp-crrs/freexml/crrs/group/crrs_group_actualcontroller_CODE1.xml"],
	["crrs_group_executives","/biapp-crrs/freexml/crrs/group/crrs_group_executives_CODE1.xml"],
	["crrs_group_ffiliated_groups","/biapp-crrs/freexml/crrs/group/crrs_group_ffiliated_groups_CODE1.xml"],
	["crrs_group_members","/biapp-crrs/freexml/crrs/group/crrs_group_members_CODE1.xml"],
	["crrs_passport","/biapp-crrs/freexml/crrs/group/crrs_passport_CODE1.xml"],
	["crrs_paper","/biapp-crrs/freexml/crrs/group/crrs_paper_CODE1.xml"],
	["crrs_ent_credit1","/biapp-crrs/freexml/crrs/ent/crrs_ent_credit_CODE1.xml"],
	["crrs_ent_credit2","/biapp-crrs/freexml/crrs/ent/crrs_ent_credit_CODE2.xml"],
	["crrs_ent_credit3","/biapp-crrs/freexml/crrs/ent/crrs_ent_credit_CODE3.xml"],
	["crrs_ent_credit4","/biapp-crrs/freexml/crrs/ent/crrs_ent_credit_CODE4.xml"],
	["crrs_ent_group_client","/biapp-crrs/freexml/crrs/ent/crrs_ent_group_client_CODE1.xml"],
	["crrs_person_personal","/biapp-crrs/freexml/crrs/person/crrs_person_personal_CODE1.xml"],
	["crrs_person_personal_loan","/biapp-crrs/freexml/crrs/person/crrs_person_personal_loan_CODE1.xml"],
	["crrs_person_joint_debtor","/biapp-crrs/freexml/crrs/person/crrs_person_joint_debtor_CODE1.xml"],
	["crrs_person_student_loan","/biapp-crrs/freexml/crrs/person/crrs_person_student_loan_CODE1.xml"],
	["crrs_person_ent_guaranteed","/biapp-crrs/freexml/crrs/person/crrs_person_ent_guaranteed_CODE1.xml"],
	["crrs_single_corporation","/biapp-crrs/freexml/crrs/customer/crrs_single_corporation_CODE1.xml"],
	["crrs_single_executives","/biapp-crrs/freexml/crrs/customer/crrs_single_executives_CODE1.xml"],
	["crrs_single_shareholder_ep","/biapp-crrs/freexml/crrs/customer/crrs_single_shareholder_ep_CODE1.xml"],
	["crrs_ent_loan","/biapp-crrs/freexml/crrs/ent/crrs_ent_loan_CODE1.xml"],
	["crrs_ent_offbalance_sa","/biapp-crrs/freexml/crrs/ent/crrs_ent_offbalance_sa_CODE1.xml"],
	["crrs_ent_guaranteed","/biapp-crrs/freexml/crrs/ent/crrs_ent_guaranteed_CODE1.xml"],
	["crrs_ent_equitystake","/biapp-crrs/freexml/crrs/ent/crrs_ent_equitystake_CODE1.xml"],
	["crrs_ent_bond","/biapp-crrs/freexml/crrs/ent/crrs_ent_bond_CODE1.xml"],
	["crrs_ent_trade_info","/biapp-crrs/freexml/crrs/ent/crrs_ent_trade_info_CODE1.xml"],
	["crrs_ent_trade_customers","/biapp-crrs/freexml/crrs/ent/crrs_ent_trade_customers_CODE1.xml"]];
var str_sqlCons = null;
var tablename = null;
var tablename_en = null;
var pkvalue = null;
var result_tab_name = null;
var type = null;// 传的是：确定性校验（1）/一致性校验（2）/提示性校验（3）
var is_back = null;// 是否有退回按钮
function AfterInit(){
	var arr_url = null;
	pkvalue = jso_OpenPars.pkvalue;
	tablename = jso_OpenPars.tablename;
	tablename_en = jso_OpenPars.tablename_en;
	type = jso_OpenPars.type;  //校验类型
	result_tab_name = jso_OpenPars.tab;
	str_sqlCons = "rid = '"+ pkvalue +"'";
	is_back = jso_OpenPars.is_back;

	for(var i=0;i<_TheArray.length;i++) {
		var arr_tab = _TheArray[i][0];
		if (tablename_en.toLowerCase() == _TheArray[i][0].toLowerCase()) {
			arr_url = _TheArray[i][1]
		} else {
			if (tablename_en.toLowerCase().indexOf("crrs_group_group_client") != -1) {
				var data = JSPFree.getHashVOs("select * from crrs_group_group_client where rid = '"+pkvalue+"'");
				if (data != null && data.length > 0) {
					if ("1" == data[0].type) {
						arr_url = "/biapp-crrs/freexml/crrs/group/crrs_group_group_client_CODE1.xml";
					} else {
						arr_url = "/biapp-crrs/freexml/crrs/group/crrs_group_group_client_CODE2.xml";
					}
				}
			} else if (tablename_en.toLowerCase().indexOf("crrs_ent_credit") != -1) {
				var data = JSPFree.getHashVOs("select * from crrs_ent_credit where rid = '"+pkvalue+"'");
				if (data != null && data.length > 0) {
					if ("1" == data[0].credit_type) {
						arr_url = "/biapp-crrs/freexml/crrs/ent/crrs_ent_credit_CODE3.xml";
					} else if ("2" == data[0].credit_type) {
						arr_url = "/biapp-crrs/freexml/crrs/ent/crrs_ent_credit_CODE1.xml";
					} else if ("3" == data[0].credit_type) {
						arr_url = "/biapp-crrs/freexml/crrs/ent/crrs_ent_credit_CODE2.xml";
					} else if ("4" == data[0].credit_type) {
						arr_url = "/biapp-crrs/freexml/crrs/ent/crrs_ent_credit_CODE4.xml";
					}
				}
			}
		}
	}
	
	// 主表卡片
	// 判断是否有退回按钮
	var ary_CardBtn = null;
	if ("Y" == is_back) {
		ary_CardBtn = ["保存/onSave/icon-ok", "退回/onBack/icon-ok"];
	} else {
		ary_CardBtn = ["保存/onSave/icon-ok"];
	}
	
	var jso_CardConfig = null;
	if ("1" == type) {
		jso_CardConfig = {
			"afterloadclass": "com.yusys.crrs.datahandle.service.AfterErrorDataLoadClass",
			"isafterloadsetcolor": "Y"
		};
	} else if ("2" == type) {
		jso_CardConfig = {
			"afterloadclass": "com.yusys.crrs.datahandle.service.AfterErrorDataLoadClass1",
			"isafterloadsetcolor": "Y"
		};
	} else if ("3" == type) {
		jso_CardConfig = {
			"afterloadclass": "com.yusys.crrs.datahandle.service.AfterErrorDataLoadClass2",
			"isafterloadsetcolor": "Y"
		};
	}
	JSPFree.createBillCard("d1",arr_url,ary_CardBtn,jso_CardConfig);
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
	var backValue = JSPFree.editTableCheckData(d1_BillCard, "Edit", tablename, tablename_en.toUpperCase(),"","2");
	if (backValue == "" || "undifind" == backValue) {
		return;
	} else if (backValue == "OK") {
		var saveFlag = JSPFree.doBillCardUpdate(d1_BillCard,null);
		if (saveFlag) {
			// 处理主数据状态
			JSPFree.doClassMethodCall("com.yusys.crrs.rulevalidate.service.CrrsResultBSDMO", 
					"updateResultData",{tab:result_tab_name,tablename_en:tablename_en,pkvalue:pkvalue});
			JSPFree.closeDialog("OK");
		}
	} else if (backValue == "Fail") {
		return;
	}
}

/**
* 退回
* @return {[type]} [description]
*/
function onBack(){
	JSPFree.confirm("提示","您确认要退回吗?",function(_isOK){
		if(_isOK){
			//弹出窗口,传入参数,然后接收返回值!
			JSPFree.openDialog("填写原因","/yujs/crrs/datahandle/BackReasonDialog.js",900,600,{templetcode:"/biapp-crrs/freexml/crrs/rule/crrs_result_sure_back.xml"},function(_rtdata){
				if(_rtdata != null && _rtdata != undefined && null != _rtdata.reason && undefined != _rtdata.reason){
					doUpdateBackStatus(result_tab_name,tablename_en,pkvalue,_rtdata.reason);
				}
			});
		}
	});
}

//退回，将已分发修改为退回
function doUpdateBackStatus(tab,tablename_en,pkvalue,_reason){
	var jso_par = {tab:tab,tablename_en:tablename_en,pkvalue:pkvalue,reason:_reason};
	var jso_rt = JSPFree.doClassMethodCall("com.yusys.crrs.rulevalidate.service.ValidateResultHandleBS","doResultBack1",jso_par);
	JSPFree.closeDialog(jso_rt);
}