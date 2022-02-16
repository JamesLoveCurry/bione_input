var fbFileName = "";

function AfterInit() {
	fbFileName = jso_OpenPars.fbFileName;
	var jso_ListConfig = {
		list_ispagebar: "Y",
		ishavebillquery: "Y",
		isSwitchQuery: "N",
		autoquery: "N",
		querycontion: "fb_file_name='"+fbFileName+"'"
	};
	JSPFree.createBillList("d1", "/biapp-cr/freexml/feedback/feedbackFile.xml", null, jso_ListConfig);
	JSPFree.queryDataByConditon(d1_BillList,"");

}

