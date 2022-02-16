<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<%@ include file="/common/taglibs.jsp"%>
<html>
<head>
<meta name="decorator" content="/template/template1_BS.jsp">
<script type="text/javascript" src="${ctx}/frs/js/rptfill/rpt-reject-index.js"></script>
<script type="text/javascript">
    var ctx = "${ctx}";
	var moduleType="${moduleType}";
	var operType = "${operType}";
	var taskComBoBoxUrl = "${ctx}/frs/rptfill/reject/taskNmComBoBox?orgTypes=${moduleType}&flag=1";//一次任务
	var rptComBoBoxUrl = "${ctx}/frs/rptfill/reject/rptNmComBoBox";
	var orgTreeSkipUrl = "${ctx}/frs/integratedquery/rptquery/searchOrgSetInfo?orgType=" + moduleType;
	var fields = [ "handSts", "taskNm", "rptNm", "orgNm","dataDate", "logicRsSts"];
	var gridUrl = "${ctx}/frs/rptfill/reject/flTaskList?orgTypes=${orgType}&moduleType=${moduleType}&operType=${operType}";
	var columns = ["taskNm", "dataDate", "endTime", "taskObjNm", "exeObjNm", "sts", "logicRs", "applySts"];//applySts，申请状态，数据库无该字段
	
	//初始化函数
	$(function() {
		searchForm();
		initGrid();
		initButtons();
		addMySearchButtons("#search", grid, "#searchbtn");
	});
	
	//搜索表单应用ligerui样式
	function searchForm() {
		var demoWidth = $("#search").width();
		var newLineNum = parseInt(demoWidth/260);
		CommonSerchForm(taskComBoBoxUrl, rptComBoBoxUrl, orgTreeSkipUrl, null, fields, "1", newLineNum);
	}
	
	//初始化Grid
	function initGrid() {
		TaskFillGrid(gridUrl, columns, false);
	}
	
	//初始化Button
	function initButtons() {
		var btns = [ {
			text : '申请解锁',
			click : apply_reject,
			icon : 'fa-unlock-alt',
			operNo : 'apply_reject'
		}];
		BIONE.loadToolbar(grid, btns, function() {
		});
	}
	//申请驳回
	function apply_reject() {
		var rows = grid.getSelectedRows();
		if (rows.length > 0) {
			var rows = grid.getSelectedRows();
            var taskInstanceId = new Array();
            for(var j = 0; j < rows.length; j++) {
                taskInstanceId.push(rows[j].taskInstanceId);
            }
			$.ajax({
				cache : false,
				async : true,
				url : "${ctx}/frs/rptfill/reject/rejectJudge?moduleType=${moduleType}",
				dataType : 'json',
				type : "post",
				data : {
					"taskInstanceId" : JSON.stringify(taskInstanceId)
				},
				success : function(result) {
					if (result) {
						BIONE.commonOpenDialog(
								"申请解锁理由",
								"applyRejWin",
								"550",
								"300",
								"${ctx}/frs/rptfill/reject/applyReject?taskInstanceId="+ JSON.stringify(taskInstanceId), null,function(){
                                                                                                                             							initGrid();
                                                                                                                             					});
					} else {
						BIONE.tip("部分任务实例已申请");
					}
				},
				error : function(result, b) {
					BIONE.tip('发现系统错误 <BR>错误码：' + result.status);
				}
			});
		} else {
			BIONE.tip("请至少选择一条记录");
		}
	}
	//查看申请
	function view_reject() {
		var height = $(window).height() - 30;
		var width = $(window).width() - 10;
		BIONE.commonOpenDialog(
				"申请信息查看",
				"viewRejWin",
				width,
				height,
				"${ctx}/frs/rptfill/reject/rejectView?orgTypes=${orgType}&moduleType=${moduleType}&operType=${operType}",null);
	}

	// 创建表单搜索按钮：搜索、高级搜索
	function addMySearchButtons(form, grid, btnContainer) {
		if (!form)
			return;
		form = $(form);
		if (btnContainer) {
			BIONE.createButton({
				appendTo : btnContainer,
				text : '查询',
				icon : 'fa-search',
				// width : '50px',
				click : function() {
					if ($.ligerui.get("logicRsSts_sel") && $.ligerui.get("logicRsSts_sel").getValue()) {
						grid.setParm("logicRsSts", $.ligerui.get("logicRsSts_sel")
								.getValue());
					} else {
						grid.removeParm("logicRsSts");
					}
					if ($.ligerui.get("sumpartRsSts_sel") && $.ligerui.get("sumpartRsSts_sel").getValue()) {
						grid.setParm("sumpartRsSts", $.ligerui.get("sumpartRsSts_sel")
								.getValue());
					} else {
						grid.removeParm("sumpartRsSts");
					}
					if ($.ligerui.get("warnRsSts_sel") && $.ligerui.get("warnRsSts_sel").getValue()) {
						grid.setParm("warnRsSts", $.ligerui.get("warnRsSts_sel")
								.getValue());
					} else {
						grid.removeParm("warnRsSts");
					}
					if ($.ligerui.get("zeroRsSts_sel") && $.ligerui.get("zeroRsSts_sel").getValue()) {
						grid.setParm("zeroRsSts", $.ligerui.get("zeroRsSts_sel")
								.getValue());
					} else {
						grid.removeParm("zeroRsSts");
					}
					var rule = BIONE.bulidFilterGroup(form);
					if (rule.rules.length) {
						grid.setParm("condition", JSON2.stringify(rule));
						grid.setParm("newPage", 1);
					} else {
						grid.setParm("condition", "");
						grid.setParm('newPage', 1);
					}
					grid.loadData();
				}
			});
	 		BIONE.createButton({
				appendTo : btnContainer,
				text : '重置',
				icon : 'fa-repeat',
				// width : '50px',
				click : function() {
					$(":input", form).not(":submit, :reset,:hidden,:image,:button, [disabled]").each(function() {
						$(this).val("");
					});
					$(":input[ltype=combobox]", form).each(function() {
						var ligerid = $(this).attr(
								'data-ligerid'), ligerItem = $.ligerui
								.get(ligerid);// 需要配置comboboxName属性
						if (ligerid && ligerItem
								&& ligerItem.clear) {// ligerUI
							// 1.2
							// 以上才支持clear方法
							ligerItem.clear();
						} else {
							$(this).val("");
						}
						grid.removeParm("logicRsSts");
						grid.removeParm("sumpartRsSts");
						grid.removeParm("warnRsSts");
					});
					$(":input[ltype=select]", form).each(function() {
						var ligerid = $(this).attr(
								'data-ligerid'), ligerItem = $.ligerui
								.get(ligerid);// 需要配置comboboxName属性
						if (ligerid && ligerItem
								&& ligerItem.clear) {// ligerUI
							// 1.2
							// 以上才支持clear方法
							ligerItem.clear();
						} else {
							$(this).val("");
						}
						grid.removeParm("logicRsSts");
						grid.removeParm("sumpartRsSts");
						grid.removeParm("warnRsSts");
					});
				}
			}); 
		}
	}
</script>
</head>
<body>
</body>
</html>