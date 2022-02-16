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

var ary_allTaskIds = [];  // 所有要启动的对象，界面选择几条则有几条。界面不选择报文任务则为空
var data_dt = "";
var report_type = ""; //界面选择几条则说明没有选择报送类型，所以是空。界面不选择报文任务则是所选择的报送类型，月报或者季报
var str_org = ""; //机构号 970110,990100
function AfterInit() {
	ary_allTaskIds = jso_OpenPars2.allTaskIds;
	data_dt = jso_OpenPars2.data_dt;
	report_type = jso_OpenPars2.report_type;
	str_org = jso_OpenPars2.str_org;
	JSPFree.createSpanByBtn("d1",["启动/onStart","刷新/onRefresh","取消/onCancel"]);
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
	var jso_statu = JSPFree.doClassMethodCall("com.yusys.imas.report.service.ImasCrReportBS", "checkCreateReportFileStatu");
	document.getElementById("d1_A").innerHTML = jso_statu.msg;

	if ("OK" == jso_statu.status) {
		$('#d1_onStart').linkbutton('disable');
	} else {
		if(jso_statu.num==0){
			$('#d1_onStart').linkbutton('enable');
		}else{
			setTimeout('secondCheck()',3000)
		}
	}
}

function secondCheck(){
	var jso = JSPFree.doClassMethodCall("com.yusys.imas.report.service.ImasCrReportBS", "checkReportFileStatu");
	
}

/**
 * 启动生成报文操作
 * 点击启动按钮，启动报文生成
 */
function onStart() {
	var jso_rt = null;
	jso_rt = JSPFree.doClassMethodCall("com.yusys.imas.report.service.ImasCrReportBS","startReportTask",{taskids:ary_allTaskIds,data_dt:data_dt,report_type:report_type,str_org:str_org,spark_type: "手动" });
    JSPFree.alert(jso_rt.msg);

	// 按钮置灰,不要重复启动!
	$('#d1_onStart').linkbutton('disable');

	// 一秒后刷新一下状态!
	self.setTimeout(doRefreshData, 200);
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
