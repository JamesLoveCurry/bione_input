/**
 * 
 * <pre>
 * Title: 明细数据中【表名】，弹窗逻辑
 * Description:
 * </pre>
 * @author wangxy31 
 * @version 1.00.00
   @date 2020年8月6日
 */
var org_no;
function AfterInit() {
	var whereSql = '';
	var jsn_result = JSPFree.doClassMethodCall("com.yusys.bfd.business.service.BfdGetReportBS", "getObjList", null);
	if (jsn_result.code == 'success') {
		whereSql = jsn_result.data;
	}
	// 获取报送机构权限
	var jso_report_org = JSPFree.doClassMethodCall("com.yusys.bfd.common.service.BfdValidateQueryConditionBS","getCurrentUserRptOrgNo",{_loginUserOrgNo:str_LoginUserOrgNo});
	if(jso_report_org.msg=="ok"){
		org_no = jso_report_org.data;
		whereSql += " and " + org_no;
	}

	JSPFree.createBillList("d1","/biapp-bfd/freexml/common/bfd_dept_ref.xml",["确定/onConfirm/icon-ok","取消/onCancel/icon-clear"],{isSwitchQuery:"N", refWhereSQL: whereSql});
}

/**
 * 确定选择机构操作
 * 选择机构树上面的机构，点击确定按钮，返回所选择机构
 * @returns
 */
function onConfirm() {
	var dept_no = '';
	var dept_name = '';
	var selectDatas = d1_BillList.datagrid('getSelections');
	if (selectDatas.length == 0) {
		$.messager.alert('提示', '至少选择一条记录进行操作', 'warning');
	}
	for(var i=0; i<selectDatas.length; i++){
		dept_no += selectDatas[i].dept_no + ";";
		dept_name += selectDatas[i].dept_name + ";";
	}
	JSPFree.closeDialog({"dept_no": dept_no, "dept_name": dept_name});
}

/**
 * 取消
 * @returns
 */
function onCancel() {
	JSPFree.closeDialog();
}
