//总行完整性评估
function AfterInit() {
	JSPFree.createBillList("d1","/biapp-east/freexml/east/report/east_cr_empty_summary_stat_fh_CODE1.xml");
}
function update(){
	var jso_rt = null;
	
	var selectDatas = d1_BillList.datagrid('getSelections');
	if (selectDatas.length != 1) {
		$.messager.alert('提示', '选择一条记录进行操作', 'warning');
		return;
	}

	JSPFree.openDialog("数据完整性评估表","/yujs/east/report/east_cr_empty_summary_stat.js",900,560,{org_no:selectDatas[0].org_no,data_dt:selectDatas[0].data_dt},function(_rtdata){
		
	});
}

function onImport(){
	var jso_rt = null;

	var selectDatas = d1_BillList.datagrid('getSelections');
	if (selectDatas.length != 1) {
		$.messager.alert('提示', '选择一条记录进行操作', 'warning');
		return;
	}

	JSPFree.openDialog("文件上传","/yujs/east/report/east_cr_report_eval_Inserts.js", 500, 240, null,function(_rtdata){
		if (_rtdata != null && _rtdata != "undefined") {
			JSPFree.queryDataByConditon(d1_BillList,_rtdata.whereSQL);  //立即查询刷新数据
		}
		
	});
}

function onExport_1(){
	var jso_rt = null;

	var selectDatas = d1_BillList.datagrid('getSelections');
	if (selectDatas.length != 1) {
		$.messager.alert('提示', '选择一条记录进行操作', 'warning');
		return;
	}

	var str_sql = "select data_dt, org_no, tab_name, col_name, fail_count, empty_rate, src_sys, reasons, solves, progress from east_cr_empty_summary where org_no = '"+selectDatas[0].org_no+"' and data_dt = '"+ selectDatas[0].data_dt +"'";

	var org = selectDatas[0].org_name;
	var date = selectDatas[0].data_dt;
	var name = org + "-" + date.substring(0,4) + "年" +  date.substring(4,6) + "月份";
	
	JSPFree.downloadExcelBySQL(name + "-EAST数据完整性评估表.xls", str_sql, name + "-EAST数据完整性评估表","采集日期,机构号,表名,字段名,空值数量,空值率,源系统,空值原因,解决方案,解决进度");
}