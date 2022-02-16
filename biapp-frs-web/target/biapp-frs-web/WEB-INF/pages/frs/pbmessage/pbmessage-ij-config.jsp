<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<%@ include file="/common/taglibs.jsp"%>
<html>
<head>
<meta name="decorator" content="/template/template1_BS.jsp">
<script type="text/javascript">
	var grid;
	var reportCodeCfgNos = [];

	$(function() {
		searchForm();
		initGrid();
		initButtons();
		initExport();
		BIONE.addSearchButtons("#search", grid, "#searchbtn");
	});
	
	function searchForm() {
		$("#search").ligerForm({
			fields : [ {
				display : "报表编号",
				name : "rptCode",
				newline : true,
				type : "text",
				cssClass : "field",
				attr : {
					op : "like",
					field : "rptCode"
				}
			},{
				display : "单元格编号",
				name : "cellNo",
				newline : false,
				type : "text",
				cssClass : "field",
				attr : {
					op : "like",
					field : "cellNo"
				}
			},{
				display : "单元格名称",
				name : "cellNm",
				newline : false,
				type : "text",
				cssClass : "field",
				attr : {
					op : "like",
					field : "cellNm"
				}
			},{
				display : "人行编码",
				name : "pbcCode",
				newline : false,
				type : "text",
				cssClass : "field",
				attr : {
					op : "like",
					field : "pbcCode"
				}
			} ]
		});
	}

	//初始化Grid
	function initGrid() {
		grid = $("#maingrid").ligerGrid({
			InWindow : false,
			height : '99%',
			width : '100%',
			columns : [{
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
			}, {
				display : '报送范围',
				name : 'submitRangeNm',
				width : '10%'
			}],
			checkbox : true,
			usePager : true,
			isScroll : true,
			rownumbers : true,
			pageSize: 50,                           //每页默认的结果数
	        pageSizeOptions: [50, 200, 500, 800, 1000],  //可选择设定的每页结果数
			alternatingRow : true,//附加奇偶行效果行
			colDraggable : true,
			dataAction : 'server',//从后台获取数据
			method : 'post',
			url : '${ctx}/frs/pbmessage/ijConfigList',
			sortName : 'rptCode', //第一次默认排序的字段
			sortOrder : 'asc',
			toolbar : {}
		});
	}

	function initButtons() {
		var btns = [ {
			text : '初始化配置',
			click : oper_add,
			icon : 'icon-add',
			operNo : 'oper_add'
		}, {
			text : '修改属性',
			click : oper_modify,
			icon : 'icon-modify',
			operNo : 'oper_modify'
		}, {
			text : '批量删除',
			click : oper_delete,
			icon : 'icon-delete',
			operNo : 'oper_delete'
		} /* ,{
			text : '导出配置信息',
			click : exportInfo,
			icon : 'export'
		},{
			text : '导入配置信息',
			click : importInfo,
			icon : 'import'
		} */, {
			text : '检测配置信息',
			click : code_check,
			icon : 'icon-import',
			operNo : 'config'
		}, {
			text : '查看地区',
			click : org_cfg,
			icon : 'icon-config',
			operNo : 'config'
		}, {
			text : '报送范围',
			click : range_cfg,
			icon : 'icon-config',
			operNo : 'config'
		} ];
		BIONE.loadToolbar(grid, btns, function() {});
	}
	var height = $(window).height() - 40;
	var width = $(window).width() - 60;
	
	//上报地区
	function open_org_grid(submitRangeCode,verId){
		if(submitRangeCode == ""){
			BIONE.tip('请先配置上报范围在进行上报机构查看！');
			return;
		}
		BIONE.commonOpenDialog("上报地区","orgConfig",width,height,"${ctx}/frs/pbmessage/orgGrid?submitRangeCode="+submitRangeCode+"&verId="+verId+"&isView=1",null);
	}
	
	//查看地区
	function org_cfg() {
		var rows = grid.getSelectedRows();
		if (rows.length < 1) {
			BIONE.tip('请选择记录!');
			return;
		}
		var submitRangeCode = "";
		var verId = "";
		var indexNo = "";
		var rptCode = "";
		for ( var i in rows) {
			if(submitRangeCode != "" && submitRangeCode != rows[i].submitRangeCode){
				BIONE.tip('请选择相同的报送范围记录查看！');
				return;
			}
			submitRangeCode = rows[i].submitRangeCode;
			if(rptCode != "" && rptCode != rows[i].rptCode){
				BIONE.tip('请选择相同的报表记录查看！');
				return;
			}
			rptCode = rows[i].rptCode;
			if(verId != "" && verId != rows[i].verId){
				BIONE.tip('请选择相同的版本记录查看！');
				return;
			}
			verId = rows[i].verId;
			indexNo = rows[i].sysIndex;
		}
		var height = $(window).height() - 30;
		var width = $(window).width() - 60;
		BIONE.commonOpenDialog("上报地区","orgConfig",width,height,"${ctx}/frs/pbmessage/orgGrid?submitRangeCode="+submitRangeCode+"&verId="+verId+"&isView=1",null);
	}
	
	//报送范围
	function range_cfg(){
		BIONE.commonOpenDialog("报送范围","rangeInfo",width,height,"${ctx}/frs/pbmessage/rangetab",null);
	}
	
	function oper_add() {
		BIONE.commonOpenDialog('新增上报信息', 'addCode', width, height,'${ctx}/frs/pbmessage/codeAdd');
	}
	//修改属性
	function oper_modify() {
		var rows = grid.getSelectedRows();
		if (rows.length < 1) {
			BIONE.tip('请选择记录!');
			return;
		}
		reportCodeCfgNos = achieveIds();
		BIONE.commonOpenLargeDialog("修改属性", "editType","${ctx}/frs/pbmessage/pbcTypeEdit");
	}
	//批量删除报文集
	function oper_delete() {
		var ids = achieveIds();
		if(ids.length > 0){
			var idsStr = ids.join(',');
			$.ligerDialog.confirm('您确定删除这' + ids.length + "条记录吗？", function(yes) {
				if (yes) {
					$.ajax({
						async : false,
						type : "post",
						url : '${ctx}/frs/pbmessage/deleteCode',
						data : {
							ids:idsStr
						},
						success : function(res){
							BIONE.tip('删除成功');
							grid.loadData();
						},
						error : function(e){
							BIONE.tip('删除失败');
						}
					});
				}
			});	
		} else {
			BIONE.tip('请选择记录');
		}
	}
	//获取选中行的主键
	function achieveIds() {
		//过滤版本
		var ids = [];
		var verId = '';
		var rows = grid.getSelectedRows();
		for ( var i in rows) {
			ids.push(rows[i].reportCodeCfgNo)
		}
		return ids;
	}
	//频度渲染
	function freqTypeRender(rowdata) {
		if (rowdata.freqType == '0') {
			return "年";
		} else if (rowdata.freqType == '3') {
			return "季";
		} else if (rowdata.freqType == '4') {
			return "月";
		} else if (rowdata.freqType == '5') {
			return "旬";
		} else if (rowdata.freqType == '6') {
			return "周";
		} else if (rowdata.freqType == '7') {
			return "日";
		} else {
			return "";
		}
	}
	
	//检测
	function code_check(){
		BIONE.commonOpenDialog('检测配置信息', 'checkInfo',  width,height,'${ctx}/frs/pbmessage/codeCheck');
	}
	
	function exportInfo(){
		BIONE.commonOpenDialog("人行报文配置导出", "exportWin", width,height,"${ctx}/frs/pbmessage/exportInfo");
	}
	
	function importInfo(){
		BIONE.commonOpenDialog("人行报文配置导入", "importWin", 600, 480, "${ctx}/report/frame/wizard?type=Pbc");
	}
	
	function reload(){
		grid.loadData();
	}

	function exportXls(fileName, rptNums){
		var src = "${ctx}/frs/pbmessage/exportXls?filepath="+fileName+"&rptNums="+rptNums+"&d="+ new Date().getTime();
		downdload.attr('src', src);
	};
	
	function initExport() {
		window["downdload"] = $('<iframe id="download"  style="display: none;"/>');
		$('body').append(downdload);
	};
	
</script>

</head>
<body>
</body>
</html>