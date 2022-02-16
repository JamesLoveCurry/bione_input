<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<%@ include file="/common/taglibs.jsp"%>
<head>
<meta name="decorator" content="/template/template1_BS.jsp">
<script type="text/javascript">
    //全局变量
    var grid;
	var url = "${ctx}/frs/xmlMessage/selectRptList.json";//URL

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
		    display : "报表编号",
		    name : "rptNum_name",
		    newline : true,
		    type : "text",
		    cssClass : "field",
		    attr : {
			field : "rpt.rptNum",
				op : "like"
		    }
		},{
		    display : "报表名称",
		    name : "rptNm_name",
		    newline : false,
		    type : "text",
		    cssClass : "field",
		    attr : {
			field : "rpt.rptNm",
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
			    display : "报表编号",
			    name : "rptNum",
			    width : "35%",
				align: 'left'
			}, {
			    display : "报表名称",
			    name : "rptNm",
			    width : "57%",
				align: 'left'
			} ],
			checkbox : false,
			userPager : true,
			rownumbers : true,
			alternatingRow : true,//附加奇偶行效果行
			colDraggable : true,
			dataAction : 'server',//从后台获取数据
			method : 'post',
			sortName : 'rptNum', //第一次默认排序的字段
			sortOrder : 'asc',
			url : url
	    });
	}
    
    function addToParent() {
		var rows = grid.getSelectedRows();
		if (rows.length < 1) {
			BIONE.tip('请选择记录');
		} else if (rows.length > 1) {
			BIONE.tip('只能选择一条记录');
		} else {
			return rows[0];
		}
		return "";
	}
</script>
</head>
<body>
</body>
</html>