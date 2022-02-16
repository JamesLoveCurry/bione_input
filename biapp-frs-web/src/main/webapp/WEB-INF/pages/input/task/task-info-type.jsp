<%@ page language="java" pageEncoding="UTF-8"%>
<%@ include file="/common/taglibs.jsp"%>
<html>
<meta name="decorator" content="/template/template14.jsp">
<head>
<script type="text/javascript">
	var tskExeobjRelVO = window.parent.deployTask.tskExeobjRelVO;
	var isNeedCheck =  window.parent.isNeedCheck;
	var form;
	var selectedObjName,selectedObjId;
	var selectedType ="01";

	$(function() {
		window.parent.taskManage.taskTypeManage = window;
		initBaseInfo();
		jQuery.metadata.setType("attr", "validate");
		BIONE.validate($("#chooseForm"));
		if(isNeedCheck)
			gatherData();
	});
	function initBaseInfo() {
		//创建表单结构 
		form = $("#chooseForm")
				.ligerForm(
						{
							inputWidth : 170,
							labelWidth : 90,
							space : 40,
							validate : true,
							onBeforeSelectTabItem: function(){
								return false;
							},
							fields : [ {
								name : "selectedObjId",
								type : "hidden"
							}, {
								display : "补录类型",
								name : "exeObjType",
								newline : false,
								type : "select",
								comboboxName : "exeObjTypeSelect",
								options : {
									data : [ {
										id : '01',
										text : '指标补录'
									}, {
										id : '02',
										text : '明细补录'
									} ]
									,
									onSelected : function(value) {
										selectedType = value;
										$.ligerui.get("selectedObjCombBox").setValue("");
										$.ligerui.get("selectedObjCombBox").setText("");
								}},
								width : 215,
								validate : {
									required : true
								}
							}, {
								display : '补录对象',
								name : 'selectedObjId',
								comboboxName : "selectedObjCombBox",
								newline : true,
								type : 'select',
								validate : {
								    maxlength : 32
								},
								width : '500',
								validate : {
									required : true
								},
								options : {
									onBeforeOpen : showselectedObjDialog,
								    initText : selectedObjName,
								    initValue : selectedObjId,
								    textField : 'selectedObjName',
								    valueField : 'selectedObjId',
								    hideOnLoseFocus : true,
								    slide : false,
								    selectBoxHeight : 238,
								    selectBoxWidth : 400,
								    resize : false,
								    switchPageSizeApplyComboBox : false,
								    grid : {
									usePager : true, //服务器分页
									alternatingRow : true, //附加奇偶行效果行
									colDraggable : true,
									url : "${ctx}/bione/schedule/trigger/list.json",
									columns : [ {
									    name : 'selectedObjId',
									    hide : 1,
									    width : '0'
									}, {
									    display : '模板名称',
									    name : 'selectedObjName',
									    width : '242'
									}, {
									    display : '备注',
									    name : 'remark',
									    width : '300'
									} ],
									pageSize : 10,
									checkbox : false,
									switchPageSizeApplyComboBox : false
								    }
								}
							} ]
						});

		if(tskExeobjRelVO==null){

			form.setData({
				exeObjType:'01'
			});
			$.ligerui.get("exeObjTypeSelect")._changeValue('01',"指标补录");
			$.ligerui.get("exeObjTypeSelect").setDisabled(true);
		}
		else{
			form.setData({
				selectedObjId:tskExeobjRelVO.exeObjId,
				selectedObjNm:tskExeobjRelVO.exeObjNm,
				exeObjType:tskExeobjRelVO.exeObjType
			});
			var typeText = tskExeobjRelVO.exeObjType==null?"": tskExeobjRelVO.exeObjType=="01"?"指标补录":"明细补录";
			$.ligerui.get("exeObjTypeSelect")._changeValue(tskExeobjRelVO.exeObjType,typeText);
			$.ligerui.get("selectedObjCombBox").setValue(tskExeobjRelVO.exeObjId);
			$.ligerui.get("selectedObjCombBox").setText(tskExeobjRelVO.exeObjNm);
			$.ligerui.get("exeObjTypeSelect").setDisabled(true);
		}
		$("#selectedObjNm").attr("disabled", "disabled");
		var height = $(window).height()-70;
		$("#tab1").height(height);
	}

  	//弹出窗口中选择触发器
    function showselectedObjDialog(options) {
  		
  		
  		
		var options = {
			url : "${ctx}/rpt/input/task/selectObjBox?selectedType="+selectedType,
			dialogname : 'selectedObjBox',
			title : '选择模板',
			comboxName : 'selectedObjBox'
		};
		BIONE.commonOpenIconDialog(options.title, options.dialogname,
				options.url, options.comboxName);
		return false;
	}
	function gatherData(){
		if($("#chooseForm").valid()){
			window.parent.deployTask.tskExeobjRelVO = {
					exeObjType:$("#exeObjType").val(),
					exeObjId:$.ligerui.get("selectedObjCombBox").getValue(),
					exeObjNm:$.ligerui.get("selectedObjCombBox").getText()
			};
			window.parent.deployTask.rptTskInfo.exeObjType = $("#exeObjType").val();
			window.parent.deployTask.rptTskInfo.exeObjId = $.ligerui.get("selectedObjCombBox").getValue();
			window.parent.checkNode();
		}else{
			window.parent.isNeedCheck = false;
		}
	}
</script>

<title>指标目录管理</title>
</head>
<body>
	<div id="template.center">
		<form id="chooseForm"></form>
	</div>
</body>
</html>