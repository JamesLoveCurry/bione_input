<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<%@ include file="/common/taglibs.jsp"%>
<html>
<head>
<meta name="decorator" content="/template/template9_BS.jsp">
<script type="text/javascript">
	
	$(init);
	var datasetObj = {
			firstEdit : true,//首次修改
			refresh : false,//是否刷新
			from : null,//数据来源
			table : ""
		};

	/* 全局初始化 */
	function init() {
		initTab();		
	}
	
	function initTab() {
		$("#tab").append('<div tabid="tab2" title="我的消息" />');
		tabObj = $("#tab").ligerTab({
			contextmenu : false,
			onBeforeSelectTabItem : function() {
				return true;
			},onAfterSelectTabItem: function (tabid){
			}
		});
		loadFrame("tab2", "${ctx}/bione/msgNoticeLog?d="+new Date().getTime(), "tab2frame");
	}

	function initSearchForm() {
	}
	
	function initGrid() {
		
	}
	
	function loadFrame(tabId, src, id) {
		var height = $(document).height() - 33;
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
</script>

</head>
<body>
</body>
</html>