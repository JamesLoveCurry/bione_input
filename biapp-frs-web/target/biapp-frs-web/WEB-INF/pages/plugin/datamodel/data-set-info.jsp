<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<%@ include file="/common/taglibs.jsp"%>
<html>
<head>
<meta name="decorator" content="/template/template5.jsp">
<script type="text/javascript">
    var f_save;
	//扩展JQuery以增强textarea编辑功能
	jQuery.extend({
		/** 
		 * 清除当前选择内容 
		 */
		unselectContents : function() {
			if (window.getSelection)
				window.getSelection().removeAllRanges();
			else if (document.selection)
				document.selection.empty();
		}
	});
	jQuery.fn
			.extend({
				/** 
				 * 选中内容 
				 */
				selectContents : function() {
					$(this)
							.each(
									function(i) {
										var node = this;
										var selection, range, doc, win;
										if ((doc = node.ownerDocument)
												&& (win = doc.defaultView)
												&& typeof win.getSelection != 'undefined'
												&& typeof doc.createRange != 'undefined'
												&& (selection = window
														.getSelection())
												&& typeof selection.removeAllRanges != 'undefined') {
											range = doc.createRange();
											range.selectNode(node);
											if (i == 0) {
												selection.removeAllRanges();
											}
											selection.addRange(range);
										} else if (document.body
												&& typeof document.body.createTextRange != 'undefined'
												&& (range = document.body
														.createTextRange())) {
											range.moveToElementText(node);
											range.select();
										}
									});
				},
				/** 
				 * 初始化对象以支持光标处插入内容 
				 */
				setCaret : function() {
					if (!$.browser.msie)
						return;
					var initSetCaret = function() {
						var textObj = $(this).get(0);
						textObj.caretPos = document.selection.createRange()
								.duplicate();
					};
					$(this).click(initSetCaret).select(initSetCaret).keyup(
							initSetCaret);
				},
				/** 
				 * 在当前对象光标处插入指定的内容 
				 */
				insertAtCaret : function(textFeildValue) {
					var textObj = $(this).get(0);
					if (document.all && textObj.createTextRange
							&& textObj.caretPos) {
						var caretPos = textObj.caretPos;
						caretPos.text = caretPos.text
								.charAt(caretPos.text.length - 1) == '' ? textFeildValue
								+ ''
								: textFeildValue;
					} else if (textObj.setSelectionRange) {
						var rangeStart = textObj.selectionStart;
						var rangeEnd = textObj.selectionEnd;
						var tempStr1 = textObj.value.substring(0, rangeStart);
						var tempStr2 = textObj.value.substring(rangeEnd);
						textObj.value = tempStr1 + textFeildValue + tempStr2;
						textObj.focus();
						var len = textFeildValue.length;
						textObj.setSelectionRange(rangeStart + len, rangeStart
								+ len);
						textObj.blur();
					} else {
						textObj.value += textFeildValue;
					}
				}
			});
	//处理不同数据源类型的验证
	jQuery.validator.addMethod("tableVLD", function(value, element) {
		var table_box = $("#mainform input[name='table_box']").val();
		return (table_box != null && table_box != "") ? true : false;
	}, "请选择一个数据库表");
	
	

	var mainform;

	$(function() {
		var parent = window.parent;
		var datasetObj = parent.datasetObj;
		var datasetId = datasetObj.datasetId;
		var groupicon = "${ctx}/images/classics/icons/communication.gif";
		var isAdd = datasetObj.isAdd;
		var readonly = false;
		if (datasetId != "" && datasetId != null) {
			readonly = true;
	    }
		//处理不同数据源类型的验证
		jQuery.validator.addMethod("sameTableEnNameCheck", function(value, element) {
			var res = true;
			$.ajax({
				async : false,
				type : "POST",
				url : "${ctx}/rpt/frame/dataset/sameTableEnNameCheck.json",
				data : {
					dsId : $("#mainform input[name='sourceId']").val(),
					datasetId : datasetId,
					setType : $("#mainform input[name='setType']").val(),
					tableEnName : value
				},
				success : function(result) {
					res = result;
				}
			});
			return res;
		}, "当前表已经配置数据模型");
		//渲染表单
		mainform = $("#mainform");
		mainform
				.ligerForm({
					inputWidth : 160,
					labelWidth : 80,
					space : 30,
					fields : [
							{
								name : "catalogId",
								type : "hidden"
							},
							{
								group : "数据集管理",
								groupicon : groupicon,
								display : "所属目录",
								name : "catalogName",
								newline : true,
								type : "select",
								width : 215,
								comboboxName: "catalog_box",
								options : {
									onBeforeOpen : function() {
										var url = "${ctx}/rpt/frame/dataset/catalogTree?catalogId="
												+ datasetObj.catalogId;
										dialog = BIONE
												.commonOpenDialog('维度目录',
														"datasetCatalogSlct", 400,300, url);
										return false;
									}
								}
							},
							{
								display : "数据集ID",
								name : "setId",
								newline : false,
								type : "text",
								width : 215,
								validate : {
									 required : true,
							         remote:{
							        		url:"${ctx}/rpt/frame/dataset/testSameSetId",
							        		type : "post",
							        		data : {
							        			isAdd : isAdd
							        		}
							        	},
							        	messages:{remote:"该数据集ID已存在"}
					        	}
							},
							{
								display : "数据集名称",
								name : "setNm",
								newline : true,
								type : "text",
								width : 215,
								validate : {
									required : true,
									maxlength : 100,
									remote : "${ctx}/rpt/frame/dataset/datasetNameCanUse?catalogId="+datasetObj.catalogId+"&setId="+datasetId+"&d="+ new Date().getTime(),
												messages : {
													remote : "相同路径下数据集名称已存在。"
												}
								}
							},
							{
								display : "数据集类型",
								name : "setType",
								newline : false,
								type : "select",
								comboboxName:"set_type_box",
								width : 215,
								validate : {
									required : true,
									messages : {
										required : "请选择一个数据集类型"
									}
								},
								options : {
									url : "${ctx}/rpt/frame/dataset/setTypeList.json",
									onBeforeSelect : function() {
										$("#mainform input[name='table_box']").val("");
									}
								}
							},
							{
								display : "业务类型",
								name : "busiType",
								newline : true,
								type : "select",
								comboboxName:"busi_type_box",
								width : 215,
								validate : {
									required : true,
									messages : {
										required : "请选择一业务类型"
									}
								},
								options : {
									url : "${ctx}/report/frame/datashow/idx/getIdxTaskType.json",
									readonly : readonly
								}
							},
							{
								display : "数据源",
								name : "sourceId",
								newline : false,
								type : "select",
								comboboxName:"ds_box",
								width : 215,
								validate : {
									required : true,
									messages : {
										required : "请选择一个数据源"
									}
								},
								options : {
									url : "${ctx}/rpt/frame/dataset/dsList.json",
									onBeforeSelect : function() {
										$("#mainform input[name='table_box']").val("");
									}
								}
							},{
								display : "数据库表",
								name : "table",
								newline : true,
								type : "select",
								width : 540,
								comboboxName: "table_box",
								validate : {
									tableVLD : true,
									required : true ,
									sameTableEnNameCheck: true
								},
								options : {
									onBeforeOpen : function() {
										var dsId = $(
												"#mainform input[name='sourceId']")
												.val();
										if (!dsId) {
											BIONE.tip("请选择一个数据源。");
											return false;
										}
										var url = "${ctx}/rpt/frame/dataset/tablePage?dsId="+dsId+"&datasetId="+datasetId;
										dialog = window.parent.BIONE
												.commonOpenDialog('请选择物理表',
														"selectGrid", "700",
														"350", url);
										return false;
									}
								}
							}, {
								display : "来源数据过滤条件",
								name : "srcDataFilterCond",
								newline : true,
								type : "hidden",
								width : 543,
								validate : {
									maxlength : 500
								}
							}, {
								display : "描述",
								name : "remark",
								newline : true,
								type : "textarea",
								width : 543,
								validate : {
									maxlength : 500
								}
							} ]

				});
		$("#remark").css("resize","none");
		jQuery.metadata.setType("attr", "validate");
		BIONE.validate("#mainform");
		
		//表单赋值
		$("#mainform input[name='catalogId']").val(datasetObj.catalogId);
		$.ligerui.get('catalog_box').setText(datasetObj.catalogName);
        if (datasetId != "" && datasetId != null) {
        	$("#mainform input[name='setId']").val(datasetObj.datasetId)
				.css("color", "#999").attr("readonly", "true").removeAttr("validate");
        }

		//修改时初始化表单
		if (datasetId != "" && datasetId != null) {
			$.ajax({
				type : "POST",
				url : "${ctx}/rpt/frame/dataset/datasetInfo.json?datasetId="
						+ datasetId,
				success : function(model) {
					datasetObj.setType =  model.setType;
					datasetObj.dsId = model.sourceId;
					datasetObj.table = model.tableEnNm==null?"":model.tableEnNm;
					$.ligerui.get('ds_box').selectValue(model.sourceId);
					$.ligerui.get('set_type_box').selectValue(model.setType);
					$.ligerui.get('busi_type_box').selectValue(model.busiType);
					$("#mainform input[name='setId']").val(model.setId);
					$("#mainform input[name='setNm']").val(model.setNm);
					$("#mainform textarea[name='remark']").val(model.remark);
                    $.ligerui.get('table_box')._changeValue(model.tableEnNm,model.tableEnNm);
                    $("#mainform textarea[name='srcDataFilterCond']").val(model.srcDataFilterCond);
					parent.isLoaded=true;
				}
			});
		}
		if(!'{show}'){
			//添加按钮
			var btns = [ {
				text : "取消",
				onclick : function() {
					window.parent.closeDsetBox();
				}
			}/* , {
				text : "下一步",
				onclick : f_save
			}  */];
			BIONE.addFormButtons(btns);
		}
		//保存
		f_save = function() {
			if ($("#mainform").valid()) {
				parent.canSelect=true;
				var setId = $("#mainform input[name='setId']").val(); 
				var dsId = $("#mainform input[name='sourceId']").val();
				var table = $("#mainform input[name='table_box']").val();
				var setType=  $("#mainform input[name='setType']").val();
				var busiType=  $("#mainform input[name='busiType']").val();
				
				//决定是否刷新
				if (datasetObj.dsId != dsId|| datasetObj.table != table||datasetObj.setType !=setType) {
					datasetObj.refresh = true;
				} else {
					datasetObj.refresh = false;
				}
				//决定数据来源
				if (!datasetObj.datasetId) {
					//新增
					datasetObj.from ="table";
				} else {
					//修改
					datasetObj.isAdd = false;
					if (datasetObj.firstEdit && datasetObj.table == table) {
						datasetObj.from = "dataset";
					} else {
						firstEdit = false;
						datasetObj.from = "table";
					}
				}
				
				//数据集基本信息缓存
				datasetObj.datasetId = setId;
				datasetObj.dsId = dsId;
				datasetObj.table = table;
				datasetObj.datasetName = $(
						"#mainform input[name='setNm']").val();
				datasetObj.remark = $("#mainform textarea[name='remark']")
						.val();
				datasetObj.srcDataFilterCond = $("#mainform textarea[name='srcDataFilterCond']").val();
				datasetObj.setType =  setType;
				datasetObj.busiType =  busiType;
				parent.next();
			} else {
				window.parent.BIONE.showInvalid(BIONE.validator);
				parent.canSelect=false;
			}
		}
		if (!(datasetId != "" && datasetId != null)) {
		   parent.isLoaded=true;
		}
	});
</script>
<title>Insert title here</title>
</head>
<body style="width:80%">
	<div id="template.center">
		<form id="mainform"
			action="${ctx}/dataquality/rulemanage/meta-rule-grp/saveRuleGrpType"
			method="POST"></form>
	</div>
</body>
</html>