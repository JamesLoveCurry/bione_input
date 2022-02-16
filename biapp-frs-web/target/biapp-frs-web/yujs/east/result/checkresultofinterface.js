//初始化界面,菜单配置路径是【/frs/yufreejs?js=/yujs/east/58table.js】
function AfterInit(){
	JSPFree.createBillList("d1","/biapp-east/freexml/east/result/east_cr_summer_data.xml",null,{isSwitchQuery:"N"});
}

// 查看详情
function detail(_btn) {
	var dataset = _btn.dataset;
	var index = dataset.rowindex;
	var rows = d1_BillList.datagrid("getRows");
	var row = rows[index];//index为行号
	
	var jso_OpenPars = {tabName: row.tab_name,tabNameEn:row.tab_name_en,dataDt:row.data_dt};
	// 查看详情前，先确认表是否存在
	var jso_rt = JSPFree.doClassMethodCall("com.yusys.east.checkresult.summary.service.FailDetailBS", 
			"checkTab",jso_OpenPars);
	
	if (jso_rt.msg == "OK") {
		JSPFree.openDialog("错误明细","/yujs/east/result/failDetail.js", 950, 500, jso_OpenPars,function(_rtdata){});
	} else {
		JSPFree.alert("当前明细表不存在，无法查看明细数据!");
	}
}

//导出报送数据
function download() {
	if (d1_BillList.CurrSQL3 == null || "undefined" == d1_BillList.CurrSQL3) {
		JSPFree.alert("当前无记录！");
		return;
	}
	
	JSPFree.downloadBillListCurrSQL3AsExcel(null,d1_BillList);
}

function exportDetail(_btn){
	var dataset = _btn.dataset;
	var index = dataset.rowindex;
	var rows = d1_BillList.datagrid("getRows");
	var row = rows[index];//index为行号
	
	var src = v_context + "/east/checkresult/data/exportCSV?tab_name="
			+ row.tab_name
			+ "&tab_name_en="
			+ row.tab_name_en
			+ "&dataDt="
			+ row.data_dt;
	var download=null;
	download = $('<iframe id="download" style="display: none;"/>');
	$('body').append(download);
	
	// 先确认表是否存在
	var jso_OpenPars = {tabName: row.tab_name,tabNameEn:row.tab_name_en,dataDt:row.data_dt};
	var jso_rt = JSPFree.doClassMethodCall("com.yusys.east.checkresult.summary.service.FailDetailBS", 
			"checkTab",jso_OpenPars);
	
	if (jso_rt.msg == "OK") {
		download.attr('src', src);
	} else {
		JSPFree.alert("当前明细表不存在，无法导出明细数据!");
	}
}
