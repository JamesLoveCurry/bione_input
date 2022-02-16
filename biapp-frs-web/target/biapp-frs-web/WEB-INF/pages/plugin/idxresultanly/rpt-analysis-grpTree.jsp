<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<%@ include file="/common/taglibs.jsp"%>
<html>
<head>
<meta name="decorator" content="/template/template5.jsp">
<script type="text/javascript">
	var idxNo = "${idxNo}";
	var idxGrpNo = parent.$.ligerui.get("grp").getValue();
	var TreeObj = "";
	var btns = [];
	$(function() {
		initTree();
		initBtn();
	});

	//初始化树
	function initTree() {
		var setting = {
			view : {
				nameIsHTML : true,
				showTitle : false
			},
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
			edit : {
				drag : {
					isCopy : false,
					isMove : false
				},
				enable : true,
				showRemoveBtn : false,
				showRenameBtn : false
			},
			check : {
				chkStyle : "checkbox",
				enable : true,
				chkboxType : {
					"Y" : "s",
					"N" : "s"
				}
			}
		}

		//右树对象
		TreeObj = $.fn.zTree.init($("#tree"), setting);

		//加载数据
		loadTree("${ctx}/idx/analysis/show/initGrpTreeData", TreeObj);
	}

	//加载树中数据
	function loadTree(url, component) {
		$.ajax({
			cache : false,
			async : true,
			url : url,
			dataType : 'json',
			type : "post",
			data : {
				idxNo : idxNo
			},
			beforeSend : function() {
				BIONE.loading = true;
				BIONE.showLoading("正在加载数据中...");
			},
			complete : function() {
				BIONE.loading = false;
				BIONE.hideLoading();
			},
			success : function(result) {
				if (result.length > 0) {
					component.addNodes(null, result, false);
					var newNodes =  component.transformToArray(component.getNodes());
					if(idxGrpNo){
						var idxGrpList = idxGrpNo.split(";");
						for(var i = 0;i< newNodes.length;i++){
							for(var j = 0;j< idxGrpList.length;j++){
								if(idxGrpList[j] == newNodes[i].id){
									newNodes[i].checked = true;
									component.updateNode(newNodes[i]);
								}
							}
						}
					}
					component.expandAll(false);
					component.expandNode(component.getNodeByParam("tId",
							"tree_1", null), true, false, false);
				}
			},
			error : function(result, b) {
				BIONE.tip('发现系统错误 <BR>错误码：' + result.status);
			}
		});
	}

	//初始化按钮
	function initBtn() {
		btns.push({
			text : "取消",
			onclick : function() {
				BIONE.closeDialog("grpCatalog");
			}
		}, {
			text : "选择",
			onclick : function() {
				var TreeNodes = TreeObj.getCheckedNodes(true);
				var grpdata = "";
				var grpNm = "";
				if(TreeNodes.length > 4){
					BIONE.tip("最多选择4个指标!");
				}else{
					for (var i = 0; i < TreeNodes.length; i++) {
						grpdata += TreeNodes[i].id + ";";
						grpNm += TreeNodes[i].text + ";";
					}
					var c = parent.$.ligerui.get("grp");
					c.setValue( grpdata,grpNm);
					BIONE.closeDialog("grpCatalog");
				}
			}
		});
		BIONE.addFormButtons(btns);
	}
</script>
<title>对比组</title>
</head>
<body>
	<div id="template.center">
		<div class="content" style="border: 1px solid #C6C6C6;width: 99%; height: 96%;">
			<div id="treeContainer"
				style="width: 100%; height: 100%; clear: both;">
				<ul id="tree"
					style="font-size: 12; background-color: F1F1F1; width: 90%; height: 99%;"
					class="ztree"></ul>
			</div>
		</div>
	</div>
</body>
</html>