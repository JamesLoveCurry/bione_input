//初始化界面,菜单配置路径是【/frs/yufreejs?js=/yujs/east/checkresultDataInter.js】 检核结果表名排名
function AfterInit(){
	JSPFree.createBillList("d1","/biapp-east/freexml/east/result/v_east_cr_summary4.xml",null,{isSwitchQuery:"N"});
}

function exports(){
	if (d1_BillList.CurrSQL3 == null || "undefined" == d1_BillList.CurrSQL3) {
		JSPFree.alert("当前无记录！");
		return;
	}
	
	JSPFree.downloadBillListCurrSQL3AsExcel(null,d1_BillList);
}
/**
 * 历史
 * @returns
 */
function historyFn(_btn){
	var dataset = _btn.dataset;  //数据都在这个map中
	var index = dataset.rowindex;
	var rows = d1_BillList.datagrid("getRows");
	var row = rows[index];//index为行号
	
	var jso_OpenPars = {};
	jso_OpenPars.col_name = row.col_name;
	JSPFree.openDialog(row.tab_name,"/yujs/east/result/checkresultFieldHistory.js", 700, 450, jso_OpenPars,function(_rtdata){});
}