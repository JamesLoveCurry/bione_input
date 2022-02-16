<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<%@ include file="/common/meta.jsp"%>
<%@ include file="/common/taglibs.jsp"%>
<html xmlns="http://www.w3.org/1999/xhtml">
<!-- 首页例子2 ->  -->
<head>
<%@ include file="/common/jquery_load.jsp"%>
<%@ include file="/common/ligerUI_load.jsp"%>
<link rel="stylesheet" type="text/css"
	href="${ctx}/css/classics/mainpage/example.css" />
<link rel="stylesheet" type="text/css"
	href="${ctx}/css/classics/mainpage/example.css" />
<link rel="stylesheet" type="text/css"
	href="${ctx}/css/classics/font-awesome/css/font-awesome.css" />
<script type="text/javascript">
	var historyList = "${historyList}";
	var dialog;
	var recordNm = 0;

	$(function() {
		var contentHeight = $(document).height();
		$("#hisrpt_inbox").height(contentHeight - 2);
		$("#hisrpt_inboxcon").height(contentHeight - 36);
		$("#hisrpt_inlist").height(contentHeight - 36);
		$("#hisrpt_con").height(contentHeight - 40);

		if (!historyList) {

			var ulHeight = $(".con").height();

			var pageSize = ulHeight / 35;
			$.ajax({
				cache : false,
				async : true,
				url : "${ctx}/rpt/frame/mainpage/getReportHistory",
				data :{
					size:5
				},
				dataType : 'json',
				type : "post",
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
					if (data == null) {
						return false;
					}
					var results = data.Rows;
					recordNm = results.length;
					for ( var i = 0; i < results.length; i++) {
						var $li = "<li><p><a title='"+results[i].rptNm+"'style='overflow: hidden;white-space: nowrap;text-overflow: ellipsis;font-size:13px;font-weight:500;text-decoration:none;' href=\"javascript:void(0);\" onclick=\"javascript:showReport('"
								+ results[i].rptId
								+ "','"
								+ results[i].rptNm
								+ "');\" title=\""
								+ results[i].rptNm
								+ "\"><i class='fa fa-table' style='margin-right:4px;color:#21498f'></i>"
								+ results[i].rptNm
								+ "</a></p>"
								+ "<div class=\"date\">"
								+ showDate(getFormatDate(results[i].accessTime,"yyyy-MM-dd" ))+ "</div>";
						$("ul").append($li);
					}
				},
				error : function(status) 
				{
				}
			});
		}

	});

	function showDate(date){
		var dateNow = getFormatDate(new Date(),"yyyy-MM-dd");
		var interval = DateDiff(dateNow,date);
		var sign;
		if(interval == 0){
			sign = "今日";
		}else if(interval == 1){
			sign = "昨日";
		}else{
			sign = weekDay(interval);
		}
		return sign;
	}
	
	function weekDay(day){
		var week = new Date().getDay();
		var sign;
		if(week-day>0){
			sign = "本周";
		}else if(day-week<7){
			sign = "上周";
		}else{
			sign = "以前";
		}
		return sign;
	}
	
	function DateDiff(d1,d2){
	    var day = 24 * 60 * 60 *1000;
	try{    
	   var dateArr = d1.split("-");
	   var checkDate = new Date();
	        checkDate.setFullYear(dateArr[0], dateArr[1]-1, dateArr[2]);
	   var checkTime = checkDate.getTime();
	  
	   var dateArr2 = d2.split("-");
	   var checkDate2 = new Date();
	        checkDate2.setFullYear(dateArr2[0], dateArr2[1]-1, dateArr2[2]);
	   var checkTime2 = checkDate2.getTime();
	    
	   var cha = (checkTime - checkTime2)/day;  
	        return cha;
	    }catch(e){
	   return false;
	   }
	}//end fun
	
	function showReport(objectId, objectName) {
		var width = window.screen.width * 0.9;
		var height = window.screen.height * 0.9;
		$.ajax({
		   type: "POST",
		   url: "${ctx}/rpt/frame/rptfav/query/rptType",
		   data: {
				rptId: objectId
			},
		   success: function(data){
			   if ('01' == data) {
					window.parent.BIONE.commonOpenFullDialog("外部报表", "alertRptIndexs", '${ctx}/rpt/rpt/rptoutershow/show/' + objectId);
				} else if ('02' == data) {
					window.parent.BIONE.commonOpenFullDialog("平台报表", "alertRptIndexs", '${ctx}/rpt/rpt/rptplatshow/show?state=1&rptId=' + objectId+"&rptNm="+encodeURI(objectName));
				}
		   }
		});
	}

	function openMore() {
		if (recordNm < 1) {
			BIONE.tip("无数据...");
			return ;
		}
		var url = "${ctx}/rpt/frame/mainpage/getMoreHistory";
		window.parent.BIONE.commonOpenFullDialog("最近访问报表","rpthisWin",url);

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
	<div id="hisrpt_inbox" class="in_box ">
		<div class="in_box_titbg">
			<div class="in_box_tit">
				<span class="icon">最近访问报表</span><span class="more"><a
					href="javascript:openMore()">更多</a></span>
			</div>
		</div>
		<div id="hisrpt_inboxcon" class="in_box_con">
			<div id="hisrpt_inlist" class="in_list">
				<div class="topbg">
					<div class="top"></div>
				</div>
				<div class="conbg">
					<div id="hisrpt_con" class="con">
						<ul class="con_ul">
						</ul>
					</div>
				</div>
			</div>
		</div>
	</div>
</body>
</html>