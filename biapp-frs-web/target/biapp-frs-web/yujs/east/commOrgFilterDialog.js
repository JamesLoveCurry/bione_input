function AfterInit(){
  JSPFree.createBillTreeByBtn("d1","/biapp-east/freexml/common/rpt_org_info_CommFilter.xml",["确定/onConfirm/icon-ok","取消/onCancel/icon-clear"],{"isCheckbox":"Y","tree_isCheckboxCascade":"Y"});
}

//确定
function onConfirm(){
  var selNodes = JSPFree.getBillTreeCheckedNodes(d1_BillTree);  //
  if(selNodes==null || selNodes.length<=0){
    $.messager.alert('提醒','必须选择一个结点数据!','info');  
  	return;
  }

  var asy_data = JSPFree.getBillTreeCheckedDatas(d1_BillTree);
  console.log(asy_data[0]); //
  var str_text = "";
  for(var i=0;i<asy_data.length;i++){
    str_text = str_text + asy_data[i]["org_no"] + "/" + asy_data[i]["org_nm"] + ";";  //把机构号与机构名称用斜杠拼接!
  }

  JSPFree.closeDialog({"org_nm":str_text});
}

//取消
function onCancel(){
JSPFree.closeDialog();
}
