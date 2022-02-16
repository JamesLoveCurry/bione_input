<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<%@ include file="/common/taglibs.jsp"%>
<html>
<head>
<meta name="decorator" content="/template/template5.jsp">
<script type="text/javascript">
	var grid;
	var taskId = "${taskId}";
	var dataDate = "${dataDate}";
	var orgTypes = "${orgTypes}";
	$(function() {
		//初始化grid
		initGrid();
		//初始化按钮
		initButton();
		
	});
	//
	function initGrid(){
		grid = $("#maingrid").ligerGrid({
			columns : [ {
				display : '报表名称',
				name : 'rptNm',
				width : '40%',
				align : 'center'
			},{
				display : '校验公式',
				name : 'expression',
				width : '40%',
				align : 'center'
			}, {
				display : '状态',
				name : 'preSts',
				width : '20%',
				align : 'center',
				render :function(rowdata, index, value){
					if(value=="01"){
						return "预校验未通过";
					}else if(value=="02"){
						return "预校验通过";
					}else if(value=="03"){
						return "无预校验";
					}else{
						return "未知";
					}
				}
			} ],
			checkbox : false,
			usePager : false,
			rownumbers : false,
			isScroll : true,
			alternatingRow : true,//附加奇偶行效果行
			colDraggable : false,
			dataAction : 'server',//从后台获取数据
			method : 'get',
			url : "${ctx}/frs/rpttsk/monitor/getAuthInsValid?taskId="+taskId+"&dataDate="+dataDate+"&orgTypes="+orgTypes+"&d="+new Date().getTime(),
			sortName : 'rptNm',//第一次默认排序的字段
			sortOrder : 'asc', //排序的方式
			width : '99.8%',
			height:$("#center").height()-4
		});
	}
	//初始化按钮
	function initButton(){
		var buttons = [];
		/* buttons.push({
		    text : '数据重跑',
		    onclick : function() {
		    	
		    }
		}); */
		buttons.push({
		    text : '关闭',
		    onclick : function(){
		    	BIONE.closeDialog("tskTskDetailWin");
		    }
		},{
		    text : '强制下发',
		    onclick : publish
		});
		BIONE.addFormButtons(buttons);
	}
	//强制下发
	function publish(){
		$.ajax({
			async : false,
			type : "get",
			dataType : "json",
			url : '${ctx}/frs/rpttsk/monitor/prePublish?taskId=' +taskId+'&dataDate='+dataDate,
		    success : function(result) {
		    	if(result.msg){
		    		BIONE.tip(msg);
		    		return ;
		    	}
		    	parent.grid.loadData();
		    	BIONE.tip('强制下发成功');
		    	BIONE.closeDialog("tskTskDetailWin", null);
				
		    },error : function(result, b) {
				BIONE.tip('强制下发错误 <BR>错误码：' + result.status);
		    }
		});
	}
</script>
<title>Insert title here</title>
</head>
<body>
	<div id="template.center">
		<div id="maingrid" class="maingrid"></div>
	</div>
</body>
</html>