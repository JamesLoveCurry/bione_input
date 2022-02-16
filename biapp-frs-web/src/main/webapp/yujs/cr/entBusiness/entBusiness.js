//初始化界面,菜单配置路径是
function AfterInit(){
    JSPFree.createTabb("d1",["收入支出表信息","资产负债表信息"]);
	JSPFree.createBillList("d1_1","/biapp-cr/freexml/entBusiness/Ent_IncomeAndExpenseStatement.xml");
	JSPFree.createBillList("d1_2","/biapp-cr/freexml/entBusiness/Ent_InstitutionBalanceSheet.xml");
}