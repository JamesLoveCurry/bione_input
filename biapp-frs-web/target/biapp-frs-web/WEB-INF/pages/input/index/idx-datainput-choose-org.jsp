<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<html>
<head>
<%@ include file="/common/taglibs.jsp"%>
<meta name="decorator" content="/template/template5.jsp">
<script type="text/javascript">
	var btns =[]; 
	var currentNode;//当前点击节点
	var checkEnable = true;
	var currentNode = null;
	var cascade = false;
	var rownum = '${rownum}';
	if("${checkbox}" == "0"){
		checkEnable = false;
	}
	$(function(){
		initTree();
		if(checkEnable == true){
			var radio = '<div style="width:100%;margin:auto;"><div style="text-align: center;">是否级联： 是 <input type="radio" id="level1"  name="level" value="level1" onclick=check() checked="true"/> 否 <input type="radio" id="level2" name="level" value="level2" onclick=check() /></div></div>';
			$(".form-bar").prepend(radio);
			$(".form-bar-inner").css("width", "10%").css("margin", "auto");
			check();
		}
		
		initBtn();
	});
	
	//初始化数
	function initTree() {
		
	var setting = {
			async : {
				enable : true,
				url : "${ctx}/report/frame/datashow/idx/getOrgTree?t="
						+ new Date().getTime(),
				autoParam : [ "id" ],
				dataType : "json",
				type : "post",
				dataFilter : function(treeId, parentNode, childNodes) {
					if (childNodes) {
						for ( var i = 0; i < childNodes.length; i++) {
							childNodes[i].isParent = true;
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
					if(cascade){
						if(treeNode.getCheckStatus().checked==true)
							leftTreeObj.reAsyncChildNodes(treeNode, "refresh");
						//rightTreeObj.expandNode(treeNode, true, true, true);

					}
				},
				onAsyncSuccess : function(event, treeId, treeNode, msg){
					if(treeNode!=null){
						if(cascade){
							if (treeNode.getCheckStatus().checked) {
								for ( var i in treeNode.children) {
									leftTreeObj.checkNode(treeNode.children[i], true, false);
								}
							}
						}
					}
				}
			}
		};
		leftTreeObj = $.fn.zTree.init($("#tree"), setting);
	}
	//加载树中数据
	function zTreeOnClick(event, treeId, treeNode) {
    	currentNode = treeNode;
	};
	function check() {
		cascade = $("#level1")[0].checked;
		if ($("#level1")[0].checked == true)
			leftTreeObj.setting.check.chkboxType = {
				"Y" : "s",
				"N" : "ps"
			};
		else
			leftTreeObj.setting.check.chkboxType = {
				"Y" : "",
				"N" : ""
			};
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
					if(!checkEnable){
						if(currentNode){
							c._changeValue(currentNode.id, currentNode.text);
						}else{
							BIONE.tip("请选择一个机构");
							return;
						}
					}else{
						var checkNodes = leftTreeObj.getCheckedNodes();
						var texts = [];
						var ids = [];
						for(var tmp in checkNodes){
							texts.push(checkNodes[tmp].text);
							ids.push(checkNodes[tmp].id);
						}
						window.parent.taskManage.templateManage.changeValue(ids, texts,rownum);
					}
					BIONE.closeDialog("chooseOrg");
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