/**
 * 
 * <pre>
 * Title: 【报送管理】-【绑定证书】
 * Description: 绑定证书选择类型页面
 * 绑定证书选择类型页面
 * </pre>
 * @author miaokx
 * @version 1.00.00
   @date 2020年8月21日
 */
var financeOrgNo = "";
var orgNo="";
function AfterInit() {
	JSPFree.createBillCard("d1", "/biapp-imas/freexml/report/imas_cr_report_bound.xml", ["确定/onConfirm","取消/onCancel"]);
	financeOrgNo = jso_OpenPars.financeOrgNo;
	orgNo = jso_OpenPars.orgNo;
}




/**
 * 确定生成
 * 判断是否选择了记录，若选择了记录，则新增这些报表的报文任务
 * 若没有选择记录，则默认生成所有报表的报文任务
 */
function onConfirm() {
	var form_vlue = JSPFree.getBillCardFormValue(d1_BillCard);
	var str_date = form_vlue.certchgtype;
	if (str_date == null || str_date == "") {
		$.messager.alert('提示', '必须选择变更类型!', 'info');
		return;
	}
	var jso_par = {certchgtype: str_date, financeOrgNo: financeOrgNo, orgNo:orgNo}
	var jso_rt = JSPFree.doClassMethodCall("com.yusys.imas.report.service.ImasSendReportBS","bound",jso_par);

	JSPFree.closeDialog(jso_rt);
}

/**
 * 取消操作
 * 点击取消按钮，关闭窗口
 * @returns
 */
function onCancel() {
	JSPFree.closeDialog();
}