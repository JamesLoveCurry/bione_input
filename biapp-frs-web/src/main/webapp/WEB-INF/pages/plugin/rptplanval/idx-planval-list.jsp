<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<%@ include file="/common/taglibs.jsp"%>
<html>
<meta name="decorator" content="/template/template1_BS.jsp">
<head>
<style>
.indexStsA,.indexNmA{
     width:55%;
     cursor:pointer;
}
.stop{
    color:red;
}
</style>
<!-- 基础的JS和CSS文件 -->
<script type="text/javascript">
    //var model_column_width="19%";     //模式列表栏宽度
	var grid;
    //var preview  = '${preview}';
	var indexCatalogNo = '${indexCatalogNo}';
	var indexNo = '${indexNo}';
	var indexVerId = '${indexVerId}';
	var indexNm = '${indexNm}';
	var defSrc = '${defSrc}';
	var orgNo;
	
	$(function() {
		initSearchForm();
		initGrid();
		initToolBar();
		BIONE.addSearchButtons("#search", grid, "#searchbtn");
		window.parent.refreshObj = window;
	});
	
	//搜索表单,应用ligerui样式
	function initSearchForm() {
		$("#search").ligerForm({
			fields : [ {
				display : "对象名称",
				name : "indexNm",
				newline : true,
				type : "text",
				cssClass : "field",
				attr : {
					field : "indexNm",
					op : "like"
				}
			}, {
				display : "机构名称",
				name : 'orgNm',
				newline : false,
				type : "text",
				cssClass : "field",
				attr : {
					field : "orgNm",
					op : "like"
				}
			}, {
				display : "计划年份",
				name : 'dataDate',
				newline : false,
				type : "date",
				cssClass : "field",
				options:{
					cancelable: false,
					showType:"year",
					format: "yyyy"
				},
				attr : {
					field : "dataDate",
					op : "="
				}
			} ]
		});
		$("#search input[name=dataDate]").attr("readonly", "true");
	};
	
	//表格显示
	function initGrid() {
		var columnArr =[{
			display : "对象名称",
			name : "indexNm",
			width : "16%",
			align : "center"
		}];
		columnArr.push({
			display : "机构名称",
			name : "orgNm",
			width : "20%",
			align : "center"
		}, {
			display : "计划年份",
			name : "dataDate",
			width : "15%",
			align : "center"
		}, {
			display : "币种",
			name : "currency",
			width : "15%",
			align : "center"
		}, {
			display : "计划值",
			name : "indexVal",
			width : "15%",
			align : "center"
		});
		
		var  url_ = "${ctx}/rpt/frame/idx/planval/getPlanvalInfoList.json?indexCatalogNo="
					+ indexCatalogNo;
		if(indexCatalogNo){  //目录不为空，展示具体指标下不同机构的界限
			url_ += "&indexNo=" + indexNo + "&indexVerId=" + indexVerId;
		}
		if(defSrc){
	         url_ += "&defSrc="+defSrc;
        }
		
		grid = $("#maingrid").ligerGrid({
			toolbar : {},
			checkbox : true,
			columns : columnArr,
			dataAction : 'server',  //从后台获取数据
			usePager : true,        //服务器分页
			alternatingRow : true,  //附加奇偶行效果行
			colDraggable : true,    //是否允许表头拖拽
			url :url_ ,
			sortName : 'indexNm',   //第一次默认排序的字段
			sortOrder : 'asc',      //排序的方式
            //delayLoad : true,     //延迟加载，若为true，则初始化grid后再加上grid.loadData();
			//pageParmName : 'page',
			//pagesizeParmName : 'pagesize',
			rownumbers : true,      //是否显示行序号
			width : '100%'
		});
		grid.setHeight($("#center").height() - 40);
	};
	
	//表格上方工具栏
	function initToolBar() {
		var toolBars = [ {
			text : '增加计划值',
			click : f_open_add,
			icon : 'fa-plus'
		}, {
			text : '修改计划值',
			click : f_open_update,
			icon : 'fa-pencil-square-o'
		}, {
			text : '删除计划值',
			click : f_delete,
			icon : 'fa-trash-o'
		} ];
		BIONE.loadToolbar(grid, toolBars, function(){});
	}
	
	//添加
	function f_open_add() {
		if (indexCatalogNo && indexNo) {
			BIONE.commonOpenDialog("指标计划值添加", "rptIdxPlanvalAddWin",800, 500,
					"${ctx}/rpt/frame/idx/planval/infoFrame?indexCatalogNo="
					+ indexCatalogNo
					+"&indexNo="+indexNo
					+"&indexNm="+indexNm
					+"&editFlag=1"); 
		} else {
			BIONE.tip("请选择一个指标");
		}
	}
	
	//修改
	function f_open_update() {
		var rows = grid.getSelectedRows();
		if (rows.length == 1) {
			BIONE.commonOpenDialog("指标计划值修改", "rptIdxPlanvalModifyWin",800, 500,
					"${ctx}/rpt/frame/idx/planval/infoFrame?indexNo=" + rows[0].indexNo +
					"&dataDate=" + rows[0].dataDate  +"&currency=" + rows[0].currencyId +
					"&orgNo=" + rows[0].orgNo  + "&indexVal=" + rows[0].indexVal +"&editFlag=2");		
		}else if(rows.length > 1){
			BIONE.tip("只能选择一行进行修改");
		}else{
			BIONE.tip("请选择一条记录");
		}		
	}
	
	//导入后刷新
	function reloadGrid(){
		grid.loadData();
	}
	
	//删除
	 function f_delete() {
		var selectedRow = grid.getSelectedRows();
		if (selectedRow.length == 0) {
			BIONE.tip('请选择行');
			return;
		}
		window.parent.$.ligerDialog.confirm('确实要删除这' + selectedRow.length + '条记录吗!',
			function(yes) {
				if (yes) {
					var id = "";
					var dataDateId= "";
					var orgId= "";
					var currencyId= "";
					var indexValId= "";
					var length = selectedRow.length;
					for ( var i = 0; i < length; i++) {
						id += selectedRow[i].indexNo + ",";
						dataDateId += selectedRow[i].dataDate +  ",";
						orgId += selectedRow[i].orgNo+  ",";
						currencyId += selectedRow[i].currencyId+  ",";
						indexValId += selectedRow[i].indexVal+  ",";
					}
					$.ajax({
						type : "POST",
						url : "${ctx}/rpt/frame/idx/planval/idxPlanvalInfoDel?indexNos="+id
								+"&dataDates="+dataDateId+"&orgNos="+orgId+"&currencys="+currencyId+"&indexVals="+indexValId,
						dataType : 'json',
						success : function(result) {
							if (result.msg) {
								if(result.msg=='0'){
									BIONE.tip("删除成功");
									grid.loadData();    //重新导入数据
								}else{
									BIONE.tip(result.msg);
								}
							} else {
								BIONE.tip("删除失败");
							}
						},
						beforeSend : function() {
		 					parent.BIONE.loading = true;
		 					parent.BIONE.showLoading("正在操作中...");
		 				},
		 				complete : function() {
		 					parent.BIONE.loading = false;
		 					parent.BIONE.hideLoading();
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