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
		    fields : [{
		    	name : 'logicSysNo',
				type : 'hidden',
		    },{
		    	name : 'layoutId',
				type : 'hidden',
		    },{
		    	name : 'designId',
				type : 'hidden',
		    },{
			    display : '首页名称',
				name : 'designNm',
				type : 'text',
				group : "公共首页信息",
				groupicon : groupicon,
				validate : {
				    required : true
				}
		    },{
				display : '备&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;注',
				name : 'remark',
				newline : true,
				width : '443',
				validate : {
				    maxlength : 500
				},
				type : 'textarea'
		    } ]
		});
		$("#remark").attr("resize","none");
		if("${designId}"!=""){
			$.ajax({
				cache : false,
				async : true,
				url : "${ctx}/bione/mainpage/design/getDesign?designId=${designId}",
				dataType : 'json',
				type : "post",
				beforeSend : function() {
					BIONE.loading = true;
					BIONE.showLoading("正在加载数据中...");
				},
				complete : function() {
					BIONE.loading = false;
					BIONE.hideLoading();
				},
				success : function(result) {
					$("#logicSysNo").val(result.logicSysNo);
					$("#layoutId").val(result.layoutId);
					$("#designId").val(result.designId);
					$("#designNm").val(result.designNm);
					$("#remark").val(result.remark);
				}
			});
			
		}
		jQuery.metadata.setType("attr", "validate");
		BIONE.validate($("#form1"));
	
		var buttons = [];
	
		buttons.push({
		    text : '取消',
		    onclick : function() {
			BIONE.closeDialog("designWin");
		    }
		});
		buttons.push({
		    text : '保存',
		    onclick : f_save
		});
		BIONE.addFormButtons(buttons);
    });
    
    function f_save() {
		BIONE.submitForm($("#form1"), function() {
		    BIONE.closeDialogAndReloadParent("designWin", "maingrid",
			    "保存成功");
		}, function() {
		    BIONE.closeDialog("designWin", "修改失败");
		});
    }
    
    function showIconDialog(options) {
		var options = {
			url : '${ctx}/bione/admin/func/selectIcon.html',
			dialogname : 'iconselector',
			title : '选择图标',
			comboxName : 'picPath'
		};
		BIONE.commonOpenIconDialog(options.title, options.dialogname,
				options.url, options.comboxName);
		return false;
	}
</script>
</head>
<body>
	<div id="template.center">
		<form name="form1" method="post" id="form1"
			action="${ctx}/bione/mainpage/design"></form>
	</div>
</body>
</html>