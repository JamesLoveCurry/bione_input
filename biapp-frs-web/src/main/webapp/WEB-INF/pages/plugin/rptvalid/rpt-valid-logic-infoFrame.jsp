<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<%@ include file="/common/taglibs.jsp"%>
<html>
<meta name="decorator" content="/template/template9.jsp">
<head>
<title>Insert title here</title>
<script type="text/javascript">
	var tabObj = null;
	var tab2Object = null;
	var canSelect = false;
	var isLoaded =  true;
	var indexCatalogNo = '${indexCatalogNo}';
	var indexNo = '${indexNo}';
	var indexNm = '${indexNm}';
	var defSrc = '${defSrc}';
	var checkId = '${checkId}';
	
	//数据集缓存对象
	var validObj = {
		firstEdit : true,//首次修改
		refresh : false,//是否刷新
		checkId : "${checkId}",
		checkNm : null,
		isPre : null,
		floatVal : null,
		startDate : null,
		startDate : null,
		busiExplain : null,
		expressionDesc : null,
		logicOperType : null,
		leftExpression : null,
		rightExpression : null,
		leftFormulaIndex : null,
		rightFormulaIndex : null,
		saveData : null
	};
	//初始化
	$(function() {
		initTab();
		function initTab() {
			var height = $(document).height() - 33;
			$("#tab").append('<div tabid="tab1" title="逻辑校验属性" />');
			$("#tab").append('<div tabid="tab2" title="关联维度项" />');
			tabObj = $("#tab").ligerTab({
				contextmenu : false,
				onBeforeSelectTabItem : function(tabId) {
					if(tabId=='tab2'&&isLoaded){
						isLoaded = false;
						var win = $('#tab1frame')[0].contentWindow;
						win.f_cache();
						
					}else if(tabId=='tab1'&&isLoaded){
						isLoaded = false;
						last();
					}
					isLoaded = true;
					return canSelect;
				}
			});
			loadFrame("tab1", "${ctx}/report/frame/rptvalid/logic/newLogic?indexCatalogNo=${indexCatalogNo}&indexNo="
					+indexNo+"&indexNm="+indexNm+"&defSrc=${defSrc}&checkId=${checkId}&d="
					+ new Date().getTime(), "tab1frame");
		}
	});

	//上一步
	function last() {
		canSelect = true;
		tabObj.selectTabItem("tab1");
		canSelect = false;
	}

	//下一步
	function next() {
		//win.callGrid();
		
		var indexNos = validObj.leftFormulaIndex +","+ validObj.rightFormulaIndex;
		loadFrame("tab2", "${ctx}/report/frame/rptvalid/logic/editDim?checkId=${checkId}&indexNos="+indexNos+"&d="
				+ new Date().getTime(), "tab2frame");
		canSelect = true;
		tabObj.selectTabItem("tab2");
		canSelect = false;
	}
	//关闭
	function closeDsetBox(){
	}

	function loadFrame(tabId, src, id) {
		var height = $(document).height() - 33;
		if ($('#' + id).attr('src') && $("#" + id).attr('src') == src) {
			return;
		}
		var frame = $('<iframe/>');
		frame.attr({
			id : id,
			frameborder : 0,
			src : src
		}).css("height", height);
		$('div[tabId=' + tabId + ']').html(frame);
	}
	
</script>
</head>
<body>
</body>
</html>