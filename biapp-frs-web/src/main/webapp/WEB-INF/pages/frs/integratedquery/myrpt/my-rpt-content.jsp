<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<%@ include file="/common/taglibs.jsp"%>
<html>
<head>
<meta name="decorator" content="/template/template1_BS.jsp">
<script type="text/javascript">
	// 全局变量
	var grid;
	var nodeType;
	var realId;
	var dialogWidth = 1000;
	var dialogHeight = 500;
	var selCatalogId = "${nodeId}";
	var selCatalogType = "${nodeType}";
	var selCatalogNm = "${nodeNm}";
	var type ="${type}";
	var eleType = "3";
	$(function() {
		var parent = window.parent;
		// 初始化dialog高、宽
		dialogWidth = $(parent.window).width() * 0.95;
		dialogHeight = $(parent.window).height() - 30;
		//初始化
		ligerSearchForm();
		$("#nodeId").val(selCatalogId);
		$("#nodeType").val(selCatalogType);
		ligerGrid();
		initButtons();
		//BIONE.addSearchButtons("#search", grid, "#searchbtn");
	});
		 //渲染查询表单
	function ligerSearchForm() {
		$("#search").ligerForm({
			fields : [ {
				display : "查询名称", name : "queryNm", newline : true, type : "text", cssClass : "field",
				attr : {
					op : "like", field : "ins.queryNm"
				}
			}, {
				display : "创建时间", name : "createTime", newline : false, type : "date", cssClass : "field",
				options : {
					onChangeDate : function(value) {
						var vl = value.split('-');
						var dt = new Date(new Number(vl[0]), new Number(vl[1]) - 1, new Number(vl[2]) + 1);
						var year = dt.getFullYear(), mont = dt.getMonth(), date = dt.getDate();
						liger.get('createTime_0').setValue(year + '-' + (mont + 1) + '-' + date);
					}
				},
				attr : {
					vt : 'date', op : ">", field : "ins.createTime"
				}
			}, {
				display : "创建时间", name : "createTime_0", newline : false, type : "date", cssClass : "field",
				attr : {
					vt : 'date', op : "<=", field : "ins.createTime"
				}
			}, {
				display : "文件夹ID", name : "folderId", newline : true, type : "hidden", cssClass : "field", 
				attr : {
					op : "=", field : "rel.folder_id"
				}
			} ]
		});
		$('#createTime_0').closest('.l-fieldcontainer').hide();
		// 添加我的查询搜索按钮
		BIONE.addSearchButtons("#search", grid, "#searchbtn");
		$('.l-trigger-hover.l-trigger-cancel').live('click', function() {
			var id = $(this).siblings('input').attr('id');
			if (id == 'createTime') {
				$('#createTime_0').val('');
			}
		});
		$('.l-btn .l-icon-refresh2').parent().click(function() {
			$('#createTime_0').val('');
		});
	}
		//渲染GRID
		function ligerGrid() {
			grid = $("#maingrid").ligerGrid({
				columns : [
					{
						display : '查询名称', name : 'queryNm', align : 'center', minWidth : '', width : '',
						render : function(rowdata, index, value, column) {
							return '<a name="' + column.name + '" class="queryName" rowid="' + rowdata.__id + 
									'" onclick="return false;"><span class="query">' + value + '</span></a>';
						}
					}, {
						display : '创建用户', name : 'createUser', align : 'center', minWidth : '', width : '', 
						render : function(rowdata) {
							var arr = rowdata.createUser.split('_');
							return arr[1]; 
						}
					}, {
						display : '创建时间', name : 'createTime', align : 'center', minWidth : '', width : '', type : 'date', format : 'yyyy-MM-dd'
					}, {
						display : '备注', name : 'remark', align : 'center', minWidth : '', width : ''
					} ], 
			/*	onDblClickRow : function (data, rowindex, rowobj) {
					var instId = data.instanceId, instType = data.instanceType;
					var width = $(window).width() + 5, height = $(window).height() - 3;
					var title = (instType == '01') ? '我的报表' : (instType == '02' ? '我的查询' : '');
					BIONE.commonOpenDialog(title, "RptDialog", width, height, app.ctx + '/rpt/frs/MyRpt/rptQuery?instId=' + instId + '&instType=' + instType);
				},*/
			/*	onSelectRow : function(rowdata, rowid, rowobj) {
					var rows = grid.getSelectedRows();
					$(rows).each(function(i, n) {
						grid.unselect(n);
					});
					grid.select(rowdata);
				},*/
			//	dateFormat: 'yyyy-MM-dd hh:mm:ss.S',
			
				/* dataAction : 'server', //从后台获取数据
				usePager : true, //服务器分页
				alternatingRow : true, //附加奇偶行效果行
				colDraggable : true,
				url : "${ctx}/bione/admin/role/list.json",
				sortName : 'lastUpdateTime',//第一次默认排序的字段
				sortOrder : 'desc', //排序的方式
				rownumbers : true,
				width:'97%', */
				
				checkbox : true,
				usePager : true,
				isScroll : false,
				rownumbers : true,
				alternatingRow : true, /* 附加奇偶行效果行 */
				colDraggable : true,	/* 是否允许表头拖拽 */
				dataAction : 'server',	/* 从后台获取数据 */
				method : 'post',
				url :  '${ctx}/frs/integratedquery/myrpt/collect/colList',
				sortName : 'createTime', //第一次默认排序的字段
				sortOrder : 'desc',
				//delayLoad : true,
				height : '100%',
				width : '100%',
				toolbar : {}
			});
		}  
	//添加或修改结束后，刷新
	function freshTree(){
		window.parent.searchHandler();
	}
	//初始化按钮
	function initButtons() {
		var btns = [{
			text : '查询',
			click : f_open_edit,
			icon : 'modify'
		}, {
			text : '删除',
			click : f_delete,
			icon : 'delete'
		}];
		BIONE.loadToolbar(grid, btns, function() {
		});
	}
	//要素添加
	function f_open_add() {  
		BIONE.commonOpenLargeDialog("要素添加", "elementAddWin","${ctx}/cabin/element/catalog/content/add?&selCatalogId=" + selCatalogId + "&selCatalogNm=" + type +"&eleType="+eleType);
	}
	//要素修改
	function f_open_edit() {  
		var selElement = grid.getSelectedRow();
		var num = "1";
		BIONE.commonOpenLargeDialog("要素修改", "elementEditWin","${ctx}/cabin/element/catalog/content/edit?&elementId=" + selElement.elementId + "&elementNm=" + selElement.elementNm+ "&catalogId=" + selElement.catalogId + "&selCatalogNm=" + type +"&num="+ num);
	}
	//要素删除
	function f_delete(){
		var selElementRow = grid.getSelecteds();
		//判断是否选中数据
		if(selElementRow.length == 0){
			BIONE.tip("请选择要素行")
		}
		$.ligerDialog.confirm('确实要删除这' + selElementRow.length + '条记录吗!',
				function(yes){
					var eleLength = selElementRow.length;
					if(yes){
						var ids = "";
						for ( var i = 0; i < eleLength; i++) {
							if (ids != "") {
								ids += ",";
							}
							ids += selElementRow[i].elementId;
						}
						$.ajax({
							type : "POST",
							url : '${ctx}/cabin/element/catalog/destroyOwn.json',
							dataType : "json",
							type : "post",
							data : {
								"ids" : ids
							},
							success : function(result) {
								if(result==true){
								BIONE.tip('删除成功');
								//刷新表格
								grid.loadData();
								//刷新树
								window.parent.searchHandler();
								}else{
								BIONE.tip('删除失败,请联系管理员!');
								}
							} 
						});
					}
	      });
	}
	function openRpt(elementId,elementNm,catalogId,selCatalogNm){
		 var num = "0";
		 url="${ctx}/cabin/element/catalog/content/edit";
		 url+="?elementId=" + elementId +"&elementNm=" + elementNm +"&catalogId=" +catalogId +"&selCatalogNm="+selCatalogNm+"&num="+ num;;
	
		dialog = BIONE.commonOpenFullDialog("要素【"+elementNm+"】", "elementEditWin",url);
	}
	
</script>
</head>
<body>
</body>
</html>