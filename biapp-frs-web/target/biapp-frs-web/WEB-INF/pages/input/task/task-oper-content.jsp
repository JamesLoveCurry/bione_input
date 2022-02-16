<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<%@ include file="/common/taglibs.jsp"%>
<html>
<meta name="decorator" content="/template/template32.jsp">
<head>
<style>
.indexStsA, .indexNmA {
	width: 55%;
	cursor: pointer;
}

.stop {
	color: red;
}
</style>
<!-- 基础的JS和CSS文件 -->
<script type="text/javascript">
	var taskInstanceId = '${taskInstanceId}';
	var taskNodeInstanceId = '${taskNodeInstanceId}';
	var taskType = '${taskType}';
	var canReOpen = '${canReOpen}';
	var frameControl;
	var downdload;
	var canEdit = window.parent.canEdit || false;
	var canSave = window.parent.canEdit || false;
	var canSubmit = window.parent.canSubmit || false;
	var canRebut = window.parent.canRebut || false;
	var taskIndexType = '${taskIndexType}';
	var taskType = '${taskTypeList}';
	$(function() {
		initBaseData();
		initFrame();
	});

	function initBaseData() {
		initOper();
	}

	function initOper() {
		//$("#mainoper").append("123123'');
		if (taskIndexType && taskIndexType != null) {
			taskType = taskType || "01";
			if (taskIndexType == "01") {
				//填报任务
				if (taskType == 1) {
					//待处理
					canSave = true;
					canSubmit = true;
				}else{
					canSave = false;
					canSubmit = false;
					canReOpen = false;
				}
			} else if (taskIndexType != "01") {
				//审批任务
				if (taskType == 1) {
					canSubmit = true;
					canRebut = true;
				}
			}
		}
		if (canSave) {
			$("#mainoper")
					.append(
							'<input type=\"button\" value=\"保存\"  class=\"l-button l-button-submit\"  onclick=\"onSave();\"/>');
		}
		if (canSubmit) {
			if (taskIndexType == "01") {
				$("#mainoper").append('<input type=\"button\" value=\"提交\"   class=\"l-button l-button-submit\"  onclick=\"onSubmit();\"/>');
			}else if (taskIndexType == "02") {
				$("#mainoper").append('<input type=\"button\" value=\"通过\"   class=\"l-button l-button-submit\"  onclick=\"onSubmit();\"/>');
			}
		}
		if (canRebut) {
			if (taskIndexType == "01") {
				$("#mainoper").append('<input type=\"button\" value=\"驳回\"   class=\"l-button l-button-submit\"  onclick=\"onRebut();\"/>');
			}else if (taskIndexType == "02") {
				$("#mainoper").append('<input type=\"button\" value=\"驳回\"   class=\"l-button l-button-submit\"  onclick=\"onRebut();\"/>');
			}
		}
		if (canSave) {
			downdload = $('<iframe id="download"  style="display: none;"/>');
			$('body').append(downdload);
			$("#mainoper")
					.append(
							'<input type=\"button\" value=\"导出Excel\"  class=\"l-button l-button-submit\"  onclick=\"onExport();\"/>');
			downdload = $('<iframe id="download"  style="display: none;"/>');
			$('body').append(downdload);
			$("#mainoper")
					.append(
							'<input type=\"button\" value=\"导入Excel\"  class=\"l-button l-button-submit\"  onclick=\"onImport();\"/>');
		}

		if (canReOpen == "true") {
			if(!parent.pageType || parent.pageType != 'taskMonitor'){
				$("#mainoper")
						.append(
								'<input type=\"button\" value=\"回写\"   class=\"l-button l-button-submit\"  onclick=\"onReWrite();\"/>');
			}
// 			$("#mainoper")
// 					.append(
// 							'<input type=\"button\" value=\"回退\"   class=\"l-button l-button-submit\"  onclick=\"onReOpen();\"/>');
		}
	}

	function onReWrite() {
		$.ligerDialog.confirm("是否要回写数据？", function(yes) {
			if (yes) {
				$.ajax({
					cache : false,
					async : true,
					url : "${ctx}/rpt/input/taskoper/reWrite",
					dataType : 'json',
					type : "post",
					data : {
						"taskInstanceId" : taskInstanceId
					},
					success : function(result) {
						if (result != "1") {
							BIONE.tip(result);
							return;
						}
						BIONE.tip('数据回写成功');
					},
					error : function(result, b) {
						BIONE.tip('回写失败,请确定回写设置无误,如果还有问题,请联系管理员');
					}
				});
			}
		});
	}
	function onReOpen() {
		$.ligerDialog.confirm("是否要回退任务到最后一个流程？", function(yes) {
			if (yes) {
				$.ajax({
					cache : false,
					async : true,
					url : "${ctx}/rpt/input/taskoper/reOpenTask",
					dataType : 'json',
					type : "post",
					data : {
						"taskInstanceId" : taskInstanceId,
						"taskNodeInstanceId" : frameControl
								.getTaskNodeInstanceId(),
						"templateId" : frameControl.getTemplateId()
					},
					success : function(result) {
						if (!result) {
							BIONE.tip('回退失败');
							return;
						}
						parent.refreshGrid();
						BIONE.tip('任务已经回退到最后一个流程');
						BIONE.closeDialog("operTaskBox");
					},
					error : function(result, b) {
						BIONE.tip('发现系统错误 <BR>错误码：' + result.status);
					}
				});
			}
		});
	}

	//模板下载
	var download;


	function onExport(){
		onSave(f_export);
	}
	
	function f_export() {
		var grid = frameControl.grid;
		var rows = grid.getCheckedRows();
		var taskId = frameControl.taskId;
		var caseId = frameControl.taskInstanceId;
		if (taskId) {
			$.ajax({
						async : true,
						url : '${ctx}/rpt/input/temple/task/temple?taskId='
								+ taskId + '&d=' + new Date().getTime(),
						beforeSend : function() {
							BIONE.loading = true;
							BIONE.showLoading("正在导出数据中...");
						},
						complete : function() {
						},
						success : function(data) {
							if (data.length > 0) {
								if (!document.getElementById('download')) {
									$('body')
											.append(
													$('<iframe id="download" style="display:none;"/>'));
								}
								$("#download").attr(
										'src',
										'${ctx}/rpt/input/taskcase/excel/'
		                                + taskId + '?taskInstanceId=' + taskInstanceId 
		                                + '&caseId=' + caseId 
		                                + '&templeId=' + data
		                                + '&taskNodeInstanceId=' + taskNodeInstanceId
		                                + '&d=' + new Date().getTime());
							} else {
								BIONE.showError("模板中没有启用的excel模板。");
							}
							
							BIONE.loading = false;
							BIONE.hideLoading();
						}
					});
		}
	}

	function onImport() {
		onSave(doImport);
	}
	function doImport(value) {
		frameControl.doImport(value);
	}

	function onSave(impFunc) {
	    frameControl.initAutoLoadRows()
	    var templateId = frameControl.templateId;
	        $.ajax({
	            data : {
	                templeId : templateId,
	                taskInstanceId : taskInstanceId,
	                taskNodeInstanceId : taskNodeInstanceId
	            },
	            type : "POST",
	            dataType : 'json',
	            url : "${ctx}/rpt/input/taskcase/saveAutoLoad",
	            beforeSend : function() {
	                BIONE.loading = true;
	                BIONE.showLoading("正在保存数据中...");
	            },
	            complete : function() {
	                BIONE.loading = false;
	                BIONE.hideLoading();
	            },
	            success : function(data) {
		            if(data.flag){
		                frameControl.updateDateType();
						frameControl.save(01,impFunc);
		            }else{
		                BIONE.tip(data.msg);
		            }
	            },
	            error : function(result, b) {
	                BIONE.tip("数据保存失败,请联系管理员.");
	            }
	        });
	}
	


	function closeDialog(isSucc) {
		if (isSucc == "01") {
			window.parent.refreshGrid();
			BIONE.tip('保存成功');
			BIONE.closeDialog("operTaskBox");
		}
	}

	function onRebut() {
		frameControl.save(03);

	}
	function doRebut(tni) {
		if (typeof taskIndexType != "undefined" && taskIndexType == "01") {
			$.ligerDialog.confirm("是否要驳回任务？", function(yes) {
				if (yes) {
					rebutIt(tni);
				}
			});
		} else
			rebutIt(tni);
	}

	function rebutIt(tni) {
		$.ajax({
			cache : false,
			async : true,
			url : "${ctx}/rpt/input/taskoper/rebutTask",
			dataType : 'json',
			type : "post",
			beforeSend : function() {
				BIONE.loading = true;
				BIONE.showLoading("正在驳回中...");
			},
			complete : function() {
				BIONE.loading = false;
				BIONE.hideLoading();
			},
			data : {
				"taskInstanceId" : taskInstanceId,
				"taskNodeInstanceId" : taskNodeInstanceId,
				"templateId" : frameControl.getTemplateId(),
				"rebutNode" : tni
			},
			success : function(result) {
				if (!result) {
					BIONE.tip('不能驳回,上一个节点为空');
					return;
				}
				parent.refreshGrid();
				BIONE.tip('提交成功');
				BIONE.closeDialog("operTaskBox");
			},
			error : function(result, b) {
				BIONE.tip('提交成功');
			}
		});
	}
	//点击提交按钮 触发
	function onSubmit() {
		frameControl.save(02);			
	}

	//任务 提交逻辑
	function doSubmit(templateId) {
		/* if (typeof taskIndexType != "undefined" && taskIndexType == "01") {
			$.ligerDialog.confirm("将要关闭当前节点操作,请确认当前节点下的所有处理人都完成操作,是否要提交任务？",function(yes) {
				if (yes) {
					BIONE.showLoading("任务提交中...");
					$.ajax({
						cache : false,
						url : "${ctx}/rpt/input/taskoper/submitTask?d="+ new Date().getTime(),
						dataType : 'json',
						type : "post",
						data : {
							"taskInstanceId" : taskInstanceId,
							"taskNodeInstanceId" : taskNodeInstanceId,
							"templateId" : templateId
						},
						success : function(result) {
							BIONE.hideLoading();
							if (!result) {
								BIONE.tip('提交错误,请联系管理员');
								return;
							}
							parent.refreshGrid();
							BIONE.tip('提交成功');
							BIONE.closeDialog("operTaskBox");
						},
						error : function(result, b) {
							BIONE.hideLoading();
							BIONE.tip('发现系统错误 <BR>错误码：' + result.status);
						}
					});	
				}
			});
		} */
		//提交，通过都走这段逻辑 20191026
		BIONE.showLoading("任务提交中...");
		$.ajax({
			cache : false,
			url : "${ctx}/rpt/input/taskoper/submitTask?d="+ new Date().getTime(),
			dataType : 'json',
			type : "post",
			data : {
				"taskInstanceId" : taskInstanceId,
				"taskNodeInstanceId" : taskNodeInstanceId,
				"templateId" : templateId
			},
			success : function(result) {
				BIONE.hideLoading();
				if (!result) {
					BIONE.tip('提交错误,请联系管理员');
					return;
				}
				parent.refreshGrid();
				BIONE.tip('提交成功');
				BIONE.closeDialog("operTaskBox");
			},
			error : function(result, b) {
				BIONE.hideLoading();
				BIONE.tip('发现系统错误 <BR>错误码：' + result.status);
			}
		});	
	}

	function initFrame() {
		var width = $(document).width() - 10;
		var height = $(document).height();
		var taskIndexType = window.parent.taskIndexType;
		if (typeof taskIndexType != "undefined" && taskIndexType == "01") {
			canEdit = true;
			if (window.parent.taskTypeList) {
				var v = window.parent.taskTypeList.getValue();
				canEdit = (v == "01" || v == "1");
			}
		}else{
			height = $(document).height()-22;
		}
		var curTabUri = "${ctx}/rpt/input/task/dealTask?taskInstanceId="
				+ taskInstanceId + "&canEdit=" + canEdit + "&d=" + new Date().getTime();
		if(canEdit){
			height = $(document).height()-22;
		}
		$("#maingrid")
				.append(
						'<iframe frameborder=\"0\" id="maingridFrame" style=\"height:'+height+'px;width:'+width+'px" src='+curTabUri+' />');
	}
</script>
</head>
<body>
</body>
</html>