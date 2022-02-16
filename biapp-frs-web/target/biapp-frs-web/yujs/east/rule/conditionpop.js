//初始化界面
function AfterInit() {
	var tab_name = jso_OpenPars2.tab_name;
	var precond= jso_OpenPars2.cond;
	JSPFree.createSplitByBtn("d1", "左右", 420,["确定/onConfirm/icon-p12","清空/onClean/icon-p15","取消/onCancel/icon-p15"]); // 页签先左右分割
	
	//JSPFree.createSplit("d1", "左右", 420); // 页签先左右分割
	
	JSPFree.createBillList("d1_A", "/biapp-east/freexml/east/report/precondition_east_cr_column.xml"); // 第1个页签左边
	JSPFree.createBillCard("d1_B", "/biapp-east/freexml/east/report/precondition_east_cr_column2.xml"); // 第1个页签右边的上边
	//赋值旧的值
	JSPFree.setBillCardValues(d1_B_BillCard,{cons: precond});
	
	var str_sqlWhere = "tab_name='" + tab_name + "'"; // 拼SQL条件

	JSPFree.queryDataByConditon(d1_A_BillList, str_sqlWhere); // 锁定规则表查询数据

	JSPFree.addBillListDoubleClick(d1_A_BillList,function(_rowIndex,_data){
		console.log(_data);
		var jso_data = JSPFree.getBillCardFormValue(d1_B_BillCard);
		var str_oldcons = jso_data.cons;
		
		var str_newcons = str_oldcons + _data.col_name;
		str_newcons = str_newcons+"=";
		str_newcons = str_newcons+"\"";
		str_newcons = str_newcons+"\"";
		str_newcons = str_newcons+",";
		JSPFree.setBillCardValues(d1_B_BillCard,{cons: str_newcons});
	});
}

function onConfirm(){
	//var selectDatas = d1_b_BillCard.datagrid('getSelections');
	var selectDatas = JSPFree.getBillCardFormValue(d1_B_BillCard);
	if (selectDatas == null) {
		$.messager.alert('提示', '至少选择一条记录进行操作', 'warning');
		return;
	}
	
	JSPFree.closeDialog({col_name:selectDatas.cons});
}

function onCancel(){
	JSPFree.closeDialog();
}
function onClean(){
	JSPFree.setBillCardValues(d1_B_BillCard,{cons: null});
}