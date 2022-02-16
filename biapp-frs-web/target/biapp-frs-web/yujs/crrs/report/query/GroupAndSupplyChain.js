//初始化界面,菜单配置路径是【/frs/yufreejs?js=/yujs/east/58table.js】
function AfterInit(){
	var str_customer_code = jso_OpenPars.customer_code;
	var str_data_dt = jso_OpenPars.data_dt;
	// 创建列表
	JSPFree.createSplit("d1", "上下", 280);
	JSPFree.createBillCard("d1_A","/biapp-crrs/freexml/crrs/generateXml/query/query_group_client_CODE1.xml");

	JSPFree.createTabb("d1_B", [ "重要联系人", "成员名单", "实际控制人", "关联集团"]);
	JSPFree.createBillList("d1_B_1", "/biapp-crrs/freexml/crrs/ref/crrs_group_executives_ref.xml",null,{isSwitchQuery:"N",ishavebillquery:"N",list_btns:""}); // 重要联系人
	JSPFree.createBillList("d1_B_2", "/biapp-crrs/freexml/crrs/ref/crrs_group_members_ref.xml",null,{isSwitchQuery:"N",ishavebillquery:"N",list_btns:""}); // 成员名单
	JSPFree.createBillList("d1_B_3", "/biapp-crrs/freexml/crrs/ref/crrs_group_actualcontroller_ref.xml",null,{isSwitchQuery:"N",ishavebillquery:"N",list_btns:""}); // 实际控制人
	JSPFree.createBillList("d1_B_4", "/biapp-crrs/freexml/crrs/ref/crrs_group_ffiliated_groups_ref.xml",null,{isSwitchQuery:"N",ishavebillquery:"N",list_btns:""}); // 关联集团

	var str_sqlWhere = "customer_code='" + str_customer_code + "' and data_dt = '" + str_data_dt + "'";
	var jso_data = JSPFree.queryBillCardData(d1_A_BillCard, str_sqlWhere);
	
	JSPFree.queryDataByConditon(d1_B_1_BillList, str_sqlWhere);
	JSPFree.queryDataByConditon(d1_B_2_BillList, str_sqlWhere);
	JSPFree.queryDataByConditon(d1_B_3_BillList, str_sqlWhere);
	JSPFree.queryDataByConditon(d1_B_4_BillList, str_sqlWhere);
}

function AfterBodyLoad() {
	FreeUtil.setBillCardItemEditable(d1_A_BillCard,"*",false);
}