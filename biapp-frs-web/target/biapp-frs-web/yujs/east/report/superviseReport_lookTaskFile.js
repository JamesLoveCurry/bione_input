//初始化界面,菜单配置路径是【/frs/yufreejs?js=/yujs/east/58table.js】
function AfterInit(){
	JSPFree.createSplitByBtn("d1","上下",300,["刷新/onRefesh"]);

	onRefesh();
}



function onRefesh(){
//远程获取所有正在运行的任务,然后直接弹出
var jso_rt = JSPFree.doClassMethodCall("com.yusys.east.business.report.service.EastCrReportBSDMO", "getAllRuningCreateFileTaskInfo",null);
var str_taskHtml = jso_rt.taskHtml;
var str_fileListHtml = jso_rt.fileListHtml;

document.getElementById("d1_A").innerHTML = str_taskHtml;
document.getElementById("d1_B").innerHTML = str_fileListHtml;
}
