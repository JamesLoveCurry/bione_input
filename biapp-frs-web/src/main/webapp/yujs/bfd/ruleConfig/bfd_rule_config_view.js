/**
 *
 * <pre>
 * Title:【配置管理】-【校验配置】
 * Description:校验配置：详情页面
 * </pre>
 * @author miaokx
 * @version 1.00.00
 * @date 2020/8/12 11:35
 */
function AfterInit(){
    JSPFree.createBillCard("d1","/biapp-bfd/freexml/ruleConfig/bfd_cr_rule_view.xml",["取消/onCancel/icon-no"],null);
    var str_sqlWhere = "id='"  + jso_OpenPars.id + "'";  //拼SQL条件
    JSPFree.queryBillCardData(d1_BillCard, str_sqlWhere);  //锁定规则表查询数据
}

function onCancel(){
    JSPFree.closeDialogAll();
}