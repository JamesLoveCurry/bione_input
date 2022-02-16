//初始化界面,菜单配置路径是【/frs/yufreejs?js=/yujs/east/58table.js】
function AfterInit(){
	JSPFree.createBillList("d1","east/engineTask/east_cr_engine_log_CODE1");
}

// 出报送数据
function download() {
	var json_data = d1_BillList.datagrid('getSelected');
	if (json_data == null) {
		$.messager.alert('提示', '必须选择一条记录进行操作', 'warning');
	}
	
	alert(JSON.stringify(json_data));
}
