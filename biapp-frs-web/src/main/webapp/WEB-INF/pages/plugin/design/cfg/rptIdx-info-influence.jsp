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
	var isRpt = "${isRpt}";
	var isLogic = "${isLogic}";
	var isWarn = "${isWarn}";
	var idxStartDate = "${idxStartDate}";
	var idxEndDate = "${idxEndDate}";
	var currCellValue = "${currCellValue}";
	var tabObj = null;
	var isLoadWarn = false;
	var isLoadLogic = false;
	//初始化
	$(function() {
		initTab();
	});

	function initTab() {
		var height = $(document).height() - 33;
		tabObj = $("#tab").ligerTab({
			contextmenu : false,
			onBeforeSelectTabItem : function (tabid){
				if("influenceLogic" == tabid){
					if(('yes' != isRpt) && ('yes' != isWarn)){
						isLoadLogic = true;
					}
					if (!$('#influenceLogicFrame').attr('src') && (isLoadLogic)) {
						$('#influenceLogicFrame').attr('src', "${ctx}/report/frame/design/cfg/influenceGrid?influenceType=logic");
					}else{
						isLoadLogic = true;
					}
				}
				if("influenceWarn" == tabid){
					if('yes' != isRpt){
						isLoadWarn = true;
					}
					if (!$('#influenceWarnFrame').attr('src') && (isLoadWarn)) {
						$('#influenceWarnFrame').attr('src', "${ctx}/report/frame/design/cfg/influenceGrid?influenceType=warn");
					}else{
						isLoadWarn = true;
					}
				}
				if("influenceIRpt" == tabid){
					if (!$('#influenceIRptFrame').attr('src')) {
						$('#influenceIRptFrame').attr('src', "${ctx}/report/frame/design/cfg/influenceGrid?influenceType=rpt");
					}
				}
			}
		});
		if('yes' == isLogic){
			tabObj.addTabItem({
				tabid : "influenceLogic",
				text : "逻辑校验",
				showClose : false,
				content : '<iframe id = "influenceLogicFrame" style="height:' + height + 'px;" src=""></iframe>'
			});
		}
		if('yes' == isWarn){
			tabObj.addTabItem({
				tabid : "influenceWarn",
				text : "预警校验",
				showClose : false,
				content : '<iframe id = "influenceWarnFrame" style="height:' + height + 'px;" src=""></iframe>'
			});
		}
		if('yes' == isRpt){
			tabObj.addTabItem({
				tabid : "influenceIRpt",
				text : "影响报表",
				showClose : false,
				content : '<iframe id = "influenceIRptFrame" style="height:' + height + 'px;" src=""></iframe>'
			});
		}
	}
</script>
</head>
<body>
</body>
</html>