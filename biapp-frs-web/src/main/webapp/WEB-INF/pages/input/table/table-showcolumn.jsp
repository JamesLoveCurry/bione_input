<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<%@ include file="/common/taglibs.jsp"%>
<html>
<meta name="decorator" content="/template/template14.jsp">
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
	var grid;
	var ids;
    var dsId  ='${dsId}';
	var tableNm='${tableNm}';

	$(function() {	
		window.parent.fieldMgr = window;
		initGrid();
		loadData();
	});
	
	function loadData(){
		grid.loadData();
	}
	
	function initGrid() {
		var  columnArr  =[{
			display : '列名',
			name : 'columnNm',
			width : "25%",
			align : 'left'		
		},  {
			display : '类型',
			name : 'dataType',
			width : "20%",
			align : 'center'
		},  {
			display : '是否为空',
			name : 'nullAble',
			width : "10%",
			align : 'center',
			render : function(rowData,rowNum,value){
				var text = "是";
				if(value=="N"){
					text = "否";
					if(rowData.isprimary=="1")
						text = text +"(主键)";
				}
				return text;
			}
		},  {
			display : '备注',
			name : 'comments',
			width : "45%",
			align : 'center'
		}];
		grid = $("#maingrid").ligerGrid({
			checkbox : false,
			columns : columnArr,
			dataAction : 'server', //从后台获取数据
			alternatingRow : true, //附加奇偶行效果行
			url : "${ctx}/rpt/input/table/getColumnInfoByDsAndTable.json?dsId="+dsId+"&tableNm="+tableNm,
//			sortName : 'columnName',//第一次默认排序的字段
//			sortOrder : 'asc', //排序的方式
            delayLoad:true,
			rownumbers : false,
			width : '100%',
			height: '95%',
			usePager : false
		});
	};
	
	function getColumnInfo(){
		var rows = grid.rows;
		if(rows.lengt==0)
			return [];
		var rs = [];
		for(var i = 0 ;i <rows.length;i++){
			rs.push({fieldEnName : rows[i].columnNm,
					 fieldType : rows[i].dataType,
					 fieldLength : rows[i].dataLength,
					 allowNull : rows[i].nullAble=="Y"?true:false,
					 decimalLength : rows[i].dataScan,
					 comments  : rows[i].comments,
					 dataPercision : rows[i].dataPercision,
					 dataScan : rows[i].dataScan,
					 isprimary : rows[i].isprimary
				});
		}
		return rs;
	}
</script>
</head>
<body>

	<div id="template.center">
		<div id="maingrid" ></div>
	</div>
</body>
</html>