	var expressionManager = {
		expression : null,
		funcList : [],//函数
		symbolList : [],//标识符
		idxInfoList : [],//[指标]和[总账指标].[度量] 的ID和Name的映射，用于其替换
		newExpression : null,
		chooseIdxInfo : [],//来源指标，表达式中出现的指标的ID和Name的映射
		idxNoAndNameMap : [],//指标的NO和Name的映射，用于在将指标名称转换为指标NO
		stack : [],//JS 数组模拟栈
		pop : function(){ //出栈
			return this.stack.pop();
		},
		push : function(data){//入栈
			this.stack.push(data);
		},
		init : function(func, symbol, idxInfoMap, idxNoAndNameMap){
			
			for(var i=0;i<func.length;i++){
				var tmp = func[i].search("\\(");
				this.funcList.push({func : func[i].substring(0, tmp + 1),size : tmp + 1});
			}
			
			for(var i=0;i<symbol.length;i++){
				this.symbolList.push({symbol : symbol[i],size : symbol.length});
			}
			
			this.idxInfoList = idxInfoMap;
			this.idxNoAndNameMap = idxNoAndNameMap;
			
			
		},
		antiProcess : function(expr){
			var regMeasure=/I\('([A-Z]|[a-z]|[0-9]|[^\x00-\xff]|[_]|[-]|[ ]|\.)+'\)/g;
			var rMeasure = expr.match(regMeasure);
			var exprDesc = expr;
			if(rMeasure != null){
				for(var k=0;k<rMeasure.length;k++){//替换指标
					exprDesc = exprDesc.replace(rMeasure[k], "I('" + this.idxNoAndNameMap[rMeasure[k].substring(rMeasure[k].indexOf("'") + 1, rMeasure[k].indexOf("'", rMeasure[k].indexOf("'") + 1))] + "')");
				}
			}
			return exprDesc;
		},
		process : function(expr){
			this.stack = [];
			this.chooseIdxInfo = [];
			this.newExpression = "";
			this.expression = $.trim(expr);//去掉空格 和回车
		
			var regMeasure=/I\('([A-Z]|[a-z]|[0-9]|[^\x00-\xff]|[_]|[-]|[ ]|\.)+'\)/g;
			var rMeasure = this.expression.match(regMeasure);
			if(rMeasure != null){
				for(var k=0;k<rMeasure.length;k++){//替换指标
				//无法找到总账指标对应的ID
					if(!this.idxInfoList[rMeasure[k].substring(3, rMeasure[k].length - 2)]){
						return "指标" + rMeasure[k] + "替换，请检查配置是否正确!"; 
					}
				
				this.expression = this.expression.replace(rMeasure[k], "I('" + this.idxInfoList[rMeasure[k].substring(3, rMeasure[k].length - 2)] + "')");
				if(rMeasure[k].indexOf(".") < 0){
					this.chooseIdxInfo[this.idxInfoList[rMeasure[k].substring(3, rMeasure[k].length -2)]] 
					= rMeasure[k].substring(3, rMeasure[k].length - 2);//已选指标
				}else{
					this.chooseIdxInfo[this.idxInfoList[rMeasure[k].substring(3, rMeasure[k].indexOf("."))]] 
					= rMeasure[k].substring(3, rMeasure[k].indexOf("."));//已选指标
				}
			}
		}
		
		/*var r = this.expression.match(regMeasure);
		if(r != null){
			for(var k=0;k<r.length;k++){//替换指标
				
				if(!this.idxInfoList[r[k].substring(1, r[k].length - 1)]){
					return "指标" + r[k] + "替换，请检查配置是否正确!"; 
				}
				
				this.expression = this.expression.replace(r[k], "I('" + this.idxInfoList[r[k].substring(1, r[k].length - 1)] + "')");
				this.chooseIdxInfo[this.idxInfoList[r[k].substring(1, r[k].length - 1)]] = r[k].substring(1, r[k].length - 1);//已选指标
			}
		}*/
		/*
		for(var i=0;i<this.expression.length;i++){
			var j=0;
			for(j=0;j<this.funcList.length;j++){//匹配到函数
				if(this.expression.substring(i, i + this.funcList[j].size) == this.funcList[j].func){
					i += this.funcList[j].size - 1;
					this.push(this.funcList[j].func);
					break;
				}
			}
			if(j>=this.funcList.length){//未匹配到函数
				if(this.expression.charAt(i) == ")"){//右括号
					var tmp = this.pop();
					var middleExpression = "";
					while(tmp.charAt(tmp.length - 1) != "("){//函数运算()
						middleExpression = tmp + middleExpression;
						tmp = this.pop();
					}
					
					middleExpression = tmp + middleExpression + this.expression.charAt(i);
					
					if(middleExpression.substring(0,2) == "IF"){
						var newExpr = middleExpression.substring(3, middleExpression.length - 1);
						var left = 0;
						var right = 0;
						var comPosition = [];
						for(var k=0;k<newExpr.length;k++){
							if(newExpr.charAt(k) == "("){
								left ++;
							}else if(newExpr.charAt(k) == ")"){
								right ++;
							}else if(left == right && newExpr.charAt(k) == ","){
								comPosition.push(k);
							}
							
						}
						if(comPosition != null && comPosition.length == 2){//将IF处理成三目运算符? : 
							newExpr = "(" + newExpr.substring(0,
								comPosition[0]) + "?"
								+ newExpr.substring(
										comPosition[0] + 1, comPosition[1]) + ":"
								+ newExpr.substring(
										comPosition[1] + 1,
										newExpr.length) + ")";
							middleExpression = newExpr;
						}else{
							return "有IF表达式出错，请检查";
						}
					
					}
					this.push(middleExpression);
				}else{//其他
					var tmp = this.pop();
					if(tmp && tmp.charAt(tmp.length - 1) == "("){
						this.push(tmp);
						this.push(this.expression.charAt(i));
					}else{
						this.push((tmp?tmp:"") + this.expression.charAt(i));
					}
				}
			}
			
		}
		var popTmp = "";
		while((popTmp = this.pop()) != null){
			this.newExpression = popTmp + (this.newExpression?this.newExpression:"");
		}*/
		return "";
	}
	};
