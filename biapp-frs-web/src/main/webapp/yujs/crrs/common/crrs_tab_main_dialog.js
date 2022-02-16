/**
 *
 * <pre>
 * Title: 明细数据中【表名】，弹窗逻辑
 * Description:
 * </pre>
 * @author wangxy31
 * @version 1.00.00
 @date 2021年10月21日
 */
function AfterInit() {
    JSPFree.createBillList("d1", "/biapp-crrs/freexml/crrs/common/crrs_cr_tab_main_ref.xml", ["确定/onConfirm/icon-ok", "取消/onCancel/icon-clear"], {
    	isSwitchQuery:"N",autoquery:"Y",list_ispagebar:"N"
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
