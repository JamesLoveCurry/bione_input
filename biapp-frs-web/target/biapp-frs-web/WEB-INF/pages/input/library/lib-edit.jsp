<%@ page language="java" pageEncoding="UTF-8"%>
<%@ include file="/common/taglibs.jsp"%>
<html>
<head>
<meta name="decorator" content="/template/template5.jsp">
<script type="text/javascript" src="${ctx}/js/datainput/remark/global.js"></script>
<script type="text/javascript" src="${ctx}/js/datainput/remark/lib.js"></script>
<script type="text/javascript">
	var groupicon = "${ctx}/images/classics/icons/communication.gif";
	var mainform,contents;
	var dictType = [ {
		text : '常量', id : '1'
	}, {
		text : '数据库表', id : '2'
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
				comboboxName : 'dictCatalogCombox',
				newline : true,
				type : 'select',
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
				validate : {
					required : true,
					maxlength : 100
				},
				groupicon : groupicon
			}, {
				display : '字典类型',
				name : 'dictTypeValue',
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
							$.ligerui.get("dictShowValue").setValue('02');
							//$.ligerui.get("dictShowValue").setDisabled();
							$('#sqlText').parents('ul:last').hide();
							$("#sqlText").attr("disabled","disabled");
							$("#sqlText").val('');
						} else {
							//$.ligerui.get("dictShowValue").setEnabled();
							$('#sqlText').parents('ul:last').show();
							$("#sqlText").removeAttr("disabled");
						}
						var newData = new Array();
	                    for (i = 0; i < dictShowValue.length; i++)
	                    {
	                        if (dictShowValue[i].pid == val)
	                        {
	                            newData.push(dictShowValue[i]);
	                        }
	                    }
	                    liger.get("dictShowValue").setData(newData);
						setCss();
					}
					}
				}
			}, {
				display : '展示方式', name : 'dictShowValue', newline : false, type : 'select',
				validate : {
					required : true
				},
				options : {
					valueFieldID : 'showType', data : null
				}
			}, {
				display : "数据来源", name : 'dsName', comboboxName : 'dsNameCombox', newline : true, type : 'select',
				options : {
					openwin : true, valueFieldID : "dsId"
				},
				validate : {
					required : true, maxlength : 32
				}
			}, {
				display : "字典内容", name : 'staticContent', newline : true, type : 'text',
				validate : {
					required : true, maxlength : 500
				}
			}, {
				display : "SQL语句", name : 'sqlText', newline : true, type : 'textarea', width : 600,
				validate : {
					maxlength : 500
				}
			} , {
				type : 'hidden', newline : true, name : 'createUser'
			}, {
				type : 'hidden', newline : true, name : 'createTime'
			}, {
				type : 'hidden', newline : true, name : 'logicSysNo'
			}, {
				type : 'hidden', newline : true, name : 'dictId'
			} ]
		});
		$("#sqlText").css({ height:'100px'});
		$("#mainform input[name=staticContent]").click(function() {
			contents = document.getElementById("staticContent").value;
			dialog = BIONE.commonOpenDialog('字典内容', "libContent", "750", "420", "${ctx}/rpt/input/library/libContent?content="+ contents);
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
		if ("${dictId}") {
			$.ajax({
				async : false,
				url : "${ctx}/rpt/input/library/findUdipDataById.json?t=" + new Date(),
				dataType : 'json',
				data : {
					"dictId" : "${dictId}"
				},
				type : "get",
				success : function(data) {
					$("#mainform input[name='dictCatalog']").val(data.catalogName);
					$("#mainform input[name='catalogId']").val(data.catalogId);
					$.ligerui.get("dsNameCombox")._changeValue(data.dsId, data.dsName);
					$.ligerui.get("dictCatalogCombox")._changeValue(data.catalogId, data.catalogName);
					$("#mainform input[name='dictName']").val(data.dictName);
					$.ligerui.get("dictTypeValue").setValue(data.dictType);
					$.ligerui.get("dictShowValue").setValue(data.showType);
					if (data.dictType == "1") {
						$("#mainform input[name='staticContent']").val(data.staticContent);
					} else {
						$("#mainform [name='sqlText']").val(data.sqlText);
					}
					$("#mainform input[name='createUser']").val(data.createUser);
					$("#mainform input[name='createTime']").val(data.createTime);
					$("#mainform input[name='logicSysNo']").val(data.logicSysNo);
					$("#mainform input[name='dictId']").val(data.dictId);
				}
			});
		}
		$("#dsNameCombox").ligerComboBox({
			onBeforeOpen : function() {
				openNewDilog();
				return false;
			}
		});
		$("#dictCatalogCombox").ligerComboBox({
			onBeforeOpen : function() {
				openNewDilogs();
				return false;
			}
		});
		if ($.ligerui.get("dictTypeValue").getValue() == "")
			$.ligerui.get("dictTypeValue").setValue('2');
		if ($.ligerui.get("dictShowValue").getValue() == "")
			$.ligerui.get("dictShowValue").setValue('2');
		check();
	});

	function f_save() {
		var libType = $("#dictType").val();
		var condition = $("#sqlText").val();
		condition=$.trim(condition);
		var dsId = $("#dsId").val();
		var libShow = document.getElementById("showType").value;
		var content = document.getElementById("staticContent").value;
		var dictName = dictName?dictName:$('#dictName').val();
		
		if (dictName == "" || dictName == null) {
			BIONE.tip('请输入字典名称。');
			return;
		}
		
		if(/^\s+$/gi.test(document.getElementById('dictName').value)){
			BIONE.tip('字典名称不能为空格！');
			return;
		}
		
		if(libType == '1'){
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
		if(condition!=null && condition!=''){
			$.ajax({ 
				async : false,
				url : "${ctx}/rpt/input/library/testDataLib.json",
				dataType : 'json',
				type : "get",
				data : {
					"sqlText" : condition ,
					"dsId" : dsId 
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
		if (libType == '1') {
			var info = {
				"dictCatalog" : document.getElementById('catalogId').value,
				"dictName" : document.getElementById("dictName").value,
				"dictType" : libType,
				"createUser" : document.getElementById("createUser").value,
				"createTime" : document.getElementById("createTime").value,
				"logicSysNo" : document.getElementById("logicSysNo").value,
				"dictId" : document.getElementById("dictId").value,
				"staticContent" : document.getElementById("staticContent").value,
				"catalogId" : document.getElementById("catalogId").value,
				"showType" : libShow
			};
		} else {
			var info = {
				"dictCatalog" : document.getElementById('catalogId').value,
				"dictName" : document.getElementById("dictName").value,
				"dictType" : libType,
				"dsId" : dsId,
				"sqlText" : condition,
				"createUser" : document.getElementById("createUser").value,
				"createTime" : document.getElementById("createTime").value,
				"logicSysNo" : document.getElementById("logicSysNo").value,
				"dictId" : document.getElementById("dictId").value,
				"catalogId" : document.getElementById("catalogId").value,
				"showType" : libShow
			};
		}
		if(isSave){
			var libName = 
			$.ajax({
				async : false,
				url : "${ctx}/rpt/input/library/checkLibName",
				dataType : 'json',
				type : "get",
				data : {
					dictId : "${dictId}",
					"dictName" : $.trim(document.getElementById("dictName").value) 
				},
				success : function(result) {
					if(result==false){
						BIONE.tip("字典名称已存在，请检查!");
					}else{
						$.ajax({
							async : false,
							url : "${ctx}/rpt/input/library/updateMode.json",
							dataType : 'json',
							data : info,
							type : "post",
							success : function(result) {
								BIONE.closeDialogAndReloadParent("libraryAddWin", "maingrid", "修改成功");
							}
						});
					}
				},
				error : function(result, b) {
					$.ajax({
						async : false,
						url : "${ctx}/rpt/input/library/updateMode.json",
						dataType : 'json',
						data : info,
						type : "post",
						success : function(result) {
							BIONE.closeDialogAndReloadParent("libraryAddWin", "maingrid", "修改成功");
						}
					});
				}
			});
		}
	}

	function setCss() {
		var val = $("#mainform input[name=dictType]").val();
		if (val == "2") {
			$("#mainform input[name=dsNameCombox]").parent().parent().parent().parent("ul").show().find("input").removeAttr("disabled").css("color", "black");
			$("#mainform input[name=staticContent]").parent().parent().parent("ul").hide().find("input").attr("disabled", "disabled");
		} else {
			$("#mainform input[name=dsNameCombox]").parent().parent().parent().parent("ul").hide().find("input").attr("disabled", "disabled").css("color", "black");
			$("#mainform input[name=staticContent]").parent().parent().parent("ul").show().find("input").removeAttr("disabled");
		}
	}

	function openNewDilog() {
		dsName = $("#dsName");
		dsId = $("#dsId");
		dsNameCombox = $.ligerui.get("dsNameCombox");
		dialog = BIONE.commonOpenDialog('选择数据源', "dsList", "850", "500", "${ctx}/bione/mtool/datasource/chackDS/1");
	}

	function openNewDilogs() {
		dictCatalogCombox = $.ligerui.get("dictCatalogCombox");
		dialog = BIONE.commonOpenDialog('选择目录', "dirList", "700", "350", "${ctx}/rpt/input/temple/templeCatalog/2");
	}

	function getContentList(paramStr) {
		$("#staticContent").ligerGetComboBoxManager().setValue(paramStr);
	}
	
	//对输入信息的提示
	function check() {
		$("#dictName").focus(function() {
			//checkLabelShow(LibRemark.global.libName);
			$("#checkLabelContainer").html(GlobalRemark.title + LibRemark.global.libName);
		});
		$("#sqlText").focus(function() {
			//checkLabelShow(LibRemark.global.condition);
			$("#checkLabelContainer").html(GlobalRemark.title + LibRemark.global.condition);
		});
		$("#dictShowValue").change(function() {
			var dictShowValue = document.getElementById("showType").value;
			if (dictShowValue == '01') {
				//checkLabelShow(LibRemark.global.libShowValue1);
				$("#checkLabelContainer").html(GlobalRemark.title + LibRemark.global.libShowValue1);
			} else {
				//checkLabelShow(LibRemark.global.libShowValue2);
				$("#checkLabelContainer").html(GlobalRemark.title + LibRemark.global.libShowValue2);
			}
		});
		$("#dictTypeValue").focus(function() {
			//checkLabelShow(LibRemark.global.libTypeValue);
			$("#checkLabelContainer").html(GlobalRemark.title + LibRemark.global.libTypeValue);
		});
	}
</script>
</head>
<body>
	<div id="template.center">
		<form name="mainform" method="post" id="mainform" action=""></form>
		<div>
			<font color="red">@USERORG代表填报人机构,@USERID代表填报人ID</font>
		</div>
	</div>
</body>
</html>