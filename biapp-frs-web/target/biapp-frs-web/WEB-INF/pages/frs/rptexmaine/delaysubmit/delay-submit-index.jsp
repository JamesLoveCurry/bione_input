<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<%@ include file="/common/taglibs.jsp"%>
<html>
<head>
<meta name="decorator" content="/template/template1_BS.jsp">

<style type="text/css">
	.link{
		    font-size: 16px;
    		font-weight: 600;
	}
</style>
<script type="text/javascript">
	var grid;
	$(function(){
		searchForm();
		initGrid();
		initButtons();
		addSearchButtons("#search", grid, "#searchbtn");
		initExport();
		$.ligerui.get('rptTypeBox').setData("02");
		$.ligerui.get('rptTypeBox').setText("1104报送");
		$("#rptType").val("02");
	});
	//搜索表单应用ligerui样式
	function searchForm() {
		$("#search").ligerForm({
			fields : [{
				display : "报送模块",
				name : "rptType",
				newline : false,
				type : "select",
				cssClass : "field",
				comboboxName : "rptTypeBox",
				attr : {
					op : "=",
					field : "rptType"
				},
				validate : {
					required : true
				},
				options : {
					onBeforeOpen : function() {
						$.ajax({
							cache : false,
                            async : false,
							url : "${ctx}/frs/validatereport/getIdxTaskType", 
							type : "get", 
							dataType : "json",
							success : function(result) {
								if(result.taskTypeList){
                                    $.ligerui.get('rptTypeBox').setData (result.taskTypeList);
                                }
							}
						});
					}
				}
			}, {
				display : "报表编号",
				name : "rptNum",
				newline : false,
				type : "text",
				attr : {
					op : "like",
					field : "rptNum"
				}
			}, {
				display : "报表名称",
				name : "rptNm",
				newline : false,
				type : "text",
				attr : {
					op : "like",
					field : "rptNm"
				}
			}, {
				display : "机构名称",
				name : "orgNo",
				newline : true,
				type : "select",
				cssClass : "field",
				comboboxName : "orgNm_sel",
				attr : {
					op : "in",
					field : "orgNo"
				},
				options : {
					onBeforeOpen : function() {
						var rptType = $("#rptType").val();
						if (rptType) {
							var height = $(window).height() - 120;
							var width = $(window).width() - 480;
							window.BIONE.commonOpenDialog(
											"机构树",
											"taskOrgWin",
											width,
											height,
											"${ctx}/frs/integratedquery/rptquery/searchOrgSetInfo?orgType=" + rptType,
											null);
							return false;
						} else {
							BIONE.tip("请选择报送模块！");
						}
					},
					cancelable : true
				}
			}, {
				display : "频度",
				name : "rptCycle",
				newline : false,
				type : "select",
				comboboxName : "rptCycleCombo",
				attr : {
					op : "=",
					field : "rptCycle"
				},
				options : {
					data : [ {
						text : '日',
						id : '01'
					}, {
						text : '月',
						id : '02' 
					}, {
						text : '季',
						id : '03' 
					}, {
						text : '年',
						id : '04' 
					}, {
						text : '周',
						id : '10' 
					}, {
						text : '旬',
						id : '11' 
					}, {
						text : '半年',
						id : '12' 
					} ]
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
			columns : [ {
				display : '报送模块',
				name : 'RPT_TYPE',
				width : '10%',
				align : 'center',
				render : function(rowdata, index, value) {
					if (value == "02")
						return "1104监管";
					if (value == "03")
						return "人行大集中";
					if (value == "05")
						return "利率报备";
					else
						return "未知类型";
				}
			}, {
				display : '报表编号',
				name : "RPT_NUM",
				width : '15%',
				align : 'center'
			}, {
				display : '报表名称',
				name : 'RPT_NM',
				width : '28%',
				align : 'left'
			}, {
				display : '频度',
				name : 'RPT_CYCLE',
				width : '6%',
				align : 'center',
				render : function(rowdata, index, value) {
					if (value == "01")
						return "日";
					if (value == "02")
						return "月";
					if (value == "03")
						return "季";
					if (value == "04")
						return "年";
					if (value == "10")
						return "周";
					if (value == "11")
						return "旬";
					if (value == "12")
						return "半年";
					else
						return "未知类型";
				}
			}, {
				display : '报送机构',
				name : 'RPT_ORG_NM',
				width : '15%',
				align : 'center'
			}, {
				display : '迟漏报次数',
				name : 'DELAY_COUNT',
				width : '7%',
				align : 'center',
				render : delayCountDetail
			}, {
				display : '错报次数',
				name : 'ERROR_COUNT',
				width : '7%',
				align : 'center',
				render : errorCountDetail
			}, {
				display : '驳回次数',
				name : 'REBUT_COUNT',
				width : '7%',
				align : 'center',
				render : rebutCountDetail
			}, {
				display : '报表id',
				name : "RPT_ID",
				hide : "true"
			}, {
				display : '机构编号',
				name : "ORG_NO",
				hide : "true"
			}],
			checkbox : false,
			usePager : true,
			isScroll : true,
			rownumbers : true,
			alternatingRow : true,//附加奇偶行效果行
			colDraggable : true,
			dataAction : 'server',//从后台获取数据
			method : 'get',
			url : '${ctx}/frs/rptexmaine/control/queryDelaySubmitList?d='+new Date().getTime(),
			sortName : 'orgNo', //第一次默认排序的字段
			sortOrder : 'asc',
			toolbar : {}
		});
	}
	//初始化Button
	function initButtons() {
		var btns = [ 
		    {
				text : '统计图',
				click : show_image,
				icon : 'fa-book',
				operNo : 'show_image'
			}, {
				text : '导出',
				click : oper_export,
				icon : 'fa-download',
				operNo : 'oper_export'
			}
		];
		BIONE.loadToolbar(grid, btns, function() {
		});
	}

 	function oper_export() {
 		//超过3000行不允许下载
 		if(grid.data.Total > 3000){
 			BIONE.tip("导出记录数不能超过3000行");
 		}else{
 			var rptType = $("#search input[name='rptType']").val();
 			var rptNum = $("#search input[name='rptNum']").val();
 			var rptNm = $("#search input[name='rptNm']").val();
 			var orgNo= $("#search input[name='orgNo']").val();
 			var rptCycle= $("#search input[name='rptCycle']").val();
 			var src = "${ctx}/frs/rptexmaine/control/download?rptType="+rptType+"&rptNum="+rptNum+
 					"&rptNm="+rptNm+"&orgNo="+orgNo+"&rptCycle="+rptCycle+"&d=" + new Date().getTime();
 			downdload.attr('src', src);
 		}
		
	} 

	function initExport() {
		downdload = $('<iframe id="download"  style="display: none;"/>');
		$('body').append(downdload);

	}
	
	//迟漏报链接
	function delayCountDetail(rowdata) {
		return "<a href='javascript:void(0)' class='link' onclick='showDelayDetail(\""+ rowdata.RPT_TYPE+"\",\"" + rowdata.RPT_ID+"\",\"" + rowdata.ORG_NO+"\",\"" + rowdata.RPT_CYCLE+"\")'>"+ rowdata.DELAY_COUNT + "</a>";
	};
	
	function showDelayDetail(rptType, rptId, orgNo, rptCycle){
		var height = $(window).height()-60;
		var width = $(window).width()-100;
		BIONE.commonOpenDialog("迟漏报明细", "delayDetailWin", width, height,
		"${ctx}/frs/rptexmaine/control/showDelayDetail?rptType="+ rptType +"&rptId="+ rptId +"&orgNo="+ orgNo +"&rptCycle="+ rptCycle);
	}
	
	//错报连接
	function errorCountDetail(rowdata) {
		return "<a href='javascript:void(0)' class='link' onclick='showErrorDetail(\""+ rowdata.RPT_TYPE+"\",\"" + rowdata.RPT_ID+"\",\"" + rowdata.ORG_NO+"\",\"" + rowdata.RPT_CYCLE+"\")'>"+ rowdata.ERROR_COUNT + "</a>";
	};
	
	function showErrorDetail(rptType, rptId, orgNo, rptCycle){
		var height = $(window).height()-60;
		var width = $(window).width()-160;
		BIONE.commonOpenDialog("错报明细", "errorDetailWin", width, height,
		"${ctx}/frs/rptexmaine/control/showErrorDetail?rptType="+ rptType +"&rptId="+ rptId +"&orgNo="+ orgNo +"&rptCycle="+ rptCycle);
	}

	//驳回连接
	function rebutCountDetail(rowdata) {
		return "<a href='javascript:void(0)' class='link' onclick='showRebutDetail(\""+ rowdata.RPT_TYPE+"\",\"" + rowdata.RPT_ID+"\",\"" + rowdata.ORG_NO+"\",\"" + rowdata.RPT_CYCLE+"\")'>"+ rowdata.REBUT_COUNT + "</a>";
	};

	function showRebutDetail(rptType, rptId, orgNo, rptCycle){
		var height = $(window).height()-60;
		var width = $(window).width()-160;
		BIONE.commonOpenDialog("驳回明细", "errorDetailWin", width, height,
				"${ctx}/frs/rptexmaine/control/showRebutDetail?rptType="+ rptType +"&rptId="+ rptId +"&orgNo="+ orgNo +"&rptCycle="+ rptCycle);
	}
	
	function show_image(){
		var rptType = $.ligerui.get("rptTypeBox").getValue();
		var orgNo = $.ligerui.get("orgNm_sel").getValue();
		if(rptType != null && rptType != ""){
			window.parent.BIONE.commonOpenDialog("迟漏报次数变化图", "imageWin", 900, 460,
			"${ctx}/frs/rptexmaine/control/showImage?rptType="+rptType+"&orgNo=" + orgNo);
		}else{
			BIONE.tip("请选择报送模块！");
		}
	}
	
	// 创建表单搜索按钮：搜索、高级搜索
	function addSearchButtons(form, grid, btnContainer) {
		if (!form)
			return;
		form = $(form);
		if (btnContainer) {
			BIONE.createButton({
				appendTo : btnContainer,
				text : '搜索',
				icon : 'search',
				//width : '50px',
				click : function() {
					BIONE.validate($("#formsearch"));
					if($("#formsearch").valid()){
						var rule = BIONE.bulidFilterGroup(form);
						if (rule.rules.length) {
							grid.setParm("condition",JSON2.stringify(rule));
							grid.setParm("newPage",1);
							grid.options.newPage=1
						} else {
							grid.setParm("condition","");
							grid.setParm('newPage', 1);
							grid.options.newPage=1
						}
						grid.loadData();
					}
				}
			});
			BIONE.createButton({
						appendTo : btnContainer,
						text : '重置',
						icon : 'refresh2',
						//width : '50px',
						click : function() {
							$(":input", form)
									.not(
											":submit, :reset,:hidden,:image,:button, [disabled]")
									.each(function() {
										$(this).val("");
									});
							$(":input[ltype=combobox]", form)
									.each(
											function() {
												var ligerid = $(this).attr(
														'data-ligerid'), ligerItem = $.ligerui
														.get(ligerid);
												if (ligerid && ligerItem
														&& ligerItem.clear) {
													ligerItem.clear();
												} else {
													$(this).val("");
												}
											});
							$(":input[ltype=select]", form)
									.each(
											function() {
												var ligerid = $(this).attr(
														'data-ligerid'), ligerItem = $.ligerui
														.get(ligerid);// 需要配置comboboxName属性
												if (ligerid && ligerItem
														&& ligerItem.clear) {// ligerUI
													// 1.2
													// 以上才支持clear方法
													ligerItem.clear();
												} else {
													$(this).val("");
												}
											});
							$.ligerui.get("rptTypeBox").setValue("02");
						}
					});
		}
	}
</script>

</head>
<body>
</body>
</html>