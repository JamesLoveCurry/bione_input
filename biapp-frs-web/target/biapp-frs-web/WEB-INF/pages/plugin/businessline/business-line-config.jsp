<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<%@ include file="/common/taglibs.jsp"%>
<html>
<head>
<meta name="decorator" content="/template/template5.jsp" />
<script type="text/javascript">
	var groupicon = "${ctx}/images/classics/icons/communication.gif";
	var mainform;
	var lineId= "${lineId}";
	
	
	
	$(function() {
		initForm();
		initData();
		initBtn();
	});
	
	function initData(){
		//修改时初始化表单
		if (lineId != "" && lineId != null) {
			$.ajax({
				cache : false,
				async : true,                         //true为异步执行
				url : "${ctx}/rpt/frame/businessline/getLineCfg",
				dataType : 'json',
				data: {					
					lineId : lineId
				},
				type : "post",
				beforeSend : function() {
					BIONE.loading = true;
					BIONE.showLoading("正在加载数据中...");
				},
				complete : function() {
					BIONE.loading = false;
					BIONE.hideLoading();
				},
				success : function(result) {
					$("#mainform input[name='lineId']").val(result.lineId);
					$("#mainform input[name='lineNm']").val(result.lineNm);
					$.ligerui.get("idxSourceId").selectValue(result.idxSourceId);
					$.ligerui.get("idxSetId")._changeValue(result.idxSetId,result.idxSetId);
					$.ligerui.get("rptSourceId").selectValue(result.rptSourceId);
					$.ligerui.get("rptSetId")._changeValue(result.rptSetId,result.rptSetId);
				}
			});
		}
	}

	function initBtn(){
		//添加按钮
		var btns = [ {
			text : "取消",
			onclick : function() {
				BIONE.closeDialog("configDialog");//关闭对话框
			}
		}, {
			text : "保存",
			onclick : f_save
		} ];
		BIONE.addFormButtons(btns);
	}
	
	//保存
	function f_save() {
		if (lineId != "" && lineId != null) {
			//修改 
			BIONE.submitForm($("#mainform"), function() {
				BIONE.closeDialog("configDialog", "保存成功");//关闭对话框
			}, function() {
				BIONE.closeDialog("configDialog", "保存失败");
			});
		}
	}
	
	function initForm(){
		//渲染表单
		mainform = $("#mainform");
		mainform.ligerForm({
					inputWidth : 160,
					labelWidth : 80,
					fields : [
							{
								display : "条线编号",
								name : "lineId",
								width : 200,
							    labelwidth : 100,
								newline : true,
								type : "text",
								attr:{
									readOnly : true
								},
								group : "条线信息",
								groupicon : groupicon
							},{
								display : "条线名称",
								name : "lineNm",
								width : 200,
								labelwidth : 100,
								newline : false,
								type : "text",
								attr:{
									readOnly : true
								}
							},{
								display : "数据源",
								name : "idxSourceId",
								newline : true,
								type : "select",
								width : 215,
								validate : {
									required : true,
									messages : {
										required : "请选择一个数据源"
									}
								},
								options : {
									url : "${ctx}/rpt/frame/businessline/dsList.json",
									onBeforeSelect : function(id) {
										$.ligerui.get("idxSetId")._changeValue("","");
									}
								},
								group : "指标事实表",
								groupicon : groupicon
							},{
								display : "数据库表",
								name : "idxSetId",
								newline : true,
								type : "select",
								width : 540,
								validate : {
									required : true 
								},
								options : {
									onBeforeOpen : function() {
										window.parent.win = window;
										var dsId = $("#mainform input[name='idxSourceId']").val();
										if (!dsId) {
											BIONE.tip("请选择一个数据源。");
											return false;
										}
										var url = "${ctx}/rpt/frame/businessline/tablePage?dsId="+dsId+"&type=idx";;
										dialog = window.parent.BIONE.commonOpenDialog('请选择物理表',"selectGrid", "600","350", url);
										return false;
									}
								}
							},{
								display : "数据源",
								name : "rptSourceId",
								newline : true,
								type : "select",
								width : 215,
								validate : {
									required : true,
									messages : {
										required : "请选择一个数据源"
									}
								},
								options : {
									url : "${ctx}/rpt/frame/businessline/dsList.json",
									onBeforeSelect : function(id) {
										$.ligerui.get("rptSetId")._changeValue("","");
									}
								},
								group : "报表事实表",
								groupicon : groupicon
							},{
								display : "数据库表",
								name : "rptSetId",
								newline : true,
								type : "select",
								width : 540,
								validate : {
									required : true 
								},
								options : {
									onBeforeOpen : function() {
										window.parent.win = window;
										var dsId = $(
												"#mainform input[name='rptSourceId']")
												.val();
										if (!dsId) {
											BIONE.tip("请选择一个数据源。");
											return false;
										}
										var url = "${ctx}/rpt/frame/businessline/tablePage?dsId="+dsId+"&type=rpt";
										dialog = window.parent.BIONE
												.commonOpenDialog('请选择物理表',
														"selectGrid", "700",
														"350", url);
										return false;
									}
								}
							}]

				});
		$("#remark").css("resize","none");
		jQuery.metadata.setType("attr", "validate");
		BIONE.validate("#mainform");
		//表单赋值
		$("#mainform input[name='lineId']").val("${lineId}");
		$("#mainform input[name='lineNm']").val("${lineNm}");
	}
</script>
<title>Insert title here</title>
</head>
<body>
	<div id="template.center">
		<form id="mainform" action="${ctx}/rpt/frame/businessline/saveBusinessCfg"
			method="POST"></form>
	</div>
</body>
</html>