<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<html>
<head>
<%@ include file="/common/taglibs.jsp"%>
<meta name="decorator" content="/template/template5.jsp">
<script type="text/javascript">

	var orgType = "${orgType}";
	var btns =[]; 
	var currentNode;//当前点击节点
	var settingAsync = null;
	var settingSync = null;

	$(function(){
		var height = $(document).height();
		$("#center").height(height-42);
		$("#content").height(height-42);
		$("#treeContainer").height(height-68);
		$("#tree").height(height-80);

		initTree();
		initBtn();
	});
	
	//初始化数
	function initTree() {
		settingAsync = {
			async:{
				enable:true,
				url:"${ctx}/frs/systemmanage/orgmanage/getTree?orgType=${orgType}&t="+ new Date().getTime(),
				autoParam:["upOrgNo","orgNo","orgNm","mgrOrgNo"],
				dataType:"json",
				type:"get",
				dataFilter:function(treeId,parentNode,childNodes){
					if(childNodes){
						for(var i = 0;i<childNodes.length;i++){
							childNodes[i].orgType = childNodes[i].params.orgType;
							childNodes[i].upOrgNo = childNodes[i].params.upOrgNo;
							childNodes[i].orgNm = childNodes[i].params.orgNm;
							childNodes[i].orgNo = childNodes[i].params.orgNo;
							childNodes[i].mgrOrgNo = childNodes[i].params.mgrOrgNo;
							//	childNodes[i].searchNm=$("#treeSearchInput").val();
						}
					}
					return childNodes;
				}
			},
			data:{
				key:{
					name:"text"
				}
			},
			view:{
				selectedMulti:false
			},
			callback:{
				onClick:zTreeOnClick,
				beforeClick : function(treeId, treeNode, clickFlag){
					if(treeNode.id == "0"){
						BIONE.tip("不能选择根节点");
						return false;
					}
					return true;
				},
				onNodeCreated:function(event, treeId, treeNode){
					if(treeNode.id == "0"){
						leftTreeObj.reAsyncChildNodes(treeNode,"refresh");
					}
				}
			}
		};
		
		settingSync = {
				data:{
					key:{
						name:"text"
					},
					simpleData:{
						enable : true,
						idKey : "id",
						pIdKey : "upId",
						rootPId : null
					}
				},
				view:{
					selectedMulti : false
				},
				callback:{
					onClick:zTreeOnClick,
					beforeClick : function(treeId, treeNode, clickFlag){
						if(treeNode.id == "0"){
							BIONE.tip("不能选择根节点");
							return false;
						}
						return true;
					}
				}
		};
		
		$("#treeSearchIcon").bind("click",function(e){
			setTree($("#treeSearchInput").val() == "");
		});
		$("#treeSearchInput").bind("keydown",function(e){
			if(e.keyCode == 13){
				setTree($("#treeSearchInput").val() == "");
			}
		}); 
		
		//leftTreeObj = $.fn.zTree.init($("#tree"), setting);
		setTree(true);
	}
	
 	function setTree(flag){
		if(flag){
			leftTreeObj = $.fn.zTree.init($("#tree"), settingAsync);
		}
		else{
			leftTreeObj = $.fn.zTree.init($("#tree"), settingSync);
			loadTree("${ctx}/frs/systemmanage/orgmanage/searchOrgTree",leftTreeObj,{orgType:"${orgType}",orgNm : $("#treeSearchInput").val()});
		}
	} 
	
	//加载树中数据
	function loadTree(url, component, data) {
		$.ajax({
			cache : false,
			async : true,
			url : url,
			dataType : 'json',
			data : data,
			type : "POST",
			beforeSend : function() {
			},
			complete : function() {
				BIONE.loading = false;
				BIONE.hideLoading();
			},
			success : function(result) {
				var nodes = component.getNodes();
				var num = nodes.length;
				for ( var i = 0; i < num; i++) {
					component.removeNode(nodes[0], false);
				}
				if (result.length > 0) {
					component.addNodes(null, result, false);
					component.expandAll(true);
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
				BIONE.closeDialog("chooseOrg");
			}
		}, {
			text : "选择",
			onclick : function() {
				if(currentNode != null){
					main = window.parent;
					var orgNo = main.selectTab?main.selectTab.$("#mainform [name='orgNo']").val():null;
					if(orgNo==currentNode.id){
						BIONE.tip("本机构不能是本机构的上级机构!");
						return;
					}
					var c= main.selectTab?main.selectTab.$.ligerui.get("upOrgNoCom"):main.$.ligerui.get("orgNm");
					c._changeValue(currentNode.id, currentNode.text);
					BIONE.closeDialog("chooseOrg");
				}else{
					BIONE.tip("请选择上级机构");
				}
			}
		});
		BIONE.addFormButtons(btns);
	}
	
</script>
</head>
<body>
	<div id="template.center">
		<div class="content" style="border: 1px solid #C6C6C6; %">
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
				style="width: 100%; height: 100%; padding-top:2px; overflow: auto; clear: both;">
				<ul id="tree"
					style="font-size: 12; background-color: F1F1F1; width: 90%"
					class="ztree"></ul>
			</div>
		</div>
	</div>
</body>
</html>