<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<%@ include file="/common/taglibs.jsp"%>
<head>
<meta name="decorator" content="/template/template1_BS.jsp">
<script type="text/javascript">
    //全局变量
    var grid;
    var dialog;
    var userAgname = '${userAgname}';

    $(function() {
		//初始化
		initSearchForm();//初始化查询表单
		initGrid();//初始化GRID
		BIONE.addSearchButtons("#search", grid, "#searchbtn");//生成搜索按钮
		
		//调整布局
		$("div .searchtitle").hide();
	
		//渲染查询表单
		function initSearchForm() {
		    $("#search").ligerForm({
			fields : [ {
			    display : "柜员号",
			    name : "gyh",
			    type : "text",
			    cssClass : "field",
			    attr : {
					field : "gyh",
					op : "like"
			    }
			}, {
			    display : "柜员名称",
			    name : "gymc",
			    type : "text",
			    cssClass : "field",
			    attr : {
					field : "gymc",
					op : "like"
			    }
			} ]
		    });
		}
	
		//渲染GRID
		function initGrid() {
		    grid = $("#maingrid").ligerGrid({
			InWindow : false,
			width : "100%", 
			height : "98%",
			InWindow : true,
			columns : [ {
			    display : "柜员号",
			    name : "gyh",
			    width : "20%"
			}, {
			    display : "柜员名称",
			    name : "gymc",
			    width : "20%"
			},{
			    display : "工号",
			    name : "gh",
			    width : "20%"
			},{
			    display : "内部机构号",
			    name : "nbjgh",
			    width : "20%"
			},{
			    display : "是否实体柜员",
			    name : "sfstgy",
			    width : "20%"
			},{
				display : "客户经理标志",
			    name : "khjlbz",
			    width : "20%"
			},{
				display : "岗位编号",
			    name : "gwbh",
			    width : "20%"
			},{
				display : "柜员状态",
			    name : "gwzt",
			    width : "20%"
			}],
			checkbox : true,
			usePager : true,
			isScroll : false,
			rownumbers : true,
			alternatingRow : true,//附加奇偶行效果行
			colDraggable : true,
			dataAction : 'server',//从后台获取数据
			method : 'post',
			sortName : 'gyh', //第一次默认排序的字段
			sortOrder : 'asc',
			isChecked: f_isChecked,
			url : "${ctx}/crrsBsStaff/getCustomerManagerList"
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
		if(userAgname){
			if (rowdata.gyh== userAgname) 
	            return true;
		}
        return false;
    }
</script>
</head>
<body>
</body>
</html>