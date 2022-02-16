<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<%@ include file="/common/taglibs.jsp"%>
<html>
<head>
<meta name="decorator" content="/template/template1_BS.jsp">
<script type="text/javascript">
	var grid;
	
	$(function(){
		searchForm();
		initGrid();
		initButtons();
		BIONE.addSearchButtons("#search", grid, "#searchbtn");
	});

	function searchForm() {
		$("#search").ligerForm({
			fields : [ {
				display : "元素名称",
				name : "elementNm",
				newline : false,
				type : "text",
				cssClass : "field",
				attr : {
					op : "like",
					field : "elementNm"
				}
			},{
				display : "上级元素",
				name : "upElementId",
				newline : false,
				cssClass : "field",
				type : "select",
	    		comboboxName : "upElementCombBox",
	    		options : {
					onBeforeOpen : selectElementDialog,
					selectBoxHeight : '150'
				},
				attr : {
					op : "=",
					field : "upElementId"
				}
			},{
				display : "所属报表",
				name : "belongRptNum",
				newline : true,
				type : "text",
				cssClass : "field",
				attr : {
					op : "like",
					field : "belongRptNum"
				}
			},{
				display : '报送周期',
	    		name : 'submitCycle',
	    		newline : false,
	    		type : "select",
	    		comboboxName : "submitCycleBox",
	    		options:{
					data:[{ text:"月报", id : "M" },{ text:"季报", id : "Q" },{ text:"半年报", id : "H" },{ text:"年报", id : "A" }]
				},
				cssClass : "field",
				attr : {
					op : "=",
					field : "submitCycle"
				}
			}]
		});

	}

	//初始化Grid
	function initGrid() {
		grid = $("#maingrid").ligerGrid({
			InWindow : false,
			height : '99%',
			width : '100%',
			columns : [{
				display : '元素名称',
				name : 'elementNm',
				width : '10%',
				align: 'left'
			},  {
				display : '上级元素',
				name : 'upElementNm',
				width : '10%',
				align: 'left'
			}, {
				display : '元素顺序',
				name : 'orderId',
				width : '8%',
				align: 'left'
			}, {
				display : '元素属性',
				name : 'elementAttr',
				width : '10%',
				align: 'left'
			}, {
				display : '是否分组',
				name : 'isGroup',
				width : '8%',
				align: 'left',
				render: isGroupRender
			}, {
				display : '报送周期',
				name : 'submitCycle',
				width : '8%',
				align: 'left',
				render: submitCycleRender
			}, {
				display : '所属报表',
				name : 'belongRptNum',
				width : '8%',
				align: 'left'
			}, {
				display : '数据来源',
				name : 'elementSrc',
				width : '8%',
				align: 'left'
			}, {
				display : '缺省值',
				name : 'defaultValue',
				width : '8%',
				align: 'left'
			}, {
				display : '开始日期',
				name : 'startDate',
				width : '8%',
				align: 'left'
			}, {
				display : '结束日期',
				name : 'endDate',
				width : '8%',
				align: 'left'
			}, {
				display : '备注',
				name : 'remark',
				width : '10%',
				align: 'left'
			}],
			checkbox : true,
			usePager : true,
			isScroll : true,
			rownumbers : true,
			alternatingRow : true,//附加奇偶行效果行
			colDraggable : true,
			dataAction : 'server',//从后台获取数据
			method : 'post',
			url : '${ctx}/frs/xmlMessage/xmlMessageList',
			sortName : 'endDate', //第一次默认排序的字段
			sortOrder : 'asc',
			toolbar : {}
		});
	}
	//转码
	function submitCycleRender(rowdata) {
		var rowDataStr = rowdata.submitCycle;
		if(rowDataStr == "M"){
			rowDataStr = "月报";
		}else if(rowDataStr == "Q"){
			rowDataStr = "季报";
		}else if(rowDataStr == "H"){
			rowDataStr = "半年报";
		}else if(rowDataStr == "A"){
			rowDataStr = "年报";
		}
		return rowDataStr;
	}
	//转码
	function isGroupRender(rowdata) {
		var rowDataStr = rowdata.isGroup;
		if(rowDataStr == "Y"){
			rowDataStr = "是";
		}else if(rowDataStr == "N"){
			rowDataStr = "否";
		}
		return rowDataStr;
	}

	function initButtons() {
		var btns = [ {
			text : '新增',
			click : oper_add,
			icon : 'fa-plus',
			operNo : 'oper_add'
		}, {
			text : '修改',
			click : oper_modify,
			icon : 'fa-pencil-square-o',
			operNo : 'oper_modify'
		}, {
			text : '删除',
			click : oper_delete,
			icon : 'fa-trash-o',
			operNo : 'oper_delete'
		}, {
			text : '生成报文',
			click : create_xml,
			icon : 'fa-pencil-square-o',
			operNo : 'oper_modify'
		}, {
			text : '预览',
			click : show_xml,
			icon : 'fa-street-view',
			operNo : 'oper_modify'
		} ];
		BIONE.loadToolbar(grid, btns, function() {});
	}
	//生成报文
	function create_xml() {
		BIONE.commonOpenDialog('生成报文', 'createXmlWin', '350', '350','${ctx}/frs/xmlMessage/createXmlWin');
	}
	var height = $(window).height() - 130;
	var width = $(window).width() - 80;
	//预览
	function show_xml(){
		BIONE.commonOpenDialog('报文预览', 'showXmlWin',width,height,'${ctx}/frs/xmlMessage/showXmlWin');
	} 
	
	//新增属性
	function oper_add() {
		BIONE.commonOpenDialog('新增配置', 'xmlMessageEidtWin',width,height,'${ctx}/frs/xmlMessage/xmlMessageEidt');
	}
	//修改属性
	function oper_modify() {
		var rows = grid.getSelectedRows();
		if (rows.length != 1) {
			BIONE.tip('请选择一条记录!');
			return;
		}
		BIONE.commonOpenDialog("修改配置", "xmlMessageEidtWin",width,height,"${ctx}/frs/xmlMessage/xmlMessageEidt?id="+rows[0].elementId);
	}
	//批量删除
	function oper_delete() {
		var ids = achieveIds();
		if(ids.length > 0){
			var idsStr = ids.join(',');
			$.ligerDialog.confirm('您确定删除这' + ids.length + "条记录吗？", function(yes) {
				if (yes) {
					$.ajax({
						async : false,
						type : "post",
						url : '${ctx}/frs/xmlMessage/deleteXmlElement?&ids=' + idsStr,
						success : function(res){
							grid.loadData();
							BIONE.tip('删除成功');
						},
						error : function(e){
							BIONE.tip('删除失败');
						}
					});
				}
			});	
		} else {
			BIONE.tip('请选择记录');
		}
	}
	//获取选中行的主键
	function achieveIds() {
		//过滤版本
		var ids = [];
		var verId = '';
		var rows = grid.getSelectedRows();
		for ( var i in rows) {
			ids.push(rows[i].elementId)
		}
		return ids;
	}
	
	//弹出窗口
    function selectElementDialog(options) {
		$.ligerDialog.open({
			name:'upElementWin',
			title : '选择上级元素',
			width : width,
			height : height,
			url : '${ctx}/frs/xmlMessage/selectUpElementWin',
			buttons : [{
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
			$("#upElementId").val(data.elementId);
			$("#upElementCombBox").val(data.elementNm);
		}
		dialog.close();
	}
	//取消按钮调用方法
	function f_selectCancel(a,dialog){
		dialog.close();
	}
</script>
</head>
<body>
</body>
</html>