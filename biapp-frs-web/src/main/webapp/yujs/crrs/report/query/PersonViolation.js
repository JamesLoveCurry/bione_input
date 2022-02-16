//初始化界面,菜单配置路径是【/frs/yufreejs?js=/yujs/east/58table.js】
function AfterInit(){
	var str_customer_code = jso_OpenPars.customer_code;
	var str_data_dt = jso_OpenPars.data_dt;
	// 创建列表
	JSPFree.createSplit("d1", "上下", 280);
	JSPFree.createBillCard("d1_A","/biapp-crrs/freexml/crrs/generateXml/query/query_PersonViolation_CODE1.xml");

	JSPFree.createTabb("d1_B", ["共同债务人", "助学贷款专项指标"], 160);
	JSPFree.createBillList("d1_B_1", "/biapp-crrs/freexml/crrs/ref/crrs_person_joint_debtor_ref.xml",null,{isSwitchQuery:"N", list_btns:"", ishavebillquery:"N", autoquery:"N"});
	JSPFree.createBillList("d1_B_2", "/biapp-crrs/freexml/crrs/ref/crrs_person_student_loan_ref.xml",null,{isSwitchQuery:"N", list_btns:"", ishavebillquery:"N", autoquery:"N"});

	var str_sqlWhere = "customer_code='"  + str_customer_code + "' and data_dt = '" + str_data_dt + "'";  //拼SQL条件
	var jso_data = JSPFree.queryBillCardData(d1_A_BillCard, str_sqlWhere);

	JSPFree.queryDataByConditon(d1_B_1_BillList, str_sqlWhere); 
	JSPFree.queryDataByConditon(d1_B_2_BillList, str_sqlWhere);
}

function AfterBodyLoad() {
	FreeUtil.setBillCardItemEditable(d1_A_BillCard,"*",false);
}