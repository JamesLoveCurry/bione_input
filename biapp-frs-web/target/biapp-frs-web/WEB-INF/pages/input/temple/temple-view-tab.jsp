<%@ page language="java" pageEncoding="UTF-8"%>
<%@ include file="/common/taglibs.jsp"%>
<%@ page
	import="org.apache.shiro.web.filter.authc.FormAuthenticationFilter"%>
<html>
<meta name="decorator" content="/template/template9_1.jsp">
<head>
<title>Insert title here</title>
<script type="text/javascript" src="${ctx}/js/udip/remark/temple.js"></script>
<script type="text/javascript">
	var tabObj = null;
	var colGrid = null;
	var canSelect = false;
	var lookType = "${lookType}";
	var templeId;
	var templeName;
	var templeInfo;
	var savaType = 0;
	//数据集缓存对象
	var datasetObj = {
		firstEdit : true,//首次修改
		refresh : false,//是否刷新
		from : null,//数据来源
		table : "",
		datasetId : "${datasetId}",
		catalogId : "${catalogId}",
		catalogName : window.parent.curCatalogName,
		dsId : null
	};
	//初始化
	$(function() {
		initTab();

		function initTab() {
			var height = $(document).height() - 33;
			$("#tab").append('<div tabid="tab1" title="基本信息" />');
			$("#tab").append('<div tabid="tab2" title="字段设置" />');
			$("#tab").append('<div tabid="tab3" title="补录模板" />');
			$("#tab").append('<div tabid="tab4" title="校验规则" />');
			tabObj = $("#tab").ligerTab({
				contextmenu : false,
				onBeforeSelectTabItem : function() {
					return canSelect;
				}
			});
			
			loadFrame("tab1", "${ctx}/udip/temple/view-tab1/${id}", "tab1frame");
		}

	});

	//下一步
	function next(tabNum,id) {
		savaType = 0;
		var tabid = "tab"+tabNum;
		var src = "${ctx}/udip/temple/view-tab"+tabNum+"/${id}";
		
		var id = "tab"+tabNum+"frame";
		loadFrame(tabid, src, id);
		canSelect = true;
		tabObj.selectTabItem("tab"+tabNum);
		canSelect = false;
	}
	
	//关闭
	function closeDsetBox(){
		BIONE.closeDialog("objDefManage");
	}

	function loadFrame(tabId, src, id) {
		var height = $(document).height() -100;
		if ($('#' + id).attr('src')) {
			return;
		}
		var frame = $('<iframe/>');
		frame.attr({
			id : id,
			frameborder : 0,
			src : src
		}).css("height", height);
		$('div[tabId=' + tabId + ']').append(frame);
	}
	
	function reloadform(){
		tabObj.reload("tab2");
		savaType = 1;
	}
</script>
</head>
<body>
</body>
</html>