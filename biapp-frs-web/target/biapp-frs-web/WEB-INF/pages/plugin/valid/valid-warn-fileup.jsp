<%@ page language="java" pageEncoding="UTF-8"%>
<%@ include file="/common/taglibs.jsp"%>
<html>
<head>
<meta name="decorator" content="/template/template14.jsp">
<style>

</style>
<script type="text/javascript">
	var groupicon = "${ctx}/images/classics/icons/communication.gif";
	var mainform;
	var grid;
	var serverId;
	var agentId = "";
	var agentStatus = "";
	var colInfo = [];
	var urlPath = '${ctx}/report/frame/valid/warn';
	$(function(){
		initForm();
		initGrid();
	});

	function initForm() {
		mainform = $("#mainform").ligerForm(
				{
					labelWidth : 80,
					space : 60,
					fields : [
						 {
							display : '上传文件',
							name : 'file',
							newline : true,
							width : 200,
							comboboxName : 'fileBox',
							type : "select",
							options : {
								onBeforeOpen : function() {
									window.parent.tab=window;
									window.parent.BIONE.commonOpenDialog("上传文件", "fileUpWin", 580, 340, urlPath +'/validWarnRel/uploadWin', null);
									return false;
								}
							},
							validate:{
								required: true
							}
						}, {
							display : '上传信息',
							name : 'serverApp',
							newline : true,
							width : '450'
						} ]
				});
		var tempLi = $("#serverApp").parent().parent();
		var tipContent = [];
		tipContent
				.push('<li style="width: 100%"><div id="maingrid" name="agentGrid" style="width: 100%;"></div></li>');
		tempLi.html(tipContent.join(''));
		jQuery.metadata.setType("attr", "validate");
		BIONE.validate($("#mainform"));
	}

	function initGrid() {
		var g = {
			columns :  [ {
				display : '报表名称',
				name : 'name',
				width :"75%",
				align : 'center'
			}],
			width : '99%',
			height : '300',
			data : null,
			checkbox: false,
			alternatingRow : false,
			usePager: false
		};

		grid = $("#maingrid").ligerGrid(g);
		grid.setHeight($("#center").height() - 60);
	}
	
	function setInfo(info){
		var data={
			Rows:info.result.info
		};
		grid.set("data",data);
		window.parent.ehcacheId=info.result.ehcacheId;
		window.parent.validateData=info.error;
		window.parent.validateFlag=true;
		$("#fileBox").val(info.result.fileName);
		$("#file").val(info.result.fileName);
	}
	
	function validate(){
		if($("#mainform").valid()){
			if(grid.getData().length>0)
				return true;
			else
				window.parent.BIONE.tip("无可导入数据");
		}
		return false;
	}
</script>
</head>
<body>
	<div id="template.center">
		<form name="mainform" method="post" id="mainform" action=""
			style="height: 100%;"></form>

	</div>
</body>
</html>