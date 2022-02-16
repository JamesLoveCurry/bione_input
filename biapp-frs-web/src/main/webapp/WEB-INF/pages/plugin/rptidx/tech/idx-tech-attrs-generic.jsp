<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<%@ include file="/common/taglibs.jsp"%>
<html>
<head>
<meta name="decorator" content="/template/template33.jsp">
<script type="text/javascript">
	var url, colTypeSelectTreeObj, treeNode_;
	var  saveType  = '${saveType}';
	var  setType  = '${setType}';
	var  currVal  = '${currVal}';
	var  indxInfoObj;
	var  isDict =  parent.isDict;
	var  defSrc  = parent.defSrc;
	function filter(node) {
	    return (node && node.params.type=="setCatalog");
	}
	function   initContext(){
		   var  baseNode   =  colTypeSelectTreeObj.getNodes()[0];
			var id  =baseNode.tId;
			$("#"+id).find(".chk#"+id+"_check").remove();
			var nodes = colTypeSelectTreeObj.getNodesByFilter(filter); // 查找节点集合
			if(nodes){
				for(var i=0;i<nodes.length;i++){
					 var node  = nodes[i];
					 var id_ =node.tId;
					 $("#"+id_).find(".chk#"+id_+"_check").remove();
				}
			}
	}
	$(function() {
		indxInfoObj = parent.indxInfoObj;
 		url ="${ctx}/rpt/frame/dataset/getDataModuleTree.json?setType="+setType+"&d=" + new Date().getTime();
		beforeTree();
		initTree();
		selectButton();
		$(".form-bar-inner").css("padding-top","22px");
	});
	function beforeTree() {
		var setting = {
				data:{
					key:{
						name:'text'
					},
					simpleData:{
						enable:true,
						idKey: "id",
						pIdKey: "upId"
					}
				},
				callback : {
					beforeClick : beforeClick,
					beforeCheck : beforeCheck,
					onClick : onClick
				},
				view : {
					selectedMulti : false,
					showLine : false,
					expandSpeed :"fast"
				},
				check:{
					chkStyle:"radio",
					enable:true,
					radioType:"all"
				}
		};
		colTypeSelectTreeObj = $.fn.zTree.init($("#tree"), setting);
	}
	
	function initTree() {
		$.ajax({
			cache : false,
			async : false,//同步
			url : url,
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
				colTypeSelectTreeObj.addNodes(null, result, true);
				colTypeSelectTreeObj.expandAll(true);
				initContext();
				showBack();
			},
			error : function(result, b) {
				BIONE.tip('发现系统错误 <BR>错误码：' + result.status);
			}
		});
	}
	function   showBack(){
		if(currVal){
			if(colTypeSelectTreeObj.getNodeByParam("id", currVal, null)){
         		colTypeSelectTreeObj.checkNode(colTypeSelectTreeObj.getNodeByParam("id", currVal, null), true, true);
			}
		}
	}
	function selectButton() {
		var btns = [ {
			text : "取消",
			onclick : function() {
			  window.parent.closeRptIdxInfoBox();
			}
		}];
		if(!isDict){
			btns.push({
				text : "保存",
				onclick : function() {
					col_save('');
				}
			});
			if (indxInfoObj.indexVerId != "" && indxInfoObj.indexVerId != null) {
				btns.push({
					text : "发布为新版本",
					onclick : function() {
						col_save('newVer');
					}
				}); 
			}
		}
// 		BIONE.addFormButtons(btns);
	}
	
	function beforeClick(treeId, treeNode, clickFlag) {
			return false;
	}
	
	function beforeCheck(treeId, treeNode, clickFlag) {
		    if(isDict)   return false;
		    return true;
	}
	
	function onClick(event, treeId, treeNode, clickFlag) {
		treeNode_ = treeNode;
	}
	     
 	function col_save(newVer) {
 		if(newVer){
			if(indxInfoObj.startDate<=indxInfoObj.oldStartDate){
				BIONE.tip("新版启用日期必须大于旧版启用日期");
				return  ;
			}
		}
 		var    radioId   =  $(".radio_true_full").attr("id");
 		if(radioId){
 			radioId  =  radioId.substring(0,radioId.length-6);
 		}else {
 			BIONE.tip("请选择数据集节点");
 			return;
 		}
 		var    currentNode  =  colTypeSelectTreeObj.getNodeByParam("tId",radioId);
 		$.ajax({
			cache : false,
			async : true,
			url : "${ctx}/report/frame/idx/checkHasMeasures.json?d=" + new Date().getTime(),
			dataType : 'json',
			type : "post",
			data : {
				setId:currentNode.id,
				setType:setType
			},
			success : function(result){
				if(result.flag=="1"){
					    var  postData  =  {
								indexNm:           indxInfoObj.indexNm ,
								indexUsualNmTemp1: indxInfoObj.indexUsualNmTemp1  ,
								indexUsualNmTemp2: indxInfoObj.indexUsualNmTemp2 ,
								indexType:         indxInfoObj.indexType  ,
								indexSts:          indxInfoObj.indexSts  ,
								dataType:          indxInfoObj.dataType  ,
								busiDef:        indxInfoObj.busiDef ,
								dataLen:           indxInfoObj.dataLen  ,
								dataPrecision:     indxInfoObj.dataPrecision  ,
								dataUnit:          indxInfoObj.dataUnit ,
								valRange:          indxInfoObj.valRange ,
								calcCycle:         indxInfoObj.calcCycle  ,
								startDate:         indxInfoObj.startDate  ,
								defDept:           indxInfoObj.defDept ,
								useDept:           indxInfoObj.useDept ,
								isSum:             indxInfoObj.isSum  ,
								infoRights:             indxInfoObj.infoRights  ,
								busiModel:           indxInfoObj.busiModel ,
			 					isPublish:           indxInfoObj.isPublish ,
								busiRule:       indxInfoObj.busiRule  ,
								remark:            indxInfoObj.remark,
								indexNo:           indxInfoObj.indexNo,
								indexCatalogNo :   indxInfoObj.indexCatalogNo,
								indexVerId :       newVer=='newVer'?(parseInt(indxInfoObj.indexVerId)+1):indxInfoObj.indexVerId,
								saveType:          saveType,
								setId:             currentNode.id ,
								isNewVer:          newVer=='newVer'?'Y':'N' ,
								statType:     indxInfoObj.statType
							};
					    if(defSrc){
					    	postData.defSrc= defSrc;
					    }
						$.ajax({
							type : "POST",
							url : "${ctx}/report/frame/idx/createRptIdxInfo",
							dataType : 'json',
							data : postData,
							success : function() {
								window.parent.parent.BIONE.tip("保存成功！");
								//刷新grid
								window.parent.parent.frames["rptIdxCenterTabFrame"].grid.loadData();
								var treeObj = window.parent.parent.currentTree;
								if(!treeObj){
									treeObj = window.parent.parent.leftTreeObj;
								}
								var ifChangeDirectionaryCatalogNode    = treeObj.getNodeByParam("id", indxInfoObj.indexCatalogNo, null);
								if(ifChangeDirectionaryCatalogNode)
								              treeObj.reAsyncChildNodes(ifChangeDirectionaryCatalogNode, "refresh");	
								treeObj.reAsyncChildNodes(window.parent.parent.currentNode, "refresh");
								window.parent.closeRptIdxInfoBox();
							},
							error : function(XMLHttpRequest, textStatus, errorThrown) {
								BIONE.tip('保存失败,错误信息:' + textStatus);
							}
						});
				}else{
					BIONE.tip("所选数据集下应至少关联一个度量！");
					return;
				}
			},
			error:function(){
				BIONE.tip("加载失败，请联系系统管理员");
			}
		});
	}
 	
 	function getData() {
 		var radioId = $(".radio_true_full").attr("id");
 		if(radioId){
 			radioId  =  radioId.substring(0,radioId.length-6);
 		}else {
 			var e = new Error('请选择数据集节点');
 			throw e;
 		}
		var currentNode = colTypeSelectTreeObj.getNodeByParam("tId",radioId);
 		return {
 			saveType: '04',
 			setId: currentNode.id
 		};
 	}
</script>
</head>
<body>
</body>
</html>