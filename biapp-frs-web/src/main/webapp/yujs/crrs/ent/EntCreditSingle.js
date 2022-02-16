//初始化界面,菜单配置路径是【/frs/yufreejs?js=/yujs/crrs/xxx/xxx.js】
function AfterInit(){
	var str_customer_code = jso_OpenPars.customer_code;
    var str_customer_name = jso_OpenPars.customer_name;
    var str_hasBaseBtn = jso_OpenPars.hasBaseBtn;
	var str_status = jso_OpenPars.status; // 判断主数据是否已锁定
	
    if(str_hasBaseBtn){
    	//创建列表
		JSPFree.createBillList("d1","/biapp-crrs/freexml/crrs/ent/crrs_ent_credit_CODE3.xml",null,{list_btns:"$VIEW",autoquery:"N"});
    }
    else{
		if ("锁定" == str_status) {
			JSPFree.createBillList("d1","/biapp-crrs/freexml/crrs/ent/crrs_ent_credit_CODE3.xml",null,{list_btns:"$VIEW",autoquery:"N"});
		} else {
			JSPFree.createBillList("d1","/biapp-crrs/freexml/crrs/ent/crrs_ent_credit_CODE3.xml",null,{autoquery:"N"});
		}
    }
    

    //设置列表的默认值对象 
	d1_BillList.DefaultValues={customer_code:str_customer_code,customer_type:'3',customer_name:str_customer_name};

	var str_sqlWhere = "customer_code='"  + str_customer_code + "' and customer_type = '3'";  //拼SQL条件
	JSPFree.setBillListForceSQLWhere(d1_BillList,str_sqlWhere);
	JSPFree.queryDataByConditon(d1_BillList,null);  //锁定规则表查询数据
}
