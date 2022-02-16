var dimFormula = {
	dealFormula : function(leftTreeObj,dimNo, isSum, dimTypeStruct,filterMode,checkid,isParent) {
		var viewformula = "";
		var filterformula = "";
		var lVal = [];
		var eVal = [];
		viewformula += "(";
		filterformula += "(";
		for ( var i in checkid) {
			if (isSum == "Y" && dimTypeStruct == "02") {
				var isParent=false;
				if(leftTreeObj!=null)
					isParent=leftTreeObj.getNodeByParam("id", checkid[i]).isParent;
				else if(isParent!=null&&isParent[i]!=null){
					isParent=isParent[i];
				}
				if (isParent != false) {
					if (filterMode == "01") {
						filterformula += "LIKE($" + dimNo + ",'" + checkid[i]
								+ "%')";
						viewformula += "LIKE($" + dimNo + ",'" + checkid[i]
								+ "%')";
						if (i < checkid.length - 1) {
							viewformula += "or";
							filterformula += "||";
						}
					}
					if (filterMode == "02") {
						viewformula += "NOTLIKE($" + dimNo + ",'" + checkid[i]
								+ "%')";
						filterformula += "NOTLIKE($" + dimNo + ",'"
								+ checkid[i] + "%')";
						if (i < checkid.length - 1) {
							filterformula += "&&";
							viewformula += "and";
						}
					}
					lVal.push(checkid[i] + "%");
				} else {
					if (filterMode == "01") {
						viewformula += "$"+dimNo + "==" + "'" + checkid[i] + "'";
						filterformula += "$"+dimNo + "==" + "'" + checkid[i]
								+ "'";
						if (i < checkid.length - 1) {
							viewformula += "or";
							filterformula += "||";
						}
					}
					if (filterMode == "02") {
						filterformula += "$"+dimNo + "!=" + "'" + checkid[i]
								+ "'";
						viewformula += "$"+dimNo + "!=" + "'" + checkid[i] + "'";
						if (i < checkid.length - 1) {
							viewformula += "and";
							filterformula += "&&";
						}
					}
					eVal.push(checkid[i] + "%");
				}
			} else {
				if (filterMode == "01") {
					viewformula += "$"+dimNo + "==" + "'" + checkid[i] + "'";
					filterformula += "$"+dimNo + "==" + "'" + checkid[i] + "'";
					if (i < checkid.length - 1) {
						viewformula += "or";
						filterformula += "||";
					}
				}
				if (filterMode == "02") {
					viewformula += "$"+dimNo + "!=" + "'" + checkid[i] + "'";
					filterformula += "$"+dimNo + "!=" + "'" + checkid[i] + "'";
					if (i < checkid.length - 1) {
						viewformula += "and";
						filterformula += "&&";
					}
				}
			}
		}
		viewformula += ")";
		filterformula += ")";
		return {
			viewformula : viewformula,
			filterformula : filterformula,
			lVal : lVal,
			eVal : eVal
		}
	}
}
