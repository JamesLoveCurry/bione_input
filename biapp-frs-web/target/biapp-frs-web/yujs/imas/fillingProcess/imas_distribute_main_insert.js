/**
 * 
 * <pre>
 * Title: 【报表处理】-【报表下发】
 * Description: 报表下发：新增页面
 * 此页面提供了报表下发的新增下发任务的功能，选择数据日期和数据表，创建新增下发任务
 * </pre>
 * @author mkx
 * @version 1.00.00
   @date 2021年4月17日
 */
var org_no = "";
var maskUtil = "";
function AfterInit(){
	var whereSql = '';
	maskUtil = FreeUtil.getMaskUtil();
	var jsn_result = JSPFree.doClassMethodCall("com.yusys.imas.common.service.ImasGetReportBS", "getReportList", null);
	if (jsn_result.code == 'success') {
		whereSql = jsn_result.data;
	}
	JSPFree.createSplitByBtn("d1","上下",220,["确定并分发/onConfirmAndDis","确定/onConfirm","取消/onCancel"]);
	JSPFree.createBillCard("d1_A","/biapp-imas/freexml/fillingProcess/imas_filling_process_CODE1_2.xml");  //卡片
	JSPFree.createBillList("d1_B","/biapp-imas/freexml/common/imas_tab_ref.xml",null,{isSwitchQuery:"N",ishavebillquery:"N", refWhereSQL: whereSql});
	
	//通过当前登录人所属内部机构获取报送机构号
	var rpt_org_no = "";
	var jso_report_org = JSPFree.doClassMethodCall("com.yusys.imas.common.service.ImasValidateQueryConditionBS", "getReportOrgNoCondition", {_loginUserOrgNo: str_LoginUserOrgNo});
	if (jso_report_org.msg == "ok") {
		rpt_org_no = jso_report_org.data;
	}
	// 如果当前机构为报送机构，则设置默认值，否则不设置值
	if (rpt_org_no) {
		var result = JSPFree.doClassMethodCall("com.yusys.imas.common.service.ImasValidateQueryConditionBS", "checkOrgIsReportOrg", {
			rptOrgNo: rpt_org_no,
			isSingle: false
		});
		if (result.result) {
			JSPFree.setBillCardItemValue(d1_A_BillCard, "org_no", result.result);
		}
	}
}

function AfterBodyLoad(){
	var dom_div = document.getElementById("d1_A_BillCardDiv");
	dom_div.style.overflow="hidden";  //隐藏滚动框
	JSPFree.setBillQueryItemEditable("data_dt","日历",true);
	var jsn_result = JSPFree.doClassMethodCall("com.yusys.imas.process.service.ImasCrProcessBSDMO", "getDeptNo", null);
	if (jsn_result.code == 'fail') {
		if (jsn_result.deptNo) {
			JSPFree.setBillCardValues(d1_A_BillCard, {dept_no:jsn_result.deptNo, dept_name: jsn_result.deptName});
			JSPFree.setBillCardItemEditable(d1_A_BillCard, "dept_no", false);
		}
	}
}

/**
 * 确认新增下发任务按钮
 * @returns
 */
function onConfirm(){
	var jso_cardData = JSPFree.getBillCardFormValue(d1_A_BillCard);
	if(jso_cardData.data_dt == null || jso_cardData.data_dt == ""){
		JSPFree.alert("数据日期，不能为空！");
		
		return;
	}
	var jso_cardData = JSPFree.getBillCardFormValue(d1_A_BillCard);
	if(jso_cardData.dept_no == null || jso_cardData.dept_no == ""){
		JSPFree.alert("所属条线，不能为空！");

		return;
	}
	
	if (jso_cardData.org_no == null || jso_cardData.org_no == "") {
		JSPFree.alert("报送机构，不能为空！");
		return;
	}
	//选择选中的数据..
	var jsy_tabs = JSPFree.getBillListSelectDatas(d1_B_BillList);
	if(jsy_tabs.length <= 0){
		JSPFree.alert("必须选择一条数据!");
		
		return;
	}
	
	var jso_tabNames = new Array();
	var jso_tabNameEns = new Array();
	for(var i=0; i<jsy_tabs.length; i++){
		jso_tabNames.push(jsy_tabs[i].tab_name);
		jso_tabNameEns.push(jsy_tabs[i].tab_name_en);
	}
	var orgNoArray = new Array();
	var orgNoArrayName = new Array();
	var arr_org = jso_cardData.org_no.split(";");
	for (var i = 0; i < arr_org.length; i++) {
		var temp = arr_org[i].substring(0, arr_org[i].indexOf("/"));
		var tempName = arr_org[i].substring(arr_org[i].indexOf("/") + 1);
		if (temp) {
			orgNoArray.push(temp);
			orgNoArrayName.push(tempName);
		}
	}
	//远程调用
	var jso_par = {
		data_dt: jso_cardData.data_dt,
		explain: jso_cardData.explain,
		tab_name: jso_tabNames,
		tab_name_en: jso_tabNameEns,
		org_no: orgNoArray,
		org_no_name: orgNoArrayName,
		dept_no: jso_cardData.dept_no
	};

	maskUtil.mask();
	setTimeout(function () {
		/*var jso_check = JSPFree.doClassMethodCall("com.yusys.imas.process.service.ImasCrProcessBSDMO", "checkInsertTaskStatus", jso_par);
		if (jso_check.code == "-999") {
			maskUtil.unmask();
			JSPFree.alert(jso_check.msg);
			return;
		}*/
		var jso_rt = JSPFree.doClassMethodCall("com.yusys.imas.process.service.ImasCrProcessBSDMO", "batchInsertTask", jso_par);
		maskUtil.unmask();
		JSPFree.closeDialog(jso_rt);  //关闭窗口,并有返回值
	}, 100);
}


/**
 * 确认新增下发并分发任务按钮
 * @returns
 */
function onConfirmAndDis(){
	var jso_cardData = JSPFree.getBillCardFormValue(d1_A_BillCard);
	if(jso_cardData.data_dt == null || jso_cardData.data_dt == ""){
		JSPFree.alert("数据日期，不能为空！");

		return;
	}
	var jso_cardData = JSPFree.getBillCardFormValue(d1_A_BillCard);
	if(jso_cardData.dept_no == null || jso_cardData.dept_no == ""){
		JSPFree.alert("所属条线，不能为空！");

		return;
	}
	if (jso_cardData.org_no == null || jso_cardData.org_no == "") {
		JSPFree.alert("报送机构，不能为空！");
		return;
	}
	//选择选中的数据..
	var jsy_tabs = JSPFree.getBillListSelectDatas(d1_B_BillList);
	if(jsy_tabs.length <= 0){
		JSPFree.alert("必须选择一条数据!");

		return;
	}

	var jso_tabNames = new Array();
	var jso_tabNameEns = new Array();
	for(var i=0; i<jsy_tabs.length; i++){
		jso_tabNames.push(jsy_tabs[i].tab_name);
		jso_tabNameEns.push(jsy_tabs[i].tab_name_en);
	}
	var orgNoArray = new Array();
	var orgNoArrayName = new Array();
	var arr_org = jso_cardData.org_no.split(";");
	for (var i = 0; i < arr_org.length; i++) {
		var temp = arr_org[i].substring(0, arr_org[i].indexOf("/"));
		var tempName = arr_org[i].substring(arr_org[i].indexOf("/") + 1);
		if (temp) {
			orgNoArray.push(temp);
			orgNoArrayName.push(tempName);
		}
	}
	//远程调用
	var jso_par = {
		data_dt: jso_cardData.data_dt,
		explain: jso_cardData.explain,
		tab_name: jso_tabNames,
		tab_name_en: jso_tabNameEns,
		org_no: orgNoArray,
		org_no_name: orgNoArrayName,
		dept_no: jso_cardData.dept_no
	};

	maskUtil.mask();
	setTimeout(function () {
		/*var jso_check = JSPFree.doClassMethodCall("com.yusys.imas.process.service.ImasCrProcessBSDMO", "checkInsertTaskStatus", jso_par);
		if (jso_check.code == "-999") {
			maskUtil.unmask();
			JSPFree.alert(jso_check.msg);
			return;
		}*/
		var jso_rt = JSPFree.doClassMethodCall("com.yusys.imas.process.service.ImasCrProcessBSDMO", "onConfirmAndDis", jso_par);
		maskUtil.unmask();
		JSPFree.closeDialog(jso_rt);  //关闭窗口,并有返回值
	}, 100);
}



function onCancel(){
	JSPFree.closeDialog();
}