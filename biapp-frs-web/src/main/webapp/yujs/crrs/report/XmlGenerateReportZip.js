//初始化界面,菜单配置路径是【/frs/yufreejs?js=/yujs/east/58table.js】
var str_date = "";
var seq = "";
var fileName = "";

function AfterInit(){

	str_date = jso_OpenPars.data_dt;
	seq = jso_OpenPars.seq;
	JSPFree.createSpanByBtn("d1",["压缩/onZip","下载/onDownload","刷新/onRefresh","取消/onCancel"]);
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
	var jso_statu = JSPFree.doClassMethodCall("com.yusys.crrs.report.service.CrrsReportBSDMO", "checkZipAndDownloadStatu",{data_dt:str_date,seq:seq});
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
	var jso_par ={"data_dt":str_date,"seq":seq};
	JSPFree.confirm('提示', '你的要压缩报文吗,这是一个耗时操作,请谨慎操作!', function(_isOK){
		if (_isOK){
			var jso_rt = JSPFree.doClassMethodCall("com.yusys.crrs.report.service.CrrsReportBSDMO", "zipTarGzReportFile",jso_par);
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
			
			var src = v_context + "/crrs/report/download?data_dt=" + str_date + "&seq=" + seq;
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
function onLookAllTask(){
	//远程获取所有正在运行的任务,然后直接弹出
	var jso_rt = JSPFree.doClassMethodCall("com.yusys.crrs.report.service.CrrsReportBSDMO", "getAllRuningZipTaskInfo",null);
	JSPFree.openHtmlMsgBox2("查看所有任务",400,350,jso_rt.htmltext);
}

function onCancel(){
	JSPFree.closeDialog();
}
