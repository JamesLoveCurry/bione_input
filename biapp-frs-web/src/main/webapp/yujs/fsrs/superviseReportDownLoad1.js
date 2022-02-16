//初始化界面,菜单配置路径是【/frs/yufreejs?js=/yujs/east/58table.js】
var data_dt = "";
var org_no = "";
var fileName = "";

function AfterInit(){
	data_dt = jso_OpenPars.data_dt;
	org_no = jso_OpenPars.org_no;
	JSPFree.createBillList("d1","/biapp-fsrs/freexml/fsrs/supervise_report_download1.xml",["压缩/onCompress","下载/onDownload","取消/onCancel"]);
}

// 压缩
function onCompress(){
	var jso_par ={"org_no":str_LoginUserOrgNo,"data_dt":data_dt};
	$.messager.confirm('提示', '确认压缩吗？', function(r){
		if (r){
			var jso_rt1 = JSPFree.doClassMethodCall("com.yusys.fsrs.report.service.FsrsCrReportBSDMO", "checkDownload",jso_par);
		}
	});
}

// 下载
function onDownload(){
	var download=null;
	download = $('<iframe id="download" style="display: none;"/>');
	$('body').append(download);
	
	var src = v_context + "/fsrs/report/download?name=" + fileName;
	download.attr('src', src);
}

function onCancel(){
	JSPFree.closeDialog();
}