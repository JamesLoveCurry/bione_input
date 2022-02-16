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
$(function() {
	var url = '${ctx}/report/frame/design/cfg/importDesignInfosImpl?q=';
	if ('${emptyCellInFormula}') {
		url += '&emptyCellInFormula=${emptyCellInFormula}';
	}
	if ('${emptyIndexInFormula}') {
		url += '&emptyIndexInFormula=${emptyIndexInFormula}';
	}
	if ('${emptyCellInVerifyWarn}') {
		url += '&emptyCellInVerifyWarn=${emptyCellInVerifyWarn}';
	}
	if ('${fromSystemRptCfg}') {
		url += '&fromSystemRptCfg=${fromSystemRptCfg}';
	}
	url += '&type=${type}';
	url += '&uuid='+ parent.uuid;
	$("#uploader").pluploadQueue({
		// General settings
		runtimes : 'html5,flash,gears,silverlight,browserplus,html4',
		url : url,
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
				uploader.bind('StateChanged', function() {
					if (uploader.files.length === (uploader.total.uploaded + uploader.total.failed)) {
						$('form')[0].submit();
					}
				});
				uploader.start();				
			} else {
				BIONE.tip("只能上传一个数据文件");
			}
		} else {
			BIONE.tip("请添加要上传的数据文件");
		}
		return false;
	});
	var uploader = $('#uploader').pluploadQueue();
	uploader.bind('FileUploaded', function(result, file, resp) {
		var s = JSON2.parse(resp.response);
		if (typeof(s.error) == 'undefined') {
			BIONE.tip("上传处理失败，请联系系统管理员");
		} else if (s.error == ""){
			parent.displayResult("上传处理成功");
		} else {
			parent.displayResult('<ul>' + s.error + '</ul>');
		}
	});
	uploader.bind('FilesAdded', function(up, files) {
		$.each(up.files, function(i, file) {
			if (up.files.length <= 1) {
				return;
			}
			up.removeFile(file);
		});
	});
	uploader.bind('BeforeUpload',function(uploader,fs){
		parent.changeLogTab();
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