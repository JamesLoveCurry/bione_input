<%@ page language="java" pageEncoding="UTF-8"%>
<%@ include file="/common/taglibs.jsp"%>
<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN">
<html>
<head>
<meta name="decorator" content="/template/template5.jsp">
<script type="text/javascript">
	var groupicon = "${ctx}/images/classics/icons/communication.gif", mainform;
	$(function(){
		initForm();
		initButton();
	});	
	
	function initForm() {
		mainform = $("#mainform").ligerForm({
			fields:[{
				name:"posiId",
				type:'hidden'
			},{
				name:"mstrId",
				type:"hidden"
			},{
				name:'orgNo',
				type:'hidden'
			},{
				display:"岗位编码<font color='red'>*</font>",
	        	name:'posiNo',
	        	newline:true, 
	        	type:'text',
	        	group: "岗位信息",
	        	validate:{
	        		required:true,
	        		maxlength:32
	        	},
				groupicon: groupicon 
			},{
				display:"岗位名称<font color='red'>*</font>",
				name:'posiName',
				newline:false,
				type:'text',
				validate:{
					required:true,
					maxlength:100
				}
			},{
				display:'岗位状态',
	        	name:'posiSts',
	        	newline:true,
	        	type:'select',
	        	options:{
	        		data:[{
	        			text:'启用',
	        			id:'1'
	        		},{
	        			text:'停用',
	        			id:'0'
	        		}]
	        	},
				validate:{
					required:true
				}
// 			},{
// 				display:'岗位类型',
// 				name:'posiTypeValues',
// 				type:"select",
// 				newline:false,
// 				options:{
// 					valueFieldID:"posiType",
// 					data:[{
// 						text:'报表岗位',
// 						id:'01'
// 					},{
// 						text:'补录岗位',
// 						id:'02'
// 					},{
// 						text:'普通岗位',
// 						id:'03'
// 					}]
// 				},
// 				validate:{
// 					required:true
// 				}
				
			},{
	            display:'备&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;注',
	            name:'remark',
	            newline:true,
	            width:493,
	            validate:{
	            	maxlength:500
	            },
	            type:'textarea',
	    		attr :
	    		{
	    			style:"resize :none"
	    		}
	        }]
		});
		if("${id}") {
			BIONE.loadForm(mainform, { url : "${ctx}/bione/admin/posi/showInfo.json?id=${id}"});
		}
		var managers = $.ligerui.find($.ligerui.controls.Input);
	    for (var i = 0, l = managers.length; i < l; i++)
	    {
	        //改变了表单的值，需要调用这个方法来更新ligerui样式
	        managers[i].updateStyle();
	    }		
	}
	
	function initButton() {
		var buttons = [];
		if("${isBasicDept}") {
			
			$("#mainform input[name]").attr("readonly", "readonly").css("color", "black");
			$("#mainform textarea[name]").attr("readOnly", "readOnly");
			
			buttons = [{
				text : '关闭',
				onclick : closeDialog
			}];
		} else {
			
			$("#mainform input[name=posiNo]").attr("readonly", "readonly");
			jQuery.metadata.setType("attr", "validate");
			BIONE.validate($("#mainform"));

			buttons = [];
			buttons.push({
				text : '取消',
				onclick : closeDialog
			});
			if("" == "${flag}") {
				buttons.push({
					text : '修改',
					onclick : dept_save
				});
			}
		}
		BIONE.addFormButtons(buttons);
	}
	
	function dept_save() {
		BIONE.submitForm($("#mainform"), function(data) {
			BIONE.closeDialogAndReloadParent("posiModifyWin","maingrid","修改成功");
		}, function(data) {
			BIONE.closeDialog("posiModifyWin","修改失败");
		});
	}
	
	function closeDialog(){
		BIONE.closeDialog("posiModifyWin");
	}
</script>
</head>
<body>
<div id="template.center">
	<form name="mainform" method="post" id="mainform" action="${ctx}/bione/admin/posi">
	</form>
</div>
</body>
</html>