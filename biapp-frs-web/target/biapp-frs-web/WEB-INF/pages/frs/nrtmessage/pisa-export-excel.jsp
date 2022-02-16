<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<%@ include file="/common/taglibs.jsp"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta name="decorator" content="/template/template1.jsp">

<script type="text/javascript">
	
	//全局变量
	var grid;
	var url = "${ctx}/frs/pisamessage/selectRptList.json";//URL
	
	$(function(){
		//初始化
		ligerSearchForm();//初始化查询表单
		ligerGrid();//初始化GRID
		initButtons()
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
			    width : "35%"
			}, {
			    display : "报表名称",
			    name : "rptNm",
			    width : "57%"
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
			url : url,
			toolbar : {}
	    });
	}
	
	function initButtons() {
		var btns = [{
			text : '导出Excel',
			click : f_save,
			icon : 'export'
		}];
		BIONE.loadToolbar(grid, btns, function() {});
	}
  
	//导出
	function f_save(){
		var rows = grid.getSelectedRows();
		if (rows.length < 1) {
			BIONE.tip('请选择记录');
		} else if (rows.length > 1) {
			BIONE.tip('只能选择一条记录');
		} else {
			var rptNums=""
			for(var i in rows){
				rptNums += rows[i].rptNum + ",";
			}
			rptNums = rptNums.substring(0,rptNums.length-1);
			
			$.ajax({
				async:true,
				type:"POST",
				dataType:"json",
				url:"${ctx}/report/frame/wizard/download?type=PisaIdx",
				data:{"ids":rptNums},
				beforeSend : function(a, b, c) {
					BIONE.showLoading('正在导出数据中...');
				},
				success:function(data){
					BIONE.hideLoading();
					window.parent.exportXls(data.fileName, rptNums);
					BIONE.closeDialog("exportWin");
				},
				error : function(result) {
					BIONE.hideLoading();
					BIONE.tip('发现系统错误 <BR>错误码：' + result.status);
				}
			});
		}
	}
</script>
</head>
<body>
</body>
</html>