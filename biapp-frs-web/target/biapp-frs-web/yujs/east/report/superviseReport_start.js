//初始化界面,菜单配置路径是【/frs/yufreejs?js=/yujs/east/58table.js】

var ary_allTaskIds = [];  //所有要启动的对象!
var data_dt = [];  //日期

function AfterInit(){
	ary_allTaskIds = jso_OpenPars2.allTaskIds;
	data_dt = jso_OpenPars2.data_dt;
	JSPFree.createSpanByBtn("d1",["启动/onStart","刷新/onRefresh","取消/onCancel","查看任务/onLookAllCreateFileTask"]);
}

function AfterBodyLoad(){
	doRefreshData();
	
	//每5秒钟刷新一次
	self.setInterval(doRefreshData,5000);
}

function doRefreshData(){
	//加载数据!
	var jso_statu = JSPFree.doClassMethodCall("com.yusys.east.business.report.service.EastCrReportBSDMO", "checkCreateReportFileStatu");
	document.getElementById("d1_A").innerHTML = jso_statu.msg;
	
	// if ("OK" == jso_statu.status) {
	// 	$('#d1_onStart').linkbutton('disable');
	// }
	// else{
	// 	$('#d1_onStart').linkbutton('enable');
	// }
}


//【启动】按钮点击逻辑!
function onStart(){
	var jso_rt=null;
	//如果选择任务为空,则表示启动所有!
	if(ary_allTaskIds==null){
		jso_rt = JSPFree.doClassMethodCall("com.yusys.east.business.report.service.EastCrReportBSDMO", 
				"startAllReportTask",{data_dt:data_dt});
	}else{
		jso_rt = JSPFree.doClassMethodCall("com.yusys.east.business.report.service.EastCrReportBSDMO", 
				"startReportTask",{taskids:ary_allTaskIds,data_dt:data_dt});
    }
    JSPFree.alert(jso_rt.msg);

	//按钮置灰,不要重复启动!
	$('#d1_onStart').linkbutton('disable');

	//一秒后刷新一下状态!
	self.setTimeout(doRefreshData,200);
}


//查看所有任务
function onLookAllCreateFileTask(){
	JSPFree.openDialog("查看所有在线任务","/yujs/east/report/superviseReport_lookTaskFile.js",800,600);
}


//【刷新】按钮点击逻辑
function onRefresh(){
	doRefreshData();
	JSPFree.alert("人工刷新完成!");
}

//关闭窗口
function onCancel(){
	JSPFree.closeDialog();
}
