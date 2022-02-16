//初始化界面,菜单配置路径是
function AfterInit(){
	JSPFree.createBillList("d1","/biapp-cr/freexml/IcrRltRepymtInf.xml",null,{isSwitchQuery:"N"});
	
	//JSPFree.createSplit("d1","上下",300);  //先是左右分割界面

	//JSPFree.createBillList("d1_A","/biapp-cr/freexml/icrBasicInformation.xml");  //左边是机构树
	//JSPFree.createBillList("d1_B","/biapp-cr/freexml/IcrRltRepymtInf.xml");  //上边是部门

    //	JSPFree.bindBillTreeOnSelect(d1_A_BillTree,onSelectCorp);  //选择机构时触发的事件
	//JSPFree.bindSelectEvent(d1_B_A_BillList,onSelectDept);  //选择部门时触发的事件
	
}


