<%@ page language="java" pageEncoding="UTF-8"%>
<%@ include file="/common/taglibs.jsp"%>
<html>
<head>
<meta name="decorator" content="/template/template5.jsp">
<script type="text/javascript" src="${ctx}/js/rptmgr/UUID.js"></script>
<script type="text/javascript">
	var groupicon = "${ctx}/images/classics/icons/communication.gif";
	var mainform;
	var grid;
	var serverId;
	var agentId = "";
	var agentStatus = "";
	var rptItemInfo={
			id:{}
	};
	var idxs=[];
	$(function() {
		initForm();
		initGrid();
		jQuery.metadata.setType("attr", "validate");
		BIONE.validate($("#mainform"));
	
		var buttons = [];
		buttons.push({
			text : '取消',
			onclick : function() {
				BIONE.closeDialog("editRptItem");
			}
		});
		if(parent.selectedTab.show==""){
			buttons.push({
				text : '保存',
				onclick : f_save
			});
		}
		
		BIONE.addFormButtons(buttons);
		initData();

	});
	function setData(idxInfos){
		var Row={
			Rows: idxInfos
		};
		grid.set("data",Row);
	}
	function initForm() {
		mainform = $("#mainform")
				.ligerForm(
						{
							labelWidth : 80,
							fields : [
									
									{
										group : "数据项属性",
										groupicon : groupicon,
										display : '数据项名称',
										name : 'rptItemNm',
										width : 200,
										space: 140,
										newline : true,
										type : 'text',
										validate : {
											required : true
										}
									},{
										display : "序号",
										name : 'orderNo',
										newline : false,
										width : 200,
										type : 'text',
										validate : {
											digits  : true
										}
									},{
										display: '关联类型',
										name: 'relateType',
										newline : true,
										width : 200,
										comboboxName: 'relateTypeBox',
										type: "select",
										options:{
											data:[{
												id: '01',
												text: '金额'
											},{
												id: '02',
												text: '客户'
											},{
												id: '03',
												text: '百分比'
											}]
										}
									},
									{
										name : "rptId",
										type : 'hidden'
									},{
										name : "rptItemId",
										type : 'hidden'
									},{
										display : '关联指标',
										name : 'idxs',
										newline : true,
										width : '650'
									}, {
										display : '数据项说明',
										name : 'rptItemDesc',
										newline : true,
										type : 'textarea',
										width : 625,
										validate : {
											maxlength : 250
										},
										attr : {
											style : "resize:none; height:50px"
										}
									} ]
						});

		jQuery.metadata.setType("attr", "validate");
		BIONE.validate($("#mainform"));
		var tempLi = $("#idxs").parent().parent();
		var tipContent = [];
		tipContent
				.push('<li style="width: 100%"><div id="maingrid" name="agentGrid" style="width: 100%;"></div></li>');
		tempLi.html(tipContent.join(''));
		
		
		
	}
	function initData(){
		if("${rptItemId}"!=""){
			rptItemInfo=parent.selectedTab.getDataItemInfo("${rptItemId}");
			if(rptItemInfo!=null){
				$("#rptId").val(rptItemInfo.id.rptId);
				$("#rptItemId").val(rptItemInfo.id.rptItemId);
				$("#rptItemNm").val(rptItemInfo.rptItemNm);
				$("#orderNo").val(rptItemInfo.orderNo);
				$("#rptItemDesc").val(rptItemInfo.rptItemDesc);
				$.ligerui.get("relateTypeBox").selectValue(rptItemInfo.relateType);
				idxs=rptItemInfo.idxs;
				grid.set("data",{
					Rows: rptItemInfo.idxs
				});
			}
		}
	}
	function initOrderNo(){
		var orderNo=parent.selectedTab.getOrder();
		if(orderNo==null){
			$("#orderNo").val("0");
		}
		else{
			$("#orderNo").val(orderNo+1);
		}
	}

	function f_save() {
		grid.endEdit();
		if($("#mainform").valid()){
			rptItemInfo.id.rptItemId=$("#rptItemId").val()||new UUID().uuid2();
			rptItemInfo.rptItemType="02";
			rptItemInfo.rptItemNm=$("#rptItemNm").val();
			rptItemInfo.relateType=$("#relateType").val();
			rptItemInfo.orderNo=$("#orderNo").val();
			rptItemInfo.rptItemDesc=$("#rptItemDesc").val();
			var idxData=grid.getData();
			var idxInfo=[];
			for(var i in idxData){
				idxInfo.push({
					id:{
						rptId: $("#rptId").val(),
						rptItemId: $("#rptItemId").val(),
						indexNo: idxData[i].id.indexNo
					},
					indexNm: idxData[i].indexNm,
					filterFormula: idxData[i].filterFormula
				});
			}
			rptItemInfo.idxs=idxInfo;
			if("${rptItemId}"!=""){
				parent.selectedTab.grid.updateRow(parent.selectedTab.getRowIndex("${rptItemId}"),rptItemInfo);
			}
			else{
				parent.selectedTab.grid.addRow(rptItemInfo);
				
			}
			BIONE.closeDialog("editRptItem");
		}
		
		
	}
	
	function initGrid() {
		var g = {
			InWindow : false,
			width : "95%",
			height : "180",
			columns : [ {
				display : "指标编号",
				name : "id.indexNo",
				width : "25%"
			}, {
				display : "指标名称",
				name : "indexNm",
				width : "25%"
			}, {
				display : "过滤公式",
				name : "filterFormula",
				width : "40%",
				editor:{
					type: 'text'
				}
			} ],
			enabledEdit: true,
			clickToEdit : true,
			checkbox : false,
			usePager : false,
			rownumbers : true,
			data: null,
			alternatingRow : true,//附加奇偶行效果行
			colDraggable : true,
			onBeforeEdit: function(){
				if(parent.selectedTab.show!=""){
					return false;
				}
			},
			toolbar:{}

		};
		var btns = [ {
			text : '新增指标',
			click : idxAdd,
			icon : 'add'
		},  {
			text : '删除指标',
			click : idxDelete,
			icon : 'delete'
		} ];
		grid = $("#maingrid").ligerGrid(g);
		if(parent.selectedTab.parent.parent.show!=""){
			$("#rptItemNm").css("color", "#333").attr("readOnly", "true")
			.removeAttr("validate");
			$("#orderNo").css("color", "#333").attr("readOnly", "true")
			.removeAttr("validate");
			$.ligerui.get("relateTypeBox").setDisabled();
			$("#relateTypeBox").css("color", "#333").attr("readOnly", "true")
					.removeAttr("validate");
			$("#rptItemDesc").css("color", "#333").attr("readOnly", "true")
			.removeAttr("validate");
			$(".l-panel-topbar").hide();
		}
		else{
			BIONE.loadToolbar(grid, btns, function() {
			});
		}
		function idxAdd() {
			window.parent.selectedDataItem=window;
			window.parent.BIONE.commonOpenDialog("新增指标", "editRptIdx",  300, 420,  "${ctx}/rpt/frame/rptmgr/info/editIdxInfo?rptItemId=${rptItemId}");
		}
		
		function idxDelete() {
			grid.deleteSelectedRow();
		}
	}
</script>
</head>
<body>
	<div id="template.center">
		<form name="mainform" method="post" id="mainform"
			action=""></form>

	</div>
</body>
</html>