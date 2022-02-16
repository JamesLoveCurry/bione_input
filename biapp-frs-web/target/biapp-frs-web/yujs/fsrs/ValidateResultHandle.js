//处理..
var str_data_dt = "";
var str_LoginUserCode = window.self.str_LoginUserCode;
function AfterInit(){
	JSPFree.createBillList("d1","/biapp-fsrs/freexml/fsrs/fsrs_result_standard_Submit_CODE1.xml",null,{isSwitchQuery:"N"});

	// 获取最新一期的日期
	var jso_data = JSPFree.getHashVOs("SELECT max(reportdate) data_dt FROM fsrs_result_standard");
	if(jso_data != null && jso_data.length > 0){
		str_data_dt = jso_data[0].data_dt;
	}

	FreeUtil.loadBillQueryData(d1_BillList, {reportdate:str_data_dt});
	JSPFree.queryDataByConditon2(d1_BillList,"reportdate ='"+str_data_dt+"' and "+getCondition());
}

function getCondition() {
	var condition = ""; //就算这里是""，下面也会对condition覆盖掉的
	var jso_rt = JSPFree.doClassMethodCall("com.yusys.fsrs.datahandle.service.FsrsValidateQueryCondition","getQueryCondition",{"_loginUserOrgNo" : str_LoginUserOrgNo});
	if(jso_rt.msg == "ok"){
		condition = jso_rt.condition;
	}
	
	return condition;
}

//单一处理
function onHandle_1_1(){
	var selectData = d1_BillList.datagrid('getSelected');
	if (selectData == null) {
		$.messager.alert('提示', '必须选择一条记录进行操作', 'warning');
		return;
	}
	
	// 判断当前日期是否锁定
	var str_data = selectData.reportdate;
	var data = JSPFree.getHashVOs("select status from fsrs_lock_data where data_dt = '"+str_data+"'");
	if (data != null && data.length > 0) {
		if ("锁定" == data[0].status) {
			JSPFree.alert('当前数据已锁定，请先对'+str_data+'数据日期进行解锁操作');
			return;
		}
	}
	
	var def = {tablename_en:selectData.tablename_en,
			tablename:selectData.tablename,pkcolname:selectData.pkcolname,pkvalue:selectData.pkvalue};
	if ("待处理" == selectData.result_status) {
		JSPFree.openDialog(selectData.tablename,"/yujs/fsrs/ValidateResultHandleData.js", 900, 560, def,function(_rtdata){
			if (_rtdata.type == "dirclose" || _rtdata == false) {
				return;
			}
			
			if (_rtdata == "OK") {
				JSPFree.alert("保存成功!<br>同时修改了对应的校验结果状态为【完成】!");
				JSPFree.queryDataByConditon2(d1_BillList,"reportdate ='"+str_data_dt+"' and "+getCondition());  //立即查询刷新数据
			}
		});
	} else {
		$.messager.alert('提示','该数据已完成，请选择其他数据进行处理!','warning');
	}
}
