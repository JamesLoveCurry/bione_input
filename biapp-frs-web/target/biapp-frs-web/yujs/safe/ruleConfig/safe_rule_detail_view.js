/**
 *
 * <pre>
 * Title:【配置管理】-【校验配置】-【查看规则】
 * Description:查看规则：页面
 * 在此界面对某一条规则进行详情查看
 * </pre>
 * @author liangzy5
 * @version 1.00.00
 * @date 2020年6月18日
 */

function AfterInit(){
	var str_className = "Class:com.yusys.safe.tabmanage.template.RuleConfigBuilderTemplate.getTemplet('" + jso_OpenPars.tab_name + "','" + jso_OpenPars.tab_name_en + "')";
	JSPFree.createBillCard("d1", str_className ,["取消/onCancel/icon-no"],null);
	var str_sqlWhere = "id='"  + jso_OpenPars.id + "'";  //拼SQL条件
	JSPFree.queryBillCardData(d1_BillCard, str_sqlWhere);  //锁定规则表查询数据
}

/**
 * 【取消】
 * 关闭查看规则详情窗口
 * @returns
 */
function onCancel(){
	JSPFree.closeDialogAll();
}
