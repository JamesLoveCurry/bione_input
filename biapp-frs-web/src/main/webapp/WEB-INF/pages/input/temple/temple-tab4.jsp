<%@ page language="java" pageEncoding="UTF-8"%>
<%@ include file="/common/taglibs.jsp"%>
<%@ page
	import="org.apache.shiro.web.filter.authc.FormAuthenticationFilter"%>
<html>
<meta name="decorator" content="/template/template1_2.jsp">
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
		//设置规则类型下拉框的值
		$.ajax({
			async : false,
			url : '${ctx}/rpt/input/rule/dataRulesCombox',
			success : function(data) {
				selection = data;
			}
		});

		searchForm();
		$("#search [name='templeId']").val("${id}");
		
		url = "${ctx}/rpt/input/rule/list.json?templeId=" + "${id}&d="+new Date().getTime();
		initGrid();
		initbutton();
		initButtons();
		BIONE.addSearchButtons("#search", grid, "#searchbtn");

		$("#ruleName").ligerComboBox();
	});
	
	function onSelected() {
		//修改补录规则类型位置,添加规则只要选择左侧模板，进入添加页面后再选择规则类型 20190605
		BIONE.commonOpenDialog("补录规则","dataRules", "650", "450","${ctx}/rpt/input/rule/rule?id=${id}&d="+new Date().getTime());
		
		/*
		var ruleName = liger.get('ruleName').getText();
		var ruleType = liger.get('ruleName').getValue();
		if (ruleType == 1) {
			BIONE.commonOpenDialog(ruleName,
					"dataRules", "575", "400",
					"${ctx}/rpt/input/rule/rule1?id=" + "${id}&d="+new Date() );
		} else if (ruleType == 2) {
			BIONE.commonOpenDialog(ruleName,
					"dataRules", "550", "380",
					"${ctx}/rpt/input/rule/rule2?id=" + "${id}&d="+new Date() );
		} else if (ruleType == 3) {
			BIONE.commonOpenDialog(ruleName,
					"dataRules", "1000", "450",
					"${ctx}/rpt/input/rule/rule3?id=" + "${id}&d="+new Date() );
		} else if (ruleType == 4) {
			BIONE.commonOpenDialog(ruleName,
					"dataRules", "1000", "450",
					"${ctx}/rpt/input/rule/rule4?id=" + "${id}&d="+new Date() );
		} else if (ruleType == 5) {
			BIONE.commonOpenDialog(ruleName,
					"dataRules", "1000", "450",
					"${ctx}/rpt/input/rule/rule5?id=" + "${id}&d="+new Date());
		} else{
			BIONE.tip("请选择规则类型！");
		}
		*/
		
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
				name : "ruleName",
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
		if(!(window.parent.lookType || "${lookType}")){
			buttons.push({
				text : '保存',
				onclick : save_obj
			});
		}
		BIONE.addFormButtons(buttons,true);
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
								align : 'left',
								width : '26%',
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
								name : 'createUser',
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
					url : url+new Date(),
					toolbar : {}
				});

	}
	
	function initButtons() {
		var btns = [];
		//新增规则按钮从搜索栏移到grid上，新增时不再先选择规则类型20190605
		if(!(window.parent.lookType || "${lookType}")){
			btns.push({
				text : '新增',
				icon : 'add',
				click : onSelected
			});
		}
		btns.push({
			text : '修改',
			click : beginEdit,
			icon : 'modify'
		},{
			text : '删除',
			click : deleteRow,
			icon : 'delete'
		});
		BIONE.loadToolbar(grid, btns, function() {
		});
	}
	function delete_templeFile() {
		achieveIds();
		$.ligerDialog.confirm('确实要删除这些条记录吗？', function(yes) {
			if (yes) {
				$.ajax({
					type : "POST",
					url : "${ctx}/rpt/input/rule/deleteRule/" + ids,
					dataType : "text",
					success : function(text) {
						BIONE.tip(text);
						grid.loadData();
					}
				});
			}
		});
	}
	function beginEdit(rowid) {
		var rows = grid.getCheckedRows();
		if (rows.length == 0) {
			BIONE.tip('请选择要修改的规则');
			return;
		}
		chackType = achieveIds();
		if (ids.length == 1 && chackType == 1) {
			BIONE.commonOpenDialog("数值规则修改",
					"dataRules", "575", "400",
					"${ctx}/rpt/input/rule/rule1?id=" + "${id}" + (chackType == null ? "" : "&ruleId=" + ids[0] ) + "&d="+new Date().getTime());
		} else if(ids.length == 1 && chackType == 2){
			BIONE.commonOpenDialog("正则规则修改",
					"dataRules", "550", "380",
					"${ctx}/rpt/input/rule/rule2?id=" + "${id}" + (chackType == null ? "" : "&ruleId=" + ids[0] ) + "&d="+new Date().getTime());
		} else if(ids.length == 1 && chackType == 3){
			BIONE.commonOpenDialog("表内横向规则修改",
					"dataRules", "1000", "450",
					"${ctx}/rpt/input/rule/rule3?id=" + "${id}" + (chackType == null ? "" : "&ruleId=" + ids[0] ) + "&d="+new Date().getTime());
		} else if(ids.length == 1 && chackType == 4){
			BIONE.commonOpenDialog("表内纵向规则修改",
					"dataRules", "1000", "450",
					"${ctx}/rpt/input/rule/rule4?id=" + "${id}" + (chackType == null ? "" : "&ruleId=" + ids[0] ) + "&d="+new Date().getTime());
		} else if(ids.length == 1 && chackType == 5){
			BIONE.commonOpenDialog("表间一致规则修改",
					"dataRules", "1000", "450",
					"${ctx}/rpt/input/rule/rule5?id=" + "${id}" + (chackType == null ? "" : "&ruleId=" + ids[0] ) + "&d="+new Date().getTime());
		}
		grid.reRender();
	}
	function beginlook(rowid) {
		chackType = achieveIds();
		if (ids.length == 1 && chackType == 1) {
			BIONE.commonOpenDialog("数值规则修改",
					"dataRules", "575", "400",
					"${ctx}/rpt/input/rule/rule1?id=" + "${id}" + (chackType == null ? "" : "&ruleId=" + ids[0] ) + "&lookType=lookType&d="+new Date().getTime());
		} else if(ids.length == 1 && chackType == 2){
			BIONE.commonOpenDialog("正则规则修改",
					"dataRules", "550", "380",
					"${ctx}/rpt/input/rule/rule2?id=" + "${id}" + (chackType == null ? "" : "&ruleId=" + ids[0] ) + "&lookType=lookType&d="+new Date().getTime());
		} else if(ids.length == 1 && chackType == 3){
			BIONE.commonOpenDialog("表内横向规则修改",
					"dataRules", "1000", "450",
					"${ctx}/rpt/input/rule/rule3?id=" + "${id}" + (chackType == null ? "" : "&ruleId=" + ids[0] ) + "&lookType=lookType&d="+new Date().getTime());
		} else if(ids.length == 1 && chackType == 4){
			BIONE.commonOpenDialog("表内纵向规则修改",
					"dataRules", "1000", "450",
					"${ctx}/rpt/input/rule/rule4?id=" + "${id}" + (chackType == null ? "" : "&ruleId=" + ids[0] ) + "&lookType=lookType&d="+new Date().getTime());
		} else if(ids.length == 1 && chackType == 5){
			BIONE.commonOpenDialog("表间一致规则修改",
					"dataRules", "1000", "450",
					"${ctx}/rpt/input/rule/rule5?id=" + "${id}" + (chackType == null ? "" : "&ruleId=" + ids[0] ) + "&lookType=lookType&d="+new Date().getTime());
		}
		grid.reRender();
	}
	function deleteRow(rowid) {
		var rows = grid.getCheckedRows();
		if (rows.length == 0) {
			BIONE.tip('请选择要删除的规则');
			return;
		}
		$.ligerDialog.confirm('确实要删除这条记录吗?', function(yes) {
			if (yes) {
				
				chackType = achieveIds();
				$.ajax({
					type : "POST",
					url : "${ctx}/rpt/input/rule/deleteRule/" + ids+"?d="+new Date().getTime(),
					dataType : "text",
					success : function(text) {
						BIONE.tip(text);
						grid.set('url', url+new Date());
					}
				});
			}
			
		})
	}
	function upset() {
		parent.next('3', '${id}');
	}
	function save_obj() {
		parent.saveTempleInfo();
	}
	function cancleCallBack() {
		parent.closeDsetBox();
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
		return "${ctx}/rpt/input/rule/list.json?templeId=" + "${id}&d="+new Date().getTime();
	}
</script>
</head>
<body>

</body>
</html>