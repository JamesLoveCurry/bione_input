<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ include file="/common/taglibs.jsp"%>
<html>
<meta name="decorator" content="/template/template5.jsp">
<head>
<script type="text/javascript">
	var groupicon = "${ctx}/images/classics/icons/communication.gif";
	//创建表单结构 
	var mainform;
	$(function() {
		mainform = $("#mainform");
		mainform.ligerForm({
			fields : [ 
			{
				display : "业务类型",
				name : "busiType",
				newline : false,
				type : "select",
				options : {
					url : "${ctx}/report/frame/datashow/idx/busiTypeList.json",
					onBeforeSelect : function(selectBusiType) {
						getSystemVer(selectBusiType);
					}
				}
			}, {
				display : "制度版本",
				name : "verId",
				newline : false,
				type : "select",
				options : {
					data : null
				}
			} ]
		});
		jQuery.metadata.setType("attr", "validate");
		BIONE.validate(mainform);
		var buttons = [];
		buttons.push({
			text : '取消',
			onclick : cancle
		});
		buttons.push({
			text : '确定',
			onclick : selectRpt
		});
		BIONE.addFormButtons(buttons);

	});
	
	function getSystemVer(busiType) {
		if (busiType) {
			$.ajax({
				cache : false,
				async : false,
				url : "${ctx}/report/frame/design/cfg/getSystemList",
				dataType : 'json',
				data : {
					busiType : busiType
				},
				type : "post",
				success : function(result) {
					if (result) {
						$.ligerui.get("verId").setData(result);
					}
				},
				error : function() {
					parent.BIONE.tip("数据加载异常，请联系系统管理员");
				}
			});
		}
	}
	function selectRpt() {
		var busiType = $("#busiType").val();
		var verId = $("#verId").val();
		window.parent.BIONE.commonOpenDialog("选择报表", "exportReports", 680, $(parent.document).height() - 10, "${ctx}/report/frame/design/cfg/exportReports?busiType="+busiType+"&verId="+verId);
		BIONE.closeDialog("exportFilter");
	} 
	 function cancle(){                     
		BIONE.closeDialog("exportFilter");
	} 
</script>
</head>
<body>
	<div id="template.center">
		<form id="mainform" action="" method="post"></form>
	</div>
</body>
</html>