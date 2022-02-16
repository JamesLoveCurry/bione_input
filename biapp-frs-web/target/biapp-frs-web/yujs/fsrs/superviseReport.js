//初始化界面,菜单配置路径是【/frs/yufreejs?js=/yujs/east/58table.js】
var fileName = "";

function AfterInit(){
	JSPFree.createBillList("d1","/biapp-fsrs/freexml/fsrs/fsrs_cr_report_CODE1.xml",null,{isSwitchQuery:"N"});
}

//创建任务
function onCreateReport(){
  //alert("创建任务");
  var jso_par = {};  //参数
  JSPFree.openDialog("创建任务","/yujs/fsrs/fsrs_cr_report_create.js",800,500,jso_par,function(_rtdata){
	  if (_rtdata != null) { 
		  if ("dirclose" == _rtdata.type) {
				return;
		  }
		  JSPFree.queryDataByConditon(d1_BillList,_rtdata.wheresql);  //立即查询数据
		  $.messager.alert('提示', '创建成功!', 'warning');
	  }
  });
}

//删除（处理中，不能删除）
function onDeleteReport(){
	var selectDatas = d1_BillList.datagrid('getSelections');
	if (selectDatas.length == 0) {
		$.messager.alert('提示', '至少选择一条记录进行操作', 'warning');
		return;
	}
	
	var tab_name = "";
	for(var i=0; i<selectDatas.length; i++){
		if (selectDatas[i].status.match("处理中")) {
			tab_name += selectDatas[i].tab_name + ","
		}
	}
	if (tab_name != null && tab_name != "") {
		$.messager.alert('提示', '包含处理中的任务，不能删除', 'warning');
	} else {
		// 执行删除(批量)
		JSPFree.doBillListBatchDelete(d1_BillList);
	}
}

//启动任务
function onStartReport(){
	var selectDatas = d1_BillList.datagrid('getSelections');
	if (selectDatas.length == 0) {
		JSPFree.openDialog("一键启动","/yujs/fsrs/superviseReportDate.js",350,350,{currSQL:d1_BillList.CurrSQL},function(_rtdata){});
	}
	else{
		var isHaveAlreadyStart = false;
		var ary_taskids = [];  //所有rid数组
		for(var i=0; i<selectDatas.length; i++){
			// ②判断当前选中的任务，状态是否是已生成完的报文；如果是已生成，则弹出提示，是否要覆盖
			if ("报文已生成" == selectDatas[i].status) {
				isHaveAlreadyStart = true;
			}
			ary_taskids.push(selectDatas[i].rid);
		}
		
		if (isHaveAlreadyStart) {
			JSPFree.confirm('提示', '当前选中的记录包含已生成的报文记录，是否继续启动，如继续启动，将覆盖原已生成文件?', function(_isOK){
				if (_isOK){
					JSPFree.openDialog2("启动选择的任务","/yujs/fsrs/superviseReport_start.js",750,350,{allTaskIds:ary_taskids});
				}
			});
		} else {
			JSPFree.openDialog2("启动选择的任务","/yujs/fsrs/superviseReport_start.js",750,350,{allTaskIds:ary_taskids});
		}
	}
}

//一键启动
function onAllStartReport(){
	JSPFree.openDialog("一键启动","/yujs/fsrs/superviseReportDate.js",350,350,{currSQL:d1_BillList.CurrSQL},function(_rtdata){
	});
}

//打包下载
function onZipAndDownload() {
	JSPFree.openDialog("压缩下载一个日期下的所有报文","/yujs/fsrs/superviseReport_choosedate.js",350,350);
}

//查看日志
function onViewLog(){
	var selectDatas = d1_BillList.datagrid('getSelections');
	if (selectDatas.length == 0) {
		$.messager.alert('提示', '至少选择一条记录进行操作', 'warning');
		return;
	} else if (selectDatas.length > 1) {
		$.messager.alert('提示', '只能选择一条记录进行操作', 'warning');
		return;
	}
	
	var json_data = selectDatas[0];
	if (json_data == null) {
		$.messager.alert('提示', '必须选择一条记录进行操作', 'warning');
	}
	
	JSPFree.openDialog("查看日志","/yujs/fsrs/fsrs_cr_report_viewlog.js",900,420,{rid:json_data.rid},function(_rtdata){
		//回调方法,立即查询数据
	});
}

function onView(_this){
	var dataset = _this.dataset;
	var index = dataset.rowindex;
	var rows = d1_BillList.datagrid("getRows");
	var row = rows[index];//index为行号
	
	$.messager.alert('文件路径','该文件较大，请到服务器下载，下载地址为：'+ row.filepath +'');
}
