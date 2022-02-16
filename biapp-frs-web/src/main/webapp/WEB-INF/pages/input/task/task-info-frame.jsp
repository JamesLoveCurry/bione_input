<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<%@ include file="/common/taglibs.jsp"%>
<html>
<meta name="decorator" content="/template/template5.jsp">
<head>
<title>新增任务</title>
<script type="text/javascript">
	//指标信息缓存对象
	var taskId = '${taskId}';
	var taskJson = '${deployTaskVO}';
	var deployTask ;
	//基本信息
	var taskManage = {
		taskBaseManage : null,
		//taskTypeManage : null,
		taskNodeManage : null
	};
	//初始化
	$(function() {
		initData();
		initTab();
		initBtn();
	});

	function initTab() {
		var height = $(document).height() ;
		$("#tab").append('<div tabid="tab1" title="基本信息" />');
		//$("#tab").append('<div tabid="tab2" title="类型设置" />');
		$("#tab").append('<div tabid="tab3" title="节点设置" />');
		tabObj = $("#tab").ligerTab(
		{
			contextmenu : false,
			onBeforeSelectTabItem : function() {
				return true;
			},
			onAfterSelectTabItem : function(tabid) {
				disableBtn();
				var src = "";
				if (tabid == "tab2") {
					src = "${ctx}/rpt/input/task/type?d="
							+ new Date().getTime();
				} else if (tabid == "tab3") {
					src = "${ctx}/rpt/input/task/node?d="
							+ new Date().getTime();
				}else
					enableBtn();
				loadFrame(tabid, src, tabid + "frame");
			}
		});
		loadFrame("tab1", "${ctx}/rpt/input/task/base?d="
				+ new Date().getTime(), "tab1frame");
	}
	function loadFrame(tabId, src, id) {
		var height = $(document).height() -75;
		if ($('#' + id).attr('src')) {
			enableBtn();
			return;
		}
		var frame = $('<iframe/>');
		frame.attr({
			id : id,
			frameborder : 0,
			src : src
		}).css("height", height);
		$('div[tabId=' + tabId + ']').append(frame);
	}
	function initData(){
		if (taskJson == null || taskJson == ""
			|| typeof taskJson == "undefined")
		{
			deployTask = {
				rptTskInfo : null,
				taskNodeOrgList : [],
				tskExeobjRelVO : null,
				orgs : [],
				taskUnifyNodeList : []
			};
		}else
			deployTask =  JSON2.parse(taskJson);
	}
	/*function initData() {
		if (taskJson == null || taskJson == ""
				|| typeof taskJson == "undefined")
			return;
		var data = JSON2.parse(taskJson);
		if (data.rptTskInfo != null) {
			deployTask.rptTskInfo = {
				taskId : data.rptTskInfo.taskId,
				checkSts : data.rptTskInfo.checkSts,
				effectDate : data.rptTskInfo.effectDate,
				exeObjId : data.rptTskInfo.exeObjId,
				exeObjType : data.rptTskInfo.exeObjType,
				sumMode : data.rptTskInfo.sumMode,
				taskDeadline : data.rptTskInfo.taskDeadline,
				taskDefId : data.rptTskInfo.taskDefId,
				taskFreq : data.rptTskInfo.taskFreq,
				taskNm : data.rptTskInfo.taskNm,
				taskSts : data.rptTskInfo.taskSts,
				taskType : data.rptTskInfo.taskType,
				triggerId : data.rptTskInfo.triggerId,
				triggerType : data.rptTskInfo.triggerType,
				triggerNm : data.rptTskInfo.triggerNm,
				upTaskId : data.rptTskInfo.upTaskId,
				isPre : data.rptTskInfo.isPre,
				dateOffsetAmount : data.rptTskInfo.dateOffsetAmount,
				invalidDate : data.rptTskInfo.invalidDate,
				isRelyData : data.rptTskInfo.isRelyData,
				logicDelNo : data.rptTskInfo.logicDelNo,
				catalogId : data.rptTskInfo.catalogId
			};

		}
		if (data.taskNodeObjList != null) {
			for ( var i = 0; i < data.taskNodeObjList.length; i++) {
				deployTask.taskNodeObjList[i] = {
					taskNodeDefId : data.taskNodeObjList[i].taskNodeDefId,
					taskOrderno : data.taskNodeObjList[i].taskOrderno,
					isCanInterrupt : data.taskNodeObjList[i].isCanInterrupt,
					taskNodeNm : data.taskNodeObjList[i].taskNodeNm,
					nodeType : data.taskNodeObjList[i].nodeType
				};

				if (data.taskNodeObjList[i].taskObjVOList != null) {
					deployTask.taskNodeObjList[i].taskObjVOList = [];
					var objVOList = data.taskNodeObjList[i].taskObjVOList;
					for ( var j = 0; j < objVOList.length; j++) {
						deployTask.taskNodeObjList[i].taskObjVOList[j] = {
							taskObjId : objVOList[j].taskObjId,
							taskObjNm : objVOList[j].taskObjNm,
							taskObjType : objVOList[j].taskObjType
						};
					}
				}
			}
		}
		if (data.tskExeobjRelVO != null) {

			deployTask.tskExeobjRelVO = {
				exeObjId : data.tskExeobjRelVO.exeObjId,
				exeObjNm : data.tskExeobjRelVO.exeObjNm,
				exeObjType : data.tskExeobjRelVO.exeObjType
			}
		}
	}
	*/

	function initBtn() {

		var buttons = [];
		buttons.push({
			text : '取消',
			onclick : f_close
		});
		buttons.push({
			text : '保存',
			onclick : f_save
		});
		/*
		buttons.push({
			text : '下发',
			onclick : f_execute
		});
		*/
		BIONE.addFormButtons(buttons);

		jQuery.metadata.setType("attr", "validate");
		BIONE.validate("#mainform");

	}

	function f_save() {
		operTask("1");
	}

	function f_execute() {
		operTask("2");
	}
	
	function doOper(){
		if(taskManage.taskNodeManage)
			taskManage.taskNodeManage.isNeedCheck = false;
		isNeedCheck = false;
		if (type == "1") {
			//执行保存操作
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
					window.parent.refreshTree();
					BIONE.tip('保存成功!');
					f_close();
				},
				error : function(result, b) {
					BIONE.tip('保存失败,发现系统错误 <BR>错误码：' + result.status);
				}
			});

		} else if (type == "2") {
			/*执行下发操作
				1. 立即下发 需要选数据日期
				2. 触发器下发,保存任务信息,定时下发
			*/
			if(deployTask.rptTskInfo.triggerType=="1"){
				showDeployDialog();
			}else{
				$.ligerDialog.confirm('确实要下发吗?', function(yes) {
					if(yes) {
						f_deploy();
					}
				});
			}
		}
	}

	function checkNode() {

		tabObj.selectTabItem("tab3");
		if(taskManage.taskNodeManage!=null)
			taskManage.taskNodeManage.gatherData();
	}

	function checkType() {

		tabObj.selectTabItem("tab2");
		if(taskManage.taskTypeManage!=null)
			taskManage.taskTypeManage.gatherData();
	}
	var type;
	var isNeedCheck = false;
	function operTask(opertype) {
		isNeedCheck = true;
		type = opertype;
		tabObj.selectTabItem("tab1");
		//type 1 保存  2 下发
		taskManage.taskBaseManage.gatherData();
	}
	function f_deploy(orgNos,dataDate) {
		if(dataDate)
			deployTask.dataDate = dataDate;
		if(orgNos)
			deployTask.orgs = orgNos;
		$.ajax({
			cache : false,
			async : true,
			url : "${ctx}/rpt/input/task/deployTask?d="
					+ new Date().getTime(),
			dataType : 'json',
			contentType : "application/json",
			type : "post",
			data : JSON2.stringify(deployTask),
			success : function(result) {
				window.parent.refreshTree();
				BIONE.tip('任务下发成功!');
				f_close();
			},
			error : function(result, b) {
				BIONE.tip('任务下发失败,发现系统错误 <BR>错误码：' + result.status);
			}
		});
	}
	//弹出窗口中选择触发器
	function showDeployDialog() {
		var options = {
			url : "${ctx}/rpt/input/task/selectDeployDept",
			dialogname : "selectDeployBox",
			title : "选择下发机构"
		};
		BIONE.commonOpenLargeDialog(options.title, options.dialogname,
				options.url);
		return false;
	}
	function f_close() {
		BIONE.closeDialog("rptTaskBox");
	}
	

	function disableBtn(){
		$("#bottom").hide();
	}
	
	function enableBtn(){
		$("#bottom").show();
	}
	
</script>
</head>
<body>
	<div id="template.center">
		<div id="tab" style="width: 100%; overflow: hidden;"></div>
	</div>
</body>
</html>