<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ include file="/common/taglibs.jsp"%>
<html>
<meta name="decorator" content="/template/template5.jsp">
<head>
<script type="text/javascript" src="${ctx}/js/uuid/uuid.js"></script>
<script type="text/javascript">
	var groupicon = "${ctx}/images/classics/icons/communication.gif";
	var templateType = "${templateType}"
	var templateId = "${templateId}"
	//创建表单结构 
	var mainform = null;
		
	$(function() {
		initForm();
		initData();
		initButtons();
	});
	
	function initForm(){
		mainform = $("#mainform");
		mainform.ligerForm({
			fields : [{
				name : "templateSts",
				type : "hidden"
			},{
				display : "名称",
				name : "templateNm",
				newline : true,
				type : "text",
				group : "模板属性",
				groupicon : groupicon,
				validate : {
					required : true,
					maxlength : 200
				}
			},{
				display : "目录",
				name : "catalogId",
				newline : false,
				type : "select",
				validate : {
					required : true
				},
				options:{
					onBeforeOpen : function() {
						window.parent.selectedTab=window;
						window.parent.BIONE
								.commonOpenDialog(
										"目录选择",
										"catalogEdit",
										400,
										380,
										'${ctx}/report/frame/datashow/detail/cataloginfo',
										null);
						return false;
					}
				}
			}, {
				display : "备注",
				name : "templateRemark",
				newline : true,
				width : '453',
				type : "textarea"
			}]
		});
		jQuery.metadata.setType("attr", "validate");
		if("${defSrc}" == "03"){
			$("#catalogId").parent().parent().parent().hide();
			$("#catalogId").parent().find("input[type=text]").removeAttr("validate");
		}
		$("#templateRemark").css("resize","none");
		BIONE.validate(mainform);
		
	}
	
	function initData(){
		if(window.parent.catalogId && window.parent.catalogNm){
			$.ligerui.get("catalogId")._changeValue(window.parent.catalogId,window.parent.catalogNm);
		}
		if(templateId!=null && templateId!=""){
			$.ajax({
				cache : false,
				async : true,
				url : "${ctx}/report/frame/detailtmp/getTmpInfo?templateId="
					+ templateId+"&d="+new Date().getTime(),
				dataType : 'json',
				type : "post",
				beforeSend : function() {
					BIONE.loading = true;
					window.top.BIONE.showLoading("正在加载数据中...");
				},
				complete : function() {
					BIONE.loading = false;
					window.top.BIONE.hideLoading();
				},
				success : function(result) {
					templateType = result.templateType;
					$("#templateNm").val(result.templateNm);
					$("#templateSts").val(result.templateSts);
					$("#templateRemark").val(result.templateRemark);
					$.ligerui.get("catalogId")._changeValue(result.catalogId,result.catalogNm)
				},
				error : function(result, b) {
					//BIONE.tip('发现系统错误 <BR>错误码：' + result.status);
				}
			});
		}
	}
	function initButtons(){
		var buttons = [];
		buttons.push({
			text : '取消',
			onclick : cancleHandler
		});
		buttons.push({
			text : '保存',
			onclick : function(){
				f_save();
			}
		});
		BIONE.addFormButtons(buttons);
	}
	
	function f_save() {
		if($("#mainform").valid()){
			if(templateId == ""){
				if(templateType == "01"){
					var data = window.parent.prepareSaveInfo(templateId);
					data.templateId = "";
					data.templateNm = $("#templateNm").val();
					data.templateType = templateType;
					data.catalogId =  $("#catalogId").val();
					data.templateRemark =  $("#templateRemark").val();
					data.templateSts = "1";
					var url = "${ctx}/report/frame/datashow/detail/saveTmp";
					if("${defSrc}" == "03"){
						url = "${ctx}/report/frame/datashow/detail/storeTmp";
					}
					$.ajax({
						cache : false,
						async : true,
						url : url,
						dataType : 'json',
						data: data,
						type : "post",
						beforeSend : function() {
							BIONE.loading = true;
							BIONE.showLoading("正在保存数据中...");
						},
						complete : function() {
							BIONE.loading = false;
							BIONE.hideLoading();
						},
						success : function(result) {
							if(window.parent.detailTreeObj!=null){
								if(window.parent.$("#detailTab").find("#treeSearchInput").val() == ""){
									window.parent.BIONE.loadTree(
											"${ctx}/report/frame/datashow/detail/getDetailTree",
											window.parent.detailTreeObj, {
												searchNm : window.parent.$("#detailTab").find(
														"#treeSearchInput").val()
											}, null, false);
								}
								else{
									window.parent.BIONE.loadTree(
											"${ctx}/report/frame/datashow/detail/getDetailTree",
											window.parent.detailTreeObj, {
												searchNm : window.parent.$("#detailTab").find(
														"#treeSearchInput").val()
											});
								}
							}
							if(window.parent.closeDialog){
								window.parent.closeDialog();
							}
							else{
								if("${defSrc}" == "03"){
									BIONE.tip("收藏成功");
									if(window.parent.loadStoreTree){
	 									window.parent.loadStoreTree();
	 								}
								}
								else{
									BIONE.tip("保存成功");
								}
								BIONE.closeDialog("infoEdit");
							}
							
						},
						error : function(result, b) {
						}
					});
				}
				if(templateType == "02"){
					
					var data = window.parent.prepareSqlInfo(templateId);
					data.templateId = "";
					data.templateNm = $("#templateNm").val();
					data.templateType = templateType;
					data.catalogId =  $("#catalogId").val();;
					data.templateRemark =  $("#templateRemark").val();
					data.templateSts = "1";
					$.ajax({
						cache : false,
						async : true,
						url : "${ctx}/report/frame/datashow/detail/saveTmp",
						dataType : 'json',
						data: data,
						type : "post",
						beforeSend : function() {
							BIONE.loading = true;
							BIONE.showLoading("正在保存数据中...");
						},
						complete : function() {
							BIONE.loading = false;
							BIONE.hideLoading();
						},
						success : function(result) {
							if(window.parent.detailTreeObj!=null){
								if(window.parent.$("#detailTab").find("#treeSearchInput").val() == ""){
									window.parent.BIONE.loadTree(
											"${ctx}/report/frame/datashow/detail/getDetailTree",
											window.parent.detailTreeObj, {
												searchNm : window.parent.$("#detailTab").find(
														"#treeSearchInput").val()
											}, null, false);
								}
								else{
									window.parent.BIONE.loadTree(
											"${ctx}/report/frame/datashow/detail/getDetailTree",
											window.parent.detailTreeObj, {
												searchNm : window.parent.$("#detailTab").find(
														"#treeSearchInput").val()
											});
								}
							}
							if(window.parent.closeDialog){
								window.parent.closeDialog();
							}
							else{
								BIONE.tip("保存成功");
								BIONE.closeDialog("infoEdit");
							}
						},
						error : function(result, b) {
						}
					});
				}
			}
			else{
				var data = {};
				data.templateId = templateId;
				data.templateNm = $("#templateNm").val();
				data.templateType = templateType;
				data.catalogId =  $("#catalogId").val();;
				data.templateRemark =  $("#templateRemark").val();
				data.templateSts = $("#templateSts").val();
				$.ajax({
					cache : false,
					async : true,
					url : "${ctx}/report/frame/datashow/detail/saveTmp",
					dataType : 'json',
					data: data,
					type : "post",
					beforeSend : function() {
						BIONE.loading = true;
						BIONE.showLoading("正在保存数据中...");
					},
					complete : function() {
						BIONE.loading = false;
						BIONE.hideLoading();
					},
					success : function(result) {
						if(window.parent.tmpGrid){
							window.parent.tmpGrid.loadData();
						}
						BIONE.tip("保存成功");
						BIONE.closeDialog("infoEdit");
						
					},
					error : function(result, b) {
					}
				});
			}
		}
		
	}
	
	function cancleHandler() {
		BIONE.closeDialog("infoEdit");
	}
	
	
</script>
</head>
<body>
	<div id="template.center">
		<form id="mainform" action="${ctx}/report/frame/design/cfg/custom/uptRpt"  method="post"></form>
	</div>
</body>
</html>