/**
 * 
 * <pre>
 * Title: 【报送管理】-【报文生成】-【查看分片日志】
 * Description: 报送管理-报文生成-查看日志：主页面
 * 此页面提供了报送管理的查看日志相关操作，可以允许查看生成报文的日志
 * </pre>
 * @author zhangzw9 
 * @version 1.00.00
   @date 2021年4月27日
 */

var str_rid = null;
var str_sql = null;
var orgNo = ""
function AfterInit(){
	str_rid = jso_OpenPars.rid;
	str_sql = "rep_rid='" + str_rid + "'";
	var json = JSPFree.doClassMethodCall("com.yusys.imas.report.service.ImasSendReportBS","isDirect",{_loginUserOrgNo:str_LoginUserOrgNo});
	if (json.code == 'success') {
		JSPFree.createBillList("d1","/biapp-imas/freexml/report/imas_cr_report_separate.xml", null ,{refWhereSQL:str_sql}); //创建列表
	} else {
		JSPFree.createBillList("d1","/biapp-imas/freexml/report/imas_cr_report_not_direct_separate.xml", null ,{refWhereSQL:str_sql}); //创建列表

	}

	JSPFree.queryDataByConditon2(d1_BillList,str_sql);
	orgNo = jso_OpenPars.orgNo;
}
/*
 * 上传报文
 */
function uploadOneFile() {
	/*var selectDatas = d1_BillList.datagrid('getSelections');
	if (selectDatas.length == 0) {
		$.messager.alert('提示', '至少选择一条记录进行操作', 'info');
		return;
	}
	var jso_rids = new Array();
	for (var i=0; i<selectDatas.length; i++) {
		jso_rids.push(selectDatas[i].rid);
	}
	JSPFree.confirm('提示', '你真的要上传选中的报文吗?', function(_isOK){
		if(_isOK) {
			var jso_rt = JSPFree.doClassMethodCall("com.yusys.imas.report.service.ImasSendReportBS","oneReportData",{"allRids": jso_rids, orgNo: orgNo});
			JSPFree.openDialog2("任务监控","/yujs/imas/report/imas_send_separate_start.js",750,350,null);
		}
	})*/
	var selectDatas = d1_BillList.datagrid('getSelections');
	if (selectDatas.length == 0) {
		$.messager.alert('提示', '至少选择一条记录进行操作', 'info');
		return;
	} else if (selectDatas.length > 1) {
		$.messager.alert('提示', '只能选择一条记录进行操作', 'info');
		return;
	}
	JSPFree.confirm('提示', '你真的要上传选中的报文吗?', function(_isOK){
		if(_isOK) {
			var jso_rt = JSPFree.doClassMethodCall("com.yusys.imas.report.service.ImasSendReportBS","oneReportData",{"rid": selectDatas[0].rid, orgNo: orgNo});
			if (jso_rt.code == 'success') {
				$.messager.alert('提示', '上传成功!', 'info');
			} else {
				$.messager.alert('提示', jso_rt.msg, 'info');
			}
			JSPFree.queryDataByConditon(d1_BillList, str_sql);  // 立即查询刷新数据
		}
	})


}

/**
 * 任务监控
 */
function showStatus() {
	JSPFree.openDialog2("任务监控","/yujs/imas/report/imas_send_separate_start.js",750,350,null);
}
/**
 * 查询报文状态
 */
function searchStatus() {
	var selectDatas = d1_BillList.datagrid('getSelections');
	if (selectDatas.length == 0) {
		$.messager.alert('提示', '至少选择一条记录进行操作', 'info');
		return;
	} else if (selectDatas.length > 1) {
		$.messager.alert('提示', '只能选择一条记录进行操作', 'info');
		return;
	}
	var jso_rt = JSPFree.doClassMethodCall("com.yusys.imas.report.service.ImasSendReportBS","searchStatus",{"rid": selectDatas[0].rid, orgNo: orgNo});
	$.messager.alert('提示', jso_rt.data, 'info');
}

function view303(){
	var single = d1_BillList.datagrid('getSelected');
	if (single == null) {
		$.messager.alert('提示', '必须选择一条记录进行操作', 'warning');
		return;
	}
	
	var rid = single.rid;
	let jso_rt = JSPFree.doClassMethodCall("com.yusys.imas.report.service.ImasSendReportBS", "getSepParams",{rid:rid,type:"1"});

	if (jso_rt.code == "success") {
		FreeUtil.openHtmlMsgBox("查看参数", 800, 300, "<span style='font-size:14px;display:block;overflow-wrap:break-word;'>303参数：<br/>" + jso_rt.data + "</span>");
	} else {
		$.messager.alert('提示', jso_rt.msg, 'info');
	}
}

function view307(){
	var single = d1_BillList.datagrid('getSelected');
	if (single == null) {
		$.messager.alert('提示', '必须选择一条记录进行操作', 'warning');
		return;
	}
	
	var rid = single.rid;
	let jso_rt = JSPFree.doClassMethodCall("com.yusys.imas.report.service.ImasSendReportBS", "getSepParams",{rid:rid,type:"2"});

	if (jso_rt.code == "success") {
		FreeUtil.openHtmlMsgBox("查看参数", 800, 300, "<span style='font-size:14px;display:block;overflow-wrap:break-word;'>307参数：<br/>" + jso_rt.data + "</span>");
	} else {
		$.messager.alert('提示', jso_rt.msg, 'info');
	}
}