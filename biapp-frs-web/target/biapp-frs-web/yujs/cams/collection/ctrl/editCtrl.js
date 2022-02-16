//初始化界面,菜单配置路径是【/frs/yufreejs?js=/yujs/crrs/collection/addSingle.js】
var ctrl = null;
var defaultValue = "";
var isLoadTabb_2 = false;
var isLoadTabb_3 = false;
var isLoadTabb_4 = false;
function AfterInit(){
	ctrl = jso_OpenPars2.ctrl;
	JSPFree.createTabb("d1",["控制人信息","姓名信息","地址信息","TIN信息"]);

	JSPFree.createBillCard("d1_1","/biapp-cams/freexml/cams/cams_corp_ctrl_CODE1.xml",["保存/onSave/icon-ok"],null);
	//赋值
	JSPFree.queryBillCardData(d1_1_BillCard, "rid = '"+ctrl.rid+"'");
	
	JSPFree.addTabbSelectChangedListener(d1_tabb, onSelect);
	defaultValue = {data_id:ctrl.rid,data_type:"2"};
}

function onSelect(_index,_title){
	var newIndex = _index+1;
	
	if(newIndex==1){
		
	}
	
	if(newIndex==2){
		if(!isLoadTabb_2){
			var def = {data_id:ctrl.rid,data_type:"2",first_name:ctrl.first_name,last_name:ctrl.last_name};
			JSPFree.createBillList("d1_2","/biapp-cams/freexml/cams/cams_indv_name_CODE1.xml",null,{ishavebillquery:"N",isSwitchQuery:"N",autoquery:"Y",autocondition:"data_id='"+ctrl.rid+"' and data_type='2'",querycontion:"data_id='"+ctrl.rid+"' and data_type='2'"});
			$.parser.parse('#d1_2');
			JSPFree.setDefaultValues(d1_2_BillList,def);
			isLoadTabb_2 = true;
		}
	}

	if(newIndex==3){
		if(!isLoadTabb_3){
			JSPFree.createBillList("d1_3","/biapp-cams/freexml/cams/cams_indv_addr_CODE1.xml",null,{ishavebillquery:"N",isSwitchQuery:"N",autoquery:"Y",autocondition:"data_id='"+ctrl.rid+"' and data_type='2'",querycontion:"data_id='"+ctrl.rid+"' and data_type='2'"});
			$.parser.parse('#d1_3');
			JSPFree.setDefaultValues(d1_3_BillList,defaultValue);
			isLoadTabb_3 = true;
		}
	}

	if(newIndex==4){
		if(!isLoadTabb_4){
			var def = {data_id:ctrl.rid,data_type:"2",in_type:"TIN"};
			JSPFree.createBillList("d1_4","/biapp-cams/freexml/cams/cams_indv_tin_CODE1.xml",null,{ishavebillquery:"N",isSwitchQuery:"N",autoquery:"Y",autocondition:"data_id='"+ctrl.rid+"' and data_type='2'",querycontion:"data_id='"+ctrl.rid+"' and data_type='2'"});
			$.parser.parse('#d1_4');
			JSPFree.setDefaultValues(d1_4_BillList,def);
			isLoadTabb_4 = true;
		}
	}
}

//新增：姓名信息
function addData2() {
	var def = {data_id:ctrl.rid,data_type:"2",first_name:ctrl.first_name,last_name:ctrl.last_name};
	var defaultVal = {type:"Add",template:"/biapp-cams/freexml/cams/cams_indv_name_CODE1.xml",
			tabname:"个人名称信息",tabnameen:"CAMS_INDV_NAME",defaultValue:def};
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
	var def = {data_id:ctrl.rid,data_type:"2",first_name:ctrl.first_name,last_name:ctrl.last_name};
	var defaultVal = {rid:rid,type:"Edit",template:"/biapp-cams/freexml/cams/cams_indv_name_CODE1.xml",
			tabname:"个人名称信息",tabnameen:"CAMS_INDV_NAME",defaultValue:def};
	JSPFree.openDialog("姓名信息","/yujs/cams/collection/check_data_edit.js",900,560,defaultVal,function(_rtdata){
		if (_rtdata == "校验并保存成功") {
			JSPFree.alert("校验并保存成功");
			JSPFree.refreshBillListCurrRow(d1_2_BillList); // 刷新当前行
		}
	},true);
}

//新增：地址信息
function addData3() {
	var defaultVal = {type:"Add",template:"/biapp-cams/freexml/cams/cams_indv_addr_CODE1.xml",
			tabname:"个人地址信息",tabnameen:"CAMS_INDV_ADDR",defaultValue:defaultValue};
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
	var defaultVal = {rid:rid,type:"Edit",template:"/biapp-cams/freexml/cams/cams_indv_addr_CODE1.xml",
			tabname:"个人地址信息",tabnameen:"CAMS_INDV_ADDR",defaultValue:defaultValue};
	JSPFree.openDialog("地址信息","/yujs/cams/collection/check_data_edit.js",900,560,defaultVal,function(_rtdata){
		if (_rtdata == "校验并保存成功") {
			JSPFree.alert("校验并保存成功");
			JSPFree.refreshBillListCurrRow(d1_3_BillList); // 刷新当前行
		}
	},true);
}

//新增：TIN信息
function addData4() {
	var def = {data_id:ctrl.rid,data_type:"2",in_type:"TIN"};
	var defaultVal = {type:"Add",template:"/biapp-cams/freexml/cams/cams_indv_tin_CODE1.xml",
			tabname:"个人TIN信息",tabnameen:"CAMS_INDV_TIN",defaultValue:def};
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
	var def = {data_id:ctrl.rid,data_type:"2",in_type:"TIN"};
	var defaultVal = {rid:rid,type:"Edit",template:"/biapp-cams/freexml/cams/cams_indv_tin_CODE1.xml",
			tabname:"个人TIN信息",tabnameen:"CAMS_INDV_TIN",defaultValue:def};
	JSPFree.openDialog("TIN信息","/yujs/cams/collection/check_data_edit.js",900,560,defaultVal,function(_rtdata){
		if (_rtdata == "校验并保存成功") {
			JSPFree.alert("校验并保存成功");
			JSPFree.refreshBillListCurrRow(d1_4_BillList); // 刷新当前行
		}
	},true);
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
	var backValue = JSPFree.editTableCheckData(d1_1_BillCard, "Edit", "机构账户控制人信息", "CAMS_CORP_CTRL","","4");
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
