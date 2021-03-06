<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<%@ include file="/common/taglibs.jsp"%>
<html>
<head>
<meta name="decorator" content="/template/template2.jsp">
<script type="text/javascript" src="${ctx}/js/udip/UDIP.js"></script>
<script type="text/javascript">
	var items, grid, ids = [], dictTypes = [];
	var initDsId = '${dsId}';
	var currentNode;
	
	$(function() {
		initTree();
		addTreeToolBar();
		searchForm();
		initGrid();
		initButtons();
		BIONE.addSearchButtons("#search", grid, "#searchbtn");
		if ("${id}") {
			BIONE.createButton({
				appendTo : "#searchbtn",
				text : '选择',
				icon : 'true',
				click : function() {
					var selectedRow = grid.getSelecteds();
					if (selectedRow.length == 0) {
						BIONE.tip('请选择行');
						return;
					}
					var libId = [],dictName;
					if (selectedRow.length == 1) {
						libId = selectedRow[0].dictId;
						dictName = selectedRow[0].dictName;
					} else if (selectedRow.length > 1) {
						BIONE.tip('只能选择一个字典。');
						return;
					}
					var libId2 = libId;
					if(parent)
						parent.setDataZidina(libId2, "${rowid}",dictName);
					BIONE.closeDialog("chackedZidian");
				}
			});
		}
	});

	function initTree() {
		taskTree = $.fn.zTree.init($("#tree"), {
			async : {
				enable : true,
				contentType : "application/json",
				url : "${ctx}/rpt/input/library/libTree.json?d=" + new Date().getTime(),
				autoParam:["id=nodeId"],
				dataType : "json",
				type : "GET"
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
					//taskTree.expandNode(treeNode);
					var dirId = treeNode.id;
					grid.set('newPage', 1);
					grid.set('url', '${ctx}/rpt/input/library/list.json?'
							+ (dirId == null ? "" : 'dirId=' + dirId));
					currentNode = treeNode;
				}
			}
		});
	}
	function addTreeToolBar(){
		/*
		$("#treeToolbar").ligerTreeToolBar({
			items : [ {
				icon : 'communication',
				text : '目录管理',
				click : dirMananer,
				operNo : 'directory-management'
			} ]
		});
		*/
		$("#treeToolbar").ligerTreeToolBar({
			items:[{
				icon:'config',
				text:'目录维护',
				operNo : 'taskCatalog',
				menu:{
					width : 90,
					items : [ {
						icon : 'add',
						text : '新建',
						click : addIdxCatalog
					}, {
						line : true
					}, {
						icon : 'modify',
						text : '修改',
						click : updateIdxCatalog
					},{
						icon : 'delete',
						text : '删除',
						click : deleteIdxCatalog
					}, {
						line : true
					}]
				}
			}]
		});
	}
	function dirMananer() {
		BIONE.commonOpenDialog("字典目录管理", "dirmanager", 800, 450,
				"${ctx}/rpt/input/catalog/2", null);
	}
	function  addIdxCatalog(){
		var id,name ;
		if (!currentNode) {
			BIONE.tip("请选择目录");
			return;
		} else{
			upNo = currentNode.id;
			upName = currentNode.text;
		}
		var modelDialogUri = "${ctx}/rpt/input/catalog/newDictCatalog?catalogType=2&upNo=" + upNo+"&upName="+encodeURI(encodeURI(upName));
		BIONE.commonOpenDialog("目录添加", "inputEditCatalog","660","320",modelDialogUri);
	}
	function  updateIdxCatalog(){
		
		var id,upName;
		if (!currentNode) {
			BIONE.tip("请选择目录");
			return;
		}else {
			parentNode = currentNode.getParentNode();
			if(parentNode ==null)
			 {
				upName = "根目录";
				BIONE.tip("根目录不允许修改！");
				return;
			}else{
				id=currentNode.id;
				upName = parentNode.text;
			}
		}
		var modelDialogUri = "${ctx}/rpt/input/catalog/updateDictCatalog?catalogType=2&catalogId=" + id+"&upName="+ encodeURI(encodeURI(upName));
		BIONE.commonOpenDialog("目录修改", "inputEditCatalog","660","320",modelDialogUri);
	}
	function deleteIdxCatalog() {
		
		if (!currentNode) {
			BIONE.tip("请选择目录");
			return;
		}else {
			parentNode = currentNode.getParentNode();
			if(parentNode ==null)
			 {
				upName = "根目录";
				BIONE.tip("根目录不允许删除！");
				return;
			}
		}
		
		$.ajax({
			type : "POST",
			url : '${ctx}/rpt/input/catalog/canDeleteCatalog?catalogType=2&catalogId=' + currentNode.id,
			success : function(result) {
				if(result == 0){
					BIONE.tip("此目录下有字典，不能进行删除操作！");
				}else if(result == 1){
					taskTree.reAsyncChildNodes(currentNode.getParentNode(), "refresh");
					BIONE.tip("删除成功！");
					currentNode = null;
				}
			}
		});
	}
	function searchForm() {
		var searchForm = $("#search").ligerForm({
			fields : [ {
				display : '字典名称',
				name : "dictName",
				newline : true,
				type : "text",
				width : 130,
				cssClass : "field",
				attr : {
					field : 'dictName',
					op : "like"
				}
			}, {
				display : "字典类型",
				name : "dictType",
				newline : false,
				type : 'select',
				width : 120,
				attr : {
					field : 'dictType',
					op : "="
				},
				options : {
					valueFieldID : 'state',
					data : [ {
						text : '常量',
						id : '1'
					}, {
						text : '数据库表',
						id : '2'
					} ]
				}
			},{
				display : '数据源',
				name : "dsName",
				comboboxName : 'dsNameBox',
				newline : false,
				type : "select",
				options : {
					valueFieldID : "dsId"
				},
				attr : {
					field : 'dsId',
					op : "="
				}
			} ]
		});

		
		$.ajax({
			async : true,
			url : "${ctx}/rpt/input/table/getDatSourceList.json",
			dataType : 'json',
			type : "get",
			success : function(data) {
				$.ligerui.get('dsNameBox').setData (data);
			}
		});
		if(initDsId&&initDsId!=null&&initDsId!=""){
			$.ligerui.get("dsNameBox").setEnabled(false);

			searchForm.setData({
				dsName:initDsId
			});
		}
	}

	function initGrid() {
		var append="";
		if(initDsId&&initDsId!=null&&initDsId!="")
			append="&dsId="+initDsId;
		grid = $("#maingrid").ligerGrid({
			width : '100%',
			height : '99%',
			columns : [ {
				hide : 1,
				name : 'dictId',
				width : "1%"
			}, {
				isSort:true,
				display : '字典名称',
				name : 'dictName',
				width : "15%",
				align : 'left',
				render:function(row){
					if(top.window.clientEnv != undefined && top.window.clientEnv["isSuper"] != 'true'){
						if (!top.window["authorizedResOperNo"]
								|| ('library_mod' && $.inArray('library_mod',
										top.window["authorizedResOperNo"]) == -1)) {
							return row.dictName;
						}
					}
					return  "<a href='javascript:void(0)'   onclick='tableLinkUpdate(\""+row.dictId+"\")'>"+row.dictName+"</a>";
				}	
			}, {
				isSort:true,
				display : '字典类型',
				name : 'dictType',
				width : '8%',
				align : 'center',
				editor : {
					type : 'select',
					data : [ {
						'id' : '1',
						'text' : '常量'
					}, {
						'id' : '2',
						'text' : '数据库表'
					} ]
				},
				render : function(row) {
					switch (row.dictType) {
					case "1":
						return "常量";
					case "2":
						return "数据库表";
					}
				}
			}, {
				isSort:true,
				display : '字典内容',
				hide : true,
				name : 'sqlText',
				width : '25%',
				align : 'center'
			}, {
				isSort:true,
				display : '创建日期',
				name : 'createTime',
				width : '18%',
				align : 'center'
			}, {
				isSort:true,
				display : '创建人',
				name : 'createUser',
				width : '12%',
				align : 'center'
			} , {
				isSort:true,
				display : '数据源',
				name : 'dsName',
				width : '12%',
				align : 'center'
			}],
			checkbox : true,
			dataAction : 'server', //从后台获取数据
			usePager : true, //服务器分页
			alternatingRow : true, //附加奇偶行效果行
			colDraggable : true,
			isScroll : true,
			url : "${ctx}/rpt/input/library/list.json?t=" + new Date().getTime()+append,
			dataType : 'json',
			method : 'post',
			sortName : 'dictName',//第一次默认排序的字段
			sortOrder : 'asc', //排序的方式
			rownumbers : true,
			toolbar : {},
			isChecked : isChecked,
			onCheckRow : onCheckRow,
			onDblClickRow : function (data, rowindex, rowobj){
				openWin(data.dictId);
				grid.reRender();
            },
			onCheckAllRow : onCheckAllRow
		});
	}
	function onCheckRow(checked, rowdata, rowindex, rowDomElement) {
		if ("${id}") {
			UDIP.onCheckRow(this, checked, rowindex)
		}
	}
	function onCheckAllRow(checked, grip) {
		if ("${id}") {
			UDIP.onCheckAllRow(this);
		}
	}
	function isChecked(rowdata) {
		if ("${id}") {
			var libId = "${id}";
			var roles = libId.split(',');
			return $.inArray(rowdata.libId, roles) != -1;
		}
	}

	function initButtons() {
		items = [ {
			text : '增加',
			click : library_add,
			icon : 'fa-plus',
			operNo : 'library_add'
		}, {
			text : '修改',
			click : library_update,
			icon : 'icon-modify',
			operNo : 'library_mod'
		}, {
			text : '删除',
			click : library_delete,
			icon : 'icon-delete',
			operNo : 'library_del'
		}, {
			text : '预览数据',
			click : library_query,
			icon : 'icon-view',
			operNo : 'library_view'
		}, {
			text : '关联补录模板',
			click : library_search,
			icon : 'icon-link',
			operNo : 'library_linkTmp'
		}, {
			text : '字典模板下载',
			click : download,
			icon : 'fa-download',
			operNo : 'library_download'
		}, {
			text : '导入字典',
			click : lib_imp,
			icon : 'fa-upload',
			operNo : 'library_imp'
		} ];
		BIONE.loadToolbar(grid, items, function() { });
	}

	function download() {
		if (!document.getElementById('download')) {
			$('body').append($('<iframe id="download" style="display:none"/>'));
		}
		var rows = grid.getSelectedRows();
		if (rows.length == 1) {
// 			if (rows[0].dictType == '1') {
				$("#download").attr('src', '${ctx}/rpt/input/library/excelDownLoalDataLibInfo?dictId=' + rows[0].dictId);
// 				$("#download").attr('src', '${ctx}/rpt/input/library/excel_down?id=' + rows[0].dictId);
// 			} else {
// 				BIONE.tip('只能下载常量类型字典模板');
// 			}
		} else {
			BIONE.tip('请选择一个字典进行下载');
		}
	}
	
	function lib_imp() {
		achieveIds();
		if (ids.length == 1) {
			if(dictTypes[0] == 1){
				BIONE.commonOpenDialog("文件导入", "upLoad", "562", "334",
						'${ctx}/rpt/input/library/implibData?dictId=' + ids,null,null);
			} else {
				BIONE.tip('只能选择数据常量字典进行导入!');
			}
			
		} else if (ids.length > 1) {
			BIONE.tip('只能选择一个数据字典进行导入!');
		} else {
			BIONE.tip('请选择一个数据字典导入数据!');
		}
	}
	
	//添加数据字典
	function library_add() {
		if (!currentNode || currentNode.getParentNode()==null) {
			BIONE.tip("请选择非根目录以增加字典！");
			return;
		}
		BIONE.commonOpenDialog('数据字典添加', 'libraryAddWin', "950",
				"530",
				'${ctx}/rpt/input/library/newLibrary?d='+new Date().getTime());
	}

	//查询指定数据字典引用的补录模板
	function library_search() {
		achieveIds();
		if (ids.length == 1) {
			BIONE.commonOpenDialog("查看关联的补录模板", "queryTempInfo", "800", "485",
					"${ctx}/rpt/input/library/" + ids[0] + "/queryTemp");
		} else if (ids.length > 1) {
			BIONE.tip('只能选择一行进行查看');
		} else {
			BIONE.tip('请选择一行进行查看');
		}
	}

	//预览数据字典
	function library_query() {
		achieveIds();
		if (ids.length == 1) {
			openWin(ids[0]);
		} else if (ids.length > 1) {
			BIONE.tip('只能选择一行进行数据预览');
		} else {
			BIONE.tip('请选择一行进行数据预览');
		}
	}
	function openWin(dictId){
		//检查该字典中的数据库是否存在该数据源中
		var queryFlag=true;

		if(queryFlag==true){
			BIONE.commonOpenLargeDialog("数据字典内容预览", "libraryAddWin",
					"${ctx}/rpt/input/library/query/" + dictId);
		}
	}


	function tableLinkUpdate(dictId){
		BIONE.commonOpenDialog("数据字典修改", "libraryAddWin", "950",
				"530", "${ctx}/rpt/input/library/update/" + dictId);
	}
	// 修改数据字典
	function library_update() {
		achieveIds();
		if (ids.length == 1) {
			var idsTmp = ids.join(",");
			BIONE.commonOpenDialog("数据字典修改", "libraryAddWin", "950",
					"530", "${ctx}/rpt/input/library/update/" + idsTmp);
		} else if (ids.length > 1) {
			BIONE.tip('只能选择一行进行修改');
		} else {
			BIONE.tip('请选择一行进行修改');
		}
	}

	// 删除
	function library_delete() {
		achieveIds();
		if (ids.length > 0) {
			var libId = '';
			for ( var i = 0; i < ids.length; i++) {
				if (i == ids.length - 1) {
					libId = libId + ids[i];
				} else {
					libId = libId + ids[i] + ",";
				}
			}
			$.ligerDialog
					.confirm(
							'确实要删除这条记录吗!',
							function(yes) {
								if (yes) {
									$.ajax({
										cache : false,
										async : true,
										url : "${ctx}/rpt/input/library/checkTempListById.json",
										dataType : 'json',
										type : "get",
										data : {
											"dictId" : libId
										},
										success : function(result) {
											if (result != null && result.length > 0) {
												var tempNames = '';
												for ( var i = 0; i < result.length; i++) {
													if (i == result.length - 1) {
														tempNames = tempNames
																+ result[i];
													} else {
														tempNames = tempNames
																+ result[i]
																+ ',';
													}
												}
												BIONE
														.tip('选择的字典中已经关联补录模板【'
																+ tempNames
																+ '】,故不能删除');
											} else {
												$.ajax({
													cache : false,
													async : true,
													url : "${ctx}/rpt/input/library/delete",
													dataType : 'json',
													type : "get",
													data : {
														"dictId" : libId
													},
													success : function(
															result) {
														f_reload()
														BIONE
																.tip('删除记录成功');

													}
												});
											}
										}
									});
								}
							});
		} else {
			BIONE.tip('请选择一行进行删除');
		}
	}

	function achieveIds() {
		ids = [];
		dictTypes = [];
		var rows = grid.getSelectedRows();
		for ( var i in rows) {
			ids.push(rows[i].dictId);
			dictTypes.push(rows[i].dictType);
		}
	}

	function f_reload() {
		var manager = $("#maingrid").ligerGetGridManager();
		manager.loadData();
	}
</script>

<title>数据字典</title>
</head>
<body>
	<div id="template.left.up.icon">
		<img src="${ctx }/images/classics/icons/application_side_tree.png" />
	</div>
	<div id="template.left.up">
		<span style="font-size: 12">数据字典树</span>
	</div>
	<div>
		<input type="text" id="txt2" value="" />
	</div>
</body>
</html>