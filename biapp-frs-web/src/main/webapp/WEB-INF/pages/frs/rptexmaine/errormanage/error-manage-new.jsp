<%@ page language="java" pageEncoding="UTF-8"%>
<%@ include file="/common/taglibs.jsp"%>
<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN">
<html>
<head>
<meta name="decorator" content="/template/template5.jsp">
<script type="text/javascript">
    var groupicon = "${ctx}/images/classics/icons/communication.gif";
    $(function() {
		$("#mainform").ligerForm({
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
						} ],
						onSelected : function(value) {
							if ("" != value) {
								$.ajax({
									async : false,
									type : "post",
									url : "${ctx}/frs/rptexmaine/control/taskNmComBoBox.json?orgTypes=" + value + "&flag=1&d=" + new Date().getTime(),
									dataType : "json",
									success : function(taskData) {
										$.ligerui.get("taskNm_sel").setValue("");
										$.ligerui.get("taskNm_sel").setData(taskData);
									}
								});
							}
						}
					}
				}, {
					display : "报送任务",
					name : "taskId",
					newline : false,
					type : "select",
					cssClass : "field",
					comboboxName : "taskNm_sel",
					validate : {
						required : true
					},
					options : {
						onBeforeOpen : function() {
							var moduleType = $.ligerui.get("moduleTypeBox").getValue();
							if ("" == moduleType) {
								$.ligerui.get("taskNm_sel").setValue("");
								$.ligerui.get("taskNm_sel").setData("");
								BIONE.tip("请选择报送模块");
							}
						},
						onSelected : function(value, text) {
							$("#taskNm").val(text);
						},
						cancelable : true,
						dataFilter : true
					}
				}, {
					display : "报送任务",
					name : "taskNm",
					type : "hidden"
				}, {
					display : "数据日期",
					name : "dataDate",
					newline : true,
					type : "date",
					cssClass : "field",
					options : {
						format : 'yyyyMMdd',
					},
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
				}, {
					display : "错误概述 ",
					name : "errorDesc",
					newline : true,
					type : "textarea",
					width : 475,
					validate : {
					    maxlength : 500
					}
				}
			]
		});
		
		jQuery.metadata.setType("attr", "validate");
		BIONE.validate($("#mainform"));
		var buttons = [];
		buttons.push({
		    text : '取消',
		    onclick : function() {
			BIONE.closeDialog("errorAddWin");
		    }
		});
		buttons.push({
		    text : '保存',
		    onclick : f_save
		});
	
		BIONE.addFormButtons(buttons);
	});
    
	function f_save() {
		BIONE.submitForm($("#mainform"), function() {
		    BIONE.closeDialogAndReloadParent("errorAddWin", "maingrid", "添加成功");
		}, function() {
		    BIONE.closeDialog("errorAddWin", "添加失败");
		});
    }
</script>
</head>
<body>
	<div id="template.center">
		<form name="mainform" method="post" id="mainform"
			action="${ctx}/frs/errormanage/save"></form>
	</div>
</body>
</html>