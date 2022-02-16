//初始化界面,菜单配置路径是【/frs/yufreejs?js=/yujs/east/58table.js】
function AfterInit(){
	JSPFree.createBillList("d1","east/engineTask/east_cr_log_ref");
	
	var task_id = jso_OpenPars.task_id;  //取得选中记录中的id值
	str_sqlWhere = "task_id='"  + task_id + "'";  //拼SQL条件
	//也可以这样写
	//d1_BillList["MyRefreshSql"] = str_sqlWhere;
	JSPFree.queryDataByConditon(d1_BillList,str_sqlWhere);  //锁定规则表查询数据
	
	setTimeout('refresh()',10000);//每三秒执行一次
}

/**
 * 刷新
 * @returns
 */
function refresh(){
	JSPFree.queryDataByConditon(d1_BillList,str_sqlWhere);
	//也可以这样
	//JSPFree.queryDataByConditon(d1_BillList,d1_BillListl.MyRefreshSql);
	
}