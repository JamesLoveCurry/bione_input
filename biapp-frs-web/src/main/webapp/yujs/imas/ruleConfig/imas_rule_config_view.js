/**
 *
 * <pre>
 * Title:【配置管理】-【校验配置】
 * Description:校验配置：详情页面
 * </pre>
 * @author miaokx
 * @version 1.00.00
 * @date 2021/4/16 11:35
 */
function AfterInit(){
    JSPFree.createBillCard("d1","/biapp-imas/freexml/ruleConfig/imas_cr_rule_view.xml",["取消/onCancel/icon-no"],null);
    //拼SQL条件
    var str_sqlWhere = "id='"  + jso_OpenPars.id + "'";
    //锁定规则表查询数据
    JSPFree.queryBillCardData(d1_BillCard, str_sqlWhere);
}

function onCancel(){
    JSPFree.closeDialogAll();
}