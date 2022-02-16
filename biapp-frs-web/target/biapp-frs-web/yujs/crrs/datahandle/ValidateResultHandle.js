//处理..
var str_LoginUserCode = window.self.str_LoginUserCode;
var st_date = "";
var is_back = "";
function AfterInit(){
	JSPFree.createTabb("d1",["确定性校验","一致性校验","提示性校验"]);
	
	// 判断database中，是否需要进行下发操作
	// 如果需要下发操作，则执行如下代码
	var jso_data = JSPFree.doClassMethodCall("com.yusys.crrs.datahandle.service.ValidateResultHandleBS","isDistribute",{});
	if ("Y" == jso_data.result) {
		st_date = "已分发";
		is_back = "Y";
		
		JSPFree.createBillList("d1_1","/biapp-crrs/freexml/crrs/rule/crrs_result_sure_Submit_CODE1.xml",null,{list_btns:"[icon-p41]关联处理/onHandle_1;[icon-p41]单一处理/onHandle_1_1;$VIEW;[icon-back]退回/onBack_1",list_ischeckstyle:"N",list_ismultisel:"N",ishavebillquery:"Y",isSwitchQuery:"N"});  //确定性校验  [icon-remove]清空所有结果/dropAllData;
		JSPFree.createBillList("d1_2","/biapp-crrs/freexml/crrs/rule/crrs_result_consistency_Submit_CODE1.xml",null,{list_btns:"[icon-p41]关联处理/onHandle_2;[icon-p41]单一处理/onHandle_2_1;$VIEW;[icon-back]退回/onBack_2",list_ischeckstyle:"N",list_ismultisel:"N",ishavebillquery:"Y",isSwitchQuery:"N"});  //一致性校验
		JSPFree.createBillList("d1_3","/biapp-crrs/freexml/crrs/rule/crrs_result_warn_Submit_CODE1.xml",null,{list_btns:"[icon-p41]关联处理/onHandle_3;[icon-p41]单一处理/onHandle_3_1;$VIEW;[icon-back]退回/onBack_3",list_ischeckstyle:"N",list_ismultisel:"N",ishavebillquery:"Y",isSwitchQuery:"N"});  //提示性校验
	} else {
		st_date = "未发布";
		is_back = "N";
		
		JSPFree.createBillList("d1_1","/biapp-crrs/freexml/crrs/rule/crrs_result_sure_Submit_CODE1.xml",null,{list_btns:"[icon-p41]关联处理/onHandle_1;[icon-p41]单一处理/onHandle_1_1;$VIEW;",list_ischeckstyle:"N",list_ismultisel:"N",ishavebillquery:"Y",isSwitchQuery:"N"});  //确定性校验  [icon-remove]清空所有结果/dropAllData;
		JSPFree.createBillList("d1_2","/biapp-crrs/freexml/crrs/rule/crrs_result_consistency_Submit_CODE1.xml",null,{list_btns:"[icon-p41]关联处理/onHandle_2;[icon-p41]单一处理/onHandle_2_1;$VIEW;",list_ischeckstyle:"N",list_ismultisel:"N",ishavebillquery:"Y",isSwitchQuery:"N"});  //一致性校验
		JSPFree.createBillList("d1_3","/biapp-crrs/freexml/crrs/rule/crrs_result_warn_Submit_CODE1.xml",null,{list_btns:"[icon-p41]关联处理/onHandle_3;[icon-p41]单一处理/onHandle_3_1;$VIEW;",list_ischeckstyle:"N",list_ismultisel:"N",ishavebillquery:"Y",isSwitchQuery:"N"});  //提示性校验
	}
	
	JSPFree.queryDataByConditon2(d1_1_BillList, getQueryCondition());
	JSPFree.queryDataByConditon2(d1_2_BillList, getQueryCondition());
	JSPFree.queryDataByConditon2(d1_3_BillList, getQueryCondition());
	
	//灵活查询加载初始化查询条件
	JSPFree.setBillListForceSQLWhere(d1_1_BillList,getQueryCondition());
	JSPFree.setBillListForceSQLWhere(d1_2_BillList,getQueryCondition());
	JSPFree.setBillListForceSQLWhere(d1_3_BillList,getQueryCondition());
}

//查询列表加载默认查询条件，用于灵活查询
function getQueryCondition(){
	var jso_rt = JSPFree.doClassMethodCall("com.yusys.crrs.rulevalidate.service.ValidateQueryCondition3","getQueryCondition",{"_loginUserCode" : str_LoginUserCode});
	var condition = "";
	if(jso_rt.msg == "ok"){
		condition = jso_rt.condition;
	}
	return condition;
}

//确定性校验-处理
function onHandle_1(){
	var jso_rowData = d1_1_BillList.datagrid('getSelected');
	if (jso_rowData == null) {
		JSPFree.alert('必须选择一条记录进行操作');
		return;
	}
	
	// 判断当前日期是否锁定
	var report_date = jso_rowData.reportdate;
	var str_data = report_date.replace(/-/g, '');
	var data = JSPFree.getHashVOs("select status from crrs_lock_data where data_dt = '"+str_data+"'");
	if (data != null && data.length > 0) {
		if ("锁定" == data[0].status) {
			JSPFree.alert('当前数据已锁定，请先对'+str_data+'数据日期进行解锁操作');
			return;
		}
	}
	
	//表名
	var str_rid = jso_rowData["rid"];  //rid
	var str_tablename_en = jso_rowData["tablename_en"];  //表名
	var str_customercode = jso_rowData["customercode"];  //客户编码
	var str_reportdate = jso_rowData["reportdate"];  //日期
	var str_pkvalue = jso_rowData["pkvalue"];  //记录主键

	//先远程调用,根据表名,
	var jso_getRootTablePar = {"rid":str_rid,"tablename_en":str_tablename_en,"customercode":str_customercode,"reportdate":str_reportdate,"pkvalue":str_pkvalue,result_tab:"crrs_result_sure",str_LoginUserCode : str_LoginUserCode};  //计算所属根主表的参数
	var jso_rootInfo = JSPFree.doClassMethodCall("com.yusys.crrs.datahandle.service.GetRootTableByErrorRecordDMO","getRootTableName",jso_getRootTablePar);
	
	console.log(jso_rootInfo);

	var str_rootTableName = jso_rootInfo["roottable"];
	var str_rootcustomercode = jso_rootInfo["rootcustomercode"];
	var str_rootreportdate = jso_rootInfo["rootreportdate"];
	str_rootreportdate = str_rootreportdate.replace(/-/g, '');

	//如果没有计算出主表与记录,则弹出提示并返回
	if(str_rootTableName=="" || str_rootcustomercode=="" || str_rootreportdate==""){
		JSPFree.alert("根据当前记录表名[" + str_tablename_en + "]与主键值计算主表与客户编码，数据日期时有一值为空[" + str_rootTableName + "][" + str_rootcustomercode + "][" + str_rootreportdate + "]"); //
		return;
	}

	//弹出不同的修改数据窗口
	var jso_par = {tab:"crrs_result_sure","customercode":str_rootcustomercode,"reportdate":str_rootreportdate,"ErrorCount":jso_rootInfo.ErrorCount,type:"1",is_back:is_back};  //
	if(str_rootTableName.toLowerCase() =="crrs_single_corporation".toLowerCase()){
		JSPFree.openDialog2("修改数据-单一法人客户","/yujs/crrs/datahandle/HandleErrorDataForSingle.js",1100,600,jso_par,function(_rtdata){JSPFree.refreshBillListCurrPage(d1_1_BillList);},true);
	}else if(str_rootTableName.toLowerCase()=="crrs_group_group_client_1".toLowerCase()){
		JSPFree.openDialog2("修改数据-集团客户","/yujs/crrs/datahandle/HandleErrorDataForGroup.js",950,600,jso_par,function(_rtdata){JSPFree.refreshBillListCurrPage(d1_1_BillList);},true);
	}else if(str_rootTableName.toLowerCase()=="crrs_group_group_client_2".toLowerCase()){
		JSPFree.openDialog2("修改数据-供应链客户","/yujs/crrs/datahandle/HandleErrorDataForSupplychain.js",950,600,jso_par,function(_rtdata){JSPFree.refreshBillListCurrPage(d1_1_BillList);},true);
	}else if(str_rootTableName.toLowerCase()=="crrs_ent_trade_info".toLowerCase()){
		JSPFree.openDialog2("修改数据-同业客户","/yujs/crrs/datahandle/HandleErrorDataForSametrade.js",950,600,jso_par,function(_rtdata){JSPFree.refreshBillListCurrPage(d1_1_BillList);},true);
	}else if(str_rootTableName.toLowerCase()=="crrs_person_personal".toLowerCase()){
		JSPFree.openDialog2("修改数据--个人客户","/yujs/crrs/datahandle/HandleErrorDataForPerson.js",950,600,jso_par,function(_rtdata){JSPFree.refreshBillListCurrPage(d1_1_BillList);},true);
	}else{
		JSPFree.alert("根据当前结果的表名[" + str_tablename_en + "]计算出所属数据表名[" + str_rootTableName + "]不合法!");
	}
}

//一致性校验-处理
function onHandle_2(){
	var jso_rowData = d1_2_BillList.datagrid('getSelected');
	if (jso_rowData == null) {
		JSPFree.alert('必须选择一条记录进行操作');
		return;
	}
	
	// 判断当前日期是否锁定
	var report_date = jso_rowData.reportdate;
	var str_data = report_date.replace(/-/g, '');
	var data = JSPFree.getHashVOs("select status from crrs_lock_data where data_dt = '"+str_data+"'");
	if (data != null && data.length > 0) {
		if ("锁定" == data[0].status) {
			JSPFree.alert('当前数据已锁定，请先对'+str_data+'数据日期进行解锁操作');
			return;
		}
	}

	//表名
	var str_rid = jso_rowData["rid"];  //rid
	var str_tablename_en = jso_rowData["tablename_en"];  //表名
	var str_customercode = jso_rowData["customercode"];  //客户编码
	var str_reportdate = jso_rowData["reportdate"];  //日期
	var str_pkvalue = jso_rowData["pkvalue"];  //记录主键

	//先远程调用,根据表名,
	var jso_getRootTablePar = {"rid":str_rid,"tablename_en":str_tablename_en,"customercode":str_customercode,"reportdate":str_reportdate,"pkvalue":str_pkvalue,result_tab:"crrs_result_consistency",str_LoginUserCode : str_LoginUserCode};  //计算所属根主表的参数
	var jso_rootInfo = JSPFree.doClassMethodCall("com.yusys.crrs.datahandle.service.GetRootTableByErrorRecordDMO","getRootTableName",jso_getRootTablePar);
	
	console.log(jso_rootInfo);

	var str_rootTableName = jso_rootInfo["roottable"];
	var str_rootcustomercode = jso_rootInfo["rootcustomercode"];
	var str_rootreportdate = jso_rootInfo["rootreportdate"];
	str_rootreportdate = str_rootreportdate.replace(/-/g, '');

	//如果没有计算出主表与记录,则弹出提示并返回
	if(str_rootTableName=="" || str_rootcustomercode=="" || str_rootreportdate==""){
		JSPFree.alert("根据当前记录表名[" + str_tablename_en + "]与主键值计算主表与客户编码，数据日期时有一值为空[" + str_rootTableName + "][" + str_rootcustomercode + "][" + str_rootreportdate + "]"); //
		return;
	}

	//弹出不同的修改数据窗口
	var jso_par = {tab:"crrs_result_sure","customercode":str_rootcustomercode,"ErrorCount":jso_rootInfo.ErrorCount,type:"2"};  //
	if(str_rootTableName.toLowerCase() =="crrs_single_corporation".toLowerCase()){
		JSPFree.openDialog2("修改数据-单一法人客户","/yujs/crrs/datahandle/HandleErrorDataForSingle.js",1100,600,jso_par,function(_rtdata){JSPFree.refreshBillListCurrPage(d1_1_BillList);},true);
	}else if(str_rootTableName.toLowerCase()=="crrs_group_group_client_1".toLowerCase()){
		JSPFree.openDialog2("修改数据-集团客户","/yujs/crrs/datahandle/HandleErrorDataForGroup.js",950,600,jso_par,function(_rtdata){JSPFree.refreshBillListCurrPage(d1_1_BillList);},true);
	}else if(str_rootTableName.toLowerCase()=="crrs_group_group_client_2".toLowerCase()){
		JSPFree.openDialog2("修改数据-供应链客户","/yujs/crrs/datahandle/HandleErrorDataForSupplychain.js",950,600,jso_par,function(_rtdata){JSPFree.refreshBillListCurrPage(d1_1_BillList);},true);
	}else if(str_rootTableName.toLowerCase()=="crrs_ent_trade_info".toLowerCase()){
		JSPFree.openDialog2("修改数据-同业客户","/yujs/crrs/datahandle/HandleErrorDataForSametrade.js",950,600,jso_par,function(_rtdata){JSPFree.refreshBillListCurrPage(d1_1_BillList);},true);
	}else if(str_rootTableName.toLowerCase()=="crrs_person_personal".toLowerCase()){
		JSPFree.openDialog2("修改数据--个人客户","/yujs/crrs/datahandle/HandleErrorDataForPerson.js",950,600,jso_par,function(_rtdata){JSPFree.refreshBillListCurrPage(d1_1_BillList);},true);
	}else{
		JSPFree.alert("根据当前结果的表名[" + str_tablename_en + "]计算出所属数据表名[" + str_rootTableName + "]不合法!");
	}
}

//提示性校验-处理
function onHandle_3(){
	var jso_rowData = d1_3_BillList.datagrid('getSelected');
	if (jso_rowData == null) {
		JSPFree.alert('必须选择一条记录进行操作');
		return;
	}
	
	// 判断当前日期是否锁定
	var report_date = jso_rowData.reportdate;
	var str_data = report_date.replace(/-/g, '');
	var data = JSPFree.getHashVOs("select status from crrs_lock_data where data_dt = '"+str_data+"'");
	if (data != null && data.length > 0) {
		if ("锁定" == data[0].status) {
			JSPFree.alert('当前数据已锁定，请先对'+str_data+'数据日期进行解锁操作');
			return;
		}
	}
	
	//表名
	var str_rid = jso_rowData["rid"];  //rid
	var str_tablename_en = jso_rowData["tablename_en"];  //表名
	var str_customercode = jso_rowData["customercode"];  //客户编码
	var str_reportdate = jso_rowData["reportdate"];  //日期
	var str_pkvalue = jso_rowData["pkvalue"];  //记录主键

	//先远程调用,根据表名,
	var jso_getRootTablePar = {"rid":str_rid,"tablename_en":str_tablename_en,"customercode":str_customercode,"reportdate":str_reportdate,"pkvalue":str_pkvalue,result_tab:"crrs_result_warn",str_LoginUserCode : str_LoginUserCode};  //计算所属根主表的参数
	var jso_rootInfo = JSPFree.doClassMethodCall("com.yusys.crrs.datahandle.service.GetRootTableByErrorRecordDMO","getRootTableName",jso_getRootTablePar);
	
	console.log(jso_rootInfo);

	var str_rootTableName = jso_rootInfo["roottable"];
	var str_rootcustomercode = jso_rootInfo["rootcustomercode"];
	var str_rootreportdate = jso_rootInfo["rootreportdate"];
	str_rootreportdate = str_rootreportdate.replace(/-/g, '');

	//如果没有计算出主表与记录,则弹出提示并返回
	if(str_rootTableName=="" || str_rootcustomercode=="" || str_rootreportdate==""){
		JSPFree.alert("根据当前记录表名[" + str_tablename_en + "]与主键值计算主表与客户编码，数据日期时有一值为空[" + str_rootTableName + "][" + str_rootcustomercode + "][" + str_rootreportdate + "]"); //
		return;
	}

	//弹出不同的修改数据窗口
	var jso_par = {tab:"crrs_result_sure","customercode":str_rootcustomercode,"ErrorCount":jso_rootInfo.ErrorCount,type:"3"};  //
	if(str_rootTableName.toLowerCase() =="crrs_single_corporation".toLowerCase()){
		JSPFree.openDialog2("修改数据-单一法人客户","/yujs/crrs/datahandle/HandleErrorDataForSingle.js",1100,600,jso_par,function(_rtdata){JSPFree.refreshBillListCurrPage(d1_1_BillList);},true);
	}else if(str_rootTableName.toLowerCase()=="crrs_group_group_client_1".toLowerCase()){
		JSPFree.openDialog2("修改数据-集团客户","/yujs/crrs/datahandle/HandleErrorDataForGroup.js",950,600,jso_par,function(_rtdata){JSPFree.refreshBillListCurrPage(d1_1_BillList);},true);
	}else if(str_rootTableName.toLowerCase()=="crrs_group_group_client_2".toLowerCase()){
		JSPFree.openDialog2("修改数据-供应链客户","/yujs/crrs/datahandle/HandleErrorDataForSupplychain.js",950,600,jso_par,function(_rtdata){JSPFree.refreshBillListCurrPage(d1_1_BillList);},true);
	}else if(str_rootTableName.toLowerCase()=="crrs_ent_trade_info".toLowerCase()){
		JSPFree.openDialog2("修改数据-同业客户","/yujs/crrs/datahandle/HandleErrorDataForSametrade.js",950,600,jso_par,function(_rtdata){JSPFree.refreshBillListCurrPage(d1_1_BillList);},true);
	}else if(str_rootTableName.toLowerCase()=="crrs_person_personal".toLowerCase()){
		JSPFree.openDialog2("修改数据--个人客户","/yujs/crrs/datahandle/HandleErrorDataForPerson.js",950,600,jso_par,function(_rtdata){JSPFree.refreshBillListCurrPage(d1_1_BillList);},true);
	}else{
		JSPFree.alert("根据当前结果的表名[" + str_tablename_en + "]计算出所属数据表名[" + str_rootTableName + "]不合法!");
	}
}

//单一处理
function onHandle_1_1(){
	var selectData = d1_1_BillList.datagrid('getSelected');
	if (selectData == null) {
		$.messager.alert('提示', '必须选择一条记录进行操作', 'warning');
		return;
	}
	
	// 判断当前日期是否锁定
	var report_date = selectData.reportdate;
	var str_data = report_date.replace(/-/g, '');
	var data = JSPFree.getHashVOs("select status from crrs_lock_data where data_dt = '"+str_data+"'");
	if (data != null && data.length > 0) {
		if ("锁定" == data[0].status) {
			JSPFree.alert('当前数据已锁定，请先对'+str_data+'数据日期进行解锁操作');
			return;
		}
	}

	if (st_date == selectData.result_status) {
		JSPFree.openDialog(selectData.tablename,"/yujs/crrs/datahandle/ValidateResultHandleData.js", 900, 560, 
				{tab:"crrs_result_sure",tabname:selectData.tabname,tablename_en:selectData.tablename_en,pkvalue:selectData.pkvalue,type:"1",is_back:is_back},function(_rtdata){
					if (_rtdata.type == "dirclose" || _rtdata == false) {
						return;
					}
					
					if (_rtdata == "OK") {
						JSPFree.alert("保存成功!<br>同时修改了对应的校验结果状态为【完成】!");
						JSPFree.queryDataByConditon2(d1_1_BillList, null);
					}
					if (_rtdata.code != null && _rtdata.code != 'undefined' && _rtdata.code == '000') {
						JSPFree.alert("退回数据成功!");
						JSPFree.queryDataByConditon2(d1_1_BillList, null);
					}
				});
	} else {
		$.messager.alert('提示','该数据已处理，请选择其他数据进行处理!','warning');
	}
}

//单一处理
function onHandle_2_1(){
	var selectData = d1_2_BillList.datagrid('getSelected');
	if (selectData == null) {
		$.messager.alert('提示', '必须选择一条记录进行操作', 'warning');
		return;
	}
	
	// 判断当前日期是否锁定
	var report_date = selectData.reportdate;
	var str_data = report_date.replace(/-/g, '');
	var data = JSPFree.getHashVOs("select status from crrs_lock_data where data_dt = '"+str_data+"'");
	if (data != null && data.length > 0) {
		if ("锁定" == data[0].status) {
			JSPFree.alert('当前数据已锁定，请先对'+str_data+'数据日期进行解锁操作');
			return;
		}
	}
	
	if (st_date == selectData.result_status) {
		JSPFree.openDialog(selectData.tablename,"/yujs/crrs/datahandle/ValidateResultHandleData.js", 900, 560, 
				{tab:"crrs_result_consistency",tabname:selectData.tabname,tablename_en:selectData.tablename_en,pkvalue:selectData.pkvalue,type:"2",is_back:is_back},function(_rtdata){
					if (_rtdata.type == "dirclose" || _rtdata == false) {
						return;
					}
					
					if (_rtdata == "OK") {
						JSPFree.alert("保存成功!<br>同时修改了对应的校验结果状态为【完成】!");
						JSPFree.queryDataByConditon2(d1_2_BillList, null);
					}
					if (_rtdata.code != null && _rtdata.code != 'undefined' && _rtdata.code == '000') {
						JSPFree.alert("退回数据成功!");
						JSPFree.queryDataByConditon2(d1_2_BillList, null);
					}
				});
	} else {
		$.messager.alert('提示','该数据已处理，请选择其他数据进行处理!','warning');
	}
}

//单一处理
function onHandle_3_1(){
	var selectData = d1_3_BillList.datagrid('getSelected');
	if (selectData == null) {
		$.messager.alert('提示', '必须选择一条记录进行操作', 'warning');
		return;
	}
	
	// 判断当前日期是否锁定
	var report_date = selectData.reportdate;
	var str_data = report_date.replace(/-/g, '');
	var data = JSPFree.getHashVOs("select status from crrs_lock_data where data_dt = '"+str_data+"'");
	if (data != null && data.length > 0) {
		if ("锁定" == data[0].status) {
			JSPFree.alert('当前数据已锁定，请先对'+str_data+'数据日期进行解锁操作');
			return;
		}
	}

	if (st_date == selectData.result_status) {
		JSPFree.openDialog(selectData.tablename,"/yujs/crrs/datahandle/ValidateResultHandleData.js", 900, 560, 
				{tab:"crrs_result_warn",tabname:selectData.tabname,tablename_en:selectData.tablename_en,pkvalue:selectData.pkvalue,type:"3",is_back:is_back},function(_rtdata){
					if (_rtdata.type == "dirclose" || _rtdata == false) {
						return;
					}
					
					if (_rtdata == "OK") {
						JSPFree.alert("保存成功!<br>同时修改了对应的校验结果状态为【完成】!");
						JSPFree.queryDataByConditon2(d1_3_BillList, null);
					}
					if (_rtdata.code != null && _rtdata.code != 'undefined' && _rtdata.code == '000') {
						JSPFree.alert("退回数据成功!");
						JSPFree.queryDataByConditon2(d1_3_BillList, null);
					}
				});
	} else {
		$.messager.alert('提示','该数据已处理，请选择其他数据进行处理!','warning');
	}
}

function dropAllData(){
	JSPFree.doClassMethodCall("com.yusys.crrs.datahandle.service.CreateDemoErrorDataDMO","dropAllData",null);
	JSPFree.alert("清除所有确定性校验结果成功!");
}

//退回
function onBack_1(){
	var selectData = d1_1_BillList.datagrid('getSelected');
	if (selectData == null) {
		$.messager.alert('提示', '必须选择一条记录进行操作', 'warning');
		return;
	}else{
		JSPFree.confirm("提示","您确认要退回吗?",function(_isOK){
			if(_isOK){
				//弹出窗口,传入参数,然后接收返回值!
				JSPFree.openDialog("填写原因","/yujs/crrs/datahandle/BackReasonDialog.js",900,600,{templetcode:"/biapp-crrs/freexml/crrs/rule/crrs_result_sure_back.xml"},function(_rtdata){
					if(_rtdata != null && _rtdata != undefined && null != _rtdata.reason && undefined != _rtdata.reason){
						doUpdateBackStatus(d1_1_BillList,"crrs_result_sure",selectData.rid, _rtdata.reason);
					}
				});
			}
		});
	}	
}

function onBack_2(){
	var selectData = d1_2_BillList.datagrid('getSelected');
	if (selectData == null) {
		$.messager.alert('提示', '必须选择一条记录进行操作', 'warning');
		return;
	}else{
		JSPFree.confirm("提示","您确认要退回吗?",function(_isOK){
			if(_isOK){
				//弹出窗口,传入参数,然后接收返回值!
				JSPFree.openDialog("填写原因","/yujs/crrs/datahandle/BackReasonDialog.js",900,600,{templetcode:"/biapp-crrs/freexml/crrs/rule/crrs_result_consistency_back.xml"},function(_rtdata){
					if(_rtdata != null && _rtdata != undefined && null != _rtdata.reason && undefined != _rtdata.reason){
						doUpdateBackStatus(d1_2_BillList,"crrs_result_consistency",selectData.rid, _rtdata.reason);
					}
				});
			}
		});
	}	
}

function onBack_3(){
	var selectData = d1_3_BillList.datagrid('getSelected');
	if (selectData == null) {
		$.messager.alert('提示', '必须选择一条记录进行操作', 'warning');
		return;
	}else{
		JSPFree.confirm("提示","您确认要退回吗?",function(_isOK){
			if(_isOK){
				//弹出窗口,传入参数,然后接收返回值!
				JSPFree.openDialog("填写原因","/yujs/crrs/datahandle/BackReasonDialog.js",900,600,{templetcode:"/biapp-crrs/freexml/crrs/rule/crrs_result_warn_back.xml"},function(_rtdata){
					if(_rtdata != null && _rtdata != undefined && null != _rtdata.reason && undefined != _rtdata.reason){
						doUpdateBackStatus(d1_3_BillList,"crrs_result_warn",selectData.rid, _rtdata.reason);
					}
				});
			}
		});
	}	
}

//退回，将已分发修改为退回
function doUpdateBackStatus(billList,_tableName,_rid,_reason){
	var jso_par = {tablename:_tableName,rid:_rid,reason:_reason};
	var jso_rt = JSPFree.doClassMethodCall("com.yusys.crrs.rulevalidate.service.ValidateResultHandleBS","doResultBack",jso_par);
	
	JSPFree.queryDataByConditon2(billList, null);  //刷新当前行
	JSPFree.alert(jso_rt.msg);  //提示成功!
}