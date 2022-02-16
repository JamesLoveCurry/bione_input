<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<%@ include file="/common/taglibs.jsp"%>
<head>
<meta name="decorator" content="/template/template1_BS.jsp">
<script type="text/javascript">
    //全局变量
    var grid;
	var url = '${ctx}/frs/xmlMessage/xmlMessageList';//URL

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
			fields : [{
				display : "元素名称",
				name : "elementNm",
				newline : false,
				type : "text",
				cssClass : "field",
				attr : {
					op : "like",
					field : "elementNm"
				}
			},{
				display : "所属报表",
				name : "belongRptNum",
				newline : false,
				type : "text",
				cssClass : "field",
				attr : {
					op : "like",
					field : "belongRptNum"
				}
			}]
	    });
	}
    
  //渲染GRID
	function ligerGrid() {
	    grid = $("#maingrid").ligerGrid({
			InWindow : false,
			width : "100%",
			height : "98%",
			InWindow : true,
			columns : [{
				display : '元素名称',
				name : 'elementNm',
				width : '25%',
				align: 'left'
			},  {
				display : '上级元素',
				name : 'upElementNm',
				width : '25%',
				align: 'left'
			}, {
				display : '元素顺序',
				name : 'orderId',
				width : '6%',
				align: 'left'
			}, {
				display : '报送周期',
				name : 'submitCycle',
				width : '6%',
				align: 'left',
				render: submitCycleRender
			}, {
				display : '所属报表',
				name : 'belongRptNum',
				width : '6%',
				align: 'left'
			}, {
				display : '数据来源',
				name : 'elementSrc',
				width : '6%',
				align: 'left'
			}, {
				display : '缺省值',
				name : 'defaultValue',
				width : '6%',
				align: 'left'
			}, {
				display : '开始日期',
				name : 'startDate',
				width : '8%',
				align: 'left'
			}, {
				display : '结束日期',
				name : 'endDate',
				width : '8%',
				align: 'left'
			}],
			checkbox : false,
			userPager : true,
			rownumbers : true,
			alternatingRow : true,//附加奇偶行效果行
			colDraggable : true,
			dataAction : 'server',//从后台获取数据
			method : 'post',
			sortName : 'endDate', //第一次默认排序的字段
			sortOrder : 'asc',
			url : url
	    });
	}
	//转码
	function submitCycleRender(rowdata) {
		var rowDataStr = rowdata.submitCycle;
		if(rowDataStr == "M"){
			rowDataStr = "月报";
		}else if(rowDataStr == "Q"){
			rowDataStr = "季报";
		}else if(rowDataStr == "H"){
			rowDataStr = "半年报";
		}else if(rowDataStr == "A"){
			rowDataStr = "年报";
		}
		return rowDataStr;
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