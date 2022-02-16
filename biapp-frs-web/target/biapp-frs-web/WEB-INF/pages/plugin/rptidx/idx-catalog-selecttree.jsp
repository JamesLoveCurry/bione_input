<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<%@ include file="/common/taglibs.jsp"%>
<html>
<head>
<meta name="decorator" content="/template/template12.jsp">
<script type="text/javascript">
	var url, selectTreeObj, treeNode_;
	var catalogId = '${catalogId}';
	var defSrc = '${defSrc}';
	$(function() {
		url = "${ctx}/report/frame/idx/listCatalogTree.json";
		if(defSrc){
			url+="?defSrc="+defSrc;
		}
		beforeTree();
		initTree();
		selectButton();

	});
	function templateShow() {
		var height = $(document).height();
		$("#center").height(height - 40);
		$("#content").height(height - 40);
		$("#treeContainer").height(height - 40);
	}
	function beforeTree() {
		var setting = {
			data : {
				key : {
					name : 'text'
				},
				simpleData : {
					enable : true,
					idKey : "id",
					pIdKey : "upId"
				}
			},
			callback : {
				beforeClick : beforeClick,
				onClick : onClick
			},
			view : {
				selectedMulti : false,
				showLine : false,
				expandSpeed : "fast"
			},
			check : {
				chkStyle : "radio",
				enable : true,
				radioType : "all"
			}
		};
		selectTreeObj = $.fn.zTree.init($("#tree"), setting);
	}

	function initTree() {
		$.ajax({
			cache : false,
			async : true,//同步
			url : url,
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
				if (result) {
					for ( var i in result) {
						if (result[i].params.open) {
							result[i].open = true;
						} else {
							result[i].open = false;
						}
					}
				}
				selectTreeObj.addNodes(null, result, true);
				selectTreeObj.expandAll(true);
				var baseNode = selectTreeObj.getNodes()[0];
				var id = baseNode.tId;
				$("#" + id).find(".chk#" + id + "_check").remove();
				showBack();
				templateShow();
			},
			error : function(result, b) {
				BIONE.tip('发现系统错误 <BR>错误码：' + result.status);
			}
		});
	}
	function showBack(){
		if(selectTreeObj.getNodeByParam("id",catalogId, null)){
    		selectTreeObj.checkNode(selectTreeObj.getNodeByParam("id",catalogId, null), true, true);
		}
	}
	function selectButton() {
		var buttons = [];
		buttons.push({
			text : '选择',
			onclick : selected
		});
		BIONE.addFormButtons(buttons);
	}

	function beforeClick(treeId, treeNode, clickFlag) {
		return false;
	}

	function onClick(event, treeId, treeNode, clickFlag) {
		treeNode_ = treeNode;
	}
	function selected() {
		var radioId = $(".radio_true_full").attr("id");
		if (radioId) {
			radioId = radioId.substring(0, radioId.length - 6);
		} else {
			BIONE.tip("请选择数据集目录");
			return;
		}
		var currentNode = selectTreeObj.getNodeByParam("tId", radioId);
		/* parent.parent.indxInfoObj.indexCatalogNo = currentNode.id;
		parent.$("#mainform [name='indexCatalogNo']").val(currentNode.id);
		parent.$.ligerui.get('catalog_box').setText(currentNode.text); */
		
		var pop = parent.liger.get('indexCatalogNm');
		pop.setText(currentNode.text);
		pop.setValue(currentNode.id);
		
		BIONE.closeDialog("idxCatalogSlct");
	}
</script>
</head>
<body>
</body>
</html>