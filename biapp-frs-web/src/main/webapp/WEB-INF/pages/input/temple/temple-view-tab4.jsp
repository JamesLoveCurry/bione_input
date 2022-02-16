<%@ page language="java" pageEncoding="UTF-8"%>
<%@ include file="/common/taglibs.jsp"%>
<%@ page
	import="org.apache.shiro.web.filter.authc.FormAuthenticationFilter"%>
<html>
<meta name="decorator" content="/template/template8_2.jsp">
<head>
<script type="text/javascript" src="${ctx}/js/udip/UDIP.js"></script>
<script type="text/javascript">
	var groupicon = "${ctx}/images/classics/icons/communication.gif";
	var grid, btns, url, ids = [], dialog, buttons = [];
	var chackType;
	var selection = [ {
		id : "",
		text : "请选择"
	} ];
	var manager;
	$(function() {
		url = "${ctx}/rpt/input/rule/list.json?templeId=" + "${id}";
		initGrid();
		initbutton();
		initButtons();

	});
	
	function onSelected() {
		var ruleNm = document.getElementById('ruleNm').value;
		var ruleType = document.getElementById('ruleType').value;
		if (ruleType == 1) {
			BIONE.commonOpenDialog(ruleNm,
					"dataRules", "575", "400",
					"${ctx}/rpt/input/rule/rule1?id=" + "${id}&d="+new Date().getTime() );
		} else if (ruleType == 2) {
			BIONE.commonOpenDialog(ruleNm,
					"dataRules", "550", "380",
					"${ctx}/rpt/input/rule/rule2?id=" + "${id}&d="+new Date().getTime() );
		} else if (ruleType == 3) {
			BIONE.commonOpenDialog(ruleNm,
					"dataRules", "1000", "450",
					"${ctx}/rpt/input/rule/rule3?id=" + "${id}&d="+new Date().getTime() );
		} else if (ruleType == 4) {
			BIONE.commonOpenDialog(ruleNm,
					"dataRules", "1000", "450",
					"${ctx}/rpt/input/rule/rule4?id=" + "${id}&d="+new Date().getTime() );
		} else if (ruleType == 5) {
			BIONE.commonOpenDialog(ruleNm,
					"dataRules", "1000", "450",
					"${ctx}/rpt/input/rule/rule5?id=" + "${id}&d="+new Date().getTime() );
		} else{
			BIONE.tip("请选择规则类型！");
		}
		
	}
	//根据ID查找该ID的补录模板
	function changerList() {
		var manager = $("#maingrid").ligerGetGridManager();
		var id = "${id}";
		manager.setOptions({
			parms : [ {
				name : 'id',
				value : id
			} ]
		});
		manager.loadData();
	}
	//搜索表单应用ligerui样式
	function searchForm() {
		$("#search").ligerForm({
			fields : [

			{
				name : "templeId",
				newline : true,
				type : "hidden"
			}, {
				display : "模板规则",
				name : "ruleNm",
				newline : true,
				type : 'select',
				options : {
					valueFieldID : 'ruleType',
					data : selection
				},
				attr : {
					field : 'temple.ruleType',
					op : "="
				}
			} ]
		}
		)
		
	}
	function initbutton() {
		
		if(!"${lookType}"){
			buttons.push({
				text : '关闭',
				onclick : cancleCallBack
			});
			buttons.push({
				text : '上一步',
				onclick : upset
			});
			
		}

		BIONE.addFormButtons(buttons);
	}
	function initGrid() {

		grid = manager = $("#maingrid").ligerGrid(
				{
					height : '300',
					width : '100%',
					columns : [
							{
								name : 'ruleId',
								hide : 1,
								width : "1%" 
							}, 
							{
								display : '规则名称',
								name : 'ruleNm',
								align : 'center',
								width : '26%',
								align : 'left',
								minWidth : '10%'
							},
							{
								display : "规则类型",
								name : "ruleType",
								width : '22%',
								minWidth : '10%',
								editor : {
									type : 'select',
									data : selection
								},
								render : function(row) {
									switch (row.ruleType) {
									case "1":
										return "数据值范围";
									case "2":
										return "正则表达式";
									case "3":
										return "表内横向校验";
									case "4":
										return "表内纵向校验";
									case "5":
										return "表间一致校验";
									}
								}
							},
							{
								display : '修改人',
								name : 'creator',
								align : 'center',
								width : '15%',
								minWidth : '10%'
							},
							{
								display : '修改时间',
								name : 'createDate',
								align : 'center',
								width : '19.5%',
								minWidth : '10%'
							} ],
					onSelectRow: function (rowdata, rowindex)
	                {
	                    $("#txtrowindex").val(rowindex);
	                },
					checkbox : true,
					usePager : false,
					isScroll : false,
					rownumbers : true,
					enabledEdit : false,
					clickToEdit : false,
					alternatingRow : true,//附加奇偶行效果行
					colDraggable : true,
					dataAction : 'server',//从后台获取数据
					method : 'post',
					sortName : 'ruleNm',//第一次默认排序的字段
					sortOrder : 'asc', //排序的方式
					url : url,
					toolbar : {}
				});

	}
	
	function initButtons() {

		btns = [ {
			text : '查看',
			click : beginlook,
			icon : 'view'
		}];
		
		BIONE.loadToolbar(grid, btns, function() {
		});
	}
	function beginlook(rowid) {
		chackType = achieveIds();
		if (ids.length == 1 && chackType == 1) {
			BIONE.commonOpenDialog("数值规则查看",
					"dataRules", "575", "400",
					"${ctx}/rpt/input/rule/rule1?id=" + "${id}" + (chackType == null ? "" : "&ruleId=" + ids[0] ) + "&lookType=lookType&d="+new Date().getTime());
		} else if(ids.length == 1 && chackType == 2){
			BIONE.commonOpenDialog("正则规则查看",
					"dataRules", "550", "380",
					"${ctx}/rpt/input/rule/rule2?id=" + "${id}" + (chackType == null ? "" : "&ruleId=" + ids[0] ) + "&lookType=lookType&d="+new Date().getTime());
		} else if(ids.length == 1 && chackType == 3){
			BIONE.commonOpenDialog("表内横向规则查看",
					"dataRules", "1000", "450",
					"${ctx}/rpt/input/rule/rule3?id=" + "${id}" + (chackType == null ? "" : "&ruleId=" + ids[0] ) + "&lookType=lookType&d="+new Date().getTime());
		} else if(ids.length == 1 && chackType == 4){
			BIONE.commonOpenDialog("表内纵向规则查看",
					"dataRules", "1000", "450",
					"${ctx}/rpt/input/rule/rule4?id=" + "${id}" + (chackType == null ? "" : "&ruleId=" + ids[0] ) + "&lookType=lookType&d="+new Date().getTime());
		} else if(ids.length == 1 && chackType == 5){
			BIONE.commonOpenDialog("表间一致规则查看",
					"dataRules", "1000", "450",
					"${ctx}/rpt/input/rule/rule5?id=" + "${id}" + (chackType == null ? "" : "&ruleId=" + ids[0] ) + "&lookType=lookType&d="+new Date().getTime());
		}else if(ids.length > 1){
			BIONE.tip('只能选择一条记录');
		}
		grid.reRender();
	}
	function upset() {
		parent.next('3', '${id}');
	}
	function cancleCallBack() {
		if(!"${lookType}"){
			parent.closeDsetBox();
		}else{
			BIONE.closeDialog("objDefManage");
		}
	}
	
	function closeWindow(){
		BIONE.closeDialog("objDefManage");
	}
	// 获取选中的行
	function achieveIds() {
		ids = [];
		var ruleType;
		var rows = grid.getSelectedRows();
		for ( var i in rows) {
			ids.push(rows[i].ruleId);
			ruleType = rows[i].ruleType;
		}
		return ruleType;
	}
	function getTempleUrl(){
		return "${ctx}/rpt/input/rule/list.json?templeId=" + "${id}";
	}
</script>
</head>
<body>

</body>
</html>