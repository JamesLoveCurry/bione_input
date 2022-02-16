//处理..
var str_data_dt = "";
var str_LoginUserCode = window.self.str_LoginUserCode;
function AfterInit(){
	JSPFree.createBillList("d1","/biapp-cams/freexml/cams/revise/cams_mistake_revise_CODE1.xml",null,{isSwitchQuery:"N"});

	// 获取最新一期的日期
	var jso_data = JSPFree.getHashVOs("SELECT max(reportdate) data_dt FROM cams_result_data");
	if(jso_data != null && jso_data.length > 0){
		str_data_dt = jso_data[0].data_dt;
	}
	FreeUtil.loadBillQueryData(d1_BillList, {reportdate:str_data_dt});
	d1_BillList.pagerType="2"; //第二种分页类型
	JSPFree.setBillListForceSQLWhere(d1_BillList,getQueryCondition()); //设置查询面板查询辖内机构
	JSPFree.queryDataBySQL(d1_BillList, getSql());
}

//机构控制：加载默认查询条件
function getQueryCondition(){
	var jso_rt = JSPFree.doClassMethodCall("com.yusys.cams.validate.service.ValidateQueryCondition","getQueryCondition",{"_loginUserOrgNo" : str_LoginUserOrgNo});
	var condition = "";
	if(jso_rt.msg == "ok"){
		condition = jso_rt.condition;
	}

	return condition;
}

//获取sql
function getSql(){
	var condition = getQueryCondition(); //获取机构权限，总行返回""，分行返回org_no in (...)
	var sql = "select * from cams_result_data where reportdate ='"+str_data_dt+"' and result_status ='待处理' "
	if(condition!=""){
		sql = sql + " and " + condition ;
	}
	sql = sql + " order by tablename_en";
	return sql;
}

//单一处理
function onHandle_1_1(){
	var selectData = d1_BillList.datagrid('getSelected');
	if (selectData == null) {
		$.messager.alert('提示', '必须选择一条记录进行操作', 'warning');
		return;
	}
	
	var def = {tablename_en:selectData.tablename_en,
			tablename:selectData.tablename,pkcolname:selectData.pkcolname,pkvalue:selectData.pkvalue};
	if ("待处理" == selectData.result_status) {
		JSPFree.openDialog(selectData.tablename,"/yujs/cams/revise/mistakeReviseHandleData.js", 900, 560, def,function(_rtdata){
			if (_rtdata.type == "dirclose" || _rtdata == false) {
				return;
			}
			
			if (_rtdata == "OK") {
				JSPFree.alert("保存成功!<br>同时修改了对应的校验结果状态为【完成】!");
				JSPFree.queryDataBySQL(d1_BillList, getSql());  //立即查询刷新数据
			}
		});
	} else {
		$.messager.alert('提示','该数据已完成，请选择其他数据进行处理!','warning');
	}
}
