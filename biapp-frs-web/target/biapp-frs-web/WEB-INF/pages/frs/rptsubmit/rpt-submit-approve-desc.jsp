<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ include file="/common/taglibs.jsp"%>
<html>
<meta name="decorator" content="/template/template5.jsp">
<head>
<script type="text/javascript">
	var groupicon = "${ctx}/images/classics/icons/communication.gif";
	//创建表单结构 
	var mainform;
	var downType = window.parent.p_downType;
	var backNode = window.parent.p_backNode;
	var taskInsIds = window.parent.p_taskInsIds;
	var	moduleType = window.parent.p_moduleType;
	var	operType = window.parent.p_operType;

	$(function() {
		mainform = $("#mainform");
		//if (backNode == "true") {  //20190726 修正boolean判断
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
				{ display : "驳回原因", name : "operContent", newline : true, type : "textarea", validate : { maxlength : 500}},
				{ name : "taskInsIds", type : "hidden"}]
			});
		} else {
			mainform.ligerForm({
				fields : [
				{ display : "驳回原因", name : "operContent", newline : true, type : "textarea", validate : { maxlength : 500}},
				{ name : "taskInsIds", type : "hidden"}]
			});
		}
		
		if(taskInsIds){
			var taskInsIdses=[];
			if(window.parent.resultList!=null){
				for(var i in window.parent.resultList){
					taskInsIdses.push(window.parent.resultList[i].taskInsIds);
				}
			}
			if(taskInsIdses.length>0){
				taskInsIds+=","+taskInsIdses.join(",");
			}
			$("#taskInsIds").val(taskInsIds);
			$("textarea").attr("style", "width: 350px;height: 200px;");
		}
		jQuery.metadata.setType("attr", "validate");
		BIONE.validate(mainform);
	
		var buttons = [];
		buttons.push({ text : '取消', onclick : cancle});
		buttons.push({ text : '驳回', onclick : save});
		BIONE.addFormButtons(buttons);
	});
	function save() {
		if($("#mainform").valid()){
			$.ajax({
				cache : false,
				async : true,
				url : "${ctx}/frs/rptsubmit/submit/returnTaskBatchNew",
				type : "post",
				data : {
					moduleType:moduleType,
					operType:operType,
					taskInsIds:taskInsIds,
					operContent:$("#operContent").val(),
					backNode:$("#backNode").val()
				},
				beforeSend : function(){
					BIONE.loading = true;
					BIONE.showLoading("正在驳回中...");
				},
				success : function(result) {
					BIONE.loading = false;
					BIONE.hideLoading();
					if(downType=="01"){
						if(window.parent.closeDialog){
							BIONE.tip("驳回成功");
					    	window.parent.closeDialog();
					    }
						window.parent.BIONE.closeDialogAndReloadParent("approveRejWin", "maingrid", "驳回成功");
					    
					}else{
						if(window.parent.closeDialog){
							BIONE.tip("驳回成功");
					    	window.parent.closeDialog();
					    }
						window.BIONE.closeDialogAndReloadParent("approveRejWin", "maingrid", "驳回成功");
						
					}
				},
				error : function(result, b) {
					window.parent.parent.document.findIframeByTitle("报表复核")[0].contentWindow.grid.loadData();
					if(downType=="01"){
						window.parent.BIONE.closeDialogAndReloadParent("approveRejWin", "maingrid", "驳回失败");
					}else{
						window.BIONE.closeDialogAndReloadParent("approveRejWin", "maingrid", "驳回失败");
					}
					
				}
			});
		}
	}
	function cancle() {
		BIONE.closeDialog("approveRejWin");
	}
</script>
</head>
<body>
	 <div id="template.center">
		 <form id="mainform" action="${ctx}/frs/rptsubmit/submit/returnTaskBatchNew" method="post">
		 </form> 
	</div> 
</body>
</html>