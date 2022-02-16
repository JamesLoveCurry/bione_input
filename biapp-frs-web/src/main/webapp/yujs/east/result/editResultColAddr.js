//初始化界面
var fail_reason = "";
var addr = "";
var checkNo = "";
var dataDt = "";
function AfterInit() {
	JSPFree.createBillCard("d1","/biapp-east/freexml/east/result/east_cr_result_check_CODE1.xml",["保存/onSave","取消/onCancel"],null);
	fail_reason = jso_OpenPars.fail_reason;
	addr = jso_OpenPars.addr;
	checkNo = jso_OpenPars.checkNo;
	dataDt =  jso_OpenPars.dataDt;
	//赋值
	JSPFree.queryBillCardData(d1_BillCard, "data_dt = '"+dataDt+"' and check_no='" + checkNo + "'");
	JSPFree.setBillCardItemValue(d1_BillCard, "fail_reason", fail_reason);
}

/**
 * 保存
 * @return {[type]} [description]
 */
function onSave(){
	var check_no = JSPFree.getBillCardItemValue(d1_BillCard,"check_no");
	fail_reason = JSPFree.getBillCardItemValue(d1_BillCard,"fail_reason");
	var data_dt = JSPFree.getBillCardItemValue(d1_BillCard,"data_dt").replace(/-/g,'');
	// 判断fail_reason是否包含&
	if (fail_reason.indexOf("&") != -1 || fail_reason.indexOf("%") != -1) {
		$.messager.alert('提示', '错误数据原因解释中不能包含&或者%字符', 'warning');
		return;
	}
	var _rid = check_no+"_"+ data_dt + '_'+addr;
	var jso_par = {
		rid:_rid,
		fail_reason:fail_reason
	}
	var jso_rt = JSPFree.doClassMethodCall("com.yusys.east.checkresult.summary.service.EastCheckResultBS","setCheckResultColExplain", jso_par);
	JSPFree.closeDialog(true);
}

function onCancel() {
	JSPFree.closeDialog(null);
}