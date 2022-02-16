//初始化界面,菜单配置路径是【/frs/yufreejs?js=/yujs/east/58table.js】
function AfterInit(){
	var str_customer_code = jso_OpenPars.customer_code;
	var str_data_dt = jso_OpenPars.data_dt;
	// 创建列表
	JSPFree.createBillCard("d1","/biapp-crrs/freexml/crrs/generateXml/query/query_personGuaranteed_CODE1.xml");

	var str_sqlWhere = "customer_code='"  + str_customer_code + "' and data_dt = '" + str_data_dt + "'";  //拼SQL条件
	var jso_data = JSPFree.queryBillCardData(d1_BillCard, str_sqlWhere);
}

function AfterBodyLoad() {
	FreeUtil.setBillCardItemEditable(d1_BillCard,"*",false);
}