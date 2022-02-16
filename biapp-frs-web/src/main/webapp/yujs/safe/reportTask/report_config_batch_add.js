/**
  * 
  * <pre>
  * Title: 【报送管理】-【报文策略】
  * Description: 报文策略：批量新增页面
  * </pre>
  * @author wangxy31 
  * @version 1.00.00
    @date 2020年5月22日
  */

var tabname = "";
var tabnameen = "";
var reporttype = "";

function AfterInit() {
	tabname = jso_OpenPars.tabname;
	tabnameen = jso_OpenPars.tabnameen;
	reporttype = jso_OpenPars.reporttype;
	
	JSPFree.createSplitByBtn("d1", "上下", 50, ["确定/onConfirm", "取消/onCancel"]);

	var str_classNameA = "Class:com.yusys.safe.reporttask.service.TaskConfigBuilderTemplate.getTemplet('" + tabname + "','" + tabnameen + "','" + reporttype + "')";
	JSPFree.createBillCard("d1_A", str_classNameA, null, {onlyItems:"report_frequency"});  // 只显示报送频率
	
	var str_classNameB = "Class:com.yusys.safe.base.common.template.OrgBuilderTemplate.getTemplet('" + reporttype + "')";
	JSPFree.createBillTree("d1_B", str_classNameB, {isCheckbox:true});
}

/**
 * 加载主体之后，隐藏滚动条
 * @returns
 */
function AfterBodyLoad() {
	var dom_div = document.getElementById("d1_A_BillCardDiv");
	dom_div.style.overflow="hidden"; // 隐藏滚动框
}

/**
 * 确定按钮
 * @returns
 */
function onConfirm() {
	// 选中报送频率
	var jso_cardData = JSPFree.getBillCardFormValue(d1_A_BillCard);
	if(jso_cardData.report_frequency == "" || jso_cardData.report_frequency == null){
		JSPFree.alert("必须选择报送频率!");
		return;
	}
	
	// 选择树中的的数据
	var jsy_orgs = JSPFree.getBillTreeCheckedDatas(d1_B_BillTree);
	if(jsy_orgs.length <= 0){
		JSPFree.alert("必须选择一个机构!");
		return;
	}
	
	// 远程调用
	var jso_par = {report_frequency:jso_cardData.report_frequency, allOrgs:jsy_orgs, report_type:reporttype};
	var jso_rt = JSPFree.doClassMethodCall("com.yusys.safe.reporttask.service.SafeReportTaskConfigDMO", "batchInsertReportConfig", jso_par);

	JSPFree.closeDialog(jso_rt);  //关闭窗口,并有返回值
}

/**
 * 取消
 * @return {[type]} [description]
 */
function onCancel() {
	JSPFree.closeDialog();
}
