<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<%@ include file="/common/taglibs.jsp"%>
<html>
<meta name="decorator" content="/template/template7.jsp">
<head>
<script type="text/javascript">
	var leftTreeObj;
	var gridCenter;
	var tmpGrid;
	var currentNode = null;
	$(function() {
		initExport();
		var $centerDom = $(document);
		gridCenter = $centerDom.height() - 26;
		initTree();
		initTab();
	});
	var searchHandler = function(){
		BIONE.loadTree("${ctx}/report/frame/datashow/detail/getCatalogTree",
				leftTreeObj, null, null, false);
	}
	
	var exportTmp=function(fileName){
		var src = '';
		src = "${ctx}/report/frame/detailtmp/exportTmpInfo?filepath="+fileName+"&d="+ new Date().getTime();
		downdload.attr('src', src);
	};
	
	var initExport=function() {
		window["downdload"] = $('<iframe id="download"  style="display: none;"/>');
		$('body').append(downdload);
	};
	//渲染树
	function initTree() {
		var setting = {
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
				selectedMulti : false,
				showLine : false
			},
			callback : {
				onClick : zTreeOnClick
			}
		};
		leftTreeObj = $.fn.zTree.init($("#tree"), setting);
		BIONE.loadTree("${ctx}/report/frame/datashow/detail/getCatalogTree",
				leftTreeObj, null, null, false);
		$("#treeToolbar").ligerTreeToolBar({
			items:[{
				icon:'config',
				text:'目录管理',
				operNo : 'detail-tmp-menu',
				menu:{
					width:90,
						items : [ {
							icon : 'add',
							text : '添加',
							click : addCatalog
						}, {
							line : true
						}, {
							icon : 'modify',
							text : '修改',
							click : editCatalog
						}, {
							line : true
						}, {
							icon : 'delete',
							text : '删除',
							click : deleteCatalog
					}]
				}
			},{
 				icon:'export',
 				text:'数据处理',
 				operNo : 'detail-tmp-impexp',
 				menu:{
 					width:90,
 					items:[{
 							icon:'export',
 							text:'导出数据',
 							click : exportReport
 						},{
 							line:true
 						},{
 							icon:'import',
 							text:'导入数据',
 							click : importReport
 					}]
 				}
 			}],
			treemenu : false
		});
	}
	
	var exportReport = function(){
		BIONE.commonOpenSmallDialog("导出数据","exportReports","${ctx}/report/frame/detailtmp/expDetail");
	};
	
	var importReport = function (){
		BIONE.commonOpenDialog("导入数据","importReports",600,380,"${ctx}/report/frame/detailtmp/impDetail",null,
			function() {
				if(window.pathname!=null&&window.pathname!=""){
					$.ajax({
						cache : false,
						async : false,
						url : "${ctx}/report/frame/design/cfg/deleteFile",
						dataType : 'json',
						data : {
							pathname: window.pathname
						},
						type : "POST"
					});
				}
			});
	};
	//渲染选项卡
	function initTab() {
		window['maintab'] = $("#tab").ligerTab({
			contextmenu : true
		});
		maintab.addTabItem({
			tabid : "topTab",
			text : "明细模板",
			showClose : false,
			content : "<div id='gridDiv' name='gridDiv' style='height:" + gridCenter
					+ "px;width:100%;'></div>"
		});
		content = "<iframe frameborder='0' id='gridFrame' name='gridFrame' style='height:100%;width:100%;' src='${ctx}/report/frame/detailtmp/content'></iframe>";
		$("#gridDiv").html(content);
	}

	//树的点击事件
	function zTreeOnClick(event, treeId, treeNode) {
		currentNode = treeNode;
		if(tmpGrid != null){
			var catalogId = treeNode.id!="0" ?  treeNode.id : "";
			tmpGrid.setParm("catalogId",catalogId);
			tmpGrid.loadData();
		}
	}

	//新增分类
	function addCatalog() {
		window.treeObj = leftTreeObj;
		if(currentNode == null || currentNode.id == "0")
			currentNode = treeObj.getNodeByParam("id","0", null);
		BIONE
		.commonOpenDialog(
				"添加目录",
				"editCatalogWin",
				600,
				280,
				'${ctx}/report/frame/detailtmp/catalogEdit',
				null);
		return false;
	}

	//修改分类
	function editCatalog() {
		window.treeObj = leftTreeObj;
		if(currentNode == null || currentNode.id == "0"){
			BIONE.tip("请选择一个目录");
			return;
		}
		BIONE
		.commonOpenDialog(
				"修改目录",
				"editCatalogWin",
				600,
				280,
				'${ctx}/report/frame/detailtmp/catalogEdit?catalogId='+currentNode.id,
				null);
		return false;
		
	}

	//删除目录
	function deleteCatalog() {
		if (!currentNode || currentNode.id == '0') {
			BIONE.tip("未选择任何目录。");
			return;
		}
		$.ligerDialog.confirm("确定删除目录 [ " + currentNode.text + " ] ？",
				function(flag) {
					if (flag) {
						$.ajax({
							type : "POST",
							url : "${ctx}/report/frame/detailtmp/deleteCatalog",
							data : {
								catalogId : currentNode.id
							},
							success : function(res) {
								if (res.msg == "ok") {
									BIONE.tip("删除成功！");
									//局部刷新树
									leftTreeObj.removeNode(currentNode);
									if(tmpGrid != null){
										tmpGrid.setParm("catalogId","");
										tmpGrid.loadData();
									}
									currentNode = null;
								} else {
									BIONE.tip("该目录下存在明细模板，无法删除。");
								}
							},
							error : function(XMLHttpRequest, textStatus,
									errorThrown) {
								BIONE.tip('删除失败,错误信息:' + textStatus);
							}
						});
					}
				});
	}
	
	
</script>
</head>
<body>
	<div id="template.left.up.icon">
		<img src="${ctx}/images/classics/icons/application_side_tree.png" />
	</div>
	<div id="template.left.up">
		<span style="font-size: 12" id="left_up_span">明细模板目录</span>
	</div>
	<div id="template.left.down">
		<div id="btn"></div>
	</div>
	<div id="template.right">
		<div id="rightDiv" >
			<div id="tab" style="width: 100%; overflow: hidden;"></div>
		</div>
	</div>
</body>
</html>