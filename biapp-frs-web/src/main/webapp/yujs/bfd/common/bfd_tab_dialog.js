/**
 *
 * <pre>
 * Title: 明细数据中【表名】，弹窗逻辑
 * Description:
 * </pre>
 * @author wangxy31
 * @version 1.00.00
 @date 2020年8月6日
 */
function AfterInit() {
    var whereSql = '';
    var jsn_result = JSPFree.doClassMethodCall("com.yusys.bfd.business.service.BfdGetReportBS", "getReportList", null);
    if (jsn_result.code == 'success') {
        whereSql = jsn_result.data;
    }
    var reportType = jso_OpenPars2.report_type;
    if (reportType) {
        reportType = reportType === '1' ? '月报' : '季报';
        whereSql = whereSql + " and report_type = '" + reportType + "'"
    }

    JSPFree.createBillList("d1", "/biapp-bfd/freexml/busiModel/bfd_cr_tab_ref.xml", ["确定/onConfirm/icon-ok", "取消/onCancel/icon-clear"], {
        isSwitchQuery: "N",
        refWhereSQL: whereSql
    });
}

/**
 * 确定选择机构操作
 * 选择机构树上面的机构，点击确定按钮，返回所选择机构
 * @returns
 */
function onConfirm() {
    var tab_name = '';
    var selectDatas = d1_BillList.datagrid('getSelections');
    if (selectDatas.length == 0) {
        $.messager.alert('提示', '至少选择一条记录进行操作', 'warning');
    }
    for (var i = 0; i < selectDatas.length; i++) {
        tab_name += selectDatas[i].tab_name + ";"
    }
    JSPFree.closeDialog({"tab_name": tab_name});
}

/**
 * 取消
 * @returns
 */
function onCancel() {
    JSPFree.closeDialog();
}