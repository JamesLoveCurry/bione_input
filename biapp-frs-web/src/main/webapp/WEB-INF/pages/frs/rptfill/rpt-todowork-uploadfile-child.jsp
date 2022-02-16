<%@ page language="java" contentType="text/html; charset=GBK"%>
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
	var doFlag = "";
	$(function() {
		$("#uploader").pluploadQueue({
			// General settings
			runtimes : 'html5,flash,gears,silverlight,browserplus,html4',
			url : '${ctx}/rpt/frs/rptfill/uploadData?d='+new Date().getTime(),
			max_file_size : '100MB',
			unique_names : true,
			multi_selection: false,
			chunk_size : '10MB',
			// Specify what files to browse for
			filters : [ 
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
					BIONE.tip("只能上传一个数据文件");
				}
			} else {
				BIONE.tip("请添加要上传的数据文件");
			}
			return false;
		});
 		var uploader = $('#uploader').pluploadQueue();
		uploader.bind('FileUploaded', function(result, file, resp) {
			var tmp = $.parseJSON(resp.response);
			$.ajax({
				async : true,
				url : "${ctx}/report/frs/rptfill/rptFillTabController.mo?_type=data_event&_field=validateFile&_event=POST&_comp=main&Request-from=dhtmlx",
				dataType : 'json',
				type : 'POST',
				data : {
					fileName :encodeURI(tmp.filePath),
					rptId : "${rptId}"
				}, 
				success: function(result) {
					if(result.result==true){
						parent.fileName = tmp.filePath;
						var title = "上传文件:" +　tmp.fileNm;
//			 			BIONE.tip("上传成功");
						var rptId = "${rptId}";
//			 			var templateId = "${templateId}";
						var dataDate = "${dataDate}";
						var orgNo = '${orgNo}';
						var lineId = "${lineId}";
						var taskInsId = "${taskInsId}";
						var type = "${type}";
						var height = $(parent.parent.window).height() - 10;
						var width = $(parent.parent.window).width();                                                                                      
						var taskId = "${taskId}";
						if("${flag}" == "ONE"){
							if("${entry}" == "GRID"){
								window.parent.parent.parent.BIONE.commonOpenDialog(title, "taskFillWin", width, height, "${ctx}/report/frs/rptfill/rptFillTabController.mo?doFlag=rptfill-todowork-oper&dataDate=" + dataDate + "&orgNo=" + orgNo + "&rptId=" + rptId + "&taskInsId=" + taskInsId + "&lineId=" + lineId + "&type=" + type + "&taskId=" + taskId, null);
								
							}else if("${entry}" == "EXCEL"){
								parent.View.ajaxJson(null, tmp.filePath);
							}else{
								BIONE.tip("数据异常，请联系系统管理员");
							}
						}
						if("${flag}" == "TWO"){
							var exeObjId = "${exeObjId}";
							var templateId = "${templateId}";
							if("${entry}" == "GRID"){
								window.parent.parent.parent.BIONE.commonOpenDialog(title, "taskFillChildWin", width, height, "${ctx}/report/frs/rptfill/rptFillTabController.mo?doFlag=rptfill-todowork-oper-child&dataDate=" + dataDate + "&exeObjId=" + exeObjId + "&rptId=" + rptId + "&templateId=" + templateId + "&taskInsId=" + taskInsId + "&lineId=" + lineId + "&orgNo=" + orgNo + "&type=" + type, null);
							}else if("${entry}" == "EXCEL"){
								parent.View.ajaxJson(null, encodeURI(tmp.filePath));
							}else{
								BIONE.tip("数据异常，请联系系统管理员");
							}
						}
						BIONE.closeDialog("uploadWin");
					}
					else{
						BIONE.tip("所选导入文件未通过校验，请选择正确的导入文件!");
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
		<div id="uploader"></div>
	</form>
</body>
</html>