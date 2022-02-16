//初始化界面,菜单配置路径是【/frs/yufreejs?js=/yujs/crrs/collection/addSingle.js】
var person = null;
var isLoadTabb_2 = false;
var isLoadTabb_3 = false;
var isLoadTabb_4 = false;
var isLoadTabb_5 = false;
function AfterInit(){
	person = jso_OpenPars2.person;
	JSPFree.createTabb("d1",["基本信息","业务信息","共同债务人","助学贷款专项指标","担保情况"],[80,80,90,150,80]);

	JSPFree.createBillCard("d1_1","/biapp-crrs/freexml/crrs/person/crrs_person_personal_CODE1.xml");
	//赋值
	JSPFree.setBillCardValues(d1_1_BillCard,person);

	JSPFree.addTabbSelectChangedListener(d1_tabb,onSelect);
}

function AfterBodyLoad(){
	JSPFree.setBillCardItemEditable(d1_1_BillCard,"*",false);
}

function onSelect(_index,_title){
	var newIndex = _index+1;
	if(newIndex==2){
		if(!isLoadTabb_2){
			JSPFree.createBillList("d1_2","/biapp-crrs/freexml/crrs/person/crrs_person_personal_loan_CODE1.xml",null,{isSwitchQuery:"N",list_btns:"$VIEW",autoquery:"Y",
				autocondition:"customer_code='"+person.customer_code+"' and data_dt='"+person.data_dt+"'",querycontion:"customer_code='"+person.customer_code+"' and data_dt='"+person.data_dt+"'"});
			$.parser.parse('#d1_2');
			isLoadTabb_2 = true;
		}
	}

	if(newIndex==3){
		if(!isLoadTabb_3){
			JSPFree.createBillList("d1_3","/biapp-crrs/freexml/crrs/person/crrs_person_joint_debtor_CODE1.xml",null,{isSwitchQuery:"N",list_btns:"$VIEW",autoquery:"Y",
				autocondition:"customer_code='"+person.customer_code+"' and data_dt='"+person.data_dt+"'",querycontion:"customer_code='"+person.customer_code+"' and data_dt='"+person.data_dt+"'"});
			$.parser.parse('#d1_3');
			isLoadTabb_3 = true;
		}
	}

	if(newIndex==4){
		if(!isLoadTabb_4){
			JSPFree.createBillList("d1_4","/biapp-crrs/freexml/crrs/person/crrs_person_student_loan_CODE1.xml",null,{isSwitchQuery:"N",list_btns:"$VIEW",autoquery:"Y",
				autocondition:"customer_code='"+person.customer_code+"' and data_dt='"+person.data_dt+"'",querycontion:"customer_code='"+person.customer_code+"' and data_dt='"+person.data_dt+"'"});
			$.parser.parse('#d1_4');
			isLoadTabb_4 = true;
		}
	}

	if(newIndex==5){
		if(!isLoadTabb_5){
			JSPFree.createBillList("d1_5","/biapp-crrs/freexml/crrs/person/crrs_person_ent_guaranteed_CODE1.xml",null,{isSwitchQuery:"N",list_btns:"$VIEW",autoquery:"Y",
				autocondition:"customer_code='"+person.customer_code+"' and data_dt='"+person.data_dt+"'",querycontion:"customer_code='"+person.customer_code+"' and data_dt='"+person.data_dt+"'"});
			$.parser.parse('#d1_5');
			isLoadTabb_5 = true;
		}
	}

}