//初始化界面,菜单配置路径是【/frs/yufreejs?js=/yujs/east/58table.js】
function AfterInit(){
	JSPFree.createBillList("d1","/biapp-pscs/freexml/pscs/datahandler/pscs_engine_log_ref.xml");
	
	var task_id = "88888888-8888-8888-8888-888888888888";  //取得选中记录中的id值
	str_sqlWhere = "task_id='"  + task_id + "'";  //拼SQL条件

	JSPFree.queryDataByConditon(d1_BillList,str_sqlWhere);  //锁定规则表查询数据
	
	setTimeout('refresh()',10000);//每三秒执行一次
}

/**
 * 刷新
 * @returns
 */
function refresh(){
	JSPFree.queryDataByConditon(d1_BillList,str_sqlWhere);
}