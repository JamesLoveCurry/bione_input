//初始化界面
function AfterInit() {
	
	JSPFree.createBillCard("d1", "east/busiModel/templetItem",["保存/onConfirm/icon-p21","取消/onCancel/icon-undo"]); // 页签先左右分割

	//设置BillCard的值
	var displayConfig = jso_OpenPars2.display_config; // 取取已经填过的值
	if(displayConfig !=null && displayConfig!=""){
		var jso_par = {displayConfig : displayConfig};
		var jso_data = JSPFree.doClassMethodCall("com.yusys.east.business.model.service.EastCrColBS","getDisplayConfig",jso_par); 
		FreeUtil.loadBillCardData(d1_BillCard, jso_data.vo.m_hData)
	}
}

function onConfirm(){
	var billCard = JSPFree.getBillCardFormValue(d1_BillCard);
	var display_config = "";
	for(var obj in billCard){
		display_config += "<"+obj+">" +billCard[obj]+"</"+obj+">\n" ;
	}
	JSPFree.closeDialog({display_config:display_config});
}

function onCancel(){
	JSPFree.closeDialog();
}