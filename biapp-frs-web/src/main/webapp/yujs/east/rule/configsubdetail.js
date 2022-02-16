//初始化界面,菜单配置url路径是【/frs/yufreejs?js=/yujs/east/configsubdetail.js】
function AfterInit(){
	var table = jso_OpenPars.table;
	var subTable ="";
	if(table == "east_cr_rule_aggr"){
		subTable = "east_cr_rule_aggr_sub";
	}
	else{
		subTable = "east_cr_rule_uprr_sub";
	}
	
	JSPFree.createBillList("d1","/biapp-east/freexml/east/rule/"+subTable+"_CODE1.xml");
	var ruleId = jso_OpenPars.id;
	d1_BillList.DefaultValues={id : ruleId};
	var id = jso_OpenPars.id;
	var _sqlWhere = "id='"+id+"'";
	JSPFree.queryDataByConditon(d1_BillList,_sqlWhere);  //锁定规则表查询数据
}