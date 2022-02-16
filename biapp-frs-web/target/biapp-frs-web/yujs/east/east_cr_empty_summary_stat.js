//总行完整性评估
function AfterInit() {
	var str_org_no = jso_OpenPars.org_no;
	var str_data_dt = jso_OpenPars.data_dt;
	JSPFree.createBillList("d1","east_cr_report_eval_ZH",null,{list_btns:"$UPDATE",autoquery:"N",list_ispagebar:"Y",ishavebillquery:"Y",list_ischeckstyle:"N",list_ismultisel:"N"});

	var str_sqlWhere = "org_no='"  + str_org_no + "' and data_dt = '"+ str_data_dt +"'";
	JSPFree.queryDataByConditon(d1_BillList,str_sqlWhere);
	
	JSPFree.setBillListForceSQLWhere(d1_BillList,str_sqlWhere);
}