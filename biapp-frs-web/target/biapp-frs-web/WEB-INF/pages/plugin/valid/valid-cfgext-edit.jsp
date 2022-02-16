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
	var verStartDate;
	var isQuery = "${isQuery}";
	var busiType = "${busiType}";
	
	//数据集缓存对象
	var validObj = {
		firstEdit : true,//首次修改
		refresh : false,//是否刷新
		templateId : "${templateId}",
		checkId : "${checkId}",
		isPre : null,
		floatVal : null,
		startDate : null,
		endDate : null,
		busiExplain : null,
		expressionDesc : null,
		logicOperType : null,
		leftExpression : null,
		rightExpression : null,
		leftFormulaIndex : null,
		rigthFormulaIndex : null,
		isOrgFilter : null,
		orgLevel : null,
		checkSrc : null,
		checkType : null,
		isInnerCheck : true,//是否表内校验
		dataUnit : null,//差值单位
		serialNumber : null,//序号
		dataPrecision : null,//精度
		busiType : "${busiType}",
		orgNo : null,
		orgNm : null,
		logicCheckCycle : null
	};
	//初始化
	$(function() {
		initTab();

		function initTab() {
			var height = $(document).height() - 33;
			$("#tab").append('<div tabid="tab1" title="公式维护" />');
			$("#tab").append('<div tabid="tab2" title="基本信息" />');
			tabObj = $("#tab").ligerTab({
				contextmenu : false,
				onBeforeSelectTabItem : function() {
					return canSelect;
				}
			});
			loadFrame("tab1", "${ctx}/report/frame/valid/logic/newLogic?templateId=${templateId}&checkId=${checkId}&rptId=${rptId}&orgType=${busiType}", "tab1frame");
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
		loadFrame("tab2", "${ctx}/report/frame/valid/logic/basicInfo", "tab2frame");
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