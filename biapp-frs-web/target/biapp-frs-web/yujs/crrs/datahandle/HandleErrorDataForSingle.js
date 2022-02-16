//单一法人客户 -- 校验结果数据处理界面
var type = null;// 传的是：确定性校验（1）/一致性校验（2）/提示性校验（3）
var str_customercode = null;
var str_reportdate = null;
var str_sqlCons = "";  //查询条件
var result_tab_name = null;
var isLoadTabb_2 = false;
var isLoadTabb_3 = false;
var isLoadTabb_4 = false;
var isLoadTabb_5 = false;
var isLoadTabb_6 = false;
var isLoadTabb_7 = false;
var isLoadTabb_8 = false;
var isLoadTabb_9 = false;
var ary_title = [];
function AfterInit(){
	//从前面页面获得客户编码,即根据这个进行查询!
	str_customercode = jso_OpenPars2.customercode;
	str_reportdate = jso_OpenPars2.reportdate;
	var jso_ErrorCount = jso_OpenPars2.ErrorCount;  //错误总数
	result_tab_name = jso_OpenPars2.tab;  //校验结果表名
	type = jso_OpenPars2.type;  //校验类型
	
	//动态创建标题,即如果有错误数据,则有图标提醒
	ary_title.push(getTitleText("基本信息","crrs_single_corporation",jso_ErrorCount)); //主表
	ary_title.push(getTitleText("高管及重要联系人","crrs_single_executives",jso_ErrorCount));
	ary_title.push(getTitleText("重要股东及主要关联企业信息","crrs_single_shareholder_ep",jso_ErrorCount));
	ary_title.push(getTitleText("授信信息","crrs_ent_credit",jso_ErrorCount));
	ary_title.push(getTitleText("贷款信息","crrs_ent_loan",jso_ErrorCount));
	ary_title.push(getTitleText("表外业务","crrs_ent_offbalance_sa",jso_ErrorCount));
	ary_title.push(getTitleText("担保信息","crrs_ent_guaranteed",jso_ErrorCount));
	ary_title.push(getTitleText("债券信息","crrs_ent_bond",jso_ErrorCount));
	ary_title.push(getTitleText("股权信息","crrs_ent_equitystake",jso_ErrorCount));

	//创建多页签
	JSPFree.createTabb("d1",ary_title,[100,150,210,100,100,100,100,100,100]);

	//定义查询条件
    str_sqlCons="customer_code='" + str_customercode + "' and data_dt = '"+str_reportdate+"'"; 

	//主表卡片
	var ary_CardBtn = ["保存/onSave/icon-ok"];
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
    
	JSPFree.createBillCard("d1_1","/biapp-crrs/freexml/crrs/customer/crrs_single_corporation_CODE1.xml",ary_CardBtn,jso_CardConfig);

	JSPFree.addTabbSelectChangedListener(d1_tabb,onSelect);
}

function onSelect(_index,_title){
	//各子表的列表
	var jso_ListConfig = null;
	if ("1" == type) {
		jso_ListConfig = {
			list_ispagebar: "Y",
			ishavebillquery: "Y",
			isSwitchQuery:"N",
			autoquery: "N",
			list_btns: "[icon-p41]修改错误数据/onUpdateErrorData",
			afterloadclass: "com.yusys.crrs.datahandle.service.AfterErrorDataLoadClass",
			isafterloadsetcolor: "Y",
			querycontion:"customer_code='"+str_customercode+"' and data_dt = '"+str_reportdate+"'"
		};
	} else if ("2" == type) {
		jso_ListConfig = {
			list_ispagebar: "Y",
			ishavebillquery: "Y",
			isSwitchQuery:"N",
			autoquery: "N",
			list_btns: "[icon-p41]修改错误数据/onUpdateErrorData",
			afterloadclass: "com.yusys.crrs.datahandle.service.AfterErrorDataLoadClass1",
			isafterloadsetcolor: "Y",
			querycontion:"customer_code='"+str_customercode+"' and data_dt = '"+str_reportdate+"'"
		};
	} else if ("3" == type) {
		jso_ListConfig = {
			list_ispagebar: "Y",
			ishavebillquery: "Y",
			isSwitchQuery:"N",
			autoquery: "N",
			list_btns: "[icon-p41]修改错误数据/onUpdateErrorData",
			afterloadclass: "com.yusys.crrs.datahandle.service.AfterErrorDataLoadClass2",
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
			JSPFree.createBillList("d1_2","/biapp-crrs/freexml/crrs/customer/crrs_single_executives_CODE1.xml",null,jso_ListConfig);  //高管及重要联系人
			$.parser.parse('#d1_2');
			//查询各子表的列表数据
			JSPFree.queryDataByConditon(d1_2_BillList,str_sqlCons);  //高管及重要联系人
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
			JSPFree.createBillList("d1_3","/biapp-crrs/freexml/crrs/customer/crrs_single_shareholder_ep_CODE1.xml",null,jso_ListConfig);  //重要股东及主要关联企业信息
			$.parser.parse('#d1_3');
			JSPFree.queryDataByConditon(d1_3_BillList,str_sqlCons);  //重要股东及主要关联企业信息
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
			JSPFree.createBillList("d1_4","/biapp-crrs/freexml/crrs/ent/crrs_ent_credit_CODE3.xml",null,jso_ListConfig);  //授信信息
			$.parser.parse('#d1_4');
			JSPFree.queryDataByConditon(d1_4_BillList,str_sqlCons);  //授信信息
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
			JSPFree.createBillList("d1_5","/biapp-crrs/freexml/crrs/ent/crrs_ent_loan_CODE1.xml",null,jso_ListConfig);  //贷款信息
			$.parser.parse('#d1_5');
			JSPFree.queryDataByConditon(d1_5_BillList,str_sqlCons);  //贷款信息
			isLoadTabb_5 = true;
		}
		//没有错误信息时，将按钮置灰
		if(ary_title[4].indexOf("/icon-p25") != -1){
			$("#d1_5_BillList_Btn修改错误数据").linkbutton('enable');
		}else{
			$("#d1_5_BillList_Btn修改错误数据").linkbutton('disable');
		}
	}

	if(newIndex==6){
		if(!isLoadTabb_6){
			JSPFree.createBillList("d1_6","/biapp-crrs/freexml/crrs/ent/crrs_ent_offbalance_sa_CODE1.xml",null,jso_ListConfig);  //表外业务
			$.parser.parse('#d1_6');
			JSPFree.queryDataByConditon(d1_6_BillList,str_sqlCons);  //表外业务
			isLoadTabb_6 = true;
		}
		//没有错误信息时，将按钮置灰
		if(ary_title[5].indexOf("/icon-p25") != -1){
			$("#d1_6_BillList_Btn修改错误数据").linkbutton('enable');
		}else{
			$("#d1_6_BillList_Btn修改错误数据").linkbutton('disable');
		}
	}

	if(newIndex==7){
		if(!isLoadTabb_7){
			JSPFree.createBillList("d1_7","/biapp-crrs/freexml/crrs/ent/crrs_ent_guaranteed_CODE1.xml",null,jso_ListConfig);  //担保信息
			$.parser.parse('#d1_7');
			JSPFree.queryDataByConditon(d1_7_BillList,str_sqlCons);  //担保信息
			isLoadTabb_7 = true;
		}
		//没有错误信息时，将按钮置灰
		if(ary_title[6].indexOf("/icon-p25") != -1){
			$("#d1_7_BillList_Btn修改错误数据").linkbutton('enable');
		}else{
			$("#d1_7_BillList_Btn修改错误数据").linkbutton('disable');
		}
	}

	if(newIndex==8){
		if(!isLoadTabb_8){
			JSPFree.createBillList("d1_8","/biapp-crrs/freexml/crrs/ent/crrs_ent_bond_CODE2.xml",null,jso_ListConfig);  //债券信息
			$.parser.parse('#d1_8');
			JSPFree.queryDataByConditon(d1_8_BillList,str_sqlCons);  //债券信息
			isLoadTabb_8 = true;
		}
		//没有错误信息时，将按钮置灰
		if(ary_title[7].indexOf("/icon-p25") != -1){
			$("#d1_8_BillList_Btn修改错误数据").linkbutton('enable');
		}else{
			$("#d1_8_BillList_Btn修改错误数据").linkbutton('disable');
		}
	}

	if(newIndex==9){
		if(!isLoadTabb_9){
			JSPFree.createBillList("d1_9","/biapp-crrs/freexml/crrs/ent/crrs_ent_equitystake_CODE2.xml",null,jso_ListConfig);  //股权信息
			$.parser.parse('#d1_9');
			JSPFree.queryDataByConditon(d1_9_BillList,str_sqlCons);  //股权信息
			isLoadTabb_9 = true;
		}
		//没有错误信息时，将按钮置灰
		if(ary_title[8].indexOf("/icon-p25") != -1){
			$("#d1_9_BillList_Btn修改错误数据").linkbutton('enable');
		}else{
			$("#d1_9_BillList_Btn修改错误数据").linkbutton('disable');
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
		type:type
	};

	//打开窗口
	JSPFree.openDialog2("修正数据-" + str_templetname,"/yujs/crrs/datahandle/UpdateHandleErrorDetailDialog.js",950,560,jso_par,function(_rtData){
		if(null != _rtData.msg){
			JSPFree.refreshBillListCurrPage(billList);  //立即刷新数据
			$.messager.show(_rtData);
		}
	},true);  //
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
	// 单条数据校验
	var backValue = JSPFree.editTableCheckData(d1_1_BillCard, "Edit", "单一法人基本情况", "CRRS_SINGLE_CORPORATION","","2");
	if (backValue == "" || "undifind" == backValue) {
		return;
	} else if (backValue == "OK") {
		// 提示验证通过，并进行保存
		var flag = JSPFree.doBillCardUpdate(d1_1_BillCard,null);
		
		var tablename_en = d1_1_BillCard.templetVO.templet_option.savetable;  
		var pkvalue = d1_1_BillCard.OldData.rid;  
		if(flag){
			var jso_rt = JSPFree.doClassMethodCall("com.yusys.crrs.rulevalidate.service.CrrsResultBSDMO", 
					"updateResultData",{tab:result_tab_name,tablename_en:tablename_en,pkvalue:pkvalue});
			JSPFree.alert("校验并保存成功,同时更改了相关校验结果的状态为【完成】");
		}
	} else if (backValue == "Fail") {
		return;
	}
}
