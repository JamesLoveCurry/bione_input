<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ include file="/common/taglibs.jsp"%>
<html>
<meta name="decorator" content="/template/template25_BS.jsp">
<head>
<style type="text/css">
.searchtitle img {
	left: 0px;
	top: 0px;
	width: 14px;
	height: 14px;
	padding-left: 5px;
	padding-top: 3px;
	position: relative;
}
.searchtitle img {
	display: block;
	float: left;
}
.searchtitle span {
	display: block;
	float: left;
	margin-top: 1px;
	margin-left: 4px;
	line-height: 19px;
}
#formsearch {
	display: block;
	float: left;
	width: 418px;
	height: 151px;
	border-top: none;
	border-right: 1px solid #D6D6D6;
	border-bottom: 1px solid #D6D6D6;
	border-left: none;
}
#maingrid {
	display: block;
	float: right;
	margin-left: 10px;
	border-top: none;
	margin:auto;
}
#treeSearchbar ul{
	margin : 0;
}
</style>
<script type="text/javascript">
	var app = {
		ctx : '${ctx}',
		queryObj : '${queryObj}' ? $.parseJSON('${queryObj}') : '',
		nodeType : '3'		// nodeType: 1.目录; 2.报表; 3.条线
	};
	var grid = null;
</script>
<script type="text/javascript" src="${ctx}/js/uuid/uuid.js"></script>
<%@ include file="/common/spreadjs_load.jsp"%>
<script type="text/javascript" src="${ctx}/frs/js/rptquery/rptquery.js" ></script>
<script type="text/javascript" src="${ctx}/frs/js/rptquery/dateCal.js" ></script>
<script type="text/javascript">
	var doFlag="";
	$.extend(app, {
		iconFolder : app.ctx + '/images/classics/icons/book.png',
		iconRpt : app.ctx + '/images/classics/icons/application_view_columns.png',
		iconLine : app.ctx + '/images/classics/icons/group.png',
		// 添加搜索按钮
		addSearchButtons : function(form, btnContainer) {
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
							app.rptIdx_readQueryParms();
							if (app.queryParms != null && app.queryParms.indexNo.length > 0) {
								// 打开 dialog
								//doFlag="rptIdxShow";
								var width = $(window).width() + 5, height = $(window).height() - 3;
								BIONE.commonOpenDialog("报表指标详情", "rptIdxDialog", width, height, app.ctx + '/frs/integratedquery/rptidxquery/info/rptIdxShow', null);
							} else {
								BIONE.tip('请选择报表指标');
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
					}
				});
			}
		}
	});
	
	$(function() {
		//=======gf======//
		var len = '${defSrc}' ? JSON2.parse('${defSrc}') : '';
		//var userInfo1 = JSON2.parse('${userInfo}');
		if(len.length>1){
			$("#treeContainer").html('');
			var  htmlArr =  [];
			htmlArr.push('<div title="通用报表" tag="通用报表" class="l-scroll"><ul id="tree"  style="font-size: 12; background-color: #FFFFFF; width: 92%" class="ztree"></div>');
/* 			if ($.inArray(2, len) > -1) {
				htmlArr.push('<div title="自定义报表" tag="自定义报表" class="l-scroll"><ul id="tree1"  style="font-size: 12; background-color: #FFFFFF; width: 92%" class="ztree"></div>');
			} */
			/* if ($.inArray(3, len) > -1) {
				htmlArr.push('<div title="用户自定义报表" tag="用户自定义报表" class="l-scroll"><ul id="tree2"  style="font-size: 12; background-color: #FFFFFF; width: 92%" class="ztree"></div>');
			} */
			
			$("#treeContainer").html(htmlArr.join(" "));
			//var height_ = $(".l-layout-center").height();
			var height_ = $("#right").height();
	        $("#treeContainer").ligerAccordion({ height: height_ - 60, speed: "slow" });
	        $(".l-accordion-header-inner").css("color","#183152").css("font-size","11");
	        if ($.inArray(2, len) > -1) {
	        	app.iniOrgRptTree();//机构自定义报表树
	        }
	        /* if ($.inArray(3, len) > -1) {
				app.iniUserRptTree(userInfo);//用户自定义报表树
	        } */
		}
        //=======gf=========//
		if($(".l-accordion-header")){
			$(".l-accordion-header").click(function(){
	            $(this).find(".l-accordion-header-inner").css("color","blue").parent().siblings(".l-accordion-header").find(".l-accordion-header-inner").css("color","#183152");
		    });
		}
		$('.togglebtn').remove();
		$('#navtab1').replaceWith($('<div id="spread" style="width: 100%; border: 1px solid #D0D0D0;"></div>').hide());
		app.iniRptTree();//全行通用报表树
	
		app.rptIdx_cover_rptAsyncTree_callback("01");
		// if($.contains(len, 2)){
		if ($.inArray(2, len) > -1) {
			app.rptIdx_cover_rptAsyncTree_callback("02");
		}
		if ($.inArray(3, len) > -1) {
			app.rptIdx_cover_rptAsyncTree_callback("03");
		}
		app.rptIdx_addSearchForm();
		app.rptIdx_addGrid();
		
		var $formsearch = $('#formsearch');
		var formWidth = $formsearch.width(), formHeight = $formsearch.height();
		var searchboxWidth = $('#searchbox').width();
		$('#maingrid').width(searchboxWidth - formWidth - 13).height(formHeight);
		grid.setHeight(formHeight - 1);
		
		$('.l-trigger-hover.l-trigger-cancel').live('click', function() {
			var id = $(this).siblings('input').attr('id');
			if (id == 'dataDate') {
				$('#dataDateHide').val('');
			}
		});
		
		if (app.queryObj && typeof app.queryObj === 'object') {
			// 查询表单反显
			var data = {};
			$(app.queryObj.rptidxItem).each(function(i, n) {
				data[n.paramType] = n.paramValExpression;
			});
			data.orgNm_sel = app.queryObj.orgNm_sel || '';
			app.injectQueryData(data);
			// 加载指标信息
			// 由于报表树的反选会重置指标区的grid, 因此这个指标的反显放到报表树的反选事件中去
			// grid.addRows(app.queryObj.rptidxRel);
			app.rptId = app.queryObj.rptInfo[0].id;
			app.rptNm = app.queryObj.rptInfo[0].text;
			app.rptNum = app.queryObj.rptInfo[0].rptNum;
			
			$('#orgNm_sel').val(data.orgNm_sel);	// 加载机构信息
			data.dataDate && liger.get('dataDate').setValue(data.dataDate);
			data.startDate && $('#startDate').val(data.startDate);
			data.endDate && $('#endDate').val(data.endDate);
			$('#dataDateHide').val(data.dataDate);
			$(':input').blur();
			// delete by lujs 20150520 收藏后的报表指标查询在我的文件夹点击查看时会提示‘请选择报表指标’ Start
			// 执行查询
			// $('.l-btn .l-icon-search3').parent().click();
			// delete by lujs 20150520 收藏后的报表指标查询在我的文件夹点击查看时会提示‘请选择报表指标’ End
		} else {
			app.addFavQueryButton();// 收藏查询，按钮
		}
		//app.addFavQueryButton();	// 收藏查询，按钮
		app.addRptQueryEvent(len);		// 报表树，点击 或  回车 时执行查询的事件
	});
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
		<%-- <div width="130" class="l-text" style="display: block; float: left; height: 17px; margin-top: 2px; width: 116px;">
			<input id="treesearchtext" name="treesearch" type="text" class="l-text l-text-field" style="float: left; height: 16px; width: 116px;" />
		</div>
		<div width="30%" style="display: block; float: left; margin-left: 8px; margin-top: 3px; margin-right: 3px;">
			<a id="treesearchbtn"><img src="${ctx}/images/classics/icons/find.png" /></a>
		</div> --%>
	</div>
</body>
</html>