<%@ page language="java" pageEncoding="UTF-8"%>
<%@ include file="/common/taglibs.jsp"%>
<html>
<meta name="decorator" content="/template/template5.jsp">
<head>
<script type="text/javascript">
	var type = "${type}";
	var num = "${num}";
	$(function() {
		var display ="宽度";
		if(type == "01"){
			display ="宽度";
		} 
		else{
			display ="高度";
		}
		 $("#mainform").ligerForm({
			align : 'center',
			fields : [{
				display : display,
				name : "num",
				newline : true,
				type : "digits"
			}]
		});
		if(num){
			$("#num").val(num);
		}
		var buttons = [];
		
		buttons.push({
			text:'取消',
			onclick:function(){
				BIONE.closeDialog("numWin");
			}
		});
		buttons.push({
			text : '设置',
			onclick : save
		});
		BIONE.addFormButtons(buttons);
 		
	});
	
	function save(){
		if(type == "01")
			window.parent.setInfoWidth($("#num").val());
		else
			window.parent.setInfoHeight($("#num").val());
		BIONE.closeDialog("numWin");
	}
	
	
</script>
</head>
<body>
<div id="template.center">
	<form name="mainform" id="mainform" action="${ctx}/rpt/frame/rptsys/var/varsave" method="post">
	</form>
</div>
</body>
</html>