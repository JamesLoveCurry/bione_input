//初始化界面,菜单配置路径是【/frs/yufreejs?js=/yujs/east/importStep1.js】
function AfterInit(){
	JSPFree.createBillList("d1","/biapp-east/freexml/east/rule/east_cr_col_ref.xml",["上一步/onBefore/icon-p71","确认/onNext/icon-ok","取消/onCancel/icon-no"],{list_ispagebar:"N"});
	var str_sqlWhere = "tab_name='"  + jso_OpenPars.tab_name + "' and col_name not in (select col_name from east_cr_rule where tab_name = '"+jso_OpenPars.tab_name+"' and type_cd = '"+jso_OpenPars.type_name+"')";  //拼SQL条件
	JSPFree.queryDataByConditon(d1_BillList,str_sqlWhere);  //锁定规则表查询数据
}

/**
 * 确定
 * @returns
 */
function onNext(){
	var selectDatas = d1_BillList.datagrid('getSelections');
	if (selectDatas.length == 0) {
		$.messager.alert('提示', '必须选择一条记录进行操作', 'warning');
		return;
	}

	var jso_par = {eastCrCols : selectDatas, typeCd : jso_OpenPars.type_name};
	var jso_data = JSPFree.doClassMethodCall("com.yusys.east.checkrule.detailmaintenance.service.EastCrRuleBS","batchInsert",jso_par);
	if(jso_data.success == 1){
		JSPFree.closeDialogAll(jso_data);
	}
}

/**
 * 取消
 * @returns
 */
function onBefore(){
	JSPFree.closeDialog();
}

function onCancel(){
	JSPFree.closeDialogAll();
}