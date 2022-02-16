<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<%@ include file="/common/taglibs.jsp"%>
<html>
<head>
<meta name="decorator" content="/template/template5.jsp" />
<script type="text/javascript">
	var mainform;
	var lineId= "${lineId}";
	var lineNm= "${lineNm}";
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
				url : "${ctx}/rpt/frame/businessline/getLineModify",
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
					$("#mainform input[name='rankOrder']").val(result.rankOrder);
				}
			});
		}
	}

	function initBtn(){
		//添加按钮
		var btns = [ {
			text : "取消",
			onclick : function() {
				BIONE.closeDialog("editlineDialog");//关闭对话框
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
				window.parent.grid.loadData();
				BIONE.closeDialog("editlineDialog", "保存成功");//关闭对话框
			}, function() {
				BIONE.closeDialog("editlineDialog", "保存失败");
			});
		}else{
			//新增
			var _lineId = $("#mainform input[name='lineId']").val();
			$.ajax({
				cache : false,
				async : false,                         //true为异步执行
				url : "${ctx}/rpt/frame/businessline/lineCheck",
				dataType : 'json',
				data: {					
					lineId : _lineId
				},
				type : "get",
				success : function(data) {
					if(data.msg == "1"){
	 					BIONE.tip("业务条线ID已存在");
	 					return;
					}else{
						BIONE.submitForm($("#mainform"), function() {
							window.parent.grid.loadData();
							BIONE.closeDialog("editlineDialog", "保存成功");//关闭对话框
						}, function() {
							BIONE.closeDialog("editlineDialog", "保存失败");
						});
					}
				}
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
								display : "业务条线编号",
								name : "lineId",
								width : 200,
							    labelwidth : 100,
								newline : true,
								validate : {
									required : true,
									messages : {
										required : "业务条线编号不能为空"
									}
								},
								type : "text"
							},{
								display : "业务条线名称",
								name : "lineNm",
								width : 200,
								labelwidth : 100,
								newline : true,
								validate : {
									required : true,
									messages : {
										required : "业务条线名称不能为空"
									}
								},
								type : "text"
							},{
								display : "序号",
								name : "rankOrder",
								width : 200,
								labelwidth : 100,
								newline : true,
								validate : {
									required : true,
									messages : {
										required : "序号不能为空"
									}
								},
								type : "digits"
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
		<form id="mainform" action="${ctx}/rpt/frame/businessline/saveBusinessLine"
			method="POST"></form>
	</div>
</body>
</html>