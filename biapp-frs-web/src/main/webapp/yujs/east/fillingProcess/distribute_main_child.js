//初始化界面,菜单配置路径是【/frs/yufreejs?js=/yujs/east/fillingProcess/distribute_main_child.js】
var task_id = "";
function AfterInit(){
	JSPFree.createBillList("d1","/biapp-east/freexml/east/fillingProcess/east_filling_process_child_CODE2.xml",
			null,{isSwitchQuery:"N",ishavebillquery:"N",autoquery:"N"});

	task_id = jso_OpenPars2.data.rid;
	JSPFree.queryDataByConditon2(d1_BillList, getQueryCondition());
	JSPFree.setBillListForceSQLWhere(d1_BillList,getQueryCondition());
}

//查询列表加载默认查询条件，用于灵活查询
function getQueryCondition(){
	var condition = "task_id = '"+task_id+"'";
	
	return condition;
}