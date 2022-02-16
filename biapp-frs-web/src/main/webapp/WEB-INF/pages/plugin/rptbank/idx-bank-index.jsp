<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ include file="/common/taglibs.jsp"%>
<html>
<meta name="decorator" content="/template/template15.jsp">
<head>
<script type="text/javascript">
	var leftTreeObj = null;
	var tab = null;
	$(function() {
		initThemeTree();
		initTab();
		initbutten();
		$('#cover').show();
	});
	function initTab(){
		
		tab = $('#right_tab').ligerTab({
			contextmenu: true,
			height: $('#right').height() - 12,
			onAfterAddTabItem: function() {
				$('#cover').hide();
			},
			onAfterRemoveTabItem: function() {
				if (tab.getTabItemCount() == 0) {
					$('#cover').show();
				}
			}
		});
	}
	function initThemeTree(){
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
					fontCss: changeColor,
					selectedMulti : false
				},
				
				callback : {
					onClick : zTreeOnClick,
					onNodeCreated : function(event, treeId, treeNode) {
						if (treeNode.id == "ROOT") {
							//若是根节点，展开下一级节点
							leftTreeOb.expand(treeNode,true,true,true);
						}
					}
				}
		};
		leftTreeObj = $.fn.zTree.init($("#tree"), setting);
		BIONE.loadTree("${ctx}/frame/idx/bank/getThemeTree",leftTreeObj);
	}
	
	function initbutten(){
		var menuTheme = {
				width : 90,
				items : [ {
					icon : 'add',
					text : '添加',
					click : addTheme
				}, {
					line : true
				}, {
					icon : 'modify',
					text : '修改',
					click : editTheme
				}, {
					line : true
				}, {
					icon : 'delete',
					text : '删除',
					click : deleteTheme
				}]
			};
		
		$("#treeToolbar").ligerTreeToolBar({
			items : [ {
				icon : 'config',
				text : '主题',
				menu : menuTheme
			},{
				icon : 'settings',
				text : '默认',
				click : defaultTemp
			}],
			treemenu : false
		});
	}
	
	//改变树节点颜色
	function changeColor(treeId, treeNode){
		if(treeNode.data){
			return treeNode.data.isDefault == "Y" ? {color:"red"} : {};
		}
	}
	
	function addTheme(){
		BIONE.commonOpenDialog("新增指标主题", "editThemeWin", 500, 320, "${ctx}/frame/idx/bank/editTheme");
	}
	
	function refreshAllTree(){
		BIONE.loadTree("${ctx}/frame/idx/bank/getThemeTree",leftTreeObj);
	}
	
	function editTheme(){
		var treeNode = leftTreeObj.getSelectedNodes();
		if(treeNode.length <= 0){
			BIONE.tip("请选择一个主题");
			return;
		}
		if(treeNode[0].id == "0"){
			BIONE.tip("请选择一个主题");
			return;
		}
		var themeId = treeNode[0].id;
		BIONE.commonOpenDialog("修改指标主题", "editThemeWin", 500, 320, "${ctx}/frame/idx/bank/editTheme?themeId="+themeId);
	}
	
	function deleteTheme(){
		var treeNode = leftTreeObj.getSelectedNodes();
		if(treeNode.length <= 0){
			BIONE.tip("请选择一个主题");
			return;
		}
		if(treeNode[0].id == "0"){
			BIONE.tip("请选择一个主题");
			return;
		}
		$.ligerDialog.confirm("确定删除主题 [ " + treeNode[0].text + " ] ？",
				function(flag) {
					if (flag) {
						$.ajax({
							type : "POST",
							url : "${ctx}/frame/idx/bank/deleteTheme?themeId="+ treeNode[0].id,
							success : function(res) {
									BIONE.tip("删除成功！");
									//局部刷新树
									window.refreshAllTree();
							},
							error : function(XMLHttpRequest, textStatus,
									errorThrown) {
								BIONE.tip('删除失败,错误信息:' + textStatus);
							}
						});
					}
				});
	}
	
	function defaultTemp(){
		var treeNode = leftTreeObj.getSelectedNodes();
		if(treeNode.length <= 0){
			BIONE.tip("请选择一个主题");
			return;
		}
		if(treeNode[0].id == "0"){
			BIONE.tip("请选择一个主题");
			return;
		}
		var themeId = treeNode[0].id;
    	$.ajax({
			cache : false,
			async : false,
			url : "${ctx}/frame/idx/bank/defaultTemp",
			dataType : 'json',
			data : {
				themeId : themeId
			},
			type : "post",
			beforeSend : function() {
				BIONE.loading = true;
				BIONE.showLoading("正在加载数据中...");
			},
			complete : function() {
				BIONE.loading = false;
				BIONE.hideLoading();
			},
			success : function(result) {
				initThemeTree();
				BIONE.tip(treeNode[0].text + '已设置为默认主题');
			},
			error : function(result, b) {
				BIONE.tip('发现系统错误 <BR>错误码：' + result.status);
			}
		});
	}
	
	function zTreeOnClick(event, treeId, treeNode){
		if(treeNode.id != "0"){
			tab.addTabItem({
				tabid: treeNode.id,
				text: treeNode.text,
				url: '${ctx}/frame/idx/bank/list?themeId=' + treeNode.id,
				showClose: true
			});
		}
	}
</script>
</head>
<body>
	<div id="template.left.up.icon">
		<img src="${ctx }/images/classics/icons/application_side_tree.png" />
	</div>
	<div id="template.left.up">
		<span style="font-size: 12">主题信息</span>
	</div>
	<div id="template.right">
		<div style="position: relative;">
			<div id='tab_loading' class='l-tab-loading' style='display:none;'></div>
			<div id='cover' class='l-tab-loading' style='display:block; background:url(${ctx}/images/classics/index/bg_center.jpg) no-repeat center center #ffffff;'></div>
			<div id="right_tab"></div>
		</div>
	</div>
</body>
</html>