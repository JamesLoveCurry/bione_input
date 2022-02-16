/**
 *
 * <pre>
 * Title:【数据查询】【报表查询】
 * Description:各个报表列表
 * </pre>
 * @author wangxy31 
 * @version 1.00.00
   @date 2020年8月6日
 */

var tab_name = "";
var report_type = "";
var str_ds = "";
var str_className = "";
var org_no = "";
var org_class = "";
var maskUtil = "";

function AfterInit() {
	tab_name = jso_OpenPars.tab_name;
	tab_name_en = jso_OpenPars.tab_name_en;
	str_ds = jso_OpenPars.ds;
	maskUtil = FreeUtil.getMaskUtil();
	
	var whereSql = '';
	var jsn_result = JSPFree.doClassMethodCall("com.yusys.bfd.business.service.BfdGetReportBS", "getObjList", null);
	if (jsn_result.code == 'success') {
		whereSql = jsn_result.data;
	}
	str_className = "Class:com.yusys.bfd.business.service.BfdModelTempletBuilder.getTemplet('" + tab_name + "','" + tab_name_en + "','" + str_LoginUserOrgNo + "')";
	JSPFree.createBillList("d1", str_className, null, {list_btns:"[icon-add]新增/insert1;[icon-edit]编辑/update1;[icon-remove]删除/delete1;[icon-p81]查看/view1;[icon-p69]导出csv/exportCSV(this);[icon-p69]导出excel/exportExcel(this);[icon-p68]导入excel/importExcel(this);[icon-p68]导入报文/onImportReport",isSwitchQuery:"N",autoquery:"N",list_ischeckstyle: "Y",list_ismultisel: "Y", refWhereSQL: whereSql});
}

/**
 * 加载页面之后处理
 * @returns
 */
function AfterBodyLoad() {
	// 判断当前用户是总行，还是分行
	var jso_data = JSPFree.doClassMethodCall("com.yusys.bfd.business.service.BfdBusinessBS","checkUserOrgNo",{str_LoginUserOrgNo: str_LoginUserOrgNo}); 
	var result = jso_data.result;
	if (result == "OK") {
		org_no = jso_data.orgNo;
		org_class = jso_data.orgClass;
	}

	if (org_class == BfdFreeUtil.getBfdOrgClass().zh || org_class == BfdFreeUtil.getBfdOrgClass().fh) {
		// 进行赋值
		FreeUtil.loadBillQueryData(d1_BillList, {rpt_org_no:org_no});
	}
}

/**
 * 新增
 * @returns
 */
function insert1() {
	var defaultVal = {type:"Add",tabname:tab_name,tabnameen:tab_name_en,str_ds:str_ds,org_no:org_no,org_class:org_class};
	JSPFree.openDialog3("新增","/yujs/bfd/busidata/bfd_check_data_edit.js",1100,560,defaultVal,function(_rtdata){
		if (_rtdata == "CHECK_OK") {
			JSPFree.alert("校验并保存成功!");
		} else if (_rtdata == "OK") {
			JSPFree.alert("保存成功!");
		}
	},true);
}

/**
 * 修改
 * @returns
 */
function update1() {
	var json_rowdata = d1_BillList.datagrid('getSelections'); // 先得到数据
	
	if (json_rowdata == null || json_rowdata == undefined) {
		$.messager.alert('提示', '至少选择一条记录进行操作', 'warning');
		return;
	}

	if(json_rowdata.length != 1){
		$.messager.alert('提示', '只能选择一条记录进行操作', 'warning');
		return;
	}
	//判断当前数据是否被锁定
	var lock_data = JSPFree.doClassMethodCall("com.yusys.bfd.process.service.BfdLockData", "getStatus", {tab_name:tab_name,data_dt:json_rowdata[0].data_dt,org_no:json_rowdata[0].rpt_org_no,type:'2'});
	if(lock_data.status == "锁定"){
		$.messager.alert('提示', '当前日期的表数据已被锁定，无法操作！', 'warning');
		return;
	}


	var defaultVal = {type:"Edit",tabname:tab_name,tabnameen:tab_name_en,str_ds:str_ds,org_no:org_no,org_class:org_class};
	defaultVal["_BillCardData"] = json_rowdata[0]; // 列表数据,这样设置就是从前端取数
		
	JSPFree.openDialog3("编辑","/yujs/bfd/busidata/bfd_check_data_edit.js",null,null,defaultVal,function(_rtdata) {
		if (_rtdata == "CHECK_OK") {
			JSPFree.alert("校验并保存成功!");
			JSPFree.refreshBillListCurrRow(d1_BillList); // 刷新当前行
		} else if (_rtdata == "OK") {
			JSPFree.alert("保存成功!");
			JSPFree.refreshBillListCurrRow(d1_BillList); // 刷新当前行
		}
	},true);
}

/**
 * 查看
 * @returns
 */
function view1() {
	var json_rowdata = d1_BillList.datagrid('getSelections'); // 先得到数据
	
	if (json_rowdata == null || json_rowdata == undefined) {
		$.messager.alert('提示', '至少选择一条记录进行操作', 'warning');
		return;
	}

	if(json_rowdata.length != 1){
		$.messager.alert('提示', '只能选择一条记录进行操作', 'warning');
		return;
	}

	var defaultVal = {templetcode:str_className,type:"View",tabname:tab_name,tabnameen:tab_name_en,str_ds:str_ds};
	defaultVal["_BillCardData"] = json_rowdata[0]; // 列表数据,这样设置就是从前端取数
		
	JSPFree.openDialog3("查看","/yujs/bfd/busidata/bfd_check_data_edit.js",null,null,defaultVal,function(_rtdata) {},true);
}


// 导出CSV
function exportCSV(){
	if (d1_BillList.CurrSQL3 == null || "undefined" == d1_BillList.CurrSQL3) {
		JSPFree.alert("当前无记录！");
		return;
	}
	var dataDt = JSPFree.getBillQueryFormValue(d1_BillQuery).data_dt;
	// 如果超过50w数据,页面提示语
	var sql = d1_BillList.CurrSQL3;
	var new_sql = 'select count(*) c from ('+sql+') t';
	var jso_data = JSPFree.getHashVOsByDS(new_sql, str_ds);

	var c = jso_data[0].c;
	var param = {tabName: tab_name,
		tabNameEn: tab_name_en,
		dsName: str_ds,
		dataSql:d1_BillList.CurrSQL3,
		orgNo: org_no,
		dataDt: dataDt,
		dataCount: c,
		type: 3,
		detailType: "BFD", //表类型
		downloadType: "1" ,  //导出类型：1、维护导出，2、错误明细导出，3、填报错误导出，4、填报全量导出，5、已修改记录导出
		fileType: "CSV"
	};
	var jso_rt = JSPFree.doClassMethodCall("com.yusys.bione.plugin.detail.download.service.DetailDownloadBS", "getMaxDataNumCsv",{type: "BFD"});
	exportFinal(c, jso_rt, param);
}

/**
 * 导出excel
 */
function exportExcel(){
	if (d1_BillList.CurrSQL3 == null || "undefined" == d1_BillList.CurrSQL3) {
		JSPFree.alert("当前无记录！");
		return;
	}
	var sql = d1_BillList.CurrSQL3;
	var new_sql = 'select count(*) c from ('+sql+') t';
	var jso_data = JSPFree.getHashVOsByDS(new_sql, str_ds);
	var dataDt = JSPFree.getBillQueryFormValue(d1_BillQuery).data_dt;
	var c = jso_data[0].c;
	var jso_rt = JSPFree.doClassMethodCall("com.yusys.bione.plugin.detail.download.service.DetailDownloadBS", "getMaxDataNumExcel",{type: "BFD"});
	var param = {tabName: tab_name,
		tabNameEn: tab_name_en,
		dsName: str_ds,
		dataSql:d1_BillList.CurrSQL3,
		dataCount: c,
		orgNo: org_no,
		dataDt: dataDt,
		type: '1',
		detailType: "BFD", //表类型
		downloadType: "1" ,  //导出类型：1、维护导出，2、错误明细导出，3、填报错误导出，4、填报全量导出，5、已修改记录导出
		fileType: "EXCEL"
	};
	exportFinal(c, jso_rt, param)
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
function importExcel(){
	JSPFree.openDialog("文件上传","/yujs/bfd/busidata/bfd_busi_data_import.js", 500, 380, {tab_name:tab_name, tab_name_en:tab_name_en},function(_rtdata){
		if (_rtdata.success == "true") {
			JSPFree.alert("导入成功！本次共导入"+_rtdata.msg+"条数据");
		}
	});
}

/**
 * 删除
 */
function delete1(){
	var json_rowdata = d1_BillList.datagrid('getSelections'); // 先得到数据

	if (json_rowdata == null || json_rowdata == undefined) {
		$.messager.alert('提示', '至少选择一条记录进行操作', 'warning');
		return;
	}

    var rid = new Array();
    for (var i=0; i<json_rowdata.length; i++) {
    	//判断当前数据是否被锁定
    	var lock_data = JSPFree.doClassMethodCall("com.yusys.bfd.process.service.BfdLockData", "getStatus", {tab_name:tab_name,data_dt:json_rowdata[i].data_dt,org_no:json_rowdata[i].rpt_org_no,type:'2'});
    	if(lock_data.status == "锁定"){
    		$.messager.alert('提示', '当前日期的表数据已被锁定，无法操作！', 'warning');
    		return;
    	}
    	rid.push(json_rowdata[i].rid);
    }

    doBillListDelete(d1_BillList);

}

function doBillListDelete (_grid) {
	var json_rowdata = _grid.datagrid('getSelections'); // 先得到数据
	if (json_rowdata == null) {
		$.messager.alert('提示', '必须选择一条数据!', 'info');
		return;
	}

	var str_divid = _grid.divid;
	var str_beforeDeleteFn = "beforeDelete_" + str_divid + "_BillList"; // 删除前校验
	if (typeof self[str_beforeDeleteFn] == "function") { // 如果有这个函数
		var isOK = self[str_beforeDeleteFn](_grid, json_rowdata); // 执行
		if (!isOK) {
			return; // 如果失败则返回
		}
	}

	// 警告提醒是否真的删除?
	$.messager.confirm('提示', '你真的要删除选中的记录吗?', function(_isConfirm) {
		if (!_isConfirm) {
			return;
		}

		var jso_templetVO = _grid.templetVO; // 模板对象
		var str_ds = jso_templetVO.templet_option.ds; // 数据源


		// 远程调用,真正删除数据库
		try {
			var str_id = [];
			var str_path = [];
			for(var i=0; i<json_rowdata.length; i++){
				str_id.push(json_rowdata[i].rid);

				var dataOurces = json_rowdata[i].data_ources;//数据来源
				if (json_rowdata[i].data_ources!=BfdFreeUtil.getDataOurces().etl_process && json_rowdata[i].data_ources!=null && json_rowdata[i].data_ources!='undefined' && json_rowdata[i].data_ources!=''){
					var source_type;
					//删除新增
					if (dataOurces==BfdFreeUtil.getDataOurces().page_entry){
						source_type=BfdFreeUtil.getSourceType().page_entry;
					} else if (dataOurces==BfdFreeUtil.getDataOurces().batch_import) {
						source_type=BfdFreeUtil.getSourceType().batch_import;
					}
					var _par={tab_name_en:tab_name_en,data_dt:json_rowdata[i].data_dt,number:-1,source_type:source_type,rpt_org_no:json_rowdata[i].rpt_org_no};
					JSPFree.doClassMethodCall("com.yusys.bfd.dataoperation.BfdStatisticalOperation","statisticalOperationForForm",_par)
				}else{
					let dataModify = json_rowdata[i].data_modify;
					if (json_rowdata[i].data_modify!=""){
						var source_type;
						if (dataModify==BfdFreeUtil.getDataModefy().data_maintenance){
							source_type=BfdFreeUtil.getSourceType().data_maintenance;
						}else if (dataModify==BfdFreeUtil.getDataModefy().data_filling){
							source_type=BfdFreeUtil.getSourceType().data_filling;
						}
						var _par={tab_name_en:tab_name_en,data_dt:json_rowdata[i].data_dt,number:-1,source_type:source_type,rpt_org_no:json_rowdata[i].rpt_org_no};
						JSPFree.doClassMethodCall("com.yusys.bfd.dataoperation.BfdStatisticalOperation","statisticalOperationForForm",_par)
					}
					var _par={tab_name_en:tab_name_en,data_dt:json_rowdata[i].data_dt,number:-1,source_type:BfdFreeUtil.getSourceType().etl_process,rpt_org_no:json_rowdata[i].rpt_org_no};
					JSPFree.doClassMethodCall("com.yusys.bfd.dataoperation.BfdStatisticalOperation","statisticalOperationForForm",_par)

				}
			}


			var idStr = "('" + str_id.join("','") + "')";
			JSPFree.doClassMethodCall("com.yusys.bfd.business.service.BfdBusinessBS", "del", {ds:str_ds, str_id:idStr, tab_name_en:tab_name_en,tabName: tab_name, jsonData: json_rowdata});
			// 从界面上删除行
			var p = _grid.datagrid('getPager');
			$(p).pagination('select');
			$.messager.alert('提示', '删除数据成功!', 'info');
		} catch (_ex) {
			console.log(_ex);
			FreeUtil.openHtmlMsgBox("发生异常", 500, 250, _ex)
		}
	});
}
/**
 * 导入报文
 */
function onImportReport() {
	JSPFree.openDialog("文件上传", "/yujs/bfd/report/bfd_report_import.js", 500, 300, {
		tab_name_en:jso_OpenPars.tab_name_en
	}, function (_rtdata) {
		if (_rtdata == "success") {
			JSPFree.alert("导入成功！");
		}
	});
}