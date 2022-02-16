<%@ page language="java" contentType="text/html; charset=UTF-8"%>
<%@ include file="/common/taglibs.jsp"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<html>
<head>
<%@ include file="/common/meta.jsp"%>
<%@ include file="/common/jquery_load.jsp"%>
<%@ include file="/common/ligerUI_load.jsp"%>
<link rel="stylesheet" type="text/css"
	href="${ctx}/css/classics/plupload/css/jquery.plupload.queue.css" />
<style type="text/css">
.l-dialog-win .l-dialog-content {
	overflow: hidden;
}
</style>
<script type="text/javascript" src="${ctx}/js/plupload/plupload.full.js"></script>
<script type="text/javascript"
	src="${ctx}/js/plupload/jquery.plupload.queue/jquery.plupload.queue.js"></script>
<script type="text/javascript" src="${ctx}/js/plupload/i18n/cn.js"></script>
<script type="text/javascript" src="${ctx}/js/bione/json2.js"></script>
<script type="text/javascript">
	var hiddenCount = 0,successCount = 0,completeMsg;
	var doFlag = "";
	var isSingleUpload = "zip" != "${importFileType}";
	var orgNo = "${orgNo}";
	var dataDate = "${dataDate}";
	var filters = [ 
		{ title : "导入文件", extensions : "${importFileType}" }
	];
	$(function() {
		$("#uploader").pluploadQueue({
			// General settings
			runtimes : 'html5,flash,gears,silverlight,browserplus,html4',
			url : '${ctx}/rpt/frs/rptfill/uploadMoreData?orgNo='+orgNo+'&dataDate='+dataDate+'&d='+new Date().getTime(),
			max_file_size : '100MB',
			unique_names : true,
			multi_selection: false,
			chunk_size : '10MB',
			filters : filters,
			flash_swf_url : '${ctx}/js/plupload/plupload.flash.swf',
			silverlight_xap_url : '${ctx}/js/plupload/plupload.silverlight.xap'
		});
		$('form').submit(function(e) {
			var uploader = $('#uploader').pluploadQueue();
			if (uploader.files.length > 0) {
				// When all files are uploaded submit form
				if (uploader.files.length ==1) {
					uploader.bind(
							'StateChanged',
							function() {
								if (uploader.files.length === (uploader.total.uploaded + uploader.total.failed)) {
									$('form')[0].submit();
								}
							});
						uploader.start();				
				}
				else{
					BIONE.tip("只能上传一个数据文件");
				}
			} else {
				BIONE.tip("请添加要上传的数据文件");
			}
			return false;
		});
 		var uploader = $('#uploader').pluploadQueue();
		uploader.bind('FileUploaded', function(result, file, resp) {
			var uploadFileRes = $.parseJSON(resp.response);	
			$.ajax({
				async : false,
				url : "${ctx}/rpt/frs/rptfill/validateFile",
				dataType : 'json',
				type : 'POST',
				beforeSend : function(){
					showLoading();
				},
				complete : function(){
					hideLoading();
				},
				data : {
					uploadFilePath :uploadFileRes.filePath,
					orgNo : orgNo,
					dataDate : dataDate,
					rptNumArr : isSingleUpload ? [""] : window.parent.selectedRptIdArr
				}, 
				success: function(result) {
					completeMsg = result.msg;
					var tmp =result;
					if(result.status== "OK"){
						if(isSingleUpload){
							var title = "上传文件:" +　tmp.fileNm;
							var rptId = "${rptId}";
							var taskInsId = "${taskInsId}";
							var type = "${type}";
							var height = $(parent.parent.window).height() - 10;
							var width = $(parent.parent.window).width();                                                                                      
							var taskId = "${taskId}";
							if("${flag}" == "ONE"){
								if("${entry}" == "GRID"){
									window.parent.parent.parent.BIONE.commonOpenDialog(title, "taskFillWin", 
											width, height, "${ctx}/rpt/frs/rptfill/showTab?path=rpt-todowork-uploadfile-save&dataDate=" + 
													dataDate + "&orgNo=" + orgNo + "&rptId=" + rptId + "&taskInsId=" + taskInsId +
													"&type=" + type + "&taskId=" + taskId+ "&fileName=" + 
													encodeURI(encodeURI(tmp.file)), null);

								}else if("${entry}" == "EXCEL"){
									parent.View.ajaxJson(null, tmp.file, true, null, null, null, null, false);
									if(parent.View.notUptCells.length > 0){
										parent.tmp.showNotUptCells();
									}
								}else{
									BIONE.tip("数据异常，请联系系统管理员");
								}
							}
							BIONE.closeDialog("uploadWin");
						}else{
							//批量导入
							var rptObjTmps = [];
							for(var rptIdTmp in tmp.rptCondition){
								var rptObjTmp = tmp.rptCondition[rptIdTmp];
								rptObjTmps.push(rptObjTmp);
							}

							if("${flag}" == "ONE"){
								if("${entry}" == "GRID"){
									$.ajax({
										cache : false,
										url : "${ctx}/rpt/frs/rptfill/batchImport",
										dataType : 'json',
										data : {
											dataDate : dataDate,
											rptObjTmps : JSON2.stringify(rptObjTmps)
										},
										type: 'post',
										beforeSend : function(){
											showLoading();
										},
										complete : function(){
											hideLoading();
										},
										success : function(result){
											var rptNms = result.rptNms;
											for (var i = 0; i < rptNms.length; i++) {
												successCount++;
												var index = window.parent.selectedRptIdArr.indexOf(rptNms[i]);
												if(index != -1){
													window.parent.selectedRptIdArr.splice(index, 1);
												}
											}
											if(result.notUptCells != null && result.notUptCells.length > 0){
												parent.notUptCells = result.notUptCells;
												parent.showNotUptCells();
											}
										}
									})
								} else if("${entry}" == "EXCEL"){
									//excel 处理
									parent.View.ajaxJson(null, tmp.filePath);
									BIONE.tip("上传成功");
								} else {
									BIONE.tip("数据异常，请联系系统管理员");
								}
							}
						}
					}else{
						window.parent.BIONE.showError(result.msg ? result.msg : "未知错误,请联系管理员!");
						BIONE.closeDialog("uploadWin");
					}
				}
				
			});
		});
		
		
		uploader.bind('FilesAdded', function(up, files) {
			$.each(up.files, function(i, file) {
				if (up.files.length <= 1) {
					return;
				}
				up.removeFile(file);
			});
		});
	});
	
	function showLoading(){
		if(hiddenCount == 0){
			BIONE.showLoading("数据导入中...");
		}
		hiddenCount ++;
	}
	function hideLoading(){
		hiddenCount--;
		if(hiddenCount == 0){
			BIONE.hideLoading();
			var errorTip =  window.parent.selectedRptIdArr.length > 0 ?
					"<br/>报表编号[" + window.parent.selectedRptIdArr.join(",") + "]未导入成功!" : "";
			window.parent.BIONE.showSuccess(completeMsg ? completeMsg + "成功导入[" + successCount + "]个文件!" + errorTip: "数据保存成功!");
			BIONE.closeDialog("uploadWin");
			return;
		}
	}
	
	function sleep(n){
		
		var start = new Date().getTime();
		while(true){
			if(new Date().getTime() - start >n){
				break;
			}
		}
		
	}
</script>

</head>

<body style="padding: 0px">
	<form id="formId" method="post">
		<div id="uploader"></div>
	</form>
</body>
</html>