<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<%@ include file="/common/taglibs.jsp"%>
<html>
<meta name="decorator" content="/template/template3.jsp">
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<script type="text/javascript">
	var zTreeObj;
	var rootId;
	var rootName;
	var currentRule ='${currentRule}';
	var indexNo ='${indexNo}';
	var rownum ='${rownum}';
	var ruleId = '${ruleId}';
	var zTreeObj;
	var mainform;

	var groupicon = "${ctx}/images/classics/icons/communication.gif";
	var auth_obj_root_icon = "${ctx}/images/classics/icons/house.png";
	$(function() {
		initTree();
		initMenubar();
		initForm();
		initBtn();
		initBaseData();
	});
	
	function initBaseData(){
			initFrame(indexNo);
			refreshTree();
			if(ruleId&&ruleId!=""){
				var node = zTreeObj.getNodeByParam("id", ruleId, null);
				zTreeObj.selectNode(node);
			}
	}
	
	function initBtn(){

		var buttons = [];
		buttons.push({
			text : '关闭',
			onclick : closeCallBack
		});
		buttons.push({
			text : '保存',
			onclick : savedCallback
		});
		buttons.push({
			text : '选择',
			onclick : onChoose
		});
		BIONE.addFormButtons(buttons); //generate the 'update' and 'delete' buttons
		jQuery.metadata.setType("attr", "validate");
		BIONE.validate($("#mainform"));
	}

	// 获得树结点数据, 追加到树上
	function initTree() {
		zTreeObj = $.fn.zTree
		.init(
				$("#tree"),
				{
					data : {
						keep : {
							parent : true
						},
						key : {
							name : "text"
						},
						simpleData : {
							enable : true,
							idKey : "id",
							pIdKey : "upId",
							rootPId : null
						}
					},
					check : {
						chkStyle : 'checkbox',
						enable : false
					},
					view : {
						selectedMulti : false,
						showLine : false
					},
					async : {
						enable : true,
						url : "${ctx}/bione/admin/auth/getAuthOrgTree.json?indexNo="+indexNo
					},
					callback : {
						onClick : zTreeOnClick,
						onDblClick : zTreeOnDblClick
					}
				}, [ {
					id : "0",
					text : "规则树",
					icon : auth_obj_root_icon
				} ]);
	}

	// init menu toolbar
	function initMenubar() {
		$("#treeToolbar").ligerTreeToolBar({
			items : [ {
				icon : 'add',
				text : '新增',
				click : addRule
			}, {
				line : true
			}, {
				icon : 'delete',
				text : '删除',
				click : deleteRule
			} ]
		});
	}
	
	function choose(id,text){
		window.parent.taskManage.templateManage.changeRule(id,text,rownum);
		BIONE.closeDialog("chooseRule");
	}
	
	function onChoose(id,text){

		if(zTreeObj.getSelectedNodes().length==0){
			BIONE.tip('请选择规则!');
			return ;
		}

		var rptInputIdxValidate ={
				ruleId : $("#mainform input[name='ruleId']").val(),
				ruleNm : $("#mainform input[name='ruleNm']").val(),
				ruleType : $("#mainform input[name='ruleType']").val(),
				ruleVal : $("#mainform input[name='ruleVal']").val(),
				sqlExpression : $("#sqlExpression").val(),
				sourceId : $("#mainform input[name='sourceId']").val(),
				symbol : $("#mainform input[name='symbol']").val(),
				indexNo : indexNo
		};
			$
			.ajax({
				cache : false,
				async : true,
				url : "${ctx}/rpt/input/idxdatainput/saveRule?d="
						+ new Date().getTime(),
				contentType : "application/json",
				dataType : 'json',
				type : "post",
				data :  JSON2.stringify(rptInputIdxValidate),
				beforeSend : function() {
					BIONE.loading = true;
					BIONE.showLoading("正在加载数据中...");
				},
				complete : function() {
					BIONE.loading = false;
					BIONE.hideLoading();
				},
				success : function(result) {
						var treeNode = zTreeObj.getSelectedNodes()[0];
						choose(treeNode.id,treeNode.text);
				},
				error : function(result, b) {
					BIONE.tip(result.responseText);
				}
			});
	}
	
	function zTreeOnDblClick(event, treeId, treeNode) {
		if(zTreeObj.getSelectedNodes().length==0){
			BIONE.tip('请选择规则!');
			return ;
		}
		choose(treeNode.id,treeNode.text);
	}

	// zTree onClick
	function zTreeOnClick(event, treeId, treeNode) {
		zTreeObj.expandNode(treeNode); /*点击节点名称也展开节点*/
		currentNode = treeNode;
		initFrame(treeNode.data);
	}

	// 刷新树
	function refreshTree() {
			var rootTmp = zTreeObj.getNodeByParam("id", "0", null);
			zTreeObj.removeChildNodes(rootTmp);
			//查询该授权对象并更新树
			$.ajax({
				cache : false,
				async : true,
				url : "${ctx}/rpt/input/idxdatainput/getRuleTree.json?d="
						+ new Date().getTime(),
				dataType : 'json',
				type : "post",
				data : {
					"indexNo" : indexNo
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
					if (!result)
						return;
					zTreeObj.addNodes(zTreeObj.getNodeByParam("id", '0', null),
							result, true);
					//展开树
					//zTreeObj.expandAll(true);
					var rootNodeTmp = zTreeObj.getNodeByParam("id", "0",
							null);
					zTreeObj.expandNode(rootNodeTmp, true, false);
					
				},
				error : function(result, b) {
					BIONE.tip('发现系统错误 <BR>错误码：' + result.status);
				}
			});
	}

	//创建表单结构 
	function initForm() {
		mainform = $("#mainform").ligerForm({
			inputWidth : 160,
			labelWidth : 80,
			space : 30,
			fields : [ {
				name : "ruleId",
				type : "hidden"
			},{
				display : "规则名称",
				name : "ruleNm",
				newline : true,
				type : "text",
				validate : {
					required : true,
					maxlength : 50
				}
			},{
				display : "符号",
				name : "symbol",
				newline : true,
				type:"select",
				comboboxName: "symbolSelect",
				options : {
					data : [ {
						id : '1',
						text : '>'
					}, {
						id : '2',
						text : '>='
					}, {
						id : '3',
						text : '<'
					}, {
						id : '4',
						text : '<='
					}, {
						id : '5',
						text : '=='
					}]
				},
				width : 215,
				validate : {
					required : true
				}
			}, {
				display : "规则类型",
				name : "ruleType",
				newline : true,
				type:"select",
				comboboxName: "ruleTypeSelect",
				options : {
					data : [ {
						id : '1',
						text : 'sql'
					}, {
						id : '2',
						text : '数值'
					}],
					onSelected :function(value,text){
						if(value =="1"){
							$("#mainform input[name='sourceId']").parent().parent().parent().show();
							$("#sqlExpression").parents("ul:first").show();
							$("#mainform input[name='ruleVal']").parent().parent().parent().hide();
						}else{
								$("#mainform input[name='ruleVal']").parent().parent().parent().show();
								$("#mainform input[name='sourceId']").parent().parent().parent().hide();
								//$("#mainform input[name='sqlExpression']").parent().parent().hide();
								//$("#mainform input[name='sqlExpression']").parents('ul:first').hide();
								$("#sqlExpression").parents("ul:first").hide();
						}
					}
				},
				width : 215,
				validate : {
					required : true
				}
			},{
				display : "数据源",
				name : "sourceId",
				newline : true,
				type : "select",
				width :200,
				options : {
					url : "${ctx}/rpt/frame/dataset/dsList.json",
					onBeforeSelect : function() {
						$("#mainform input[name='sourceId']").val("");
					}
				}
			}, {
				display : "SQL语句",
				name : "sqlExpression",
				newline : true,
				width : 300,
				type : "textarea",
			}, {
				display : "数值",
				name : "ruleVal",
				newline : false,
				type : "number"
			}]
		});
	};
	function showIconDialog(options) {
		var options = {
			url : '${ctx}/bione/admin/func/selectIcon.html',
			dialogname : 'iconselector',
			title : '选择图标',
			comboxName : 'IconBoxID'
		};
		BIONE.commonOpenIconDialog(options.title, options.dialogname,
				options.url, options.comboxName);
		return false;
	}
	//DELETE
	function deleteRule() {
		var selectedNodes = zTreeObj.getSelectedNodes();
		if(zTreeObj.getSelectedNodes().length==0){
			BIONE.tip('请选择需要删除的规则!');
			return ;
		}
		var ruleId = selectedNodes[0].id;
		$
		.ajax({
			cache : false,
			async : true,
			url : "${ctx}/rpt/input/idxdatainput/deleteRule?d="
					+ new Date().getTime(),
			dataType : 'json',
			type : "post",
			data :  {ruleId:ruleId},
			beforeSend : function() {
				BIONE.loading = true;
				BIONE.showLoading("正在加载数据中...");
			},
			complete : function() {
				BIONE.loading = false;
				BIONE.hideLoading();
			},
			success : function(result) {
				BIONE.tip('删除成功!');
				refreshTree();
			},
			error : function(result, b) {
				BIONE.tip('保存失败,发现系统错误 <BR>错误码：' + result.status);
			}
		});
	};
	//SAVE
	function savedCallback() {
		var rptInputIdxValidate ={
				ruleId : $("#mainform input[name='ruleId']").val(),
				ruleNm : $("#mainform input[name='ruleNm']").val(),
				ruleType : $("#mainform input[name='ruleType']").val(),
				ruleVal : $("#mainform input[name='ruleVal']").val(),
				sqlExpression : $("#sqlExpression").val(),
				sourceId : $("#mainform input[name='sourceId']").val(),
				symbol : $("#mainform input[name='symbol']").val(),
				indexNo : indexNo
		};
		
			$
			.ajax({
				cache : false,
				async : true,
				url : "${ctx}/rpt/input/idxdatainput/saveRule?d="
						+ new Date().getTime(),
				contentType : "application/json",
				dataType : 'json',
				type : "post",
				data :  JSON2.stringify(rptInputIdxValidate),
				beforeSend : function() {
					BIONE.loading = true;
					BIONE.showLoading("正在加载数据中...");
				},
				complete : function() {
					BIONE.loading = false;
					BIONE.hideLoading();
				},
				success : function(result) {
						BIONE.tip('保存成功!');
						refreshTree();
				},
				error : function(result, b) {
					BIONE.tip(result.responseText);
				}
			});
	};

	//CANCLE
	function closeCallBack() {
		BIONE.closeDialog("chooseRule");
	};

	function addRule() {
		initFrame();
	}
	function initFrame(data){
		if(!data||data==""){
			mainform.setData({
				ruleId:"",
				ruleNm:"",
				symbol:"",
				ruleType:"",
				sourceId:"",
				sqlExpression:"",
				ruleVal:""
			});

			$.ligerui.get("ruleTypeSelect").setValue("1");
		}else{
			mainform.setData({
				ruleId:data.ruleId,
				ruleNm:data.ruleNm,
				symbol:data.symbol,
				ruleType:data.ruleType,
				sourceId:data.sourceId,
				sqlExpression:data.sqlExpression,
				ruleVal:data.ruleVal
			});
			$.ligerui.get("ruleTypeSelect").setValue(data.ruleType);
		}
	}
</script>
</head>
<body>
	<div id="template.left.up.icon">
		<img src="${ctx}/images/classics/icons/application_side_tree.png" />
	</div>
	<div id="template.left.up">
		<span id="treeTitle" style="font-size: 12">规则树</span>
	</div>
	<div id="template.right">
		<form id="mainform"></form>
	</div>
</body>
</html>