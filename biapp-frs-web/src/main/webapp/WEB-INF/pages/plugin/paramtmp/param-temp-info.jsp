<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<%@ include file="/common/taglibs.jsp"%>
<html>
<meta name="decorator" content="/template/template14.jsp">
<head>
<script src="${ctx}/plugin/js/jQuerySortable/jquery.ui.core.js"></script>
<script src="${ctx}/plugin/js/jQuerySortable/jquery.ui.widget.js"></script>
<script src="${ctx}/plugin/js/jQuerySortable/jquery.ui.mouse.js"></script>
<script src="${ctx}/plugin/js/jQuerySortable/jquery.ui.sortable.js"></script>

<!-- 动态控件样式 -->
<style>
#sorts {
	list-style-type: none;
	margin: 0;
	padding: 0;
	width: 500px;
}

#sorts>li {
	margin: 2px 0px 2px 18px;
	padding: 1px;
	float: left;
	width: 215px;
	height: 25px;
	border: 1px solid #F1F1F1;
	padding-top: 16px;
}

.labelli {
	width: 80px;
	text-align: left;
	float: left;
}

.label {
	float: left;
	width: 60px;
	border: 1px solid #F1F1F1;
	text-align: center;
	background-color: #F1F1F1;
}

.editorli {
	width: 100px;
	text-align: left;
	float: left;
}

.editor {
	width: 130px;
}

.cert {
	float: left;
	width: 96%;
	height: 100%;
}
</style>
<title>-------------------</title>
<script type="text/javascript">
	var propEditorWidth = 195;//属性控件宽

	//控件类型
	var Constants = {
		TEXT_TYPE : "01",
		COMB_TYPE : "02",
		DATE_TYPE : "03",
		TREE_TYPE : "04",
		HIDDEN_TYPE : "05",
		TYPE : {
			"t01" : "text",
			"t02" : "comb",
			"t03" : "date",
			"t04" : "tree",
			"t05" : "hidden"
		}
	};

	//参数模板缓存
	var Cache = {
		temp : {
			paramtmpId : null
		},
		params : {}
	};

	//当前选中控件
	var currentParam = {
		editorId : null,
		type : null
	};

	//生成唯一的控件Id
	var getEditorId = (function() {
		var id = 1;
		return function() {
			return new Date().getTime() + "" + (id++);
		};
	})();

	//属性gird
	var propGrid = null;

	$(function() {
		//修改时获取数据
		Cache.temp = eval('(' + '${temp}' + ')') || {};
		Cache.params = eval('(' + '${params}' + ')') || {};

		//参数属性
		var properties = [
				{
					"__status" : null,
					"name" : "参数标识",
					"value" : getText("paramNo")
				},
				{
					"__status" : null,
					"name" : "参数名称",
					"value" : getText("paramName")
				},
				{
					"__status" : null,
					"name" : "参数类型",
					"value" : getText("paramType_box")
				},
				{
					"__status" : null,
					"name" : "值类型",
					"value" : getText("valueType_box")
				},
				{
					"__status" : null,
					"name" : "默认值",
					"value" : getText("initValue")
				},

				{
					"__status" : null,
					"name" : "只读",
					"value" : getText("isReadonly_box")
				},
				{
					"__status" : null,
					"name" : "非空",
					"value" : getText("isNotnull_box")
				},
				{
					"__status" : null,
					"name" : "最大长度",
					"value" : getText("maxLen")
				},
				{
					"__status" : null,
					"name" : "下拉框数据源",
					"value" : "<textarea style='width:"+propEditorWidth+"px;height:70px;resize:none;margin-left:-2px;' name='combSource' id='combSource' class='l-textarea'></textarea>"
				},
				{
					"__status" : null,
					"name" : "日期格式",
					"value" : getText("dateFormat_box")
				},
				{
					"__status" : null,
					"name" : "选择方式",
					"value" : getText("selectType_box")
				},
				{
					"__status" : null,
					"name" : "根可选",
					"value" : getText("rootIssel_box")
				},
				{
					"__status" : null,
					"name" : "数据树数据源",
					"value" : "<textarea style='width:"+propEditorWidth+"px;height:70px;resize:none;margin-left:-2px;' name='treeSource' id='treeSource' class='l-textarea'></textarea>"
				},
				{
					"__status" : null,
					"name" : "备注",
					"value" : "<textarea style='width:"+propEditorWidth+"px;height:70px;resize:none;margin-left:-2px;' name='remark' id='remark' class='l-textarea'></textarea>"
				}, {
					"__status" : null,
					"name" : "hiddenId",
					"value" : getText("hiddenId")
				} ];

		//布局
		$("#tempframe").ligerLayout({
			leftWidth : 130,
			rightWidth : 300,
			bottomHeight : 36,
			allowLeftCollapse : false,
			allowRightCollapse : false,
			allowLeftResize : false,
			allowRightResize : false,
			allowTopResize : false,
			allowBottomResize : false

		});
		$(".l-layout-right .l-layout-header").hide();
		initTree();
		initPreview();
		initProp();
		initBottom();
		initCss();
		//修改时初始化数据
		if (!!Cache.temp.paramtmpId) {
			initData();
		}

		//左侧控件树
		function initTree() {
			//var rootIcon = "${ctx}/images/classics/icons/package.png";
			var inputIcon = "${ctx}/images/classics/icons/page_edit.gif";
			var combIcon = "${ctx}/images/classics/icons/mnuMenu.gif";
			var dateIcon = "${ctx}/images/classics/icons/date.png";
			var treeIcon = "${ctx}/images/classics/icons/mnuProjExpPlus.gif";
			var hiddenIcon = "${ctx}/images/classics/icons/tag_blue_edit.png";
			var setting = {
				data : {
					simpleData : {
						enable : true
					}
				},
				edit : {
					drag : {
						isCopy : true,
						isMove : false,
						prev : false,
						next : false,
						inner : false
					},
					enable : true,
					showRemoveBtn : false,
					showRenameBtn : false
				},
				callback : {
					beforeDrag : function(treeId, treeNodes) {
						if (treeNodes && treeNodes.length == 1
								&& treeNodes[0].id != "root") {
							return true;
						}
						return false;
					},
					onDrop : function(ev, treeId, treeNodes, targetNode,
							moveType) {
						var content = $("#dropDiv");
						var offset = content.offset();
						var mX = ev.clientX, mY = ev.clientY, cL = offset.left, cR = cL
								+ content.width(), cT = offset.top, cF = cT
								+ content.height();
						if ((mX > cL && mX < cR) && (mY > cT && mY < cF)) {
							var type = treeNodes[0].id;
							addEditor(type);
						}
					}
				}
			};
			var zNodes = [ {
				id : "01",
				type : "text",
				pId : "root",
				name : "文本框",
				isParent : false,
				icon : inputIcon
			}, {
				id : "02",
				type : "comb",
				pId : "root",
				name : "下拉框",
				isParent : false,
				icon : combIcon
			}, {
				id : "03",
				type : "date",
				pId : "root",
				name : "日期控件",
				isParent : false,
				icon : dateIcon
			}, {
				id : "04",
				type : "tree",
				pId : "root",
				name : "树形控件",
				isParent : false,
				icon : treeIcon
			}, {
				id : "05",
				type : "hidden",
				pId : "root",
				name : "隐藏域",
				isParent : false,
				icon : hiddenIcon
			} ];

			$(document).ready(function() {
				$.fn.zTree.init($("#edits"), setting, zNodes);
			});

		}

		//中间控件视图
		function initPreview() {
			var setting = {
				cursor : 'hand'
			};
			window['sorts'] = $("#sorts").sortable(setting);
			$("#removeParam").click(function() {
				var editorId = currentParam.editorId;
				$("#" + editorId).empty();
				$("#" + editorId).remove();
				delete Cache.params[editorId];//清除缓存中对应参数
				currentParam = {
					editorId : null,
					type : null
				};
				changeGrid();
				param_reset();
			});
			$("#removeAllParam").click(function() {
				$("#sorts").empty();
				Cache.params = {};
				currentParam = {
					editorId : null,
					type : null
				};
				changeGrid();
				param_reset();
			});
			//$("#sorts").disableSelection();
			$("#centerBox").click(
					function(e) {
						var ev = e || window.event;
						var target = ev.srcElement || ev.target;
						var editorId = null;
						var items = $(target).parents(".edititem");
						if (items && items.length == 1) {
							editorId = $(items[0]).attr('id');
						} else if ($(target).attr("class") == "edititem") {
							editorId = $(target).attr("id");
						}
						if (!!editorId) {
							if ($(target).attr("class")
									&& $(target).attr("class") == "label") {
								return;
							}
							$("#sorts li").css("border-color", "#F1F1F1");
							$("#" + editorId).css("border-color", "#D1D1D1");
							selectParamBox(editorId, getEditorType(editorId));
						}
					});

		}

		//右侧控件属性
		function initProp() {
			propGrid = $("#maingrid").ligerGrid({
				width : "100%",
				height : "150%",
				columns : [ {
					display : "属性名",
					name : "name",
					width : "30%"
				}, {
					display : "属性值",
					name : "value",
					width : "70%"
				} ],
				checkbox : false,
				rownumbers : false,
				usePager : false,
				alternatingRow : false,
				colDraggable : false,
				rowDraggable : false,
				data : {
					Rows : properties,
					Total : properties.length
				},
				toolbar : {}

			});
			var btns = [ {
				text : "保存",
				icon : "save",
				click : param_save,
				operNo : "param_save"
			}, {
				text : "重置",
				icon : "refresh",
				click : param_reset,
				operNo : "param_reset"
			} ];
			BIONE.loadToolbar(propGrid, btns, function() {
			});
			ligerPropEditor();
			selectParamBox();
		}

		//底部按钮区域
		function initBottom() {
			//添加按钮
			var btns = [ {
				text : "取消",
				onclick : function() {
					BIONE.closeDialog("paramtmpBox");
				}
			}, {
				text : "保存",
				onclick : tmp_save
			}, {
				text : "预览",
				onclick : tmp_preview
			} ];
			BIONE.addFormButtons(btns);
		}

		//初始化数据
		function initData() {
			$("#paramtmpName").val(Cache.temp.paramtmpName || "");
			$("#paramtmpRemark").val(Cache.temp.remark || "");
			if (!!Cache.params) {
				var ps = Cache.params;
				for ( var i in ps) {
					addEditor(ps[i].paramType, ps[i].paramName, i);
				}
			}
		}

		//调整样式
		function initCss() {
			$("#paramtmpName").ligerTextBox({
				width : 210
			});
			$("div.l-layout-header")
					.css("background",
							"url('${ctx}/css/classics/ligerUI/Gray/images/ui/gridbar.jpg')");
			$("#hiddenId").parents("tr").hide();
			$("#maingrid").css("border-left", "none");
			$(".l-layout-left .l-layout-header-inner")
					.html(
							"<ul><li style='float:left;'><img src='${ctx}/images/classics/icons/package.png' style='margin-top:4px;' /></li><li style='float:left;'>&nbsp;控件</li></ul>");
			$($(".l-dialog-btn").get(2)).css("margin-right", "400px");
			$($(".l-dialog-btn").get(3)).css("margin-right", "20px");
			$($(".l-dialog-btn").get(4)).css("margin-right", "20px");
			if (!Cache.temp.paramtmpId) {
				$($(".l-dialog-btn").get(4)).hide();
			}
		}

		//追加视图控件
		function addEditor(type, name, id) {
			var editorId = id || type + "_" + getEditorId();
			var html = '<li id='+editorId+' class="edititem"><div class="editorDiv"><ul><li class="labelli">'
					//label
					+ '<input id="'
					+ editorId
					+ '_label" class="label" type="text" value="'
					+ (name || Constants.TYPE["t" + type])
					+ '"  />&nbsp;:'
					+ '</li><li class="editorli">'
					//控件
					+ '<input class="editor type_'+Constants.TYPE['t'+type]+'" type="text" />'
					+ '</li></ul></div></li>';

			$("#sorts").append(html);
			if (type == Constants.TEXT_TYPE) {
				$("#" + editorId + " .editor").ligerTextBox();
			} else if (type == Constants.DATE_TYPE) {
				$("#" + editorId + " .editor").ligerDateEditor();
			} else if (type == Constants.HIDDEN_TYPE) {
				$("#" + editorId + " .editor").css("background-color", "#DDD");
			} else {
				$("#" + editorId + " .editor").ligerComboBox({
					disabled : true
				});
			}
			$("#" + editorId + "_label").blur(function() {
				if (currentParam.editorId == editorId) {
					$("#paramName").val($(this).val());
				}
			});
		}

		//重新渲染某个视图控件
		function changeEditor(editorId, type) {
			var html = '<input class="editor type_'+Constants.TYPE[type]+'" type="text" />';
			$("#" + editorId + " .editorli").html(html);
			if (type == Constants.TEXT_TYPE) {
				$("#" + editorId + " .editor").ligerTextBox();
			} else if (type == Constants.DATE_TYPE) {
				$("#" + editorId + " .editor").ligerDateEditor();
			} else if (type == Constants.HIDDEN_TYPE) {
				$("#" + editorId + " .editor").css("background-color", "#DDD");
			} else {
				$("#" + editorId + " .editor").ligerComboBox({
					disabled : true
				});
			}
			$("#" + editorId + "_label").blur(function() {
				if (currentParam.editorId == editorId) {
					$("#paramName").val($(this).val());
				}
			});
			//修改缓存中对应的类型
			currentParam.type = type;
			if (Cache.params[editorId]) {
				Cache.params[editorId].paramType = currentParam.type;
			}
			changeGrid(type);
		}

		//选择控件事件回调
		function selectParamBox(editorId, type) {
			currentParam.editorId = editorId;
			currentParam.type = type;
			changeGrid(type);
			param_reset(editorId);
		}

		/*-------------------       样式          ---------------------*/
		//渲染属性表中的所有下拉列表
		function ligerPropEditor(type) {
			var textEditor = [ "paramNo", "paramName", "initValue" ];
			for ( var i = 0, l = textEditor.length; i < l; i++) {
				$("#" + textEditor[i]).ligerTextBox({
					width : propEditorWidth
				});
			}
			$("#maxLen").ligerTextBox({
				digits : true,
				width : propEditorWidth
			});

			var paramType = [ {
				id : "01",
				text : "文本框"
			}, {
				id : "02",
				text : "下拉列表"
			}, {
				id : "03",
				text : "日期"
			}, {
				id : "04",
				text : "数据树"
			}, {
				id : "05",
				text : "隐藏域"
			} ];
			var valueType = [ {
				id : "01",
				text : "定值"
			}, {
				id : "02",
				text : "SQL语句"
			}, {
				id : "03",
				text : "系统参数"
			}, {
				id : "04",
				text : "数据集"
			} ];
			var dateFormat = [ {
				id : "yyyy-MM-dd",
				text : "yyyy-MM-dd"
			}, {
				id : "yyyy/MM/dd",
				text : "yyyy/MM/dd"
			}, {
				id : "yyyy.MM.dd",
				text : "yyyy.MM.dd"
			}, {
				id : "yyyyMMdd",
				text : "yyyyMMdd"
			} ];
			var selectType = [ {
				id : "01",
				text : "单选"
			}, {
				id : "02",
				text : "多选"
			}, {
				id : "03",
				text : "级联多选"
			} ];
			var TOF = [ {
				text : "是",
				id : "1"
			}, {
				text : "否",
				id : "0"
			} ];

			$("#paramType_box").ligerComboBox({
				valueFieldID : "paramType",
				resize : false,
				width : propEditorWidth,
				data : paramType,
				onSelected : selectParamType
			});
			$("#valueType_box").ligerComboBox({
				valueFieldID : "valueType",
				resize : false,
				width : propEditorWidth,
				data : valueType,
				onSelected : selectValueType
			});
			$("#isReadonly_box").ligerComboBox({
				valueFieldID : "isReadonly",
				resize : false,
				width : propEditorWidth,
				data : TOF
			});
			$("#isNotnull_box").ligerComboBox({
				valueFieldID : "isNotnull",
				resize : false,
				width : propEditorWidth,
				data : TOF
			});
			$("#dateFormat_box").ligerComboBox({
				valueFieldID : "dateFormat",
				resize : false,
				width : propEditorWidth,
				data : dateFormat
			});
			$("#selectType_box").ligerComboBox({
				valueFieldID : "selectType",
				resize : false,
				width : propEditorWidth,
				data : selectType
			});
			$("#rootIssel_box").ligerComboBox({
				valueFieldID : "rootIssel",
				resize : false,
				width : propEditorWidth,
				data : TOF
			});

			//下拉框值类型为系统参数时
			window.parent.hiddenIdBox = $("#hiddenId");
			window.parent.combSourceBox = $("#combSource");
			window.parent.treeSourceBox = $("#treeSource");
			$("#combSource")
					.focus(
							function() {
								if ($("#valueType").val() == "02") {
									if ($("#combSource").css("color")
											.toUpperCase() == "#A0A0A0"
											&& $("#combSource").val() == "select id,text from tables") {
										$("#combSource").val("");
									}
									$("#combSource").css("color", "#000000");
								}
								if ($("#valueType").val() == "03") {
									window.parent.BIONE
											.commonOpenDialog("系统参数",
													"selectBox", "600", "380",
													"${ctx}/report/frame/param/templates/sysparamPage");
								}
							});
			$("#combSource").blur(
					function() {
						if ($("#valueType").val() == "02") {
							if ($.trim($("#combSource").val()) == "") {
								$("#combSource").val(
										"select id,text from tables").css(
										"color", "#A0A0A0");
							} else {
								$("#combSource").css("color", "#000000");
							}
						}
					});
			$("#treeSource")
					.focus(
							function() {
								if ($("#valueType").val() == "02") {
									if ($("#combSource").css("color")
											.toUpperCase() == "#A0A0A0"
											&& $("#treeSource").val() == "select id,text,upId from tables") {
										$("#treeSource").val("");
									}
									$("#treeSource").css("color", "#000000");
								}
								if ($("#valueType").val() == "04") {
									window.parent.BIONE
											.commonOpenDialog("数据集",
													"selectBox", "600", "380",
													"${ctx}/report/frame/param/templates/datasetPage");
								}
							});
			$("#treeSource").blur(
					function() {
						if ($("#valueType").val() == "02") {
							if ($.trim($("#treeSource").val()) == "") {
								$("#treeSource").val(
										"select id,text,upId from tables").css(
										"color", "#A0A0A0");
							} else {
								$("#treeSource").css("color", "#000000");
							}
						}
					});
		}

		//改变属性表结构
		function changeGrid(type) {
			var shows = null;
			var hides = null;

			switch (type) {
			case Constants.TEXT_TYPE:
				shows = [ "initValue", "maxLen" ], hides = [ "valueType",
						"dateFormat", "selectType", "rootIssel", "combSource",
						"treeSource" ];
				break;
			case Constants.COMB_TYPE:
				shows = [ "valueType", "combSource" ], hides = [ "maxLen",
						"selectType", "rootIssel", "dateFormat", "treeSource",
						"initValue" ];
				break;
			case Constants.DATE_TYPE:
				shows = [ "dateFormat" ], hides = [ "valueType", "maxLen",
						"selectType", "rootIssel", "combSource", "treeSource",
						"initValue" ];
				break;
			case Constants.TREE_TYPE:
						shows = [ "valueType", "selectType", "rootIssel",
								"treeSource" ], hides = [ "dateFormat",
								"maxLen", "combSource", "initValue" ];
				break;
			default:
				shows = [], hides = [ "initValue", "valueType", "selectType",
						"rootIssel", "dateFormat", "maxLen", "combSource",
						"treeSource" ];
				break;
			}
			if (shows && shows.length > 0) {
				for ( var i = 0, l = shows.length; i < l; i++) {
					$("#" + shows[i]).parents("tr").show();
				}
			}
			if (hides && hides.length > 0) {
				for ( var i = 0, l = hides.length; i < l; i++) {
					$("#" + hides[i]).parents("tr").hide();
				}
			}

			var vt = $.ligerui.get('valueType_box');
			if (type == Constants.COMB_TYPE) {
				vt.setData([ {
					id : "01",
					text : "定值"
				}, {
					id : "02",
					text : "SQL语句"
				}, {
					id : "03",
					text : "系统参数"
				} ]);
			} else if (type == Constants.TREE_TYPE) {
				vt.setData([ {
					id : "01",
					text : "定值"
				}, {
					id : "02",
					text : "SQL语句"
				}, {
					id : "04",
					text : "数据集"
				} ]);
			} else {
				vt.setData([ {
					id : "",
					text : ""
				} ]);
			}
		}

		//获取文本框html
		function getText(name, value) {
			return "<input id='" + name + "' name='" + name
					+ "' type='text' class='cert' value='" + (value || "")
					+ "' />";
		}

		//修改参数控件类型回调
		function selectParamType(id, text) {
			if (currentParam.editorId && currentParam.type
					&& currentParam.type != id) {
				changeEditor(currentParam.editorId, id);
			}
		}

		//修改值类型回调
		function selectValueType(id, text) {
			if (id != currentParam.valueType) {
				if (id == "02") {
					if (isEmpty($("#combSource").val()))
						$("#combSource").val("select id,text from tables").css(
								"color", "#A0A0A0");
					if (isEmpty($("#treeSource").val()))
						$("#treeSource").val("select id,text,upId from tables")
								.css("color", "#A0A0A0");
				} else if (id == "03" || id == "04") {
					$("#combSource").val("").css("color", "#000000");
					$("#treeSource").val("").css("color", "#000000");
				} else {
					$("#combSource").css("color", "#000000");
					$("#treeSource").css("color", "#000000");
				}
			}
			currentParam.valueType = id;
		}

		//获取控件类型
		function getEditorType(eidtorId) {
			var ed = $("#" + eidtorId + " .editor");
			if (ed && ed.attr("class")) {
				var t = ed.attr("class");
				if (t.indexOf("type_text") >= 0)
					return Constants.TEXT_TYPE;
				else if (t.indexOf("type_comb") >= 0)
					return Constants.COMB_TYPE;
				else if (t.indexOf("type_date") >= 0)
					return Constants.DATE_TYPE;
				else if (t.indexOf("type_tree") >= 0)
					return Constants.TREE_TYPE;
				else
					return Constants.HIDDEN_TYPE;
			}
		}

		/*-------------------       保存          ---------------------*/
		//保存参数属性
		function param_save() {
			if (!currentParam.editorId) {
				BIONE.tip("未选择任何参数控件。");
				return;
			}
			var cp = currentParam;
			var cSrc = $.trim($("#combSource").val());
			var tSrc = $.trim($("#treeSource").val());
			if (($("#combSource").css("color").toUpperCase() == "#A0A0A0")
					|| cSrc == "select id,text from tables") {
				cSrc = "";
			}
			if (($("#treeSource").css("color").toUpperCase() == "#A0A0A0")
					|| tSrc == "select id,text,upId from tables") {
				tSrc = "";
			}
			cSrc = encodeURIComponent(cSrc);
			tSrc = encodeURIComponent(tSrc);

			var param = {
				editorId : cp.editorId,
				orderNum : "",
				combSource : cp.type == Constants.COMB_TYPE ? cSrc : "",
				dateFormat : cp.type == Constants.DATE_TYPE ? $("#dateFormat")
						.val() : "",
				initValue : (cp.type == Constants.TEXT_TYPE || cp.type == Constants.HIDDEN_TYPE) ? $(
						"#initValue").val()
						: "",
				isNotnull : $("#isNotnull").val(),
				isReadonly : $("#isReadonly").val(),
				maxLen : cp.type == Constants.TEXT_TYPE ? $("#maxLen").val()
						: "",
				paramName : $("#paramName").val(),
				paramNo : $("#paramNo").val(),
				paramType : $("#paramType").val(),
				remark : $("#remark").val(),
				rootIssel : cp.type == Constants.TREE_TYPE ? $("#rootIssel")
						.val() : "",
				selectType : cp.type == Constants.TREE_TYPE ? $("#selectType")
						.val() : "",
				treeSource : cp.type == Constants.TREE_TYPE ? tSrc : "",
				valueType : $("#valueType").val(),
				hiddenId : $("#hiddenId").val()
			};
			if (checkParam(param)) {
				Cache.params[cp.editorId] = param;
				$("#" + param.editorId + "_label").val(param.paramName);//控件视图中修改label
				BIONE.tip("参数属性保存成功。");
			}
		}

		//使用缓存中填充参数属性
		function param_reset() {

			var editorId = currentParam.editorId;
			var p = Cache.params[currentParam.editorId];
			var pName = $("#" + editorId + "_label").val();
			if (!!p) {
				//public
				$("#paramNo").val(p.paramNo);
				$("#paramName").val(pName);
				$.ligerui.get('paramType_box').selectValue(p.paramType);
				$("#initValue").val(p.initValue);
				$.ligerui.get('valueType_box').selectValue(p.valueType);
				$.ligerui.get('isNotnull_box').selectValue(p.isNotnull);
				$.ligerui.get('isReadonly_box').selectValue(p.isReadonly);
				$("#remark").val(p.remark);
				//text
				$("#maxLen").val(p.maxLen);
				//comb
				$("#combSource").val(decodeURIComponent(p.combSource));
				//date
				$.ligerui.get('dateFormat_box').selectValue(p.dateFormat);
				//tree
				$.ligerui.get('rootIssel_box').selectValue(p.rootIssel);
				$.ligerui.get('selectType_box').selectValue(p.selectType);
				$("#treeSource").val(decodeURIComponent(p.treeSource));
				$("#hiddenId").val(p.hiddenId);
				$("#combSource").blur();
				$("#treeSource").blur();
			} else {
				//public
				$("#paramNo").val("");
				$("#paramName").val(pName);
				$.ligerui.get('paramType_box').selectValue(
						editorId ? getEditorType(editorId) : "");
				$("#initValue").val("");
				$.ligerui.get('valueType_box').selectValue("01");
				$.ligerui.get('isNotnull_box').selectValue("0");
				$.ligerui.get('isReadonly_box').selectValue("0");
				$("#remark").val("");
				//text
				$("#maxLen").val("");
				//comb
				$("#combSource").val("");
				//date
				$.ligerui.get('dateFormat_box').selectValue("yyyy-mm-dd");
				//tree
				$.ligerui.get('rootIssel_box').selectValue("1");
				$.ligerui.get('selectType_box').selectValue("01");
				$("#treeSource").val("");
				$("#hiddenId").val("");
			}
			propertiesAble((!!editorId));
		}

		//保存模板
		function tmp_save() {
			hideError();
			var paramtmpName = $("#paramtmpName").val();
			var remark = $("#paramtmpRemark").val();
			if (isEmpty(paramtmpName)) {
				$("#paramtmpName").parent().addClass("l-text-invalid");
				BIONE.tip("模板名称不能为空。");
				return;
			}
			var sorts = $("#sorts").sortable('toArray');

			//temp
			var ct = Cache.temp;
			var paramtmpId = ct.paramtmpId;
			var catalogId = ct.catalogId;
			var ss = {};
			for ( var i = 0, l = sorts.length; i < l; i++) {
				ss[sorts[i]] = i + 1;
			}

			var cps = Cache.params;
			var params = [];
			for ( var i in cps) {
				if (!cps[i]) {
					continue;
				}
				var p = cps[i];
				delete p.editorId;
				p.orderNum = ss[i];
				params.push(p);
			}
			if (params && params.length == 0) {
				BIONE.tip("模板中至少配置一个参数。");
				return;
			}

			var paramsJsonStr = '{params:' + JSON2.stringify(params) + '}';
			$.ajax({
				type : "POST",
				url : "${ctx}/report/frame/param/templates/tmpNameCanUse",
				data : {
					paramtmpId : paramtmpId,
					catalogId : catalogId,
					paramtmpName : paramtmpName
				},
				success : function(canUse) {
					if (canUse) {
						$.ajax({
							type : "POST",
							url : "${ctx}/report/frame/param/templates/save",
							data : {
								paramtmpId : paramtmpId,
								catalogId : catalogId,
								paramtmpName : paramtmpName,
								remark : remark,
								paramsJsonStr : paramsJsonStr
							},
							success : function(paramtmpId) {
								BIONE.tip("参数模板保存成功！");
								//刷新grid
								window.parent.tempGrid.loadData();
								Cache.temp.paramtmpId = paramtmpId;
								$($(".l-dialog-btn").get(4)).show();
							},
							error : function(XMLHttpRequest, textStatus,
									errorThrown) {
								BIONE.tip('保存失败,错误信息:' + textStatus);
							}
						});
					} else {
						$("#paramtmpName").parent().addClass("l-text-invalid");
						BIONE.tip("该目录下已存在此名称模板。");
					}
				},
				error : function(XMLHttpRequest, textStatus, errorThrown) {
					BIONE.tip('错误信息:' + textStatus);
				}
			});
		}

		//
		//验证
		//
		//属性合法验证
		function checkParam(param) {
			hideError();
			var errorMsg = "";
			var editors = [];
			var flag = true;
			var type = param.paramType;

			//公共属性验证
			if (isEmpty(param.paramNo)) {
				errorMsg += "参数标识不能为空;";
				editors.push("paramNo");
				flag = false;
			}
			for ( var id in Cache.params) {
				if (id != param.editorId
						&& Cache.params[id].paramNo == param.paramNo) {
					errorMsg += "模板中已存在此参数标识;";
					editors.push("paramNo");
					flag = false;
					break;
				}
			}
			if (isEmpty(param.paramName)) {
				errorMsg += "参数名称不能为空;";
				editors.push("paramName");
				flag = false;
			}

			//类型属性验证
			//comb
			if (type == Constants.COMB_TYPE) {
				if (isEmpty(param.combSource)) {
					errorMsg += "下拉框数据源不能为空;";
					editors.push("combSource");
					flag = false;
				}
				if (param.valueType == "01") {
					try {
						jQuery.parseJSON(decodeURIComponent(param.combSource));
					} catch (e) {
						errorMsg += "请填入标准的JSON字符串;";
						editors.push("combSource");
						flag = false;
					}
				}
			}

			//tree
			if (type == Constants.TREE_TYPE) {
				if (isEmpty(param.treeSource)) {
					errorMsg += "下拉框数据源不能为空;";
					editors.push("treeSource");
					flag = false;
				}
				if (param.valueType == "01") {
					try {
						jQuery.parseJSON(decodeURIComponent(param.treeSource));
					} catch (e) {
						errorMsg += "请填入标准的JSON字符串;";
						editors.push("treeSource");
						flag = false;
					}
				}
			}

			//SQL校验
			var simpleChecker = /^select[\s][^;]+[\s]from[\s][^;]+$/;
			//var idChecker = /^[\s]*\b[^;\s]+\b[\s]+(as[\s]+id|[\s]*id)[\s]*$/;
			//var upIdChecker = /^[\s]*\b[^;\s]+\b[\s]+(as[\s]+upid|[\s]*upid)[\s]*$/;
			//var textChecker = /^[\s]*\b[^;\s]+\b[\s]+(as[\s]+text|[\s]*text)[\s]*$/;
			var columnChecker = /^[\s]*\b[^;\s]+\b(|[\s]+as[\s]+\b[^;\s]+\b|[\s]+\b[^;\s]+\b)[\s]*$/;
			if (param.valueType == "02") {
				var isComb = (type == Constants.COMB_TYPE);
				var editorName = (isComb ? "combSource" : "treeSource");
				sql = decodeURIComponent(isComb ? param.combSource
						: param.treeSource);
				sql = sql.toLowerCase();
				if (!simpleChecker.test(sql)) {
					errorMsg += "请填入标准的SQL字符串;";
					editors.push(editorName);
					flag = false;
				} else {
					sql = sql.substring(0, sql.indexOf(" from "));
					sql = sql.substring(sql.indexOf("select") + 6, sql.length);
					sql = $.trim(sql);
					var items = sql.split(",");

					if (isComb) {
						var res = false;
						if (!items || items.length != 2) {
							res = false;
						} else {
							if (columnChecker.test(items[0])
									&& columnChecker.test(items[1])) {
								res = true;
							}
						}
						if (!res) {
							errorMsg += "SQL查询项应为两项，顺序为[id],[text];";
							editors.push(editorName);
							flag = false;
						}
					} else {
						var res = false;
						if (!items || items.length != 3) {
							res = false;
						} else {
							if (columnChecker.test(items[0])
									&& columnChecker.test(items[1])
									&& columnChecker.test(items[2])) {
								{
									res = true;
								}
							}
						}
						if (!res) {
							errorMsg += "SQL查询项应为三项，顺序为[id],[text],[upId];";
							editors.push(editorName);
							flag = false;
						}
					}
				}
			}

			if (!flag) {
				showError(errorMsg, editors);
			}
			return flag;
		}

		//预览
		function tmp_preview() {
			if (!!Cache.temp.paramtmpId) {
				dialog = window.parent.BIONE.commonOpenLargeDialog('',
						'preview',
						'${ctx}/report/frame/param/generate/generateTmp?paramtmpId='
								+ Cache.temp.paramtmpId);
			} else {
				BIONE.tip("请先保存模板再预览。");
			}

		}

		/*-------------------       UTILS         ---------------------*/

		//属性grid可用
		function propertiesAble(flag) {
			if (flag) {
				$("#paramNo").removeAttr("disabled");
				$("#paramName").removeAttr("disabled");
				$("#remark").removeAttr("disabled");
				$.ligerui.get("paramType_box").options.disabled = false;
				$.ligerui.get("isReadonly_box").options.disabled = false;
				$.ligerui.get("isNotnull_box").options.disabled = false;
			} else {
				$("#paramNo").attr("disabled", "true");
				$("#paramName").attr("disabled", "true");
				$("#remark").attr("disabled", "true");
				$.ligerui.get("paramType_box").options.disabled = true;
				$.ligerui.get("isReadonly_box").options.disabled = true;
				$.ligerui.get("isNotnull_box").options.disabled = true;
			}
		}

		//错误提示
		function showError(msg, editors) {
			if (editors && editors.length > 0) {
				for ( var i = 0, l = editors.length; i < l; i++) {
					//页面上给出指定标签invalid样式
					var tag = $("[name='" + editors[i] + "']").get(0).tagName
							.toUpperCase();
					if (tag == "INPUT") {
						//若是文本框，找上一个dom
						$("[name='" + editors[i] + "']").parent().addClass(
								"l-text-invalid");
					} else if (tag == "TEXTAREA") {
						$("[name='" + editors[i] + "']").addClass(
								"l-text-invalid");
					}
				}
			}
			if (msg && msg != "") {
				BIONE.tip(msg);
			}
		}

		//错误样式重置
		function hideError() {
			var editors = [ "paramtmpName", "paramNo", "paramName",
					"combSource", "maxLen", "selectType", "rootIssel",
					"dateFormat", "treeSource" ];
			for ( var i = 0, l = editors.length; i < l; i++) {
				//页面上隐藏标签invalid样式
				var tag = $("[name='" + editors[i] + "']").get(0).tagName
						.toUpperCase();
				if (tag == "INPUT") {
					//若是文本框，找上一个dom
					$("[name='" + editors[i] + "']").parent().removeClass(
							"l-text-invalid");
				} else if (tag == "TEXTAREA") {
					$("[name='" + editors[i] + "']").removeClass(
							"l-text-invalid");
				}
			}
		}

		//判断非空
		function isEmpty(str) {
			if (!!str && str.length > 0)
				return false;
			return true;
		}

	});
</script>
</head>
<body>
	<div id="template.center">
		<div id="tempframe">
			<div position="left" style="background-color: #F1F1F1;">
				<ul id="edits" style="font-size: 12; width: 92%; height: 400px;"
					class="ztree"></ul>
			</div>
			<div id="centerBox" position="center"
				style="background-color: #F1F1F1;">
				<div id="centerHead" style="height: 40px;">
					<ul>
						<li
							style="text-align: left; float: left; line-height: 35px; margin-left: 10px;">模板名称：</li>
						<li style="text-align: left; float: left; padding-top: 7px;">
							<input id="paramtmpName" name="paramtmpName"
							style="width: 200px;" />
						</li>
					</ul>
				</div>
				<div style="width: 100%; height: 50px; margin-top: -5px;">
					<ul>
						<li
							style="text-align: left; float: left; line-height: 35px; margin-left: 10px;">备注：</li>
						<li style="text-align: left; float: left; line-height: 35px;">
							<textarea id="paramtmpRemark" name="paramtmpRemark"
								style="resize: none; height: 40px; width: 350px; margin-left: 24px;"
								class='l-textarea'></textarea>
						</li>
					</ul>
				</div>

				<div style="width: 100%; height: 1px; background-color: #D0D0D0;"></div>
				<div id="dropDiv" style="height: 100%;">
					<div id="sortsHead" style="height: 30px;">
						<ul style="float: left;">
							<li style="float: left; line-height: 30px;">
								<div id="removeParam" class="l-dialog-btn"
									style="float: left; margin: 3px 0px 0px 8px;">
									<div class="l-dialog-btn-l"></div>
									<div class="l-dialog-btn-r"></div>
									<div class="l-dialog-btn-inner">移除参数</div>
								</div>
							</li>
							<li style="float: left; line-height: 30px;">
								<div id="removeAllParam" class="l-dialog-btn"
									style="float: left; margin: 3px 0px 0px 8px;">
									<div class="l-dialog-btn-l"></div>
									<div class="l-dialog-btn-r"></div>
									<div class="l-dialog-btn-inner">移除全部</div>
								</div>
							</li>
						</ul>
					</div>
					<div id="sortsDiv"
						style="width: 100%; height: 76.5%; overflow: auto; position: relative;">
						<ul id="sorts">
						</ul>
					</div>
				</div>
			</div>
			<div position="right" style="background-color: #F1F1F1;">
				<div id="maingrid" style="background-color: #D0D0D0;"></div>
			</div>
			<div position="bottom"
				style="background-color: #F1F1F1; height: 100px;">
				<div class="form-bar"
					style="padding: 5px 0px 5px 0px; background-color: #F1F1F1;">
					<div class="form-bar-inner" style="background-color: #F1F1F1;"></div>
				</div>
			</div>
		</div>
	</div>
</body>
</html>