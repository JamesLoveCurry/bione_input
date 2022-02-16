/**
 * 
 * <pre>
 * Title: 【报送管理】-【报文生成】
 * Description: 报送管理-报文生成：启动生成报文页面
 * 此页面提供了启动生成报文的相关操作和管理，可以允许用户启动生成报文，监控报文生成状态
 * </pre>
 * @author liangzy5 
 * @version 1.00.00
   @date 2020年8月21日
 */

function AfterInit() {
	JSPFree.createSpanByBtn("d1",["刷新/onRefresh","取消/onCancel"]);
}

/**
 * 页面加载完成之后执行的方法，每隔5秒钟刷新一次页面
 * @returns
 */
function AfterBodyLoad() {
	doRefreshData();
	
	// 每5秒钟刷新一次
	self.setInterval(doRefreshData,5000);
}

/**
 * 刷新数据
 * @returns
 */
function doRefreshData() {
	// 加载数据
	var jso_statu = JSPFree.doClassMethodCall("com.yusys.imas.report.service.ImasSendReportBS", "checkSendShardStatus");
	document.getElementById("d1_A").innerHTML = jso_statu.msg;


}


/**
 * 刷新操作
 * 点击刷新按钮，刷新页面
 */
function onRefresh() {
	doRefreshData();
	JSPFree.alert("人工刷新完成!");
}

/**
 * 关闭窗口
 */
function onCancel() {
	JSPFree.closeDialog();
}
