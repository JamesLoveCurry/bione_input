<%@ page language="java" pageEncoding="UTF-8"%>
<%@ include file="/common/taglibs.jsp"%>
<%@ page import="org.apache.shiro.web.filter.authc.FormAuthenticationFilter"%>
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
    var catalogId='${catalogId}';
    var catalogName='${catalogName}';
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
		/* 	$("#tab").append('<div tabid="tab4" title="校验规则" />'); *///去除校验规则配置，统一走校验规则维护功能
			tabObj = $("#tab").ligerTab({
				contextmenu : false,
				onBeforeSelectTabItem : function() {
					return canSelect;
				}
			});
			loadFrame("tab1", "${ctx}/rpt/input/temple/tab1/${id}?catalogId="+catalogId+'&catalogName='+catalogName, "tab1frame");
		}
	});

	//下一步
	function next(tabNum, id) {
		savaType = 0;
		var tabid = "tab" + tabNum;
		var src = "${ctx}/rpt/input/temple/tab" + tabNum + "/${id}";
		var id = "tab" + tabNum + "frame";
		loadFrame(tabid, src, id);
		canSelect = true;
		tabObj.selectTabItem("tab" + tabNum);
		canSelect = false;
	}
	
	//关闭
	function closeDsetBox() {
		BIONE.closeDialog("objDefManage");
		//parent.reloadgrid();
	}

	function loadFrame(tabId, src, id) {
		var height = $(document).height() - 100;
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
	function setTempleInfo_tab1(info) {
		templeId = info.templeId;
		templeName = info.templeName;
		templeInfo = info;
	}
	function setTempleInfo_tab2(rdbId, tableName, orgColumnName, connLower, paramStr){
		templeInfo.dsId = rdbId;
		templeInfo.tableEnName = tableName;
		templeInfo.orgColumn = orgColumnName;
		templeInfo.setTempleInfo_tab2 = connLower;
		templeInfo.paramStr = paramStr;
	}
	function setTempleInfo_tab4() {
		
	}
	function beanWrap(bean, map) {
		var b = {};
		$.each(bean, function(i, n) {
			if (map[i]) {
				b[map[i]] = n;
			} else {
				b[i] = n;
			}
		});
		return b;
	}
	function saveTempleInfo() {
		if (templeInfo.templeName == "" || templeInfo.templeName == null) {
			BIONE.tip("请填写完整相关模板信息。");
			return;
		}
		$.ajax({
			async : true,//新增遮盖，调整同步方式
			url : "${ctx}/rpt/input/temple/saveTempleInfo.json?d=" + new Date(),
			dataType : 'json',
			data : templeInfo,
			type : "post",
            beforeSend : function() {
                BIONE.showLoading("正在保存数据中...");
            },
            complete : function() {
                BIONE.hideLoading();
            },
			success : function() {
				BIONE.tip("保存成功！");
				parent.$.ligerui.get("maingrid").setOptions({ parms: []  }); 
				parent.$.ligerui.get("maingrid").loadData();
			},
			error : function(result, b) {
				BIONE.tip('发现系统错误 <BR>错误码：' + result.status);
			}
		});
	}
	function reloadform() {
		tabObj.reload("tab2");
		savaType = 1;
	}
</script>
</head>
<body>
</body>
</html>