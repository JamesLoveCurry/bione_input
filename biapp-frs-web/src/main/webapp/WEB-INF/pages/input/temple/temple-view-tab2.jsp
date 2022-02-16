<%@ page language="java" pageEncoding="UTF-8"%>
<%@ include file="/common/taglibs.jsp"%>
<%@ page
	import="org.apache.shiro.web.filter.authc.FormAuthenticationFilter"%>
<html>
<meta name="decorator" content="/template/template1_6.jsp">
<head>
<script type="text/javascript" src="${ctx}/js/udip/remark/global.js"></script>
<script type="text/javascript" src="${ctx}/js/udip/UDIP.js"></script>
<script type="text/javascript" src="${ctx}/js/udip/remark/temple.js"></script>
<script type="text/javascript">
	var groupicon = "${ctx}/images/classics/icons/communication.gif";
	var grid, btns, url, ids = [], dialog, buttons = [];
	var rowSize = 0, savaType = window.parent.savaType;
	var connLowermanager;
	var connLowerState = "0", setValue = "0", columnSpit = "#@#";
	var dsIdInfo = "",tableNameInfo = '';
	var dataZidian2;
	var libMap = {};
	$(function() {
		url = "${ctx}/udip/data/getTableMoreList?id="+"${id}";
		initGrid();
		//changerList();

		searchForm();
		connLowermanager = $("#connLower").ligerCheckBox({
			disabled : false
		});
		setCss();
		initbutton();

		BIONE.createButton({
			appendTo : "#searchbtn",
			text : '查看主键',
			icon : 'save',
			width : '75px',
			click : function() {
				var dsId = dsIdInfo;
				var tableId = tableNameInfo;
				
				$.ajax({
					async : false,
					url : '${ctx}/udip/data/getColumnList/' + "${id}",
					success : function(data1) {
						var manager = $("#maingrid").ligerGetGridManager();
						var data = manager.getData();
						BIONE.commonOpenDialog("查看主键", "templeKey",
								"750", "380",
								"${ctx}/udip/temple/templeKey?id="
										+ "${id}"
										+ "&lookType=lookType&d="
										+ new Date().getTime());
					}
				});

			}
		});
		BIONE.createButton({
			appendTo : "#searchbtn",
			text : '数据预加载查看',
			icon : 'save',
			width : '110px',
			click : function() {
				var dsId = dsIdInfo;
				var tableId = tableNameInfo;
				$.ajax({
					async : false,
					url : '${ctx}/udip/data/getColumnList/' + "${id}",
					success : function(data1) {
						var manager = $("#maingrid").ligerGetGridManager();
						var data = manager.getData();
						BIONE.commonOpenDialog("预加载信息查看", "templeKey",
								"750", "380",
								"${ctx}/udip/temple/templeDataLoad?id="
										+ "${id}"
										+ "&lookType=lookType&d="
										+ new Date().getTime());
					}
				});
			}
		});
		if (window.parent.lookType) {
			savaType = 1;
		}

		if ("${id}") {
			$.ajax({
				url : "${ctx}/udip/temple/findTempleInfo?templeId="+"${id}"+"&d="
						+ new Date().getTime(),
				data : {
					id : "${id}"
				},
				success : function(data) {
					if (data.connLower == "yes") {
						connLowermanager.setValue(true);
					}
					dsIdInfo = data.dsId;
					tableNameInfo = data.tableName;
					$("#search [name='dsId']").val(data.dsId);
					$("#search [name='dsName']").val(data.logicSysNo);
					$("#search [name='tableName']").val(data.tableName);
					$("#search [name='tableNameId']").val(data.tableName);
				}
			});
		}
		check();
	});
	//根据ID查找该补录模板的表字段
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
			fields : [ {
				display : "补录数据源",
				name : 'dsName',
				newline : true,
				type : 'text',
				width : 140,
				space : 10,
				labelWidth : 100,
				options : {
					disabled : true
				}
			}, {
				display : "补录表",
				name : 'tableName',
				newline : false,
				type : 'text',
				width : 140,
				space : 10,
				labelWidth : 100,
				options : {
					disabled : true
				}
			}, {
				display : "可否补录下级",
				name : 'connLower',
				width : 100,
				space : 10,
				labelWidth : 100,
				newline : false,
				type : 'checkbox'
			} ]
		});

	}

	function initbutton() {

		buttons.push({
			text : '关闭',
			onclick : cancleCallBack
		});
		buttons.push({
			text : '下一步',
			onclick : next
		});
		buttons.push({
			text : '上一步',
			onclick : upset
		});
		BIONE.addFormButtons(buttons);
	}
	function initGrid() {

		grid = manager = $("#maingrid")
				.ligerGrid(
						{
							width : '100%',
							columns : [
									{
										name : 'dataZidian',
										hide : 1,
										width : '1%'
									},
									{
										name : 'columnDetail',
										hide : 1,
										width : '1%'
									},
									{
										display : '序号',
										name : 'seqNo',
										align : 'center',
										width : '5%'
									},
									{
										display : '字段名称',
										name : 'columnName',
										align : 'center',
										isSort : true,
										width : '15%'
									},
									{
										display : '中文名',
										name : 'columnComment',
										align : 'center',
										width : '15%'
									},
									{
										display : '类型',
										name : 'columnType',
										align : 'center',
										width : '10%'
									},
									{
										display : '长度',
										name : 'columnLength',
										align : 'center',
										width : '7%'
									},
									{
										display : '小数位',
										name : 'pNumber',
										align : 'center',
										width : '7%'
									},
									{
										display : '可为空',
										name : 'nullable',
										isSort : true,

										width : '7%',
										render : function(rowdata, rowindex,
												value, column) {
											var h = '<input name="checkbox" type="checkbox"  checked="checked" id = "nullable_' + rowdata['columnName'] + '" columnname = "' + column.name + '"/>';
											return h;
										}
									},
									{
										display : '可筛选',
										name : 'selectable',
										isSort : true,

										width : '7%',
										render : function(rowdata, rowindex,
												value, column) {
											var h = '<input name="checkbox" type="checkbox"  checked="checked" id = "selectable_' + rowdata['columnName'] + '" columnname = "' + column.name + '"/>';
											return h;
										}
									},
									{
										display : '机构字段',
										name : 'orgColumn',

										width : '10%',
										render : function(rowdata, rowindex,
												value, column) {
											var h = '<input onclick="CookieGroup('+rowindex+')" name="checkbox" type="checkbox"  id = "orgColumn_' + rowdata['columnName'] + '" columnname = "' + column.name + '"/>';
											return h;
										}
									} ],
							checkbox : false,
							usePager : false,
							enabledEdit : true,
							clickToEdit : true,
							resizable : true,
							alternatingRow : true,//附加奇偶行效果行
							colDraggable : true,
							dataAction : 'server',//从后台获取数据
							method : 'post',
							url : url,
							frozen: true, 
							onAfterShowData : function(){
								if (setValue == "0") {
									var data = manager.getData();
									for (i = 0; i < data.length; i++) {
										if (data[i].nullable == "1") {
											$('#nullable_' + data[i].columnName).removeAttr("checked");
										}
										if (data[i].selectable == "0") {
											$('#selectable_'+ data[i].columnName).removeAttr("checked");
										}
										if (data[i].logicSysNo == data[i].columnName) {
											$("#orgColumn_"+data[i].columnName).attr("checked", true);
											connLowerState = "1";
											setCss();
										}
										$('#nullable_' + data[i].columnName).attr('disabled', 'disabled');
										$('#selectable_'+ data[i].columnName).attr('disabled', 'disabled');
										$('#orgColumn_' + data[i].columnName).attr('disabled', 'disabled');
									}
								}
								$('#connLower').attr('disabled', 'disabled');
								setValue = "1";
							},
							sortName : 'seqNo', //第一次默认排序的字段
							sortOrder : 'asc'
						});

	}
	function CookieGroup(num){
		var data = manager.getData();
		chackOrg(num);
		setCss();
		orgCheck();
	}
	
	function chackOrg(num) {
		var data = manager.getData();
		var chackboxmanager;
		for (i = 0; i < data.length; i++) {
			if ( i == num) {
				if(!$("#orgColumn_"+data[i].columnName).attr("checked")){
					$("#orgColumn_"+data[i].columnName).removeAttr("checked");
					connLowerState = "0";
				}else{
					$("#orgColumn_"+data[i].columnName).attr("checked", true)
					connLowerState = "1";
				}
			}else{
				$("#orgColumn_"+data[i].columnName).removeAttr("checked");
			}
			
		}

	}
	function setCss() {
		if (connLowerState == "1") {
			$('#connLower').removeAttr('disabled');
		} else {
			connLowermanager.setValue(false);
			$('#connLower').attr('disabled', 'disabled');
		}
	}
	function editTree() {
		BIONE.commonOpenLargeDialog('新建数据源', 'objDefManage',
				'${ctx}/bione/mtool/datasource/new');
	}
	function next() {
		
		if (savaType > 0) {
			$.ajax({
				async : false,
				url : '${ctx}/udip/data/getColumnList/' + "${id}?d="
						+ new Date().getTime(),
				success : function(data1) {
					var manager = $("#maingrid").ligerGetGridManager();
					var data = manager.getData();
					if (data1.length == 0) {
						BIONE.tip('此操作需要先保存模板信息。');
						return;
					} else if (data1.length != data.length) {
						BIONE.tip('此操作需要先保存模板信息。');
						return;
					} else {
						parent.next('3', '${id}');
						if (window.parent.lookType) {
							savaType = 1;
						}else{
							savaType = 0;
						}
					}
				}
			});
		}

	}
	function upset() {
		savaType = 1;
		parent.next('1', '${id}');
	}

	function cancleCallBack() {
		parent.closeDsetBox();
	}
	function deleteRow(rowid) {
		$.ligerDialog.confirm('确实要删除这条记录吗?', function(yes) {
			if (yes) {
				manager.deleteRow(rowid);
			}
		});
	}
	function getDataZidian(rowid) {
		if(!window.parent.lookType){
			var data = manager.getData();
			dataZidian2 = data[rowid].dataZidian;
			if (dataZidian2 == '') {
				dataZidian2 = null;
			}
			dialog = BIONE.commonOpenDialog('选择数据字典', "chackedZidian", "850",
					"370", "${ctx}/udip/library/getDataZidian?id=" + dataZidian2
							+ "&rowid=" + rowid + "&d=" + new Date().getTime());
		}
	}
	function setDataZidina(libId2, rowid) {
		if (libId2 == null || libId2 == "") {
			manager.updateCell("dataZidian", "", manager.getRowObj(rowid))
			grid.reRender();
		} else {
			manager.updateCell("dataZidian", libId2, manager.getRowObj(rowid))
			grid.reRender();
		}
	}

	function setDetail(rowid) {
		if(!window.parent.lookType){
			var data = manager.getData();
			columnDetail = data[rowid].columnDetail;
			$.ligerDialog.prompt = detailController.detailSet('设置描述', columnDetail,
					true, function(yes, value) {
						if (yes) {
							manager.updateCell("columnDetail", value, manager
									.getRowObj(rowid));
							grid.reRender();
						}
					});
		}

	}
	var detailController = {
		detailSet : function(title, value, multi, callback) {
			var target = $('<input type="text" class="l-dialog-inputtext"/>');
			if (typeof (multi) == "function") {
				callback = multi;
			}
			if (typeof (value) == "function") {
				callback = value;
			} else if (typeof (value) == "boolean") {
				multi = value;
			}
			if (typeof (multi) == "boolean" && multi) {
				target = $('<textarea cols="45" rows="5" class="l-dialog-textarea"></textarea>');
			}
			if (typeof (value) == "string" || typeof (value) == "int") {
				target.val(value);
			}
			var btnclick = function(item, Dialog, index) {
				Dialog.close();
				if (callback) {
					callback(item.type == 'yes', target.val());
				}
			}
			p = {
				title : title,
				target : target,
				width : 320,
				buttons : [ {
					text : $.ligerDefaults.DialogString.ok,
					onclick : btnclick,
					type : 'yes'
				}, {
					text : $.ligerDefaults.DialogString.cancel,
					onclick : btnclick,
					type : 'cancel'
				} ]
			};
			return $.ligerDialog(p);
		}
	}
	//对输入信息的提示
	function check() {
		$("#connLower").change(
				function() {
					checkLabelShow(TempleRemark.global.connLower);
					$("#checkLabelContainer").html(
							GlobalRemark.title
									+ TempleRemark.global.connLower);
				});
	}

	function orgCheck() {
		checkLabelShow(TempleRemark.global.orgColumn);
		$("#checkLabelContainer").html(
				GlobalRemark.title
						+ TempleRemark.global.orgColumn);
	}
</script>
</head>
<body>
	<div id="content" style="width: 100%; overflow: auto; clear: both;">
		<div id="template.center">
			<form id="mainsearch" action="" method="post"></form>
		</div>
	</div>
</body>
</html>