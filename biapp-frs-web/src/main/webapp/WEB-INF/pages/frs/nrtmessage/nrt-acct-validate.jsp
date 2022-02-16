<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ include file="/common/taglibs.jsp"%>
<!-- 账户持有人对私批量校验页面 -->
<html>
<meta name="decorator" content="/template/template5.jsp">
<head>
<script type="text/javascript">
	var groupicon = "${ctx}/images/classics/icons/communication.gif";
	var module = "${module}"
	 
	//创建表单结构 
	var mainform;
	$(function() {
		mainform = $("#mainform");
		mainform.ligerForm({
			fields : [ 
		/* 	 {
				display : "报送频度",
				name : "taskFreq",
				comboboxName: "taskFreqBox",
				newline : true,
				hide:true,
				type : "select",
				cssClass : "field",
				options:{
					url: "${ctx}/frs/pisamessage/getComboInfo.json?paramTypeNo=pisaSubmitFreq&d=" + new Date()
				},
				group : "报文生成信息",
				groupicon : groupicon,
				validate : {
					required : false
				}
			}, */
			{
				display : "校验日期",
				name : "validateDate",
				newline : true,
				type : "date",
				cssClass : "field",
				options : {
					format : 'yyyy-MM-dd'
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
			text : '校验',
			onclick : validate
		});
		BIONE.addFormButtons(buttons);

	});
	
	 function validate() {
		if ($('#mainform').valid()) {
			  var dataDate = $("#validateDate").val(); 
			$.ajax({
				cache : false,
				async : true,
				url : "${ctx}/frs/nrtmessage/batchValidate",
				dataType : 'json',
				type : "post",
				height:100,
				beforeSend: function(){
					BIONE.showLoading('数据正在校验中...');
				},
				data : {
					validateDate: dataDate,
					module : module
				},
				complete: function(){
					BIONE.hideLoading();
				},
				success : function(result) {
// 					var downloadUrl = "${ctx}/frs/nrtmessage/downLoadFile?id="+result.id+"&d="+ new Date();
// 					if (parent.$("msg_down").length > 0) {
// 						parent.$("#msg_down").attr('src',downloadUrl);
// 					} else {
// 						parent.$('body').append($('<iframe id="msg_down" />'));
// 						parent.$("#msg_down").attr('src',downloadUrl);
// 					}
					
 					//重新加载 列表数据 
					window.parent.grid.loadData();
					 
					BIONE.closeDialog("batchValidateWin"," 处理成功  ");
				},
				error : function(result, b) {
					BIONE.tip('发现系统错误 <BR>错误码：' + result.status);
				}
			});
			
		}
	} 
	 
	function save() {
		if ($('#mainform').valid()) {
			  var dataDate = $("#generateDate").val();
			 
			//var freqType = $("#taskFreq").val(); 
			 var freqType ="M";
			$.ajax({
				cache : false,
				async : true,
				url : "${ctx}/frs/nrtmessage/createXmlFile",
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
					var downloadUrl = "${ctx}/frs/nrtmessage/downLoadFile?id="+result.id+"&d="+ new Date().getTime();
					if (parent.$("msg_down").length > 0) {
						parent.$("#msg_down").attr('src',downloadUrl);
					} else {
						parent.$('body').append($('<iframe id="msg_down" />'));
						parent.$("#msg_down").attr('src',downloadUrl);
					}
					 
					//重新加载 列表数据
					window.parent.grid.loadData();
 					BIONE.closeDialog("batchValidateWin","maingrid", "处理成功");
				},
				error : function(result, b) {
					BIONE.tip('发现系统错误 <BR>错误码：' + result.status);
				}
			});
			
		}
	} 
	 function cancle(){                     
		BIONE.closeDialog("batchValidateWin");
	} 
</script>
</head>
<body>
	<div id="template.center">
		<form id="mainform" action="${ctx}/report/frs/pbmessage/pbcMsgsetController.mo?_type=data_event&_field=createCode&_event=POST&_comp=main&Request-from=dhtmlx" method="post"></form>
	</div>
</body>
</html>