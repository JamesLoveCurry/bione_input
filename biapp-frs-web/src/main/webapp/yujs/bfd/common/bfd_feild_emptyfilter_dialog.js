/**
 * 
 * <pre>
 * Title: 明细数据中【为空】【不为空】字段，弹窗逻辑
 * Description:
 * </pre>
 * @author wangxy31 
 * @version 1.00.00
   @date 2021年8月19日
 */
var tabName = null;
function AfterInit() {
	tabName = jso_OpenPars.tabName;

	JSPFree.createBillTreeByBtn("d1","/biapp-bfd/freexml/common/bfd_field_empty_comm_filter.xml",["确定/onConfirm/icon-ok","取消/onCancel/icon-clear"],
			{"isCheckbox": "Y",
			"tree_isCheckboxCascade": "N",
			"autoquery": "Y",
            "querycontion": "tab_name = '"+tabName+"' and is_export = 'Y' order by col_no"});
}

/**
 * 确定选择机构操作
 * 选择机构树上面的机构，点击确定按钮，返回所选择机构
 * @returns
 */
function onConfirm() {
	var selNodes = JSPFree.getBillTreeCheckedNodes(d1_BillTree);
  
	if (selNodes == null || selNodes.length <= 0) {
		$.messager.alert('提醒','必须选择一个结点数据!','info');  
		return;
	}

	var asy_data = JSPFree.getBillTreeCheckedDatas(d1_BillTree);
	console.log(asy_data[0]);
  
	var str_text = "";
	for(var i=0;i<asy_data.length;i++){
		str_text = str_text + asy_data[i]["col_name_en"]+"/"+asy_data[i]["col_name"]+ ";";
	}

	JSPFree.closeDialog({"col_name_en": str_text});
}

/**
 * 取消
 * @returns
 */
function onCancel() {
	JSPFree.closeDialog();
}
