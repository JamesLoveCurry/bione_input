<%@ page language="java" pageEncoding="UTF-8"%>
<%@ include file="/common/taglibs.jsp"%>
<%@ page
	import="org.apache.shiro.web.filter.authc.FormAuthenticationFilter"%>
<html>
<meta name="decorator" content="/template/template5.jsp">
<head>
<script type="text/javascript">

	var groupicon = "${ctx}/images/classics/icons/communication.gif";
	//创建表单结构 
	var mainform;
	$(function() {
		mainform = $("#mainform");
		mainform.ligerForm({
			fields : [{
				display : "数据状态",
				name : "dataStateInfo",
	        	newline:true,
	        	type : 'text',
	        	/* type:'select',
	        	options:{
	        		valueFieldID:'operateType'
	        	}, */ 
				validate:{
					required:true
				}
			},{
				display:"说明",
	        	name:'remark',
				newline : true,
				type : "textarea",
				width : 503,
				attr : {
					style : "resize: none;"
				},
				validate : {
					required:true,
					maxlength : 500
				}
			}]
		});
		$.ajax({
			async : false,
			url : "${ctx}/udip/taskcase/findAuthRecordInfo.json",
			dataType : 'json',
			data : {
				"templeId" :"${templeId}",
				"caseId" : "${caseId}"
			},
			type : "get",
			success : function(data) {
				if(data!=null){
					$("#mainform [name='remark']").val(data.remark);
					if (data.dataState == 'dispatch') {
						$("#mainform [name='dataStateInfo']").val("已下发") ;
					} else if (data.dataState == 'save') {
						$("#mainform [name='dataStateInfo']").val("已保存") ;
					} else if (data.dataState == 'validate') {
						$("#mainform [name='dataStateInfo']").val("已校验") ;
					} else if (data.dataState == 'submit') {
						$("#mainform [name='dataStateInfo']").val("已提交") ;
					} else if (data.dataState == 'sucess') {
						$("#mainform [name='dataStateInfo']").val("审核通过") ;
					} else if (data.dataState == 'refuse') {
						$("#mainform [name='dataStateInfo']").val("已回退") ;
					} 
				}
				
			}
		});	
	})
</script>
</head>
<body>
<div id="template.center">
		<form id="mainform"></form>
	</div>
</body>
</html>