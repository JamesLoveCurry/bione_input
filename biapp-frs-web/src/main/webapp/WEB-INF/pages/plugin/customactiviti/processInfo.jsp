<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ include file="/common/taglibs.jsp"%>
<%@ include file="/common/jquery_load.jsp"%>
<html>
<head>
	<link rel="stylesheet" href="${ctx}/process-edit/diagram-viewer/style.css" type="text/css" media="screen">
	<link rel="stylesheet" href="${ctx}/css/classics/ligerUI/Aqua/css/ligerui-dialog.css" type="text/css" media="screen">
	
	<script src="${ctx}/process-edit/diagram-viewer/js/jstools.js" type="text/javascript" charset="utf-8"></script>
  	<script src="${ctx}/process-edit/diagram-viewer/js/raphael.js" type="text/javascript" charset="utf-8"></script>
  	
  	<script src="${ctx}/process-edit/diagram-viewer/js/jquery/jquery.js" type="text/javascript" charset="utf-8"></script>
  	<script src="${ctx}/process-edit/diagram-viewer/js/jquery/jquery.progressbar.js" type="text/javascript" charset="utf-8"></script>
  	<script src="${ctx}/process-edit/diagram-viewer/js/jquery/jquery.asyncqueue.js" type="text/javascript" charset="utf-8"></script>
  	
  	<script src="${ctx}/process-edit/diagram-viewer/js/Color.js" type="text/javascript" charset="utf-8"></script>
	<script src="${ctx}/process-edit/diagram-viewer/js/Polyline.js" type="text/javascript" charset="utf-8"></script>
	<script src="${ctx}/process-edit/diagram-viewer/js/ActivityImpl.js" type="text/javascript" charset="utf-8"></script>
	<script src="${ctx}/process-edit/diagram-viewer/js/ActivitiRest.js" type="text/javascript" charset="utf-8"></script>
	<script src="${ctx}/process-edit/diagram-viewer/js/LineBreakMeasurer.js" type="text/javascript" charset="utf-8"></script>
	<script src="${ctx}/process-edit/diagram-viewer/js/ProcessDiagramGenerator.js" type="text/javascript" charset="utf-8"></script>
	<script src="${ctx}/process-edit/diagram-viewer/js/ProcessDiagramCanvas.js" type="text/javascript" charset="utf-8"></script>
	<script src="${ctx}/js/bione/BIONE.js" type="text/javascript" charset="utf-8"></script>
 </head>

<%@ include file="/common/ligerUI_load.jsp"%>

<script language='javascript'>
	var processDefinitionId = "${processDefinitionId}";
	var processInstanceId = "${processInstanceId}";
	var DiagramGenerator = {};
	var pb1;
	var ctx ="${ctx}";
	var grid;
	$(document).ready(function(){
	  pb1 = new $.ProgressBar({
	    	boundingBox: '#pb1',
		    on: {
		      complete: function() {
		        	if (processInstanceId) {
		          		ProcessDiagramGenerator.drawHighLights(processInstanceId);
		          		initGridWidth = $("svg").width();
		          		initGrid(initGridWidth);
		        	}
		      },
		      valueChange: function(e) {
		      }
		    },
		    value: 0
	  });
	  
	  ProcessDiagramGenerator.options = {
	    diagramHolderId: "diagramHolder",
	    diagramInfoId: "diagramInfo",
	    on: {
	      click: function(canvas, element, contextObject){
	        var mouseEvent = this;
	        console.log("[CLICK] mouseEvent: %o, canvas: %o, clicked element: %o, contextObject: %o", mouseEvent, canvas, element, contextObject);
	
	        if (contextObject.getProperty("type") == "callActivity") {
	          var processDefinitonKey = contextObject.getProperty("processDefinitonKey");
	          var processDefinitons = contextObject.getProperty("processDefinitons");
	          var processDefiniton = processDefinitons[0];
	          console.log("Load callActivity '" + processDefiniton.processDefinitionKey + "', contextObject: ", contextObject);
	
	        ProcessDiagramGenerator.drawDiagram(processDefiniton.processDefinitionId);
	        }
	      },
	      rightClick: function(canvas, element, contextObject){
	        	var mouseEvent = this;
	        	console.log("[RIGHTCLICK] mouseEvent: %o, canvas: %o, clicked element: %o, contextObject: %o", mouseEvent, canvas, element, contextObject);
	      },
	      over: function(canvas, element, contextObject){
	        	var mouseEvent = this;
	        	ProcessDiagramGenerator.showActivityInfo(contextObject);
	      },
	      out: function(canvas, element, contextObject){
	       	 	var mouseEvent = this;
	        	ProcessDiagramGenerator.hideInfo();
	      }
	    }
	  };
	  
	  var baseUrl = window.document.location.protocol + "//" + window.document.location.host + "/";
	  var shortenedUrl = window.document.location.href.replace(baseUrl, "");
	  baseUrl = baseUrl + shortenedUrl.substring(0, shortenedUrl.indexOf("/"));
	  
	  ActivitiRest.options = {
		processDefinitionId: processDefinitionId,
		processInstanceId: processInstanceId,
	    processInstanceHighLightsUrl: baseUrl + "/service/process-instance/{processInstanceId}/highlights?callback=?",
	    processDefinitionUrl: baseUrl + "/service/process-definition/{processDefinitionId}/diagram-layout?callback=?",
	    processDefinitionByKeyUrl: baseUrl + "/service/process-definition/{processDefinitionKey}/diagram-layout?callback=?"
	  };
	  
	  if (processDefinitionId) {
	    ProcessDiagramGenerator.drawDiagram(processDefinitionId);
	  } else {
	    alert("processDefinitionId parameter is required");
	  }
	  
	  $("#bottom").click(function() {
		  var main = parent || window;
		  var dialog = main.jQuery.ligerui.get("processInfo");
		  dialog.close();
	  });
	  
		function initGrid(gridWidth) {
			grid = $("#maingrid").ligerGrid(
					{
						width : gridWidth+5,
						title:"流转日志",
						columns : [
								{
									display : '环节名',
									name : 'name',
									align : 'left',
									width : '25%'
								},
								{
									display : '开始时间',
									name : 'startTime',
									align : 'left',
									width : '25%'
								},
								{
									display : '结束时间',
									name : 'endTime',
									align : 'left',
									width : '25%'
								},
								{
									display : '处理人',
									name : 'assignee',
									align : 'left',
									width : '25%'
								}],
						checkbox : false,
						usePager : false,
						isScroll : true,
						rownumbers : true,
						alternatingRow : true,
						colDraggable : false,
						dataAction : 'server',
						method : 'POST',
						url : '${ctx}/activiti/getHistoryInfo?processInstanceId='+processInstanceId
					});
		};
	});
	
</script>

<body>
	<div id="center" >
		<div id="diagramHolder" class="diagramHolder">
		<div class="l-panel-header"><span class="l-panel-header-text">流转信息:</span></div>
		</div>
		<div class="content">
			<div id="maingrid" class="maingrid" style="border:0px"></div>
		</div>
	</div>
</body>
<style>
	
	html {
		overflow-x: auto;
	}
	#maingrid {
		margin-left:0px;
	}
	
</style>
</html>