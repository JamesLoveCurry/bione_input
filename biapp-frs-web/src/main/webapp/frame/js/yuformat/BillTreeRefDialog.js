function AfterInit(){
  var str_templetcode = jso_OpenPars._templetcode;  //模板编码
  var jso_BillCardData = jso_OpenPars._BillCardData;   //父窗口卡片数据
  var type = parent.jso_OpenPars.type;
  if (type && SafeFreeUtil.isSafeReportType(type)) {
    // 获取查询条件
    var jsonSql = JSPFree.doClassMethodCall("com.yusys.safe.business.service.SafeRptOrgInfoCommFilter", "getSQLCondition", {
      reportType: parent.jso_OpenPars.type,
      loginUserOrgNo: str_LoginUserOrgNo
    });
    // 外汇各模块有单独的 type
    JSPFree.createBillTreeByBtn("d1", str_templetcode, ["确定/onConfirm/icon-ok", "取消/onCancel/icon-clear"], {
      "querycontion": jsonSql.sql,
      "autocondition": jsonSql.sql
    });
  } else {
    JSPFree.createBillTreeByBtn("d1", str_templetcode, ["确定/onConfirm/icon-ok", "取消/onCancel/icon-clear"]);
  }
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
  JSPFree.closeDialog(jso_rowdata);
}

//取消
function onCancel(){
JSPFree.closeDialog();
}
