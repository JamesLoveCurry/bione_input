//初始化界面,菜单配置路径是
function AfterInit(){
	JSPFree.createBillList("d1","/biapp-cr/freexml/codelibrary/icr_codelibrary.xml",null,{isSwitchQuery:"N"});
	
	

}
function onExport_1(){
	var str_sql = d1_BillList.CurrSQL;
	if (str_sql == null) {
		JSPFree.alert("当前无记录！");
		return;
	}
	JSPFree.downloadExcelBySQL("个人码值表.xls", str_sql, "个人码值表","数据项类型,数据项名称,银行代码,银行代码说明,规范代码,规范代码说明,备注");
}	

