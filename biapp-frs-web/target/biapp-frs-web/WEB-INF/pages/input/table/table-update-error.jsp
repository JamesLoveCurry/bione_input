<%@ page language="java" pageEncoding="UTF-8"%>
<%@ include file="/common/taglibs.jsp"%>
<%@ page
	import="org.apache.shiro.web.filter.authc.FormAuthenticationFilter"%>
<html>
<meta name="decorator" content="/template/template8.jsp">
<head>
<script type="text/javascript">
	var groupicon = "${ctx}/images/classics/icons/communication.gif";
	var manager;
	$(function() {
		initGrid();
		manager = $("#maingrid").ligerGetGridManager();
		manager.addRow({
			resultInfos : '失败原因：' + '${result}'
		});
	});
	function initGrid() {
		manager = $("#maingrid").ligerGrid(
				{
					height : '96%',
					width : '100%',
					columns : [ {
						display : "提示信息",
						name : 'resultInfos',
						align : 'left',
						width : "80%",
						editor : {
							type : 'text'
						}
					} ],
					data : {
						Rows : []
					},
					checkbox : false,
					usePager : false, //服务器分页
					detail : {
						height : 'auto',
						onShowDetail : function(r, p) {
							$(p).append(
									$('<div>失败原因：' + '${result}' + '</div>')
											.css('margin', 20));
						}
					}
				});
	}
</script>
</head>
<body>
	<div id="content" style="width: 100%; overflow: auto; clear: both;">
	</div>
</body>
</html>