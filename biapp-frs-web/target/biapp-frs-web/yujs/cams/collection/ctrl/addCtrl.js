var def = "";
function AfterInit(){
	def = jso_OpenPars.def;
	JSPFree.createTabb("d1",["控制人信息","姓名信息","地址信息","TIN信息"]);

	JSPFree.createBillCard("d1_1","/biapp-cams/freexml/cams/cams_corp_ctrl_CODE1.xml",["保存/onSave/icon-ok"],null);
	document.getElementById("d1_2").innerHTML="<div style=\"width:100%;text-align:center;\">请先维护个人账户信息</div>";
	document.getElementById("d1_3").innerHTML="<div style=\"width:100%;text-align:center;\">请先维护个人账户信息</div>";
	document.getElementById("d1_4").innerHTML="<div style=\"width:100%;text-align:center;\">请先维护个人账户信息</div>";
	
	JSPFree.setBillCardValues(d1_1_BillCard, def);
}

/**
 * 保存
 * @return {[type]} [description]
 */
function onSave(){
	JSPFree.setBillCardValues(d1_1_BillCard,{rid:FreeUtil.getUUIDFromServer()}); // 初始化状态
	var flag = JSPFree.doBillCardInsert(d1_1_BillCard,null);
	if(flag){
		JSPFree.alert("校验并保存成功");
		var ctrl = JSPFree.getBillCardFormValue(d1_1_BillCard);
		JSPFree.openDialogAndCloseMe2("控制人信息","/yujs/cams/collection/ctrl/editCtrl.js",900,560,{ctrl:ctrl},true);
	}
	
//	JSPFree.setBillCardValues(d1_1_BillCard,{rid:FreeUtil.getUUIDFromServer()});
//	
//	// 单条数据校验
//	var backValue = JSPFree.editTableCheckData(d1_1_BillCard, "Add", "机构账户控制人信息", "CAMS_CORP_CTRL","","4");
//	if (backValue == "" || "undifind" == backValue) {
//		return;
//	} else if (backValue == "OK") {
//		// 提示验证通过，并进行保存
//		var flag = JSPFree.doBillCardInsert(d1_1_BillCard,null);
//		if(flag){
//			JSPFree.alert("校验并保存成功");
//			var ctrl = JSPFree.getBillCardFormValue(d1_1_BillCard);
//			JSPFree.openDialogAndCloseMe2("控制人信息","/yujs/cams/collection/ctrl/editCtrl.js",900,560,{ctrl:ctrl},true);
//		}
//	} else if (backValue == "Fail") {
//		return;
//	}
}