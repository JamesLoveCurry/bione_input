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
	var orgNo = "${orgNo}";
	var dataDate = "${dataDate}";
	var tmpId = '${tmpId}';
	var moduleParam = '${moduleParam}';
	var taskInsId = '${taskInsId}';
	var rptId = '${rptId}';
	$(function() {
		$("#uploader").pluploadQueue({
			runtimes : 'html5,flash,gears,silverlight,browserplus,html4',
			url : '${ctx}/rpt/frs/rptfill/uploadMoreData',
			max_file_size : '100MB',
			unique_names : true,
			multi_selection: false,
			chunk_size : '10MB',
			filters : [{ title : "导入文件", extensions : "xls,xlsx" }],
			flash_swf_url : '${ctx}/js/plupload/plupload.flash.swf',
			silverlight_xap_url : '${ctx}/js/plupload/plupload.silverlight.xap'
		});
		$('form').submit(function(e) {
			var uploader = $('#uploader').pluploadQueue();
			if (uploader.files.length > 0) {
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
				async : true,
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
					rptNumArr : [""],
					orgNo : orgNo
				}, 
				success: function(result) {
					completeMsg = result.msg ? result.msg : "上传失败,请联系管理员!";
					var tmp =result;
					if(result.status== "OK"){
						$.ajax({
							cache : false,
							async : false,
							url : "${ctx}/rpt/frs/rptfill/saveUploadDetailData" ,
							dataType : 'json',
							type : "post",
							data : {
								rptId : rptId,
								taskInsId : taskInsId,
								dataDate : dataDate,
								tmpId : tmpId,
								orgNo : orgNo,
								moduleParam : moduleParam,
								busiLineId : tmp.busiLibId,
								fileName : encodeURI(encodeURI(tmp.file))
							},
							beforeSend : function(){
								showLoading();
							},
							complete : function(){
								hideLoading();
							},
							success : function(result){
								if(result != null){
									if(result.status == "true"){
										window.parent.BIONE.showSuccess(result.msg);
										window.parent.tmp.viewInit(false);
									}else{
										var errorMsg = result.msg ? result.msg : "上传失败,请联系管理员!";
										var config = {};
										if(errorMsg.length > 80){
											config = {
												width: $(window.parent).width() * 0.8
											};
										}
										window.parent.BIONE.showError(errorMsg, null, config);
									}
									BIONE.closeDialog("uploadWin");
								}
							}
						});
					}else{
						var config = {};
						if(completeMsg.length > 80){
							config = {
								width: $(window.parent).width() * 0.8
							};
						}
						window.parent.BIONE.showError(completeMsg, null, config);
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