<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ include file="/common/taglibs.jsp"%>
<html>
<meta name="decorator" content="/template/template2.jsp">
<head>
<script type="text/javascript" src="${ctx}/js/udip/UDIP.js"></script>
<script type="text/javascript">
	var items, grid, manager,ids = [];
	var operateType = "",allOrSomeMission = "", tempelName = "";
	var taskId;
	var caseId = '${caseId}';
	var click = '${click}';
	var libData = {
		Rows : []
	};
	$(function() {
		initTree();
		$("#treeToolbar").ligerTreeToolBar({
			items : [ {
				icon : 'all-doc',
				text : '全部',
				click : getAllMission
			} ]
		});
		$("#treeToolbar").ligerTreeToolBar({
			items : [ {
				icon : 'warning-doc',
				text : '进行中',
				click : getSomeMission
			} ]
		});
		searchForm();
		initGrid();
		var managers = $("#maingrid").ligerGetGridManager();
		BIONE.addSearchButtons("#search", grid, "#searchbtn");
		BIONE.loadToolbar(grid, [ {
			text : '在线补录', click : beginInputTask, icon : 'input'
		}, {
			text : '过滤补录', click : form_search_terms, icon : 'find_edit'
		}, {
			text : '批量导入', click : downloadPage, icon : 'input_excel'
		}, {
			text : '模板下载', click : download, icon : 'download'
		}, {
			text : '模板', click : templeInfo, icon : 'temp'
		}, {
			text : '数据查看', click : look2, icon : 'view'
		}, {
			text : '状态', click : authRecordInfo, icon : 'press'
		} ]);
		$("#tableName").focusout(function() {
			var tableName = document.getElementById("tableName").value;
			$.ligerui.get("tableName").setValue(tableName.toUpperCase());
		});
		
	});

	window.afterLayOut = function(){
		if(caseId){
			var ids = caseId.split(',');
			if(ids.length>1){
				taskTree.expandAll(true);
			}else{
				var selectNode = taskTree.getNodesByParam('id',caseId)[0];//选中指定的节点
				if(selectNode){
					zTreeOnClick(null,null,selectNode);
					taskTree.selectNode(selectNode);
				}
			}
		}
	}
	function initTree() {
		taskTree = $.fn.zTree.init($("#tree"), {
			async:{
				enable:true,
				url: "${ctx}/rpt/input/taskcase/taskCatalogTree.json?",
				autoParam:["nodeType", "id=nodeId"],
				dataType:"json"
			},
			data : {
				keep : {
					parent : true
				},
				key : {
					title: "title",
					name : "text"
				},
				simpleData : {
					enable : true,
					idKey : "id",
					pIdKey : "upId",
					rootPId : null
				}
			},
			view : {
				nameIsHTML: true,
				showLine : false,
				selectedMulti:false
			},
			callback : {
				onClick : zTreeOnClick
			}
		});

		$.ajax({
			cache : false,
			async : false,
			url : "${ctx}/rpt/input/taskProcess/notemple/list.json?allOrSomeMission="+allOrSomeMission +"&caseId=" + (caseId||'')+"&click="+(click||''),
			success : function(result) {
				taskTree.addNodes(null, result, false);
				if(allOrSomeMission == '1'){
					taskTree.expandAll(true);
				}
			},
			error : function(result, b) {
				BIONE.tip('发现系统错误 <BR>错误码：' + result.status);
			}
		});
	}

	function getAllMission(){
		allOrSomeMission = "";
		caseId = "";
		initTree();
	}
	function getSomeMission(){
		allOrSomeMission = "1";
		caseId = "";
		initTree();
	}
	
	function zTreeOnClick(event, treeId, treeNode){
		if(treeNode.params.type=='case'){
			caseId = treeNode.id;
			$("#search [name='caskId']").val(caseId);
			grid.set('newPage', 1);
			grid.set('url', '${ctx}/rpt/input/taskcase/list.json?' + (caseId == null ? "" : 'caseId=' + caseId));
		}
	}
	
	function searchForm() {
		$("#search").ligerForm({
			fields : [ {
				name : "caskId", type : "hidden"
			}, {
				display : "模板名称", name : "templeName", width:140, newline : true, type : "text", cssClass : "field",
				attr : {
					op : "like", field : "temple.templeName"
				}
			}, {
				display : "模板状态", name : "stateValue", width:140, newline : false, type : 'select',
				options : {
					valueFieldID : 'state',
					data : [ { text : '正常', id : '1' }, { text : '已锁定', id : '0' } ]
				},
				cssClass : "field",
				attr : {
					op : "=", field : "temple.state"
				}
			}, {
				display : "物理表名", name : "tableName", width:140, newline : true, type : "text", cssClass : "field",
				attr : {
					op : "like", field : "temple.tableName"
				}
			} ]
		});
	}

	function initGrid() {
		grid = manager = $("#maingrid").ligerGrid({
			width : '100%',
			columns : [ 
            {
            	hide : 1 ,name : 'templeId',width : '0.5%'
            }, {
				isSort: true, display : '模板名称', name : 'templeName', align : 'left', width : '25%', minWidth : '20%'
			}, {
				isSort: true, display : '物理表名', name : 'tableName', align : 'left', width : '25%', minWidth : '20%'
			}, {
				isSort: true, display : "模板状态", name : "state", width : '10%', minWidth : '10%',
				editor : {
					type : 'select',
					data : [ { 'id' : '1', 'text' : '正常' }, { 'id' : '0', 'text' : '已锁定' } ]
				},
				render : function(row) {
					switch (row.state) {
						case "1": return "正常";
						case "0": return "已锁定";
					}
				}
			}, {
				isSort: true, display : '进度', name : 'creator', align : 'center', width : '10%', minWidth : '10%',
				render : function(data, row, context, it) {
					if (context == 'dispatch') {
						return "已下发";
					} else if (context == 'save') {
						return "已保存";
					} else if (context == 'submit') {
						return "已提交";
					} else if (context == 'validate') {
						return "已校验";
					} else if (context == 'sucess') {
						return "审核通过";
					} else if (context == 'refuse') {
						return "已回退";
					} else {
						return context;
					}
				}
			}, {
				display : '描述', name : 'remark', align : 'center', width : '21%', minWidth : '10%'
			} ],
			url : '${ctx}/rpt/input/taskcase/list.json?caseId='+caseId+"&d="+new Date().getTime(),
			checkbox : true,
			dataAction : 'server', //从后台获取数据
			usePager : false, //服务器分页
			alternatingRow : true, //附加奇偶行效果行
			colDraggable : true,
			isScroll : false,
			dataType : 'text',
			toolbar : {},
			method : 'get',
			data : libData,
			sortName : 'templeName, tableEnName',//第一次默认排序的字段
			sortOrder : 'asc', //排序的方式
			rownumbers : true,
			onDblClickRow : function (data, rowindex, rowobj){
				var caseId = document.getElementById('caskId').value;
				getSelectRow(data.templeId,caseId,data.templeName);
				grid.reRender();
            }
		});
	}
	function onCheckRow(checked, rowdata, rowindex, rowDomElement){
		UDIP.onCheckRow(this,checked,rowindex)
	}
	function onCheckAllRow(checked,grip){
			UDIP.onCheckAllRow(this);
	}
	function beginInputTask(){
// 		achieveIds();
// 		var caseId = document.getElementById('caskId').value;
// 		if (ids.length == 1) {
			getSelectRow(ids[0],caseId,tempelName);
// 		} else {
// 			BIONE.tip("请选择一条数据进行操作");
// 			return;
// 		}
		grid.reRender();
	}
	
	function form_search_terms(){
		achieveIds();
		var caseId1 = document.getElementById('caskId').value;
		if (ids.length == 1) {
			BIONE.commonOpenDialog("过滤条件", "search", "750", "380",
					"${ctx}/rpt/input/taskcase/getSearchTerms?templeId=" + ids[0]+"&caseId=" +caseId1+"&d=" + new Date().getTime());
		} else {
			BIONE.tip("请选择一条数据进行操作");
			return;
		}
	}
	function searchDataInfo(templeId,caseId,info,sqlStr){
		UDIP.commonOpenFullDialog(tempelName+"数据补录",
				"inputTaskInfo", "${ctx}/rpt/input/taskcase/inputTaskInfo?templeId=" + templeId+"&caseId=" +caseId+ "&sqlStr="+encodeURIComponent(sqlStr) + "&searchTerms="+info);
	}
	function downloadPage(){
		achieveIds();
		var caseId = document.getElementById('caskId').value;
		if (ids.length == 1) {
				$.ajax({
					url : "${ctx}/rpt/input/taskcase/authRecordDataType?d=" + new Date().getTime(),
					type : 'get',
					async : true,
					data: {
						templeId: ids[0],
						caseId : caseId
					},
					beforeSend : function() {
						BIONE.loading = true;
						BIONE.showLoading("正在检查补录表信息...");
					},
					complete : function() {
						BIONE.loading = false;
						BIONE.hideLoading();
					},
					success : function(data) {
						if(data == "" || data == null){
							BIONE.tip("任务未下发到你所在机构！");
						}else if(data == "2"){
							BIONE.tip("补录表已锁定。");
						}else if(data == "1"){
							BIONE.tip("补录表已不存在或已修改。");
						}else if(data == "0"){
							BIONE.tip("该批次补录已结束。");
						}else if(data=="submit" || data=="sucess" ){
							BIONE.tip('数据已提交。');
						}else{
							UDIP.commonOpenFullDialog(tempelName+"数据导入",
									"inputTaskInfo", "${ctx}/rpt/input/taskcase/downloadPage?templeId=" + ids[0]+"&caseId=" +caseId);
						}
					}
				});
			
		}else{
			BIONE.tip("请选择一条数据进行操作");
			return;
		}
	}
	function download(){
		achieveIds();
		var rows = grid.getCheckedRows();
		if (rows.length == 1) {
			$.ajax({
				async : true,
				url : '${ctx}/rpt/input/temple/templeFileExit/' + ids[0],
				beforeSend : function() {
					BIONE.loading = true;
					BIONE.showLoading("正在下载文件...");
				},
				complete : function() {
					BIONE.loading = false;
					BIONE.hideLoading();
				},
				success : function(data) {
					if(data!=1){
						BIONE.showError("模板中没有启用的excel模板。");
					}else{
						var caseId = document.getElementById('caskId').value;
						if (!document.getElementById('download')) {
							$('body').append($('<iframe id="download" style="display:none"/>'));
						}
						$("#download").attr('src',
								'${ctx}/rpt/input/taskcase/excelDownLoalTaskcaseInfo?templeId=' +ids[0]+'&caseId='+caseId+'&orgId='+'' );
					}
				}
			});
		}else{
			BIONE.tip("请选择一条数据进行操作");
			return;
		} 
	}
	function look(info,sqlStr){
		achieveIds();
		var caseId = document.getElementById('caskId').value;
		if (ids.length == 1) {
			orgId = "";
			UDIP.commonOpenFullDialog(tempelName+"数据预览",
					"inputTaskInfo", "${ctx}/rpt/input/taskcase/inputTaskInfoLook?templeId=" + ids[0]+"&caseId=" +caseId +"&searchTerms="+info+ "&orgId="+orgId+ "&sqlStr="+encodeURIComponent(encodeURIComponent(sqlStr)));
		}else{
			BIONE.tip("请选择一条数据进行操作");
			return;
		}
	}
	function look2(){
		achieveIds();
		var caseId = document.getElementById('caskId').value;
		if (ids.length == 1) {
			orgId = "";
			UDIP.commonOpenFullDialog(tempelName+"数据预览",
					"inputTaskInfo", "${ctx}/rpt/input/taskcase/inputTaskInfoLook?templeId=" + ids[0]+"&caseId=" +caseId + "&orgId="+orgId);
		}else{
			BIONE.tip("请选择一条数据进行操作");
			return;
		}
	}

	function f_reload() {
		var manager = $("#maingrid").ligerGetGridManager();
		manager.loadData();
	}
	
	// 获取选中的行
	function achieveIds() {
		ids = [];
		var rows = grid.getSelectedRows();
		for ( var i in rows) {
			ids.push(rows[i].templeId);
			tempelName = rows[0].templeName;
		}
		return rows;
	}
	function templeInfo(){
		achieveIds();
		var caseId = document.getElementById('caskId').value;
		if (ids.length == 1) {
			BIONE.commonOpenDialog(tempelName+'查看', 'objDefManage', 562, 334,'${ctx}/rpt/input/temple/viewtemple?id=' + ids[0]+'&lookType='+'lookType');
		}else{
			BIONE.tip("请选择一条数据进行操作");
			return;
		}
	}
	function authRecordInfo(){
		achieveIds();
		var caseId = document.getElementById('caskId').value;
		if (ids.length == 1) {
			
			BIONE.commonOpenDialog(tempelName+"任务进度查看", "dataRules", "575", "420",
					"${ctx}/rpt/input/taskcase/chackAuthRecordInfo?templeId=" + ids[0]+"&caseId=" +caseId);
		}else{
			BIONE.tip("请选择一条数据进行操作");
			return;
		}
	}
	function getSelectRow(id,caseId,tempelName){
		$.ajax({
			url : "${ctx}/rpt/input/taskcase/authRecordDataType?d=" + new Date().getTime(),
			type : 'get',
			async : true,
			data: {
				templeId: id,
				caseId : caseId
			},
			beforeSend : function() {
				BIONE.loading = true;
				BIONE.showLoading("正在检查补录表信息...");
			},
			complete : function() {
				BIONE.loading = false;
				BIONE.hideLoading();
			},
			success : function(data) {
				if(data == "" || data == null){
					BIONE.tip("任务未下发到你所在机构！");
				}else if(data == "2"){
					BIONE.tip("补录表已锁定。");
				}else if(data == "1"){
					BIONE.tip("补录表已不存在或已修改。");
				}else if(data == "0"){
					BIONE.tip("该批次补录已结束。");
				}else if(data=="submit" || data=="sucess" ){
					BIONE.tip('数据已提交。');
				}else{
					BIONE.commonOpenFullDialog(tempelName + "数据补录", "inputTaskInfo", "${ctx}/rpt/input/taskcase/inputTaskInfo?templeId=" + id + "&caseId=" + caseId);
				}
			}
		});
	}
	//提交操作
	function form_submit(){
		achieveIds();
		if (ids.length > 0) {
			var caseId = document.getElementById('caskId').value;
			$.ajax({
				url : "${ctx}/rpt/input/taskcase/authRecordDataType?d=" + new Date().getTime(),
				type : 'get',
				async : true,
				data: {
					templeId: ids.join(','),
					caseId : caseId
				},
				beforeSend : function() {
					BIONE.loading = true;
					BIONE.showLoading("正在检查补录表信息...");
				},
				complete : function() {
					BIONE.loading = false;
					BIONE.hideLoading();
				},
				success : function(data) {
					if(data == "" || data == null){
						BIONE.tip("数据状态异常，请联系管理员！");
					}else if(data == "2"){
						BIONE.tip("补录表已锁定。");
					}else if(data == "1"){
						BIONE.tip("补录表已不存在或已修改。");
					}else if(data == "0"){
						BIONE.tip("该批次补录已结束。");
					}else if(data=="submit" || data=="sucess" ){
						BIONE.tip('数据已提交。');
					}else if(data=="validate"){
						submitData();
					}else{
						$.ligerDialog.confirm('数据尚未校验，是否强制提交?', function(yes) {
							if (yes) {
								submitData();
							}
						});
					}
				}
			});
		}else{
			BIONE.tip("请选择数据进行提交。");
			return;
		}
	}
	function submitData(){
		var caseId = document.getElementById('caskId').value;
		
	    var info = {"templeId" : ids.join(','),
				"caseId" : caseId
		};
	    $.ajax({
			async : true,
			url : "${ctx}/rpt/input/taskcase/submitTaskcaseTempleInfoList.json",
			dataType : 'text',
			data : info,
			type : "post",
			beforeSend : function() {
				BIONE.loading = true;
				BIONE.showLoading("正在提交数据中...");
			},
			complete : function() {
				BIONE.loading = false;
				BIONE.hideLoading();
			},
			success : function(data) {
				if(data == null ||data == "" ){
					BIONE.showSuccess("提交成功");
					manager.loadData(); 
				}else{
					BIONE.showError("提交失败： "+data);
				}
			},
			error : function(result, b) {
				BIONE.tip('发现系统错误 <BR>错误码：' + result.status);
			}
			
		});
	}
</script>

<title></title>
</head>
<body>
	<div id="template.left.up.icon">
		<img src="${ctx }/images/classics/icons/application_side_tree.png" />
	</div>
	<div id="template.left.up">
		<span style="font-size: 12">任务树</span>
	</div>
	<div>
		<input type="text" id="txt2" value="" />
	</div>
</body>
</html>