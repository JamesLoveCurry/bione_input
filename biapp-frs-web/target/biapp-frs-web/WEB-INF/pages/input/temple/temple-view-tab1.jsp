<%@ page language="java" pageEncoding="UTF-8"%>
<%@ include file="/common/taglibs.jsp"%>
<%@ page
	import="org.apache.shiro.web.filter.authc.FormAuthenticationFilter"%>
<html>
<meta name="decorator" content="/template/template5.jsp">
<head>
<script type="text/javascript" src="${ctx}/js/udip/remark/global.js"></script>
<script type="text/javascript" src="${ctx}/js/udip/remark/temple.js"></script>
<script type="text/javascript">

	var groupicon = "${ctx}/images/classics/icons/communication.gif";
	//创建表单结构 
	var mainform;
	$(function() {
		mainform = $("#mainform");
		mainform.ligerForm({
			fields : [ 
			          {
			        	  name : "templeId",
			        	  type : "hidden"
			          },
			{
				display : "模板名称",
				name : "templeName",
				newline : true,
				type : "text",
				group : "模板基础信息",
				groupicon : groupicon,
				options : {
					disabled : true
				}
			},{
				display : "负责人",
				name : "operator",
				newline : true,
				type : "text",
				options : {
					disabled : true
				}
			},{
				display : "所属单位",
				name : "operatorUnit",
				newline : true,
				type : "text",
				options : {
					disabled : true
				}
			},{
				display:"所属目录",
	        	name:'dirName',
	        	newline:true,
	        	type:'text',
	        	options : {
					disabled : true
				}
				
			},{
				display:"模板状态",
	        	name:'stateValue',
	        	newline:true,
	        	type:'text',
	        	options:{
	        		
					disabled : true
	        	}
			},{
				display:"是否审核",
	        	name:'authable',
	        	newline:true,
	        	type:'checkbox',
	        	options : {
	        		disabled : true
	        	}
			},{
				display:"可新增",
	        	name:'addable',
	        	newline:false,
	        	type:'checkbox',
	        	options : {
	        		disabled : true
	        	}
			},{
				display:"可修改",
	        	name:'updatable',
	        	newline:true,
	        	type:'checkbox',
	        	options : {
	        		disabled : true
	        	}
			},{
				display:"可删除",
	        	name:'deletable',
	        	newline:false,
	        	type:'checkbox',
	        	options : {
	        		disabled : true
	        	}
			},/*{
				display:"可否补录历史",
	        	name:'hisable',
	        	newline:true,
	        	type:'checkbox',
	        	options : {
	        		disabled : true
	        	}
			},*/
			{
				display : "模板描述",
				name : "remark",
				newline : true,
				type : "textarea",
				width : 503,
				attr : {
					style : "resize: none;"
				},
				options : {
	        		disabled : true
	        	}
			}]
		});
		if("${id}") {
			$.ajax({
				url : "${ctx}/udip/temple/findTempleInfo?templeId="+"${id}"+"&d="+new Date().getTime(),
				success : function(data) {
					var authable = $("#authable").ligerCheckBox({ disabled: false });
					var addable = $("#addable").ligerCheckBox({ disabled: false });
					var updatable = $("#updatable").ligerCheckBox({ disabled: false });
					var deletable = $("#deletable").ligerCheckBox({ disabled: false });
					//var hisable = $("#hisable").ligerCheckBox({ disabled: false });
					if(data.authable==null){
						authable.setValue(true);
						addable.setValue(true);
						updatable.setValue(true);
						deletable.setValue(true);
					}else{
						if(data.authable=='1'){
							authable.setValue(true);
						}else{
							authable.setValue(false);
						}
						if(data.addable=='1'){
							addable.setValue(true);
						}else{
							addable.setValue(false);
						}
						if(data.updatable=='1'){
							updatable.setValue(true);
						}else{
							updatable.setValue(false);
						}
						if(data.deletable=='1'){
							deletable.setValue(true);
						}else{
							deletable.setValue(false);
						}
						//if(data.hisable=='1'){
						//	hisable.setValue(true);
						//}else{
						//	hisable.setValue(false);
						//}
						$("#mainform [name='templeId']").val(data.templeId);
						$("#mainform [name='templeName']").val(data.templeName);
						$("#mainform [name='remark']").val(data.remark);
						$("#mainform [name='operator']").val(data.operator);
						$("#mainform [name='operatorUnit']").val(data.operatorUnit);
						$("#mainform input[name='state']").val(data.state);
						if(data.state == "1"){
							$("#mainform [name='stateValue']").val("启用");
						}else{
							$("#mainform [name='stateValue']").val("停用");
						}
						$.ajax({
							async : false,
							url : '${ctx}/udip/dir/findDirIdAndName/'+data.dirId,
							success : function(data2) {
								
								$("#mainform input[name='dirName']").val(data2.dirName);
								$("#mainform input[name='dirId']").val(data2.dirId);
							}
						});
					}
					$('#authable').attr('disabled', 'disabled');
					$('#addable').attr('disabled', 'disabled');
					$('#deletable').attr('disabled', 'disabled');
					$('#updatable').attr('disabled', 'disabled');
					//$('#hisable').attr('disabled', 'disabled');
				}
			});	
		}
		jQuery.metadata.setType("attr", "validate");
		BIONE.validate(mainform);
		var buttons = [];
		buttons.push({
			text : '关闭',
			onclick : cancleCallBack
		});
		buttons.push({
			text : '下一步',
			onclick : next
		});
		
		BIONE.addFormButtons(buttons);
	});
	
	function next() {
		BIONE.submitForm($("#mainform"),function(text) {
			parent.next('2','${id}');
		});
		
	}
	function cancleCallBack() {
		parent.closeDsetBox();
	}

</script>
</head>
<body>
<div id="template.center">
		<form id="mainform" action="" method="post"></form>
	</div>
</body>
</html>