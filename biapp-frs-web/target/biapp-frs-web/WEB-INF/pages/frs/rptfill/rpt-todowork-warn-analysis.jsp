<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<%@ include file="/common/taglibs.jsp"%>
<html>
<head>
<meta name="decorator" content="/template/template1_BS.jsp">
<script type="text/javascript"
	src="${ctx}/js/bignumber/bignumber.min.js"></script>
<!-- 基础的JS和CSS文件 -->
<script type="text/javascript">
	var rptId = '${rptId}';
	var orgNo = '${orgNo}';
	var dataDate = '${dataDate}';
	var orgType = '${orgType}';
	var templateId = '${templateId}';
	var verId = '${verId}';
	var curColId = ""; //分析模式-当前选择的指标列
	var colIds = []; //分析模式-报表指标列
	var grid;
	$(function() {
		initIndexCol();
		initSearchForm();
		//设置初始值
		$.ligerui.get("indexNoBox").selectValue(colIds[0].id);
		initGrid();
		initButtons();
		BIONE.addSearchButtons("#search", grid, "#searchbtn");
	});

	function initButtons() {
		var btns = [ {
			text : '导出',
			click : oper_fdown,
			icon : 'fa-download',
			operNo : 'oper_fdown'
		} ]
		BIONE.loadToolbar(grid, btns, function() {
		});
	};

	function oper_fdown() {
		var download;
		var flag = false;

		BIONE.tip("正在导出数据中...");
		var src = '';
		src = "${ctx}/rpt/frs/rptfill/anlysisExp?rptId="+rptId+"&templateId="
				+ templateId + "&orgNo=" + orgNo + "&dataDate=" + dataDate + "&colId=" + curColId;
		downdload = $('<iframe id="download"  style="display: none;"/>');
		$('body').append(downdload);
		downdload.attr('src', src);
	}

	function initIndexCol(){
		$.ajax({
			cache : false,
			async : false,
			url : '${ctx}/rpt/frs/rptfill/getIndexCol',
			dataType : 'json',
			type : "get",
			data : {
				templateId : templateId,
				verId : verId
			},
			success : function(result) {
				if(result){
					colIds = result;
				}
			}
		});
	}

	function initSearchForm() {
		$("#search").ligerForm({
			fields : [{
				display : "选择指标列",
				name : "colId",
				comboboxName : 'indexNoBox',
				newline : true,
				type : "select",
				width : '140',
				options : {
					initValue :  colIds[0].id,
					url : '${ctx}/rpt/frs/rptfill/getIndexCol?templateId='+templateId+"&verId="+verId,
					onSelected : function(id, text){
						if(id != "" && grid){
							curColId = id;
							reloadGrid(id);
						}
					}
				}
			}
			]
		});

	};

	function initGrid() {
		var columns = [];
		$.ajax({
			cache : false,
			async : false,
			url : '${ctx}/rpt/frs/rptfill/getColumns',
			dataType : 'json',
			type : "post",
			data : {
				templateId : templateId,
				dataDate : dataDate
			},
			success : function(data) {
				if(data){
					for ( var i = 0; i < data.length; i++) {
						columns.push({
							display : data[i].display,
							name : data[i].name,
							width : null == data[i].width ? '10%' : data[i].width,
							render : function (row, index, value){
							    if("errorConmtent" == data[i].name){
                                    return "<a href='javascript:void(0)' style='color :blue' class='link' >"+value+"</a>";
                                }
								if(null == value || "" == value){
									return value;
								}
								var key = findKey(row, value);
								if("diff" == key.substr(0,4)){
									var colorkey = "color"+key;
									if("1" == row[colorkey]){
										return "<div style='width:100%;height:100%;background:red;'>"+value+"</div>";
									} else if ("0" == row[colorkey]) {
										return "<div style='width:100%;height:100%;background:green;'>"+value+"</div>";
									} else {
										return value;
									}
								}
								return value;
							}
						});
					}
				}
			}
		});

		grid = $("#maingrid").ligerGrid({
			toolbar : {},
			width : '100%',
			height : '99%',
			columns : columns,
			checkbox : false,
			rownumbers : true,
			isScroll : true,
			alternatingRow : true,//附加奇偶行效果行
			usePager : false,
			dataAction : 'server',//从后台获取数据
			method : 'post',
			url : '${ctx}/rpt/frs/rptfill/changeAnalysis?rptId=' + rptId + "&orgNo=" + orgNo
					+ "&dataDate=" + dataDate + "&colId=" + colIds[0].id
		});
	};

	function findKey (obj, value, compare = (a, b) => a === b) {
		return Object.keys(obj).find(k => compare(obj[k], value))
	}

	function reloadGrid(curColId){
		grid.setParm('newPage', 1);
		grid.set("url","${ctx}/rpt/frs/rptfill/changeAnalysis?rptId=" + rptId + "&orgNo=" + orgNo
				+ "&dataDate=" + dataDate + "&colId=" + curColId);
	}
</script>
</head>
<body>
</body>
</html>