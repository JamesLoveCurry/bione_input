<%@ page language="java" pageEncoding="UTF-8"%>
<%@ include file="/common/taglibs.jsp"%>
<html>
<head>
<meta name="decorator" content="/template/template5.jsp">
<script type="text/javascript" src="${ctx}/js/datainput/remark/global.js"></script>
<script type="text/javascript" src="${ctx}/js/datainput/remark/lib.js"></script>
<script type="text/javascript">
	var groupicon = "${ctx}/images/classics/icons/communication.gif";
	//创建表单结构 
	var mainform,contents;
	var dictType = [ {
		text : '常量',
		id : '1'
	}, {
		text : '数据库表',
		id : '2'
	} ];
	var dictShowValue = [
	    {text : '数据树',id : '01', pid : '2'}, 
	    {text : '下拉列表',id : '02', pid : '2'},
	    {text : '下拉列表',id : '02', pid : '1'}];
	$(function() {
		mainform = $("#mainform");
		mainform.ligerForm({
			fields : [ {
				display : "所属目录",
				name : 'dictCatalog',
				newline : true,
				type : 'select',
				comboboxName : 'dictCatalogCombox',
				options : {
					openwin : true,
					valueFieldID : "catalogId"
				},
				group : "数据字典信息",
				groupicon : groupicon,
				validate : {
					required : true,
					maxlength : 100
				}
			}, {
				display : "字典名称",
				name : 'dictName',
				newline : false,
				type : 'text',
				options : {
					openwin : true,
					valueFieldID : "catalogId"
				},
				validate : {
// 					remote : {
// 						url : "${ctx}/rpt/input/library/checkLibName",
// 						type : 'get',
// 						async : true
// 					},
// 					messages : {
// 						remote : "字典名称已存在，请检查！"
// 					},
					required : true,
					maxlength : 100
				}
			}, {
				display : '字典类型',
				name : 'libTypeValue',
				newline : true,
				type : 'select',
				validate : {
					required : true
				},
				options : {
					valueFieldID : 'dictType',
					data : dictType,
					onSelected : function(val) {
						if(val != ""){
						if (val == '1') {
							$.ligerui.get("libShowValue").setValue('02');
							$("#sqlText").parent().parent().parent().hide();
							$("#sqlText").val('');
						} else {
							$("#sqlText").parent().parent().parent().show();
						}
						setCss();
						var newData = new Array();
	                    for (i = 0; i < dictShowValue.length; i++)
	                    {
	                        if (dictShowValue[i].pid == val)
	                        {
	                            newData.push(dictShowValue[i]);
	                        }
	                    }
	                    liger.get("libShowValue").setData(newData);
					}
					}
				}
			}, {
				display : '展示方式',
				name : 'libShowValue',
				newline : false,
				type : 'select',
				validate : {
					required : true
				},
				options : {
					valueFieldID : 'showType'
				}
			}, {
				display : "数据来源",
				name : 'dsName',
				newline : true,
				type : 'select',
				comboboxName : 'dsNameCombox',
				options : {
					openwin : true,
					valueFieldID : "dsId"
				},
				validate : {
					required : true,
					maxlength : 32
				}
			},
			{
				display : "字典内容",
				name : 'staticContent',
				newline : true,
				type : 'text',
				validate : {
					maxlength : 500
				}
			},{
				display : "SQL语句",
				name : 'sqlText',
				newline : true,
				type : 'textarea',
				width : 600,
				validate : {
					maxlength : 500
				}
			} ]
		});
		$("#sqlText").css({ height:'100px'});
		$("#mainform input[name=staticContent]")
				.click(
						function() {
							contents = document.getElementById("staticContent").value;
							dialog = BIONE.commonOpenDialog(
											'字段内容',
											"libContent",
											"750",
											"420",
											"${ctx}/rpt/input/library/libContent?content="+ contents);
						});
		jQuery.metadata.setType("attr", "validate");
		BIONE.validate(mainform);
		var buttons = [];
		buttons.push({
			text : '取消',
			onclick : function() {
				BIONE.closeDialog("libraryAddWin");
			}
		});
		buttons.push({
			text : '保存',
			onclick : f_save
		});

		BIONE.addFormButtons(buttons);
		$("#dsNameCombox").ligerComboBox({
			onBeforeOpen : function() {
				openNewDilog();
				return false;
			}
		});

		if(parent.currentNode.id){
			$("#mainform input[name='dictCatalog']").val(parent.currentNode.text);
			$("#mainform input[name='catalogId']").val(parent.currentNode.id);
			$.ligerui.get("dictCatalogCombox")._changeValue(parent.currentNode.id, parent.currentNode.text);
		}


		$("#dictCatalogCombox").ligerComboBox({
			onBeforeOpen : function() {
				openNewDilogs();
				return false;
			}
		});
		if ($.ligerui.get("libTypeValue").getValue() == "")
			$.ligerui.get("libTypeValue").setValue('2');
		if ($.ligerui.get("libShowValue").getValue() == "")
			$.ligerui.get("libShowValue").setValue('02');
		check();
	});

	function f_save() {
		var content = document.getElementById("staticContent").value;
		var condition = document.getElementById("sqlText").value;
		var libId = document.getElementById("catalogId").value;
		var libCon = document.getElementById("dictName").value;
		condition=$.trim(condition);
		var dsId = document.getElementById("dsId").value;
		if($("#dictType").val() == '1'){
			if(content.length == 0){
				BIONE.tip('请输入字典内容。');
				return;
			}
			
		}else{
			if(condition.length == 0){
				BIONE.tip('请输入sql语句。');
				return;
			}
		}

		
		//是否保存数据
		var isSave = true;
		if (content.length > 4000) {
			BIONE.tip('字段内容长度不能超过500');
			return false;
		}
		if (libId != null && libId != '') {
			$.ajax({
				async : false,
				url : "${ctx}/rpt/input/library/testLibId",
				data : {
					"catalogId" : libId,
					"dictName" : libCon
				},
				success : function(result) {
					if(result != 0){
						isSave=false;
					}
				}
			});
		}
		if(isSave==false){
			BIONE.tip("相同目录下，字典名称不允许重复！");
			return;
		}
		if(condition!=null && condition!=''){
			$.ajax({ 
				async : false,
				url : "${ctx}/rpt/input/library/testDataLib.json",
				dataType : 'json',
				type : "get",
				data : {
					"sqlText" : condition ,
					"dsId" : dsId ,
				},
				success : function(result) {
					if(result==false){
						isSave=false;
					}
				}
			});
		}
		if(isSave==false){
			BIONE.tip("测试不通过，请输入正确的SQL语句！");
			return;
		}
		if(isSave){
			BIONE.submitForm($("#mainform"), function(text) {
				BIONE.closeDialogAndReloadParent("libraryAddWin", "maingrid",
						"添加成功");
			}, function() {
				BIONE.closeDialog("libraryAddWin", "添加失败");
			});
		}
	}
	function setCss() {
		var val = $("#mainform input[name=dictType]").val();
		if (val == "2") {
			$("#mainform input[name=dsNameCombox]").parent().parent().parent()
					.parent("ul").show().find("input").removeAttr("disabled")
					.css("color", "black");
			$("#mainform input[name=staticContent]").parent().parent().parent("ul")
					.hide().find("input").attr("disabled", "disabled");
		} else {
			$("#mainform input[name=dsNameCombox]").parent().parent().parent()
					.parent("ul").hide().find("input").attr("disabled",
							"disabled").css("color", "black");
			$("#mainform input[name=staticContent]").parent().parent().parent("ul")
					.show().find("input").removeAttr("disabled");
		}
	}

	function openNewDilog() {
		dsName = $("#dsName");
		dsId = $("#dsId");
		dsNameCombox = $.ligerui.get("dsNameCombox");
		dialog = BIONE.commonOpenDialog('选择数据源', "dsList", "850", "500",
				"${ctx}/bione/mtool/datasource/chackDS/1");
	}

	function openNewDilogs() {
		dictCatalogCombox = $.ligerui.get("dictCatalogCombox");
		dialog = BIONE.commonOpenDialog('选择目录', "dirList", "700", "350",
				"${ctx}/rpt/input/temple/templeCatalog/" + "${dirTypeRule}");
	}
	
	function getContentList(paramStr) {
		$("#staticContent").ligerGetComboBoxManager().setValue(paramStr);
	}

	//对输入信息的提示
	function check() {
		$("#dictName").focus(
				function() {
					//checkLabelShow(LibRemark.global.libName);
					$("#checkLabelContainer").html(
							GlobalRemark.title + LibRemark.global.libName);
				});
		$("#dateColId").focus(
				function() {
					//checkLabelShow(LibRemark.global.dateColId);
					$("#checkLabelContainer").html(
							GlobalRemark.title + LibRemark.global.dateColId);
				});

		$("#libShowValue")
				.change(
						function() {
							var libTypeValue = document
									.getElementById("showType").value;
							if (libTypeValue == '1') {
								//checkLabelShow(LibRemark.global.libShowValue1);
								$("#checkLabelContainer")
										.html(
												GlobalRemark.title
														+ LibRemark.global.libShowValue1);
							} else {
								//checkLabelShow(LibRemark.global.libShowValue2);
								$("#checkLabelContainer")
										.html(
												GlobalRemark.title
														+ LibRemark.global.libShowValue2);
							}
						});
		$("#libTypeValue")
				.focus(
						function() {
							//checkLabelShow(LibRemark.global.libTypeValue);
							$("#checkLabelContainer").html(
									GlobalRemark.title
											+ LibRemark.global.libTypeValue);
						});
		$("#sqlText").focus(
				function() {
					//checkLabelShow(LibRemark.global.condition);
					$("#checkLabelContainer").html(
							GlobalRemark.title + LibRemark.global.condition);
				});
	}
</script>
</head>
<body>
	<div id="template.center">
		<form method="post" id="mainform" action="${ctx}/rpt/input/library/add"></form>
		<div>
			<font color="red">@USERORG代表填报人机构,@USERID代表填报人ID</font>
		</div>
	</div>
</body>
</html>