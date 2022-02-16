//初始化界面,菜单配置路径是
function AfterInit(){
    //JSPFree.createBillList("d1","/biapp-cr/freexml/icrBasicInformation.xml",null,{isSwitchQuery:"N"});
    JSPFree.createBillList("d1","/biapp-cr/freexml/entIncome2002/Ent_BalanceSheet2002.xml");
    
    JSPFree.createSplit("d1","上下",750);  //分页
    //JSPFree.createSplit("d1","上下",750);  //分页

    JSPFree.createTabb("d1_A",["资产负债表信息","现金流量表信息","利润及利润分配表"]);
    JSPFree.createBillList("d1_A_1","/biapp-cr/freexml/entIncome2002/Ent_BalanceSheet2002.xml");  //2002版资产负债表信息
    JSPFree.createBillList("d1_A_2","/biapp-cr/freexml/entIncome2002/Ent_CashFlows2002.xml");  //2002版现金流量表信息
    JSPFree.createBillList("d1_A_3","/biapp-cr/freexml/entIncome2002/Ent_Income2002.xml");  //2002版利润及利润分配表信息
    JSPFree.createTabb("d1",["资产负债表信息","现金流量表信息","利润及利润分配表"]);
    JSPFree.createBillList("d1_1","/biapp-cr/freexml/entIncome2002/Ent_BalanceSheet2002.xml");  //2002版资产负债表信息
    JSPFree.createBillList("d1_2","/biapp-cr/freexml/entIncome2002/Ent_CashFlows2002.xml");  //2002版现金流量表信息
    JSPFree.createBillList("d1_3","/biapp-cr/freexml/entIncome2002/Ent_Income2002.xml");  //2002版利润及利润分配表信息

}
