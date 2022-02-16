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
			fields : [
				{
					name:'rid',
					type:'hidden'
				},{
					display : "报送模块",
					name : "moduleType",
					newline : false,
					type : "select",
					cssClass : "field",
					comboboxName : "moduleTypeBox",
					group : "基本信息",
					groupicon : groupicon,
					validate : {
						required : true
					},
					options : {
						data : [ {
							text : '请选择',
							id : ''
						}, {
							text : '1104监管',
							id : '02'
						}, {
							text : '人行大集中',
							id : '03'
						}, {
							text : '利率报备',
							id : '05'
						} ]
					}
				}, {
					display : "报表编号",
					name : "rptNum",
					newline : false,
					type : "text",
					cssClass : "field",
					validate : {
						required : true
					}
				}, {
					display : "报表名称",
					name : "rptNm",
					newline : false,
					type : "text",
					cssClass : "field",
					validate : {
						required : true
					}
				}, {
					display : "报送机构",
					name : "orgNo",
					newline : false,
					type : "select",
					cssClass : "field",
					comboboxName : "orgNmBox",
					validate : {
						required : true
					},
					options : {
						onBeforeOpen : function() {
							var moduleType = $.ligerui.get("moduleTypeBox").getValue();
							if (moduleType) {
								var height = $(window).height() - 120;
								var width = $(window).width() - 480;
								window.BIONE.commonOpenDialog(
												"机构树",
												"orgTreeWin",
												width,
												height,
												"${ctx}/frs/submitdivision/searchOrgTree?moduleType=" + moduleType,
												null);
								return false;
							} else {
								BIONE.tip("请选择报送模块！");
							}
						},
						cancelable : true
					}
				}, {
					display : "负责部门",
					name : "deptNo",
					newline : true,
					type : "select",
					cssClass : "field",
					comboboxName : "deptNoID",
					options : {
						onBeforeOpen : function() {
							var orgNo = $.ligerui.get("orgNmBox").getValue();
							if(orgNo){
								var deptOpts = {
									url : "${ctx}/frs/submitdivision/searchDeptTree?orgNo=" + orgNo,
									dialogname : 'deptComBoBox',
									title : '选择部门',
									comboxName : 'deptNoID',
									height : '410',
									width : '450'
								};
								BIONE.commonOpenIconDialog(deptOpts.title, deptOpts.dialogname,
										deptOpts.url, deptOpts.comboxName);
								return false;
							}else{
								BIONE.tip("请选择报送机构！");
							}
						},
						cancelable : true
					}
				}
			]
		});

		if('${id}'){
			$.ajax({
				url : "${ctx}/frs/submitdivision/${id}",
				dataType : "json",
				success : function(result) {
					$("#rid").val(result.rid);
					$.ligerui.get("moduleTypeBox").setValue(result.moduleType);
					$("#rptNum").val(result.rptNum);
					$("#rptNm").val(result.rptNm);
					$.ligerui.get("orgNmBox").setValue(result.orgNo);
					$.ligerui.get("orgNmBox").setText(result.orgName);
					$.ligerui.get("deptNoID").setValue(result.deptNo);
					$.ligerui.get("deptNoID").setText(result.deptName);
				}
			});
		}
		
		jQuery.metadata.setType("attr", "validate");
		BIONE.validate($("#form1"));

		var buttons = [];
	
		buttons.push({
		    text : '取消',
		    onclick : function() {
			BIONE.closeDialog("errorModifyWin");
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
		    BIONE.closeDialogAndReloadParent("errorModifyWin", "maingrid",
			    "修改成功");
		}, function() {
		    BIONE.closeDialog("errorModifyWin", "修改失败");
		});
	}
</script>
</head>
<body>
	<div id="template.center">
		<form name="form1" method="post" id="form1"
			action="${ctx}/frs/submitdivision/save"></form>
	</div>
</body>
</html>