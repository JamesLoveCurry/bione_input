//业务数据查询【/frs/yufreejs?js=/yujs/east/busidataquery.js】
function AfterInit() {
	JSPFree.createSplitByBtn("d1", "左右", 235, [ "确定/onConfirm/icon-ok",
			"取消/onCancel/icon-clear" ], null);
	JSPFree.createBillTree("d1_A","/biapp-crrs/freexml/crrs/rule/rpt_org_info_ByCrrsDispatch.xml");
	JSPFree.createBillList("d1_B","/biapp-crrs/freexml/crrs/ent/crrs_bs_staff_info_CODE1.xml",null,{autoquery:"N",ishavebillquery:"N"});

	// 树的绑定事件
	JSPFree.bindBillTreeOnSelect(d1_A_BillTree, doQueryRule);
}

// 树的查询事件
function doQueryRule() {
	var treeData = JSPFree.getBillTreeSelectData(d1_A_BillTree);//取得选中的树节点
	var whereSql = "nbjgh = '"+treeData.org_no+"'";
	JSPFree.queryDataByConditon(d1_B_BillList, whereSql);
}

// 确定
function onConfirm() {
	var jso_rowdata = JSPFree.getBillTreeSelectData(d1_A_BillTree);
	var selectData = d1_B_BillList.datagrid('getSelected');
	if (selectData == null) {
		$.messager.alert('提醒', '请选择客户经理!', 'info');
		return;
	} else {
		var returnObj = new Object();
		returnObj.inst_code = jso_rowdata.org_no;
		returnObj.inst_name = jso_rowdata.org_nm;
		returnObj.staff_code = selectData.gyh;
		returnObj.staff_name = selectData.gymc;
		console.log(returnObj);
		JSPFree.closeDialog(returnObj);
	}
}

// 取消
function onCancel() {
	JSPFree.closeDialog(false);
}
