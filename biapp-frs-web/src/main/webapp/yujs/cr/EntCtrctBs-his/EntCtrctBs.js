//初始化界面,菜单配置路径是
function AfterInit(){
	JSPFree.createSplit("d1","上下",300);
	JSPFree.createBillList("d1_A","/biapp-cr/freexml/hentctrctbs/EntCreditInformation.xml");
	JSPFree.createTabb("d1_B",["共同受信人信息","额度信息"]);
	JSPFree.createBillList("d1_B_1","/biapp-cr/freexml/hentctrctbs/EntCredit1.xml");
	JSPFree.createBillList("d1_B_2","/biapp-cr/freexml/hentctrctbs/EntCredit2.xml");
	
	
	
	JSPFree.bindSelectEvent(d1_A_BillList,onSelectIDSgmt);  //选择人员基础信息时触发的事件
	
	//选择人员基础信息时触发的事件,即查询该人员的其他信息!
function onSelectIDSgmt(rowIndex, rowData){
	
	var str_deptid = rowData["contractcode"];  //授信协议标识码
	//alert(str_deptid);
	JSPFree.queryDataByConditon(d1_B_1_BillList,"ContractCode='" + str_deptid + "'");  

	JSPFree.setDefaultValues(d1_B_1_BillList,{"ContractCode":str_deptid});
	JSPFree.queryDataByConditon(d1_B_2_BillList,"ContractCode='" + str_deptid + "'");  

	JSPFree.setDefaultValues(d1_B_2_BillList,{"ContractCode":str_deptid});
}
}


