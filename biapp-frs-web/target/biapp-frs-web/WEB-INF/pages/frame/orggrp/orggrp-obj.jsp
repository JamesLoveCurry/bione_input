<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<%@ include file="/common/taglibs.jsp"%>
<html>
<head>
<meta name="decorator" content="/template/template5.jsp">
<script type="text/javascript">
var treeInfo;
var checkNodes = [];
var	treeObj;
var url;
var cascade = 2;
var upOrgNos;//修改时已经被勾选的机构的父节点
var orgNos;//修改时已经被勾选的机构列表
	$(function() {
		initTreeInfo();
		url='${ctx}/bione/admin/orgtree/getOrgTree';
		var setting = {
				async : {
					enable : true,
					url :  url,
					autoParam : [ "id"],
					dataType : "json",
					enable : true,
					type : "post",
					dataFilter : function(treeId, parentNode, childNodes) {
						if (childNodes) {
							for ( var i = 0; i < childNodes.length; i++) {
								childNodes[i].orgNo = childNodes[i].params.orgNo;
								// childNodes[i].orgType = childNodes[i].params.orgType;
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
					}
				},
				view : {
					selectedMulti : false
				},
				check:{
					enable : true,
					chkboxType :{"Y":"","N":""},
					chkStyle :"checkbox"
				},
				callback : {
					beforeCheck: function(treeId, treeNode){
						if(treeNode.id=='0')
							return false;
					},
					onNodeCreated : function(event, treeId, treeNode) {
						if (treeNode.id == "0") {
							treeObj.reAsyncChildNodes(treeNode, "refresh");
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
								checkCascadeCheck(treeObj,treeNode.id,true);
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
								unCheckCascadeCheck(treeObj,treeNode.id,true);
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
								checkCascadeCheck(treeObj,treeNode.id,false);
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
								unCheckCascadeCheck(treeObj,treeNode.id,false);
							}
						}
					}
				}
		};
		treeObj = $.fn.zTree.init($("#tree"), setting);
		$("#treeContainer").height($("#center").height()-25);
		var btns=[];
		btns.push({
			text : '取消',
			onclick : f_close
		});
		btns.push({
			text : '保存',
			onclick : f_save
		});
		BIONE.addFormButtons(btns);
	});

	function f_close(){
		BIONE.closeDialog("config_obj");
	};

	//保存机构配置信息
	function f_save(){
		var nodes=checkNodes;
		var ids="";
		if(nodes.length>0){
			for(var i=0;i<nodes.length;i++){
				ids+=nodes[i].id+",";
			};
			ids=ids.substring(0, ids.length-1);
		}
		$.ajax({
			cache : false,
			async : true,
			url : "${ctx}/bione/admin/orggrp/saveorgobj",
			type : 'POST',
			data :{
				grpId : "${grpId}",
				ids : ids
			},
			success : function (){
				BIONE.tip("保存成功");
				f_close();
			},
			error : function (result,b){
				BIONE.tip("发现系统错误<BR>:"+result.status);
				f_close();
			}
		});
	};

	//获取已经被勾选的节点
	function getNodes(){
		$.ajax({
			cache:false,
			async:false,
			type:'POST',
			dataType:'json',
			url:"${ctx}/bione/admin/orggrp/gettreeobj?grpId=${grpId}",
			success: function(data){
				orgNos=data.orgNos;
				if(orgNos!=null){
					for ( var l in orgNos) {
						var node={};
						node.id = orgNos[l];
						if(getIndex(node,checkNodes)==-1)
							checkNodes.push(node);
					}
				}
			},
			error: function(result,b){
				BIONE.tip("发现系统错误<BR>:"+result.status);
			}
		});
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

	function initTreeInfo(){
		$.ajax({
			cache : false,
			// no async
			async : true,
			url :  "${ctx}/bione/admin/orgtree/getAllOrgTree",
			dataType : "json",
			type : "POST",
			success : function(result) {
				treeInfo = result.treeInfo;
			},
			error : function(result, b) {
			}
		});
		getNodes();
	}

	function getIndex(info,array){
		for(var i in array){
			if(array[i].id==info.id){
				return i;
			}
		}
		return -1;
	}

	function getUpOrgNo(id) {
		for ( var i in upOrgNos) {
			if (id == upOrgNos[i]) {
				return true;
			}
		}
		return false;
	}

	function getOrgNo(id) {
		for ( var i in orgNos) {
			if (id == orgNos[i]) {
				return true;
			}
		};
		return false;
	}

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
</script>
</head>
<body>
<div id="template.center">
		<div style="width:98%;height: 20px;background-color: #FFFFFF;border-bottom: solid 1px #D0D0D0;"><div style="text-align: left;width:98%; padding-left: 10px;padding-top: 2px;">级联方式： <input type="radio" id="level1" style="width:auto" name="level" value="level1" onclick=check() />全部级联<input type="radio" id="level3" name="level" value="level3" onclick=check()  style="width:auto"/>级联下级<input type="radio" id="level2" name="level" value="level2" onclick=check() checked="true" style="width:auto"/>不级联</div></div>
		<div id="treeContainer"
					style="width: 99%; overflow: auto; clear: both; background-color: #FFFFFF;">
					<ul id="tree"
						style="font-size: 12; background-color: #FFFFFF; width: 200px;"
						class="ztree"></ul>
		</div>
	</div>
</body>
</html>