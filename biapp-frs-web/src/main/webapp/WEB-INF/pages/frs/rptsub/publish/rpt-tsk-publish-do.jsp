<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ include file="/common/taglibs.jsp"%>
<html>
<head>
<meta name="decorator" content="/template/template5.jsp">
<script type="text/javascript">
//小于当前系统时间
jQuery.validator.addMethod("lessThanNow", function(value, element, params) {
	if (!value) return true;
	if (params) {
		var tdate = new Date();
		var fdate = new Date(tdate.getFullYear(), tdate.getMonth(), tdate.getDate()).valueOf() / (60 * 60 * 24 * 1000);
		if (value.indexOf('-') != -1 && value.length >= 10) {
			tdate = new Date(new Number(value.substr(0, 4)), new Number(value.substr(5, 2)) - 1, new Number(value.substr(8, 2)))
					.valueOf() / (60 * 60 * 24 * 1000);
		} else {
			tdate = new Date(value).valueOf() / (60 * 60 * 24 * 1000);
		}
		return tdate - fdate <= 0 ? true : false;
	}
}, "数据日期必须小于等于当前日期.");
</script>

<script type="text/javascript">
	var grid;
	var taskId="${taskId}";
	var moduleType = window.parent.moduleType;
	var taskInfo = '${taskInfo}'? JSON2.parse('${taskInfo}') : {};
	$(function() {
		//初始化form
		$("#mainform").ligerForm({
			labelWidth : 100,
			inputWidth : 280,
			fields : [ {
				display : "任务名称",
				name : "taskNm",
				newline : true,
				type : "text",
				cssClass : "field",
				group : "基本信息", 
				groupicon : "${ctx}/images/classics/icons/communication.gif"
			},{
				display:"数据日期",
				name:"dataDate",
				type:"date",
				cssClass:"field",
				newline : false,
				validate :{
					required : true
				}
			},{
				display : "下发报表",
				name : "tskobjId",
				newline : true,
				type : "text",
				cssClass : "field"
			}, {
				display : "下发机构",
				name : "exeObjId",
				newline : false,
				type : "text",
				cssClass : "field"
			}]
		
		});
		//改变输入框的样式-----开始--------
		
		var tempLi_l = $("#tskobjId").parent().parent();
		var tipContent_l = [];
		
		var content_l = '<iframe frameborder="0" id="leftframe" name="leftframe" style="height:300px;width:280px;" src="${ctx}/frs/rpttsk/publish/showDoTree?canEdit=false&taskType='+taskInfo.taskInfoVO.taskType+'" ></iframe>';
		tipContent_l.push(content_l);
		tempLi_l.html(tipContent_l.join(''));
		
		var tempLi_r = $("#exeObjId").parent().parent();
		var tipContent_r = [];
		
		var content_r = '<iframe frameborder="0" id="rightframe" name="rightframe" style="height:300px;width:280px;" src="${ctx}/frs/rpttsk/publish/showDoTree?canEdit=false&taskType='+taskInfo.taskInfoVO.taskType+'"></iframe>';
		tipContent_r.push(content_r);
		tempLi_r.html(tipContent_r.join(''));
		
		//改变输入框的样式-----结束--------
		jQuery.metadata.setType("attr", "validate");
		BIONE.validate($("#mainform"));
		//初始化按钮
		var buttons = [];
		buttons.push({
		    text : '取消',
		    onclick : function() {
				BIONE.closeDialog("publishTskWin", null);
		    }
		});
		buttons.push({
		    text : '临时下发',
		    onclick : publishTsk
		});
		BIONE.addFormButtons(buttons);
		//初始化数据
	});
	//初始化值
	var leftInited = false;
	var rightInited = false;
	function initData(){
		var tskInfo = taskInfo.taskInfoVO.taskInfo;
		$("#mainform input[name='taskNm']").val(tskInfo.taskNm).attr("readOnly","true");
		$.ligerui.get("taskNm").updateStyle();
		//初始化报表树、机构树
		if (! leftInited && window.frames.leftframe.initTree) {
			leftInited = true;
			window.frames.leftframe.initTree("rpt",tskInfo.taskType)
		}
		if (! rightInited && window.frames.rightframe.initTree) {
			rightInited = true;
			window.frames.rightframe.initTree("org",tskInfo.taskType)
		}
	}
	
	//保存按钮-任务保存方法
	function publishTsk(){
		//校验
		if (!$("#mainform").valid()) {
			BIONE.tip("校验未通过！");
			return ;
		}
		// 1、 任务基本信息
		var tskInfo = taskInfo.taskInfoVO.taskInfo;
		tskInfo["dataDate"] = splitDataDate();
		// 2、报表-任务对象 
		var tskobjs = [];
		var leftTreeNodes = window.frames.leftframe.getCheckedRptNodes();
		if(leftTreeNodes.length>0){
			for ( var m = 0; m < leftTreeNodes.length; m++) {
				var node_l = leftTreeNodes[m];
				if(node_l.params.type=="rpt"){
					tskobjs.push(node_l.params.realId);
				}
			}
		}else{
			BIONE.tip("请至少选择一个报表");
			return;
		}
		var exeobjs = [];
		var exeUpObjs = [];
		// 3、机构-执行对象
		var rightTreeNodes = window.frames.rightframe.getCheckedOrgNodes();
		if(rightTreeNodes.length>0){
			for ( var n = 0; n < rightTreeNodes.length; n++) {
				var node_r = rightTreeNodes[n];
				if(node_r.params.type=="org"){
					exeobjs.push(node_r.params.realId);
					exeUpObjs.push(node_r.upId);
				}
			}
		}else{
			BIONE.tip("请至少选择一个机构");
			return;
		}
		var tskVO = {
		    	taskInfo : JSON2.stringify(tskInfo),
		    	tskobjs : tskobjs.join(","),
		    	exeobjs : exeobjs.join(","),
		    	exeUpObjs : exeUpObjs.join(",")
		    };
		$.ajax({
   		    async : true,
   		    dataType : "json",
   		    type : "post",
   			url : '${ctx}/frs/rpttsk/publish/rptTempCheck', 
   		    data :tskVO,
   		    beforeSend : function() {
   				BIONE.showLoading("正在下发任务...");
   		    },
   		    success : function(result) {
   		    	if(result.msg){
   		    		BIONE.hideLoading();
   		    		BIONE.tip(result.msg);
   		    	}else{
   		    	// 判定是否已下发
   		 		$.ajax({
   		 		    async : true,
   		 		    dataType : "json",
   		 		    type : "post",
   		 		    url : '${ctx}/frs/rpttsk/publish/isPublish', 
   		 		    data :tskVO,
   		 		    success : function(result) {
   		 		    	if(result.msg){
   		 		    		BIONE.hideLoading();
   		 		    		BIONE.tip(result.msg);
   		 		    	}else{
   		 		    		if(null != result.publishRpts && result.publishRpts.length > 0){
	   		 		    		BIONE.hideLoading();
		 		    			BIONE.tip(result.msg);
		 		    			return ;
		 		    		}else if(null != result.unPublishRpts && result.unPublishRpts.length > 0){
   		 		    			tskVO.tskobjs = result.unPublishRpts.join(",");
   		 		    		}
   		 		    		// 临时下发
   		 		    		$.ajax({
   		 		    		    async : true,
   		 		    		    dataType : "json",
   		 		    		    type : "post",
   		 		    			url : '${ctx}/frs/rpttsk/publish/publishTsk', 
   		 		    		    data :tskVO,
   		 		    		    success : function(result) {
   		 		    		    	if(result.msg){
   		 		    		    		BIONE.tip(result.msg);
   		 		    		    	}else{
   		 		    		    		parent.BIONE.tip('下发成功');
   		 		    		    		BIONE.hideLoading();
   		 		    					BIONE.closeDialog("publishTskWin", null);
   		 		    		    	}
   		 		    		    },
   		 		    			    error : function(result, b) {
   		 		    				BIONE.tip('下发失败');
   		 		    		    }

   		 		    		});
   		 		    	}
   		 				
   		 		    },
   		 			    error : function(result, b) {
   		 			    BIONE.hideLoading();
   		 				BIONE.tip('下发错误 <BR>错误码：' + result.status);
   		 		    }

   		 		});
   		    	}
   				
   		    },
   			    error : function(result, b) {
   			    	BIONE.hideLoading();
   				BIONE.tip('下发错误 <BR>错误码：' + result.status);
   		    }

   		});
	}
	//数据日期 
	function splitDataDate(){
		var val = $("#mainform input[name='dataDate']").val();
		var valArray = val.split("-");
		var dataDate =""; 
		for(var i = 0;i<valArray.length;i++){
			dataDate += valArray[i];
		}
		return dataDate;
	}
	
</script>
<title>临时下发</title>
</head>
<body>
	<div id="template.center">
		<form name="mainform" method="post" id="mainform"
			action=""></form>
	</div>
</body>
</html>