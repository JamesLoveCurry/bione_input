var fileName = "";

function AfterInit(){
	JSPFree.createBillList("d1","/biapp-cams/freexml/cams/receipt/cams_datagram_receipt.xml",null,{isSwitchQuery:"N"});
}

//回执导入
function onImport(){
	JSPFree.openDialog("文件上传", "/yujs/cams/receipt/importReceipt.js", 500, 240, null,
		function(_rtdata) {
			if (_rtdata != null && _rtdata != "undefined") {
				if(_rtdata.msg){
					JSPFree.alert(_rtdata.msg);
				}
				JSPFree.queryDataByConditon(d1_BillList, _rtdata.whereSQL); // 立即查询刷新数据
			}
	});
}

//回执预览
function onPreview(){
	var selectDatas = d1_BillList.datagrid('getSelections');
	if (selectDatas.length == 0) {
		$.messager.alert('提示', '至少选择一条记录进行操作', 'warning');
		return;
	}
	var jso_rt = JSPFree.doClassMethodCall("com.yusys.cams.query.service.CamsQueryBSDMO","getReportContentHtml",{filePath:selectDatas[0].filepath});
	
	FreeUtil.openHtmlMsgBox2("报文预览",800,600,jso_rt.html);
}