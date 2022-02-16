/**
 * 
 * <pre>
 * Title: 【报表处理】-【报表下发】
 * Description: 报表下发：新增页面
 * </pre>
 * @author wangxy31 
 * @version 1.00.00
   @date 2020年6月18日
 */

var className = null;
var subfix = null;

function AfterInit() {
	className = jso_OpenPars.className;
	subfix = jso_OpenPars.subfix;

	JSPFree.createSplitByBtn("d1","上下",120,["确定/onConfirm","取消/onCancel"]);
	JSPFree.createBillCard("d1_A", className, null, {onlyItems:"data_dt;explain"});
	var str_className = "Class:com.yusys.safe.base.common.template.TabBuilderTemplate.getTemplet('" + subfix + "')";
	JSPFree.createBillList("d1_B", str_className, null, {isSwitchQuery:"N",ishavebillquery:"N",onlyItems:"tab_name;tab_name_en;report_code"});
}

/**
 * 页面初始化后
 * @returns
 */
function AfterBodyLoad(){
	var dom_div = document.getElementById("d1_A_BillCardDiv");
	dom_div.style.overflow="hidden";  // 隐藏滚动框
	
	JSPFree.setBillQueryItemEditable("data_dt","日历",true);
}

/**
 * 确定按钮
 * @returns
 */
function onConfirm(){
	var jso_cardData = JSPFree.getBillCardFormValue(d1_A_BillCard);
	if(jso_cardData.data_dt == null || jso_cardData.data_dt == ""){
		JSPFree.alert("数据日期，不能为空！");
		
		return;
	}
	
	// 选择选中的数据..
	var jsy_tabs = JSPFree.getBillListSelectDatas(d1_B_BillList);
	if(jsy_tabs.length <= 0){
		JSPFree.alert("必须选择一条数据!");
		
		return;
	}
	
	// 远程调用
	var jso_par = {data_dt:jso_cardData.data_dt,explain:jso_cardData.explain,report_type:subfix,allTabs:jsy_tabs,org_no:str_LoginUserOrgNo};
	var jso_rt = JSPFree.doClassMethodCall("com.yusys.safe.fillingProcess.service.SafeFillingProcessBS", "batchInsertTaskJs", jso_par);

	JSPFree.closeDialog(jso_rt); // 关闭窗口,并有返回值
}

/**
 * 取消
 * @returns
 */
function onCancel(){
	JSPFree.closeDialog();
}