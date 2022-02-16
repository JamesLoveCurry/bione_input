//初始化界面,菜单配置路径是【/frs/yufreejs?js=/yujs/crrs/xxx/xxx.js】
function AfterInit(){
	JSPFree.createBillList("d1","/biapp-crrs/freexml/crrs/ent/crrs_ent_trade_info_CODE1.xml");
}

// 授信信息
function entCredit() {
	var jso_OpenPars = d1_BillList.datagrid('getSelected');
	if (jso_OpenPars == null) {
		$.messager.alert('提示', '必须选择一条记录进行操作', 'warning');
		return;
	}
	//弹出窗口,传入参数,然后接收返回值!
	JSPFree.openDialog2("授信信息","/yujs/crrs/ent/EntCreditTy.js",900,600,
			{customer_code:jso_OpenPars.customer_code,customer_name:jso_OpenPars.customer_name},function(_rtdata){
		
	});
}

// 业务明细
function entTradeCustomers() {
	var jso_OpenPars = d1_BillList.datagrid('getSelected');
	if (jso_OpenPars == null) {
		$.messager.alert('提示', '必须选择一条记录进行操作', 'warning');
		return;
	}
	//弹出窗口,传入参数,然后接收返回值!
	JSPFree.openDialog2("业务明细","/yujs/crrs/ent/EntTradeCustomers.js",900,600,
			{customer_code:jso_OpenPars.customer_code,customer_name:jso_OpenPars.customer_name},function(_rtdata){
		
	});
}
