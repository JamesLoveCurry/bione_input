<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<%@ include file="/common/taglibs.jsp"%>
<head>
<meta name="decorator" content="/template/template1_BS.jsp">
<script type="text/javascript">
    //全局变量
    var grid;
    var dialog;
    var dsId = '${dsId}';

    $(function() {
	//初始化
	ligerSearchForm();//初始化查询表单
	ligerGrid();//初始化GRID
	BIONE.addSearchButtons("#search", grid, "#searchbtn");//生成搜索按钮

	//调整布局
	$("div .searchtitle").hide();

	//渲染查询表单
	function ligerSearchForm() {
	    $("#search").ligerForm({
		fields : [ {
		    display : "表空间名称",
		    name : "tableSpaceNm",
		    newline : true,
		    type : "text",
		    cssClass : "field",
		    attr : {
			field : "tableSpaceNm",
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
		    display : "表空间名称",
		    name : "tableSpaceNm",
		    width : "60%"
		} ],
		checkbox : false,
		userPager : true,
		rownumbers : true,
		alternatingRow : true,//附加奇偶行效果行
		colDraggable : true,
		dataAction : 'server',//从后台获取数据
		method : 'post',
		url : '${ctx}/rpt/input/table/getTableSpace.json?dsId='+dsId+'&d='+new Date().getTime(),
		onDblClickRow : function(rowdata, rowindex, rowDomElement) {//双击选择
		    addToParent(rowdata.tableSpaceNm, rowdata.tableSpaceNm);
		}
	    });
	}
	BIONE.createButton({
		appendTo : "#searchbtn",
		text : '选择',
		icon : 'true',
		width : '50px',
		click : function() {
			var rows = grid.getSelectedRows();
			if(rows.length==0){
			    BIONE.closeDialog("tableSpaceBox");
			    return ;
			}
			var rowdata = rows[0];
			addToParent(rowdata.triggerId, rowdata.tableSpaceNm);
		}
	});
	//触发器ID与NAME赋予父页面对应控件
	function addToParent(triggerId, triggerName) {
	    var main = parent || window;
	    var selectBox = main.$("[name='tableSpaceSelect']");
	    selectBox.val(triggerName);
	    var hiddenBox = main.$("[name='tableSpace']");
	    hiddenBox.val(triggerId);
	    BIONE.closeDialog("tableSpaceBox");
	}
    });
</script>
</head>
<body>
</body>
</html>