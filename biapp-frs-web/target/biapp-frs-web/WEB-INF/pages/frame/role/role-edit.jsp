<%@ page language="java" pageEncoding="UTF-8"%>
<%@ include file="/common/taglibs.jsp"%>
<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN">
<html>
<head>
<meta name="decorator" content="/template/template5.jsp">
<script type="text/javascript">
    var groupicon = "${ctx}/images/classics/icons/communication.gif";
    $(function() {
	var mainform = $("#form1").ligerForm({
	    fields : [ {
			name : 'roleId',
			type : 'hidden'
	    }, {
		display : '角色标识',
		name : 'roleNo',
		newline : true,
		type : 'text',
		group : "角色信息",
		validate : {
		    required : true,
		    maxlength : 32
		},
		groupicon : groupicon
	    }, {
			display : '角色名称',
			name : 'roleName',
			newline : false,
			type : 'text',
			validate : {
			    required : true,
			    maxlength : 100
			}
	    }, {
			display : '平台角色',
			name : 'roleTypeJg',
			newline : false,
			type : 'select',
			options : {
			    url : "${ctx}/bione/admin/role/getBioneRoleInfoExt",
			},
			validate : {
			    required : true
			}
		 }, {
			display : '角色状态',
			name : 'roleSts',
			newline : true,
			type : 'select',
			options : {
			    data : [ {
				text : '启用',
				id : '1'
			    }, {
				text : '停用',
				id : '0'
			    } ]
			}
	    }, {
			display : '备&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;注',
			name : 'remark',
			newline : true,
			width : '493',
			validate : {
			    maxlength : 500
			},
			type : 'textarea'
		}, {
			name : 'lastUpdateUser',
			type: "hidden"
		}]
	});

	BIONE.loadForm(mainform, {
	    url : "${ctx}/bione/admin/role/${id}"
	});
	$("#form1 input[name=roleNo]").attr("readonly", "true").removeAttr("validate");
	jQuery.metadata.setType("attr", "validate");
	BIONE.validate($("#form1"));

	var managers = $.ligerui.find($.ligerui.controls.Input);
	for ( var i = 0, l = managers.length; i < l; i++) {
	    //改变了表单的值，需要调用这个方法来更新ligerui样式
	    managers[i].updateStyle();
	}

	var buttons = [];

	buttons.push({
	    text : '取消',
	    onclick : function() {
		BIONE.closeDialog("roleModifyWin");
	    }
	});
	buttons.push({
	    text : '修改',
	    onclick : f_save
	});
	BIONE.addFormButtons(buttons);
    });
    function f_save() {
	BIONE.submitForm($("#form1"), function() {
	    BIONE.closeDialogAndReloadParent("roleModifyWin", "maingrid",
		    "修改成功");
	}, function() {
	    BIONE.closeDialog("roleModifyWin", "修改失败");
	});
    }
</script>
</head>
<body>
	<div id="template.center">
		<form name="form1" method="post" id="form1"
			action="${ctx}/bione/admin/role"></form>
	</div>
</body>
</html>