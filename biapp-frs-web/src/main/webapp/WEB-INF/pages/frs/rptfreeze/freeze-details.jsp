<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<%@ include file="/common/meta.jsp"%>
<html>
<head>
<meta name="decorator" content="/template/template28.jsp">
<%@ include file="/common/taglibs.jsp"%>
<%@ include file="/common/spreadjs_load.jsp"%>
<style type="text/css">
.l-btn {
	font-size: 12px;
	display: block;
	line-height: 29px;
	overflow: hidden;
	height: 29px;
	position: relative;
	padding-left: 3px;
	padding-right: 3px;
	cursor: pointer;
/*  	background: #D9D9D9 url(../ligerUI/Gray/images/ui/btn.png) repeat-x; */
	text-align: center;
	color: #000000;
	text-decoration: none;
	cursor: pointer;
	float: left;
	text-align: center;
	margin-left: 3px;
	margin-right: 3px;
}
</style>
<script type="text/javascript">
	var app = {
		ctx : '${ctx}',
		queryObj : null,//$.parseJSON('${queryObj}')
		nodeType : '2'		// nodeType: 1.目录; 2.报表; 3.条线
	};
</script>
<script type="text/javascript">
	//var operType ="${operType}";
	var sts="${sts}";
	var taskInstanceId = "${taskInstanceId}";
	//var strs = "${strs}";
	//var moduleType="${moduleType}";
	var orgNo = "${orgNo}"; 
	var orgName = "${orgNm}";
	var View;
	$(function() {
		$("#top").html('<div id="button" style="margin-left: 10px ;padding: 0px auto;position: relative;"></div><div id="queryBox" style="margin-left: 10px ;padding: 0px auto;position: relative;"></div>');
		tmp.initButtons();
		tmp.initSearchForm();
		tmp.initDesign(window.parent.color);

		//tmp.initGridData();
	});
	var fl = false;
	var rl = null;
	var c = null;
	var download=null;
	var tabFalg=false;
	var View;
	var Spread;
	var objTmp;
	var Logic=null;
	var Sumpart=null;
	var Warn=null;
	
	var selIdxs = []; // 选择的指标信息
	
	var checkedType="NO";//校验类型 
	
	var tmp = {

		jsonStr : null,
		tmpId: "",
		verId: "",
		//TAB
		logicUrl: '',
		sumpartUrl: '',
		warnUrl: '',
		logicFlag: true,
		sumpartFlag: true,
		warnFlag: true,
		//urlTmp : ['${ctx}/report/frs/rptfill/rptFillTabController.mo?_type=data_event&_field=logicCacheInfo&_event=POST&_comp=main&Request-from=dhtmlx'],
		RptIdxInfo : null,
		//form
		mainform : null
	};

	tmp.back = function() {
		$.ligerDialog.confirm("确定返回？", function(yes) {
			if(yes){
				BIONE.closeDialog("approveRejWin");
			}
		});
			
	};
	tmp.initSearchForm = function(){
		$("#queryBox").ligerForm({
		/* 	labelWidth : 80,
			inputWidth : 160,
			space : 30, */
			fields : [ 
			{
				display : "机构名称",
				name : "orgNm",
				newline : false,
				type : "select",
				labelWidth : '100',
				comboboxName : "orgNm_sel",
					options : { 
						valueFieldID : "orgId", 
						url : "${ctx}/bione/frs/rptfreeze/getOrgList?taskId=${taskId}&taskObjId=${taskObjId}&dataDate=${dataDate}", 
						ajaxType : "post",
						onSelected : function(value) {
							if ("" != value) {
								//orgNo = value; 
								//orgName = $.ligerui.get("orgNm_sel").selectedText;
								//tmp.initDesign(window.parent.color);
								argsArr = [];
								var args1 = {'DimNo':'ORG','Op':'=','Value':value};
								argsArr.push(args1);
								View._settings.ajaxData.orgNm = $.ligerui.get("orgNm_sel").selectedText;
								//View._settings.ajaxData.dates = data.dataDate;
								View._settings.searchArgs = JSON2.stringify(argsArr),
								View.ajaxJson();
							}
						}, 
						cancelable  : true,dataFilter : true
					}
			}]
		});
	};
	// 初始化设计器
	tmp.initDesign = function(color) {
		require.config({
			baseUrl : "${ctx}/plugin/js/",
			paths:{
				"view" : "show/views/rptview"
			}
		});
		require(["view"] , function(view){
			var jQdom = $('#spread');
			var rptId = "${taskObjId}";
			var dataDate = "${dataDate}";
			var argsArr = [];
			var args1 = {'DimNo':'ORG','Op':'=','Value':orgNo};
			//var args2 = {'dimNo':'busiLineId','op':'=','value':lineId};
			argsArr.push(args1);
			var settings = {
			targetHeight : ($("#spread").height()),
			ctx : app.ctx,
			readOnly : true ,
			cellDetail: false,
			initFromAjax : true,
			searchArgs : JSON2.stringify(argsArr),
			ajaxData : {
				rptId : rptId,
				dataDate : dataDate,
				busiLineId : '',
				orgNm : orgName
				}
			};
			View = view;
			spread = view.init($("#spread") , settings);
		});
	};
	
	tmp.buttonObject1 = [
			{ text : '返回', width : '50px', appendTo : '#button', operNo:'backBtn', click : tmp.back}
			];
	tmp.initButtons = function() {
		for(i in tmp.buttonObject1)
			BIONE.createButton(tmp.buttonObject1[i]);
	};
</script>
</head>
<body>
</body>

</html>