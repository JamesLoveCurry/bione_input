<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<%@ include file="/common/taglibs.jsp"%>
<html>
<meta name="decorator" content="/template/template3.jsp">
<head>
<script type="text/javascript">
	var zTreeObj;
	var dirType;
	var rootName;
	var currentNode = null;
	var currentNodeId = "0";
	
    var groupicon = "${ctx}/images/classics/icons/communication.gif";
	var buttons = [];
	buttons.push({
		text : '关闭',
		onclick : closeCallBack
	});
	buttons.push({
		text : '保存',
		onclick : savedCallback
	});
	
	$(init);
	
	function init() {
		initRoot();
		beforeTree();
		initTree();
		initMenubar();
	}
	
	function initRoot() {
		dirType = "${dirType}";
		if (dirType.length != 0) {
			$.get('${ctx}/rpt/input/catalog/' + dirType+'/name', function(returnedData) {
				$("#treeTitle").text(returnedData);
			});
		}
	}

	// 组织树对象
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
				onClick : zTreeOnClick
			}
		};
		zTreeObj = $.fn.zTree.init($("#tree"), setting);
	}

	// 获得树结点数据, 追加到树上
	function initTree() {
		$.ajax({
			cache : false,
			async : false,
			url : '${ctx}/rpt/input/catalog/' + dirType + '/list.json',
			success : function(result) {
				var id = null;
				$(result).each(function() {
					if (this.ischecked) {
						id = this.id;
					}
				});
				zTreeObj.addNodes(null, result, false);
				if(id==null){
					id = currentNodeId; 
				}
				if (id != null && id != "") {
					zTreeObj.selectNode(currentNode = zTreeObj.getNodeByParam("id", id));
					getCurrentInfo(currentNode);
				}
			}
		});
	}

	// init menu toolbar
	function initMenubar() {
		$("#treeToolbar").ligerTreeToolBar({
			items : [ {
				icon : 'add',
				text : '新增',
				click : addDir
			}, {
				line : true
			}, {
				icon : 'delete',
				text : '删除',
				click : deleteCallback
			} ]
		});
	}

	// zTree onClick
	function zTreeOnClick(event, treeId, treeNode) {
		zTreeObj.expandNode(treeNode);		/*点击节点名称也展开节点*/
		currentNode = treeNode;
		getCurrentInfo(treeNode);
	}

	// 刷新树
	function refreshTree() {
		var rootTmp = zTreeObj.getNodeByParam("id", "0", null);
		if (rootTmp) {
			zTreeObj.removeNode(rootTmp);
		}
		initTree();
		if("${dirType}"){
			parent.initTree();
		}
	}

	function getCurrentInfo(node) { //load current info
		if (node.id == "0")
			return false;
		if ($(".l-group l-group-hasicon").length == 0) //judge the form whether exist where id='mainform' 
			ligerFormNow();
		$("#mainform input[name]").val("");
		$("#mainform textarea[name]").val("");
		$("#mainform input[name=parentName]").attr("disabled", "disabled");
		BIONE.loadForm($("#mainform"), {
			url : '${ctx}/rpt/input/catalog/show/' + node.id
		});
// 		load upFunc
		$.ajax({
			type : 'get',
			url : '${ctx}/rpt/input/catalog/show/' + node.upId ,
			dataType : 'json',
			success : function(data) {
				if (data && data.funcName != ""){
					$("#mainform input[name='upCatalog']").val(data.catalogId);
					$("#mainform input[name='parentName']").val(data.catalogName);
				}else{
					$("#mainform input[name='upCatalog']").val("0");
					$("#mainform input[name='parentName']").val(rootName);
				}
			},
			error : function() {
				$("#mainform input[name='upCatalog']").val("0");
				$("#mainform input[name='parentName']").val(rootName);
			}
		});
		$(".l-dialog-btn").remove();
		BIONE.addFormButtons(buttons); //generate the 'update' and 'delete' buttons
		jQuery.metadata.setType("attr", "validate");
		BIONE.validate($("#mainform"));
	}

	//创建表单结构 
	function ligerFormNow() {
		$("form").ligerForm({
			inputWidth : 160,
			labelWidth : 80,
			space : 30,
			fields : [ {
				name : "catalogId",
				type : "hidden"
			}, {
				name : "upCatalog",
				type : "hidden"
			}, {
				name : "catalogType",
				type : "hidden"
			}, {
				display : "目录名称",
				name : "catalogName",
				newline : true,
				type : "text",
				group : "目录名称",
				groupicon : groupicon,
				validate : {
					required : true,
					maxlength : 50
				}
			}, {
				display : "顺序编号",
				name : "orderNo",
				newline : false,
				type : "text",
				validate : {
					number : true,
					required : true,
					maxlength : 5
				}
			},{
				display : "上级目录 ",
				name : "parentName",
				newline : true,
				type : "text",
				validate : {
					required : true,
					maxlength : 32
				}
			}]
		});
	};

	//DELETE
	function deleteCallback() {
		if (currentNode) {
			if (currentNode.id != '0') {
				setCurrentId();
				$.ligerDialog.confirm('确实要删除该记录吗?', function(yes) {
					if (yes) {
						var flag = false;
						$.ajax({
							async : false,
							cache : false,
							type : "POST",
							url : "${ctx}/rpt/input/catalog/" + currentNode.id,
							dataType : "script",
							success : function() {
								flag = true;
							}
						});
						if (flag == true) {
							refreshTree();
							BIONE.tip('删除成功!');
							currentNode = null;
						} else {
							BIONE.tip('删除失败!');
						}
					}
				});
			} else {
				BIONE.tip('此节点不能删除!');
			}
		}
	};
	
	function setCurrentId(){
		currentNodeId = currentNode.upId;
		
		var nodes = zTreeObj.getNodesByParam("upId", currentNode.upId, null);
		if(nodes.length > 1){
			currentNodeId =nodes[0].id;
		}
		
	}
	
	//SAVE
	function savedCallback() {
		var dirName = $('#catalogName').val();
		var dirId = $('#catalogId').val();
		var nodes = zTreeObj.getNodesByParam("text",dirName , null);
		
		if(nodes.length !=0&&nodes[0].upId==$('#parentId').val()&&dirId!=nodes[0].id){
			BIONE.tip('已存在同名节点！');
			return ;
		}

		BIONE.submitForm($("#mainform"), function(data) {
			currentNodeId = data.id;
			BIONE.tip("保存成功!");
			refreshTree();
			BIONE.closeDialog("dirmanager");
		});
	};

	//CANCLE
	function closeCallBack() {
		BIONE.closeDialog("dirmanager");
	};

	function addDir() {
		if ($(".l-group l-group-hasicon").length == 0)
			ligerFormNow();

		$("#mainform input[name != 'parentName']").removeAttr("disabled").css("color", "black");
		$("#mainform input").val("");
		$("#mainform input[name='parentName']").attr("disabled", "disabled");
		if (currentNode) {
			$("#mainform input[name=parentName]").val(currentNode.text);
			$("#mainform input[name=upCatalog]").val(currentNode.id);
		} else {
			$("#mainform input[name=parentName]").val("根节点");
			$("#mainform input[name=upCatalog]").val("0");
		}


		$("#mainform input[name='catalogType']").val(dirType);

		$(".l-dialog-btn").remove();
		BIONE.addFormButtons(buttons);
		jQuery.metadata.setType("attr", "validate");
		BIONE.validate($("#mainform"));
	}
</script>
</head>
<body>
	<div id="template.left.up.icon">
		<img src="${ctx}/images/classics/icons/application_side_tree.png" />
	</div>
	<div id="template.left.up">
		<span id="treeTitle" style="font-size: 12">功能树</span>
	</div>
	<div id="template.right">
		<form id="mainform" action="${ctx}/rpt/input/catalog" method="POST"></form>
	</div>
</body>
</html>