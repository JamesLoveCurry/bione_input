function AfterInit(){
	JSPFree.createBillList("d1","/biapp-cams/freexml/cams/query/cams_report_query_CODE1.xml",null,{isSwitchQuery:"N"});
}

//报文预览
function onPreview(){
	var report = d1_BillList.datagrid('getSelected');
	if (report == null) {
		$.messager.alert('提示', '必须选择一条记录进行操作', 'warning');
		return;
	}
	var jso_rt = JSPFree.doClassMethodCall("com.yusys.cams.query.service.CamsQueryBSDMO","getReportContentHtml",{filePath:report.filepath});
	
	FreeUtil.openHtmlMsgBox2("报文预览",800,600,jso_rt.html);
}