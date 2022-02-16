<%@ page language="java" pageEncoding="UTF-8"%>
<%@ include file="/common/taglibs.jsp"%>
<%@ page import="org.apache.shiro.web.filter.authc.FormAuthenticationFilter"%>
<html>
<meta name="decorator" content="/template/template8.jsp">
<head>
<script type="text/javascript" src="${ctx}/js/udip/UDIP.js"></script>
<script type="text/javascript">
	var groupicon = "${ctx}/images/classics/icons/communication.gif";
	var grid,manager;
	var ids = [];
	var tableList;
	$(function() {
		initGrid();
		BIONE.loadToolbar(grid, [ {
			text : '导出结果',
			click : download,
			icon : 'export'
		} ]);
		
		UDIP.heart("${ctx}/datainput/heart/on");//心跳包
	});
	function initGrid() {
		grid = manager = $("#maingrid").ligerGrid(
			{
				height : '99%',
				width : '100%',
				columns : [{
					display : "规则名称", name : "ruleName", align : 'center', minWidth : 100, width : "30%"
				},{
					display : '校验结果', isSort : false, width : '59%', name : "validateResult", align : 'left'
				} ],
				checkbox : false,
				width : "100%",
				rownumbers : false,
				dataAction : 'server', //从后台获取数据
				//usePager : true, //服务器分页
				alternatingRow : true, //附加奇偶行效果行
				colDraggable : true,
				method : 'get',
				toolbar : {},
				sortName : 'rowNumber', //第一次默认排序的字段
				sortOrder : 'asc',
				pageParmName : 'page',
				pagesizeParmName : 'pagesize',
				url : '${ctx}/rpt/input/taskcase/getTaskCaseValidateLog?templeId='+'${templeId}'+'&caseId=' + '${caseId}'+'&d='+ new Date().getTime(),
                detail: {height:'auto', onShowDetail: function (r, p){
                        $(p).append($('<div>校验结果：' + r.validateResult + '</div>').css('margin', 20));
                    }
               	}
			});
	}
	function download(){
		if (!document.getElementById('download')) {
			$('body').append($('<iframe id="download" style="display:none"/>'));
		}
		$("#download").attr('src',
				'${ctx}/rpt/input/taskcase/excelDownLoalValidateInfo?templeId='+'${templeId}'+'&caseId=' + '${caseId}'+'&d=' + new Date().getTime());
	}
	
	
</script>
</head>
<body>
	<div id="content" style="width: 100%; overflow: auto; clear: both;">
	</div>
</body>
</html>