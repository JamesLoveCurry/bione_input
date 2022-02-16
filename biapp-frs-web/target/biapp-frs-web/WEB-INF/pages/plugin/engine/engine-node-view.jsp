<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<%@ include file="/common/taglibs.jsp"%>
<html>
<head>
<meta name="decorator" content="/template/template5.jsp">
<meta name="description" content="morris charts">
<script type="text/javascript" src="${ctx}/js/echarts/js/echarts.min.js"></script>
<script type="text/javascript">
	//全局变量
	var groupicon = "${ctx}/images/classics/icons/communication.gif";
	var mainform = null;
	function initMainForm() {
		//渲染表单
		mainform = $("#mainform");
		mainform.ligerForm({
			inputWidth : 210,
			fields : [ {
				display : '节点名称',
				name : 'nodeNm',
				newline : false,
				type : 'text',
				attr : {
					readonly : true
				},
				group : "引擎节点信息",
				groupicon : groupicon,
			}, {
				display : '节点类型',
				name : 'nodeTypeBox',
				newline : false,
				comboboxName : "nodeTypeCombo",
				type : 'select',
				options : {
					readonly : true,
					data : [ {
						"id" : "01",
						"text" : "计算节点"
					}, {
						"id" : "02",
						"text" : "查询节点"
					}, {
						"id" : "03",
						"text" : "实时检核节点"
					}, {
						"id" : "04",
						"text" : "实时机构汇总节点"
					}, {
						"id" : "05",
						"text" : "批量检核节点"
					}, {
						"id" : "06",
						"text" : "批量机构汇总节点"
					}, {
						"id" : "07",
						"text" : "批量计算节点"
					}, {
						"id" : "11",
						"text" : "引擎节点缓存1"
					}, {
						"id" : "12",
						"text" : "引擎节点缓存2"
					} ]
				}
			}, {
				display : '节点地址',
				name : 'ipAddress',
				newline : false,
				type : 'text',
				attr : {
					readonly : true
				}
			}, {
				display : '节点端口',
				name : 'port',
				newline : false,
				type : 'text',
				attr : {
					readonly : true
				}
			}, {
				display : '最大并发数',
				name : 'maxThread',
				newline : false,
				type : 'text',
				attr : {
					readonly : true
				}
			}, {
				display : '备注',
				name : 'remark',
				newline : false,
				width : '210',
				type : 'textarea'
			} ]

		});
		$("#remark").attr("readonly", true);
	}
	function initChart(data) {
		var myChart = echarts.init(document.getElementById('chart'));
		var is_online;
		var is_online_color;
		if (data.nodeSts == 'Y') {
			is_online = '在线';
			is_online_color = 'green';
		} else {
			is_online = '离线';
			is_online_color = 'red';
		}
		var option = {
			title : {
				text : '节点' + data.nodeNm,
				subtext : is_online,
				subtextStyle : {
					color : is_online_color
				},
				x : 'center'
			},
			tooltip : {
				trigger : 'item',
				formatter : "{a} <br/>{b} : {c} ({d}%)"
			},
			legend : {
				x : 'center',
				y : 'bottom',
				data : [ '空闲线程数', '使用线程数' ]
			},
			color : [ '#009100', '#BB3D00' ],
			series : [ {
				name : '使用状态',
				type : 'pie',
				radius : '55%',
				center : [ '50%', '60%' ],
				data : [ {
					value : data.freeCount,
					name : '空闲线程数'
				}, {
					value : data.activeCount,
					name : '使用线程数'
				} ]
			} ]
		};
		myChart.setOption(option);
	}
	function initData() {
		if ("${nodeId}" != "") {
			$
					.ajax({
						cache : false,
						async : true,
						url : "${ctx}/report/frame/engine/log/node/getNode/${nodeId}?d="
								+ new Date(),
						dataType : 'json',
						type : "GET",
						success : function(result) {
							$("#nodeNm").val(result.nodeNm);
							$("#ipAddress").val(result.ipAddress);
							$("#port").val(result.port);
							$("#maxThread").val(result.maxThread);
							$.ligerui.get("nodeTypeCombo").selectValue(
									result.nodeType);
							$("#remark").val(result.remark);
							initChart(result);
						}
					});
		}
	}
	function f_save() {
		BIONE.submitForm($("#mainform"), function() {
			window.parent.grid.loadData();
			BIONE.closeDialog("engineNodeDialog", "保存成功");
		}, function() {
			BIONE.tip("engineNodeDialog", "保存失败");
		});
	}
	$(function() {
		initMainForm();
		initData();
		//添加表单按钮
		var btns = [ {
			text : "取消",
			onclick : function() {
				BIONE.closeDialog("engineNodeDialog");
			}
		} ];
		BIONE.addFormButtons(btns);
	});
</script>
<title></title>
</head>
<body>
	<div id="template.center">
		<div id="left" style="width: 50%; height: 100%; float: left">
			<div id="chart" class="chart" style="width: 100%; height: 100%;"></div>
		</div>
		<div id="right" style="width: 50%; float: left">
			<form id="mainform" action="${ctx}/report/frame/engine/log/node"
				method="POST"></form>
		</div>

	</div>
</body>
</html>