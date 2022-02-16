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
var mts = "";

function AfterInit() {
    mts = jso_OpenPars.mts;
    template = jso_OpenPars.templetcode;

    JSPFree.createBillCard("d1", template, ["保存/onSave/icon-p21", "取消/onCancel/icon-undo"], null);
    // 赋值
    JSPFree.queryBillCardData(d1_BillCard, "rid = '"+mts.rid+"'");
}

/**
 * 新增监听事件
 * @constructor
 */
function AfterBodyLoad() {
    var str_fnName = "onBillCardItemEdit"; // 在创建表单时会创建这个函数
    if (typeof self[str_fnName] == "function") {  // 如果的确定义了这个函数
        FreeUtil.addBillCardItemEditListener(d1_BillCard, self[str_fnName]);  // 增加监听事件
    }
    // 处理After逻辑
    var str_fnName_after = "afterUpdate";
    if(typeof self[str_fnName_after] == "function") {  // 如果的确定义了这个函数
        self[str_fnName_after](d1_BillCard);  // 增加监听事件
    }
    //默认SFTP 如之后提供了FTP，解开即可
    JSPFree.setBillCardItemEditable(d1_BillCard, "transport_protocol", false);

    FreeUtil.setBillCardLabelHelptip(d1_BillCard); // 必须写在最后一行
}

/**
 * 添加监听
 * @param _billCard
 * @param _itemkey
 * @param _jsoValue
 */
function onBillCardItemEdit(_billCard, _itemkey, _jsoValue) {
    if (_itemkey == "transport_protocol") {
        var str_value = _jsoValue;
        if (str_value.value == "FTP") {
            JSPFree.setBillCardItemVisible(_billCard, "ftp_type", true);
            JSPFree.setBillCardItemIsMust(_billCard,"ftp_type",true);
        } else {
            JSPFree.setBillCardItemVisible(_billCard, "ftp_type", false);
            JSPFree.setBillCardItemIsMust(_billCard,"ftp_type",false);
            JSPFree.setBillCardItemClearValue(_billCard, "ftp_type");
        }
    }

}

/**
 * 修改后逻辑
 * @param _billCard
 */
function afterUpdate(_billCard) {
    if (false == _billCard.OldData.ftp_type) {
        JSPFree.setBillCardItemVisible(_billCard, "ftp_type", false);
        JSPFree.setBillCardItemIsMust(_billCard,"ftp_type",false);
    }
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
    var jso_data = JSPFree.doClassMethodCall(
        "com.yusys.east.mtsConfig.service.TaskMtsConfigBS",
        "isOnline", jso_formData);
    var code = jso_data.code;
    if (code == "success") {
        var flag = JSPFree.doBillCardUpdate(d1_BillCard, null);
        if (flag) {
            JSPFree.closeDialog("保存成功");
        }
    } else {
        if (code == "error") {
            JSPFree.alert(jso_data.msg);
        }
    }
}

/**
 * 取消
 * @return {[type]} [description]
 */
function onCancel() {
    JSPFree.closeDialog();
}