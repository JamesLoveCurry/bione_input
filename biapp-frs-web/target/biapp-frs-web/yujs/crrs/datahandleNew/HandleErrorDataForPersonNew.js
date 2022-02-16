//个人客户 -- 校验结果数据处理界面
var type = null; // 传的是：确定性校验（1）/一致性校验（2）/提示性校验（3）
var source = null;// 传的是：1（对于已填报的数据，进行数据编辑）
var str_customercode = null;
var str_reportdate = null;
var str_sqlCons = "";  // 查询条件
var result_tab_name = null;
var is_back = null; // 是否有退回按钮
var isLoadTabb_2 = false;
var isLoadTabb_3 = false;
var isLoadTabb_4 = false;
var isLoadTabb_5 = false;
var ary_title = [];

function AfterInit(){
	str_customercode = jso_OpenPars2.customercode;
	str_reportdate = jso_OpenPars2.reportdate;
	var jso_ErrorCount = jso_OpenPars2.ErrorCount;  //错误总数
	result_tab_name = jso_OpenPars2.tab;  //校验结果表名
	type = jso_OpenPars2.type;  //校验类型
	source = jso_OpenPars2.source;
	is_back = jso_OpenPars2.is_back; // 是否有退回按钮

	//动态创建标题,即如果有错误数据,则有图标提醒
	ary_title.push(getTitleText("基本信息","crrs_person_personal",jso_ErrorCount)); //主表
	ary_title.push(getTitleText("业务信息","crrs_person_personal_loan",jso_ErrorCount));
	ary_title.push(getTitleText("共同债务人","crrs_person_joint_debtor",jso_ErrorCount));
	ary_title.push(getTitleText("助学贷款专项指标","crrs_person_student_loan",jso_ErrorCount));
	ary_title.push(getTitleText("担保情况","crrs_person_ent_guaranteed",jso_ErrorCount));

	JSPFree.createTabb("d1",ary_title,[100,100,110,170,120]);

    str_sqlCons="customer_code='" + str_customercode + "' and data_dt = '"+str_reportdate+"'"; 

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
	JSPFree.createBillCard("d1_1","/biapp-crrs/freexml/crrs/person/crrs_person_personal_CODE1.xml",ary_CardBtn,jso_CardConfig);

	JSPFree.addTabbSelectChangedListener(d1_tabb,onSelect);
}

function onSelect(_index,_title){
	//各子表的列表
	var jso_ListConfig = null;
	if ("1" == type) {
		var afterloadclass = "";
		if ("1" == source) {
			afterloadclass = "com.yusys.crrs.datahandle.service.AfterErrorDataLoadClass3New"
		} else {
			afterloadclass = "com.yusys.crrs.datahandle.service.AfterErrorDataLoadClassNew"
		}
		jso_ListConfig = {
			list_ispagebar: "Y",
			ishavebillquery: "Y",
			isSwitchQuery:"N",
			autoquery: "N",
			list_btns: "[icon-p41]修改错误数据/onUpdateErrorData",
			afterloadclass: afterloadclass,
			isafterloadsetcolor: "Y",
			querycontion:"customer_code='"+str_customercode+"' and data_dt = '"+str_reportdate+"'"
		};
	} else if ("2" == type) {
		var afterloadclass = "";
		if ("1" == source) {
			afterloadclass = "com.yusys.crrs.datahandle.service.AfterErrorDataLoadClass4New"
		} else {
			afterloadclass = "com.yusys.crrs.datahandle.service.AfterErrorDataLoadClass1New"
		}
		jso_ListConfig = {
			list_ispagebar: "Y",
			ishavebillquery: "Y",
			isSwitchQuery:"N",
			autoquery: "N",
			list_btns: "[icon-p41]修改错误数据/onUpdateErrorData",
			afterloadclass: afterloadclass,
			isafterloadsetcolor: "Y",
			querycontion:"customer_code='"+str_customercode+"' and data_dt = '"+str_reportdate+"'"
		};
		
	} else if ("3" == type) {
		var afterloadclass = "";
		if ("1" == source) {
			afterloadclass = "com.yusys.crrs.datahandle.service.AfterErrorDataLoadClass5New"
		} else {
			afterloadclass = "com.yusys.crrs.datahandle.service.AfterErrorDataLoadClass2New"
		}
		jso_ListConfig = {
			list_ispagebar: "Y",
			ishavebillquery: "Y",
			isSwitchQuery:"N",
			autoquery: "N",
			list_btns: "[icon-p41]修改错误数据/onUpdateErrorData",
			afterloadclass: afterloadclass,
			isafterloadsetcolor: "Y",
			querycontion:"customer_code='"+str_customercode+"' and data_dt = '"+str_reportdate+"'"
		};
	}

	var newIndex = _index+1;
	if(newIndex==1){
		//没有错误信息时，将按钮置灰
		if(ary_title[0].indexOf("/icon-p25") != -1){
			$("#d1_1_BillCardBtn1").removeAttr('disabled');
			$("#d1_1_BillCardBtn2").removeAttr('disabled');
			$("#d1_1_BillCardBtn3").removeAttr('disabled');
		}else{
			$("#d1_1_BillCardBtn1").attr('disabled',"true");
			$("#d1_1_BillCardBtn2").attr('disabled',"true");
			$("#d1_1_BillCardBtn3").attr('disabled',"true");
		}
	}
	
	if(newIndex==2){
		if(!isLoadTabb_2){
			JSPFree.createBillList("d1_2","/biapp-crrs/freexml/crrs/person/crrs_person_personal_loan_CODE1.xml",null,jso_ListConfig);  //业务信息
			$.parser.parse('#d1_2');
			JSPFree.queryDataByConditon(d1_2_BillList,str_sqlCons);  //业务信息
			isLoadTabb_2 = true;
		}
		//没有错误信息时，将按钮置灰
		if(ary_title[1].indexOf("/icon-p25") != -1){
			$("#d1_2_BillList_Btn修改错误数据").linkbutton('enable');
		}else{
			$("#d1_2_BillList_Btn修改错误数据").linkbutton('disable');
		}
	}

	if(newIndex==3){
		if(!isLoadTabb_3){
			JSPFree.createBillList("d1_3","/biapp-crrs/freexml/crrs/person/crrs_person_joint_debtor_CODE1.xml",null,jso_ListConfig);  //共同债务人
			$.parser.parse('#d1_3');
			JSPFree.queryDataByConditon(d1_3_BillList,str_sqlCons);  //共同债务人
			isLoadTabb_3 = true;
		}
		//没有错误信息时，将按钮置灰
		if(ary_title[2].indexOf("/icon-p25") != -1){
			$("#d1_3_BillList_Btn修改错误数据").linkbutton('enable');
		}else{
			$("#d1_3_BillList_Btn修改错误数据").linkbutton('disable');
		}
	}

	if(newIndex==4){
		if(!isLoadTabb_4){
			JSPFree.createBillList("d1_4","/biapp-crrs/freexml/crrs/person/crrs_person_student_loan_CODE1.xml",null,jso_ListConfig);  //助学贷款专项指标
			$.parser.parse('#d1_4');
			JSPFree.queryDataByConditon(d1_4_BillList,str_sqlCons);  //助学贷款专项指标
			isLoadTabb_4 = true;
		}
		//没有错误信息时，将按钮置灰
		if(ary_title[3].indexOf("/icon-p25") != -1){
			$("#d1_4_BillList_Btn修改错误数据").linkbutton('enable');
		}else{
			$("#d1_4_BillList_Btn修改错误数据").linkbutton('disable');
		}
	}

	if(newIndex==5){
		if(!isLoadTabb_5){
			JSPFree.createBillList("d1_5","/biapp-crrs/freexml/crrs/person/crrs_person_ent_guaranteed_CODE1.xml",null,jso_ListConfig);  //担保情况
			$.parser.parse('#d1_5');
			JSPFree.queryDataByConditon(d1_5_BillList,str_sqlCons);  //担保情况
			isLoadTabb_5 = true;
		}
		//没有错误信息时，将按钮置灰
		if(ary_title[4].indexOf("/icon-p25") != -1){
			$("#d1_5_BillList_Btn修改错误数据").linkbutton('enable');
		}else{
			$("#d1_5_BillList_Btn修改错误数据").linkbutton('disable');
		}
	}
}

function AfterBodyLoad(){
	//查询主表卡片数据
	JSPFree.queryBillCardData(d1_1_BillCard,str_sqlCons,"Y");
	
	JSPFree.setBillCardItemEditable(d1_1_BillCard,"customer_code",false);
	JSPFree.setBillCardItemEditable(d1_1_BillCard,"customer_name",false);
	JSPFree.setBillCardItemEditable(d1_1_BillCard,"data_dt",false);
}


//计算出title的标题
function getTitleText(_text,_tableName,jso_ErrorCount){
	if(typeof jso_ErrorCount == "undefined"){
		return _text;  //直接返回文本
	}
	var str_count = jso_ErrorCount[_tableName];  //
	//如果有数据,且不是0
	if(typeof str_count != "undefined" && str_count!=null && str_count!="" && str_count!="0" && str_count!=0){
		return "" + str_count + "" + _text + "/icon-p25";  //带图标,带数字
	}else{
		return _text;  //直接返回文本
	}
}

//修改错误数据
function onUpdateErrorData(_btn,_event){
	var billList = FreeUtil.getBtnBindBillList(_btn);  //根据按钮取得属于哪个列表
	var jso_templetVO = billList.templetVO;  //模板VO
	var str_templetcode = jso_templetVO.templet_option.templetcode;  //模板编码,后面根据这个模板编码创建卡片
	var str_templetname = jso_templetVO.templet_option.templetname;  //模板名称
	var str_fromtable = jso_templetVO.templet_option.fromtable;  //表名

	var jso_rowdata = JSPFree.getBillListSelectData(billList);  //
	if(jso_rowdata==null){
		JSPFree.alert("必须选择一条【" + str_templetname + "】记录!");
		return;
	}

    var str_sqlwhere = FreeUtil.getSQLWhereByPK(jso_templetVO,jso_rowdata);  //SQL条件
    var jso_par = {
		rowdata:jso_rowdata,
		tempeltcode:str_templetcode,
		tempeltname:str_templetname,
		SQLWhere:str_sqlwhere,
		fromtable:str_fromtable,
		tab:result_tab_name,
		type:type,
		source:source
	};

	//打开窗口
	JSPFree.openDialog2("修正数据-" + str_templetname,"/yujs/crrs/datahandleNew/UpdateHandleErrorDetailDialogNew.js",950,560,jso_par,function(_rtData){
		if(null != _rtData.msg){
			JSPFree.refreshBillListCurrPage(billList);  //立即刷新数据
			$.messager.show(_rtData);
		}
	},true);
}

//隐藏提示
function onHiddenErr(){
	JSPFree.setBillCardItemWarnMsgVisible(d1_1_BillCard,false);
}

function onShowErr(){
	JSPFree.setBillCardItemWarnMsgVisible(d1_1_BillCard,true);
}

/**
 * 保存
 * @return {[type]} [description]
 */
function onSave(){
	// 是否进行单条数据检核操作
	var jso_check = JSPFree.doClassMethodCall("com.yusys.crrs.common.service.CrrsCommonBS", "getCrrsCheckProperties", {});
	var ischeck = jso_check.ischeck;
	if ("Y" == ischeck) {
		var backValue = JSPFree.editTableCheckData(d1_1_BillCard, "Edit", "个人基本信息", "CRRS_PERSON_PERSONAL","","2");
		if (backValue == "" || "undifind" == backValue) {
			return;
		} else if (backValue == "OK") {
			// 提示验证通过，并进行保存
			var flag = JSPFree.doBillCardUpdate(d1_1_BillCard,null);

			var tablename_en = d1_1_BillCard.templetVO.templet_option.savetable;  
			var pkvalue = d1_1_BillCard.OldData.rid;  
			if(flag){
				var jso_rt = JSPFree.doClassMethodCall("com.yusys.crrs.rulevalidate.service.CrrsResultBSDMO", 
						"updateResultDataNew",{tab:result_tab_name,tablename_en:tablename_en,pkvalue:pkvalue});
				JSPFree.alert("校验并保存成功,同时更改了相关校验结果的状态为【完成】");
			}
		} else if (backValue == "Fail") {
			return;
		}
	} else {
		// 提示验证通过，并进行保存
		var flag = JSPFree.doBillCardUpdate(d1_1_BillCard,null);

		var tablename_en = d1_1_BillCard.templetVO.templet_option.savetable;  
		var pkvalue = d1_1_BillCard.OldData.rid;  
		if(flag){
			var jso_rt = JSPFree.doClassMethodCall("com.yusys.crrs.rulevalidate.service.CrrsResultBSDMO", 
					"updateResultDataNew",{tab:result_tab_name,tablename_en:tablename_en,pkvalue:pkvalue});
			JSPFree.alert("保存成功,同时更改了相关校验结果的状态为【完成】");
		}
	}
}
