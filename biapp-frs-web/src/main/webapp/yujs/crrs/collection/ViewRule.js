//校验规则维护..
function AfterInit(){
	var jso_par1 = jso_OpenPars2.jso_par1;
	var jso_par2 = jso_OpenPars2.jso_par2;
	var jso_par3 = jso_OpenPars2.jso_par3;
	
	if(jso_par1 !=  undefined && jso_par1 != null){
		JSPFree.createBillList("d1","/biapp-crrs/freexml/crrs/rule/crrs_rule_Sure.xml",null,jso_par1);  //确定性校验
	}else if(jso_par2 !=  undefined && jso_par2 != null){
		JSPFree.createBillList("d1","/biapp-crrs/freexml/crrs/rule/crrs_rule_Consistency.xml",null,jso_par2);  //一致性校验
	}else if(jso_par3 !=  undefined && jso_par3 != null){
		JSPFree.createBillList("d1","/biapp-crrs/freexml/crrs/rule/crrs_rule_Warn.xml",null,jso_par3);  //提示性校验
	}
	
}

//导入规则
function onImportRule(){
	JSPFree.alert("选择一个Excel,并导入校验规则!<br>系统开发中。。。");
}