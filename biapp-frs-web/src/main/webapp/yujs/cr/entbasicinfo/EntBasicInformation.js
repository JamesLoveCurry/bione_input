//初始化界面,菜单配置路径是
function AfterInit(){
	JSPFree.createSplit("d1","上下",300);
	JSPFree.createBillList("d1_A","/biapp-cr/freexml/entbasicinfoxml/EntBasicInfo.xml");
	JSPFree.createTabb("d1_B",["基本概况","实际控制人","注册资本及主要出资人","主要组成人员","联系方式","其他标识","上级机构","企业身份标识整合","企业间关联关系"]);
	JSPFree.createBillList("d1_B_1","/biapp-cr/freexml/entbasicinfoxml/EntBasic1.xml");
	JSPFree.createBillList("d1_B_2","/biapp-cr/freexml/entbasicinfoxml/EntBasic2.xml");
	JSPFree.createBillList("d1_B_3","/biapp-cr/freexml/entbasicinfoxml/EntBasic3.xml");
	JSPFree.createBillList("d1_B_4","/biapp-cr/freexml/entbasicinfoxml/EntBasic4.xml");
	JSPFree.createBillList("d1_B_5","/biapp-cr/freexml/entbasicinfoxml/EntBasic5.xml");
	JSPFree.createBillList("d1_B_6","/biapp-cr/freexml/entbasicinfoxml/EntBasic6.xml");
	JSPFree.createBillList("d1_B_7","/biapp-cr/freexml/entbasicinfoxml/EntBasic7.xml");
	JSPFree.createBillList("d1_B_8","/biapp-cr/freexml/entbasicinfoxml/EntBasic8.xml");
	JSPFree.createBillList("d1_B_9","/biapp-cr/freexml/entbasicinfoxml/EntBasic9.xml");
	
	JSPFree.bindSelectEvent(d1_A_BillList,onSelectIDSgmt);  //选择人员基础信息时触发的事件
}	
	//选择人员基础信息时触发的事件,即查询该人员的其他信息!
function onSelectIDSgmt(rowIndex, rowData){
	
	var str_deptid = rowData["entcertnum"];  //证件号码
	var str_deptid1 =  rowData["entcerttype"];  //证件类型
	
	JSPFree.queryDataByConditon(d1_B_1_BillList,"EntCertNum='" + str_deptid + "' and EntCertType ='"+str_deptid1+"'");  
	JSPFree.setDefaultValues(d1_B_1_BillList,{"EntCertNum":str_deptid,"EntCertType":str_deptid1});
	
	JSPFree.queryDataByConditon(d1_B_2_BillList,"EntCertNum='" + str_deptid + "' and EntCertType ='"+str_deptid1+"'");  
	JSPFree.setDefaultValues(d1_B_2_BillList,{"EntCertNum":str_deptid,"EntCertType":str_deptid1});
	
	JSPFree.queryDataByConditon(d1_B_3_BillList,"EntCertNum='" + str_deptid + "' and EntCertType ='"+str_deptid1+"'");  
	JSPFree.setDefaultValues(d1_B_3_BillList,{"EntCertNum":str_deptid,"EntCertType":str_deptid1});
	
	JSPFree.queryDataByConditon(d1_B_4_BillList,"EntCertNum='" + str_deptid + "' and EntCertType ='"+str_deptid1+"'");  
	JSPFree.setDefaultValues(d1_B_4_BillList,{"EntCertNum":str_deptid,"EntCertType":str_deptid1});
	
	JSPFree.queryDataByConditon(d1_B_5_BillList,"EntCertNum='" + str_deptid + "' and EntCertType ='"+str_deptid1+"'");  
	JSPFree.setDefaultValues(d1_B_5_BillList,{"EntCertNum":str_deptid,"EntCertType":str_deptid1});
	
	JSPFree.queryDataByConditon(d1_B_6_BillList,"EntCertNum='" + str_deptid + "' and EntCertType ='"+str_deptid1+"'");  
	JSPFree.setDefaultValues(d1_B_6_BillList,{"EntCertNum":str_deptid,"EntCertType":str_deptid1});
	
	JSPFree.queryDataByConditon(d1_B_7_BillList,"EntCertNum='" + str_deptid + "' and EntCertType ='"+str_deptid1+"'");  
	JSPFree.setDefaultValues(d1_B_7_BillList,{"EntCertNum":str_deptid,"EntCertType":str_deptid1});
	
	JSPFree.queryDataByConditon(d1_B_8_BillList,"EntCertNum='" + str_deptid + "' and EntCertType ='"+str_deptid1+"'");  
	JSPFree.setDefaultValues(d1_B_8_BillList,{"EntCertNum":str_deptid,"EntCertType":str_deptid1});
	
	JSPFree.queryDataByConditon(d1_B_9_BillList,"EntCertNum='" + str_deptid + "' and EntCertType ='"+str_deptid1+"'");  
	JSPFree.setDefaultValues(d1_B_9_BillList,{"EntCertNum":str_deptid,"EntCertType":str_deptid1});

}



