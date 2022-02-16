//初始化界面,菜单配置路径是【/frs/yufreejs?js=/yujs/crrs/collection/addSingle.js】
function AfterInit(){
	group = jso_OpenPars.group;
	JSPFree.createSplit("d1","上下",250);
	JSPFree.createBillCard("d1_A","/biapp-crrs/freexml/crrs/group/crrs_group_executives_CODE1.xml");
	//赋值
	JSPFree.setBillCardValues(d1_A_BillCard,jso_OpenPars.executive);
	d1_A_BillCard.OldData = jso_OpenPars.executive;

	var executiveId = jso_OpenPars.executive.executives_id;
	var str_data_dt = jso_OpenPars.executive.data_dt;
	JSPFree.createSplit("d1_B","左右",490);
	JSPFree.createBillList("d1_B_A","/biapp-crrs/freexml/crrs/group/crrs_passport_CODE1.xml",null,{list_btns:"$VIEW",autoquery:"Y",ishavebillquery:"N",list_ispagebar:"N",
		autocondition:"executives_id='"+executiveId+"' and data_dt='"+str_data_dt+"'",querycontion:"executives_id='"+executiveId+"' and data_dt='"+str_data_dt+"'"});
	JSPFree.createBillList("d1_B_B","/biapp-crrs/freexml/crrs/group/crrs_paper_CODE1.xml",null,{list_btns:"$VIEW",autoquery:"Y",ishavebillquery:"N",list_ispagebar:"N",
		autocondition:"executives_id='"+executiveId+"' and data_dt='"+str_data_dt+"'",querycontion:"executives_id='"+executiveId+"' and data_dt='"+str_data_dt+"'"});
}

function AfterBodyLoad(){
	JSPFree.setBillCardItemEditable(d1_A_BillCard,"*",false);
}

