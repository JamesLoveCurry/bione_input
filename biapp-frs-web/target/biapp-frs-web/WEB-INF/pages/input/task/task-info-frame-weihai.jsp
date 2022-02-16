<%@ page language="java" pageEncoding="UTF-8"%>
<%@ include file="/common/taglibs.jsp"%>
<html>
<meta name="decorator" content="/template/template5.jsp">
<head>
<script type="text/javascript">
	var mainform;
	var taskObjGrid;
	var grid;
	var catalogId;
	var rptTskInfo;
	var tskExeobjRelVO;
	var afterTaskVo;
	var triggerName, triggerId;
	var ROOT_NO = '0';
	var selectedObjName,selectedObjId,afterTaskName,afterTaskId;
	var selectedType ="02";
	//授权资源根节点图标
	var auth_obj_root_icon = "${ctx}/images/classics/icons/house.png";
	var taskId = '${taskId}';
	var canOper='${canOper}';
	//记录当前点击的授权对象id
	var selectedObjId = "";

	$(function() {
		initBaseInfo();
		initBtn();
		jQuery.metadata.setType("attr", "validate");
		BIONE.validate($("#mainform"));
	});

	function initBtn() {
		var buttons = [];
		var main = parent || window;
		var dialog = main.jQuery.ligerui.get("rptTaskBox");
		if(dialog){
			buttons.push({
				text : '取消',
				onclick : f_close
			});
		}
		if(!canOper)
			canOper="0";
		if(canOper=="1"){
			buttons.push({
				text : '保存',
				onclick : f_save
			});
		}
		BIONE.addFormButtons(buttons);
	}

	function f_close() {
		var main = parent || window;
		var dialog = main.jQuery.ligerui.get("rptTaskBox");
		if(dialog){
			BIONE.closeDialog("rptTaskBox");
		}
	}
	
	function f_save(){
		var deployTask = gatherData();
		if(!deployTask||deployTask == null)
			return ;
		$.ajax({
			cache : false,
			async : true,
			url : "${ctx}/rpt/input/task/saveTask?d="
					+ new Date().getTime(),
			dataType : 'json',
			contentType : "application/json",
			type : "post",
			data : JSON2.stringify(deployTask),
			success : function(result) {
				BIONE.tip('保存成功!');
				window.parent.refreshTree();
				f_close();
			},
			error : function(result, b) {
				BIONE.tip('保存失败,发现系统错误 <BR>错误码：' + result.status);
			}
		});
	}

	//弹出窗口中选择触发器
	function showtriggerDialog(options) {
		var options = {
			url : "${ctx}/rpt/input/task/selectTrigger",
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
	}
	
	function initFrame(){
		var groupicon = "${ctx}/images/classics/icons/communication.gif";
		mainform = $("#mainform").ligerForm({
			inputWidth : 120,
			labelWidth : 80,
			space : 50,
			align : "left",
			fields : [{
					name:"taskDefId",
					type:"hidden"
				},{
					group : "补录信息设置",
					groupicon : groupicon,
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
					minlength : 4,
					remote : {
						url : "${ctx}/rpt/input/task/testRptIdxInputNm",
						type : "POST",
						data : {
							"catalogId" : catalogId,
							'taskId' : taskId,
							'taskDefId' : rptTskInfo ? rptTskInfo.taskDefId : ""
						}
					},
					messages : {
						remote : "相同路径下任务已存在"
					}
				}
			}, {
				display : "任务状态",
				name : "taskSts",
				newline : false,
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
				display : "任务类型",
				name : "taskType",
				newline : false,
				type : "hidden",
				comboboxName : "taskTypeSelect",
				options : {
					url : "${ctx}/rpt/input/task/getTaskType.json"
				},
				width : 215,
				validate : {
					required : true
				}
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
                display : "后置任务",
                name : "afterTaskSts",
                newline : true,
                type : "select",
                comboboxName : "afterTaskStsSelect",
                options : {
                    data : [ {
                        id : '0',
                        text : '停用'
                    }, {
                        id : '1',
                        text : '启用'
                    } ],
                    onSelected :function(value,text){
                        if(value =="1"){
//                             $.ligerui.get("afterTaskCombBox").setDisabled();
                            $("#mainform input[name='afterTask']").parent().parent().parent().show();
                        }else{
                            
//                             $.ligerui.get("#afterTaskCombBox").show();
                            $("#mainform input[name='afterTask']").parent().parent().parent().hide();
                        }
                    }
                },
                width : 215
            },{
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
									$.ligerui.get("triggerCombBox").setData("");
									$.ligerui.get("triggerCombBox").setValue("");
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
			}/*, {
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
			}*/,{
                display : '后置任务',
                name : 'afterTask',
                comboboxName : "afterTaskCombBox",
                newline : true,
                type : 'select',
                validate : {
                    maxlength : 32
                },
                width : '500',
                options : {
                    onBeforeOpen : showAfterTaskDialog,
                    initText : afterTaskName,
                    initValue : afterTaskId,
                    textField : 'afterTaskName',
                    valueField : 'afterTaskId',
                    hideOnLoseFocus : true,
                    slide : false,
                    selectBoxHeight : 238,
                    selectBoxWidth : 400,
                    resize : false,
                    switchPageSizeApplyComboBox : false
                }
            } ]
		});
		initAfterData();
	}
	
	function initDataBefore() {
		$.ajax({
			cache : false,
			async : true,
			url : "${ctx}/rpt/input/task/getBaseTaskById?taskId="+taskId+"&d="
					+ new Date().getTime(),
			dataType : 'json',
			type : "post",
			success : function(result) {
				rptTskInfo = result.rptTskInfo;
				tskExeobjRelVO = result.tskExeobjRelVO;
				afterTaskVo = result.afterTask;
				if (rptTskInfo == null) {

					var selectedNodes = window.parent.leftTreeObj
							.getSelectedNodes();
					if (selectedNodes == null || selectedNodes.length == 0)
						catalogId = "0";
					else {
						var node = selectedNodes[0];
						var nodeType = node.params.nodeType;
						if (nodeType == "taskinfo") {
							if (node.getParentNode() == null)
								catalogId = "0";
							else
								catalogId = node.getParentNode().id;
						} else if (nodeType == "catalog")
							catalogId = node.id;
					}
				} else {
					taskId = rptTskInfo.taskId;
					catalogId = rptTskInfo.catalogId;
				}
				initFrame();
			},
			error : function(result, b) {
				BIONE.tip('访问任务数据出错,' + result.status);
			}
		});
		
		//rptTskInfo = window.parent.deployTask.rptTskInfo;
	}
	function initAfterData() {
		if (rptTskInfo != null) {
			var eff = rptTskInfo.effectDate.substring(0,4) + 
				"-" +rptTskInfo.effectDate.substring(4,6)+ 
					"-" +rptTskInfo.effectDate.substring(6,8);
			
			var inv = rptTskInfo.invalidDate.substring(0,4) + 
				"-" +rptTskInfo.invalidDate.substring(4,6)+ 
					"-" +rptTskInfo.invalidDate.substring(6,8);
			rptTskInfo.taskNm;
			mainform.setData({
				checkSts : rptTskInfo.checkSts,
				effectDate : eff,
				exeObjId : rptTskInfo.exeObjId,
				//exeObjType : rptTskInfo.exeObjType,
				sumMode : rptTskInfo.sumMode,
				taskDeadline : rptTskInfo.taskDeadline,
				taskDefId : rptTskInfo.taskDefId,
				taskFreq : rptTskInfo.taskFreq,
				taskNm : rptTskInfo.taskNm,
				taskSts : rptTskInfo.taskSts,
				taskType : rptTskInfo.taskType,
				triggerId : rptTskInfo.triggerId,
				triggerType : rptTskInfo.triggerType,
				triggerNm : rptTskInfo.triggerNm,
				upTaskId : rptTskInfo.upTaskId,
				isPre : rptTskInfo.isPre,
				dateOffsetAmount : rptTskInfo.dateOffsetAmount,
				invalidDate : inv,
				isRelyData : rptTskInfo.isRelyData,
				logicDelNo : rptTskInfo.logicDelNo,
				selectedObjId:tskExeobjRelVO.exeObjId,
				selectedObjNm:tskExeobjRelVO.exeObjNm,
				afterTaskSts : rptTskInfo.afterTaskSts
				//,exeObjType:tskExeobjRelVO.exeObjType
			});
			$.ligerui.get("triggerCombBox").setText(rptTskInfo.triggerNm);
			//var typeText = tskExeobjRelVO.exeObjType==null?"": tskExeobjRelVO.exeObjType=="02"?"指标补录":"明细补录";
			var typeText="02";
			//$.ligerui.get("exeObjTypeSelect")._changeValue(tskExeobjRelVO.exeObjType,typeText);
			$.ligerui.get("selectedObjCombBox").setValue(tskExeobjRelVO.exeObjId);
			$.ligerui.get("selectedObjCombBox").setText(tskExeobjRelVO.exeObjNm);
			
			if(afterTaskVo){
				$.ligerui.get("afterTaskCombBox").setValue(JSON2.stringify(afterTaskVo));
				$.ligerui.get("afterTaskCombBox").setText(afterTaskVo.rptTskInfo.taskNm);
			}
			

			
		} else {
			$.ligerui.get("triggerCombBox").setDisabled();
			$("#mainform input[name='dateOffsetAmount']").attr("disabled", "disabled");
			mainform.setData({
				dateOffsetAmount : 0,
				triggerType : "2",
				taskSts : "1",
				afterTaskSts : "0",
				taskType : "02"
			});
		}
// 		$("#mainform input[name='taskNm']").attr("readonly", "true").removeAttr("validate");
		$("#selectedObjNm").attr("disabled", "disabled");
	}

	function gatherData() {
		var deployTask = {
				rptTskInfo : null,
				taskNodeOrgList : [],
				tskExeobjRelVO : null,
				orgs : [],
				taskUnifyNodeList : []
			};
		if($("#mainform").valid()){
			var afterTaskSts = $("#mainform input[name='afterTaskSts']").val();
			var triggerType = $("#mainform input[name='triggerType']").val();
			var triggerId = $.ligerui.get("triggerCombBox").getValue();
			if(triggerType=="2"&&(triggerId==null||triggerId=="")){
				BIONE.tip('请选择触发器');
				return null;
			}
			deployTask.rptTskInfo = {
				taskId : taskId,
				checkSts : $("#mainform input[name='checkSts']").val(),
				effectDate : $("#mainform input[name='effectDate']").val().replace("-","").replace("-",""),
				exeObjId : $("#mainform input[name='exeObjId']").val(),
				//exeObjType : $("#mainform input[name='exeObjType']").val(),
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
				afterTaskSts : $("#mainform input[name='afterTaskSts']").val(),
				"catalogId" : catalogId
			};

			deployTask.tskExeobjRelVO = {
					//exeObjType:$("#exeObjType").val(),
					exeObjType:"02",
					exeObjId:$.ligerui.get("selectedObjCombBox").getValue(),
					exeObjNm:$.ligerui.get("selectedObjCombBox").getText()
			};
			deployTask.rptTskInfo.exeObjType = "02";//$("#exeObjType").val();
			deployTask.rptTskInfo.exeObjId = $.ligerui.get("selectedObjCombBox").getValue();
			if(afterTaskSts == 1){
				if($("#afterTask").val() == ""){
					BIONE.tip("请配置后置任务信息.");
				    return null;
				}
				deployTask.afterTask = JSON.parse($("#afterTask").val());
				deployTask.rptTskInfo.afterTaskObjId = deployTask.afterTask.rptTskInfo.taskId;
			}
			return deployTask;
		}
		return null;
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

  //弹出窗口中选择后置任务
    function showAfterTaskDialog(options) {
        var triggerType = $("#mainform input[name='triggerType']").val();
        var options = {
            url : "${ctx}/rpt/input/task/afterTaskOption?triggerType=" + triggerType + "&taskId=" + taskId,
            dialogname : 'afterTaskBox',
            title : '配置后置任务'
        };
        var width = $(parent.document).width()*0.75;
        var height = $(parent.document).height()*0.95;
        parent.BIONE.commonOpenDialog(options.title, options.dialogname,width,height,options.url);
        return false;
    }

    parent.setDeployTask = function (name,val){
        $("#afterTaskCombBox").val(name);
        $("#afterTask").val(val);
    }

    parent.getDeployTask = function (name,val){
        return $("#afterTask").val()
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