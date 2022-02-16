<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<%@ include file="/common/taglibs.jsp"%>
<html>
<head>
<meta name="decorator" content="/template/template5.jsp" />
<script type="text/javascript">
	//参数模板缓存
	var paramtmpId = "${paramtmpId}";
	var catalogId = "${catalogId}";
	var defaultFields = [ {
		name : 'catalogId',
		type : 'hidden'
	}, {
		name : 'paramtmpId',
		type : 'hidden'
	}, {
		display : '参数模板名称',
		name : 'paramtmpName',
		newline : true,
		type : 'text',
		group : "参数模板信息",
		validate : {
			remote : {
				url : '${ctx}/report/frame/param/templates/tmpNameCanUse',
				type : 'post',
				data : {
					'catalogId' : "${catalogId}",
					'paramtmpId' : "${paramtmpId}"
				}
			},
			required : true,
			maxlength : 32,
			messages : {
				required : "名称不能为空。",
				remote : "该路径下已存在相同名称。"
			}
		}
	}, {
		display : '参数模板类型',
		name : 'templateType',
		newline : true,
		type : 'select',
		comboboxName : 'templateTypeID',
		options : {
			data : [ {
				id : "static",
				text : "固定模版"
			}, {
				id : "custom",
				text : "自定义模版"
			} ]
		},
		validate : {
			required : true
		}
	}, {
		display : '参数模板URL',
		name : 'templateUrl',
		newline : true,
		type : 'text',
		validate : {
			required : true,
			maxlength : 2000
		}
	}, {
		display : '自定义参数模板',
		name : 'paramJson',
		newline : true,
		textField : 'paramJsonText',
		type : 'popup',
		options : {
			onButtonClick : function() {
				window.paramDialog = window.parent.$.ligerDialog.open({
					id : 'paramDialog',
					title : '参数模版配置',
					url : '${ctx}/report/frame/param/templates/design',
					width : $(window).width() + 100,
					height : $(window).height() + 100,
					left : 5,
					top : 2,
					allowClose : false,
					isResize : true
				});
			}
		},
		validate : {
			required : true
		}
	}, {
		display : '备&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;注',
		name : 'remark',
		newline : true,
		width : 300,
		validate : {
			maxlength : 500
		},
		type : 'textarea',
		attr : {
			style : "resize :none"
		}
	} ];

	function selectedCustom() {
		$("#mainform input[name=paramJson]").parent().parent().parent().attr(
				"style", "display:inline");
		$("#mainform input[name=templateUrl]").parent().parent().parent().attr(
				"style", "display:none");
		$("#mainform input[name=templateUrl]").attr("validate",
				"{\"required\":false,\"maxlength\":2000}");
		$("#mainform input[name=paramJson]").attr("validate",
				"{\"required\":true}");
	}

	function selectedStatic() {
		$("#mainform input[name=templateUrl]").parent().parent().parent().attr(
				"style", "display:inline");
		$("#mainform input[name=paramJson]").parent().parent().parent().attr(
				"style", "display:none");
		$("#mainform input[name=paramJson]").attr("validate",
				"{\"required\":false}");
		$("#mainform input[name=paramJsonText]").attr("validate",
				"{\"required\":false}");
		$("#mainform input[name=templateUrl]").attr("validate",
				"{\"required\":true,\"maxlength\":2000}");
	}

	function selectType() {
		var catalogId = $("#mainform input[name=catalogId]").val(), paramtmpId = $(
				"#mainform input[name=paramtmpId]").val(), paramtmpName = $(
				"#mainform input[name=paramtmpName]").val(), remark = $(
				"#mainform textarea[name=remark]").val();
		if (this.getValue() === 'static') {
			selectedStatic();
		} else if (this.getValue() === 'custom') {
			selectedCustom();
		} else {
			templateTypeSelect = $.ligerui.get("templateTypeID");
		}
		$("#mainform input[name=catalogId]").val(catalogId);
		$("#mainform input[name=paramtmpId]").val(paramtmpId);
		$("#mainform input[name=paramtmpName]").val(paramtmpName);
		$("#mainform textarea[name=remark]").val(remark);
		BIONE.validator.element("#paramtmpName");
	}

	$(function() {
		initForm();
	});
	function initForm() {
		$("#mainform").ligerForm({
			fields : defaultFields,
			labelWidth : 120,
			inputWidth : 300
		});
		$.ligerui.get("templateTypeID").set("onSelected", selectType);
		$("#mainform input[name=catalogId]").val(catalogId);
		$("#mainform input[name=paramtmpId]").val(paramtmpId);
		if (paramtmpId != "") {
			var params = null;
			var url = null;
			var paramType = null;
			$
					.ajax({
						cache : false,
						async : true,
						url : "${ctx}/report/frame/param/templates/"
								+ paramtmpId + "?type=edit",
						dataType : 'json',
						type : "get",
						success : function(data) {
							$("#mainform input[name=catalogId]").val(
									data.catalogId);
							$("#mainform input[name=paramtmpId]").val(
									data.paramtmpId);

							params = data.paramJson;
							url = data.templateUrl;
							paramType = data.templateType;

							$("#mainform input[name=paramtmpName]").val(
									data.paramtmpName);
							$("#mainform textarea[name=remark]").val(
									data.remark);
							$.ligerui.get('templateTypeID').selectValue(
									data.templateType);
						},
						complete : function() {
							if (paramType == "custom") {
								$("#mainform input[name=paramJson]")
										.val(params);
								$("#mainform input[name=paramJsonText]").val(
										"已定义参数模版");
							} else {
								$.ligerui.get('templateUrl').setValue(url);
							}
						}
					});
		}
		jQuery.metadata.setType("attr", "validate");

		BIONE.validate($("#mainform"));
		var buttons = [];

		buttons.push({
			text : '取消',
			onclick : function() {
				BIONE.closeDialog("paramtmpBox");
			}
		});
		buttons.push({
			text : '保存',
			onclick : f_save
		});
		BIONE.addFormButtons(buttons);
		$("#mainform input[name=templateUrl]").parent().parent().parent().attr(
				"style", "display:none");
		$("#mainform input[name=paramJson]").parent().parent().parent().attr(
				"style", "display:none");
	}

	function f_save() {
		BIONE.submitForm($("#mainform"),
				function() {
					window.parent.tempGrid.loadData();
					BIONE.closeDialogAndReloadParent("paramtmpBox", "maingrid",
							"保存成功");
				}, function() {
					BIONE.closeDialog("paramtmpBox", "保存失败");
				});
	}
</script>
</head>
<body>
	<div id="template.center">
		<form name="mainform" method="post" id="mainform"
			action="${ctx}/report/frame/param/templates"></form>
	</div>
</body>
</html>