<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<%@ include file="/common/taglibs.jsp"%>
<html>
<head>
<meta name="decorator" content="/template/template5.jsp">
<%@ include file="/common/codemirror_load.jsp"%>
<script>
var sqlEdit = null;
var sqlgrid = null;
$(function(){
	if(window.parent.paramId != ""){
		initSqlGrid();
	}
	else{
		$("#sqlgrid").hide();
	}
	initSql();
	initBtn();
});

function initSql(){
	var mime="text/x-mysql";  
	sqlEdit = CodeMirror.fromTextArea(document.getElementById('sql'), {  
        mode: mime,  
        indentWithTabs: true,  
        smartIndent: true,  
        lineNumbers: true,  
        matchBrackets : true,  
        autofocus: true  
     });  
	$(".CodeMirror.cm-s-default").css("width","99%").css("height",$("#center").height()-20).css("padding-bottom","0px");
	sqlEdit.setValue(window.parent.sqlEdit.getValue());
}

function initSqlGrid(){
	sqlgrid = $('#sqlgrid').ligerGrid(
			{
				width : '18%',
				columns : [{
					display : '查询参数',
					name : 'text',
					width : "49%",
					align : 'center'
				},{
					display : '查询标识',
					name : 'id',
					width : "49%",
					align : 'center'
				}],
				usePager : false,
				checkbox : false,
				dataAction : 'server',
				allowHideColumn : false,
				delayLoad : false,
				url : "${ctx}/report/frame/param/templates/getParamGird.json?paramTmpId="+window.parent.paramId,
				onDblClickRow : function(data,rowindex,rowObj) {
					var pos = sqlEdit.getCursor();
					var value = sqlEdit.getValue();
					var vals = value.split("\n");
					var info = vals[pos.line];
					info = info.substring(0,pos.ch)+"#"+data.id+"#"+info.substring(pos.ch,info.length);
					vals[pos.line] = info;
					value =vals.join("\n");
					sqlEdit.setValue(value);
					pos.ch +=  ("#"+data.id+"#").length;
					sqlEdit.setCursor(pos);
					//sqlEdit.setFocus();
				},
				onAfterChangeColumnWidth : function() {
				},
				onAfterShowData : function() {
				},
				onBeforeChangeColumnWidth : function() {
				},
				mouseoverRowCssClass : null
			});
	sqlgrid.setHeight($("#center").height()-20);
}
function initBtn(){
	var btns = [{
		text : '取消',
		onclick : function() {
			BIONE.closeDialog("queryWin");
		}
	},{
		text : '确认',
		onclick : function(){
			window.parent.sqlEdit.setValue(sqlEdit.getValue());
			BIONE.closeDialog("queryWin");
		}
	}];
	BIONE.addFormButtons(btns);
	
}
</script>
</head>
<body>
<div id = "template.center">
	<div id="sqlDiv">
		<div id="sqlgrid" style="width :18%;float:left;"></div>
		<div id="sqlEdit" style="background: #fff;  overflow:hidden;">
			<textarea id="sql" style="width:99%;height:99%"></textarea>
		</div>
	</div>
</div>
</body>
</html>