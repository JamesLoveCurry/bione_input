<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<%@ include file="/common/taglibs.jsp"%>
<html>
<head>
<meta name="decorator" content="/template/template9_BS.jsp">
<script type="text/javascript">
	
	$(init);

	/* 全局初始化 */
	function init() {
		initTab();		
	}
	
	function initTab() {
		var height = $(document).height() - 33;
		$("#tab").append('<div tabid="tab1" title="数据模型" />');
		$("#tab").append('<div tabid="tab2" title="组合/派生指标" />');
		$("#tab").append('<div tabid="tab3" title="报表" />');
		$("#tab").append('<div tabid="tab4" title="泛化指标" />');
		tabObj = $("#tab").ligerTab({
			contextmenu : false,
			onBeforeSelectTabItem : function() {
				return true;
			},onAfterSelectTabItem: function (tabid){
				var src = "";
				if(tabid == "tab1"){
					src = "${ctx}/rpt/frame/rptenginetsk/module?d="+new Date().getTime();
				}else if(tabid == "tab2"){
					src = "${ctx}/rpt/frame/rptenginetsk/index?tskType=01&d="+new Date().getTime();
				}else if(tabid == "tab3"){
					src = "${ctx}/rpt/frame/rptenginetsk/report?d="+new Date().getTime();
				}else if(tabid == "tab4"){
					src = "${ctx}/rpt/frame/rptenginetsk/index?tskType=03&d="+new Date().getTime();
				}
				loadFrame(tabid, src, tabid + "frame");
			}
		});
		loadFrame("tab1", "${ctx}/rpt/frame/rptenginetsk/module?d="+new Date().getTime(), "tab1frame");
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
	
	function updateAutoTaskStatus(selectedObj){
		$.ajax({
		    cache : false,
		    async : false,
		    url : "${ctx}/rpt/frame/rptenginetsk/update.json",
		    dataType : 'json',
		    data : {
		    	selectedObj : selectedObj
		    },
		    type : "post",
		    beforeSend : function() {
				BIONE.loading = true;
				BIONE.showLoading("正在更新数据中...");
		    },
		    complete : function() {
				BIONE.loading = false;
				BIONE.hideLoading();
		    },
		    success : function(result) {
		    	if(result && result.msg == "ok"){
		    		BIONE.tip("重跑成功！");
		    	}
		    },
		    error : function(result, b) {
				////BIONE.tip('发现系统错误 <BR>错误码：' + result.status);
		    }
		});
	};
	
</script>

</head>
<body>
</body>
</html>