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
	var importUrl;
	var importType;

	$(function() {
		importType = "zip,xls,xlsx";
		importUrl = '${ctx}/frs/integratedquery/rptquery/batchRptUpload';

		$("#uploader").pluploadQueue({
			// General settings
			runtimes : 'html5,flash,gears,silverlight,browserplus,html4',
			url : importUrl,
			max_file_size : '100MB',
			unique_names : true,
			multi_selection : false,
			chunk_size : '10MB',
			filters : [ {
				title : importType,
				extensions : importType
			} ],
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
				async : true,
				url : "${ctx}/frs/integratedquery/rptquery/validateFile",
				dataType : 'json',
				type : 'POST',
				beforeSend : function(){
					BIONE.showLoading("数据导入中...");
				},
				complete : function(){
				},
				data : {
					uploadFilePath :uploadFileRes.filePath
				}, 
				success: function(result) {
					if(result.status== "OK"){
						var params = result.params;
						var isImportExcel = false;
						if($("#crossValid").is(':checked')){
							isImportExcel = true;
						}
						$.ajax({
							cache : false,
							url : "${ctx}/frs/integratedquery/rptquery/banchSaveReport",
							dataType : 'json',
							type : 'POST',
							data : {
								params : params,
								isImportExcel: isImportExcel
							},
							beforeSend : function(){
								BIONE.showLoading("数据导入中...");
							},
							success : function(result){
								top.BIONE.tip(result.msg);
								BIONE.closeDialog("uploadWin");
							}
						});
					}else{
						top.BIONE.tip(result.msg);
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
</script>

</head>

<body style="padding: 0px">
	<form id="formId" method="post">
		<h3 id="batchRemind" style="color: red;">
			文件格式：报表编号-机构号-数据日期.xls</br>如：G0101-001-20191219.xls
			<div style="display: none;float: right; "><input value="0" id="crossValid" type="checkbox"/>是否导入跨监管校验表</div>
		</h3>
		<div id="uploader"></div>
	</form>
</body>
</html>