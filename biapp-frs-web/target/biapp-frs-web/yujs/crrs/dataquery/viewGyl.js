//初始化界面,菜单配置路径是【/frs/yufreejs?js=/yujs/crrs/collection/addSingle.js】
var gylData = null;
var isLoadTabb_2 = false;
var isLoadTabb_3 = false;
var isLoadTabb_4 = false;
function AfterInit(){
	gylData = jso_OpenPars2.gylData;
	JSPFree.createTabb("d1",["基本信息","集团成员","授信信息","授信拆分信息"],[80,80,80,100]);

	JSPFree.createBillCard("d1_1","/biapp-crrs/freexml/crrs/group/crrs_group_group_client_CODE2.xml");
	//赋值
	JSPFree.setBillCardValues(d1_1_BillCard,gylData);
	JSPFree.addTabbSelectChangedListener(d1_tabb,onSelect);
}

function AfterBodyLoad(){
	JSPFree.setBillCardItemEditable(d1_1_BillCard,"*",false);
}

function onSelect(_index,_title){
	var newIndex = _index+1;
	if(newIndex==2){
		if(!isLoadTabb_2){
			JSPFree.createBillList("d1_2","/biapp-crrs/freexml/crrs/group/crrs_group_members_CODE2.xml",null,{isSwitchQuery:"N",list_btns:"$VIEW",autoquery:"Y",
				autocondition:"customer_code='"+gylData.customer_code+"' and data_dt='"+gylData.data_dt+"'",querycontion:"customer_code='"+gylData.customer_code+"' and data_dt='"+gylData.data_dt+"'"});
			$.parser.parse('#d1_2');
			isLoadTabb_2 = true;
		}
	}

	if(newIndex==3){
		if(!isLoadTabb_3){
			JSPFree.createBillList("d1_3","/biapp-crrs/freexml/crrs/ent/crrs_ent_credit_CODE2.xml",null,{isSwitchQuery:"N",list_btns:"$VIEW",autoquery:"Y",
				autocondition:"customer_code='"+gylData.customer_code+"' and data_dt='"+gylData.data_dt+"'",querycontion:"customer_code='"+gylData.customer_code+"' and data_dt='"+gylData.data_dt+"'"});
			$.parser.parse('#d1_3');
			isLoadTabb_3 = true;
		}
	}

	if(newIndex==4){
		if(!isLoadTabb_4){
			JSPFree.createBillList("d1_4","/biapp-crrs/freexml/crrs/ent/crrs_ent_group_client_CODE2.xml",null,{isSwitchQuery:"N",list_btns:"$VIEW",autoquery:"Y",
				autocondition:"customer_code='"+gylData.customer_code+"' and data_dt='"+gylData.data_dt+"'",querycontion:"customer_code='"+gylData.customer_code+"' and data_dt='"+gylData.data_dt+"'"});
			$.parser.parse('#d1_4');
			//强制设置查询的时候附带下面的条件
			d1_4_BillList.InsertOrUpdateRefSQLWhere={customer_code:gylData.customer_code,data_dt:gylData.data_dt};
			isLoadTabb_4 = true;
		}
	}
}