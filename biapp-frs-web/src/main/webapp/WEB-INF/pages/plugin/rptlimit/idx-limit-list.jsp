<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<%@ include file="/common/taglibs.jsp"%>
<html>
<meta name="decorator" content="/template/template1.jsp">
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
	var defSrc = '${defSrc}';
	var orgNo;
	
	$(function() {
		initSearchForm();
		initGrid();
		//grid.loadData();
		initToolBar();
		BIONE.addSearchButtons("#search", grid, "#searchbtn");
	});
	
	//搜索表单,应用ligerui样式
	function initSearchForm() {
		$("#search").ligerForm({
			fields : [ {
				display : "指标名称",
				name : "indexNm",
				newline : true,
				type : "text",
				cssClass : "field",
				attr : {
					field : "indexNm",
					op : "like"
				}
			}, {
				display : "指标类型",
				name : 'indexType',
				newline : false,
				type : "select",
				comboboxName:"index_type_box",
				cssClass : "field",
				attr : {
					field : 'indexType',
					op : "="
				},
				options : {  //下拉框选项
					url : "${ctx}/rpt/frame/idx/limit/indexTypeList.json?defSrc=" + defSrc  //指标类型选项
				}
			} ]
		});
	};
	
	//表格显示
	function initGrid() {
		var columnArr =[{
			display : "指标名称",
			name : "indexNm",
			width : "16%",
			align : "left",			
		}, {
			display : "指标类型",
			name : "indexType",
			width : "16%",
			align : "center",
			render: typeRender
		}];
//		if(!preview){
		columnArr.push({
			display : "机构名称",
			name : "orgNm",
			width : "13%",
			align : "center"
		},
		{
			display : "最高界限",
			name : "upperLimit",
			width : "15%",
			align : "center",
		},{
			display : "最低界限",
			name : "lowerLimit",
			width : "15%",
			align : "center",
		},{
			display : "警告界限",
			name : "warningLimit",
			width : "15%",
			align : "center",
		},{
			display : "警告方式",
			name : "warningMode",
			width : "15%",
			align : "center",
			render: modeRender
		});
//		}
		
		var  url_ = "${ctx}/rpt/frame/idx/limit/getLimitInfoList.json?indexCatalogNo="
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
			text : '增加',
			click : f_open_add,
			icon : 'add'
		}, {
			text : '修改',
			click : f_open_update,
			icon : 'modify'
		}, {
			text : '删除',
			click : f_delete,
			icon : 'delete'
		} ];
		BIONE.loadToolbar(grid, toolBars, function(){});
	}
	
	//添加
	function f_open_add() {
		if (indexCatalogNo && parent.currentNode) {
			window.parent.curCatalogName = parent.currentNode.text;
			BIONE.commonOpenSmallDialog("指标界限添加", "rptIdxLimitAddWin",
					"${ctx}/rpt/frame/idx/limit/idxInfoLimitAdd?indexCatalogNo="
					+  encodeURI(encodeURI(indexCatalogNo))+"&defSrc="+defSrc);
		} else {
			BIONE.tip("请选择一个指标");
		}
	}
	
	//修改
	function f_open_update() {
		var rows = grid.getSelectedRows();
		if (rows.length == 1) {
			BIONE.commonOpenSmallDialog("指标界限修改", "rptIdxLimitModifyWin",
					"${ctx}/rpt/frame/idx/limit/edit?indexNo=" + rows[0].indexNo +
					"&indexVerId=" + rows[0].indexVerId  + "&orgNo=" + rows[0].limit.id.orgNo);		
		}else if(rows.length > 1){
			BIONE.tip("只能选择一行进行修改");
		}else{
			BIONE.tip("请选择一条记录");
		}		
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
					var verId= "";
					//var orgId= "";
					var upperLimitId= "";
					var lowerLimitId= "";
					var warningLimitId= "";
					var warningModeId= "";
					var length = selectedRow.length;
					for ( var i = 0; i < length; i++) {
						id += selectedRow[i].indexNo + ",";
						verId += selectedRow[i].indexVerId+  ",";
						upperLimitId += selectedRow[i].upperLimit+  ",";
						lowerLimitId += selectedRow[i].lowerLimit+  ",";
						warningLimitId += selectedRow[i].warningLimit+  ",";
						warningModeId += selectedRow[i].warningMode+  ",";
					}
					$.ajax({
						type : "POST",
						url : "${ctx}/rpt/frame/idx/limit/idxLimitInfoDel?indexNos="+id
								+"&indexVerIds="+verId+"&upperLimits="+upperLimitId
								+"&lowerLimits="+lowerLimitId+"&warningLimits="+warningLimitId
								+"&warningModes="+warningModeId ,
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

	//警告模式显示转化
    function modeRender(rowdata) {
		if (rowdata.warningMode == '0') {
			return "大于";
		}
		if (rowdata.warningMode == '1') {
			return "小于";
		} else {
			return rowdata.warningMode;
		}
	}
    
	//指标类型显示转化
    function typeRender(rowdata) {
		if (rowdata.indexType == '01') {
			return "根指标";
		}else if(rowdata.indexType == '02'){
			return "组合指标";
		}else if(rowdata.indexType == '03'){
			return "派生指标";
		}else if(rowdata.indexType == '04'){
			return "泛化指标";
		}else if(rowdata.indexType == '05'){
			return "总账指标";
		}else if(rowdata.indexType == '06'){
			return "报表指标";
		}else if(rowdata.indexType == '07'){
			return "补录指标";
		}else {
			return rowdata.indexType;
		}
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