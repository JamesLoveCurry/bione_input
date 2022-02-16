var tab_name = "";
var tab_name_en = "";
var org_no = "";
var org_class = "";
var org_nm = "";
function AfterInit(){
	//通过当前登录人所属内部机构获取报送机构号
	var jso_report_org = JSPFree.doClassMethodCall("com.yusys.imas.common.service.ImasValidateQueryConditionBS","getReportOrgNoCondition",{_loginUserOrgNo:str_LoginUserOrgNo});
	if(jso_report_org.msg=="ok"){
		org_no = jso_report_org.data;
	}
	org_class = ImasFreeUtil.getOrgClass(org_no); //获取机构层级
	org_nm = ImasFreeUtil.getOrgNm(org_no); //获取机构名称

	JSPFree.createBillList("d1", "/biapp-imas/freexml/report/imas_report_receipt.xml",null,{isSwitchQuery:"N",autoquery:"N"});

	JSPFree.queryDataByConditon2(d1_BillList, getCondition());
	// 如果当前机构为报送机构，则设置默认值，否则不设置值
	if (org_no) {
		var result = JSPFree.doClassMethodCall("com.yusys.imas.common.service.ImasValidateQueryConditionBS", "checkOrgIsReportOrg", {
			rptOrgNo: org_no,
			isSingle: false
		});
		if (result.result) {
			FreeUtil.loadBillQueryData(d1_BillList, {org_no: result.result});
		}
	}

}

/**
 * 页面加载结束后，对查询框中的机构下拉框进行处理
 * @returns
 */
function AfterBodyLoad(){
	JSPFree.setBillQueryItemEditable("status", "下拉框", false);
}
/**
 * 获取辖内机构号的过滤条件，作为sql的一部分
 * @returns
 */
function getCondition() {
	var condition = "";
	var jso_rt = JSPFree.doClassMethodCall("com.yusys.imas.common.service.ImasValidateQueryConditionBS","getQueryCondition",{"_loginUserOrgNo" : str_LoginUserOrgNo});
	if(jso_rt.msg == "ok"){
		condition = jso_rt.condition;
	}

	return condition;
}

function onView(){
	var single = d1_BillList.datagrid('getSelected');
	if (single == null) {
		$.messager.alert('提示', '必须选择一条记录进行操作', 'warning');
		return;
	}
	var rid = single.rid;
	let res_remark = single.res_remark;
	let sub_remark = single.sub_remark;
	let detail = single.detail;
	let appendix = single.appendix;
	if(!appendix){
		JSPFree.alert("暂无明细信息");
		return;
	}
	var jso_OpenPars = {rid:rid};
	JSPFree.openDialog("查询明细","/yujs/imas/report/imas_report_receipt_detail.js", 950, 500, jso_OpenPars, function(_rtdata){});
}

function download(_btn){
	var single = d1_BillList.datagrid('getSelected');
	if (single == null) {
		$.messager.alert('提示', '必须选择一条记录进行操作', 'warning');
		return;
	}
	
	var rid = single.rid;
	var zipUrl = single.zip_url;
	if(!zipUrl){
		JSPFree.alert("暂无附件");
		return;
	}
	
	
	var download = $('<iframe id="download" style="display: none;"/>');
	$('body').append(download);
	
	var src = v_context + "/imas/report/downLoadModel?url="+zipUrl;
	download.attr('src', src);
}

function importfile(){
	JSPFree.openDialog("导入回执文件","/yujs/imas/report/imas_receipt_import.js", 500, 240, null,function(_rtdata){
		JSPFree.queryDataByConditon(d1_BillList,null);  //立即查询刷新数据
	});
}

function view304(){
	var single = d1_BillList.datagrid('getSelected');
	if (single == null) {
		$.messager.alert('提示', '必须选择一条记录进行操作', 'warning');
		return;
	}
	
	var rid = single.rid;
	let jso_rt = JSPFree.doClassMethodCall("com.yusys.imas.report.service.ImasCrReportBS", "viewParam",{rid:rid,type:"1"});

	if (jso_rt.code == "success") {
		FreeUtil.openHtmlMsgBox("查看参数", 800, 500, "<span style='font-size:14px;display:block;overflow-wrap:break-word;'>304参数：<br/>" + jso_rt.data + "</span>");
	} else {
		$.messager.alert('提示', jso_rt.msg, 'info');
	}
}

function view306(){
	var single = d1_BillList.datagrid('getSelected');
	if (single == null) {
		$.messager.alert('提示', '必须选择一条记录进行操作', 'warning');
		return;
	}
	
	var rid = single.rid;
	let jso_rt = JSPFree.doClassMethodCall("com.yusys.imas.report.service.ImasCrReportBS", "viewParam",{rid:rid,type:"2"});

	if (jso_rt.code == "success") {
		FreeUtil.openHtmlMsgBox("查看参数", 800, 500, "<span style='font-size:14px;display:block;overflow-wrap:break-word;'>306参数：<br/>" + jso_rt.data + "</span>");
	} else {
		$.messager.alert('提示', jso_rt.msg, 'info');
	}
}

