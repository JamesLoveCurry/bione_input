//校验规则维护..
function AfterInit(){
	var jso_par1 = jso_OpenPars2.jso_par1;
	
	if(jso_par1 !=  undefined && jso_par1 != null){
		JSPFree.createBillList("d1","/biapp-pscs/freexml/pscs/configmanage/pscs_cr_rule_view.xml",null,jso_par1);
	}
}