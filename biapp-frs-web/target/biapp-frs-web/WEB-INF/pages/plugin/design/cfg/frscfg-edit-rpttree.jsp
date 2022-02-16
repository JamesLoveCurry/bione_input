<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<html>
<head>
<%@ include file="/common/taglibs.jsp"%>
<meta name="decorator" content="/template/template5.jsp">
<script type="text/javascript">
	 
	var mainRptId = "${mainRptId}";
	var rptTreeNodePre = "r";   // 报表树节前缀
	var currentNode;            //当前点击节点
	var btns =[];
	var leftTreeObj = null;
	
	$(function(){
		var height = $(document).height();
 		$("#center").height(height-42);
		$("#content").height(height-44);
		$("#treeContainer").height(height-66);
		$("#tree").height(height-80); 
		
		if(mainRptId == null || mainRptId == ""){
			mainRptId = window.parent.selectedMainRpt.$("#mainRptId").val();
		}
		initTree();
		initBtn();
	});
	
	//初始化数
	function initTree() {
		var setting = {
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
					selectedMulti : false,
					showLine : false
				},
				async : {
					enable : true,
					dataFilter : function(treeId, parentNode, childNodes) {
						//由于采用了同步加载, 此部分不起作用
						if (childNodes) {
							if(childNodes[i].params.isParent == "false" ){
								childNodes=null;
							}
							else{
								if(childNodes[i].params.nodeType == "02" ){
									childNodes.isParent="false";
								}
							}
						}
						return childNodes;
					}
				},
				callback : {
					onClick : zTreeOnClick,
					onNodeCreated : function(event, treeId, treeNode) {
						if (treeNode.id == "ROOT") {
							//若是根节点，展开下一级节点
							leftTreeObj.expand(treeNode, true, true, true);
						}
					}
				}
			};
		
		//查询
		$("#treeSearchIcon").bind("click", function(e){
			loadTree(leftTreeObj);
		});
		$("#treeSearchInput").bind("keydown", function(e){
			if(e.keyCode == "13"){
				loadTree(leftTreeObj);
			}
		});
		
		leftTreeObj = $.fn.zTree.init($("#tree"), setting);
		loadTree(leftTreeObj);
	}
	
	//加载树中数据
	function loadTree(treeObj) {
		$.ajax({
			cache : false,
			async : true,
			url : "${ctx}/report/frame/design/cfg/getRptTree",
			dataType : 'json',
			type : "POST",
			data : {searchNm : $("#treeSearchInput").val()},
			beforeSend : function() {
			},
			complete : function() {
				BIONE.loading = false;
				BIONE.hideLoading();
			},
			success : function(result) {
				var nodes = treeObj.getNodes();
				var num = nodes.length;
				for ( var i = 0; i < num; i++) {
					treeObj.removeNode(nodes[i], false);      //如果树上有节点, 则先移除树上的节点
				}
				if (result.length > 0) {
					treeObj.addNodes(null, result, false);
					//treeObj.expandAll(true);
					if(mainRptId != null && mainRptId != ""
							&& typeof mainRptId != "undefined"){
						var selNode = treeObj.getNodeByParam("id", rptTreeNodePre+mainRptId, null);
						if(selNode){
							treeObj.selectNode(selNode , false);
						}
					}
				}
			},
			error : function(result, b) {
				//BIONE.tip('发现系统错误 <BR>错误码：' + result.status);
			}
		});
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
				BIONE.closeDialog("rptTreeWin");
			}
		}, {
			text : "选择",
			onclick : function() {
				if (typeof currentNode == 'undefined') {
					BIONE.tip("请选择目录节点");
					return;
				}
				//判断所选节点是否为报表节点, 目录节点id开始字母为f, 报表id开始字母为r, 根节点为0
				var strId = currentNode.id.substr(0,1);  
				if (strId !="0" && strId != "f") {  
					var main = window.parent.selectedMainRpt;
					main.$("#mainRptId").val(currentNode.params.realId);
					main.$("#mainRptNmCombo").val(currentNode.text);
					main.$("#mainRptNm").val(currentNode.text);
					main.mainRptId = currentNode.params.realId;
					BIONE.closeDialog("rptTreeWin");
				} else {
					BIONE.tip("请选择一个报表节点");
				}
			}
		});
		BIONE.addFormButtons(btns);
	}

</script>
</head>
<body>
	<div id="template.center">
		<div id="content" class="content" style="border: 1px solid #C6C6C6; ">
			<div id="treeSearchbar" style="width:99%; height:20px; margin-top:2px; padding-left:2px;">
				<ul>
					<li style="width:100%;text-align:left;">                      
						<div class="l-text-wrapper" style="width: 100%;">                         
							<div class="l-text l-text-date" style="width: 100%;">                    
								<input id="treeSearchInput" type="text" class="l-text-field"  style="width: 100%; padding-top:5px;" />    
								<div class="l-trigger">                                                                      
									<div id="treeSearchIcon" style="width:100%; height:100%; background:url(${ctx}/images/classics/icons/find.png) no-repeat 50% 50% transparent;"></div>                                                 
								</div>
							</div>                                                                                                   
						</div>
					</li>
				</ul>                                                                                                         
			</div>
			<div id="treeContainer"
				style="height: 100%; overflow: auto; clear: both;">
				<ul id="tree"
					style="font-size: 12; background-color: F1F1F1; width: 90%"
					class="ztree">
				</ul>
			</div>
		</div>
	</div>
</body>
</html>