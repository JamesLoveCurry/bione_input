<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<%@ include file="/common/taglibs.jsp"%>
<html>
<meta name="decorator" content="/template/template1_BS.jsp">
<head>
<style>
.indexStsA,.indexNmA{
     width:55%;
     cursor:pointer;
}
.stop{
    color:red;
}
</style>
<!-- 基础的JS和CSS文件 -->
<script type="text/javascript">
    var index_sts_column_width="11%";
    var isPreview  = true;
	var grid;
	var ids;
    var  preview  ='${preview}';
    var  indexName_  ='${indexNm}';
	var indexCatalogNo='${indexCatalogNo}';
	var INDEX_STS_START='${INDEX_STS_START}';
	var INDEX_STS_STOP='${INDEX_STS_STOP}';
	var defSrc='${defSrc}';
	if(preview){
		index_sts_column_width  ="26%";
		isPreview  =  false;
	}
	function initSearchForm() {
		//搜索表单应用ligerui样式
		$("#search").ligerForm({
			fields : [{
				display : '指标编号',
				name : "indexNo",
				newline : true,
				type : "text",
				cssClass : "field",
				attr : {
					field : 't1_.indexNo',
					op : "like"
				}
			}, {
				display : '指标名称',
				name : "indexNm",
				newline : false,
				type : "text",
				cssClass : "field",
				attr : {
					field : 'indexNm',
					op : "like"
				}
			}, {
				display : '指标类型',
				name : 'indexType',
				newline : false,
				type : "select",
				comboboxName:"index_type_box",
				cssClass : "field",
				attr : {
					field : 'indexType',
					op : "="
				},
				options : {
					url : "${ctx}/report/frame/idx/indexTypeList.json?defSrc="+defSrc
				}
			}/* , {
				display : "业务类型",
				name : "busiType",
				newline : false,
				type : "select",
				options : {
					url : "${ctx}/rpt/frame/dataset/busiTypeList.json"
				},
				attr : {
					field : "busiType",
					op : "="
				}
			} */]
		});
	};
	function initGrid() {
		var  columnArr  =[{
			display : '指标编号',
			name : 'id.indexNo',
			width : "15%",
			align : 'left',
			render:function(row){
				   if(preview){
					 return  "<a href='javascript:void(0)' style='color:blue'     onclick='showBusiInfoSingle(\""+row.id.indexNo+"\","+row.id.indexVerId+",\""+row.infoRights+"\")'>"+ row.id.indexNo+"</a>";
				   }else{
					   return   row.id.indexNo;
				   }
				}
			},{
			display : '指标名称',
			name : 'indexNm',
			width : "20%",
			align : 'left'
			
		},  {
			display : '指标类型',
			name : 'indexType',
			width : "15%",
			align : 'center',
			render :function(a,b,c){
				switch(c){
				case "01":
					return "根指标";
					break;
				case "02":
					return "组合指标";
					break;
				case "03":
					return "派生指标";
					break;
				case "04":
					return "泛化指标";
					break;
				case "05":
					return "总账指标";
					break;
				case "07":
					return "补录指标";
					break;
				}
			}
		}];
		var toolbar = null;
		if(!preview){
			toolbar = {};
			columnArr.push({
						display : '起始日期',
						name : 'startDate',
						width : "15%",
						align : 'center'
					}, {
						display : '终止日期',
						name : 'endDate',
						width : "15%",
						align : 'center',
						render:function(row){
							if(!row.endDate){
								return "";
							}
							return  row.endDate;
						}
					},{
						display : '是否发布',
						name : 'indexSts',
						width : index_sts_column_width,
						align : 'center',
						render:function(row){
							return renderHandler(row);
						}						
					}
		   );
		}else{
		 	columnArr.push({
				display : '起始日期',
				name : 'startDate',
				width : "19%",
				align : 'center'
			}, {
				display : '终止日期',
				name : 'endDate',
				width : "19%",
				align : 'center',
				render:function(row){
					if(!row.endDate){
						return "";
					}
					return  row.endDate;
				}
			},{
				display : '历史版本',
				name : 'indexSts',
				width : "17%",
				isSort : false,
				editor : {
					type : 'text'
				},
				render : function(row) {
						return "<a href='javascript:void(0)'   style='color:blue'   onclick='showVersionInfoInRow(\""+row.id.indexNo+"\",\""+row.id.indexVerId+"\")'>历史版本</a>";
				}
			}); 
		}
		var  url_= "${ctx}/report/frame/idx/getIdxInfoList.json?d="+new Date().getTime();
		url_ += "&auth=Y";
		if(defSrc){
	         url_ += "&defSrc="+defSrc;
        }
		
		grid = $("#maingrid").ligerGrid({
			toolbar : toolbar,
			checkbox : isPreview,
			columns : columnArr,
			dataAction : 'server', //从后台获取数据
			usePager : true, //服务器分页
			alternatingRow : true, //附加奇偶行效果行
			colDraggable : true,
			url :url_ ,
			sortName : 'indexNm',//第一次默认排序的字段
			sortOrder : 'asc', //排序的方式
            delayLoad:true,
			pageParmName : 'page',
			pagesizeParmName : 'pagesize',
			rownumbers : true,
			width : '100%',
			height : '99%',
			onAfterShowData : function(){
				if (!$.browser.msie || parseInt($.browser.version, 10) >= 9) {
					if($(".rcswitcher").length > 0){
						$(".rcswitcher").rcSwitcher({
							onText: '启用',
							offText: '停用',
							height:16.5,
							autoFontSize : true
						}).on({
							'turnon.rcSwitcher': function( e, dataObj ){
								var indexNo = dataObj.$input.attr("indexNo");
								var indexVerId = dataObj.$input.attr("indexVerId");
								var rowId = dataObj.$input.attr("rowid");
								if(indexNo
										&& rowId){
									changeSts(indexNo , indexVerId,'N' , rowId);
								}
						    },
						    'turnoff.rcSwitcher': function( e, dataObj ){
						    	var indexNo = dataObj.$input.attr("indexNo");
								var indexVerId = dataObj.$input.attr("indexVerId");
								var rowId = dataObj.$input.attr("rowid");
								if(indexNo
										&& rowId){
									changeSts(indexNo , indexVerId, 'Y' , rowId);
								}
						    }			
						});
					}
				}
			}
		});
	};
	
	function renderHandler(row){
		if ($.browser.msie && parseInt($.browser.version, 10) < 9) {
			if ("Y"== row.indexSts){
				return "启用"
			} else {
				return "停用";
			}
		}else{
			var html = "<input class='rcswitcher' type='checkbox'  name='check"+row.id.indexNo+"' indexNo='"+row.id.indexNo+"' indexVerId='"+row.id.indexVerId+"' rowId='"+row.__id+"' ";
			if(row.indexSts == "Y"){
				html += "checked";
			}
			html += " />";
			return html;
		}
	}
	
	// 更改角色状态
	function changeSts(indexNo,indexVerId , sts , rowId){
		$.ajax({
			cache : false,
			async : false,
			url : '${ctx}/report/frame/idx/updateIndexSts',
			dataType : 'json',
			type : "GET",
			data : {
				id : indexNo,
				vid : indexVerId,
				indexSts : sts
			},
			beforeSend : function() {
			},
			success : function(result){
				if(rowId
						&& grid){
					var row = grid.getRow(rowId);
					if(row){
						row.indexSts = sts;
					}
				}
			}
		});	
	}
	
	function initToolBar() {
		var toolBars = [ {
			text : '增加',
			click : f_open_add,
			operNo : 'idx-info-add',
			icon : 'fa-plus',
		}, {
			text : '修改',
			click : f_open_update,
			operNo : 'idx-info-modify',
			icon : 'fa-pencil-square-o'
		}, {
			text : '删除',
			click : f_delete,
			operNo : 'idx-info-delete',
			icon : 'fa-trash-o'
		} ,/* {
			text : "配置",
			icon : "fa-puzzle-piece",
			operNo : 'idx-info-config',
			menu : {
				items : [{
					text : '对比组配置',
					click : idx_compgrp,
					icon: 'idx-info-config'
				},{
					text : '同类组配置',
					click : idx_simigrp,
					icon: 'idx-info-config'
				}]
			}
		},  {
			text : "数据处理",
			icon : "fa-cog",
			operNo : 'idx-info-impexp',
			menu : {
				items : [{
					text : '导入对比组',
					click : importCompgrp,
					icon:"fa-upload"
				},{
					text : '导出对比组',
					click : exportCompgrp,
					icon:"fa-download"
				},{
					text : '导入同类组',
					click : importSimilar,
					icon: "fa-upload"
				},{
					text : '导出同类组',
					click : exportSimilar,
					icon: "fa-download",
					color :'#fcca54'
				}]
			}
		}, {
			icon : 'fa-code-fork',
			text : '版本',
			operNo : 'idx-info-calendar',
			click : showVersionInfo
		},{
			text : '标签查看',
			click : idx_label,
			icon:"fa-puzzle-piece"
		}*/];
		BIONE.loadToolbar(grid, toolBars, function() {
		});
	}
	
	//标签配置
	function idx_label(){
		if(!grid){
			return ;
		}
		var rows = grid.getSelectedRows();
		if (rows.length == 1) {
			var selRow = rows[0];
			BIONE.commonOpenDialog(selRow.indexNm+"标签查看" , "labelEdit" , 800 , 400 , "${ctx}/bione/label/labelConfig/rel?id="+selRow.id.indexNo+"&labelObjNo=idx");
		}else{
			BIONE.tip("请选择一条记录");
		}	
	}
	
	function idx_compgrp(){
		if(!grid){
			return ;
		}
		var rows = grid.getSelectedRows();
		if (rows.length == 1) {
			var selRow = rows[0];
			if(rows[0].indexType == "总账指标"){
				BIONE.tip("请选择非总账指标");
				return;
			}
			window.parent.BIONE.commonOpenDialog(selRow.indexNm+"对比组配置" , "compgrpEdit" , 800 , 500 , "${ctx}/rpt/frame/idx/compgrp?indexNo="+selRow.id.indexNo);
		}else{
			BIONE.tip("请选择一条记录");
		}	
	}
	
	//指标同类组配置
	function idx_simigrp(){
		if(!grid){
			return ;
		}
		var rows = grid.getSelectedRows();
		if (rows.length == 1) {
			var selRow = rows[0];
			if(rows[0].indexType == "总账指标"){
				BIONE.tip("请选择非总账指标");
				return;
			}
			window.parent.BIONE.commonOpenDialog(selRow.indexNm+"同类组配置" , "simigrpEdit" , 800 , 500 , "${ctx}/rpt/frame/idx/simigrp?indexNo="+selRow.id.indexNo);
		}else{
			BIONE.tip("请选择一条记录");
		}	
	}
	
	//导入后刷新
	function reload(){
		grid.loadData();
	}
	
	//导入对比组，定义导入类型type为Compgrp
	function importCompgrp(){
		BIONE.commonOpenDialog("指标对比组导入", "importWin", 600, 480,
		"${ctx}/report/frame/wizard?type=Compgrp");
	}
	
	//指标同类组导入，定义导入类型type为Similar
	function importSimilar(){
		BIONE.commonOpenDialog("指标同类组导入", "importWin", 600, 480,
		"${ctx}/report/frame/wizard?type=Similar");
	}
	
	//导出指标对比组
	function exportCompgrp() {
		var type = 'Compgrp';
		var rows = grid.getSelectedRows();
		if (rows.length == 0) {
			BIONE.tip('请至少选择一条数据');
			return;
			}
		var id = "";
		var length = rows.length;
		for ( var i = 0; i < length; i++) {
			id += rows[i].id.indexNo + ",";
		}
		
		$.ajax({
			async:true,
			type:"POST",
			dataType:"json",
			url:"${ctx}/report/frame/wizard/download?ids="+id+"&type="+type,
			beforeSend : function(a, b, c) {
				BIONE.showLoading('正在导出数据中...');
			},
			success:function(data){
				BIONE.hideLoading();
				if(data.fileName==""){
					BIONE.tip('导出异常');
					return;
				}
				var src='${ctx}/report/frame/wizard/export?type='+type+'&fileName='+data.fileName;//导出成功后的excell文件地址
				window["downdload"] = $('<iframe id="download"  style="display: none;"/>');//下载文件显示框
				$('body').append(downdload);//添加文件显示框在body的下方
				downdload.attr('src', src);//给下载文件显示框加上文件地址链接
			},
			error : function(result) {
				BIONE.hideLoading();
				//BIONE.tip('发现系统错误 <BR>错误码：' + result.status);
			}
		});
				
	}
	//导出指标同类组
	function exportSimilar(){
		var type = 'Similar';
		$.ajax({
			async:true,
			type:"POST",
			dataType:"json",
			url:"${ctx}/report/frame/wizard/download",
			data:{type:type},
			beforeSend : function(a, b, c) {
				BIONE.showLoading('正在导出数据中...');
			},
			success:function(data){
				BIONE.hideLoading();
				if(data.fileName==""){
					BIONE.tip('导出异常');
					return;
				}
				var src='${ctx}/report/frame/wizard/export?type='+type+'&fileName='+data.fileName;//导出成功后的excell文件地址
				window["downdload"] = $('<iframe id="download"  style="display: none;"/>');//下载文件显示框
				$('body').append(downdload);//添加文件显示框在body的下方
				downdload.attr('src', src);//给下载文件显示框加上文件地址链接
			},
			error : function(result) {
				BIONE.hideLoading();
				//BIONE.tip('发现系统错误 <BR>错误码：' + result.status);
			}
		});
	}
	
	$(function() {	
		initSearchForm();
		initGrid();
		grid.setParm("indexCatalogNo",indexCatalogNo);
		grid.setParm("indexNm",indexName_);
		grid.loadData();
		initToolBar();
		BIONE.addSearchButtons("#search", grid, "#searchbtn");
		$("input[name='indexCatalogNo']").val(indexCatalogNo);
		if(indexName_){
		$("input[name='indexNm']").val(indexName_);
		}
	});
	function f_open_add() {
		if (indexCatalogNo&&parent.currentNode) {
			window.parent.curCatalogName = parent.currentNode.text;
			dialog = window.parent.BIONE.commonOpenDialog("新建指标",
					"idxEdit",$(parent.document).width(), $(parent.document).height(),
					"${ctx}/report/frame/idx/edit?indexCatalogNo="
							+  encodeURI(encodeURI(indexCatalogNo))+"&defSrc="+defSrc , null);
		} else {
			BIONE.tip("请选择一个指标目录。");
		}
	}
	function f_open_update() {
		var rows = grid.getSelectedRows();
		if (rows.length == 1) {
			dialog = window.parent.BIONE.commonOpenDialog("修改指标",
					"idxEdit",$(parent.document).width(), $(parent.document).height(),
					"${ctx}/report/frame/idx/"+rows[0].id.indexNo+"/edit?indexCatalogNo="
							+ encodeURI(encodeURI(rows[0].indexCatalogNo))
							+"&indexVerId="+rows[0].id.indexVerId+"&defSrc="+defSrc , null);
		}else{
			BIONE.tip("请选择一条记录");
		}		
	}
	 function  showVersionInfo(){
		 var rows = grid.getSelectedRows();
		 if (rows.length == 1) {
		    dialog = window.parent.BIONE.commonOpenLargeDialog("版本信息",
					"idxInfoVerPreBox",
					"${ctx}/report/frame/idx/idxInfoVerPre?indexNo="+rows[0].id.indexNo+"&indexVerId="+rows[0].id.indexVerId+"&defSrc="+defSrc , null);
		 }else{
				BIONE.tip("请选择一条记录");
		 }	
	 }
	 function  showVersionInfoInRow(indexNo,indexVerId){
		    dialog = window.parent.BIONE.commonOpenLargeDialog("版本信息",
					"idxInfoVerPreBox",
					"${ctx}/report/frame/idx/idxInfoVerPre?indexNo="+indexNo+"&indexVerId="+indexVerId+"&defSrc="+defSrc , null);
	 }
	 function  showBusiInfo(){
		 var rows = grid.getSelectedRows();
		 if (rows.length == 1) {
			if(rows[0].infoRights!='Y'){
				dialog = window.parent.BIONE.commonOpenDialog("指标查看",
						"rptIdxInfoPreviewBox",
						$(parent.document).width(), $(parent.document).height(),"${ctx}/report/frame/idx/idxInfoPreview?indexNo="+rows[0].id.indexNo+"&indexVerId="+rows[0].id.indexVerId , null);
			}else{
				$.ajax({
					type : "POST",
					url : "${ctx}/report/frame/idx/isHasInfoRights.json?indexNo="+ rows[0].id.indexNo,
					success : function(result) {
						  if(result=="0"){
							  BIONE.tip("您没有查看此指标的权限！");
							  return;
						  }else if(result=="1"){
							  dialog = window.parent.BIONE.commonOpenDialog("指标查看",
										"rptIdxInfoPreviewBox",$(parent.document).width(), $(parent.document).height(),
										"${ctx}/report/frame/idx/idxInfoPreview?indexNo="+rows[0].id.indexNo+"&indexVerId="+rows[0].id.indexVerId , null);
						  }
					}
				});
			}
		 }else{
				BIONE.tip("请选择一条记录");
		 }	
	 }
	 function  showBusiInfoSingle(indexNo,indexVerId,infoRights){
		 dialog = window.parent.BIONE.commonOpenDialog("指标查看",
					"rptIdxInfoPreviewBox",$(parent.document).width(), $(parent.document).height(),
					"${ctx}/report/frame/idx/"+indexNo+"/show", null);
	 }
	 function f_delete() {
			var checkedRole = grid.getSelectedRows();
			if (checkedRole.length == 0) {
				BIONE.tip('请选择行');
				return;
			}
			window.parent.$.ligerDialog.confirm('将进行级联删除操作，确实要删除这' + checkedRole.length + '条记录吗!',
					function(yes) {
						if (yes) {
							var id = "";
							var vid= "";
							var length = checkedRole.length;
							for ( var i = 0; i < length; i++) {
								id += checkedRole[i].id.indexNo + ",";
							}
							$.ajax({
								type : "POST",
								url : '${ctx}/report/frame/idx/idxInfoDel/' + id,
								dataType : 'json',
								success : function(result) {
									if (result.msg) {
										if(result.msg=='1'){
											var  treeObj =  window.parent.currentTree;
											if(!treeObj){
												treeObj =  window.parent.leftTreeObj;
											}  
											treeObj.reAsyncChildNodes(window.parent.currentNode, "refresh");
											refreshIt();
											BIONE.tip("删除成功");
										}else{
											BIONE.tip(result.msg);
										}
									} else {
										BIONE.tip("删除失败");
									}
								},
								beforeSend : function() {
				 					parent.BIONE.loading = true;
				 					parent.BIONE.showLoading("正在操作中...");
				 				},
				 				complete : function() {
				 					parent.BIONE.loading = false;
				 					parent.BIONE.hideLoading();
				 				}
							});
						}
					});
		}
	
	function achieveIds() {
		ids = [];
		var rows = grid.getSelectedRows();
		for ( var i in rows) {
			ids.push(rows[i]);
		}
	}
	function changeCatalog(indexCatalogNo) {
		$("input[name='indexNm']").val("");
		$("input[name='indexType']").val("");
		if (indexCatalogNo) {
			$("input[name='indexCatalogNo']").val(indexCatalogNo);
		} else {
			$("input[name='indexCatalogNo']").val("");
		}
		refreshIt();
	}
	function  refreshIt(){
		var  indexNm_ = $("input[name='indexNm']").val();
		var  indexType_ = $("input[name='indexType']").val();
		$(".l-btn-hasicon").click();
		$("input[name='indexNm']").val(indexNm_);
		$.ligerui.get('index_type_box').selectValue(indexType_);
		grid.reload();
	}
	
    function  updateTheIndexSts(id,vid,indexSts){
        var  tipStr="是否启用此指标";
        if(indexSts==INDEX_STS_START){
        	tipStr="是否停用此指标";
        }
    	$.ligerDialog.confirm(tipStr, '指标状态修改', function(yes) {
			if (yes) {
				if (id != null) {
					BIONE.ajax({
						type : 'GET',
						url : "${ctx}/report/frame/idx/updateIndexSts?id="+id+"&vid="+vid+"&indexSts="+indexSts
					}, function(result) {
						var returnStr = result.msg;
						if (returnStr == "0") {
							refreshIt();
							BIONE.tip("状态更新成功");
						}else {
							BIONE.tip("状态更新失败");
						} 
					});
				}
			}
			var checkedRole = grid.getSelectedRows();
			var length = checkedRole.length;
			if (length > 0) {
				for ( var i = 0; i < length; i++) {
					var  currentRow  = checkedRole[i];
					var   id_ = currentRow.id.indexNo;
					var   vid_ = currentRow.id.indexVerId;
					if(id_==id&&vid_==vid){
						$(".l-selected").click();
						break;
					}
				}
			}
		});
    }
</script>
</head>
<body>
	<div id="template.right.down">
		<div id="aaa">
			<div id="maingrid" style="margin-top: 60px;"></div>
		</div>
	</div>
</body>
</html>
