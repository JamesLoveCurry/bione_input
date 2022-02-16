<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<%@ include file="/common/taglibs.jsp"%>
<html>
<meta name="decorator" content="/template/template5.jsp">
<head>
<style>
.checklabel {
	color: blue;
}
</style>
<script type="text/javascript"
	src="${ctx}/js/report/frame/dim/dimFormula.js"></script>
<script type="text/javascript">
	var content = '${content}';

	$(function() {
		initContent();
		initBtn();
	});
	
	function initContent (){
		$("#content").val(content);
		//$("#content");
	}
	
	function initBtn() {
		var btns = [ {
			text : "确定",
			onclick : function() {
				BIONE.closeDialog("commentBox",null,true,
						$("#content").val());
			}
		}, {
			text : "取消",
			onclick : function() {
				BIONE.closeDialog("commentBox");
			}
		} ];
		BIONE.addFormButtons(btns);
	}

</script>
</head>
<body>
	<div id="template.center">
		<div id="contentDiv" style="background-color: #FFFFFF">
			<textarea id="content" class="l-textarea" style="width:97%;height:115px;margin:2px;resize: none;"></textarea>
		</div>
	</div>
</body>
</html>