/**
 * 前置条件/规则/预警
 * @returns
 */
function AfterInit(){
	JSPFree.createBillCard("d1","/biapp-east/freexml/east/rule/east_cr_rule_view.xml",["取消/onCancel/icon-no"],null);
	var str_sqlWhere = "id='"  + jso_OpenPars.id + "'";  //拼SQL条件
	JSPFree.queryBillCardData(d1_BillCard, str_sqlWhere);  //锁定规则表查询数据
}

function onCancel(){
	JSPFree.closeDialogAll();
}
