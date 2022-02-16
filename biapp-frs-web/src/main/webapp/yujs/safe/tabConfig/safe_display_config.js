/**
 *
 * <pre>
 * Title:【报表管理】-【维护字段】-【界面展示配置】
 * Description:维护字段：界面展示配置页面
 * 在此界面选择配置该字段的展示信息，包括是否保存，是否必填等一系列控件
 * </pre>
 * @author liangzy5
 * @version 1.00.00
 * @date 2020/6/15 14:03
 */

function AfterInit() {
	var str_className = "Class:com.yusys.safe.tabmanage.template.DisplayConfigBuilderTemplate.getTemplet()";
	JSPFree.createBillCard("d1", str_className,["保存/onConfirm/icon-p21","取消/onCancel/icon-undo"]); // 页签先左右分割

	// 设置BillCard的值
	var displayConfig = jso_OpenPars2.display_config; // 取取已经填过的值
	if (displayConfig !=null && displayConfig!="") {
		var jso_par = {displayConfig : displayConfig};
		var jso_data = JSPFree.doClassMethodCall("com.yusys.safe.tabmanage.service.SafeCrTabBS","getDisplayConfig",jso_par); 
		FreeUtil.loadBillCardData(d1_BillCard, jso_data.vo.m_hData)
	}
}

/**
 * 确认按钮，保存!
 * @returns
 */
function onConfirm(){
	var billCard = JSPFree.getBillCardFormValue(d1_BillCard);
	var display_config = "";
	for (var obj in billCard) {
		display_config += "<"+obj+">" +billCard[obj]+"</"+obj+">\n" ;
	}
	
	JSPFree.closeDialog({display_config:display_config});
}

/**
 * 取消操作
 * @returns
 */
function onCancel() {
	JSPFree.closeDialog();
}