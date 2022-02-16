<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ include file="/common/taglibs.jsp"%>
<%@ include file="/common/meta.jsp"%>
<%@ include file="/common/jquery_load.jsp"%>
<html>
<head>
<title>图片浏览</title>
<meta http-equiv="Content-Type" content="text/html; charset=utf-8">
<link rel="stylesheet" type="text/css"
	href="${ctx}/css/classics/ligerui-custom.css" />
<link rel="stylesheet" type="text/css"
	href="${ctx}/css/classics/template/common.css" />
<style type="text/css">
.iconlist {
	width: 100%;
	padding: 10px;
}

.iconlist li {
	border: 3px solid #FFFFFF;
	float: left;
	display: block;
	padding: 5px;
	cursor: pointer;
	width: 100px;
	height: 130px;
}

.iconlist li.over {
	border: 1px solid #516B9F;
}

.iconlist li img {
	width: 100px;
	height: 120px;
}
</style>
<script type="text/javascript">      
	function funCallback(funcNum, fileUrl) {
		var parentWindow = ( window.parent == window ) ? window.opener : window.parent;
		parentWindow.CKEDITOR.tools.callFunction(funcNum, fileUrl);
		window.close();
	}
</script>
<script type="text/javascript">
	$(init);
	function init() {
		$.ajax({
			cache : false,
			async : true,
			url : "${ctx}/bione/message/ckmedia/browseCK?d=" + new Date().getTime(),
			type : "get",
			data : {
				type : "${type}",
				CKEditor : "${CKEditor}",
				CKEditorFuncNum : "${CKEditorFuncNum}",
				langCode : "${langCode}"
			},
			success : function(result) {
				if (result && result.html) {
					$(".iconlist").append(result.html);
				}
			},
			error : function(result, b) {
				//BIONE.tip('发现系统错误 <BR>错误码：' + result.status);
			}
		});
	}
</script>
</head>
<body>
	<div id="winicons">
		<ul class="iconlist">
		</ul>
	</div>
</body>
</html>