<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ include file="/common/taglibs.jsp"%>
<html>
<head>
<meta name="decorator" content="/template/template1.jsp">
<!-- 基础的JS和CSS文件 -->
<script type="text/javascript">

	var grid;
	function initSearchForm() {
		$("#search").ligerForm({
			fields : [{
				display : '报表名称',
				name : "rptNm",
				newline : false,
				type : "text",
				cssClass : "field",
				attr : {
					op : "like",
					field : 'usr.obj_Name'
				}
				
			},{
				display : '用户名',
				name : "userName",
				newline : false,
				cssClass : "field",
				attr : {
					op : "like",
					field : 't.user_Name'
				}
			}]
		});
	}; 
	
	function initGrid() {	
		grid = $("#maingrid").ligerGrid({
			columns : [{
				display : '报表名称',
				name : 'objName',
				width : "30%",
				type : 'date',
				format : 'yyyy-MM-dd hh:mm:ss',
				align : 'center'
			},/* {
				display : '对象类型',
				name : 'objType',
				width : "10%",
				align : 'center',
				render : function(row,a,val){
					switch(val){
						case "1":
							return "自定义报表"
							break;
						case "2":
							return "指标查询组"
							break;
					}
				}
			}, */{
				display : '分享用户',
				name : 'objUserName',
				width : "25%",
				align : 'center'
			},{
				display : '分享时间',
				name : 'createTime',
				width : "25%",
				align : 'center',
				type : "date",
				format : "yyyy-MM-dd hh:mm:ss"
			},{
				display : '分享状态',
				name : 'shareSts',
				width : "15%",
				align : 'center',
				render : function(row){
					return renderHandler(row);
				}
			}],
			checkbox: false,
			dataAction : 'server', 
			usePager : true, 
			alternatingRow : true,
			colDraggable : true,
			url : "${ctx}/rpt/frame/rptmgr/share/otherlist.json",
 			sortName : 'create_Time',//第一次默认排序的字段
 			sortOrder : 'desc', //排序的方式
			pageParmName : 'page',
			pagesizeParmName : 'pagesize',
			checkbox : false,
			rownumbers : true
		});
	};
	
	function renderHandler(row){
		if (1 == row.shareSts){
			return "分享中"
		}
		else if(2 == row.shareSts){
			return "取消分享";
		}
		else if(row.shareSts == "3"){
			return "已发布";
		}
		else if(row.shareSts == "4"){
			return "已删除";
		}
	}
	
	
	$(function() {	
		initSearchForm();
		initGrid();
		BIONE.addSearchButtons("#search", grid, "#searchbtn");
	});
	
	
</script>
</head>
<body>
</body>
</html>