//初始化界面,菜单配置路径是【/frs/yufreejs?js=/yujs/crrs/collection/addSingle.js】
var executive = null;
var executiveId = null;
var str_data_dt = null; //保存之后的日期
var defaultValue = ""; //这个defaultValue是给OnSave准备的，保存之后把高管的默认值传给护照和证件
function AfterInit(){
	JSPFree.createSplit("d1","上下",250);
	JSPFree.createBillCard("d1_A","/biapp-crrs/freexml/crrs/group/crrs_group_executives_CODE1.xml",["保存/onSave/icon-ok"],null);
	//赋值
	JSPFree.setBillCardValues(d1_A_BillCard,jso_OpenPars); //这里的参数不是defaultValue的原因:jso_OpenPars获取的参数就是上一级传下来的默认值
	
	executiveId = jso_OpenPars.executives_id;
	JSPFree.createSplit("d1_B","左右",490);
	JSPFree.createBillList("d1_B_A","/biapp-crrs/freexml/crrs/group/crrs_passport_CODE1.xml",null,{autoquery:"N",ishavebillquery:"N",list_ispagebar:"N"});
	JSPFree.createBillList("d1_B_B","/biapp-crrs/freexml/crrs/group/crrs_paper_CODE1.xml",null,{autoquery:"N",ishavebillquery:"N",list_ispagebar:"N"});
}

function AfterBodyLoad() {
	if (executiveId == null) {
		JSPFree.setBillListBtnEnable(d1_B_A_BillList, "新增", false);
		JSPFree.setBillListBtnEnable(d1_B_A_BillList, "编辑", false);
		JSPFree.setBillListBtnEnable(d1_B_A_BillList, "删除", false);
		
		JSPFree.setBillListBtnEnable(d1_B_B_BillList, "新增", false);
		JSPFree.setBillListBtnEnable(d1_B_B_BillList, "编辑", false);
		JSPFree.setBillListBtnEnable(d1_B_B_BillList, "删除", false);
	} else {
		JSPFree.setBillListBtnEnable(d1_B_A_BillList, "新增", true);
		JSPFree.setBillListBtnEnable(d1_B_A_BillList, "编辑", true);
		JSPFree.setBillListBtnEnable(d1_B_A_BillList, "删除", true);
		
		JSPFree.setBillListBtnEnable(d1_B_B_BillList, "新增", true);
		JSPFree.setBillListBtnEnable(d1_B_B_BillList, "编辑", true);
		JSPFree.setBillListBtnEnable(d1_B_B_BillList, "删除", true);
	}
}

//新增护照
function addData1() {
	defaultValue={issued_no:jso_OpenPars.issued_no};
	var defaultVal = {type:"Add",template:"/biapp-crrs/freexml/crrs/group/crrs_passport_CODE1.xml",
			tabname:"护照",tabnameen:"CRRS_PASSPORT",defaultValue:defaultValue};
	
	JSPFree.openDialog("护照信息","/yujs/crrs/collection/check_data_edit.js",900,560,defaultVal,function(_rtdata){
		if (_rtdata == "校验并保存成功") {
			JSPFree.alert("校验并保存成功");
			JSPFree.queryDataByConditon(d1_B_A_BillList,"executives_id='"+executiveId+"' and data_dt='"+str_data_dt+"'"); //立即查询刷新数据
		}
	},true);
}

//修改护照
function editData1() {
	// 获取选中的数据的rid
	var json_data = d1_B_A_BillList.datagrid('getSelections');
	var rid = json_data[0].rid;
	defaultValue={issued_no:jso_OpenPars.issued_no};
	var defaultVal = {rid:rid,type:"Edit",template:"/biapp-crrs/freexml/crrs/group/crrs_passport_CODE1.xml",
			tabname:"护照",tabnameen:"CRRS_PASSPORT",defaultValue:defaultValue};
	
	JSPFree.openDialog("护照信息","/yujs/crrs/collection/check_data_edit.js",900,560,defaultVal,function(_rtdata){
		if (_rtdata == "校验并保存成功") {
			JSPFree.alert("校验并保存成功");
			JSPFree.queryDataByConditon(d1_B_A_BillList,"executives_id='"+executiveId+"' and data_dt='"+str_data_dt+"'"); //立即查询刷新数据
		}
	},true);
}

//新增其他证件
function addData2() {
	defaultValue={issued_no:jso_OpenPars.issued_no};
	var defaultVal = {type:"Add",template:"/biapp-crrs/freexml/crrs/group/crrs_paper_CODE1.xml",
			tabname:"其他证件",tabnameen:"CRRS_PAPER",defaultValue:defaultValue};
	
	JSPFree.openDialog("其他证件","/yujs/crrs/collection/check_data_edit.js",900,560,defaultVal,function(_rtdata){
		if (_rtdata == "校验并保存成功") {
			JSPFree.alert("校验并保存成功");
			JSPFree.queryDataByConditon(d1_B_B_BillList,"executives_id='"+executiveId+"' and data_dt='"+str_data_dt+"'"); //立即查询刷新数据
		}
	},true);
}

//修改其他证件
function editData2() {
	// 获取选中的数据的rid
	var json_data = d1_B_B_BillList.datagrid('getSelections');
	var rid = json_data[0].rid;
	defaultValue={issued_no:jso_OpenPars.issued_no};
	var defaultVal = {rid:rid,type:"Edit",template:"/biapp-crrs/freexml/crrs/group/crrs_paper_CODE1.xml",
			tabname:"其他证件",tabnameen:"CRRS_PAPER",defaultValue:defaultValue};
	
	JSPFree.openDialog("其他证件","/yujs/crrs/collection/check_data_edit.js",900,560,defaultVal,function(_rtdata){
		if (_rtdata == "校验并保存成功") {
			JSPFree.alert("校验并保存成功");
			JSPFree.queryDataByConditon(d1_B_B_BillList,"executives_id='"+executiveId+"' and data_dt='"+str_data_dt+"'"); //立即查询刷新数据
		}
	},true);
}

/**
 * 保存
 * @return {[type]} [description]
 */
var saveFlag;
function onSave(){
	// 是否进行单条数据检核操作
	var jso_check = JSPFree.doClassMethodCall("com.yusys.crrs.collection.service.CrrsCollectionBS", "getCrrsCheckProperties", {});
	var ischeck = jso_check.ischeck;
	if ("Y" == ischeck) {
		if(!saveFlag){
			JSPFree.setBillCardValues(d1_A_BillCard,{rid:FreeUtil.getUUIDFromServer(),executives_id:FreeUtil.getUUIDFromServer(),issued_no:jso_OpenPars.issued_no});

			var backValue = JSPFree.editTableCheckData(d1_A_BillCard, "Add", "高管及重要关联人信息", "CRRS_GROUP_EXECUTIVES","","2");
			if (backValue == "" || "undifind" == backValue) {
				return;
			} else if (backValue == "OK") {
				saveFlag = JSPFree.doBillCardInsert(d1_A_BillCard,null);
			} else if (backValue == "Fail") {
				return;
			}
		} else {
			var backValue = JSPFree.editTableCheckData(d1_A_BillCard, "Edit", "高管及重要关联人信息", "CRRS_GROUP_EXECUTIVES","","2");
			if (backValue == "" || "undifind" == backValue) {
				return;
			} else if (backValue == "OK") {
				saveFlag = JSPFree.doBillCardUpdate(d1_A_BillCard,null);
			} else if (backValue == "Fail") {
				return;
			}
		}
		
		if(saveFlag){
			//设置默认值
			executive = JSPFree.getBillCardFormValue(d1_A_BillCard);
			defaultValue ={executives_id:executive.executives_id,customer_type:"1",data_dt:executive.data_dt,issued_no:jso_OpenPars.issued_no};
			JSPFree.setDefaultValues(d1_B_A_BillList,defaultValue);
			JSPFree.setDefaultValues(d1_B_B_BillList,defaultValue);

			JSPFree.alert("校验并保存成功");
			executiveId = executive.executives_id;
			str_data_dt = executive.data_dt;
			AfterBodyLoad();
		}
	} else {
		if(!saveFlag){
			JSPFree.setBillCardValues(d1_A_BillCard,{rid:FreeUtil.getUUIDFromServer(),executives_id:FreeUtil.getUUIDFromServer(),issued_no:jso_OpenPars.issued_no});
			saveFlag = JSPFree.doBillCardInsert(d1_A_BillCard,null);
		} else {
			saveFlag = JSPFree.doBillCardUpdate(d1_A_BillCard,null);
		}
		
		if(saveFlag){
			//设置默认值
			executive = JSPFree.getBillCardFormValue(d1_A_BillCard);
			defaultValue ={executives_id:executive.executives_id,customer_type:"1",data_dt:executive.data_dt,issued_no:jso_OpenPars.issued_no};
			JSPFree.setDefaultValues(d1_B_A_BillList,defaultValue);
			JSPFree.setDefaultValues(d1_B_B_BillList,defaultValue);

			JSPFree.alert("保存成功");
			executiveId = executive.executives_id;
			str_data_dt = executive.data_dt;
			AfterBodyLoad();
		}
	}
}