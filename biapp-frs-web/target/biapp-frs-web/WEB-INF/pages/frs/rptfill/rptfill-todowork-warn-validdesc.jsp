<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ include file="/common/taglibs.jsp"%>
<html>
<meta name="decorator" content="/template/template5.jsp">
<head>
<script type="text/javascript">
	var groupicon = "${ctx}/images/classics/icons/communication.gif";
	//创建表单结构 
	var mainform;
	var checkId ='${checkId}';
	var dataDate ='${dataDate}';
	var orgNo ='${orgNo}';
	var validDesc = '${validDesc}';
	$(function() {
		mainform = $("#mainform");
		mainform.ligerForm({
			fields : [{
				display : "未通过说明", 
				name : "validDesc", 
				newline : false, 
				type : "textarea", 
				validate : { 
					required : true,
					maxlength : 4000
				}
			}, { 
				name : "checkId", 
				type : "hidden"
			}]
		});
		$("textarea").attr("style", "width: 470px;height: 200px;resize: none;");
		//初始化表单中特殊组件动作
		jQuery.metadata.setType("attr", "validate");
		BIONE.validate("#mainform");
		if(validDesc != null && validDesc != "null"){
			$("#validDesc").val(validDesc);
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
				url : "${ctx}/frs/verificationWarning/updateValidDesc",
				type : "POST",
				data : {
					checkId : checkId,
					dataDate : dataDate,
					orgNo : orgNo,
					validDesc : $("#validDesc").val()
				},
				beforeSend : function() {
					BIONE.loading = true;
					BIONE.showLoading("保存数据中...");
				},
				complete : function() {
					BIONE.loading = false;
					BIONE.hideLoading();
				},
				success : function(result) {
					window.parent.document.getElementById("warnTab").contentWindow.grid.loadData();
					window.parent.document.getElementById("warnTab").contentWindow.BIONE.tip("保存成功！");
					BIONE.closeDialog("validDescWin");
				},
				error : function(result, b) {
					BIONE.tip("保存失败,请查看后台日志！");
				}
			});
		}
	}
	
	function cancle() {
		BIONE.closeDialog("validDescWin");
	}
</script>
</head>
<body>
	 <div id="template.center">
		 <form id="mainform" action="${ctx}/frs/verificationWarning/updateValidDesc" method="post">
		 </form> 
	</div> 
</body>
</html>