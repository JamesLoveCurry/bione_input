//初始化界面,菜单配置路径是【/frs/yufreejs?js=/yujs/east/58table.js】
var fileName = "";

function AfterInit() {
	JSPFree.createBillList("d1", "/biapp-cr/freexml/feedback/feedback.xml", null, {isSwitchQuery: "N"});
}

//下载
function downfile() {
	const jsy_datas = JSPFree.getBillListSelectDatas(d1_BillList);
	// alert(JSON.stringify(jsy_datas))
	if (jsy_datas == null || jsy_datas.length <= 0) {
		JSPFree.alert("至少选择一条数据！");
		return;
	}
	// var filepath= JSPFree.doClassMethodCall("com.yusys.cr.fileexport.service.CrReportBSDMO","getEntFilePath");
	for(var i = 0; i < jsy_datas.length; i++){
		let filename= jsy_datas[i]['fb_file_name'];
		let filepath = jsy_datas[i]['file_path'];
		filepath= filepath+"/"+filename;
		let download=null;
		download = $('<iframe id="download" style="display: none;"/>');
		$('body').append(download);
		const src = v_context + "/cr/common/downLoadModel?fileNm="+filepath;
		download.attr('src', src);
	}
}

//上传
function uploadFile() {
	JSPFree.openDialog("文件上传1", "/yujs/cr/feedback/importfeedback.js", 500, 240, null, function (_rtdata) {
		JSPFree.queryDataByConditon(d1_BillList, null);  //立即查询刷新数据
	});
}

/**
 * 按数据表查询：查看明细
 * @param _btn
 * @returns
 */
function viewFailDetail(_btn) {

	var dataset = _btn.dataset;
	var index = dataset.rowindex;
	var rows = d1_BillList.datagrid("getRows");
	var row = rows[index]; // index为行号

	// 判断，如果fail_count=0, 则不能查看明细
	if ("0" == row.failcounts) {
		JSPFree.alert("当前错误记录数为0，无法查看错误明细数据！");
		return;
	}

	var jso_OpenPars = {fbFileName: row.fb_file_name};
	JSPFree.openDialog("错误明细","/yujs/cr/feedback/cr_feedback_fail.js", 950, 500, jso_OpenPars, function(_rtdata){});
}
