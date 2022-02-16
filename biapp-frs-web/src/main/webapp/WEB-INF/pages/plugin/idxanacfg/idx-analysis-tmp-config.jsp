<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<%@ include file="/common/taglibs.jsp"%>
<html>
<meta name="decorator" content="/template/template10.jsp">
<head>
<script type="text/javascript">
	var leftTreeObj=null;
	var rightTreeObj=null;
	var searchNm ="";
	var isTmpidx = [];
	
	$(function(){
		initRightTree();
		initLeftTree(searchNm);
		initBotten();
		initTool();
	});
	
	function initTool(){		
		//搜索框
		$('#treeToolbar').append('<div class="l-text l-text-date" style="width: 99.9%;"><input id="treeSearchInput" type="text" class="l-text-field"  style="width: 100%;" />'+'<div class="l-trigger">'+                                                                      
				'<i id="treeSearchIcon" style="width:100%;height:100%;" class="icon-search search-size"></i>' +                                          
					'</div> </div>  </div></li> </ul> </div>');	
		//调整左框高度
		$("#leftTreeContainer").height($("#left").height()-56);
		$("#leftTree").height($("#leftTreeContainer").height() - 10);
		
		//搜索点击事件
 		$('#treeSearchIcon').live('click', function() {
 			initLeftTree($('#treeSearchInput').val());
		});
		
		//响应回车事件
		$('#treeSearchInput').bind('keydown', function(event) {
			if (event.keyCode == 13) {
				initLeftTree($('#treeSearchInput').val());
			}
		});		
	};
	
	//初始化树	
	function initLeftTree(searchNm) {
		//左树
		var setting = {
			async : {
				enable : true,
				url : "${ctx}/idx/analysis/show/getAsyncTreeIdxShow.json",
				autoParam : [ "nodeType", "id", "indexVerId" ],
				otherParam : {
					'searchNm' : searchNm,
					'isShowIdx' : '1',
					'isShowDim' : 1,
					'isShowMeasure' : 0,
					'isPublish' : '1',
					"isAuth" : "1",
					"showEmptyFolder" : 0
				},
				dataType : "json",
				dataFilter : function(treeId, parentNode, childNodes) {
					if (childNodes) {
						for (var i = 0; i < childNodes.length; i++) {
							childNodes[i].nodeType = childNodes[i].params.nodeType;
							childNodes[i].indexVerId = childNodes[i].params.indexVerId;
						}
					}
					return childNodes;
				}
			},
			check : {
				enable : false
			},
			data : {
				key : {
					name : "text"
				}
			},
			view : {
				fontCss: changeColor,
				selectedMulti : false
			},
			edit:{
				enable: true,
				showRemoveBtn: false,
				showRenameBtn: false,
				drag:{
					isCopy: false,
					isMove: true,
					prev:false,
					next:false,
					inner:false
				}
			} ,
			callback : {
				beforeDrop: leftbeforeDrop
			} 
		};		
		//左树对象
		leftTreeObj = $.fn.zTree.init($("#leftTree"), setting, []);
	}
	
	function initRightTree () {
		//右树
		var setting2 = {
			view : {
				nameIsHTML : true,
				showTitle : false
			},
			data : {
				key : {
					name : 'text'
				},
				simpleData : {
					enable : true,
					idKey : "id",
					pIdKey : "upId"
				}
			},
			edit : {
				drag : {
					isCopy : false,
					isMove : false
				},
				enable : true,
				showRemoveBtn : true,
				showRenameBtn: false
			} ,
			callback : {
				beforeRemove : beforeRemove 
			} 
		};
		//右树对象
		rightTreeObj = $.fn.zTree.init($("#rightTree"), setting2);
		//加载数据
		loadTree("${ctx}/idx/tmp/config/getMenuToTree",rightTreeObj);	
	};
	
	//加载树中数据
	function loadTree(url,component,data){
		$.ajax({
			cache : false,
			async : false,
			url : url,
			dataType : 'json',
			type : "post",
			data:data,
			beforeSend : function() {
				BIONE.loading = true;
				BIONE.showLoading("正在加载数据中...");
			},
			complete : function() {
				BIONE.loading = false;
				BIONE.hideLoading();
			},
			success : function(result) {
				if(result.nodes.length>0){
					var resultNodes = result.nodes;
					isTmpidx = resultNodes;
					component.addNodes(null,resultNodes,false);
					component.expandAll(false);	
				}
			},
			error : function(result, b) {
				BIONE.tip('发现系统错误 <BR>错误码：' + result.status);
			}
		});
	}
	/**添加节点信息
	 *tree 目标树对象
	 *treeNode 拖拽节点
	 *targetNode 目标节点
	 */
	function addTreeNode(tree, treeNode, parentNode) {
		if (parentNode == null) {
			BIONE.tip("请放到文件夹下");
			return;
		}
		if(treeNode.params.nodeType =="idxCatalog"){
			BIONE.tip("请放到文件夹下");
			return;
		}
		getNodeArray(treeNode);
		var haveNode;
		for (var i = 0; i < nodeArray.length; i++) {
			var currNode = nodeArray[i];
			haveNode = tree.getNodeByParam("text", currNode.text, null);
			if (haveNode) {
				BIONE.tip("该节点子节点已经存在");
				return;
			}
		}
		tree.addNodes(parentNode,treeNode,false);
	}
	//获取拖拽节点的所有字节点
	function getNodeArray(treeNode) {
		nodeArray = [];
		nodeArray.push(treeNode);
		var nodes = treeNode.children;
		if (nodes) {
			for (var i = 0; i < nodes.length; i++) {
				getNodeArray(nodes[i]);
			}
		}
	}
	//左边拖右边    拖拽 放开前
	function leftbeforeDrop(treeId, treeNodes, parentNode) {
		if(treeNodes[0].children == null){
			eleMapped = treeNodes[0].id;
			eleName = treeNodes[0].text;
			for (var i = 0; i < treeNodes.length; i++) {
				addTreeNode(rightTreeObj, treeNodes[i],parentNode);
			}
		}else{
			for (var i = 0; i < treeNodes[0].children.length; i++) {
				addTreeNode(rightTreeObj, treeNodes[0].children[i],parentNode);
			}
		}
		return false;
	}
	//初始化按钮
	function initBotten() {
		BIONE.createButton({
			text : '保存',
			width : '80px',
			appendTo : '#bottom',
			operNo : 'saveButton',
			icon : 'save',
			click : function() {
				var nodes = rightTreeObj.transformToArray(rightTreeObj.getNodes());
				var paramIdx = "";
				var paramTid = ""; 
				var newNodes = [];
				var NodesCatalog = [];
				for(var i = 0;i < nodes.length;i++){
					node = nodes[i].params.nodeType
					if(node == "idxInfo"){
						paramIdx += nodes[i].upId+":"+nodes[i].id+";";
					}
					else{
						paramTid +=nodes[i].id+",";
					}
				}
				var url = "${ctx}/idx/tmp/config/saveMenu.json";
				$.ajax({
					cache : false,
					async : false,
					url : url,
					data :{
						paramIdx : paramIdx 
					},
					dataType : 'json',
					type : "post",
					beforeSend : function() {
						BIONE.loading = true;
						BIONE.showLoading("正在加载数据中...");
					},
					complete : function() {
						BIONE.loading = false;
						BIONE.hideLoading();
					},
					success : function() {
						BIONE.tip("保存成功！");
						initRightTree();
						initLeftTree(searchNm);
					},
					error : function(result, b) {
						BIONE.tip('发现系统错误 <BR>错误码：' + result.status);
					}
				});
			}
		});

		BIONE.createButton({
			text : '还原',
			width : '80px',
			appendTo : '#bottom',
			operNo : 'resetButton',
			icon : 'refresh',
			click : function() {
				initRightTree();				
			}
		});
	}
	function beforeRemove(treeId,treeNode){
		if(treeNode.params.nodeType == "idxCatalog"){
			rightTreeObj.removeChildNodes(treeNode);
			return false;
		}else{
			return true;
		}
	}
	
	//改变树节点颜色
	function changeColor(treeId, treeNode){
		if(isTmpidx.length > 0){
			for(var i = 0;i < isTmpidx.length;i++){
				if(treeNode.id == isTmpidx[i].id){
					return {color:"red"};
				}
			}
		}
	}
</script>
</head>
<body>
	<div id="template.left.up.icon">
		<img src="${ctx}/images/classics/icons/application_side_tree.png" />
	</div>
	<div id="template.right.up.icon">
		<img src="${ctx}/images/classics/icons/application_side_tree.png" />
	</div>
	<div id="template.left.up">指标列表</div>
	<div id="template.right.up">模版目录</div>
</body>
</html>