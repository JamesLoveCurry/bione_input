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
	var data=null;
	$(function() {
		initGrid();
		initData();
	});

	function initData(){
	 
		var info=clone(window.parent.validateData);
		grid.set("data",{
			Rows: window.parent.validateData
		});
		window.parent.validateFlag=false;
	}
	
	function initGrid() {
		var g = {
			columns :  [ {
				display : '报表名称',
				name : 'fieldName',
				width :"20%",
				align : 'center'
			}, {
				display : '工作薄名称',
				name : 'sheetName',
				width :"10%",
				align : 'center'
			}, {
				display : '行号',
				name : 'excelRowNo',
				width :"6%",
				align : 'center'
			},{
				display : '列号',
				name : 'excelColNo',
				width :"4%",
				align : 'center'
			},{
				display : '校验类型名称',
				name : 'validTypeNm',
				width :"20%",
				align : 'center'
			},{
				display : '错误信息',
				name : 'errorMsg',
				width :"40%",
				align : 'center'
			}],
			width : '99%',
			height : '99%',
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
		$("#fileBox").val(info.result.fileName);
		$("#file").val(info.result.fileName);
	}
	
	function validate(){
		if(grid.getData().length>0){
			window.parent.BIONE.tip("存在校验错误，无法导入数据！");
			return false;
		}
		return true;
	}
	
	function loadData(){
		if(window.parent.validateFlag){
			grid.set("data",{
				Rows: window.parent.validateData
			});
			window.parent.validateFlag=false;
		}
		
	}
	
	function clone(obj){
		if (obj === null) return null;
		var o = [];
		for(var i in obj){
			o[i] = (obj[i] instanceof Date) ? new Date(obj[i].getTime()) : (typeof obj[i] === "object" ? arguments.callee(obj[i]) : obj[i]);
		}
		return o;
	}
	
</script>
</head>
<body>
	<div id="template.center">
		<div id='maingrid'>
		</div>
	</div>
</body>
</html>