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
	//202003 lcy 明细校验
	var isDetailValid = "${isDetailValid}";
	var urlPath = '${ctx}/report/frame/valid/logic';

	$(function(){
		if(isDetailValid){
			urlPath = '${ctx}/rpt/frs/rptDetailValid';
		}
		initForm();
		initGrid();
		if(isDetailValid){
			$("#isNmImp").parents("li").hide();
		}
	});

	function initForm() {
		mainform = $("#mainform").ligerForm(
				{
					labelWidth : 80,
					space : 60,
					fields : [
						{
							display : '上传格式',
							name : 'isNmImp',
							newline : true,
							width : 200,
							type : "select",
							comboboxName : "isNmImpCombo",
							options : {
								initValue:'1',
								data : [ {
									text : '按照报表指标编号导入',
									id : '1'
								}, {
									text : '按照单元格名称导入',
									id : '2'
								}, {
									text : '按照单元格编号导入',
									id : '3'
								}],
								onSelected : function(newvalue){
									window.parent.isNmImp = newvalue;
								}
							}
						}, {
							display : '上传文件',
							name : 'file',
							newline : true,
							width : 200,
							comboboxName : 'fileBox',
							type : "select",
							options : {
								onBeforeOpen : function() {
									window.parent.tab=window;
									window.parent.BIONE.commonOpenDialog("上传文件", "fileUpWin", 580, 340, urlPath +'/validLogicRel/uploadWin', null);
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