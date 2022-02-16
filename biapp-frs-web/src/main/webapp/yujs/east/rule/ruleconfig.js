//初始化界面
function AfterInit() {
	JSPFree.createBillList("d1", "/biapp-east/freexml/east/rule/east_cr_rule_CODE1.xml",null,{isSwitchQuery:"N",list_ischeckstyle:"Y",list_ismultisel:"Y"});
}

/**
 * 导入
 * 
 * @returns
 */
function importRule() {
	JSPFree.confirm("提示","规则导入会全量覆盖系统中的校验规则，请谨慎操作，请确认是否执行导入操作！",function(_isOK){
		if(_isOK){
			JSPFree.openDialog("文件上传", "/yujs/east/rule/inputRule.js", 500, 240, null,
			function(_rtdata) {
				if (_rtdata != null && _rtdata != "undefined") {
					if(_rtdata.type == "dirclose"){ //直接关闭窗口
						
					}else{
						JSPFree.alert(_rtdata.msg);
						JSPFree.queryDataByConditon(d1_BillList, _rtdata.whereSQL); // 立即查询刷新数据
					}
				}
			});
		}
	});
}

/**
 * 导出
 * 
 * @returns
 */
function exportRule(_btn) {
	var download = null;
	download = $('<iframe id="download" style="display: none;"/>');
	$('body').append(download);

	var billList = FreeUtil.getBtnBindBillList(_btn);

	var ruleId = JSPFree.getBillQueryFormValue(d1_BillQuery).id;
	var tabName = JSPFree.getBillQueryFormValue(d1_BillQuery).tab_name;
	var ruleName = JSPFree.getBillQueryFormValue(d1_BillQuery).rule_name;
	var reportCd = JSPFree.getBillQueryFormValue(d1_BillQuery).report_cd;
	var reportNo = JSPFree.getBillQueryFormValue(d1_BillQuery).report_no;
	var ruleSource = JSPFree.getBillQueryFormValue(d1_BillQuery).rule_source;

	var src = v_context + "/east/rules/checkRule/download?ruleId=" + ruleId
			+ "&tabName=" + tabName + "&ruleName=" + ruleName + "&reportCd="
			+ reportCd + "&reportNo=" + reportNo + "&ruleSource=" + ruleSource;
	download.attr('src', src);
}

/**
 * 启用
 * 
 * @param _btn
 * @returns
 */
function enable(_btn) {
	var selectDatas = d1_BillList.datagrid('getSelections');
	if (selectDatas.length == 0) {
		$.messager.alert('提示', '至少选择一条记录进行操作', 'warning');
		return;
	}
	var ids = "";
	for (var i=0; i<selectDatas.length; i++) {
		ids += selectDatas[i].id + ",";
	}
	if (ids != "") {
		ids = ids.substring(0,ids.length-1);
	}
	var jso_par = {
		"rule_sts" : "Y",
		"ids" : ids
	};
	var jso_data = JSPFree
			.doClassMethodCall(
					"com.yusys.east.checkrule.detailmaintenance.service.EastCrRuleBS",
					"updateRuleSts", jso_par);
	if (jso_data.success == 1) {
		$.messager.show({
			title : '消息提示',
			msg : '启用成功',
			showType : 'show'
		});
	}
	for (var j=0; j<selectDatas.length; j++) {
		var seleDate = selectDatas[j];
		FreeUtil.refreshBillListOneRowDataByUpdate(d1_BillList, seleDate._rownum, "id='" + seleDate.id + "'");
	}
}

/**
 * 停用
 * 
 * @param _btn
 * @returns
 */
function noenable(_btn) {
	var selectDatas = d1_BillList.datagrid('getSelections');
	if (selectDatas.length == 0) {
		$.messager.alert('提示', '至少选择一条记录进行操作', 'warning');
		return;
	}
	var ids = "";
	for (var i=0; i<selectDatas.length; i++) {
		ids += selectDatas[i].id + ",";
	}
	if (ids != "") {
		ids = ids.substring(0,ids.length-1);
	}
	var jso_par = {
		"rule_sts" : "N",
		"ids" : ids
	};
	var jso_data = JSPFree
			.doClassMethodCall(
					"com.yusys.east.checkrule.detailmaintenance.service.EastCrRuleBS",
					"updateRuleSts", jso_par);
	if (jso_data.success == 1) {
		$.messager.show({
			title : '消息提示',
			msg : '停用成功',
			showType : 'show'
		});
	}
	for (var j=0; j<selectDatas.length; j++) {
		var seleDate = selectDatas[j];
		FreeUtil.refreshBillListOneRowDataByUpdate(d1_BillList, seleDate._rownum, "id='" + seleDate.id + "'");
	}
}

function onView(_this) {
	var dataset = _this.dataset;
	var index = dataset.rowindex;
	var rows = d1_BillList.datagrid("getRows");
	var row = rows[index];//index为行号

	var jso_OpenPars = {
		id : row.id,
		type_cd : row.type_cd
	};
	JSPFree.openDialog("规则查看", "/yujs/east/rule/condrulewarn1.js", 750, 500,
			jso_OpenPars, function(_rtdata) {
			});
}

/**
 * 重启单条检核服务
 */
function restartCheckService(){
	JSPFree.confirm("提示","确定要重启单条检核服务么?",function(_isOK){
		if(_isOK){
			var _rt = JSPFree.doClassMethodCall("com.yusys.east.checkrule.detailmaintenance.service.EastCrRuleBS","restartCheckService",{});
			if (_rt.status == "OK") {
				$.messager.alert('提示', '单条检核服务启动程序调用成功！', 'info');
			} else {
				$.messager.alert('提示', '单条检核服务启动程序调用失败！', 'warning');

			}
		}
	});
}
/*
*//**
 * 配置明细
 * 
 * @returns
 *//*
function configDetail(_btn) {
	var billList = FreeUtil.getBtnBindBillList(_btn);
	if (billList == d1_1_B_BillList) {
		var jso_OpenPars = d1_1_B_BillList.datagrid('getSelected');
		if (jso_OpenPars == null) {
			$.messager.alert('提示', '必须选择一条记录进行操作', 'warning');
			return;
		}

		if (jso_OpenPars.type_cd == "空值" || jso_OpenPars.type_cd == "记录数") {
			$.messager.show({
				title : '消息提示',
				msg : jso_OpenPars.type_cd + '没有明细',
				showType : 'show'
			});
		} else {
			FreeUtil.setClientEnv("config_tab_name", jso_OpenPars["tab_name"]); // 设置配置的表名
			JSPFree.openDialog("配置明细", "/yujs/east/configdetail.js", 900, 450,
					jso_OpenPars, function(_rtdata) {
						if (_rtdata) {
							$.messager.show({
								title : '消息提示',
								msg : '保存成功',
								showType : 'show'
							});
						}
					});
		}
	} else {
		var jso_OpenPars = d1_2_B_BillList.datagrid('getSelected');
		if (jso_OpenPars == null) {
			$.messager.alert('提示', '必须选择一条记录进行操作', 'warning');
			return;
		}

		if (jso_OpenPars.type_cd == "空值" || jso_OpenPars.type_cd == "记录数") {
			$.messager.show({
				title : '消息提示',
				msg : jso_OpenPars.type_cd + '没有明细',
				showType : 'show'
			});
		} else {
			FreeUtil.setClientEnv("config_tab_name", jso_OpenPars["tab_name"]); // 设置配置的表名
			JSPFree.openDialog("配置明细", "/yujs/east/configdetail.js", 900, 450,
					jso_OpenPars, function(_rtdata) {
						if (_rtdata) {
							$.messager.show({
								title : '消息提示',
								msg : '保存成功',
								showType : 'show'
							});
						}
					});
		}
	}
}*/