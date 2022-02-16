<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<html>
<head>
<%@ include file="/common/taglibs.jsp"%>
<meta name="decorator" content="/template/template5.jsp">
<script type="text/javascript">
	var btns =[]; 
	var checkNodes = [];
	var currentNode;//当前点击节点
	var checkEnable = true;
	var currentNode = null;
	var cascade = 2;
	var treeInfo = null;
	var treeData = null;
	var orgType = window.parent.busiType || "01";
	if("${checkbox}" == "0"){
		checkEnable = false;
	}
	function initCombo(){
		 $("#orggrp")
			.ligerComboBox(
					{
						url : "${ctx}/rpt/frame/rptorggrp/getorgGrpCombo?orgType="+orgType+"&d="
								+ new Date().getTime(),
						ajaxType : "post",
						labelAligh : "center",
						slide : false,
						onSelected : function(val) {
							var data = {
								grpId : val
							};
							if(val != null && val != ""){
								$.ajax({
									cache : false,
									async : true,
									url : "${ctx}/rpt/frame/rptorggrp/getGrpOrgNos",
									type : 'POST',
									data : data,
									success : function (data){
										leftTreeObj.checkAllNodes(false);
										checkNodes = [];
										for(var i in data){
											var node = {};
											node.id = data[i];
											checkNodes.push(node);
											var node = leftTreeObj.getNodeByParam("id", data[i], null);
											if(node){
												leftTreeObj.checkNode(node, true, false);
											}
											
										}
									},
									error : function (result,b){
									
									}
								});
							}
						}
					});

	}
	$(function(){
		initTree();
		initTreeInfo();
		if(checkEnable == true){
			var radio = '<div style="width:100%;margin:auto;"><div style="text-align: center;">级联方式：<input type="radio" id="level1" style="width:auto" name="level" value="level1" onclick=check() />全部级联<input type="radio" id="level3" name="level" value="level3" onclick=check()  style="width:auto"/>级联下级<input type="radio" id="level2" name="level" value="level2" onclick=check() checked="true" style="width:auto"/>不级联</div></div>';
			$(".form-bar").prepend(radio);
			$(".form-bar-inner").css("width", "10%").css("margin", "auto");
			check();
		}
		initCombo();
		initBtn();
	});
	
	//初始化数
	function initTree() {
		var setting = {
				async : {
					enable : true,
					url : "${ctx}/report/frame/datashow/idx/getOrgTree?orgType="+orgType+"&t="
							+ new Date().getTime(),
					autoParam : [ "id" ],
					dataType : "json",
					type : "post",
					dataFilter : function(treeId, parentNode, childNodes) {
						if (childNodes) {
							for ( var i = 0; i < childNodes.length; i++) {
								childNodes[i].isParent = true;
								if(checkNodes!=null&&checkNodes.length>0){
									for ( var l in checkNodes) {
										var node = checkNodes[l];
										if(childNodes[i].id == node.id){
											childNodes[i].checked=true;
											childNodes[i].ischecked = true;
										}
									}
								}
								
							}
						}
						return childNodes;
					}
				},
				data : {
					key : {
						name : "text"
					},
					simpleData : {
						enable : true,
						idKey : "id",
						pIdKey : "upId"
					}
				},
				view : {
					selectedMulti : true
				},
				check : {
					chkboxType : {"Y":"", "N":""},
					chkStyle : 'checkbox',
					enable : checkEnable
				},
				callback : {
					onClick : zTreeOnClick,
					onNodeCreated : function(event, treeId, treeNode) {
						if (treeNode.id == "ROOT") {
							//若是根节点，展开下一级节点
							leftTreeObj.expand(treeNode, true, true, true);
						}
					},
					onCheck:function(event, treeId, treeNode){
						if(cascade==1){
							if (treeNode.getCheckStatus().checked) {
								var node={};
								node.id=treeNode.id;
								node.text=treeNode.text;
								node.params=treeNode.params;
								if(getIndex(node,checkNodes)==-1)
									checkNodes.push(node);
								checkCascadeCheck(leftTreeObj,treeNode.id,true);
							}
							else{
								var node={};
								node.id=treeNode.id;
								node.text=treeNode.text;
								node.params=treeNode.params;
								var index=getIndex(node,checkNodes);
								if(index>=0){
									checkNodes.splice(index,1);
								}
								unCheckCascadeCheck(leftTreeObj,treeNode.id,true);
							}
						}
						else if(cascade==2){
							if (treeNode.getCheckStatus().checked) {
								var node={};
								node.id=treeNode.id;
								node.text=treeNode.text;
								node.params=treeNode.params;
								if(getIndex(node,checkNodes)==-1)
									checkNodes.push(node);
							}
							else{
								var node={};
								node.id=treeNode.id;
								node.text=treeNode.text;
								node.params=treeNode.params;
								var index=getIndex(node,checkNodes);
								if(index>=0){
									checkNodes.splice(index,1);
								}
							}
						}
						else if(cascade==3){
							if (treeNode.getCheckStatus().checked) {
								var node={};
								node.id=treeNode.id;
								node.text=treeNode.text;
								node.params=treeNode.params;
								if(getIndex(node,checkNodes)==-1)
									checkNodes.push(node);
								checkCascadeCheck(leftTreeObj,treeNode.id,false);
							}
							else{
								var node={};
								node.id=treeNode.id;
								node.text=treeNode.text;
								node.params=treeNode.params;
								var index=getIndex(node,checkNodes);
								if(index>=0){
									checkNodes.splice(index,1);
								}
								unCheckCascadeCheck(leftTreeObj,treeNode.id,false);
							}
						}
					}
				}
			};
			if(window.parent.srcCode){
				setting.async.otherParam = {srcCode : window.parent.srcCode};
			}
			leftTreeObj = $.fn.zTree.init($("#tree"), setting);
			$(".content").height($("#center").height()-34);
	}
	//加载树中数据
	function zTreeOnClick(event, treeId, treeNode) {
    	currentNode = treeNode;
	};
	//是否级联选择
	function check() {
		if($("#level1")[0].checked){
			cascade =1;
		}
		if($("#level2")[0].checked){
			cascade =2;
		}
		if($("#level3")[0].checked){
			cascade =3;
		}
	}
	//树点击事件
	function zTreeOnClick(event, treeId, treeNode) {
		if (currentNode != treeNode) {
			currentNode = treeNode;
		}
	}

	//初始化按钮
	function initBtn() {
		btns.push({
			text : "取消",
			onclick : function() {
				BIONE.closeDialog("chooseOrg");
			}
		}, {
			text : "选择",
			onclick : function() {
					var nodes = [];
					for(var i in checkNodes){
						var node ={};
						node.id = checkNodes[i].id;
						if(checkNodes[i].text){
							node.text = checkNodes[i].text;
						}else{
							node.text = treeData[checkNodes[i].id];
						}
						nodes.push(node);
					}
					var checkOrgNms = "";
					if(nodes && nodes.length > 0){
						var checkOrgs = [];
						for(var i=0;i<nodes.length;i++){
							checkOrgs.push(nodes[i].id);
							if(nodes[i].text){
								checkOrgNms = checkOrgNms + nodes[i].text + ",";
							}
						}
						if(window.parent.$.ligerui.get('queryOrg')){
							if(checkOrgNms){
								window.parent.$.ligerui.get('queryOrg').setText(checkOrgNms);
							}
						}
						window.parent.dimFilters["${dimTypeNo}"] = {checkIds :  checkOrgs, selectedNodes : createTreeNode(nodes)};
						BIONE.closeDialog("chooseOrg");
					}else{
						BIONE.tip("请选择一个机构");
					}
					
			}
		});
		BIONE.addFormButtons(btns);
	}
	function createTreeNode(nodes){
		var result = [];
		var map = [];
		for(var i=0;i<nodes.length;i++){
			map[nodes[i].id] = nodes[i];
			nodes[i].children = [];
		}
		var parent = null;
		for (var i=0;i<nodes.length;i++) {
			parent = map[nodes[i].upId];
			if (parent == null) {
				result.push(nodes[i]);
			} else {
				parent.children.push(nodes[i]);
			}
		}
		return result;
	}
	
	function initTreeInfo(){
		var data = {orgType : orgType};
		$.ajax({
			cache : false,
			// no async
			async : false,
			url :  "${ctx}/rpt/frame/rptorg/getAllOrgTree?d="+ new Date().getTime(),
			dataType : "json",
			type : "POST",
			data : data,
			success : function(result) {
				treeInfo = result.treeInfo;
				treeData = result.treeData;
			},
			error : function(result, b) {
			}
		});
		if(window.parent.dimFilters["${dimTypeNo}"]){
			for(var j=0;j<window.parent.dimFilters["${dimTypeNo}"].checkIds.length;j++){
				var node = {};
				node.id = window.parent.dimFilters["${dimTypeNo}"].checkIds[j];
				checkNodes.push(node);
			}
		} 
	}
	
	function checkCascadeCheck(treeObj,id,flag){
		var nodes=treeInfo[id];
		if(nodes!=null&&nodes.length>0){
			for(var i in nodes){
				var node={};
				node.id=nodes[i].id;
				node.text=nodes[i].text;
				node.params=nodes[i].params;
				if(getIndex(node,checkNodes)==-1){
					var treenode = treeObj.getNodeByParam("id", node.id, null);
					if(treenode!=null)
						treeObj.checkNode(treenode, true, false,false);
					checkNodes.push(node);
					
				}
				if(flag)
					checkCascadeCheck(treeObj,node.id,true);
					
			}
		}
	}

	function unCheckCascadeCheck(treeObj,id,flag){
		var nodes=treeInfo[id];
		if(nodes!=null&&nodes.length>0){
			for(var i in nodes){
				var node={};
				node.id=nodes[i].id;
				node.text=nodes[i].text;
				node.params=nodes[i].params;
				var index=getIndex(node,checkNodes);
				if(index>=0){
					var treenode = treeObj.getNodeByParam("id", node.id, null);
					if(treenode!=null)
						treeObj.checkNode(treenode, false, false,false);
					checkNodes.splice(index,1);
				}
				if(flag)
					unCheckCascadeCheck(treeObj,node.id,true);
			}
		}
	}
	
	function getIndex(info,array){
		for(var i in array){
			if(array[i].id==info.id){
				return i;
			}
		}
		return -1;
	}
	
</script>
</head>
<body>
	<div id="template.center">
		<div style="margin: 8px; width:80px;padding-left: 5px;float:left">
			<span>机构组选择：</span>
		</div>
		<div style="margin: 5px; margin-left: 80px;padding-left: 5px;">
		<input id="orggrp"></input>
		</div>
		<div class="content" style="border: 1px solid #C6C6C6;">
			<div id="treeContainer"
				style="width: 100%;  height: 100%;overflow: auto; clear: both;">
				<ul id="tree"
					style="font-size: 12; background-color: F1F1F1; width: 90%"
					class="ztree"></ul>
			</div>
		</div>
	</div>
</body>
</html>