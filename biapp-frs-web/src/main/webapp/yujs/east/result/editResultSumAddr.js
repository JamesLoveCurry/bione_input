//初始化界面
var addr = "";
var fail_reason = "";
var checkNo = "";
var dataDt = "";
function AfterInit() {
	JSPFree.createBillCard("d1","/biapp-east/freexml/east/result/east_cr_result_check_CODE2.xml",["保存/onSave","取消/onCancel"],null);
	addr =  jso_OpenPars.addr;
	fail_reason = jso_OpenPars.fail_reason;
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
	var subject_no = JSPFree.getBillCardItemValue(d1_BillCard,"subject_no");
	var curr_cd = JSPFree.getBillCardItemValue(d1_BillCard,"curr_cd");
	differ_desc = JSPFree.getBillCardItemValue(d1_BillCard,"differ_desc");
	var data_dt = JSPFree.getBillCardItemValue(d1_BillCard,"data_dt").replace(/-/g,'');
	// 判断differ_desc是否包含&
	if (differ_desc.indexOf("&") != -1 || differ_desc.indexOf("%") != -1) {
		$.messager.alert('提示', '差异原因解释中不能包含&或者%字符', 'warning');
		return;
	}
	var _rid = subject_no+"_"+curr_cd+"_"+ data_dt + '_'+addr;

	var jso_par = {
		rid:_rid,
		differ_desc:differ_desc
	}
	
	var jso_rt = JSPFree.doClassMethodCall("com.yusys.east.checkresult.summary.service.EastCheckResultBS","setCheckResultSumExplain", jso_par);
	JSPFree.closeDialog(true);
}

function onCancel() {
	JSPFree.closeDialog(null);
}