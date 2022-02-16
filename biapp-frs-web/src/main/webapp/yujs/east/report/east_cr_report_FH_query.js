//初始化界面,菜单配置路径是【/frs/yufreejs?js=/yujs/east/east_cr_report_query.js】
function AfterInit(){
    JSPFree.createBillList("d1","/biapp-east/freexml/east/report/east_cr_report_FH_ref.xml");
}

//打包下载
function onZipAndDownload() {
	JSPFree.openDialog("一键打包下载本机构某一时间的所有报文","/yujs/east/report/superviseReport_choosedate.js",350,350);
}