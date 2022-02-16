function AfterInit(){
	JSPFree.createBillList("d1","/biapp-crrs/freexml/crrs/supervisehandle/crrs_cross_bank_Consistency.xml",null,{isSwitchQuery:"N"});  //确定性校验
}

/**
 * 导入跨行一致性txt
 * @return {[type]} [description]
 */
function importCrossBankConsistencyFun(){
	JSPFree.openDialog("文件上传","/yujs/crrs/crossbank/importCrossBankConsistency.js", 500, 240, null,function(_rtdata){
		JSPFree.queryDataByConditon(d1_BillList,null);  //立即查询刷新数据
	});
}

/**
 * 导出选中
 * @returns
 */
function downloadSelected(){
	var jsy_datas = JSPFree.getBillListSelectDatas(d1_BillList);
	var _sql = "SELECT * FROM CRRS_CROSS_BANK_CONSISTENCY WHERE 1=1 AND RULERID IN (";
	
	if (jsy_datas==null || jsy_datas.length<=0) {
		$.messager.alert('提示', '必须选择一条记录进行操作', 'warning');
		return;
	} else{
		for(var i=0;i<jsy_datas.length;i++){
			if(i==jsy_datas.length-1){
				_sql += "'"+jsy_datas[i]["rulerid"]+"')";
			} else{
				_sql += "'"+jsy_datas[i]["rulerid"]+"',";
			}
		}
	}
	JSPFree.downloanDataBySQLAsExcel(null, d1_BillList, _sql, "24-CrossBankConsistencyInfo.xls", "客户风险报送跨行一致性校验结果汇总表.xls","1");
}

/**
 * 导出查询结果记录
 * @returns
 */
function downloadQuery(){
	if (d1_BillList.CurrSQL == null || "undefined" == d1_BillList.CurrSQL) {
	JSPFree.alert("当前无查询记录！");
		return;
	}
	JSPFree.downloanDataBySQLAsExcel(null, d1_BillList, d1_BillList.CurrSQL, "24-CrossBankConsistencyInfo.xls", "客户风险报送跨行一致性校验结果汇总表.xls","1");
}
