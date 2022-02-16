//初始化界面,菜单配置路径是【/frs/yufreejs?js=/yujs/east/east_cr_report_config.js】
var str_rid = null;
var str_sql = null;
function AfterInit(){
	str_rid = jso_OpenPars.rid;
	str_sql = "task_rid='" + str_rid + "'";
	JSPFree.createBillList("d1","/biapp-pscs/freexml/pscs/datagramtask/pscs_datagram_task_log.xml"); //创建列表
	JSPFree.queryDataByConditon(d1_BillList,str_sql);
}

// 手动刷新
function onRefresh(){
	str_sql = "task_rid='" + str_rid + "'";
	JSPFree.createBillList("d1","pscs/datagramtask/pscs_datagram_task_log");
	JSPFree.queryDataByConditon(d1_BillList,str_sql);
}
