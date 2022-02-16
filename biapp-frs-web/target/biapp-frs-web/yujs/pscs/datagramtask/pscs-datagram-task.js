var fileName = "";

function AfterInit(){
	JSPFree.createBillList("d1","/biapp-pscs/freexml/pscs/datagramtask/pscs_datagram_task.xml",null,{isSwitchQuery:"N"});
}

//创建任务
function onCreateReport(){
  var jso_par = {};  //参数
  JSPFree.openDialog("创建任务","/yujs/pscs/datagramtask/pscs-datagram-task-create.js",800,500,jso_par,function(_rtdata){
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
	var taskIds = "";
	for(var i=0; i<selectDatas.length; i++){
		if (selectDatas[i].status.match("生成报文中")) {
			tab_name += selectDatas[i].tab_name + ","
		}
		taskIds = taskIds + selectDatas[i].rid + ";";
	}
	if (tab_name != null && tab_name != "") {
		$.messager.alert('提示', '包含生成报文中的任务，不能删除', 'warning');
	} else {
		JSPFree.confirm("提示", "你真的要删除选中的记录吗?", function(_isOK){
			if(_isOK){
				var jso_rt = JSPFree.doClassMethodCall("com.yusys.pscs.datagramtask.service.PscsDatagramTaskBS", "deleteTaskAndReportList",{taskIds:taskIds});
				if(jso_rt.code == 0){
					$.messager.show({title:'消息提示',msg: jso_rt.msg ,showType:'show'});
					JSPFree.queryDataByConditon(d1_BillList);  //立即查询刷新数据
				}
				else{
					JSPFree.alert(jso_rt.msg);
				}
			}
		});
	}
}

//启动任务
function onStartReport(){
	var selectDatas = d1_BillList.datagrid('getSelections');
	if (selectDatas.length == 0) {
		JSPFree.openDialog2("一键启动","/yujs/pscs/datagramtask/pscs-datagram-task-startAll.js",350,350,{currSQL:d1_BillList.CurrSQL},function(_rtdata){
		});
	} else{
		var isHaveAlreadyStart = false;
		var ary_taskids = [];  //所有rid数组
		for(var i=0; i<selectDatas.length; i++){
			// ②判断当前选中的任务，状态是否是已生成完的报文；如果是已生成，则弹出提示，是否要覆盖
			if ("完成" == selectDatas[i].status) {
				isHaveAlreadyStart = true;
			}
			ary_taskids.push(selectDatas[i].rid);
		}
		
		if (isHaveAlreadyStart) {
			JSPFree.confirm('提示', '当前选中的部分任务已生成报文，重新启动将覆盖已有报文文件?', function(_isOK){
				if (_isOK){
					JSPFree.openDialog2("启动选择的任务","/yujs/pscs/datagramtask/pscs-datagram-task-start.js",750,350,{allTaskIds:ary_taskids});
				}
			});
		} else {
			JSPFree.openDialog2("启动选择的任务","/yujs/pscs/datagramtask/pscs-datagram-task-start.js",750,350,{allTaskIds:ary_taskids});
		}
	}
}

//zip打包下载
function onZipAndDownload() {
	JSPFree.openDialog("一键打包下载本机构某一时间的所有报文","/yujs/pscs/datagramtask/pscs-datagram-task-zipdownlod.js",350,350);
}

//sec打包下载
function onSecAndDownload() {
	JSPFree.openDialog("一键打包下载本机构某一时间的所有加密报文","/yujs/pscs/datagramtask/pscs-datagram-task-secdownlod.js",350,350);
}

//多选，打包后直接下载
function onZipAndDownloadBySelection() {
	var jsy_datas = JSPFree.getBillListSelectDatas(d1_BillList);
	if(jsy_datas==null || jsy_datas.length<=0){
		JSPFree.alert("至少选择一条数据！");
		return;
	}
	
	var jsy_rids = [];  //存储所有选中的主键
	var finishCount = 0;
	
	var data_dt = jsy_datas[0]["data_dt"]; //储存第一个日期，用于和其他日期比较
	for(var i=0;i<jsy_datas.length;i++){
		if(jsy_datas[i]["data_dt"] != data_dt){
			JSPFree.alert("您选择的报文任务不是同一采集日期，<br/>请重新选择！");
			return;
		}
	}
	
	var org_no = jsy_datas[0]["org_no"]; //储存第一个机构编号，用于和其他机构比较
	for(var i=0;i<jsy_datas.length;i++){
		if(jsy_datas[i]["org_no"] != org_no){
			JSPFree.alert("您选择的报文任务不属于同一机构，<br/>请重新选择！");
			return;
		}
	}
	
	for(var i=0;i<jsy_datas.length;i++){
		if("已创建" == jsy_datas[i]["status"] || "失败" == jsy_datas[i]["status"] || "处理中" == jsy_datas[i]["status"]){ //判断选中报文任务报文是否全部生成，否则提示重新选择
			finishCount++;
		}else{
			jsy_rids.push(jsy_datas[i]["rid"]);
		}
	}
	if(finishCount != 0){
		JSPFree.alert("您选择的报文任务对应报文未全部生成，<br/>请重新选择！");
		return;
	}
	
	JSPFree.confirm('提示', '压缩打包下载报文是一个耗时操作，需要耐心等待。请确定是否继续！', function(_isOK){
		if (_isOK){
			var _sql = "select * from pscs_datagram_task where 1=1 and rid in(";
			for(var i=0;i<jsy_rids.length;i++){
				if(i==jsy_rids.length-1){
					_sql += "'"+jsy_rids[i]+"')";
				} else{
					_sql += "'"+jsy_rids[i]+"',";
				}
			}
			var jso_status = JSPFree.doClassMethodCall("com.yusys.pscs.datagramtask.service.PscsDatagramTaskBS","checkZipAndDownloadReportFileBySelection",{SQL:_sql});
			if(jso_status.code=="1"){
				var download=null;
				download = $('<iframe id="download" style="display: none;"/>');
				$('body').append(download);
				
				var src = v_context + "/datagramtask/download?org_no=" + org_no + "&data_dt=" + data_dt;
				download.attr('src', src);
			}else{
				JSPFree.alert(jso_status.msg);
				return;
			}
		}
	});
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
	
	JSPFree.openDialog("查看日志","/yujs/pscs/datagramtask/pscs-datagram-task-log.js",900,420,{rid:json_data.rid},function(_rtdata){
		//回调方法,立即查询数据
	});
}