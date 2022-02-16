<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ include file="/common/taglibs.jsp"%>
<html>
<meta name="decorator" content="/template/template8.jsp">
<head>
<%@ include file="/common/meta.jsp"%>
<script type="text/javascript">
	var app = {
		ctx : '${ctx}'
	};
	var grid = null;
</script>
<script type="text/javascript">
	$(function() {
		initExport();
		initGrid();
		initBtn();
	});
	
	function initGrid(){
		grid = $("#maingrid").ligerGrid({
			columns : [ 
		   		{
		   			display : '机构名称', name : 'orgNm', align : 'center', minWidth : '', width : ''
		   		}, {
		   		/* 	display : '币种名称', name : 'dimItemNm', align : 'center', minWidth : '', width : ''
		   		}, { */
		   			display : '数据日期', name : 'dataDate', align : 'center', minWidth : '', width : ''
		   		}, {
		   			display : '报表编码', name : 'rptNum', align : 'center', minWidth : '', width : ''
		   		}, {
		   			display : '报表名称', name : 'rptNm', align : 'center', minWidth : '', width : ''
		   		}/* , {
		   			display : '指标编号', name : 'indexNo', align : 'center', minWidth : '', width : ''
		   		} */, {
		   			display : '单元格编号', name : 'indexNm', align : 'center', minWidth : '', width : ''
		   		}, {
		   			display : '指标值', name : 'indexVal', align : 'center', minWidth : '', width : ''
		   		} 
		   	], 
			onLoaded: function(rowdata, index, value, column) {
				var rows = rowdata.data.Rows, indexInfo = parent.app.indexInfo, orgInfo = parent.app.orgInfo;
				var rptId = parent.app.rptId, rptNm = parent.app.rptNm, rptNum = parent.app.rptNum;
				for (var i = 0, len = rows.length; i < len; i++) {
					var row = rows[i];
					// 下次若有问题, 直接将 Mapper.xml 中的 searchRptidxInfo 方法 去掉 rpt_idx_info 关联, 
					// 而使用此处的 indexInfo 获取 indexNm
					$.extend(row, {
						rptId : rptId,
						rptNm : rptNm,
						rptNum : rptNum,
						orgNm : orgInfo[row.orgNo],
						indexNm : indexInfo[row.indexNo]
					});
				}
			},
			checkbox : false,
			usePager : true,
			isScroll : false,
			rownumbers : false,
			alternatingRow : true, /* 附加奇偶行效果行 */
			colDraggable : true,	/* 是否允许表头拖拽 */
			dataAction : 'server',	/* 从后台获取数据 */
			method : 'post',
			sortName : '', // dataDate	//第一次默认排序的字段
			sortOrder : '',	// desc	// 由于此处数据较多, 加上此两段严重影响性能
			delayLoad : true,
			url : app.ctx + '/frs/integratedquery/rptidxquery/info/searchRptidxInfo.json',
			height : '100%',
			width : '100%',
			toolbar: {}
		});	
		
		// grid.setParm('p', JSON2.stringify(parent.app.queryParms));
		// grid.set('parms', $.extend({}, parent.app.queryParms, { page:1, pagesize:20, sortname:'dataDate', sortorder:'desc' }));
		// grid.set('parms', $.extend({}, parent.app.queryParms, { page:1, pagesize:20 }));
		// grid._setUrl(app.ctx + '/rpt/frs/RptidxQuery/searchRptidxInfo.json');
		grid.setParm('indexNo', parent.app.queryParms.indexNo);
		grid.setParm('orgNo', parent.app.queryParms.orgNo);
		if(parent.app.queryParms.dataDate!=null)
			grid.setParm('dataDate', parent.app.queryParms.dataDate);
		if(parent.app.queryParms.startDate!=null)	
			grid.setParm('startDate',parent.app.queryParms.startDate);
		if(parent.app.queryParms.endDate!=null)	
			grid.setParm('endDate',parent.app.queryParms.endDate);
		grid.loadData();
	}
	
	function initBtn(){
		var btns = [{
			text : '导出',
			click : exportData,
			icon : 'export'
		}];
		
		
		BIONE.loadToolbar(grid, btns, function() {
		});
	}
	
	function exportData(fileName){
		var data={};
		data.indexNo=parent.app.queryParms.indexNo;
		data.orgNo=parent.app.queryParms.orgNo;
		if(parent.app.queryParms.dataDate!=null)
			data.dataDate=parent.app.queryParms.dataDate + "";
		if(parent.app.queryParms.startDate!=null)	
			data.startDate=parent.app.queryParms.startDate;
		if(parent.app.queryParms.endDate!=null)	
			data.endDate=parent.app.queryParms.endDate;
		data.rptNm=parent.app.rptNm;
		data.rptNum = parent.app.rptNum;
		data.orgInfo = JSON2.stringify(parent.app.orgInfo);
		$.ajax({
			async:true,
			type:"POST",
			dataType:"json",
			url:"${ctx}/report/frs/rptquery/rptidxQueryController.mo?_type=data_event&_field=searchRptidxCache&_event=POST&_comp=main&Request-from=dhtmlx",
			data: data,
			beforeSend : function(a, b, c) {
				BIONE.showLoading('正在导出数据中...');
			},
			success:function(data){
				BIONE.hideLoading();
				var src = '';
				src = "${ctx}/report/frs/rptquery/rptidxQueryController.mo?_type=data_event&_field=exportTmp&_event=POST&_comp=main&Request-from=dhtmlx&filepath="+data.fileName+"&d="+ new Date();
				downdload.attr('src', src);
			},
			error : function(result) {
				BIONE.hideLoading();
				//BIONE.tip('发现系统错误 <BR>错误码：' + result.status);
			}
		});
		
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