//初始化界面,菜单配置路径是【/frs/yufreejs?js=/yujs/east/58table.js】
function AfterInit(){
	JSPFree.createSplitByBtn("d1","左右",250,["刷新/onRefesh"]);
	JSPFree.createSplit("d1_B","上下",300);
	
	onRefesh();
}


function onRefesh(){
	//远程获取所有正在运行的任务,然后直接弹出
	var jso_rt = JSPFree.doClassMethodCall("com.yusys.fsrs.report.service.FsrsCrReportBSDMO", "getAllRuningZipTaskInfo",null);
	var str_taskHtml = jso_rt.zipTaskHtml;

	var str_fileListHtml_1 = jso_rt.fileListHtml_1;
	var str_fileListHtml_2 = jso_rt.fileListHtml_2;

	document.getElementById("d1_A").innerHTML = str_taskHtml;
    document.getElementById("d1_B_A").innerHTML = str_fileListHtml_1;
    document.getElementById("d1_B_B").innerHTML = str_fileListHtml_2;
}

