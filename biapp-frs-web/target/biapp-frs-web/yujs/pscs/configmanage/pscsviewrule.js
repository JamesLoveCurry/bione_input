/**
 * 查看具体某一条规则
 * @returns
 */
function AfterInit(){
	JSPFree.createBillCard("d1","/biapp-pscs/freexml/pscs/configmanage/pscs_cr_rule_view.xml",["取消/onCancel/icon-no"],null);
	var str_sqlWhere = "id='"  + jso_OpenPars.id + "'";  //拼SQL条件
	JSPFree.queryBillCardData(d1_BillCard, str_sqlWhere);  //锁定规则表查询数据
}

function onCancel(){
	JSPFree.closeDialogAll();
}
