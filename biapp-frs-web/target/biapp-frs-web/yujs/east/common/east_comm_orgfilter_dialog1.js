/**
 * 
 * <pre>
 * Title: 明细数据中【内部机构号】，弹窗逻辑
 * Description:
 * </pre>
 * @author wangxy31 
 * @version 1.00.00
   @date 2020年8月6日
 */
function AfterInit() {
	JSPFree.createBillTreeByBtn("d1","/biapp-east/freexml/common/east_rpt_org_info_comm_filter1.xml",["确定/onConfirm/icon-ok","取消/onCancel/icon-clear"],{"isCheckbox":"Y","tree_isCheckboxCascade":"N"});
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
		str_text = str_text + asy_data[i]["org_no"]+"/"+asy_data[i]["org_nm"]+ ";";
	}

	JSPFree.closeDialog({"org_no": str_text});
}

/**
 * 取消
 * @returns
 */
function onCancel() {
	JSPFree.closeDialog();
}
