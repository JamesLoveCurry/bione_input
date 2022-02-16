<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<%@ include file="/common/taglibs.jsp"%>
<html>
<meta name="decorator" content="/template/template5.jsp">
<head>
<style type="text/css">
</style>
<script type="text/javascript" src="${ctx}/js/numeral/numeral.js"></script>

<script type="text/javascript">

var download;
$(function(){
	downdload = $('<iframe id="download"  style="display: none;"/>');
	$('body').append(downdload);

	var grid;
	var flag = false;
	BIONE.addFormButtons({
		text: '导出报表',
		onclick : function(){
			if(!flag){
				BIONE.tip("查询出错，不允许保存");
			}else{
				var src = '';

				src = "${ctx}//report/frame/datashow/idx/resultExport";
				downdload.attr('src', src);
			}
		}
	});
	var win = parent || window;
	grid = $('#maingrid').ligerGrid({
		usePager: true,
		checkbox: false,
		dataAction : 'local',
		allowHideColumn: false,
		height: $('#center').height() - 13,
		columns: win.col,
		data : null,
		mouseoverRowCssClass : null
	});
	$.ajax({
		url: '${ctx}/report/frame/datashow/idx/search/result',
		type: 'post',
		data: {
			p: JSON2.stringify(win.req),
			indexNos : JSON2.stringify(win.indexNos),
			col : JSON2.stringify(win.colums)
		},
		dataType: 'json',
		beforeSend : function() {
			BIONE.loading = true;
			BIONE.showLoading("正在加载数据中...");
		},
		complete : function() {
			BIONE.loading = false;
			BIONE.hideLoading();
		},
		success: function(json) {
			if (json && json.isSuccess && json.isSuccess == true) {
				flag = true;
				
				var newResult = [];
				if(json.result != null && json.result.length > 0){
					for(var i=0;i<json.result.length;i++){
						
						for(var info in win.indexInfos){
							if(json.result[i][info] != null){
								if(win.indexInfos[info].dataType == "01"){
									json.result[i][info] = json.result[i][info]/win.priceUnit;
									if(win.priceUnit == 1 || win.priceUnit == 10000 ){
										var number = numeral(json.result[i][info]);
										var string = number.format('0,0');
										json.result[i][info] = string;
									}else{
										var number = numeral(json.result[i][info]);
										var string = number.format('0,0.00');
										json.result[i][info] = string;
									}
								}else if(win.indexInfos[info].dataType == "02"){
									json.result[i][info] = json.result[i][info]/win.numberUnit;
									if(win.numberUnit == 1 || win.numberUnit == 10000 ){
										var number = numeral(json.result[i][info]);
										var string = number.format('0,0');
										json.result[i][info] = string;
									}else{
										var number = numeral(json.result[i][info]);
										var string = number.format('0,0.00');
										json.result[i][info] = string;
									}
								}
								
							}
						}
						if(json.result[i].DATE && json.result[i].ORG){
							newResult.push(json.result[i]);
						}
					}
				}
				grid.set('data',{Rows: newResult,Total:newResult.length});
				$("td[columnname=DATE]:first").addClass("l-grid-hd-cell-desc");
				$("td[columnname=DATE]:first").find('div:first').find('span:first').after("<span class='l-grid-hd-cell-sort l-grid-hd-cell-sort-desc'>&nbsp;&nbsp;</span>");
				grid.changeSort('DATE', 'desc');
			} else {
				flag = false;
				BIONE.tip("后台未成功返回数据！");
			}
		},
		error : function(result, b) {
			BIONE.tip('发现系统错误 <BR>错误码：' + result.status);
		}
	});
	
});
</script>
</head>
<body>
	<div id="template.center">
		<div class="content">
			<div id="maingrid" class="maingrid"></div>
		</div>
	</div>
</body>
</html>