var dateCal = {
	startDate : null,
	endDate : null,
	init : function(startDate, endDate) {
		dateCal.startDate = dateCal._getDate(startDate);
		dateCal.endDate = dateCal._getDate(endDate);
	},
	_getDate : function(date) {
		var year = parseInt(date.substring(0, 4));
		var month = parseInt(date.substring(4, 6)) - 1;
		var day = parseInt(date.substring(6, 8));
		return new Date(year, month, day, 0, 0, 0);
	},
	_getDateString : function(date) {
		var year = date.getFullYear();
		var month = (date.getMonth() + 1) < 10 ? "0"
				+ (date.getMonth() + 1).toString() : (date.getMonth() + 1);
		var day = date.getDate() < 10 ? "0" + date.getDate().toString() : date.getDate();
		return year.toString() + month.toString() + day.toString();
	},
	_getMonthLastDate : function(year, month) {
		var new_year = year;
		var new_month = month + 1;
		if (month > 12) {
			new_month -= 12;
			new_year++;
		}
		var new_date = new Date(new_year, new_month, 1);
		var date = new Date(new_date.getTime() - 1000 * 60 * 60 * 24);
		return date;
	},
	_getDay : function() {
		var dates = [];
		var new_Date = dateCal.startDate;
		while (new_Date.getTime() <= dateCal.endDate.getTime()) {
			dates.push(dateCal._getDateString(new_Date));
			new_Date = new Date(new_Date.getTime() + 1000 * 60 * 60 * 24);
		}
		return dates;
	},
	_getWeekDay : function(startDate, endDate) {
		var dates = [];
		var new_Date = dateCal.startDate;
		// 周频度是取的周五的数据不是周日的start
		var retainday = 5 - new_Date.getDay();
		if(retainday < 0){
			retainday = retainday + 7;
		}
		// 周频度是取的周五的数据不是周日的 end
		new_Date = new Date(new_Date.getTime() + retainday * 1000 * 60 * 60 * 24);
		while (new_Date.getTime() <= dateCal.endDate.getTime()) {
			dates.push(dateCal._getDateString(new_Date));
			new_Date = new Date(new_Date.getTime() + 7 * 1000 * 60 * 60 * 24);
		}
		return dates;
	},
	_getTenDay : function() {
		var dates = [];
		var new_Date = dateCal.startDate;
		while (new_Date.getTime() <= dateCal.endDate.getTime()) {
			if (new_Date.getDate() == 10
					|| new_Date.getDate() == 20
					|| new_Date.getDate() == dateCal._getMonthLastDate(
							new_Date.getFullYear(), new_Date.getMonth())
							.getDate())
				dates.push(dateCal._getDateString(new_Date));
			new_Date = new Date(new_Date.getTime() + 1000 * 60 * 60 * 24);
		}
		return dates;
	},
	_getMonthDay : function(startDate, endDate) {
		var dates = [];
		var new_Date = dateCal.startDate;
		while (new_Date.getTime() <= dateCal.endDate.getTime()) {
			new_Date = dateCal._getMonthLastDate(new_Date.getFullYear(),
					new_Date.getMonth());
			if (new_Date.getTime() <= dateCal.endDate.getTime()) {
				dates.push(dateCal._getDateString(new_Date));
			}
			new_Date = new Date(new_Date.getTime() + 1000 * 60 * 60 * 24);
		}
		return dates;
	},
	_getSeasonDay : function(startDate, endDate) {
		var dates = [];
		var new_Date = dateCal.startDate;
		while (new_Date.getTime() <= dateCal.endDate.getTime()) {
			new_Date = dateCal._getMonthLastDate(new_Date.getFullYear(),
					new_Date.getMonth());
			if (new_Date.getTime() <= dateCal.endDate.getTime()) {
				if (new_Date.getMonth() == 2 || new_Date.getMonth() == 5
						|| new_Date.getMonth() == 8
						|| new_Date.getMonth() == 11)
					dates.push(dateCal._getDateString(new_Date));
			}
			new_Date = new Date(new_Date.getTime() + 1000 * 60 * 60 * 24);
		}
		return dates;
	},
	_getHalfyearDay : function(startDate, endDate) {
		var dates = [];
		var new_Date = dateCal.startDate;
		while (new_Date.getTime() <= dateCal.endDate.getTime()) {
			new_Date = dateCal._getMonthLastDate(new_Date.getFullYear(),
					new_Date.getMonth());
			if (new_Date.getTime() <= dateCal.endDate.getTime()) {
				if (new_Date.getMonth() == 5 || new_Date.getMonth() == 11)
					dates.push(dateCal._getDateString(new_Date));
			}
			new_Date = new Date(new_Date.getTime() + 1000 * 60 * 60 * 24);
		}
		return dates;
	},
	_getYearDay : function(startDate, endDate) {
		var dates = [];
		var new_Date = dateCal.startDate;
		while (new_Date.getTime() <= dateCal.endDate.getTime()) {
			new_Date = dateCal._getMonthLastDate(new_Date.getFullYear(),
					new_Date.getMonth());
			if (new_Date.getTime() <= dateCal.endDate.getTime()) {
				if (new_Date.getMonth() == 11)
					dates.push(dateCal._getDateString(new_Date));
			}
			new_Date = new Date(new_Date.getTime() + 1000 * 60 * 60 * 24);
		}
		return dates;
	},
	_getYesterday: function(date) {
		var today=this._getDate(date);
		var t=today.getTime() - 1000*60*60*24;
		var yesterday=new Date(t);
		return this._getDateString(yesterday);
	},
	getDuringDate : function(id) {
		switch (id) {
		case "01": {
			return dateCal._getDay();
			break;
		}
		case "02": {
			return dateCal._getTenDay();
			break;
		}
		case "03": {
			return dateCal._getMonthDay();
			break;
		}
		case "04": {
			return dateCal._getSeasonDay();
			break;
		}
		case "05": {
			return dateCal._getHalfyearDay();
			break;
		}
		case "06": {
			return dateCal._getYearDay();
			break;
		}
		case "07": {
			return dateCal._getWeekDay();
			break;
		}
		default: {
			return [];
			break;
		}
		}
	}

}
