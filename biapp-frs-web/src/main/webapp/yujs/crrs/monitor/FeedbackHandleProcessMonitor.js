//初始化界面,菜单配置路径是【/frs/yufreejs?js=/yujs/crrs/xxx/xxx.js】
function AfterInit(){
	JSPFree.createTabb("d1",["提示性校验反馈","跨行一致性校验反馈"],150);
	var str_sqlCons = "";  //过滤条件

	JSPFree.createBillList("d1_1","/biapp-crrs/freexml/crrs/rule/crrs_result_warn_FeedBack.xml",null,{list_btns:"$VIEW",querycontion:str_sqlCons});  //提示性校验
	JSPFree.createBillList("d1_2","/biapp-crrs/freexml/crrs/rule/crrs_rule_result_FeedBack.xml",null,{list_btns:"$VIEW",querycontion:str_sqlCons});  //提示性校验
	
	//人工再次查询数据
	JSPFree.queryDataByConditon2(d1_1_BillList,null);  //
	JSPFree.queryDataByConditon2(d1_2_BillList,null);  //
}