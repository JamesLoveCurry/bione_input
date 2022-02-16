//锁定与解锁数据
function AfterInit(){
	JSPFree.createBillList("d1","/biapp-crrs/freexml/crrs/rule/crrs_lock_data_CODE1.xml",null,{list_btns:"[icon-p38]新增/createLock;[icon-p18]锁定/onLock_1;[icon-p20]解锁/onUnLock_1",isSwitchQuery:"N"});

	JSPFree.queryDataByConditon2(d1_BillList,null);
}

//创建任务
function createLock(){
	JSPFree.openDialog("新增","/yujs/crrs/datahandle/LockDataDate.js",350,350,{currSQL:d1_BillList.CurrSQL},function(_rtdata){
		if (_rtdata != null) {
			if (_rtdata.status == "OK") {
				JSPFree.queryDataByConditon(d1_BillList,_rtdata.wheresql);  //立即查询数据
				 $.messager.alert('提示', '创建成功!', 'info');
			} else if ("dirclose" == _rtdata.type) {
				return;
			} else {
				$.messager.alert('提示', '创建失败，原因提交重复日期!', 'info');
			}
		  }
	});
}

//锁定
function onLock_1(){
	var jso_rt = null;
	
	var selectDatas = d1_BillList.datagrid('getSelections');
	if (selectDatas.length != 1) {
		$.messager.alert('提示', '选择一条记录进行操作', 'warning');
		return;
	}

	if ("锁定" == selectDatas[0].status) {
		JSPFree.confirm("提示","当前数据已锁定，确定要继续么?",function(_isOK){
			if(_isOK){
				doCommitUpdateDB(selectDatas[0].data_dt, "锁定");
			}
		});
	} else {
		doCommitUpdateDB(selectDatas[0].data_dt, "锁定");
	}
}

//解锁
function onUnLock_1	(){
	var jso_rt = null;
	
	var selectDatas = d1_BillList.datagrid('getSelections');
	if (selectDatas.length != 1) {
		$.messager.alert('提示', '选择一条记录进行操作', 'warning');
		return;
	}
	
	if ("解锁" == selectDatas[0].status) {
		JSPFree.confirm("提示","当前数据已解锁，确定要继续么?",function(_isOK){
			if(_isOK){
				doCommitUpdateDB(selectDatas[0].data_dt,"解锁");
			}
		});
	} else {
		doCommitUpdateDB(selectDatas[0].data_dt, "解锁");
	}
}

function doCommitUpdateDB(data_dt, status){
	jso_rt = JSPFree.doClassMethodCall("com.yusys.crrs.lock.service.CrrsLockBSDMO", "startLockData", {data_dt:data_dt,status:status});
	if (jso_rt != null && jso_rt.status == "OK") {
		JSPFree.refreshBillListCurrRows(d1_BillList);  //刷新当前行
	}
}