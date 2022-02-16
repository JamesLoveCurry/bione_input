<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<%@ include file="/common/taglibs.jsp"%>
<html>
<head>
<meta name="decorator" content="/template/template14.jsp">
<link rel="stylesheet" href="${ctx}/js/exlabel/exlabel.css">
<script type="text/javascript" src="${ctx}/js/exlabel/exlabel.js"></script>
<style>
#left {
	border-width: 1px;
	border-style: solid;
	border-color: #D6D6D6;
	width :240px;
	float:left;
}
#right {
	 border-width: 0px;
	border-style: solid;
	border-color: #D6D6D6; 
	background-color: #FCFCFC;
	float:left;
}
#tree {
	background-color: #F1F1F1;
}
.l-layout-center {
	border: none;
}

</style>

<script type="text/javascript">
	var dimTreeObj = null;
	var dimTreeNodeIcon = "${ctx}/images/classics/icons/list-items.gif";
	var dimItemValArr = [];
	// 拖拽标签头图标
	var notAllowedIcon = "/images/classics/icons/cancel.gif";
	var allowedIcon = "/images/classics/icons/accept.png";
	var indexNo = "";
	var measureNo = "";
	var grid = null;
	var draggingNode = null;
	var dimMap = {};
	var dimSMap = {};
	$(function() {
		initLayout();
		initSrcIndexNo();
		initPanel();
		initGrid();
		initDimTree();
		initData();
	});
	
	function initData(){
		// 初始化
		if ('${id}') {
			$.ajax({
				url: '${ctx}/report/frame/idx/${id}/tech/comp/initData',
				data: {
					indexVerId: '${indexVerId}'
				},
				type: 'post',
				dataType: 'json',
				success: function(data){
					if (data.srcIdx == null) {
						return;
					}
					$.ligerui.get("srcIndexNo")._changeValue(data.srcIdx.sidxNo,data.srcIdx.sidxNm);
					$("#srcIndexNo").attr("title",data.srcIdx.sidxNo);
					window.indexNo = data.srcIdx.idxNo;
					window.measureNo = data.srcIdx.measureNo;
					loadDimNodes(data.srcIdx.idxNo);
					var dimRels = data.idxRelInfo.idxDimRel;
					var dimFilters = data.idxRelInfo.idxFilterInfo;
					panel.add({
						id : "DATE",
						text : "日期",
						type : "noclose"
					});
					panel.add({
						id : "ORG",
						text : "机构编号",
						type : "noclose"
					});
					for(var i in dimRels){
						var dimRel = dimRels[i];
						if(dimRel.id.dimNo != "DATE" && dimRel.id.dimNo != "ORG"){
							panel.add({
									id : dimRel.id.dimNo,
									text : dimMap[dimRel.id.dimNo]
							});
						}
					}
					var formulaContent = data.idxRelInfo.idxFormulaInfo[0].formulaContent;
					dimItemValArr = data.idxRelInfo.textArr;
					var f_arr = formulaContent ? formulaContent.split("&&") : [];
					f_arr = $.grep(f_arr, function(n) {
						if (n && $.trim(n) !== "&&") {
							return true;
						} else {
							return false;
						}
					});
					for(var i in dimFilters){
						var dimFilter = dimFilters[i];
						if(dimFilter.filterVal){
							var row = {
									dimTypeNo : dimFilter.id.dimNo,
									dimTypeNm :  dimMap[dimFilter.id.dimNo],
									dimTypeStruct : dimSMap[dimFilter.id.dimNo],
									isFilter : true,
									itemArr :  dimFilter.filterVal.split(","),
									filterMode : dimFilter.filterMode,
									formulaInfo : {
										filterformula : f_arr[i]
									}
								};
							grid.addRow(row);
						}
						
					}
					
				}
			});
		}
	}
	function checkFilterVal(dimTypeNo){
		for(var i=0;i<dimItemValArr.length;i++){
			var obj  =dimItemValArr[i];
			if(obj.dimTypeNo==dimTypeNo){
				return  obj.value;
			}
		}
		return  "";
	}
	
	function initGrid(){
		grid = $("#maingrid").ligerGrid({
			height : $('#right').height() - 105,
			columns : [{
				display : "维度编码",
				name : "dimTypeNo",
				width : '20%'
			}, {
				display : "维度名称",
				name : "dimTypeNm",
				width : '20%'
			}, {
				display : "过滤值",
				width : '40%',
				name : "setFilter",
				render:function(row){
					return checkFilterVal(row.dimTypeNo);
				} 
			}, {
				display : "操作",
				width : "15%",
				name : "operation",
				render:function(row, rowindex, value){
					return row.isFilter ? '<a class="link" href="javascript:alertDimValWin(' + rowindex + ')">过滤</a>&nbsp&nbsp<a class="link" href="javascript:deleteRowWin(' + rowindex + ')">删除</a>' : '';
				}
			}],
			dataAction : 'local',
			enabledEdit : false,
			checkbox: false,
			usePager : false,
			alternatingRow : false,
			colDraggable : false,
			rowDraggable : false,
			clickToEdit : false
		});
	}
	
	function initDimTree(){
		var setting = {
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
				view : {
					selectedMulti : false,
					showLine : false
				},
				callback:{
					onNodeCreated : function(event , treeId , treeNode){
						setDragDrop("#"+treeNode.tId+"_span" , "#panel,#maingrid",treeNode.text);
					}

				}
			};
			dimTreeObj = $.fn.zTree.init($("#tree"),setting);
	}
	
	function initLayout(){
		var $content = $(window);
		$("#right").height($content.height() - 5);
		$("#rightDiv").height($("#right").height() - 1);
		$("#left").height($content.height() - 5);
		$("#right").width($content.width() - 245);
		var rightHeight = $("#right").height();
		var leftHeight = $("#left").height();
		var $treeContainer = $("#treeContainer");
		$treeContainer.height(leftHeight - 56 - $("#treeSearchbar").height() );
	}
	
	function initSrcIndexNo(){
		$("#srcIndexNo").ligerComboBox({
			url : "${ctx}/rpt/frame/dataset/dsList.json",
			onBeforeOpen : function() {
				var url = "${ctx}/report/frame/idx/idxchoose";
				dialog = BIONE
						.commonOpenDialog('请选择来源指标',
								"srcIndexWin", "620",
								"550", url);
				return false;
			}
		});
		$("#srcIndexNo").parent().width("99%");
	}
	
	function initPanel(){
		$("#panel").css("height", "100px");
		$("#panel").css("width", $("#right").width()-10);
		panel = $('#panel').exlabel({
			text : 'text',
			value : 'id',
			isEdit : false,
			isInput : false
		});
	}
	
	function initDimNodes(treeNode) {
		var nindexNo,neasureNo;
		nindexNo = treeNode.data.id.indexNo;
		if(treeNode.nodeType == "measureInfo")
			nmeasureNo = treeNode.id;
		else
			nmeasureNo = "";
		if(nindexNo != indexNo || nmeasureNo != nmeasureNo){
			indexNo = nindexNo;
			measureNo = nmeasureNo;
			$.ajax({
				url: '${ctx}/report/frame/idx/getDimListBySrcIndex.json',
				data: {
					indexNo : treeNode.data.id.indexNo
				},
				type: 'post',
				dataType: 'json',
				beforeSend: function() {
					BIONE.showLoading();
				},
				complete: function() {
					BIONE.hideLoading();
				},
				success: function(data) {
					var nodes =[];
					nodes = dimTreeObj.getNodes();
					var num = nodes.length;
					for(var i=0;i<num;i++){
						dimTreeObj.removeNode(nodes[0],false);
					}
					nodes = [];
					for(var i in data){
						if(data[i].dimTypeNo == "INDEXNO")
							continue;
						var node = {
							id : data[i].dimTypeNo,
							text : data[i].dimTypeNm,
							icon : dimTreeNodeIcon,
							params : {
								dimTypeStruct : data[i].dimTypeStruct
							},
							upId :"0"
						};
						dimMap[data[i].dimTypeNo] = data[i].dimTypeNm;
						dimSMap[data[i].dimTypeNo] = data[i].dimTypeStruct;
						nodes.push(node);
					}
					dimTreeObj.addNodes(null, nodes , true);
					panel.removeAll();
					panel.adds([{
						id : "DATE",
						text : "日期",
						type : "noClose"
					},{
						id : "ORG",
						text : "机构编号",
						type : "noClose"
					}]);
					grid.set("data",{Rows:[]});
				}
			});
		}
	}
	
	
	function loadDimNodes(indexNo){
		$.ajax({
			async : false,
			url: '${ctx}/report/frame/idx/getDimListBySrcIndex.json',
			data: {
				indexNo : indexNo
			},
			type: 'post',
			dataType: 'json',
			beforeSend: function() {
				BIONE.showLoading();
			},
			complete: function() {
				BIONE.hideLoading();
			},
			success: function(data) {
				var nodes =[];
				nodes = dimTreeObj.getNodes();
				var num = nodes.length;
				for(var i=0;i<num;i++){
					dimTreeObj.removeNode(nodes[0],false);
				}
				nodes = [];
				for(var i in data){
					if(data[i].dimTypeNo == "INDEXNO")
						continue;
					var node = {
						id : data[i].dimTypeNo,
						text : data[i].dimTypeNm,
						icon : dimTreeNodeIcon,
						params : {
							dimTypeStruct : data[i].dimTypeStruct
						},
						upId :"0"
					};
					dimMap[data[i].dimTypeNo] = data[i].dimTypeNm;
					dimSMap[data[i].dimTypeNo] = data[i].dimTypeStruct;
					nodes.push(node);
				}
				dimTreeObj.addNodes(null, nodes , true);
			}
		});
	}
	//添加拖拽控制
	function setDragDrop(dom, receiveDom,labelInfo) {
		if (typeof dom == "undefined" || dom == null) {
			return;
		}
		$(dom).ligerDrag({
			proxy : function(target, g, e) {
				var defaultName = "维度";
				var proxyLabel = "${ctx}" + notAllowedIcon;
				var targetTitle = labelInfo;
				var proxyHtml = $('<div style="width：150;position: absolute;padding-left: 10px;color: #183152;font-weight: bold;height: 20px;line-height: 19px;overflow: hidden;background: #E5EFFE ;border: 1px solid #DDDDDD;border-top:none; z-index:9999;"><span class="dragimage_span" style="position:absolute;width:16px;height:16px;left:5px;top:2px;background:url('
								+ proxyLabel
								+ ')" ></span><span style="padding-left: 14px;">'
								+ targetTitle + '</span></div>');
				var div = $(proxyHtml);
				div.appendTo('body');
				return div;
			},
			revert : false,
			receive : receiveDom,
			onStartDrag : function(current, e) {
				// 获取拖拽树节点信息
				var treeAId = current.target.attr("id");
				var treeId = treeAId;
				if (treeAId) {
					var strsTmp = treeAId.split("_");
					var treeId = treeAId;
					if (strsTmp.length > 1) {
							var newStrsTmp = [];
							for (var i = 0; i < strsTmp.length - 1; i++) {
								newStrsTmp.push(strsTmp[i]);
							}
							treeId = newStrsTmp.join("_");
					}
					draggingNode = dimTreeObj.getNodeByTId(treeId);
				}
			},
			onDragEnter : function(receive, source, e) {
				var allowLabel = "${ctx}" + allowedIcon;
				source.children(".dragimage_span").css("background","url('" + allowLabel + "')");
			},
			onDragOver : function(receive, source, e) {
				var allowLabel = "${ctx}" + allowedIcon;
				source.children(".dragimage_span").css("background","url('" + allowLabel + "')");
			},
			onDragLeave : function(rceive, source, e) {
				var notAllowLabel = "${ctx}" + notAllowedIcon;
				source.children(".dragimage_span").css("background","url('" + notAllowLabel + "')");
			},
			onDrop : function(obj, target, e) {
				if($(obj).attr("id") == "maingrid"){
					if(draggingNode.id == "DATE"){
						BIONE.tip("日期维度不可过滤");
						return;
					}
					grid.addRow({
						dimTypeNo : draggingNode.id,
						dimTypeNm : draggingNode.text,
						dimTypeStruct : draggingNode.params.dimTypeStruct,
						isFilter : true
					});
				}
				if($(obj).attr("id") == "panel"){
					if(panel.findsByPro("id",draggingNode.id).length<=0){
						panel.add({
							 id : draggingNode.id,
							 text :draggingNode.text
						});
					}
				}
			}
		});
	}
	
	function alertDimValWin(rowindex){
		var row = alertDimValWin.row = liger.get('maingrid').getRow(rowindex);
		var modelDialogUri = "${ctx}/report/frame/idx/${id}/dim/"+row.dimTypeNo+"/items?indexVerId=${indexVerId}";
		BIONE.commonOpenDialog("维度树", "rptCmpoIdxSeltTree",400,600,modelDialogUri);
	}
	
	function deleteRowWin(rowindex){
		var row = grid.getRow(rowindex);
		grid.remove(row);
		for(var i in dimItemValArr){
			if(dimItemValArr[i].dimTypeNo == row.dimTypeNo){
				dimItemValArr.splice(i,1);
				break;
			}
		}
	}
	
	function getData() {
		if (!indexNo) {
			throw new Error('未选择源指标');
		}
		var dataJson = {fields: []};
		var dataJsonFilterMode = {fields: []};
		var formulaContent = "";
		var dataJsonDim = [];
		var datas =grid.getData();
		var labels = panel.getData();
		dataJsonDim.push({
			dimTypeNo : "INDEXNO"
		});
		$.each(labels, function(i, n) {
			dataJsonDim.push({
				dimTypeNo: n.id
			});
		});
		$.each(datas, function(i, n) {
			if (n.itemArr && n.itemArr.length > 0) {
				dataJson.fields.push({
					dimTypeNo: n.dimTypeNo,
					itemArr: n.itemArr || []
				});
			}
			if (n.filterMode && $.type(n.filterMode) == 'string') {
				dataJsonFilterMode.fields.push({
					dimTypeNo: n.dimTypeNo,
					filterMode: n.filterMode || []
				});
			}
			if (n.formulaInfo && n.formulaInfo.filterformula) {
				if (formulaContent.length > 0) {
					formulaContent+="&&";
				}
				formulaContent+=n.formulaInfo.filterformula;
			}
		});
		
		return {
			srcIndexNo:           indexNo,
			saveType:             '02',
			dataJsonStr:          JSON2.stringify(dataJson),
			dataJsonFilterModeStr:JSON2.stringify(dataJsonFilterMode),
			dataJsonDimStr:		  JSON2.stringify(dataJsonDim),
			formulaContent:       formulaContent,
			sumAccMeaNo:          measureNo,
			isNewVer:             'N'
		}
	}
</script>
</head>
<body>
	<div id = "template.center">
		
		<div id="left" position="left" style="background-color: #FFFFFF">
			<div class="l-form" style="margin: 2px;">
				<ul>
					<li style="width: 40%;">来源指标选择：</li>
					<li style="width: 59%;"><input id="srcIndexNo" /></li>
				</ul>
			</div>
			<div id="lefttable" width="100%" border="0">
				<div id="leftTitle" width="100%"
					style="height:30px;background-image: url(${ctx}/css/classics/ligerUI/Gray/images/ui/gridbar.jpg);border-bottom:1px solid #D6D6D6;">
					<div  width="8%"
						style="float: left; position: relative; height: 20p; margin-top: 7px">
						<i class = "icon-guide search-size"></i>
					</div>
					<div width="90%">
						<span id="leftSpan"
							style="font-size: 12; float: left; position: relative; line-height: 30px; padding-left: 2px">
							维度选择
						</span>
					</div>
				</div>
			</div>
			<div id="treeContainer"
				style="width: 100%; overflow: auto; clear: both; background-color: #FFFFFF;">
				<ul id="tree"
					style="font-size: 12; background-color: #FFFFFF; width: 92%"
					class="ztree"></ul>
			</div>
		</div>
		<div id="right" position="center" style="overflow-x: auto;overflow-y: hidden;">
			<div id="rightDiv" style="width:100%">
				<div id="panel" class="panel"></div>
				<div class="content">
					<div id="maingrid" class="maingrid"></div>
				</div>
			</div>
		</div>
	</div>
	
</body>
</html>