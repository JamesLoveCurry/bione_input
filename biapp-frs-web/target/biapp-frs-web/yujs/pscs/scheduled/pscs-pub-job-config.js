function AfterInit(){
 	JSPFree.createSplit("d1", "上下", 400); // 页签先左右分割

	JSPFree.createBillList("d1_A", "/biapp-pscs/freexml/pscs/scheduled/pscs_pub_job_config.xml",null,{isSwitchQuery:"N"});
 	JSPFree.createBillList("d1_B", "/biapp-pscs/freexml/pscs/scheduled/pscs_pub_job_log.xml",null,{isSwitchQuery:"N"});


 	// 绑定表格选择事件,d1_A_BillList会根据命名规则已创建
	JSPFree.bindSelectEvent(d1_A_BillList, function(rowIndex, rowData) {
		var rid = rowData.rid; // 取得选中记录中的id值
		var str_sqlWhere = "jobid='" + rid + "' order by begintime desc"; // 拼SQL条件
		JSPFree.queryDataByConditon(d1_B_BillList, str_sqlWhere); // 锁定规则表查询数据
	});
}


function startJob(){
	var selectDatas = d1_A_BillList.datagrid('getSelections');
	if (selectDatas.length == 0) {
		$.messager.alert('提示', '至少选择一条记录进行操作', 'warning');
		return;
	}
	else if (selectDatas[0].activeflag == "N") {
		$.messager.alert('提示', '请先激活！', 'warning');
		return;
	}
	else if (selectDatas[0].jobstat == "启动") {
		$.messager.alert('提示', '任务已启动无需再启动', 'warning');
		return;
	}

	var jso_par = {jobName:selectDatas[0].name};
	var jso_data = JSPFree.doClassMethodCall("com.yusys.pscs.scheduled.PscsJobBS","startJob",jso_par); 
	if(jso_data.code == 0){
		$.messager.alert('提示', '启动成功');
	}
	else{
		$.messager.alert('提示', jso_data.msg, 'warning');
	}
	JSPFree.refreshBillListCurrRow(d1_A_BillList);
}

function endJob(){
	var selectDatas = d1_A_BillList.datagrid('getSelections');
	if (selectDatas.length == 0) {
		$.messager.alert('提示', '至少选择一条记录进行操作', 'warning');
		return;
	}
	else if (selectDatas[0].jobstat == "停止") {
		$.messager.alert('提示', '任务已经是停止状态', 'warning');
		return;
	}


	var jso_par = {jobName:selectDatas[0].name};
	var jso_data = JSPFree.doClassMethodCall("com.yusys.pscs.scheduled.PscsJobBS","stopJob",jso_par); 
	if(jso_data.code == 0){
		$.messager.alert('提示', '停止成功');
	}
	else{
		$.messager.alert('提示', jso_data.msg, 'warning');
	}
	JSPFree.refreshBillListCurrRow(d1_A_BillList);
}

function delete1(){
	var job = d1_A_BillList.datagrid('getSelected');
	if (job == null) {
		$.messager.alert('提示', '必须选择一条记录进行操作', 'warning');
		return;
	}
	//判断状态
	if(job.jobstat == "启动"){
		$.messager.alert('提示', '当前任务处于启动状态，请先停止在删除！', 'warning');
		return;
	}
	
	//校验是否被使用了
	var jso_rt = JSPFree.doClassMethodCall("com.yusys.pscs.scheduled.PscsJobBS", "checkDeleteJob",{jobId:job.rid});
	if(jso_rt.code == 1){
		JSPFree.alert(jso_rt.msg);
		return;
	}

	JSPFree.confirm("提示", "你真的要删除选中的记录吗?", function(_isOK){
		if(_isOK){
			var jso_rt = JSPFree.doClassMethodCall("com.yusys.pscs.scheduled.PscsJobBS", "deleteJob",{jobId:job.rid});
			if(jso_rt.code == 0){
				$.messager.show({title:'消息提示',msg: jso_rt.msg ,showType:'show'});
				JSPFree.queryDataByConditon(d1_A_BillList);  //立即查询刷新数据
			}
			else{
				JSPFree.alert(jso_rt.msg);
			}
		}
	});
}