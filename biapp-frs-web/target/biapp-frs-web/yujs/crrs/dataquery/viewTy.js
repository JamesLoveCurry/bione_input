//初始化界面,菜单配置路径是【/frs/yufreejs?js=/yujs/crrs/collection/addSingle.js】
var tyData = null;
var isLoadTabb_2 = false;
var isLoadTabb_3 = false;
function AfterInit(){
	tyData = jso_OpenPars2.tyData;
	JSPFree.createTabb("d1",["基本信息","授信信息","业务明细"],[80,80,80]);

	JSPFree.createBillCard("d1_1","/biapp-crrs/freexml/crrs/ent/crrs_ent_trade_info_CODE1.xml");
	//赋值
	JSPFree.setBillCardValues(d1_1_BillCard,tyData);
	JSPFree.addTabbSelectChangedListener(d1_tabb,onSelect);
}

function AfterBodyLoad(){
	JSPFree.setBillCardItemEditable(d1_1_BillCard,"*",false);
}

function onSelect(_index,_title){
	var newIndex = _index+1;
	if(newIndex==2){
		if(!isLoadTabb_2){
			JSPFree.createBillList("d1_2","/biapp-crrs/freexml/crrs/ent/crrs_ent_credit_CODE4.xml",null,{isSwitchQuery:"N",list_btns:"$VIEW",autoquery:"Y",
				autocondition:"customer_code='"+tyData.customer_code+"' and data_dt='"+tyData.data_dt+"' and customer_type='3' and credit_type = '4'",querycontion:"customer_code='"+tyData.customer_code+"' and data_dt='"+tyData.data_dt+"' and customer_type='3' and credit_type = '4'"});
			$.parser.parse('#d1_2');
			isLoadTabb_2 = true;
		}
	}

	if(newIndex==3){
		if(!isLoadTabb_3){
			JSPFree.createBillList("d1_3","/biapp-crrs/freexml/crrs/ent/crrs_ent_trade_customers_CODE1.xml",null,{isSwitchQuery:"N",list_btns:"$VIEW",autoquery:"Y",ishavebillquery:"N",
				autocondition:"customer_code='"+tyData.customer_code+"' and data_dt='"+tyData.data_dt+"'",querycontion:"customer_code='"+tyData.customer_code+"' and data_dt='"+tyData.data_dt+"'"});
			$.parser.parse('#d1_3');
			isLoadTabb_3 = true;
		}
	}

}