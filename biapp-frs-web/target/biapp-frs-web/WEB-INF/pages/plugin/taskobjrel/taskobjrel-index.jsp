<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<%@ include file="/common/taglibs.jsp"%>
<html>
<head>
<meta name="decorator" content="/template/template1_BS.jsp">
<script type="text/javascript">

	var dialog;
	var grid;
	
	$(function() {
		$("#search").ligerForm({
			fields : [ {
				display : "任务名称 ",
				name : "taskNm",
				newline : true,
				type : "text",
				cssClass : "field",
				attr : {
					field : "taskNm",
					op : "like"
				}
			}]
		});

		initGrid();
		
		var btns = [{
			text : '添加报表组',
			click : addRpt,
			icon : 'fa-plus',
			operNo : 'add'
		},{
			text : '添加指标组',
			click : addIdx,
			icon : 'fa-plus',
			operNo : 'add'
		},{
			text : '修改',
			click : modify,
			icon : 'fa-pencil-square-o',
			operNo : 'modify'
		},{
			text : '删除',
			click : deleteDs,
			icon : 'fa-trash-o',
			operNo : 'deleteDs'
		}];

		BIONE.loadToolbar(grid, btns, function() {});
		
		BIONE.addSearchButtons("#search", grid, "#searchbtn");
		if ("${id}") {
			
		}
		
	});
	//初始化表格 
	function initGrid() {
		//alert("进入initDrid");
		grid = $("#maingrid").ligerGrid({
			//InWindow : false,
			width : '100%',
			height : '99%',
			columns : [{
				display : '任务组编号',
				name : 'taskNo',
				width : '30%'
			},{
				display : '任务组名称',
				name : 'taskNm',
				width : '30%'
			}, {
				display : '任务组类型 ',
				name : 'objType',
				width : '20%',
				render:function(a,b,c){
					if(c=="01")
						return "报表组";
					if(c=="03")
						return "指标组";
				}
			}],
			checkbox : true,
			isScroll : true,
			rownumbers : true,
			dataAction : 'server',//从后台获取数据
			method : 'post',
			url : "${ctx}/report/frame/taskobjrel/list.json",
			usePager : true, //服务器分页
			alternatingRow : true, //附加奇偶行效果行
			colDraggable : true,
			pageParmName :  'page',
			pagesizeParmName : 'pagesize',
			toolbar : {}
		});
		
	}
	//新加
	function addRpt() {
		BIONE.commonOpenDialog('新增报表组', 'editWin',$(document).width()-600,$(document).height()-100,
		'${ctx}/report/frame/taskobjrel/edit?type=01');
	}
	//新加
	function addIdx() {
		BIONE.commonOpenDialog('新增指标组', 'editWin',$(document).width()-600,$(document).height()-100,
		'${ctx}/report/frame/taskobjrel/edit?type=03');
	}
	//修改
	function modify(){
		var rows = grid.getSelectedRows();
		if(rows.length == 0){
			BIONE.tip("请选择一个任务组");
			return;
		}
		if(rows.length > 1){
			BIONE.tip("只能选择一个任务组");
			return;
		}
		if (rows.length == 1) {
			if(rows[0].objType=="01")
				BIONE.commonOpenDialog('修改报表组', 'editWin',$(document).width()-600,$(document).height()-100,
				'${ctx}/report/frame/taskobjrel/edit?id='+rows[0].taskNo+'&type=01');
			if(rows[0].objType=="03")
				BIONE.commonOpenDialog('修改指标组', 'editWin',$(document).width()-600,$(document).height()-100,
				'${ctx}/report/frame/taskobjrel/edit?id='+rows[0].taskNo+'&type=03');
		}
	}

	//删除
	function deleteDs() {
		//获得被选中 的行
		var rows = grid.getSelectedRows();
		//判断被选中的行数  ，分别进行 不同的操作 
		if (rows.length > 0) {
			$.ligerDialog
					.confirm(
							'您确定删除这' + rows.length + "条记录么？",
							function(yes) {
								if (yes) {
									var ids=[];
									for ( var i = 0; i < rows.length; i++) {
										ids.push(rows[i].taskNo);
									}
									$.ajax({
										type : "POST",
										url : "${ctx}/report/frame/taskobjrel/remove",
										data:{
											ids :ids.join(",")
										},
										dataType : "json",
										success : function(result) {
												if(result.msg){
													BIONE.tip(result.msg);
												}else{
													BIONE.tip('删除成功');
													grid.loadData();
												}
										} 
									});
									
									
								}
							});
		} else {
			BIONE.tip('请选择要删除的行');
		}
	}
	
	
</script>
</head>
<body>
</body>
</html>