<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<%@ include file="/common/taglibs.jsp"%>
<html>
<head>
<meta name="decorator" content="/template/template5.jsp">
<style >
	#left {
	 border-width: 1px;
	border-style: solid;
	border-color: #D6D6D6; 
}
#right {
	border-width: 0px;
	border-style: solid;
	border-color: #D6D6D6; 
	background-color: #FCFCFC;
}
#tree {
	background-color: #F1F1F1;
}
.l-layout-center {
	border: none;
}
.noSelectText{
		-moz-user-select:none;
	}
	.haveBorder{
		border: 1px solid #999;
	}
</style>
<script type="text/javascript" src="${ctx}/plugin/js/idx/cursorPosition.js"></script>
<script type="text/javascript">
	var checkForm = null;
	var lPosition = {start:0, end:0};
	var rPosition = {start:0, end:0};
	var flag = true;
	var spread;
	var select;
	var designInfo4Upt; //报表设计器信息
	var leftTreeObj, items,currentNode;
	var searchObj = {exeFunction : "initTree",searchType : "idx"};//默认执行initTree方法
	var treeRootNo = '${treeRootNo}';  //参数在开始显示主界面时传入
	var rootIcon = '${rootIcon}';
	var indexCatalogNo = '${indexCatalogNo}';
	var indexNo = '${indexNo}';
	var indexNm = '${indexNm}';
	var defSrc = '${defSrc}';
	var checkId = '${checkId}';
	var funcTreeObj;
	var symbolTreeObj;
	
	function savePos(textBox){
		lPosition = cursorPosition.get(textBox);
	}
	
	function saveRPos(textBox){
		rPosition = cursorPosition.get(textBox);
	}
	
	function initLayout(){
		$("#left").height($("#center").height()-10);
		$("#right").height($("#center").height()-10);
		$("#right").width($("#center").width()-320);
		$("#treeContainer").height($("#left").height()-93);
	}
	
	function initAccording(){
		$("#accordion").ligerAccordion({
			height: $("#center").height()-2
         });
	}
	$(function(){
		
		initLayout();
		initAccording();
		initTree();
		initForm();
		initFuncTree();
		initSymbolTree();
		if(indexNo==null||indexNo==""){	
		}else{
			initData(indexNo, indexNm);
			flag = false;
		}
		$("#checkNm").focus(); 
		addEvent();
		addTipPic();
		addFormButton();
		initInfo();
		
	
	});
	function callGrid(){
		f_cache();
	}
	function initForm(){
		checkForm = $("#checkForm").ligerForm({
			labelWidth: 100,
			space : 200,
			fields : [ {
				display : '校验公式名称',
				name : 'checkNm',
				newline : true,
				type : 'text',
				width: 600,
				validate : {
				    required : true,
				    remote:{
						url:"${ctx}/report/frame/rptvalid/logic/testSameCheckNm",
						type : "post",
						data : {
							checkId : "${checkId}"?"${checkId}":""
						}
					},
					messages:{remote:"该校验名称已存在"}
				}
			},{
				display : '校验指标名称',
				name : 'lexpression',
				newline : true,
				type : 'text'
			},{
				display : '运算符',
				name : 'oper',
				newline : false,
				type : 'select',
				options : {
					initValue : "==",
					data :[{
						id : "<=",
						text : "<="
					},{
						id : "<",
						text : "<"
					},{
						id : "==",
						text : "=="
					},{
						id : ">",
						text : ">"
					},{
						id : ">=",
						text : ">="
					},{
						id : "!=",
						text : "!="
					}]
				}
			}, {
				display : "容差值",
				name : "floatVal",
				newline : true,
				type : "number"
			 },{
				display : "开始日期",
				name : "startDate",
				newline : false,
				type : "date",
				options : {
					format : "yyyyMMdd"
				},
				validate : {
					required : true,
					validStartDate : false,
					messages : {
						required : "开始日期不能为空。"
					}
				}
			},{
				display : "校验公式",
				name : "rexpression",//edit by fangjuan 2014-07-22
				newline : true,
				width: 600,
				type : "textarea"
			},{
				display : "业务说明",
				name : "busiExplain",//edit by fangjuan 2014-07-22
				newline : true,
				width: 600,
				type : "textarea"
			}]
		});
		jQuery.metadata.setType("attr", "validate");
		BIONE.validate($("#checkForm"));
		$("#rexpression").height(150);
		$("#busiExplain").height(150);
		$("#checkForm input[name=lexpression]").attr("readonly", "true");

	}
	function initData(id, text){
		if(flag){
			var lPosition = cursorPosition.get(document.getElementById("lexpression"));
			cursorPosition.add(document.getElementById("lexpression"), lPosition, "I('"+ text + "')");
			lPosition.start += ("I('"+ text + "')").length;
			lPosition.end += ("I('"+ text + "')").length;
		}else{
			var rPosition = cursorPosition.get(document.getElementById("rexpression"));
			cursorPosition.add(document.getElementById("rexpression"), rPosition, "I('"+ text + "')");
			rPosition.start += ("I('"+ text + "')").length;
			rPosition.end += ("I('"+ text + "')").length;
		}
	}
	
	function addFormButton(){
		var btns = [  {
			text : "取消",
			onclick : function(){
				window.parent.parent.BIONE.closeDialog("logicAdd");
			} 
		},{
			text : "下一步",
			onclick : function() {
				f_cache();
			}
		}];
		BIONE.addFormButtons(btns);
	}
	
	function f_cache() {
		jQuery.metadata.setType("attr", "validate");
		BIONE.validate("#checkForm");
		if($("#checkForm").valid()){
			
			if ($("#lexpression").val() == "") {
				BIONE.tip("左表达式不能为空，请修改!");
				return;
			}
			if ($("#rexpression").val() == "") {
				BIONE.tip("右表达式不能为空，请修改!");
				return;
			}

			$.ajax({
						cache : false,
						async : false,
						url : "${ctx}/report/frame/rptvalid/logic/replaceExpression",
						dataType : 'json',
						type : "POST",
						data : {
							"leftExpression" : $("#lexpression").val(),
							"rightExpression" : $("#rexpression").val()
						},
						success : function(data) {
							if (!data.message) {
								window.parent.validObj.checkNm = $("#checkNm").val();
								window.parent.validObj.leftExpression = data.leftExpression;
								window.parent.validObj.rightExpression = data.rightExpression;
								
								window.parent.validObj.expressionDesc = "(" + $("#lexpression").val() + ")" + $(
								"input[data-ligerid=oper]").val() + "(" + $("#rexpression").val() + ")";
								
								window.parent.validObj.logicOperType = $(
										"input[data-ligerid=oper]").val();
								window.parent.validObj.leftFormulaIndex = data.leftFormulaIndex;
								window.parent.validObj.rightFormulaIndex = data.rightFormulaIndex;
								window.parent.validObj.saveData = f_save;
								window.parent.next();
							} else {
								BIONE.tip(data.message);
							}
						},
						error : function(result, b) {
							BIONE.tip('发现系统错误 <BR>错误码：' + result.status);
						}
					});
		}
	}
	
	function addEvent(){
		$(".l-treetoolbar-item").live("mouseover", function(){
			$("#tip").html(this.attributes[1].nodeValue);
			addTipPic();
		});
		
		$(".l-treetoolbar-item").live("click", function(){
			var val =  this.getElementsByTagName("span")[0].innerHTML;
			
			val = val.replace("&gt;", ">");
			val = val.replace("&lt;", "<");
			var reg = new RegExp("amp;", "g");
			val = val.replace(reg, "");
			
			
			if(flag){
				var lPosition = cursorPosition.get(document.getElementById("lexpression"));
				cursorPosition.add(document.getElementById("lexpression"), lPosition, val);
				lPosition.start += val.length;
				lPosition.end += val.length;
			}else{
				var rPosition = cursorPosition.get(document.getElementById("rexpression"));
				cursorPosition.add(document.getElementById("rexpression"), rPosition, val);
				rPosition.start += val.length;
				rPosition.end += val.length;  
			}
		});
		
		$("#checkNm").live("mouseover", function(){
			$("#tip").html("校验公式名称");
			addTipPic();
		});
		$("#lexpression").live("mouseover", function(){
			$("#tip").html("校验指标名称（左表达式）");
			addTipPic();
		});
		$("#rexpression").live("mouseover", function(){
			$("#tip").html("校验公式（右表达式）");
			addTipPic();
		});
		$("#floatVal").live("mouseover", function(){
			$("#tip").html("容差值");
			addTipPic();
		});
		$("#startDate").live("mouseover", function(){
			$("#tip").html("开始日期");
			addTipPic();
		});
		$("#busiExplain").live("mouseover", function(){
			$("#tip").html("业务说明");
			addTipPic();
		});
		$("#oper").prev("div").find("input").live("mouseover", function(){
			$("#tip").html("逻辑比较符");
			addTipPic();
		});
		$("#treeSearchbar").live("mouseover", function(){
			$("#tip").html("指标筛选");
			addTipPic();
		});
		$("#treeContainer").live("mouseover", function(){
			$("#tip").html("指标树");
			addTipPic();
		});
		$("#treeContainer1").live("mouseover", function(){
			$("#tip").html("运算符");
			addTipPic();
		});
		$("#treeContainer2").live("mouseover", function(){
			$("#tip").html("计算公式");
			addTipPic();
		});
		$("#rexpression").bind("click", function(){
			flag = false;
		}); 

	}
	 function initInfo(){
		//修改时初始化表单
		if ("${checkId}" != "" && "${checkId}" != null) {
			
			$.ajax({
				cache : false,
				async : false,
				url : "${ctx}/report/frame/rptvalid/logic/${checkId}",
				dataType : 'json',
				type : "GET",
				beforeSend : function() {
					BIONE.loading = true;
					BIONE.showLoading();
				},
				complete : function() {
					BIONE.loading = false;
					BIONE.hideLoading();
				},
				success : function(data){
					$.ligerui.get('oper').selectValue(data.logicOperType);
					$.ligerui.get('oper').setText(data.logicOperType);
					var flag = 1;var i=1;
					
					if (data.expressionDesc) {
								for (i = 1; i < data.expressionDesc.length; i++) {
									if (data.expressionDesc.charAt(i) == "(") {
										flag++;
									} else if (data.expressionDesc.charAt(i) == ")") {
										flag--;
									}
									if (flag == 0) {

										break;
									}

								}
							}
					        $("#checkNm").val(data.checkNm);
					        $("#floatVal").val(data.floatVal);
					        $("#startDate").val(data.startDate);
							$("#lexpression").val(
									data.expressionDesc.substring(1, i));
							lPosition.start += data.expressionDesc.substring(1, i).length;
							lPosition.end += data.expressionDesc.substring(1, i).length;
							
							$("#rexpression").val(
									data.expressionDesc.substring(i + 2 + data.logicOperType.length,
											data.expressionDesc.length - 1));
							
							rPosition.start += data.expressionDesc.substring(i + 3, data.expressionDesc.length - 1).length;
							rPosition.end += data.expressionDesc.substring(i + 3, data.expressionDesc.length - 1).length;
					
							window.parent.validObj.checkNm = data.checkNm;
							window.parent.validObj.isPre = data.isPre;
							window.parent.validObj.isSelfDef = data.isSelfDef;
							window.parent.validObj.floatVal = data.floatVal;
							window.parent.validObj.startDate = data.startDate;
							window.parent.validObj.endDate = data.endDate;
							window.parent.validObj.busiExplain = data.busiExplain;

						},
						error : function(result, b) {
							BIONE.tip('发现系统错误 <BR>错误码：' + result.status);
						}
					});

		}
	} 
	//保存
    function f_save() {
    	jQuery.metadata.setType("attr", "validate");
    	BIONE.validate("#checkForm");
    	if($("#checkForm").valid()){
    	var data = {
    			isPre : '0',
    			floatVal : $("#checkForm input[name='floatVal']").val(),
    			startDate : $("#checkForm input[name='startDate']").val(),
    			endDate : '29991231',
    			expressionDesc : window.parent.validObj.expressionDesc,
    			leftExpression : window.parent.validObj.leftExpression,
    			rightExpression : window.parent.validObj.rightExpression,
    			logicOperType : window.parent.validObj.logicOperType,
    			checkId : window.parent.validObj.checkId,
    			checkNm : window.parent.validObj.checkNm,
    			busiExplain : $("#checkForm textarea[name='busiExplain']").val(),
    			leftFormulaIndex : window.parent.validObj.leftFormulaIndex,
    			rightFormulaIndex : window.parent.validObj.rightFormulaIndex
    			
    	}
    	
    	$.ajax({
    		cache : false,
    		async : true,
    		url : "${ctx}/report/frame/rptvalid/logic",
    		dataType : 'json',
    		data : data,
    		type : "post",
    		beforeSend : function() {
    			BIONE.loading = true;
    			BIONE.showLoading("正在加载数据中...");
    		},
    		complete : function() {
    			BIONE.loading = false;
    			BIONE.hideLoading();
    		},
    		success : function(result) {
    			BIONE.tip("保存成功");
    			window.parent.parent.logicWindow.reloadGrid();
    			window.parent.BIONE.closeDialog("logicAdd");
    		},
    		error : function(result, b) {
    			BIONE.tip('发现系统错误 <BR>错误码：' + result.status);
    		}
    	});
    }
    }

	function addTipPic() {
		var tipIcon = "${ctx}/images/classics/icons/lightbulb.png";
		$("#tip").prepend(
				"<div style='width:24px;float:left;height:16px;background:url("
						+ tipIcon + ") no-repeat' />");
	}

	function initTree(searchNm,searchObj) {
		if(searchObj == undefined){
			var url_ =  "${ctx}/report/frame/idx/getAsyncTreeIdxShow.json?isShowIdx=1&isShowMeasure=1&showEmptyFolder=1&t="
					+ new Date().getTime();
			var setting ={
	 		   async:{
					enable:true,
					url:url_,
					autoParam:["nodeType", "id", "indexVerId"],   //将该三个参数提交,post方式
					dataType:"json",
					dataFilter:function(treeId, parentNode, childNodes){  //对 Ajax返回数据进行预处理
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
						name:"text"
					}
				},
				view:{
					selectedMulti:false
				},
				callback:{
					onClick : zTreeOnClick
				}
			};
			
			leftTreeObj = $.fn.zTree.init($("#rptTree"), setting, [ {
				id: '0',
				text: '全部',
				params: {"nodeType" : 'idxCatalog'},
				data: {"indexCatalogNo" : '0'},
				open: true,
				icon: '${ctx}'+rootIcon,
				isParent: true
			} ]);
			}else{
				var _url = "${ctx}/report/frame/idx/getSyncTree";
				var data = {'searchNm':searchNm ,'isShowIdx':'1','isShowDim':'0', 'isShowMeasure':'1','isPublish':'1', "isAuth" : "0", "showEmptyFolder":""};
				if(searchObj != null && searchObj != ""){
					_url = "${ctx}/report/frame/idx/getSyncTreePro";
					data = {'searchObj':JSON2.stringify(searchObj) ,'isShowIdx':'1','isShowDim':'0', 'isShowMeasure':'1','isPublish':'1', "isAuth" : "0", "showEmptyFolder":""};
				}
				setting ={
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
						},
						callback:{
							onClick : zTreeOnClick
						}
				};
				leftTreeObj = $.fn.zTree.init($("#rptTree"),setting,[]);
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
			//添加高级搜索弹框
			$("#highSearchIcon").click(function(){
				BIONE.commonOpenDialog("高级搜索","highSearch","600","250","${ctx}/report/frame/idx/highSearch?searchObj="+JSON2.stringify(window.searchObj));
			});
			initTreeSearch();//普通搜索
	}
	//指标节点单击事件z
	function zTreeOnClick(event, treeId, treeNode, clickFlag) {
		var temp = treeNode.params.indexNo;
		currentNode = treeNode;
		var catalogId = currentNode.data.indexCatalogNo;
		var type = currentNode.params.nodeType;
 		if(type=="idxInfo"){
 			initData(currentNode.data.id.indexNo,currentNode.data.indexNm);
 		}else if(type=="measureInfo"){
 			var  pNode  =  currentNode.getParentNode();
 			var  compNo = pNode.data.id.indexNo+"."+currentNode.id;
 			var  compNm = pNode.data.indexNm+"."+currentNode.text;
 			initData(compNo,compNm);
 		}else{
			if(clickFlag == "1") {
				if(treeRootNo == currentNode.id) {  //当前的节点id为根节点0
				} 
			}
		} 
	} 	
	function initTreeSearch(){
		$("#treeSearchIcon").live('click',function(){// 树搜索
			initTree($('#treeSearchInput').val(),"");
		});
		$('#treeSearchInput').bind('keydown', function(event) {
			if (event.keyCode == 13) {
				initTree($('#treeSearchInput').val(),"");
			}
		});
	}
	function loadTree(url, component, data) {
		$.ajax({
			cache : false,
			async : true,
			url : url,
			dataType : 'json',
			data : data,
			type : "post",
			beforeSend : function() {
				BIONE.loading = true;
				BIONE.showLoading("正在加载数据中...");
			},
			complete : function() {
				BIONE.loading = false;
				BIONE.hideLoading();
			},
			success : function(result) {
				var nodes = component.getNodes();
				var num = nodes.length;
				for ( var i = 0; i < num; i++) {
					component.removeNode(nodes[0], false);
				}
				if (result.length > 0) {
					component.addNodes(null, result, false);
					component.expandAll(true);
				}
			},
			error : function(result, b) {
				//BIONE.tip('发现系统错误 <BR>错误码：' + result.status);
			}
		});
	}
	
	function initSymbolTree() {
		var setting = {
			data : {
				key : {
					name : 'text'
				},
				simpleData : {
					enable : true,
					idKey : "id",
					pIdKey : "upId"
				}
			},
			callback : {
				onClick : symbolTreeOnClick,
				onNodeCreated : function(event , treeId , treeNode){
					$("#"+treeNode.tId+"_a").attr("title",treeNode.title);
				}
			}
		};
		symbolTreeObj = $.fn.zTree.init($("#symbolTree"), setting);
		//加载数据
		loadTree("${ctx}/report/frame/valid/logic/getSymbolTree.json",
				symbolTreeObj);
	}

	function symbolTreeOnClick(event, treeId, treeNode, clickFlag) {
		if (treeNode.children == null) {
			var rPosition = cursorPosition.get(document.getElementById("rexpression"));
			cursorPosition.add(document.getElementById("rexpression"), rPosition, treeNode.text);
		}
	}
	function initFuncTree() {
		var setting = {
			data : {
				key : {
					name : 'text'
				},
				simpleData : {
					enable : true,
					idKey : "id",
					pIdKey : "upId"
				}
			},
			callback : {
				onClick : funcTreeOnClick,
				onNodeCreated : function(event , treeId , treeNode){
					$("#"+treeNode.tId+"_a").attr("title",treeNode.title);
				}
			}
		};
		funcTreeObj = $.fn.zTree.init($("#funcTree"), setting);
		//加载数据
		loadTree("${ctx}/report/frame/valid/logic/getFuncTree.json",
				funcTreeObj);
	}
	
	function funcTreeOnClick(event, treeId, treeNode, clickFlag) {
		if (treeNode.children == null) {
			var rPosition = cursorPosition.get(document.getElementById("rexpression"));
			cursorPosition.add(document.getElementById("rexpression"), rPosition, treeNode.text);
			//var p = (treeNode.text).match("\\)");
			rPosition.start = rPosition.start + treeNode.text.length;
			rPosition.end = rPosition.end + treeNode.text.length;
			cursorPosition.set(document.getElementById("rexpression"), rPosition);
			
		}
	}
	
</script>
</head>
<body>
<div id="template.center">
		<div id="left"
			style=" width: 300px; float: left; ">
			<div id="accordion">
				<div title="指标">
					<div id="treeSearchbar"
						style="width: 99%; margin-top: 2px; padding-left: 2px;">
						<ul>
							<li style="width: 98%; text-align: left;">
								<div class="l-text-wrapper" style="width: 100%;">
									<div class="l-text l-text-date" style="width: 100%;">
										<input id="treeSearchInput" type="text"
											class="l-text-field" style="width: 100%;" />
										<div class="l-trigger" style="right:26px">
											<div id="treeSearchIcon"
												style="width:100%;height:100%;background:url(${ctx}/images/classics/icons/find.png) no-repeat 50% 50% transparent;"></div>
										</div>
										<div title="高级筛选" class="l-trigger" style="right:0px;">
									         <div id="highSearchIcon" style="width:100%;height:100%;background:
									         url(${ctx}/images/classics/icons/application_form.png) no-repeat 50% 50% transparent;">
									         </div>
									    </div>
									</div>
								</div>
							</li>
						</ul>
					</div>
					<div id="treeContainer"
						style="width: 100%; overflow: auto; clear: both; background-color: #FFFFFF;">
						<ul id="rptTree"
							style="font-size: 12; background-color: #FFFFFF; width: 92%"
							class="ztree"></ul>
					</div>
				</div>
				<div title="运算符">
					<div id="treeContainer1"
						style="width: 100%; overflow: auto; clear: both; background-color: #FFFFFF;">
						<ul id="symbolTree"
							style="font-size: 12; background-color: #FFFFFF; width: 92%"
							class="ztree"></ul>
					</div>
				</div>
				<div title="计算公式">
				    <div id="treeContainer2"
						style="width: 100%; overflow: auto; clear: both; background-color: #FFFFFF;">
						<ul id="funcTree"
							style="font-size: 12; background-color: #FFFFFF; width: 92%"
							class="ztree"></ul>
					</div>
				</div>
			</div>
			
		</div>
		<div id="right"
			style=" float: left;">
			<div id="right-top"
				style=" height:8%; margin-left: auto; margin-right: auto; margin-top: auto;">
				<div id="tip"
					style="border: 1px solid #FFF; width: 99%; height: 99%; padding: 0px 2px; background: #fffee6; color: #8f5700;">提示
				</div>
			</div>
			<form id="checkForm"
				style="width: 99%;height:86%; margin-top: 1%; margin-bottom:20px;">
			</form>
		</div>
	</div>
			
</body>
</html>