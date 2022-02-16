//校验规则维护..
function AfterInit(){
	JSPFree.createTabb("d1",["确定性校验","一致性校验","提示性校验"]);

	JSPFree.createBillList("d1_1","/biapp-crrs/freexml/crrs/rule/crrs_rule_Sure.xml",null,{isSwitchQuery:"N"}); //确定性校验
	JSPFree.createBillList("d1_2","/biapp-crrs/freexml/crrs/rule/crrs_rule_Consistency.xml",null,{isSwitchQuery:"N"}); //一致性校验
	JSPFree.createBillList("d1_3","/biapp-crrs/freexml/crrs/rule/crrs_rule_Warn.xml",null,{isSwitchQuery:"N"}); //提示性校验

}

//导入规则
function onImportRule(){
	JSPFree.alert("选择一个Excel,并导入校验规则!<br>系统开发中。。。");
}

/**
 * 启用
 * @param _btn
 * @returns
 */
function enable(_btn){
	var billList = FreeUtil.getBtnBindBillList(_btn);
	var seleDate = null;
	seleDate = billList.datagrid('getSelected');
	if (seleDate == null) {
		$.messager.alert('提示', '必须选择一条记录进行操作', 'warning');
		return;
	}
	if(seleDate.isopen == "Y"){
		$.messager.alert('提示', '该规则已启用，无须操作', 'warning');
		return;
	}
	var jso_par ={"isopen":"Y", "rid":seleDate.rid};
	var jso_data = JSPFree.doClassMethodCall("com.yusys.crrs.rulevalidate.service.CrrsRuleBS","updateRuleSts",jso_par);
	if(jso_data.success == 1){
		$.messager.show({title:'消息提示',msg: '启用成功',showType:'show'});
	}
	FreeUtil.refreshBillListOneRowDataByUpdate(billList,seleDate._rownum,"rid='"+seleDate.rid+"'");
}

/**
 * 停用
 * @param _btn
 * @returns
 */
function noenable(_btn){
	var billList = FreeUtil.getBtnBindBillList(_btn);
	var seleDate = null;
	seleDate = billList.datagrid('getSelected');

	if (seleDate == null) {
		$.messager.alert('提示', '必须选择一条记录进行操作', 'warning');
		return;
	}
	if(seleDate.isopen == "N"){
		$.messager.alert('提示', '该规则已停用，无须操作', 'warning');
		return;
	}
	var jso_par ={"isopen":"N", "rid":seleDate.rid};
	var jso_data = JSPFree.doClassMethodCall("com.yusys.crrs.rulevalidate.service.CrrsRuleBS","updateRuleSts",jso_par);
	if(jso_data.success == 1){
		$.messager.show({title:'消息提示',msg: '停用成功',showType:'show'});
	}
	FreeUtil.refreshBillListOneRowDataByUpdate(billList,seleDate._rownum,"rid='"+seleDate.rid+"'");
}
