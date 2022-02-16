//初始化界面,菜单配置路径是【/frs/yufreejs?js=/yujs/crrs/xxx/xxx.js】
var str_hasBaseBtn;
function AfterInit(){
	var str_customer_code = jso_OpenPars2.customer_code;
	var str_customer_name = jso_OpenPars2.customer_name;
    str_hasBaseBtn = jso_OpenPars2.hasBaseBtn;
	var str_status = jso_OpenPars2.status; // 判断主数据是否已锁定
	
	if(str_hasBaseBtn){
		//创建列表
		JSPFree.createBillList("d1","/biapp-crrs/freexml/crrs/group/crrs_group_executives_CODE1.xml",null,{list_btns:"$VIEW;[icon-p20]护照信息/crrsPassport;[icon-p11]其他证件/crrsPaper;",autoquery:"N"});
	}
	else{
		if ("锁定" == str_status) {
			JSPFree.createBillList("d1","/biapp-crrs/freexml/crrs/group/crrs_group_executives_CODE1.xml",null,{list_btns:"$VIEW;",autoquery:"N"});
		} else {
			JSPFree.createBillList("d1","/biapp-crrs/freexml/crrs/group/crrs_group_executives_CODE1.xml",null,{autoquery:"N"});
		}
	}

    //设置列表的默认值对象 
	d1_BillList.DefaultValues={customer_code:str_customer_code,customer_name:str_customer_name};

	var str_sqlWhere = "customer_code='"  + str_customer_code + "'";  //拼SQL条件
	JSPFree.setBillListForceSQLWhere(d1_BillList,str_sqlWhere);
	JSPFree.queryDataByConditon(d1_BillList,null);  //锁定规则表查询数据
}

// 护照信息
function crrsPassport() {
	var jso_OpenPars = d1_BillList.datagrid('getSelected');
	if (jso_OpenPars == null) {
		$.messager.alert('提示', '必须选择一条记录进行操作', 'warning');
		return;
	}

	//弹出窗口,传入参数,然后接收返回值!
	JSPFree.openDialog2("护照信息","/yujs/crrs/group/Passport.js",900,600,{executives_id:jso_OpenPars.executives_id, hasBaseBtn:str_hasBaseBtn, status:str_status},function(_rtdata){
		
	});
}

// 其他证件
function crrsPaper() {
	var jso_OpenPars = d1_BillList.datagrid('getSelected');
	if (jso_OpenPars == null) {
		$.messager.alert('提示', '必须选择一条记录进行操作', 'warning');
		return;
	}

	//弹出窗口,传入参数,然后接收返回值!
	JSPFree.openDialog2("其他证件","/yujs/crrs/group/Paper.js",900,600,{executives_id:jso_OpenPars.executives_id, hasBaseBtn:str_hasBaseBtn, status:str_status},function(_rtdata){
		
	});
}