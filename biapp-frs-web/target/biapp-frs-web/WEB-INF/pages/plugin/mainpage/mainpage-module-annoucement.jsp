<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<%@ include file="/common/meta.jsp"%>
<%@ include file="/common/taglibs.jsp"%>
<%@ include file="/common/taglibs.jsp"%>
<%@ include file="/common/jquery_load.jsp"%>
<%@ include file="/common/ligerUI_load.jsp"%>
<html xmlns="http://www.w3.org/1999/xhtml">
<head>
<%@ include file="/common/jquery_load.jsp"%>
<%@ include file="/common/ligerUI_load.jsp"%>
<link rel="stylesheet" type="text/css"
	href="${ctx}/css/classics/mainpage/example.css" />
	<link rel="stylesheet" type="text/css"
	href="${ctx}/css/classics/font-awesome/css/font-awesome.css" />
<style>
html{background-color:#fff;}
.an_color{color:#fc8f4a}
.an_color1{color:#4bbdfb}
.an_content li{height:25px;line-height:25px;padding:0 10px;}
.an_content li a:hover{color:#4bbdfb}
.an_content li a{color:#666;font-size:12px;text-overflow: ellipsis; overflow: hidden; white-space: nowrap;text-decoration:none;display:block;}
.an_content li:nth-child(odd){background-color:#ffffff}
.an_content li:nth-child(evens){background-color:#fff}
</style>
<script type="text/javascript">
	var msgTypes = {};
	var maxRows = 7; // 显示的最大行数
	var maxlength = 15; // 每行显示的最多字数
	var recordNm = 0;
	$(function() {
		var contentHeight = $(document).height();
		$("#all_div").height(contentHeight-2);
		$("#msg_inbox").height(contentHeight-35);
		$("#msg_inlist").height(contentHeight-35);
		$("#_msg_content").height(contentHeight-35-4);
		$("#scroll").height(contentHeight-35-4);
		$("#scroll").width("100%");
		initMsgType();
		loadPageData();
		
	});
	
	function initMsgType(){
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
	
	function loadPageData() 
	{
		$.ajax({
			cache : false,
			async : true,
			url : "${ctx}/bione/msg/bulletin/list.json?maxRows="+maxRows+"&d="+new Date().getTime(),
			dataType : 'json',
			type : "post",
			success : function(data)
			{
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
		var pwidth = $("#all_div").width()-150;
		$(".msg-main-a").width(pwidth);
		$(".msg-main-p").width(pwidth+40);
		
	}
	//展示错误信息
	function showErr()
	{
		var errors = [];
		errors.push('<ul class="con_ul">');
		errors.push('  <li><p>数据加载发生异常...</p></li>');
		errors.push('</url>');
		$("#_msg_content").html(errors.join(''));
	}
	//添加空白行
	function fillBlank() 
	{
		return buildItem('　', '　', '　', 'javascript:void(0)');
	}
	//创建htmldom
	function buildItem(msgShort, msgTitle, editDate, hrefs,msgType,parity) 
	{
		var liItem = [];
		var icon = '<img style="width:20px;margin-right:10px;margin-bottom:10px;vertical-align:text-bottom;border-radius:4px;padding:0 0 0 5px;" src="${ctx}/images/classics/icons/new.gif"/>';
		liItem.push('<ul class="an_content">');
		if(parity % 2 == 1){
			liItem.push('<li><a href="' + hrefs + '"><span class="an_color">'+ msgType +'</span>'+ msgTitle + (getDateCalcDayTime(editDate) <= 7 ? icon : '')+'</a>');
		}else{
			liItem.push('<li><a href="' + hrefs + '"><span class="an_color1">'+ msgType +'</span>'+ msgTitle + (getDateCalcDayTime(editDate) <= 7 ? icon : '')+'</a>');
		}
		liItem.push('  <div class="date" style="right:0px">'+editDate+'</div></li>');
		liItem.push('</ul>');
		return liItem.join('');
	}
	//获取消息是否是近七天发布
	function getDateCalcDayTime(issueDate){
		var nowVal = new Date().getTime();
 		var issVal = new Date(issueDate).getTime();
 		var d_value = parseInt(((nowVal - issVal)/1000)/(24*60*60))
 		return d_value;
	}
	//更多
	function openMore(id)
	{	
		if (recordNm < 1) {
			BIONE.tip("无数据...");
			return ;
		} 
		var url = "${ctx}/bione/msg/announcement/viewIdx?d="+new Date().getTime();
		window.parent.BIONE.commonOpenFullDialog("公告查看","noticeWin",url);
	}
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
            {
                format = format.replace(RegExp.$1, RegExp.$1.length == 1 ? o[k] : ("00" + o[k]).substr(("" + o[k]).length));
            }
        }
        return format;
    }
</script>
</head>
<body>
	<div id="all_div" class="in_box w300">
        <div class="in_box_titbg">
          <div class="in_box_tit" style = "background-color:#4b9efb;"><span class="icon">公告</span><span class="more"><a href="javascript:openMore()">更多</a></span></div>
        </div>
        <div id="msg_inbox" class="in_box_con">
          <div id="msg_inlist" class="in_list">
           <div class="topbg">
              <div class="top"></div>
            </div>
            <div class="conbg">
<!--        <marquee id="scroll"  scrollAmount=5 scrollDelay=1 direction=up onMouseOut="this.start()" onMouseOver="this.stop()"></marquee>-->
            <div id="_msg_content" class="con_right"></div>
            </div>
          </div>
        </div>
      </div>
</body>
</html>