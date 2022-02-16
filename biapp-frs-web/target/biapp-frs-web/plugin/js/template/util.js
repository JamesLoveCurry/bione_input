/*!
 * 工具方法类，提供给多个模块调用的统一方法接口
 * 
 */
define(function() {

	//得到当前项目的上下文路径，如“http://localhost:8080/bireport-web/login”中的“bireport-web/”
	var getContextPath = function() {
		var contextPath = document.location.pathname;
		var index = contextPath.substr(1).indexOf("/");
		contextPath = contextPath.substr(0, index + 1);
		delete index;
		return contextPath;
	};
	
	var getLigeruiID = function ($e) {
		return $e.attr('ligeruiid') || $e.attr('data-ligerid');
	};

	return {getContextPath : getContextPath, getLigeruiID : getLigeruiID};
});