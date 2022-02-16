<%@ page language="java" pageEncoding="UTF-8"%>
<%@ include file="/common/taglibs.jsp"%>
<html>
<meta name="decorator" content="/template/template5.jsp">
<head>
<script type="text/javascript">
	var mainform;
	var taskNodeObjList = window.parent.deployTask.taskNodeObjList;
	var tskExeobjRelVO = window.parent.deployTask.tskExeobjRelVO;
	var taskObjGrid;
	var grid;
	var catalogId, taskId;
	var rptTskInfo;
	var triggerName, triggerId;
	var ROOT_NO = '0';
	var selectedObjName,selectedObjId;
	var selectedType ="01";
	//授权资源根节点图标
	var auth_obj_root_icon = "${ctx}/images/classics/icons/house.png";

	//记录当前点击的授权对象id
	var selectedObjId = "";

	$(function() {
		window.parent.taskManage.taskBaseManage = window;
		initBaseInfo();
		jQuery.metadata.setType("attr", "validate");
		BIONE.validate($("#mainform"));
	});

	//弹出窗口中选择触发器
	function showtriggerDialog(options) {
		var options = {
			url : "${ctx}/bione/schedule/task/selectTrigger?checkEndTime=1",
			dialogname : 'triggerBox',
			title : '选择触发器',
			comboxName : 'triggerCombBox'
		};
		BIONE.commonOpenIconDialog(options.title, options.dialogname,
				options.url, options.comboxName);
		return false;
	}
	function initBaseInfo() {
		initDataBefore();
		var groupicon = "${ctx}/images/classics/icons/communication.gif";
		mainform = $("#mainform").ligerForm({
			inputWidth : 120,
			labelWidth : 80,
			space : 50,
			align : "left",
			fields : [{
					name:"taskDefId",
					type:"hidden"
				}, {
				group : "任务信息",
				groupicon : groupicon,
				display : "任务名称",
				name : "taskNm",
				newline : false,
				type : "text",
				width : 215,
				validate : {
					required : true,
					maxlength : 100,
					remote : {
						url : "${ctx}/rpt/input/task/testRptIdxInputNm",
						type : "POST",
						data : {
							"catalogId" : catalogId,
							'taskId' : taskId
						}
					},
					messages : {
						remote : "相同路径下任务已存在"
					}
				}
			}, {
				display : "任务类型",
				name : "taskType",
				newline : false,
				type : "select",
				comboboxName : "taskTypeSelect",
				options : {
					url : "${ctx}/rpt/input/task/getTaskType.json"
				},
				validate : {
					required : true
				},
				width : 215
			}, {
				display : "生效日期",
				name : "effectDate",
				newline : true,
				type : 'date',
				format : 'yyyy-MM-dd',
				width : 215,
				validate : {
					required : true
				}
			}, {
				display : "失效日期",
				name : "invalidDate",
				newline : false,
				type : 'date',
				format : 'yyyy-MM-dd',
				width : 215,
				validate : {
					greaterThan : "effectDate",
					required : true
				}
			}, {
				display : "任务状态",
				name : "taskSts",
				newline : true,
				type : "select",
				comboboxName : "taskStsSelect",
				options : {
					data : [ {
						id : '0',
						text : '停用'
					}, {
						id : '1',
						text : '启用'
					} ]
				},
				width : 215
			}, {
				group : "触发器设置",
				groupicon : groupicon,
				display : "触发类型",
				name : "triggerType",
				newline : true,
				type : "select",
				comboboxName : "triggerTypeSelect",
				validate : {
					required : true
				},
				options : {
					data : [ {
						id : '1',
						text : '立即下发'
					}, {
						id : '2',
						text : '触发器下发'
					}, {
						id : '3',
						text : '后置任务'
					} ],
					onSelected :function(value,text){
							if(value =="2"){
								$.ligerui.get("triggerCombBox").setEnabled();
								$("#mainform input[name='dateOffsetAmount']").removeAttr("disabled");
								$("#mainform input[name='triggerId']").parent().parent().parent().show();
								$("#mainform input[name='dateOffsetAmount']").parent().parent().parent().show();
							}else{
								if($.ligerui.get("triggerCombBox")){
									$.ligerui.get("triggerCombBox").setDisabled();
									$("#mainform input[name='dateOffsetAmount']").attr("disabled", "disabled");
									$("#mainform input[name='triggerId']").parent().parent().parent().hide();
									$("#mainform input[name='dateOffsetAmount']").parent().parent().parent().hide();
								}
							}
						}
					},
				width : 215
			}, {
				display : '触发器',
				name : 'triggerId',
				comboboxName : "triggerCombBox",
				newline : true,
				type : 'select',
				validate : {
					maxlength : 32
				},
				width : '500',
				options : {
					onBeforeOpen : showtriggerDialog,//0809gaofeng修改,
					initText : triggerName,
					initValue : triggerId,
					textField : 'triggerName',
					valueField : 'triggerId',
					hideOnLoseFocus : true,
					slide : false,
					selectBoxHeight : 238,
					selectBoxWidth : 400,
					resize : false,
					switchPageSizeApplyComboBox : false,
					grid : {
						usePager : true, //服务器分页
						alternatingRow : true, //附加奇偶行效果行
						colDraggable : true,
						url : "${ctx}/bione/schedule/trigger/list.json",
						columns : [ {
							name : 'triggerId',
							hide : 1,
							width : '0'
						}, {
							display : '触发器名称',
							name : 'triggerName',
							width : '242'
						}, {
							display : '备注',
							name : 'remark',
							width : '300'
						} ],
						pageSize : 10,
						checkbox : false,
						switchPageSizeApplyComboBox : false
					}
				}
			}, {
				display : "日期偏移数",
				name : "dateOffsetAmount",
				newline : true,
				type : "number",
				width : 215
			}, {
				name : "selectedObjId",
				type : "hidden"
			}, {
				group : "补录信息设置",
				groupicon : groupicon,
				display : "补录类型",
				name : "exeObjType",
				newline : false,
				type : "select",
				comboboxName : "exeObjTypeSelect",
				options : {
					data : [ {
						id : '01',
						text : '指标补录'
					}, {
						id : '02',
						text : '明细补录'
					} ]
					,
					onSelected : function(value) {
						selectedType = value;
						$.ligerui.get("selectedObjCombBox").setValue("");
						$.ligerui.get("selectedObjCombBox").setText("");
				}},
				width : 132,
				validate : {
					required : true
				}
			}, {
				display : '补录对象',
				name : 'selectedObjId',
				comboboxName : "selectedObjCombBox",
				newline : true,
				type : 'select',
				validate : {
				    maxlength : 32
				},
				width : '500',
				validate : {
					required : true
				},
				options : {
					onBeforeOpen : showselectedObjDialog,
				    initText : selectedObjName,
				    initValue : selectedObjId,
				    textField : 'selectedObjName',
				    valueField : 'selectedObjId',
				    hideOnLoseFocus : true,
				    slide : false,
				    selectBoxHeight : 238,
				    selectBoxWidth : 400,
				    resize : false,
				    switchPageSizeApplyComboBox : false,
				    grid : {
					usePager : true, //服务器分页
					alternatingRow : true, //附加奇偶行效果行
					colDraggable : true,
					url : "${ctx}/bione/schedule/trigger/list.json",
					columns : [ {
					    name : 'selectedObjId',
					    hide : 1,
					    width : '0'
					}, {
					    display : '模板名称',
					    name : 'selectedObjName',
					    width : '242'
					}, {
					    display : '备注',
					    name : 'remark',
					    width : '300'
					} ],
					pageSize : 10,
					checkbox : false,
					switchPageSizeApplyComboBox : false
				    }
				}
			} ]
		});
		initAfterData();
	}
	
	function initDataBefore() {
		rptTskInfo = window.parent.deployTask.rptTskInfo;
		if (rptTskInfo == null) {

			var selectedNodes = window.parent.parent.leftTreeObj
					.getSelectedNodes();
			if (selectedNodes == null || selectedNodes.length == 0)
				catalogId = "ROOT";
			else {
				var node = selectedNodes[0];
				var nodeType = node.params.nodeType;
				if (nodeType == "taskinfo") {
					if (node.getParentNode() == null)
						catalogId = "ROOT";
					else
						catalogId = node.getParentNode().id;
				} else if (nodeType == "catalog")
					catalogId = node.id;
			}
		} else {
			taskId = rptTskInfo.taskId;
			catalogId = rptTskInfo.catalogId;
		}
	}
	function initAfterData() {
		if (rptTskInfo != null) {
			mainform.setData({
				checkSts : rptTskInfo.checkSts,
				effectDate : rptTskInfo.effectDate,
				exeObjId : rptTskInfo.exeObjId,
				exeObjType : rptTskInfo.exeObjType,
				sumMode : rptTskInfo.sumMode,
				taskDeadline : rptTskInfo.taskDeadline,
				taskDefId : rptTskInfo.taskDefId,
				taskFreq : rptTskInfo.taskFreq,
				taskNm : rptTskInfo.taskNm,
				taskSts : rptTskInfo.taskSts,
				taskType : rptTskInfo.taskTypeCode,
				triggerId : rptTskInfo.triggerId,
				triggerType : rptTskInfo.triggerType,
				triggerNm : rptTskInfo.triggerNm,
				upTaskId : rptTskInfo.upTaskId,
				isPre : rptTskInfo.isPre,
				dateOffsetAmount : rptTskInfo.dateOffsetAmount,
				invalidDate : rptTskInfo.invalidDate,
				isRelyData : rptTskInfo.isRelyData,
				logicDelNo : rptTskInfo.logicDelNo,
				selectedObjId:tskExeobjRelVO.exeObjId,
				selectedObjNm:tskExeobjRelVO.exeObjNm,
				exeObjType:tskExeobjRelVO.exeObjType
			});
			$.ligerui.get("triggerCombBox").setText(rptTskInfo.triggerNm);
			var typeText = tskExeobjRelVO.exeObjType==null?"": tskExeobjRelVO.exeObjType=="01"?"指标补录":"明细补录";
			$.ligerui.get("exeObjTypeSelect")._changeValue(tskExeobjRelVO.exeObjType,typeText);
			$.ligerui.get("selectedObjCombBox").setValue(tskExeobjRelVO.exeObjId);
			$.ligerui.get("selectedObjCombBox").setText(tskExeobjRelVO.exeObjNm);
		} else {
			$.ligerui.get("triggerCombBox").setDisabled();
			$("#mainform input[name='dateOffsetAmount']").attr("disabled", "disabled");
			mainform.setData({
				dateOffsetAmount : 0,
				triggerType : "2",
				taskSts : "1"
			});
		}
		$("#selectedObjNm").attr("disabled", "disabled");
	}

	function gatherData() {
		if($("#mainform").valid()){
			var triggerType = $("#mainform input[name='triggerType']").val();
			var triggerId = $.ligerui.get("triggerCombBox").getValue();
			if(triggerType=="2"&&(triggerId==null||triggerId=="")){
				BIONE.tip('请选择触发器');
				window.parent.isNeedCheck = false;
				return;
			}
			window.parent.deployTask.rptTskInfo = {
				taskId : taskId,
				checkSts : $("#mainform input[name='checkSts']").val(),
				effectDate : $("#mainform input[name='effectDate']").val().replace("-","").replace("-",""),
				exeObjId : $("#mainform input[name='exeObjId']").val(),
				exeObjType : $("#mainform input[name='exeObjType']").val(),
				sumMode : $("#mainform input[name='sumMode']").val(),
				taskDeadline : $("#mainform input[name='taskDeadline']").val(),
				taskDefId : $("#mainform input[name='taskDefId']").val(),
				taskFreq : $("#mainform input[name='taskFreq']").val(),
				taskNm : $("#mainform input[name='taskNm']").val(),
				taskSts : $("#mainform input[name='taskSts']").val(),
				taskType : $("#mainform input[name='taskType']").val(),
				triggerId : $.ligerui.get("triggerCombBox").getValue(),
				triggerNm : $.ligerui.get("triggerCombBox").getText(),
				triggerType : $("#mainform input[name='triggerType']").val(),
				upTaskId : $("#mainform input[name='upTaskId']").val(),
				isPre : $("#mainform input[name='isPre']").val(),
				dateOffsetAmount : $("#mainform input[name='dateOffsetAmount']")
						.val(),
				invalidDate : $("#mainform input[name='invalidDate']").val().replace("-","").replace("-",""),
				isRelyData : $("#mainform input[name='isRelyData']").val(),
				logicDelNo : $("#mainform input[name='logicDelNo']").val(),
				"catalogId" : catalogId
			};

			window.parent.deployTask.tskExeobjRelVO = {
					exeObjType:$("#exeObjType").val(),
					exeObjId:$.ligerui.get("selectedObjCombBox").getValue(),
					exeObjNm:$.ligerui.get("selectedObjCombBox").getText()
			};
			window.parent.deployTask.rptTskInfo.exeObjType = $("#exeObjType").val();
			window.parent.deployTask.rptTskInfo.exeObjId = $.ligerui.get("selectedObjCombBox").getValue();
			window.parent.checkNode();
		}else{
			window.parent.isNeedCheck = false;
		}
	}

  	//弹出窗口中选择模板
    function showselectedObjDialog(options) {
		var options = {
			url : "${ctx}/rpt/input/task/selectObjBox?selectedType="+selectedType,
			dialogname : 'selectedObjBox',
			title : '选择模板',
			comboxName : 'selectedObjBox'
		};
		BIONE.commonOpenIconDialog(options.title, options.dialogname,
				options.url, options.comboxName);
		return false;
	}
</script>

<title>指标目录管理</title>
</head>
<body style="width: 80%">
	<div id="template.center">
		<form name="mainform" method="post" id="mainform"></form>
	</div>
</body>
</html>