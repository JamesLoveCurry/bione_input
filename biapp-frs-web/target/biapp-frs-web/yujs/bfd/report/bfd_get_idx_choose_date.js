/**
 * 
 * <pre>
 * Title: 【报送管理】-【报文生成】-【指标取数】-选择日期
 * Description: 报送管理-报文生成-指标取数：选择日期的页面
 * 此页面提供了指标取数的日期操作，选择日期从相应的指标中取数，生成明细季报的明细数据
 * </pre>
 * @author liangzy5 
 * @version 1.00.00
   @date 2020年10月09日
 */

function AfterInit() {
	JSPFree.createSplitByBtn("d1", "上下", 120, ["确定生成/onConfirm", "取消/onCancel" ], false);
	JSPFree.createBillCard("d1_A", "/biapp-bfd/freexml/report/bfd_report_choose_date.xml");
	
	JSPFree.createBillList("d1_B", "/biapp-bfd/freexml/common/bfd_cr_tab_ref.xml", null, 
	{isSwitchQuery:"N",ishavebillquery:"N",querycontion:"datatype='指标类' and report_code !='20201'",autocondition:"datatype='指标类' and report_code !='20201'"});
}

/**
 * 页面初始化后，隐藏滚动框
 * @returns
 */
function AfterBodyLoad() {
	var dom_div = document.getElementById("d1_A_BillCardDiv");
	dom_div.style.overflow="hidden";  // 隐藏滚动框
}

/**
 * 确定操作，点击按钮，确定取数
 * @returns
 */
function onConfirm() {
	var jso_cardData = JSPFree.getBillCardFormValue(d1_A_BillCard);
	var str_date = jso_cardData.data_dt;
	
	if (str_date == null || str_date == "") {
		$.messager.alert('提示', '必须选择数据日期!', 'info');
		return;
	}
	var selectDatas = d1_B_BillList.datagrid('getSelections');
	var jso_tabNames = new Array();
	var jso_tabNameEns = new Array();
	var jso_reportCodes = new Array();
	for(var i=0; i<selectDatas.length; i++){
		jso_tabNames.push(selectDatas[i].tab_name);
		jso_tabNameEns.push(selectDatas[i].tab_name_en);
		jso_reportCodes.push(selectDatas[i].report_code);
	}
	//若选择了表名
	var jso_par = null;
	if(jso_tabNameEns.length>0){
		jso_par = {
				tab_name_en : jso_tabNameEns,
				tab_name : jso_tabNames,
				report_code : jso_reportCodes,
				data_dt : str_date,
			};
	}else{
		jso_par = {
				data_dt : str_date,
			};
	}
	
	//远程调用确认指标有数据
	var jso_check = JSPFree.doClassMethodCall("com.yusys.bfd.report.service.BfdCrReportBS", "checkIdxStatus", jso_par);
	if (jso_check.code == "-999") {
	       JSPFree.alert(jso_check.msg);
	       return;
	}
	
	var jso_rt = JSPFree.doClassMethodCall("com.yusys.bfd.report.service.BfdCrReportBS", "getIdx", jso_par);
	if(jso_rt.code == "-999"){
		JSPFree.closeDialog({msg:"指标取数失败，请稍后重试或者联系管理员！"});
	}else{
		JSPFree.closeDialog({msg:"指标取数成功！"});
	}
}

/**
 * 取消
 * @returns
 */
function onCancel() {
	JSPFree.closeDialog();
}