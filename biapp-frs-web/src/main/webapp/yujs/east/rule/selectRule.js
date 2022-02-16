//初始化界面,菜单配置路径是【/frs/yufreejs?js=/yujs/east/importStep1.js】
function AfterInit(){
	JSPFree.createBillList("d1","/biapp-east/freexml/east/rule/east_cr_rule_type_import.xml",["上一步/onCancel/icon-p71","下一步/onNext/icon-p72"]);
	
	var str_sqlWhere = "type_name='空值' or type_name='取值范围' or type_name='格式'";
	JSPFree.queryDataByConditon(d1_BillList, str_sqlWhere); // 锁定规则表查询数据
}

/**
 * 确定
 * @returns
 */
function onNext(){
	var selectData = d1_BillList.datagrid('getSelected');
	if (selectData == null) {
		$.messager.alert('提示', '必须选择一条记录进行操作', 'warning');
		return;
	}
	jso_OpenPars.type_name = selectData.type_name;
	JSPFree.openDialog("选择","/yujs/east/rule/selectCol.js", 700, 560, jso_OpenPars);
}

/**
 * 取消
 * @returns
 */
function onCancel(){
	JSPFree.closeDialog();
}