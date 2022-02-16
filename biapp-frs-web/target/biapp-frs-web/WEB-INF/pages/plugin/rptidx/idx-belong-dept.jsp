<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<html>
<head>
<%@ include file="/common/taglibs.jsp"%>
<meta name="decorator" content="/template/template5.jsp">
<script type="text/javascript">
	var btns =[]; 
	var currentNode;//当前点击节点
	
	$(function(){
		initTree();
		initBtn();
	});
	
	//初始化树
	function initTree() {
		
	var setting = {
			data : {
				key : {
					name : "text"
				},
				simpleData:{
					enable:true,
					idKey: "id",
					pIdKey: "upId"
				}
			},
			view : {
				selectedMulti : false
			},
			
			callback : {
				onDblClick : zTreeOnDblClick,
				onClick : zTreeOnClick
			}
		};
		leftTreeObj = $.fn.zTree.init($("#tree"), setting);
		BIONE.loadTree("${ctx}/bione/admin/dept/getDeptTree",leftTreeObj);
	}
	//树单击事件
	function zTreeOnClick(event, treeId, treeNode){
		currentNode = treeNode;
	}
	//树双击击事件
	function zTreeOnDblClick(event, treeId, treeNode) {
		if(treeNode.params.nodetype == "dept"){
			var $w = window.parent.$window;
			$w.liger.get("defDept")._changeValue(treeNode.id,treeNode.text);
			BIONE.closeDialog("selectWin");
		}
	}

	//初始化按钮
	function initBtn() {
		btns.push({
			text : "取消",
			onclick : function() {
				BIONE.closeDialog("selectWin");
			}
		}, {
			text : "选择",
			onclick : function() {
				if(currentNode != null && currentNode.params.nodetype == "dept"){
					var $w = window.parent.$window;
					$w.liger.get("belongDeptNm")._changeValue(currentNode.data.deptId,currentNode.text);
					BIONE.closeDialog("selectWin");
				}else{
					BIONE.tip("请选择部门");
				}
			}
		});
		BIONE.addFormButtons(btns);
	}
</script>
</head>
<body>
	<div id="template.center">
		<div class="content" style="border: 1px solid #C6C6C6; height: 98%">
			<div id="treeContainer"
				style="width: 100%; height: 100%; overflow: auto; clear: both;">
				<ul id="tree"
					style="font-size: 12; background-color: F1F1F1; width: 90%"
					class="ztree"></ul>
			</div>
		</div>
	</div>
</body>
</html>