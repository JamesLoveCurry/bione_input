//初始企业担保基础信息
function AfterInit(){
	JSPFree.createSplit("d1","上下",300);
	JSPFree.createBillList("d1_A","/biapp-cr/freexml/entguarxml/GuarBasicInfomation.xml");
	//创建相关联信息tab标签
	JSPFree.createTabb("d1_B",["基本信息","相关还款责任人","抵质押物信息","授信额度信息","在保责任信息"]);
	//基本信息
	JSPFree.createBillList("d1_B_1","/biapp-cr/freexml/entguarxml/GuarAcctBsInf.xml");
	//相关还款责任人
	JSPFree.createBillList("d1_B_2","/biapp-cr/freexml/entguarxml/RltRepymtInf.xml");
	//抵质押物信息
	JSPFree.createBillList("d1_B_3","/biapp-cr/freexml/entguarxml/GuarMotgaCltalCtrctInf.xml");
	//授信额度信息
	JSPFree.createBillList("d1_B_4","/biapp-cr/freexml/entguarxml/GuarAcctCred.xml");
	//在保责任信息
	JSPFree.createBillList("d1_B_5","/biapp-cr/freexml/entguarxml/GuarRltRepymtInf.xml");
	//选择某条企业担保基础信息时触发的事件
	JSPFree.bindSelectEvent(d1_A_BillList,onSelectIDSgmt); 
	
}

//选择某条企业担保基础信息时触发的事件，加载其他关联信息
function onSelectIDSgmt(rowIndex, rowData){
	
	var AcctCode = rowData["acctcode"];  //账户标识码
	
	JSPFree.queryDataByConditon(d1_B_1_BillList,"AcctCode='" + AcctCode + "'");
	JSPFree.setDefaultValues(d1_B_1_BillList,{"AcctCode":AcctCode});
	
	JSPFree.queryDataByConditon(d1_B_2_BillList,"AcctCode='" + AcctCode + "'");  
	JSPFree.setDefaultValues(d1_B_2_BillList,{"AcctCode":AcctCode});
	
	JSPFree.queryDataByConditon(d1_B_3_BillList,"AcctCode='" + AcctCode + "'");  
	JSPFree.setDefaultValues(d1_B_3_BillList,{"AcctCode":AcctCode});
	
	JSPFree.queryDataByConditon(d1_B_4_BillList,"AcctCode='" + AcctCode + "'");  
	JSPFree.setDefaultValues(d1_B_4_BillList,{"AcctCode":AcctCode});
	
	JSPFree.queryDataByConditon(d1_B_5_BillList,"AcctCode='" + AcctCode + "'");  
	JSPFree.setDefaultValues(d1_B_5_BillList,{"AcctCode":AcctCode});
}