var str_rid = null;
var str_sql = null;
function AfterInit(){
	str_rid = jso_OpenPars.reportId;
	str_sql = "report_rid='" + str_rid + "'";
	JSPFree.createBillList("d1","/biapp-cams/freexml/cams/datagram/cams_datagram_task_log.xml"); //创建列表
	JSPFree.queryDataByConditon(d1_BillList,str_sql);
}

function onCancel(){
	JSPFree.closeDialog(null);
}