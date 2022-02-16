<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ include file="/common/taglibs.jsp"%>
<html>
<meta name="decorator" content="/template/template21_BS.jsp">
<head>
<style type="text/css">
.searchtitle img {
	display: block;
	float: left;
}
.searchtitle span {
	margin-top: 1px;
	margin-left: 2px;
	line-height: 19px;
}
.l-tab-links {
/* 	position: absolute; */
}
</style>
<link rel="stylesheet" type="text/css" href="${ctx}/js/myPagination/js/msgbox/msgbox.css" />
<link rel="stylesheet" type="text/css" href="${ctx}/js/myPagination/js/myPagination/page.css" />
<script src="${ctx}/js/myPagination/js/myPagination/jquery.myPagination6.0.js" type="text/javascript"></script>
<script language="javascript" type="text/javascript" src="${ctx}/js/myPagination/js/msgbox/msgbox.js"></script>
<script type="text/javascript">
	var app = {
		ctx : '${ctx}',
		queryObj : '${queryObj}' ? $.parseJSON('${queryObj}') : "",
		nodeType : '2'		// nodeType: 1.目录; 2.报表; 3.条线
	};
</script>
<script type="text/javascript" src="${ctx}/js/uuid/uuid.js" ></script>
<script type="text/javascript" src="${ctx}/frs/js/rptquery/dateCal.js" ></script>
<script type="text/javascript" src="${ctx}/frs/js/rptquery/rptquery.js" ></script>
<%@ include file="/common/spreadjs_load.jsp"%>
<script type="text/javascript">
	var doFlag="";
	var data = '${data}';
	var pageSize=20;//明细类报表分页展示条数
	var Pagination=null;
	if(data){
		var datajson = eval("("+'${data}'+")");
	}
	$.extend(app, {
		height : null,
		iconFolder : app.ctx + '/images/classics/icons/book.png',
		iconRpt : app.ctx + '/images/classics/icons/application_view_columns.png',
		iconLine : app.ctx + '/images/classics/icons/group.png',
		// 添加搜索按钮
		rptQuery_addSearchButtons : function(form, btnContainer) {
			if (!form) return;
			form = $(form);
			if (btnContainer) {
				BIONE.createButton({
					appendTo : btnContainer,
					text : '查询',
					icon : 'search3',
					width : '50px',
					click : function() {
						var checkType = liger.get('checkTypeCombox').getValue();
						if (BIONE.validator.element('#orgTypeBox') & BIONE.validator.element('#orgNm_sel') & ((checkType == '01' & BIONE.validator.element('#dataDate'))
								|| (checkType == '02' & BIONE.validator.element('#startDate') & BIONE.validator.element('#endDate')))) {
							var showType = liger.get('showTypeBox').getValue();
							var nodes = new Array();
							if(datajson){
								datajson.text = datajson.rptNm;
								datajson.id = datajson.rptId;
								datajson.nodeType = '2';
								nodes.push(datajson); 
							}else{
								nodes = app.zTreeObj.getCheckedNodes(true);
							}
							if (showType == '01') {
								var argsArr = app.rptQuery_readExportParms(nodes);
								if (argsArr.length < 31) {
									// 封装查询参数
									var parms = app.rptQuery_readQueryParms(nodes);
									// 执行查询
									app.rptQuery_execQuery(parms.argsArr, parms.rptInfo, parms.lineInfo);
								}else{
									BIONE.tip('最终文档将超过 30个标签页(超过了Excel的限制), 请适当减少条件');
								}
							} else if (showType == '02') {
								var argsArr = app.rptQuery_readExportParms(nodes);
								if (argsArr.length < 61) {
									BIONE.ajax({
										type : 'post',
										dataType : 'text',
										url : app.ctx + '/frs/integratedquery/rptquery/createExcel',
										data : { json : JSON2.stringify(argsArr),showType:"02" }
									}, function(filePath) {
										if (!filePath) {
											BIONE.tip('无数据可供下载');
										} else {
											$('#download').attr('src', app.ctx + '/frs/integratedquery/rptquery/downloadFile?filePath=' + filePath);
										}
									});
								} else {
									BIONE.tip('最终文档将超过 60个标签页(超过了Excel的限制), 请适当减少条件');
								}
							} else if (showType == '03') {
								var argsArr = app.rptQuery_readExportParms(nodes);
								if (argsArr.length < 61) {
									BIONE.ajax({
										type : 'post',
										dataType : 'text',
										url : app.ctx + '/frs/integratedquery/rptquery/createZip',
										data : { json : JSON2.stringify(argsArr) }
									}, function(filePath) {
										if (!filePath) {
											BIONE.tip('无数据可供下载');
										} else {
											$('#download').attr('src', app.ctx + '/frs/integratedquery/rptquery/downloadFile?filePath=' + filePath);
										}
									});
								} else {
									BIONE.tip('最终将生成超过 60个文档, 请适当减少条件');
								}
							}
						}
					}
				});
				BIONE.createButton({
					appendTo : btnContainer,
					text : '重置',
					icon : 'refresh2',
					width : '50px',
					click : function() {
						$(":input", form).not(":submit,:reset,:hidden,:image,:button,[disabled]")
							.each(function() {
								$(this).val("");
							});
						$(":input[ltype=combobox]", form).each(function() {
							var ligerid = $(this).attr('data-ligerid'), ligerItem = $.ligerui.get(ligerid);// 需要配置comboboxName属性
							if (ligerid && ligerItem && ligerItem.clear) {
								// ligerUI 1.2 以上才支持clear方法
								ligerItem.clear();
							} else {
								$(this).val("");
							}
						});
						$(":input[ltype=select]", form).each(function() {
							var ligerid = $(this).attr('data-ligerid'), ligerItem = $.ligerui.get(ligerid);// 需要配置comboboxName属性
							if (ligerid && ligerItem && ligerItem.clear) {
								// ligerUI 1.2 以上才支持clear方法
								ligerItem.clear();
							} else {
								$(this).val("");
							}
						});
						$.liger.get('dataDate').setValue("");
						// $(":input[ltype=date]", form).each(function() {
						// 	var ligerid = $(this).attr('data-ligerid'), ligerItem = $.ligerui.get(ligerid);// 需要配置comboboxName属性
						// 	if (ligerid && ligerItem && ligerItem.clear) {
						// 		// ligerUI 1.2 以上才支持clear方法
						// 		ligerItem.clear();
						// 	} else {
						// 		$(this).val("");
						// 	}
						// });
					}
				});
			}
		}
	});	
	$(function() {
		//明细类报表分页 20200212
		$('#content').height($('#content').height() - 40);
		app.height = $('#navtab1').height() - 40;
        $('#navtab1').height(app.height);
		$(document).on("focusout","#pagination input",jumpPage);
		$(document).on("change","#pageSize",function(val,text){
			changeSize($(this).val());
		});
		
		
		if(datajson){
			$("#left").parent().width("0px");
			$("#left").parent().html("");
			$("#right").parent().css('left','0px');
			$("#right").parent().width("100%");
		}
		var len = '${defSrc}' ? JSON2.parse('${defSrc}') : "";
		//var userInfo = JSON2.parse('${userInfo}');
			if(len.length>1){
				$("#treeContainer").html('');
				var  htmlArr =  [];
				htmlArr.push('<div title="通用报表" tag="通用报表" class="l-scroll"><ul id="tree"  style="font-size: 12; background-color: #FFFFFF; width: 92%" class="ztree"></div>');
	/* 			if($.contains(len, 2)){
					htmlArr.push('<div title="自定义报表" tag="自定义报表" class="l-scroll"><ul id="tree1"  style="font-size: 12; background-color: #FFFFFF; width: 92%" class="ztree"></div>');
				} */
				/* if($.contains(len, 3)){
					htmlArr.push('<div title="用户自定义报表" tag="用户自定义报表" class="l-scroll"><ul id="tree2"  style="font-size: 12; background-color: #FFFFFF; width: 92%" class="ztree"></div>');
				} */
				
				$("#treeContainer").html(htmlArr.join(" "));
				var height_ = $(".l-layout-center").height();
		        $("#treeContainer").ligerAccordion({ height: height_ - 60, speed: "slow" });
		        $(".l-accordion-header-inner").css("color","#183152").css("font-size","11");
		        if($.contains(len, 2)){
		        	//app.iniUserRptTree(userInfo);20160808cl
		        	app.iniOrgRptTree();//机构自定义报表树
		        }
		       /*  if($.contains(len, 3)){
					app.iniUserRptTree(userInfo);//用户自定义报表树
		        } */
			}
		
        //=======gf=========//
		if($(".l-accordion-header")){
			$(".l-accordion-header").click(function(){
	            $(this).find(".l-accordion-header-inner").css("color","blue").parent().siblings(".l-accordion-header").find(".l-accordion-header-inner").css("color","#183152");
		    });
		}
        
		$('div.searchtitle span').text('报表查询');
		if(datajson){
			app.iniRptTree(datajson.busiType);//全行通用报表树
		}else{
			app.iniRptTree()
		}
		app.rptQuery_addSearchForm();
		//当前日期插件点击事件仅限于日历本身
	 	$('#dataDate').click(function() {
			BIONE.commonOpenDialog("日期选择", "dateDialog", "400", "300", app.ctx + '/frs/integratedquery/rptquery/dataDate', null);
		}); 
		//光大未开发完下钻，暂时隐藏
		 var settings = {
//			onCellDoubleClick : spreadDbclkCell
		}; 
		app.rptQuery_initTab(settings);
		
		if (app.queryObj && typeof app.queryObj === 'object') {
			// 查询表单反显
			var data = {};
			$(app.queryObj.querysItem).each(function(i, n) {
				data[n.paramType] = n.paramValExpression;
			});
			data.orgNm_sel = app.queryObj.orgNm_sel || '';
			app.injectQueryData(data);
			$('#orgNm_sel').val(data.orgNm_sel);
			data.dataDate && liger.get('dataDate').setValue(data.dataDate);
			data.startDate && $('#startDate').val(data.startDate);
			data.endDate && $('#endDate').val(data.endDate);
			$('#dataDateHide').val(data.dataDate);
			$(':input').blur();			
			// 封装查询参数
			var parms = app.rptQuery_readQueryParms(app.queryObj.rptInfo);
			// 执行查询
			app.rptQuery_execQuery(parms.argsArr, parms.rptInfo, parms.lineInfo);
		} else {
			var nodes = new Array();
			if(datajson){
				datajson.text = datajson.rptNm;
				datajson.id = datajson.rptId;
				datajson.nodeType = '2';
				nodes.push(datajson); 
			}else{
				nodes = app.zTreeObj.getCheckedNodes(true);
				app.addFavQueryButton();
			}
			app.addFullScreenButton(nodes);
		}
		// tmp();
		app.addRptQueryEvent(len);
		$('input:radio').live('click', function(ev) {
			var id = $(this).attr('id');
			switch (id) {
				case 'level1' : app.zTreeObj.setting.check.chkboxType = { 'Y': 's', 'N': 'ps'};	break;
				case 'level2' : app.zTreeObj.setting.check.chkboxType = { 'Y': '', 'N': 'ps'};	break;
			}
		});
		$('body').append('<iframe id="download" style="visibility: hidden;" src=""></iframe>');
		//添加数据导入按钮,管理员有此按钮权限
		<%--if('${isSuperUser}' == "true"){--%>
		<%--	app.addUploadButton();--%>
		<%--}--%>
	});
	
	function spreadDbclkCell(sender , args,rptIdxInfo){
		if(args){
			if(args.sheetArea
					&& args.sheetArea != 3){
				// 只有选中单元格区域才进行后续动作
				return ;
			}
			var currSheet = spread.getActiveSheet();
			var selRow = currSheet.getActiveRowIndex();
			var selCol = currSheet.getActiveColumnIndex();
			var currCell = spread.getActiveSheet().getCell(selRow , selCol);
			var seq = currCell.tag();
			var isFormal = currCell.formula() == null ? false : true;
			if(isFormal === true){
				return ;
			}
			if(!rptIdxInfo||rptIdxInfo==null){
				// 指标单元格,来源指标为空(空指标)
				return ;
			}
			var searchArgs = JSON2.parse(this.searchArgs);
			var orgNo = "";
			for(var i =0;i<searchArgs.length;i++){
				if(searchArgs[i].dimNo=="ORG")
					orgNo = searchArgs[i].value;
			}
			var dataDate = $('#dataDate').val();
			BIONE.commonOpenDialog('',
					'chooseTypeDialog',
					500,
					200,
					'${ctx}/frs/integratedquery/rptquery/dbClick?&indexNo='+rptIdxInfo.indexNo+'&orgNo='+orgNo+'&dataDate='+dataDate+'&d='+new Date().getTime());

		}
	}
	
	function tmp() {
		var title = '# 空 tab';
		app.isInit = true;
		var height = $(".l-layout-center").height() - $("#mainsearch").height() + 8;
		app.tab = $("#navtab1").ligerTab({
		    showSwitch : true,
		    onAfterSelectTabItem : function (targettabid) {
		    	if (!app.isInit) {
			    	var $tabIframe = $('#' + targettabid + '');
			    	if (!$tabIframe.attr('src')) {
			    		$tabIframe.attr('src', $tabIframe.attr('alt'));
			    	}
		    	}
		    }
		});
		for (var i = 0, len = 3; i < len; i++) {
			app.tab.addTabItem({
				tabid : 'tab_' + i,
				text : 'tab 名称',
				showClose : true,
				height : height,
				content : '<div tabid="tab_' + i + '" title="' + title + '" style="height: ' + height + 'px;">'
			});
		}
		app.isInit = false;
		app.tab.selectTabItem('tab_0');
	}
	
	//分页-输入页码跳转 20200212
	function jumpPage() {
		var size = pageSize;
		var page = $("#pagination").find("input").val();
		p = parseInt(page)
		if (isNaN(p) || p != page) {
			p = oldVal;
			$("#pagination").find("input").val(oldVal);
		}
		if (total < p) {
			p = total;
			Pagination.jumpPage(p);
		}
		if (p != oldVal) {
			var sumPage = parseInt(window.total / size)
			if (window.total % size != 0) {
				sumPage++;
			}
			var isInit = false;
			if (page == sumPage) {
				isInit = true;
				window.islast = true;
			} else {
				if (window.islast) {
					isInit = true;
					window.islast = false;
				}
			}
			oldVal = p;
			Pagination.jumpPage(p);
			View.ajaxJson(null, null, isInit, "/report/frame/tmp/view/getViewInfo?d="+new Date().getTime(), (p - 1) * size + 1, size, false);
		}
	}
		
	//分页-初始化分页栏 20200212
	function initPagination(total){
		window.total = total;
		this.total=total;
		var size=pageSize;
		if(total != null && total != 0){
			if(size == "全部"){
				total=1;
			}else{
				total=parseInt((total-1)/size)+1;
			}
		}else{
			total=1;
		}
		Pagination=$("#pagination").myPagination({
			pageCount: total,
			pageNumber: 10,
			cssStyle: 'liger',
	        panel: {
	            tipInfo_on: true,
	            tipInfo: '<span class="tip">{input}/{sumPage}页,每页显示<select id="pageSize"><option value="20">20</option><option value="50">50</option><option value="100">100</option><option value="全部">全部</option></select>条</span>',
	            tipInfo_css: {
	              width: '25px',
	              height: "20px",
	              border: "1px solid #777",
	              padding: "0 0 0 5px",
	              margin: "0 5px 0 5px",
	              color: "#333"
	            }
	        },
	        ajax: {
	            on: false,
	            onClick: function(page) {
	            	var sumPage =parseInt(window.total/size)
	            	if(window.total%size !=0){
	            		sumPage ++;
	            	}
	            	var isInit = false;
	            	if(page == sumPage){
	            		isInit = true;
	            		window.islast = true;
	            	}
	            	else{
	            		if(window.islast){
	            			isInit = true;
		            		window.islast = false;
	            		}
	            	}
	            	oldVal = page;
	        		View.ajaxJson(null,null,isInit,"/report/frame/tmp/view/getViewInfo?d="+new Date().getTime(),(page-1)*size+1,size,false);
	        		setTimeout(function(){
	        			if($("#pageSize").val()!= size){
	        				$("#pageSize").val(size);
	        				setTimeout(function(){
	    	        			if($("#pageSize").val()!= size){
	    	        				$("#pageSize").val(size);
	    	        			}
	    	        		})
	        			}
	        		});
	            }
	        }
	    });
		$("#pagination").css("overflow","hidden").css("margin-top","0px").css("padding-top","0px");
		$("#pageSize").val(size);
	}
	
	//分页-切换显示条数 20200212
	function changeSize(size){
		pageSize=size;
		View.ajaxJson(null,null,true,"/report/frame/tmp/view/getViewInfo?d="+new Date().getTime(),1,size);
		
	}
</script>
</head>
<body>
	<div id="template.left.up.icon">
		<i class = "icon-guide search-size"></i>
	</div>
	<div id="template.left.up">
		<span id="treeTitle" style="font-size: 12">报表树</span>
	</div>
	<div id="template.left.up.right">
	</div>
	<div id="template.left.down">
		<div id="level" style="line-height: 24px; height: 24px; margin-left: 5px;">
			<table style=" height: 24px;">
				<tr>
					<td>是否级联： 是 </td>
					<td><input type="radio" id="level1" name="level" value="level1" style="width: 20px;" />否 </td>
					<td><input type="radio" id="level2" name="level" value="level2" style="width: 20px; " checked="true" /></td>
				</tr>
			</table>
		</div>
	</div>
</body>
</html>