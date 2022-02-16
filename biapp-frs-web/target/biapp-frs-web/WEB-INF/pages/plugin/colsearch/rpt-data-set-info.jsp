<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<%@ include file="/common/taglibs.jsp"%>
<html>
<head>
<meta name="decorator" content="/template/template14.jsp">
<script type="text/javascript">
    var f_save;
	var mainform;

	$(function() {
		var parent = window.parent;
		var datasetObj = parent.datasetObj;
		var datasetId = datasetObj.datasetId;
		var groupicon = "${ctx}/images/classics/icons/communication.gif";

		//渲染表单
		mainform = $("#mainform");
		mainform
				.ligerForm({
					inputWidth : 160,
					labelWidth : 80,
					space : 30,
					fields : [
							{
								name : "setId",
								type : "hidden"
							},
							{
								name : "catalogId",
								type : "hidden"
							},
							{
								group : "数据集管理",
								groupicon : groupicon,
								display : "所属目录",
								name : "catalogName",
								newline : true,
								type : "select",
								width : 215,
								comboboxName: "catalog_box",
								options : {
									readonly : true,
									onBeforeOpen : function() {
										var url = "${ctx}/rpt/frame/dataset/catalogTree?catalogId="
												+ datasetObj.catalogId;
										dialog = BIONE
												.commonOpenDialog('维度目录',
														"datasetCatalogSlct", 400,300, url);
										return false;
									}
								}
							},
							{
								display : "数据集名称",
								name : "setNm",
								newline : false,
								type : "text",
								width : 215,
								options : {
									readonly : true
								}
							},
							{
								display : "数据集类型",
								name : "setType",
								newline : true,
								type : "select",
								comboboxName:"set_type_box",
								width : 215,
								options : {
									readonly : true,
									url : "${ctx}/rpt/frame/dataset/setTypeList.json"
							}
							},
							{
								display : "数据源",
								name : "sourceId",
								newline : false,
								type : "select",
								comboboxName:"ds_box",
								width : 215,
								options : {
									readonly : true,
									url : "${ctx}/rpt/frame/dataset/dsList.json",
									onBeforeSelect : function() {
										$("#mainform input[name='table_box']").val("");
									}
								}
							},{
								display : "数据库表",
								name : "table",
								newline : true,
								type : "select",
								width : 540,
								comboboxName: "table_box",
								options : {
									readonly : true,
									onBeforeOpen : function() {
										var dsId = $(
												"#mainform input[name='sourceId']")
												.val();
										if (!dsId) {
											BIONE.tip("请选择一个数据源。");
											return false;
										}
										var url = "${ctx}/rpt/frame/dataset/tablePage?dsId="+dsId+"&datasetId="+datasetId;
										dialog = window.parent.BIONE
												.commonOpenDialog('请选择物理表',
														"selectGrid", "700",
														"350", url);
										return false;
									}
								}
							}, {
								display : "描述",
								name : "remark",
								newline : true,
								type : "textarea",
								width : 543
							} ]

				});
		$("#remark").css("resize","none");
		
		$("#remark").attr("readonly", "true");
		jQuery.metadata.setType("attr", "validate");
		BIONE.validate("#mainform");
		

		//表单赋值
		$("#mainform input[name='catalogId']").val(datasetObj.catalogId);
		$.ligerui.get('catalog_box').setText(datasetObj.catalogName);

		//修改时初始化表单
		if (datasetId != "" && datasetId != null) {
			$.ajax({
				type : "POST",
				url : "${ctx}/rpt/frame/dataset/datasetInfo.json?datasetId="
						+ datasetId,
				success : function(model) {
					datasetObj.setType =  model.setType;
					datasetObj.dsId = model.sourceId;
					datasetObj.table = model.tableEnNm==null?"":model.tableEnNm;
					$.ligerui.get('ds_box').selectValue(model.sourceId);
					$.ligerui.get('set_type_box').selectValue(model.setType);
					$("#mainform input[name='setNm']").val(model.setNm);
					$("#mainform textarea[name='remark']").val(model.remark);
// 					$("#mainform input[name='table_box']").set(model.tableEnNm);
                     $.ligerui.get('table_box').selectValue(model.tableEnNm);
					$.ligerui.get('table_box').setText(model.tableEnNm);
					parent.isLoaded=true;
				}
			});
		}
		
		//保存
		f_save = function() {
			if ($("#mainform").valid()) {
				parent.canSelect=true;
				var dsId = $("#mainform input[name='sourceId']").val();
				var table = $("#mainform input[name='table_box']").val();
				var setType=  $("#mainform input[name='setType']").val();
				
				//决定是否刷新
				if (datasetObj.dsId != dsId|| datasetObj.table != table||datasetObj.setType !=setType) {
					datasetObj.refresh = true;
				} else {
					datasetObj.refresh = false;
				}
				//决定数据来源
				if (!datasetObj.datasetId) {
					//新增
					datasetObj.from ="table";
				} else {
					//修改
					if (datasetObj.firstEdit && datasetObj.table == table) {
						datasetObj.from = "dataset";
					} else {
						firstEdit = false;
						datasetObj.from = "table";
					}
				}
				//数据集基本信息缓存
				datasetObj.dsId = dsId;
				datasetObj.table = table;
				datasetObj.datasetName = $(
						"#mainform input[name='setNm']").val();
				datasetObj.remark = $("#mainform textarea[name='remark']")
						.val();
				datasetObj.setType =  setType;
				parent.next();
			} else {
				window.parent.BIONE.showInvalid(BIONE.validator);
				parent.canSelect=false;
			}
		}

		if (!(datasetId != "" && datasetId != null)) {
		   parent.isLoaded=true;
		}
	});
</script>
<title>Insert title here</title>
</head>
<body style="width:80%">
	<div id="template.center">
		<form id="mainform"
			action="${ctx}/dataquality/rulemanage/meta-rule-grp/saveRuleGrpType"
			method="POST"></form>
	</div>
</body>
</html>