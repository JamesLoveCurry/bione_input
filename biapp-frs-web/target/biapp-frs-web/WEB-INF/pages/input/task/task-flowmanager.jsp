<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<%@ include file="/common/taglibs.jsp"%>
<html>
<meta name="decorator" content="/template/template3.jsp">
<head>
<script type="text/javascript">
	var zTreeObj;
	var dirType;
	var rootName;
	var currentNode = null;
	var currentNodeId = "0";
	var nodetypeTranslate = {
        'combox' : [],
        'translate' : {}
   	};

   	$.ajax({
   	    cache : false,
   	    async : false,
   	    url : "${ctx}/rpt/input/task/getTaskNodeType.json?paramTypeNo=TASK_NODE_TYPE&d="
   	            + new Date().getTime(),
   	    dataType : 'json',
   	    success : function(result){
   	        nodetypeTranslate.combox = result.combox;
   	        nodetypeTranslate.translate = result.translate;
   	    },
   	    error : function(result, b){
   	   	    BIONE.tip('节点类型加载失败');
 	   	}
   	});
   	
    var groupicon = "${ctx}/images/classics/icons/communication.gif";
	var buttons = [];
	buttons.push({
		text : '关闭',
		onclick : closeCallBack
	});
	buttons.push({
		text : '保存',
		onclick : savedFlowNode
	});
	
	$(init);
	
	function init() {
		beforeTree();
		initTree();
		initGrid();
		initMenubar();
		initToolBar();
	}
	
	function initToolBar() {
		var toolBars = [ {
			text : '保存',
			click : f_save_node,
			icon : 'add'
		},{
			text : '增行',
			click : f_add_one,
			icon : 'modify'
		},{
			text : '删行',
			click : f_del_one,
			icon : 'delete'
		}];
		BIONE.loadToolbar(grid, toolBars, function() {
		});
	}
	
	function f_save_node(){
		grid.endEdit();
		var rows = grid.rows;
		if(rows.length == 0)
		{
			BIONE.tip('请设置节点!');
			return ;
		}
		var selectedRows = zTreeObj.getSelectedNodes();
		var flowNodeList = [];
		var taskDefId = selectedRows[0].id;
		for(var i = 0 ;i <rows.length;i++){
			var nodeType = rows[i].nodeType;
			var taskNodeNm = rows[i].taskNodeNm;
			var taskNodeDefId = rows[i].taskNodeDefId;
			if(taskNodeNm == null || taskNodeNm ==""||nodeType== null || nodeType == ""){
				BIONE.tip('请设置第'+(i+1)+'行节点的数据!');
				return ;
			}
			flowNodeList.push({
				taskNodeDefId : taskNodeDefId?taskNodeDefId:"",
				flowNodeNm :taskNodeNm,
				taskDefId : taskDefId,
				taskOrderno : i,
				nodeType : nodeType
			});
			
		}
		
		var taskFlowNodeVO ={
			flowNodeList : flowNodeList,
			taskDefId : taskDefId
		};
		
		$.ajax({
			cache : false,
			async : true,
			url : "${ctx}/rpt/input/task/saveTaskFlowNode?d="
					+ new Date().getTime(),
			dataType : 'json',
			contentType : "application/json",
			type : "post",
			data : JSON2.stringify(taskFlowNodeVO),
			success : function(result) {
				BIONE.tip('保存成功!');
			},
			error : function(result, b) {
				BIONE.tip('保存失败,发现系统错误 <BR>错误码：' + result.status);
			}
		});
	}
	
	function f_add_one(){
		grid.addRow({
			
		});
	}
	
	function f_del_one(){
		var selectedRows = grid.getSelectedRows();
		if(selectedRows.length==0){
			BIONE.tip('请选择流程进行修改!');
			return;
		}
		if(selectedRows.length>1){
			BIONE.tip('请选择一条流程进行修改!');
			return;
		}
		grid.deleteRow(selectedRows[0]);
	}

	// 组织树对象
	function beforeTree() {
		var setting = {
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
			callback : {
				onClick : zTreeOnClick
			}
		};
		zTreeObj = $.fn.zTree.init($("#tree"), setting);
	}

	// 获得树结点数据, 追加到树上
	function initTree() {
		$.ajax({
			cache : false,
			async : false,
			url : '${ctx}/rpt/input/task/getListWorkFlow',
			type : "post",
			success : function(result) {
				

				var nodes = zTreeObj.getNodes();
				var num = nodes.length;
				for ( var i = 0; i < num; i++) {
					zTreeObj.removeNode(nodes[0], false);
				}
				if(!result)return ;
				
				var id = null;
				zTreeObj.addNodes(null, result, false);
			},
			error : function(result, b) {
				BIONE.tip('发现系统错误 <BR>错误码：' + result.status);
			}
		});
	}

	// init menu toolbar
	function initMenubar() {
		$("#treeToolbar").ligerTreeToolBar({
			items : [ {
				icon : 'add',
				text : '新增',
				click : addFlow
			},  {
				icon : 'modify',
				text : '修改',
				click : modifyFlow
			}, {
				icon : 'delete',
				text : '删除',
				click : deleteFlow
			} ]
		});
	}

	// zTree onClick
	function zTreeOnClick(event, treeId, treeNode) {
		zTreeObj.expandNode(treeNode);		/*点击节点名称也展开节点*/
		currentNode = treeNode;
		initTaskFlowNode(currentNode.id);
	}
	
	function initTaskFlowNode(taskDefId){
		grid.setParm('taskDefId', taskDefId);
		grid.loadData();
	}

	// 刷新树
	function refreshTree() {
		var rootTmp = zTreeObj.getNodeByParam("id", "0", null);
		if (rootTmp) {
			zTreeObj.removeNode(rootTmp);
		}
		initTree();
	}

	function addFlow() {
		var options = {
				url : "${ctx}/rpt/input/task/editFlow",
				dialogname : "addFlow",
				title : "新建流程"
			};
		BIONE.commonOpenDialog(options.title, options.dialogname, 300, 200,
				options.url);
	}
	function modifyFlow(){
		var selectedRows = zTreeObj.getSelectedNodes();
		if(selectedRows.length==0){
			BIONE.tip('请选择流程进行修改!');
			return;
		}
		if(selectedRows.length>1){
			BIONE.tip('请选择一条流程进行修改!');
			return;
		}
		
		var options = {
				url : "${ctx}/rpt/input/task/editFlow?taskDefId="+selectedRows[0].id+"&flowNm="+encodeURI(encodeURI(selectedRows[0].text)),
				dialogname : "addFlow",
				title : "修改流程"
			};
		BIONE.commonOpenDialog(options.title, options.dialogname,  300, 200,
				options.url);
	}
	//DELETE
	function deleteFlow() {
		var selectedRows = zTreeObj.getSelectedNodes();
		if(selectedRows.length==0){
			BIONE.tip('请选择流程进行删除!');
			return;
		}
		if(selectedRows.length>1){
			BIONE.tip('请选择一条流程进行删除!');
			return;
		}
		$.ligerDialog.confirm('确实要删除该记录吗?', function(yes) {
			if (yes) {
				var flag = false;
				$.ajax({
					async : false,
					cache : false,
					type : "POST",
					url : "${ctx}/rpt/input/task/delTaskFlow?taskDefId="+selectedRows[0].id,
					dataType : "script",
					success : function(res) {
						if(res == '1'){
							refreshTree();
							BIONE.tip('删除成功!');
						}else if(res == '0'){
							BIONE.tip('该流程已有任务引用');
						}
					}
				});
			}
		});
	}
	
	function setCurrentId(){
		currentNodeId = currentNode.upId;
		
		var nodes = zTreeObj.getNodesByParam("upId", currentNode.upId, null);
		if(nodes.length > 1){
			currentNodeId =nodes[0].id;
		}
		
	}
	
	//SAVE
	function savedFlowNode() {
		
	}

	//CANCLE
	function closeCallBack() {
		BIONE.closeDialog("flowManager");
	}

	function initGrid() {
		var  columnArr  =[{
			display : '节点名称',
			name : 'taskNodeNm',
			width : "60%",
			align : 'left'	,
			editor : {
				type : "text"
			}
		},{
			display : '节点类型',
			name : 'nodeType',
			width : "20%",
			align : 'left',
			editor : {
				type : 'select',
				data : nodetypeTranslate.combox,
				valueField : 'id',
				textField : 'text'
			},
			render : function(rowdata,rownum,value){
				return value == '' ? '' : nodetypeTranslate.translate[value]
			}
		}];
		grid = $("#maingrid").ligerGrid({
			url : "${ctx}/rpt/input/task/getTaskNodeList.json",
			toolbar : {},
			checkbox : false,
			columns : columnArr,
			dataAction : 'server', //从后台获取数据
			usePager : false, //服务器分页
			alternatingRow : true, //附加奇偶行效果行
			colDraggable : true,
			sortName : 'taskOrderno',//第一次默认排序的字段
			sortOrder : 'asc', //排序的方式
            delayLoad:true,
			width : '99%',
			clickToEdit : true,
			enabledEdit : true,
			height : '88%'
		});
		grid.setHeight($("#center").height() - 40);
	}
</script>
</head>
<body>
	<div id="template.left.up.icon">
		<img src="${ctx}/images/classics/icons/application_side_tree.png" />
	</div>
	<div id="template.left.up">
		<span id="treeTitle" style="font-size: 12">流程树</span>
	</div>
	<div id="template.right">
		<div id="maingrid"></div>
	</div>
</body>
</html>