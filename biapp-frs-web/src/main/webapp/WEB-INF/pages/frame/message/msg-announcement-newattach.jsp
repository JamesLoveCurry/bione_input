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

<!-- Third party script for BrowserPlus runtime (Google Gears included in Gears runtime now) -->
<script type="text/javascript" src="${ctx}/js/uuid/uuid.js"></script>
<script type="text/javascript" src="${ctx}/js/plupload/plupload.full.js"></script>
<script type="text/javascript" src="${ctx}/js/plupload/jquery.plupload.queue/jquery.plupload.queue.js"></script>
<script type="text/javascript" src="${ctx}/js/plupload/i18n/cn.js"></script>

<script type="text/javascript">
	$(function() {
		$("#uploader").pluploadQueue({
			// General settings
			runtimes : 'html5,flash,gears,silverlight,browserplus,html4',
			url : '${ctx}/bione/message/attach/startUpload/?type=${type}',
			max_file_size : '100MB',
			unique_names : true,
			multi_selection: false,
			chunk_size : '100MB',
			// Specify what files to browse for
			filters : [ 
				{ title : "zip", extensions : "zip" },
				{ title : "rar", extensions : "rar" },
				{ title : "txt", extensions : "txt" },
				{ title : "pdf", extensions : "pdf" },
				{ title : "gif", extensions : "gif" },
				{ title : "png", extensions : "png" },
				{ title : "jpg,jpeg", extensions : "jpg,jpeg" },
				{ title : "doc,docx", extensions : "doc,docx" },
				{ title : "xls,xlsx", extensions : "xls,xlsx" }
			],
			
			// Flash settings
			flash_swf_url : '${ctx}/js/plupload/plupload.flash.swf',
			// Silverlight settings
			silverlight_xap_url : '${ctx}/js/plupload/plupload.silverlight.xap'
		});
		$('form').submit(
						function(e) {
							var uploader = $('#uploader').pluploadQueue();
							if (uploader.files.length > 0) {
								// When all files are uploaded submit form
								uploader.bind('StateChanged',
												function() {
													if (uploader.files.length === (uploader.total.uploaded + uploader.total.failed)) {
														$('form')[0].submit();
													}
												});
								uploader.start();
								
							} else {
								BIONE.tip("?????????????????????????????????");
							}
							return false;
						});
		var uploader = $('#uploader').pluploadQueue();
		uploader.bind('FileUploaded', function(result, file, resp) {
			if (resp) {
				if (resp.response) {
					parent.afterAttachOperat("add", $.parseJSON(resp.response));
				}
			}
			
		});
		//??????????????????????????????????????????????????????
		uploader.bind('UploadComplete',function(uploader,fs){
			uploader.trigger("Destroy");
			uploader.unbindAll();
			BIONE.closeDialog("attachUpload");
			
		});
	 	uploader.bind('BeforeUpload',function(uploader,fs){
			if(uploader.files.length == 1)
				return true;
			
			for(var i=0; i<uploader.files.length-1;i++){
				var m = 1;
				for(var j=1;j<uploader.files.length;j++){
					if(uploader.files[i].name == uploader.files[j].name && i != j){
						var extName = uploader.files[j].name.substring(uploader.files[j].name.indexOf('.'));
						var fileName = uploader.files[j].name.substring(0,
								uploader.files[j].name.indexOf('.'));
						uploader.files[j].name = fileName + "(" + m + ")" + extName;
						m++;
					}
				}
				
			}
		}); 
	});
</script>

</head>

<body style="padding: 0px">
	<div style="width: 550px; margin: 0px auto">
		<form id="formId" method="post">
			<div id="uploader"></div>
		</form>
	</div>
</body>
</html>