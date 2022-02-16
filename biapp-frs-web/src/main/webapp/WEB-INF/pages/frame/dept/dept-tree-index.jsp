<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<%@ include file="/common/taglibs.jsp"%>
<%@ include file="/common/meta.jsp"%>
<html xmlns="http://www.w3.org/1999/xhtml">
<head>
<%@ include file="/common/jquery_load.jsp"%>
<%@ include file="/common/ligerUI_load.jsp"%>
<%@ include file="/common/zTree_load.jsp"%>
<script type="text/javascript">
	var leftTreeObj;
	var currentNode;
	$(function() {
		var settings = {
				data : {
				    keep : {
						parent : true
				    },
				    key : {
						name : "text"
				    }
				},
				view : {
				    selectedMulti : false,
				    showLine : true
				},
				callback : {
					beforeClick : beforeClick,
					onClick : f_clickNode, 
					onDblClick: zTreeOnDblClick,
					onAsyncSuccess: selectTreeNode//异步加载完成回调函数
				},
				async: {
					enable: true,
					url: "${ctx}/bione/admin/depttree/getDeptTree?orgNo=${id}",
					autoParam: ['id'],
					dataFilter: function(id, pNode, cNodes) {
						return cNodes;
					}
				}
			    };
		leftTreeObj = $.fn.zTree.init($("#tree"), settings);
    	$("#treeSearchIcon").live('click',function(){
    		leftTreeObj.setting.async.otherParam = ["search",$('#treeSearchInput').val()];
    		leftTreeObj.reAsyncChildNodes(null, 'refresh', false);
    	});
    	$('#treeSearchInput').bind('keydown', function(event) {
    		if (event.keyCode == 13) {
    			leftTreeObj.setting.async.otherParam = ["search",$('#treeSearchInput').val()];
        		leftTreeObj.reAsyncChildNodes(null, 'refresh', false);
     		}
    	});
    	
    	var btns = [
    				//{ text : "取消", onclick : function() { BIONE.closeDialog("orgWin"); } },	
    				{ text : "选择", onclick : function() {
    					if(!currentNode) {
    						BIONE.tip("请选择条线");
    						return;
    					}
    					choose(currentNode);
    					}
    				}
    		];
    		BIONE.addFormButtons(btns);
    		$('.form-bar-inner').css('margin-top', '7px');
    	
	});
	function zTreeOnDblClick(event, treeId, treeNode) {
		if(treeNode.id == "0"){
			BIONE.tip("该节点无效!");
			return false;
		}
		choose(treeNode);
		
	};
	 //树的点击前事件
	function beforeClick(treeId, treeNode, clickFlag) {
		if(treeNode.id == "0") {
			BIONE.tip("该节点无效!");
			return false;
		}
	}
	//树点击事件
	function f_clickNode(event, treeId, treeNode) {
		currentNode = treeNode;
	}
	function choose(treeNode){
		var dialogName = 'deptComBoBox';
		var main = parent || window;
		var dialog = main.jQuery.ligerui.get(dialogName);
		var c = main.jQuery.ligerui.get('deptNoID');
		c._changeValue(treeNode.id, treeNode.text);
		dialog.close();
	}
	//异步加载完成回调函数
	function selectTreeNode(event, treeId, treeNode, msg) {
		if(!treeNode){
			var pNode = leftTreeObj.getNodeByParam("id","0");
			leftTreeObj.expandNode(pNode, true);
		}
		
	}
</script>

<title>条线管理</title>
</head>
<body style="padding: 0px;">
	<div id="center"
		style="width: auto; border: solid 1px #D0D0D0; position: relative; top: 0; z-index: 0; overflow: hidden; margin: 0px auto 0px auto;">
		<div id="content" style="width: auto;background-color: #ffffff;">
		<ul style="width:50%;">
				<li style="width:100%;text-align:left;">                      
					<div class="l-text-wrapper" style="width: 100%;">                         
						<div class="l-text l-text-date" style="width: 100%;">                    
							<input id="treeSearchInput" type="text" class="l-text-field"  style="width: 100%;" />    
							<div class="l-trigger">                                                                      
								<div id="treeSearchIcon" style="width:100%;height:100%;background:url(${ctx}/images/classics/icons/find.png) no-repeat 50% 50% transparent;"></div>                                                 
							</div>
						</div>                                                                                                   
					</div>
				</li>
			</ul>  
			<div id="treeContainer"
				style="width: auto;height: 258px; overflow: auto; clear: both; background-color: #FFFFFF;">
				<ul id="tree"
					style="font-size: 12; background-color: #FFFFFF; width: 268px;"
					class="ztree"></ul>
			</div>
		</div>
	</div>
</body>
</html>