<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<%@ include file="/common/taglibs.jsp"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<link rel="stylesheet" type="text/css" href="${ctx}/js/bootstrap3/css/bootstrap.min.css" />
<style>
* {
  -webkit-box-sizing: content-box;
  -moz-box-sizing: content-box;
  box-sizing: content-box;
}
#center{
	background-color : #F1F1F1;
}
</style>
<link rel="stylesheet" type="text/css"
	href="${ctx}/css/classics/ligerui-custom.css" />
<link rel="stylesheet" type="text/css"
	href="${ctx}/css/classics/template/common.css" />
<link rel="stylesheet" type="text/css" href="${ctx}/css/classics/temp/template.css" />
<link rel="stylesheet" type="text/css" href="${ctx}/css/classics/daterangepicker/daterangepicker.css"/>
<script type="text/javascript" src="${ctx}/js/require/require.js"></script>
<link rel="stylesheet" type="text/css" href="${ctx}/images/classics/icons/icon/style.css" />
<style type="text/css">
html {
	background-color : #FFF;
}
.l-form {
    margin: 7px;
    overflow: hidden;
}
</style>
<title>参数模板预览</title>
</head>
<body>
	<!-- Templates -->
	<form id="form"></form>
	<script type="text/javascript">
		var paramtmpId = "${paramtmpId}";
		require.config({
			baseUrl : '${ctx}/js',
			paths : {
				jquery : 'jquery/jquery-1.7.1.min',
				JSON2 : 'bione/json2.min'
			},
			shim : {
				JSON2 : {
					exports : 'JSON2'
				}
			}
		});
		require([ 'jquery', 'JSON2', '../plugin/js/template/viewMain' ], function() {
			$(function() {
				if (paramtmpId != "") {
					$.ajax({
						cache : false,
						async : true,
						url : "${ctx}/report/frame/param/templates/" + paramtmpId
								+ "?type=view",
						dataType : 'json',
						success : function(data) {
							$('#form').templateView({
								data : JSON2.parse(data.paramJson)
							});
							parent.$("#view").height($('#form').height() + 15);
							parent.$("#content").height(
									parent.$("#center").height()
											- parent.$("#mainsearch").height()
											- 8);
						}
					});
				} else {
					$('#form').templateView(
							{
								data : window.parent.app.template
										._getComponentes().toJSON()
							});
				}
			});
		});
	</script>
</body>
</html>