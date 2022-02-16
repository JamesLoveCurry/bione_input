//校验规则维护..
function AfterInit(){
	var jso_par1 = jso_OpenPars2.jso_par1;
	
	if(jso_par1 !=  undefined && jso_par1 != null){
		JSPFree.createBillList("d1","/biapp-fsrs/freexml/fsrs/fsrs_rule_standard.xml",null,jso_par1);  //确定性校验
	}
}