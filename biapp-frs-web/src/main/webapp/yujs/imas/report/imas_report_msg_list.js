var tab_name = "";
var tab_name_en = "";
var org_no = "";
var org_class = "";
var org_nm = "";

function AfterInit() {
	//通过当前登录人所属内部机构获取报送机构号
	var jso_report_org = JSPFree.doClassMethodCall("com.yusys.imas.common.service.ImasValidateQueryConditionBS","getReportOrgNoCondition",{_loginUserOrgNo:str_LoginUserOrgNo});
	if(jso_report_org.msg=="ok"){
		org_no = jso_report_org.data;
	}
	org_class = ImasFreeUtil.getOrgClass(org_no); //获取机构层级
	org_nm = ImasFreeUtil.getOrgNm(org_no); //获取机构名称
	var json = JSPFree.doClassMethodCall("com.yusys.imas.report.service.ImasSendReportBS","isDirect",{_loginUserOrgNo:str_LoginUserOrgNo});
	if (json.code == 'success') {
		JSPFree.createBillList("d1","/biapp-imas/freexml/report/imas_cr_report_CODE.xml",null,{
			querycontion:" status = '报文已生成'  ",autocondition:" status = '报文已生成' ",
			isSwitchQuery:"N",autoquery:"N",
			list_btns:"[icon-p47]打包下载/onZipAndDownload;[icon-p59]上传报文/uploadFile;[icon-p60]强制提交/submitForced;[icon-p61]业务状态查询/searchStatus;[icon-p58]查看分片日志/onSeparate;[icon-p49]任务监控/showStatus;[icon-p58]301参数/view301;[icon-p31]305参数/view305;[icon-p31]307参数/view307;"});
	} else {
		JSPFree.createBillList("d1","/biapp-imas/freexml/report/imas_cr_report_not_direct_CODE.xml",null,{
			querycontion:" status = '报文已生成'  ",autocondition:" status = '报文已生成' ",
			isSwitchQuery:"N",autoquery:"N",
			list_btns:"[icon-p47]打包下载/onZipAndDownload;[icon-p58]查看分片日志/onSeparate;"});

	}
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
 * 任务监控
 */
function showStatus() {
	JSPFree.openDialog2("任务监控","/yujs/imas/report/imas_send_report_start.js",1000,600,null);
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
	var jso_rt = JSPFree.doClassMethodCall("com.yusys.imas.report.service.ImasSendReportBS","searchForceStatus",{"rid": selectDatas[0].rid, orgNo: selectDatas[0].org_no});
	$.messager.alert('提示', jso_rt.data, 'info');
}


/**
 * 刷新操作
 * 点击刷新按钮，实现刷新操作
 * @returns
 */
function onRefresh() {
	JSPFree.queryDataByConditon2(d1_BillList,getCondition());
}


/**
 * 打包下载报文
 * @returns
 */
function onZipAndDownload() {
	var selectDatas = d1_BillList.datagrid('getSelections');
	if (selectDatas.length == 0) {
		JSPFree.openDialog("打包压缩下载一个机构某一日期下的所有报文", "/yujs/imas/report/imas_report_task_choosedate.js", 400, 350, {org_no:org_no,org_class:org_class,org_nm:org_nm});
	}else{
		var dates = new Array();
		var orgs = new Array();
		var str_date="";
		var orgStr="";
		var rids = "";
		for (var i=0; i<selectDatas.length; i++) {
			str_date = selectDatas[0].data_dt;
			if(dates.indexOf(selectDatas[i].data_dt)==-1){
				dates.push(selectDatas[i].data_dt);
			}
			if(orgs.indexOf(selectDatas[i].org_no)==-1){
				orgs.push(selectDatas[i].org_no);
			}
			if(orgStr.indexOf(selectDatas[i].org_no)==-1){
				orgStr = orgStr + selectDatas[i].org_no+",";
			}
			rids = rids + selectDatas[i].rid+",";
		}

		if(dates.length>1){
			$.messager.alert('提示', '请选择同日期数据进行打包下载操作', 'info');
			return;
		}

		if(orgs.length>1){
			$.messager.alert('提示', '请选择同机构数据进行打包下载操作', 'info');
			return;
		}

		JSPFree.openDialogAndCloseMe("打包压缩下载报文","/yujs/imas/report/imas_report_task_zip.js",780,300,{rids:rids,data_dt:str_date,org_no:orgStr,report_type:'日报',org_class:org_class,org_nm:org_nm});
	}

}

/**
 * 查看日志
 * @returns
 */
function onViewLog() {
	var selectDatas = d1_BillList.datagrid('getSelections');
	if (selectDatas.length == 0) {
		$.messager.alert('提示', '至少选择一条记录进行操作', 'info');
		return;
	} else if (selectDatas.length > 1) {
		$.messager.alert('提示', '只能选择一条记录进行操作', 'info');
		return;
	}

	var json_data = selectDatas[0];
	if (json_data == null) {
		$.messager.alert('提示', '必须选择一条记录进行操作', 'info');
	}

	JSPFree.openDialog("查看日志","/yujs/imas/report/imas_cr_report_viewlog.js",900,420,{rid:json_data.rid},function(_rtdata){
		//回调方法,立即查询数据
	});
}

function onSeparate(){
	var selectDatas = d1_BillList.datagrid('getSelections');
	if (selectDatas.length == 0) {
		$.messager.alert('提示', '至少选择一条记录进行操作', 'info');
		return;
	} else if (selectDatas.length > 1) {
		$.messager.alert('提示', '只能选择一条记录进行操作', 'info');
		return;
	}

	var json_data = selectDatas[0];
	if (json_data == null) {
		$.messager.alert('提示', '必须选择一条记录进行操作', 'info');
	}

	JSPFree.openDialog("查看日志","/yujs/imas/report/imas_cr_report_separate.js",900,420,{rid:json_data.rid, orgNo: json_data.org_no},function(_rtdata){
		//回调方法,立即查询数据
	});
}

/**
 * 直连上传报文
 */
function uploadFile() {
	var selectDatas = d1_BillList.datagrid('getSelections');
	if (selectDatas.length == 0) {
		// 如果不选择，则选择日期和报送频率全部上传。
		JSPFree.openDialog("上传一个机构某一日期下的所有报文", "/yujs/imas/report/imas_report_task_upload.js", 400, 350, {org_no:org_no,org_class:org_class,org_nm:org_nm},function(_rtdata){
			if (_rtdata.code == 'success') {
				$.messager.show({
					title : '消息提示',
					msg : '报文上传中,请稍候查看上传结果!',
					showType : 'show'
				});
			} else {
				if (_rtdata.msg) {
					$.messager.alert('提示', _rtdata.msg, 'info');
				}
			}
		});
	} else {
		var jso_rids = new Array();
		for (var i=0; i<selectDatas.length; i++) {
			jso_rids.push(selectDatas[i].rid);
			if (i > 0) {
				if(selectDatas[i].org_no != selectDatas[i-1].org_no) { //其实应该可以去掉这个限制，后面再考虑
					$.messager.alert('提示', '只能选择同一报送机构的记录进行操作', 'warning');
					return;
				}
				if (i > 0) {
					if(selectDatas[i].data_dt != selectDatas[i-1].data_dt) { //其实应该可以去掉这个限制，后面再考虑
						$.messager.alert('提示', '只能选择同一日期的记录进行操作', 'warning');
						return;
					}
				}
			}
		}
		JSPFree.confirm('提示', '你真的要上传选中的报文吗?', function(_isOK){
			if(_isOK) {
				var orgNo = selectDatas[0].org_no;
				var jso_rt = JSPFree.doClassMethodCall("com.yusys.imas.report.service.ImasSendReportBS","uploadFile",{"allReportId": jso_rids, "loginUserOrgNo" : str_LoginUserOrgNo, orgNo: orgNo});
				if (jso_rt.code == 'success') {
					onRefresh();
					JSPFree.openDialog2("任务监控","/yujs/imas/report/imas_send_report_start.js",1000,600,null);
				} else {
					$.messager.alert('提示', jso_rt.msg, 'info');
				}
			}
		});
	}
}

/**
 * 强制提交
 */
function submitForced() {
	var selectDatas = d1_BillList.datagrid('getSelections');
	if (selectDatas.length == 0) {
		$.messager.alert('提示', '至少选择一条记录进行操作', 'info');
		return;
	} else if (selectDatas.length > 1) {
		$.messager.alert('提示', '只能选择一条记录进行操作', 'info');
		return;
	}
	var json_data = selectDatas[0];
	if (json_data == null) {
		$.messager.alert('提示', '必须选择一条记录进行操作', 'info');
	}

	JSPFree.openDialog("强制提交","/yujs/imas/report/imas_report_task_import.js",600,360,{rid:json_data.rid,orgNo: json_data.org_no},function(_rtdata){
		if (_rtdata != null && _rtdata != '' && _rtdata != undefined) {
			//回调方法,立即查询数据
			if (_rtdata == 'success') {
				$.messager.alert('提示', '强制提交成功!', 'info');
			} else {
				if (_rtdata.msg) {
					$.messager.alert('提示', _rtdata.msg, 'info');
				}
			}
		}

	});
}

function testSign() {
	var jso_rt = JSPFree.doClassMethodCall("com.yusys.imas.report.service.ImasSendReportBS","testDsin",null);
}

function view301(){
	var single = d1_BillList.datagrid('getSelected');
	if (single == null) {
		$.messager.alert('提示', '必须选择一条记录进行操作', 'warning');
		return;
	}

	var rid = single.rid;
	let jso_rt = JSPFree.doClassMethodCall("com.yusys.imas.report.service.ImasSendReportBS", "getParams",{rid:rid,type:"1"});

	if (jso_rt.code == "success") {
		FreeUtil.openHtmlMsgBox("查看参数", 800, 500, "<span style='font-size:14px;display:block;overflow-wrap:break-word;'>301参数：<br/>" + jso_rt.data + "</span>");
	} else {
		$.messager.alert('提示', jso_rt.msg, 'info');
	}
}

function view305(){
	var single = d1_BillList.datagrid('getSelected');
	if (single == null) {
		$.messager.alert('提示', '必须选择一条记录进行操作', 'warning');
		return;
	}

	var rid = single.rid;
	let jso_rt = JSPFree.doClassMethodCall("com.yusys.imas.report.service.ImasSendReportBS", "getParams",{rid:rid,type:"2"});

	if (jso_rt.code == "success") {
		FreeUtil.openHtmlMsgBox("查看参数", 800, 500, "<span style='font-size:14px;display:block;overflow-wrap:break-word;'>305参数：<br/>" + jso_rt.data + "</span>");
	} else {
		$.messager.alert('提示', jso_rt.msg, 'info');
	}
}
/*
* 查询307报文，主要是为了方便验收，没有实际意义
 */
function view307(){
	var single = d1_BillList.datagrid('getSelected');
	if (single == null) {
		$.messager.alert('提示', '必须选择一条记录进行操作', 'warning');
		return;
	}

	var rid = single.rid;
	let jso_rt = JSPFree.doClassMethodCall("com.yusys.imas.report.service.ImasSendReportBS", "getSepParams",{rid:rid,type:"3"});

	if (jso_rt.code == "success") {
		FreeUtil.openHtmlMsgBox("查看参数", 800, 500, "<span style='font-size:14px;display:block;overflow-wrap:break-word;'>307参数：<br/>" + jso_rt.data + "</span>");
	} else {
		$.messager.alert('提示', jso_rt.msg, 'info');
	}
}

/**
 * 光大专用，按钮暂时屏蔽
 *  txt文件生成最终zip文件
 */
function createXmlFile() {
	JSPFree.openDialog("生成报文","/yujs/imas/report/imas_report_create_file.js",600,360,{org_no:org_no});
}