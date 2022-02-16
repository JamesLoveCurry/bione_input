<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ include file="/common/taglibs.jsp"%>
<html>
<head>
<meta name="decorator" content="/template/template14.jsp">
<script type="text/javascript">
	var reportManage=window.parent.parent;
	var mainform=null;
	$(function(){
		window.parent.reportShow=window;
		initForm();
		if(reportManage.reportInfo.reportShowInfo!=null){
			injectFormData(reportManage.reportInfo.reportShowInfo);

			$("#serverIdBox").val(reportManage.reportInfo.reportShowInfo.serverNm);

			$("#paramtmpIdBox").val(reportManage.reportInfo.reportShowInfo.paramtmpNm);
		}
		if(parent.parent.show!=""){
			$.ligerui.get("serverIdBox").setDisabled();
			$("#serverIdBox").css("color", "#333").attr("readOnly", "true")
			.removeAttr("validate");
			$.ligerui.get("paramtmpIdBox").setDisabled();
			$("#paramtmpIdBox").css("color", "#333").attr("readOnly", "true")
					.removeAttr("validate");
			$("#searchPath").css("color", "#333").attr("readOnly", "true")
			.removeAttr("validate");
			$("#rptSrcPath").css("color", "#333").attr("readOnly", "true")
			.removeAttr("validate");
		}
	});
	function initForm(){
		mainform = $("#mainform").ligerForm({
			inputWidth: 490,
			fields: [{
				display: '报表服务器',
				name: 'serverId',
				comboboxName: 'serverIdBox',
				type: "select",
				options : {
					onBeforeOpen : function() {
						window.parent.parent.selectedTab=window;
						window.parent.parent.BIONE
								.commonOpenDialog(
										"报表服务器",
										"serverBox",
										800,
										380,
										'${ctx}/frs/integratedquery/rptmesquery/info/showAdapter',
										null);
						return false;
					}
				},
				group : "报表展现信息",
				groupicon : "${ctx}/images/classics/icons/communication.gif"/* ,
				validate: {
					required: true
				} */
			},{
				display: '参数模板',
				name: 'paramtmpId',
				comboboxName: 'paramtmpIdBox',
				type: "select",
				options : {
					onBeforeOpen : function() {
						window.parent.parent.selectedTab=window;
						window.parent.parent.BIONE
								.commonOpenDialog(
										"参数模版目录",
										"paramtmpTreeWin",
										400,
										380,
										'${ctx}/frs/integratedquery/rptmesquery/info/showParamtmpTree',
										null);
						return false;
					}
				}/* ,
				validate: {
					required: true
				} */
			},{
				display: '搜索路径',
				name: 'searchPath',
				type: 'text',
				/* validate: {
					required: true
				}, */
				attr:{
					readOnly: true
				}
				
			},{
				display: '报表源路径',
				name: 'rptSrcPath',
				type: 'text',
				/* validate: {
					required: true
				}, */
				attr:{
					readOnly: true
				}
			}]
		});
		jQuery.metadata.setType("attr", "validate");
		BIONE.validate($("#mainform"));
	};
	function validate(){
		return $("#mainform").valid();
	}
	/*注入form 数据*/
	function injectFormData(data) {
		for ( var p in data) {
			var ele = $("[name=" + p + "]");
			// 针对复选框和单选框 处理
			ele.val(data[p]);
		}
		// 下面是更新表单的样式
		var managers = $.ligerui.find($.ligerui.controls.Input);
		for ( var i = 0, l = managers.length; i < l; i++) {
			// 改变了表单的值，需要调用这个方法来更新ligerui样式
			var o = managers[i];
			o.updateStyle();
			if (managers[i] instanceof $.ligerui.controls.TextBox)
				o.checkValue();
		}
	};
	function getData(){
		var reportShowInfo={
			serverId:$("#serverId").val(),
			paramtmpId:$("#paramtmpId").val(),
			searchPath: $("#searchPath").val(),
			rptSrcPath: $("#rptSrcPath").val()
		};
		return reportShowInfo;
	}
</script>
</head>
<body>
<div id='template.center'>
	<form id="mainform" action="{ctx}/report/mgr/reportInfo/saveReportInfo"method="POST"></form>
</div>
</body>
</html>