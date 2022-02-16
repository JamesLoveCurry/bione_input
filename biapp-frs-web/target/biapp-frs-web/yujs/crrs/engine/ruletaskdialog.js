//初始化界面,菜单配置路径是【/frs/yufreejs?js=/yujs/east/importStep1.js】
function AfterInit(){
	JSPFree.createBillList("d1","/crrs/engine/refer_crrs_engine_task_CODE1",["下一步/onNext/icon-p72"]);
}

/**
 * 确定
 * @returns
 */
function onNext(){
	var jso_queryValue = JSPFree.getBillQueryFormValue(d1_BillQuery);
	var str_fromDate = jso_queryValue.data_dt;  //来源日期

	var jsy_datas = JSPFree.getBillListAllDatas(d1_BillList);
	if (jsy_datas == null || jsy_datas.length<=0) {
		$.messager.alert('提示', '表格中没有一条数据,无法复制!<br>必须查询一下保证该日期内有任务数据才可以复制!', 'warning');
		return;
	}
    	
	JSPFree.openDialog("把【" + str_fromDate + "】的任务复制到哪个目标日期","/yujs/east/ruletaskdialogtime.js", 500, 420, {fromDate:str_fromDate});
}

