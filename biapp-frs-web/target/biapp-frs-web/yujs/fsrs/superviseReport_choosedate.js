//初始化界面,菜单配置路径是【/frs/yufreejs?js=/yujs/east/58table.js】

function AfterInit(){
	JSPFree.createBillCard("d1","/biapp-fsrs/freexml/fsrs/supervise_report_download.xml",["下一步/onNext","取消/onCancel"]);
}

function onNext(){
	var jso_cardData = JSPFree.getBillCardFormValue(d1_BillCard);
	var str_date = jso_cardData.data_dt;
	var str_org_no = jso_cardData.org_no;
	if(str_date==null || str_date==""){
		JSPFree.alert("数据日期不能为空!");
		return;
	}

	JSPFree.openDialogAndCloseMe("打包压缩下载报文","/yujs/fsrs/superviseReport_zip.js",780,300,{data_dt:str_date,org_no:str_org_no});
}

/**
 * 加载主体之后，隐藏滚动条
 * @returns
 */
function AfterBodyLoad() {
	var dom_div = document.getElementById("d1_BillCardDiv");
	dom_div.style.overflow="hidden"; // 隐藏滚动框
}

function onCancel(){
	JSPFree.closeDialog();
}