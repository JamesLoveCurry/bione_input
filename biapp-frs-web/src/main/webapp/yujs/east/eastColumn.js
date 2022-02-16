//初始化界面
function AfterInit() {

	JSPFree.createBillList("d1", "east_cr_column",["确定/onConfirm/icon-p12","取消/onCancel/icon-p15"]); // 页签先左右分割
	var tab_name = jso_OpenPars2.tab_name; // 取得选中记录中的id值
	var str_sqlWhere = "tab_name='" + tab_name + "'"; // 拼SQL条件
	JSPFree.queryDataByConditon(d1_BillList, str_sqlWhere); // 锁定规则表查询数据
}

function onConfirm(){
	var selectDatas = d1_BillList.datagrid('getSelections');
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