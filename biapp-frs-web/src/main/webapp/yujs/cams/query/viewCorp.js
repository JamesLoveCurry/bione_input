//初始化界面,菜单配置路径是【/frs/yufreejs?js=/yujs/crrs/collection/addSingle.js】
var isLoadTabb_2 = false;
var isLoadTabb_3 = false;
var isLoadTabb_4 = false;
var isLoadTabb_5 = false;
function AfterInit(){
	corp = jso_OpenPars2.corp;
	JSPFree.createTabb("d1",["账户信息","名称信息","地址信息","TIN信息","控制人信息"],[80,80,80,80,80]);
	
	JSPFree.createBillCard("d1_1","/biapp-cams/freexml/cams/query/cams_corp_acct_ref.xml");
	//赋值
	JSPFree.setBillCardValues(d1_1_BillCard,corp);
	JSPFree.addTabbSelectChangedListener(d1_tabb,onSelect);
}

function AfterBodyLoad(){
	JSPFree.setBillCardItemEditable(d1_1_BillCard,"*",false);
}

function onSelect(_index,_title){
	var newIndex = _index+1;
	
	if(newIndex==2){
		if(!isLoadTabb_2){
			JSPFree.createBillList("d1_2","/biapp-cams/freexml/cams/query/cams_corp_name_ref.xml",null,{isSwitchQuery:"N",list_btns:"$VIEW",ishavebillquery:"N",autoquery:"Y",autocondition:"account_number='"+corp.account_number+"'",querycontion:"account_number='"+corp.account_number+"'"});
			$.parser.parse('#d1_2');
			isLoadTabb_2 = true;
		}
	}

	if(newIndex==3){
		if(!isLoadTabb_3){
			JSPFree.createBillList("d1_3","/biapp-cams/freexml/cams/query/cams_corp_addr_ref.xml",null,{isSwitchQuery:"N",list_btns:"$VIEW",ishavebillquery:"N",autoquery:"Y",autocondition:"account_number='"+corp.account_number+"'",querycontion:"account_number='"+corp.account_number+"'"});
			$.parser.parse('#d1_3');
			isLoadTabb_3 = true;
		}
	}

	if(newIndex==4){
		if(!isLoadTabb_4){
			JSPFree.createBillList("d1_4","/biapp-cams/freexml/cams/query/cams_corp_tin_ref.xml",null,{isSwitchQuery:"N",list_btns:"$VIEW",ishavebillquery:"N",autoquery:"Y",autocondition:"account_number='"+corp.account_number+"'",querycontion:"account_number='"+corp.account_number+"'"});
			$.parser.parse('#d1_4');
			isLoadTabb_4 = true;
		}
	}
	
	if(newIndex==5){
		if(!isLoadTabb_5){
			JSPFree.createBillList("d1_5","/biapp-cams/freexml/cams/query/cams_corp_ctrl_ref.xml",null,{isSwitchQuery:"N",list_btns:"[icon-p31]编辑/viewData",ishavebillquery:"N",autoquery:"Y",autocondition:"account_number='"+corp.account_number+"'",querycontion:"account_number='"+corp.account_number+"'"});
			$.parser.parse('#d1_5');
			isLoadTabb_5 = true;
		}
	}
}

function viewData() {
	var ctrl = d1_5_BillList.datagrid('getSelected');
	if (ctrl == null) {
		$.messager.alert('提示', '必须选择一条记录进行操作', 'warning');
		return;
	}
	
	JSPFree.openDialog2("控制人信息","/yujs/cams/collection/ctrl/viewCtrl.js",950,600,{ctrl:ctrl},function(_rtData){
		if (_rtData != null) {
			if ("dirclose" == _rtData.type) {
				return;
			} 
		}
	},true);
}
