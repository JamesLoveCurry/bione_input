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
			fields : [ {
				display : "存款人姓名",
				name : "DEPOSIT_NAME",
				labelWidth : 90,
				newline : true
			},{
				display : "业务日期",
				name : "STATISTICS_DT",
				newline : false
			},{
				name : "BANK_ACCT_NUM",
				newline : false,
				type : "hidden"
			},{
				name : "FINAL_SUBMIT_DATE",
				newline : false,
				type : "hidden"
			},{
				name : "ERROR_TYPE",
				newline : false,
				type : "hidden"
			},
			{ display : "不再催办原因", name : "opercontent",labelWidth : 90,  newline : true, type : "textarea", validate : { maxlength : 500}},
			]
		});	
		$("#STATISTICS_DT").attr("value", '${statistics_dt}');
		$("#BANK_ACCT_NUM").attr("value", '${bank_acct_num}');
		$("#FINAL_SUBMIT_DATE").attr("value", '${final_submit_date}');
		$("#ERROR_TYPE").attr("value", '${error_type}');
		$("#DEPOSIT_NAME").attr("value", '${deposit_name}');
		$("#DEPOSIT_NAME").css("color","#000");
		$("#STATISTICS_DT").css("color","#000");
		$.ligerui.get("DEPOSIT_NAME").setDisabled();
		$.ligerui.get("STATISTICS_DT").setDisabled();
		var bank_acct_num = '${BANK_ACCT_NUM}';
		var final_submit_date = '${final_submit_date}';
		var error_type = '${error_type}';
		$("textarea").attr("style", "width: 470px;height: 200px;resize: none;");
		jQuery.metadata.setType("attr", "validate");
		BIONE.validate(mainform);
		var buttons = [];
		buttons.push({ text : '取消', onclick : cancle});
		buttons.push({ text : '督导', onclick : save});
		BIONE.addFormButtons(buttons);
	});
	function save() {
		$.ajax({
			cache : false,
			async : true,
			url : "${ctx}/frs/personal/personalAccount/returnSupervision?d="+new Date(),
			type : "post",
			data : {opercontent:$("#opercontent").val(),
					bank_acct_num:$("#BANK_ACCT_NUM").val(),
					final_submit_date:$("#FINAL_SUBMIT_DATE").val(),
					error_type:$("#ERROR_TYPE").val()
				   },
				   beforeSend : function() {
		   				BIONE.showLoading("正在督导...");
		   		    },	
			success : function() {
				BIONE.tip("督导成功");
				window.parent.grid.loadData();
				BIONE.closeDialog("supervision-manager");
				
			},
			error : function() {
				BIONE.tip("督导失败,请联系管理员");
			}
		});
	}
	window.parent.ligerGrid();
	function cancle() {
		BIONE.closeDialog("supervision-manager");
	}
</script>
</head>
<body>
	 <div id="template.center">
		 <form id="mainform"></form> 
	</div> 
</body>
</html>