<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<%@ include file="/common/taglibs.jsp"%>
<html>
<head>
<meta name="decorator" content="/template/template5.jsp" />
<script type="text/javascript">
	var mainform;
	var themeId= "${themeId}";
	var indexId= "${indexId}";
	var upNo= "${upNo}";
	var currNm= "${currNm}";
	var indexLevel = "${upIndexLevel}" == "" ? 0 : (parseInt("${upIndexLevel}") +1);
	$(function() {
		initForm();
		initData();
		initBtn();
	});
	
	function initData(){
		//修改时初始化表单
		if (indexId != "" && indexId != null) {
			$.ajax({
				cache : false,
				async : true,
				url : "${ctx}/frame/idx/bank/getBankInfo",
				dataType : 'json',
				data: {
					indexId : indexId,
					themeId : themeId
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
					$("#mainform input[name='id.themeId']").val(result.id.themeId);
					$("#mainform input[name='id.indexId']").val(result.id.indexId);
					$("#mainform input[name='upNo']").val(result.upNo);
					$("#mainform input[name='indexNm']").val(result.indexNm);
					$.ligerui.get("mainNo")._changeValue(result.mainNo,result.mainNm);
					$.ligerui.get("partNo")._changeValue(result.partNo,result.partNm);
					$.ligerui.get("currency")._changeValue(result.currency,currNm);
					$("#remark").val(result.remark);
					$("#mainform input[name='indexLevel']").val(result.indexLevel);
				}
			});
		}else{
			$("#mainform input[name='indexLevel']").val(indexLevel);
		}
	}

	function initBtn(){
		//添加按钮
		var btns = [ {
			text : "取消",
			onclick : function() {
				BIONE.closeDialog("editBankWin");
			}
		}, {
			text : "保存",
			onclick : f_save
		} ];
		BIONE.addFormButtons(btns);
	}
	
	//保存
	function f_save() {
		BIONE.submitForm($("#mainform"), function() {
			window.parent.grid.loadData();
			BIONE.closeDialog("editBankWin", "保存成功");
		}, function() {
			BIONE.closeDialog("editBankWin", "保存失败");
		});
	}
	
	function initForm(){
		//渲染表单
		mainform = $("#mainform");
		mainform.ligerForm({
					inputWidth : 160,
					labelWidth : 80,
					space : 30,
					fields : [
							{
								name : "id.indexId",
								type : "hidden"
							},
							{
								name : "indexLevel",
								type : "hidden"
							},
							{
								name : "upNo",
								type : "hidden"
							},
							{
								name : "id.themeId",
								type : "hidden"
							},{
								group : "指标信息",
								groupicon : "${ctx}/images/classics/icons/communication.gif",
								display : "指标名称",
								name : "indexNm",
								width : 150,
								newline : false,
								type : "text",
								validate : {
									required : true,
									maxlength : 100,
									remote :{
										url: "${ctx}/frame/idx/bank/validateIndexNm?themeId=${themeId}&indexId=${indexId}&d="
											+ new Date().getTime(),
										type:'post',
										data : {
											indexNm : $("#indexNm").val()
										}
									},
									messages : {
										required : "指标名称不能为空。",
										remote : "在此主题下，已存在同名指标。"
									}
								}
							}, {
								display : '币种类型',
								name : 'currency',
								width : 150,
								newline : false,
								type : "select",
								options : {
									url : "${ctx}/frame/idx/bank/getCurrType.json"
								}
							},{
								display : "主指标",
								name : "mainNo",
								width : 150,
								type : "select",
								options:{
									onBeforeOpen:function(){
										window.parent.selectTab=window;
										window.comboNm = "mainNo";
										parent.BIONE.commonOpenDialog(
												"主指标选择",
												"mainIdx",
												400,
												380,
												"${ctx}/frame/idx/bank/mainIdxWin",
												null
											);
											return false;
									}
								},
								validate : {
									required : true
								}
							},{
								display : "分指标",
								name : "partNo",
								newline : false,
								width : 150,
								type : "select",
								options:{
									onBeforeOpen:function(){
										window.parent.selectTab=window;
										window.comboNm = "partNo";
										parent.BIONE.commonOpenDialog(
												"分指标选择",
												"mainIdx",
												400,
												380,
												"${ctx}/frame/idx/bank/mainIdxWin",
												null
											);
											return false;
									}
								}
							},{
								display : "描述",
								name : "remark",
								width : 410,
								newline : true,
								type : "textarea",
								validate : {
									maxlength : 500
								}
							}]

				});
		$("#remark").css("resize","none");
		jQuery.metadata.setType("attr", "validate");
		BIONE.validate("#mainform");
		//表单赋值
		$("#mainform input[name='id.themeId']").val("${themeId}");
		$("#mainform input[name='id.indexId']").val("${indexId}");
		$("#mainform input[name='upNo']").val("${upNo}");
	}
</script>
<title>Insert title here</title>
</head>
<body>
	<div id="template.center">
		<form id="mainform" action="${ctx}/frame/idx/bank/saveIdx"
			method="POST"></form>
	</div>
</body>
</html>