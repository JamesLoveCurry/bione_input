//初始化界面,菜单配置路径是【/frs/yufreejs?js=/yujs/east/58table.js】
var str_ds = null;
var str_className = null;
var tab_name = "";
var tab_name_en = "";
var str_subfix = "";
var maskUtil = "";
function AfterInit(){
	maskUtil = FreeUtil.getMaskUtil();
	//更新默认的登录机构号。原因是str_LoginUserOrgNo存的实际上是rpt_org_info表里的mgr_org_no，而不是想要的org_no
	//通过下面这行代码，把str_LoginUserOrgNo更新为mgr_org_no对应的org_no
//	var jso_rt = JSPFree.doClassMethodCall("com.yusys.east.common.GetRealOrgNoBS","updateRealOrgNo",{OrgType:"04",OrgNo : str_LoginUserOrgNo});
//	str_LoginUserOrgNo = jso_rt.realOrgNo;
	
	str_subfix = jso_OpenPars.subfix;  //模板后辍,月报就是_M
	str_ds = jso_OpenPars.ds;  //数据源
	tab_name = jso_OpenPars.tab_name;
	tab_name_en = jso_OpenPars.tab_name_en;
	
	str_className = "Class:com.yusys.east.business.model.service.East58ModelTempletBuilder.getTemplet('" + tab_name + "','" + tab_name_en + "','" + str_LoginUserOrgNo + "','" + str_subfix + "')";
	  
	// 产品：创建表格.
	if (str_subfix != null && str_subfix != "") {
		JSPFree.createBillList("d1", str_className, null, {
			list_btns: "[icon-p81]查看/view1;[icon-p69]导出csv/exportCSV(this);[icon-p69]导出excel/exportExcel(this);"
			, isSwitchQuery: "N"
		});
	} else {
		JSPFree.createBillList("d1", str_className, null, {
			list_btns: "[icon-p81]查看/view1;[icon-p69]导出csv/exportCSV(this);",
			isSwitchQuery: "N"
		});
	}
}

/**
 * 加载页面之后处理
 * @returns
 */
function AfterBodyLoad() {
	// 判断当前用户是总行，还是分行
	var jso_data = JSPFree.doClassMethodCall("com.yusys.east.business.service.EastBusinessBS","checkUserOrgNo",{str_LoginUserOrgNo: str_LoginUserOrgNo});
	var result = jso_data.result;
	if (result == "OK") {
		org_no = jso_data.orgNo;
		org_class = jso_data.orgClass;
	}

	if (org_class == EastFreeUtil.getEastOrgClass().zh || org_class == EastFreeUtil.getEastOrgClass().fh) {
		// 进行赋值
		FreeUtil.loadBillQueryData(d1_BillList, {org_no:org_no});
	}
}


/**
 * 查看
 * @returns
 */
function view1() {
	var json_rowdata = d1_BillList.datagrid('getSelected'); // 先得到数据

	if (json_rowdata == null || json_rowdata == undefined) {
		$.messager.alert('提示', '至少选择一条记录进行操作', 'warning');
		return;
	}

	var defaultVal = {templetcode:str_className,type:"View",tabname:tab_name,tabnameen:tab_name_en,str_ds:str_ds};
	defaultVal["_BillCardData"] = json_rowdata; // 列表数据,这样设置就是从前端取数

	JSPFree.openDialog3("查看","/yujs/east/check_data_edit.js",null,null,defaultVal,function(_rtdata) {

	},true);
}




// 导出CSV
function exportCSV(){
	if (d1_BillList.CurrSQL3 == null || "undefined" == d1_BillList.CurrSQL3) {
		JSPFree.alert("当前无记录！");
		return;
	}
	var dataDt = JSPFree.getBillQueryFormValue(d1_BillQuery).cjrq;
	// 如果超过50w数据,页面提示语
	var sql = d1_BillList.CurrSQL3;
	var new_sql = 'select count(1) c from ('+sql+') t';
	var jso_data = JSPFree.getHashVOsByDS(new_sql, str_ds);
	var c = jso_data[0].c;
	var param = {tabName: tab_name,
			tabNameEn: tab_name_en,
			dsName: str_ds,
			dataSql:d1_BillList.CurrSQL3,
			orgNo: org_no,
			dataDt: dataDt,
			detailType: "EAST", //表类型
			downloadType: "1" ,  //导出类型：1、维护导出，2、错误明细导出，3、填报错误导出，4、填报全量导出，5、已修改记录导出
			fileType: "CSV",
			dataCount: c
	};

	var jso_rt = JSPFree.doClassMethodCall("com.yusys.bione.plugin.detail.download.service.DetailDownloadBS", "getMaxDataNumCsv",{type: "EAST"});
	if (parseInt(c) > parseInt(jso_rt.downloadNum)) {
		var showMsg = JSPFree.doClassMethodCall("com.yusys.bione.plugin.detail.download.service.DetailDownloadBS", "showMsg",param);
		if (showMsg.code == 'success') {
			param.code = 'success';
			maskUtil.mask();
			setTimeout(function () {
				var json = JSPFree.doClassMethodCall("com.yusys.bione.plugin.detail.download.service.DetailDownloadBS", "exportDataThread",param);
				if (json.code='success') {
					$.messager.alert('提示', '导出数据较大，后台异步生成文件中，请稍后前往下载列表下载。', 'warning');
				} else {
					$.messager.alert('提示', json.msg, 'warning');
				}
				maskUtil.unmask();
			}, 100);
		} else if (showMsg.code == "warn") {
			JSPFree.confirm('提示', '当前导出数据已存在,是否重新生成?', function(_isOK){
				if (_isOK) {
					param.code = 'warn';
					param.rid = showMsg.rid;
					maskUtil.mask();
					setTimeout(function () {
						var json = JSPFree.doClassMethodCall("com.yusys.bione.plugin.detail.download.service.DetailDownloadBS", "exportDataThread",param);
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
		maskUtil.mask();
		setTimeout(function () {
			var json = JSPFree.doClassMethodCall("com.yusys.bione.plugin.detail.download.service.DetailDownloadBS", "exportCSV",param);
			if (json.code='success') {
				var filepath = json.data;
				var src = v_context + "/east/export/business/downloadData?filepath=" + filepath;
				var download = $('<iframe id="download" style="display: none;"/>');
				$('body').append(download);
				download.attr('src', src);
			} else {
				$.messager.alert('提示', json.msg, 'warning');
			}
			maskUtil.unmask();
		}, 100);
	}
}


/**
 * 导出excel。
 */
function exportExcel(){
	if (d1_BillList.CurrSQL3 == null || "undefined" == d1_BillList.CurrSQL3) {
		JSPFree.alert("当前无记录！");
		return;
	}

	JSPFree.confirm('提示', '数据量越大,导出等待时间越长,是否导出?', function(_isOK){
		if (_isOK) {
			maskUtil.mask();
			setTimeout(function () {
				var sql = d1_BillList.CurrSQL3;
				var new_sql = 'select count(*) c from ('+sql+') t';
				var jso_data = JSPFree.getHashVOsByDS(new_sql, str_ds);
				var dataDt = JSPFree.getBillQueryFormValue(d1_BillQuery).cjrq;
				var c = jso_data[0].c;
				var json = JSPFree.doClassMethodCall("com.yusys.east.business.service.EastBusinessBS", "downloadData",{tabName: tab_name, tabNameEn: tab_name_en, dsName: str_ds, dataSql:d1_BillList.CurrSQL3, dataCount: c, orgNo: org_no, dataDt: dataDt});
				if (json.code='success') {
					var filepath = json.data;
					var src = v_context + "/east/export/business/downloadData?filepath=" + filepath;
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

/**
 * 导入
 * @return {[type]} [description]
 */
function importExcel() {
	JSPFree.openDialog("文件上传", "/yujs/east/busiModel/east_busi_data_import.js", 500, 300, {
		tab_name: tab_name,
		tab_name_en: tab_name_en
	}, function (_rtdata) {
		if (_rtdata == "success") {
			JSPFree.alert("导入成功！");
		}
	});
}

function downloadBillListCurrSQL3AsExcel(_ds,_billList,_cols) {
	var str_templetcode = _billList.templetVO.templet_option.templetcode; // 模板编码
	var str_templetname = _billList.templetVO.templet_option.templetname; // 模板名称
	var str_sql = _billList.CurrSQL3; //当前SQL
	
	//如果没有定义定段,则从模板取!
	var jsy_cols = [];
	var jsy_aligns = [];
	if(typeof _cols == "undefined" || _cols==null){
		var itemVOs = _billList.templetVO.templet_option_b;
		for(var i=0;i<itemVOs.length;i++){
			if("Y"==itemVOs[i].list_isshow){  //如果列表显示!
				jsy_cols.push(itemVOs[i].itemkey + "/" + itemVOs[i].itemname);
				jsy_aligns.push(itemVOs[i].itemkey + "/" + itemVOs[i].list_align);
			}
		}
	}else{
		jsy_cols = _cols; //使用传入的!
	}
	
	var jso_par = {
		TempletCode : str_templetcode,
		TempletName : str_templetname,
		SQL3 : str_sql,
		Cols : jsy_cols,
		Aligns : jsy_aligns,
		Ds : _ds
	};
	JSPFree.downloadFile(FreeUtil.CommServicePath + "ExportExcelByCurrSQL3DMO", str_templetname + ".xls",jso_par); //
};

/**
 * 导入报文
 */
function onImportReport() {
	JSPFree.openDialog("文件上传", "/yujs/east/report/east_report_import.js", 500, 300, {
		tab_name_en: tab_name_en
	}, function (_rtdata) {
		if (_rtdata == "success") {
			JSPFree.alert("导入成功！");
		}
	});
}