<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<%@ include file="/common/taglibs.jsp"%>
<html>
<head>
<link rel="stylesheet" href="${ctx}/js/bootstrap3/css/bootstrap.css" />
<meta name="decorator" content="/template/template14.jsp">
<link rel="stylesheet" href="${ctx}/js/exlabel/exlabel.css">
<script type="text/javascript" src="${ctx}/js/exlabel/exlabel.js"></script>
<style>
#left {
	float: left;
	width: 200px ;
	border-width: 1px;
	border-style: solid;
	border-color: #D6D6D6;
}

#right {
	width: 100%;
	float: right;
	border-width: 1px;
	border-style: solid;
	border-color: #D6D6D6;
	overflow: auto;
}

div{
	font-size :12px;
}
#tree {
	background-color: #F1F1F1;
}
.searchtitle .togglebtn{
	top:90px;
}
.searchbox{
	width:99%;
	margin:0;
}
#fengediv{
	height:20px;
}
</style>
<script type="text/javascript">
	var treeObj = null; // 树对象
	var panel = null; // 标签对象
	var draggingNode = null//拖拽节点
	var labelIds,labelNames,labelPids;
	var grid = null// 表格对象
	// 拖拽标签头图标
	var notAllowedIcon = "/images/classics/icons/cancel.gif";
	var allowedIcon = "/images/classics/icons/accept.png";
	function templateshow() {
		var $content = $(document);
		var height = $content.height() - 10;
		$("#left").height(height);
		$("#right").height(height);
		$("#panel").css("height","60px");
		var leftHeight = $("#left").height();
		var $treeContainer = $("#treeContainer");
		$treeContainer.height(leftHeight - 58);
		rightWidth=$content.width()-$("#left").width()-5;  
		$("#right").width(rightWidth);
	}
	
	$(function() {
		initPanel();
		initData();
		initInfo("rpt");
		BIONE.addSearchButtons("#search", grid, "#searchbtn");
		initSearchPanel("rpt");
		$(".l-btn-hasicon").width("50px");
		$(".togglebtn").click(function(){
			if($(".togglebtn").attr("class") == 'togglebtn'){
				grid.setHeight($("#right").height()-120);
			}else{
				grid.setHeight($("#right").height()-$("#searchbox").height()-120);
			}
			$("#searchbox").toggle(function(){
 				if($(".togglebtn").attr("class") == 'togglebtn'){
 					$(".togglebtn").attr("class","togglebtn togglebtn-down");
				}else{
					$(".togglebtn").attr("class","togglebtn");
				}
			});
		})
	});
	
	function changetype(){
		grid.setParm("labelIds","");
		if($("#rptCheck")[0].checked == true){
			initInfo("rpt");
		}
		else{
			initInfo("idx");
		}
	}
	
	function initInfo(type){
		panel.removeAll();
		initTree(type);
		initGrid(type);
		initSearchPanel(type);
	}
	//初始化下拉搜索栏
	function initSearchPanel(type){
		$("#searchbox").hide();
		$(".togglebtn").attr("class","togglebtn togglebtn-down");
		if(type=="rpt"){
			$("#search").ligerForm({
				fields : [ {
					display : "报表编号 ",
					name : "rptNum",
					newline : true,
					type : "text",
					cssClass : "field",
					attr : {
						field : "rpt.rptNum",
						op : "="
					}
				},{
					display : "报表名称 ",
					name : "rptNm",
					newline : false,
					type : "text",
					cssClass : "field",
					attr : {
						field : "rpt.rptNm",
						op : "like"
					}
				},{
					display : "报表频率 ",
					name : "rptCycle",
					newline : false,
					type: 'select',
					options:{
						data: [{ 
							id: '01',
							text: '日' 
						},{
							id: '02',
							text: '月' 
						},{
							id: '03',
							text: '季' 
						},{
							id: '04',
							text: '年' 
						},{
							id: '10',
							text: '周' 
						},{
							id: '11',
							text: '旬' 
						},{
							id: '12',
							text: '半年' 
						}]
					},
					attr : {
						field : "rpt.rptCycle",
						op : "="
					}
				}]
			});
		}else{
			$("#search").ligerForm({
				fields : [ {
					display : "指标编号 ",
					name : "indexNo",
					newline : true,
					type : "text",
					cssClass : "field",
					attr : {
						field : "t1_.indexNo",
						op : "="
					}
				},{
					display : "指标名称 ",
					name : "indexNm",
					newline : false,
					type : "text",
					cssClass : "field",
					attr : {
						field : "t1_.indexNm",
						op : "like"
					}
				},{
					display : "指标类型 ",
					name : "indexType",
					newline : false,
					type: 'select',
					options:{
						data: [{ 
							id: '01',
							text: '根指标' 
						},{
							id: '02',
							text: '组合指标' 
						},{
							id: '03',
							text: '派生指标' 
						},{
							id: '04',
							text: '泛化指标' 
						},{
							id: '05',
							text: '总账指标' 
						},{
							id: '06',
							text: '补录指标' 
						}]
					},
					attr : {
						field : "t1_.indexType",
						op : "="
					}
				}]
			});
		}
	}
	//初始化标签函数
	function initPanel(){
		panel = $('#panel').exlabel({
			text: 'text',
			value: 'id',
			isEdit : false,
			isInput : false,
			callback: {
		        remove: function(item){
		        	searchGrid();
		        }
		     }
		});
	}
	
	//初始化树函数
	function initTree(labelObjNo) {
		var setting = {
	  		async : {
	  			enable : true,
	  			type : "post",
	  			dataType:"json",
	  			url : "${ctx}/bione/label/labelConfig/treeNode.json?labelObjNo="+labelObjNo+"&d="+new Date().getTime(),
	  			autoParam : ["realId", "type"],
				dataFilter : function(treeId, parentNode, childNodes) {
				    if(childNodes) {
				    	$.each(childNodes, function(i, n) {
				    		childNodes[i].type = n.params.type;
				    		childNodes[i].realId = n.params.realId;
				    	});
				    }
				    return childNodes;
				}
			},
			data:{
				keep:{
					parent : true
				},
				key : {
					name : "text"
				}
			},
			callback: {
				onNodeCreated : function(event , treeId , treeNode){
					setDragDrop("#"+treeNode.tId+"_a" , "#panel");
				}
			}
		};
		treeObj = $.fn.zTree.init($("#tree"), setting);
	}
	
	function initData(){
		$.ajax({
			async : true,
			cache : false,
			url : '${ctx}/bione/label/labelConfig/getLabelMap',
			dataType : 'json',
			type : 'post',
			data :{
				id : "${id}",
				labelObjNo : "${labelObjNo}"
			},
			success : function(result){
				labelIds = result.id;
				labelNames = result.name;
				labelPids = result.pid;
			}
			
		})
	}
	
	//添加拖拽控制
 	function setDragDrop(dom , receiveDom){
 		if(typeof dom == "undefined"
 			|| dom == null){
 			return ;
 		}
 		$(dom).ligerDrag({
 			proxy : function(target , g , e){
 				var defaultName = "标签";
 				var proxyLabel = "${ctx}"+notAllowedIcon;
 				var targetTitle = $(dom).attr("title") == null ? defaultName : defaultName+":"+$(dom).attr("title");
 				var proxyHtml = $('<div style="width：150;position: absolute;padding-left: 10px;color: #183152;font-weight: bold;height: 20px;line-height: 19px;overflow: hidden;background: #E5EFFE ;border: 1px solid #DDDDDD;border-top:none; z-index:9999;"><span class="dragimage_span" style="position:absolute;width:16px;height:16px;left:5px;top:2px;background:url('+proxyLabel+')" ></span><span style="padding-left: 14px;">'+targetTitle+'</span></div>');
                var div = $(proxyHtml);
                div.appendTo('#center');
                return div;
 			},
 			revert : false , 
 			receive : receiveDom, 
 			onStartDrag : function(current , e){
				// 获取拖拽树节点信息
 				var treeAId = current.target.attr("id");
 				var treeId = treeAId;
				if(treeAId){
					var strsTmp = treeAId.split("_");
					var treeId = treeAId;
					if(strsTmp.length > 1){
						var newStrsTmp = [];
						for(var i = 0 ; i < strsTmp.length - 1 ; i++){
							newStrsTmp.push(strsTmp[i]);
						}
						treeId = newStrsTmp.join("_");
					}
					draggingNode = treeObj.getNodeByTId(treeId);
				}
 			},
 			onDragEnter : function(receive , source , e){
 				var allowLabel = "${ctx}"+allowedIcon;
 				source.children(".dragimage_span").css("background" , "url('"+allowLabel+"')");
 			},
 			onDragLeave : function(receive , source , e){
 				var notAllowLabel = "${ctx}"+notAllowedIcon;
 				source.children(".dragimage_span").css("background" , "url('"+notAllowLabel+"')");
 			},
 			onDrop : function(obj , target , e){
 				var data=panel.getData();
 				var delData = [];
 				for(var i in data){
 					var pid = labelIds[draggingNode.realId].split(",")
 					if($.inArray(data[i].id, pid)>=0){
 						delData.push(data[i]);
 					}
 					var ids =labelIds[data[i].id];
 					if(ids.indexOf(draggingNode.realId)>=0){
 						delData.push(data[i]);
 					}
 				}
 				for(var i in delData){
 					panel.remove(delData[i]);
 				}
 				panel.add({
 	 				id : draggingNode.realId,
 	 				text : labelNames[draggingNode.realId]
 	 			});
 				searchGrid();
 			}
 		});
	}
	
	function searchGrid(){
		var vals = panel.val();
		var AllIds = [];
		for(var i in vals){
			var lIds = [];
			lIds.push(vals[i]);
			if(labelPids[vals[i]] != null && labelPids[vals[i]].split(",").length>0){
				lIds = $.merge(lIds,labelPids[vals[i]].split(","));
			}
			AllIds.push(lIds.join(","))
		}
		grid.setParm("labelIds",AllIds.join(";"));
		grid.setParm("newPage",1);
		grid.options.newPage=1
		grid.loadData();
	}
	
	//获取报表表格配置
	function getRptConfig(){
		return {
			columns: [{
				display : '报表编号',
				name : 'rptNum',
				width : "30%",
				align : 'left',
				render : function(row) {
					return "<a href='javascript:void(0)' class='link' style='color:blue' onclick='openRpt(\""+ row.rptId+ "\",\""+row.rptNm+"\",\""+row.rptType+"\")'>"+ row.rptNum + "</a>";
				}
			},{
				display : '报表名称',
				name : 'rptNm',
				width : "30%",
				align : 'left',
				render : function(row) {
					return "<a href='javascript:void(0)' class='link' style='color:blue' onclick='openRpt(\""+ row.rptId+ "\",\""+row.rptNm+"\",\""+row.rptType+"\")'>"+ row.rptNm + "</a>";
				}
			},{
				display : '报表频率',
				name : 'rptCycle',
				width : "20%",
				align : 'center',
				render:function(a,b,c){
					switch(c){
					case '01':
						return '日';
					case '02':
						return '月';
					case '03':
						return '季';
					case '04':
						return '年';
					case '10':
						return '周';
					case '11':
						return '旬';
					case '12':
						return '半年';
					}
				}
			}/* ,{
				display : '报表类别',
				name : 'rptType',
				width : "20%",
				align : 'center',
				render:function(a,b,c){
					switch(c){
					case '01':
						return '外部报表';
					case '02':
						return '平台报表';
					}
				}
			} */,{
				display : '报表状态',
				name : 'rptSts',
				width : "15%",
				align : 'center',
				render: function(a,b,c){
					if(c=="Y")
						return "启用";
					else
						return "停用";
				}
			}],
			url: "${ctx}/rpt/rpt/rptmgr/info/rptLabelList.json",
			sortName: "rankOrder"
		};
	}
	
	//获取指标表格配置
	function getIdxConfig(){
		return {
			columns: [{
				display : '指标编号',
				name : 'id.indexNo',
				width : "15%",
				align : 'left',
				render:function(row){
					return  "<a href='javascript:void(0)' style='color:blue' onclick='showBusiInfoSingle(\""+row.id.indexNo+"\","+row.id.indexVerId+",\""+row.infoRights+"\")'>"+row.id.indexNo+"</a>";
				}
			},{
				display : '指标名称',
				name : 'indexNm',
				width : "15%",
				align : 'left',
				render:function(row){
					return  "<a href='javascript:void(0)' style='color:blue' onclick='showBusiInfoSingle(\""+row.id.indexNo+"\","+row.id.indexVerId+",\""+row.infoRights+"\")'>"+row.indexNm+"</a>";
				}
				
			},{
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
					case "06":
						return "补录指标";
						break;
					}
				}
			},{
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
			/* },{
				display : '历史版本',
				name : 'indexSts',
				width : "17%",
				isSort : false,
				editor : {
					type : 'text'
				},
				render : function(row) {
						return "<a href='javascript:void(0)' style='color:blue' onclick='showVersionInfoInRow(\""+row.id.indexNo+"\",\""+row.id.indexVerId+"\")'>历史版本</a>";
				} */
			}],
			url: "${ctx}/report/frame/idx/getIdxInfoLabelList.json?auth=Y&d="+new Date().getTime(),
			sortName: "indexNm"
		};
	}
	//初始化报表表格
 	function initGrid(type) {
		var config = {};
		if(type=="rpt"){
			config = getRptConfig();
		}
		if(type=="idx"){
			config = getIdxConfig();
		}
		grid = $("#grid").ligerGrid({
			checkbox :true,
			columns : config.columns,
			checkbox: false,
			dataAction : 'server', //从后台获取数据
			usePager : true, //服务器分页
			alternatingRow : true, //附加奇偶行效果行
			colDraggable : true,
			sortName: config.sortName,
			url : config.url,
			rownumbers : true,
			width : '97%'
		});
		grid.setHeight($("#right").height()-120);
	};
	
	
	function openRpt(rptId,rptNm,rptType){
		if(rptType=="01")
			url="${ctx}/rpt/rpt/rptmgr/info/reportInfo";
		else
			url="${ctx}/rpt/rpt/rptmgr/view/reportView";
		url+="?rptId=" + rptId + "&show=2";
		dialog = BIONE.commonOpenFullDialog("报表【"+rptNm+"】", "rptViewWin",
				url);
	}
	
	 function  showVersionInfoInRow(indexNo,indexVerId){
		    dialog = window.parent.BIONE.commonOpenLargeDialog("版本信息",
					"idxInfoVerPreBox",
					"${ctx}/report/frame/idx/idxInfoVerPre?indexNo="+indexNo+"&indexVerId="+indexVerId+"&defSrc="+defSrc , null);
	 }
	 
	 function  showBusiInfoSingle(indexNo,indexVerId){
		 
		 dialog = window.parent.BIONE.commonOpenDialog("指标查看",
					"rptIdxInfoPreviewBox",$(parent.document).width(), $(parent.document).height(),
					"${ctx}/report/frame/idx/"+indexNo+"/show?d="+new Date().getTime(), null);
	 }
	
	
	
</script>
	
</head>
<body>
	<div id="template.center">
		<div id="left"  style="background-color: #FFFFFF">
			<div id="lefttable" width="100%" border="0">
				<div width="100%"
					style="height:30px;background-image: url(${ctx}/css/classics/ligerUI/Gray/images/ui/gridbar.jpg);border-bottom:1px solid #D6D6D6;">
					<div width="8%"
						style="padding-left: 10px; float: left; position: relative; height: 20p; margin-top: 8px">
						<img src="${ctx}/images/classics/icons/application_side_tree.png" />
					</div>
					<div width="90%">
						<span
							style="font-size: 12px; float: left; position: relative; line-height: 30px; padding-left: 2px">
							标签选择
						</span>
					</div>
				</div>
			</div>
			<div id="treeContainer"
				style="width: 100%; overflow: auto; clear: both; background-color: #FFFFFF;">
				<div style="background-image: url(/biapp-frs-web/css/classics/ligerUI/Gray/images/ui/gridbar.jpg);border:1px solid #D6D6D6;padding-left:10px">
					<span>报表</span>
					<input type="radio" id="rptCheck"  name="showtype" value="rptCheck" style="width:20px;" onclick="changetype()" checked="true"/> 
					<span>指标</span> 
					<input type="radio" id="idxCheck" name="showtype" value=""idxCheck"" style="width:20px;" onclick="changetype()"  /></div>
				<ul id="tree"
					style="font-size: 12; background-color: #FFFFFF; width: 92%"
					class="ztree"></ul>
			</div>
		</div>
		<div id="right">
			<div style="margin: 5px;">
				<div style="width:10%; padding-left:10px;font-size: 12px;font-weight: 700;color: #959595;text-transform: uppercase;letter-spacing: 1px;">
					检索标签：
				</div>
				<div id="panel" style="width:99%;">
				</div>
				<div class="searchtitle">
					<div id="fengediv"></div>
					<div class="togglebtn togglebtn-down">&nbsp;</div>
				</div>
				<div id="searchbox" class="searchbox">
					<form id="formsearch">
						<div id="search"></div>
						<div class="l-clear"></div>
					</form>
				<div id="searchbtn" class="searchbtn"></div>
			</div>
			</div>
			<div style="margin: 5px;">
				<div id="grid"></div>
			</div>
		</div>
	</div>
</body>
</html>