//账户查询【/frs/yufreejs?js=/yujs/cams/query/acctDataQuery.js】
function AfterInit(){
	JSPFree.createTabb("d1", [ "个人账户", "机构账户" ]);
	JSPFree.createBillList("d1_1","/biapp-cams/freexml/cams/query/cams_indv_acct_ref.xml",null,{isSwitchQuery:"N",autoquery:"Y",list_ispagebar:"Y"});
	JSPFree.createBillList("d1_2","/biapp-cams/freexml/cams/query/cams_corp_acct_ref.xml",null,{isSwitchQuery:"N",autoquery:"Y",list_ispagebar:"Y"});

	JSPFree.queryDataByConditon2(d1_1_BillList, getQueryCondition());
	JSPFree.setBillListForceSQLWhere(d1_1_BillList,getQueryCondition());

	JSPFree.queryDataByConditon2(d1_2_BillList, getQueryCondition());
	JSPFree.setBillListForceSQLWhere(d1_2_BillList,getQueryCondition());
}

//查询列表加载默认查询条件
function getQueryCondition(){
	var jso_rt = JSPFree.doClassMethodCall("com.yusys.cams.validate.service.ValidateQueryCondition","getQueryCondition",{"_loginUserOrgNo" : str_LoginUserOrgNo});
	var condition = "";
	if(jso_rt.msg == "ok"){
		condition = jso_rt.condition;
	}

	return condition;
}

/**
 * 个人账户：查看
 */
function onView1(){
	var indv = d1_1_BillList.datagrid('getSelected');
	if (indv == null) {
		$.messager.alert('提示', '必须选择一条记录进行操作', 'warning');
		return;
	}
	
	JSPFree.openDialog2("个人账户","/yujs/cams/query/viewIndv.js",950,600,{indv:indv},function(_rtdata){

	},true);
}

/**
 * 机构账户：查看
 */
function onView2(){
	var corp = d1_2_BillList.datagrid('getSelected');
	if (corp == null) {
		$.messager.alert('提示', '必须选择一条记录进行操作', 'warning');
		return;
	}
	
	JSPFree.openDialog2("机构账户","/yujs/cams/query/viewCorp.js",950,600,{corp:corp},function(_rtdata){

	},true);
}
