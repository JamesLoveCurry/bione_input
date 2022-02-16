<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ include file="/common/taglibs.jsp"%>

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

<style>
</style>
<script language='javascript'>
	var processDefinitionId = "${processDefinitionId}";
	var processInstanceId = "${processInstanceId}";
	var DiagramGenerator = {};
	var pb1;
	var ctx ="${ctx}";
	$(document).ready(function(){
	  
	  pb1 = new $.ProgressBar({
	    	boundingBox: '#pb1',
	    	label: 'Progressbar!',
		    on: {
		      complete: function() {
		        	this.set('label', 'complete!');
		        	if (processInstanceId) {
		          		ProcessDiagramGenerator.drawHighLights(processInstanceId);
		        	}
		      },
		      valueChange: function(e) {
		        	this.set('label', e.newVal + '%');
		      }
		    },
		    value: 0
	  });
	  
	  ProcessDiagramGenerator.options = {
	    diagramBreadCrumbsId: "diagramBreadCrumbs",
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
	});


</script>

<body>
	<div class="wrapper">
		<div id="pb1"></div>
		<div id="overlayBox" >
		    <div id="diagramBreadCrumbs" class="diagramBreadCrumbs" onmousedown="return false" onselectstart="return false"></div>
		    <div id="diagramHolder" class="diagramHolder"></div>
		    <div class="diagram-info" id="diagramInfo"></div>
		</div>
	</div>
	<div id="bottom" style = "height:9%">
		<div class="form-bar">
			<div class = "form-bar-inner" style="padding-top: 5px">
				<div class = "l-dialog-btn">
				     <div class = "l-dialog-btn-l"></div>
				     <div class = "l-dialog-btn-r"></div>
				     <div class = "l-dialog-btn-inner">关闭</div>
				</div>
			</div>
		</div>
	</div>
</body>
</html>