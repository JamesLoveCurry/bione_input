function AfterInit() {
	var str_templetcode = jso_OpenPars.templetcode;  //模板编码
	JSPFree.createBillCard("d1",str_templetcode,["确定/onSaveAndClose/icon-p21","取消/onClose/icon-undo"],null);
}

function AfterBodyLoad(){
	var dom_div = document.getElementById("d1_BillCardDiv");
	dom_div.style.overflow="hidden";  //隐藏滚动框
}

//【确定】按钮逻辑
function onSaveAndClose(){
	var jso_cardData = JSPFree.getBillCardFormValue(d1_BillCard);
	 if(jso_cardData.reason == null || jso_cardData.reason==""){
		  JSPFree.alert("提交原因不能为空!");
		  return;
	 }	
	JSPFree.closeDialog({reason : jso_cardData.reason});  //关闭窗口,返回数据
}

//直接关闭
function onClose(){
	JSPFree.closeDialog();
}