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
	
	var rptId = "${rptId}";
	var verId = "${verId}";
	var verNm = "${verNm}";
	var busiType = "${busiType}";
	var importUrl;
	var importType;

	$(function() {
		
		if(rptId){
			importType = "xls,xlsx";
			importUrl = '${ctx}/report/frame/design/cfg/singleTemplateUpload?rptId='+rptId+'&busiType='+busiType+'&verId='+verId+'&verNm='+verNm;
			$("#batchRemind").hide();
		}else{
			importType = "zip";
			importUrl = '${ctx}/report/frame/design/cfg/batchTemplateUpload';
		}
		
		$("#uploader").pluploadQueue({
			// General settings
			runtimes : 'html5,flash,gears,silverlight,browserplus,html4',
			url : importUrl,
			max_file_size : '100MB',
			unique_names : true,
			multi_selection: false,
			chunk_size : '10MB',
			// Specify what files to browse for
			filters : [ 
			{ title : importType, extensions : importType }
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
				}else{
					parent.parent.BIONE.tip("上传成功");
				}
			}else{
				parent.parent.BIONE.tip("上传成功");
			}
			BIONE.closeDialog("importTemplate");
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
		<h3 id="batchRemind" style="color:red;">批量上传请用zip压缩包；包内模板名称格式：业务类型_制度版本_报表名称.xls</br>如：1104_2019年制度_G10.xls</h3>
		<div id="uploader"></div>
	</form>
</body>
</html>