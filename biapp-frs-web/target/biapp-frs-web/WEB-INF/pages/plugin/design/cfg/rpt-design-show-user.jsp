<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<%@ include file="/common/taglibs.jsp"%>
<html>
<meta name="decorator" content="/template/template1_BS.jsp">
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<script>
	var rptNum = '${rptNum}';
	var mainform;
	var grid;
	$(function() {
		initForm();
		initGrid();
		initButtons();
		BIONE.addSearchButtons("#search", grid, "#searchbtn");
	});
	
	//初始化按钮
	function initButtons() {
		var btns = [{
			text : "删除",
			icon : "icon-delete",
			click : onDelete,
			operNo : "onDelete"
			}];
		BIONE.loadToolbar(grid, btns, function() {});
	}
		
	function onDelete(){
		var rows = grid.getSelectedRows();
		if(rows != null && rows.length >0){
			var ids = "";
			for(var i=0; i<rows.length; i++){
				ids = ids + rows[i].id + ",";
			}
			$.ajax({
				cache : false,
				async : false,
				url : "${ctx}/report/frame/design/cfg/deleteRptUserRel",
				dataType : 'json',
				data : {
					ids : ids
				},
				type : "post",
				success : function(result) {
					if(result && result.msg){
						grid.loadData();
						BIONE.tip(result.msg);
					}
				},
			});
		}else{
			BIONE.tip("请选择要删除的记录！");
		}
	}
	
	function initForm() {
		mainform = $("#search").ligerForm({
			fields : [ {
				display : "机构名称",
				name : "orgNm",
				newline : false,
				type : "text",
				width : '140',
				cssClass : "field",
				labelWidth : '90',
				attr : {
					op : "like",
					field : "t3.org_nm"
				}
			}]
		});
	}

	function initGrid() {
		grid = $("#maingrid").ligerGrid({
			columns : [{
				display : '报表名称',
				name : 'rptNm',
				width : "25%",
				align : "left"
			}, {
				display : '机构名称',
				name : 'orgNm',
				width : "15%"
			}, {
				display : '填报人',
				name : 'fillEr',
				width : "8%"
			}, {
				display : '负责人',
				name : 'charger',
				width : "8%"
			}, {
				display : '复核人',
				name : 'auditor',
				width : "8%"
			}, {
				display : '电话',
				name : 'phoneNumber',
				width : "12%"
			}, {
				display : '备注',
				name : 'remark',
				width : "20%"
			}],
			height : '99%',
			width : '100%',
			checkbox : true,
			rownumbers : true,
            isScroll : true,
            alternatingRow : true,//附加奇偶行效果行
            colDraggable : false,
			dataAction : 'server',//从后台获取数据
            method : 'post',
			url : "${ctx}/report/frame/design/cfg/getRptUserRel?rptNum="+rptNum,
			toolbar : {}
		});
	}
</script>
<title>人员详情</title>
</head>
<body>
</body>
</html>