//加密报文打包下载
var str_date = "";
var str_org_no = "";
var fileName = "";

function AfterInit(){
	str_date = jso_OpenPars.data_dt;
	JSPFree.createSpanByBtn("d1",["压缩/onZip","下载/onDownload","刷新/onRefresh","取消/onCancel","查看任务/onLookAllZipTaskAndFile"]);
}

//一定要页面加载完之后才行
function AfterBodyLoad(){
	//立即刷新界面
	doRefreshData();

	//每10秒钟刷新一次
	self.setInterval(doRefreshData,5000);
}

//刷新一下状态
function doRefreshData(){
	var jso_statu = JSPFree.doClassMethodCall("com.yusys.pscs.datagramtask.service.PscsDatagramTaskBS", "checkSecAndDownloadStatu",{data_dt:str_date});
	setMsgHtmlText(jso_statu.msg);

	if(jso_statu.code=="-999"){  //发生错误,则什么都不能干!
	  $('#d1_onZip').linkbutton('disable');
      $('#d1_onDownload').linkbutton('disable');
	} else{
		str_org_no = jso_statu.org_no;  //
		if(jso_statu.code=="1"){  //如果正在下载
		  $('#d1_onZip').linkbutton('disable');
	      $('#d1_onDownload').linkbutton('disable');
		} else if(jso_statu.code=="2"){  //系统空闲,并且文件已生成
	      $('#d1_onZip').linkbutton('enable');
	      $('#d1_onDownload').linkbutton('enable');
		} else if(jso_statu.code=="3"){  //系统空闲,但文件还没有,只能先压缩
	      $('#d1_onZip').linkbutton('enable');
	      $('#d1_onDownload').linkbutton('disable');
		}
	}
}

//设置提示消息
function setMsgHtmlText(_html){
   var dom_d1A = document.getElementById("d1_A");  //
   dom_d1A.innerHTML=_html;
}

//压缩
function onZip(){
	var jso_par ={"org_no":str_org_no,"data_dt":str_date};
	JSPFree.confirm('提示', '你的要压缩报文吗,这是一个耗时操作,请谨慎操作!', function(_isOK){
		if (_isOK){
			var jso_rt = JSPFree.doClassMethodCall("com.yusys.pscs.datagramtask.service.PscsDatagramTaskBS", "secTarGzReportFile",jso_par);
			doRefreshData();  //必须立即刷新一下
			JSPFree.alert(jso_rt.msg);
		}
	});
}

//【下载】按钮点击逻辑
function onDownload(){
	JSPFree.confirm('提示', '你真的要下载压缩文件么?这是一个非常耗时的操作,请谨慎操作!<br>建议使用Ftp工具下载或直接在服务器端拷贝!', function(_isOK){
		if (_isOK){
			var download=null;
			download = $('<iframe id="download" style="display: none;"/>');
			$('body').append(download);
			
			var src = v_context + "/datagramtask/secDownload?org_no=" + str_org_no + "&data_dt=" + str_date;
			download.attr('src', src);
		}
	});
}

//【刷新】按钮点击逻辑
function onRefresh(){
	doRefreshData();
	JSPFree.alert("人工刷新完成!");
}

//查看所有任务
function onLookAllZipTaskAndFile(){
	JSPFree.openDialog("查看所有在线任务","/yujs/pscs/datagramtask/pscs-datagram-task-zipview.js",950,600);
}

function onCancel(){
	JSPFree.closeDialog();
}
