<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<%@ include file="/common/taglibs.jsp"%>
<html>
<head>
<meta name="decorator" content="/template/template14.jsp">
<style type="text/css">
	#mainform{
		height : 90%;
	}
</style>
<script type="text/javascript">
	var groupicon = "${ctx}/images/classics/icons/communication.gif";
	var templateIds = '${templateIds}';
	var verIds = '${verIds}';
	$(function() {
		initForm();
	});
	
	//创建表单结构 
	function initForm() {
		var field = [ {
			display : "单元格类型",
			name : "cellType",
			comboboxName:'cellTypeBox',
			newline : true,
			type : "select",
			group : "筛选条件",
			groupicon : groupicon,
			options : {
				data : [ {
					text : '指标单元格',
					id : '03'
				}, {
					text : 'excel公式单元格',
					id : '04' 
				}, {
					text : '表间计算单元格',
					id : '05' 
				}, {
					text : '数据模型单元格',
					id : '02' 
				} ],
				onSelected : function(value) {
					if("03" == value){//指标单元格,隐藏“是否可空”属性
						$("#isNull").rules("remove");
						$("#isNull").val("");
						$("#isNull").parents("li").hide();
						$("#isSum").parents("li").show();
						$("#isSum").rules("add" , {required : true});
						$.ligerui.get("isSumBox").setValue("Y");
						$("#isUpt").parents("li").show();
						$("#isUpt").rules("add" , {required : true});
						$.ligerui.get("isUptBox").setValue("Y");
					}else if("04" == value){//excel公式单元格,隐藏“是否可修改”，“是否汇总”，“是否可空”属性
						$("#isNull").rules("remove");
						$("#isNull").val("");
						$("#isNull").parents("li").hide();
						$("#isSum").rules("remove");
						$("#isSum").val("");
						$("#isSum").parents("li").hide();
						$("#isUpt").rules("remove");
						$("#isUpt").val("");
						$("#isUpt").parents("li").hide();
					}else if("05" == value || "02" == value){//表间计算单元格/数据模型单元格,隐藏“是否汇总”属性
						$("#isSum").rules("remove");
						$("#isSum").val("");
						$("#isSum").parents("li").hide();
						$("#isUpt").parents("li").show();
						$("#isUpt").rules("add" , {required : true});
						$.ligerui.get("isUptBox").setValue("Y");
						$("#isNull").parents("li").show();
						$("#isNull").rules("add" , {required : true});
						$.ligerui.get("isNullBox").setValue("Y");
					}
				}
			},
			validate : {
				required : true
			}
		}, {
			display: '显示格式',
			name :'displayFormat',
			comboboxName:'displayFormatBox',
			type: 'select',
			newline: false,
			options : {
				data : [ {
					text : '金额',
					id : '01'
				}, {
					text : '百分比',
					id : '02' 
				}, {
					text : '数值',
					id : '03' 
				}, {
					text : '文本',
					id : '04' 
				} ],
				onSelected : function(value) {
					if("03" == value){//数值,隐藏“数据精度”属性
						$("#dataPrecision").rules("remove");
						$("#dataPrecision").val("");
						$("#dataPrecision").parents("li").hide();
						$("#dataUnit").parents("li").show();
						$("#dataUnit").rules("add" , {required : true});
					}else if("04" == value){//文本,隐藏“数据单位”，“数据精度”属性
						$("#dataUnit").rules("remove");
						$("#dataUnit").val("");
						$("#dataUnit").parents("li").hide();
						$("#dataPrecision").rules("remove");
						$("#dataPrecision").val("");
						$("#dataPrecision").parents("li").hide();
					}else if("02" == value){//百分比,隐藏“数据精度”属性
						$("#dataUnit").rules("remove");
						$("#dataUnit").val("");
						$("#dataUnit").parents("li").hide();
						$("#dataPrecision").parents("li").show();
						$("#dataPrecision").rules("add" , {required : true});
					}else if("01" == value){
						$("#dataUnit").parents("li").show();
						$("#dataUnit").rules("add" , {required : true});
						$("#dataPrecision").parents("li").show();
						$("#dataPrecision").rules("add" , {required : true});
					}
				}
			},
			validate:{
				required: true
			}
		},{
			display: '数据单位',
			name :'dataUnit',
			comboboxName:'dataUnitBox',
			type: 'select',
			newline: false,
			group : "单元格属性",
			groupicon : groupicon,
			options : {
				initValue : "04",
				data : [ {
					text : '元',
					id : '01'
				}, {
					text : '百',
					id : '02' 
				}, {
					text : '千',
					id : '03' 
				}, {
					text : '万',
					id : '04' 
				}, {
					text : '亿',
					id : '05' 
				}, {
					text : '无单位',
					id : '00' 
				} ]
			},
			validate:{
				required: true
			}
		}, {
			display : "数据精度",
			name : 'dataPrecision',
			type : "text",
			newline : false
		}, {
			display: '是否可修改',
			name :'isUpt',
			comboboxName:'isUptBox',
			type: 'select',
			newline: false,
			options : {
				initValue : "Y",
				data : [ {
					text : '是',
					id : 'Y'
				}, {
					text : '否',
					id : 'N' 
				}]
			},
			validate:{
				required: true
			}
		}, {
			display: '是否跑数汇总',
			name :'isSum',
			comboboxName:'isSumBox',
			type: 'select',
			newline: false,
			options : {
				initValue : "Y",
				data : [ {
					text : '是',
					id : 'Y'
				}, {
					text : '否',
					id : 'N' 
				}]
			},
			validate:{
				required: true
			}
		},{
			display: '是否可空',
			name :'isNull',
			comboboxName:'isNullBox',
			type: 'select',
			newline: false,
			options : {
				initValue : "Y",
				data : [ {
					text : '是',
					id : 'Y'
				}, {
					text : '否',
					id : 'N' 
				}]
			},
			validate:{
				required: true
			}
		}];
		
		var mainform = $("#mainform").ligerForm({
			inputWidth : 170,
			labelWidth : 100,
			labelAlign : 'right',
			space : 40,
			fields : field
		});
		
		var buttons = [{
			text:'取消',
			onclick:function(){
				BIONE.closeDialog("baseEdit");
			}
		}, {
			text : '调整',
			onclick : updateCell
		}];
		BIONE.addFormButtons(buttons);
		jQuery.metadata.setType("attr", "validate");
		BIONE.validate("#mainform");
	}
	
	function updateCell() {
		var data = {
				templateIds : templateIds,
				verIds : verIds,
				cellType : $.ligerui.get("cellTypeBox").getValue(),
				displayFormat : $.ligerui.get("displayFormatBox").getValue(),
				dataUnit : $.ligerui.get("dataUnitBox").getValue(),
				dataPrecision : $("#dataPrecision").val(),
				isUpt : $.ligerui.get("isUptBox").getValue(),
				isSum : $.ligerui.get("isSumBox").getValue(),
				isNull : $.ligerui.get("isNullBox").getValue()
		};
		if(data.dataUnit == "" && data.dataPrecision == "" && data.isUpt == "" && data.isSum == "" && data.isNull == ""){
			BIONE.tip("无可调整的单元格属性！");
			return;
		}
		$.ajax({
			cache : false,
			async : false,
			url : "${ctx}/report/frame/design/cfg/updateCell",
			dataType : 'json',
			data : data,
			type : "post",
			success : function(result){
				window.parent.BIONE.tip("保存成功！");
				BIONE.closeDialog("baseEdit");
			},
			error:function(){
				BIONE.tip("保存失败，请联系系统管理员！");
			}
		});
	}
	
</script>
</head>
<body>
	<div id="template.center">
		<form name="mainform" method="post" id="mainform" action=""></form>
		<div id="bottom">
			<div class="form-bar">
				<div class="form-bar-inner" style="padding-right:20px"></div>
			</div>
		</div>
	</div>
</body>
</html>