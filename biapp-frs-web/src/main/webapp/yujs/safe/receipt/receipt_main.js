/**
 *
 * <pre>
 * Title:回执信息
 * Description:
 * </pre>
 * @author miaokx
 * @version 1.00.00
 * @date 2020/7/6 15:32
 */
var fileName = "";
var tab_name = "";
var tab_name_en = "";
var str_subfix = "";
var str_className = "";
var report_type = "";
function AfterInit() {
	// 默认查询所有
    if (jso_OpenPars.type) {
        str_subfix = jso_OpenPars.type;
        report_type = jso_OpenPars.type;
    } else {
        str_subfix = 'ALL';
        report_type = 'ALL';
    }
   
    // 获取【回执信息表】常量类
    tab_name = SafeFreeUtil.getTableNames().SAFE_CR_RECEIPT;
    str_className = "Class:com.yusys.safe.base.common.template.CommonBuilderTemplate.getTemplet('" + tab_name + "','v_safe_receipt_info_feedback','" + str_subfix + "')";
    JSPFree.createBillList("d1", str_className, null, {
        list_btns: "[icon-p20]导入/onImport;[icon-search]回执预览/onPreview;[icon-p67]下载并导入/downLoadAndImport;",
        isSwitchQuery: "N",
        autoquery: "N"
    });
}

function AfterBodyLoad(){
    if ("ALL" != report_type) {
        $("#report_type").combobox("setValue", report_type);
        JSPFree.setBillQueryItemEditable("report_type", "下拉框", false);
    }
}
function getOrgCondition() {
    var jso_org = JSPFree.doClassMethodCall("com.yusys.safe.base.common.service.SafeValidateQueryConditionBS", "getQueryCondition", {
        _loginUserOrgNo: str_LoginUserOrgNo,
        report_type: report_type
    });
    var orgCondition = " 1=2 ";
    if (jso_org.msg == "ok") {
        orgCondition = jso_org.condition;
    }

    return orgCondition;
}

/**
 * 回执导入
 * @returns
 */
function onImport() {
	report_type = JSPFree.getBillQueryFormValue(d1_BillQuery).report_type;
	str_subfix = JSPFree.getBillQueryFormValue(d1_BillQuery).report_type;
	if (report_type == null || report_type == "") {
    	$.messager.alert('提醒', '请选择报送类型!', 'info')
        return;
    }
	
    JSPFree.openDialog("文件上传", "/yujs/safe/receipt/receipt_import.js", 500, 240, {reportType: str_subfix},
        function(_rtdata) {
            if (_rtdata != null && _rtdata != "undefined") {
                if(_rtdata.msg){
                    JSPFree.alert(_rtdata.msg);
                }
                JSPFree.queryDataByConditon(d1_BillList, _rtdata.whereSQL); // 立即查询刷新数据
            }
        });
}

/**
 * 回执预览
 * @returns
 */
function onPreview(){
    var selectDatas = d1_BillList.datagrid('getSelections');
    if (selectDatas.length == 0) {
        $.messager.alert('提示', '至少选择一条记录进行操作', 'warning');
        return;
    }
    var jso_rt = JSPFree.doClassMethodCall("com.yusys.safe.receipt.service.SafeReceiptBS","getReportContentHtml",{filePath:selectDatas[0].filepath});

    FreeUtil.openHtmlMsgBox2("报文预览",800,600,jso_rt.html);
}

/**
 * 远程下载报文回执到服务器，并导入回执
 */
function downLoadAndImport() {
	report_type = JSPFree.getBillQueryFormValue(d1_BillQuery).report_type;
	str_subfix = JSPFree.getBillQueryFormValue(d1_BillQuery).report_type;
	if (report_type == null || report_type == "") {
    	$.messager.alert('提醒', '请选择报送类型!', 'info')
        return;
    }
	
    var jso_par = {className: str_className, reportType: str_subfix};
    JSPFree.openDialog("下载并导入回执", "/yujs/safe/receipt/receipt_download_import.js", 400, 350, jso_par, function (_rt) {
        if (_rt != null) {
            if ("success" == _rt.status) {
                JSPFree.queryDataByConditon(d1_BillList, null);  // 立即查询刷新数据x
                $.messager.alert('提示', _rt.msg, 'info');
            } else if (_rt.type != "dirclose") {
                $.messager.alert('提示', _rt.msg, 'warning');
            }
        }
    });
}