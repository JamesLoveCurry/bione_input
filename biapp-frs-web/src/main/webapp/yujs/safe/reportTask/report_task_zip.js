/**
 *
 * <pre>
 * Title: 【报送管理】-【报文生成】
 * Description: 查看所有压缩在线任务页面
 * </pre>
 * @author miaokx
 * @version 1.00.00
 * @date 2020年6月18日
 */

var dataDt = "";
var reportType = "";

function AfterInit() {
	reportType = jso_OpenPars.reportType;
	dataDt = jso_OpenPars.dataDt;
	
	JSPFree.createSpanByBtn("d1", ["压缩/onZip","下载/onDownload","刷新/onRefresh","取消/onCancel"]);
}

/**
 * 页面初始化后
 * @returns
 */
function AfterBodyLoad(){
	// 立即刷新界面
	doRefreshData();

	// 每10秒钟刷新一次
	self.setInterval(doRefreshData,5000);
}

/**
 * 刷新状态
 * @returns
 */
function doRefreshData(){
	var jso_statu = JSPFree.doClassMethodCall("com.yusys.safe.reporttask.service.SafeCrReportBS", "checkZipAndDownloadStatu",{data_dt:dataDt,org_no:str_LoginUserOrgNo,report_type:reportType});
	setMsgHtmlText(jso_statu.msg);

	if(jso_statu.code=="-999"){  // 发生错误,则什么都不能干!
	  $('#d1_str_LoginUserOrgNo').linkbutton('disable');
      $('#d1_onDownload').linkbutton('disable');
	} else{
		if(jso_statu.code=="1"){  // 如果正在下载
		  $('#d1_str_LoginUserOrgNo').linkbutton('disable');
	      $('#d1_onDownload').linkbutton('disable');
		} else if(jso_statu.code=="2"){  // 系统空闲,并且文件已生成
	      $('#d1_str_LoginUserOrgNo').linkbutton('enable');
	      $('#d1_onDownload').linkbutton('enable');
		} else if(jso_statu.code=="3"){  // 系统空闲,但文件还没有,只能先压缩
	      $('#d1_str_LoginUserOrgNo').linkbutton('enable');
	      $('#d1_onDownload').linkbutton('disable');
		}
	}
}

/**
 * 设置消息提醒
 * @param _html
 */
function setMsgHtmlText(_html){
   var dom_d1A = document.getElementById("d1_A");
   dom_d1A.innerHTML=_html;
}

/**
 * 压缩
 */
function onZip() {
	var jso_par ={"org_no":str_LoginUserOrgNo, "data_dt":dataDt,"report_type":reportType};
	JSPFree.confirm('提示', '你的要压缩报文吗,这是一个耗时操作,请谨慎操作!', function(_isOK){
		if (_isOK){
			var jso_rt = JSPFree.doClassMethodCall("com.yusys.safe.reporttask.service.SafeCrReportBS", "zipTarGzReportFile", jso_par);
			doRefreshData();  //必须立即刷新一下
			JSPFree.alert(jso_rt.msg);
		}
	});
}

/**
 * 下载按钮逻辑
 */
function onDownload() {
	JSPFree.confirm('提示', '你真的要下载压缩文件么?这是一个非常耗时的操作,请谨慎操作!<br>建议使用Ftp工具下载或直接在服务器端拷贝!', function(_isOK){
		if (_isOK){
			var download=null;
			download = $('<iframe id="download" style="display: none;"/>');
			$('body').append(download);
			
			var src = v_context + "/safe/report/download?org_no=" + str_LoginUserOrgNo + "&data_dt=" + dataDt + "&report_type=" + reportType;
			download.attr('src', src);
		}
	});
}

/**
 * 【刷新】按钮点击逻辑
 */
function onRefresh(){
	doRefreshData();
	JSPFree.alert("人工刷新完成!");
}

/**
 * 取消
 * @returns
 */
function onCancel(){
	JSPFree.closeDialog();
}
