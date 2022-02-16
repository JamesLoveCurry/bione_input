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
	var searchNm;
	$(function() {
		initSearchForm();
		BIONE.addSearchButtons("#search", leftTreeObj, "#searchbtn");	
		initMainForm();
		$("#dataDate").val(getDate());
		$("#treeContainer").height($("#center").height()-$("#mainsearch").height()-$("#mainform").height()-40);
		//添加表单按钮
		var btns = [{
			text : "取消",
			onclick : function() {
				setTimeout('BIONE.closeDialog("addDependencyIdxDialog");',0);
			}
		}, {
			text : "运行",
			onclick : function() {
				f_save();
			}
		}];
		BIONE.addFormButtons(btns);
		initTree('','');
		$($(".l-btn-hasicon")[0]).unbind("click");
		$($(".l-btn-hasicon")[0]).click(function(){
			 indexNm =  $("#search").find("input[name=indexNm]").val();
			 indexNo =  $("#search").find("input[name=indexNo]").val();
			 initTree(indexNm, indexNo);
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
		mainform.ligerForm({
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
					}]

		});
	}
	
	 function  f_save(){
		 var isReRunsal = "Y";
		 var dataDate   =  $("#mainform  input[name='dataDate']").val();
		 var nodes = leftTreeObj.getCheckedNodes(true);
		 var checkedIndexIds ="";
		 $(nodes).each(function (index, domEle) { 
			  if(domEle.params.nodeType=="idxInfo"){
				  checkedIndexIds += domEle.id+",";
			  }
		  });
		 if(checkedIndexIds.length==0){
			 BIONE.tip("请选择指标！");
			 return false;
		 }else if(checkedIndexIds.length > 10){
			 BIONE.tip("最多只能选择10个指标，请重新选择！");
			 return false;
		 }
		 if($("#dataDate").val()==''){
			 BIONE.tip("数据日期不能为空！");
			 return false;
		 }
		 parent.changeRefreshSts();//当点击运行，立即该表刷新状态，默认10秒
		 $.ajax({
			    async : false,//同步
				type : "POST",
				url : "${ctx}/report/frame/enginelog/idx/isSelectedIndexRunning.json",
				dataType : 'json',
				data : {
					checkedIndexIds:   checkedIndexIds    ,
					dataDate:          dataDate
				},
				success : function(result) {
					if(result.msg=="1"){
							$.ajax({
								async : false,//同步
				 				type : "POST",
				 				url : "${ctx}/report/frame/enginelog/idx/saveOrUpdateEngineIdxSts.json",
				 				dataType : 'json',
				 				data : {
				 					isReRunsal :       isReRunsal,
				 					checkedIndexIds:   checkedIndexIds,
				 					dataDate:          dataDate
				 				},
				 				success : function(rs) {
				 						BIONE.tip(rs.msg);
					 					window.parent.grid.loadData();
					 					parent.quickRefresh();//指令发送完成后立即执行自动刷新
					 					BIONE.closeDialog("addDependencyIdxDialog");
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
						var   indexNames  =  result.indexNames;
						BIONE.tip('所选指标['+indexNames+']还未完成执行任务！');
					}		
				},
				error : function(XMLHttpRequest, textStatus, errorThrown) {
					BIONE.tip('获取数据失败,错误信息:' + textStatus);
				}
			});
	 }
	 
	 function initTree(indexNm, indexNo){
		 if(indexNm == '' && indexNo == ''){
			 var url_ =  "${ctx}/report/frame/idx/getAsyncTreeIdxShow.json?isShowIdx=1&isShowMeasure=0&isPublish=1&isAuth=1&isEngine=3&t="
					+ new Date().getTime();
			 var setting ={
				async:{
					enable:true,
					url:url_,
					autoParam:["nodeType", "id", "indexVerId"],
					dataType:"json",
					dataFilter:function(treeId,parentNode,childNodes){
						if(childNodes){
							for(var i = 0;i<childNodes.length;i++){
								childNodes[i].nodeType = childNodes[i].params.nodeType;
								childNodes[i].indexVerId = childNodes[i].params.indexVerId;
							}
						}
						return childNodes;
					}
				},
				data:{
					key:{
						name:"text",
						title:"title"
					}
				},
				view:{
					selectedMulti:false
				}
			};
			leftTreeObj = $.fn.zTree.init($("#tree"),setting,[]);
		 }else{
			var _url = "${ctx}/report/frame/idx/getSyncTree";
			var data = {'isShowIdx':'1', 'isShowMeasure':0, 'isEngine':"3", 'isPublish':1, 'indexNm':indexNm, 'indexNo':indexNo, 'isAuth': 1};
			var setting ={
					data : {
						keep : {
							parent : true
						},
						key : {
							name : "text"
						},
						simpleData : {
							enable : true,
							idKey : "id",
							pIdKey : "upId",
							rootPId : null
						}
					},
					view:{
						selectedMulti:false
					}
			};
			leftTreeObj = $.fn.zTree.init($("#tree"),setting,[]);
			BIONE.loadTree(_url,leftTreeObj,data,function(childNodes){
				if(childNodes){
					for(var i = 0;i<childNodes.length;i++){
						childNodes[i].nodeType = childNodes[i].params.nodeType;
						childNodes[i].indexVerId = childNodes[i].params.indexVerId;
					}
				}
				return childNodes;
			},false);
		 }
	}
	
	 function initSearchForm() {
			//搜索表单应用ligerui样式
			$("#search").ligerForm({
				fields : [{
					display : '指标标识',
					name : 'indexNo',
					newline : true,
					labelWidth : 100,
					width : 220,
					space : 30,
					type : "text",
					cssClass : "field"
				}, {
					display : '指标名称',
					name : "indexNm",
					newline : false,
					labelWidth : 100,
					width : 220,
					space : 30,
					type : "text",
					cssClass : "field"
				}  ]
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
			style="width: 99%; overflow: auto;  clear: both; background-color: #FFFFFF;">
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