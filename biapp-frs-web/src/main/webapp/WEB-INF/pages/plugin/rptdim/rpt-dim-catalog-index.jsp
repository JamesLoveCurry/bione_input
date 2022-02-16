<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<%@ include file="/common/taglibs.jsp"%>
<html>
<head>
<meta name="decorator" content="/template/template22_BS.jsp">
<script type="text/javascript">
	var currentNode;
	var tabObj;
	var leftTreeObj;
	var newFlag =false;
	var preview  ='${preview}';
	var async = {
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
				onClick : f_clickNode
			}
		};
	
	var exportTmp=function(fileName){
		var src = '';
		src = "${ctx}/rpt/frame/dimCatalog/exportTmpInfo?filepath="+fileName+"&d="+ new Date();
		downdload.attr('src', src);
	};
	
	var initExport=function() {
		window["downdload"] = $('<iframe id="download"  style="display: none;"/>');
		$('body').append(downdload);
	};
	
	// 加载树节点
	var initNodes = function(searchText){
		if(leftTreeObj)  leftTreeObj.destroy();
		leftTreeObj = $.fn.zTree.init($("#tree"),async,[]);
		if(leftTreeObj == null 
				|| typeof leftTreeObj == "undefined"){
			return ;
		}
		$.ajax({
			cache : false,
			async : true,
			url : "${ctx}/rpt/frame/dimCatalog/listcatalogs.json?preview=${preview}&d=" + new Date().getTime(),
			dataType : 'json',
			type : "post",
			data : {
				searchText:searchText
			},
			success : function(result){
				if(result != null
						&& typeof result != "undefined"){
					// 渲染新节点
					leftTreeObj.addNodes(null, result, true);
// 					leftTreeObj.expandAll(true);
				}
			},
			error:function(){
				BIONE.tip("加载失败，请联系系统管理员");
			}
		});
	}	
	//update
	function  updateDim(){
		var id;
		if (!currentNode) {
			BIONE.tip("请选择目录");
			return;
		} else if (currentNode.id == "${treeRoot}") {
			return;
		} else {
			var  type  =   currentNode.params.type;
			if(type=="dimTypeInfo"){
				return;
			}
			id = currentNode.data.catalogId;
		}
		var modelDialogUri = "${ctx}/rpt/frame/dimCatalog/update?catalogId=" + id;
		BIONE.commonOpenDialog("维度目录修改", "rptDimCatalogUpdateWin","660","320",modelDialogUri);
	}
	
	//delete
	function deleteDim() {
		if (!currentNode) {
			BIONE.tip("请选择目录");
			return;
		}
		var  type  =   currentNode.params.type;
		if(type=="dimTypeInfo"){
			return;
		}
		var id = currentNode.data.catalogId;
		if(id=='${treeRoot}'){
			return;
		}			
		if(currentNode.params.hasTypes=="true"){
			BIONE.tip("该维度目录下面包含维度类型，不能进行删除操作！");
			return;
		}
			$.ligerDialog.confirm('将执行级联删除操作，是否继续', '维度目录删除', function(yes) {
				if (yes) {
					if (id != null) {
						BIONE.ajax({
							type : 'POST',
							url : '${ctx}/rpt/frame/dimCatalog/deleteDim?dimNo=' + id
						}, function(result) {
							var returnStr = result.msg;
							if (returnStr == "0") {
								searchHandler();
								var f = document.getElementById("rptDimCatalogTypeInfoTabFrame");
								f.contentWindow.refreshIt();
								f.contentWindow.catalogId = "";
								currentNode = null;
								BIONE.tip("删除成功");
							}else {
								BIONE.tip("删除失败");
							} 
						});
					}
				}
			});
	}
	//新增目录
	function addDim() {
		newFlag = true;
		var id;
		if (!currentNode) {
			id = "${treeRoot}";
		} else if (currentNode.id == "${treeRoot}") {
			id = "${treeRoot}";
		} else {
			var  type  =   currentNode.params.type;
			if(type=="dimTypeInfo"){
				return;
			}
			id = currentNode.data.catalogId;
		}
		var modelDialogUri = "${ctx}/rpt/frame/dimCatalog/new?catalogId=" + id;
		BIONE.commonOpenDialog("维度目录添加", "rptDimCatalogAddWin","660","320",modelDialogUri);
	}
	 //树点击事件
	function f_clickNode(event, treeId, treeNode, clickFlag) {
		newFlag =false;
		currentNode = treeNode;
		var catalogId = currentNode.data.catalogId;
		var  type = currentNode.params.type;
		var f = document.getElementById("rptDimCatalogTypeInfoTabFrame");
		if(type=="dimTypeInfo"){
			initCurrentTab("iteminfo",currentNode.data.dimTypeNo,currentNode.data.dimTypeNm);
		}else{
			if(clickFlag == "1") {
				if("${treeRoot}" == currentNode.id) {
					initCurrentTab("typeinfo","");
				} else {
					initCurrentTab("typeinfo",currentNode.data.catalogId,currentNode.data.catalogNm);
                }
			}
		}
		
	} 	
	function  initCurrentTab(type,id,name){
		tabObj.removeTabItem("rptDimCatalogTypeInfoTab");
		curTabUri = "${ctx}/rpt/frame/dimCatalog/dimType?catalogId="+id+"&preview="+preview;
		tabTitleName = "维度"; 
		if(type!="iteminfo"){
			if(type!=""&&id){
			   tabTitleName ="维度目录["+name+"]: "+tabTitleName;
		    }  
		}else{
			tabTitleName ="维度["+name+"]: 维度项";
		}
		if(type!="iteminfo"){
			tabObj
			.addTabItem({
				tabid : "rptDimCatalogTypeInfoTab",
				text:tabTitleName,
				showClose : false,
				content : '<iframe frameborder="0" id="rptDimCatalogTypeInfoTabFrame" name="rptDimCatalogTypeInfoTabFrame"  style="height:'+height+'px;" src='+curTabUri+' ></iframe>'
			});
		}else{
			curTabUri = "${ctx}/rpt/frame/dimCatalog/dimItem?dimTypeNo=" +id+"&flag="+preview; 
			tabObj
			.addTabItem({
				tabid : "rptDimCatalogTypeInfoTab",
				text:tabTitleName,
				showClose : false,
				content : '<iframe frameborder="0" id="rptDimCatalogTypeInfoTabFrame" name="rptDimCatalogTypeInfoTabFrame"  style="height:'+height+'px;" src='+curTabUri+' ></iframe>'
			});	
		}
	    tabObj.selectTabItem("rptDimCatalogTypeInfoTab");
	}
	// 搜索动作
	var searchHandler = function(){
		var searchName = $("#treeSearchInput").val();
		initNodes(searchName);
	}
	$(function() {
		//初始化导出
		initExport();
		//初始化树查询按钮事件
		$("#treeSearchIcon").bind("click",function(e){
			searchHandler();
		});
		$("#treeSearchInput").bind("keydown",function(e){
			// 按下回车键进行查询
			var code = e.keyCode;
			if(code == null
					|| typeof code == "undefined"
					|| code != "13"){
				return ;
			}
			searchHandler();
		});
		tabObj = $("#tab").ligerTab({
			contextmenu : true
		});
		var curTabUri = "${ctx}/rpt/frame/dimCatalog/dimType";
		var $centerDom = $(window.document);
		height = $centerDom.height() - 26;
		initCurrentTab("","","");
		initNodes("");
		if(!preview){
				$("#treeToolbar").ligerTreeToolBar({
					items:[{
						icon:'config',
						text:'目录管理',
						operNo : 'rpt-dim-menu',
						menu:{
							width:90,
							items : [ {
								icon : 'add',
								text : '新建',
								click : addDim
							}, {
								line:true
							},{
								icon : 'delete',
								text : '删除',
								click : deleteDim
							},{
								line:true
							}, {
								icon : 'modify',
								text : '修改',
								click : updateDim
							}]
						}
					},{
						icon:'export',
						text:'数据处理',
						operNo : 'rpt-dim-impexp',
						menu:{
							width:90,
							items:[{
								icon:'import',
								text:'模板导入',
								click : importDimTmp
							},{
								icon:'export',
								text:'模板导出',
								click : exportDimTmp
							} ]
						}
					}]
				});
				if ($.browser.msie && parseInt($.browser.version, 10) == 7) {
					// ie7 divs position:relative bug
					$(".l-treemenubar-item").css("z-index" , '999');
				}
		}
		
		function exportDim(){
			commonOpenSmallDialog("导出数据","exportDim","${ctx}/rpt/frame/dimCatalog/exportDim");
		}
		
		function importDim(){
			BIONE.commonOpenDialog("导入数据","importDim",600,380,"${ctx}/rpt/frame/dimCatalog/impDim",null,
				function() {
					if(window.pathname!=null&&window.pathname!=""){
						$.ajax({
							cache : false,
							async : false,
							url : "${ctx}/rpt/frame/dimCatalog/deleteFile",
							dataType : 'json',
							data : {
								pathname: window.pathname
							},
							type : "POST"
						});
					}
				});
		};

		function commonOpenSmallDialog(title, name,
				url, beforeClose) {
			var width = 450;
			var height = 600;
			var _dialog = $.ligerui.get(name);
			if (_dialog) {
				$.ligerui.remove(name);
			}
			_dialog = $.ligerDialog.open({
				height : height,
				width : width,
				url : url,
				name : name,
				id : name,
				title : title,
				isResize : false,
				isDrag : false,
				isHidden : false
			});
			if (beforeClose != null && typeof beforeClose == "function") {
				_dialog.beforeClose = beforeClose;
			}
			return _dialog;
		};
	});
	
	function importDimTmp(){
		BIONE.commonOpenDialog("维度模板导入", "importWin", 600, 480,
				"${ctx}/report/frame/wizard?type=Dim");
		
	}
	
	function exportDimTmp(){
		BIONE.commonOpenDialog("维度模板导出", "exportWin", 600, 480,
				"${ctx}/report/frame/wizard/exportWin?type=Dim");
		
	}

	var exportExcel=function(fileName,type){
		var src='';
		src='${ctx}/report/frame/wizard/export?type='+type+'&fileName='+fileName;
		downdload.attr('src', src);
	};
	
	function   reload(){
		searchHandler();
	}
</script>
<title></title>
</head>
<body>
	<div id="template.left.up.icon">
		<img src="${ctx }/images/classics/icons/application_side_tree.png" />
	</div>
	<div id="template.left.up">
		<span style="font-size: 12px">目录管理</span>
	</div>
	<div id="template.right">
		<div id="tab" style="width: 100%; overflow: hidden;"></div>
	</div>
</body>
</html>