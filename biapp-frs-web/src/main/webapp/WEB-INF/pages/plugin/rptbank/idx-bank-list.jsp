<%@ page language="java" pageEncoding="UTF-8"%>
<%@ include file="/common/taglibs.jsp"%>
<html>
<meta name="decorator" content="/template/template1.jsp">
<head>
<!-- 基础的JS和CSS文件 -->
<script type="text/javascript">
	var grid=null;
	var themeId = "${themeId}";
	
	$(function() {
		initGrid();
		initSearchForm();
		initToolBar();
	});
	
	function initSearchForm() {
		//搜索表单应用ligerui样式
		$("#search").ligerForm({
			fields :[ {
				name : "indexNm",
				display : "指标名称",
				type : "text",
				attr : {
					field : 'info.indexNm',
					op : "like"
				}
			}]
		});
		BIONE.addSearchButtons("#search", grid, "#searchbtn");
	};
	
	
	function initGrid() {
		grid = $("#maingrid").ligerGrid({
			toolbar : {},
			checkbox :false,
			columns :[{
				display : '指标名称',
				name : 'indexNm',
				width : '20%',
				align : 'left',
				sortname : "info.indexNm"
			}, {
				display : '主指标名称',
				name : 'mainNm',
				width : '25%',
				align : 'left',
				sortname : "idx1.indexNm"
			}, {
				display : '分指标名称',
				name : 'partNm',
				width : '25%',
				align : 'left',
				sortname : "idx2.indexNm"
			}, {
				display : '币种',
				name : 'currency',
				width : '20%',
				align : 'left',
				sortname : "idx2.currency"
			}],
			dataAction : 'server', //从后台获取数据
			alternatingRow : true, //附加奇偶行效果行
			colDraggable : true,
			sortName : 'info.orderNum',//第一次默认排序的字段
			sortOrder : 'asc', //排序的方式
			url : "${ctx}/frame/idx/bank/getBankInfoByTheme?themeId="+themeId,
			rownumbers : true,
			width : '99.8%',
			usePager : false,
			tree : { columnName: 'indexNm' },
			autoCheckChildren : false
		});
	};
	
	function initToolBar() {
		var toolBars = [ {
			text : '增加根指标',
			click : r_addRoot,
			icon : 'add'
		}, {
			text : '增加子指标',
			click : r_addChild,
			icon : 'add'
		}, {
			text : '修改',
			click : r_update,
			icon : 'modify'
		}, {
			text : '删除',
			click : r_delete,
			icon : 'delete'
		}, {
			icon:'database',
			text:'数据处理',
			menu:{
				width:90,
				items:[{
					icon : 'export',
					text : '模板导出',
					click : themeExport
				},{
					icon : 'import',
					text : '模板导入',
					click : themeImport
				} ]
			}
		}];
		BIONE.loadToolbar(grid, toolBars, function() {});
	}
	
	//模板导入
	function themeImport(){
		BIONE.commonOpenDialog("主题信息模板导入", "importWin", 600, 480,
				"${ctx}/report/frame/wizard?type=RptBank");
	}
	
	//模板导出
	function themeExport(){
		var type = 'RptBank';
		$.ajax({
			async:true,
			type:"POST",
			dataType:"json",
			url:"${ctx}/report/frame/wizard/download",
			data:{type:type,ids:themeId},
			beforeSend : function(a, b, c) {
				BIONE.showLoading('正在导出数据中...');
			},
			success:function(data){
				BIONE.hideLoading();
				if(data.fileName==""){
					BIONE.tip('导出异常');
					return;
				}
				var src='${ctx}/report/frame/wizard/export?type='+type+'&fileName='+data.fileName;//导出成功后的excell文件地址
				window["downdload"] = $('<iframe id="download"  style="display: none;"/>');//下载文件显示框
				$('body').append(downdload);//添加文件显示框在body的下方
				downdload.attr('src', src);//给下载文件显示框加上文件地址链接
			},
			error : function(result) {
				BIONE.hideLoading();
			}
		});
	}
	
	function reload(){
		grid.reload();
	}
	
	function r_addRoot() {
		BIONE.commonOpenDialog("新增指标信息", "editBankWin", 600, 320, "${ctx}/frame/idx/bank/editBank?themeId="+themeId+"&upNo=0");
	}
	function r_addChild() {
		var row = grid.getSelectedRow();
		if(row == null){
			BIONE.tip("请选择一个上级指标");
			return;
		}
		BIONE.commonOpenDialog("新增指标信息", "editBankWin", 600, 320, "${ctx}/frame/idx/bank/editBank?themeId="+themeId+"&upNo="+row.id.indexId+"&upIndexLevel="+row.indexLevel);
	}
	function r_update() {
		var row = grid.getSelectedRow();
		if(row == null){
			BIONE.tip("请选择指标");
			return;
		}
		BIONE.commonOpenDialog("修改指标信息", "editBankWin", 600, 320, "${ctx}/frame/idx/bank/editBank?indexId="+row.id.indexId+"&themeId="+themeId+"&currNm="+row.currency);
	}
	function  r_delete(){
		var row = grid.getSelectedRow();
		if(row == null){
			BIONE.tip("请选择指标");
			return;
		}
		$.ligerDialog.confirm("确定删除指标 [ " + row.indexNm + " ]及其下级指标吗 ？",
				function(flag) {
					if (flag) {
						$.ajax({
							type : "POST",
							url : "${ctx}/frame/idx/bank/deleteIdx?indexId="+ row.id.indexId+"&themeId="+themeId,
							success : function(res) {
									BIONE.tip("删除成功！");
									grid.loadData();
							},
							error : function(XMLHttpRequest, textStatus,
									errorThrown) {
								BIONE.tip('删除失败,错误信息:' + textStatus);
							}
						});
					}
				});
	}
</script>
</head>
<body>

	<div id="template.right.down">
		<div id="aaa">
			<div id="maingrid" style="margin-top: 60px;"></div>
		</div>
	</div>
</body>
</html>