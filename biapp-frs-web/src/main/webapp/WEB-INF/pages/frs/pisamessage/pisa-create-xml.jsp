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
				display : "报送频度",
				name : "taskFreq",
				comboboxName: "taskFreqBox",
				newline : true,
				type : "select",
				cssClass : "field",
				options:{
					url: "${ctx}/frs/pisamessage/getComboInfo.json?paramTypeNo=pisaSubmitFreq&d=" + new Date()
				},
				group : "报文生成信息",
				groupicon : groupicon,
				validate : {
					required : true
				}
			},{
				display : "报送期数",
				name : "generateDate",
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
			var dataDate = $("#generateDate").val();
			var freqType = $("#taskFreq").val(); 
			$.ajax({
				cache : false,
				async : true,
				url : "${ctx}/frs/pisamessage/createXmlFile",
				dataType : 'json',
				type : "post",
				beforeSend: function(){
					BIONE.showLoading('正在生成报文数据中...');
				},
				data : {
					dataDate: dataDate,
					freqType: freqType
				},
				complete: function(){
					BIONE.hideLoading();
				},
				success : function(result) {
					var downloadUrl = "${ctx}/frs/pisamessage/downLoadFile?id="+result.id+"&d="+ new Date();
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