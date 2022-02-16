var rowId;

function AfterInit(){
	JSPFree.createSplit("d1", "上下", 300);
	JSPFree.createBillList("d1_A","/biapp-cr/freexml/entLoanInfo-his/EntLoanInformation.xml");
	JSPFree.createTabb("d1_B",["基本信息","授信额度","抵质押物","还款表现","相关还款责任人","初始债权说明","特定交易说明"]);
	
	JSPFree.createBillList("d1_B_1","/biapp-cr/freexml/entLoanInfo-his/EntAcctBsInfo.xml");
	JSPFree.createBillList("d1_B_2","/biapp-cr/freexml/entLoanInfo-his/EntAcctCred.xml");
	JSPFree.createBillList("d1_B_3","/biapp-cr/freexml/entLoanInfo-his/EntMotgaCltalCtrctInfo.xml");
	JSPFree.createBillList("d1_B_4","/biapp-cr/freexml/entLoanInfo-his/EntActLbltyInfo.xml");
	JSPFree.createBillList("d1_B_5","/biapp-cr/freexml/entLoanInfo-his/EntRltRepymtInfo.xml");
	JSPFree.createBillList("d1_B_6","/biapp-cr/freexml/entLoanInfo-his/EntOrigCreditorInfo.xml");
	JSPFree.createBillList("d1_B_7","/biapp-cr/freexml/entLoanInfo-his/EntAcctSpecTrstDspnInfo.xml");
	
	JSPFree.bindSelectEvent(d1_A_BillList, onSelectDept); 
}

function onSelectDept(rowIndex, rowData) {
	var rowId = rowData["acctcode"];
	
	JSPFree.queryDataByConditon(d1_B_1_BillList, "ACCTCODE='" + rowId + "'");
	JSPFree.setDefaultValues(d1_B_1_BillList,{"ACCTCODE":rowId});
	
	JSPFree.queryDataByConditon(d1_B_2_BillList, "ACCTCODE='" + rowId + "'");
	JSPFree.setDefaultValues(d1_B_2_BillList,{"ACCTCODE":rowId});
	
	JSPFree.queryDataByConditon(d1_B_3_BillList, "ACCTCODE='" + rowId + "'");
	JSPFree.setDefaultValues(d1_B_3_BillList,{"ACCTCODE":rowId});
	
	JSPFree.queryDataByConditon(d1_B_4_BillList, "ACCTCODE='" + rowId + "'");
	JSPFree.setDefaultValues(d1_B_4_BillList,{"ACCTCODE":rowId});
	
	JSPFree.queryDataByConditon(d1_B_5_BillList, "ACCTCODE='" + rowId + "'");
	JSPFree.setDefaultValues(d1_B_5_BillList,{"ACCTCODE":rowId});
	
	JSPFree.queryDataByConditon(d1_B_6_BillList, "ACCTCODE='" + rowId + "'");
	JSPFree.setDefaultValues(d1_B_6_BillList,{"ACCTCODE":rowId});
	
	JSPFree.queryDataByConditon(d1_B_7_BillList, "ACCTCODE='" + rowId + "'");
	JSPFree.setDefaultValues(d1_B_7_BillList,{"ACCTCODE":rowId});
	
	
	
}
