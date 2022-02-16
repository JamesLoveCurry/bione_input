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
	var catalogId = "";
	var catalogName = "";
	var indexCatalogNo = '${indexCatalogNo}';
	var indexNo = '${indexNo}';
	var indexVerId = '${indexVerId}';
	var indexNm = '${indexNm}';
	var defSrc = '${defSrc}';
	
	$(function() {	
		//初始化
		searchForm();
		initGrid();
		initToolbar();
		BIONE.addSearchButtons("#search", grid, "#searchbtn");
		window.parent.refreshObj2 = window;
	});
	
	function searchForm() {
		$("#search").ligerForm({
			fields : [ {
				display : '校验名称',
				name : 'checkNm',
				newline : true,
				type : "text",
				attr : {
					field : 'checkNm',
					op : "like"
				}
			}, {
				display : '对象指标',
				name : 'indexNm',
				newline : false,
				type : "text",
				attr : {
					field : 'indexNm',
					op : "like"
				}
			}, {
				display : '对象度量',
				name : 'measureNm',
				newline : false,
				type : "text",
				attr : {
					field : 'measureNm',
					op : "like"
				}
			}, {
				display : '比较值类型',
				name : "compareValType",
				newline : true,
				type : "select",
				cssClass : "field",
				options : {
					data : [{"id":"00","text":"常量"},{"id":"01","text":"上日"},
					        {"id":"02","text":"月初"},{"id":"03","text":"上月末"},
					        {"id":"04","text":"上月同期"},{"id":"05","text":"季初"},
					        {"id":"06","text":"上季末"},{"id":"07","text":"年初"},
					        {"id":"08","text":"上年末"},{"id":"09","text":"上年同期"}]
				},
				attr : {
					field : 'compareValType',
					op : "="
				}
			},{
				display : '幅度类型',
				name : "rangeType",
				newline : false,
				type : "select",
				cssClass : "field",
				options : {
					data :  [{"id":"01","text":"数字"},{"id":"02","text":"百分比"}]
				},
				attr : {
					field : 'rangeType',
					op : "="
				}
			}  ]
		});
		
	}

	function initGrid() {
		var  url_ = "${ctx}/report/frame/rptvalid/warn/list.json?indexCatalogNo="
			+ indexCatalogNo;
		if(indexCatalogNo){  //目录不为空，展示具体指标下不同机构的界限
			url_ += "&indexNo=" + indexNo + "&indexVerId=" + indexVerId;
		}
		if(defSrc){
			url_ += "&defSrc="+defSrc;
			}
		grid = $("#maingrid").ligerGrid(
				{
					// height:"80%",
					width : "100%",
					columns : [{
						display : '校验名称',
						name : 'checkNm',
						width : '15%',
						align : 'center'
					}, {
						display : '对象名称',
						name : 'indexNm',
						width : '15%',
						align : 'center'
					}, {
						display : '比较值类型',
						name : 'compareValType',
						width : '15%',
						align : 'center',
						render : function(data) {
							if (data.compareValType == "00")
								return "常量";
							if (data.compareValType == "01")
								return "上日";
							if (data.compareValType == "02")
								return "月初";
							if (data.compareValType == "03")
								return "上月末";
							if (data.compareValType == "04")
								return "上月同期";
							if (data.compareValType == "05")
								return "季初";
							if (data.compareValType == "06")
								return "上季末";
							if (data.compareValType == "07")
								return "年初";
							if (data.compareValType == "08")
								return "上年末";
							if (data.compareValType == "09")
								return "上年同期";
							
						}
					}, {
						display : '幅度类型',
						name : 'rangeType',
						width : '15%',
						align : 'center',
						render : function(data) {
							if (data.rangeType == "01")
								return "数字";
							if (data.rangeType == "02")
								return "百分比";
						}
					}, {
						display : '开始时间',
						name : 'startDate',
						width : '15%',
						align : 'center'
					}, {
						display : '结束时间',
						name : 'endDate',
						width : '15%',
						align : 'center'
					}],
					usePager : true,
					checkbox : true,
					dataAction : 'server', //从后台获取数据
					alternatingRow : true, //附加奇偶行效果行
					url : url_,
					method : 'post', // get
					sortName : 'indexNm', //第一次默认排序的字段
					sortOrder : 'asc', //排序的方式
					rownumbers : true,
					toolbar : {}
				});

	}
	function reloadGrid(){
		grid.set('parms', {
			condition : ''
		});
		var  url_ = "${ctx}/report/frame/rptvalid/warn/list.json?indexCatalogNo="
			+ indexCatalogNo;
		if(indexCatalogNo){  //目录不为空，展示具体指标下不同机构的界限
			url_ += "&indexNo=" + indexNo + "&indexVerId=" + indexVerId;
		}
		if(defSrc){
			url_ += "&defSrc="+defSrc;
			}
		grid.set('url',url_);
	}

	function initToolbar() {
		items = [{
					text : '增加警示校验',
					click : function() {
						if(indexCatalogNo && indexNo){
							BIONE.commonOpenDialog('警示校验新增',
									'warnAdd', 800, 500,
									"${ctx}/report/frame/rptvalid/warn/infoFrame?indexCatalogNo="
									+ indexCatalogNo
									+"&indexNo="+indexNo
					                +"&indexNm="+indexNm
					                +"&editFlag=1");
							}else{
								BIONE.tip("请选择一个指标");
							}

					},
					icon : 'fa-plus'
				},
				{
					text : '修改警示校验',
					click : function() {
						var selected = grid.getSelectedRows();
						if (!selected||selected.length == '0') {
							BIONE.tip("请先选择需要修改的校验");
							return;
						}
						if(selected.length > 1){
							BIONE.tip("至多选择一个校验公式");
							return ;
						}
						window.parent.BIONE.commonOpenDialog("警示校验修改", "warnEdit",
								800, 500,
								"${ctx}/report/frame/rptvalid/warn/infoFrame?indexCatalogNo="
									+ indexCatalogNo
									+"&indexNo="+selected[0].indexNo
									+"&checkId=" + selected[0].checkId
									+"&editFlag=2" );
					},
					icon : 'fa-pencil-square-o'
				}, {
					text : '删除警示校验',
					click : f_delete,
					icon : 'fa-trash-o'
				} ];
		BIONE.loadToolbar(grid, items, function() {
		});
	}
	
	function f_delete(){
		var selected = grid.getSelectedRows();
		if (!selected||selected.length == '0') {
			BIONE.tip("请先选择需要删除的校验");
			return;
		}
		$.ligerDialog
		.confirm(
				"确定删除这些校验吗？",
				function(flag) {
					if (flag) {
						var ids = "";
						for(var i=0;i<selected.length;i++){
							ids += selected[i].checkId + ",";
						}
						
						$.ajax({
									type : "POST",
									url : "${ctx}/report/frame/rptvalid/warn/" + ids,
									success : function() {
										BIONE.tip("删除成功！");
										reloadGrid();
									},
									error : function(XMLHttpRequest,
											textStatus, errorThrown) {
										BIONE.tip('删除失败,错误信息:'
												+ textStatus);
									}
								});
					}
				});
	}
</script>
<title>Insert title here</title>
</head>
<body>
</body>
</html>