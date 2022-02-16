//初始化界面,菜单配置路径是【/frs/yufreejs?js=/yujs/crrs/xxx/xxx.js】
function AfterInit(){
	JSPFree.createBillList("d1","/biapp-crrs/freexml/crrs/group/crrs_group_group_client_CODE2.xml");
}

// 集团成员
function members() {
	var jso_OpenPars = d1_BillList.datagrid('getSelected');
	if (jso_OpenPars == null) {
		$.messager.alert('提示', '必须选择一条记录进行操作', 'warning');
		return;
	}
	//弹出窗口,传入参数,然后接收返回值!
	JSPFree.openDialog2("集团成员","/yujs/crrs/group/GroupMembers1.js",900,600,
			{customer_code:jso_OpenPars.customer_code,customer_name:jso_OpenPars.customer_name},function(_rtdata){
		
	});
}

//授信信息
function entCredit() {
	var jso_OpenPars = d1_BillList.datagrid('getSelected');
	if (jso_OpenPars == null) {
		$.messager.alert('提示', '必须选择一条记录进行操作', 'warning');
		return;
	}
	//弹出窗口,传入参数,然后接收返回值!
	JSPFree.openDialog2("授信信息","/yujs/crrs/ent/EntCreditGyl.js",900,600,
			{customer_code:jso_OpenPars.customer_code,customer_name:jso_OpenPars.customer_name},function(_rtdata){
		
	});
}

//授信拆分情况
function entGroupClient() {
	var jso_OpenPars = d1_BillList.datagrid('getSelected');
	if (jso_OpenPars == null) {
		$.messager.alert('提示', '必须选择一条记录进行操作', 'warning');
		return;
	}
	//弹出窗口,传入参数,然后接收返回值!
	JSPFree.openDialog2("授信拆分情况","/yujs/crrs/ent/EntGroupClientGroup.js",900,600,
			{customer_code:jso_OpenPars.customer_code,customer_name:jso_OpenPars.customer_name},function(_rtdata){
		
	});
}