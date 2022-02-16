//初始化界面,菜单配置路径是【/frs/yufreejs?js=/yujs/crrs/xxx/xxx.js】
function AfterInit(){
	JSPFree.createBillList("d1","/biapp-crrs/freexml/crrs/group/crrs_group_group_client_CODE3.xml",null,{querycontion:"type='1'",autocondition:"type='1'",autoquery:"N",isSwitchQuery:"N"});

	JSPFree.queryDataByConditon2(d1_BillList, getQueryCondition());
	JSPFree.setBillListForceSQLWhere(d1_BillList,getQueryCondition());
}
//查询列表加载默认查询条件，用于灵活查询
function getQueryCondition(){
	var jso_rt = JSPFree.doClassMethodCall("com.yusys.crrs.customer.service.ValidateQueryCondition","getQueryCondition",{"_loginUserOrgNo" : str_LoginUserOrgNo});
	var condition = "";
	if(jso_rt.msg == "ok"){
		condition = jso_rt.condition;
	}
	return condition;
}


function onView(){
	var group = d1_BillList.datagrid('getSelected');
	if (group == null) {
		$.messager.alert('提示', '必须选择一条记录进行操作', 'warning');
		return;
	}

	JSPFree.openDialog2("基础数据","/yujs/crrs/dataquery/viewGroup.js",950,600,{group:group},function(_rtdata){

	},true);
}