//初始化界面,菜单配置路径是
function AfterInit(){
	JSPFree.createSplit("d1","上下",300);
	JSPFree.createBillList("d1_A","/biapp-cr/freexml/icrInfo_his/basc/IcrBasicInformation.xml");
	//JSPFree.createBillList("d1_B","/biapp-cr/freexml/icrbasic/IcrBasic1.xml");
	JSPFree.createTabb("d1_B",["其他标识","基本概况","婚姻信息","教育信息","职业信息","居住地址","通讯地址","收入信息","家族关系","个人证件有效期","个人证件整合"]);
	JSPFree.createBillList("d1_B_1","/biapp-cr/freexml/icrInfo_his/basc/IcrBasic1.xml");
	JSPFree.createBillList("d1_B_2","/biapp-cr/freexml/icrInfo_his/basc/IcrBasic2.xml");
	JSPFree.createBillList("d1_B_3","/biapp-cr/freexml/icrInfo_his/basc/IcrBasic3.xml");
	JSPFree.createBillList("d1_B_4","/biapp-cr/freexml/icrInfo_his/basc/IcrBasic4.xml");
	JSPFree.createBillList("d1_B_5","/biapp-cr/freexml/icrInfo_his/basc/IcrBasic5.xml");
	JSPFree.createBillList("d1_B_6","/biapp-cr/freexml/icrInfo_his/basc/IcrBasic6.xml");
	JSPFree.createBillList("d1_B_7","/biapp-cr/freexml/icrInfo_his/basc/IcrBasic7.xml");
	JSPFree.createBillList("d1_B_8","/biapp-cr/freexml/icrInfo_his/basc/IcrBasic8.xml");
	JSPFree.createBillList("d1_B_9","/biapp-cr/freexml/icrInfo_his/basc/IcrBasic9.xml");
	JSPFree.createBillList("d1_B_10","/biapp-cr/freexml/icrInfo_his/basc/IcrBasic10.xml");
	JSPFree.createBillList("d1_B_11","/biapp-cr/freexml/icrInfo_his/basc/IcrBasic11.xml");
	JSPFree.bindSelectEvent(d1_A_BillList,onSelectIDSgmt);  //选择人员基础信息时触发的事件
	
	//选择人员基础信息时触发的事件,即查询该人员的其他信息!
function onSelectIDSgmt(rowIndex, rowData){
	
	var str_deptid = rowData["idnum"];  //证件号码
	//alert(str_deptid);
	JSPFree.queryDataByConditon(d1_B_1_BillList,"IDNum='" + str_deptid + "'");  
	JSPFree.setDefaultValues(d1_B_1_BillList,{"IDNum":str_deptid});
	JSPFree.queryDataByConditon(d1_B_2_BillList,"IDNum='" + str_deptid + "'");  
	JSPFree.setDefaultValues(d1_B_2_BillList,{"IDNum":str_deptid});
	JSPFree.queryDataByConditon(d1_B_3_BillList,"IDNum='" + str_deptid + "'");  
	JSPFree.setDefaultValues(d1_B_3_BillList,{"IDNum":str_deptid});
	JSPFree.queryDataByConditon(d1_B_4_BillList,"IDNum='" + str_deptid + "'");  
	JSPFree.setDefaultValues(d1_B_4_BillList,{"IDNum":str_deptid});
	
	JSPFree.queryDataByConditon(d1_B_5_BillList,"IDNum='" + str_deptid + "'");  
	JSPFree.setDefaultValues(d1_B_5_BillList,{"IDNum":str_deptid});
	JSPFree.queryDataByConditon(d1_B_6_BillList,"IDNum='" + str_deptid + "'");  
	JSPFree.setDefaultValues(d1_B_6_BillList,{"IDNum":str_deptid});
	JSPFree.queryDataByConditon(d1_B_7_BillList,"IDNum='" + str_deptid + "'");  
	JSPFree.setDefaultValues(d1_B_7_BillList,{"IDNum":str_deptid});
	JSPFree.queryDataByConditon(d1_B_8_BillList,"IDNum='" + str_deptid + "'");  
	JSPFree.setDefaultValues(d1_B_8_BillList,{"IDNum":str_deptid});
	JSPFree.queryDataByConditon(d1_B_9_BillList,"IDNum='" + str_deptid + "'");  
	JSPFree.setDefaultValues(d1_B_9_BillList,{"IDNum":str_deptid});
	JSPFree.queryDataByConditon(d1_B_10_BillList,"IDNum='" + str_deptid + "'");  
	JSPFree.setDefaultValues(d1_B_10_BillList,{"IDNum":str_deptid});
	JSPFree.queryDataByConditon(d1_B_11_BillList,"IDNum='" + str_deptid + "'");  
	JSPFree.setDefaultValues(d1_B_11_BillList,{"IDNum":str_deptid});
}
}


