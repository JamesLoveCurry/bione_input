//初始化界面,菜单配置路径是
function AfterInit(){
	JSPFree.createBillList("d1","/biapp-cr/freexml/codelibrary/icr_blacklist.xml",null,{isSwitchQuery:"N"});

}
function onExport_1(){
	var str_sql = d1_BillList.CurrSQL;
	if (str_sql == null) {
		JSPFree.alert("当前无记录！");
		return;
	}
	JSPFree.downloadExcelBySQL("个人黑名单.xls", str_sql, "个人黑名单","证件类型,证件号码,公司名称,备注");
}	
