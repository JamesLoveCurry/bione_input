<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<%@ include file="/common/taglibs.jsp"%>
<head>
<meta name="decorator" content="/template/template1_BS.jsp">
<script type="text/javascript">
    //全局变量
    var grid;
    var dialog;

    $(function() {

	//初始化
	var url = "${ctx}/rpt/input/temple/list.json?toTask=true";//URL
	ligerSearchForm();//初始化查询表单
	ligerGrid();//初始化GRID
	BIONE.addSearchButtons("#search", grid, "#searchbtn");//生成搜索按钮

	//调整布局
	$("div .searchtitle").hide();

	//渲染查询表单
	function ligerSearchForm() {
	    $("#search").ligerForm({
		fields : [ {
		    display : "模板名称",
		    name : "selectedObjName_name",
		    newline : true,
		    type : "text",
		    cssClass : "field",
		    attr : {
			field : "templeName",
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
		    display : "模板名称",
		    name : "templeName",
		    width : "35%"
		}, {
		    display : "备注",
		    name : "remark",
		    width : "57%"
		} ],
		checkbox : false,
		usePager : true,
		rownumbers : true,
		alternatingRow : true,//附加奇偶行效果行
		sortName : 'templeName',//第一次默认排序的字段
		colDraggable : true,
		dataAction : 'server',//从后台获取数据
		method : 'post',
		url : url,
		onDblClickRow : function(rowdata, rowindex, rowDomElement) {//双击选择
		    addToParent(rowdata.templeId, rowdata.templeName);
		}
	    });
	}
	BIONE.createButton({
		appendTo : "#searchbtn",
		text : '选择',
		icon : 'true',
		click : function() {
			var rows = grid.getSelectedRows();
			if(rows.length==0){
				BIONE.tip("请选择模板");
				return; 
			}
			var rowdata = rows[0];
			addToParent(rowdata.templeId, rowdata.templeName);
		}
	});
	//触发器ID与NAME赋予父页面对应控件
	function addToParent(selectedObjId, selectedObjName) {
	    var main = parent || window;
	    var taskNm = main.$("[name='taskNm']");
	    taskNm.val(selectedObjName);
	    var selectBox = main.$("[name='selectedObjCombBox']");
	    selectBox.val(selectedObjName);
	    var hiddenBox = main.$("[name='selectedObjId']");
	    hiddenBox.val(selectedObjId);
	    BIONE.closeDialog("selectedObjBox");
	}
    });
</script>
</head>
<body>
</body>
</html>