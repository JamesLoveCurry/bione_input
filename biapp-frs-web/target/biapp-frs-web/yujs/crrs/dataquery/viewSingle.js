//初始化界面,菜单配置路径是【/frs/yufreejs?js=/yujs/crrs/collection/addSingle.js】
var isLoadTabb_2 = false;
var isLoadTabb_3 = false;
var isLoadTabb_4 = false;
var isLoadTabb_5 = false;
var isLoadTabb_6 = false;
var isLoadTabb_7 = false;
var isLoadTabb_8 = false;
var isLoadTabb_9 = false;
function AfterInit(){
	single = jso_OpenPars2.single;
	JSPFree.createTabb("d1",["基本信息","高管及重要联系人","重要股东及主要关联企业信息","授信信息","贷款信息","表外业务","担保信息","债券信息","股权信息"],[80,120,170,80,80,80,80,80,80]);

	JSPFree.createBillCard("d1_1","/biapp-crrs/freexml/crrs/customer/crrs_single_corporation_CODE1.xml");
	//赋值
	JSPFree.setBillCardValues(d1_1_BillCard,single);
	JSPFree.addTabbSelectChangedListener(d1_tabb,onSelect);
}

function AfterBodyLoad(){
	JSPFree.setBillCardItemEditable(d1_1_BillCard,"*",false);
}

function onSelect(_index,_title){
	var newIndex = _index+1;
	
	if(newIndex==2){
		if(!isLoadTabb_2){
			JSPFree.createBillList("d1_2","/biapp-crrs/freexml/crrs/customer/crrs_single_executives_CODE1.xml",null,{isSwitchQuery:"N",list_btns:"[icon-p31]查看/onView",list_ispagebar:"N",ishavebillquery:"Y",autoquery:"Y",
				autocondition:"customer_code='"+single.customer_code+"' and data_dt='"+single.data_dt+"'",querycontion:"customer_code='"+single.customer_code+"' and data_dt='"+single.data_dt+"'"});
			$.parser.parse('#d1_2');
			isLoadTabb_2 = true;
		}
	}

	if(newIndex==3){
		if(!isLoadTabb_3){
			JSPFree.createBillList("d1_3","/biapp-crrs/freexml/crrs/customer/crrs_single_shareholder_ep_CODE1.xml",null,{isSwitchQuery:"N",list_btns:"$VIEW",list_ispagebar:"N",ishavebillquery:"Y",autoquery:"Y",
				autocondition:"customer_code='"+single.customer_code+"' and data_dt='"+single.data_dt+"'",querycontion:"customer_code='"+single.customer_code+"' and data_dt='"+single.data_dt+"'"});
			$.parser.parse('#d1_3');
			isLoadTabb_3 = true;
		}
	}

	if(newIndex==4){
		if(!isLoadTabb_4){
			JSPFree.createBillList("d1_4","/biapp-crrs/freexml/crrs/ent/crrs_ent_credit_CODE3.xml",null,{isSwitchQuery:"N",list_btns:"$VIEW",autoquery:"Y",
				autocondition:"customer_code='"+single.customer_code+"' and data_dt='"+single.data_dt+"' and customer_type='2' and credit_type = '1'",querycontion:"customer_code='"+single.customer_code+"' and data_dt='"+single.data_dt+"' and customer_type='2' and credit_type = '1'"});
			$.parser.parse('#d1_4');
			isLoadTabb_4 = true;
		}
	}

	if(newIndex==5){
		if(!isLoadTabb_5){
			JSPFree.createBillList("d1_5","/biapp-crrs/freexml/crrs/ent/crrs_ent_loan_CODE1.xml",null,{isSwitchQuery:"N",list_btns:"$VIEW",autoquery:"Y",
				autocondition:"customer_code='"+single.customer_code+"' and data_dt='"+single.data_dt+"'",querycontion:"customer_code='"+single.customer_code+"' and data_dt='"+single.data_dt+"'"});
			$.parser.parse('#d1_5');
			//强制设置查询的时候附带下面的条件
			d1_5_BillList.InsertOrUpdateRefSQLWhere={customer_code:single.customer_code,data_dt:single.data_dt};
			isLoadTabb_5 = true;
		}
	}

	if(newIndex==6){
		if(!isLoadTabb_6){
			JSPFree.createBillList("d1_6","/biapp-crrs/freexml/crrs/ent/crrs_ent_offbalance_sa_CODE1.xml",null,{isSwitchQuery:"N",list_btns:"$VIEW",autoquery:"Y",
				autocondition:"customer_code='"+single.customer_code+"' and data_dt='"+single.data_dt+"'",querycontion:"customer_code='"+single.customer_code+"' and data_dt='"+single.data_dt+"'"});
			$.parser.parse('#d1_6');
			//强制设置查询的时候附带下面的条件
			d1_6_BillList.InsertOrUpdateRefSQLWhere={customer_code:single.customer_code,data_dt:single.data_dt};
			isLoadTabb_6 = true;
		}
	}

	if(newIndex==7){
		if(!isLoadTabb_7){
			JSPFree.createBillList("d1_7","/biapp-crrs/freexml/crrs/ent/crrs_ent_guaranteed_CODE1.xml",null,{isSwitchQuery:"N",list_btns:"$VIEW",autoquery:"Y",
				autocondition:"customer_code='"+single.customer_code+"' and data_dt='"+single.data_dt+"'",querycontion:"customer_code='"+single.customer_code+"' and data_dt='"+single.data_dt+"'"});
			$.parser.parse('#d1_7');
			isLoadTabb_7 = true;
			
		}
	}

	if(newIndex==8){
		if(!isLoadTabb_8){
			JSPFree.createBillList("d1_8","/biapp-crrs/freexml/crrs/ent/crrs_ent_bond_CODE2.xml",null,{isSwitchQuery:"N",list_btns:"$VIEW",autoquery:"Y",
				autocondition:"customer_code='"+single.customer_code+"' and data_dt='"+single.data_dt+"'",querycontion:"customer_code='"+single.customer_code+"' and data_dt='"+single.data_dt+"'"});
			$.parser.parse('#d1_8');
			//强制设置查询的时候附带下面的条件
			d1_8_BillList.InsertOrUpdateRefSQLWhere={customer_code:single.customer_code,data_dt:single.data_dt};
			isLoadTabb_8 = true;
		}
	}

	if(newIndex==9){
		if(!isLoadTabb_9){
			JSPFree.createBillList("d1_9","/biapp-crrs/freexml/crrs/ent/crrs_ent_equitystake_CODE2.xml",null,{isSwitchQuery:"N",list_btns:"$VIEW",autoquery:"Y",
				autocondition:"customer_code='"+single.customer_code+"' and data_dt='"+single.data_dt+"'",querycontion:"customer_code='"+single.customer_code+"' and data_dt='"+single.data_dt+"'"});
			$.parser.parse('#d1_9');
			//强制设置查询的时候附带下面的条件
			d1_9_BillList.InsertOrUpdateRefSQLWhere={customer_code:single.customer_code,data_dt:single.data_dt};
			isLoadTabb_9 = true;
		}
	}

}

function onView(){
	var executive = d1_2_BillList.datagrid('getSelected');
	if (executive == null) {
		$.messager.alert('提示', '必须选择一条记录进行操作', 'warning');
		return;
	}
	var defaultValue ={executive:executive};
	JSPFree.openDialog("高管及重要联系人","/yujs/crrs/dataquery/viewSingleExecutives.js",900,560,defaultValue,function(_rtdata){

	},true);
}
