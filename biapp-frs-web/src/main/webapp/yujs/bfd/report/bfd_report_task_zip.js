/**
 * 
 * <pre>
 * Title: 【报送管理】-【报文生成】
 * Description: 报送管理-报文生成-报文下载：主页面
 * 此页面提供了报文下载的相关操作，可以允许压缩报文，下载报文
 * </pre>
 * @author liangzy5 
 * @version 1.00.00
   @date 2020年8月25日
 */

var str_date = "";
var str_org_no = ""; //970110,990100
var str_report_type = "";
var str_org_class = "";
var org_nm = "";

function AfterInit(){
	str_date = jso_OpenPars.data_dt;
	str_org_no = jso_OpenPars.org_no;
	str_report_type = jso_OpenPars.report_type;
	str_org_class = jso_OpenPars.org_class;
	org_nm = jso_OpenPars.org_nm;
	
	JSPFree.createSpanByBtn("d1",["上一步/onNext","压缩/onZip","下载/onDownload","刷新/onRefresh","取消/onCancel"]);
	
}

/**
 * 页面初始化后，每隔5秒钟自动刷新页面
 * @returns
 */
function AfterBodyLoad(){
	//立即刷新界面
	doRefreshData();

	//每10秒钟刷新一次
	self.setInterval(doRefreshData,5000);
}

/**
 * 刷新数据
 * @returns
 */
function doRefreshData(){
	var jso_statu = JSPFree.doClassMethodCall("com.yusys.bfd.report.service.BfdCrReportBS", "checkZipAndDownloadStatu",{data_dt:str_date,org_no:str_org_no,report_type:str_report_type,loginOrg:str_LoginUserOrgNo});
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

/**
 * 设置提示消息
 * @param _html
 * @returns
 */
function setMsgHtmlText(_html){
   var dom_d1A = document.getElementById("d1_A");  //
   dom_d1A.innerHTML=_html;
}

/**
 * 上一步
 * @returns
 */
function onNext() {
	JSPFree.openDialogAndCloseMe("打包压缩下载一个机构某一日期下的所有报文","/yujs/bfd/report/bfd_report_task_choosedate.js", 400, 350,{data_dt:str_date,org_no:str_org_no,report_type:str_report_type,org_class:str_org_class,org_nm:org_nm});
}

/**
 * 压缩操作
 * 点击压缩按钮，对报文进行压缩
 * @returns
 */
function onZip(){
	var jso_par ={org_no:str_org_no,data_dt:str_date,report_type:str_report_type,loginOrg:str_LoginUserOrgNo};
	JSPFree.confirm('提示', '你的要压缩报文吗,这是一个耗时操作,请谨慎操作!', function(_isOK){
		if (_isOK){
			var jso_rt = JSPFree.doClassMethodCall("com.yusys.bfd.report.service.BfdCrReportBS", "zipReportFile",jso_par);
			doRefreshData();  //必须立即刷新一下
			JSPFree.alert(jso_rt.msg);
		}
	});
}


/**
 * 下载操作
 * 点击下载按钮，下载压缩包
 * @returns
 */
function onDownload(){
	JSPFree.confirm('提示', '你真的要下载压缩文件么?这是一个非常耗时的操作,请谨慎操作!<br>建议使用Ftp工具下载或直接在服务器端拷贝!', function(_isOK){
		if (_isOK){
			var download=null;
			download = $('<iframe id="download" style="display: none;"/>');
			$('body').append(download);
			
			var src = v_context + "/bfd/report/download?org_no=" + str_org_no + "&loginOrg=" + str_LoginUserOrgNo + "&data_dt=" + str_date+"&report_type="+str_report_type ;
			download.attr('src', src);
		}
	});
}

/**
 * 刷新操作
 * 点击刷新按钮，刷新数据
 * @returns
 */
function onRefresh(){
	doRefreshData();
	JSPFree.alert("人工刷新完成!");
}

function onCancel(){
	JSPFree.closeDialog();
}
