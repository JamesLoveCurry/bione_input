<%@ page language="java" pageEncoding="UTF-8"%>
<%@ include file="/common/taglibs.jsp"%>
<html>
<meta name="decorator" content="/template/template5.jsp">
<head>
<script type="text/javascript">
	var taskDefId='${taskDefId}';
	var flowNm='${flowNm}';
	var mainform;
	$(function() {
		var groupicon = "${ctx}/images/classics/icons/communication.gif";
			mainform = $("#mainform").ligerForm({
			align : "center",
			fields : [ {
		    	name:'taskDefId',
		    	type:'hidden'
		    }, {
				display : "流程名称",
				name : "flowNm",
				newline : false,
				type : "text",
				validate : {
				    required : true,
				    maxlength : 100,
	        		remote :  {
	        			url : "${ctx}/rpt/input/task/testSameFlowNm",
						type : "POST",
						data : {
							"taskDefId":taskDefId
						}
	        		},
					messages : {
						remote:"流程名字重名,请重新输入"
					}
				}
			}]
		});
			$("#mainform [name='flowNm']").val(flowNm);
			$("#mainform [name='taskDefId']").val(taskDefId);
		var buttons = [];
		buttons.push({
			text : '取消',
			onclick : f_close
		});
		buttons.push({
			text : '保存',
			onclick : f_save
		});
		BIONE.addFormButtons(buttons);
	    
		jQuery.metadata.setType("attr", "validate");
		BIONE.validate("#mainform");
	});

	function f_save() {
		var flowNm = $("#mainform [name='flowNm']").val();
		if(!flowNm){//校验名称不能为空
			BIONE.tip('流程名称不能为空!');
			return;
		}
		$.ajax({
			cache : false,
			async : false,
			url :"${ctx}/rpt/input/task/saveFlow.json?d="+ new Date().getTime(),
			dataType : 'json',
			type : "post",
			data : {
				"taskDefId" : taskDefId,
				"flowNm" : flowNm
			},
			success : function(result) {
				if(result=="1"){
					BIONE.tip('保存成功!');
					window.parent.refreshTree();
					f_close() ;
				}else if(result=="2"){
					BIONE.tip('已存在同名流程!');
				}else{
					BIONE.tip(result);
				}
			},
			error : function(result, b) {
				BIONE.tip('发现系统错误 <BR>错误码:' + result.status);
			}
		});
	}
	
	function f_close() {
		BIONE.closeDialog("addFlow");
	}
</script>

<title>指标目录管理</title>
</head>
<body>
<div id="template.center">
	<form name="mainform" method="post" id="mainform" >
	</form>
</div>
</body>
</html>