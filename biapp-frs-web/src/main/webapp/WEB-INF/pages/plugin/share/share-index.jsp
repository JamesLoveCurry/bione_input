<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<%@ include file="/common/taglibs.jsp"%>
<html>
<head>
<meta name="decorator" content="/template/template9.jsp">
<script type="text/javascript">
	
	$(init);

	/* 全局初始化 */
	function init() {
		initTab();		
	}
	
	function initTab() {
		var height = $(document).height() - 33;
		$("#tab").append('<div tabid="tab1" title="我的分享" />');
		$("#tab").append('<div tabid="tab2" title="分享给我" />');
		tabObj = $("#tab").ligerTab({
			contextmenu : false,
			onBeforeSelectTabItem : function() {
				return true;
			},onAfterSelectTabItem: function (tabid){
				var src = "";
				if(tabid == "tab2"){
					src = "${ctx}/rpt/frame/rptmgr/share/otherinfo?d="+new Date().getTime();
				}else{
					src = "${ctx}/rpt/frame/rptmgr/share/mineinfo?d="+new Date().getTime();
				}
				loadFrame(tabid, src, tabid + "frame");
			}
		});
		loadFrame("tab1", "${ctx}/rpt/frame/rptmgr/share/mineinfo?d="+new Date().getTime(), "tab1frame");
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