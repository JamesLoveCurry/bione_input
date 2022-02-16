var fileName = "";

function AfterInit(){
	JSPFree.createBillList("d1","/biapp-cams/freexml/cams/datagram/cams_datagram_task.xml",null,{isSwitchQuery:"N"});
}

//创建任务
function onCreateReport(){
	var jso_par = {};  //参数
	JSPFree.openDialog("创建任务","/yujs/cams/datagram/cams-datagram-task-create.js",600,350,jso_par,function(_rtdata){});
}

//重报报文
function onAgainReport(){
	var report_size = 2000;//jso_cardData.report_size;
	var report_type = "CRS701";//新数据
	var rtype = "2";//重报报文
	
	$.ajax({
		cache : false,
		async : false,
		url : "cams/datagram/task/startReportTask",
		dataType : 'json',
		data : {
			reportSize : report_size,
			reportType : report_type,
			rtype : rtype
		},
		type : "post",
		success : function(result) {
			if (result && result.state) {
				if(result.warning){
					JSPFree.alert(result.warning);
				}else{
					JSPFree.alert(result.msg);
				}
			}
			onRefresh();  //立即查询数据
		},
		error : function() {
			JSPFree.alert("报文生成错误");
		}
	});
}

//删除报文
function onDeleteReport(){
	var selectDatas = d1_BillList.datagrid('getSelections');
	if (selectDatas.length == 0) {
		$.messager.alert('提示', '至少选择一条记录进行操作', 'warning');
		return;
	}
	if (selectDatas.length > 1) {
		$.messager.alert('提示', '只能选择一条记录进行操作', 'warning');
		return;
	}
	var reportIds = "";
	for(var i=0; i<selectDatas.length; i++){
		reportIds = reportIds + selectDatas[i].rid + ",";
		if (("回执处理" == selectDatas[i].status) || ("完成" == selectDatas[i].status)) {//判断是否有已完成的报文
			JSPFree.alert("选择报文中有已报送的报文，所以不能删除");
			return;
		}
	}
	reportIds = reportIds.slice(0, reportIds.length - 1);
	JSPFree.confirm("提示", "你真的要删除选中的记录吗?", function(_isOK){
		if(_isOK){
			$.ajax({
				cache : false,
				async : false,
				url : "cams/datagram/task/deleteReportTask",
				dataType : 'json',
				data : {
					reportIds : reportIds
				},
				type : "post",
				success : function(result) {
					if (result && result.state) {
						JSPFree.alert(result.msg);
					}
					onRefresh();  //立即查询数据
				},
				error : function() {
					JSPFree.alert("报文删除错误");
				}
			});
		}
	});
}

//打包下载
function onZipAndDownload(){
	JSPFree.openDialog("一键打包下载本机构某一时间的所有报文","/yujs/cams/datagram/cams-datagram-task-zipdownlod.js",350,350);
}

//查看日志
function onViewLog(){
	var selectDatas = d1_BillList.datagrid('getSelections');
	if (selectDatas.length != 1) {
		$.messager.alert('提示', '请选择一条记录进行操作', 'warning');
		return;
	}
	
	var reportId = selectDatas[0].rid;
	
	var jso_par = {reportId : reportId};  //参数
	JSPFree.openDialog("查看日志","/yujs/cams/datagram/cams-datagram-task-viewLog.js",750,600,jso_par,function(_rtdata){
		if (_rtdata != null) {
			if ("dirclose" == _rtdata.type) {
				return;
			}
		}
	});
}

//报文预览
function onPreview(){
	var selectDatas = d1_BillList.datagrid('getSelections');
	if (selectDatas.length == 0) {
		$.messager.alert('提示', '至少选择一条记录进行操作', 'warning');
		return;
	}
	if("生成失败"==selectDatas[0].status){
		$.messager.alert('提示', '当前报文任务生成失败，无法预览报文', 'warning');
		return;
	 }
	var jso_rt = JSPFree.doClassMethodCall("com.yusys.cams.query.service.CamsQueryBSDMO","getReportContentHtml",{filePath:selectDatas[0].filepath});
	
	FreeUtil.openHtmlMsgBox2("报文预览",800,600,jso_rt.html);
}

//刷新列表
function onRefresh(msg){
	if(msg){
		JSPFree.alert(msg);
	}
	JSPFree.queryDataByConditon(d1_BillList);  //立即查询数据s
}