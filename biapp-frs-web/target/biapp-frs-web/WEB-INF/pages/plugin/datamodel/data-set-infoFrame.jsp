<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<%@ include file="/common/taglibs.jsp"%>
<html>
<meta name="decorator" content="/template/template9.jsp">
<head>
<title>Insert title here</title>
<script type="text/javascript">
	var tabObj = null;
	var colGrid = null;
	var canSelect = false;
	var isLoaded =  false;
	var measureNoArr  =[];
	var show = "${show}";
	//数据集缓存对象
	var datasetObj = {
		firstEdit : true,//首次修改
		refresh : false,//是否刷新
		from : null,//数据来源
		isAdd : true, //是否新增
		table : "",
		datasetId : "${datasetId}",
		catalogId : "${catalogId}",
		// 20150506 修改 by lujs
		catalogName : "${catalogName}",
		setId : null,
		dsId : null,
		setType : null,
		srcSysCode : null
	};
	//初始化
	$(function() {
		initTab();
		function initTab() {
			var height = $(document).height() - 33;
			$("#tab").append('<div tabid="tab1" title="基本信息" />');
			$("#tab").append('<div tabid="tab2" title="数据项维护" />');
			tabObj = $("#tab").ligerTab({
				contextmenu : false,
				onBeforeSelectTabItem : function(id) {
					if(id=='tab2'&&isLoaded){
						window.frames["tab1frame"].f_save();
					}
					return canSelect;
				}
			});
			loadFrame("tab1", "${ctx}/rpt/frame/dataset/info?show=" + show, "tab1frame");
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
		if (!colGrid) {
			loadFrame("tab2", "${ctx}/rpt/frame/dataset/cols?show=" + show, "tab2frame");
		} else {
			colGrid.set('parms', {
				isAdd : datasetObj.isAdd,
				from : datasetObj.from,
				datasetId : datasetObj.datasetId,
				dsId : datasetObj.dsId,
				table : datasetObj.table,
				d : new Date().getTime(),
				setType : datasetObj.setType,
				srcSysCode : datasetObj.srcSysCode
			});
			if (datasetObj.refresh) {
				colGrid.loadData();
			}
// 			canAddOrDel(false);
		}
// 		canSelect = true;
// 		tabObj.selectTabItem("tab2");
// 		canSelect = false;
	}
	
	//关闭
	function closeDsetBox(){
		setTimeout('BIONE.closeDialog("datasetBox");',0);//防止在chrome下的页面崩溃
	}

	function loadFrame(tabId, src, id) {
		var height = $(document).height() - 33;
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