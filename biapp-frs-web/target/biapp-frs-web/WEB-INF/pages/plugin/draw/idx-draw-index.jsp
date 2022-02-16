<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<%@ include file="/common/taglibs.jsp"%>
<html>
<head>
<meta name="decorator" content="/template/template1.jsp">
<script type="text/javascript">

	var dialog;
	var grid;
	
	$(function() {
		$("#search").ligerForm({
			fields : [ {
				display : "指标编号 ",
				name : "indexNo",
				newline : true,
				type : "text",
				cssClass : "field",
				attr : {
					field : "idx.index_no",
					op : "="
				}
			},{
				display : "指标名称 ",
				name : "indexNm",
				newline : false,
				type : "text",
				cssClass : "field",
				attr : {
					field : "idx.index_nm",
					op : "like"
				}
			}]
		});

		initGrid();
		
		var btns = [{
			text : '手工翻牌',
			click : modify,
			icon : 'modify',
			operNo : 'modify'
		}];

		BIONE.loadToolbar(grid, btns, function() {});
		
		BIONE.addSearchButtons("#search", grid, "#searchbtn");
		if ("${id}") {
			
		}
		
	});
	//初始化表格 
	function initGrid() {
		grid = $("#maingrid").ligerGrid({
			//InWindow : false,
			width : '100%',
			columns : [{
				display : '指标编号',
				name : 'indexNo',
				sortname : 'idx.INDEX_NO',
				width : '20%',
				render : function(row,index,val){
					return "<a style='color:blue' onclick='f_view(\""+row.indexNo+"\",\""+row.indexNm+"\")'>"+val+"</a>"
				}
			}, {
				display : '指标名称 ',
				name : 'indexNm',
				sortname : 'idx.INDEX_NM',
				width : '40%',
			},{
				display : '翻牌日期',
				name : 'drawDate',
				sortname : 'pid.DRAW_DATE',
				width : '35%'
			}],
			checkbox : true,
			isScroll : true,
			rownumbers : true,
			dataAction : 'server',//从后台获取数据
			method : 'post',
			url : "${ctx}/rpt/idx/draw/list.json",
			usePager : true, //服务器分页
			alternatingRow : true, //附加奇偶行效果行
			colDraggable : true,
			pageParmName :  'page',
			pagesizeParmName : 'pagesize',
			toolbar : {}
		});
		
	}
	//手工翻牌
	function modify(){
		var rows = grid.getSelectedRows();
		var indexNos = '';//用于处理批量选择的记录
		if(rows.length == 0){
			BIONE.tip("请至少选择一条记录");
			return false;
		}
		for(var i in rows){
			indexNos += rows[i].indexNo+",";
		}
		window.indexNos = indexNos;
		BIONE.commonOpenSmallDialog('手工翻牌', 'editWin','${ctx}/rpt/idx/draw/edit');
	}
	//点击指标查看指标翻牌详情
	function f_view(indexNo,indexNm){
		BIONE.commonOpenDialog(indexNo+":"+indexNm+"--翻牌日志",
				"indexDrawLogDialog",700,300, 
				"${ctx}/rpt/idx/draw/view?indexNo="+indexNo);
	}
	
</script>
</head>
<body>
</body>
</html>