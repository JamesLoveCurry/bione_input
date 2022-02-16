//初始化界面,菜单配置路径是【/frs/yufreejs?js=/yujs/east/fillingProcess/distribute_fillin.js】
function AfterInit(){

	JSPFree.createBillList("d1","/biapp-east/freexml/east/fillingProcess/east_filling_process_child_CODE1.xml",null,{isSwitchQuery:"N",orderbys:"data_dt,tab_name_en"});
	
	JSPFree.queryDataByConditon2(d1_BillList, getQueryCondition());
	JSPFree.setBillListForceSQLWhere(d1_BillList,getQueryCondition());
}

function record() {
	var json_data = d1_BillList.datagrid('getSelections');
	if (json_data == null || json_data == undefined || json_data.length == 0) {
		$.messager.alert('提示', '必须选择一条记录进行操作', 'warning');
		return;
	}
	if (json_data.length>1) {
		$.messager.alert('提示', '只能选择一条记录进行操作', 'warning');
		return;
	}

	var jso_par = {data_dt:json_data[0].data_dt,tab_name:json_data[0].tab_name};
	JSPFree.openDialog("处理日志信息","/yujs/east/record/east_modified_record_main.js",900,600,jso_par,function(_rtData){

	});
}

//查询列表加载默认查询条件，用于灵活查询
function getQueryCondition(){
	var condition = "";
	var jso_rt = JSPFree.doClassMethodCall("com.yusys.east.process.service.EastCrProcessBSDMO",
			"getQueryConditionReportFillIn",{"_loginUserOrgNo" : str_LoginUserOrgNo});
	
	if(jso_rt.msg == "ok"){
		condition = jso_rt.condition;
	}
	var whereSql = '';
	var jsn_result = JSPFree.doClassMethodCall("com.yusys.east.common.service.EastGetReportBS", "getReportList", null);
	if (jsn_result.code == 'success') {
		whereSql = jsn_result.data;
	}
	condition += " and " + whereSql;
	return condition;
}

//填报
function created(){
	var json_data = d1_BillList.datagrid('getSelections');
	if (json_data == null || json_data == undefined || json_data.length == 0) {
		$.messager.alert('提示', '必须选择一条记录进行操作', 'warning');
		return;
	}
	if (json_data.length>1) {
		$.messager.alert('提示', '只能选择一条记录进行操作', 'warning');
		return;
	}
	var distributeType = json_data[0].distribute_type;
	var jso_Pars = {taskId:json_data[0].rid,tabNameEn:json_data[0].tab_name_en,tabName:json_data[0].tab_name,dataDt:json_data[0].data_dt, org_no: json_data[0].org_no, rpt_org_no: json_data[0].rpt_org_no, distributeType: distributeType};
	JSPFree.openDialog("填报","/yujs/east/fillingProcess/distribute_detail_fillin1.js",1200,800,jso_Pars,function(_rtData){
		if (_rtData != null) {
			if ("dirclose" == _rtData.type) {
				return;
			}
		}
		if (_rtData == "提交") {
			$.messager.alert('提示', '任务提交成功!', 'info');
		} else if (_rtData == "强制提交") {
			$.messager.alert('提示', '任务强制提交成功!', 'info');
		}

		JSPFree.queryDataByConditon(d1_BillList);
	});
}

// 查看处理日志
function processReasonDetail(){
	var json_data = d1_BillList.datagrid('getSelections');
	if (json_data == null || json_data == undefined || json_data.length == 0) {
		$.messager.alert('提示', '必须选择一条记录进行操作', 'warning');
		return;
	}
	if (json_data.length>1) {
		$.messager.alert('提示', '只能选择一条记录进行操作', 'warning');
		return;
	}

	var jso_par = {child_task_id:json_data[0].rid};
	JSPFree.openDialog("处理日志信息","/yujs/east/fillingProcess/distribute_process_detail.js",900,600,jso_par,function(_rtData){
		if (_rtData != null) {
			if ("dirclose" == _rtData.type) {
				return;
			}
		}
	});
}

//导出填报数据
function exportData(_btn) {
	var dataset = _btn.dataset;
	var index = dataset.rowindex;
	var rows = d1_BillList.datagrid("getRows");
	var row = rows[index];//index为行号
	if ("0" == row.no_handle) {
		JSPFree.alert("当前错误记录数为0，无法导出错误明细数据！");
		return;
	}
	JSPFree.confirm('提示', '导出数据量越大速度越慢,是否导出?', function (_isOK) {
		if (_isOK) {
			var src = v_context + "/east/distribute/data/exportData?tabName="
				+ row.tab_name + "&tabNameEn=" + row.tab_name_en + "&dataDt=" + row.data_dt + "&taskId=" + row.rid + "&orgNo=" + row.org_no;
			var download = null;
			download = $('<iframe id="download" style="display: none;"/>');
			$('body').append(download);
			download.attr('src', src);
		}
	})
	
}

/**
 * 导入
 * @return {[type]} [description]
 */
function importData() {
	JSPFree.openDialog("文件上传","/yujs/east/fillingProcess/east_distribute_fillin_import.js", 500, 240, null,function(_rtdata){
		if (_rtdata == "success") {
			JSPFree.queryDataByConditon(d1_BillList,null);  //立即查询刷新数据
			JSPFree.alert("导入成功！");
		}
	});
}

/**
 * 创建任务
 */
function addFillingChild() {
	JSPFree.openDialog("新增填报任务","/yujs/east/fillingProcess/distribute_fillin_insert.js",900,600,null,function(_rtData){
		if (_rtData.code == "success") {
			$.messager.alert('提示', '填报任务创建成功!', 'info');
		}
		JSPFree.queryDataByConditon(d1_BillList);
	});
}

/**
 * 删除任务
 */
function deleteFillChild() {
	var selectDatas = d1_BillList.datagrid('getSelections');
	if (selectDatas == null || selectDatas == undefined || selectDatas.length == 0) {
		$.messager.alert('提示', '必须选择一条记录进行操作', 'warning');
		return;
	}
	var selectRows = [];
	var status = "";
	for(var i=0; i<selectDatas.length; i++){
		selectRows.push(selectDatas[i]);
		if (!selectDatas[i].status.match("4")) {
			status += selectDatas[i].status + ","
		}
	}
	if (status != null && status != "") {
		$.messager.alert('提示', '只能删除手动创建的任务', 'warning');
	} else {
		JSPFree.confirm('提示', '是否删除填报任务?', function (_isOK) {
			if (_isOK) {
				var str_json = {jsonData:selectDatas}
				var jsn_result = JSPFree.doClassMethodCall("com.yusys.east.process.service.EastCrProcessBSDMO", "deleteFill", str_json);
				if (jsn_result.msg == 'OK') {
					for (var i = 0; i < selectRows.length; i++) {
						var str_rownumValue = selectRows[i]['_rownum']; // 取得行号数据
						var int_selrow = d1_BillList.datagrid("getRowIndex",
							str_rownumValue); // 根据_rownum的值,计算出是第几行,后面刷新就是这一行!
						// 从界面上删除行
						d1_BillList.datagrid('deleteRow', int_selrow);
					}
					$.messager.alert('提示', '删除成功', 'warning');
				}
			}
		})

	}
}

/**
 * 校验
 */
function check() {
	var json_data = d1_BillList.datagrid('getSelections');
	if (json_data == null || json_data == undefined || json_data.length == 0) {
		$.messager.alert('提示', '必须选择一条记录进行操作', 'warning');
		return;
	}
	if (json_data.length>1) {
		$.messager.alert('提示', '只能选择一条记录进行操作', 'warning');
		return;
	}
	if (json_data[0].distribute_type != '1') {
		$.messager.alert('提示', '只有按法人下发的记录可以进行校验', 'warning');
		return;
	}
	var str_json = {jsonData:json_data[0]}
	JSPFree.confirm('提示', '是否校验已填报数据?', function (_isOK) {
		if (_isOK) {
			var jsn_result = JSPFree.doClassMethodCall("com.yusys.east.process.service.EastCrProcessBSDMO", "doCheck", str_json);
			if (jsn_result.code == 'success') {
				$.messager.alert('提示', '检核任务已加入检核队列中，请前往检核任务页面查看检核详情', 'warning');
			} else {
				$.messager.alert('提示', jsn_result.msg, 'warning');
			}
		}
	})
}