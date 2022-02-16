<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<%@ include file="/common/meta.jsp"%>
<%@ include file="/common/taglibs.jsp"%>
<html xmlns="http://www.w3.org/1999/xhtml">
<head>
<%@ include file="/common/jquery_load.jsp"%>
<%@ include file="/common/ligerUI_load.jsp"%>
<%@ include file="/common/zTree_load.jsp"%>
<style type="text/css">
#center {
	position: relative;
	top: 0;
	width: 100%;
	z-index: 0;
	overflow: hidden;
	margin-left: auto;
	margin-right: auto;
}
</style>
<script type="text/javascript">
	var treeObj = null;
	var cascade = false;
	var treeInfo = null;
	var checkNodes = [];
	var treeType = "rpt";
	var checkRptNodes = [];
	var checkedCata = [];//这个只盛放目录节点
	var checkedRpt = [];//这个只盛放报表节点
	var rptTreeNodeIcon = "${ctx}/images/classics/icons/layout_sidebar.png";
	
	var moduleType = "${orgType}";
	$(function() {
		var height = $(document).height();
		$("#center").height(height - 42);
		$("#content").height(height);
		$("#treeContainer").height(height - 60 - 2);
		$("#tree").height(height - 60 - 2);
		initCheck();
		initBtn();
	});
	function getReturnNodes() {
		var nodes = treeObj.getCheckedNodes();
		var cataNm = "";
		var rptNm = "";
		if(nodes.length>0){
			for(var i=0;i<nodes.length;i++){
				//1是文件夹，2是报表
				if(nodes[i].nodeType == "TSKRPT"){
					rptNm += nodes[i].id + ",";
				}else{
					cataNm += nodes[i].id + ",";
				}
			}
		}
		var data = {
			cataRpt : cataNm,
			rptRpt : rptNm,
			moduleType : moduleType
		}
		var url = "${ctx}/rpt/frs/rptexport/saveWatchRpt";
		$.ajax({
			cache : false,
			async : true,
			url : url,
			dataType : 'json',
			type : "post",
			data : data,
			success : function(result) {
				BIONE.tip('保存成功');
				BIONE.closeDialog("setWatchWin", null, false);
			},
			error : function(result, b) {
				BIONE.tip('发现系统错误 ,请联系管理员');
			}
		});
	}

	function initBtn() {
		var btns = [ {
			text : "取消",
			onclick : function() {
				BIONE.closeDialog("setWatchWin", null, false);
			}
		}, {
			text : "确定",
			onclick : getReturnNodes
		} ];
		BIONE.addFormButtons(btns);
	}

	//复选的数据
	function initCheck(){
		var data = {
				moduleType : moduleType
		}
		var url = "${ctx}/rpt/frs/rptexport/getWatchRpt";
		$.ajax({
			cache : false,
			async : true,
			url : url,
			dataType : 'json',
			type : "get",
			data : data,
			success : function(result) {
			 	if(result.length>0){
			 		for(var i=0;i<result.length;i++){
			 			checkNodes.push(result[i]);
			 			
			 			if (getIndex(result[i].id, checkedCata) == -1){
			 				checkedCata.push(result[i].cataId);
			 			}
			 			checkedRpt.push(result[i].id.rptId);
			 		}
			 		
			 	}
			 	inintTree();
			},
			error : function(result, b) {
				BIONE.tip('发现系统错误 ,请联系管理员');
			}
		});
	}
	function inintTree(){
		var setting = {
				async : {
					enable : true,
					url : this.getAsyncUrl,
					autoParam : [ "rid","tskType","dataDate" ],
					otherParam:{'moduleType': moduleType,'onlyWatch':null},
					dataType : "json",
					type : "get",
					dataFilter : function(treeId, parentNode, childNodes) {
						if (childNodes) {
							for ( var i = 0; i < childNodes.length; i++) {
								childNodes[i].rid = childNodes[i].params.rid;
								childNodes[i].nodeType = childNodes[i].params.nodeType;
								childNodes[i].tskType = childNodes[i].params.tskType;
								childNodes[i].dataDate = childNodes[i].params.dataDate;
								
								if (childNodes[i].params.nodeType == "TSKRPT") {
									if(getIndex(childNodes[i], checkedRpt) != -1){
										childNodes[i].checked = true;
									}
									childNodes[i].icon = rptTreeNodeIcon;
								}else{
									if (getIndex(childNodes[i], checkedCata) != -1 || childNodes[i].id == "0"){
										childNodes[i].checked = true;
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
				check : {
					enable : true,
					chkStyle : "checkbox",
					chkboxType : {"Y":"ps","N":"ps"}
				},
				callback : {
					onNodeCreated : function(event, treeId, treeNode) {
						if (treeNode.nodeType == "ROOT") {
							//若是根节点，展开下一级节点
							treeObj.reAsyncChildNodes(treeNode, "refresh");
						}
					},
					onCheck : function(event, treeId, treeNode) {
						 if (treeType == "rpt") {
							if (cascade) { //级联
								if (treeNode.getCheckStatus().checked) {
									addCurrentNode(treeNode, checkRptNodes); //当前节点
									checkCascade(treeNode.id, checkRptNodes,
											treeType); //当前节点所有下级节点
								} else {
									removeCurrentNode(treeNode, checkRptNodes);
									unCheckCascade(treeNode.id, checkRptNodes,
											treeType);
								}
							} else { //非级联
								if (treeNode.getCheckStatus().checked) {
									addCurrentNode(treeNode, checkRptNodes);
								} else {
									removeCurrentNode(treeNode, checkRptNodes);
								}
							}
		
						}
					}
				}
			};
			treeObj = $.fn.zTree.init($("#tree"), setting);
	}
	
	getAsyncUrl = function (treeId, treeNode) {
		if (!treeNode || treeNode.params.nodeType == "ROOT") {
			return "${ctx}/rpt/frs/rptfill/getTaskTree?t="+ new Date().getTime();
		} else {
			return "${ctx}/rpt/frs/rptfill/getTaskRptTree?t="+ new Date().getTime();
		}
	};
	function getIndex(info, array) {
		if(array[i]){
			for ( var i in array) {
				if (array[i].id == info.id) {
					return i;
				}
			}
		}else{
			for(var i in array){
				if(array[i] == info.id){
					return i;
				}
			}
		}
		return -1;
	}
	
	//添加当前节点
	function addCurrentNode(treeNode, nodes) {
		var node = {};
		node.id = treeNode.id;
		node.text = treeNode.text;
		node.params = treeNode.params;
		if (getIndex(node, nodes) == -1)
			nodes.push(node);
	}
	
	//添加当前节点下级节点
	function checkCascade(id, checkedNodes, treeType) {
		var nodes = null;
		 if (treeType == "rpt") {
			nodes = rptTreeInfo[id];
		}

		if (nodes != null && nodes.length > 0) {
			for ( var i in nodes) {
				var node = {};
				node.id = nodes[i].id;
				node.text = nodes[i].text;
				node.params = nodes[i].params;
				if (getIndex(node, checkedNodes) == -1)
					checkedNodes.push(node); //添加节点
				checkCascade(node.id, checkedNodes, treeType); //递归处理
			}
		}
	}

	//移除当前节点下级节点
	function unCheckCascade(id, checkedNodes, treeType) {
		var nodes = null;
		if (treeType == "rpt") {
			nodes = rptTreeInfo[id];
		}

		if (nodes != null && nodes.length > 0) {
			for ( var i in nodes) {
				var node = {};
				node.id = nodes[i].id;
				node.text = nodes[i].text;
				node.params = nodes[i].params;
				var index = getIndex(node, checkedNodes);
				if (index >= 0) {
					checkedNodes.splice(index, 1); //移除节点
				}
				unCheckCascade(node.id, checkedNodes, treeType); //递归处理
			}
		}
	}
	
	//是否级联选择
	function check() {
		cascade = $("#level1")[0].checked;
		if ($("#level1")[0].checked == true)
			treeObj.setting.check.chkboxType = {
				"Y" : "ps",
				"N" : "ps"
			};
		else
			treeObj.setting.check.chkboxType = {
				"Y" : "p",
				"N" : "p"
			};
	}
</script>
<title></title>
</head>
<body style="padding: 0px 0px 0px 0px;">
	<div id="center" style="width: 278px; border: solid 1px #D0D0D0;">
		<div id="content" style="width: 278px;">
			<div
				style="width: 100%; height: 20px; background-color: #FFFFFF; border-bottom: solid 1px #D0D0D0;">
				<div
					style="text-align: left; width: 100%; padding-left: 10px; padding-top: 2px;">
					是否级联： 是 <input type="radio" id="level1" name="level" value="level1"
						onclick=check() /> 否 <input type="radio" id="level2" name="level"
						value="level2" onclick=check() checked="true" />
				</div>
			</div>
			<div id="treeContainer"
				style="width: 278px; overflow: auto; clear: both; background-color: #FFFFFF;">
				<ul id="tree"
					style="font-size: 12; background-color: #FFFFFF; width: 268px;"
					class="ztree"></ul>
			</div>
		</div>
	</div>
	<div id="bottom">
		<div class="form-bar">
			<div class="form-bar-inner" style="padding-top: 5px"></div>
		</div>
	</div>
</body>
</html>