//初始化界面,菜单配置路径是【/frs/yufreejs?js=/yujs/east/east_pub_job_config.js】
function AfterInit(){
 	JSPFree.createSplit("d1", "上下", 400); // 页签先左右分割

	JSPFree.createBillList("d1_A", "/biapp-fsrs/freexml/fsrs/fsrs_pub_job_CODE.xml",null,{isSwitchQuery:"N"});
 	JSPFree.createBillList("d1_B", "/biapp-fsrs/freexml/fsrs/fsrs_pub_job_b_CODE.xml",null,{isSwitchQuery:"N"});


 	// 绑定表格选择事件,d1_A_BillList会根据命名规则已创建
	JSPFree.bindSelectEvent(d1_A_BillList, function(rowIndex, rowData) {
		var rid = rowData.rid; // 取得选中记录中的id值
		var str_sqlWhere = "jobid='" + rid + "'"; // 拼SQL条件
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
	var jso_data = JSPFree.doClassMethodCall("com.yusys.fsrs.scheduled.FsrsJobBS","startJob",jso_par); 
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
	var jso_data = JSPFree.doClassMethodCall("com.yusys.fsrs.scheduled.FsrsJobBS","stopJob",jso_par); 
	if(jso_data.code == 0){
		$.messager.alert('提示', '停止成功');
	}
	else{
		$.messager.alert('提示', jso_data.msg, 'warning');
	}
	JSPFree.refreshBillListCurrRow(d1_A_BillList);
}

/**
 * 删除
 * @return {[type]} [description]
 */
function delete1(){
	var selectDatas = d1_A_BillList.datagrid('getSelected');
	if (selectDatas == null) {
		$.messager.alert('提示', '必须选择一条记录进行操作', 'warning');
		return;
	}
	
	if (selectDatas.jobstat == "启动") {
		$.messager.alert('提示', '当前任务已启动，不能删除！', 'warning');
		return;
	}
	
	var jso_data = JSPFree.getHashVOs("SELECT ORG_NAME,REPORT_TYPE FROM fsrs_cr_report_config where AUTOJOBTRIGGER='"+selectDatas.rid+"'");
	if(jso_data != null && jso_data.length > 0){
		var msg = "当前任务已被报文策略【";
		if(jso_data.length > 1){
			for(var i=0; i<jso_data.length; i++){
				str_org_name = jso_data[i].org_name;
				str_report_type = jso_data[i].report_type;
				if(i==jso_data.length-1){
					msg = msg +"机构名称为"+str_org_name+"，报送频率为"+str_report_type+"】关联，不能删除或取消关联后再删除！";
				}else{
					msg = msg +"机构名称为"+str_org_name+"，报送频率为"+str_report_type+"；";
				}
			}
		}else{
			str_org_name = jso_data[0].org_name;
			str_report_type = jso_data[0].report_type;
			msg = msg +"机构名称为"+str_org_name+"，报送频率为"+str_report_type+"】关联，不能删除或取消关联后再删除！";
		}
		
		JSPFree.alert(msg);
		return;
	}
	
	JSPFree.confirm("提示", "你真的要删除选中的记录吗?", function(_isOK){
		if(_isOK){
			var jso_rt = JSPFree.doClassMethodCall("com.yusys.fsrs.scheduled.FsrsScheduleBS", "deleteJobData",{rid:selectDatas.rid});
			if(jso_rt.msg == "OK"){
				$.messager.show({title:'消息提示',msg: '删除成功',showType:'show'});
				JSPFree.queryDataByConditon(d1_A_BillList);  //立即查询刷新数据
				JSPFree.queryDataByConditon(d1_B_BillList);  //立即查询刷新数据
			}
			else{
				JSPFree.alert(jso_rt.msg);
			}
		}
	});
}