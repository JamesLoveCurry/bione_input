<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<%@ include file="/common/taglibs.jsp"%>
<html>
<head>
<meta name="decorator" content="/template/template5.jsp">
<script type="text/javascript">
	//全局变量
	var groupicon = "${ctx}/images/classics/icons/communication.gif";
	var  mainform=null;
	function initMainForm(){
		//渲染表单
		mainform = $("#mainform");
		mainform
				.ligerForm({
					fields : [{
								name : 'timeoutTime',
								newline : false,
								type : 'hidden',
								value : '1000'
							},{
								display : '节点名称',
								name : 'nodeNm',
								newline : false,
								type : 'text',
								validate:{
									required: true,
									remote : "${ctx}/report/frame/engine/log/node/checkNodeNm?d="+new Date(),
									messages : {
										remote : "该节点名称已经存在"
									}
								},
								group : "引擎节点信息",
								groupicon : groupicon,
							},{
								display : '节点类型',
								name : 'nodeTypeBox',
								newline : false,
								comboboxName: "nodeTypeCombo",
								type : 'select',
								options : {
									data :[{
										"id"  :"01",
										"text":"计算节点"
										}, {
								        "id"  :"02",
								        "text":"查询节点"
								        }, {
								        "id"  :"03",
								        "text":"实时检核节点"
								        }, {
								        "id"  :"04",
								        "text":"实时机构汇总节点"
								        }, {
								        "id"  :"05",
								        "text":"批量检核节点"
								        }, {
								        "id"  :"06",
								        "text":"批量机构汇总节点"
								       },{
										 "id"  :"07",
										 "text":"批量计算节点"
									   },{
										   "id"  :"11",
											"text":"引擎节点缓存1"
									   },{
										   "id"  :"12",
											"text":"引擎节点缓存2"
									   }]
								},
								validate:{
									required: true
								}
							},{
								display : '节点地址',
								name : 'ipAddress',
								newline : true,
								type : 'text',
								validate:{
									required: true
								}
							},{
								display : '节点端口',
								name : 'port',
								newline : false,
								type : 'digits',
								validate:{
									required: true
								}
							},{
								display : '最大并发数',
								name : 'maxThread',
								newline : true,
								type : 'digits',
								validate:{
									required: true
								}
							},{
								display : '备注',
								name : 'remark',
								newline : true,
								width: '410',
								type : 'textarea'
							}]

				});
		jQuery.metadata.setType("attr", "validate");
		BIONE.validate(mainform);
	}
	function initData(){
		if("${nodeId}"!=""){
			$.ajax({
				cache : false,
				async : true,
				url : "${ctx}/report/frame/engine/log/node/${nodeId}?d="+new Date(),
				dataType : 'json',
				type : "GET",
				success : function(result) {
					$("#nodeNm").val(result.nodeNm);
					$("#nodeNm").attr("readonly",true);
					$.ligerui.get("ipAddress").updateStyle();
					$("#nodeNm").attr("validate","{required : true}");
					$("#ipAddress").val(result.ipAddress);
					$("#port").val(result.port);
					$("#maxThread").val(result.maxThread);
					$.ligerui.get("nodeTypeCombo").selectValue(result.nodeType);
					$("#remark").val(result.remark);
				}
			});
		}
	}
	function f_save(){
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
		var btns = [{
			text : "取消",
			onclick : function() {
				BIONE.closeDialog("engineNodeDialog");
			}
		}, {
			text : "保存",
			onclick : function() {
				f_save();
			}
		}];
		BIONE.addFormButtons(btns);
	});
</script>
<title></title>
</head>
<body>
	<div id="template.center">
		<form id="mainform"
			action="${ctx}/report/frame/engine/log/node"
			method="POST"></form>
	</div>
</body>
</html>