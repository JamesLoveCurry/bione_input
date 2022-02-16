function AfterInit(){
  JSPFree.createBillTreeByBtn("d1","/biapp-pscs/freexml/pscs/pscs_rpt_org_info.xml",["确定/onConfirm/icon-ok","取消/onCancel/icon-clear"],{"isCheckbox":"N","tree_isCheckboxCascade":"N"});
}

//确定
function onConfirm(){
  var selNodes = JSPFree.getBillTreeSelectNode(d1_BillTree)
  if(selNodes==null || selNodes.length<=0){
    $.messager.alert('提醒','必须选择一个结点数据!','info');  
  	return;
  }

  console.log(selNodes);
  var str_text = selNodes.id + "/" + selNodes.text + ";";  //把机构号与机构名称用斜杠拼接!

  JSPFree.closeDialog({"org_nm":str_text});
}

//取消
function onCancel(){
	JSPFree.closeDialog();
}
