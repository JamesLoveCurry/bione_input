<%@ page language="java" pageEncoding="UTF-8"%>
<%@ include file="/common/taglibs.jsp"%>
<%@ page
	import="org.apache.shiro.web.filter.authc.FormAuthenticationFilter"%>
<html>
<head>
<meta name="decorator" content="/template/template2B.jsp">
<script type="text/javascript" src="${ctx}/js/udip/UDIP.js"></script>
<script type="text/javascript" src="${ctx}/js/bione/json2.js"></script>
<style>
.searchtitle img{
	left : 310px;
}
</style>
<script type="text/javascript">
	var id, dirId = '',taskState = "";var checkedTempleId = [],checkedTempleName = [];;
	var tempState = "0";
	var rptId = "";
	var buttons = [];
	var searchA = "Y";
	var showA = "Y";
	var autoReload = true;
	var dim = [];
	var orgType,Dname;
	var dimItem = [{}];
	var checkedSearch = [],checkedSearchCN = [],checkedShow = [],checkedShowCN = [];
	var UcheckedSearch = [],UcheckedSearchCN = [],UcheckedShow = [],UcheckedShowCN = [];
	
	var rptTreeNodeIcon = "${ctx}/images/classics/icons/layout_sidebar.png";
	$(function() {
		
		initTree();
		searchForm();
		initConfigForm();
		initButton();
		initGrid();
		BIONE.addSearchButtons("#search", grid, "#searchbtn");
		//document.getElementById('treeType').innerHTML = "报表树";
		BIONE.loadToolbar(grid, [ {
			text : '数据下载',
			click : dataDownLoad,
			icon : 'loaction'
		},{
			text : '配置',
			click : config,
			icon : 'config'
		} ]);
		$("#maingrid").height(window.outerHeight - 200);
		$("#maingridgrid").height(window.outerHeight - 200+15);
		
	});
	
	function searchAll(){
		if(searchA=="Y"){
			checkedAll(checkedSearch,"search")
			searchA="N";
		}else if(searchA=="N"){
			uncheckedAll(checkedSearch,"search")
			searchA="Y";
		}
	}
	
	function showAll(){
		if(showA=="Y"){
			checkedAll(checkedShow,"show")
			showA="N";
		}else if(showA=="N"){
			uncheckedAll(checkedShow,"show")
			showA="Y";
		}
		
	}
	
	function checkedAll(check,type){
		if(type=="show"){
			for(i=0;i<check.length;i++){
				$("#"+"show_"+check[i]).ligerCheckBox({ disabled: false }).setValue(true);
			}
		}else if(type=="search"){
			for(i=0;i<check.length;i++){
				$("#"+"search_"+check[i]).ligerCheckBox({ disabled: false }).setValue(true);
			}
		}
	}
	
	function uncheckedAll(check,type){
		if(type=="show"){
			for(i=0;i<check.length;i++){
				$("#"+"show_"+check[i]).ligerCheckBox({ disabled: false }).setValue(false);
			}
		}else if(type=="search"){
			for(i=0;i<check.length;i++){
				$("#"+"search_"+check[i]).ligerCheckBox({ disabled: false }).setValue(false);
			}
		}
	}
	
	function searchForm() {
		$("#search").ligerForm({
			fields : []
		});
	}
	
	function configSearchForm(){
		var fields = liger.get("search").get("fields");
		fields = [];
		var le = -1;
		var url = "${ctx}/frs/integratedquery/detailsel/getDimInfo";
		var data = {
			rptId : rptId		
		};
		$.ajax({
			cache : false,
			async : false,
			url : url,
			dataType : 'json',
			type : "post",
			data : data,
			success : function(result){
				dim = [];
				if(result&&result!=null){
					for(i=0;i<result.length;i++){
						dim.push(result[i])
					}
				}
			}
		});
		for(i=0;i<UcheckedSearch.length;i++){
			var isNewLine = false;
			le=le+1;
			if((le%3) == 0){   
				isNewLine = true;   //当 fields数量为3 的倍数时换行
			}
			var d = false;
			var currentDim;
			for(k=0;k<dim.length;k++){
				if(UcheckedSearchCN[i]==dim[k].cnNm&&dim[k].dimTypeNo!=null){
					d = true;
					currentDim = dim[k];
				}
			}
			if(d){
				if(currentDim.dimTypeNo=="DATE"){
					fields.push({
						display : UcheckedSearchCN[i],
						name : UcheckedSearch[i],
						newline : isNewLine,
						width: 150,
						type : "date",
						cssClass : "field",
						comboboxName : UcheckedSearch[i],
						options : {
							format : "yyyyMMdd"
						},
						attr : {
							op : "=",
							field : UcheckedSearch[i]
						}
					});
				}else if(currentDim.dimTypeNo=="ORG"){
					var orgTreeSkipUrl = "${ctx}/frs/rptfill/reject/searchOrgTree";
					Dname = UcheckedSearch[i];
					fields.push({
						display : UcheckedSearchCN[i],
						name : UcheckedSearch[i],
						newline : isNewLine,
						width: 150,
						type : "select",
						cssClass : "field",
						comboboxName : UcheckedSearch[i],
						attr : {
							op : "=",
							field : UcheckedSearch[i]
						},
						options : {
							     onBeforeOpen : function() {
							       var rptFillFlag = "detail";
							       var height = $(window).height() - 120;
							       var width = $(window).width() - 480;
							       window.BIONE.commonOpenDialog("机构树", "taskOrgWin", width, height, orgTreeSkipUrl+"&orgType="+orgType+"&rptFillFlag="+rptFillFlag+"&Dname="+Dname, null);
							       return false;
							     }, 
							     cancelable  : true
						}
					});
				}else{
					$.ajax({
						cache : false,
						async : false,
						url : "${ctx}/frs/integratedquery/detailsel/getDimItem?dimTypeNo="+currentDim.dimTypeNo,
						dataType : 'json',
						type : "post",
						success : function(result){
							if(result!=null){
								dimItem = result;
							}else{
								dimItem = [{}];
							}
						}
					});
					fields.push({
						display : UcheckedSearchCN[i],
						name : UcheckedSearch[i],
						newline : isNewLine,
						width: 150,
						type : "select",
						cssClass : "field",
						comboboxName : UcheckedSearch[i],
						options : {
							data : dimItem
						},
						attr : {
							op : "=",
							field : UcheckedSearch[i]
						}
					});
				}
			}else{
				fields.push({
					display : UcheckedSearchCN[i],
					name : UcheckedSearch[i],
					newline : isNewLine,
					width: 150,
					type : "text",
					cssClass : "field",
					comboboxName : UcheckedSearch[i],
					attr : {
						op : "=",
						field : UcheckedSearch[i]
					}
				});
			}
		}
		$("#search").ligerForm({fields: fields});
	}
	
	
    function queryConditionList(){
  	   var form = $('#formsearch');
 	   var data = {};
 	   for(i=1;i<UcheckedSearch.size;i++){
 		   if("" != $.ligerui.get(UcheckedSearch[i]).getValue())
 			  data.UcheckedSearch[i]=$.ligerui.get(UcheckedSearch[i]).getValue();
 		}
 	   var rule = BIONE.bulidFilterGroup(form);
       if (rule.rules.length) {
 			data.condition=JSON2.stringify(rule);
 		}else{
 			data.condition="";
 		}
       return data; 
    }
	
	function initConfigForm(){
		var url = "${ctx}/frs/integratedquery/detailsel/getEditInfo";
		var data = {
			editType : "1",
			rptId : rptId
		}
		$.ajax({
			cache : false,
			async : false,
			url :url,
			dataType : 'json',
			type : "post",
			data : data,
			success : function(result){
				checkedSearch = [];
				checkedSearchCN = [];
				if(result.checked.length>0){
					for(i=0;i<result.checked.length;i++){
						checkedSearch.push(result.checked[i]);
						checkedSearchCN.push(result.checkedCN[i]);
					}
				}
			}
		})
		$("#searchConfig").ligerForm({
			fields : []
		});
		var le = -1;
		var fields = liger.get("searchConfig").get("fields");
		for(i=0;i<checkedSearch.length;i++){
			var isNewLine = false;
			le=le+1;
			if((le%4) == 0){   
				isNewLine = true;   //当 fields数量为4 的倍数时换行
			}
			fields.push({
				display : checkedSearchCN[i],
				name : "search_"+checkedSearch[i],
				newline : isNewLine,
				labelWidth : 150, width : 30, space : 20,
				type : "checkbox"
			});
		}
		$("#searchConfig").ligerForm({fields: fields});
		/* var inofUrl = "${ctx}/frs/integratedquery/detailsel/getEditInfo";
		var infoData = {
				editType : "2",
				rptId : rptId
			} */
		$.ajax({
			cache : false,
			async : false,
			url : "${ctx}/frs/integratedquery/detailsel/getEditInfo",
			dataType : 'json',
			data : {
				editType : "2",
				rptId : rptId
			},
			type : "post",
			success : function(result){
				checkedShow = [];
				checkedShowCN = [];
				if(result.checked.length>0){
					for(i=0;i<result.checked.length;i++){
						checkedShow.push(result.checked[i]);
						checkedShowCN.push(result.checkedCN[i]);
					}
				}
			}
		})
		
		$("#showConfig").ligerForm({
			fields : []
		});
		var le = -1;
		var fields = liger.get("showConfig").get("fields");
		for(i=0;i<checkedShow.length;i++){
			var isNewLine = false;
			le=le+1;
			if((le%4) == 0){   
				isNewLine = true;   //当 fields数量为4 的倍数时换行
			}
			fields.push({
				display : checkedShowCN[i],
				name : "show_"+checkedShow[i],
				newline : isNewLine,
				labelWidth : 150, width : 30, space : 20,
				type : "checkbox"
			});
		}
		$("#showConfig").ligerForm({fields: fields});
	}
	
	function initButton(){
		buttons.push({
			text : '设为默认',
			onclick : saveDefault
		},{
		    text : '保存',
		    onclick : save
		});
		BIONE.addFormButtons(buttons);
	}
	
	function closeConfig(){
		if(autoReload){
			grid.set('url', '${ctx}/frs/integratedquery/detailsel/infoList?&rptId='+rptId+'&UcheckedShow='+UcheckedShow);
			configGrid();
		}else{
			grid.set('url', '${ctx}/frs/integratedquery/detailsel/infoList');
			configGrid();
		};
		var display = document.getElementById("config").style.display;
		if(display=="block"){
			document.getElementById("config").style.display='none';
		}
	}
	
	function config(){
		var display = document.getElementById("config").style.display;
		if(display=='none'){
			document.getElementById("config").style.display='block';
		}else{
			document.getElementById("config").style.display='none';
		}
	}
    
	//数据下载
	function dataDownLoad(){
		download = $('<iframe id="download"  style="display: none;"/>');
        $('body').append(download);
        var data = queryConditionList();
 	    var condition = data.condition;
 	    if(rptId!=null&&rptId!=""&&rptId!="undefined"&&rptId!="ROOT"){
			$.ajax({
				type : "POST",
				url : "${ctx}/frs/integratedquery/detailsel/getCount?rptId="+rptId+"&UcheckedShow="+UcheckedShow+"&UcheckedShowCN="+encodeURI(encodeURI(UcheckedShowCN))+"&condition="+encodeURI(encodeURI(condition)),
				success : function(result) {
					if(result>20000){
						$.ligerDialog.confirm("数据量太大,仅支持前两万条记录的下载,是否继续下载?", function(yes) { 
							if(yes){ 
						 	    var src = "${ctx}/frs/integratedquery/detailsel/downLoad?rptId="+rptId+"&UcheckedShow="+UcheckedShow+"&UcheckedShowCN="+encodeURI(encodeURI(UcheckedShowCN))+"&condition="+encodeURI(encodeURI(condition));
						 		download.attr('src', src);
							}
						})
					}else{
				 	    var src = "${ctx}/frs/integratedquery/detailsel/downLoad?rptId="+rptId+"&UcheckedShow="+UcheckedShow+"&UcheckedShowCN="+encodeURI(encodeURI(UcheckedShowCN))+"&condition="+encodeURI(encodeURI(condition));
				 		download.attr('src', src);
					}
				}
			});
 	    }else{
 	    	BIONE.tip("请选择一张报表")
 	    }
	}
	
	
	function save(){
		UcheckedSearch = [];
		UcheckedSearchCN = [];
		UcheckedShow = [];
		UcheckedShowCN = [];
		// 获取选中的
		//var length = 0;
		$("#searchConfig").find("input").each(function(){
			$this = $(this);
			if($this[0].checked){
				UcheckedSearch.push($this.attr("name").substr(7));
				var text = $this.parent().parent().parent().children(0).text();
				text = text.substring(0,text.length-1);
				UcheckedSearchCN.push(text);
			}
		});
		$("#showConfig").find("input").each(function(){
			$this = $(this);
			if($this[0].checked){
				UcheckedShow.push($this.attr("name").substr(5));
				var text = $this.parent().parent().parent().children(0).text();
				text = text.substring(0,text.length-1);
				UcheckedShowCN.push(text);
			}
		});
		autoReload = false;
		closeConfig();
		autoReload = true;
		configSearchForm();
		configGrid();
	}
	
	function saveDefault(){
		UcheckedSearch = [];
		UcheckedSearchCN = [];
		UcheckedShow = [];
		UcheckedShowCN = [];
		$("#searchConfig").find("input").each(function(){
			$this = $(this);
			if($this.attr("checked")){
				UcheckedSearch.push($this.attr("name").substr(7));
				var text = $this.parent().parent().parent().children(0).text();
				text = text.substring(0,text.length-1);
				UcheckedSearchCN.push(text);
			}
		});
		$("#showConfig").find("input").each(function(){
			$this = $(this);
			if($this.attr("checked")){
				UcheckedShow.push($this.attr("name").substr(5));
				var text = $this.parent().parent().parent().children(0).text();
				text = text.substring(0,text.length-1);
				UcheckedShowCN.push(text);
			}
		});
		var saveUrl = "${ctx}/frs/integratedquery/detailsel/saveDefault" 
		$.ajax({
			cache : false,
			async : false,
			url : saveUrl,
			dataType : 'json',
			type : "post",
			data : {
				"UcheckedSearch" : JSON2.stringify(UcheckedSearch),
				"UcheckedSearchCN" : JSON2.stringify(encodeURI(encodeURI(UcheckedSearchCN))),
				"UcheckedShow" : JSON2.stringify(UcheckedShow),
				"UcheckedShowCN" : JSON2.stringify(encodeURI(encodeURI(UcheckedShowCN))),
				"rptId" : rptId
			},
			success : function(result){
				BIONE.tip("设置成功！")
			},
			error : function(){
				BIONE.tip("设置失败，请联系管理员！")
			}
		});
		autoReload = false;
		closeConfig();
		autoReload = true;
		configSearchForm();
		configGrid();
	}
	
	function initGrid() {
		var uri = '${ctx}/frs/integratedquery/detailsel/templelistTree'
		grid = manager = $("#maingrid").ligerGrid(
				{
					columns : [],
					url : uri,
					checkbox : false,
					width : "100%",
					rownumbers : true,
					dataAction : 'server', //从后台获取数据
					usePager : true, //服务器分页
					alternatingRow : true, //附加奇偶行效果行
					pageSize : 20,
					colDraggable : true,
					sortName : 'rptNm',//第一次默认排序的字段
					sortOrder : 'asc', //排序的方式
					method : 'post',
					pageParmName : 'page',
					pagesizeParmName : 'pagesize',
					toolbar : {}
				});
	}
	
	function configGrid(){
		var columns = liger.get("maingrid").get("columns");
		columns = [];
		var wid;
		if(UcheckedShow.length<7){
			wid = 98/UcheckedShow.length + "%";
		}else{
			wid = "15%";
		}
		for(i=0;i<UcheckedShow.length;i++){
			columns.push({
				display : UcheckedShowCN[i],
				name : UcheckedShow[i],
				width : wid,
				align: "center"
			});
		}
		$("#maingrid").ligerGrid({columns: columns});
		grid.loadData();
	}
	
	function initTree() {
		var url = "${ctx}/frs/integratedquery/detailsel/templelistTree"
		taskTree = $.fn.zTree.init($("#tree"), {
			async : {
				enable : true,
				url : url,
				autoParam : [ "id","upId"],
				dataType : "json",
				type : "post",
				dataFilter : function(treeId, parentNode, childNodes) {
					if (childNodes) {
						for ( var i = 0; i < childNodes.length; i++) {
							if(childNodes[0].upId != "ROOT"){
								childNodes[i].icon = rptTreeNodeIcon;
							}
						}
						return childNodes;
					}
				}
			},
			data : {
				keep : {
					parent : true
				},
				key : {
					name : "text"
				},
				simpleData : {
					id : "id"
				}
			},
			view : {
				showLine : false
			},
			callback : {
				onClick : function(event, treeId, treeNode) {
					orgType = treeNode.params.orgType;
					rptId = treeNode.id;
					initConfigForm();
					initDefault(rptId);
					configSearchForm();
					configGrid();
					searchA = "Y";
					showA = "Y";
					grid.set('newPage', 1);
				},
				onNodeCreated : function(event, treeId, treeNode) {
					if (treeNode.upId == "ROOT") {
						//若是根节点，展开下一级节点
						taskTree.reAsyncChildNodes(treeNode, "refresh");
					}
				}
			}
		});
	}
	function test(id, text,Dname){
		$.ligerui.get(Dname).setValue(id);
	 	$.ligerui.get(Dname).setData(id);
	 	$.ligerui.get(Dname).setText(text);
	}
	function initDefault(rptId){
		var url = "${ctx}/frs/integratedquery/detailsel/getDefaultInfo"
		var data = {
			rptId : rptId	
		}
		$.ajax({
			cache : false,
			async : false,
			url : url,
			data : data,
			dataType : 'json',
			type : "post",
			success : function(result){
				UcheckedSearch = [];
				UcheckedSearchCN = [];
				UcheckedShow = [];
				UcheckedShowCN = [];
 				if(result.length>0){
 					document.getElementById("config").style.display='block';
					for(i=0;i<result.length;i++){
						if(result[i].editType==1){
							UcheckedSearch.push(result[i].colNameEn);
							UcheckedSearchCN.push(result[i].colNameCn);
							$("#"+"search_"+result[i].colNameEn).ligerCheckBox({ disabled: false }).setValue(true);
						}else if(result[i].editType==2){
							UcheckedShow.push(result[i].colNameEn);
							UcheckedShowCN.push(result[i].colNameCn);
							$("#"+"show_"+result[i].colNameEn).ligerCheckBox({ disabled: false }).setValue(true);
						}
					}
				}else{
					document.getElementById("config").style.display='none';
				}
			}
		})
	}
	
</script>
</head>
<body>
	<div id="template.left.up.icon">
		<i class="icon-guide search-size"></i>
	</div>
	<div id="template.left.up">
		<span id="treeTitle" style="font-size: 12">报表树</span>
	</div>
</body>
</html>