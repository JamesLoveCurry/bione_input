<%@ page language="java" contentType="text/html; charset=UTF-8"%>
<!DOCTYPE html>
<html>
<head>
<%
out.println(new com.yusys.bione.plugin.yuformat.utils.YuFormatUtil().getJspHeadHtml(request));
%>

<script type="text/JavaScript">
function afterLoad(){
	var dom_loading = document.getElementById("loading");
	dom_loading.parentNode.removeChild(dom_loading);
	
	//如果本页面定义了这个函数,则执行之
	if((typeof AfterBodyLoad)=="function"){
		AfterBodyLoad();
	}
}
</script>
</head>

<body style="overflow:hidden" onload="JavaScript:afterLoad();">
<div id=loading     style="position:absolute;width:100%;height:100%;background:#FFFFFF;padding:5px;z-index:99999">
<span>系统正在加载,请稍候。。。</span>
</div>
<div id="d1" style="width:100%;height:600px;z-index:1"></div>

<form id="commform"  name="commform" method="post" action=""  style="display:none">
<input id="commFormHiddenValue"  name="commFormHiddenValue"  type="hidden" >
</form>

<iframe id="commdownload"  name="commdownload" src="about:blank"  width="1px" height="1px" style="display:none"></iframe>


<script type="text/JavaScript">
if("N"==v_isopen){
 JSPFree.resetHeight("d1");
}else if("Y"==v_isopen){
 JSPFree.resetHeight2("d1");
}
//调用初始化方法
if((typeof v_js)=="undefined"){
	var str_msg = "url后面没有定义js参数指定一个js文件!"
	console.log(str_msg);
	alert(str_msg);
}else{
	if((typeof AfterInit)=="function"){
		AfterInit();  //调用js中的初始化!
	} else {
		var str_msg = "js文件【" + v_js + "】不存在,或者js文件中没有定义AfterInit()方法!"
		console.log(str_msg);
		alert(str_msg);
	}
}
</script>
</body>
</html>
