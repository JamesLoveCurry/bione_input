/**
 * 
 * <pre>
 * Title: 【报表处理】-【报表填报】
 * Description: 报表处理-报表填报：提交原因界面
 * 强制提交的时候，需要填写提交原因
 * </pre>
 * @author liangzy5 
 * @version 1.00.00
   @date 2020年8月18日
 */

function AfterInit() {
	var str_templetcode = jso_OpenPars.templetcode;  //模板编码
	JSPFree.createBillCard("d1",str_templetcode,["确定/onSaveAndClose/icon-p21","取消/onClose/icon-undo"],null);
}

/**
 * 页面加载结束之后的加载方法
 * 对滚动条进行隐藏
 * @returns
 */
function AfterBodyLoad(){
	var dom_div = document.getElementById("d1_BillCardDiv");
	dom_div.style.overflow="hidden";  //隐藏滚动框
}

/**
 * 确定操作
 * 点击确定按钮，确定提交原因，保存数据并关闭窗口
 * @returns
 */
function onSaveAndClose(){
	var jso_cardData = JSPFree.getBillCardFormValue(d1_BillCard);
	 if(jso_cardData.reason == null || jso_cardData.reason==""){
		  JSPFree.alert("提交原因不能为空!");
		  return;
	 }	
	JSPFree.closeDialog({reason : jso_cardData.reason});  //关闭窗口,返回数据
}

/**
 * 关闭操作
 * 点击取消，直接关闭窗口
 * @returns
 */
function onClose(){
	JSPFree.closeDialog();
}