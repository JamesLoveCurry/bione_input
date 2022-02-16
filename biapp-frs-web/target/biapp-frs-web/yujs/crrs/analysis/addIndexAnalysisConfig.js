//初始化界面
function AfterInit() {
	JSPFree.createBillCard("d1","/biapp-crrs/freexml/crrs/summary/crrs_index_config_bydate_CODE2.xml",["保存/onSave/icon-p21","取消/onCancel/icon-undo"],null);
}

/**
 * 保存
 * @return {[type]} [description]
 */
function onSave(){
	var str_sql = JSPFree.getBillCardItemValue(d1_BillCard,"index_sql").trim();
	
	if(!str_sql.startsWith("select")){
		JSPFree.alert("统计SQL必须以select开头，请重新填写！");
		return;
	}

	if(str_sql.indexOf("index_count")==-1){
		if(str_sql.indexOf("index_sum")==-1){
			JSPFree.alert("统计SQL必须包含index_count或者index_sum，请重新填写！");
			return;
		}
	}
	if(str_sql.indexOf("index_sum")==-1){
		if(str_sql.indexOf("index_count")==-1){
			JSPFree.alert("统计SQL必须包含index_count或者index_sum，请重新填写！");
			return;
		}
	}
	
	if(str_sql.indexOf("from")==-1 || str_sql.indexOf("where")==-1){
		JSPFree.alert("统计SQL必须包含from和where，请重新填写！");
		return;
	}
	if(str_sql.indexOf("data_dt")!=-1){ //如果包含data_dt字段，则必须匹配【Date】
		if(str_sql.indexOf("data_dt=【Date】")==-1){
			JSPFree.alert("统计SQL中数据日期必须写成data_dt=【Date】，请重新填写！");
			return;
		}
	}
	
	var _sql = str_sql.replace(/【Date】/g,"'20200101'"); //随便替换一个日期去测试填写的sql
	var jso_result = JSPFree.doClassMethodCall("com.yusys.crrs.monitor.service.CrrsAnalyseBSDMO", "checkSql", {SQL:_sql});
	if(jso_result.Code == -999){
		JSPFree.alert("统计SQL执行异常，请检查后重新填写！");
		return;
	}
	
	JSPFree.setBillCardValues(d1_BillCard,{rid:FreeUtil.getUUIDFromServer(),index_type:'报表指标项'});
	var flag = JSPFree.doBillCardInsert(d1_BillCard);
	if(flag){
		JSPFree.closeDialog(flag);
	}
}

function onCancel() {
	JSPFree.closeDialog(null);
}