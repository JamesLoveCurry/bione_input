<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<html>
<head>
<%@ include file="/common/taglibs.jsp"%>
<meta name="decorator" content="/template/template5.jsp">
<script type="text/javascript">
	var btns = [];
	var currentNode;//当前点击节点
	var idxNos = "";
	var checkNos = [];
	var checkBaseNos=[];
	var checkNms=[];
	var checkBaseNms=[];
	var mainform=null;
	var main = parent.selectedDataItem;
	var idxInfos = main.idxs;
	$(function() {
		
		for ( var i in idxInfos) {
			idxNos += idxInfos[i].id.indexNo;
			if(i<idxInfos.length-1){
				idxNos+=",";
			}
		}
		initTree();
		initForm();
		initBtn();
		$("#treeContainer").height(
				$("#center").height() - $("#treeSearchbar").height() - $("#idxform").height()-20);
	});
	function initForm(){
		mainform = $("#idxform").ligerForm({
			fields : [{
				display : "",
				labelWidth: "0",
				name : "idxInfo",
				width : 260,
				newline : true,
				type : "textarea",
				group: '已选指标'
				}]
		});
		$("#idxInfo").attr("style",$("#idxInfo").attr("style")+"; height: 50px; resize:none;").attr("readOnly","true");
		
	}
	//初始化数
	function initTree() {
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
			check : {
				enable : true,
				chkStyle : "checkbox",
				chkboxType: { "Y": "s", "N": "ps" }
			},
			callback : {
				onClick : zTreeOnClick,
				onCheck: zTreeOnCheck

			}
		};
		

		leftTreeObj = $.fn.zTree.init($("#tree"), setting);
		//加载数据
		loadTree("${ctx}//report/frame/idx/getTree.json?searchNm="
				+ $("#treeSearchInput").val() + "&isShowIdx=1&idxNos=" + idxNos, leftTreeObj);
		$("#treeSearchIcon").live(
				'click',
				function() {
					loadTree("${ctx}//report/frame/idx/getTree.json?isShowIdx=1&idxNos=" + idxNos, leftTreeObj,{searchNm:$("#treeSearchInput").val()});
					
				});
	}
	function zTreeOnCheck(event, treeId, treeNode) {
		var nodes=leftTreeObj.getCheckedNodes(true);
		checkNos=checkBaseNos.concat();
		checkNms=checkBaseNms.concat();
		for(var i in nodes){
			if(nodes[i].params.type=="idxInfo"){
				checkNos.push(nodes[i].id);
				checkNms.push(nodes[i].text);
			}

		}
		$("#idxInfo").val(checkNms.join(","));
	};
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
				checkBaseNos=[];
				checkBaseNms=[];
				for ( var i in checkNos) {
					var flag=true;
					var node = component
							.getNodesByParam("id", checkNos[i], null);
					for ( var j in node) {
						if (node[j].params.type == "idxInfo") {
							component.checkNode(node[j], true, true);
							flag=false;
						}
					}
					if(flag){
						checkBaseNos.push(checkNos[i]);
						checkBaseNms.push(checkNms[i]);
					}
					
				}

			},
			error : function(result, b) {
				//BIONE.tip('发现系统错误 <BR>错误码：' + result.status);
			}
		});
	}

	//树点击事件
	function zTreeOnClick(event, treeId, treeNode) {
		if (currentNode != treeNode) {
			currentNode = treeNode;
		}
	}

	//初始化按钮
	function initBtn() {
		btns.push({
			text : "取消",
			onclick : function() {
				BIONE.closeDialog("editRptIdx");
			}
		}, {
			text : "选择",
			onclick : function() {
				if(checkNos.length>0){
					$.ajax({
						cache : false,
						async : true,
						url : "${ctx}/rpt/frame/rptmgr/info/getIdxInfo.json?idxNos="+checkNos.join(","),
						dataType : 'json',
						type : "post",
						success : function(result) {
							idxInfos=idxInfos.concat(result);
							main.setData(idxInfos);
							BIONE.closeDialog("editRptIdx");
						}
						
					});
				}
				else{
					parent.BIONE.tip("");
				}
			}
		});
		BIONE.addFormButtons(btns);
	}
</script>
</head>
<body>
	<div id="template.center">

		<div class="content" style="border: 1px solid #C6C6C6;width:93%;margin-left:3%">
			<div id="treeSearchbar"
				style="width: 99%; margin-top: 2px; padding-left: 2px;">
				<ul>
					<li style="width: 98%; text-align: left;">
						<div class="l-text-wrapper" style="width: 100%;">
							<div class="l-text l-text-date" style="width: 100%;">
								<input id="treeSearchInput" type="text" class="l-text-field"
									style="width: 100%;" />
								<div class="l-trigger">
									<div id="treeSearchIcon"
										style="width:100%;height:100%;background:url(${ctx}/images/classics/icons/find.png) no-repeat 50% 50% transparent;"></div>
								</div>
							</div>
						</div>
					</li>
				</ul>
			</div>
			<div id="treeContainer"
				style="width: 100%; overflow: auto; clear: both;">
				<ul id="tree"
					style="font-size: 12; background-color: F1F1F1; width: 90%"
					class="ztree"></ul>
			</div>
			
		</div>
		
	</div>
</body>
</html>