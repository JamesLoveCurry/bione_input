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
<script type="text/javascript">
	$(function() {
		$("#uploader").pluploadQueue({
			// General settings
			runtimes : 'html5,flash,gears,silverlight,browserplus,html4',
			url : '${ctx}/rpt/input/temple/startUploadTempleData?templeId=' + '${templeId}'+'&caseId=' +'${caseId}'+'&taskNodeInstanceId='+'${taskNodeInstanceId}',
			max_file_size : '1000MB',
			chunk_size : '1MB',
			// Specify what files to browse for
			// Flash settings
			unique_names : true,
			filters : [ {
				title : "xls文档",
				extensions : "xls"
			}],
			flash_swf_url : '${ctx}/js/plupload/plupload.flash.swf',
			// Silverlight settings
			silverlight_xap_url : '${ctx}/js/plupload/plupload.silverlight.xap'
		});
		$('form').submit(function(e) {
			var uploader = $('#uploader').pluploadQueue();
			if (uploader.files.length > 0) {
				// When all files are uploaded submit form
				uploader.bind('StateChanged', function() {
					if (uploader.files.length == (uploader.total.uploaded + uploader.total.failed)) {
						$('form')[0].submit();
					}
				});
				uploader.start();
			} else {
				BIONE.tip("请添加要上传的数据文件");
			}
			return false;
		});
		var uploader = $('#uploader').pluploadQueue();

		uploader.bind('BeforeUpload', function() {
			BIONE.showLoading("文件上传中。。。");
		});

		
		uploader.bind('FileUploaded', function(result, file, resp) {
			parent.$.ligerui.get("maingrid").loadData();
			if (resp.response) {
				var res = JSON2.parse(resp.response);
				var rec = [];
				if(res.error){
					rec.push({
						errorPosi : "导入异常",
						errorMsg : res.error
					});
				}else if (res.OK){
					var s = "";
					$.each(res.OK, function(i, n) {
						if (n.length > 0) {
							var sarr = n.split(",");
							if(sarr.length > 0){
								rec.push({
									errorPosi : sarr[0],
									errorMsg : sarr[1]
								});
							}else{
								rec.push({
									errorPosi : "导入异常",
									errorMsg : sarr[0]
								});
							}
							
						}
					});
				}
				if(rec.length > 0 ){
					parent.Rows = rec;
				}
				BIONE.closeDialog("inputTaskInfo");
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