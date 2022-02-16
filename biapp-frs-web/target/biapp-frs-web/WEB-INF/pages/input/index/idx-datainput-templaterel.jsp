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
	var url = "${ctx}/rpt/input/idxdatainput/getTemplateList?catalogId=showAll";//URL
	ligerSearchForm();//初始化查询表单
	ligerGrid();//初始化GRID
	BIONE.addSearchButtons("#search", grid, "#searchbtn");//生成搜索按钮

	//调整布局
	$("div .searchtitle").hide();

	//渲染查询表单
	function ligerSearchForm() {
	    $("#search").ligerForm({
		fields : [ {
		    display : "指标模板名称",
		    name : "selectedObjName_name",
		    newline : true,
		    type : "text",
		    cssClass : "field",
		    attr : {
			field : "selectedObjName",
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
		    display : "指标模板名称",
		    name : "templateNm",
		    width : "35%"
		}, {
		    display : "备注",
		    name : "remark",
		    width : "57%"
		} ],
		checkbox : false,
		userPager : true,
		rownumbers : true,
		alternatingRow : true,//附加奇偶行效果行
		colDraggable : true,
		dataAction : 'server',//从后台获取数据
		method : 'post',
		url : url,
		onDblClickRow : function(rowdata, rowindex, rowDomElement) {//双击选择
		    addToParent(rowdata.templateId, rowdata.templateNm);
		}
	    });
	}

	//触发器ID与NAME赋予父页面对应控件
	function addToParent(selectedObjId, selectedObjName) {
	    var main = parent || window;
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