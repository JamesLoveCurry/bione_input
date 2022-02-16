//初始化界面
function AfterInit() {
	var single = FreeUtil.getClientEnv("single"); //获取客户端变量，单一法人
	var customer_code = single.customer_code;
	var str_data_dt = single.data_dt;
	
	JSPFree.createBillList("d1","/biapp-crrs/freexml/crrs/ent/crrs_ent_credit_ref.xml",["确定/onConfirm/icon-p21","取消/onCancel/icon-undo"],{list_btns:"",autoquery:"N",ishavebillquery:"N"});
	
	var sql = "SELECT * FROM crrs_ent_credit WHERE data_dt='"+str_data_dt+"' and customer_code = '"+customer_code+"' AND customer_type = '2' "+
	 		  "union "+
	 		  "select * FROM crrs_ent_credit where CREDIT_CODE in " +
	 		  "(SELECT DISTINCT CREDIT_CODE FROM crrs_ent_group_client WHERE customer_client_code = '"+customer_code+"' and data_dt='"+str_data_dt+"')";
	
	JSPFree.queryDataBySQL(d1_BillList, sql)
}

function onConfirm(){
	var jso_OpenPars = d1_BillList.datagrid('getSelected');
	if (jso_OpenPars == null) {
		$.messager.alert('提示', '必须选择一条记录进行操作', 'warning');
		return;
	}

	var credit_code = jso_OpenPars.credit_code;
	
	JSPFree.closeDialog({credit_code:credit_code});
}

function onCancel(){
	JSPFree.closeDialog();
}