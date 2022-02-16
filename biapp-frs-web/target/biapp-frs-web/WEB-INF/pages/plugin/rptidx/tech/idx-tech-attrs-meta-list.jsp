<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<%@ include file="/common/taglibs.jsp"%>
<html>
<head>
<meta name="decorator" content="/template/template14.jsp">
<!-- <meta name="decorator" content="/template/template5.jsp"> -->
<script type="text/javascript">
	//全局变量
	var isDict = parent.isDict;
	var defSrc = parent.defSrc;
	var grid;
	var gridLoadOver = false;
	var saveType  =  '${saveType}';
	var indxInfoObj = parent.indxInfoObj;
	var indexNo = indxInfoObj.indexNo ;
	var indexVerId =  indxInfoObj.indexVerId;
	var allRowJsonArr=[];
	var dataJsonStr ="";
	var isModify = false;
    function  src_add(){
    	if(grid.getData().length>0){
    		BIONE.tip("目前只支持配置一个数据来源");
    		return;
    	}
    	var modelDialogUri = "${ctx}/report/frame/idx/gotoTechAttrMeta";
		BIONE.commonOpenDialog("来源选择", "rptIdxDatasetSrcWin",800,360,modelDialogUri);
	}
    function getAllRow(grid){
    	var  temp_=[];
    	var   i   =  0;
    	var   row ;
    	while(row=grid.getRow(i++)){
    		temp_.push({
    			       "dsId":row.dsId,
    			    	"storeCol":row.storeCol   
    				});
    	}
    	return  temp_;
    }
    function  addSrc(jsonRow){
    	if(isModify){
    		grid.deleteSelectedRow();
    		isModify = false;
    	}
    	grid.addRow({
    		"dsId":jsonRow.dsId,
    		"storeCol":jsonRow.storeCol,
    		"tableEnNm": jsonRow.tableEnNm,
    		"setNm":jsonRow.setNm,
    		"cnNm":jsonRow.cnNm
    	}, null, null, null); 
    }
    function sameValidate(addJson){
    	allRowJsonArr  =  getAllRow(grid);
    	for(var i=0;i<allRowJsonArr.length;i++){
    		var obj_ =  allRowJsonArr[i];
   			if(obj_.dsId==addJson.dsId){
    			return  false;
    		}
    	}
    	return  true;
    }
	function  src_delete(){
		var checkedRow = grid.getSelectedRows();
		if (checkedRow.length == 0) {
			BIONE.tip('请选择行');
			return;
		}
	    window.parent.$.ligerDialog.confirm('确实要删除这' + checkedRow.length + '条记录吗!',
				function(yes) {
					if (yes) {
                  	    grid.deleteSelectedRow();
					}
				});
	}
	function  src_modify(){
		var checkedRow = grid.getSelectedRows();
		if (checkedRow.length == 1) {
			var  dsId_ =checkedRow[0].dsId;
			var  storeCol_ =checkedRow[0].storeCol;
			isModify    =  true;
	    	var modelDialogUri = "${ctx}/report/frame/idx/gotoTechAttrMeta?dsId="+dsId_+"&storeCol="+storeCol_;
			BIONE.commonOpenDialog("来源选择", "rptIdxDatasetSrcWin",800,360,modelDialogUri);
		}else{
			BIONE.tip('请选择1行记录');
			return;
		}
	}
	$(function() {
		var indxInfoObj = parent.indxInfoObj;
		//渲染grid
		ligerGrid();
		//渲染GRID
		function ligerGrid() {
			                   grid = $("#maingrid").ligerGrid({
			                    	toolbar : {},
			            			checkbox : !isDict,
			            			columns : [{
			            				display : '来源数据集',
			            				name : 'setNm',
			            				width : "25%",
			            				align : 'left'
			            			}, {
			            				display : '来源数据表',
			            				name : 'tableEnNm',
			            				width : "25%",
			            				align : 'left'
			            			},  {
			            				display : '度量字段中文',
			            				name : 'cnNm',
			            				width : "20%",
			            				align : 'left'
			            			}, {
			            				display : '度量字段英文',
			            				name : 'storeCol',
			            				width : "20%",
			            				align : 'left'
			            			}],
			            			dataAction : 'server', //从后台获取数据
			            			usePager : false, //服务器分页
			            			alternatingRow : true, //附加奇偶行效果行
			            			colDraggable : true,
			            			delayLoad : true,
			            			url : "${ctx}/report/frame/idx/getMeasureRelInfoList.json",
// 			            			pageParmName : 'page',
// 			            			pagesizeParmName : 'pagesize',
			            			rownumbers : true,
			            			width : "99.8%",
									height : "98%"
							});
			//初始化按钮
			var btns = [];
			if(!isDict){
				btns=[/*{
						text : "重置",
						icon : "refresh",
						click : function() {
							grid.loadData();
						},
						operNo : "col_refresh"
					}, */{
						text : "增加",
						icon : "add",
						click : src_add,
						operNo : "src_add"
					}, {
						text : "修改",
						icon : "modify",
						click : src_modify,
						operNo : "src_modify"
					}, {
						text : "删除",
						icon : "delete",
						click : src_delete,
						operNo : "src_delete"
					}];
			}
			BIONE.loadToolbar(grid, btns, function() {}
			);

        
			//加载数据
			grid.set('parms', {
				indexNo : indexNo,
				indexVerId : indexVerId || '',
				d : new Date().getTime()
			});
			grid.loadData();
		}

		//添加表单按钮
		var btns = [{
			text : "取消",
			onclick : function() {
					window.parent.closeRptIdxInfoBox();
			}
		}];
		if(!isDict){
			btns.push({
					text : "保存",
					onclick : function() {
						f_save('');
					}
				});
			if (indxInfoObj.indexVerId != "" && indxInfoObj.indexVerId != null) {
				btns.push({
					text : "发布为新版本",
					onclick : function() {
						f_save('newVer');
					}
				}); 
			}
		}
// 		BIONE.addFormButtons(btns);
		//保存
		function f_save(newVer) {
			if(newVer){
				if(indxInfoObj.startDate<=indxInfoObj.oldStartDate){
					BIONE.tip("新版启用日期必须大于旧版启用日期");
					return  ;
				}
			}
			allRowJsonArr  =  getAllRow(grid);
// 			if(allRowJsonArr.length==0){//in case
// 				BIONE.tip("至少添加一个来源！");
// 				return;
// 			}
            var  postData =  {
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
                    dataJsonStr:        '{fields:' + JSON2.stringify(allRowJsonArr) + '}',
					saveType:          saveType,
					isNewVer:          newVer=='newVer'?'Y':'N',
				    statType:   indxInfoObj.statType
				} ;
            if(defSrc){
            	postData.defSrc  =  defSrc;
            }
			$.ajax({
				type : "POST",
				url : "${ctx}/report/frame/idx/createRptIdxInfo",
				dataType : 'json',
				data :postData,
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
		}
	});
	function getData(newVer) {
		allRowJsonArr  =  getAllRow(grid);
		return {
			indexVerId :       newVer=='newVer'?(parseInt(indxInfoObj.indexVerId)+1):indxInfoObj.indexVerId,
            dataJsonStr:        '{fields:' + JSON2.stringify(allRowJsonArr) + '}',
			saveType:          '01',
			isNewVer:          newVer=='newVer'?'Y':'N'
		};
	}
</script>
<title></title>
</head>
<body>
	<div id="template.center">
		<div id="maingrid"></div>
	</div>
</body>
</html>