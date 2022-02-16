<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<%@ include file="/common/taglibs.jsp"%>
<html>
<head>
<meta name="decorator" content="/template/template14.jsp">
<style>
.singlebox,#allSelect{
  margin-top:5px;
}
</style>
<script type="text/javascript">
	//全局变量
	var Design = window.parent.Design;
	
	var grid2;
	var rptInfo;
	
	var rowFilterMap = [];
	var colFilterMap = [];
	
	if (window.parent.parent.selectionIdx
			&& typeof window.parent.parent.selectionIdx != "undefined") {
		rptInfo = window.parent.parent.selectionIdx;
	} else {
		rptInfo = {};
	}
	var srcIndexNo = rptInfo.indexNo();
	var srcIndexVerId = rptInfo.indexVerId();
	var jsonStr = '${jsonStr}';
	var jsonArr = eval('(' + jsonStr + ')');
	var isSum = rptInfo.isSum();

	/* var checkDimsArr = [];//已选维度
	var dimItemValArr = [];//
	var rowValArr = [];*/
	var  dimItemArr = [];//某维度下的过滤 
	var checkDimsArr = [];//已选维度
	//var filterformulaArr=[];
	function  initContext(){
		if(rptInfo.factDims() != null && !rptInfo.factDims() == ""){
			checkDimsArr = rptInfo.factDims().split(",");	
		}
		if(rptInfo.filtInfos()){
			if(typeof rptInfo.filtInfos() == "string"){				
				dimItemArr = JSON2.parse(rptInfo.filtInfos());
			} else {
				jQuery.extend(dimItemArr , rptInfo.filtInfos());
			}
		}
		/* if(rptInfo.dimItemValArr()){
			dimItemValArr = rptInfo.dimItemValArr();
		} */
	}
	function  checkChecked(dimTypeNo){
		if(rptInfo.allDims() == ""){
     		return  "checked";
		}
		if(checkDisabled(dimTypeNo)=="disabled") return  "checked";
		if(checkDimsArr.length>0){
			for(var   ch=0;ch<checkDimsArr.length;ch++){
			  var   checkDim  = checkDimsArr[ch];
			  if(checkDim==dimTypeNo){
				  return  "checked";
			  }
			} 
		}
		return  "";
	}
	
	// 获取指定维度类型下的维度项过滤信息
	function  getItemArr(dimTypeNo){
		for(var  i  =0;i<dimItemArr.length;i++){
			var  obj_  =dimItemArr[i];
			if(obj_.dimNo==dimTypeNo){
				return  obj_.filterVal.split(",");
			}
		}
		return  [];
	}
	
	function  getItemFilterMode(dimTypeNo){
		for(var  i = 0;i<dimItemArr.length;i++){
			var  obj_  = dimItemArr[i];
			if(obj_.dimNo == dimTypeNo){
				return  obj_.filterMode;
			}
		}
		return  "";
	} 
	function  updateItemArr(json){
		var exist =  false;
		for(var  i=0 ;i<dimItemArr.length;i++){
			var  obj_  =dimItemArr[i];
			if(obj_.dimNo==json.dimNo){
				obj_.filterVal=json.filterVal;
				obj_.filterMode = json.filterMode;
				exist=true;
				break;
			}
	    }
		if(!exist){
			dimItemArr.push(json);
		}
	}
/* 	function  updateFilterformulaArr(json){
		var exist =  false;
		for(var  i=0 ;i<filterformulaArr.length;i++){
			var  obj_  =filterformulaArr[i];
			if(obj_.dimTypeNo==json.dimTypeNo){
				obj_.filterformula=json.filterformula;
				exist=true;
				break;
			}
	    }
		if(!exist){
			filterformulaArr.push(json);
		}
	} */
	function alertDimValWin(dimTypeNo,rowId,isSum,dimTypeStruct){
		var modelDialogUri = "${ctx}/report/frame/design/cfg/gotoCmpoIdxSeltTree?dimTypeNo="+dimTypeNo+"&rowId="+rowId+"&isSum="+isSum+"&dimTypeStruct="+dimTypeStruct+"&isDict=1";
		BIONE.commonOpenDialog("维度树", "rptCmpoIdxSeltTree",500,$("#center").height()-30,modelDialogUri);
	}
	function  checkDisabled(dimTypeNo){
		for(var i =0;i<jsonArr.length;i++){
			 if(jsonArr[i]==dimTypeNo){
				 return  "disabled";
			 }
		 } 
		 return "";
	}
	function  checkFilterVal(dimTypeNo){
		var rowColObj = getRowColFiltInfos(dimTypeNo);
		if(rowColObj
				&& rowColObj.rowColArr
				&& rowColObj.rowColArr.length > 0){
			var filterModel = rowColObj.filterModel;
			// 存在行/列过滤
			var textTmp = "包含";
			if(filterModel == "02"){
				textTmp = "不包含";
			}
			return "<font color='red'>[行列过滤]</font> "+textTmp + rowColObj.rowColArr.length + " 过滤值";
		}
		for(var i=0;i<dimItemArr.length;i++){
			var obj = dimItemArr[i];
			if(obj.dimNo == dimTypeNo && obj.filterVal && !obj.filterVal == ""){
				if(obj.filterMode == "01"){
					return "包含 " + obj.filterVal.split(",").length + " 过滤值";
				}
				if(obj.filterMode == '02'){
					return "不包含 " + obj.filterVal.split(",").length + " 过滤值";
				}
			}
		} 
		return  "";
	}
	
	function   updateDimItemValArr(rowId){
		var   row = grid2.getRow(rowId);
		grid2.updateCell('setFilter',"", row);
	}
	
	function checkRowColFilter(){
		// 检查行列过滤
		if(Design
				&& typeof Design.rowColsSize == "function"
				&& Design.rowColsSize() > 0){
			// 若存在行列信息定义，检查当前双击的指标行、列是否有定义
            var rowSeq = Design._getStyleProperty(rptInfo.rowId(), -1, "seq");
			// 是否有行过滤
			if(rowSeq
					&& Design.rowCols[rowSeq]){
				rowFilterMap = Design.rowCols[rowSeq].filtMapping();
			}
            var colSeq = Design._getStyleProperty(-1, rptInfo.colId(), "seq");
			// 是否有列过滤
			if(colSeq
					&& Design.rowCols[colSeq]){
				colFilterMap = Design.rowCols[colSeq].filtMapping();
			}
		}
	}
	
	// 分析行/列过滤信息，提取某维度类型下过滤项并集
	// @return {
	//	    rowColArr : 行列具体的过滤维信息集合
	//     filterModel : 过滤方式
	//  }
	//  1.既有行过滤，又有列过滤：
	//    a.行列过滤的过滤方式一致，取过滤值的并集
	//    b.行列过滤的过滤方式不一致，行列过滤不生效
	//  2.行列过滤只有其一（只有行过滤/只有列过滤），生勒个效啊，还用问  = 。 =
	function getRowColFiltInfos(dimTypeNo){
		var returnObj = {
			rowColArr : [],
			filterModel : "01"
		};
		var rowFiltTmp = rowFilterMap[dimTypeNo];
		var colFiltTmp = colFilterMap[dimTypeNo];
		var rowColArr = [];
		var filterModel = "";
		if(rowFiltTmp
				&& colFiltTmp){
			if(rowFiltTmp.filterModelVal == colFiltTmp.filterModelVal){
				// 该维度即有行过滤也有列过滤，且过滤类型一致 , 取维度并集
				if(Design.Utils){
					var rowArr = rowFiltTmp.checkedVal ? rowFiltTmp.checkedVal.split(",") : [];
					var colArr = colFiltTmp.checkedVal ? colFiltTmp.checkedVal.split(",") : [];
					rowColArr = Design.Utils.arraySum(rowArr , colArr);
					filterModel = rowFiltTmp.filterModelVal;
				}
			}else{
				// 过滤维度不一致，行列过滤不生效
				rowColArr = [];
			}
		} else if(rowFiltTmp){
			rowColArr = rowFiltTmp.checkedVal ? rowFiltTmp.checkedVal.split(",") : [];
			filterModel = rowFiltTmp.filterModelVal;
		} else if(colFiltTmp){
			rowColArr = colFiltTmp.checkedVal ? colFiltTmp.checkedVal.split(",") : [];
			filterModel = colFiltTmp.filterModelVal;
		}
		returnObj.rowColArr = rowColArr;
		returnObj.filterModel = filterModel;
		return returnObj;
	}

	$(function() {
		// 检查行列过滤
		checkRowColFilter();
		$("#center").css("overflow","hidden");
		if (srcIndexNo) {
			//渲染grid
			initContext();
			ligerGrid();
		}
		//渲染GRID
		function ligerGrid() {
			var height = $("#center").height() - 2;
			grid2 = $("#maingrid2")
					.ligerGrid(
							{
								width : "99.8%",
								height : height,
								columns : [
										{
											display : "指标维度",
											name : "dimTypeNm",
											width : '18%'
										},
										{
											display : "维度编码",
											name : "dimTypeNo",
											width : '18%'
										},
										{
											display : "设置过滤值",
											width : '45%',
											name : "setFilter",
											render : function(row) {
												return checkFilterVal(row.dimTypeNo);
											}
										},
										{
											display : "操作",
											width : '15%',
											name : "operation",
											render : function(row) {
												if(checkDisabled(row.dimTypeNo) == ""){
													return "<a href='javascript:void(0)'  onclick='alertDimValWin(\""+row.dimTypeNo+"\",\""+row.__id+"\",\""+isSum+"\",\""+row.dimTypeStruct+"\")' "+">查看过滤</a>";
												}else
													return "";
											}
										} ],
								onSelectRow : function(rowdata, rowindex) {
									$("#txtrowindex").val(rowindex);
								},
								onAfterShowData:function(currentData){
									var ids=$(".l-grid-row-cell-inner");
									for(var i=0;i<ids.length;i++){
										if($(ids[i]).html()=="INDEXNO"){
											$(ids[i]).parent().parent().hide();
										}
									}
								},
								dataAction : 'server',//从后台获取数据
								method : 'post',
								url : "${ctx}/report/frame/idx/getFrsDimListBySrcIndex.json",
								delayLoad : true,//初始化时不加载数据
								enabledEdit : false,
								checkbox : false,
								usePager : false,
								alternatingRow : false,//奇偶行效果行
								colDraggable : false,
								rowDraggable : false,
								clickToEdit : false,
								onLoading : function() {
									gridLoadOver = false;
								},
								onLoaded : function() {
									gridLoadOver = true;
								}
							});


			//加载数据
			grid2.set('parms', {
				srcIndexNo : srcIndexNo,
				srcIndexVerId : srcIndexVerId,
				d : new Date().getTime()
			});
			grid2.loadData();
			$("#tipMainDiv").show();
		}

		//保存
		function f_save() {
			var factDims = "";
			var allDims = "";
            var rows = grid2.getData();
            for(var i=0;i<rows.length;i++){
            	allDims += rows[i].dimTypeNo + ",";
            }
            if(allDims!= "" && allDims.charAt(allDims.length -1) == ","){
            	allDims = allDims.substring(0, allDims.length - 1);
            }
            factDims = allDims;
            rptInfo.isSubDim("N");
            var newDimItemArr = [];
            for(var i=0;i<dimItemArr.length;i++){
            	if(dimItemArr[i].filterVal != null && dimItemArr[i].filterVal != ""){
            		newDimItemArr.push(dimItemArr[i]);
            	}
            }
            rptInfo.allDims(allDims);
            rptInfo.factDims(factDims);
            if(newDimItemArr.length <= 0){
            	rptInfo.filtInfos([]);
            }else{            	
            	rptInfo.filtInfos(newDimItemArr);
            }
            if(rptInfo.filtInfos() == null
            		|| rptInfo.filtInfos().length <= 0){
            	rptInfo.isFiltDim("N");
            }else{
            	rptInfo.isFiltDim("Y");
            }
            if(rptInfo.cellLabel != null
					&& typeof rptInfo.cellLabel == "function"
					&& window.parent.spread){
				var currSheet = window.parent.spread.getActiveSheet();
				var selRow = currSheet.getActiveRowIndex();
				var selCol = currSheet.getActiveColumnIndex();
				var spans = window.parent.Design.getSelectionSpans(currSheet);
				if(spans 
						&& typeof spans.length != "undefined"
						&& spans.length > 0){
					selRow = spans[0].row;
					selCol = spans[0].col;
				}
				var currCell = currSheet.getCell(selRow , selCol);
				var posiNew = initLabel(rptInfo);
				window.parent.Design._SelectionModule.set("val" , posiNew);
				currSheet.isPaintSuspended(true);
				currCell.value(posiNew);
				currSheet.isPaintSuspended(false);
			}
            if(typeof window.parent.selCellSettingHandler == "function"){			
				window.parent.selCellSettingHandler("" , "03");
			}
			BIONE.closeDialog("moduleClkDialog");
		}
		
		function initLabel(obj){
			var label = "";
			if(window.parent.Design.getShowType() 
					== window.parent.Design.Constants.SHOW_TYPE_CELLNM){
				var tmp = obj.cellNm() ? obj.cellNm() : obj.cellNo();
				label += ("{" + tmp + "}");
			}else if(window.parent.Design.getShowType() 
					== window.parent.Design.Constants.SHOW_TYPE_BUSINO){
				var tmp = obj.busiNo() ? obj.busiNo() : obj.cellNo();
				label += ("{" + tmp + "}");
			}else{				
				label += ("{"+obj.indexNm()+"}");
				//if(obj.isSubDim() == "Y"){
				//	label += (".[减维]");
				//}
				if(obj.isFiltDim() == "Y"){
					label += (".[过滤]");
				}
			}
			return label;
		}
	});
</script>
	<title></title>
	</head>
	<body>
		<div id="template.center">
<!-- 			<div id="tipMainDiv" -->
<!-- 				style="width: 99.8%;display:none; overflow: hidden; position: relative; border: 1px solid gray; padding-top: 1px; padding-bottom: 1px;"> -->
<!-- 				<div id="tipContainerDiv" -->
<!-- 					style="background: #fffee6; color: #8f5700;"> -->
<!-- 					<div id="tipAreaDiv"> -->
<!-- 						tips : 该页面功能是对指标进行减维操作,并对勾选维度及相关过滤值进行保存！<br /> -->
<!-- 					</div> -->
<!-- 				</div> -->
<!-- 			</div> -->
<!-- 			<div style="height: 2px;"></div> -->
<!-- 			<form id="mainform"></form> -->
			<div id="maingrid2" style="border-top:none;border-bottom:none;"></div>
		</div>
	</body>
	</html>