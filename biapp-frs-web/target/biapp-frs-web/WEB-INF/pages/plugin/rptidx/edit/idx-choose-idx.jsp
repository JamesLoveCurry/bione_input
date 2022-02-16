<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<%@ include file="/common/taglibs.jsp"%>
<html>
<meta name="decorator" content="/template/template14.jsp">
<head>

<script type="text/javascript">
	//在自定义参照中定义【{"url":"/report/frame/rptidx/idx-choose-idx.jsp","窗口宽度":"450","窗口高度":"600","是否显示标签":"Y"}】就可以直接弹出这个页面,并能将参照定义中的参数自动传到这里! 
	var v_dialogid = '${dialogid}';  //我做了一个设计,如果是从配置模板中的参照弹出到这个页面中必然有这个参数! 这是根据时间毫秒搓成的一个永远不会重名的id
	var v_par = null;
	if(v_dialogid){
		v_par = window.parent.winobj[v_dialogid];  //得到父亲窗口中的参数值
	}
	var idxlabelFlag = false;
	var searchObj = {exeFunction : "initNodes",searchType : "idx",obj:"searchObj"};//默认执行initNodes方法
	var idxtree ;
	var type = "";
	$(function() {
		$("#treeContainer").height($("#center").height()-50);
		initIdxTree();
		$("#idxhighSearchIcon").click(function(){
			if(idxlabelFlag){
				BIONE.commonOpenDialog("高级搜索","highSearch","600","250","${ctx}/rpt/frame/rptSearch/labelhighSearch?type=idx");
			}
			else{
				BIONE.commonOpenDialog("高级搜索","highSearch","600","250","${ctx}/rpt/frame/rptSearch/highSearch?searchObj="+JSON2.stringify(searchObj));
			}
		});
	});
	
	function idxshowtype() {
		$("#idxtreeSearchInput").val("");
		if($("#idxcatalog")[0].checked == true){
			type = "async";
			initIdxTree();
			idxlabelFlag = false;
		}
		else{
			type = "lasync";
			initIdxLabelTree();
			idxlabelFlag = true;
		}
	}
	
	function initIdxTree() {
		//树
		setting ={
				async:{
					enable:true,
					type:"post",
					url:"${ctx}/report/frame/idx/getAsyncTreeIdxShow.json",
					autoParam:["nodeType", "id", "indexVerId"],
					otherParam:{'isShowIdx':'1','isShowDim':'1', 'isShowMeasure':'1','isPublish':'1', "isAuth" : "1", "showEmptyFolder":""},
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
						title : "title"
					}
				},
				view:{
					selectedMulti:false
				},
				callback:{
					onClick : idxzTreeOnClick
				}
		};
	
		idxtree = $.fn.zTree.init($("#idxtree"), setting);

		$("#idxtreeSearchIcon").unbind("click");
		$("#idxtreeSearchInput").unbind("keydown");
		
		$("#idxtreeSearchIcon").live(
				'click',function(){
			initNodes($("#idxtreeSearchInput").val());
		});
		
		$("#idxtreeSearchInput").live('keydown',function(){
			if (event.keyCode == 13) {
				initNodes($("#idxtreeSearchInput").val());
			}
		});
		
	}
	
	function initIdxLabelTree(){
		setting ={
				async:{
					enable:true,
					type:"post",
					url:"${ctx}/report/frame/idx/getAsyncLabelTree.json",
					autoParam:["nodeType", "id", "indexVerId"],
					otherParam:{'isShowIdx':'1','isShowDim':'1', 'isShowMeasure':'1','isPublish':'1', "isAuth" : "0", "showEmptyFolder":""},
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
						title : "title"
					}
				},
				view:{
					selectedMulti:false
				},
				callback:{
					onClick : idxzTreeOnClick
				}
		};
	
		idxtree = $.fn.zTree.init($("#idxtree"), setting);
		$("#template.left.center").hide();

		$("#idxtreeSearchIcon").unbind("click");
		$("#idxtreeSearchInput").unbind("keydown");
		
		$("#idxtreeSearchIcon").live(
				'click',function(){
			initLabelNodes($("#idxtreeSearchInput").val());
		});
		
		$("#idxtreeSearchInput").live('keydown',function(){
			if (event.keyCode == 13) {
				initLabelNodes($("#idxtreeSearchInput").val());
			}
		});
	}
	
	
	// 刷新树节点
	function initNodes(searchNm,searchObj){
		if(searchNm == null || searchNm == ""){
			if(type != "async"){
				type = "async";
				setting ={
						async:{
							enable:true,
							type:"post",
							url:"${ctx}/report/frame/idx/getAsyncTreeIdxShow.json",
							autoParam:["nodeType", "id", "indexVerId"],
							otherParam:{'isShowIdx':'1','isShowDim':'1', 'isShowMeasure':'1','isPublish':'1', "isAuth" : "1", "showEmptyFolder":""},
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
								name:"text"
							}
						},
						view:{
							selectedMulti:false
						},
						callback:{
							onClick : idxzTreeOnClick
						}
				};
				idxtree = $.fn.zTree.init($("#idxtree"), setting);
			}
		}
		else{
			var _url = "${ctx}/report/frame/idx/getSyncTree";
			var data = {'searchNm':searchNm ,'isShowIdx':'1','isShowDim':'1', 'isShowMeasure':'1','isPublish':'1', "isAuth" : "0", "showEmptyFolder":""};
			if(searchObj != null && searchObj != ""){
				_url = "${ctx}/report/frame/idx/getSyncTreePro";
				data = {'searchObj':JSON2.stringify(searchObj) ,'isShowIdx':'1','isShowDim':'1', 'isShowMeasure':'1','isPublish':'1', "isAuth" : "0", "showEmptyFolder":""};
			}
			if(type !="sync"){
				type = "sync";
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
							onClick : idxzTreeOnClick
						}
				};
				idxtree = $.fn.zTree.init($("#idxtree"), setting);
			}
			BIONE.loadTree(_url,idxtree,data,function(childNodes){
				if(childNodes){
					for(var i = 0;i<childNodes.length;i++){
						childNodes[i].nodeType = childNodes[i].params.nodeType;
						childNodes[i].indexVerId = childNodes[i].params.indexVerId;
					}
				}
				return childNodes;
			});
		}
	}
	
	function initLabelNodes(searchNm,searchObj){
		if(searchNm == null || searchNm == ""){
			if(type != "lasync"){
				type = "lasync";
				setting ={
						async:{
							enable:true,
							type:"post",
							url:"${ctx}/report/frame/idx/getAsyncLabelTree.json",
							autoParam:["nodeType", "id", "indexVerId"],
							otherParam:{'isShowIdx':'1','isShowDim':'1', 'isShowMeasure':'1','isPublish':'1', "isAuth" : "0", "showEmptyFolder":""},
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
								name:"text"
							}
						},
						view:{
							selectedMulti:false
						},
						callback:{
							onClick : idxzTreeOnClick
						}
				};
				idxtree = $.fn.zTree.init($("#idxtree"), setting);
			}
		}
		else{
			var _url = "${ctx}/report/frame/idx/getSyncLabelTree.json";
			var data = {'searchNm':searchNm ,'isShowIdx':'1','isShowDim':'1', 'isShowMeasure':'1','isPublish':'1', "isAuth" : "0", "showEmptyFolder":""};
			if(type !="lsync"){
				type = "lsync";
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
							onClick : idxzTreeOnClick
						}
				};
				idxtree = $.fn.zTree.init($("#idxtree"), setting);
			}
			BIONE.loadTree(_url,idxtree,data,function(childNodes){
				if(childNodes){
					for(var i = 0;i<childNodes.length;i++){
						childNodes[i].nodeType = childNodes[i].params.nodeType;
						childNodes[i].indexVerId = childNodes[i].params.indexVerId;
					}
				}
				return childNodes;
			});
		}
	}
	
	function initLabelIdx(ids,stype){
		type = "lsync";
		var _url = "${ctx}/report/frame/idx/getSyncLabelFilter.json";
		var data = {'ids':ids ,'type':stype,'isShowDim':'1', 'isAuth':'0','isShowMeasure':'1'};
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
					onClick : idxzTreeOnClick
				}
		};
		idxtree = $.fn.zTree.init($("#idxtree"),setting,[]);
		BIONE.loadTree(_url,idxtree,data,function(childNodes){
			if(childNodes){
				for(var i = 0;i<childNodes.length;i++){
					childNodes[i].nodeType = childNodes[i].params.nodeType;
					childNodes[i].indexVerId = childNodes[i].params.indexVerId;
				}
			}
			return childNodes;
		},false);
	}
	
	function idxzTreeOnClick(a,b,treeNode){
		if(treeNode.nodeType == "idxInfo" && treeNode.data.idxType != '05'){
			if(v_par==null){  //原来的逻辑
			 window.parent.$.ligerui.get("srcIndexNo")._changeValue(treeNode.id,treeNode.text);
			 window.parent.$("#srcIndexNo").attr("title",treeNode.text);
			 window.parent.initDimNodes(treeNode);
			 BIONE.closeDialog("srcIndexWin");
			}else{  //从参照中弹出来的逻辑!尽可能重用原来的功能
				setRefValueText(treeNode.data.id.indexNo,treeNode.data.indexNm);  //回写参照中的值
		    }
		}
		else if(treeNode.nodeType == "measureInfo"){
			if(v_par==null){  //原来的逻辑
				window.parent.$.ligerui.get("srcIndexNo")._changeValue(treeNode.data.id.indexNo+"."+treeNode.id,treeNode.data.indexNm+"."+treeNode.text);
				window.parent.$("#srcIndexNo").attr("title",treeNode.data.indexNm+"."+treeNode.text);
				window.parent.initDimNodes(treeNode);
				BIONE.closeDialog("srcIndexWin");
			}else{  //从参照中弹出来的逻辑!
				setRefValueText(treeNode.data.id.indexNo,treeNode.data.indexNm);  //回写参照中的值
		    }
		}
	}

	//设置参照中的值!
	function setRefValueText(_idxNo,_idxName){
		var v_ligerForm = v_par.editform; //Ligerform表单
		var str_fieldName = v_par.fieldName;  //字段名
		var v_item = v_ligerForm.getEditor(str_fieldName);  //从Form中取得控件
		v_item.setValue(_idxNo);  //设置参照的value
		v_item.setText("【" + _idxNo + "】" + _idxName);  //设置参照的显示文本,客户说要编码与名称一起显示!
		BIONE.closeDialog(v_dialogid);
	}
</script>
</head>
<body>
	<div id="template.center">
		<div id="left" style="background-color: #FFFFFF">
			<div id="idxtreeSearchbar"
				style="width: 99%; margin-top: 2px; padding-left: 2px;">
				<ul>
					<li style="width: 98%; text-align: left;">
						<div class="l-text-wrapper" style="width: 100%;">
							<div class="l-text l-text-date" style="width: 100%;">
								<input id="idxtreeSearchInput" type="text" class="l-text-field"
									style="width: 75%;" />
								<div class="l-trigger" style="right: 26px;">
								<a id="idxtreeSearchIcon" class="icon-search search-size"></a></div>
								<i class="l-trigger" style="right:20px;width:1px;height:12px;border-left:1px dotted gray;margin-top:2px;"></i>
								<div title="高级筛选" class="l-trigger" style="right: 0px;">
									<a id="idxhighSearchIcon" class = "icon-light_off font-size"></a>
								</div>
							</div>
						</div>
					</li>
				</ul>
			</div>
			<div
				style="background-image: url(/biapp-frs-web/css/classics/ligerUI/Gray/images/ui/gridbar.jpg); border: 1px solid #D6D6D6; padding-left: 10px; padding-top: 3px; height: 20px;">
				<span>目录</span><input type="radio" id="idxcatalog"
					name="idxshowtype" value="idxcatalog" style="width: 20px;"
					onclick="idxshowtype()" checked="true" /> <span>标签</span> <input
					type="radio" id="idxlabel" name="idxshowtype" value="idxlabel"
					style="width: 20px;" onclick="idxshowtype()" />
			</div>
			<div id="treeContainer"
				style="width: 100%; overflow: auto; clear: both; background-color: #FFFFFF;">
				<ul id="idxtree"
					style="font-size: 12; background-color: #FFFFFF; width: 92%"
					class="ztree"></ul>
			</div>
		</div>
		<div id="bottom">
		<div class="form-bar">
			<div id="bottom" style="padding-top: 5px"></div>
		</div>
	</div>

	</div>
	
</body>
</html>