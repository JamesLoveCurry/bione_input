<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<%@ include file="/common/taglibs.jsp"%>
<html>
<head>
<meta name="decorator" content="/template/template14.jsp">
<link rel="stylesheet" type="text/css"
	href="${ctx}/css/classics/mainpage/example.css" />
<link rel="stylesheet" type="text/css" href="${ctx}/js/myPagination/js/myPagination/page.css" />
<script src="${ctx}/js/myPagination/js/myPagination/jquery.myPagination6.0.js" type="text/javascript"></script>
<link rel="stylesheet" type="text/css"
	href="${ctx}/css/classics/mainpage/example.css" />
	<link rel="stylesheet" type="text/css"
	href="${ctx}/css/classics/font-awesome/css/font-awesome.css" />
<style>
#center{
	background-color : #fff;
}
html{background-color:#fff;}
.an_color{color:#fc8f4a}
.an_color1{color:#4bbdfb}
.an_content li{height:25px;line-height:25px;padding:0 10px;}
.an_content li a:hover{color:#4bbdfb}
.an_content li a{color:#666;font-size:12px;text-overflow: ellipsis; overflow: hidden; white-space: nowrap;text-decoration:none;display:block;}
.an_content li:nth-child(odd){background-color:#f9f9f9}
.an_content li:nth-child(evens){background-color:#fff}
</style>
<script type="text/javascript">
	var oldVal = 1;
	var pageCount = 1;
	var total = 0;
	var grid, btns, url, ids = [];
	var msgTypes = {};
	var maxRows = 10; // 显示的最大行数
	var maxlength = 15; // 每行显示的最多字数
	var contentHeight ;
	var condition  = "";
	$(init);

	/* 全局初始化 */
	function init() {
		initSearchForm();
		initMsgType();
		loadPageData(1);
		initPagination();
		addSearchButtons("#search", grid, "#searchbtn");
		$("#pagination").find("input").live("focusout",jumpPage);
	}
	
	function initPagination(){
		var size=maxRows;
		if(pageCount!=null&&pageCount!=0){
			pageCount=parseInt((total-1)/size)+1;
		}
		else{
			pageCount=1;
		}
		Pagination=$("#pagination").myPagination({
			pageCount: pageCount,
			pageNumber: 10,
			cssStyle: 'liger',
	        panel: {
	            tipInfo_on: true,
	            tipInfo: '<span class="info">{input}/{sumPage}页',
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
	            	oldVal = page;
	            	loadPageData(page);
	            }
	        }
	    });
		$("#pagination").css("overflow","hidden").css("margin-top","0px").css("padding-top","0px");
		$("#pageSize").val(size);
	}
	
	function jumpPage(){
		var size=maxRows;
		var page=$("#pagination").find("input").val();
		p = parseInt(page)
		if(isNaN(p) || p!=page){
			p = oldVal;
			$("#pagination").find("input").val(oldVal);
		}
		if(pageCount<p){
			p=pageCount;
			Pagination.jumpPage(p);
		}
		if(p != oldVal){
			oldVal = p;
			Pagination.jumpPage(p);
			loadPageData(p);
		}
	}
	
	
	
	function initMsgType(){
		contentHeight = $(document).height();
		$("#msg_inbox").height(contentHeight-$("#mainsearch").height()-74);
		$("#msg_inlist").height(contentHeight-$("#mainsearch").height()-74);
		$("#_msg_content").height(contentHeight-$("#mainsearch").height()-78);
		$.ajax({
			cache : false,
			async : false,
			url : "${ctx}/bione/variable/param/find?typeNo=SysBulletin",
			dataType : 'json',
			type : "post",
			success : function(data)
			{
				for(var i in data){
					msgTypes[data[i].paramValue] = data[i].paramName;
				}
			}
		});
	}
	
	function loadPageData(page) 
	{
		$.ajax({
			cache : false,
			async : false,
			url : "${ctx}/bione/msg/bulletin/list.json?d="+new Date().getTime(),
			dataType : 'json',
			type : "post",
			data :{
				pagesize : maxRows,
				page : page,
				condition : condition,
				sortname : "lastUpdateTime desc"
			},
			beforeSend: function ()
            {
				// 放个加载中的提示 
            },
			complete : function()
			{
				// 加载中提示关闭
			},
			success : function(data)
			{
				total = data.Total;
				showData(data);
			},
			error : function(status) 
			{
				showErr();
			}
		});
	}
	function showData(result) 
	{
		var dataSets = [];
		dataSets.push('<ul class="con_ul">');
		if (result) {
			var dataRows = result['Rows'];
			if(!dataRows){
				return ;
			}
			var rowSize = dataRows.length;
			recordNm = dataRows.length;
			if(rowSize > maxRows) {
				rowSize = maxRows;
			}
			for (var j=0; j<rowSize; j++) {
				var dataRow = dataRows[j];
				var msg_date = getFormatDate(dataRow.lastUpdateTime, 'yyyy-MM-dd');
				var msg_id = dataRow.announcementId;
				msg_type = "["+msgTypes[dataRow.announcementType]+"]:";
				var msg_title = dataRow.announcementTitle;
				var msg_short = msg_title;
				if (msg_short.length > maxlength) {
					msg_short = msg_short.substring(0, maxlength) + "...";
				}
				dataSets.push(buildItem(msg_short, msg_title, msg_date, "javascript:openDetail(\'" + msg_id + "\')",msg_type,j));
			}
		} else {
		}
		dataSets.push('</url>');
		$("#_msg_content").html(dataSets.join(''));
	}
	
	//创建htmldom
	function buildItem(msgShort, msgTitle, editDate, hrefs,msgType,parity) 
	{
		var liItem = [];
		var icon = '<img style="width:2.5%;margin-left:10px;vertical-align:text-bottom;border-radius:4px;" src="${ctx}/images/classics/icons/new.gif"/>';
		liItem.push('<ul class="an_content">');
		if(parity % 2 == 1){
			liItem.push('<li><a href="' + hrefs + '"><span class="an_color">'+ msgType +'</span>'+ msgTitle + (getDateCalcDayTime(editDate) <= 7 ? icon : '')+'</a>');
		}else{
			liItem.push('<li><a href="' + hrefs + '"><span class="an_color1">'+ msgType +'</span>'+ msgTitle + (getDateCalcDayTime(editDate) <= 7 ? icon : '')+'</a>');
		}
		liItem.push('  <div class="date" style="right:0px">'+editDate+'</div></li>');
		liItem.push('</ul>');
/* 		liItem.push('<li>');
		liItem.push('  <p><a href="' + hrefs + '" title="' + msgTitle + '"><i class="fa fa-bullhorn" style="margin-right:4px;color:#21498f"></i><font color="red">'+msgType+'</font>' + msgShort +(getDateCalcDayTime(editDate) <= 7 ? icon : '') + '</a></p>');
		liItem.push('  <div class="date">'+editDate+'</div>');
		liItem.push('</li>'); */
		return liItem.join('');
	}
	//获取消息是否是近七天发布
	function getDateCalcDayTime(issueDate){
		var nowVal = new Date().getTime();
 		var issVal = new Date(issueDate).getTime();
 		var d_value = parseInt(((nowVal - issVal)/1000)/(24*60*60))
 		return d_value;
	}
	/* Date - Format - Util */
	function getFormatDate(dateobj, dateformat)
    {
    	var date=null;
    	if(dateobj.time){
    		date=new Date(dateobj.time);
    	}else{
    		date=new Date(dateobj);
    	}
        var g = this, p = this.options;
        if (isNaN(date)) return null;
        var format = dateformat;
        var o = {
            "M+": date.getMonth() + 1,
            "d+": date.getDate(),
            "h+": date.getHours(),
            "m+": date.getMinutes(),
            "s+": date.getSeconds(),
            "q+": Math.floor((date.getMonth() + 3) / 3),
            "S": date.getMilliseconds()
        }
        if (/(y+)/.test(format))
        {
            format = format.replace(RegExp.$1, (date.getFullYear() + "").substr(4 - RegExp.$1.length));
        }
        for (var k in o)
        {
            if (new RegExp("(" + k + ")").test(format))
            {buildItem
                format = format.replace(RegExp.$1, RegExp.$1.length == 1 ? o[k] : ("00" + o[k]).substr(("" + o[k]).length));
            }
        }
        return format;
    }
	
	function initSearchForm() {
		$("#search").ligerForm({
			fields : [ {
				display : "公告标题",
				name : "announcementTitle",
				newline : true,
				type : "text",
				cssClass : "field",
				attr : {
					op : "like",
					field : "msgAnno.announcementTitle"
				}
			},{
				display : "公告类型",
				name : "announcementType",
				newline : false,
				type : "select",
				options : {
//						data : msgTypeOpts,
			    	valueFieldID : 'announcementType',
					url: '${ctx}/bione/variable/param/find?typeNo=SysBulletin',
					valueField: 'paramValue',
					textField: 'paramName'
		    	},
				cssClass : "field",
				attr : {
					op : "=",
					field : "msgAnno.announcementType"
				}
			}]
		});
		$(".searchtitle .togglebtn").live(
				'click',
				function() {
				    var searchbox = $(this).parent().nextAll(
					    "div.searchbox:first");
				    var centerHeight = $("#center").height();
				    if ($(this).hasClass("togglebtn-down")) {
					$(this).removeClass("togglebtn-down");
					searchbox.slideToggle('fast', function() {
						$("#msg_inbox").height(contentHeight-$("#mainsearch").height()-44);
						$("#msg_inlist").height(contentHeight-$("#mainsearch").height()-44);
						$("#_msg_content").height(contentHeight-$("#mainsearch").height()-48);
					});
				    } else {
					$(this).addClass("togglebtn-down");
					searchbox.slideToggle('fast', function() {
						$("#msg_inbox").height(contentHeight-$("#mainsearch").height()-40);
						$("#msg_inlist").height(contentHeight-$("#mainsearch").height()-40);
						$("#_msg_content").height(contentHeight-$("#mainsearch").height()-44);
					});
				    }
		});
		var searchbox = $(".searchtitle .togglebtn").parent().nextAll(
	    "div.searchbox:first");
    	var centerHeight = $("#center").height();
    	$(".searchtitle .togglebtn").addClass("togglebtn-down");
		searchbox.slideToggle('fast', function() {
			$("#msg_inbox").height(contentHeight-$("#mainsearch").height()-44);
			$("#msg_inlist").height(contentHeight-$("#mainsearch").height()-44);
			$("#_msg_content").height(contentHeight-$("#mainsearch").height()-48);
		});
	}

	function msgAnno_view(id) {
		openDialog('消息公告浏览', 'msgDefManage',
				$(parent.parent.document.body).width() - 50, 
				$(parent.parent.document.body).height() - 50, 
				'${ctx}/bione/msg/announcement/' + id + '/view');
	}
	function openDialog(title, name, width, height, url){
		var _dialog = parent.parent.$.ligerui.get(name);
		if (_dialog) {
			parent.parent.$.ligerui.remove(name);
		}
		_dialog = parent.parent.$.ligerDialog.open({
			height : height,
			width : width,
			url : url,
			name : name,
			id : name,
			title : title,
			opener : opener,
			isResize : false,
			isDrag : false,
			isHidden : false
		});
		return _dialog;
	}
	
	// 获取选中的行
	function achieveIds() {
		ids = [];
		var rows = grid.getSelectedRows();
		for ( var i in rows) {
			ids.push(rows[i].announcementId);
		}
	}
	
	// 创建表单搜索按钮：搜索、高级搜索
	function addSearchButtons(form, grid, btnContainer) {
		if (!form)
			return;
		form = $(form);
		if (btnContainer) {
			BIONE.createButton({
				appendTo : btnContainer,
				text : '搜索',
				icon : 'search3',
				width : '50px',
				click : function() {
					BIONE.validate($("#formsearch"));
					if($("#formsearch").valid()){//edit by fangjuan 20150707
						var rule = BIONE.bulidFilterGroup(form);
						if (rule.rules.length) {
							condition = JSON2.stringify(rule);
						} else {
							condition = "";
						}
						loadPageData(1);
						initPagination();
					}
				}
			});
			BIONE
					.createButton({
						appendTo : btnContainer,
						text : '重置',
						icon : 'refresh2',
						width : '50px',
						click : function() {
							$(":input", form)
									.not(
											":submit, :reset,:hidden,:image,:button, [disabled]")
									.each(function() {
										$(this).val("");
									});
							$(":input[ltype=combobox]", form)
									.each(
											function() {
												var ligerid = $(this).attr(
														'data-ligerid'), ligerItem = $.ligerui
														.get(ligerid);// 需要配置comboboxName属性
												if (ligerid && ligerItem
														&& ligerItem.clear) {// ligerUI
													// 1.2
													// 以上才支持clear方法
													ligerItem.clear();
												} else {
													$(this).val("");
												}
											});
							$(":input[ltype=select]", form)
									.each(
											function() {
												var ligerid = $(this).attr(
														'data-ligerid'), ligerItem = $.ligerui
														.get(ligerid);// 需要配置comboboxName属性
												if (ligerid && ligerItem
														&& ligerItem.clear) {// ligerUI
													// 1.2
													// 以上才支持clear方法
													ligerItem.clear();
												} else {
													$(this).val("");
												}
											});
						}
					});
		}
	};
	
	//单个明细
	function openDetail(id)
	{	
		var hight = window.parent.parent.$("body").height()*0.96
		var width = window.parent.parent.$("body").width()*0.96;
		window.top.BIONE.commonOpenDialog("公告", "noticeWin", 
				width, 
				hight, 
				'${ctx}/bione/msg/announcement/'+id+'/view');
	}
</script>

</head>
<body>
	<div id="template.center">
		<div id="mainsearch">
			<!-- <div class="searchtitle">
				<img src="${ctx}/images/classics/icons/find.png" />
				<div class="togglebtn">&nbsp;</div>
			</div>
			<div class="navline" style="margin-bottom: 4px;"></div> -->
			<div id="searchbox" class="searchbox">
				<form id="formsearch">
					<div id="search"></div>
					<div class="l-clear"></div>
				</form>
				<div id="searchbtn" class="searchbtn"></div>
			</div>

		</div>
		<div class="content">
		  <div id="msg_inbox" class="in_box_con" style="overflow-y:auto;">
	          <div id="msg_inlist" class="in_list">
	            <div class="topbg">
	              <div class="top"></div>
	            </div>
	            <div class="conbg">
	              <div id="_msg_content" class="con_right">
	              </div>
	             </marquee>
	            </div>
	          </div>
	        </div>
		</div>
		<div id="pagination" style="margin-left:4px;margin-top:1px;width:99%;"></div>
	</div>
</body>
</html>