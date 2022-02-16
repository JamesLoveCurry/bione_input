//初始化界面
var rid = "";
var org_no = "";
var differ_desc = "";
var addr = "";
function AfterInit() {
	JSPFree.createBillCard("d1","/biapp-east/freexml/east/result/east_cr_result_check_CODE2.xml",["保存/onSave","取消/onCancel"],null);
	rid = jso_OpenPars.rid;
	org_no = jso_OpenPars.org_no;
	differ_desc = jso_OpenPars.differ_desc;
	addr =  jso_OpenPars.addr;
	//赋值
	JSPFree.queryBillCardData(d1_BillCard, "rid = '"+rid+"'");
	JSPFree.setBillCardItemValue(d1_BillCard, "differ_desc", differ_desc);
}

/**
 * 保存
 * @return {[type]} [description]
 */
function onSave(){
	/*var subject_no = JSPFree.getBillCardItemValue(d1_BillCard,"subject_no");
	var curr_cd = JSPFree.getBillCardItemValue(d1_BillCard,"curr_cd");
	differ_desc = JSPFree.getBillCardItemValue(d1_BillCard,"differ_desc");
	var data_dt = JSPFree.getBillCardItemValue(d1_BillCard,"data_dt").replace(/-/g,'');
	// 判断differ_desc是否包含&
	if (differ_desc.indexOf("&") != -1 || differ_desc.indexOf("%") != -1) {
		$.messager.alert('提示', '差异原因解释中不能包含&或者%字符', 'warning');
		return;
	}
	var _rid ;
	if (org_no) {
		_rid = subject_no+"_"+curr_cd+"_"+ data_dt + '_'+org_no;
	} else {
		_rid = subject_no+"_"+curr_cd+"_"+ data_dt + '_'+addr;
	}
	var jso_par = {
		rid:_rid,
		differ_desc:differ_desc
	}
	
	var jso_rt = JSPFree.doClassMethodCall("com.yusys.east.checkresult.summary.service.EastCheckResultBS","setCheckResultSumExplain", jso_par);
	*/
	var flag = JSPFree.doBillCardUpdate(d1_BillCard);
	if(flag){
		JSPFree.closeDialog(flag);
	}
}

function onCancel() {
	JSPFree.closeDialog(null);
}