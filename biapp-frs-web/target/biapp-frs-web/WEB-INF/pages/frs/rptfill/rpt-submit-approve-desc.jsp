<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ include file="/common/taglibs.jsp"%>
<html>
<meta name="decorator" content="/template/template5.jsp">
<head>
<script type="text/javascript">
	var groupicon = "${ctx}/images/classics/icons/communication.gif";
	//创建表单结构 
	var mainform;
	var strs ="${strs}"
	var downType = "${operType}";
	var applyUser = "${applyUser}";
	var taskId = "${taskId}";
	var backNode = "${backNode}" == ""?window.parent.backNode : "${backNode}";
	$(function() {
		$("#strs").attr("value", strs);
		mainform = $("#mainform");
		if (backNode == true) {
			mainform.ligerForm({
				fields : [ {
					display : "打回节点", 
					name : "backNode", 
					newline : false, 
					type : "select", 
					comboboxName : 'backID',
					options : {
						data : [ {
							id : "begin",
							text : "打回开始节点重新审批"
						}, {
							id : "last",
							text : "打回上一级"
						} ]
					} ,
					validate:{
						required:true
					}
				}, 
				{ display : "驳回原因", name : "operContent", newline : false, type : "textarea", validate : { maxlength : 500}},
				{ name : "taskInsIds", type : "hidden"}]
			});
		} else {
			mainform.ligerForm({
				fields : [
					{ display : "驳回原因", name : "operContent", newline : false, type : "textarea", validate : { maxlength : 500}},
					{ name : "taskInsIds", type : "hidden"}]
				});
		}
		
		if("${taskInsIds}"){
			var taskInsIdses=[];
			if(window.parent.resultList!=null){
				for(var i in window.parent.resultList){
					taskInsIdses.push(window.parent.resultList[i].taskInsIds);
				}
			}
			var taskInsIds="${taskInsIds}";
			if(taskInsIdses.length>0){
				taskInsIds+=","+taskInsIdses.join(",");
			}
			$("#taskInsIds").val(taskInsIds);
			$("textarea").attr("style", "width: 470px;height: 200px;resize: none;");
			
		}
		jQuery.metadata.setType("attr", "validate");
		BIONE.validate(mainform);
	
		var buttons = [];
		buttons.push({ text : '取消', onclick : cancle});
		buttons.push({ text : '驳回', onclick : save});
		BIONE.addFormButtons(buttons);
	});
	function save() {
		$.ajax({
			cache : false,
			async : true,
			url : "${ctx}/frs/rptsubmit/submit/returnTaskBatchNew?moduleType=${moduleType}&operType=${operType}&taskInsIds=${taskInsIds}&applyUser="+applyUser+"&taskId="+taskId+"&d="+new Date(),
			type : "post",
			data : {operContent:$("#operContent").val(),
					backNode:$("#backNode").val(),
					strs:'${strs}'
				   },
			success : function(result) {
				if(downType=="01"){
					window.parent.parent.frames.findIframeByTitle("报表复核")[0].contentWindow.grid.loadData();
					if(window.parent.closeDialog){
						BIONE.tip("驳回成功");
				    	window.parent.closeDialog();
				    }
					window.parent.BIONE.closeDialogAndReloadParent("approveRejWin", "maingrid", "驳回成功");
				    
				}else{
					window.parent.parent.frames.findIframeByTitle("报表审核")[0].contentWindow.grid.loadData();
					if(window.parent.closeDialog){
						BIONE.tip("驳回成功");
				    	window.parent.closeDialog();
				    }
					window.BIONE.closeDialogAndReloadParent("approveRejWin", "maingrid", "驳回成功");
					
				}
			},
			error : function(result, b) {
				if(downType=="01"){
					window.parent.parent.frames.findIframeByTitle("报表复核")[0].contentWindow.grid.loadData();
					window.parent.BIONE.closeDialogAndReloadParent("approveRejWin", "maingrid", "驳回失败");
				}else{
					window.parent.parent.frames.findIframeByTitle("报表审核")[0].contentWindow.grid.loadData();
					window.BIONE.closeDialogAndReloadParent("approveRejWin", "maingrid", "驳回失败");
				}
				
			}
		});
	}
	function cancle() {
		BIONE.closeDialog("approveRejWin");
	}
</script>
</head>
<body>
	 <div id="template.center">
		 <form id="mainform" action="${ctx}/frs/rptsubmit/submit/returnTaskBatchNew" method="post">
			<input type="hidden" id="strs" name="strs">
		 </form> 
	</div> 
</body>
</html>