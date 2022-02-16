/**
 * 菜单路径
 */
 function AfterInit(){

    JSPFree.createSplit("d1","上下",250);  //右边再上下分割
    JSPFree.createTabb("d1_B",["抵押物","质押物","其他债务人信息","基本信息"]);  //下面再搞一个多页签
	JSPFree.createBillList("d1_A","/biapp-cr/freexml/hentmotgacltalctrctbs/Basic_information_list.xml");//基础信息
	//JSPFree.billListBindCustQueryEvent(d1_A_BillList, onSingleSummary);
	JSPFree.createBillList("d1_B_1","/biapp-cr/freexml/hentmotgacltalctrctbs/Guarantee.xml");  //抵押物
	JSPFree.createBillList("d1_B_3","/biapp-cr/freexml/hentmotgacltalctrctbs/Other_debtor.xml");  //其他债务人信息
	JSPFree.createBillList("d1_B_2","/biapp-cr/freexml/hentmotgacltalctrctbs/Pledge_information.xml");  //质押物
	JSPFree.createBillList("d1_B_4","/biapp-cr/freexml/hentmotgacltalctrctbs/Essential_information.xml");  //基本信息
	
    JSPFree.bindSelectEvent(d1_A_BillList,onSelectPl);  //选择质押物时触发的事件
	
}
//选择基础信息时触发的事件,即查询该基本信息下!
function onSelectPl(rowIndex, rowData){
    //console.log(rowIndex);
	var str_deptid = rowData["cccode"]; 
	//alert(str_deptid);
	JSPFree.queryDataByConditon(d1_B_2_BillList,"CcCode='" + str_deptid + "'");  //查询基础信息表,查看质押物信息
	JSPFree.setDefaultValues(d1_B_2_BillList,{"CcCode":str_deptid});
	
	JSPFree.queryDataByConditon(d1_B_1_BillList,"CcCode='" + str_deptid + "'");  //查询基础信息表,查看抵押物信息
	JSPFree.setDefaultValues(d1_B_1_BillList,{"CcCode":str_deptid});
	
	JSPFree.queryDataByConditon(d1_B_4_BillList,"CcCode='" + str_deptid + "'");  //查询基础信息表,查看基本信息
	JSPFree.setDefaultValues(d1_B_4_BillList,{"CcCode":str_deptid});
	
	JSPFree.queryDataByConditon(d1_B_3_BillList,"CcCode='" + str_deptid + "'");  //查询基础信息表,查看其他债务人信息
	JSPFree.setDefaultValues(d1_B_3_BillList,{"CcCode":str_deptid});
}




