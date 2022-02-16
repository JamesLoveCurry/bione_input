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
				display : "报送周期",
				name : "submitCycle",
				newline : true,
				cssClass : "field",
				group : "报文生成信息",
				groupicon : groupicon,
				type : "select",
	    		options:{
					data:[{
						text:"月报",
						id : "M"
					},{
						text:"季报",
						id : "Q"
					},{
						text:"半年报",
						id : "H"
					},{
						text:"年报",
						id : "A"
					}]
				},
				validate : {
					required : true
				}
			},{
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
			text : '生成',
			onclick : save
		});
		BIONE.addFormButtons(buttons);

	});
	 function save() {
		if ($('#mainform').valid()) {
			var dataDate = $("#dataDate").val();
			var submitCycle = $("#submitCycle").val(); 
			$.ajax({
				cache : false,
				async : true,
				url : "${ctx}/frs/xmlMessage/createXmlFile",
				dataType : 'json',
				type : "post",
				beforeSend: function(){
					BIONE.showLoading('正在生成报文数据中...');
				},
				data : {
					dataDate: dataDate,
					submitCycle: submitCycle
				},
				complete: function(){
					BIONE.hideLoading();
				},
				success : function(result) {
					var downloadUrl = "${ctx}/frs/xmlMessage/downLoadFile?id="+result.id+"&d="+ new Date();
					if (parent.$("msg_down").length > 0) {
						parent.$("#msg_down").attr('src',downloadUrl);
					} else {
						parent.$('body').append($('<iframe id="msg_down" />'));
						parent.$("#msg_down").attr('src',downloadUrl);
					}
					
					//重新加载 列表数据
					window.parent.grid.loadData();
					BIONE.closeDialog("createXmlWin");
				},
				error : function(result, b) {
					BIONE.tip('发现系统错误 <BR>错误码：' + result.status);
				}
			});
		}
	} 
	 function cancle(){                     
		BIONE.closeDialog("createXmlWin");
	} 
</script>
</head>
<body>
	<div id="template.center">
		<form id="mainform" action="${ctx}/report/frs/pbmessage/pbcMsgsetController.mo?_type=data_event&_field=createCode&_event=POST&_comp=main&Request-from=dhtmlx" method="post"></form>
	</div>
</body>
</html>