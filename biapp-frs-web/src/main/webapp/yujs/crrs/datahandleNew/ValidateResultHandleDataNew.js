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
var type = null;// ??????????????????????????????1???/??????????????????2???/??????????????????3???
var source = null;// ????????????1???????????????????????????????????????????????????
var is_back = null;// ?????????????????????
function AfterInit(){
	var arr_url = null;
	pkvalue = jso_OpenPars.pkvalue;
	tablename = jso_OpenPars.tablename;
	tablename_en = jso_OpenPars.tablename_en;
	type = jso_OpenPars.type;  //????????????
	source = jso_OpenPars.source;

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
	
	// ????????????
	// ???????????????????????????
	var ary_CardBtn = null;
	if ("Y" == is_back) {
		ary_CardBtn = ["??????/onSave/icon-ok", "??????/onBack/icon-ok"];
	} else {
		ary_CardBtn = ["??????/onSave/icon-ok"];
	}

	var jso_CardConfig = null;
	debugger;
	if ("1" == type) {
		if ("1" == source) {
			jso_CardConfig = {
					"afterloadclass": "com.yusys.crrs.datahandle.service.AfterErrorDataLoadClass3New",
					"isafterloadsetcolor": "Y"
				};
		} else {
			jso_CardConfig = {
					"afterloadclass": "com.yusys.crrs.datahandle.service.AfterErrorDataLoadClassNew",
					"isafterloadsetcolor": "Y"
				};
		}
	} else if ("2" == type) {
		if ("1" == source) {
			jso_CardConfig = {
					"afterloadclass": "com.yusys.crrs.datahandle.service.AfterErrorDataLoadClass4New",
					"isafterloadsetcolor": "Y"
				};
		} else {
			jso_CardConfig = {
					"afterloadclass": "com.yusys.crrs.datahandle.service.AfterErrorDataLoadClass1New",
					"isafterloadsetcolor": "Y"
				};
		}
	} else if ("3" == type) {
		if ("1" == source) {
			jso_CardConfig = {
					"afterloadclass": "com.yusys.crrs.datahandle.service.AfterErrorDataLoadClass5New",
					"isafterloadsetcolor": "Y"
				};
		} else {
			jso_CardConfig = {
					"afterloadclass": "com.yusys.crrs.datahandle.service.AfterErrorDataLoadClass2New",
					"isafterloadsetcolor": "Y"
				};
		}
	}
	JSPFree.createBillCard("d1",arr_url,ary_CardBtn,jso_CardConfig);
}

function AfterBodyLoad(){
	//????????????????????????
	JSPFree.queryBillCardData(d1_BillCard, str_sqlCons, "Y");
}

//????????????
function onHiddenErr(){
	JSPFree.setBillCardItemWarnMsgVisible(d1_BillCard,false);
}

//????????????
function onShowErr(){
	JSPFree.setBillCardItemWarnMsgVisible(d1_BillCard,true);
}

/**
* ??????
* @return {[type]} [description]
*/
function onSave(){
	// ????????????????????????????????????
	var jso_check = JSPFree.doClassMethodCall("com.yusys.crrs.common.service.CrrsCommonBS", "getCrrsCheckProperties", {});
	var ischeck = jso_check.ischeck;
	if ("Y" == ischeck) {
		var backValue = JSPFree.editTableCheckData(d1_BillCard, "Edit", tablename, tablename_en.toUpperCase(),"","2");
		if (backValue == "" || "undifind" == backValue) {
			return;
		} else if (backValue == "OK") {
			var saveFlag = JSPFree.doBillCardUpdate(d1_BillCard,null);
			if (saveFlag) {
				// ?????????????????????
				JSPFree.doClassMethodCall("com.yusys.crrs.rulevalidate.service.CrrsResultBSDMO", 
						"updateResultDataNew",{tab:result_tab_name,tablename_en:tablename_en,pkvalue:pkvalue});
				JSPFree.closeDialog("OK");
			}
		} else if (backValue == "Fail") {
			return;
		}
	} else {
		var saveFlag = JSPFree.doBillCardUpdate(d1_BillCard,null);
		if (saveFlag) {
			// ?????????????????????
			JSPFree.doClassMethodCall("com.yusys.crrs.rulevalidate.service.CrrsResultBSDMO", 
					"updateResultDataNew",{tab:result_tab_name,tablename_en:tablename_en,pkvalue:pkvalue});
			JSPFree.closeDialog("OK");
		}
	}
}

/**
* ??????
* @return {[type]} [description]
*/
function onBack(){
	JSPFree.confirm("??????","??????????????????????",function(_isOK){
		if(_isOK){
			//????????????,????????????,?????????????????????!
			JSPFree.openDialog("????????????","/yujs/crrs/datahandle/BackReasonDialog.js",900,600,{templetcode:"/biapp-crrs/freexml/crrs/rule/crrs_result_sure_back.xml"},function(_rtdata){
				if(_rtdata != null && _rtdata != undefined && null != _rtdata.reason && undefined != _rtdata.reason){
					doUpdateBackStatus(result_tab_name,tablename_en,pkvalue,_rtdata.reason);
				}
			});
		}
	});
}

//????????????????????????????????????
function doUpdateBackStatus(tab,tablename_en,pkvalue,_reason){
	var jso_par = {tab:tab,tablename_en:tablename_en,pkvalue:pkvalue,reason:_reason};
	var jso_rt = JSPFree.doClassMethodCall("com.yusys.crrs.rulevalidate.service.ValidateResultHandleBS","doResultBack1",jso_par);
	JSPFree.closeDialog(jso_rt);
}