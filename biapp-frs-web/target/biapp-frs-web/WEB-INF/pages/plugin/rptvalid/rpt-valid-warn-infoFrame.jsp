<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<%@ include file="/common/taglibs.jsp"%>
<html>
<meta name="decorator" content="/template/template9.jsp">
<head>
<title>Insert title here</title>
<script type="text/javascript">
	var tabObj = null;
	var canSelect = false;
	var isLoaded =  false;
	var isSeleced =  true;
	var indexNo = '${indexNo}';
	var indexNm = '${indexNm}';
	var checkId = '${checkId}';
	var editFlag = '${editFlag}';
	var indexCatalogNo = '${indexCatalogNo}';
	
	//数据集缓存对象
	var datasetObj = {
		checkId : checkId,
		indexNo : indexNo,
		saveData : null
	};
	//初始化
	$(function() {
		initTab();

		function initTab() {
			var height = $(document).height() - 15;
			$("#tab").append('<div tabid="tab1" title="警示校验属性" />');
			$("#tab").append('<div tabid="tab2" title="关联维度项" style="height: 100%; overflow: auto;"/>');
			tabObj = $("#tab").ligerTab({
// 				contextmenu : false,
				onBeforeSelectTabItem : function(id) {
					if(id=='tab2'&&isLoaded){
						window.frames["tab1frame"].f_save();
					}
					if(id=='tab1'&&isSeleced){
						isSeleced = false;
						last();
					}
					if(id=='tab2'&&isSeleced){
						isSeleced = false;
						var win = $('#tab1frame')[0].contentWindow;
						win.f_cache();
					}
					isSeleced=true
					return canSelect;
				}
			});
			loadFrame("tab1", "${ctx}/report/frame/rptvalid/warn/new?indexNo=" + indexNo 
					+"&indexNm=" + indexNm+"&indexCatalogNo=" + indexCatalogNo  
					+"&checkId=" + checkId
					+"&editFlag=" + editFlag, "tab1frame");
		}

	});

	//上一步
	function last() {
		canSelect = true;
		tabObj.selectTabItem("tab1");
		canSelect = false;
	}

	//下一步
	function next(editFlag) {
		loadFrame("tab2", "${ctx}/report/frame/rptvalid/warn/editDim?checkId=" + datasetObj.checkId 
				+"&indexNo="+datasetObj.indexNo
				+"&editFlag="+editFlag, "tab2frame");
		canSelect = true;
		tabObj.selectTabItem("tab2");
		canSelect = false;
	}
	
	//关闭
	function closeDsetBox(editFlag){
		if(editFlag == '1'){
			setTimeout('BIONE.closeDialog("warnAdd");',0);//防止在chrome下的页面崩溃
		}
		if(editFlag == '2'){
			setTimeout('BIONE.closeDialog("warnEdit");',0);//防止在chrome下的页面崩溃
		}
	}

	function loadFrame(tabId, src, id) {
		var height = $(document).height()-30;
		if ($('#' + id).attr('src')) {
			return;
		}
		var frame = $('<iframe/>');
		frame.attr({
			id : id,
			name : id,
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