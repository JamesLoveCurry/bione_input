//初始化界面,菜单配置路径是【/frs/yufreejs?js=/yujs/crrs/collection/addSingle.js】
var executive = null;
var group = null;
var defaultValue = "";
function AfterInit(){
	group = jso_OpenPars2.group;
	JSPFree.createSplit("d1","上下",250);
	JSPFree.createBillCard("d1_A","/biapp-crrs/freexml/crrs/group/crrs_group_executives_CODE1.xml",["保存/onSave/icon-ok"],null);
	//赋值
	JSPFree.setBillCardValues(d1_A_BillCard,jso_OpenPars2.executive);
	d1_A_BillCard.OldData = jso_OpenPars2.executive;

	var executiveId = jso_OpenPars2.executive.executives_id;
	var str_data_dt = jso_OpenPars2.executive.data_dt;
	var str_issued_no = jso_OpenPars2.executive.issued_no;
	JSPFree.createSplit("d1_B","左右",490);
	JSPFree.createBillList("d1_B_A","/biapp-crrs/freexml/crrs/group/crrs_passport_CODE1.xml",null,{isSwitchQuery:"N",autoquery:"Y",ishavebillquery:"N",list_ispagebar:"N",
		autocondition:"executives_id='"+executiveId+"' and data_dt='"+str_data_dt+"'",querycontion:"executives_id='"+executiveId+"' and data_dt='"+str_data_dt+"'"});
	JSPFree.createBillList("d1_B_B","/biapp-crrs/freexml/crrs/group/crrs_paper_CODE1.xml",null,{isSwitchQuery:"N",autoquery:"Y",ishavebillquery:"N",list_ispagebar:"N",
		autocondition:"executives_id='"+executiveId+"' and data_dt='"+str_data_dt+"'",querycontion:"executives_id='"+executiveId+"' and data_dt='"+str_data_dt+"'"});

	defaultValue ={executives_id:executiveId,customer_type:"1",data_dt:str_data_dt,issued_no:str_issued_no};
	JSPFree.setDefaultValues(d1_B_A_BillList,defaultValue);
	JSPFree.setDefaultValues(d1_B_B_BillList,defaultValue);
}

function AfterBodyLoad(){
	if ("锁定" == group.status) {
		$("#d1_A_BillCardBtn1").linkbutton('disable');  //按钮失效
		$("#d1_A_BillCardBtn1" ).attr("title","因为锁定，所以无法进行处理");
		
		JSPFree.setBillListBtnEnable(d1_B_A_BillList, "新增", false);
		JSPFree.setBillListBtnEnable(d1_B_A_BillList, "编辑", false);
		JSPFree.setBillListBtnEnable(d1_B_A_BillList, "删除", false);
		
		JSPFree.setBillListBtnEnable(d1_B_B_BillList, "新增", false);
		JSPFree.setBillListBtnEnable(d1_B_B_BillList, "编辑", false);
		JSPFree.setBillListBtnEnable(d1_B_B_BillList, "删除", false);
		
		JSPFree.setBillListBtnTip(d1_B_A_BillList, "新增", "因为锁定，所以无法进行处理");
		JSPFree.setBillListBtnTip(d1_B_A_BillList, "编辑", "因为锁定，所以无法进行处理");
		JSPFree.setBillListBtnTip(d1_B_A_BillList, "删除", "因为锁定，所以无法进行处理");
		
		JSPFree.setBillListBtnTip(d1_B_B_BillList, "新增", "因为锁定，所以无法进行处理");
		JSPFree.setBillListBtnTip(d1_B_B_BillList, "编辑", "因为锁定，所以无法进行处理");
		JSPFree.setBillListBtnTip(d1_B_B_BillList, "删除", "因为锁定，所以无法进行处理");
	}
}

//新增护照
function addData1() {
	var defaultVal = {type:"Add",template:"/biapp-crrs/freexml/crrs/group/crrs_passport_CODE1.xml",
			tabname:"护照",tabnameen:"CRRS_PASSPORT",defaultValue:defaultValue};
	JSPFree.openDialog("护照信息","/yujs/crrs/collection/check_data_edit.js",900,560,defaultVal,function(_rtdata){
		JSPFree.queryDataByConditon(d1_B_A_BillList,null);  //立即查询刷新数据
	},true);
}

//修改护照
function editData1() {
	// 获取选中的数据的rid
	var json_data = d1_B_A_BillList.datagrid('getSelections');
	var rid = json_data[0].rid;
	var defaultVal = {rid:rid,type:"Edit",template:"/biapp-crrs/freexml/crrs/group/crrs_passport_CODE1.xml",
			tabname:"护照",tabnameen:"CRRS_PASSPORT",defaultValue:defaultValue};
	JSPFree.openDialog("护照信息","/yujs/crrs/collection/check_data_edit.js",900,560,defaultVal,function(_rtdata){
		JSPFree.queryDataByConditon(d1_B_A_BillList,null);  //立即查询刷新数据
	},true);
}

//新增其他证件
function addData2() {
	var defaultVal = {type:"Add",template:"/biapp-crrs/freexml/crrs/group/crrs_paper_CODE1.xml",
			tabname:"其他证件",tabnameen:"CRRS_PAPER",defaultValue:defaultValue};
	JSPFree.openDialog("其他证件","/yujs/crrs/collection/check_data_edit.js",900,560,defaultVal,function(_rtdata){
		JSPFree.queryDataByConditon(d1_B_B_BillList,null);  //立即查询刷新数据
	},true);
}

//修改其他证件
function editData2() {
	// 获取选中的数据的rid
	var json_data = d1_B_B_BillList.datagrid('getSelections');
	var rid = json_data[0].rid;
	var defaultVal = {rid:rid,type:"Edit",template:"/biapp-crrs/freexml/crrs/group/crrs_paper_CODE1.xml",
			tabname:"其他证件",tabnameen:"CRRS_PAPER",defaultValue:defaultValue};
	JSPFree.openDialog("其他证件","/yujs/crrs/collection/check_data_edit.js",900,560,defaultVal,function(_rtdata){
		JSPFree.queryDataByConditon(d1_B_B_BillList,null);  //立即查询刷新数据
	},true);
}

/**
 * 保存
 * @return {[type]} [description]
 */
function onSave(){
	// 是否进行单条数据检核操作
	var jso_check = JSPFree.doClassMethodCall("com.yusys.crrs.collection.service.CrrsCollectionBS", "getCrrsCheckProperties", {});
	var ischeck = jso_check.ischeck;
	if ("Y" == ischeck) {
		var backValue = JSPFree.editTableCheckData(d1_A_BillCard, "Edit", "高管及重要关联人信息", "CRRS_GROUP_EXECUTIVES","","2");
		if (backValue == "" || "undifind" == backValue) {
			return;
		} else if (backValue == "OK") {
			// 提示验证通过，并进行保存
			var flag = JSPFree.doBillCardUpdate(d1_A_BillCard,null);
			if(flag){
				JSPFree.alert("校验并保存成功");
			}
		} else if (backValue == "Fail") {
			return;
		}
	} else {
		var flag = JSPFree.doBillCardUpdate(d1_A_BillCard,null);
		if(flag){
			JSPFree.alert("保存成功");
		}
	}
}