<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<%@ include file="/common/taglibs.jsp"%>
<html>
<meta name="decorator" content="/template/template9.jsp">
<head>
<title>Insert title here</title>
<script type="text/javascript">
	var indexNo = "${indexNo}";
	var indexVerId = "${indexVerId}";
	var tabObj = null;
	var isLoad = false;
	//初始化
	$(function() {
		initTab();
	});

	function initTab() {
		var height = $(document).height() - 33;
		tabObj = $("#tab").ligerTab({
			contextmenu : false,
			onBeforeSelectTabItem : function (tabid){
				if("influenceIdx" == tabid){
					if ((!$('#influenceIdxFrame').attr('src')) && (!isLoad)) {
						isLoad = true;
						return;
					}
					if(isLoad){
						$('#influenceIdxFrame').attr('src', "${ctx}/report/frame/idx/influenceGrid?influenceType=idx");
						isLoad = false
					}
				}
			}
		});
		tabObj.addTabItem({
			tabid : "influenceIdx",
			text : "直接影响指标",
			showClose : false,
			content : '<iframe id = "influenceIdxFrame" style="height:' + height + 'px;" src=""></iframe>'
		});
		tabObj.addTabItem({
			tabid : "influenceRpt",
			text : "直接影响报表",
			showClose : false,
			content : '<iframe style="height:' + height + 'px;" src="${ctx}/report/frame/idx/influenceGrid?influenceType=rpt"></iframe>'
		});
	}
</script>
</head>
<body>
</body>
</html>