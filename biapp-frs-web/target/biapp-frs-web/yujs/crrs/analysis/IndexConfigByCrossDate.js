//初始化界面,菜单配置路径是【/frs/yufreejs?js=/yujs/crrs/xxx/xxx.js】
function AfterInit(){
	JSPFree.createTabb("d1", [ "报表统计数统计配置", "报表指标项统计配置"],[150,150]);
	JSPFree.createBillList("d1_1","/biapp-crrs/freexml/crrs/summary/crrs_index_config_bydate_CODE1.xml",null,null);
	JSPFree.createBillList("d1_2","/biapp-crrs/freexml/crrs/summary/crrs_index_config_bydate_CODE2.xml",null,null);
}

/**
 * 新增
 * @return {[type]} [description]
 */
function insert1(){
	JSPFree.openDialog("报表统计数统计配置","/yujs/crrs/analysis/addTabAnalysisConfig.js", 900, 560, null,function(_rtdata){
		if (_rtdata == true) {
			JSPFree.queryDataByConditon(d1_1_BillList, null);  //立即查询刷新数据
		}
	});
}

/**
 * 新增
 * @return {[type]} [description]
 */
function insert2(){
	JSPFree.openDialog("报表指标项统计配置","/yujs/crrs/analysis/addIndexAnalysisConfig.js", 900, 560, null,function(_rtdata){
		if (_rtdata == true) {
			JSPFree.queryDataByConditon(d1_2_BillList, null);  //立即查询刷新数据
		}
	});
}

/**
 * 编辑
 * @return {[type]} [description]
 */
function update1(){
	var selectData = d1_1_BillList.datagrid('getSelected');
	if (selectData == null) {
		$.messager.alert('提示', '必须选择一条记录进行操作', 'warning');
		return;
	}
	
	JSPFree.openDialog("报表统计数统计配置","/yujs/crrs/analysis/editTabAnalysisConfig.js", 900, 560, {rid : selectData.rid},function(_rtdata){
		if (_rtdata == true) {
			JSPFree.queryDataByConditon(d1_1_BillList, null);  //立即查询刷新数据
		}
	});
}

/**
 * 编辑
 * @return {[type]} [description]
 */
function update2(){
	var selectData = d1_2_BillList.datagrid('getSelected');
	if (selectData == null) {
		$.messager.alert('提示', '必须选择一条记录进行操作', 'warning');
		return;
	}
	
	JSPFree.openDialog("报表指标项统计配置","/yujs/crrs/analysis/editIndexAnalysisConfig.js", 900, 560, {rid : selectData.rid},function(_rtdata){
		if (_rtdata == true) {
			JSPFree.queryDataByConditon(d1_2_BillList, null);  //立即查询刷新数据
		}
	});
}