/**
 * 
 * <pre>
 * Title: 【报表处理】-【报表填报】
 * Description: 报表处理-报表填报：主页面
 * 此页面提供了报表处理的填报管理功能，可以允许用户选择具体的子任务，点击填报
 * 列表展示子任务中的具体错误明细数据，选择一条错误明细数据，即可对数据进行填报
 * </pre>
 * @author liangzy5 
 * @version 1.00.00
   @date 2020年8月18日
 */

var org_no = "";
var maskUtil = "";
var whereSql = "";
function AfterInit(){
	//获取报表权限
	var jsn_result = JSPFree.doClassMethodCall("com.yusys.imas.common.service.ImasGetReportBS", "getReportList", null);
	if (jsn_result.code == 'success') {
		whereSql = jsn_result.data;
	}
	//通过当前登录人所属内部机构获取报送机构号
	var jso_report_org = JSPFree.doClassMethodCall("com.yusys.imas.common.service.ImasValidateQueryConditionBS","getReportOrgNoCondition",{_loginUserOrgNo:str_LoginUserOrgNo});
	if(jso_report_org.msg=="ok"){
		org_no = jso_report_org.data;
	}
	maskUtil = FreeUtil.getMaskUtil();
	JSPFree.createBillList("d1","/biapp-imas/freexml/fillingProcess/imas_filling_process_child_CODE1.xml",null,{isSwitchQuery:"N",autoquery:"N",orderbys:"data_dt desc,tab_name_en"});
	JSPFree.queryDataByConditon2(d1_BillList, getQueryCondition() + " and " + whereSql); //获取辖内机构的填报子任务
	JSPFree.setBillListForceSQLWhere(d1_BillList,getQueryCondition() + " and " + whereSql);
}

/**
 * 获取辖内机构号的过滤条件，作为sql的一部分
 * @returns
 */
function getQueryCondition(){
	var condition = "";
	var jso_rt = JSPFree.doClassMethodCall("com.yusys.imas.process.service.ImasCrProcessBSDMO", "getQueryConditionReportFillIn",{"_loginUserOrgNo" : str_LoginUserOrgNo} );
	if(jso_rt.msg == "ok"){
		condition = jso_rt.condition;
	}
	return condition;
}

/**
 * 填报按钮操作
 * 选择一条子任务，点击填报按钮，展示这条子任务对应的错误明细数据，并进行下一步操作
 * 只能查看到登录机构辖内机构的错误明细数据
 * @returns
 */
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
	//判断当前数据是否被锁定
	for (var i=0;i<json_data.length;i++) {
		var lock_data = JSPFree.doClassMethodCall("com.yusys.imas.process.service.ImasLockData", "getStatus", {tab_name:json_data[i].tab_name,data_dt:json_data[i].data_dt,org_no:json_data[i].rpt_org_no,type:'3'});
		if(lock_data.status == "锁定"){
			$.messager.alert('提示', '当前日期的表数据已被锁定，无法操作！', 'warning');
			return;
		}
	}

	var jso_Pars = {taskId:json_data[0].rid,tabNameEn:json_data[0].tab_name_en,tabName:json_data[0].tab_name,dataDt:json_data[0].data_dt};
	
	JSPFree.openDialog("填报","/yujs/imas/fillingProcess/imas_distribute_detail_fillin1.js",1000,600,jso_Pars,function(_rtData){
		if (_rtData != null) {
			if ("dirclose" == _rtData.type) {
				return;
			}
		}
		if (_rtData == "提交") {
			// 从界面上删除行
			var p = d1_BillList.datagrid('getPager');
			$(p).pagination('select');
			$.messager.alert('提示', '任务提交成功!', 'info');
		} else if (_rtData == "强制提交") {
			// 从界面上删除行
			var p = d1_BillList.datagrid('getPager');
			$(p).pagination('select');
			$.messager.alert('提示', '任务强制提交成功!', 'info');
		}

	});
}

/**
 * 查看处理日志
 * 选择一条子任务记录，点击按钮，即可查看这条子任务的处理日志
 * @returns
 */
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
	JSPFree.openDialog("处理日志信息","/yujs/imas/fillingProcess/imas_distribute_process_detail.js",700,400,jso_par,function(_rtData){
		if (_rtData != null) {
			if ("dirclose" == _rtData.type) {
				return;
			}
		}
	});
}
//导出填报数据
function exportData(_btn){
	var dataset = _btn.dataset;
	var index = dataset.rowindex;
	var rows = d1_BillList.datagrid("getRows");
	var row = rows[index];//index为行号
	if ("0" == row.no_handle) {
		JSPFree.alert("当前错误记录数为0，无法导出错误明细数据！");
		return;
	}

	// 获取错误表名
	// 计算错误明细表：表名，如Z191031_IBANKDPST
	var result= JSPFree.doClassMethodCall("com.yusys.imas.common.template.ImasCommonTemplate","getZTable",{orgNo: row.rpt_org_no, dataDt: row.data_dt, tableNameEn: row.tab_name_en});
	var tabNameEnZ = result.data;
	var param = {
		taskId: row.rid,
		type: '1',
		tabNameEn: row.tab_name_en,
		errorTableName: tabNameEnZ,
		detailType: "IMAS"
	}
	// 获取数量和sql
	var data= JSPFree.doClassMethodCall("com.yusys.bione.plugin.detail.download.service.DetailDownloadBS","getErrorSql",param);
	var jso_OpenPars = {tabName: row.tab_name,
		tabNameEn:row.tab_name_en,
		dataDt:row.data_dt,
		orgNo: row.org_no,
		dataSql: data.dataSql,
		dataCount: data.count,
		type: '2',
		detailType: "IMAS", //表类型
		downloadType: "3" ,  //导出类型：1、维护导出，2、错误明细导出，3、填报错误导出，4、填报全量导出，5、已修改记录导出
		fileType: "EXCEL",
		dsName: data.dsName,
		params: 'rpt_org_no= '+ row.rpt_org_no + ' and org_no=' + row.org_no + ' and data_dt=' + row.data_dt + ' and dept_no=' + row.dept_no
	};
	var jso_rt = JSPFree.doClassMethodCall("com.yusys.bione.plugin.detail.download.service.DetailDownloadBS", "getMaxDataNumExcel",{type: "IMAS"});
	exportFinal(data.count, jso_rt, jso_OpenPars);
}
/**
 * 导出
 */
function exportFinal(c,jso_rt, param) {
	if (parseInt(c) > parseInt(jso_rt.downloadNum)) {
		var showMsg = JSPFree.doClassMethodCall("com.yusys.bione.plugin.detail.download.service.DetailDownloadBS", "showMsg",param);
		if (showMsg.code == 'success') {
			JSPFree.confirm('提示', '当前导出数据量较大,可能导出时间很长,是否导出?', function(_isOK){
				if (_isOK) {
					param.code = 'success';
					maskUtil.mask();
					setTimeout(function () {
						// 调用线程启动类
						var json = JSPFree.doClassMethodCall("com.yusys.bione.plugin.detail.download.service.DetailDownloadBS", "exportDataThread", param);
						if (json.code='success') {
							$.messager.alert('提示', '导出数据较大，后台异步生成文件中，请稍后前往下载列表下载。', 'warning');
						} else {
							$.messager.alert('提示', json.msg, 'warning');
						}
						maskUtil.unmask();
					}, 100);
				}
			})

		}else if (showMsg.code == "warn") {
			JSPFree.confirm('提示', '当前导出数据已存在,是否重新生成?', function(_isOK) {
				if (_isOK) {
					param.code = 'warn';
					param.rid = showMsg.rid;
					maskUtil.mask();
					setTimeout(function () {
						// 调用线程启动类
						var json = JSPFree.doClassMethodCall("com.yusys.bione.plugin.detail.download.service.DetailDownloadBS", "exportDataThread", param);
						if (json.code='success') {
							$.messager.alert('提示', '导出数据较大，后台异步生成文件中，请稍后前往下载列表下载。', 'warning');
						} else {
							$.messager.alert('提示', json.msg, 'warning');
						}
						maskUtil.unmask();
					}, 100);
				}
			});
		} else {
			$.messager.alert('提示', showMsg.msg, 'warning');
		}

	} else {
		JSPFree.confirm('提示', '是否导出?', function(_isOK){
			if (_isOK) {
				maskUtil.mask();
				setTimeout(function () {
					var json = JSPFree.doClassMethodCall("com.yusys.bione.plugin.detail.download.service.DetailDownloadBS", "exportData",param);
					if (json.code='success') {
						var filepath = json.data;
						var src = v_context + "/detail/download/data/downloadData?filepath=" + filepath;
						var download = $('<iframe id="download" style="display: none;"/>');
						$('body').append(download);
						download.attr('src', src);
					} else {
						$.messager.alert('提示', json.msg, 'warning');
					}
					maskUtil.unmask();
				}, 100);

			}
		});
	}
}
/**
 * 导入
 * @return {[type]} [description]
 */
function importDatas(){
	JSPFree.openDialog("文件上传","/yujs/imas/fillingProcess/imas_distribute_fillin_import.js", 500, 240, null,function(_rtdata){
		if (_rtdata == "success") {
			JSPFree.queryDataByConditon(d1_BillList,null);  //立即查询刷新数据
			JSPFree.alert("导入成功！");
		}
	});
}