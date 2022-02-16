//数据校验
var maskUtil = "";
function AfterInit(){
	maskUtil = FreeUtil.getMaskUtil();
	
	var jsy_btns = ["校验/onValidate/icon-p52","校验日志/onTaskLog/icon-p48","导出CSV/onExportAllCSV/icon-redo","导出EXCEL/onExportAllExcel/icon-redo"];
	if("Y"==self["v_isPOC"]){
		jsy_btns.push("POC假设校验/onCreatePOCDemoData");
	}

	JSPFree.createTabbByBtn("d1",["确定性校验","一致性校验","提示性校验"],jsy_btns,false);

	JSPFree.createBillList("d1_1","/biapp-crrs/freexml/crrs/rule/crrs_result_sure_CODE1.xml",null,{list_btns:"$VIEW;[icon-p68]导出选中/onExportBySelect1;[icon-p09]查看规则/viewRule1",list_ischeckstyle:"Y",list_ismultisel:"Y",isSwitchQuery:"N"});  //确定性校验
	JSPFree.createBillList("d1_2","/biapp-crrs/freexml/crrs/rule/crrs_result_consistency_CODE1.xml",null,{list_btns:"$VIEW;[icon-p68]导出选中/onExportBySelect2;[icon-p09]查看规则/viewRule2",list_ischeckstyle:"Y",list_ismultisel:"Y",isSwitchQuery:"N"});  //一致性
	JSPFree.createBillList("d1_3","/biapp-crrs/freexml/crrs/rule/crrs_result_warn_CODE1.xml",null,{list_btns:"$VIEW;[icon-p68]导出选中/onExportBySelect3;[icon-p09]查看规则/viewRule3",list_ischeckstyle:"Y",list_ismultisel:"Y",isSwitchQuery:"N"});  //提示性
}

function onValidate(){
	// 校验之前，先做判断
	var task_id = "88888888-8888-8888-8888-888888888888";
	var jsy_data = JSPFree.getHashVOs("select status from crrs_engine_task where task_id ='" + task_id + "'");

	if (jsy_data !=null && jsy_data.length > 0) {
		var status = jsy_data[0].status;
		if ("进行中" == status) {
			$.messager.alert('提示', '当前任务正在进行校验，请勿重复操作！', 'warning');
			return;
		}
	} else {
		$.messager.alert('提示', '当前任务不存在！', 'warning');
		return;
	}
	
	JSPFree.openDialog("选择日期","/yujs/crrs/datahandle/supervise_date.js",350,350,null,function(_rtdata){
		if (_rtdata != null && _rtdata.code == 1) {
			$.messager.show({title:'消息提示',msg: '后台校验中，请稍后...',showType:'show'});
			JSPFree.queryDataByConditon(d1_1_BillList,null);  //立即查询刷新数据
		}
	});
}

/**
 * 导出全部校验结果CSV
 * @param condition
 * @returns
 */
function onExportAllCSV(condition){
	var str_status = "";
	var jso_data = JSPFree.getHashVOs("select status from crrs_result_job where rid='77777777-7777-7777-7777-777777777777'");
	if(jso_data != null && jso_data.length > 0){
		str_status = jso_data[0].status;
	}
	
	//对导出并压缩下载校验结果的状态进行判断，若在进行中则不允许重复操作，即单线程
	if("进行中"==str_status){
		$.messager.alert('提示', '当前下载任务正在进行中，请勿重复操作！', 'warning');
		return;
	}
	
	var jso_status = JSPFree.doClassMethodCall("com.yusys.crrs.rulevalidate.service.CrrsResultBSDMO","checkZipAndDownloadCSVFile",{type:'EXCEL'});
	if(jso_status.code=="1"){
		JSPFree.confirm('提示', '导出校验结果为csv并压缩是一个耗时操作。请确定是否继续！', function(_isOK){
			if (_isOK){
				maskUtil.mask();
				var d1_sql_where = d1_1_BillList.CurrSQL.split("where")[1];
				var d2_sql_where = d1_2_BillList.CurrSQL.split("where")[1];
				var d3_sql_where = d1_3_BillList.CurrSQL.split("where")[1];
				
				var customercode1 = JSPFree.getBillQueryFormValue(d1_1_BillQuery).customercode;
				var customercode2 = JSPFree.getBillQueryFormValue(d1_2_BillQuery).customercode;
				var customercode3 = JSPFree.getBillQueryFormValue(d1_3_BillQuery).customercode;
				var customername1 = JSPFree.getBillQueryFormValue(d1_1_BillQuery).customername;
				var customername2 = JSPFree.getBillQueryFormValue(d1_2_BillQuery).customername;
				var customername3 = JSPFree.getBillQueryFormValue(d1_3_BillQuery).customername;
				var instCode1 = JSPFree.getBillQueryFormValue(d1_1_BillQuery).inst_code;
				var instCode2 = JSPFree.getBillQueryFormValue(d1_2_BillQuery).inst_code;
				var instCode3 = JSPFree.getBillQueryFormValue(d1_3_BillQuery).inst_code;

				//传参把3个where条件传过去
				var src = v_context + "/crrs/validateresult/exportCSV?" +
						"&customercode1="+ customercode1 + "&customername1=" + customername1 + "&instCode1="+ instCode1+ 
						"&customercode2="+ customercode2 + "&customername2=" + customername2 + "&instCode2="+ instCode2+ 
						"&customercode3="+ customercode3 + "&customername3=" + customername3 + "&instCode3="+ instCode3+ "&type=EXCEL";
				
				var download=null;
				download = $('<iframe id="download" style="display: none;"/>');
				$('body').append(download);
				
				download.attr('src', src);
				maskUtil.unmask();
			}
		});
	} else{
		JSPFree.alert(jso_status.msg);
		return;
	}
}

/**
 * 导出全部校验结果EXCEL
 * @param condition
 * @returns
 */
function onExportAllExcel(condition){
	var str_status = "";
	var jso_data = JSPFree.getHashVOs("select status from crrs_result_job where rid='77777777-7777-7777-7777-777777777777'");
	if(jso_data != null && jso_data.length > 0){
		str_status = jso_data[0].status;
	}
	
	//对导出并压缩下载校验结果的状态进行判断，若在进行中则不允许重复操作，即单线程
	if("进行中"==str_status){
		$.messager.alert('提示', '当前下载任务正在进行中，请勿重复操作！', 'warning');
		return;
	}
	
	// 如果超过50w数据,页面提示语
    var sql1 = d1_1_BillList.CurrSQL3;
	var new_sql1 = 'select count(*) c from ('+sql1+') t';
	var jso_data1 = JSPFree.getHashVOs(new_sql1);
	var c1 = jso_data1[0].c;
	var jso_rt = JSPFree.doClassMethodCall("com.yusys.crrs.batchExport.service.CrrsDownloadBS", "getDownloadNum",{});
	if (parseInt(c1) > parseInt(jso_rt.downloadNum)) {
		JSPFree.alert("【确定性校验】导出数量超过" + parseInt(jso_rt.downloadNum)
				 + "条！将导出" + parseInt(jso_rt.downloadNum) + "条数据！");
	}
	
	 var sql2 = d1_2_BillList.CurrSQL3;
	 var new_sql2 = 'select count(*) c from ('+sql2+') t';
	 var jso_data2 = JSPFree.getHashVOs(new_sql2);
	 var c2 = jso_data2[0].c;
	 if (parseInt(c2) > parseInt(jso_rt.downloadNum)) {
		 JSPFree.alert("【一致性校验】导出数量超过" + parseInt(jso_rt.downloadNum)
				 + "条！将导出" + parseInt(jso_rt.downloadNum) + "条数据！");
	 }
	
	 var sql3 = d1_3_BillList.CurrSQL3;
	 var new_sql3 = 'select count(*) c from ('+sql3+') t';
	 var jso_data3 = JSPFree.getHashVOs(new_sql3);
	 var c3 = jso_data3[0].c;
	 if (parseInt(c3) > parseInt(jso_rt.downloadNum)) {
		 JSPFree.alert("【提示性校验】导出数量超过" + parseInt(jso_rt.downloadNum) 
				 + "条！将导出" + parseInt(jso_rt.downloadNum) + "条数据！");
	 }
	
	var jso_status = JSPFree.doClassMethodCall("com.yusys.crrs.rulevalidate.service.CrrsResultBSDMO","checkZipAndDownloadCSVFile",{type:'EXCEL'});
	if(jso_status.code=="1"){
		JSPFree.confirm('提示', '导出校验结果为excel并压缩是一个耗时操作。请确定是否继续！', function(_isOK){
			if (_isOK){
				maskUtil.mask();
				var d1_sql_where = d1_1_BillList.CurrSQL.split("where")[1];
				var d2_sql_where = d1_2_BillList.CurrSQL.split("where")[1];
				var d3_sql_where = d1_3_BillList.CurrSQL.split("where")[1];
				
				var customercode1 = JSPFree.getBillQueryFormValue(d1_1_BillQuery).customercode;
				var customercode2 = JSPFree.getBillQueryFormValue(d1_2_BillQuery).customercode;
				var customercode3 = JSPFree.getBillQueryFormValue(d1_3_BillQuery).customercode;
				var customername1 = JSPFree.getBillQueryFormValue(d1_1_BillQuery).customername;
				var customername2 = JSPFree.getBillQueryFormValue(d1_2_BillQuery).customername;
				var customername3 = JSPFree.getBillQueryFormValue(d1_3_BillQuery).customername;
				var instCode1 = JSPFree.getBillQueryFormValue(d1_1_BillQuery).inst_code;
				var instCode2 = JSPFree.getBillQueryFormValue(d1_2_BillQuery).inst_code;
				var instCode3 = JSPFree.getBillQueryFormValue(d1_3_BillQuery).inst_code;

				//传参把3个where条件传过去
				var src = v_context + "/crrs/validateresult/exportCSV?" +
						"&customercode1="+ customercode1 + "&customername1=" + customername1 + "&instCode1="+ instCode1+ 
						"&customercode2="+ customercode2 + "&customername2=" + customername2 + "&instCode2="+ instCode2+ 
						"&customercode3="+ customercode3 + "&customername3=" + customername3 + "&instCode3="+ instCode3+ "&type=EXCEL";
				
				var download=null;
				download = $('<iframe id="download" style="display: none;"/>');
				$('body').append(download);
				
				download.attr('src', src);
				maskUtil.unmask();
			}
		});
	} else{
		JSPFree.alert(jso_status.msg);
		return;
	}
}

//勾选导出确定性校验信息
function onExportBySelect1(){
	var jsy_datas = JSPFree.getBillListSelectDatas(d1_1_BillList);
	var d1_sql = "select ruletype, customercode, customername, flag1, flag2, flag3, tablename, colname, colvalue, problemcode, problemmsg, relbankcode, relbankname, inst_code, inst_name, staff_code, staff_name , result_status from crrs_result_sure where 1=1 and rid in (";
	if (jsy_datas==null || jsy_datas.length<=0) {
		$.messager.alert('提示', '必须选择一条记录进行操作', 'warning');
		return;
	} else{
		for(var i=0;i<jsy_datas.length;i++){
			if(i==jsy_datas.length-1){
				d1_sql += "'"+jsy_datas[i]["rid"]+"')";
			} else{
				d1_sql += "'"+jsy_datas[i]["rid"]+"',";
			}
		}
	}
	JSPFree.downloadExcelBySQL("确定性数据校验信息.xls", d1_sql, "确定性校验","校验类型,客户代码,客户名称,识别码1,识别码2,识别码3,表名,字段名,字段值,校验关系编码,问题提示,银行相关机构代码,银行相关机构名称,经办机构号,经办机构名称,客户经理柜员号,客户经理名称,错误处理状态");
}

//勾选导出一致性校验信息
function onExportBySelect2(){
	var jsy_datas = JSPFree.getBillListSelectDatas(d1_2_BillList);
	var d2_sql = "select ruletype, customercode, customername, flag1, flag2, flag3, problemcode, textconfirmtype, tablename, colname, colvalue, same_value, problemmsg, lastdt_value, relbankcode, relbankname, inst_code, inst_name, staff_code, staff_name , result_status  from crrs_result_consistency where 1=1 and rid in (";
	if (jsy_datas==null || jsy_datas.length<=0) {
		$.messager.alert('提示', '必须选择一条记录进行操作', 'warning');
		return;
	} else{
		for(var i=0;i<jsy_datas.length;i++){
			if(i==jsy_datas.length-1){
				d2_sql += "'"+jsy_datas[i]["rid"]+"')";
			} else{
				d2_sql += "'"+jsy_datas[i]["rid"]+"',";
			}
		}
	}
	JSPFree.downloadExcelBySQL("一致性数据校验信息.xls", d2_sql, "一致性校验","校验类型,客户代码,客户名称,识别码1,识别码2,识别码3,校验关系编码,信息核实类型,表名,需核实字段,需核实字段值,相同信息值,参考提示,上期字段值,银行相关机构代码,银行相关机构名称,经办机构号,经办机构名称,客户经理柜员号,客户经理名称,错误处理状态");
}

//勾选导出提示性校验信息
function onExportBySelect3(){
	var jsy_datas = JSPFree.getBillListSelectDatas(d1_3_BillList);
	var d3_sql = "select remark, varify_num, ruletype, customercode, customername, flag1, flag2, flag3, problemcode, tablename, colname, colvalue, problemmsg, isremark, bank_remark, submit_bankcode, submit_bankname, submit_adminbankcode, check_bankcode, check_bankname, check_adminbankcode, isdoubtdata, cbrc_remark, bank_checkresult, bank_datamodify, data_changeexplain, cbrc_suggestion, inst_code, inst_name, staff_code, staff_name, result_status from crrs_result_warn where 1=1 and rid in (";
	if (jsy_datas==null || jsy_datas.length<=0) {
		$.messager.alert('提示', '必须选择一条记录进行操作', 'warning');
		return;
	} else{
		for(var i=0;i<jsy_datas.length;i++){
			if(i==jsy_datas.length-1){
				d3_sql += "'"+jsy_datas[i]["rid"]+"')";
			} else{
				d3_sql += "'"+jsy_datas[i]["rid"]+"',";
			}
		}
	}
	JSPFree.downloadExcelBySQL("提示性数据校验信息.xls", d3_sql, "提示性校验","备注情况,校验次数,校验类型,客户代码,客户名称,识别码1,识别码2,识别码3,校验关系编码,表名,字段名,字段值,问题提示,是否需要银行填写备注,银行机构填写备注,报送机构代码,报送机构名称,报送机构管辖机构代码,核实举证银行机构代码,核实举证银行机构名称,核实举证银行机构的监管机构代码,是否为监管机构质疑数据,监管机构备注,银行核实结果,银行将本批次数据修改为,银行异常变动说明,监管机构审核意见,经办机构号,经办机构名称,客户经理柜员号,客户经理名称,错误处理状态");
}

/**
 * 查看确定性校验规则
 * @return {[type]} [description]
 */
function viewRule1(){
	var jso_par1 = {list_btns:"",isSwitchQuery:"N",autoquery:"Y",ishavebillquery:"Y",list_ispagebar:"Y",autocondition:"ruletype='确定性校验' and isopen='Y'",querycontion:"ruletype='确定性校验' and isopen='Y'"};
	var jso_par ={jso_par1:jso_par1};
	JSPFree.openDialog2("确定性校验规则","/yujs/crrs/collection/ViewRule.js",960,600,jso_par);
}

/**
 * 查看一致性校验规则
 * @return {[type]} [description]
 */
function viewRule2(){
	var jso_par2 = {list_btns:"",isSwitchQuery:"N",autoquery:"Y",ishavebillquery:"Y",list_ispagebar:"Y",autocondition:"ruletype='一致性校验' and isopen='Y'",querycontion:"ruletype='一致性校验' and isopen='Y'"};
	var jso_par ={jso_par2:jso_par2};
	JSPFree.openDialog2("一致性校验规则","/yujs/crrs/collection/ViewRule.js",960,600,jso_par);
}

/**
 * 查看提示性校验规则
 * @return {[type]} [description]
 */
function viewRule3(){
	var jso_par3 = {list_btns:"",isSwitchQuery:"N",autoquery:"Y",ishavebillquery:"Y",list_ispagebar:"Y",autocondition:"ruletype='提示性校验' and isopen='Y'",querycontion:"ruletype='提示性校验' and isopen='Y'"};
	var jso_par ={jso_par3:jso_par3};
	JSPFree.openDialog2("提示性校验规则","/yujs/crrs/collection/ViewRule.js",960,600,jso_par);
}

//POC假设校验
function onCreatePOCDemoData(){
	JSPFree.confirm("提示","你真的要创建演示数据么?<br>这可能需要半分钟左右的时间!",function(_isOK){
		if(_isOK){
			JSPFree.doClassMethodCall("com.yusys.crrs.datahandle.service.CreateDemoErrorDataDMO", "createDemoData", null);  //
			JSPFree.alert("创建POC假设校验数据成功!");  //
		}
	});
}

function onTaskLog(){
	JSPFree.openDialog("日志", "/yujs/crrs/engine/viewtasklog.js", 900, 600,
			null, function(_rtdata) {

	});
}