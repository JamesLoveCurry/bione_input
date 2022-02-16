/**
 * 
 * <pre>
 * Title: 【报送管理】-【报文生成】
 * Description: 报送管理-报文生成-上传报文：选择日期，机构和报送频率的页面
 * </pre>
 * @author liangzy5 
 * @version 1.00.00
   @date 2020年8月25日
 */
var _str_date = "";
var _str_org_no = ""; //970110,990100
var _str_report_type = "";
var org_class = "";
var org_nm = "";

function AfterInit() {
	if(jso_OpenPars!=null){ //如果是主界面过来的，参数是null，如果是从压缩界面回退回来的，则把日期和报送频率赋值
		_str_org_no = jso_OpenPars.org_no;
		org_class = jso_OpenPars.org_class;
		org_nm = jso_OpenPars.org_nm;
		
		_str_date = jso_OpenPars.data_dt;
		_str_report_type = jso_OpenPars.report_type;
	}
	
	JSPFree.createBillCard("d1","/biapp-imas/freexml/report/imas_report_upload.xml",["确定/onNext","取消/onCancel"]);
	
}

/**
 * 页面初始化后，隐藏滚动框
 * @returns
 */
function AfterBodyLoad() {
	var dom_div = document.getElementById("d1_BillCardDiv");
	dom_div.style.overflow="hidden";  // 隐藏滚动框
	JSPFree.setBillCardItemValue(d1_BillCard, "report_type", '日报');
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
	
	if(_str_date!=null && _str_date!=""){
		JSPFree.setBillCardItemValue(d1_BillCard, "data_dt", _str_date);
	}
}

/**
 * 下一步
 * @returns
 */
function onNext() {
	var jso_cardData = JSPFree.getBillCardFormValue(d1_BillCard);
	var str_org = jso_cardData.org_no;
	str_org = str_org.substr(0, str_org.lastIndexOf("/"));
	var str_date = jso_cardData.data_dt;
	var str_report_type = jso_cardData.report_type;
	
	if (str_org == null || str_org == "") {
        $.messager.alert('提示', '必须选择机构!', 'info');
        return;
    }
	if (str_date == null || str_date == "") {
		$.messager.alert('提示', '必须选择日期!', 'info');
		return;
	}
    if (str_report_type == null || str_report_type == "") {
        $.messager.alert('提示', '必须选择报送频率!', 'info');
        return;
    }
	var jso_rt = JSPFree.doClassMethodCall("com.yusys.imas.report.service.ImasSendReportBS","uploadFileByOrg",{"orgNo": str_org, "dataDt": str_date, "reportType": str_report_type});
	JSPFree.closeDialog(jso_rt);

}

/**
 * 取消
 * @returns
 */
function onCancel() {
	JSPFree.closeDialog();
}