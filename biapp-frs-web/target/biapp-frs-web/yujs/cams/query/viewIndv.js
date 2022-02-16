//初始化界面,菜单配置路径是【/frs/yufreejs?js=/yujs/crrs/collection/addSingle.js】
var isLoadTabb_2 = false;
var isLoadTabb_3 = false;
var isLoadTabb_4 = false;
function AfterInit(){
	indv = jso_OpenPars2.indv;
	JSPFree.createTabb("d1",["账户信息","姓名信息","地址信息","TIN信息"],[80,80,80,80]);
	
	JSPFree.createBillCard("d1_1","/biapp-cams/freexml/cams/query/cams_indv_acct_ref.xml");
	//赋值
	JSPFree.setBillCardValues(d1_1_BillCard,indv);
	JSPFree.addTabbSelectChangedListener(d1_tabb,onSelect);
}

function AfterBodyLoad(){
	JSPFree.setBillCardItemEditable(d1_1_BillCard,"*",false);
}

function onSelect(_index,_title){
	var newIndex = _index+1;
	
	if(newIndex==2){
		if(!isLoadTabb_2){
			JSPFree.createBillList("d1_2","/biapp-cams/freexml/cams/query/cams_indv_name_ref.xml",null,{isSwitchQuery:"N",list_btns:"$VIEW",ishavebillquery:"N",autoquery:"Y",autocondition:"data_id='"+indv.rid+"' and data_type='1'",querycontion:"data_id='"+indv.rid+"' and data_type='1'"});
			$.parser.parse('#d1_2');
			isLoadTabb_2 = true;
		}
	}

	if(newIndex==3){
		if(!isLoadTabb_3){
			JSPFree.createBillList("d1_3","/biapp-cams/freexml/cams/query/cams_indv_addr_ref.xml",null,{isSwitchQuery:"N",list_btns:"$VIEW",ishavebillquery:"N",autoquery:"Y",autocondition:"data_id='"+indv.rid+"' and data_type='1'",querycontion:"data_id='"+indv.rid+"' and data_type='1'"});
			$.parser.parse('#d1_3');
			isLoadTabb_3 = true;
		}
	}

	if(newIndex==4){
		if(!isLoadTabb_4){
			JSPFree.createBillList("d1_4","/biapp-cams/freexml/cams/query/cams_indv_tin_ref.xml",null,{isSwitchQuery:"N",list_btns:"$VIEW",ishavebillquery:"N",autoquery:"Y",autocondition:"data_id='"+indv.rid+"' and data_type='1'",querycontion:"data_id='"+indv.rid+"' and data_type='1'"});
			$.parser.parse('#d1_4');
			isLoadTabb_4 = true;
		}
	}
}
