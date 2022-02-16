<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<%@ include file="/common/taglibs.jsp"%>
<head>
<meta name="decorator" content="/template/template1_BS.jsp">
<script type="text/javascript">
    //全局变量
    var grid;
	var url = "${ctx}/frs/pbmessage/selectRangeList.json";//URL

    $(function() {
		//初始化
		ligerSearchForm();//初始化查询表单
		ligerGrid();//初始化GRID
		BIONE.addSearchButtons("#search", grid, "#searchbtn");//生成搜索按钮
		//调整布局
		$("div .searchtitle").hide();
    });
    
  	//渲染查询表单
	function ligerSearchForm() {
	    $("#search").ligerForm({
		fields : [ {
		    display : "范围编号",
		    name : "submitRangeCode_name",
		    newline : true,
		    type : "text",
		    cssClass : "field",
		    attr : {
			field : "range.submitRangeCode",
				op : "like"
		    }
		},{
		    display : "范围名称",
		    name : "submitRangeNm_name",
		    newline : false,
		    type : "text",
		    cssClass : "field",
		    attr : {
			field : "range.submitRangeNm",
				op : "like"
		    }
		} ]
	    });
	}
    
  //渲染GRID
	function ligerGrid() {
	    grid = $("#maingrid").ligerGrid({
			InWindow : false,
			width : "100%",
			height : "98%",
			InWindow : true,
			columns : [ {
			    display : "报送范围编号",
			    name : "submitRangeCode",
			    width : "35%"
			}, {
			    display : "报送范围名称",
			    name : "submitRangeNm",
			    width : "57%"
			} ],
			checkbox : false,
			userPager : true,
			rownumbers : true,
			alternatingRow : true,//附加奇偶行效果行
			colDraggable : true,
			dataAction : 'server',//从后台获取数据
			method : 'post',
			sortName : 'submitRangeCode', //第一次默认排序的字段
			sortOrder : 'asc',
			url : url
	    });
	}
    
    function addToParent() {
		var rows = grid.getSelectedRows();
		if (rows.length != 1) {
			BIONE.tip('请选择一条记录!');
		} else {
			return rows[0];
		}
		return;
	}
</script>
</head>
<body>
</body>
</html>