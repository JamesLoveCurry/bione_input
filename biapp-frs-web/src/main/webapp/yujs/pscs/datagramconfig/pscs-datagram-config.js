function AfterInit(){
	JSPFree.createBillList("d1","/biapp-pscs/freexml/pscs/datagramconfig/pscs_datagram_config.xml",null,{isSwitchQuery:"N"});
}

//删除
function onDeleteConfig(){
	// 执行删除(批量)
	JSPFree.doBillListBatchDelete(d1_BillList);
}
