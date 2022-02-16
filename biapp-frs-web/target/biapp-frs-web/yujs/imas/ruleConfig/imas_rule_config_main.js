/**
 *
 * <pre>
 * Title:【配置管理】-【校验配置】
 * Description:校验配置：主页面
 * </pre>
 * @author miaokx
 * @version 1.00.00
 * @date 2021/4/16 10:21
 */
function AfterInit() {
    var whereSql = '';
    var jsn_result = JSPFree.doClassMethodCall("com.yusys.imas.ruleconfig.service.ImasCrRuleBS", "getReportCNList", null);
    if (jsn_result.code == 'success') {
        whereSql = jsn_result.data;
    }
    JSPFree.createBillList("d1", "/biapp-imas/freexml/ruleConfig/imas_cr_rule_code.xml",null,{isSwitchQuery:"N",list_ischeckstyle:"Y",list_ismultisel:"Y", refWhereSQL: whereSql});
}

/**
 * 【导入】
 * 点击按钮，上传excel模板文件，对校验规则进行导入
 *
 * @returns
 */
function importRule() {
    JSPFree.confirm("提示","规则导入会全量更新系统中的校验规则，请谨慎操作，请确认是否执行导入操作！",function(_isOK){
        if(_isOK){JSPFree.openDialog("文件上传", "/yujs/imas/ruleConfig/imas_input_rule.js", 500, 240, null,function(_rtdata) {
            if (_rtdata != null && _rtdata != undefined && _rtdata.type != "dirclose") { // 不是直接关闭窗口
                JSPFree.alert(_rtdata.msg);
                // 导入之后立即查询
                JSPFree.queryDataByConditon2(d1_BillList, null); // 立即查询刷新数据
            }
        });
        }
    });
}
/**
 * 启用规则
 *
 * @returns
 */
function enable() {
    updateRuleStsAndForceSubSts("RULE_STS","Y");
}
/**
 * 停用规则
 *
 * @returns
 */
function disable() {
    updateRuleStsAndForceSubSts("RULE_STS","N");
}
/**
 * 启用强制提交
 *
 * @returns
 */
function enForceSubSts() {
    updateRuleStsAndForceSubSts("FORCE_SUB_STS","Y");
}

/**
 * 停用强制提交
 *
 * @returns
 */
function disForceSubSts() {
    updateRuleStsAndForceSubSts("FORCE_SUB_STS","N");
}

/**
 * 修改停用启用状态和停用启用强制提交状态
 * @param sts
 */
function updateRuleStsAndForceSubSts(column,sts) {
    var selectDatas = d1_BillList.datagrid('getSelections');
    if (selectDatas.length == 0) {
        $.messager.alert('提示', '至少选择一条记录进行操作', 'warning');
        return;
    }
    var ids = "";
    for (var i=0; i<selectDatas.length; i++) {
        ids += selectDatas[i].id + ",";
    }
    if (ids != "") {
        ids = ids.substring(0,ids.length-1);
    }
    var jso_par = {
        "column":column,
        "sts" : sts,
        "ids" : ids
    };
    var jso_data = JSPFree
        .doClassMethodCall(
            "com.yusys.imas.ruleconfig.service.ImasCrRuleBS",
            "updateRuleStsAndForceSubSts", jso_par);
    if (jso_data.code == 'success') {
        var msg = null;
        if (sts == 'Y') {
            if (column=="RULE_STS"){
                msg = '启用成功';
            }else {
                msg = '启用强制提交成功';
            }
        } else {
            if (column=="RULE_STS"){
                msg = '停用成功';
            }else {
                msg = '停用强制提交成功';
            }
        }
        $.messager.show({
            title : '消息提示',
            msg : msg,
            showType : 'show'
        });
    }
    for (var j=0; j<selectDatas.length; j++) {
        var seleDate = selectDatas[j];
        FreeUtil.refreshBillListOneRowDataByUpdate(d1_BillList, seleDate._rownum, "id='" + seleDate.id + "'");
    }
}
/**
 * 查看详情
 */
function onView(_this) {
    var dataset = _this.dataset;
    var index = dataset.rowindex;
    var rows = d1_BillList.datagrid("getRows");
    var row = rows[index];//index为行号
    var jso_OpenPars = {
        id : row.id,
        type_cd : row.type_cd
    };
    JSPFree.openDialog("规则查看", "/yujs/imas/ruleConfig/imas_rule_config_view.js", 750, 500,
        jso_OpenPars, function(_rtdata) {
        });
}

/**
 * 导出
 *
 * @returns
 */
function exportRule(_btn) {
    var download = $('<iframe id="download" style="display: none;"/>');
    $('body').append(download);
    var ruleId = JSPFree.getBillQueryFormValue(d1_BillQuery).id;
    var tabName = JSPFree.getBillQueryFormValue(d1_BillQuery).tab_name;
    var ruleName = JSPFree.getBillQueryFormValue(d1_BillQuery).rule_name;


    var src = v_context + "/imas/rule/export?ruleId=" + ruleId
        + "&tabName=" + tabName + "&ruleName=" + ruleName;
    download.attr('src', src);
}

/**
 * 重启单条检核服务
 */
function restartCheckService(){
    JSPFree.confirm("提示","确定要重启单条检核服务么?",function(_isOK){
        if(_isOK){
            var _rt = JSPFree.doClassMethodCall("com.yusys.imas.engineTask.service.ImasEngieTaskBS","restartCheckService",{});
            if (_rt.status == "OK") {
                $.messager.alert('提示', '单条检核服务启动程序调用成功！', 'info');
            } else {
                $.messager.alert('提示', '单条检核服务启动程序调用失败！', 'warning');

            }
        }
    });
}