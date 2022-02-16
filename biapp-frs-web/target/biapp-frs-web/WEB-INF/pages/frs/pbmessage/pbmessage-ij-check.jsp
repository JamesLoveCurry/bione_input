<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ include file="/common/taglibs.jsp"%>
<html>
<head>
<meta name="decorator" content="/template/template14.jsp">
<!-- 基础的JS和CSS文件 -->
<style>
.singlebox,#allSelect{
  margin-top:5px;
}
.tipptitle{
  margin-left:24px;
  text-align:left
}

.tipp{
  margin-left:40px;
  text-align:left
}

#fdiv{
 margin-top:5% 
}
</style>
<script type="text/javascript">
	$(function() {
		initGrid();
    });
	
	function initGrid(){
		grid = $("#maingrid").ligerGrid({
			height : '99%',
			width : '99%',
			columns : [ {
				display : '报表编号',
				name : 'rptCode',
				width : '10%',
				align: 'left'
			},  {
				display : '报表版本号',
				name : 'verId',
				width : '8%'
			}, {
				display : '单元格编号',
				name : 'cellNo',
				width : '10%'
			}, {
				display : '单元格名称',
				name : 'cellNm',
				width : '15%'
			}, {
				display : '人行指标编码',
				name : 'pbcCode',
				width : '10%'
			}, {
				display : '数据属性',
				name : 'dataAttr',
				width : '10%',
				render: function(a,b,c){
					switch(c){
					case "1":
						return '余额';
					case "2":
						return '发生额';
					}
				}
			}, {
				display : '币种属性',
				name : 'currtype',
				width : '12%',
				render: function(a,b,c){
					switch(c){
					case "CNY0001":
						return '人民币(CNY0001)';
					case "USD0002":
						return '美元(USD0002)';
					case "BWB0001":
						return '本外币(BWB0001)';
					}
				}
			}],
			url : "${ctx}/frs/pbmessage/getCheckCode",
			clickToEdit : true,
			alternatingRow : false,
			usePager: false
		});
	}
</script>
</head>
<body>
	<div id="template.center">
		<div id="tipAreaDiv" style="width: 99.8%;float: left;padding-top: 10px;padding-bottom: 20px;background-color: #FFFEE6;border: solid 1px #D0D0D0;">
			<div style="padding-left: 60px;">
				<div style="width: 24px; height: 16px; float: left; background-image: url('${ctx}/images/classics/icons/lightbulb.png'); background-attachment: scroll; background-repeat: no-repeat; background-position-x: 0%; background-position-y: 0%; background-size: auto; background-origin: padding-box; background-clip: border-box; background-color: transparent;">
				</div>
					<p>失效配置列表</p>
					<p class="tipp">以下列表展示失效的[生成报文配置]信息。</p>
					<p class="tipp">如果列表无记录，表示不存在失效的配置信息，如果列表存在记录，需要对失效的配置信息进行重新[初始化配置]操作。</p>
					<p class="tipptitle">存在失效配置的影响：</p>
					<p class="tipp">当存在失效配置信息，会导致[生成报文]时，J文件无法生成对应失效单元格及版本的数据。</p>
					<p class="tipptitle">出现失效配置的情况：</p>
					<p class="tipp">当在[通用报表定制]中 对[生成报文配置]中报表的单元格 进行修改或删除版本及数据源信息时，会出现失效的[生成报文配置]信息。</p>
			</div>
		</div>
		<div id="cdiv" style="width: 99.8%;float: left;padding-top: 5px;">
			<div id="maingrid" class="maingrid"></div>
		</div>
	</div>
</body>
</html>