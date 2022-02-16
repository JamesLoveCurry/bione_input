<%@ page language="java" pageEncoding="UTF-8"%>
<%@ include file="/common/taglibs.jsp"%>
<html>
<meta name="decorator" content="/template/template1E.jsp">
<head>
<script type="text/javascript">
	var form;
	var templateId = '${templateId}';
	var selectedType;
	var nodeObj;
	var tableNm;
	$(function() {
		initform();
		initBaseInfo();
	});
	
	function initBaseInfo(){
		if(templateId&&templateId!=null){
			$.ajax({
				cache : false,
				async : true,
				url : "${ctx}/rpt/input/temple/getRewriteByTempleId?templeId="+templateId+"&d="
						+ new Date().getTime(),
				dataType : 'json',
				type : "post",
				success : function(result) {
					if(result){
						if(!result.updateType||result.updateType==null||result.updateType=="")
							result.updateType="0";
						form.setData({
							//isStart :result.isStart,
							updateType : result.updateType,
							dsId : result.id.dsId,
							autoRewrite : result.autoRewrite == 'true'
						});
						tableNm = result.tableName;
						onSelected(result.id.dsId,tableNm);
					}else{
						onSelected("","");
					}
					$.ligerui.get("dsIdBox").set("onSelected", onSelected);
				},
				error : function(result, b) {
					BIONE.tip('加载失败,发现系统错误 <BR>错误码：' + result.status);
				}
			});
		}else
		{
			onSelected("","");
			$.ligerui.get("dsIdBox").set("onSelected", onSelected);
		}
	}

	function initform() {
		//创建表单结构 
		form = $("#mainform").ligerForm({
			labelWidth : 90,
			space : 40,
			validate : true,
			fields : [ /*{
				display : "是否启用",
				name : "isStart",
				newline : true,
				type : "checkbox"
			},*/{
				display : "更新类型",
				name : "updateType",
				newline : false,
				type : "select",
				width : 300,
				comboboxName : "updateTypeSelect",
				options : {
					data : [ {
						id : '0',
						text : '不存在的数据新增,存在的数据更新'
					}, {
						id : '1',
						text : '完全覆盖'
					} , {
						id : '2',
						text : '只更新存在的数据'
					}, {
						id : '3',
						text : '只新增不存在的数据'
					}  ]
				}
			}, {
                display : "自动回写数据",
                name : "autoRewrite",
                newline : false,
                type : 'checkbox',
            }, {
				display : "数据源选择",
				name : "dsId",
				newline : true,
				type : "select",
				width : 500,
				comboboxName : 'dsIdBox',
				validate : {
					required : true,
					messages : {
						required : "请选择数据源"
					}
				},
				options : {
					//initValue : '156',
					url : "${ctx}/rpt/input/temple/getDsList.json"
				}
			}]
		});
		/*
		if(dsId && dsId!=""){
			form.setData({
				dsId : dsId
			});

			var src = "${ctx}/rpt/input/table/selectSchemaTable?dsId=" + dsId+"&templateId="+templateId+"&operType=template"+ "&d="
			+ new Date();
			$("#nodeContent")
					.html(
							'<iframe frameborder="0" id="rewriteFrame" name="rewriteFrame" style="height:300px;width:100%;" src="'
									+ src + '"></iframe>');
		}else 
			$("#nodeContent")
			.html(
					'<iframe frameborder="0" id="rewriteFrame" name="rewriteFrame" style="height:300px;width:100%;" src=""></iframe>');
		
		$("#nodeContent")
		.html(
		'<iframe frameborder="0" id="rewriteFrame" name="rewriteFrame" style="height:300px;width:100%;" src=""></iframe>');
		*/
	}

	function onSelected(val, text) {
		var url  = "${ctx}/rpt/input/table/selectSchemaTable?dsId="+val+"&templateId="+templateId+"&operType=template"+"&tableNm="+tableNm+ "&d=" + new Date().getTime();
		$("#rewriteFrame").attr(
				"src",
				url);
	}

	function checkTaskUnifyNode(taskUnifyNodeList) {
		if (!taskUnifyNodeList || taskUnifyNodeList.length == 0) {
			BIONE.tip('请选择任务节点');
			return false;
		}
		for ( var i = 0; i < taskUnifyNodeList.length; i++) {
			var taskUnifyNode = taskUnifyNodeList[i];
			if (taskUnifyNode.taskObjIdMap == null
					|| taskUnifyNode.taskObjIdMap.length == 0) {
				BIONE.tip("请选择节点[" + taskUnifyNode.taskNodeNm + "]的执行对象信息");
				return false;
			}
			if (!taskUnifyNode.taskObjType || taskUnifyNode.taskObjType == null) {
				BIONE.tip("请选择节点[" + taskUnifyNode.taskNodeNm + "]的执行对象");
				return false;
			}
			if (taskUnifyNode.handleType == null
					|| taskUnifyNode.handleType == "") {
				BIONE.tip("请选择节点[" + taskUnifyNode.taskNodeNm + "]的处理级别");
				return false;
			}
		}
		return true;
	}
	function gatherData(fields,tableId) {
		var data = {
			tableName : tableId ,
			columnList : fields ,
			updateType : $("#mainform input[name='updateType']").val() ,
			dsId : $("#mainform input[name='dsId']").val(),
			templeId : templateId,
		    autoRewrite :$.ligerui.get("autoRewrite").getValue()
			/*,
			isStart : $.ligerui.get("isStart").getValue()
			*/
		};

		$.ajax({
			cache : false,
			async : true,
			url : "${ctx}/rpt/input/temple/saveReWrite?d="
					+ new Date().getTime(),
			dataType : 'json',
			contentType : "application/json",
			type : "post",
			data : JSON2.stringify(data),
			success : function(result) {
				BIONE.tip('保存成功!');
				BIONE.closeDialog("selectSchemaTableBox");
			},
			error : function(result, b) {
				BIONE.tip('保存失败,发现系统错误 <BR>错误码：' + result.status);
			}
		});
	}
</script>

<title>指标目录管理</title>
</head>
<body>
</body>
</html>