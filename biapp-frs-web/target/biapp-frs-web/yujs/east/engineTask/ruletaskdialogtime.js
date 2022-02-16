//初始化界面,菜单配置路径是【/frs/yufreejs?js=/yujs/east/importStep1.js】
var str_fromDate ='';  //来源日期

function AfterInit(){
	str_fromDate = jso_OpenPars.fromDate;  //来源日期
	JSPFree.createBillCard("d1","east/engineTask/rule_task_time",["上一步/onBefore/icon-p71","确认/onNext/icon-p01","取消/onCancel/icon-no"]);	
}

/**
 * 确定
 * @returns
 */
function onNext(){
	var jso_cardValue = JSPFree.getBillCardFormValue(d1_BillCard);  //
	var str_toDate = jso_cardValue.new_time;  //新的日期
	
	if (str_toDate == null||str_toDate=="") {
		JSPFree.alert('必须输入要生成的数据日期操作');
		return;
	}

	if(str_fromDate==str_toDate){
		JSPFree.alert('来源日期与目标日期不能相同,现在两个都是【' + str_fromDate + '】');
		return;
	}

	JSPFree.confirm("提示","复制【" + str_fromDate + "】的任务到【" + str_toDate + "】，请确认!",function(_isOK){
		if(_isOK){
			var jso_par = {fromDate : str_fromDate, toDate : str_toDate};
			var jso_data = JSPFree.doClassMethodCall("com.yusys.east.checkrule.detailmaintenance.service.EastCrTaskBatchBS","batchRuleCreate", jso_par);
			if(jso_data.code == "OK"){
			   JSPFree.closeDialogAll(jso_data);
			} else {
				JSPFree.alert(jso_data.msg);
			}
		}
	});
}

/**
 * 取消
 * @returns
 */
function onBefore(){
	JSPFree.closeDialog();
}

function onCancel(){
	JSPFree.closeDialogAll();
}