/**
 * 
 * <pre>
 * Title: 【报送管理】-【报文报文列表】
 * Description: 报文列表新增txt生成最终报文的功能
 * </pre>
 * @author miaokx
 * @version 1.00.00
   @date 2021年8月17日
 */
var _str_org_no = ""; //970110,990100
function AfterInit() {
	_str_org_no = jso_OpenPars.org_no;
	JSPFree.createBillCard("d1","/biapp-imas/freexml/report/imas_report_choose.xml",["确定/onNext","取消/onCancel"]);
}

/**
 * 页面初始化后，隐藏滚动框
 * @returns
 */
function AfterBodyLoad() {
	var dom_div = document.getElementById("d1_BillCardDiv");
	dom_div.style.overflow="hidden";  // 隐藏滚动框
	// 如果当前机构为报送机构，则设置默认值，否则不设置值
	if (_str_org_no) {
		var result = JSPFree.doClassMethodCall("com.yusys.imas.common.service.ImasValidateQueryConditionBS", "checkOrgIsReportOrg", {
			rptOrgNo: _str_org_no,
			isSingle: true
		});
		if (result.result) {
			JSPFree.setBillCardItemValue(d1_BillCard, "org_no", result.result);
		}
	}
}

/**
 * 下一步
 * @returns
 */
function onNext() {
	var jso_cardData = JSPFree.getBillCardFormValue(d1_BillCard);
	var str_org = jso_cardData.org_no;
	var str_date = jso_cardData.data_dt;

	if (str_org == null || str_org == "") {
        $.messager.alert('提示', '必须选择机构!', 'info');
        return;
    }
	if (str_date == null || str_date == "") {
		$.messager.alert('提示', '必须选择日期!', 'info');
		return;
	}

	
	var arr_org = str_org.split(";");
	var orgStr = ""; //970110,990100
	for(var i=0; i<arr_org.length; i++){
		if (arr_org[i]) {
			orgStr = orgStr + arr_org[i].substring(0, arr_org[i].indexOf("/"))+",";
		}
	}
	//去掉最后一个逗号
	if(arr_org.length>0){
		orgStr = orgStr.substring(0,orgStr.length-1);
	}
	var params = {data_dt:str_date,org_no:orgStr};
	JSPFree.openDialogAndCloseMe("生成报文","/yujs/imas/report/imas_report_create_file_start.js",780,300,params);
	/*var result = JSPFree.doClassMethodCall("com.yusys.imas.report.service.ImasCrReportBS", "crateXmlFile", params);
	if (result.code == 'success') {
		$.messager.alert('提示', '生成文件成功!', 'info');
	} else {
		if (result.msg) {
			$.messager.alert('提示', result.msg, 'info');
		}
	}*/
}

/**
 * 取消
 * @returns
 */
function onCancel() {
	JSPFree.closeDialog();
}