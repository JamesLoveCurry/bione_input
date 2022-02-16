//处理..
var str_data_dt = "";
var str_LoginUserCode = window.self.str_LoginUserCode;
function AfterInit(){
	JSPFree.createBillList("d1","/biapp-cams/freexml/cams/revise/cams_receipt_revise_CODE1.xml",null,{isSwitchQuery:"N"});

	// 获取最新一期的日期
	var jso_data = JSPFree.getHashVOs("SELECT max(reportdate) data_dt FROM cams_receipt_data ");
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
	var sql = "select * from cams_receipt_data where reportdate ='"+str_data_dt+"' "
	if(condition!=""){
		sql = sql + " and " + condition ;
	}
	sql = sql + " order by tablename_en";
	return sql;
}

//单一处理
function onHandle(){
	
	var selectData = d1_BillList.datagrid('getSelected');
	if (selectData == null) {
		$.messager.alert('提示', '必须选择一条记录进行操作', 'warning');
		return;
	}
	
	var def = {tablename_en:selectData.tablename_en,docrefid:selectData.docrefid};
	if ("待处理" == selectData.result_status) {
		
		if("CAMS_INDV_ACCT_M"==selectData.tablename_en || "CAMS_CORP_ACCT_M"==selectData.tablename_en){ //如果是两张主表直接单一处理
			JSPFree.openDialog(selectData.tablename,"/yujs/cams/revise/receiptReviseHandleData.js",950,600,def,function(_rtdata){
				if (_rtdata.type == "dirclose" || _rtdata == false) {
					return;
				}
				
				if (_rtdata == "OK") {
					JSPFree.alert("保存成功!<br>同时修改了对应的回执结果状态为【完成】!");
					JSPFree.queryDataBySQL(d1_BillList, getSql());  //立即查询刷新数据
				}
			});
			 //下面两种情况，先展示控制人列表，再编辑。1、这条回执属于机构，并且是个人姓名/地址/tin信息，2、这条回执的表名是机构控制人
		} else if ((("CAMS_INDV_NAME_M"==selectData.tablename_en || "CAMS_INDV_ADDR_M"==selectData.tablename_en ||
				"CAMS_INDV_TIN_M"==selectData.tablename_en) && "2"==selectData.acct_type) || 
				"CAMS_CORP_CTRL_M"==selectData.tablename_en){ 
			JSPFree.openDialog2("机构账户控制人信息","/yujs/cams/revise/viewCorpCtrlReceipt.js",950,600,def,function(_rtdata){
				JSPFree.queryDataBySQL(d1_BillList, getSql());  //立即查询刷新数据
			},true);
		}else{ //其余情况走这里，直接展示列表，并允许修改
			JSPFree.openDialog2(selectData.tablename,"/yujs/cams/revise/viewReceipt.js",950,600,def,function(_rtdata){
				JSPFree.queryDataBySQL(d1_BillList, getSql());  //立即查询刷新数据
			},true);
		}
		
	} else {
		$.messager.alert('提示','该数据已完成，请选择其他数据进行处理!','warning');
	}
}
