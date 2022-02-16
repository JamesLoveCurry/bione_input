<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ include file="/common/taglibs.jsp"%>
<html>
<meta name="decorator" content="/template/template5.jsp">
<head>
<script type="text/javascript">
	var groupicon = "${ctx}/images/classics/icons/communication.gif";
	var templateId = "${templateId}";
	//创建表单结构 
	var mainform;
	$(function() {
		mainform = $("#mainform");
		mainform.ligerForm({
			fields : [ {
				group : "明细报表校验",
				groupicon : groupicon,
				display : "数据日期",
				name : "dataDate",
				newline : true,
				type : "date",
				cssClass : "field",
				options : {
					format : 'yyyyMMdd'
				},
				validate : {
					required : true
				}
			}, {
				display : "机构编号",
				name : "orgNo",
				newline : true,
				cssClass : "field",
				type : "text"
			}]
		});
		jQuery.metadata.setType("attr", "validate");
		BIONE.validate(mainform);
		var buttons = [];
		buttons.push({
			text : '取消',
			onclick : cancle
		});
		buttons.push({
			text : '数据结果',
			onclick : showValidResult
		});
		buttons.push({
			text : '数据校验',
			onclick : validData
		});
		BIONE.addFormButtons(buttons);

	});
	//数据结果
	 function showValidResult() {
		if ($('#mainform').valid()) {
			var dataDate = $("#dataDate").val();
			var orgNo = $("#orgNo").val();
			var rsUrl = "${ctx}/rpt/frs/rptDetailValid/detailValidResult?tmpId="+templateId+"&dataDate="+dataDate+"&orgNo="+orgNo;
			window.parent.BIONE.commonOpenDialog('明细校验结果','showValidResultWin', 800, 500,rsUrl);
		}
	 }
	 //数据校验
	 function validData() {
		if ($('#mainform').valid()) {
			var dataDate = $("#dataDate").val();
			var orgNo = $("#orgNo").val(); 
			$.ajax({
				cache : false,
				async : true,
				url : "${ctx}/rpt/frs/rptDetailValid/rptDetailValid",
				dataType : 'json',
				type : "post",
				beforeSend: function(){
					BIONE.showLoading('正在校验报表数据...');
				},
				data : {
					tmpId: templateId,
					dataDate: dataDate,
					orgNo: orgNo
				},
				complete: function(){
					BIONE.hideLoading();
				},
				success : function(result) {
					if(false == result.isPass){
						showValidResult();
					}else{
						window.parent.BIONE.tip(result.msg);
						//BIONE.closeDialog("validLogicWin");
					}
					
				},
				error : function(result, b) {
					BIONE.tip('发现系统错误 <BR>错误码：' + result.status);
				}
			});
		}
	} 
	 function cancle(){                     
		BIONE.closeDialog("validLogicWin");
	} 
</script>
</head>
<body>
	<div id="template.center">
		<form id="mainform" action="${ctx}/report/frs/pbmessage/pbcMsgsetController.mo?_type=data_event&_field=createCode&_event=POST&_comp=main&Request-from=dhtmlx" method="post"></form>
	</div>
</body>
</html>