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
<script type="text/javascript">
	var app = {
		ctx : '${ctx}',
		queryObj : '${queryObj}' ? $.parseJSON('${queryObj}') : "",
		nodeType : '2'		// nodeType: 1.目录; 2.报表; 3.条线
	};
</script>
<script type="text/javascript" src="${ctx}/js/uuid/uuid.js" ></script>
<script type="text/javascript" src="${ctx}/frs/js/rptquery/dateCal.js" ></script>
<script type="text/javascript" src="${ctx}/frs/js/rptquery/fscheckdata.js" ></script>
<%@ include file="/common/spreadjs_load.jsp"%>
<script type="text/javascript">
	var doFlag="";
	var data = '${data}';
	if(data){
		var datajson = eval("("+'${data}'+")");
	}
	$.extend(app, {
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
								console.log(nodes);
								 
							}
							if (showType == '01') {
								var argsArr = app.rptQuery_readExportParms(nodes);
							 
								if (argsArr.length < 31) {
									// 封装查询参数
									var parms = app.rptQuery_readQueryParms(nodes);
									 
									console.log(parms.rptInfo)
									console.log(parms.argsArr)
									// 执行查询
									app.rptQuery_execQuery(parms.argsArr, parms.rptInfo, parms.lineInfo);
								}else{
									BIONE.tip('最终文档将超过 30个标签页(超过了Excel的限制), 请适当减少条件');
								}
							}  
						}
					}
				});
				BIONE.createButton({
					appendTo : btnContainer,
					text : '重置',
					icon : 'refresh2',
					align : 'left',
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
					}
				});
			}
		}
	});	
	
	
	$(function() {
		//=======gf======//
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
				htmlArr.push('<div title="通用报表 " tag="通用报表" class="l-scroll"><ul id="tree"  style="font-size: 12; background-color: #FFFFFF; width: 92%" class="ztree"></div>');
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
				/* app.addFavQueryButton(); */
			}
			//app.addFullScreenButton(nodes);
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
		var height = $(window).height() - $("#mainsearch").height() + 8;
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