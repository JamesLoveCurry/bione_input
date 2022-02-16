/**
 * 
 * <pre>
 * Title: 【报表处理】-【报表下发】
 * Description: 报表下发：编辑页面
 * </pre>
 * @author wangxy31 
 * @version 1.00.00
   @date 2021年10月25日
 */
var status = "";
function AfterInit(){
	status = jso_OpenPars.data.status;
	if (status == '1' || status == '3') {
		JSPFree.createBillCard("d1","/biapp-crrs/freexml/crrs/fillingProcess/crrs_filling_process_CODE1_1.xml",["取消/onCancel/icon-undo"],null);
	} else {
		JSPFree.createBillCard("d1","/biapp-crrs/freexml/crrs/fillingProcess/crrs_filling_process_CODE1.xml",["保存/onSave/icon-p21"],null);
	}
	//赋值
	JSPFree.queryBillCardData(d1_BillCard, "rid = '"+jso_OpenPars.data.rid+"'");
}

/**
 * 保存
 * @return {[type]} [description]
 */
function onSave(){
	var flag = JSPFree.doBillCardUpdate(d1_BillCard,null);
	if(flag){
		JSPFree.alert("保存成功");
	}
	JSPFree.closeDialog(flag);
}

/**
 * 取消
 * @return {[type]} [description]
 */
function onCancel(){
	JSPFree.closeDialog();
}
