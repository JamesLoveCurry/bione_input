var child_task_id = "";
function AfterInit(){
	child_task_id = jso_OpenPars.child_task_id;
	JSPFree.createBillList("d1","/biapp-wmp/freexml/wmp/wmp_filling_reason_CODE1.xml",null,{isSwitchQuery:"N",autoquery:"N",ishavebillquery:"N"});
	
	JSPFree.queryDataByConditon2(d1_BillList, getQueryCondition());
	JSPFree.setBillListForceSQLWhere(d1_BillList,getQueryCondition());
}

//查询列表加载默认查询条件，用于灵活查询
function getQueryCondition(){
	var condition = "child_task_id = '"+child_task_id+"'";
	
	return condition;
}