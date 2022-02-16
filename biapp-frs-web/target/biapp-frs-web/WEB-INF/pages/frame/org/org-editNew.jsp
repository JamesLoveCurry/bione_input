<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<%@ include file="/common/taglibs.jsp"%>
<html>
<head>
<meta name="decorator" content="/template/template14.jsp">
<script type="text/javascript">
	jQuery.validator.addMethod("packageIdReg", function(value, element) {
	    var packageCode = /^[0-9a-zA-Z]*$/;
	    return this.optional(element) || (packageCode.test(value));
	}, "请不要输入包含中文及特殊字符");

	jQuery.validator.addMethod("orgNm", function(value, element) {
	    var numReg = /^[0-9a-zA-Z\u2160-\u216B\u4e00-\u9fa5\uFF08]*$/;//数字、字母、罗马数字、中文
	    return this.optional(element) || (numReg.test(value));
	}, "机构名称命名不合法");
	
	var groupicon = "${ctx}/images/classics/icons/communication.gif";
	var mainform;
	var upNo='${orgNo}';
	var upName='${upName}';
	var currentNode;
    var field = [ {
    	name:'upNo',
    	type:'hidden'
    },{
		display : "机构编号",
		name : "orgNo",
		newline : true,
		type : "text",
		validate : {
			packageIdReg:true,
		    required : true,
		    maxlength : 32,
		    remote : "${ctx}/bione/admin/org/testOrgNo",
		    messages : {
				remote : "机构编号已存在"
		    }
		},
		group : "机构信息",
		groupicon : groupicon
	}, {
		display : "机构名称",
		name : "orgName",
		newline : false,
		type : "text",
		validate : {
			orgNm : true,
		    required : true,
		    maxlength : 100,
		    remote : "${ctx}/bione/admin/org/testOrgNm",
		    messages : {
				remote : "机构名称已存在"
		    }
		    
		}
	}, {
		display : "上级机构 ",
		name : "upOrg",
		newline : true,
		type : "text"
	}, {
		display : '机构状态',
		id : 'orgSts',
		name : 'orgSts',
		newline : false,
		type : 'select',
		options:{
    		initValue:'1',
    		data:[{
    			text:'启用',
    			id:'1'
    		},{
    			text:'停用',
    			id:'0'
    		}]
    	}
    }, {
		display : "备注 ",
		name : "remark",
		newline : true,
		type : "textarea",
		width : 475,
		validate : {
		    maxlength : 500
		}
    } ];
   
	//创建表单结构 
	function ligerFormNow() {
		mainform = $("#mainform").ligerForm({
		    inputWidth : 170,
		    labelWidth : 90,
		    space : 40,
		    fields : field
		});
		jQuery.metadata.setType("attr", "validate");
		BIONE.validate($("#mainform"));
	}
	$(function(){
		ligerFormNow();
		$("#mainform [name='upNo']").val(upNo);
		$("#mainform [name='upOrg']").val(decodeURI(decodeURI(upName)));
		$("#mainform [name='upOrg']").attr("disabled", "disabled");
	});
	
	function f_save() {
		$.ligerDialog.confirm('确认保存', '是否确认保存', function(yes) {
			if (yes) {
				var treeObj = window.parent.leftTreeObj;
				BIONE.submitForm($("#mainform"), function() {
					BIONE.tip("保存成功");
					if(upNo=="0"){
						upNo ="0";
					}
					var pnode =treeObj.getNodeByParam("id",upNo);
					if(!pnode.isParent){
						upNo=pnode.upId;
					}
					BIONE
					.refreshAsyncTreeNodes(treeObj, "id", upNo,
							"refresh");
					parent.$(".l-dialog-btn").remove();
				}, function() {
					BIONE.tip("保存失败");
				});
			}
		});
	}
</script>

<title>机构管理</title>
</head>
<body>
<div id="template.center">
	<form name="mainform" method="post" id="mainform" action="${ctx}/bione/admin/org">
	</form>
</div>
</body>
</html>