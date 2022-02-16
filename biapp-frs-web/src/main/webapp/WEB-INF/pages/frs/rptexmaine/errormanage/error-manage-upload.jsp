<%@ page language="java" contentType="text/html; charset=UTF-8"%>
<%@ include file="/common/taglibs.jsp"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<html>
<head>
<%@ include file="/common/meta.jsp"%>
<%@ include file="/common/jquery_load.jsp"%>
<%@ include file="/common/ligerUI_load.jsp"%>
<link rel="stylesheet" type="text/css" href="${ctx}/css/classics/plupload/css/jquery.plupload.queue.css" />
<style type="text/css">
.l-dialog-win .l-dialog-content {
	overflow: hidden;
}
</style>
<script type="text/javascript" src="${ctx}/js/plupload/plupload.full.js"></script>
<script type="text/javascript" src="${ctx}/js/plupload/jquery.plupload.queue/jquery.plupload.queue.js"></script>
<script type="text/javascript" src="${ctx}/js/plupload/i18n/cn.js"></script>
<script type="text/javascript" src="${ctx}/js/bione/json2.js"></script>
<script type="text/javascript">
	
	var moduleType = "${moduleType}";
	var rid = "${rid}";

	$(function() {
		$("#uploader").pluploadQueue({
			// General settings
			runtimes : 'html5,flash,gears,silverlight,browserplus,html4',
			url : '${ctx}/frs/errormanage/singleFileUpload?moduleType='+moduleType+'&rid='+rid,
			max_file_size : '100MB',
			unique_names : true,
			multi_selection: false,
			chunk_size : '10MB',
			// Specify what files to browse for
			filters : [ 
				{ title : "*", extensions : "*" },
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
					parent.parent.BIONE.tip("只能上传一个数据文件");
				}
			} else {
				parent.parent.BIONE.tip("请添加要上传的数据文件");
			}
			return false;
		});
 		var uploader = $('#uploader').pluploadQueue();
		uploader.bind('FileUploaded', function(result, file, resp) {
			if(resp && resp.response){
				var res = $.parseJSON(resp.response);
				if(res.info){
					parent.parent.BIONE.tip(res.info);
					parent.reload();
				}else{
					parent.parent.BIONE.tip("上传成功");
					parent.reload();
				}
			}else{
				parent.parent.BIONE.tip("上传成功");
				parent.reload();
			}
			BIONE.closeDialog("importFile");
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
</script>

</head>

<body style="padding: 0px">
	<form id="formId" method="post">
		<div id="uploader"></div>
	</form>
</body>
</html>