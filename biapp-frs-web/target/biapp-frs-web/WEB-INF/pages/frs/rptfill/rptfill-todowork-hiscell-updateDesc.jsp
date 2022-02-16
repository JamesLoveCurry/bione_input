<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ include file="/common/taglibs.jsp"%>
<html>
<meta name="decorator" content="/template/template5.jsp">
<head>
<script type="text/javascript">
	var groupicon = "${ctx}/images/classics/icons/communication.gif";
	//创建表单结构 
	var mainform;
	var hisId ='${hisId}';
	var updateDesc = '${updateDesc}';
	$(function() {
		mainform = $("#mainform");
		mainform.ligerForm({
			fields : [{
				display : "修改说明", 
				name : "updateDesc", 
				newline : false, 
				type : "textarea", 
				validate : { 
					required : true,
					maxlength : 4000
				}
			}, { 
				name : "hisId", 
				type : "hidden"
			}]
		});
		$("textarea").attr("style", "width: 470px;height: 200px;resize: none;");
		//初始化表单中特殊组件动作
		jQuery.metadata.setType("attr", "validate");
		BIONE.validate("#mainform");
		if(updateDesc != null && updateDesc != "null"){
			$("#updateDesc").val(updateDesc);
		}
		var buttons = [];
		buttons.push({ text : '取消', onclick : cancle});
		buttons.push({ text : '保存', onclick : save});
		BIONE.addFormButtons(buttons);
	});
	
	function save() {
		if (mainform.valid()) {
			$.ajax({
				cache : false,
				async : true,
				url : "${ctx}/rpt/frs/rptfill/editUpdateDesc",
				type : "POST",
				data : {
					hisId : hisId,
					updateDesc : $("#updateDesc").val()
				},
				success : function(result) {
					window.parent.document.getElementById("detailTab").contentWindow.grid.loadData();
					parent.BIONE.tip("保存成功！");
					BIONE.closeDialog("updateDescWin");
				},
				error : function(result, b) {
					BIONE.tip("保存失败,请查看后台日志！");
				}
			});
		}
	}
	
	function cancle() {
		BIONE.closeDialog("updateDescWin");
	}
</script>
</head>
<body>
	 <div id="template.center">
		 <form id="mainform" action="" method="post">
		 </form> 
	</div> 
</body>
</html>