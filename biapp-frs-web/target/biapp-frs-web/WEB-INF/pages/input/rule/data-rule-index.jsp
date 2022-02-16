<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<%@ include file="/common/taglibs.jsp"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<%@ include file="/common/meta.jsp"%>
<meta name="decorator" content="/template/template2.jsp">
<script type="text/javascript" src="${ctx}/js/udip/UDIP.js"></script>
<script type="text/javascript">
	var items, grid, ids = [];
	var templeId,templeUrl;
	var currentNode;
	var selection = [ {
		id : "",
		text : "请选择"
	} ];
	var libData = {
		Rows : []
	};
	$(function() {
		//设置规则类型下拉框的值
		$.ajax({
			async : false,
			url : '${ctx}/rpt/input/rule/dataRulesCombox',
			success : function(data) {
				selection = data;
			}
		});
		if(!"${lookType}"){
			initTree();
			/*补录规则功能中不维护补录模板 20190612
			$("#treeToolbar").ligerTreeToolBar({
				items : [ {
					icon : 'communication',
					text : '目录管理',
					click : dirMananer
				} ]
			});
			*/
			searchForm();
			initGrid();
		}else{
			searchForm();
			initGrid();
			grid.set('url', "${ctx}/rpt/input/rule/list.json?templeId=${templeId}");
		}
		
		initButtons();
		BIONE.addSearchButtons("#search", grid, "#searchbtn");
		var managers = $("#maingrid").ligerGetGridManager();
	});

	function initTree() {
		var dirType = 2;
		window['orgTreeInfo'] = $.fn.zTree.init($("#tree"),
				{
					async : {
						enable : true,
						contentType : "application/json",
						url : "${ctx}/rpt/input/temple/templeTree.json",
						dataType : "json",
						type : "post"
					},
					data : {
						keep : {
							parent : true
						},
						key : {
							name : "text"
						},
						simpleData : {
							enable : true,
							idKey : "id",
							pIdKey : "upId",
							rootPId : null
						}
					},
					view : {
						showLine : false
					},
					callback : {
						onClick : function(event, treeId, treeNode) {
							templeId = treeNode.id;
							currentNode = treeNode;
							grid.set('newPage', 1);
							grid.set('url', "${ctx}/rpt/input/rule/list.json?templeId="+templeId);
							templeUrl = "${ctx}/rpt/input/rule/list.json?"+ (templeId == null ? "d=" : ('templeId='+ templeId+"&d="));
						}
					
					}
				});
	}
	function dirMananer() {
		BIONE.commonOpenDialog("模板目录管理", "dirmanager", 800, 450,
				"${ctx}/rpt/input/catalog/1", null);
	}

	function searchForm() {
		$("#search").ligerForm({
			fields : [ {
				display : "模板规则",
				name : "ruleType",
				comboboxName: "ruleNm",
				newline : true,
				type : 'select',
				options : {
					data : selection
				},
				attr : {
					field : 'temple.ruleType',
					op : "="
				}
			} ]
		});
	}

	function initGrid() {
		grid = $("#maingrid").ligerGrid({
			width : '100%',
			height : '99%',
			columns : [ {
				hide : true,
				name : 'ruleId',
				width : "0.5%" 
			}, {
				isSort:true,
				display : '规则名称',
				name : 'ruleNm',
				width : "15%",
				align : 'left'
			}, {
				isSort:true,
				display : "规则类型",
				name : "ruleType",
				width : '15%',
				minWidth : '15%',
				editor : {
					type : 'select',
					data : selection
				},
				render : function(row) {
					switch (row.ruleType) {
					case "1":
						return "数据值范围";
					case "2":
						return "正则表达式";
					case "3":
						return "表内横向校验";
					case "4":
						return "表内纵向校验";
					case "5":
						return "表间一致校验";
					}
				}
			}, {
				isSort:true,
				display : '校验字段',
				name : 'fieldNm',
				width : '15%',
				align : 'center'
			}, {
				isSort:true,
				display : '修改人',
				name : 'createUser',
				width : '10%',
				align : 'center'
			}, {
				isSort:true,
				display : '修改时间',
				name : 'createDate',
				width : '20%',
				align : 'center'
			} ],
			checkbox : true,
			dataAction : 'server', //从后台获取数据
			usePager : true, //服务器分页
			alternatingRow : true, //附加奇偶行效果行
			colDraggable : true,
			isScroll : true,
			method : 'post',
			url : "${ctx}/rpt/input/rule/list.json?templeId=${templeId}",
			onDblClickRow : function (data, rowindex, rowobj){
				getSelectRow(templeId,data.ruleId,data.ruleType);
				grid.reRender();
            },
			data : libData,
			sortName : 'ruleNm',//第一次默认排序的字段
			sortOrder : 'asc', //排序的方式
			rownumbers : true,
			toolbar : {}
		});
	}

	function initButtons() {
		if(!"${lookType}"){
			items = [ {
				text : '添加',
				click : library_add,
				icon : 'fa-plus'
			},{
				text : '修改',
				click : library_update,
				icon : 'icon-modify'
			}, {
				text : '删除',
				click : library_delete,
				icon : 'icon-delete'
			} ];
		}else{
			items = [ {
				text : '查看',
				click : library_look,
				icon : 'icon-search'
			} ];
		}
		
		BIONE.loadToolbar(grid, items, function() {
		});

	}

	// 修改
	function library_update() {
		chackType = achieveIds();
		var id = templeId;
		if (ids.length == 1) {
			getSelectRow(id,ids[0],chackType)
		} else {
			BIONE.tip("只能选择一行进行修改");
			return;
		}
		grid.reRender();
	}
	function library_look(){
		chackType = achieveIds();
		var id = templeId;
		if (ids.length == 1 && chackType == 1) {
			BIONE.commonOpenDialog(ruleNm,
					"dataRules", "575", "400",
					"${ctx}/rpt/input/rule/rule1?id=" + id+"&lookType=lookType&d="+new Date().getTime() );
		} else if (ids.length == 1 && chackType == 2) {
			BIONE.commonOpenDialog(ruleNm,
					"dataRules", "550", "380",
					"${ctx}/rpt/input/rule/rule2?id=" + id+"&lookType=lookType&d="+new Date().getTime() );
		} else if (ids.length == 1 && chackType == 3) {
			BIONE.commonOpenDialog(ruleNm,
					"dataRules", "1000", "450",
					"${ctx}/rpt/input/rule/rule3?id=" + id+"&lookType=lookType&d="+new Date().getTime() );
		} else if (ids.length == 1 && chackType == 4) {
			BIONE.commonOpenDialog(ruleNm,
					"dataRules", "1000", "450",
					"${ctx}/rpt/input/rule/rule4?id=" + id+"&lookType=lookType&d="+new Date().getTime() );
		} else if (ids.length == 1 && chackType == 5) {
			BIONE.commonOpenDialog(ruleNm,
					"dataRules", "1000", "450",
					"${ctx}/rpt/input/rule/rule5?id=" + id+"&lookType=lookType&d="+new Date().getTime() );
		}else{
			BIONE.tip("请选择规则类型！");
		}
	}
	function library_add(){
		//修改补录规则类型位置,添加规则只要选择左侧模板，进入添加页面后再选择规则类型 20190605
		if(!currentNode || currentNode.isParent == true){
			BIONE.tip("请选择左侧模板！");
			return;
		}
		chacktempleId = templeId;
		if (chacktempleId) {
			BIONE.commonOpenDialog("补录规则","dataRules", $(document).width()-50, $(document).height()-30,"${ctx}/rpt/input/rule/rule?id=" + chacktempleId +"&d="+new Date().getTime());
		}		
					
		/*
		chacktempleId = templeId;
		chackruleType =$('#ruleType').val();
		ruleNm=$('#ruleNm').val();
		if (chacktempleId == "" || chacktempleId == null) {
			BIONE.tip('请选择模板。');
			return;
		}
		
		if (chackruleType == "" || chackruleType == null) {
			BIONE.tip('请选择规则。');
			return;
		}
		
		if (chackruleType == 1) {
			BIONE.commonOpenDialog(ruleNm,
					"dataRules", "575", "400",
					"${ctx}/rpt/input/rule/rule1?id=" + chacktempleId +"&d="+new Date() );
		} else if (chackruleType == 2) {
			BIONE.commonOpenDialog(ruleNm,
					"dataRules", "550", "380",
					"${ctx}/rpt/input/rule/rule2?id=" + chacktempleId +"&d="+new Date() );
		} else if (chackruleType == 3) {
			BIONE.commonOpenDialog(ruleNm,
					"dataRules", "1000", "450",
					"${ctx}/rpt/input/rule/rule3?id=" + chacktempleId +"&d="+new Date() );
		} else if (chackruleType == 4) {
			BIONE.commonOpenDialog(ruleNm,
					"dataRules", "1000", "450",
					"${ctx}/rpt/input/rule/rule4?id=" + chacktempleId +"&d="+new Date() );
		} else if (chackruleType == 5) {
			BIONE.commonOpenDialog(ruleNm,
					"dataRules", "1000", "450",
					"${ctx}/rpt/input/rule/rule5?id=" + chacktempleId +"&d="+new Date());
		}else{
			BIONE.tip("请选择规则类型！");
		}
		*/
	}

	// 删除
	function library_delete() {
		chackType = achieveIds();
		if (ids.length > 0) {
			$.ligerDialog.confirm('确实要删除这些记录吗?', function(yes) {
				if (yes) {
					$.ajax({
						type : "POST",
						url : "${ctx}/rpt/input/rule/deleteRule/" + ids,
						dataType : "text",
						success : function(text) {
							BIONE.tip(text);
							/* grid.set('url', templeUrl); */
							grid.loadData();
						}
					});
				}
			});
		}else{
			BIONE.tip("请选择删除的规则！");
		}
	}

	function f_reload() {
		var manager = $("#maingrid").ligerGetGridManager();
		manager.loadData();
	}

	// 获取选中的行
	function achieveIds() {
		ids = [];
		var ruleType;
		var rows = grid.getSelectedRows();
		for ( var i in rows) {
			ids.push(rows[i].ruleId);
			ruleType = rows[i].ruleType;
		}
		return ruleType;
	}
	
	//子页面或者地址url
	function getTempleUrl(){
		return templeUrl;
	}
	function getSelectRow(id,ruleId,chackType){
		var width = $(document).width()-50;
		var height = $(document).height()-30;
		if (chackType == 1) {
			BIONE.commonOpenDialog("数值规则修改", "dataRules", width, height,
					"${ctx}/rpt/input/rule/rule1?id=" + id + "&ruleId=" + ruleId+"&d="+ new Date().getTime());
		} else if ( chackType == 2) {
			BIONE.commonOpenDialog("正则规则修改", "dataRules", width, height,
					"${ctx}/rpt/input/rule/rule2?id=" + id + "&ruleId=" + ruleId+"&d="+ new Date().getTime());
		} else if (chackType == 3) {
			BIONE.commonOpenDialog("表内横向规则修改", "dataRules",width, height,
					"${ctx}/rpt/input/rule/rule3?id=" + id + "&ruleId=" + ruleId+"&d="+ new Date().getTime());
		} else if ( chackType == 4) {
			BIONE.commonOpenDialog("表内纵向规则修改", "dataRules",width, height,
					"${ctx}/rpt/input/rule/rule4?id=" + id + "&ruleId=" + ruleId+"&d="+ new Date().getTime());
		} else if (chackType == 5) {
			BIONE.commonOpenDialog("表间一致规则修改", "dataRules",width, height,
					"${ctx}/rpt/input/rule/rule5?id=" + id + "&ruleId=" + ruleId+"&d="+ new Date().getTime());
		}
	}
</script>

<title>数据字典</title>
</head>
<body>
	<div id="template.left.up.icon">
		<img src="${ctx }/images/classics/icons/application_side_tree.png" />
	</div>
	<div id="template.left.up">
		<span style="font-size: 12">模板目录树</span>
	</div>
	<div>
		<input type="text" id="txt2" value="" />
	</div>
</body>
</html>