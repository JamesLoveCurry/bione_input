//校验规则查询
function AfterInit(){
	JSPFree.createTabb("d1",["确定性校验","一致性校验","提示性校验"]);
    var jso_listConfig = {list_btns:"$VIEW",ishavebillquery:"Y",isSwitchQuery:"N"};  //列表参数,只有查看按钮,有查询框

	JSPFree.createBillList("d1_1","/biapp-crrs/freexml/crrs/rule/crrs_rule_Sure.xml",null,jso_listConfig);  //确定性校验
	JSPFree.createBillList("d1_2","/biapp-crrs/freexml/crrs/rule/crrs_rule_Consistency.xml",null,jso_listConfig);  //一致性校验
	JSPFree.createBillList("d1_3","/biapp-crrs/freexml/crrs/rule/crrs_rule_Warn.xml",null,jso_listConfig);  //提示性校验

}
