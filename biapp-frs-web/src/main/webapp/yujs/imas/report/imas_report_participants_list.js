/**
 * 默认展示列表
 * @constructor
 */
var org_no = "";
var org_class = "";
function AfterInit() {
	var jso_data = JSPFree.doClassMethodCall("com.yusys.imas.business.service.ImasBusinessBS","checkUserOrgNo",{str_LoginUserOrgNo: str_LoginUserOrgNo});
	var result = jso_data.result;
	if (result == "OK") {
		org_no = jso_data.orgNo;
		org_class = jso_data.orgClass;
	}
	if (org_class == ImasFreeUtil.getImasOrgClass().zh) {
		JSPFree.createBillList("d1","/biapp-imas/freexml/report/imas_cr_report_participants.xml",null,{list_btns:"$INSERT;$UPDATE;$DELETE;[icon-p59]数字证书绑定/bound;[icon-p60]数字证书下载/download;[icon-p70]数字证书查看/viewCert;[icon-p61]清除登录token/removeToken;[icon-p58]证书绑定903参数/view903;[icon-p31]证书下载919参数/view919;",isSwitchQuery:"N",autoquery:"N"});
	} else {
		JSPFree.createBillList("d1","/biapp-imas/freexml/report/imas_cr_report_participants.xml",null,{list_btns:"$INSERT;$UPDATE;$DELETE;[icon-p59]数字证书绑定/bound;[icon-p58]证书绑定903参数/view903;[icon-p61]清除登录token/removeToken;",isSwitchQuery:"N",autoquery:"N"});
	}
	JSPFree.queryDataByConditon(d1_BillList, getCondition());
}
/**
 * 获取辖内机构号的过滤条件，作为sql的一部分
 * @returns
 */
function getCondition() {
	var condition = "";
	var jso_rt = JSPFree.doClassMethodCall("com.yusys.imas.common.service.ImasValidateQueryConditionBS","getReportRptNoCondition",{"_loginUserOrgNo" : str_LoginUserOrgNo});
	if(jso_rt.msg == "ok"){
		condition = jso_rt.data;
	}

	return condition;
}
/**
 * 绑定证书
 */
function bound() {
	var selectDatas = d1_BillList.datagrid('getSelections');
	if (selectDatas.length == 0) {
		$.messager.alert('提示', '至少选择一条记录进行操作', 'info');
		return;
	} else if (selectDatas.length > 1) {
		$.messager.alert('提示', '只能选择一条记录进行操作', 'info');
		return;
	}
	var json_data = selectDatas[0];
	JSPFree.openDialog("绑定证书","/yujs/imas/report/imas_report_bound.js",400,350,{financeOrgNo:json_data.finance_org_no, orgNo: json_data.rpt_org_no },function(_rtdata){
		if (_rtdata != null) {
			if (_rtdata.code == "success") {
				$.messager.alert('提示', '操作成功!', 'info');
				JSPFree.queryDataByConditon(d1_BillList, null);  // 立即查询刷新数据
			} else if ("dirclose" == _rtdata.type) {
				return;
			} else {
				$.messager.alert('提示', _rtdata.msg, 'info');
			}

		}
	});
}

/**
 * 清除token
 */
function removeToken() {
	var selectDatas = d1_BillList.datagrid('getSelections');
	if (selectDatas.length == 0) {
		$.messager.alert('提示', '至少选择一条记录进行操作', 'info');
		return;
	} else if (selectDatas.length > 1) {
		$.messager.alert('提示', '只能选择一条记录进行操作', 'info');
		return;
	}
	JSPFree.confirm('提示', '请确保当前token无效,清除token后,首次上传会生成新的token,不要频繁的操作token,是否确认清除?', function(_isOK){
		if(_isOK) {
			var json_data = selectDatas[0];
			var jso_par = {"orgNo" : json_data.rpt_org_no}
			var jso_rt = JSPFree.doClassMethodCall("com.yusys.imas.report.service.ImasSendReportBS","removeToken",jso_par);
			if (jso_rt.code == "success") {
				$.messager.alert('提示', '操作成功!', 'info');
			} else {
				$.messager.alert('提示', jso_rt.msg, 'info');
			}
		}
	})

}
/**
 * 919报文
 */
function download() {
	JSPFree.confirm('提示', '你真的要下载DEMP的证书吗?', function(_isOK){
		if(_isOK) {
			var jso_par = {"loginUserOrgNo" : str_LoginUserOrgNo}
			var jso_rt = JSPFree.doClassMethodCall("com.yusys.imas.report.service.ImasSendReportBS","download",jso_par);

			if (jso_rt.code == "success") {
				$.messager.alert('提示', '操作成功!', 'info');
				JSPFree.queryDataByConditon(d1_BillList, null);  // 立即查询刷新数据
			} else {
				$.messager.alert('提示', jso_rt.msg, 'info');
			}
		}
	})

}
function viewCert() {
	var jso_par = {"loginUserOrgNo" : str_LoginUserOrgNo}
	var jso_rt = JSPFree.doClassMethodCall("com.yusys.imas.report.service.ImasSendReportBS","viewCert",jso_par);

	if (jso_rt.code == "success") {
		FreeUtil.openHtmlMsgBox("查看数字签名", 800, 500, "<span style='font-size:14px;display:block;overflow-wrap:break-word;'>" + jso_rt.data + "</span>");
	} else {
		$.messager.alert('提示', jso_rt.msg, 'info');
	}
}

function view903(){
	var selectDatas = d1_BillList.datagrid('getSelections');
	if (selectDatas.length == 0) {
		$.messager.alert('提示', '至少选择一条记录进行操作', 'info');
		return;
	} else if (selectDatas.length > 1) {
		$.messager.alert('提示', '只能选择一条记录进行操作', 'info');
		return;
	}
	var rid = selectDatas[0].rid;
	var jso_rt = JSPFree.doClassMethodCall("com.yusys.imas.report.service.ImasSendReportBS","viewParam",{rid:rid});

	if (jso_rt.code == "success") {
		FreeUtil.openHtmlMsgBox("查看参数", 800, 500, "<span style='font-size:14px;display:block;overflow-wrap:break-word;'>请求参数：<br/>" + jso_rt.data + "</span>");
	} else {
		$.messager.alert('提示', jso_rt.msg, 'info');
	}
}

function view919(){
	var selectDatas = d1_BillList.datagrid('getSelections');
	if (selectDatas.length == 0) {
		$.messager.alert('提示', '至少选择一条记录进行操作', 'info');
		return;
	} else if (selectDatas.length > 1) {
		$.messager.alert('提示', '只能选择一条记录进行操作', 'info');
		return;
	}
	var rid = selectDatas[0].rid;
	var jso_rt = JSPFree.doClassMethodCall("com.yusys.imas.report.service.ImasSendReportBS","viewParam919",{rid:rid});

	if (jso_rt.code == "success") {
		FreeUtil.openHtmlMsgBox("查看参数", 800, 500, "<span style='font-size:14px;display:block;overflow-wrap:break-word;'>请求参数：<br/>" + jso_rt.data + "</span>");
	} else {
		$.messager.alert('提示', jso_rt.msg, 'info');
	}
}