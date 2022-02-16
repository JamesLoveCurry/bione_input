<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ include file="/common/taglibs.jsp"%>
<html>
<meta name="decorator" content="/template/template5.jsp">
<head>
<script type="text/javascript">
	jQuery.extend(jQuery.validator.messages, {
		greaterThan : "结束时间应当大于开始日期"
	});
	
	parent.parent.parent.window.child = window;
	var groupicon = "${ctx}/images/classics/icons/communication.gif";
	var verId = window.parent.verId;
	var designInfo = null;
	var grid=null;
	var mainform;
	var rptCode_sel_val;//报表编号选择项，供统一配置使用
	var rangeData;
	function initRangeData(){
		$.ajax({
			cache : false,
			async : true,
			url : "${ctx}/frs/pbmessage/getRangeData",
			dataType : 'json',
			type : "GET",
			beforeSend : function() {
			},
			complete: function(){
			},
			success : function(result) {
				rangeData=result;
			}
		});
	}
	$(function() {
		initRangeData();
		mainform = $("#mainform");
		mainform.ligerForm({
			fields : [{
				display : "报表编码",
				name : "rptCode_Name",
				newline : true,
				type : "select",
				group : "上报信息",
				width: '200',
				groupicon : groupicon,
				comboboxName : "rptCode_sel",
				validate : {
					required : true
				},
				options : {
					onBeforeOpen : selectRptDialog,
					selectBoxHeight : '150'
				}
			},{ 
				display : "版本", 
				name : "verId", 
				newline : false, 
				width: '200',
				comboboxName: 'verIdBox',
				type : "select",
				options : {
					onSelected: function(id,text){
						if(id!=null&&id!=''&&text!=null&&text!=''){
							var info=id.split("-");
							var infos=text.split("(")[1].split("-");
							var startDate=infos[0];
							var startDate=startDate.substring(0,4)+"-"+startDate.substring(4,6)+"-"+startDate.substring(6,8);
							var endDate=infos[1].substring(0,infos[1].length-1);
							var endDate=endDate.substring(0,4)+"-"+endDate.substring(4,6)+"-"+endDate.substring(6,8);
							$.ligerui.get("startTime").setValue(startDate);
							$.ligerui.get("endTime").setValue(endDate);
							$.ajax({
								cache : false,
								async : true,
								url : "${ctx}/frs/pbmessage/getRptIdxInfo",
								dataType : 'json',
								type : "GET",
								beforeSend : function() {
									BIONE.showLoading();
								},
								data : {
									verId: info[1],
									templateId: info[0]
								},
								complete: function(){
									BIONE.hideLoading();
								},
								success : function(result) {
									var rows=result.Rows;
									for(var i in rows){
										rows[i].dataAttr="1";
										rows[i].currtype="CNY0001";
										rows[i].isMain="N";
										rows[i].pbcCode=rows[i].busiNo;
									}
									grid.set("data",result);
								}
							});
						}
					}
				},
				validate : {
 					required : true
 				}
			}, {
				display : "人行编码",
				name : "pbcCode",
				newline : true,
				width : '800'
			},
 			{
 				display : '开始时间',
 				name : 'startTime',
 				newline : true,
 				width : '200',
 				validate : {
 					required : true
 				},
 				type : 'date'
			}, {
 				display : '结束时间',
 				name : 'endTime',
				newline : false,
				type : 'date',
				width : '200',
				validate : {
 					required : true,
 					greaterThan : "startTime"
 				}
 			},
 			{
				name : "reportCodeCfgNo",
				type : "hidden"
			}]
		});
		jQuery.metadata.setType("attr", "validate");
		BIONE.validate(mainform);
		var buttons = [];
		buttons.push({
			text : '取消',
			onclick : cancle
		});
		buttons.push({
			text : '保存',
			onclick : save
		});
		BIONE.addFormButtons(buttons);
		var tempLi = $("#pbcCode").parent().parent();
		var tipContent = [];
		tipContent
				.push('<li style="width: 100%"><div id="maingrid" name="maingrid" style="width: 100%;"></div></li>');
		tempLi.html(tipContent.join(''));
		initGrid();
	});
	
	function initGrid(){
		grid = $("#maingrid").ligerGrid({
			height : '70%',
			width : '99%',
			columns : [{
				display : '单元格编号',
				name : 'cellNo',
				width : "10%"
			}, {
				display : '单元格名称',
				name : 'cellNm',
				width : "15%"
			}, {
				display : '人行编码',
				name : 'pbcCode',
				width : "10%",
				editor:{
					type: "text"
				}
			} , {
				display : '数据属性',
				name : 'dataAttr',
				width : "10%",
				editor: {
					type:"select",
					data:[{
						text : '余额',
						id : "1"
					},{
						text : '发生额',
						id : "2"	
					}]
				},
				render: function(a,b,c){
					switch(c){
					case "1":
						return '余额';
					case "2":
						return '发生额';
					}
				}
			} ,{
				display : '币种',
				name : 'currtype',
				width : "15%",
				editor: {
					type:"select",
					data:[{
						text : '人民币(CNY0001)',
						id : "CNY0001"
					},{
						text : '美元(USD0002)',
						id : "USD0002"	
					},{
						text : '本外币(BWB0001)',
						id : "BWB0001"	
					}]
				},
				render: function(a,b,c){
					switch(c){
					case "CNY0001":
						return '人民币(CNY0001)';
					case "USD0002":
						return '美元(USD0002)';
					case "BWB0001":
						return '本外币(BWB0001)';
					}
				}
			} ,{
				display : '是否只报送法人',
				name : 'isMain',
				width : "15%",
				editor: {
					type:"select",
					data:[{
						text : '是',
						id : "Y"
					},{
						text : '否',
						id : "N"	
					}]
				},
				render: function(a,b,c){
					switch(c){
					case "Y":
						return '是';
					case "N":
						return '否';
					}
				}
			}, {
				display : '报送范围',
				name : 'dtrctNo',
				width : '10%',
				render: function(a,b,c){
					if(rangeData[c]){
						return rangeData[c];
					}else{
						return c;
					}
				}
			}/**, {
				display : '上报机构',
				width : '8%',
				render : function(row){
					return "<a href='javascript:void(0)'  onclick='open_org_grid(\""+row.dtrctNo+"\")' >编辑</a>";
				}
			}**/ ],
			data : null,
			enabledEdit : true,
			clickToEdit : true,
			alternatingRow : false,
			usePager: false,
			toolbar : {}
		});
		var btns = [ {
			text : '预览模板', click : f_view, icon : 'view'
		},{
			text : '批量选择', click : f_batchClick, icon : 'modify'
		},{
			text : '配置信息', click : f_config, icon : 'config'
		},{
			text : '检测编码', click : f_check, icon : 'modify'
		},{
			text : '删除', click : f_delete, icon : 'delete'
		}];
		
		BIONE.loadToolbar(grid, btns, function() { });
	}
	
	//批量选择
    function f_batchClick(options) {
    	var rptCode =  $("#rptCode_sel").val();
		var verId = $.ligerui.get("verIdBox").getValue().split("-")[1];
		if(rptCode&&verId){
			var width = 600;
			var height = 320;
			$.ligerDialog.open({
				name:'batchClickWin',
				title : '批量选择',
				width : width,
				height : height,
				url : '${ctx}/frs/pbmessage/selectBatchClickDialog',
				buttons : [ {
					text : '确定',
					onclick : f_selectBatchClickOK
				}, {
					text : '取消',
					onclick : f_selectCancel
				} ]
			});
			return false;
		}else{
			BIONE.tip("请选择报表编码和版本号!");
		}
	}
  
	//保存按钮调用方法
	function f_selectBatchClickOK(a,dialog){
		var data = dialog.frame.addToParent();
		if(data!=""){
			var rows = grid.rows;
			for(var i in rows){
				var cellNo = rows[i].cellNo;
				if(cellNo.indexOf(data) > -1){
					grid.select(rows[i]);
				}else{
					grid.unselect(rows[i]);
				}
			}
		}
		dialog.close();
	}
	
	//检测人行编码
	function f_check(){
		var rptCode =  $("#rptCode_sel").val();
		var verId = $.ligerui.get("verIdBox").getValue().split("-")[1];
		if(rptCode&&verId){
			var rows = grid.getSelectedRows();
			if(rows.length > 0){
				var countNum = 0;
				var delRows = [];
				for(var i in rows){
					if(!rows[i].pbcCode){
						delRows.push(rows[i]);
						countNum++;
					}
				}
				if(countNum > 0){
					$.ligerDialog.confirm("检测出"+countNum + "条记录[人行编码]为空！<br/><br/>是否删除？", function(yes) {
						if (yes) {
							grid.deleteRange(delRows);
						}
					});	
				}else{
					BIONE.tip("检测通过！");
				}
			}else{
				BIONE.tip("请选择需要检测的记录！");
			}
			
		}else{
			BIONE.tip("请选择报表编码和版本号!");
		}
	}
	
  	//弹出窗口
    function selectRptDialog(options) {
    	var height = $(window).height() - 50;
		var width = $(window).width() - 80;
		$.ligerDialog.open({
			name:'addRptWin',
			title : '报表选择',
			width : width,
			height : height,
			url : '${ctx}/frs/pbmessage/selectRptDialog',
			buttons : [ {
				text : '确定',
				onclick : f_selectOK
			}, {
				text : '取消',
				onclick : f_selectCancel
			} ]
		});
		return false;
	}
	
	//保存按钮调用方法
	function f_selectOK(a,dialog){
		var data = dialog.frame.addToParent();
		if(data!=""){
			$("#mainform input[name='rptCode_sel']").val(data.rptNum);
			$("#mainform input[name='rptCode_name']").val(data.rptNum);
		}
		dialog.close();
		var id = data.rptNum;
		if(id!=null&&id!=""){
			$.ajax({
				cache : false,
				async : true,
				url : "${ctx}/frs/pbmessage/getVerId",
				dataType : 'json',
				type : "post",
				data : {
					rptCode: id
				},
				success : function(result) {
					$.ligerui.get("verIdBox").selectValue("");
					$.ligerui.get("verIdBox").setData(result);
				}
			});
			if(grid!=null){
				grid.set("data",{Rows:[]});
			}
		}
	}
	//取消按钮调用方法
	function f_selectCancel(a,dialog){
		dialog.close();
	}
  	
	//上报地区
	function open_org_grid(dtrctNo){
		var height = $(window).height() - 30;
		var width = $(window).width() - 60;
		if(dtrctNo == ""){
			BIONE.tip('请先配置上报范围在进行上报机构编辑！');
			return;
		}
		BIONE.commonOpenDialog("上报地区","orgConfig",width,height,"${ctx}/frs/pbmessage/orgGrid?submitRangeCode="+dtrctNo+"&verId="+verId+"&isView=0",null);
	}
	
	function f_view(){
		var rptCode =  $("#rptCode_sel").val();
		var verId = $.ligerui.get("verIdBox").getValue().split("-")[1];
		var tmpId = $.ligerui.get("verIdBox").getValue().split("-")[0];
		var height = $(parent.parent.parent.window).height() - 30;
		var width = $(parent.parent.parent.window).width();
		if(rptCode&&tmpId&&verId){
			parent.parent.parent.window.BIONE
			.commonOpenDialog("系统指标", "taskIdxWin", width, height,
					"${ctx}/frs/pbmessage/taskIdxWin?tmpId="+tmpId+"&verId="+verId, null);
		}else{
			BIONE.tip("请选择报表编码和版本号");
		}
	}
	
	function f_config(){
		window.rows = grid.getSelectedRows();
		if(rows.length<=0){
			BIONE.tip("请至少选择一条记录");
			return;
		}
		verId = $.ligerui.get("verIdBox").getValue().split("-")[1];
		rptCode_sel_val = $.ligerui.get("rptCode_sel").getValue();
		var height = $(window).height() - 30;
		var width = $(window).width() - 60;
		BIONE.commonOpenDialog("统一配置","codeConfig",width,height,"${ctx}/frs/pbmessage/codeConfig",null);
	}
	
	function f_delete(){
		var rows = grid.getSelectedRows();
		if(rows.length<=0){
			BIONE.tip("请至少选择一条记录");
			return;
		}
		$.ligerDialog.confirm('您确定删除这' + rows.length + "条记录吗？", function(yes) {
			if (yes) {
				grid.deleteSelectedRow();
			}
		});	
	}
	//保存方法
	function save() {
		verId = $.ligerui.get("verIdBox").getValue().split("-")[1];
		if ($("#mainform").valid()) {
			var rows=grid.getData();
			var configs=[];
			if(rows.length<=0){
				BIONE.tip("未配置人行编码！");
				return;
			}
			for(var i in rows){
				var config={
					indexNo: rows[i].id.indexNo,
					pbcCode: rows[i].pbcCode,
					currType: rows[i].currtype,
					dataAttr: rows[i].dataAttr,
					isMain: rows[i].isMain,
					dtrctNo: rows[i].dtrctNo,
					cellNo: rows[i].cellNo,
					cellNm: rows[i].cellNm
				};
				if(rows[i].id.indexNo=="" || rows[i].id.indexNo==null){
					BIONE.tip("报表内部关联指标信息有误，无法保存！");
					return;
				}
				if(rows[i].pbcCode=="" || rows[i].pbcCode==null || rows[i].dtrctNo=="" || rows[i].dtrctNo==null){
					BIONE.tip("存在未配置人行编码和报送范围的记录，无法保存！");
					return;
				}
				configs.push(config);
			}
			var data={
					reportCodeCfgNo: $("#reportCodeCfgNo").val(),
					rptCode : $("#rptCode_sel").val(),
					startDt : $("#startTime").val(),
					endDt : $("#endTime").val(),
					verId:verId,
					configs: configs,
			};
			$.ajax({
				cache : false,
				async : true,
				url : "${ctx}/frs/pbmessage/codeJudge",
				dataType : 'json',
				type : "post",
				data : {
					info: JSON2.stringify(data),
					reportCodeCfgNo: $("#reportCodeCfgNo").val(),
					rptCode : $("#rptCode_sel").val(),
					startDt : $("#startTime").val(),
					endDt : $("#endTime").val(),
					verId:verId
				},
				success : function(result) {
					BIONE.closeDialogAndReloadParent("addCode", "maingrid", "保存成功");
				},
				error : function(result, b) {
					BIONE.tip('发现系统错误 <BR>错误码：' + result.status);
				}
			});
		}
	}
	//取消方法
	function cancle() {
		BIONE.closeDialog("addCode");
	}
</script>
</head>
<body>
	<div id="template.center">
		<form id="mainform" action="" method="post"></form>
	</div>
</body>
</html>