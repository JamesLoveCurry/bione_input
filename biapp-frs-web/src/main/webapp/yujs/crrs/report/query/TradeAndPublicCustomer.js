//初始化界面,菜单配置路径是【/frs/yufreejs?js=/yujs/east/58table.js】
function AfterInit(){
	var str_customer_code = jso_OpenPars.customer_code;
	var str_customer_type = jso_OpenPars.customer_type;
	var str_data_dt = jso_OpenPars.data_dt;

	// 创建列表
	JSPFree.createSplit("d1", "上下", 120);
	JSPFree.createBillList("d1_A","/biapp-crrs/freexml/crrs/ref/crrs_ent_credit_ref.xml");

	var str_sqlWhere = "customer_code='" + str_customer_code + "' and data_dt = '" + str_data_dt + "'"; //拼SQL条件
	JSPFree.queryDataByConditon(d1_A_BillList,str_sqlWhere);

	if (str_customer_type != null && str_customer_type == '1') {
		JSPFree.createTabb("d1_B", [ "授信拆分情况", "贷款明细", "持有企业债券明细", "持有企业股权明细", "表外业务明细" ]);
		JSPFree.createBillList("d1_B_1", "/biapp-crrs/freexml/crrs/ref/crrs_ent_group_client_ref.xml",null,{autoquery:"N",ishavebillquery:"N",list_btns:""});
		JSPFree.createBillList("d1_B_2", "/biapp-crrs/freexml/crrs/ref/crrs_ent_loan_ref.xml",null,{autoquery:"N",ishavebillquery:"N",list_btns:""});
		JSPFree.createBillList("d1_B_3", "/biapp-crrs/freexml/crrs/ref/crrs_ent_bond_ref.xml",null,{autoquery:"N",ishavebillquery:"N",list_btns:""});
		JSPFree.createBillList("d1_B_4", "/biapp-crrs/freexml/crrs/ref/crrs_ent_equitystake_ref.xml",null,{autoquery:"N",ishavebillquery:"N",list_btns:""});
		JSPFree.createBillList("d1_B_5", "/biapp-crrs/freexml/crrs/ref/crrs_ent_offbalance_sa_ref.xml",null,{autoquery:"N",ishavebillquery:"N",list_btns:""});
	} else if (str_customer_type != null && str_customer_type == '2') {
		JSPFree.createTabb("d1_B", [ "贷款明细", "持有企业债券明细", "持有企业股权明细", "表外业务明细" ]);
		JSPFree.createBillList("d1_B_1", "/biapp-crrs/freexml/crrs/ref/crrs_ent_loan_ref.xml",null,{autoquery:"N",ishavebillquery:"N",list_btns:""});
		JSPFree.createBillList("d1_B_2", "/biapp-crrs/freexml/crrs/ref/crrs_ent_bond_ref.xml",null,{autoquery:"N",ishavebillquery:"N",list_btns:""});
		JSPFree.createBillList("d1_B_3", "/biapp-crrs/freexml/crrs/ref/crrs_ent_equitystake_ref.xml",null,{autoquery:"N",ishavebillquery:"N",list_btns:""});
		JSPFree.createBillList("d1_B_4", "/biapp-crrs/freexml/crrs/ref/crrs_ent_offbalance_sa_ref.xml",null,{autoquery:"N",ishavebillquery:"N",list_btns:""});
	} else if (str_customer_type != null && str_customer_type == '3') {
		JSPFree.createTabb("d1_B", [ "业务明细" ]);
		JSPFree.createBillList("d1_B_1", "/biapp-crrs/freexml/crrs/ref/crrs_ent_trade_customers_ref.xml",null,{autoquery:"N",ishavebillquery:"N",list_btns:""});
	}
	
	JSPFree.bindSelectEvent(d1_A_BillList,function(_row,_rowData){
		doQuery(str_customer_type, str_customer_code, _rowData.credit_code, _rowData.data_dt);
	});
}

function doQuery(type, code, credit_code, data_dt){
	var str_sqlWhere = "customer_code='" + code + "' and data_dt = '"+ data_dt +"'"; //拼SQL条件
	var str_sqlWhere1 = "credit_code = '" + credit_code +"' and customer_code in (select member_code from crrs_group_members where customer_code = '" + code + "' and data_dt = '" + data_dt + "') and data_dt = '" + data_dt + "'"; //拼SQL条件
	var str_sqlWhere2 = "customer_code = '" + code +"' and credit_code='" + credit_code + "' and data_dt = '"+ data_dt +"'"; //拼SQL条件
	if (type != null && (type == '1')) {
		JSPFree.queryDataByConditon(d1_B_1_BillList,str_sqlWhere2);
		JSPFree.queryDataByConditon(d1_B_2_BillList,str_sqlWhere1);
		JSPFree.queryDataByConditon(d1_B_3_BillList,str_sqlWhere1);
		JSPFree.queryDataByConditon(d1_B_4_BillList,str_sqlWhere1);
		JSPFree.queryDataByConditon(d1_B_5_BillList,str_sqlWhere1);
	} else if (type != null && type == '2') {
		JSPFree.queryDataByConditon(d1_B_1_BillList,str_sqlWhere2);
		JSPFree.queryDataByConditon(d1_B_2_BillList,str_sqlWhere2);
		JSPFree.queryDataByConditon(d1_B_3_BillList,str_sqlWhere2);
		JSPFree.queryDataByConditon(d1_B_4_BillList,str_sqlWhere2);
	} else if (type != null && type == '3') {
		JSPFree.queryDataByConditon(d1_B_1_BillList,str_sqlWhere);
	}
}