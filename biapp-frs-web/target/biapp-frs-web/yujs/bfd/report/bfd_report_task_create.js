/**
 * 
 * <pre>
 * Title: 【报送管理】-【报文生成】
 * Description: 报送管理-报文生成：新增报文任务界面
 * 此页面提供了报送生成-新增报文任务的相关操作，选择机构，日期和报送频率，即可生成相应的报文任务
 * </pre>
 * @author liangzy5 
 * @version 1.00.00
   @date 2020年8月21日
 */

var org_no = "";
var org_class = "";
var org_nm = "";

function AfterInit() {
	org_no = jso_OpenPars.org_no;
	org_class = jso_OpenPars.org_class;
	org_nm = jso_OpenPars.org_nm;
	
	JSPFree.createSplitByBtn("d1", "上下", 120, ["确定生成/onConfirm", "取消/onCancel" ], false);
	JSPFree.createBillCard("d1_A", "/biapp-bfd/freexml/report/bfd_cr_report_ref.xml");
	
	JSPFree.createBillList("d1_B", "/biapp-bfd/freexml/common/bfd_cr_tab_ref.xml", null, {isSwitchQuery:"N",ishavebillquery:"N"});
}

/**
 * 页面加载完成之后执行的方法，对滚动条进行隐藏，获取机构号并对机构号进行赋值，置灰不允许编辑
 * @returns
 */
function AfterBodyLoad() {
	var dom_div = document.getElementById("d1_A_BillCardDiv");
	dom_div.style.overflow="hidden"; // 隐藏滚动框
	
	//添加监听事件放在禁用的前面，因为在添加监听事件的时候，会把禁用放开！！
//	FreeUtil.addBillCardItemEditListener(d1_A_BillCard,onEditChanged); //监听报送频率的变化
	
	if(BfdFreeUtil.getBfdOrgClass().fh==org_class){
		// 如果是分行，则机构赋默认值并且置灰
		JSPFree.setBillCardItemValue(d1_A_BillCard, "org_no", org_no+"/"+org_nm);
		JSPFree.setBillCardItemEditable(d1_A_BillCard, "org_no", false);
	}else if(BfdFreeUtil.getBfdOrgClass().zh==org_class){
		JSPFree.setBillCardItemValue(d1_A_BillCard, "org_no", org_no+"/"+org_nm);
	}
}

/*function onEditChanged(_billCard,_itemKey,_value){
	console.log("字段：[" + _itemKey + "],值["+_value.value+"]");
	if(_itemKey=="report_type"){
		if(_value.value=="月报"){
			JSPFree.queryDataBySQL(d1_B_BillList, "select * from bfd_cr_tab where tab_belong='业务表' and report_type='月报'");
		}else if (_value.value=="季报"){
			JSPFree.queryDataBySQL(d1_B_BillList, "select * from bfd_cr_tab where tab_belong='业务表' and report_type='季报'");
		}
	}
}*/

/**
 * 确定生成
 * 判断是否选择了记录，若选择了记录，则新增这些报表的报文任务
 * 若没有选择记录，则默认生成所有报表的报文任务
 */
function onConfirm() {
	var form_vlue = JSPFree.getBillCardFormValue(d1_A_BillCard);
	
	var str_date = form_vlue.data_dt;
	if (str_date == null || str_date == "") {
		$.messager.alert('提示', '必须选择日期!', 'info');
		return;
	}
	
	var str_org = form_vlue.org_no;
	if (str_org == null || str_org == "") {
        $.messager.alert('提示', '必须选择机构!', 'info');
        return;
    }
	
    var str_report_type = form_vlue.report_type;
    if (str_report_type == null || str_report_type == "") {
        $.messager.alert('提示', '必须选择报送频率!', 'info');
        return;
    }
	
	var selectDatas = d1_B_BillList.datagrid('getSelections');
	var arr_org = str_org.split(";");
	var orgStr = ""; //970110,990100，后面可以考虑这里验证机构号！！因为自定义参照放开了编辑，str_org不指定能拿到什么东西呢
	//但是当前对程序无碍，不影响运行
	for(var i=0; i<arr_org.length; i++){
		orgStr = orgStr + arr_org[i].substring(0, arr_org[i].indexOf("/"))+",";
	}
	
	//若选择了表名
	var jso_par = null;
	if(selectDatas.length>0){
		jso_par = {
				chooseTasks : selectDatas,
				data_dt : str_date,
		        str_org: orgStr,
		        report_type: str_report_type
			};
	}else{
		/*jso_par = {
				data_dt : str_date,
		        str_org: orgStr,
		        report_type: str_report_type
			};*/
		$.messager.alert('提示', '至少选择一张表生成报文', 'info');
	}

	var jso_rt = JSPFree.doClassMethodCall("com.yusys.bfd.report.service.BfdCrReportBS","createReportTask",jso_par);

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