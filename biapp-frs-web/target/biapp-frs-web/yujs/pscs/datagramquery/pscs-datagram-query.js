function AfterInit(){
    JSPFree.createBillList("d1","/biapp-pscs/freexml/pscs/datagramquery/pscs_datagram_query.xml",null,{isSwitchQuery:"N"});
    
	JSPFree.queryDataByConditon2(d1_BillList, getQueryCondition());
	JSPFree.setBillListForceSQLWhere(d1_BillList,getQueryCondition());
}

function getQueryCondition(){
	var jso_rt = JSPFree.doClassMethodCall("com.yusys.pscs.common.PscsOrgQueryCondition","getQueryCondition",{"_loginUserOrgNo" : str_LoginUserOrgNo});
	var condition = "";
	if(jso_rt.msg == "ok"){
		condition = jso_rt.condition;
	}
	return condition;
}

//打包下载
function onZipAndDownload() {
	JSPFree.openDialog("一键打包下载本机构某一时间的所有报文","/yujs/pscs/datagramtask/pscs-datagram-task-zipdownlod.js",350,350);
}

//sec打包下载
function onSecAndDownload() {
	JSPFree.openDialog("一键打包下载本机构某一时间的所有加密报文","/yujs/pscs/datagramtask/pscs-datagram-task-secdownlod.js",350,350);
}