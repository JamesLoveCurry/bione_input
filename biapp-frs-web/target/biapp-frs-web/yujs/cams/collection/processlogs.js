var data_id = "";
var data_type = "";
function AfterInit(){
	data_id = jso_OpenPars.data_id;
	data_type = jso_OpenPars.data_type;
	JSPFree.createBillList("d1","/biapp-cams/freexml/cams/cams_data_log_CODE1.xml",null,{isSwitchQuery:"N",autoquery:"N",ishavebillquery:"N"});
	
	JSPFree.queryDataByConditon2(d1_BillList, getQueryCondition());
	JSPFree.setBillListForceSQLWhere(d1_BillList,getQueryCondition());
}

//查询列表加载默认查询条件，用于灵活查询
function getQueryCondition(){
	var condition = "data_id = '"+data_id+"' and data_type = '"+data_type+"'";
	
	return condition;
}