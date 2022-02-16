/*******************************************************/
/****             报表设计器 - 工具类     ****/
/*******************************************************/
require.config({
	paths:{
		"uuid":"../../js/uuid/uuid" ,
		"constants" : "cfg/utils/constants"
	}
});

define(["uuid" , "constants"] , function(Uuid , Constants){
	
	var Utils = {};
	
	Utils._font = {
			font : "宋体",
			fontsize : "10pt",
			bold :"",
			italic : "",
			_toString : function(){
				var fontStr = "";
				fontStr = (this.bold == null? "" : this.bold) + 
					(this.italic == null || this.italic == "" ? "" : " " + this.italic) +
					(this.fontsize == null || this.fontsize == "" ? Toolbar._default.fontsize : " " + this.fontsize) + 
					(this.font == null || this.font == "" ? Toolbar._default.font : " " + this.font);
				return $.trim(fontStr);
			},
	};
	/**
	 * 初始化一块选择区域的位置显示
	 */
	Utils.initAreaPosiLabel = function(beginRow , beginCol , rowCount , colCount){
		var posiLabel = "";
		if(beginRow != null
				&& typeof beginRow != "undefined"
				&& beginCol != null
				&& typeof beginCol != "undefined"){
			if(rowCount == null
					|| typeof rowCount == "undefined"	
					|| colCount == null
					|| typeof colCount == "undefined"){
				posiLabel = Utils.toABC(Number(beginCol)+1) + (Number(beginRow)+1);
			} else if(rowCount == 1 
					&& colCount == 1){
				posiLabel = Utils.toABC(Number(beginCol)+1) + (Number(beginRow)+1);
			} else {			
				posiLabel = rowCount + "R × " + colCount + "C"
			}
		}
		return posiLabel;
	}
	
	/**
	 * 数值转字母
	 */
	Utils.toABC = function(num){
		var ABC = [ "", "A", "B", "C", "D", "E", "F", "G", "H", "I", "J", "K",
					"L", "M", "N", "O", "P", "Q", "R", "S", "T", "U", "V", "W",
					"X", "Y", "Z" ];
		var abc = "";
		while (num > 26) {
			var y = num % 26;
			if(y==0){
				abc = ABC[26] + abc;
				num = (num - y) / 26 -1;
			}
			else{
				abc = ABC[y] + abc;
				num = (num - y) / 26;
			}
			
		}
		if (num > 0) {
			abc = ABC[num] + abc;
		}
		return abc;
	}
	
	/**
	 * 字母转数字
	 */
	Utils.to123 = function(num){
		var numReturn = "0";
		if(num != null
				&& typeof num == "string"){
			for(var i = 0 ; i < num.length ; i++){
				numReturn = Number(((numReturn*26)) + Number((num.charCodeAt(i) - 64)));
			}
		}
		if(numReturn == ""){
			numReturn = "0";
		}
		return numReturn;
	}
	
	/**
	 * 将形如 A3 的位置编码，转换成行号列号
	 */
	Utils.posiToRowCol = function(posi){
		var row = "";
		var col = "";
		if(posi
				&& posi != ""){
			var numIndex = 0;
			for(var i = 0 , j = posi.length ; i < j ; i++){
				if(posi.charCodeAt(i) < 64){
					//数字
					numIndex = i;
					break;
				}
			}
			col = Number(Utils.to123(posi.substring(0,numIndex))) - 1;
			row = Number(posi.substring(numIndex , posi.length)) - 1;
		}
		return {
			row : row+"" ,
			col : col+""
		}
	}
	
	/**
	 * @param json  待压缩大小的json
	 * @param rptIdxMapping 生成的json中会将该mapping内单元格内容置空
	 */
	Utils.jsonMin = function(json , rptIdxMapping){
		if(typeof json == "string"){
			try{			
				json = JSON2.parse(json);
			}catch(e){
				return json;
			}
		}
		if(json){
			// 将单元格style做成样式映射
			var sheets = json.sheets;
			if(!sheets){
				return json;
			}
			if(rptIdxMapping == null
					|| typeof rptIdxMapping == "undefined"){
				rptIdxMapping = [];
			}
			var styleMappings = []; // name + 样式信息
			var stylesTmp = []; // 样式信息
			var mappingCount = 1;
			if(json.namedStyles
					&& typeof json.namedStyles.length != "undefined"){
				// 已经做了样式映射
				jQuery.extend(true , styleMappings , json.namedStyles);
				for(var i = 0 , j = json.namedStyles.length ; i < j ; i++){
					stylesTmp.push(JSON2.stringify(json.namedStyles[i]));
				}
				mappingCount = json.namedStyles.length+1;
			}
	//		var mappingNmPre = "__builtInStyle";
			var mappingNmPre = "";
			sheet : for(var sheet in sheets){
				// sheet
				if(sheets[sheet].data == null
						|| typeof sheets[sheet].data == "undefined"){
					continue sheet;
				}
				var cellData = sheets[sheet].data.dataTable;
				row : for(var row in cellData){
					// 行
					var rowData = cellData[row];
					if(rowData == null
							|| typeof rowData != "object"){
						continue row;
					}
					col : for(var col in rowData){
						// 列
						var cell = rowData[col];
						if(cell == null){
							continue col;
						}
						if(cell.value
								&& cell.value != ""
								&& jQuery.inArray((row+","+col) , rptIdxMapping) != -1){
							// 将主模板中报表指标单元格value置空
							cell.value = "";
						}
						if(cell.formula
								&& cell.formula != ""
								&& typeof cell.value == "object"
								&& typeof cell.value._error != "undefined"){
							// 将excel公式单元格中，类似#VALUE!等计算错误信息去掉
							cell.value = "0";
						}
						if(cell.style == null
								|| typeof cell.style != "object"){
							continue col;
						}
						var index = jQuery.inArray(JSON2.stringify(cell.style) , stylesTmp);
						var nameTmp = (index+1)+"";
						if(index == -1){
							var styleTmp = {};
							jQuery.extend(true , styleTmp , cell.style);
							stylesTmp.push(JSON2.stringify(styleTmp));
							var styleMappingTmp = {};
							jQuery.extend(true , styleMappingTmp , cell.style);
							nameTmp = mappingCount+"";
							styleMappingTmp.name = nameTmp;
							styleMappings.push(styleMappingTmp);
							mappingCount++;
						}
						cell.style = nameTmp;
					}
				}
			}
			json.namedStyles = styleMappings;
		}
		return json;
	}
	
	/**
	 * @param json 还原压缩后的json
	 */
	Utils.jsonRestore = function(json){
		if(typeof json == "string"){
			try{			
				json = JSON2.parse(json);
			}catch(e){
				return json;
			}
		}
		if(json 
				&& json.namedStyles
				&& typeof json.namedStyles.length != "undefined"
				&& json.sheets){
			var sheets = json.sheets;
			var styleMapping = json.namedStyles;
			var styleMappingFormat = {};
			for(var i = 0 , l = styleMapping.length ; i < l ; i++){
				if(!styleMapping[i]
						|| typeof styleMapping[i].name == "undefined"){
					continue;
				}
				styleMappingFormat[styleMapping[i].name] = styleMapping[i];
			}
			sheet : for(var sheet in sheets){
				// sheet
				if(sheets[sheet].data == null
						|| typeof sheets[sheet].data == "undefined"){
					continue sheet;
				}
				var cellData = sheets[sheet].data.dataTable;
				row : for(var row in cellData){
					// 行
					var rowData = cellData[row];
					if(rowData == null
							|| typeof rowData != "object"){
						continue row;
					}
					col : for(var col in rowData){
						// 列
						var cell = rowData[col];
						if(cell == null
								|| cell.style == null
								|| typeof cell.style != "string"){
							continue col;
						}
						var styleNmTmp = cell.style;
						var mappingIndex = 0;
						try{						
							mappingIndex = Number(styleNmTmp);
						}catch(e){
							continue col;
						}
						var styleTmp = styleMappingFormat[mappingIndex];
						if(styleTmp != null
								&& typeof styleTmp != "undefined"){
							cell.style = styleTmp;
							if(cell.style.name){
								delete cell.style.name;
							}
						}
					}
				}
			}
			delete json.namedStyles;
		}
		return json;
	}
	
	/**
	 * 解析宽/高属性
	 * @params target 待解析的宽/高配置项 ， 支持数字和字符串类型的百分比
	 * @parmas windowSize 参考dom对象大小
	 */
	Utils.transWidthOrHeight = function(target , windowSize){
		if(target
				&& typeof windowSize == "number"
				&& typeof target == "string"
				&& target.indexOf("%") == (target.length - 1)){
			// 若是百分比
			target = target.substring(0 , target.length - 1);
			var per = Number(target) / 100;
			target = windowSize * per;
		}
		return target;
	}
	
	/**
	 * 分析两个数组，返回两个数组交叉的元素集合
	 * @param 数组1
	 * @param 数组2
	 */
	Utils.arrayCros = function(fstArray , secArray){
		if(!fstArray
				|| fstArray.length <= 0){
			return [];
		}
		if(!secArray
				|| secArray.length <= 0){
			return fstArray;
		}
		var returnArray = [];
		for(var i = 0 , l = secArray.length ; i < l ; i++){
			if(jQuery.inArray(secArray[i] , fstArray) != -1){
				// 在fstArray中存在该数据
				returnArray.push(secArray[i]);
			}
		}
		return returnArray;
	}
	
	/**
	 * 分析两个数组，返回两个数组的并集集合
	 * @param 数组1
	 * @param 数组2
	 */
	Utils.arraySum = function(fstArray , secArray){
		if(!fstArray
				|| fstArray.length <= 0){
			fstArray = [];
		}
		if(!secArray
				|| secArray.length <= 0){
			secArray = [];
		}
		var returnArray = fstArray;
		for(var i = 0 , l = secArray.length ; i < l ; i++){
			if(jQuery.inArray(secArray[i] , returnArray) == -1){
				returnArray.push(secArray[i]);
			}
		}
		return returnArray;
	}
	
	/**
	 * 分析单元格字体字符串返回单元格字体对象
	 * @param 单元格字体字符
	 */
	Utils.getFont = function(font){
		var newFont = {};
		jQuery.extend(newFont , Utils._font);
		if(font!=null){
			$("body").append("<div id='font' style='display:none'></div>");
			
			$("#font").css("font",font);
			var fontfamily=$("#font").css("font-family");
			if(font == ""){
				fontfamily = "宋体";
			}
			var bold=$("#font").css("font-weight");
			var fontsize=$("#font").css("font-size");
			fontsize=fontsize.substring(0,fontsize.length-2);
			var italic=$("#font").css("font-style");
			$("#font").remove();
			if(fontfamily){
				newFont.font=fontfamily;
			}
			if(bold){
				newFont.bold=(bold==400)?"normal":bold;
			}
			if(fontsize){
				newFont.fontsize=Math.round(fontsize*3/4)+"pt";
			}
			var fonts = font.split(' ');
			for(j = 0; j < fonts.length; j++){
				var fontStyle = fonts[j];
				if(fontStyle.indexOf('pt') > 0){
					newFont.fontsize = fontStyle;
					break;
				}
			}
			if(italic){
				newFont.italic=italic;
			}
		}
		return newFont;
	};
	
	return Utils;
	
})