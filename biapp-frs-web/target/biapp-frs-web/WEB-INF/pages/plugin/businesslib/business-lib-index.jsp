<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<%@ include file="/common/taglibs.jsp"%>
<html>
<meta name="decorator" content="/template/template1_BS.jsp">
<head>
<!-- 基础的JS和CSS文件 -->
<script type="text/javascript">
	var grid;
	var renderData;
	
	$(function() {
		initRanderData();
		initSearchForm();
		initGrid();
	});
	
	//初始化数据码值数据
	function initRanderData(){
		$.ajax({
			async : false,
			type : "post",
			url : '${ctx}/rpt/frame/businesslib/getDsData',
			success : function(res){
				renderData = res;
			},
			error : function(e){
				BIONE.tip('初始化数据加载失败');
			}
		});
	}
	
	function initSearchForm(){
		$("#search").ligerForm({
			fields : [{
				display : '业务库编号',
				labelwidth : 100,
				name : "busiLibNo",
				newline : true,
				type : "text",
				cssClass : "field",
				attr : {
					field : 'r.busiLibNo',
					op : "like"
				}
			},{
				display : '业务库名称',
				labelwidth : 100,
				name : "busiLibNm",
				newline : false,
				type : "text",
				cssClass : "field",
				attr : {
					field : 'r.busiLibNm',
					op : "like"
				}
			}]
		});
	};

	function initGrid(){
		grid = $("#maingrid").ligerGrid({
			columns : [ {
				display : '业务库编号',
				name : 'busiLibNo',
				width : "15%",
				align : 'left'
			},{
				display : '业务库名称',
				name : 'busiLibNm',
				width : "19%",
				align : 'left'
			},{
				display : '数据源名称',
				name : 'dsId',
				width : "19%",
				align : 'left',
				render: dsRender
			},{
				display : '数据集编号',
				name : 'setId',
				width : "19%",
				align : 'left'
			},{
				display : '备注',
				name : 'remark',
				width : "19%",
				align : 'left'
			}],
			checkbox : true,
			isScroll : true,
			dataAction : 'server', //从后台获取数据
			usePager : true, //服务器分页
			alternatingRow : true, //附加奇偶行效果行
			colDraggable : true,
			url : "${ctx}/rpt/frame/businesslib/gridList.json",
			sortName : 'r.busiLibNo',//第一次默认排序的字段
			sortOrder : 'desc', //排序的方式
			rownumbers : true,
			width:'100%',
			height : '99%',
			toolbar : {}
		});
		
		BIONE.loadToolbar(grid, [{
			text : '新增',
			click : add,
			icon : 'fa-plus'
		},{
			text : '修改',
			click : modify,
			icon : 'fa-pencil-square-o'
		},{
			text : '删除',
			click : remove,
			icon : 'fa-trash-o'
		}]);
		BIONE.addSearchButtons("#search", grid, "#searchbtn");
	}
	
	function dsRender(rowdata){
		var renderVal = renderData[rowdata.dsId];
		return renderVal;
	}
	
	function add(){
		var dialogUrl = "${ctx}/rpt/frame/businesslib/edit";
		BIONE.commonOpenDialog("新增业务库","editDialogWin",700,500, dialogUrl);
	}
	
	function modify(){
		var rows = grid.getSelectedRows();
		if(rows.length != 1){
			BIONE.tip("请选择一条记录");
			return;
		}
		var busiLibId = rows[0].busiLibId;
		var dialogUrl = "${ctx}/rpt/frame/businesslib/edit?id="+busiLibId;
		BIONE.commonOpenDialog("修改业务库","editDialogWin",700,500, dialogUrl);
	}
	
	function remove(){
		var rows = grid.getSelectedRows();
		if(rows.length == 0){
			BIONE.tip("请至少选择一条记录");
			return;
		}
		var ids = [];
		for(var i in rows){
			ids.push(rows[i].busiLibId);
		}
		$.ligerDialog.confirm('您确定删除选中记录么？', function(yes) {
			if (yes) {
				$.ajax({
				    url:"${ctx}/rpt/frame/businesslib/deleteBusiLib",  //请求的url地址
				    async:false,
				    data:{"ids":ids.join(",")},    //参数值
				    type:"POST",   //请求方式
				    success:function(){
				    	grid.loadData();
				    	BIONE.tip('删除成功');
				    },
				    error:function(){
						BIONE.tip('请求出错');
				    }
				});
			}
		});
	}
</script>
</head>
<body>
</body>
</html>