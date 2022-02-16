<%@ page language="java" pageEncoding="UTF-8"%>
<%@ include file="/common/taglibs.jsp"%>
<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN">
<html>
<head>
<meta name="decorator" content="/template/template5.jsp">
<script type="text/javascript">
    var groupicon = "${ctx}/images/classics/icons/communication.gif";
    var checkMap={};
    var treeObj=null;
    var treeInfo={};
    var cascade=true;
    var checkNodes=[];
    var ids;
    var searchNm="";
    var urlPath="";
    if("${type}"=="01"){
    	urlPath="${ctx}/rpt/rpt/rptplatshow/getRptTreeSyncView";
	}if("${type}"=="03"){
		urlPath="${ctx}/report/frame/idx/getSyncTree";
	}

    
    function initForm(){
    	var mainform = $("#form").ligerForm({
		    fields : [ {
		    	display : '任务组编号',
				name : 'taskNo',
				type : 'text',
				validate : {
				    required : true,
				    maxlength : 32,
				    remote :  {
	        			url : "${ctx}/report/frame/taskobjrel/checkValidate",
						type : "POST",
						data : {
							"taskNo" : $("#taskNo").val()
						},
						messages : {
							remote : "该任务组编号已存在"
						}
	        		}
				},
				group : "任务信息",
				groupicon : groupicon
		    }, {
				display : '任务名称',
				name : 'taskNm',
				newline : true,
				type : 'text',
				validate : {
				    required : true,
				    maxlength : 32
				}
		    }]
		});

		jQuery.metadata.setType("attr", "validate");
		BIONE.validate($("#form"));
	
		var managers = $.ligerui.find($.ligerui.controls.Input);
		for ( var i = 0, l = managers.length; i < l; i++) {
		    //改变了表单的值，需要调用这个方法来更新ligerui样式
		    managers[i].updateStyle();
		}

		var buttons = [];
	
		buttons.push({
		    text : '取消',
		    onclick : function() {
			BIONE.closeDialog("editWin");
		    }
		});
		buttons.push({
		    text : '保存',
		    onclick : f_save
		});
		BIONE.addFormButtons(buttons);
    }

    function initData(){
    	if("${id}"!=""){
    		$.ajax({
    			async: false,
    			type : "POST",
    			url : "${ctx}/report/frame/taskobjrel/getInfo",
    			data:{
    				type : "${type}",
    				taskNo : "${id}"
    			},
    			dataType : "json",
    			success : function(result) {
    				checkMap=result.checkMap;
    				for(var i in checkMap){
    					checkNodes.push(i);
    				}
    				$("#taskNo").val("${id}");
    				$("#taskNm").val(result.taskNm);
    			} 
    		});
    		$("#taskNo").attr("readOnly","true");
    		$("#taskNo").removeAttr("validate");
    	}
    }

	function check(value){
		if($("#level1")[0].checked){
			cascade=true;
			treeObj.setting.check.chkboxType= { "Y": "ps", "N": "ps" };
		}
		if($("#level2")[0].checked){
			cascade=false;
			treeObj.setting.check.chkboxType= { "Y": "", "N": "" };
		}
	}



	$(function() {
		initForm();
		initData();
		var height = $(window).height();
		var radio = '<div style="width:100%;margin:auto;"><div style="text-align: center;">是否级联： 是 <input type="radio" id="level1"  name="level" value="level1" onclick=check() checked="true"/> 否 <input type="radio" id="level2" name="level" value="level2" onclick=check() /></div></div>';
		$("#bottom").prepend(radio).css("height","60px");
		$("#center").height(height - 60-3);
		var $treeContainer = $("#treeContainer");
		$treeContainer.height($(window).height() - 80-$("#form").height());
		initTree();
		refreshTree();
		function loadRptTree(){
			$.ajax({
				cache : false,
				async : true,
				url :urlPath,
				dataType : 'json',
				type : "post",
				data : {
					defSrc : "${type}",
					searchNm: searchNm
				},
				success : function(result){
					if(result != null
							&& typeof result != "undefined"){
						// 移除旧节点
						treeObj.removeChildNodes(treeObj.getNodeByParam("id", '0', null));
						treeObj.removeNode(treeObj.getNodeByParam("id", '0', null),false);
						// 渲染新节点
						treeObj.addNodes(treeObj.getNodeByParam("id", '0', null) , result , true);
						treeObj.expandAll(true);
					}
				},
				error:function(){
					BIONE.tip("加载失败，请联系系统管理员");
				}
			});
		}
		function loadIdxTree(){
			$.ajax({
				cache : false,
				async : true,
				url :urlPath,
				dataType : 'json',
				type : "post",
				data : {'isShowIdx':'1','isShowMeasure':0,'isEngine':1,'isPublish':1,"searchNm":searchNm},
				success : function(result){
					if(result != null
							&& typeof result != "undefined"){
						// 移除旧节点
						treeObj.removeChildNodes(treeObj.getNodeByParam("id", '0', null));
						treeObj.removeNode(treeObj.getNodeByParam("id", '0', null),false);
						// 渲染新节点
						treeObj.addNodes(treeObj.getNodeByParam("id", '0', null) , result , true);
						treeObj.expandAll(true);
					}
				},
				error:function(){
					BIONE.tip("加载失败，请联系系统管理员");
				}
			});
		}

		function initTree(){
			var setting = {
				data : {
					key : {
						name : "text",
						title : "title"
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
				check: {
					enable: true,
					chkStyle: "checkbox",
					chkboxType: { "Y": "ps", "N": "ps" }
				},
				callback : {
					onClick: zTreeOnClick,
					onCheck: function (event, treeId, treeNode) {
						if (!treeNode.ischecked) {
							if("${type}"=="01"){
								if (treeNode.params.isLeaf == "true") {
									checkNodes.push(treeNode.params.realId);
								} else {
									var childNodes = treeNode.children;
									for (var node in childNodes) {
										if (childNodes[node].params.isLeaf == "true") {
											var index=checkNodes.indexOf(childNodes[node].params.realId)
											if (index != -1) {
												checkNodes.splice(index, 1);
											}else{
												checkNodes.push(childNodes[node].params.realId)
											}
										}
									}
								}
							}if("${type}"=="03"){
								if (treeNode.params.isLeaf == "true") {
									checkNodes.push(treeNode.params.id);
								} else {
									var childNodes = treeNode.children;
									for (var node in childNodes) {
										if (childNodes[node].params.isLeaf == "true") {
											var index=checkNodes.indexOf(childNodes[node].params.id)
											if (index != -1) {
												checkNodes.splice(index, 1);
											}else{
												checkNodes.push(childNodes[node].params.id)
											}
										}
									}
								}
							}
						}else{
							if("${type}"=="01"){
								if(treeNode.params.isLeaf=="false"){
									var childNodes=treeNode.children;
									for (var node in childNodes) {
										var index=checkNodes.indexOf(childNodes[node].params.realId)
										if (index != -1) {
											checkNodes.splice(index, 1);
										}
									}
								}else{
									var index=checkNodes.indexOf(treeNode.params.realId)
									if (index != -1) {
										checkNodes.splice(index, 1);
									}
								}
							}if("${type}"=="03"){
								if(treeNode.params.isLeaf=="false"){
									var childNodes=treeNode.children;
									for (var node in childNodes) {
										if(childNodes[node].params.isLeaf=="true"){
											var index=checkNodes.indexOf(childNodes[node].params.id)
											if (index != -1) {
												checkNodes.splice(index, 1);
											}
										}
									}
								}else{
									var index=checkNodes.indexOf(treeNode.params.id)
									if (index != -1) {
										checkNodes.splice(index, 1);
									}
								}
							}
						}
					},
					onNodeCreated:function (event,treeId,treeNode){
						if(checkNodes!=null && checkNodes.length>0){
							if("${type}"=="01"){
								for (var i in checkNodes) {
									if(treeNode.params.nodeType=="03"){// 报表节点
										if(treeNode.params.realId ==checkNodes[i]){
											treeNode.checked=true;
											treeNode.ischecked=true;
											treeObj.checkNode(treeNode, true, true);
										}
									}
								}
							}if("${type}"=="03"){
								for (var i in checkNodes) {
									if(treeNode.params.nodeType=="idxInfo"){
										if(treeNode.params.id ==checkNodes[i]){
											treeNode.checked=true;
											treeNode.ischecked=true;
											treeObj.checkNode(treeNode, true, true);
										}
									}
								}
							}
						}
					}
				}
			};
			treeObj = $.fn.zTree.init($("#tree") , setting);
			if("${type}"=="01"){
				$("#treeSearchIcon").bind("click",function (){
					searchNm = $("#treeSearchInput").val();
					loadRptTree();
				});
				$("#treeSearchInput").bind("keydown",function (e){
					if(e.keyCode==13){
						searchNm = $("#treeSearchInput").val();
						loadRptTree();
					}
				});
			}
			else if("${type}"=="03"){
				$("#treeSearchIcon").bind("click",function (){
					searchNm = $("#treeSearchInput").val();
					loadIdxTree();
				});
				$("#treeSearchInput").bind("keydown",function (e){
					if(e.keyCode==13){
						searchNm = $("#treeSearchInput").val();
						loadIdxTree();
					}
				});
			};
		}

		function zTreeOnClick(event, treeId, treeNode) {
			currentNode = treeNode;
		}

		function refreshTree() {  //刷新树
			if (treeObj) {
				if("${type}"=="01"){
						loadRptTree();
					}
				else if("${type}"=="03"){
					loadIdxTree();
				}
			}
		}
	});

	function getChecked(){
		var ids=[];
		if("${type}"=="01"){
			var nodes = treeObj.getCheckedNodes(true);
			for(var i in nodes){
				if(nodes[i].params.nodeType=="03"){
					ids.push(nodes[i].params.realId);
				}
			}
			return ids.join(",");
		}
		if("${type}"=="03"){
			var nodes = treeObj.getCheckedNodes(true);
			for(var i in nodes){
				if(nodes[i].params.nodeType=="idxInfo"){
					ids.push(nodes[i].id);
				}
			}
			return ids.join(",");
		}
	}

	function f_save() {
		if($("#taskNo").val()==""){
			BIONE.tip("请填写任务组编号");
			return;
		}
		if($("#taskNm").val()==""){
			BIONE.tip("请填写任务组名称");
			return;
		}
		var ids=getChecked();
		if(ids==""){
			if("${type}"=="01")
				BIONE.tip("请选择报表");
			if("${type}"=="03")
				BIONE.tip("请选择指标");
			return;
		}
		$.ajax({
			type : "POST",
			url : "${ctx}/report/frame/taskobjrel/create",
			data:{
				ids : ids,
				type : "${type}",
				id : "${id}",
				taskNm : $("#taskNm").val(),
				taskNo : $("#taskNo").val()
			},
			dataType : "json",
			success : function(result) {
				if(result.msg){
					BIONE.tip(result.msg);
				}else{
					BIONE.closeDialogAndReloadParent("editWin", "maingrid",
							"保存成功");
				}
			}
		});
	}
</script>
</head>
<body>
	<div id="template.center">
		<form name="form" method="post" id="form" action="">
		</form>
		<div id="treeSearchbar" style="width: 98%;height: 20px;margin-top: 2px;padding-left: 2px;">
			<ul>
				<li style="width: 100%;text-align: left;">
					<div class="l-text-wrapper" style="width: 100%">
						<div class="l-text l-text-date" style="width: 100%;">
							<input id="treeSearchInput" type="text" class="l-text-field" style="width: 98%"/>
							<div class="l-trigger">
								<i id="treeSearchIcon" class="icon-search search-size"></i>
							</div>
						</div>
					</div>
				</li>
			</ul>
		</div>
		<div id="treeContainer"
			style="width: 100%; overflow: auto; clear: both; background-color: #FFFFFF;">
			<ul id="tree"
				style="font-size: 12px; background-color: #FFFFFF; width: 92%"
				class="ztree"></ul>
		</div>
	</div>
</body>
</html>