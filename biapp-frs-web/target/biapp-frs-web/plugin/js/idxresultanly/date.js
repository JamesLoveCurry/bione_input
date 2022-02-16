(function(){
	// 对Date的扩展，将 Date 转化为指定格式的String 
	// 月(M)、日(d)、小时(h)、分(m)、秒(s)、季度(q) 可以用 1-2 个占位符， 
	// 年(y)可以用 1-4 个占位符，毫秒(S)只能用 1 个占位符(是 1-3 位的数字) 
	// 例子： 
	// (new Date()).Format("yyyy-MM-dd hh:mm:ss.S") ==> 2006-07-02 08:09:04.423 
	// (new Date()).Format("yyyy-M-d h:m:s.S")      ==> 2006-7-2 8:9:4.18 
	Date.prototype.Format = function(fmt) 
	{ //author: meizz 
	  var o = { 
	    "M+" : this.getMonth()+1,                 //月份 
	    "d+" : this.getDate(),                    //日 
	    "h+" : this.getHours(),                   //小时 
	    "m+" : this.getMinutes(),                 //分 
	    "s+" : this.getSeconds(),                 //秒 
	    "q+" : Math.floor((this.getMonth()+3)/3), //季度 
	    "S"  : this.getMilliseconds()             //毫秒 
	  }; 
	  if(/(y+)/.test(fmt)) 
	    fmt=fmt.replace(RegExp.$1, (this.getFullYear()+"").substr(4 - RegExp.$1.length)); 
	  for(var k in o) 
	    if(new RegExp("("+ k +")").test(fmt)) 
	  fmt = fmt.replace(RegExp.$1, (RegExp.$1.length==1) ? (o[k]) : (("00"+ o[k]).substr((""+ o[k]).length))); 
	  return fmt; 
	}
	
	/**
	 * 取当前日期的前一天
	 */
	Date.prototype.getYesterday = function(){
		return new Date(this.getTime() - 24*60*60*1000);
	}
	
	/**
	 * 取去年同期的值
	 */
	Date.prototype.getDateOfLastYear = function(){
		if(this.getMonth() == 1 && this.getDate == 29){
			return new Date(this.getFullYear() - 1, this.getMonth(), 28);
		}else{
			return new Date(this.getFullYear() - 1, this.getMonth(), this.getDate());
		}
	}
	/**
	 * 取月初
	 */
	Date.prototype.getMonthFirst = function(){
		return new Date(this.getFullYear(), this.getMonth() , 1);
	}
	/**
	 * 取月末
	 */
	Date.prototype.getMonthLastDate = function() {
		var new_year = this.getFullYear();
		var new_month = this.getMonth() + 1;
		if (new_month > 12) {
			new_month -= 12;
			new_year++;
		}
		var new_date = new Date(new_year, new_month, 1);
		var date = new Date(new_date.getTime() - 1);
		return date;
	}
	
	/**
	 * 取年初
	 */
	Date.prototype.getYearFirst = function(){
		return new Date(this.getFullYear(), 0, 1);
	}
	
	
	
})();