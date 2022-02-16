//初始化界面,菜单配置路径是【/frs/yufreejs?js=/yujs/crrs/collection/addSingle.js】
var isLoadTabb_2 = false;
var isLoadTabb_3 = false;
var isLoadTabb_4 = false;
var isLoadTabb_5 = false;
var isLoadTabb_6 = false;
var isLoadTabb_7 = false;

var group;
function AfterInit(){
	group = jso_OpenPars2.group;
	JSPFree.createTabb("d1",["基本信息","高管及重要联系人","成员名单","实际控制人","关联集团","授信信息","授信拆分信息"],[80,120,80,90,80,80,100]);

	JSPFree.createBillCard("d1_1","/biapp-crrs/freexml/crrs/group/crrs_group_group_client_CODE1.xml");
	//赋值
	JSPFree.setBillCardValues(d1_1_BillCard,group);
	JSPFree.addTabbSelectChangedListener(d1_tabb,onSelect);
}

function AfterBodyLoad(){
	JSPFree.setBillCardItemEditable(d1_1_BillCard,"*",false);
}

function onSelect(_index,_title){
	var newIndex = _index+1;
	if(newIndex==2){
		if(!isLoadTabb_2){
			JSPFree.createBillList("d1_2","/biapp-crrs/freexml/crrs/group/crrs_group_executives_CODE1.xml",null,{isSwitchQuery:"N",list_btns:"[icon-p31]查看/onView",autoquery:"Y",
				autocondition:"customer_code='"+group.customer_code+"' and data_dt='"+group.data_dt+"'",querycontion:"customer_code='"+group.customer_code+"' and data_dt='"+group.data_dt+"'"});
			$.parser.parse('#d1_2');
			isLoadTabb_2 = true;
		}
	}

	if(newIndex==3){
		if(!isLoadTabb_3){
			JSPFree.createBillList("d1_3","/biapp-crrs/freexml/crrs/group/crrs_group_members_CODE1.xml",null,{isSwitchQuery:"N",list_btns:"$VIEW",autoquery:"Y",
				autocondition:"customer_code='"+group.customer_code+"' and data_dt='"+group.data_dt+"'",querycontion:"customer_code='"+group.customer_code+"' and data_dt='"+group.data_dt+"'"});
			$.parser.parse('#d1_3');
			isLoadTabb_3 = true;
		}
	}

	if(newIndex==4){
		if(!isLoadTabb_4){
			JSPFree.createBillList("d1_4","/biapp-crrs/freexml/crrs/group/crrs_group_actualcontroller_CODE1.xml",null,{isSwitchQuery:"N",list_btns:"$VIEW",autoquery:"Y",
				autocondition:"customer_code='"+group.customer_code+"' and data_dt='"+group.data_dt+"'",querycontion:"customer_code='"+group.customer_code+"' and data_dt='"+group.data_dt+"'"});
			$.parser.parse('#d1_4');
			isLoadTabb_4 = true;
		}
	}

	if(newIndex==5){
		if(!isLoadTabb_5){
			JSPFree.createBillList("d1_5","/biapp-crrs/freexml/crrs/group/crrs_group_ffiliated_groups_CODE1.xml",null,{isSwitchQuery:"N",list_btns:"$VIEW",autoquery:"Y",
				autocondition:"customer_code='"+group.customer_code+"' and data_dt='"+group.data_dt+"'",querycontion:"customer_code='"+group.customer_code+"' and data_dt='"+group.data_dt+"'"});
			$.parser.parse('#d1_5');
			isLoadTabb_5 = true;
		}
	}

	if(newIndex==6){
		if(!isLoadTabb_6){
			JSPFree.createBillList("d1_6","/biapp-crrs/freexml/crrs/ent/crrs_ent_credit_CODE1.xml",null,{isSwitchQuery:"N",list_btns:"$VIEW",autoquery:"Y",
				autocondition:"customer_code='"+group.customer_code+"' and data_dt='"+group.data_dt+"' and customer_type='1' and credit_type = '2'",querycontion:"customer_code='"+group.customer_code+"' and data_dt='"+group.data_dt+"' and customer_type='1' and credit_type = '2'"});
			$.parser.parse('#d1_6');
			isLoadTabb_6 = true;
		}
	}

	if(newIndex==7){
		if(!isLoadTabb_7){
			JSPFree.createBillList("d1_7","/biapp-crrs/freexml/crrs/ent/crrs_ent_group_client_CODE1.xml",null,{isSwitchQuery:"N",list_btns:"$VIEW",autoquery:"Y",
				autocondition:"customer_code='"+group.customer_code+"' and data_dt='"+group.data_dt+"'",querycontion:"customer_code='"+group.customer_code+"' and data_dt='"+group.data_dt+"'"});
			$.parser.parse('#d1_7');
			//强制设置查询的时候附带下面的条件
			d1_7_BillList.InsertOrUpdateRefSQLWhere={customer_code:group.customer_code,data_dt:group.data_dt};
			isLoadTabb_7 = true;
		}
	}

}

/**
 * 查看高管
 * @return {[type]} [description]
 */
function onView(){
	var executive = d1_2_BillList.datagrid('getSelected');
	if (executive == null) {
		$.messager.alert('提示', '必须选择一条记录进行操作', 'warning');
		return;
	}
	var defaultValue ={executive:executive};
	JSPFree.openDialog("高管及重要联系人","/yujs/crrs/dataquery/viewGroupExecutives.js",900,560,defaultValue,function(_rtdata){

	},true);
}