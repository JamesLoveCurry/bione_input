//初始化界面,菜单配置路径是【/frs/yufreejs?js=/yujs/east/dataqualitypoc.js】
function AfterInit(){
	JSPFree.createBillList("d1","/biapp-east/freexml/east/result/east_cr_data_quality_poc_CODE.xml",null,{isSwitchQuery:"N"});
}

function onView(){
	var jso_OpenPars = {};
	JSPFree.openDialog("数据质量报告","/yujs/east/result/dataqualitypoc1.js", 960, 560, jso_OpenPars);
}