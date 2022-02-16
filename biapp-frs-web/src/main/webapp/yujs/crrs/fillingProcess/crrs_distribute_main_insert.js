/**
 * 
 * <pre>
 * Title: 【报表处理】-【报表下发】
 * Description: 报表下发：新增页面
 * 此页面提供了报表下发的新增下发任务的功能，选择数据日期和数据表，创建新增下发任务
 * </pre>
 * @author wangxy31 
 * @version 1.00.00
   @date 2021年10月22日
 */
var org_no = "";
var maskUtil = "";
function AfterInit() {
	maskUtil = FreeUtil.getMaskUtil();
	org_no = jso_OpenPars.org_no;
	
	JSPFree.createSplitByBtn("d1","上下",220,["确定并分发/onConfirmAndDis","确定/onConfirm","取消/onCancel"]);
	JSPFree.createBillCard("d1_A","/biapp-crrs/freexml/crrs/fillingProcess/crrs_filling_process_CODE1_2.xml");  //卡片
	JSPFree.createBillList("d1_B","/biapp-crrs/freexml/crrs/common/crrs_cr_tab_main_ref.xml",null,{isSwitchQuery:"N",ishavebillquery:"N",list_ischeckstyle:"Y",list_ismultisel:"Y"});
}

function AfterBodyLoad() {
	var dom_div = document.getElementById("d1_A_BillCardDiv");
	dom_div.style.overflow="hidden";  // 隐藏滚动框
	JSPFree.setBillQueryItemEditable("data_dt","日历",true);

	var org_class = CrrsFreeUtil.getOrgClass(org_no);
	var org_nm = CrrsFreeUtil.getOrgNm(org_no);
	if (org_class == CrrsFreeUtil.getCrrsOrgClass().zh) {
		JSPFree.setBillCardItemValue(d1_A_BillCard, "org_no", org_no + '/' + org_nm + ';');
	} else {
		JSPFree.setBillCardItemValue(d1_A_BillCard, "org_no", org_no + '/' + org_nm + ';');
		JSPFree.setBillCardItemEditable(d1_A_BillCard, "org_no", false);
	}
}

/**
 * 确认新增下发任务按钮
 * @returns
 */
function onConfirm() {
	var jso_cardData = JSPFree.getBillCardFormValue(d1_A_BillCard);
	if(jso_cardData.data_dt == null || jso_cardData.data_dt == ""){
		JSPFree.alert("数据日期，不能为空！");
		
		return;
	}

	if (jso_cardData.org_no == null || jso_cardData.org_no == "") {
		JSPFree.alert("机构，不能为空！");
		return;
	}

	// 选择选中的数据
	var jsy_tabs = JSPFree.getBillListSelectDatas(d1_B_BillList);
	if (jsy_tabs.length <= 0) {
		JSPFree.alert("必须选择一条数据!");
		
		return;
	}

	var jso_tabNames = new Array();
	var jso_tabNameEns = new Array();
	var jso_tabCode = new Array();
	for(var i=0; i<jsy_tabs.length; i++){
		jso_tabNames.push(jsy_tabs[i].tab_name);
		jso_tabNameEns.push(jsy_tabs[i].tab_name_en);
		jso_tabCode.push(jsy_tabs[i].rid);
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
	
	// 下发操作，判断重复数据（表+机构+日期）
	var checkPar = {
			data_dt: jso_cardData.data_dt,
			tab_name_en: jso_tabNameEns,
			org_no: orgNoArray
		};

	var checkJso = JSPFree.doClassMethodCall("com.yusys.crrs.process.service.CrrsCrProcessBSDMO", "checkBatchInsertTask", checkPar);
	if (checkJso.msg != "OK") {
		var backData = checkJso.data;
		var newData = "";
		for (var i=0; i<backData.length; i++) {
			newData = newData + backData[i] + ";"
		}
		JSPFree.alert("新增失败，当前选中的数据存在重复记录:" + newData);
		
		return;
	}
	
	// 远程调用
	var jso_par = {
		data_dt: jso_cardData.data_dt,
		explain: jso_cardData.explain,
		tab_name: jso_tabNames,
		tab_name_en: jso_tabNameEns,
		tab_code: jso_tabCode,
		org_no: orgNoArray,
		org_no_name: orgNoArrayName
	};
	maskUtil.mask();
	setTimeout(function () {
		var jso_rt = JSPFree.doClassMethodCall("com.yusys.crrs.process.service.CrrsCrProcessBSDMO", "batchInsertTask", jso_par);
		maskUtil.unmask();
		JSPFree.closeDialog(jso_rt); // 关闭窗口,并有返回值
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
	
	if (jso_cardData.org_no == null || jso_cardData.org_no == "") {
		JSPFree.alert("报送机构，不能为空！");
		return;
	}

	// 选择选中的数据
	var jsy_tabs = JSPFree.getBillListSelectDatas(d1_B_BillList);
	if(jsy_tabs.length <= 0){
		JSPFree.alert("必须选择一条数据!");

		return;
	}

	var jso_tabNames = new Array();
	var jso_tabNameEns = new Array();
	var jso_tabCode = new Array();
	for(var i=0; i<jsy_tabs.length; i++){
		jso_tabNames.push(jsy_tabs[i].tab_name);
		jso_tabNameEns.push(jsy_tabs[i].tab_name_en);
		jso_tabCode.push(jsy_tabs[i].rid);
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
	
	// 下发操作，判断重复数据（表+机构+日期）
	var checkPar = {
			data_dt: jso_cardData.data_dt,
			tab_name_en: jso_tabNameEns,
			org_no: orgNoArray
		};

	var checkJso = JSPFree.doClassMethodCall("com.yusys.crrs.process.service.CrrsCrProcessBSDMO", "checkBatchInsertTask", checkPar);
	if (checkJso.msg != "OK") {
		var backData = checkJso.data;
		var newData = "";
		for (var i=0; i<backData.length; i++) {
			newData = newData + backData[i] + ";"
		}
		JSPFree.alert("新增失败，当前选中的数据存在重复记录:" + newData);
		
		return;
	}
	
	// 远程调用
	var jso_par = {
		data_dt: jso_cardData.data_dt,
		explain: jso_cardData.explain,
		tab_name: jso_tabNames,
		tab_name_en: jso_tabNameEns,
		tab_code: jso_tabCode,
		org_no: orgNoArray,
		org_no_name: orgNoArrayName
	};

	maskUtil.mask();
	setTimeout(function () {
		var jso_rt = JSPFree.doClassMethodCall("com.yusys.crrs.process.service.CrrsCrProcessBSDMO", "onConfirmAndDis", jso_par);
		maskUtil.unmask();
		JSPFree.closeDialog(jso_rt);  //关闭窗口,并有返回值
	}, 100);
}

function onCancel(){
	JSPFree.closeDialog();
}