/**
 *
 * <pre>
 * Title:【配置管理】【报表管理】
 * Description:打开displayConfig模板
 * </pre>
 * @author miaokx
 * @version 1.00.00
 * @date 2020/8/7 11:30
 */
function AfterInit() {
	
	JSPFree.createBillCard("d1", "/biapp-bfd/freexml/tabConfig/bfd_templet_item.xml",["保存/onConfirm/icon-p21","取消/onCancel/icon-undo"]); // 页签先左右分割

	//设置BillCard的值
	var displayConfig = jso_OpenPars2.display_config; // 取已经填过的值
	if(displayConfig !=null && displayConfig!=""){
		var jso_par = {displayConfig : displayConfig};
		var jso_data = JSPFree.doClassMethodCall("com.yusys.bfd.tabconfig.service.BfdCrTabBS","getDisplayConfig",jso_par);
		FreeUtil.loadBillCardData(d1_BillCard, jso_data.vo.m_hData)
	}
}

/**
 * 保存
 */
function onConfirm(){
	var billCard = JSPFree.getBillCardFormValue(d1_BillCard);
	var display_config = "";
	for(var obj in billCard){
		display_config += "<"+obj+">" +billCard[obj]+"</"+obj+">\n" ;
	}
	JSPFree.closeDialog({display_config:display_config});
}

/**
 * 取消
 */
function onCancel(){
	JSPFree.closeDialog();
}