/**
 *
 * <pre>
 * Title:MTS配置修改页面
 * Description:
 * </pre>
 * @author miaokx
 * @version 1.00.00
 * @date 2020/6/8 10:18
 */

var template = ""; // 模板路径
var oldData = "";

function AfterInit() {
    oldData = jso_OpenPars.oldData;
    template = jso_OpenPars.templetcode;

    JSPFree.createBillCard("d1", template, ["保存/onSave/icon-p21", "取消/onCancel/icon-undo"], null);
    // 赋值
    JSPFree.queryBillCardData(d1_BillCard, "rid = '"+oldData.rid+"'");
}


/**
 * 保存
 * @return {[type]} [description]
 */
function onSave() {
    var jso_formData = JSPFree.getBillCardFormValue(d1_BillCard); // 取得数据
    var jso_templetVO = d1_BillCard.templetVO; // 模板配置数据
    var isValidateNullSucess = FreeUtil.validateNullBilldData(d1_BillCard,
        jso_templetVO, jso_formData);
    if (!isValidateNullSucess) {
        return false;
    }

    var flag = JSPFree.doBillCardUpdate(d1_BillCard, null);
    if (flag) {
        JSPFree.closeDialog("保存成功");
    }
}

/**
 * 取消
 * @return {[type]} [description]
 */
function onCancel() {
    JSPFree.closeDialog();
}