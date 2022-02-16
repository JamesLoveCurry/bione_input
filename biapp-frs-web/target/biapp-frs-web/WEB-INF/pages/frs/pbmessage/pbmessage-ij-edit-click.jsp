<%@ page language="java" pageEncoding="UTF-8"%>
<%@ include file="/common/taglibs.jsp"%>
<html>
<meta name="decorator" content="/template/template5h.jsp">
<head>
<script type="text/javascript">
    var groupicon = "${ctx}/images/classics/icons/communication.gif";
	$(function() {
		initForm();
	});
	
	function initForm() {
		var width = $(window).width() - 178;
		$('#mainform').ligerForm({
			fields : [  {
				display : "单元格编号",
				name : "cellNo",
				newline : true,
				type:"text",
				group : "批量选择",
				groupicon : groupicon,
				validate : { 
					required : true
				}
			}]
		});
		jQuery.metadata.setType("attr", "validate");
		BIONE.validate($('#mainform'));
	}
	
	function addToParent() {
		return $("#cellNo").val();
	}
</script>
</head>
<body style="width: 80%">
	<div id="template.center">
		<form name="mainform" method="post" id="mainform"></form>
	</div>
</body>
</html>