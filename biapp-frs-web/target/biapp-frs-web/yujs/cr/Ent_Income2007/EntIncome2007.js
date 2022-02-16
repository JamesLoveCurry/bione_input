//初始化界面,菜单配置路径是
function AfterInit(){
    //JSPFree.createSplit("d1","上下",300);
    
    JSPFree.createBillList("d1","/biapp-cr/freexml/entIncome2007/Ent_BalanceSheet2007.xml");
    JSPFree.createTabb("d1",["资产负债表信息","现金流量表信息","利润及利润分配表"]);
    JSPFree.createBillList("d1_1","/biapp-cr/freexml/entIncome2007/Ent_BalanceSheet2007.xml");
    JSPFree.createBillList("d1_2","/biapp-cr/freexml/entIncome2007/Ent_CashFlows2007.xml");
    JSPFree.createBillList("d1_3","/biapp-cr/freexml/entIncome2007/Ent_Income2007.xml");
    /*JSPFree.createBillList("d1_B_4","/biapp-cr/freexml/entbasicinfoxml/EntBasic4.xml");
    JSPFree.createBillList("d1_B_5","/biapp-cr/freexml/entbasicinfoxml/EntBasic5.xml");
    JSPFree.createBillList("d1_B_6","/biapp-cr/freexml/entbasicinfoxml/EntBasic6.xml");
    JSPFree.createBillList("d1_B_7","/biapp-cr/freexml/entbasicinfoxml/EntBasic7.xml");
    JSPFree.createBillList("d1_B_8","/biapp-cr/freexml/entbasicinfoxml/EntBasic8.xml");
    JSPFree.createBillList("d1_B_9","/biapp-cr/freexml/entbasicinfoxml/EntBasic9.xml");*/
    
    //JSPFree.bindSelectEvent(d1_A_BillList,onSelectIDSgmt);  //选择人员基础信息时触发的事件
}    
    //选择人员基础信息时触发的事件,即查询该人员的其他信息!



