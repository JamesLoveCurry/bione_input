//初始化界面
function AfterInit() {
	JSPFree.createBillList("d1", "/biapp-east/freexml/east/rule/east_cr_rule_CODE1.xml",null,{isSwitchQuery:"N",list_btns:""});
}

function onView(_this){
	var dataset = _this.dataset;
	var index = dataset.rowindex;
	var rows = d1_BillList.datagrid("getRows");
	var row = rows[index];//index为行号

	var jso_OpenPars = {
		id : row.id
	};
	/*JSPFree.openDialog("规则查看","/yujs/east/condrulewarn.js", 900, 450, jso_OpenPars,function(_rtdata){});*/
	JSPFree.openDialog("规则查看","/yujs/east/rule/condrulewarn1.js", 900, 450, jso_OpenPars,function(_rtdata){});
}