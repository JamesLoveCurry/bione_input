//初始化界面,菜单配置路径是【/frs/yufreejs?js=/yujs/crrs/collection/addSingle.js】
var corp = null;
var defaultValue = "";
var isLoadTabb_2 = false;
var isLoadTabb_3 = false;
var isLoadTabb_4 = false;
var isLoadTabb_5 = false;
function AfterInit(){
	corp = jso_OpenPars2.corp;
	var jsy_btns = ["提交/onSaveSubmit/icon-ok","查看日志/onLogs/icon-ok","返回/onBack/icon-ok"];
	JSPFree.createTabbByBtn("d1",["账户信息","姓名信息","地址信息","TIN信息","控制人信息"],jsy_btns,false);

	JSPFree.createBillCard("d1_1","/biapp-cams/freexml/cams/cams_corp_acct_CODE1.xml",["保存/onSave/icon-ok"],null);
	//赋值
	JSPFree.queryBillCardData(d1_1_BillCard, "rid = '"+corp.rid+"'");
	
	JSPFree.addTabbSelectChangedListener(d1_tabb, onSelect);
	defaultValue = {account_number:corp.account_number};
}

function onSelect(_index,_title){
	var newIndex = _index+1;
	
	if(newIndex==1){
		
	}
	
	if(newIndex==2){
		if(!isLoadTabb_2){
			JSPFree.createBillList("d1_2","/biapp-cams/freexml/cams/cams_corp_name_CODE1.xml",null,{ishavebillquery:"N",isSwitchQuery:"N",autoquery:"Y",autocondition:"account_number='"+corp.account_number+"'",querycontion:"account_number='"+corp.account_number+"'"});
			$.parser.parse('#d1_2');
			JSPFree.setDefaultValues(d1_2_BillList,defaultValue);
			isLoadTabb_2 = true;
		}
	}

	if(newIndex==3){
		if(!isLoadTabb_3){
			JSPFree.createBillList("d1_3","/biapp-cams/freexml/cams/cams_corp_addr_CODE1.xml",null,{ishavebillquery:"N",isSwitchQuery:"N",autoquery:"Y",autocondition:"account_number='"+corp.account_number+"'",querycontion:"account_number='"+corp.account_number+"'"});
			$.parser.parse('#d1_3');
			JSPFree.setDefaultValues(d1_3_BillList,defaultValue);
			isLoadTabb_3 = true;
		}
	}

	if(newIndex==4){
		if(!isLoadTabb_4){
			var def = {account_number:corp.account_number,in_type:"TIN"};
			JSPFree.createBillList("d1_4","/biapp-cams/freexml/cams/cams_corp_tin_CODE1.xml",null,{ishavebillquery:"N",isSwitchQuery:"N",autoquery:"Y",autocondition:"account_number='"+corp.account_number+"'",querycontion:"account_number='"+corp.account_number+"'"});
			$.parser.parse('#d1_4');
			JSPFree.setDefaultValues(d1_4_BillList,def);
			isLoadTabb_4 = true;
		}
	}

	if(newIndex==5){
		if(!isLoadTabb_5){
			JSPFree.createBillList("d1_5","/biapp-cams/freexml/cams/cams_corp_ctrl_CODE1.xml",null,{ishavebillquery:"N",isSwitchQuery:"N",autoquery:"Y",autocondition:"account_number='"+corp.account_number+"'",querycontion:"account_number='"+corp.account_number+"'"});
			$.parser.parse('#d1_5');
			JSPFree.setDefaultValues(d1_5_BillList,defaultValue);
			isLoadTabb_5 = true;
		}
	}
}

//新增：姓名信息
function addData2() {
	var defaultVal = {type:"Add",template:"/biapp-cams/freexml/cams/cams_corp_name_CODE1.xml",
			tabname:"机构名称信息",tabnameen:"CAMS_CORP_NAME",defaultValue:defaultValue};
	JSPFree.openDialog("姓名信息","/yujs/cams/collection/check_data_edit.js",900,560,defaultVal,function(_rtdata){
		if (_rtdata == "校验并保存成功") {
			JSPFree.queryDataByConditon(d1_2_BillList);
			JSPFree.alert("校验并保存成功");
		}
	},true);
}

//修改:姓名信息
function editData2() {
	// 获取选中的数据的rid
	var json_data = d1_2_BillList.datagrid('getSelections');
	var rid = json_data[0].rid;
	var defaultVal = {rid:rid,type:"Edit",template:"/biapp-cams/freexml/cams/cams_corp_name_CODE1.xml",
			tabname:"机构名称信息",tabnameen:"CAMS_CORP_NAME",defaultValue:defaultValue};
	JSPFree.openDialog("姓名信息","/yujs/cams/collection/check_data_edit.js",900,560,defaultVal,function(_rtdata){
		if (_rtdata == "校验并保存成功") {
			JSPFree.alert("校验并保存成功");
			JSPFree.refreshBillListCurrRow(d1_2_BillList); // 刷新当前行
		}
	},true);
}

//新增：地址信息
function addData3() {
	var defaultVal = {type:"Add",template:"/biapp-cams/freexml/cams/cams_corp_addr_CODE1.xml",
			tabname:"机构地址信息",tabnameen:"CAMS_CORP_ADDR",defaultValue:defaultValue};
	JSPFree.openDialog("地址信息","/yujs/cams/collection/check_data_edit.js",900,560,defaultVal,function(_rtdata){
		if (_rtdata == "校验并保存成功") {
			JSPFree.queryDataByConditon(d1_3_BillList);
			JSPFree.alert("校验并保存成功");
		}
	},true);
}

//修改:地址信息
function editData3() {
	// 获取选中的数据的rid
	var json_data = d1_3_BillList.datagrid('getSelections');
	var rid = json_data[0].rid;
	var defaultVal = {rid:rid,type:"Edit",template:"/biapp-cams/freexml/cams/cams_corp_addr_CODE1.xml",
			tabname:"机构地址信息",tabnameen:"CAMS_CORP_ADDR",defaultValue:defaultValue};
	JSPFree.openDialog("地址信息","/yujs/cams/collection/check_data_edit.js",900,560,defaultVal,function(_rtdata){
		if (_rtdata == "校验并保存成功") {
			JSPFree.alert("校验并保存成功");
			JSPFree.refreshBillListCurrRow(d1_3_BillList); // 刷新当前行
		}
	},true);
}

//新增：TIN信息
function addData4() {
	var def = {account_number:corp.account_number,in_type:"TIN"};
	var defaultVal = {type:"Add",template:"/biapp-cams/freexml/cams/cams_corp_tin_CODE1.xml",
			tabname:"机构TIN信息",tabnameen:"CAMS_CORP_TIN",defaultValue:def};
	JSPFree.openDialog("TIN信息","/yujs/cams/collection/check_data_edit.js",900,560,defaultVal,function(_rtdata){
		if (_rtdata == "校验并保存成功") {
			JSPFree.queryDataByConditon(d1_4_BillList);
			JSPFree.alert("校验并保存成功");
		}
	},true);
}

//修改:TIN信息
function editData4() {
	// 获取选中的数据的rid
	var json_data = d1_4_BillList.datagrid('getSelections');
	var rid = json_data[0].rid;
	var def = {account_number:corp.account_number,in_type:"TIN"};
	var defaultVal = {rid:rid,type:"Edit",template:"/biapp-cams/freexml/cams/cams_corp_tin_CODE1.xml",
			tabname:"机构TIN信息",tabnameen:"CAMS_CORP_TIN",defaultValue:def};
	JSPFree.openDialog("TIN信息","/yujs/cams/collection/check_data_edit.js",900,560,defaultVal,function(_rtdata){
		if (_rtdata == "校验并保存成功") {
			JSPFree.alert("校验并保存成功");
			JSPFree.refreshBillListCurrRow(d1_4_BillList); // 刷新当前行
		}
	},true);
}

//新增：控制人信息
function addData5() {
	JSPFree.openDialog("控制人信息","/yujs/cams/collection/ctrl/addCtrl.js", 950,600, {def:defaultValue},function(_rtdata){
		JSPFree.queryDataByConditon(d1_5_BillList,null);  //立即查询刷新数据
	},true);
}

//修改:控制人信息
function editData5() {
	var json_data = d1_5_BillList.datagrid('getSelections');
	if (json_data == null || json_data == undefined || json_data.length ==0) {
		$.messager.alert('提示', '必须选择一条记录进行操作', 'warning');
		return;
	}
	if (json_data.length>1) {
		$.messager.alert('提示', '只能选择一条记录进行操作', 'warning');
		return;
	}
	// 只能修改状态为“初始化”、“退回”的数据
	if (corp.status != '1' && corp.status != '4') { // 1初始化2待复核3待审批4退回5完成
		var ss = "";
		if(corp.status == '1'){
			ss = "初始化";
		}else if(corp.status == '2'){
			ss = "待复核";
		}else if(corp.status == '3'){
			ss = "待审批";
		}else if(corp.status == '4'){
			ss = "退回";
		}else if(corp.status == '5'){
			ss = "完成";
		}
		$.messager.alert('提示', '当前数据状态为【'+ss+'】，不可修改', 'warning');
		
		return;
	}
	
	JSPFree.openDialog2("控制人信息","/yujs/cams/collection/ctrl/editCtrl.js",950,600,{ctrl:json_data[0]},function(_rtData){
		if (_rtData != null) {
			if ("dirclose" == _rtData.type) {
				return;
			} 
			
			if (_rtData) {
				$.messager.alert('提示', '提交数据成功!', 'info');
				JSPFree.refreshBillListCurrPage(d1_5_BillList);
			}
		}
	},true);
	
	/*// 获取选中的数据的rid
	var json_data = d1_5_BillList.datagrid('getSelections');
	var rid = json_data[0].rid;
	var defaultVal = {rid:rid,type:"Edit",template:"/biapp-cams/freexml/cams/cams_corp_ctrl_CODE1.xml",
			tabname:"机构账户控制人信息",tabnameen:"CAMS_CORP_CTRL",defaultValue:defaultValue};
	JSPFree.openDialog("控制人信息","/yujs/cams/collection/check_data_edit.js",900,560,defaultVal,function(_rtdata){
		if (_rtdata == "校验并保存成功") {
			JSPFree.alert("校验并保存成功");
			JSPFree.refreshBillListCurrRow(d1_5_BillList); // 刷新当前行
		}
	},true);*/
}

/**
 * 保存
 * @return {[type]} [description]
 */
function onSave(){
	var flag = JSPFree.doBillCardUpdate(d1_1_BillCard,null);
	if(flag){
		JSPFree.alert("校验并保存成功");
	}
	/*// 单条数据校验
	var backValue = JSPFree.editTableCheckData(d1_1_BillCard, "Edit", "个人账户信息", "CAMS_CORP_ACCT","","4");
	if (backValue == "" || "undifind" == backValue) {
		return;
	} else if (backValue == "OK") {
		// 提示验证通过，并进行保存
		var flag = JSPFree.doBillCardUpdate(d1_1_BillCard,null);
		if(flag){
			JSPFree.alert("校验并保存成功");
		}
	} else if (backValue == "Fail") {
		return;
	}*/
}

// 提交
function onSaveSubmit() {
	var def = {rid:corp.rid,user_no:str_LoginUserCode,data_type:"corp"};
	
	JSPFree.confirm('提示', '你确定将数据提交至复核岗?', function(_isOK){
		if (_isOK){
			var jsn_result = JSPFree.doClassMethodCall("com.yusys.cams.corp.service.CamsCorpBSDMO", "submitData", def);
			if (jsn_result.msg == 'OK') {
				JSPFree.closeDialog(true);
			}
		}
	});
}

// 查看日志
function onLogs() {
	var jso_par = {data_id:corp.rid,data_type:"corp"};
	JSPFree.openDialog("处理日志信息","/yujs/cams/collection/processlogs.js",900,600,jso_par,function(_rtData){
		if (_rtData != null) {
			if ("dirclose" == _rtData.type) {
				return;
			}
		}
	});
}

// 返回
function onBack() {
	JSPFree.closeDialog(false);
}
