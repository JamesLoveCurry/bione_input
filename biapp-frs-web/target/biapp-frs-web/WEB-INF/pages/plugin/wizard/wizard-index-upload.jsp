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
	
	var reportRequireInfo="";
	$(function() {
		// 文件类型
		var filters = [{ title : "xls,xlsx", extensions : "xls,xlsx" }];
		// 上传最大文件数
		var maxFilesLengh = 1;
		// 人行大集中  报文导入 逻辑
		if('${type}' == 'PbcIJFile'){
			filters = [{ title : "zip文件", extensions : "zip" }];
		}
		$("#uploader").pluploadQueue({
			// General settings
			runtimes : 'html5,flash,gears,silverlight,browserplus,html4',
			url : '${ctx}/report/frame/wizard/frsUpload?type=${type}&dsId='+window.parent.dsId,
			max_file_size : '100MB',
			unique_names : true,
			multi_selection: false,
			chunk_size : '10MB',
			// Specify what files to browse for
			filters : filters,
			// Flash settings
			flash_swf_url : '${ctx}/js/plupload/plupload.flash.swf',
			// Silverlight settings
			silverlight_xap_url : '${ctx}/js/plupload/plupload.silverlight.xap'
		});
		$('form').submit(function(e) {
			var uploader = $('#uploader').pluploadQueue();
			if (uploader.files.length > 0) {
				// When all files are uploaded submit form
				if (uploader.files.length <= maxFilesLengh) {
					uploader.bind('StateChanged',function() {
						if (uploader.files.length === (uploader.total.uploaded + uploader.total.failed)) {
							$('form')[0].submit();
						}
					});
					uploader.start();				
				}else{
					BIONE.tip("最多上传" + maxFilesLengh + "个数据文件!");
				}
			} else {
				BIONE.tip("请添加要上传的数据文件");
			}
			return false;
		});
 		var uploader = $('#uploader').pluploadQueue();
		uploader.bind('FileUploaded', function(result, file, resp) {
			var s=JSON2.parse(resp.response);
			window.parent.tab.setInfo(s);
			//批量上传文件 控件会逐个文件上传，每上传完成一个文件会调用后台 upload 上传方法，后台上传文件逻辑内的校验信息需要通过 数据库来解决校验结果展示问题。
			BIONE.tip("上传成功");
			BIONE.closeDialog("fileUpWin");
		});
		uploader.bind('FilesAdded', function(up, files) {
			$.each(up.files, function(i, file) {
				if (up.files.length <= maxFilesLengh) {
					return;
				}else{
					BIONE.tip("最多上传" + maxFilesLengh + "个数据文件!");
					up.removeFile(file);
				}
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