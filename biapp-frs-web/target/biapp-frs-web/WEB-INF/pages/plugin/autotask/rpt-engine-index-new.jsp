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
	var indexNo;
	var data = {'isShowIdx':'1','isShowMeasure':0,isEngine:"",'isPublish':1,indexNm:"",indexNo:""};
	var taskType = '${taskType}';
	$(function() {
		initSearchForm();
		BIONE.addSearchButtons("#search", leftTreeObj, "#searchbtn");	
		initMainForm();
		$("#dataDate").val(getDate());
		$("#treeContainer").height($("#center").height()-$("#mainsearch").height()-$("#mainform").height()-20);
		 if(taskType == "01"){
			 data.isEngine = "1";
		 }
		 if(taskType == "03"){
			 data.isEngine = "2";
		 }
		initTree();
		//添加表单按钮
		var btns = [{
			text : "取消",
			onclick : function() {
				setTimeout('BIONE.closeDialog("newIndexTaskBatchBox"+taskType);',0);
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
			 var indexNm =  $("#search").find("input[name=indexNm]").val();
			 var indexNo =  $("#search").find("input[name=indexNo]").val();
			 data.indexNm = indexNm;
			 data.indexNo = indexNo;	
			 BIONE.loadTree("${ctx}/report/frame/idx/getSyncTree",leftTreeObj,data);
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
							}]

				});
	}
	
	 function  f_save(){
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
		 }
		 if($("#dataDate").val()==''){
			 BIONE.tip("数据日期不能为空！");
			 return false;
		 }
		// parent.changeRefreshSts();//当点击运行，立即该表刷新状态，默认10秒
		 $.ajax({
			    async : false,
				type : "POST",
				url : "${ctx}/rpt/frame/rptenginetsk/saveOrUpdateAutoTask.json",
				dataType : 'json',
				data : {
					checkedTaskIds:   checkedIndexIds    ,
					dataDate:          dataDate   ,
					taskType:          taskType
				},
				success : function(result) {
					window.parent.grid.loadData();
					BIONE.closeDialog("newIndexTaskBatchBox"+taskType);
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
		 BIONE.loadTree("${ctx}/report/frame/idx/getSyncTree",leftTreeObj,data);
	 }
	 function initSearchForm() {
			//搜索表单应用ligerui样式
			$("#search").ligerForm({
				fields : [{
					display : '指标编号',
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
	   <div id="mainsearch"  style="width:99.8%">
			<div id="searchbox" class="searchbox">
				<form id="formsearch">
					<div id="search"></div>
					<div class="l-clear"></div>
				</form>
				<div id="searchbtn" class="searchbtn"></div>
			</div>

		</div>
		<div id="treeContainer"
			style="width: 100%; overflow: auto; clear: both; background-color: #FFFFFF;">
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