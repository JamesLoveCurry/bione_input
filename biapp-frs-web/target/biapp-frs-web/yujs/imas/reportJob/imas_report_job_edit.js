/**
 *
 * <pre>
 * Title:MTS配置新增页面
 * Description:
 * </pre>
 * @author miaokx
 * @version 1.00.00
 * @date 2020/6/8 10:18
 */
var type = "";
var rid = "";
function AfterInit() {
    type = jso_OpenPars.type;
    JSPFree.createBillCard("d1", "/biapp-imas/freexml/reportJob/imas_report_job_code.xml", ["保存/onSave/icon-p21", "取消/onCancel/icon-undo"], null);
    if (type == 'EDIT') {
        rid  = jso_OpenPars.rid;
        // 赋值
        JSPFree.queryBillCardData(d1_BillCard, "rid = '"+rid+"'");
    }
}

/**
 * 新增监听事件
 * @constructor
 */
function AfterBodyLoad() {
    var str_fnName = "onBillCardItemEdit"; // 在创建表单时会创建这个函数
    if(typeof self[str_fnName] == "function") {  // 如果的确定义了这个函数
        FreeUtil.addBillCardItemEditListener(d1_BillCard, self[str_fnName]);  // 增加监听事件
    }
    if (type == 'ADD') {
        // 处理After逻辑
        var str_fnName_after = "afterInsert";
        if (typeof self[str_fnName_after] == "function") {  // 如果的确定义了这个函数
            self[str_fnName_after](d1_BillCard);  // 增加监听事件
        }
    } else {
        // 处理After逻辑
        var str_fnName_after = "afterUpdate";
        if(typeof self[str_fnName_after] == "function") {  // 如果的确定义了这个函数
            self[str_fnName_after](d1_BillCard);  // 增加监听事件
        }

    }
    FreeUtil.setBillCardLabelHelptip(d1_BillCard); // 必须写在最后一行
}

/**
 * 新增之后将不展示项默认不展示
 * @param _billCard
 */
function afterInsert(_billCard) {
    JSPFree.setBillCardItemVisible(_billCard, "report_time_limit", false);
    JSPFree.setBillCardItemVisible(_billCard, "err_is_generate", false);
    JSPFree.setBillCardItemVisible(_billCard, "is_out_already", false);
    JSPFree.setBillCardItemVisible(_billCard, "is_unified", false);
    JSPFree.setBillCardItemVisible(_billCard, "is_issue", false);
}
/**
 * 新增之后将不展示项默认不展示
 * @param _billCard
 */
function afterUpdate(_billCard) {
    if ('com.yusys.imas.scheduled.service.ScheduledImasRptTask' == _billCard.OldData.classname) {
        JSPFree.setBillCardItemVisible(_billCard, "report_time_limit", false);
        JSPFree.setBillCardItemVisible(_billCard, "err_is_generate", false);
        JSPFree.setBillCardItemVisible(_billCard, "is_out_already", false);
        JSPFree.setBillCardItemVisible(_billCard, "is_unified", false);
    } else {
        JSPFree.setBillCardItemVisible(_billCard, "is_issue", false);
        if ('Y' == _billCard.OldData.err_is_generate) {
            JSPFree.setBillCardItemVisible(_billCard, "is_unified", false);
        }
    }
}
/**
 * 传输协议为ftp时，展示FTP传输模式
 * @param _billCard
 * @param _itemkey
 * @param _jsoValue
 */
function onBillCardItemEdit(_billCard, _itemkey, _jsoValue) {
    if (_itemkey == "classname") {
        var str_value = _jsoValue;
        if (str_value.value == "com.yusys.imas.scheduled.service.ScheduledImasReportTask") {
            JSPFree.setBillCardItemVisible(_billCard, "report_time_limit", true);
            JSPFree.setBillCardItemVisible(_billCard, "err_is_generate", true);
            JSPFree.setBillCardItemVisible(_billCard, "is_out_already", true);
            JSPFree.setBillCardItemVisible(_billCard, "is_issue", false);
            JSPFree.setBillCardItemValue(_billCard, "is_issue", "");
        } else if(str_value.value == "com.yusys.imas.scheduled.service.ScheduledImasRptTask"){
            JSPFree.setBillCardItemVisible(_billCard, "report_time_limit", false);
            JSPFree.setBillCardItemVisible(_billCard, "err_is_generate", false);
            JSPFree.setBillCardItemVisible(_billCard, "is_out_already", false);
            JSPFree.setBillCardItemVisible(_billCard, "is_unified", false);
            JSPFree.setBillCardItemValue(_billCard, "report_time_limit", "");
            JSPFree.setBillCardItemValue(_billCard, "err_is_generate", "");
            JSPFree.setBillCardItemValue(_billCard, "is_out_already", "");
            JSPFree.setBillCardItemValue(_billCard, "is_unified", "");
            JSPFree.setBillCardItemVisible(_billCard, "is_issue", true);
        } else {
            JSPFree.setBillCardItemVisible(_billCard, "report_time_limit", false);
            JSPFree.setBillCardItemVisible(_billCard, "err_is_generate", false);
            JSPFree.setBillCardItemVisible(_billCard, "is_out_already", false);
            JSPFree.setBillCardItemVisible(_billCard, "is_unified", false);
            JSPFree.setBillCardItemValue(_billCard, "report_time_limit", "");
            JSPFree.setBillCardItemValue(_billCard, "err_is_generate", "");
            JSPFree.setBillCardItemValue(_billCard, "is_out_already", "");
            JSPFree.setBillCardItemValue(_billCard, "is_unified", "");
            JSPFree.setBillCardItemVisible(_billCard, "is_issue", false);
            JSPFree.setBillCardItemValue(_billCard, "is_issue", "");
        }
    }
    if (_itemkey == "err_is_generate") {
        var str_value = _jsoValue;
        if (str_value.value == "N") {
            JSPFree.setBillCardItemVisible(_billCard, "is_unified", true);
        } else {
            JSPFree.setBillCardItemVisible(_billCard, "is_unified", false);
            JSPFree.setBillCardItemValue(_billCard, "is_unified", "");
        }
    }
}

/**
 * 保存
 * @return {[type]} [description]
 */
function onSave() {
    var classname = JSPFree.getBillCardItemValue(d1_BillCard, "classname");
    var report_time_limit = JSPFree.getBillCardItemValue(d1_BillCard, "report_time_limit");
    var err_is_generate = JSPFree.getBillCardItemValue(d1_BillCard, "err_is_generate");
    var is_out_already = JSPFree.getBillCardItemValue(d1_BillCard, "is_out_already");
    var is_unified = JSPFree.getBillCardItemValue(d1_BillCard, "is_unified");
    var is_issue = JSPFree.getBillCardItemValue(d1_BillCard, "is_issue");
    if (classname == 'com.yusys.imas.scheduled.service.ScheduledImasRptTask') {
        if (is_issue == null || is_issue == "") {
            $.messager.alert('提示', '当自动检核任务时，检核完成是否下发选项不可为空!', 'warning');
            return;
        }
    } else if (classname == 'com.yusys.imas.scheduled.service.ScheduledImasReportTask'){
        if (report_time_limit == null || report_time_limit == "") {
            $.messager.alert('提示', '当自动报文任务时，报送时限不可为空!', 'warning');
            return;
        }
        if (err_is_generate == null || err_is_generate == "") {
            $.messager.alert('提示', '当自动报文任务时，检核不通过是否生成报文选项不可为空!', 'warning');
            return;
        } else {
            if ('N' == err_is_generate) {
                if (is_unified == null || is_unified == "") {
                    $.messager.alert('提示', '当校验不通过是否生成报文为否时，是否统一报送选项不可为空!', 'warning');
                    return;
                }
            }
        }
        if (is_out_already == null || is_out_already == "") {
            $.messager.alert('提示', '当自动报文任务时，是否排除已报送报表选项不可为空!', 'warning');
            return;
        }
    }
    var jso_formData = JSPFree.getBillCardFormValue(d1_BillCard); // 取得数据
    var jso_templetVO = d1_BillCard.templetVO; // 模板配置数据
    var isValidateNullSucess = FreeUtil.validateNullBilldData(d1_BillCard,
        jso_templetVO, jso_formData);
    if (!isValidateNullSucess) {
        return false;
    }
    var flag = "";
    if (type == 'ADD') {
        JSPFree.setBillCardValues(d1_BillCard,{rid:FreeUtil.getUUIDFromServer()});
        flag = JSPFree.doBillCardInsert(d1_BillCard, null);
    } else {
        flag = JSPFree.doBillCardUpdate(d1_BillCard, null);

    }
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