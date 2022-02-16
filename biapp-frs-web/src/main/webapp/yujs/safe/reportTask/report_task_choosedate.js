/**
 * 
 * <pre>
 * Title: 【报送管理】-【报文生成】
 * Description: 报文生成：打包下载页面
 * </pre>
 * @author wangxy31 
 * @version 1.00.00
   @date 2020年5月29日
 */

var reportType = "";
var className = "";

function AfterInit() {
	reportType = jso_OpenPars.reportType;
	className = jso_OpenPars.className;

	JSPFree.createBillCard("d1", className, ["下一步/onNext","取消/onCancel"], {onlyItems:"data_dt"});
}

/**
 * 页面初始化后
 * @returns
 */
function AfterBodyLoad() {
	var dom_div = document.getElementById("d1_BillCardDiv");
	dom_div.style.overflow="hidden";  // 隐藏滚动框
}

/**
 * 下一步
 * @returns
 */
function onNext() {
	var jso_cardData = JSPFree.getBillCardFormValue(d1_BillCard);
	var str_date = jso_cardData.data_dt;
	if(str_date==null || str_date==""){
		JSPFree.alert("数据日期不能为空!");
		return;
	}

	JSPFree.openDialogAndCloseMe("一键压缩打包下载本机构【" + str_date + "】的所有报文","/yujs/safe/reportTask/report_task_zip.js",780,300,{dataDt:str_date,reportType:reportType});
}

/**
 * 取消
 * @returns
 */
function onCancel() {
	JSPFree.closeDialog();
}