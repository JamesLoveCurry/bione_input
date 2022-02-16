<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<%@ include file="/common/taglibs.jsp"%>
<html>
<meta name="decorator" content="/template/template14.jsp">
<head>
<script type="text/javascript">
	var reportManage=window.parent;
	var dimFlag = true;
	var dataitemFlag = true;
	var moduleFlag=true;
	var showFlag=true;
	var reportDataItem=null;
	var reportDataDim=null;
	var reportShow=null;
	var reportModule=null;
	function initTab() {
		$("#tab").ligerTab({
				changeHeightOnResize : true,
				contextmenu : false,
				onAfterSelectTabItem : function(tabId) {
					if(tabId == 'show'){
						if(!showFlag){
							content = "<iframe frameborder='0' id='editShow' name='editShow' style='height:100%;width:100%;' src='${ctx}/frs/integratedquery/rptmesquery/info/reportShow?rptId=${rptId}'></iframe>";
							$("#showFrame").html(content);
							showFlag = true;
						}
					}
				/* 	if (tabId == 'dim') {
						if (!dimFlag) {
							content = "<iframe frameborder='0' id='editDim' name='editDim' style='height:100%;width:100%;' src='${ctx}/rpt/frame/rptmgr/info/reportDim?rptId=${rptId}'></iframe>";
							$("#dimFrame").html(content);
							dimFlag = true;
						}
					} */
					/* if (tabId == 'dataitem') {
						if (!dataitemFlag) {
							content = "<iframe frameborder='0' id='editDatatime' name='editDatatime' style='height:100%;width:100%;' src='${ctx}/rpt/frame/rptmgr/info/reportDatatime?rptId=${rptId}'></iframe>";
							$("#dataitemFrame").html(content);
							dataitemFlag = true;
						}
					} */
					if(tabId == 'module'){
						if(!moduleFlag){
							content = "<iframe frameborder='0' id='editModule' name='editModule' style='height:100%;width:100%;' src='${ctx}/report/frame/rptidx/rptIdxDsRelController.mo?doFlag=edit&rptId=${rptId}'></iframe>";
							$("#moduleFrame").html(content);
							moduleFlag = true;
						}
					}
				}
		});
		tabObj = $("#tab").ligerGetTabManager();
		addTabItem("show","展现信息","showFrame",'showFlag');
		//addTabItem("dim", "维度信息", "dimFrame", 'dimFlag');
		//addTabItem("dataitem", "数据项信息", "dataitemFrame", 'dataitemFlag');
		addTabItem("module", "数据模型信息", "moduleFrame", 'moduleFlag');

	}

	$(function() {
		window.parent.reportManage.reportData=window;
		initTab();
		tabObj.selectTabItem("show");
	})
	function addTabItem(tabId, tabText, frameId, flag) {
		var $centerDom = $(document);
		framCenter = $centerDom.height() - 75;
		tabObj.addTabItem({//添加标签tab页
			tabid : tabId,
			text : tabText,
			showClose : false,
			content : "<div id='" + frameId + "' style='height:" + framCenter
					+ "px;width:100%;'></div>"
		});
		this[flag] = false;
	}
	function getShowData(){
		return reportShow.getData();
	}
	function getDimData(){
		if(reportDataDim!=null){
			return reportDataDim.getData();
		}
		else{
			return reportManage.reportInfo.reportDimTypes;
		}
	}
	function getDataitemData(){
		if(reportDataItem!=null){
			return reportDataItem.getData();
		}
		else{
			return reportManage.reportInfo.reportDataItems;
		}
	}
	
	function getModuleData(){
		if(reportModule!=null){
			return reportModule.getData();
		}
		else{
			return reportManage.reportInfo.reportModuleInfo;
		}
	}
</script>
</head>
<body>
	<div id="template.center">
		<div id="tab" style="width: 100%; overflow: hidden;"></div>
	</div>
</body>
</html>