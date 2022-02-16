/**
 * 
 * <pre>
 * Title: 【引擎管理】
 * Description:【检核任务】--表任务--复制任务--下一步
 * </pre>
 * @author wangxy31 
 * @version 1.00.00
   @date 2020年8月17日
 */

var str_fromDate ='';  // 来源日期
function AfterInit() {
	str_fromDate = jso_OpenPars.fromDate;  // 来源日期
	JSPFree.createBillCard("d1", "/biapp-bfd/freexml/engineTask/bfd_engine_task_data_ref.xml", ["上一步/onBefore/icon-p71","确认/onNext/icon-p01","取消/onCancel/icon-no"]);
}

/**
 * 【确定】按钮
 * @returns
 */
function onNext() {
	var jso_cardValue = JSPFree.getBillCardFormValue(d1_BillCard);
	var str_toDate = jso_cardValue.new_time;  // 新的日期
	
	if (str_toDate == null||str_toDate == "") {
		JSPFree.alert('必须输入要生成的数据日期操作');
		return;
	}

	if(str_fromDate == str_toDate){
		JSPFree.alert('来源日期与目标日期不能相同，现在两个都是【' + str_fromDate + '】');
		return;
	}

	JSPFree.confirm("提示","复制【" + str_fromDate + "】的任务到【" + str_toDate + "】，请确认!",function(_isOK){
		if (_isOK) {
			var jso_par = {fromDate : str_fromDate, toDate : str_toDate};
			var jso_data = JSPFree.doClassMethodCall("com.yusys.bfd.engineTask.service.BfdEngieTaskBS", "batchTabCreate", jso_par);
			if (jso_data.code == "OK") {
			   JSPFree.closeDialogAll(jso_data);
			}
		}
	});
}

/**
 * 【上一步】按钮
 * @returns
 */
function onBefore() {
	JSPFree.closeDialog();
}

/**
 * 【取消】按钮
 * @returns
 */
function onCancel() {
	JSPFree.closeDialogAll();
}