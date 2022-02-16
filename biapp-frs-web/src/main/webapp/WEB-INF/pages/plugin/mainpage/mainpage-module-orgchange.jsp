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
	var url = "${ctx}/bione/frs/orgchange/initGrid";
	var data = {
		d : new Date()	
	}

	grid = $("#maingrid").ligerGrid({
		columns : [
					{
						display : '机构编号',
						name : 'orgNo',
						align : 'center',
						minWidth : '',
						width : '23%'
					}, {
						display : '机构名称',
						name : 'orgNm',
						align : 'center',
						minWidth : '',
						width : '48%',
					} , {
						display : '变更类型',
						name : 'changeType',
						align : 'center',
						minWidth : '',
						width : '',
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
						}, 
						type : 'date',
						format : 'yyyy-MM-dd'
					}], 
		checkbox: false,
		dataAction : 'server', 
		usePager : false, 
		alternatingRow : true,
		colDraggable : true,
		isScroll : true,
		url : url,
		sortOrder : 'desc', //排序的方式
		pageParmName : 'page',
		pagesizeParmName : 'pagesize',
		checkbox : false,
		rownumbers : true
	});
}

//更多
function openMore() {	
	//var url = "${ctx}/bione/frs/orgchange?d="+new Date().getTime();
	window.top.doMenuClick("1dd2c453d85e48d98e97d76d64a2858d","机构变动提醒","/bione/frs/orgchange","false");
}
</script>
<style>
.in_box_tit a{float:right;color:#4bbdfb;text-decoration:none;}
</style>
</head>
<body>
	<div id="all_div" class="in_box w300">
        <div class="in_box_titbg">
          <div class="in_box_tit" style = "background-color:#4b9efb;"><span class="icon">机构变动</span><span class="more"><a href="javascript:openMore()">更多</a></span></div>
        </div>
		<div id="maingrid" class="maingrid"></div>
	</div>
</body>
</html>