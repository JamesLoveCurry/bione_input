var str_templetcode = ""; // 模板编码
var str_templetname = ""; // 模板名称
var str_sqlWhere = ""; // SQL条件
var str_tab = ""; // 
var type = null;// 传的是：确定性校验（1）/一致性校验（2）/提示性校验（3）
var fromtable = null;
var rowdata = null;

var executiveId = null;

function AfterInit() {
	str_templetcode = jso_OpenPars2.tempeltcode; // 模板编码
	str_templetname = jso_OpenPars2.tempeltname; // 模板名称
	str_sqlWhere = jso_OpenPars2.SQLWhere; // SQL条件
	str_tab = jso_OpenPars2.tab;
	type = jso_OpenPars2.type; //校验类型
	fromtable = jso_OpenPars2.fromtable; // 表名
	rowdata = jso_OpenPars2.rowdata; // 表名
	
	// 主表卡片
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

	// 以下内容，高管会涉及
	executiveId = jso_OpenPars2.rowdata.executives_id;
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
			querycontion:"executives_id= '"+executiveId+"'"
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
			querycontion:"executives_id= '"+executiveId+"'"
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
			querycontion:"executives_id= '"+executiveId+"'"
		};
	}
	
	//创建卡片
	if (fromtable  == "crrs_group_executives" || fromtable  == "crrs_single_executives") { // 集团高管 或者 单一法人高管
		JSPFree.createSplit("d1","上下",250);
		JSPFree.createBillCard("d1_A", str_templetcode, ary_CardBtn, jso_CardConfig);
		
		
		JSPFree.createSplit("d1_B","左右",490);
		JSPFree.createBillList("d1_B_A","/biapp-crrs/freexml/crrs/group/crrs_passport_CODE1.xml",null,jso_ListConfig);
		JSPFree.createBillList("d1_B_B","/biapp-crrs/freexml/crrs/group/crrs_paper_CODE1.xml",null,jso_ListConfig);

	} else {
		JSPFree.createBillCard("d1", str_templetcode, ary_CardBtn, jso_CardConfig);
	}
}

function AfterBodyLoad() {
	// 查询主表卡片数据
	if (fromtable  == "crrs_group_executives" || fromtable  == "crrs_single_executives") { // 集团高管 或者 单一法人高管
		JSPFree.queryBillCardData(d1_A_BillCard, str_sqlWhere, "Y");
		JSPFree.setBillCardLabelHelptip(d1_A_BillCard); // 设置帮助说明 必须写在最后一行
		
		JSPFree.queryDataByConditon(d1_B_A_BillList, null);
		JSPFree.queryDataByConditon(d1_B_B_BillList, null);
	} else {
		JSPFree.queryBillCardData(d1_BillCard, str_sqlWhere, "Y");
		JSPFree.setBillCardLabelHelptip(d1_BillCard); // 设置帮助说明 必须写在最后一行
	}
}

// 隐藏提示
function onHiddenErr() {
	if (fromtable  == "crrs_group_executives" || fromtable  == "crrs_single_executives") { // 集团高管 或者 单一法人高管
		JSPFree.setBillCardItemWarnMsgVisible(d1_A_BillCard, false);
	} else {
		JSPFree.setBillCardItemWarnMsgVisible(d1_BillCard, false);
	}
}

function onShowErr() {
	if (fromtable  == "crrs_group_executives" || fromtable  == "crrs_single_executives") { // 集团高管 或者 单一法人高管
		JSPFree.setBillCardItemWarnMsgVisible(d1_A_BillCard, true);
	} else {
		JSPFree.setBillCardItemWarnMsgVisible(d1_BillCard, true);
	}
}

function onSave() {
	var tablename_en = d1_1_BillCard.templetVO.templet_option.savetable;
	var pkvalue = d1_A_BillCard.OldData.rid; 

	// 根据英文表名，找中文表名
	var tablename = "";
	var tablename_jor = JSPFree.getHashVOs("select DISTINCT tablename from crrs_rule where tablename_en = '"+tablename_en.toUpperCase()+"'");
	if(tablename_jor.length>0){
		tablename = tablename_jor[0].tablename
	}
	
	if (fromtable  == "crrs_group_executives" || fromtable  == "crrs_single_executives") { // 集团高管 或者 单一法人高管
		try {
			// 单条数据校验
			var backValue = JSPFree.editTableCheckData(d1_A_BillCard, "Edit", tablename, tablename_en.toUpperCase(),"","2");
			if (backValue == "" || "undifind" == backValue) {
				return;
			} else if (backValue == "OK") {
				// 提示验证通过，并进行保存
				var flag = JSPFree.doBillCardUpdate(d1_1_BillCard,null);
				
				if(flag){
					var jso_rt = JSPFree.doClassMethodCall("com.yusys.crrs.rulevalidate.service.CrrsResultBSDMO", 
							"updateResultData",{tab:result_tab_name,tablename_en:tablename_en,pkvalue:pkvalue});
					JSPFree.alert("校验并保存成功,同时更改了相关校验结果的状态为【完成】<br>同时更改了相关校验结果的状态为【完成】");
				}
			} else if (backValue == "Fail") {
				return;
			}
		} catch (_ex) {
			console.log(_ex);
			FreeUtil.openHtmlMsgBox2("发生异常", 800, 250, _ex)
		}
	} else {
		try {
			// 单条数据校验
			var backValue = JSPFree.editTableCheckData(d1_A_BillCard, "Edit", tablename, tablename_en.toUpperCase(),"","2");
			if (backValue == "" || "undifind" == backValue) {
				return;
			} else if (backValue == "OK") {
				// 提示验证通过，并进行保存
				var flag = JSPFree.doBillCardUpdate(d1_1_BillCard,null);
				
				if(flag){
					var jso_rt = JSPFree.doClassMethodCall("com.yusys.crrs.rulevalidate.service.CrrsResultBSDMO", 
							"updateResultData",{tab:result_tab_name,tablename_en:tablename_en,pkvalue:pkvalue});
					JSPFree.alert("校验并保存成功,同时更改了相关校验结果的状态为【完成】<br>同时更改了相关校验结果的状态为【完成】");
				}
			} else if (backValue == "Fail") {
				return;
			}
		} catch (_ex) {
			console.log(_ex);
			FreeUtil.openHtmlMsgBox2("发生异常", 800, 250, _ex)
		}
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
		tab:str_fromtable,
		type:type
	};

	//打开窗口
	JSPFree.openDialog2("修正数据-" + str_templetname,"/yujs/crrs/datahandle/UpdateHandleErrorDetailDialog.js",950,560,jso_par,function(_rtData){
		if(null != _rtData.msg){
			JSPFree.refreshBillListCurrPage(billList);  //立即刷新数据
			$.messager.show(_rtData);
		}
	},true);
}
