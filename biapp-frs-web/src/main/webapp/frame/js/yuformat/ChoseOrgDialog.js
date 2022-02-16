function AfterInit(){
  JSPFree.createBillTreeByBtn("d1","/biapp-east/freexml/common/bione_org_info_08_CODE1.xml",["确定/onConfirm/icon-ok","取消/onCancel/icon-clear"]);
}

//确定
function onConfirm(){
  var selNode = JSPFree.getBillTreeSelectNode(d1_BillTree);  //
  if(selNode==null){
    $.messager.alert('提醒','必须选择一个结点数据!','info');  
  	return;
  }

  var isRoot = JSPFree.isBillTreeNodeRoot(d1_BillTree,selNode);
  if(isRoot){
  	$.messager.alert('提醒','不能选择根结点!','info');  
  	return;
  }

  var jso_rowdata = JSPFree.getBillTreeSelectData(d1_BillTree);
  console.log(jso_rowdata);
  JSPFree.closeDialog(jso_rowdata);
}

//取消
function onCancel(){
JSPFree.closeDialog();
}
