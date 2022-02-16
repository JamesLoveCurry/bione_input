function AfterInit() {
    if ("ALL" === parent.report_type) {
        var reportType = JSPFree.getBillQueryFormValue(parent.d1_BillQuery).report_type;
        if (reportType == null || reportType == "") {
            $.messager.alert('提醒', '请先选择申报类型!', 'info')
            return;
        }
    } else {
        reportType = parent.report_type;
    }
    // 获取查询条件
    var jsonSql = JSPFree.doClassMethodCall("com.yusys.safe.business.service.SafeRptOrgInfoCommFilter", "getSQLCondition", {
        reportType: reportType,
        loginUserOrgNo: str_LoginUserOrgNo
    });
    var sql = jsonSql.sql;
    JSPFree.createBillTreeByBtn("d1", "Class:com.yusys.safe.base.common.template.OrgBuilderTemplate.getTemplet('"+reportType+"')", ["确定/onConfirm/icon-ok", "取消/onCancel/icon-clear"], {
        "isCheckbox": "Y",
        "tree_isCheckboxCascade": "Y",
        "autocondition": sql,
        "querycontion": sql,
        "refWhereSQL":sql
    });
}

//确定
function onConfirm() {
    var selNodes = JSPFree.getBillTreeCheckedNodes(d1_BillTree);  //
    if (selNodes == null || selNodes.length <= 0) {
        $.messager.alert('提醒', '必须选择一个结点数据!', 'info');
        return;
    }
    var asy_data = JSPFree.getBillTreeCheckedDatas(d1_BillTree);
    console.log(asy_data[0]); //
    var str_text = "";
    for (var i = 0; i < asy_data.length; i++) {
        str_text = str_text + asy_data[i]["org_no"] + "/" + asy_data[i]["org_nm"] + ";";  //把机构号与机构名称用斜杠拼接!
    }
    parent.$("#org_no").textbox('setValue',str_text)
    JSPFree.closeDialog();
}

//取消
function onCancel() {
    JSPFree.closeDialog();
}
