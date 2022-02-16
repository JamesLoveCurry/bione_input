<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<%@ include file="/common/taglibs.jsp"%>
<head>
<meta name="decorator" content="/template/template1_BS.jsp">
<script type="text/javascript">
    //全局变量
    var grid;
    var dialog;
	var triggerId = '${triggerId}';

    $(function() {
		//初始化
		var url = "${ctx}/frs/rpttsk/publish/getTriggerList";
		ligerSearchForm();//初始化查询表单
		ligerGrid();//初始化GRID
		BIONE.addSearchButtons("#search", grid, "#searchbtn");//生成搜索按钮
		
		//调整布局
		$("div .searchtitle").hide();
	
		//渲染查询表单
		function ligerSearchForm() {
		    $("#search").ligerForm({
			fields : [ {
			    display : "触发器名称",
			    name : "triggerName_name",
			    newline : true,
			    type : "text",
			    cssClass : "field",
			    attr : {
					field : "triggerName",
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
			    display : "触发器名称",
			    name : "triggerName",
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
			isChecked: f_isChecked,
			url : url
		    });
		}
	});
    
  //触发器ID与NAME赋予父页面对应控件
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
	function f_isChecked(rowdata)
    {
		if(triggerId){
			if (rowdata.triggerId== triggerId) 
	            return true;
		}
        return false;
    }
</script>
</head>
<body>
</body>
</html>