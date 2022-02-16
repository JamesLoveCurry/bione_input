<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<%@ include file="/common/meta.jsp"%>
<%@ include file="/common/taglibs.jsp"%>
<%@ include file="/common/taglibs.jsp"%>
<%@ include file="/common/jquery_load.jsp"%>
<%@ include file="/common/ligerUI_load.jsp"%>
<html xmlns="http://www.w3.org/1999/xhtml">
<html>
<head>
<%@ include file="/common/jquery_load.jsp"%>
<%@ include file="/common/ligerUI_load.jsp"%>
<link rel="stylesheet" type="text/css"
	href="${ctx}/css/classics/mainpage/example.css" />
	<link rel="stylesheet" type="text/css"
	href="${ctx}/css/classics/font-awesome/css/font-awesome.css" />
<script type="text/javascript">

//初始化
$(function() {
	initGrid();
});
function initGrid() {
	var url = "${ctx}/bione/frs/itemchange/initGrid";
	var data = {
		d : new Date()	
	}
	
	grid = $("#maingrid").ligerGrid({
		columns : [
					{
						display : '科目号',
						name : 'subjectNum',
						align : 'center',
						minWidth : '',
						width : '23%'
					}, {
						display : '科目名称',
						name : 'subjectName',
						align : 'center',
						minWidth : '',
						width : '30%'
					}, {
						display : '科目类型代码',
						name : 'subjectTypeCd',
						align : 'center',
						minWidth : '',
						width : '25%'
					} , {
						display : '变更类型',
						name : 'changeType',
						align : 'center',
						minWidth : '',
						width : 'auto',
						render: function(row){
							var changeType = row.changeType;
							if(changeType!=null&&changeType!=""&&changeType!="undefined"){
								if(changeType=="01"){
									return "新增";
								}else if(changeType=="02"){
									return "修改";
								}else{
									return "删除";
								}
							} 
						}
					} ], 
		checkbox: false,
		dataAction : 'server', 
		usePager : false, 
		alternatingRow : true,
		colDraggable : true,
		isScroll : true,
		url : url,
		data : data,
		sortOrder : 'desc', //排序的方式
		pageParmName : 'page',
		pagesizeParmName : 'pagesize',
		checkbox : false,
		rownumbers : true,
	});
}

//更多
	function openMore()
	{	
		//var url = "${ctx}/bione/frs/itemchange?d="+new Date().getTime();
		window.top.doMenuClick("b9fd294b0f6146f3b5a4255b304f7db7","科目变动提醒","/bione/frs/itemchange","false");
	}
</script>
<style>
.in_box_tit a{float:right;color:#4bbdfb;text-decoration:none;}
</style>
</head>
<body>
	<div id="all_div" class="in_box w300">
        <div class="in_box_titbg">
           <div class="in_box_tit" style = "background-color:#4b9efb;"><span class="icon">科目变动</span><span class="more"><a href="javascript:openMore()">更多</a></span></div>
        </div>
		<div id="maingrid" class="maingrid"></div>
	</div>
	<div class="gd-ibContent" >
		<div id="maingrid"></div> 
	</div>
</body>
</html>