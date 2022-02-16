<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<%@ include file="/common/taglibs.jsp"%>
<html>
<head>
<meta name="decorator" content="/template/template7_BS.jsp">
<script type="text/javascript">
	var treeObj = null;
	var currentNode = null;
	var grid = null;
	$(function() {
		//构建右tab
		$("#tab").ligerTab({
			contextmenu : true
		});
		tabObj = $("#tab").ligerGetTabManager();
		var h = $("#left").height() - 100;
		tabObj
		.addTabItem({
			tabid : "paramTab",
			text : "参数信息",
			showClose : false,
			content : '<iframe frameborder="0" id="parTab" style="height:'+h+'px;" src="${ctx}/bione/variable/param/toGrid" ></iframe>'
		});
		//构建左树
		var setting = {
			async : {
				autoParam : [ "id=nodeId" ],
				enable : true,
				url : "${ctx}/bione/variable/paramType/list.json",
				dataType : "json",
				type : "post",
				dataFilter : function(treeId, parentNode, childNodes) {
					if (childNodes) {
						for ( var i = 0; i < childNodes.length; i++) {
							var isHadChild = childNodes[i].params.isParent;
							childNodes[i].isParent = isHadChild;
							if (isHadChild == "true") {
								childNodes[i].icon = '${ctx}/images/classics/icons/folder.png';
							} else {
								childNodes[i].icon = '${ctx}/images/classics/icons/collapse-all.gif';
							}
						}
					}
					return childNodes;
				}
			},
			data : {
				key : {
					name : "text"
				}
			},
			view : {
				selectedMulti : false,
				showLine : false
			},
			callback : {
				onClick : function(event, treeId, treeNode) {
					$("#treeContainer").bind("mousedown", onBodyMouseDown);
					currentNode = treeNode;
					tabObj.setHeader("paramTab","["+currentNode.data.paramTypeName+"]类型参数维护");
					grid = document.getElementById("parTab").contentWindow.grid;
					grid._clearGrid();
					grid.setParm('newPage', 1);
					grid.options.newPage=1;
					grid.set('parms', {
						paramTypeNo : currentNode.data.paramTypeNo
					});
					$("#search input[name='paramTypeNo']").val(
							currentNode.data.paramTypeNo);
					grid.loadData();
				}
			}
		};
		treeObj = $.fn.zTree.init($("#tree"), setting);

		$("#treeToolbar").ligerTreeToolBar({
			items : [ {
				text : '增加',
				click : addParamType,
				icon : 'add'
			}, {
				line : true
			}, {
				text : '修改',
				click : updateParamType,
				icon : 'xg-01'
			}, {
				text : '删除',
				click : delParamType,
				icon : 'delete'
			} ]
		});
		
		//单击空白处取消选择
		function onBodyMouseDown(event) {
			if (!(event.target.id == "treeToolbar" || $(event.target).parents(
					"#treeToolbar").length > 0)) {
				treeObj.cancelSelectedNode();
				currentNode = null;
			}
		}
		 var str="";
			function getParamIdAndChildIdByInfo(rowInfo){
				
				if(rowInfo==1){
					str="";
					}
				var children=grid.getChildren(rowInfo);
				str+=rowInfo.paramId+",";
				if(children){
					for(var i=0;i<children.length;i++){
						getParamIdAndChildIdByInfo(children[i]);
					}
				}
			}
		function addParamType() {
			if (currentNode != null) {
				dialog = BIONE.commonOpenLargeDialog('新增参数类型',
						'paramTypeAddWin',
						'${ctx}/bione/variable/paramType/new?upNo='
								+ currentNode.data.paramTypeNo);
			} else{
				dialog = BIONE.commonOpenLargeDialog('新增参数类型',
						'paramTypeAddWin',
						'${ctx}/bione/variable/paramType/new?upNo=0');
			}
		}

		
		function updateParamType() {
			if (currentNode == null) {
				BIONE.tip('请选择参数类型');
				return;
			}
			dialog = BIONE.commonOpenLargeDialog('修改参数类型',
					'paramTypeUpdateWin', '${ctx}/bione/variable/paramType/'
							+ currentNode.data.paramTypeNo + '/edit');
		}

		function delParamType() {
			if (currentNode == null) {
				BIONE.tip('请选择参数类型');
				return;
			}
			;
			$.ligerDialog.confirm('确实要删除' + currentNode.data.paramTypeName
					+ '及所有子类型吗!', function(yes) {
				if (yes) {
					$.ajax({
						async : false,
						type : "POST",
						url : '${ctx}/bione/variable/paramType/'
								+ currentNode.data.paramTypeNo,
						success : function() {
							BIONE.tip('删除成功');
							window.parent.refreshTabByTitle("业务参数信息维护");
						},
						error : function() {
							BIONE.tip('删除失败');
						}
					});
					//刷新表格和树
					grid.loadData();
					BIONE.refreshAsyncTreeNodes(treeObj, "id",
							currentNode.upId, "refresh");
					currentNode = null;
				}
			});
		}
		;

		// 状态显示,停/启用等
		function QYBZRenderSts(rowdata) {
			if (rowdata.userSts == '1') {
				return "启用";
			} else if (rowdata.userSts == '0') {
				return "停用";
			} else {
				return rowdata.userSts;
			}
		}
		//参数类型标志转名称
		function QYBZRenderParTypeName(rowdata) {
			return BIONE.paramTransformer(rowdata.paramTypeNo,
					'${ctx}/bione/common/getParamTypeName');
		}
	});
	//刷新树
	function refreshTree(){
		if(treeObj){
			treeObj.reAsyncChildNodes(null, "refresh");
			treeObj.expandAll(true);
		}
	}
</script>
</head>
<body>
	<div id="template.left.up.icon">
		<img src="${ctx }/images/classics/icons/application_side_tree.png" />
	</div>
	<div id="template.left.up">
		<span style="font-size: 12">参数类型信息</span>
	</div>
	<div id="template.right"></div>
</body>
</html>