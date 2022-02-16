<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ include file="/common/taglibs.jsp"%>
<html>
<head>
<meta name="decorator" content="/template/template12A.jsp">
<script type="text/javascript" src="${ctx}/js/datainput/UDIP.js"></script>
<script type="text/javascript">
	var libId_date = "${libId}".split(';;');
	var name, id;
	$(function() {
		BIONE.showLoading("正在加载中...");
		var height = $(document).height();
		if (libId_date[1] != 'query' && !"${formType}") {
			BIONE.addFormButtons({
// 				appendTo : ".form-bar",
				text : '导出',
				icon : 'export',
				onclick : function() {
					if (libId_date[1] != '') {
						if (!document.getElementById('download')) {
							$('body').append($('<iframe id="download" style="display:none"/>'));
						}
						$("#download").attr('src', '${ctx}/rpt/input/library/excel_down?id=' + libId_date[0] + '&dataDate=' + libId_date[1]);
					} else {
						if (!document.getElementById('download')) {
							$('body').append($('<iframe id="download" style="display:none"/>'));
						}
						$("#download").attr('src', '${ctx}/rpt/input/library/excel_down?id=' + libId_date[0]);
					}
				}
			});
			$("#center").height(height-10);
			$("#content").height(height-10);
			$("#treeContainer").height(height-10);
		} else if("${formType}"){
			initTreeSearch();
			
			BIONE.addFormButtons({
				text : '选择',
				icon : 'true',
				onclick : function() {
					//$("#"+"${formType}").focusout();
					parent.setDataZidina(name, id, "${formName}", "${formType}");
					BIONE.closeDialog("libraryAddWin"); 
				}
			});
			$("#center").height(height);
			$("#content").height(height);
			$("#treeContainer").height(height);
		}else{
			$("#center").height(height+60);
			$("#content").height(height+60);
			$("#treeContainer").height(height+60);
		}
		if (libId_date[1] == 'query') {
			libId_date[1] = '';
		}
		initTree();
	});

	function initTreeSearch(){
	    $("#treeSearchbar").show();
		$("#treeSearchIcon").live(
				'click',function(){
					searchTreeNode();
		});
		
		$("#treeSearchInput").live('keydown',function(){
			if (event.keyCode == 13) {
				searchTreeNode();
			}
		});
	}

	function searchTreeNode(){
		var searchText = $("#treeSearchInput").val();
		var tree = window['orgTreeInfo'];
		var searchNode = tree.getNodesByFilter(function(node){
			return node.text.indexOf(searchText) != -1;
		});

		var nodes = tree.getNodes();
		for(var i = nodes.length - 1; i >= 0 ; i--){
			tree.removeNode(nodes[i]);
		}
		if(searchText == ''){
			initNodes();
		}else{
			tree.addNodes(null,searchNode);
		}
	}
	
	function initTree() {
		window['orgTreeInfo'] = $.fn.zTree.init($("#tree"), {
			data : {
				keep : {
					parent : true
				},
				key : {
					name : "text",
					title: "id" 
				},
				simpleData : {
					enable : true,
					idKey : "id",
					pIdKey : "upId",
					rootPId : null
				}
			},
			view : {
				showLine : false
			},
			callback : {
				onClick : function(event, treeId, treeNode) {
					id= treeNode.id;
					name = treeNode.text;
				},
				onDblClick : function(event, treeId, treeNode){
					parent.setDataZidina(treeNode.text, treeNode.id, "${formName}", "${formType}");
					BIONE.closeDialog("libraryAddWin"); 
				}
			}
		});

		initNodes();
	}

	function initNodes(){
		$.ajax({
			cache : false,
			async : true,
			url : "${ctx}/rpt/input/library/libTreeByLibId.json?libId=" + libId_date[0] + "&dataDate=" + libId_date[1]+"&orgId="+"${orgId}",
			success : function(result) {
				orgTreeInfo.addNodes(null, result, false);
				/**选中已选值**/
				if ("${formName}") {
					var selectValue = parent.document.getElementById("${formType}").value;
					var values = selectValue.split(',');
					$.each(values, function(i, n) {
						var selectNode = orgTreeInfo.getNodesByParam('text', n)[0];//选中指定的节点
						if (selectNode) {
							orgTreeInfo.selectNode(selectNode);
							return false;
						}
					});
				}
				BIONE.hideLoading();
			},
			error : function(result, b) {
				BIONE.tip('发现系统错误 <BR>错误码：' + result.status);
				BIONE.hideLoading();
			}
		});

	}
</script>
</head>
<body>
</body>
</html>