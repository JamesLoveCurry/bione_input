var tabName = "";
var taskId = "";
var _sql = "";
var _sql1 = "";
var d = "";
var isLoadTabb_2 = false;
var org_no = "";
var rpt_org_no = "";
var distributeType = "";
var str_classNameAll = "";
var str_classNameError = "";
var str_classNameUpdated = "";
var str_ds = "";
var tabNameEnZ = "";
var maskUtil = FreeUtil.getMaskUtil();
var _sql = "";
var _sqlAll= "";
var i = "N";
var str_ds_r = "";
function AfterInit(){
	taskId = jso_OpenPars.taskId;
	tabName = jso_OpenPars.tabName;
	tabNameEn = jso_OpenPars.tabNameEn;
	dataDt = jso_OpenPars.dataDt;
	org_no = jso_OpenPars.org_no;
	rpt_org_no = jso_OpenPars.rpt_org_no;
	distributeType = jso_OpenPars.distributeType;
	d = getDate(jso_OpenPars.dataDt);
	JSPFree.createSplitByBtn("d1","上下",650,["提交/submission","强制提交/forcesubmission"]);
	JSPFree.createTabb("d1_A", [ "错误数据", "全量数据", "已修改数据" ]);
	str_classNameError = "Class:com.yusys.east.business.model.service.EastModelTempleteBuilderFull.getTemplet('"+tabName+"','"+tabNameEn+"','"+rpt_org_no+"','" + org_no+ "','" + distributeType  + "','"+d+"','2')";
	JSPFree.createBillList("d1_A_1",str_classNameError,null,{list_btns:"[icon-p41]编辑/updateError(this);$VIEW;[icon-p69]导出excel/exportError;",isSwitchQuery:"N",autoquery:"N",list_ispagebar:"Y",ishavebillquery:"Y",list_ismultisel: "Y"});
	str_ds = d1_A_1_BillList.templetVO.templet_option.ds_y;
	str_ds_r = d1_A_1_BillList.templetVO.templet_option.ds_r;
	// 计算错误明细表：表名，如Z191031_IBANKDPST
	var result= JSPFree.doClassMethodCall("com.yusys.east.common.service.EastCommonBS","getErrorTableName",{orgNo: rpt_org_no, dataDt: d, tableNameEn: tabNameEn});
	tabNameEnZ = result.data;
	_sql = getSql(tabNameEnZ);
	var isOrNot= JSPFree.doClassMethodCall("com.yusys.east.common.service.EastCommonBS","isOrNot",{dsName: str_ds_r, errTable: tabNameEnZ});
	if (isOrNot.code == 'success') {
		//d1_A_1_BillList.pagerType = "1";
		//JSPFree.queryDataBySQL(d1_A_1_BillList, _sql);
	} else {
		i = "Y";
	}
	JSPFree.billListBindCustQueryEvent(d1_A_1_BillList, onDetailInfoErrorSummary);
	JSPFree.addTabbSelectChangedListener(d1_A_tabb,onSelect);

}

function AfterBodyLoad(){
	hidden(d1_A_1_BillList);
}

var sql = "";
/**
 * 绑定查询按钮
 * @param _condition
 */
function onDetailInfoErrorSummary(_condition) {
	if (i == 'Y') {
		$.messager.alert('提示', '当前日期下该机构的数据未检核,无错误数据！', 'warning');
		return;
	}
	if (_condition != "") {
		sql = _sql + " and" + _condition;
	}
	JSPFree.queryDataBySQL(d1_A_1_BillList, sql);
	FreeUtil.resetToFirstPage(d1_A_1_BillList); //手工跳转到第一页
}
var sqlAll = "";
/**
 * 绑定查询按钮
 * @param _condition
 */
function onDetailInfoErrorSummary1(_condition) {

	if (_condition != "") {
		sqlAll = _sqlAll + " and" + _condition;
	}
	JSPFree.queryDataBySQL(d1_A_2_BillList, sqlAll);
	FreeUtil.resetToFirstPage(d1_A_2_BillList); //手工跳转到第一页
}
/**
 * 全量数据修改
 */
function update() {
	var selectDatas = d1_A_2_BillList.datagrid('getSelections');
	if (selectDatas.length == 0) {
		$.messager.alert('提示', '至少选择一条记录进行操作', 'info');
		return;
	} else if (selectDatas.length > 1) {
		$.messager.alert('提示', '只能选择一条记录进行操作', 'info');
		return;
	}
	var json_rowdata = selectDatas[0]; // 先得到数据

	//若未选中则提示先选中再编辑
	if (json_rowdata == null || json_rowdata == undefined) {
		$.messager.alert('提示', '至少选择一条记录进行操作', 'warning');
		return;
	}

	var defaultVal = {
		templetcode : str_classNameAll,
		type:"Edit",
		tabname:tabName,
		tabnameen:tabNameEn,
		str_ds:str_ds_r,
		dataDt : dataDt,
		orgNo: org_no,
		rptOrgNo: rpt_org_no,
		distributeType: distributeType
	};
	defaultVal["_BillCardData"] = json_rowdata; // 列表数据,这样设置就是从前端取数

	JSPFree.openDialog("编辑","/yujs/east/fillingProcess/distribute_detail_fillin_full_edit.js",900,560,defaultVal,function(_rtdata){
		if (_rtdata && _rtdata.result == "校验并保存成功") {
			JSPFree.alert("校验并保存成功");
			//  修改成功删除当前行
			//d1_A_2_BillList.datagrid('deleteRow', JSPFree.getBillListSelectRow(d1_A_2_BillList));
		}
	},true);
}

/**
 * 全量数据页面删除
 */
function deleteAllSheet () {
	var selectDatas = d1_A_2_BillList.datagrid('getSelections');
	if (selectDatas == null || selectDatas == undefined || selectDatas.length == 0) {
		$.messager.alert('提示', '必须选择一条记录进行操作', 'warning');
		return;
	}
	JSPFree.confirm("提示","您确认要删除所选数据吗?",function(_isOK){
		if(_isOK){
			var jso_templetVO = d1_A_2_BillList.templetVO; // 模板配置数据
			var array_items = jso_templetVO.templet_option_b; // 模板子表
			var items = new Array();
			var rids = new Array();
			for (var i = 0; i < array_items.length; i++) {
				var str_itemkey = array_items[i].itemkey;
				var listShow = array_items[i].list_isshow;
				if ("Y" == listShow) {
					items.push(str_itemkey);
				}
			}
			var selectRows = [];
			for (var i=0; i<selectDatas.length; i++) {
				rids.push(selectDatas[i].rid);
				selectRows.push(selectDatas[i]);
			}
			var jso_par = {ds:str_ds_r,allrids:rids,items:items,dataDt:d,tabNameEn:tabNameEn, orgNo: rpt_org_no, selectDatas: selectDatas, tabName: tabName,dsY: str_ds};
			var jsn_result = JSPFree.doClassMethodCall("com.yusys.east.process.service.EastCrProcessBSDMO","deleteAllSheet",jso_par);
			if (jsn_result.code = 'success') {
				/*for (var i=0; i<selectRows.length;i++) {
					var str_rownumValue = selectRows[i]['_rownum']; // 取得行号数据
					var int_selrow = d1_A_2_BillList.datagrid("getRowIndex",
						str_rownumValue); // 根据_rownum的值,计算出是第几行,后面刷新就是这一行!
					// 从界面上删除行
					d1_A_2_BillList.datagrid('deleteRow', int_selrow);
				}*/

				JSPFree.alert("删除成功，请提交审核！");//  修改成功删除当前行
			}
		}
	})

}
/**
 * 错误数据修改
 */
function updateError() {
	var json_rowdata = d1_A_1_BillList.datagrid('getSelected'); // 先得到数据

	//若未选中则提示先选中再编辑
	if (json_rowdata == null || json_rowdata == undefined) {
		$.messager.alert('提示', '至少选择一条记录进行操作', 'warning');
		return;
	}

	var defaultVal = {
		templetcode : str_classNameError,
		type:"Edit",
		tabname:tabName,
		tabnameen:tabNameEn,
		str_ds:str_ds_r,
		disType : "1",
		dataDt : dataDt,
		orgNo: org_no,
		rptOrgNo: rpt_org_no,
		distributeType: distributeType
	};
	defaultVal["_BillCardData"] = json_rowdata; // 列表数据,这样设置就是从前端取数

	JSPFree.openDialog("编辑","/yujs/east/fillingProcess/distribute_detail_fillin_full_edit.js",900,560,defaultVal,function(_rtdata){
		if (_rtdata && _rtdata.result == "校验并保存成功") {
			JSPFree.alert("校验并保存成功");//  修改成功删除当前行
			d1_A_1_BillList.datagrid('deleteRow', JSPFree.getBillListSelectRow(d1_A_1_BillList));
		}
	},true);
}

/**
 * 已修改数据删除
 */
function deleteUpdate() {
	var selectDatas = d1_A_3_BillList.datagrid('getSelections');
	if (selectDatas == null || selectDatas == undefined || selectDatas.length == 0) {
		$.messager.alert('提示', '必须选择一条记录进行操作', 'warning');
		return;
	}
	JSPFree.confirm("提示","您确认要删除所选数据吗?",function(_isOK){
		var rids = new Array();
		if(_isOK){
			for (var i=0; i<selectDatas.length; i++) {
				rids.push(selectDatas[i].rid);
			}
			var jso_par = {ds:str_ds_r,allrids:rids,tabNameEn:tabNameEn, selectDatas: selectDatas, tabName: tabName};
			var jsn_result = JSPFree.doClassMethodCall("com.yusys.east.process.service.EastCrProcessBSDMO","updateProcStsDel",jso_par);
			if (jsn_result.code = 'success') {
				JSPFree.refreshBillListCurrRows(d1_A_3_BillList); // 刷新当前行
				JSPFree.alert("删除成功，请提交审核！");//  修改成功删除当前行
			}
		}
	})
}
/**
 *  点击sheet页切换
 * @param _index
 * @param _title
 */
function onSelect(_index,_title) {
	var newIndex = _index+1;

	if(newIndex==1){
		if (i == 'N') {
			// 由于每次切换tab，都会重新查数据，导致很慢。所以先注释掉
			if (sql == "" || sql == null) {
				sql = _sql;
			}
			d1_A_1_BillList.pagerType = "1";
			JSPFree.queryDataBySQL(d1_A_1_BillList, sql);
			FreeUtil.resetToFirstPage(d1_A_1_BillList); //手工跳转到第一页
		}

	} else if(newIndex==2){
		if (str_classNameAll) {
			JSPFree.queryDataByConditon(d1_A_2_BillList);
		} else {
			str_classNameAll = "Class:com.yusys.east.business.model.service.EastModelTempleteBuilderFull.getTemplet('"+tabName+"','"+tabNameEn+"','"+rpt_org_no+"','" + org_no+ "','" + distributeType  + "','"+ d+"','1')";
			JSPFree.createBillList("d1_A_2",str_classNameAll,null,{list_btns:"[icon-p41]编辑/update(this);$VIEW;[icon-remove]删除/deleteAllSheet;[icon-p69]导出excel/exportAll;",isSwitchQuery:"N",autoquery:"Y",list_ispagebar:"Y",ishavebillquery:"Y",list_ischeckstyle:"Y",list_ismultisel:"Y"});
			$.parser.parse('#d1_A_2');
		}

		hidden(d1_A_2_BillList);
	} else {
		if (str_classNameUpdated) {
			JSPFree.queryDataByConditon(d1_A_3_BillList);
		} else {
			str_classNameUpdated = "Class:com.yusys.east.business.model.service.EastModelTempleteBuilderFull.getTemplet('"+tabName+"','"+tabNameEn+"','"+rpt_org_no+"','" + org_no+ "','" + distributeType  + "','"+ d+"','3')";
			JSPFree.createBillList("d1_A_3",str_classNameUpdated,null,{list_btns:"[icon-p39]新增/insertR(this);[icon-p41]编辑/updateR(this);$VIEW;[icon-remove]删除/deleteUpdate;[icon-p69]导出excel/exportUpdate;[icon-p68]导入excel/importData;",isSwitchQuery:"N",autoquery:"Y",list_ispagebar:"Y",ishavebillquery:"Y",list_ischeckstyle:"Y",list_ismultisel:"Y"});
			$.parser.parse('#d1_A_3');
		}
		hidden(d1_A_3_BillList);
	}
}
//新增
function insertR() {
	var defaultVal = {
		templetcode : str_classNameUpdated,
		type:"Add",
		tabname:tabName,
		tabnameen:tabNameEn,
		str_ds:str_ds_r,
		orgNo: org_no,
		dataDt : d,
		rptOrgNo: rpt_org_no,
		distributeType: distributeType
	};
	JSPFree.openDialog("新增","/yujs/east/fillingProcess/distribute_update_edit.js",900,560,defaultVal,function(_rtdata){
		if (_rtdata && _rtdata.result == "校验并保存成功") {
			JSPFree.alert("校验并保存成功");
			JSPFree.queryDataByConditon(d1_A_3_BillList);
		}
	},true);
}
//修改
function updateR() {
	var selectDatas = d1_A_3_BillList.datagrid('getSelections');
	if (selectDatas.length == 0) {
		$.messager.alert('提示', '至少选择一条记录进行操作', 'info');
		return;
	} else if (selectDatas.length > 1) {
		$.messager.alert('提示', '只能选择一条记录进行操作', 'info');
		return;
	}
	var json_rowdata = selectDatas[0]; // 先得到数据
	var defaultVal = {
		templetcode : str_classNameUpdated,
		type:"Edit",
		tabname:tabName,
		tabnameen:tabNameEn,
		str_ds:str_ds_r,
		dataDt : d,
		orgNo: org_no,
		rptOrgNo: rpt_org_no,
		distributeType: distributeType
	};
	defaultVal["_BillCardData"] = json_rowdata; // 列表数据,这样设置就是从前端取数

	JSPFree.openDialog("编辑","/yujs/east/fillingProcess/distribute_update_edit.js",900,560,defaultVal,function(_rtdata){
		if (_rtdata && _rtdata.result == "校验并保存成功") {
			JSPFree.alert("校验并保存成功");
			JSPFree.refreshBillListCurrRow(d1_A_3_BillList); // 刷新当前行
		}
	},true);
}
/**
 * 插入或者更新总行的数据
 * @param jsonResult 新增/修改表示
 * @param param 参数
 * @param templetcode 模板代码
 */
function executeInsertOrUpdate(jsonResult, param, flag) {
	if (jsonResult) {
		temp = temp.replace('\'\'', "'_R'");
		doInsertOrUpdateData(flag, param, temp);
	}
}
function doInsertOrUpdateData(flag, param, temp) {
	if ("insert" === flag) {
		FreeUtil.execInsertBillCardData(param.ds, temp, param.formData);
	} else if ("update" === flag) {
		FreeUtil.execUpdateBillCardData(param.ds, temp, param.formData, param.SQLWhere);
	}
}
//提交
function submission(){
	if (d1_A_1_BillList.CurrSQL3 != null || undefined != d1_A_1_BillList.CurrSQL3) {
		var sql = d1_A_1_BillList.CurrSQL3.replace("*", " count(1) c ");
		var countResult = JSPFree.getHashVOs(sql, str_ds_r);
		// 如果当前待编辑列表中有数据，则不能进行提交
		if (countResult[0].c != '0') {
			$.messager.alert('提示', '当前列表存在错误待编辑数据，无法提交！', 'warning');
			return;
		}

	}

	var jso_allrids=[]; 
	jso_allrids.push(taskId);

	// 修改数据状态：1：待审核
	var jsn_result = JSPFree.doClassMethodCall("com.yusys.east.process.service.EastCrProcessBSDMO", "updateDataByTaskByRids", {allrids:jso_allrids,status:'1',type:"1",userNo:str_LoginUserCode});
	if (jsn_result.msg == 'OK') {
		JSPFree.closeDialog("提交");
	}
}

//强制提交
function forcesubmission(){
	var jso_allrids=[]; 
	jso_allrids.push(taskId);

	JSPFree.confirm("提示","您确认要强制提交吗?",function(_isOK){
		if(_isOK){
			//弹出窗口,传入参数,然后接收返回值!
			JSPFree.openDialog("填写原因","/yujs/east/fillingProcess/distribute_detail_fillin_reason.js",600,300,{templetcode:"/biapp-east/freexml/east/fillingProcess/east_filling_process_remark.xml"},function(_rtdata){
				if(_rtdata != null && _rtdata != undefined && null != _rtdata.reason && undefined != _rtdata.reason){
					doUpdateBackStatus(d1_A_1_BillList,jso_allrids, _rtdata.reason);
				}
			});
		}
	});
}

//强制提交，修改状态
function doUpdateBackStatus(billList,_rids,_reason){
	var jso_templetVO = billList.templetVO; // 模板配置数据
	var array_items = jso_templetVO.templet_option_b; // 模板子表
	var colStr = "";
	for (var i = 0; i < array_items.length; i++) {
		var str_itemkey = array_items[i].itemkey;
		var listShow = array_items[i].list_isshow;
		if ("Y" == listShow) {
			colStr += str_itemkey + ',';
		}
	}
	colStr = colStr.substring(0, colStr.length - 1);
	var errorSql = billList.CurrSQL3;
	var jso_par = {allrids:_rids,type:'1',reason:_reason,userNo:str_LoginUserCode, colArry:colStr, dsName: str_ds_r, errorSql:errorSql};
	var jsn_result = JSPFree.doClassMethodCall("com.yusys.east.process.service.EastCrProcessBSDMO","doResultBack",jso_par);
	
	if (jsn_result.msg == 'OK') {
		JSPFree.closeDialog("强制提交");
	}
}

function getDate(dataDt) {
	var d = dataDt.replace(/-/g, '');
	return d;
}

/**
 * 隐藏部分展示框
 */
function hidden(billList) {
	// 隐藏日历
	FreeUtil.loadBillQueryData(billList, {cjrq: dataDt,kjrq: dataDt, org_no: rpt_org_no});
	/*JSPFree.setBillQueryItemEditable("cjrq", "日历", false);
	JSPFree.setBillQueryItemEditable("kjrq", "日历", false);
	// 隐藏法人机构
	JSPFree.setBillQueryItemEditable("org_no", "自定义参照", false);*/
	// 如果是按照网点，则也隐藏网点
	if (distributeType == '2') {
		FreeUtil.loadBillQueryData(billList, {issued_no: org_no });
		// 隐藏网点机构
		//JSPFree.setBillQueryItemEditable("issued_no", "自定义参照", false);
	}
}

/**
 * 全量数据页面导出excel
 */
function exportAll() {
	if (d1_A_2_BillList.CurrSQL3 == null || "undefined" == d1_A_2_BillList.CurrSQL3) {
		JSPFree.alert("当前无记录！");
		return;
	}
	var sql = d1_A_2_BillList.CurrSQL3;
	var new_sql = 'select count(1) c from ('+sql+') t';
	var jso_data = JSPFree.getHashVOsByDS(new_sql, str_ds);

	var c = jso_data[0].c;
	var jso_rt = JSPFree.doClassMethodCall("com.yusys.bione.plugin.detail.download.service.DetailDownloadBS", "getMaxDataNumExcel",{type: "EAST"});
	var param = {
		tabName: tabName,
		tabNameEn: tabNameEn,
		dsName: str_ds,
		dataSql:d1_A_2_BillList.CurrSQL3,
		dataCount: c,
		orgNo: org_no,
		dataDt: dataDt,
		type: '1',
		detailType: "EAST", //表类型
		downloadType: "4" ,  //导出类型：1、维护导出，2、错误明细导出，3、填报错误导出，4、填报全量导出，5、已修改记录导出
		fileType: "EXCEL"
	}

	exportFinal(c, jso_rt, param);


}

/**
 * 导出错误记录
 */
function exportError() {
	if (d1_A_1_BillList.CurrSQL3 == null || "undefined" == d1_A_1_BillList.CurrSQL3) {
		JSPFree.alert("当前无记录！");
		return;
	}
	var sql = d1_A_1_BillList.CurrSQL3;
	var new_sql = 'select count(1) c from ('+sql+') t';
	var jso_data = JSPFree.getHashVOsByDS(new_sql, str_ds_r);
	var c = jso_data[0].c;
	var jso_rt = JSPFree.doClassMethodCall("com.yusys.bione.plugin.detail.download.service.DetailDownloadBS", "getMaxDataNumExcel",{type: "EAST"});
	var param = {
		tabName: tabName,
		tabNameEn: tabNameEn,
		dsName: str_ds_r,
		dataSql:d1_A_1_BillList.CurrSQL3,
		dataCount: c,
		orgNo: org_no,
		dataDt: dataDt,
		type: '2',
		detailType: "EAST", //表类型
		downloadType: "3" ,  //导出类型：1、维护导出，2、错误明细导出，3、填报错误导出，4、填报全量导出，5、已修改记录导出
		fileType: "EXCEL"
	}
	exportFinal(c, jso_rt, param);

}

/**
 * 导出错误记录
 */
function exportUpdate() {
	if (d1_A_3_BillList.CurrSQL3 == null || "undefined" == d1_A_3_BillList.CurrSQL3) {
		JSPFree.alert("当前无记录！");
		return;
	}
	var sql = d1_A_3_BillList.CurrSQL3+ "and proc_sts < 3";
	var new_sql = 'select count(1) c from ('+sql+') t';
	var jso_data = JSPFree.getHashVOsByDS(new_sql, str_ds_r);
	var c = jso_data[0].c;
	var jso_rt = JSPFree.doClassMethodCall("com.yusys.bione.plugin.detail.download.service.DetailDownloadBS", "getMaxDataNumExcel",{type: "EAST"});
	var param = {
		tabName: tabName,
		tabNameEn: tabNameEn,
		dsName: str_ds_r,
		dataSql:d1_A_3_BillList.CurrSQL3,
		dataCount: c,
		orgNo: org_no,
		dataDt: dataDt,
		type: '3',
		detailType: "EAST", //表类型
		downloadType: "5" ,  //导出类型：1、维护导出，2、错误明细导出，3、填报错误导出，4、填报全量导出，5、已修改记录导出
		fileType: "EXCEL"
	}
	exportFinal(c, jso_rt, param);
}

/**
 * 导出
 */
function exportFinal(c,jso_rt, param) {
	if (parseInt(c) > parseInt(jso_rt.downloadNum)) {
		var showMsg = JSPFree.doClassMethodCall("com.yusys.bione.plugin.detail.download.service.DetailDownloadBS", "showMsg",param);
		if (showMsg.code == 'success') {
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
 */
function importData() {
	JSPFree.openDialog("文件上传","/yujs/east/fillingProcess/east_distribute_import.js", 500, 360, {tab_name:tabName, tab_name_en:tabNameEn,orgNo: org_no, rptOrgNo:rpt_org_no,distributeType: distributeType,dataDt: d},function(_rtdata){
		if (_rtdata.success == "true") {
			JSPFree.alert("导入成功！本次共导入"+_rtdata.msg+"条数据");
			JSPFree.queryDataByConditon(d1_A_3_BillList);
		} else {
			if (_rtdata.msg) {
				$.messager.alert({
					title:'系统提示',
					msg:'<div style="height:80px;text-align:center;overflow-x: hidden; overflow-y: auto;">' + _rtdata.msg + '</div>',
					width:320,
					top: 150,
					icon:'info'
				});
			}

		}
	});
}

/**
 * 获取sql语句，根据实际情况拼接sql语句，包含日期、机构等过滤条件
 *
 */
function getSql(tableName) {
	var cond = "";
	cond += " and org_no='" + rpt_org_no + "'";
	if ("2" == (distributeType)) {
		cond  += " and issued_no='" + org_no + "'";
	}
	cond += " and cjrq='" + d + "'";
	var _sql = "select * from " + tableName + " z where not exists (select 1 from " + tabNameEn + "_r r where r.rid=z.rid and r.is_check='0'" + cond +")";

	return _sql + cond;
}