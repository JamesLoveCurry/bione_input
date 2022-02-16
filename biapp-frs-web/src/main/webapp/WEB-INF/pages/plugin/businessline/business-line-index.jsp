<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<%@ include file="/common/taglibs.jsp"%>
<html>
<meta name="decorator" content="/template/template1.jsp">
<head>
<!-- 基础的JS和CSS文件 -->
<script type="text/javascript">
	var grid;
	$(function() {
		initSearchForm();
		initGrid();
		BIONE.loadToolbar(grid, [{
			text : '新增',
			click : add,
			icon : 'add'
		},{
			text : '修改',
			click : modify,
			icon : 'modify'
		},{
			text : '删除',
			click : remove,
			icon : 'delete'
		}/* ,{
			text : '配置',
			click : config,
			icon : 'config'
		} */]);
		BIONE.addSearchButtons("#search", grid, "#searchbtn");
	});
	
	function add(){
		BIONE.commonOpenDialog("新增业务条线","editlineDialog",600,250,
				"${ctx}/rpt/frame/businessline/addline");
	}
	
	function modify(){
		var rows = grid.getSelectedRows();
		if(rows.length == 0){
			BIONE.tip("请选择一条记录");
			return;
		}
		if(rows.length > 1){
			BIONE.tip("只能选择一条记录");
			return;
		}
		var lineId = rows[0].lineId;
		var lineNm = rows[0].lineNm;
		BIONE.commonOpenDialog("修改业务条线","editlineDialog",600,250,
		"${ctx}/rpt/frame/businessline/editline?lineId="+lineId+"&lineNm="+lineNm);
	}
	
	function remove(){
		var rows = grid.getSelectedRows();
		if(rows.length == 0){
			BIONE.tip("请至少选择一条记录");
			return;
		}
		var ids = [];
		for(var i in rows){
			ids.push(rows[i].lineId);
		}
		$.ligerDialog.confirm('您确定删除选中记录么？', function(yes) {
			if (yes) {
				$.ajax({
				    url:"${ctx}/rpt/frame/businessline/delline",  //请求的url地址
				    dataType:"json",   //返回格式为json
				    async:false,
				    data:{"ids":ids.join(",")},    //参数值
				    type:"GET",   //请求方式
				    success:function(data){
				    	if(data.msg == "success"){
				    		grid.reload();
				    		BIONE.tip('删除成功');
				    	}else{
				    		BIONE.tip('删除失败');
				    	}
				    },
				    error:function(){
						BIONE.tip('请求出错');
				    }
				});
			}
		});
	}

	function config(){
		var rows = grid.getSelectedRows();
		if(rows.length == 0){
			BIONE.tip("请选择一条记录");
			return;
		}
		if(rows.length > 1){
			BIONE.tip("只能选择一条记录");
			return;
		}
		var lineId = rows[0].lineId;
		BIONE.commonOpenDialog("配置模型","configDialog",700,500,
		"${ctx}/rpt/frame/businessline/config?lineId="+lineId);
	}
	
	function initSearchForm(){
		$("#search").ligerForm({
			fields : [{
				display : '业务条线编号',
				labelwidth : 100,
				name : "lineId",
				newline : true,
				type : "text",
				cssClass : "field",
				attr : {
					field : 'info.lineId',
					op : "like"
				}
			},{
				display : '业务条线名称',
				labelwidth : 100,
				name : "lineNm",
				newline : false,
				type : "text",
				cssClass : "field",
				attr : {
					field : 'info.lineNm',
					op : "like"
				}
			}]
		});
	};
	
	function initGrid(){
		grid = $("#maingrid").ligerGrid({
			columns : [ {
				display : '业务条线编号',
				name : 'lineId',
				width : "19%",
				align : 'left'
			},{
				display : '业务条线名称',
				name : 'lineNm',
				width : "19%",
				align : 'left'
			}],
			checkbox : true,
			dataAction : 'server', //从后台获取数据
			usePager : true, //服务器分页
			alternatingRow : true, //附加奇偶行效果行
			colDraggable : true,
			url : "${ctx}/rpt/frame/businessline/list.json",
			sortName : 'info.rankOrder,info.lineId',//第一次默认排序的字段
			sortOrder : 'desc', //排序的方式
			rownumbers : true,
			width:'100%',
			toolbar : {}
		});
	}
	
</script>
</head>
<body>
</body>
</html>