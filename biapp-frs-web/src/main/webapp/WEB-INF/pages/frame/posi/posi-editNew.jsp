<%@ page language="java" pageEncoding="UTF-8"%>
<%@ include file="/common/taglibs.jsp"%>
<html>
<head>
<meta name="decorator" content="/template/template5.jsp">
<script type="text/javascript">

jQuery.validator.addMethod("packageIdReg", function(value, element) {
    var packageCode = /^[0-9a-zA-z]*$/;
    return this.optional(element) || (packageCode.test(value));
}, "请不要输入包含中文及特殊字符");

	var orgId = '${orgId}';
	//var upDeptId = '${deptId}';
	var orgNo='';
	var upNo='';
	var groupicon = "${ctx}/images/classics/icons/communication.gif";
	$(function(){
		$.ajax({
			cache : false,
			async : true,
			url : "${ctx}/bione/admin/posi/getRealCode.json",
			dataType : 'json',
			type : "get",
			data : {
				"orgId" : orgId
				//"deptId" : upDeptId
			},
			success : function(result) {
				if (!result){					
					return;
				}
				orgNo = result.orgNo;
				//upNo = result.deptNo;
				initForm();
			}
		});
	});	
	
	function initForm() {
		$("#mainform").ligerForm({
			fields:[{
				name:'orgNo',
				type:'hidden'
			},{
				display:"岗位编码<font color='red'>*</font>",
	        	name:'posiNo',
	        	newline:true, 
	        	type:'text',
	        	group: "岗位信息",
	        	validate:{
	        		packageIdReg:true,
	        		required:true,
	        		maxlength:32,
	        		remote :  {
	        			url : "${ctx}/bione/admin/posi/testPosiNo",
						type : "POST"
	        		},
					messages : {
						remote:"岗位编码已存在"
					}
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
	        		initValue:'1',
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
// 					initValue:'01',
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
		$("#mainform input[name=orgNo]").val(orgNo);
		//$("#mainform input[name=upNo]").val(upNo);
		
		jQuery.metadata.setType("attr", "validate");
		BIONE.validate($("#mainform"));
		var buttons = [];
		
		buttons.push({
			text:'取消',
			onclick:function(){
				BIONE.closeDialog("posiAddWin");
			}
		});
		buttons.push({
			text : '保存',
			onclick : f_save
		});
		BIONE.addFormButtons(buttons);
	}
	
	function f_save() {
		BIONE.submitForm($("#mainform"), function(result) {
			if(result.msg==null){
				BIONE.closeDialogAndReloadParent("posiAddWin","maingrid","岗位保存成功");
			}else{
				BIONE.closeDialog("posiAddWin","岗位保存失败");
			}
		}, function() {
			BIONE.closeDialog("posiAddWin","保存失败");
		});
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