var maskUtil = "";
function AfterInit(){
	maskUtil = FreeUtil.getMaskUtil();
	JSPFree.createSplitByBtn("d1","上下",220,["确定并分发/onConfirmAndDis","确定/onConfirm","取消/onCancel"]);
	JSPFree.createBillCard("d1_A","/biapp-east/freexml/east/fillingProcess/east_filling_process_CODE1_2.xml");  //卡片
	var whereSql = '';
	var jsn_result = JSPFree.doClassMethodCall("com.yusys.east.common.service.EastGetReportBS", "getReportList", null);
	if (jsn_result.code == 'success') {
		whereSql = jsn_result.data;
	}
	JSPFree.createBillList("d1_B","/biapp-east/freexml/east/busiModel/east_cr_tab_ref.xml",null,{isSwitchQuery:"N",ishavebillquery:"N", refWhereSQL: whereSql});
}

function AfterBodyLoad(){
	var dom_div = document.getElementById("d1_A_BillCardDiv");
	dom_div.style.overflow="hidden";  //隐藏滚动框
	
	JSPFree.setBillQueryItemEditable("data_dt","日历",true);
}

// 确定按钮
function onConfirm(){
	var jso_cardData = JSPFree.getBillCardFormValue(d1_A_BillCard);
	if(jso_cardData.data_dt == null || jso_cardData.data_dt == ""){
		JSPFree.alert("数据日期，不能为空！");
		return;
	}
	if(jso_cardData.distribute_type == null || jso_cardData.distribute_type == ""){
		JSPFree.alert("下发分类，不能为空！");
		return;
	}
	if(jso_cardData.org_no == null || jso_cardData.org_no == ""){
		JSPFree.alert("报送机构，不能为空！");
		return;
	}
	//选择选中的数据..
	var jsy_tabs = JSPFree.getBillListSelectDatas(d1_B_BillList);
	if(jsy_tabs.length <= 0){
		JSPFree.alert("至少选择一条数据!");
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
		distributeType: jso_cardData.distribute_type
	};
	maskUtil.mask();
	setTimeout(function () {
		var jso_rt = JSPFree.doClassMethodCall("com.yusys.east.process.service.EastCrProcessBSDMO", "batchInsertTask", jso_par);
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
	if(jso_cardData.distribute_type == null || jso_cardData.distribute_type == ""){
		JSPFree.alert("下发分类，不能为空！");
		return;
	}
	if (jso_cardData.org_no == null || jso_cardData.org_no == "") {
		JSPFree.alert("报送机构，不能为空！");
		return;
	}
	//选择选中的数据..
	var jsy_tabs = JSPFree.getBillListSelectDatas(d1_B_BillList);
	if(jsy_tabs.length <= 0){
		JSPFree.alert("至少选择一条数据!");

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
		distributeType: jso_cardData.distribute_type
	};

	maskUtil.mask();
	setTimeout(function () {
		var jso_rt = JSPFree.doClassMethodCall("com.yusys.east.process.service.EastCrProcessBSDMO", "onConfirmAndDis", jso_par);
		maskUtil.unmask();
		JSPFree.closeDialog(jso_rt);  //关闭窗口,并有返回值
	}, 100);
}
function onCancel(){
	JSPFree.closeDialog();
}