<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<%@ include file="/common/taglibs.jsp"%>
<html>
<meta name="decorator" content="/template/template14.jsp">
<head>
<style type="text/css">
.detail-table {
	margin: 2px 0;
	width: 100%;
}
.detail-table .text {
	height: 70px;
}
.detail-table td {
	vertical-align: middle;
	text-align: center;
	height: 30px;
	border: 1px solid #BEC0C2;
}
.detail-table .title {
	background-color: #E6E8EB;
}
.detail-table .col1 td {
	text-align: left;
}
.detail-table .col1 .title {
	padding-left: 10px;
}
.detail-table .col4 .title {
	width: 16%;
}
.detail-table .col4 .value {
	width:34%;
}
</style>
<script type="text/javascript">
	var indexType = {
			"01": "根指标",
			"02": "组合指标",
			"03": "派生指标",
			"04": "泛化指标",
			"05": "总账指标"
	},
	dataType = {
			"01": "金额",
			"02": "数值",
			"03": "比例"
	},
	calcCycle = {
			"01": "日",
			"02": "月",
			"03": "季",
			"04": "年"
	},
	indexSts = {
			"Y": "启用",
			"N": "停用"
	},
	dept = {
			"01": "计划财务部",
			"02": "资产负债部",
			"03": "会计部",
			"04": "资产托管部",
			"05": "公司银行部",
			"06": "国际业务部",
			"07": "投资银行部",
			"08": "集团客户部",
			"09": "机构业务部",
			"10": "理财业务管理部",
			"11": "零售银行部",
			"12": "财富管理与私人银行部",
			"13": "消费金融部",
			"14": "小企业金融部",
			"15": "信用卡中心",
			"16": "网络银行部",
			"17": "合规部",
			"18": "审计部",
			"19": "风险管理部",
			"20": "授信审批部",
			"21": "信贷管理部",
			"22": "法律保全部",
			"23": "金融市场部",
			"24": "金融同业部",
			"25": "运营管理部",
			"26": "零售板块"
	};
	$(function() {
		$.ajax({
			url: '${ctx}/report/frame/datashow/idx/detail/info.json',
			type: 'post',
			data: {
				idxNo: '${idxNo}',
				verId: '${verId}'
			},
			dataType: 'json',
			success: function(json) {
				json.indexType = indexType[json.indexType] || '';
// 				switch(json.indexType){
// 					case "01":json.indexType = "根指标";break;
// 					case "02":json.indexType = "组合指标";break;
// 					case "03":json.indexType = "派生指标";break;
// 					case "04":json.indexType = "泛化指标";break;
// 					case "05":json.indexType = "总账指标";break;
// 					default : json.indexType = "";
// 				}
				json.dataType = dataType[json.dataType] || '';
// 				switch(json.dataType){
// 					case "01":json.dataType = "金额";break;
// 					case "02":json.dataType = "数值";break;
// 					case "03":json.dataType = "比例";break;
// 					default : json.dataType = "";
// 				}
				json.calcCycle = calcCycle[json.calcCycle] || '';
// 				switch(json.calcCycle){
// 					case "01":json.calcCycle = "日";break;
// 					case "02":json.calcCycle = "月";break;
// 					case "03":json.calcCycle = "季";break;
// 					case "04":json.calcCycle = "年";break;
// 					default : json.calcCycle = "";
// 				}
				json.indexSts = indexSts[json.indexSts] || '';
// 				switch(json.indexSts){
// 					case "Y":json.indexSts = "启用";break;
// 					case "N":json.indexSts = "停用";break;
// 					default : json.indexSts = "";
// 				}
				json.defDept = parse(json.defDept, dept) || '';
				json.useDept = parse(json.useDept, dept) || '';
				$('#detail-table .value[name]').each(function(i, n) {
					var node = $(n);
					var name = node.attr('name');
					node.text(json[name]);
				});
				$('#loading').css('display', 'none');
			}
			
		});
	});
	
	function parse(str, code) {
		str = $.trim(str);
		var strs = str.split(';');
		var rt = [];
		$.each(strs, function(i, n){
			rt.push(code[n]);
		});
		return rt.join(';')
	}
</script>
</head>
<body>
	<div id="template.center">
		<div id='loading' class='l-tab-loading' style="display: block;"></div>
		<div style="height: 100%; margin: 0 2px;">
			<table id="detail-table" class="detail-table">
				<tr class="col4">
					<td class="title">版本：</td>
					<td class="value" name="indexVerId"></td>
					<td class="title">名称：</td>
					<td class="value" name="indexNm"></td>
				</tr>
				<tr class="col4">
					<td class="title">业务定义：</td>
					<td class="value" name="serviceDef"></td>
					<td class="title">种类：</td>
					<td class="value" name="indexType"></td>
				</tr>
				<tr class="col4">
					<td class="title">数据类型：</td>
					<td class="value" name="dataType"></td>
					<td class="title">数据长度：</td>
					<td class="value" name="dataLen"></td>
				</tr>
				<tr class="col4">
					<td class="title">计算周期：</td>
					<td class="value" name="calcCycle"></td>
					<td class="title">技术规则：</td>
					<td class="value"></td>
				</tr>
				<tr class="col4">
					<td class="title">启用日期：</td>
					<td class="value" name="startDate"></td>
					<td class="title">终止日期：</td>
					<td class="value" name="endDate"></td>
				</tr>
				<tr class="col4">
					<td class="title">定义部门：</td>
					<td class="value" name="defDept"></td>
					<td class="title">使用部门：</td>
					<td class="value" name="useDept"></td>
				</tr>
				<tr class="col4">
					<td class="title">状态：</td>
					<td class="value" name="indexSts"></td>
					<td class="title"></td>
					<td class="value"></td>
				</tr>
				<tr class="col1">
					<td colspan="4" class="title">业务规则：</td>
				</tr>
				<tr>
					<td colspan="4" class="value text" name="serviceRule"></td>
				</tr>
				<tr class="col1">
					<td colspan="4" class="title">备注：</td>
				</tr>
				<tr>
					<td colspan="4" class="value text" name="remark"></td>
				</tr>
			</table>
		</div>
	</div>
</body>
</html>