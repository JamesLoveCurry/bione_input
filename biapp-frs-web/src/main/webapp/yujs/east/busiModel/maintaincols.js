//字段维护页面
//初始化界面,菜单配置路径是【/frs/yufreejs?js=/yujs/east/maintaincols.js】
function AfterInit(){
	var str_tab_name = jso_OpenPars.tab_name;  //从主表传过来的表名
    
    //创建列表
	JSPFree.createBillList("d1","east/busiModel/east_cr_col_CODE1",null,{isSwitchQuery:"N"});

    //设置列表的默认值对象 
	d1_BillList.DefaultValues={tab_name:str_tab_name};

	var str_sqlWhere = "tab_name='"  + str_tab_name + "'";  //拼SQL条件
	JSPFree.queryDataByConditon(d1_BillList,str_sqlWhere);  //锁定规则表查询数据
	
	JSPFree.setBillListForceSQLWhere(d1_BillList,str_sqlWhere);
}

