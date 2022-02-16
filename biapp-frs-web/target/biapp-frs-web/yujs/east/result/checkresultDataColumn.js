//初始化界面,菜单配置url路径是【/frs/yufreejs?js=/yujs/east/warningLevel.js】
var tabName = "";
var dataDt="";
function AfterInit(){
	tabName = jso_OpenPars.tab_name; 
	dataDt = jso_OpenPars.data_dt;
	JSPFree.createBillList("d1","/biapp-east/freexml/east/result/v_east_cr_summary8.xml");
	var str_sqlWhere = "tab_name='"  + tabName + "' and data_dt='"+dataDt+"'";  //拼SQL条件
	JSPFree.queryDataByConditon(d1_BillList,str_sqlWhere);  //锁定规则表查询数据
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
	jso_OpenPars.tab_name = tabName;
	JSPFree.openDialog(row.tab_name,"/yujs/east/checkresultInterFieldHistory.js", 700, 450, jso_OpenPars,function(_rtdata){});
}
/*规则函数*/
function ruleFn(_btn){
	var dataset = _btn.dataset;  //数据都在这个map中
	var index = dataset.rowindex;
	var rows = d1_BillList.datagrid("getRows");
	var row = rows[index];//index为行号
	
	var jso_OpenPars = {};
	jso_OpenPars.col_name = row.col_name;
	jso_OpenPars.tab_name = tabName;
	JSPFree.openDialog(row.tab_name,"/yujs/east/checkresultInterFieldRules.js", 700, 450, jso_OpenPars,function(_rtdata){});
}
/**
 * 导出
 * @returns
 */
function exports(){
	var download=null;
	download = $('<iframe id="download" style="display: none;"/>');
	$('body').append(download);
	
	var src = v_context + "/east/interface/order/downloadField?tabName="
			+ tabName
			+ "&dataDt="
			+ dataDt;
	download.attr('src', src);
}