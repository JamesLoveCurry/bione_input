//初始化界面,菜单配置路径是【/frs/yufreejs?js=/yujs/crrs/xxx/xxx.js】
function AfterInit(){
	var str_executives_id = jso_OpenPars2.executives_id;
	var str_hasBaseBtn = jso_OpenPars2.hasBaseBtn;
	var str_status = jso_OpenPars2.status; // 判断主数据是否已锁定

	if(str_hasBaseBtn){
		//创建列表
		JSPFree.createBillList("d1","/biapp-crrs/freexml/crrs/group/crrs_passport_CODE1.xml",null,{list_btns:"$VIEW",autoquery:"N",ishavebillquery:"N",list_ispagebar:"N"});
	}
	else{
		if ("锁定" == str_status) {
			JSPFree.createBillList("d1","/biapp-crrs/freexml/crrs/customer/crrs_single_executives_CODE1.xml",null,{list_btns:"$VIEW",autoquery:"N"});
		} else {
			JSPFree.createBillList("d1","/biapp-crrs/freexml/crrs/group/crrs_passport_CODE1.xml",null,{autoquery:"N",ishavebillquery:"N",list_ispagebar:"N"});
		}
	}

    //设置列表的默认值对象 
	d1_BillList.DefaultValues={executives_id:str_executives_id};

	var str_sqlWhere = "executives_id='"  + str_executives_id + "'";  //拼SQL条件
	JSPFree.setBillListForceSQLWhere(d1_BillList,str_sqlWhere);
	JSPFree.queryDataByConditon(d1_BillList,null);  //锁定规则表查询数据
}
