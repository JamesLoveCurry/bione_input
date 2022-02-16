//初始化界面,菜单配置路径是【/frs/yufreejs?js=/yujs/east/58table.js】

var ary_allTaskIds = [];  //所有要启动的对象!
var data_dt = [];  //日期

function AfterInit(){
	ary_allTaskIds = jso_OpenPars2.allTaskIds;
	data_dt = jso_OpenPars2.data_dt;
	JSPFree.createSpanByBtn("d1",["启动/onStart","刷新/onRefresh","取消/onCancel"]);
}

function AfterBodyLoad(){
	doRefreshData();
	
	//每5秒钟刷新一次
	self.setInterval(doRefreshData,5000);
}

function doRefreshData(){
	//加载数据!
	var jso_statu = JSPFree.doClassMethodCall("com.yusys.fsrs.report.service.FsrsCrReportBSDMO", "checkCreateReportFileStatu");
	document.getElementById("d1_A").innerHTML = jso_statu.msg;

	if ("OK" == jso_statu.status) {
		$('#d1_onStart').linkbutton('disable');
	}
	else{
		$('#d1_onStart').linkbutton('enable');
	}
}


//【启动】按钮点击逻辑!
function onStart(){
	var jso_rt=null;
	//如果选择任务为空,则表示启动所有!
	if(ary_allTaskIds==null){
		jso_rt = JSPFree.doClassMethodCall("com.yusys.fsrs.report.service.FsrsCrReportBSDMO", 
				"startAllReportTask",{data_dt:data_dt});
	}else{
		jso_rt = JSPFree.doClassMethodCall("com.yusys.fsrs.report.service.FsrsCrReportBSDMO", 
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
	//远程获取所有正在运行的任务,然后直接弹出
	var jso_rt = JSPFree.doClassMethodCall("com.yusys.fsrs.report.service.FsrsCrReportBSDMO", "getAllRuningCreateFileTaskInfo",null);
	JSPFree.openHtmlMsgBox2("查看所有正在创建报文的任务",800,450,jso_rt.htmltext);
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
