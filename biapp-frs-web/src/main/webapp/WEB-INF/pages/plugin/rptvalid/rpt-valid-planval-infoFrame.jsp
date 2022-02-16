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
	var isSeleced = true;
	var indexNo = '${indexNo}';
	var indexNm = '${indexNm}';
	var indexVerId = '${indexVerId}';
	var dataDate = '${dataDate}';
	var orgNo = '${orgNo}';
	var currency = '${currency}';
	var currencyId = '${currencyId}';
	var indexVal = '${indexVal}';
	var editFlag = '${editFlag}';
	var indexCatalogNo = '${indexCatalogNo}';
	
	//数据集缓存对象
	var datasetObj = {
		indexNo : indexNo,
		dataDate : dataDate,
		orgNo : orgNo,
		currency : currency,
		currencyId : currencyId,
		indexVal : indexVal,
		saveData : null
	};
	//初始化
	$(function() {
		initTab();
		function initTab() {
			var height = $(document).height() - 15;
			$("#tab").append('<div tabid="tab1" title="计划值校验属性" />');
			$("#tab").append('<div tabid="tab2" title="关联维度项" />');
			tabObj = $("#tab").ligerTab({
				contextmenu : false,
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
			if(editFlag == '1'){
				loadFrame("tab1", "${ctx}/rpt/frame/idx/planval/idxInfoPlanvalAdd?indexNo=" + indexNo 
						+"&indexNm=" + indexNm+"&indexCatalogNo=" + indexCatalogNo  
						+"&editFlag=" + editFlag, "tab1frame");
			}
			if(editFlag == '2'){
				loadFrame("tab1", "${ctx}/rpt/frame/idx/planval/edit?indexNo=" + indexNo +
						"&dataDate=" + dataDate  +"&currency=" + currency +
						"&orgNo=" + orgNo  + "&indexVal=" + indexVal + "&editFlag=" + editFlag, "tab1frame");
			}	
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
		loadFrame("tab2", "${ctx}/rpt/frame/idx/planval/editDim?indexNo=" + datasetObj.indexNo +
				"&dataDate=" + datasetObj.dataDate  +"&currency=" + datasetObj.currencyId+
				"&orgNo=" + datasetObj.orgNo + "&editFlag="+editFlag, "tab2frame");
		canSelect = true;
		tabObj.selectTabItem("tab2");
		canSelect = false;
	}
	
	//关闭
	function closeDsetBox(editFlag){
		if(editFlag == '1'){
			setTimeout('BIONE.closeDialog("rptIdxPlanvalAddWin");',0);//防止在chrome下的页面崩溃
		}
		if(editFlag == '2'){
			setTimeout('BIONE.closeDialog("rptIdxPlanvalModifyWin");',0);//防止在chrome下的页面崩溃
		}
		window.parent.reloadGrid();
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