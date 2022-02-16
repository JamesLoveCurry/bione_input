<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<%@ include file="/common/taglibs.jsp"%>
<html>
<head>
<meta name="decorator" content="/template/template1_BS.jsp">
<script type="text/javascript">
	//全局变量
	var grid;
	var dialog;

	$(function() {
		var parent = window.parent;
		var catalogId = "";
		var catalogName = "";

		//初始化
		ligerSearchForm();
		ligerGrid();
		initButtons();
		BIONE.addSearchButtons("#search", grid, "#searchbtn");

		//渲染查询表单
		function ligerSearchForm() {
			$("#search").ligerForm({
				fields : [ {
					display : "数据源",
					name : "sourceId",
					newline : true,
					type : "select",
					cssClass : "field",
					attr : {
						field : "sourceId",
						op : "="
					},
					options : {
						url : "${ctx}/rpt/frame/dataset/dsList.json",
						cancelable:true
					}
				}, {
					display : "数据集名称",
					name : "dataset_box",
					newline : false,
					type : "text",
					cssClass : "field",
					attr : {
						field : "setNm",
						op : "like"
					}
				},{
					display : "数据集类型",
					name : "setType_box",
					newline : false,
					type : "select",
					cssClass : "field",
					options : {
						url : "${ctx}/rpt/frame/dataset/setTypeList.json",
						cancelable:true
					},
					attr : {
						field : "setType",
						op : "="
					}
				}]
			});
		}

		//渲染GRID
		function ligerGrid() {
			parent.datasetGrid = grid = $("#maingrid").ligerGrid({
				width : "100%",
				height : "99%",
				InWindow : true,
				columns : [ {
					display : "数据集ID",
					name : "setId",
					width : "25%"
				}, {
					display : "数据集名称",
					name : "setNm",
					width : "35%"
				}, {
					display : "数据集类型",
					name : "setType",
					sortname: "setType",
					width : "15%",
					render:function(row){
						switch(row.setType){
						case "00":
							return  "明细模型";
						case "01":
							return  "多维模型";
						case "02":
							return  "泛化模型";
						case "03":
							return  "总账模型";
						}
					}
				}, {
					display : "数据源",
					name : "dsName",
					sortname: "dsId",
					width : "10%"
				} ],
				delayLoad : true,//初始化时不加载数据
				checkbox : true,
				isScroll : true,
				userPager : true,
				rownumbers : true,
				alternatingRow : true,//附加奇偶行效果行
				colDraggable : false,
				dataAction : 'server',//从后台获取数据
				method : 'post',
				url : "${ctx}/rpt/frame/dataset/setList.json",
				sortName : 'setNm', //第一次默认排序的字段
				sortOrder : 'asc',
				toolbar : {}
			});
			//设置参数
			catalogId = "";
			catalogName = "";
			if (parent.currentNode) {
				catalogId = parent.currentNode.realId;
				catalogName = parent.currentNode.text;
			}
			grid.set('parms', {
				catalogId : catalogId,
				catalogName : catalogName,
				d : new Date().getTime()
			});
			$("input[name='catalogId_box']").val(catalogId);
			grid.loadData();
			parent.catalogBox = $("input[name='catalogId_box']");
		}

		//初始化按钮
		function initButtons() {
			var btns = [ {
				text : "添加",
				icon : "fa-plus",
				click : dataset_add,
				operNo : "data-set-add"
			}, {
				text : "修改",
				icon : "fa-pencil-square-o",
				click : dataset_modify,
				operNo : "data-set-modify"
			}, {
				text : "删除",
				icon : "fa-trash-o",
				click : dataset_delete,
				operNo : "data-set-delete"
			}, {
				text : "预览",
				icon : "fa-picture-o",
				click : dataset_preview,
				operNo : "data-set-preview"
			} ];

			BIONE.loadToolbar(grid, btns, function() {
			});
		}

		//新增
		function dataset_add(type) {
			if (parent.currentNode) {
				var curCatalogName = parent.currentNode.text;
				window.parent.curCatalogName = curCatalogName;
				dialog = window.parent.BIONE.commonOpenDialog("添加数据集",
						"datasetBox",900,$("#center").height(),
						"${ctx}/rpt/frame/dataset/infoFrame?catalogId="
								+ parent.currentNode.realId + "&show=" + "1", null);
			} else {
				parent.BIONE.tip("请选择一个目录。");
			}
		}
	
		//修改
		function dataset_modify() {
			// 20150506修改 by lujs 开始
			// 修改问题：当选择根结点时，搜索后选择数据集点击修改，提示要选择目录，无法直接修改。
			//if (parent.currentNode) {
				var rows = grid.getSelectedRows();
				if (rows.length == 1) {
					//var curCatalogName = parent.currentNode.text;
					//window.parent.curCatalogName = curCatalogName;
					dialog = window.parent.BIONE.commonOpenDialog("修改数据集",
							"datasetBox",900,$("#center").height(),
							"${ctx}/rpt/frame/dataset/infoFrame?datasetId="
									+ rows[0].setId + "&show=" + "1"
									);
				} else {
                    parent.BIONE.tip("请选择一条记录");
				}
			//} else {
			//	BIONE.tip("请选择一个目录。");
			//}
			// 
			// 20150506 修改结束
		}

		//删除
		function dataset_delete() {
			var rows = grid.getSelectedRows();
			if (rows.length > 0) {
				//拼接待删除条目ID
				parent.$.ligerDialog.confirm('确定要删除选定的数据集吗', function(flag) {
					if (flag) {
						var ids = "";
						for ( var i = 0, l = rows.length; i < l; i++) {
							ids += rows[i].setId + ",";
						}
						ids = ids.substring(0, ids.length - 1);
						$.ajax({
							type : "POST",
							url : "${ctx}/rpt/frame/dataset/deleteDataset",
							dataType : "json",
							data : {
								ids : ids
							},
							success : function(result) {
								if(result.msg){
                                    parent.BIONE.tip(result.msg);
								}else{
                                    parent.BIONE.tip("删除成功！");
									grid.loadData();
								}
							},
							error : function(XMLHttpRequest, textStatus,
									errorThrown) {
                                parent.BIONE.tip('删除失败,错误信息:' + textStatus);
							}
						});
					}
				});
			} else {
                parent.BIONE.tip('请选择要删除的记录。');
			}
		}

		//预览
		function dataset_preview() {
			var rows = grid.getSelectedRows();
			if (rows.length == 1) {
				dialog = window.parent.BIONE.commonOpenLargeDialog("数据集预览",
						"previewBox",
						"${ctx}/rpt/frame/dataset/preview?datasetId="
								+ rows[0].setId, null);
			} else {
                parent.BIONE.tip("请选择一条记录");
			}

		}
	    });
	</script>
<title>Insert title here</title>
</head>
<body>
</body>
</html>