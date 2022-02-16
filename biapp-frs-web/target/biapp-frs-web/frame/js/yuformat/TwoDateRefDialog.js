var date_sel1 = null;
var date_sel2 = null;
var str_itemkey = null;  //该参照的字段名
var str_is8 = null; //
var str_range = null;// 日期区间，需要范围；例如：10天内

function AfterInit(){
  str_range = jso_OpenPars.range;
  str_itemkey = jso_OpenPars.itemkey;  //传过来的
  str_is8 = jso_OpenPars.is8;  //是否是YYYYMMDD8位的样子

  JSPFree.createSplitByBtn("d1","上下",20,["确定/onConfirm","取消/onCancel"],false);  //
  
  document.getElementById("d1_A").innerHTML="<span id=\"helpinfo\" style=\"font-size:12px;\">请选择日期!</span>";

  //d1_A是提示语,d1_B是两个日历
  var str_html = "";

  str_html = str_html + "<div style=\"width:540px;height:270px\">\r\n"
  str_html = str_html + "<div id=\"date_1\" style=\"float:left;width:250px;height:250px;margin:10px\"></div>\r\n";
  str_html = str_html + "<div id=\"date_2\" style=\"float:left;width:250px;height:250px;margin:10px\"></div>\r\n";
  str_html = str_html + "<div style=\"clear:both\></div>";
  str_html = str_html + "</div>";

  document.getElementById("d1_B").innerHTML=str_html;

  var date_currDate = new Date();
  $('#date_1').calendar({
	onSelect: function(_date){
		date_sel1 = _date;
    validateDate();  //校验日期
	},
	current:date_currDate
  });

  $('#date_2').calendar({
	onSelect: function(_date){
		date_sel2 = _date;
    validateDate(); //校验日期
	},
	current:date_currDate
  });

  date_sel1 = date_currDate;
  date_sel2 = date_currDate;

  validateDate();
}

//校验日期
function validateDate(){
  var str_format="yyyy-MM-dd";
   if("Y"==str_is8){
   str_format = "yyyyMMdd";
  }
  var str_date1 = FreeUtil.dateFormat(str_format,date_sel1);
  var str_date2 = FreeUtil.dateFormat(str_format,date_sel2);
  var str_help = "日期范围【" +  str_date1 +"】-【" + str_date2 + "】";

  if(str_date2<str_date1){
     str_help = str_help + ",起始日期不能小于结束日期!";
  }
  
  var str_limit = "N";
  if (str_range != null && str_range != "") {
	  var date_1 = new Date(FreeUtil.dateFormat("yyyy-MM-dd",date_sel1));
	  var date_2 = new Date(FreeUtil.dateFormat("yyyy-MM-dd",date_sel2));
	  var t_1 = date_1.getTime();
	  var t_2 = date_2.getTime();
	  var t = 24*60*60*1000*10;
	  if (t_2 - t_1 > t) {
		  str_help = str_help + ",限制日期区间为10天!";
		  str_limit = "Y";
	  }
  }
  
  var dom_help = document.getElementById("helpinfo");
  dom_help.textContent=str_help;

  if(str_date2<str_date1)  {
    dom_help.style.color="red";
  } else if(str_limit == "Y") {
	  dom_help.style.color="red";
  } else {
    dom_help.style.color="blue";
  }
}

//确定
function onConfirm(){
 var str_format="yyyy-MM-dd";
 if("Y"==str_is8){
 	str_format = "yyyyMMdd";
 }
 var str_date1 = FreeUtil.dateFormat(str_format,date_sel1);
 var str_date2 = FreeUtil.dateFormat(str_format,date_sel2);

 if(str_date2<str_date1){
 	JSPFree.alert("起始日期【"+str_date1+"】必须小于结束日期【"+str_date2+"】");
 	return;
 }
 
 if (str_range != null && str_range != "") {
	  var date_1 = new Date(FreeUtil.dateFormat("yyyy-MM-dd",date_sel1));
	  var date_2 = new Date(FreeUtil.dateFormat("yyyy-MM-dd",date_sel2));
	  var t_1 = date_1.getTime();
	  var t_2 = date_2.getTime();
	  var t = 24*60*60*1000*10;
	  if (t_2 - t_1 > t) {
		  JSPFree.alert("起始日期【"+str_date1+"】-结束日期【"+str_date2+"】,限制日期区间为10天!");
		  return;
	  }
 }
 //console.log("["+str_date1+"]["+str_date2+"]");
 var str_sql = str_itemkey + " >='" + str_date1 + "' and " + str_itemkey + "<='" + str_date2 + "'";  //
 var str_text = "【" +  str_date1 +"】-【" + str_date2 + "】";

 JSPFree.closeDialog({sqlValue:str_sql,sqlText:str_text});
}

//取消
function onCancel(){
  JSPFree.closeDialog();
}
