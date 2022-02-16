//初始化界面
function AfterInit() {

	JSPFree.createSplitByBtn("d1", "左右", 420,["确定/onConfirm/icon-p12","取消/onCancel/icon-p15"]); // 页签先左右分割

	JSPFree.createBillList("d1_A", "east/busiModel/east_cr_table"); // 第1个页签左边
	JSPFree.createBillList("d1_B", "east/busiModel/east_cr_column"); // 第1个页签右边的上边
	// 绑定表格选择事件,d1_A_BillList会根据命名规则已创建
	JSPFree.bindSelectEvent(d1_A_BillList, function(rowIndex, rowData) {
		var tab_name = rowData.tab_name; // 取得选中记录中的id值
		var str_sqlWhere = "tab_name='" + tab_name + "'"; // 拼SQL条件
		JSPFree.queryDataByConditon(d1_B_BillList, str_sqlWhere); // 锁定规则表查询数据
	});
}

function onConfirm(){
	var selectDatas = d1_B_BillList.datagrid('getSelections');
	if (selectDatas.length == 0) {
		$.messager.alert('提示', '至少选择一条记录进行操作', 'warning');
		return;
	}
	var col_name="";
	var tab_name =selectDatas[0].tab_name;
	for(var i=0;i<selectDatas.length;i++){
		if(i==selectDatas.length-1)
			col_name += selectDatas[i].col_name
		else
			col_name += selectDatas[i].col_name+","
	}
	JSPFree.closeDialog({col_name:col_name,tab_name:tab_name});
}

function onCancel(){
	JSPFree.closeDialog();
}