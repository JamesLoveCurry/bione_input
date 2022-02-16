//初始化界面,菜单配置url路径是【/frs/yufreejs?js=/yujs/east/warningLevel.js】
function AfterInit(){
	JSPFree.createBillList("d1","/biapp-east/freexml/east/rule/east_cr_warn_CODE1.xml");
	var id = jso_OpenPars.id;
	d1_BillList.DefaultValues={rule_id : id};
	var _sqlWhere = "rule_id='"+id+"'";
	JSPFree.queryDataByConditon(d1_BillList,_sqlWhere);  //锁定规则表查询数据
}

