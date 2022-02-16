<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ include file="/common/taglibs.jsp"%>
<html>
<meta name="decorator" content="/template/template21.jsp">
<head>
<style type="text/css">
.searchtitle img {
	display: block;
	float: left;
}
.searchtitle span {
	display: block;
	float: left;
	margin-top: 1px;
	margin-left: 30px;
	line-height: 19px;
}
.l-tab-links {
/* 	position: absolute; */
}
</style>
<script type="text/javascript">
	var app = {
		ctx : '${ctx}',
		queryObj : $.parseJSON('${queryObj}'),
		nodeType : '2'		// nodeType: 1.目录; 2.报表; 3.条线
	};
</script>
<script type="text/javascript" src="${ctx}/js/uuid/uuid.js" ></script>
<script type="text/javascript" src="${ctx}/frs/js/rptquery/rptquery.js" ></script>
<script type="text/javascript" src="${ctx}/frs/js/rptquery/dateCal.js" ></script>
<%@ include file="/common/spreadjs_load.jsp"%>
<script type="text/javascript">

	var tab ;
	var isInit;
	$(function(){
		initTree();
		initBtn();
		initForm();
		rptQuery_initTab();
	})
	function initForm(){
		$("#search").ligerForm({
			fields : [ {
				display : "统计类型", name : "orgType", newline : true, type : "select", cssClass : "field", comboboxName : 'orgTypeBox',
				options : {
					valueFieldID : 'orgType',
					/*data : [{ id : '02', text : '1104' }, { id : '03', text : '人行大集中'}],*/
					data : [{ id : '02', text : '非现场监管统计' }, { id : '03', text : '金融统计'},{id:'01',text:'其他类型'}],
					onSelected : function(value, text) {
						
					}
				},
				attr : {
					op : "like", field : "module.moduleName"
				},
				validate : { required : true }
			}, {
				display : "选择机构", name : "orgNo", newline : false, type : "select", cssClass : "field", comboboxName : 'orgNm_sel',
				options : {
					onBeforeOpen : function() {
						//'${ctx}/frs/systemmanage/orgmanage/getTree'
						if (BIONE.validator.element('#orgTypeBox')) {
							var orgTypeBox = $("#orgTypeBox").val();
							var orgs = $("#orgTypeBox").attr("id");
							var orgType = "";
							if(orgTypeBox =="金融统计"){
								orgType = "03";
							} else if(orgTypeBox =="非现场监管统计"){
								orgType = "02";
							}else{
								orgType = "01";
							}
						
							BIONE.commonOpenDialog("机构树", "taskOrgWin", "450", "600",'${ctx}/frs/integratedquery/rptquery/searchOrgSetInfo?&orgType='+orgType , null);
						}
						return false;
					},
					onSelected : function(value, text) {
						$('#orgNm_sel').blur();
					}
				},
				attr : {
					op : "=", field : "org"
				},
				validate : { required : true }
			}, {
				display : "查询方式", name : "checkType", newline : true, type : "select", cssClass : "field", comboboxName : 'checkTypeCombox',
				options : {
					valueFieldID : 'checkType',
					data : [ { text : '时间点值', id : '01' }, { text : '时间段值', id : '02' } ],
					onSelected : function(value, text) {
						if (value == '01') {
							$('#dataDate').closest('.l-fieldcontainer').show();
							$('#cycle').closest('.l-fieldcontainer').hide();
							$('#startDate').closest('.l-fieldcontainer').hide();
							$('#endDate').closest('.l-fieldcontainer').hide();
							$('#dataDate').removeAttr('disabled');
							liger.get('cycleBox').setDisabled();
							$('#cycle').attr('disabled', 'disabled');
							liger.get('startDate').setDisabled();
							liger.get('endDate').setDisabled();
							//$("#checkTypeCombox").css("width","150px");
						} else if (value == '02') {
							$('#dataDate').closest('.l-fieldcontainer').hide();
							$('#cycle').closest('.l-fieldcontainer').show();
							$('#startDate').closest('.l-fieldcontainer').show();
							$('#endDate').closest('.l-fieldcontainer').show();
							$('#dataDate').attr('disabled', 'disabled');
							liger.get('cycleBox').setEnabled();
							$('#cycle').removeAttr('disabled');
							liger.get('startDate').setEnabled();
							liger.get('endDate').setEnabled();
							//$("#checkTypeCombox").css("width","150px");
						} else if (value ==''){
							$('#dataDate').closest('.l-fieldcontainer').show();
							$('#cycle').closest('.l-fieldcontainer').hide();
							$('#startDate').closest('.l-fieldcontainer').hide();
							$('#endDate').closest('.l-fieldcontainer').hide();
							$('#dataDate').removeAttr('disabled');
							liger.get('cycleBox').setDisabled();
							$('#cycle').attr('disabled', 'disabled');
							liger.get('startDate').setDisabled();
							liger.get('endDate').setDisabled();
							//$("#checkTypeCombox").css("width","150px");
						}
						$('#checkTypeCombox,#cycleBox').blur();
					}
				},
				attr : {
					op : "like", field : "module.moduleName"
				}
			}, {
				display : "查询频度", name : "cycle", newline : false, type : "select", cssClass : "field", comboboxName : 'cycleBox',
				options : {
					valueFieldID : 'cycle', initValue : '01',
					data : [ { text : '日', id : '01' }, { text : '旬', id : '02' }, { text : '月', id : '03' }, { text : '季度', id : '04' }, 
					         { text : '半年', id : '05' }, { text : '年', id : '06' }, { text : '周', id : '07' } ]
				},
				attr : {
					op : "=", field : "module.moduleNo"
				}
			}, {
				display : "时间点值", name : "dataDate", newline : false, type : "date", cssClass : "field",
				options : { 
					// cancelable : false,
					format: 'yyyyMMdd',
					onChangeDate : function(value, text) {
						var dataDateHideVal = $('#dataDateHide').val(), dateArr = [];
						if (dataDateHideVal) {
							dateArr = dataDateHideVal.split(',');
						}
						dateArr.push(value);
						var dateObj = {};
						$(dateArr).each(function(i, n) {
							dateObj[n] = '';
						});
						dateArr = [];
						for (var p in dateObj) {
							dateArr.push(p);
						}
						dataDateHideVal = dateArr.join(',');
						liger.get('dataDate').setValue(dataDateHideVal);
						$('#dataDateHide').val(dataDateHideVal);
						BIONE.validator.element('#dataDate');
					}
				},
				attr : {
					op : "like", field : "module.moduleName"
				},
				validate : { required : true }
			}, {
				display : "开始日期", name : "startDate", newline : true, type : "date", cssClass : "field",
				options : {
					format: 'yyyyMMdd',
					onChangeDate : function(value, text) {
						BIONE.validator.element('#startDate');
					}
				},
				attr : {
					vt : 'date', op : "=", field : "module.moduleNo"
				},
				validate : { required : true }
			}, {
				display : "结束日期", name : "endDate", newline : false, type : "date", cssClass : "field",
				options : {
					format: 'yyyyMMdd',
					onChangeDate : function(value, text) {
						BIONE.validator.element('#endDate');
					}
				},
				attr : {
					vt : 'date', op : "like", field : "module.moduleName"
				},
				validate : { required : true }
			}, {
				display : "展示类型", name : "showType", newline : true, type : "select", cssClass : "field", comboboxName : 'showTypeBox',
				options : {
					valueFieldID : 'showType', initValue : '01',
					data : [ { text : '网页展示', id : '01' }, { text : 'EXCEL', id : '02' }, { text : 'ZIP', id : '03' } ]
				},
				attr : {
					op : "like", field : "module.moduleName"
				}
			}, {
				display : '数据日期', name : 'dataDateHide', newline: false, type : 'hidden', cssClass : 'field',
				attr : {
					op : "like", field : "module.moduleName", disabled : 'disabled'
				}
			}/*, {
				display : '统计类型', name : 'orgType', newline: false, type : 'hidden', cssClass : 'field',
				attr : {
					op : "like", field : "module.moduleName"
				}
			}*/ ]
		});
		//app.rptQuery_addSearchButtons("#search", "#searchbtn");
		// liger.get('checkTypeCombox').setValue('01');
		// 由于 IE7 在页面初始化时无法触发上面这种写法的联动, 为下面这种写法 begin
		$('#dataDate').closest('.l-fieldcontainer').show();
		$('#cycle').closest('.l-fieldcontainer').hide();
		$('#startDate').closest('.l-fieldcontainer').hide();
		$('#endDate').closest('.l-fieldcontainer').hide();
		$('#dataDate').removeAttr('disabled');
		liger.get('cycleBox').setDisabled();
		$('#cycle').attr('disabled', 'disabled');
		liger.get('startDate').setDisabled();
		liger.get('endDate').setDisabled();
		// end
		liger.get('checkTypeCombox').setValue('01');
		$(':input').blur();
		jQuery.metadata.setType("attr", "validate");
		BIONE.validate('#formsearch');
		$('.l-trigger-hover.l-trigger-cancel').live('click', function() {
			var id = $(this).siblings('input').attr('id');
			if (id == 'dataDate') {
				$('#dataDateHide').val('');
			}
		});
	}
	function initBtn(){

		if (!form) return;
		form = $(form);
		if (btnContainer) {
			BIONE.createButton({
				appendTo : btnContainer,
				text : '查询',
				icon : 'search3',
				width : '50px',
				click : function() {
				
					var checkType = liger.get('checkTypeCombox').getValue();
					if (BIONE.validator.element('#orgTypeBox') & BIONE.validator.element('#orgNm_sel') & ((checkType == '01' & BIONE.validator.element('#dataDate'))
							|| (checkType == '02' & BIONE.validator.element('#startDate') & BIONE.validator.element('#endDate')))) {
						var showType = liger.get('showTypeBox').getValue();
					 
						if (showType == '01') {
							var nodes = app.zTreeObj.getCheckedNodes(true);
							var argsArr = app.rptQuery_readExportParms();
							if (argsArr.length < 31) {
								// 封装查询参数
								var parms = app.rptQuery_readQueryParms(nodes);
								// 执行查询
								app.rptQuery_execQuery(parms.argsArr, parms.rptInfo, parms.lineInfo);
							}else{
								BIONE.tip('最终文档将超过 30个标签页(超过了Excel的限制), 请适当减少条件');
							}
						} else if (showType == '02') {
							var argsArr = app.rptQuery_readExportParms();
							if (argsArr.length < 61) {
								// BIONE.showLoading();
								BIONE.ajax({
									type : 'post',
									dataType : 'text',
									url : app.ctx + '/report/frs/rptquery/rptQueryController.mo?_type=data_event&_field=createExcel&_event=POST&_comp=main&Request-from=dhtmlx',
									data : { json : JSON2.stringify(argsArr),showType:"02" }
								}, function(filePath) {
									if (filePath == null) {
										BIONE.tip('无数据可供下载');
									} else {
										$('#download').attr('src', app.ctx + '/report/frs/rptquery/rptQueryController.mo?_type=data_event&_field=downloadFile&_event=POST&_comp=main&Request-from=dhtmlx&filePath=' + encodeURI(encodeURI(filePath)));
									}
								});
							} else {
								BIONE.tip('最终文档将超过 60个标签页(超过了Excel的限制), 请适当减少条件');
							}
						} else if (showType == '03') {
							var argsArr = app.rptQuery_readExportParms();
							if (argsArr.length < 61) {
								BIONE.ajax({
									type : 'post',
									dataType : 'text',
									url : app.ctx + '/report/frs/rptquery/rptQueryController.mo?_type=data_event&_field=createZip&_event=POST&_comp=main&Request-from=dhtmlx',
									data : { json : JSON2.stringify(argsArr) }
								}, function(filePath) {
									if (filePath == null) {
										BIONE.tip('无数据可供下载');
									} else {
										$('#download').attr('src', app.ctx + '/report/frs/rptquery/rptQueryController.mo?_type=data_event&_field=downloadFile&_event=POST&_comp=main&Request-from=dhtmlx&filePath=' +encodeURI(encodeURI(filePath)));
									}
								});
							} else {
								BIONE.tip('最终将生成超过 60个文档, 请适当减少条件');
							}
						}
					}
				}
			});
			BIONE.createButton({
				appendTo : btnContainer,
				text : '重置',
				icon : 'refresh2',
				width : '50px',
				click : function() {
					$(":input", form).not(":submit,:reset,:hidden,:image,:button,[disabled]")
						.each(function() {
							$(this).val("");
						});
					$(":input[ltype=combobox]", form).each(function() {
						var ligerid = $(this).attr('data-ligerid'), ligerItem = $.ligerui.get(ligerid);// 需要配置comboboxName属性
						if (ligerid && ligerItem && ligerItem.clear) {
							// ligerUI 1.2 以上才支持clear方法
							ligerItem.clear();
						} else {
							$(this).val("");
						}
					});
					$(":input[ltype=select]", form).each(function() {
						var ligerid = $(this).attr('data-ligerid'), ligerItem = $.ligerui.get(ligerid);// 需要配置comboboxName属性
						if (ligerid && ligerItem && ligerItem.clear) {
							// ligerUI 1.2 以上才支持clear方法
							ligerItem.clear();
						} else {
							$(this).val("");
						}
					});
				}
			});
		}
	}
	function initTree(){
		var setting = {
				data : {
					key : {
						name : "text"
					},
					simpleData:{
						enable:true,
						idKey: "id",
						pIdKey: "upId"
					}
				},
				check : {
 				    chkStyle : 'checkbox',
 				    enable : true,
 				    chkboxType : {
 					"Y" : "",
 					"N" : ""
 				    }
 				},
				view : {
					selectedMulti : false
				},
				callback : {
					onClick : onRptClick
				}
		};
		var data ={
			text : "",
			nodeType : "2",
			defSrc : "01"
		}
		leftTreeObj = $.fn.zTree.init($("#tree"), setting/* ,[{
			id : '0',
			text : '报表树',
			params : {"nodeType" : 'idxCatalog'},
			data : {"indexCatalogNo" : '0'},
			open : true,
			isParent : true
		}] */);
		loadTree("${ctx}/frs/integratedquery/rptquery/searchRptForTree.json",leftTreeObj,data);
	}
	function loadTree(url, component,data) {
		$.ajax({
			cache : false,
			async : true,
			url : url,
			dataType : 'json',
			type : "post",
			data : data,
			beforeSend : function() {
				BIONE.loading = true;
				BIONE.showLoading("正在加载数据中...");
			},
			complete : function() {
				BIONE.loading = false;
				BIONE.hideLoading();
			},
			success : function(result) {
				if (result.length > 0) {
					component.addNodes(null, result, false);
				}
			}
		});
	}
	function onRptClick(event, treeId, treeNode){
		if (treeNode.nodeType == '2') {	
			// nodeType: 1.目录; 2.报表; 3.条线
			liger.get('orgTypeBox').setValue(treeNode.busiType);
			$('#orgTypeBox').blur();
			grid.currentData && (grid.currentData.Rows = []);	// 先清除备份
			 grid._clearGrid();			// 如果是报表指标查询则清空指标展示区的 Grid
			app.rptId = treeNode.id;	// 报表ID
			app.rptNm = treeNode.text;	// 报表名称
			app.rptNum = treeNode.rptNum;	// 报表编码
			app.busiType = treeNode.busiType;	// 业务类型 */
			
			var ajaxData={
					rptId : treeNode.id,
					rptNm : treeNode.text,
					rptNum : treeNode.rptNum,
					busiType : treeNode.busiType,
					dataDate : "${dataDate}",
					orgNm : "${orgNm}",
					unit : "${unit}"
			}
			
			$('#spread').empty().show();
			require.config({
				baseUrl : '${ctx}/plugin/js/',
				paths: {
					design : 'cfg/views/rptdesign'
				}
			});
			require(["design"] , function(design) {
				var settings = {
					targetHeight : ($("#content").height() - 2),
					ctx : app.ctx,
					readOnly : true,
					cellDetail: false,
					showType: 'cellnm',
					onEnterCell: function(sender, args, rptIdxTmp) {
						if (rptIdxTmp.realIndexNo) {
							var has = $.map(grid.getData(), function(n) {
								if (rptIdxTmp.realIndexNo == n.realIndexNo) {
									return '1';
								}
							});
							(has.length == 0) && grid.addRow($.extend({}, rptIdxTmp, { rptId : app.rptId, rptNm: app.rptNm }));
						}
					},
					initFromAjax : true,
					ajaxData : {
						templateId : treeNode.cfgId
					}
				};
				initSlcanvas(app.ctx);
				try {
					spread = design.init($("#spread") , settings);
				} catch (e) {
					
				}
			});
			
		}
	}

</script>
</head>
<body>
	<div id="template.left.up.icon">
		<img src="${ctx}/images/classics/icons/application_side_tree.png" />
	</div>
	<div id="template.left.up">
		<span id="treeTitle" style="font-size: 12">报表树</span>
	</div>
	<div id="template.left.up.right">
		<%-- <div class="l-text" style="display: block; float: left; height: 17px; margin-top: 2px; width: 117px;">
			<input id="treesearchtext" name="treesearch" type="text" class="l-text l-text-field" style="float: left; height: 16px; width: 117px;" />
		</div>
		<div width="30%" style="display: block; float: left; margin-left: 8px; margin-top: 3px; margin-right: 3px;">
			<a id="treesearchbtn"><img src="${ctx}/images/classics/icons/find.png" /></a>
		</div> --%>
	</div>
	<div id="template.left.down">
		<div id="level" style="line-height: 24px; height: 24px; margin-left: 5px;">
			<table style=" height: 24px;">
				<tr>
					<td>是否级联： 是 </td>
					<td><input type="radio" id="level1" name="level" value="level1" style="width: 20px;" />否 </td>
					<td><input type="radio" id="level2" name="level" value="level2" style="width: 20px; " checked="true" /></td>
				</tr>
			</table>
			<!-- <span style="display: block; float: left;">是否级联： 是 </span>
			<input type="radio" id="level1" name="level" value="level1" style="display: block; float: left;" />
			<span style="display: block; float: left;">否 </span>
			<input type="radio" id="level2" name="level" value="level2" style="display: block; float: left; " checked="true" /> -->
		</div>
	</div>
</body>
</html>