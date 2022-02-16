<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<%@ include file="/common/taglibs.jsp"%>
<html>
<head>
<meta name="decorator" content="/template/template5.jsp">
<script type="text/javascript">
	//全局变量
	var  mainform='';
	var leftTreeObj;
	var data = {rptNm : "",rptNum : ""};
	$(function() {
		initSearchForm();
		BIONE.addSearchButtons("#search", leftTreeObj, "#searchbtn");	
		initMainForm();
		$("#dataDate").val(getDate());
		$("#treeContainer").height($("#center").height()-$("#mainsearch").height()-$("#mainform").height()-40);
		initTree();
		//添加表单按钮
		var btns = [{
			text : "取消",
			onclick : function() {
				setTimeout('BIONE.closeDialog("rptEngineBatchBox");',0);
			}
		}, {
			text : "运行",
			onclick : function() {
				f_save();
			}
		}];
		BIONE.addFormButtons(btns);
		$($(".l-btn-hasicon")[0]).unbind("click");
		$($(".l-btn-hasicon")[0]).click(function(){
			  var   rptNm =  $("#search").find("input[name=rptNm]").val();
			  var   rptNum =  $("#search").find("input[name=rptNum]").val();
			  data.searchNm = rptNm;
			  data.rptNum = rptNum;
			  BIONE.loadTree("${ctx}/report/frame/enginelog/rpt/getRptTree?d=" + new Date().getTime(),leftTreeObj,data);
				 
		});
	});
	
	function getDate(){
		var date = new Date();
		var yy = date.getFullYear();
		var Mm = ((date.getMonth() + 1) < 10) ? ('0' + (date.getMonth() + 1))
				: (date.getMonth() + 1);
		var dd = (date.getDate() < 10) ? ('0' + date.getDate()) : date
				.getDate();
		return yy + '' + Mm + '' + dd;
	}
	
	function initMainForm(){
		//渲染表单
		mainform = $("#mainform");
		mainform
				.ligerForm({
					inputWidth : 160,
					labelWidth : 120,
					space : 30,
					fields : [{
								display : '数据日期',
								id : 'dataDate',
								name : 'dataDate',
								newline : false,
								width : 220,
								type : 'date',
								options : {
									format : "yyyyMMdd"
								}
							} ,{
								display : "是否重跑关联指标",
								name : "isReRun",
								type : "hidden",
								width : 220,
								newline : false
							}]

				});
		$("isReRun").val("N");
	}
	
	 function  f_save(){
		 var dataDate   =  $("#mainform  input[name='dataDate']").val();
		 var nodes = leftTreeObj.getCheckedNodes(true);
		 var checkedRptIds ="";
		 $(nodes).each(function (index, domEle) { 
			  if(domEle.params.nodeType=="03"){//叶子节点
				  checkedRptIds += domEle.id.substring(1,domEle.id.length)+",";
			  }
		  });
		 if(checkedRptIds.length==0){
			 BIONE.tip("请选择报表！");
			 return false;
		 }
		 if($("#dataDate").val()==''){
			 BIONE.tip("数据日期不能为空！");
			 return false;
		 }
		 parent.changeRefreshSts();//当点击运行，立即该表刷新状态，默认10秒

		 //查询报表状态是否是提交过
		 $.ajax({
			 async : false,//同步
			 type : "POST",
			 url : "${ctx}/report/frame/enginelog/rpt/getRptSts",
			 dataType : 'json',
			 data : {
				 checkedRptIds: checkedRptIds,
				 dataDate: dataDate
			 },
			 success: function (result) {
			 	if(result.status){
					$.ligerDialog.confirm(result.msg, function (yes){
						if(yes){
							saveEngineRptSts(checkedRptIds, dataDate);
						}
					})
				}else{
					saveEngineRptSts(checkedRptIds, dataDate);
				}
			 }
		 });
	 }

	 function saveEngineRptSts(checkedRptIds, dataDate){
		 var isReRunsal = "N";
		 $.ajax({
			 async : false,//同步
			 type : "POST",
			 url : "${ctx}/report/frame/enginelog/rpt/isSelectedRptRunning.json",
			 dataType : 'json',
			 data : {
				 checkedRptIds:   checkedRptIds,
				 dataDate:          dataDate
			 },
			 success : function(result) {
				 if(result.msg=="1"){
					 $.ajax({
						 async : false,//同步
						 type : "POST",
						 url : "${ctx}/report/frame/enginelog/rpt/saveOrUpdateEngineRptSts.json",
						 dataType : 'json',
						 data : {
							 isReRunsal :       isReRunsal,
							 checkedRptIds:   checkedRptIds,
							 dataDate:          dataDate
						 },
						 success : function(rs) {
							 window.parent.parent.BIONE.tip(rs.msg);
							 window.parent.grid.loadData();
							 parent.quickRefresh();//指令发送完成后立即执行自动刷新
							 BIONE.closeDialog("rptEngineBatchBox");
						 },
						 beforeSend : function() {
							 BIONE.loading = true;
							 BIONE.showLoading("正在操作中...");
						 },
						 complete : function() {
							 BIONE.loading = false;
							 BIONE.hideLoading();
						 },
						 error : function(XMLHttpRequest, textStatus, errorThrown) {
							 BIONE.tip('操作失败,错误信息:' + textStatus);
						 }
					 });
				 }else if(result.msg=="0"){
					 var   rptNames  =  result.rptNames;
					 BIONE.tip('所选报表['+rptNames+']还未完成执行任务！');
				 }
			 },
			 error : function(XMLHttpRequest, textStatus, errorThrown) {
				 BIONE.tip('获取数据失败,错误信息:' + textStatus);
			 }
		 });
	 }

	 function initTree(){
		 var async = {
				    check : {
						enable : true,
						chkStyle : "checkbox",
						chkboxType: { "Y": "ps", "N": "ps" }
					},
					data : {
						keep : {
							parent : true
						},
						key : {
							name : "text",
							title : "title"
						},
						simpleData : {
							enable : true,
							idKey : "id",
							pIdKey : "upId",
							rootPId : null
						}
					},
					view : {
						selectedMulti : false,
						showLine : false
					}
				};
		 leftTreeObj = $.fn.zTree.init($("#tree"),async,[]);
		 BIONE.loadTree("${ctx}/report/frame/enginelog/rpt/getRptTree?d=" + new Date().getTime(),leftTreeObj,data);
	 }
	 function initSearchForm() {
			//搜索表单应用ligerui样式
			$("#search").ligerForm({
				fields : [ {
					display : '报表编号',
					name : 'rptNum',
					newline : true,
					labelWidth : 100,
					width : 220,
					space : 30,
					type : "text",
					cssClass : "field"
				} ,{
					display : '报表名称',
					name : "rptNm",
					newline : false,
					labelWidth : 100,
					width : 220,
					space : 30,
					type : "text",
					cssClass : "field"
				} ]
			});
		};
</script>
<title></title>
</head>
<body>
	<div id="template.center">
	   <div id="mainsearch"  style="width:99%">
			<div id="searchbox" class="searchbox">
				<form id="formsearch">
					<div id="search"></div>
					<div class="l-clear"></div>
				</form>
				<div id="searchbtn" class="searchbtn"></div>
			</div>

		</div>
		<div id="treeContainer"
			style="width: 99%; overflow: auto; clear: both; background-color: #FFFFFF;">
			<ul id="tree"
				style="font-size: 12; background-color: #FFFFFF; width: 92%"
				class="ztree"></ul>
		</div>
		<form id="mainform"
			action=""
			method="POST"></form>
	</div>
</body>
</html>