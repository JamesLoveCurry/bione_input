/*******************************************************/
/****     报表设计器 - 当前选择的区域信息对象  
/*******************************************************/
define(function(){

	var SelectionModule = {
		positionX : "",			// 行位置，若选择了多行，取鼠标mousedown时的那一行
		positionY : "",			// 列位置 , 若选择了多列，取鼠标mousedown时的那一列
		positionLabel : "",	// 位置，显示用
		val : "",					// 真实值
		displayVal : ""			// 显示值
	}
	
	if(ko != null
			&& typeof ko == "object"){	
		// 基于knockout的对象操作方法
		SelectionModule.newInstance = function(positionX , positionY , positionLabel , val , displayVal , editable){
			var module = {};
			jQuery.extend(module , SelectionModule);
			module.positionX = ko.observable(positionX != null ? positionX : "");
			module.positionY = ko.observable(positionY != null ? positionY : "");
			module.positionLabel = ko.observable(positionLabel != null? positionLabel : "");
			module.val = ko.observable(val != null? val : "");
			module.displayVal = ko.observable(displayVal != null? displayVal : "");
			return module;
		}
	}
	
	SelectionModule.set = function(argName , argValue){
		if(ko != null
				&& typeof ko == "object"){		
			if(argName 
					&& argName in SelectionModule
					&& typeof this[argName] == "function"){
				this[argName](argValue == null ? "" : argValue);
			}
		} else {
			this[argName] = (argValue == null ? "" : argValue);
		}
	}
	
	SelectionModule.get = function(argName){
		if(ko != null
				&& typeof ko == "object"){
			if(argName 
					&& argName in SelectionModule
					&& typeof this[argName] == "function"){
				return eval("this."+argName+"()");
			} else {
				return "";
			}
		} else {
			return this[argName];
		}
	}

	return SelectionModule;
	
})