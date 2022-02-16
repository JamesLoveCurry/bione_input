/**
 *
 * <pre>
 * Title: 明细数据中【内部机构号】，弹窗逻辑
 * Description:
 * </pre>
 * @author wangxy31
 * @version 1.00.00
 @date 2020年8月6日
 */
function AfterInit() {
    JSPFree.createBillTreeByBtn("d1", "/biapp-bfd/freexml/common/bfd_rpt_org_info_comm_filter.xml", ["确定/onConfirm/icon-ok", "取消/onCancel/icon-clear"], {
        "isCheckbox": "N",
        "tree_isCheckboxCascade": "N"
    });
}

/**
 * 确定选择机构操作
 * 选择机构树上面的机构，点击确定按钮，返回所选择机构
 * @returns
 */
function onConfirm() {
    var selNode = JSPFree.getBillTreeSelectNode(d1_BillTree);  //
    if (selNode == null) {
        $.messager.alert('提醒', '必须选择一个结点数据!', 'info');
        return;
    }
    
    var isRoot = JSPFree.isBillTreeNodeRoot(d1_BillTree, selNode);
    if (isRoot) {
        $.messager.alert('提醒', '不能选择根结点!', 'info');
        return;
    }
    var jso_rowdata = JSPFree.getBillTreeSelectData(d1_BillTree);
    
    JSPFree.closeDialog({"org_no": jso_rowdata.org_no + "/" + jso_rowdata.org_nm});
}

/**
 * 取消
 * @returns
 */
function onCancel() {
    JSPFree.closeDialog();
}
