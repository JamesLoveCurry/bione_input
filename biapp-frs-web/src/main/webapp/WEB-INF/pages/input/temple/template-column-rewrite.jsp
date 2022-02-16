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
	var templateId = '${templateId}';
	var tableNm='${tableNm}';

	$(function() {
		window.parent.fieldMgr = window;
		initGrid();
		loadData();
	});
	
	function loadData(){
		grid.loadData();
	}
	
	function on_selectField(rowData,rowNum,colData){
		//grid.getSelected();
		window.parent.parent.BIONE.commonOpenDialog('选择更新字段', 'selectFiledBox', '600', '480',
				'${ctx}/rpt/input/temple/selectField?templateId='+templateId+"&dataType="+rowData.dataType, null, function(
						data) {
					grid.endEdit();
					if (!data)
						return;
					rowData.updFieldName = data.fieldEnName;
					rowData.updFieldType = data.fieldType;
					rowData.updFieldLength = data.fieldLength;
					rowData.updateDecimalLength = data.decimalLength;
					grid.updateRow(rowData, rowData);
		});
	}
	
	function initGrid() {
		var  columnArr  =[{
			display : '主键',
			name : 'isprimary',
			width : "10%",
			align : 'left',
			render : function(rowData,rowNum,data){
				return "<input style='margin-top:5px;' onclick='checkPrimary(\""
				+ rowData.__id
				+ "\",this);' type='checkbox' "
				+ ((rowData.isprimary == "1") ? "checked='checked'"
						: "") + "/>";
			}
		},{
			display : '可为空',
			name : 'nullAble',
			width : "10%",
			align : 'left',
			render : function(rowData,rowNum,data){
				if(data =="YES")
					return "可为空";
				if(data=="NO")
					return "不为空";
			}
		},{
			display : '列名',
			name : 'columnNm',
			width : "30%",
			align : 'left'		
		}/*, {
			display : '是否主键',
			name : 'isprimary',
			width : "30%",
			align : 'left'	,
			render : function(row){
			}
		},  {
			display : '表类型',
			name : 'dataType',
			width : "30%",
			align : 'center'
		}*/,  {
			display : '选择字段',
			name : 'updFieldName',
			width : "30%",
			align : 'center',
			editor : {
				type : "select",
				ext : on_selectField
			},
			render : function(rowData){
				return rowData.updFieldName;
			}
		},  {
			display : '',
			width : "9%",
			align : 'center',
			render : function(rowData){
				return "<a href='javascript:void(0)' class='link' onclick='clearSelect(\""+ rowData.__id + "\")'>"+"清空"+"</a>";
			}
		},  {
			display : '',
			width : "9%",
			align : 'center',
			render : function(rowData){
				return "<a href='javascript:void(0)' class='link' onclick='deleteRow(\""+ rowData.__id + "\")'>"+"删除"+"</a>";
			}
		}];
		grid = $("#maingrid").ligerGrid({
			checkbox : false,
			columns : columnArr,
			dataAction : 'server', //从后台获取数据
			alternatingRow : true, //附加奇偶行效果行
			url : "${ctx}/rpt/input/table/getColumnInfoByDsAndTable.json?dsId="+dsId+"&tableNm="+tableNm+"&templeId="+templateId,
			sortName : 'TABLE_NM',//第一次默认排序的字段
			sortOrder : 'asc', //排序的方式
            delayLoad:true,
			rownumbers : false,
			width : '98%',
			height: '98%',
			enabledEdit : true,
			clickToEdit : true,
			usePager : false
		});
	};
	
	function getColumnInfo(){
		var rows = grid.rows;
		if(rows.lengt==0)
			return [];
		var rs = [];
		var keycont =0;
		for(var i = 0 ;i <rows.length;i++){
			if(rows[i].nullAble=="NO"&&(rows[i].updFieldName==null||rows[i].updFieldName==""))
			{
				BIONE.tip("字段:"+rows[i].columnNm+"不可为空");
				return;
			}
			if(rows[i].isprimary=="1")
			{
				keycont = keycont+1;
			}
			
			rs.push({fieldEnName : rows[i].columnNm,
					 fieldType : rows[i].dataType,
					 fieldLength : rows[i].dataLength,
					 allowNull : rows[i].nullAble=="YES"?"0":"1",
					 decimalLength : rows[i].dataScan,
					 comments  : rows[i].comments,
					 dataPercision : rows[i].dataPercision,
					 dataScan : rows[i].dataScan,
					 isprimary : rows[i].isprimary,
					 updFieldName : rows[i].updFieldName,
					 updFieldType : rows[i].updFieldType,
					 updFieldLength : rows[i].updFieldLength,
					 updateDecimalLength : rows[i].updateDecimalLength
				});
		}
		if(keycont==0)
		{
			BIONE.tip("请设置主键");
			return;
		}
		return rs;
	}
	
	function checkPrimary(rowId,ck){
		var row = grid.getRow(rowId);
		row.isprimary = ck.checked ? "1": "0";
	}
	
	function clearSelect(rowId){
		var rowData = grid.getRow(rowId);
		rowData.updFieldName = "";
		rowData.updFieldType = "";
		rowData.updFieldLength = "";
		rowData.updateDecimalLength = "";
		grid.updateRow(rowData, rowData);
	}
	function deleteRow(rowId)
    {
    	grid.deleteRow(rowId);
    }
	
</script>
</head>
<body>

	<div id="template.center" >
		<div id="maingrid" class="maingrid"></div>
	</div>
</body>
</html>