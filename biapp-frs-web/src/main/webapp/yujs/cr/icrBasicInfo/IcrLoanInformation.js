//初始化界面,菜单配置路径是
function AfterInit(){
	//JSPFree.createBillList("d1","/biapp-cr/freexml/icrBasicInformation.xml",null,{isSwitchQuery:"N"});
	
	JSPFree.createSplit("d1","上下",300);  //先是上下分割界面

	JSPFree.createBillList("d1_A","/biapp-cr/freexml/icrBasicInfoxml/IcrLoanInformation.xml");  //上面是基础段信息
	JSPFree.createTabb("d1_B",["基本信息","相关还款责任人","抵质押物信息","授信额度信息","初始债权说明","月度表现信息","大额专项分期信息","非月度表现信息","特殊交易说明","授信协议基础信息","授信协议共同受信人信息","授信协议额度信息","特殊事件说明"]);

	JSPFree.createBillList("d1_B_1","/biapp-cr/freexml/icrBasicInfoxml/IcrAcctBsInf.xml");  //个人基本信息 	

	JSPFree.createBillList("d1_B_2","/biapp-cr/freexml/icrBasicInfoxml/IcrRltRepymtInf.xml");  //个人借贷相关还款责任人
	JSPFree.createBillList("d1_B_3","/biapp-cr/freexml/icrBasicInfoxml/IcrMotgaCltalCtrctInf.xml");  //抵质押物信息
	JSPFree.createBillList("d1_B_4","/biapp-cr/freexml/icrBasicInfoxml/IcrAcctCred.xml");  //借贷授信额度信息	

	JSPFree.createBillList("d1_B_5","/biapp-cr/freexml/icrBasicInfoxml/IcrOrigCreditorInf.xml");  //初始债权说明
	JSPFree.createBillList("d1_B_6","/biapp-cr/freexml/icrBasicInfoxml/IcrAcctMthlyBlgInf.xml");  //月度表现信息	

	JSPFree.createBillList("d1_B_7","/biapp-cr/freexml/icrBasicInfoxml/IcrSpecPrd.xml");  //大额专项分期信息
	JSPFree.createBillList("d1_B_8","/biapp-cr/freexml/icrBasicInfoxml/IcrAcctDbtInf.xml");  //非月度表现信息	

	JSPFree.createBillList("d1_B_9","/biapp-cr/freexml/icrBasicInfoxml/IcrAcctSpecTrstDspn.xml");  //特殊交易说明
	JSPFree.createBillList("d1_B_10","/biapp-cr/freexml/icrBasicInfoxml/IcrCtrctBs.xml");  //授信协议基础信息	
    JSPFree.createBillList("d1_B_11","/biapp-cr/freexml/icrBasicInfoxml/IcrCtrctCertRel.xml");  //授信协议共同受信人信息
	JSPFree.createBillList("d1_B_12","/biapp-cr/freexml/icrBasicInfoxml/IcrCreditLim.xml");  //授信协议额度信息
	JSPFree.createBillList("d1_B_13","/biapp-cr/freexml/icrBasicInfoxml/IcrInSpcEvtDscInf.xml");  //特殊事件说明

    //	JSPFree.bindBillTreeOnSelect(d1_A_BillTree,onSelectCorp);  //选择机构时触发的事件
	//JSPFree.bindSelectEvent(d1_B_A_BillList,onSelectDept);  //选择部门时触发的事件
	

JSPFree.bindSelectEvent(d1_A_BillList,onSelectIDSgmt);  //选择人员基础信息时触发的事件

//选择人员基础信息时触发的事件,即查询该人员的其他信息!
function onSelectIDSgmt(rowIndex, rowData){
	
	var str_deptid = rowData["acctcode"];  //证件号码
	var str_contractid = rowData["contractcode"];  //授信协议标识码
	//alert(str_deptid);
	JSPFree.queryDataByConditon(d1_B_1_BillList,"AcctCode='" + str_deptid + "'");  
	JSPFree.setDefaultValues(d1_B_1_BillList,{"AcctCode":str_deptid});
	
	JSPFree.queryDataByConditon(d1_B_2_BillList,"AcctCode='" + str_deptid + "'");  
	JSPFree.setDefaultValues(d1_B_2_BillList,{"AcctCode":str_deptid});
	
	JSPFree.queryDataByConditon(d1_B_3_BillList,"AcctCode='" + str_deptid + "'");  
	JSPFree.setDefaultValues(d1_B_3_BillList,{"AcctCode":str_deptid});
	
	JSPFree.queryDataByConditon(d1_B_4_BillList,"AcctCode='" + str_deptid + "'");  
	JSPFree.setDefaultValues(d1_B_4_BillList,{"AcctCode":str_deptid});
	
	JSPFree.queryDataByConditon(d1_B_5_BillList,"AcctCode='" + str_deptid + "'");  
	JSPFree.setDefaultValues(d1_B_5_BillList,{"AcctCode":str_deptid});
	
	JSPFree.queryDataByConditon(d1_B_6_BillList,"AcctCode='" + str_deptid + "'");  
	JSPFree.setDefaultValues(d1_B_6_BillList,{"AcctCode":str_deptid});
	
	JSPFree.queryDataByConditon(d1_B_7_BillList,"AcctCode='" + str_deptid + "'");  
	JSPFree.setDefaultValues(d1_B_7_BillList,{"AcctCode":str_deptid});
	
	JSPFree.queryDataByConditon(d1_B_8_BillList,"AcctCode='" + str_deptid + "'");  
	JSPFree.setDefaultValues(d1_B_8_BillList,{"AcctCode":str_deptid});
	
	JSPFree.queryDataByConditon(d1_B_9_BillList,"AcctCode='" + str_deptid + "'");  
	JSPFree.setDefaultValues(d1_B_9_BillList,{"AcctCode":str_deptid});
	
	JSPFree.queryDataByConditon(d1_B_10_BillList,"ContractCode='" + str_contractid + "'");  
	JSPFree.setDefaultValues(d1_B_10_BillList,{"ContractCode":str_contractid});
	
	JSPFree.queryDataByConditon(d1_B_11_BillList,"ContractCode='" + str_contractid + "'");  
	JSPFree.setDefaultValues(d1_B_11_BillList,{"ContractCode":str_contractid});
	
	JSPFree.queryDataByConditon(d1_B_12_BillList,"ContractCode='" + str_contractid + "'");  
	JSPFree.setDefaultValues(d1_B_12_BillList,{"ContractCode":str_contractid});
	
	JSPFree.queryDataByConditon(d1_B_13_BillList,"AcctCode='" + str_deptid + "'");  
	JSPFree.setDefaultValues(d1_B_13_BillList,{"AcctCode":str_deptid});
	
}
}


function onSelectType(rowIndex, rowData){
		var str_deptid = rowData["BrerType"];  //证件号码
if(str_deptid=='1'){
	
		JSPFree.setDefaultValues(d1_B_1_BillList,{"BrerType":str_deptid});

	}
	else {
		
		JSPFree.setDefaultValues(d1_B_1_BillList,{"BrerType":str_deptid});

		}

	
	}