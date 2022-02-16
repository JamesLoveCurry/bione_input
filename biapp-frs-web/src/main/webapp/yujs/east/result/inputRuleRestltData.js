//初始化界面,菜单配置路径是【/frs/yufreejs?js=/yujs/east/58table.js】
var str_html = "";
function AfterInit() {
	str_html = jso_OpenPars2.str_html;
	JSPFree.createSplitByBtn("d1", "上下", 300, [ "刷新/onRefesh" ]);

	onRefesh();
}

function onRefesh() {
	document.getElementById("d1").innerHTML = str_html;
}
