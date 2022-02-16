<%@ page language="java" pageEncoding="UTF-8"%>
<%@ include file="/common/taglibs.jsp"%>
<%@ page
	import="org.apache.shiro.web.filter.authc.FormAuthenticationFilter"%>
<html>
<head>
<meta name="decorator" content="/template/template2A_BS.jsp">
<script type="text/javascript" src="${ctx}/js/udip/UDIP.js"></script>

<script type="text/javascript">
	var id, dirId = '',taskState = "";var checkedTempleId = [],checkedTempleName = [];;
	var tempState = "0";
	var rptId;
	// 树节点类型
	var rootNodeType = "01";
	var folderNodeType;
	var uri = '${ctx}/frs/submitConfig/detailrpt/list';
	var rptTreeNodeIcon = "${ctx}/images/classics/icons/layout_sidebar.png";
	$(function() {
		initTree();
		searchForm();
		initGrid();
		BIONE.addSearchButtons("#search", grid, "#searchbtn");

		BIONE.loadToolbar(grid, [ {
			text : '添加',
			click : add,
			icon : 'add',
			operNo : 'add'
		}, {
			text : '修改',
			click : edit,
			icon : 'modify'
		}, {
			text : '删除',
			click : deleteRows,
			icon : 'delete'
		} ]);
		$("#treeToolbar").ligerTreeToolBar({
			items : [{
				icon:'config',
				text:'目录维护',
				menu: {
					width : 90,
					items : [ {
						icon : 'add',
						text : '添加',
						click : addCatalog
					}, {
						line : true
					}, {
						icon : 'modify',
						text : '修改',
						click : editCatalog
					}, {
						line : true
					}, {
						icon : 'delete',
						text : '删除',
						click : deleteCatalog
					}]
				}
			}]
		});
	});
	function searchForm() {
		$("#search").ligerForm({
			fields : [ {
				display : "报表名称",
				name : "rptName",
				newline : true,
				width: 120,
				type : "text",
				cssClass : "field",
				attr : {
					op : "like",
					field : "info.rptNm"
				}
			}]
		});
	}
	function initGrid() {
		grid = manager = $("#maingrid").ligerGrid(
				{
					width : '100%',
					columns : [
							{
								name : 'rptId',
								hide : 1,
								width : '1%'
							},{
								name : 'catalogId',
								hide : 1,
								width : '1%'
							},
							{
								isSort:true,
								display : '报表名称',
								name : 'rptNm',
								align : 'center',
								width : '40%'
							},
							{
								isSort:true,
								display : '业务类型',
								name : 'busiType',
								align : 'center',
								width : '14%',
								render : function(data, row, context, it) {
									if ("03" == context){
										return "人行大集中";
									} else if ("02" == context){
										return "1104监管";
									} else if ("01" == context){
										return "利率报备";
									} else if("04" == context) {
										return "east明细";
									} else if("05" == context){
										return "存款保险"
									}else {
										return "未知";
									}
								}
							},
							{
								isSort:true,
								display : "报表类型",
								name : "rptType",
								align : 'center',
								width : '14%',
								render : function(data , row , context , it){
									if ("00" == context){
										return "汇总类";
									}else if ("01" == context){
										return "明细类";
									}else if ("02" == context){
										return "单元格类";
									}else if ("03" == context){
										return "综合类";
									}else if ("04" == context){
										return "指标列表类";
									}else if ("05" == context){
										return "查询明细类";
									}
								}
							},
							{
								display : '状态',
								name : 'rptSts',
								align : 'center',
								width : '7%',
								render : function(data , row , context , it){
									if ("Y" == context){
										return "启用"
									} else {
										return "停用";
									}
								}
							} ,
							{
								display : '创建时间',
								name : 'createTime',
								align : 'center',
								width : '19%',
								type : 'date',
								format : 'yyyy-MM-dd hh:mm:ss',
								render : function(data , row , context , it){
									return (""+context).substring(0,19);
								}
							}],
					url : uri+"?d=new Date().getTime()",
					checkbox : true,
					width : "100%",
					rownumbers : true,
					dataAction : 'server', //从后台获取数据
					usePager : true, //服务器分页
					alternatingRow : true, //附加奇偶行效果行
					colDraggable : true,
					sortName : 'rptNm',//第一次默认排序的字段
					sortOrder : 'asc', //排序的方式
					method : 'get',
					pageParmName : 'page',
					pagesizeParmName : 'pagesize',
					toolbar : {}
				});
	}
	
	//新增
	function add() {
		if(taskTree.getSelectedNodes().length == 0){
			BIONE.tip("请选择目录节点");
			return;
		}
		var selectedNodes = taskTree.getSelectedNodes();
		if(selectedNodes[0].isParent){
			var catalogId = selectedNodes[0].id;
			var catalogNm = selectedNodes[0].text;
			top.BIONE.commonOpenDialog("新增报表" , "rptEdit" , $(top).width()*0.95 , $(top).height()*0.95 , "${ctx}/frs/submitConfig/detailrpt/detailTab?operFlag=add&catalogId="+catalogId+"&catalogNm="+encodeURI(encodeURI(catalogNm)));
		}else{
			BIONE.tip("只有目录节点才可以添加报表");
		}
		taskTree.refresh();
	}
	
	//修改
	function edit(type , row) {
		var rows  = grid.getSelectedRows();
		if(rows.length == 1){
			var catalogId = rows[0].catalogId;
			var modifyUrl = "${ctx}/frs/submitConfig/detailrpt/detailTab?operFlag=edit&catalogId="+catalogId+"&rptId="+rows[0].rptId+"&datetime="+new Date().getTime();
			top.BIONE.commonOpenDialog("报表维护" , "rptEdit" , $(top).width()*0.95 , $(top).height()*0.95 , modifyUrl);
		}else{
			BIONE.tip("请选择一条记录")
		}
	}

	
	function deleteRows() {
		var rows  = grid.getSelectedRows();
		if(rows.length == 1){
			$.ligerDialog.confirm('确实要删除这条记录吗?', function(yes) {
				if (yes) {
					var flag = false;
					$.ajax({
						async : false,
						url : "${ctx}/frs/submitConfig/detailrpt/deleteDetails?rptId=" + rows[0].rptId,
						success : function() {
							flag = true;
						}
					});
					if (flag == true) {
						BIONE.tip('删除成功');
						manager.loadData(); 
						taskTree.refresh();
					} else {
							BIONE.tip('删除失败');
					}
				}
			});
		}else {
			BIONE.tip("请选择一条记录");
			return;
		}
		

	}
	
	function initTree() {
		taskTree = $.fn.zTree.init($("#tree"), {
			async : {
				enable : true,
				url : "${ctx}/frs/submitConfig/detailrpt/templelistTree?treeType=dingzhi",
				autoParam : [ "id","upId"],
				dataType : "json",
				type : "post",
				dataFilter : function(treeId, parentNode, childNodes) {
					if (childNodes) {
						for ( var i = 0; i < childNodes.length; i++) {
							if(childNodes[0].isParent){
								childNodes[i].icon = rptTreeNodeIcon;
							}
						}
						return childNodes;
					}
				}
			},
			data : {
				keep : {
					parent : true
				},
				key : {
					name : "text"
				},
				simpleData : {
					id : "id"
				}
			},
			view : {
				showLine : false
			},
			callback : {
				onClick : function(event, treeId, treeNode) {
					var paramsNm;
					id = treeNode.id;
					upId = treeNode.upId;
					if(treeNode.isParent){
						paramsNm="catalogId";
					}else{
						paramsNm = "id";
					}
					grid.set('newPage', 1);
					grid.set('url', '${ctx}/frs/submitConfig/detailrpt/list?'+paramsNm+"="+id);
				},
				onNodeCreated : function(event, treeId, treeNode) {
					if (treeNode.upId.isParent) {
						//若是根节点，展开下一级节点
						taskTree.reAsyncChildNodes(treeNode, "refresh");
					}
				}
			}
		},[]);
	}
	// 目录管理功能
	var addCatalog = function(){
		catalogEdit = BIONE.commonOpenSmallDialog("目录维护","catalogEdit","${ctx}/frs/submitConfig/detailrpt/detail/catalogedit");
	}
	
	var editCatalog = function(){
		if(taskTree == null
				|| typeof taskTree == "undefined"){
			return ;
		}
		var selNodes = taskTree.getSelectedNodes();
		if(selNodes.length 
				&& selNodes.length > 0
				&& folderNodeType == selNodes[0].params.nodeType){			
			catalogEdit = BIONE.commonOpenSmallDialog("目录维护","catalogEdit","${ctx}/frs/submitConfig/detailrpt/detail/catalogedit?catalogId="+selNodes[0].params.catalogId);
		} else {
			BIONE.tip("请选择要维护的目录节点");
		}
	}
	
	var deleteCatalog = function(){
		if(taskTree == null
				|| typeof taskTree == "undefined"){
			return ;
		}
		$.ligerDialog.confirm('您确定删除这条记录么？', function(yes) {
			if (yes) {
				var selNodes = taskTree.getSelectedNodes();
				if(selNodes.length 
						&& selNodes.length > 0){			
					$.ajax({
						cache : false,
						async : true,
						url : "${ctx}/report/frame/design/cfg/delCatalog",
						dataType : 'json',
						data : {
							catalogId : selNodes[0].params.catalogId
						},
						type : "post",
						success : function(result){
							if(result != null
									&& typeof result != "undefined"){
								var flag = result.success;
								if(flag === true){
									BIONE.tip("删除成功");
									// 刷新树
									initTree();
								}else{
									if(result.msg
											&& typeof result.msg != "undefined"){
										BIONE.tip(result.msg);
									}else{								
										BIONE.tip("删除失败，请联系管理员");
									}
								}
							}
						},
						error:function(){
							BIONE.tip("删除失败，请联系系统管理员");
						}
					});
				} else {
					BIONE.tip("请选择要删除的目录节点");
				}
			}
		});
	}
</script>
</head>
<body>
	<div id="template.left.up.icon">
		<i class = "icon-guide search-size"></i>
	</div>
	<div id="template.left.up">
		<span style="font-size: 12">报表树</span>
	</div>
	<div id="template.right">
		<div id="tab" style="width: 100%; overflow: hidden;"></div>
	</div>
</body>
</html>