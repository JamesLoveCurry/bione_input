<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<%@ include file="/common/taglibs.jsp"%>
<html>
<meta name="decorator" content="/template/template14.jsp">
<head>
<%@ include file="/common/wijmo.jsp"%>
<style type="text/css">


.ztree li span.button.add {
	background-position: -144px 0;
	margin-left: 2px;
	margin-right: -1px;
	vertical-align: top;
}


.frame {
	margin: 0 2px;
}

.link {
	color: #065FB9;
	text-decoration: none;
}

.link:hover {
	text-decoration: underline;
}

.label-item {
	height: 20px;
	float: left;
	line-height: 20px;
	border: 1px solid #D4D4D4;
	color: #4C4C4C;
	margin: 0 10px 5px 0;
	padding: 0 2px;
	background-color: #F0F0F0;
	border-radius: 2px;
}

.label-item .text {
	float: left;
	height: 20px;
}

.label-item .icon {
	float: left;
	width: 10px;
	height: 10px;
	margin: 5px 0 5px 2px;
	background: url(${ctx}/images/classics/icons/icons_label.png) no-repeat
		-288px -30px;
	cursor: pointer;
}

.win {
	height: 100%;
}

.win .title {
	height: 20px;
	line-height: 20px;
	border: 1px solid #CCC;
	border-bottom: 0;
	background: url('${ctx}/images/classics/ligerui/tabs-bg.gif') repeat-x 0
		-3px;
	font-weight: bold;
	padding-left: 5px;
}

.win .content {
	border: 1px solid #CCC;
	height: 10px;
	overflow-y: auto;
	padding: 2px;
	background-color: white;
}

.gridinput {
	font-size: 12px;
	line-height: 12px;
	height: 12px;
	width: 90%;
	margin: 1px 0;
	padding: 2px;
}

.gridtitle {
	height: 20px;
	line-height: 20px;
	padding-left: 2px;
	font-weight: bold;
}

.lform {
	width: 100%;
	height: 100%;
	margin: 0;
	padding: 0;
}

.lform table {
	width: 100%;
	height: 100%;
}


</style>
<script type="text/javascript">
/* jQuery.extend(jQuery.validator.messages, {
	greaterThan : "结束日期应当大于开始日期"
}); */
jQuery.validator.addMethod("greaterThan", function(value, element, params) {
	if (value == '' || value == null) {
		// edit by caiqy,have no endtime , return
		return true;
	}
	var ele = $("[name=" + params + "]");
	if (ele.is(":text") && ele.attr("ltype") == "date") {
		// edit by caiqy, ps:this is not a best way ,0.0
		var tdate;
		var fdate;
		if (typeof value == 'string' && value.length >= 8) {
			// if format as 'yyyy-MM-dd' or 'yyyy-mm-dd hh:mm:ss'
			tdate = new Date(new Number(value.substr(0, 4)), new Number(value
					.substr(4, 2)) - 1, new Number(value.substr(6, 2)))
					.valueOf()
					/ (60 * 60 * 24 * 1000);
		} else {
			tdate = new Date(value).valueOf() / (60 * 60 * 24 * 1000);
		}
		if (ele.val().indexOf(ele.val().length >= 8)) {
			// if format as 'yyyy-MM-dd' or 'yyyy-mm-dd hh:mm:ss'
			fdate = new Date(new Number(ele.val().substr(0, 4)), new Number(ele
					.val().substr(4, 2)) - 1,
					new Number(ele.val().substr(6, 2))).valueOf()
					/ (60 * 60 * 24 * 1000);
		} else {
			fdate = new Date(ele.val()).valueOf() / (60 * 60 * 24 * 1000);
		}
		// edit end
		//  old code :
		//		var tdate = new Date(value).valueOf() / (60 * 60 * 24 * 1000);
		//		var fdate = new Date(ele.val()).valueOf() / (60 * 60 * 24 * 1000);

		return tdate - fdate >= 0 ? true : false;
	} else {
		return value > ele.val() ? true : false;
	}
}, "结束日期应当大于开始日期");
var tree = {}, tab = {}, grid = {}, labelPad = {}, disselect = {};
var idxType = {
	'01': '根指标',
	'02': '组合指标',
	'03': '派生指标',
	'04': '泛化指标',
	'05': '总账指标',
	'06': '报表指标'
};
var Component = liger.core.Component;

function LabelItem(option) {
	LabelItem.base.constructor.call(this, options);
}
LabelItem.ligerExtend(Component, {
	setDisclose: function() {
		this.showClose = false;
	}
});
function Label(target) {
	Label.base.constructor.call(this);
	this.target = target;
	this.data = {};
}
Label.count = 0;
Label.ligerExtend(Component, {
	getId: function(prev){
		prev = prev || 'label';
        var id = prev + (1000 + Label.count);
        Label.count++;
        return id;
	},
	clear: function() {
		this.target.empty();
		delete this.data;
		this.data = {};
	},
	addLabel: function(param) {
		var data = $.extend({
			lId: this.getId()
		}, param);
		if (this.trigger('beforeAdd', data) == false) {
			return;
		}
		if (param instanceof Array == true) {
			for (var i = 0; i < param.length; i++) {
				this.addLabel(param[i]);
			}
			return;
		}
		var label = {};
		label.content = $('<div class="label-item"/>');
		label.text = $('<div class="text"/>').text(data.text).appendTo(label.content);
		if (param.showClose && param.showClose == true) {
			var item = this;
			label.acton = $('<div class="icon"/>').appendTo(label.content);
			label.acton.bind('click', data, function(event) {
				item.closeLabel(event.data.lId);
			});
		}
		label.content.appendTo(this.target);
		data.label = label;
		this.data[data.lId] = data;
		this.trigger('afterAdd', data);
		return data;
	},
	getLabel: function() {
		var data = this.data;
		var result = [];
		var args = arguments;
		$.each(data, function(i, n) {
			if (args.length == 0 || (args.length == 1 && n.lId == args[0]) || (args.length == 2 && n[args[0]] && n[args[0]] == args[1])) {
				result.push(n);
			}
		});
		return result;
	},
	closeLabel: function(lId) {
		var label = this.data[lId];
		if (this.trigger('beforeClose', label) == false) {
			return;
		}
		if (label && label.label) {
			label.label.content.remove();
		}
		this.trigger('afterClose', label);
		delete this.data[lId];
	},
	removeLabel: function(lId) {
		var label = this.data[lId];
		if (label && label.label) {
			label.label.content.remove();
		}
		delete this.data[lId];
	},
	removeAllLable : function(){
		var base = this;
		$.each(this.data, function(i, n) {
			base.removeLabel(i);
		});
	}
});
function IdxGrid(wrap, setting) {
	IdxGrid.base.constructor.call(this);
	var base = this;
	this.setting = {
		usePager: false,
		checkbox: false,
		allowHideColumn: false,
		columns: [{
			isSort: false,
			display: '指标名称',
			name: 'indexNm',
			width: 150
		}, {
			isSort: false,
			display: '指标别名',
			width: 150,
			name: 'rename',
			render: function(row) {
				var cell = arguments[3];
				var input = $('<input/>').attr({
					id: cell.__domid + '|input|' + arguments[1],
					type: 'text',
					value: row.rename
				}).addClass('gridinput');
				return input[0].outerHTML;
			}
		}, {
			isSort: false,
			display: '类型',
			name: 'indexType',
			width: 150,
			render: function(row) {
				return idxType[row.indexType];
			}
		}, {
			isSort: false,
			display: '维度设置',
			width: 150,
			name: 'indexCfg',
			render: function(row) {
				if (row.indexCfg) {
					return row.indexCfg
				} else {
					return '无配置'
				}
			}
		}, {
			isSort: false,
			display: '操作',
			width: 150,
			render: function() {
				return '<a class="link" href="javascript:conf(' + arguments[1] + ')">设置</a>&nbsp;<a class="link" href="javascript:delIdx(' + arguments[1] + ')">删除</a>';
			}
		}]
	};
	$.extend(this.setting, setting);
	this.wrap = wrap;
	this.grid = this.wrap.ligerGrid(this.setting);
	this.data = {};
}
IdxGrid.ligerExtend(Component, {
	_renderDetail: function(row) {
		var dom = row.detailPanel;
		dom.append('<div class="gridtitle">维度设置：</div>');
		var content = $('<div/>').css({
			'padding-left': '14px'
		}).appendTo(dom);
		if (row.dimInfo && row.dimInfo.length > 0) {
			$.each(row.dimInfo, function(i, n) {
				if(i > 0) {
					content.append('<br/>');
				}
				var text = n.dimTypeNm + '[';
				$.each(n.checkedItem, function(ii, nn) {
					if (ii > 0) {
						text += '|';
					}
					text += nn.dimItemNm;
				});
				text += ']';
				content.append($('<span/>').text(text));
			});
		} else {
			content.append($('<span/>').text('无过滤'));
		}
	},
	addIdx: function(idxNm, idxNo, verId) {
		var base = this;
		var row = this.grid.addRow({
			indexNm: idxNm,
			idxNo: idxNo,
			verId: verId
		});
		$.ajax({
			url: '${ctx}/report/frame/datashow/idx/grid',
			type: 'get',
			data: {
				idxNo: idxNo,
				verId: verId
			},
			dataType: 'json',
			success: function(data) {
				base.grid.update(row, data.idx);
				base.trigger('loadIdx', [data, row]);
			}
		});
		return row;
	},
	addStoreIdx : function(instanceId){
		var base = this;
		$.ajax({
			url: '${ctx}/report/frame/datashow/idx/getStoreInfo',
			type: 'get',
			data: {
				instanceId : instanceId
			},
			dataType: 'json',
			success: function(data) {
				var showIdx = [];
				for(var i=0;i<data.detailList.length;i++){
					var filter = [];
					for(var j=0;j<data.filterList.length;j++){
						if(data.detailList[i].detailId == data.filterList[j].id.detailId){
							filter.push({
									dimNo : data.filterList[j].id.dimNo,
									dimNm : data.filterList[j].dimTypeNm,
									filterVal : data.filterList[j].filterVal,
									filterMode : data.filterList[j].filterMode
							});
						}
						
					}
					var cfgStr = "";
					if (filter) {
						$.each(filter, function(i, n) {
							if (i > 0) {
								cfgStr += "<br/>";
							}
							cfgStr = cfgStr + n.dimNm + '(' + n.filterVal + ')';
						});
						cfgRow.indexCfg = cfgStr;
						grid.grid.update(cfgRow, cfgRow);
					}
					var row = base.grid.addRow({
						indexNm: data.detailList[i].indexNm,
						indexNo: data.detailList[i].indexNo,
						verId: data.detailList[i].indexVerId,
						dimCfg : filter,
						indexCfg : cfgStr,
						rename : data.detailList[i].indexAlias,
						indexType : data.detailList[i].indexType
					});
					
					base.grid.update(row, row.idxNo);
					showIdx[data.detailList[i].indexNo] = {
							text : data.detailList[i].indexNm,
							showClose : false,
							regist: data.detailList[i].indexNo,
							colName : data.detailList[i].indexNo
					};
				}
				for(var tmp in showIdx){
					labelPad.addLabel(showIdx[tmp]);
				}
				var showDim = [];
				var noShowDim = [];
				for(var i=0;i<data.dimTypeList.length;i++){
					var j=0;
					for(j=0;j<data.dimList.length;j++){
						if(data.dimTypeList[i].dimTypeNo == data.dimList[j].id.dimNo){
							showDim.push(data.dimTypeList[i]);
							break;
						}
					}
					if(j>=data.dimList.length){
						noShowDim.push(data.dimTypeList[i]);
					}
				}
				base.trigger('loadIdx', [{dim:showDim}, {}]);
				for(var i=0;i<noShowDim.length;i++){
					disselect.addLabel({text:noShowDim[i].dimTypeNm, showClose:true});	
				}
				
			}
		});
		//return row;
	},
	removeIdx: function(row) {
		this.grid.deleteRow(row);
		this.trigger('removeIdx', row);
	}
});
$.fn.extend({
	bioneLabel: function() {
		return new Label(this);
	},
	idxGrid: function(setting) {
		return new IdxGrid(this, setting);
	},
	exTree: function(option) {
		return new ExTree(this, option);
	}
});
// 初始样式控制
$(function(){
	$('#content').height($('#container').height() - 30);
	$('#from-item').height($('#form').height() - 5);
	$('#selected').height($('#col1').height() - 8 - 20);
	$('#disselect').height($('#col2').height() - 10 - 20);
});
$(function() {
	labelPad = $('#selected').bioneLabel();
	disselect = $('#disselect').bioneLabel();
	disselect.bind('afterClose', function(label) {
		labelPad.addLabel(label);
	});
	labelPad.bind('afterClose', function(label) {
		disselect.addLabel(label);
	});
	
	
	grid = $('#maingrid').idxGrid({
		height: $('#table').height() - 27
	});
	$('#maingrid tr.l-grid-row td:nth-child(4)').live({
		'mouseover.tip': function(event) {
			$(this).ligerTip({
				content: $('div',this).text()
			});
		},
		'mouseout.tip': function(event) {
			$(this).ligerHideTip();
		}
	});
	grid.addStoreIdx("${instanceId}");
	// 指标别名-标签联动
	$('.gridinput').live('blur', function(event) {
		var target = event.target;
		var arr = target.id.split('|');
		var row = grid.grid.getRow(arr[4]);
		if (row.label) {
			var labels = labelPad.getLabel(row.label);
			if (labels.length > 0) {
				var label = labels[0];
				var text = this.value ? this.value: row.indexNm;
				label.text = text;
				label.label.text.text(text);
				row.rename = this.value
			}
		}
	});
	
	// 指标加载完成
	grid.bind('loadIdx', function(data, row){
		var dim = data.dim;
		var labels = [];
		$.each(dim, function(i, n) {
			n.text = n.dimTypeNm;
			n.showClose = (n.dimType == '00');//是否显示 删除维度
			n.regist = row.idxNo;
			n.dimNo = n.dimTypeNo;
			var hasLabel = [].concat(labelPad.getLabel('dimTypeNo', n.dimTypeNo), disselect.getLabel('dimTypeNo', n.dimTypeNo));
			if (hasLabel.length == 0) {
				labels.push(n);
			} else {
				$.each(hasLabel, function(i1, n1) {
					if (n1.regist) {
						if(n1.regist.length > 0) {
							n1.regist += '|';
						}
						n1.regist += row.idxNo
					} else {
						n1.regist = row.idxNo;
					}
				});
			}
		});
		if(labels && labels.length > 0){
			labelPad.addLabel(labels);
			if(data.idx){
				var l = labelPad.addLabel({
					text: data.idx.indexNm,
					showClose: false,
					regist: row.idxNo,
					colName : data.idx.indexNo
				});
				row.label = l.lId;
			}
		}
	});
	grid.bind('removeIdx', function(row) {
		var labels = [];
		labels = labels.concat(labelPad.getLabel(), disselect.getLabel());
		$.each(labels, function(i, n) {
			var r = n.regist;
			if (r && r.length > 0) {
				var s = r.split('|');
				if ($.inArray(row.idxNo, s) != -1) {
					if (s.length == 1) {
						labelPad.removeLabel(n.lId);
						disselect.removeLabel(n.lId);
					} else {
						var ss = $.grep(s, function(x, y) {
							return row.idxNo != x;
						});
						n.regist = ss.join('|');
					}
				}
			}
		});
	});
	BIONE.addFormButtons([{
		text : '查询',
		onclick : function(){
			var indexNos = window.indexNos = [];
			var req = window.req = {
				'queryType':'index', 
				'indexNo': [], 
				'dimNo': [], 
				'searchArg':[],
 				'startDate': '',
 				'endDate': ''
			};
			var rows = grid.grid.rows;
			var dims = null;
			$.metadata.setType("attr", "validate");
			BIONE.validate($("#form"));
			if($("#form").valid()){
			$.each(rows, function(i, n){
				indexNos.push({indexNo:n.indexNo, indexVerId : n.verId});
				
				req.indexNo.push(n.indexNo);
				dims = n.dimCfg;
				if (dims) {
					$.each(dims, function(i1, n1) {
						req.searchArg.push({
							indexNo: n.indexNo,
							dimNo: n1.dimNo,
							op: '=',
							value: n1.filterVal
						});
					});
				}
			});
			var labels = labelPad.getLabel();
			var col = window.col = [];
			$.each(labels, function(i, n){
				if (n && n.dimNo) {
					col.push({
						isSort: false,
						display: n.text,
						name: n.dimTypeNo,
						width: 150
					});
					req.dimNo.push(n.dimNo);
				}else if(n && n.colName){
					col.push({
						isSort: false,
						display: n.text,
						name: n.colName,
						width: 150
					});
				}
			});
			req.searchArg.push({
				dimNo : 'org',
				op : '=',
				value : liger.get("org").selectedValue
			});
			req.searchArg.push({
				dimNo : 'currency',
				op : '=',
				value : liger.get("currency").selectedValue
			});
			
			var gt = liger.get('startDate').getValue();
			var lt = liger.get('endDate').getValue();
			req.startDate = gt;
			req.endDate = lt;
			BIONE.commonOpenLargeDialog('查询', 'search', 
					'${ctx}/report/frame/datashow/idx/search');
			}
		}
	}]);
	var width = $("#form").width();
 	$('#form').ligerForm({
 		labelWidth : 50,
 		inputWidth : 120,
 		fields : [{
 			display : '机构',
 			name : 'org',
 			type : 'select',
 			options : {
 				onBeforeOpen: function() {
 					BIONE.commonOpenSmallDialog('选择机构', 'chooseOrg', '${ctx}/report/frame/datashow/idx/chooseOrg');
 					return false;
 				}
 			},
 			validate : {
				required : true
			}
 		},{
 			display : '时间段',
 			name : 'startDate',
 			type : 'date',
 			newline : false,
 			options : {
 				format : 'yyyyMMdd',
 				cancelable : false
 			},
 			validate : {
				required : true
			}
 		},{
 			display : "<div id='deadEnd'></div>",
			name:"endDate",
			newline:false,
			type:"date",
			labelWidth:20,
			options : {
 				format : 'yyyyMMdd',
 				cancelable : false
 			},
 			validate : {
				required : true,
				greaterThan : "startDate"
			}
 		},{
 			display : '币种',
 			type :　'select',
 			name : 'currency',
 			newline : false,
 			options :{
 				onBeforeOpen: function() {
 					BIONE.commonOpenSmallDialog('选择机构', 'chooseCurrency', '${ctx}/report/frame/datashow/idx/chooseCurrency');
 					return false;
 				}
 			},
 			validate : {
				required : true
			}
 			
 		}]
 	});
 	$("#deadEnd").parent().html("至");
});

// 删除指标
function delIdx(i) {
	var row = grid.grid.getRow(i);
	grid.removeIdx(row);
}
// 维度设置
var cfgRow = {};
function conf(i) {
	cfgRow = grid.grid.getRow(i);
	window.dimFilterInfo = cfgRow.dimCfg;
	BIONE.commonOpenDialog('指标维度过滤', 'dimFilterWin', 600, 480, 
			"${ctx}/rpt/frame/mgr/idxdsrel/idxdimfliter?indexNo="
			+ cfgRow.indexNo+"&type=2", null, function(data) {
		if (data && data.dimFilterInfo) {
			cfgRow.dimCfg = data.dimFilterInfo;
			var cfgStr = "";
			$.each(data.dimFilterInfo, function(i, n) {
				if (i > 0) {
					cfgStr += "<br/>";
				}
				cfgStr = cfgStr + n.dimNm + '(' + n.filterVal + ')';
			});
			cfgRow.indexCfg = cfgStr;
			grid.grid.update(cfgRow, cfgRow);
		}
	});
}
</script>
</head>
<body>
<div id="template.center">
	<div id="container" style="height: 100%;">
		<div id="content">
		<!-- 	表格 -->
			<div id="table" class="frame" style="height: 50%;">
				<div class="win" style="height: 100%; margin-top: 2px;">
					<div class="title">已选定的指标：</div>
					<div id="maingrid" class="maingrid"></div>
					<table id="wijgrid"></table>
				</div>
			</div>
		<!-- 	表单 -->
			<form id="form" class="frame" style="height: 10%; min-width: 800px;"></form>
				<!-- <div id="from-item" style="border: 1px solid #ccc;">
					<form action="" id="lform" style="width: 100%; height: 100%; margin: 0; padding: 0;">
						<table style="width: 100%; height: 100%;  min-width: 800px;">
							<tr>
								<td style="padding-left: 5px;">
									<div style="float: left; height: 22px; line-height: 22px;"><label for="txtName">机构:</label></div>
									<div style="float: left; margin-left: 10px;" class="l-table-edit-td"><input name="org" type="text" id="org" ltype="select" validate="{required:true}"/></div>
								</td>
								<td style="border-left: 1px solid #CCC; border-right: 1px solid #CCC;padding-left: 5px; ">
									<div style="float: left; height: 22px; line-height: 22px;"><label for="data1">时间段:</label></div>
									<div style="float: left; margin-left: 10px;"><input name="startDate" type="text" id="startDate" ltype="date" validate="{required:true}"/></div>
									<div style="float: left; margin-left: 10px; height: 22px; line-height: 22px;">至</div>
									<div style="float: left; margin-left: 10px;"><input name="endDate" type="text" id="endDate" ltype="date" validate="{required:true}"/></div>
								</td>
								<td  style="padding-left: 5px;">
									<div style="float: left; height: 22px; line-height: 22px;"><label for="bz">币种:</label></div>
									<div style="float: left; margin-left: 10px;"><input name="currency" type="text" id="currency" ltype="select" validate="{required:true}"/></div>
								</td>
							</tr>
						</table>
						
					</form>
				</div> 
			</div>-->
		<!-- 	已选定列 -->
			<div id="col1" class="frame" style="height: 20%; overflow-y: auto;">
				<div class="win" style="height: 100%;">
					<div class="title">已展示列</div>
					<div id="selected" class="content"></div>
				</div>
			</div>
		<!-- 	未选定列 -->
			<div id="col2" class="frame" style="height: 20%;">
				<div class="win">
					<div class="title">未展示列</div>
					<div id="disselect" class="content"></div>
				</div>
			</div>
		</div>
		<div id="bottom" style="height: 30px;">
			<div class="form-bar">
			 	<div id="form-bar-inner" class="form-bar-inner"></div>
			</div>
		</div>
	</div>
</div>
</body>
</html>