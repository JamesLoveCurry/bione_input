//初始化界面,菜单配置路径是【/frs/yufreejs?js=/yujs/crrs/xxx/xxx.js】
function AfterInit(){
	JSPFree.createBillList("d1","/biapp-crrs/freexml/crrs/ent/crrs_ent_credit_CODE1.xml",null,{autoquery:"N"});
	var str_customer_code = jso_OpenPars.customer_code;  //从主表传过来的表名
	var str_customer_name = jso_OpenPars.customer_name;
	//设置列表的默认值对象 
	d1_BillList.DefaultValues={customer_code:str_customer_code,customer_name:str_customer_name};

	var str_sqlWhere = "customer_code='"  + str_customer_code + "'";  //拼SQL条件
	JSPFree.setBillListForceSQLWhere(d1_BillList,str_sqlWhere);
	JSPFree.queryDataByConditon(d1_BillList,null);  //锁定规则表查询数据
}
