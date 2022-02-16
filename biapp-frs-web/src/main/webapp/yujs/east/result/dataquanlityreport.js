//初始化界面,菜单配置路径是【/frs/yufreejs?js=/yujs/east/dataqualitypoc.js】
var str_org_no = "";
var str_org_type = "";
var str_report_name = "";
var str_report_date = "";
var str_data_time = "";
var str_tab_count = "";
var str_total_count = "";
var str_fail_count = "";
var str_fail_rate = "";
var str_mom_ratio = "";
var str_yoy_ratio = "";
var str_rule_type_count = "";
var str_rule_count = "";
var str_pass_rate = "";

var busi_type = "";
var str_report_id = "";
var type_cd = [];
var type_cd_count = [];
function AfterInit(){
	str_report_id = jso_OpenPars.rid; //传参
	
	var jsy_data = JSPFree.getHashVOs("select * from east_data_quality_report where report_id='"+str_report_id+"'");
	// 判断是报送总行还是报送分行
	str_org_no = jsy_data[0].org_no;
	str_org_type = jsy_data[0].org_type;
	
	str_report_name = jsy_data[0].report_name;
	str_report_date = jsy_data[0].report_date;
	str_data_time = jsy_data[0].data_time;
	str_tab_count = jsy_data[0].tab_count;
	str_total_count = jsy_data[0].total_count;
	str_fail_count = jsy_data[0].fail_count;
	str_fail_rate = jsy_data[0].fail_rate;
	str_mom_ratio = jsy_data[0].mom_ratio;
	str_yoy_ratio = jsy_data[0].yoy_ratio;
	str_rule_type_count = jsy_data[0].rule_type_count;
	str_rule_count = jsy_data[0].rule_count;
	
	str_pass_rate = 100-str_fail_rate;
	
	// 获取9类校验的各自校验的数量
	// 修改日期格式8位转换10位
	var dd = dataFormat(str_report_date);
	
	if (str_org_type == "1") {
		busi_type = "1_" + str_org_no;
	} else if (str_org_type == "2") {
		busi_type = "2_" + str_org_no;
	}
	var jsy_rule = JSPFree.getHashVOs("select type_cd,count(*) c from v_east_cr_summary_rule where type_cd is not null and data_dt = '"+dd+"' and busi_type like '"+busi_type+"%' group by type_cd order by count(*)");
	for (var i=0;i<jsy_rule.length;i++) {
		type_cd.push(jsy_rule[i].type_cd);
		type_cd_count.push(jsy_rule[i].c);
	}
	// 获取59张表数据
	var jsy_tabs = JSPFree.getHashVOs("SELECT * from (SELECT tt.tab_name,sum(cc.fail_count) fail_count,sum(cc.total_count) total_count, " +
			"case when sum(cc.total_count)=0 then '' else to_char(ROUND(sum(cc.fail_count)/(sum(cc.total_count)),4)*100,'FM999990.00')||'%' end AS fail_rate " +
			"FROM east_Cr_tab tt, east_check_result_col cc where tt.tab_name = CC.tab_name and IS_VIRTUAL = 'N' and CC.BUSI_TYPE like '"+busi_type+"%' and CC.DATA_DT = '"+str_report_date+"' GROUP BY tt.tab_name,tab_no ORDER BY tt.tab_no) order by fail_count asc");

	getTableOrder(jsy_tabs);
}
function upTable() {
	var jsy_tabs = JSPFree.getHashVOs("SELECT * from (SELECT tt.tab_name,sum(cc.fail_count) fail_count,sum(cc.total_count) total_count, " +
		"case when sum(cc.total_count)=0 then '' else to_char(ROUND(sum(cc.fail_count)/(sum(cc.total_count)),4)*100,'FM999990.00')||'%' end AS fail_rate " +
		"FROM east_Cr_tab tt, east_check_result_col cc where tt.tab_name = CC.tab_name and IS_VIRTUAL = 'N' and CC.BUSI_TYPE like '"+busi_type+"%' and CC.DATA_DT = '"+str_report_date+"' GROUP BY tt.tab_name,tab_no ORDER BY tt.tab_no) order by fail_count asc");
	getTableOrder(jsy_tabs);
}
function downTable() {
	var jsy_tabs = JSPFree.getHashVOs("SELECT * from (SELECT tt.tab_name,sum(cc.fail_count) fail_count,sum(cc.total_count) total_count, " +
		"case when sum(cc.total_count)=0 then '' else to_char(ROUND(sum(cc.fail_count)/(sum(cc.total_count)),4)*100,'FM999990.00')||'%' end AS fail_rate " +
		"FROM east_Cr_tab tt, east_check_result_col cc where tt.tab_name = CC.tab_name and IS_VIRTUAL = 'N' and CC.BUSI_TYPE like '"+busi_type+"%' and CC.DATA_DT = '"+str_report_date+"' GROUP BY tt.tab_name,tab_no ORDER BY tt.tab_no) order by fail_count desc");
	getTableOrder(jsy_tabs);
}
function getTableOrder(jsy_tabs) {
	var str_html= getStrHtml();
	// 获取59张表数据
	var tab_html = "";
	for (var j=0;j<jsy_tabs.length;j++) {
		var tab_name = jsy_tabs[j].tab_name;
		var fail_count = jsy_tabs[j].fail_count;
		var total_count = jsy_tabs[j].total_count;
		var fail_rate = jsy_tabs[j].fail_rate;
		tab_html = tab_html +
			"				<div class='report-vue-list'>                                                             "+
			"					<div class='list-introduce-left'>                                                     "+
			"					  <div class='introduce-left-tit'>"+tab_name+"</div>                                      "+
			"					  <div class='introduce-left-content'>                                                "+
			"						<ul>                                                                              "+
			"							<li><span>总记录数：</span><i>"+total_count+"</i></li>                         "+
			"							<li><span>错误记录数:</span><i class='importantColor'>"+fail_count+"</i></li>     "+
			"							<li><span>错误率:</span><i>"+fail_rate+"</i></li>                                    "+
			"							<li><span>环比:</span><i class='positiveColor'>+ 0.00%</i></li>               "+
			"							<li><span>同比:</span><i class='negative'>- 0.00%</i></li>                   "+
			"						</ul>                                                                             "+
			"					  </div>                                                                              "+
			"					</div>                                                                                "+
			"					<div class='list-vue-right'>                                                          "+
			"						<div id='bar-chart"+j+"'></div>                                                      "+
			"						<i><div class='moreButton' data-tabNm='"+tab_name+"'>更多</div></i>             "+
			"					</div>                                                                                "+
			"				</div>                                                                                    ";
	}
	tab_html = tab_html +
		"			</div>                                                                                        "+
		"		</div>                                                                                            "+
		"	</div>                                                                                                "+
		"</div>                                                                                                   "+
		"</body>";
	str_html = str_html + tab_html;
	document.getElementById("d1").innerHTML = str_html;
	load(jsy_tabs);

	$(".moreButton").bind('click', function(e) {
		var tabName = e.toElement.getAttribute('data-tabNm');
		openMoreColoum(tabName);
	});
	$(".lool-all-btn").bind('click', function(e) {
		openMoreColoumAll();
	});
	$(".list-up").bind('click', function(e) {
		upTable();
	});
	$(".list-down").bind('click', function(e) {
		downTable();
	});
}
function getStrHtml() {
	var str_html = "<head><style type='text/css'>"+

	"body,                                                                                                          "+
	"ul,                                                                                                            "+
	"li,                                                                                                            "+
	"p,                                                                                                             "+
	"h1,                                                                                                            "+
	"h2,                                                                                                            "+
	"h3,                                                                                                            "+
	"h4,                                                                                                            "+
	"h5 {                                                                                                           "+
	"	margin: 0px;                                                                                                "+
	"	padding: 0px;                                                                                               "+
	"}                                                                                                              "+
	"                                                                                                               "+
	"i {                                                                                                            "+
	"	font-style: normal;                                                                                         "+
	"}                                                                                                              "+
	"                                                                                                               "+
	"body {                                                                                                         "+
	"	background-color: #EFF5F9;                                                                                  "+
	"	font-weight: 400;                                                                                           "+
	"	font-family: '微软雅黑';                                                                                    "+
	"}                                                                                                              "+
	"                                                                                                               "+
	"li {                                                                                                           "+
	"	list-style: none;                                                                                           "+
	"}                                                                                                              "+
	"                                                                                                               "+
	".content_box {                                                                                                 "+
	"	padding: 24px;                                                                                              "+
	"	color: #495A63;                                                                                             "+
	//"	min-width: 960px;                                                                                          "+
	//"   overflow:scroll; height:540px;																				"+
	"}                                                                                                              "+
	"                                                                                                               "+
	".el-row {                                                                                                      "+
	"	margin-left: -12px;                                                                                         "+
	"	margin-right: -12px;                                                                                        "+
	"	margin-bottom: 24px;                                                                                        "+
	"	overflow: hidden;                                                                                           "+
	"}                                                                                                              "+
	"                                                                                                               "+
	"/*.el-row:before,                                                                                              "+
	".el-row:after {                                                                                                "+
	"	display: table;                                                                                             "+
	"	content: '';                                                                                                "+
	"}*/                                                                                                            "+
	"                                                                                                               "+
	".el-row:after {                                                                                                "+
	"	clear: both;                                                                                                "+
	"}                                                                                                              "+
	"                                                                                                               "+
	".el-col {                                                                                                      "+
	"	padding: 0px 12px;                                                                                          "+
	"	display: block;                                                                                             "+
	"	box-sizing: border-box;                                                                                     "+
	"	float: left;                                                                                                "+
	"}                                                                                                              "+
	"                                                                                                               "+
	".el-col-3 {                                                                                                    "+
	"	width: 25%;                                                                                                 "+
	"}                                                                                                              "+
	"                                                                                                               "+
	".el-col-6 {                                                                                                    "+
	"	width: 50%;                                                                                                 "+
	"}                                                                                                              "+
	"                                                                                                               "+
	".el-col-3 img {                                                                                                "+
	"	width: 48px;                                                                                                "+
	"	height: 48px;                                                                                               "+
	"	margin-top: 20px;                                                                                           "+
	"}                                                                                                              "+
	"                                                                                                               "+
	".el-col>div {                                                                                                  "+
	"	background: #fff;                                                                                           "+
	"	box-shadow: 0px 0px 8px 0px #d9d9d9;                                                                        "+
	"	border-radius: 4px;                                                                                         "+
	"	position: relative;                                                                                         "+
	"}                                                                                                              "+
	"                                                                                                               "+
	".el-col-3>div {                                                                                                "+
	"	width: 100%;                                                                                                "+
	"	height: 120px;                                                                                              "+
	"	text-align: center;                                                                                         "+
	"}                                                                                                              "+
	"                                                                                                               "+
	".el-col-6>div {                                                                                                "+
	"	width: 100%;                                                                                                "+
	"}                                                                                                              "+
	"                                                                                                               "+
	".el-col-3>div>i {                                                                                              "+
	"	font-size: 16px;                                                                                            "+
	"	font-weight: 400;                                                                                           "+
	"	display: block;                                                                                             "+
	"	line-height: 40px;                                                                                          "+
	"	height: 40px;                                                                                               "+
	"}                                                                                                              "+
	"                                                                                                               "+
	".col-box {                                                                                                     "+
	"	padding: 0px 12px;                                                                                          "+
	"}                                                                                                              "+
	"                                                                                                               "+
	".box-tit {                                                                                                     "+
	"	height: 58px;                                                                                               "+
	"	line-height: 58px;                                                                                          "+
	"	font-size: 16px;                                                                                            "+
	"}                                                                                                              "+
	"                                                                                                               "+
	".color1,                                                                                                       "+
	".color2,                                                                                                       "+
	".color3 {                                                                                                      "+
	"	border-top: solid 2px;                                                                                      "+
	"	box-sizing: border-box;                                                                                     "+
	"	                                                                                                            "+
	"}                                                                                                              "+
	"                                                                                                               "+
	".color1 {                                                                                                      "+
	"	border-color: #5BC0DD;                                                                                      "+
	"}                                                                                                              "+
	"                                                                                                               "+
	".color2 {                                                                                                      "+
	"	border-color: #1B88E5;                                                                                      "+
	"}                                                                                                              "+
	".color3 {                                                                                                      "+
	"	border-color: #75BEDA;                                                                                      "+
	"	box-shadow: 0px 0px 8px 0px #D9D9D9;                                                                        "+
	"}                                                                                                              "+
	"                                                                                                               "+
	".box-tit>i {                                                                                                   "+
	"	float: right;                                                                                               "+
	"	border: 1px solid #D8DCE0;                                                                                  "+
	"	/* padding: 0px 10px; */                                                                                    "+
	"	height: 32px;                                                                                               "+
	"	line-height: 32px;                                                                                          "+
	"	margin-top: 12px;                                                                                           "+
	"	width: 60px;                                                                                                "+
	"	text-align: center;                                                                                         "+
	"	border-radius: 16px;                                                                                        "+
	"}                                                                                                              "+
	"                                                                                                               "+
	".box-content>ul>li {                                                                                           "+
	"	height: 40px;                                                                                               "+
	"	line-height: 40px;                                                                                          "+
	"	border-top: 1px solid #EAEAF0;                                                                              "+
	"	box-sizing: border-box;                                                                                     "+
	"	cursor: pointer;                                                                                            "+
	"}                                                                                                              "+
	"                                                                                                               "+
	".box-content>ul>li:hover {}                                                                                    "+
	"                                                                                                               "+
	".box-content>ul>li>span {                                                                                      "+
	"	width: 345px;                                                                                               "+
	"    height: 40px;                                                                                              "+
	"    white-space: nowrap;                                                                                       "+
	"    text-overflow: ellipsis;                                                                                   "+
	"    overflow: hidden;                                                                                          "+
	"    float: left;                                                                                               "+
	"}                                                                                                              "+
	"                                                                                                               "+
	".box-content>ul>li>b {                                                                                         "+
	"	    color: #667C89;                                                                                         "+
	"    float: right;                                                                                              "+
	"    margin-right: 10px;                                                                                        "+
	"}                                                                                                              "+
	"                                                                                                               "+
	".box-content>ul>li>i {                                                                                         "+
	"    font-size: 12px;                                                                                           "+
	"    padding: 0px 6px;                                                                                          "+
	"    background-color: #99aab4;                                                                                 "+
	"    color: #fff;                                                                                               "+
	"    vertical-align: middle;                                                                                    "+
	"    margin-right: 5px;                                                                                         "+
	"    float: left;                                                                                               "+
	"    height: 20px;                                                                                              "+
	"    line-height: 20px;                                                                                         "+
	"    margin-top: 10px;                                                                                          "+
	"}                                                                                                              "+
	"                                                                                                               "+
	".box-content>ul>li>i.no-read {                                                                                 "+
	"	background-color: #f7b132;                                                                                  "+
	"}                                                                                                              "+
	"                                                                                                               "+
	".show-box-tit {                                                                                                "+
	"	padding: 0px 12px;                                                                                          "+
	"	height: 24px;                                                                                               "+
	"	line-height: 24px;                                                                                          "+
	"	vertical-align: middle;                                                                                     "+
	"	font-size: 16px;                                                                                            "+
	"	margin-bottom: 20px;                                                                                        "+
	"}                                                                                                              "+
	"                                                                                                               "+
	".show-box-tit>img {                                                                                            "+
	"	vertical-align: middle;                                                                                     "+
	"	margin-right: 10px;                                                                                         "+
	"}                                                                                                              "+
	"                                                                                                               "+
	".box-content>span {                                                                                            "+
	"	color: #667C89;                                                                                             "+
	"	font-size: 12px;                                                                                            "+
	"	height: 12px;                                                                                               "+
	"	line-height: 12px;                                                                                          "+
	"	display: block;                                                                                             "+
	"}                                                                                                              "+
	"                                                                                                               "+
	".show-pic {                                                                                                    "+
	"	font-size: 24px;                                                                                            "+
	"}                                                                                                              "+
	"                                                                                                               "+
	".show-pic>p {                                                                                                  "+
	"	color: #667C89;                                                                                             "+
	"	height: 24px;                                                                                               "+
	"	line-height: 24px;                                                                                          "+
	"	margin-top: 36px;                                                                                           "+
	"}                                                                                                              "+
	"                                                                                                               "+
	".show-pic>p>span {                                                                                             "+
	"	color: #495A63;                                                                                             "+
	"	display: inline-block;                                                                                      "+
	"}                                                                                                              "+
	"                                                                                                               "+
	".show-pic>p>span.num-light {                                                                                   "+
	"	color: #75BEDA;                                                                                             "+
	"}                                                                                                              "+
	"                                                                                                               "+
	".show-pic>p>span.num-all {                                                                                     "+
	"	margin-left: 5px;                                                                                           "+
	"}                                                                                                              "+
	"                                                                                                               "+
	".show-pic>p>b {                                                                                                "+
	"	color: #fff;                                                                                                "+
	"	background-color: #75BEDA;                                                                                  "+
	"	font-size: 14px;                                                                                            "+
	"	padding: 3px 10px;                                                                                          "+
	"	border-radius: 14px;                                                                                        "+
	"	float: right;                                                                                               "+
	"	height: 18px;                                                                                               "+
	"	line-height: 18px;                                                                                          "+
	"}                                                                                                              "+
	"                                                                                                               "+
	".show-pic>p>b:after {                                                                                          "+
	"	content: '%';                                                                                               "+
	"}                                                                                                              "+
	"                                                                                                               "+
	".num-line {                                                                                                    "+
	"	width: 100%;                                                                                                "+
	"	height: 12px;                                                                                               "+
	"	border-radius: 12px;                                                                                        "+
	"	background-color: #f1f4f9;                                                                                  "+
	"	margin-top: 15px;                                                                                           "+
	"}                                                                                                              "+
	"                                                                                                               "+
	".num-line>div {                                                                                                "+
	"	height: 12px;                                                                                               "+
	"	background-color: #75BEDA;                                                                                  "+
	"	border-radius: 12px;                                                                                        "+
	"}                                                                                                              "+
	"                                                                                                               "+
	".compare {                                                                                                     "+
	"	border-top: 1px solid #ececec;                                                                              "+
	"	overflow: hidden;                                                                                           "+
	"	height: 64px;                                                                                               "+
	"	position: absolute;                                                                                         "+
	"	bottom: 0px;                                                                                                "+
	"	width: 100%;                                                                                                "+
	"	margin: 0px -12px;                                                                                          "+
	"}                                                                                                              "+
	"                                                                                                               "+
	".compare>div {                                                                                                 "+
	"	float: left;                                                                                                "+
	"	width: 50%;                                                                                                 "+
	"	height: 64px;                                                                                               "+
	"	font-size: 14px;                                                                                            "+
	"	text-align: center;                                                                                         "+
	"	padding-top: 15px;                                                                                          "+
	"	box-sizing: border-box;                                                                                     "+
	"	position: relative;                                                                                         "+
	"	line-height: 20px;                                                                                          "+
	"}                                                                                                              "+
	"                                                                                                               "+
	".compare>div:first-child {                                                                                     "+
	"	border-right: 1px solid #EAEAF0;                                                                            "+
	"}                                                                                                              "+
	"                                                                                                               "+
	".up {                                                                                                          "+
	"	color: #E85168;                                                                                             "+
	"}                                                                                                              "+
	"                                                                                                               "+
	".down {                                                                                                        "+
	"	color: #63C2BC;                                                                                             "+
	"}                                                                                                              "+
	"                                                                                                               "+
	".compare>div:before {                                                                                          "+
	"	content: '';                                                                                                "+
	"	display: inline-block;                                                                                      "+
	"	height: 10px;                                                                                               "+
	"	width: 12px;                                                                                                "+
	"	margin-right: 10px;                                                                                         "+
	"}                                                                                                              "+
	"                                                                                                               "+
	".compare>div.up:before {                                                                                       "+
	"	background: url(../images/east/img/up.png) transparent no-repeat;                                                       "+
	"}                                                                                                              "+
	"                                                                                                               "+
	".compare>div.down:before {                                                                                     "+
	"	background: url(../images/east/img/down.png) transparent no-repeat;                                                     "+
	"}                                                                                                              "+
	"                                                                                                               "+
	".compare>div>span {                                                                                            "+
	"	display: block;                                                                                             "+
	"	color: #495A63;                                                                                             "+
	"}                                                                                                              "+
	"                                                                                                               "+
	"#doughnut-chart {                                                                                              "+
	"	width: 180px;                                                                                               "+
	"	height: 180px;                                                                                              "+
	"	float: right;                                                                                               "+
	"}                                                                                                              "+
	"                                                                                                               "+
	".box-content>h3 {                                                                                              "+
	"	color: #E9596F;                                                                                             "+
	"	float: left;                                                                                                "+
	"	margin-top: 70px;                                                                                           "+
	"	margin-left: 75px;                                                                                          "+
	"	font-size: 28px;                                                                                            "+
	"	font-weight: 400;                                                                                           "+
	"}                                                                                                              "+
	"                                                                                                               "+
	".box-content>h3>i {                                                                                            "+
	"	font-size: 14px;                                                                                            "+
	"}                                                                                                              "+
	"                                                                                                               "+
	".box-content.ranking>ul>li {                                                                                   "+
	"	height: 65px;                                                                                               "+
	"	line-height: 65px;                                                                                          "+
	"	box-sizing: border-box;                                                                                     "+
	"	border-left: 2px solid transparent;                                                                         "+
	"	cursor: pointer;                                                                                            "+
	"}                                                                                                              "+
	"                                                                                                               "+
	".box-content.ranking>ul>li:hover {                                                                             "+
	"	border-left: 2px solid #2288e4;                                                                             "+
	"	box-sizing: border-box;                                                                                     "+
	"}                                                                                                              "+
	"                                                                                                               "+
	".box-content.ranking>ul>li>span {                                                                              "+
	"	width: 56px;                                                                                                "+
	"	color: #fff;                                                                                                "+
	"	height: 24px;                                                                                               "+
	"	background: #9CAAB3;                                                                                        "+
	"	border-radius: 4px;                                                                                         "+
	"	display: inline-block;                                                                                      "+
	"	line-height: 24px;                                                                                          "+
	"	text-align: center;                                                                                         "+
	"	vertical-align: middle;                                                                                     "+
	"	margin-left: 20px;                                                                                          "+
	"	margin-top: 20px;                                                                                           "+
	"}                                                                                                              "+
	"                                                                                                               "+
	".box-content.ranking>ul>li:first-child>span {                                                                  "+
	"	background: #E9596F;                                                                                        "+
	"}                                                                                                              "+
	"                                                                                                               "+
	".box-content.ranking>ul>li:nth-child(2)>span {                                                                 "+
	"	background: #F4B44B;                                                                                        "+
	"}                                                                                                              "+
	"                                                                                                               "+
	".box-content.ranking>ul>li:nth-child(3)>span {                                                                 "+
	"	background: #7064E6;                                                                                        "+
	"}                                                                                                              "+
	"                                                                                                               "+
	".ranking-detail {                                                                                              "+
	"	display: inline-block;                                                                                      "+
	"	margin-left: 30px;                                                                                          "+
	"	height: 60px;                                                                                               "+
	"	line-height: 60px;                                                                                          "+
	"	position: relative;                                                                                         "+
	"	width: 180px;                                                                                               "+
	"	vertical-align: middle;                                                                                     "+
	"}                                                                                                              "+
	"                                                                                                               "+
	".ranking-detail>span {                                                                                         "+
	"	font-size: 18px;                                                                                            "+
	"	display: inline-block;                                                                                      "+
	"	height: 30px;                                                                                               "+
	"	line-height: 30px;                                                                                          "+
	"	width: 300px;                                                                                               "+
	"	position: absolute;                                                                                         "+
	"	top: 5px;                                                                                                   "+
	"}                                                                                                              "+
	"                                                                                                               "+
	".ranking-detail>i {                                                                                            "+
	"	font-size: 12px;                                                                                            "+
	"	display: block;                                                                                             "+
	"	position: absolute;                                                                                         "+
	"	width: 300px;                                                                                               "+
	"	overflow: hidden;                                                                                           "+
	"	text-overflow: ellipsis;                                                                                    "+
	"	white-space: nowrap;                                                                                        "+
	"	top: 10px;                                                                                                  "+
	"}                                                                                                              "+
	"                                                                                                               "+
	".ranking-num>div {                                                                                             "+
	"	width: 100px;                                                                                               "+
	"	height: 64px;                                                                                               "+
	"	font-size: 14px;                                                                                            "+
	"}                                                                                                              "+
	"                                                                                                               "+
	".ranking-num>div:before {                                                                                      "+
	"	content: '';                                                                                                "+
	"	display: inline-block;                                                                                      "+
	"}                                                                                                              "+
	"                                                                                                               "+
	".ranking-num {                                                                                                 "+
	"	width: 100px;                                                                                               "+
	"	display: inline-block;                                                                                      "+
	"	float: right;                                                                                               "+
	"}                                                                                                              "+
	"                                                                                                               "+
	".ranking-num>div.up:before {                                                                                   "+
	"	background: url(../images/east/img/up.png) transparent no-repeat;                                                       "+
	"	width: 12px;                                                                                                "+
	"	height: 10px;                                                                                               "+
	"	margin-right: 10px;                                                                                         "+
	"}                                                                                                              "+
	"                                                                                                               "+
	".ranking-num>div.down:before {                                                                                 "+
	"	background: url(../images/east/img/down.png) transparent no-repeat;                                                     "+
	"	width: 12px;                                                                                                "+
	"	height: 10px;                                                                                               "+
	"	margin-right: 10px;                                                                                         "+
	"}                                                                                                              "+
	"                                                                                                               "+
	".bar-pic {                                                                                                     "+
	"	width: 100%;                                                                                                "+
	"	height: 420px;                                                                                              "+
	"	position: relative;                                                                                         "+
	"}                                                                                                              "+
	"                                                                                                               "+
	".bar-box {                                                                                                     "+
	"	background-color: #fff;                                                                                     "+
	"}                                                                                                              "+
	"                                                                                                               "+
	".el-col-12 {                                                                                                   "+
	"	width: 100%;                                                                                                "+
	"}                                                                                                              "+
	"                                                                                                               "+
	".bar-detail {                                                                                                  "+
	"	width: 100%;                                                                                                "+
	"	height: 70px;                                                                                               "+
	"	border-top: 1px solid #EAEAF0;                                                                              "+
	"	position: absolute;                                                                                         "+
	"	bottom: 0px;                                                                                                "+
	"	text-align: center;                                                                                         "+
	"	line-height: 70px;                                                                                          "+
	"	font-size: 14px;                                                                                            "+
	"}                                                                                                              "+
	"                                                                                                               "+
	"#bar-box {                                                                                                     "+
	"	height: 350px;                                                                                              "+
	"	width: 100%;                                                                                                "+
	"}                                                                                                              "+
	"                                                                                                               "+
	".bar-detail>span {                                                                                             "+
	"	display: inline-block;                                                                                      "+
	"	margin: 0px 10px;                                                                                           "+
	"}                                                                                                              "+
	"                                                                                                               "+
	".bar-detail>span:before {                                                                                      "+
	"	content: '';                                                                                                "+
	"	width: 16px;                                                                                                "+
	"	height: 16px;                                                                                               "+
	"	border-radius: 16px;                                                                                        "+
	"	margin-right: 10px;                                                                                         "+
	"	display: inline-block;                                                                                      "+
	"	vertical-align: middle;                                                                                     "+
	"}                                                                                                              "+
	"                                                                                                               "+
	".bar-detail>span.bar-item1:before {                                                                            "+
	"	background-color: #3F88DE;                                                                                  "+
	"}                                                                                                              "+
	"                                                                                                               "+
	".bar-detail>span.bar-item2:before {                                                                            "+
	"	background-color: #5EC3D7;                                                                                  "+
	"}                                                                                                              "+
	"                                                                                                               "+
	".bar-item1 {                                                                                                   "+
	"	color: #3F88DE;                                                                                             "+
	"}                                                                                                              "+
	"                                                                                                               "+
	".bar-item2 {                                                                                                   "+
	"	color: #5EC3D7;                                                                                             "+
	"}                                                                                                              "+
	"                                                                                                               "+
	".report-head {                                                                                                 "+
	"	width: 100%;                                                                                                "+
	"	height: 80px;                                                                                               "+
	"	background-color: #a7b8c2;                                                                                  "+
	"	border-top-left-radius: 5px;                                                                                "+
	"	border-top-right-radius: 5px;                                                                               "+
	"	text-align: center;                                                                                         "+
	"	position: relative;                                                                                         "+
	"}                                                                                                              "+
	"                                                                                                               "+
	".report-head>h3 {                                                                                              "+
	"	font-size: 18px;                                                                                            "+
	"	color: #fff;                                                                                                "+
	"	height: 55px;                                                                                               "+
	"	line-height: 55px;                                                                                          "+
	"	font-weight: 400;                                                                                           "+
	"}                                                                                                              "+
	"                                                                                                               "+
	".report-head-detail>span {                                                                                     "+
	"	font-size: 14px;                                                                                            "+
	"	color: #fff;                                                                                                "+
	"}                                                                                                              "+
	"                                                                                                               "+
	".report-head-detail {                                                                                          "+
	"	height: 12px;                                                                                               "+
	"	line-height: 12px;                                                                                          "+
	"}                                                                                                              "+
	"                                                                                                               "+
	".left-detail {                                                                                                 "+
	"	float: left;                                                                                                "+
	"	margin-left: 12px;                                                                                          "+
	"}                                                                                                              "+
	"                                                                                                               "+
	".right-detail {                                                                                                "+
	"	float: right;                                                                                               "+
	"	margin-right: 12px;                                                                                         "+
	"}                                                                                                              "+
	"                                                                                                               "+
	".report-down {                                                                                                 "+
	"	width: 24px;                                                                                                "+
	"	height: 24px;                                                                                               "+
	"	background-color: #b8c7ce;                                                                                  "+
	"	border-radius: 24px;                                                                                        "+
	"	top: 17px;                                                                                                  "+
	"	right: 12px;                                                                                                "+
	"	position: absolute;                                                                                         "+
	"	cursor: pointer;                                                                                            "+
	"}                                                                                                              "+
	"                                                                                                               "+
	".report-down:before {                                                                                          "+
	"	content: '';                                                                                                "+
	"	width: 14px;                                                                                                "+
	"	height: 14px;                                                                                               "+
	"	background: url(../images/east/img/download.png) no-repeat;                                                             "+
	"	display: inline-block;                                                                                      "+
	"	vertical-align: middle;                                                                                     "+
	"}                                                                                                              "+
	"                                                                                                               "+
	".report-array-tit>.report-array-tit01,                                                                         "+
	".report-array-tit>.report-array-tit02 {                                                                        "+
	"	height: 90px;                                                                                               "+
	"	background-color: #fff;                                                                                     "+
	"	box-shadow: 0px 15px 10px -15px #ccc;                                                                       "+
	"	box-sizing: content-box;                                                                                    "+
	"	overflow: hidden;                                                                                           "+
	"	border-top: 1px solid #EAEAF0;                                                                              "+
	"}                                                                                                              "+
	"                                                                                                               "+
	".report-array-tit>div>h1 {                                                                                     "+
	"	font-size: 24px;                                                                                            "+
	"	line-height: 64px;                                                                                          "+
	"	padding-left: 10px;                                                                                         "+
	"	height: 64px;                                                                                               "+
	"	border-left: 2px solid #E9596F;                                                                             "+
	"	margin-top: 15px;                                                                                           "+
	"	height: 64px;                                                                                               "+
	"	vertical-align: middle;                                                                                     "+
	"}                                                                                                              "+
	"                                                                                                               "+
	".report-array-tit01>h1>span,                                                                                   "+
	".report-array-tit02>h1>span {                                                                                  "+
	"	color: #E9596F;                                                                                             "+
	"	font-size: 28px;                                                                                            "+
	"	height: 64px;                                                                                               "+
	"	line-height: 64px;                                                                                          "+
	"	display: inline-block;                                                                                      "+
	"	font-weight: 400;                                                                                           "+
	"	vertical-align: bottom;                                                                                     "+
	"}                                                                                                              "+
	"                                                                                                               "+
	".report-array-tit02>h1>span {                                                                                  "+
	"	color: #75BEDA;                                                                                             "+
	"}                                                                                                              "+
	"                                                                                                               "+
	".report-array-tit01>h1>span>i,                                                                                 "+
	".report-array-tit02>h1>span>i {                                                                                "+
	"	margin-right: 5px;                                                                                          "+
	"	font-size: 20px;                                                                                            "+
	"	vertical-align: bottom;                                                                                     "+
	"}                                                                                                              "+
	"                                                                                                               "+
	".report-array-box {                                                                                            "+
	"	background-color: #fff;                                                                                     "+
	"}                                                                                                              "+
	"                                                                                                               "+
	".report-vue-list {                                                                                             "+
	"	border: 1px solid #AAB8C1;                                                                                  "+
	"}                                                                                                              "+
	"                                                                                                               "+
	".all-Overview .report-array-content>div {                                                                      "+
	"	height: 300px;                                                                                              "+
	"	margin: 0px 24px;                                                                                           "+
	"	margin-top: 24px;                                                                                           "+
	"}                                                                                                              "+
	"                                                                                                               "+
	".report-array-content {                                                                                        "+
	"	padding-bottom: 24px;                                                                                       "+
	"}                                                                                                              "+
	"                                                                                                               "+
	".report-vue-list>.list-introduce {                                                                             "+
	"	width: 300px;                                                                                               "+
	"	text-align: center;                                                                                         "+
	"	margin-left: 63px;                                                                                          "+
	"}                                                                                                              "+
	"                                                                                                               "+
	".all-Overview .report-vue-list>div {                                                                           "+
	"	width: 50%;                                                                                                 "+
	"	float: left;                                                                                                "+
	"}                                                                                                              "+
	"                                                                                                               "+
	".list-introduce-left>ul {                                                                                      "+
	"	margin-left: 63px;                                                                                          "+
	"	margin-top: 70px;                                                                                           "+
	"	font-size: 16px;                                                                                            "+
	"	line-height: 30px;                                                                                          "+
	"	color: #495A63;                                                                                             "+
	"}                                                                                                              "+
	"                                                                                                               "+
	".list-introduce-left>ul>li>i {                                                                                 "+
	"	color: #E9596F;                                                                                             "+
	"}                                                                                                              "+
	"                                                                                                               "+
	"#doughnut-chart01 {                                                                                            "+
	"	width: 80%;                                                                                                 "+
	"	height: 260px;                                                                                              "+
	"	margin-top: 20px;                                                                                           "+
	"}                                                                                                              "+
	"                                                                                                               "+
	".list-introduce-left>ul.list-left-numberbar {                                                                  "+
	"	margin-top: 106px;                                                                                          "+
	"}                                                                                                              "+
	"                                                                                                               "+
	"#bar-chart01 {                                                                                                 "+
	"	width: 100%;                                                                                                "+
	"	height: 300px;                                                                                              "+
	"}                                                                                                              "+
	"                                                                                                               "+
	".all-Results .report-array-content>div {                                                                       "+
	"	height: 380px;                                                                                              "+
	"	margin: 0px 24px;                                                                                           "+
	"	margin-top: 24px;                                                                                           "+
	"}                                                                                                              "+
	"                                                                                                               "+
	".report-array-tit01,                                                                                           "+
	".report-array-tit02 {                                                                                          "+
	"	position: relative;                                                                                         "+
	"	height: 40px;                                                                                               "+
	"	line-height: 40px;                                                                                          "+
	"}                                                                                                              "+
	"                                                                                                               "+
	".list-sort {                                                                                                   "+
	"	position: absolute;                                                                                         "+
	"	top: 25px;                                                                                                  "+
	"	right: 12px;                                                                                                "+
	"}                                                                                                              "+
	"                                                                                                               "+
	".list-sort>div {                                                                                               "+
	"	float: left;                                                                                                "+
	"	margin-right: 8px;                                                                                          "+
	"	height: 40px;                                                                                               "+
	"	line-height: 40px;                                                                                          "+
	"}                                                                                                              "+
	"                                                                                                               "+
	".list-sort>div input {                                                                                         "+
	"	width: 123px;                                                                                               "+
	"	height: 40px;                                                                                               "+
	"	border-radius: 4px;                                                                                         "+
	"	border: 1px solid #D9DDE1;                                                                                  "+
	"	padding: 0px;                                                                                               "+
	"	padding-left: 5px;                                                                                          "+
	"	font-size: 16px;                                                                                            "+
	"	color: #495A63;                                                                                             "+
	"	font-weight: 400;                                                                                           "+
	"	box-sizing: border-box;                                                                                     "+
	"}                                                                                                              "+
	"                                                                                                               "+
	".list-sort>div input:focus {                                                                                   "+
	"	outline: none;                                                                                              "+
	"}                                                                                                              "+
	"                                                                                                               "+
	".list-change-btn {                                                                                             "+
	"	width: 128px;                                                                                               "+
	"	height: 40px;                                                                                               "+
	"	border: 1px solid #ddd;                                                                                     "+
	"	border-radius: 4px;                                                                                         "+
	"	box-sizing: border-box;                                                                                     "+
	"}                                                                                                              "+
	"                                                                                                               "+
	".list-change-btn>span {                                                                                        "+
	"	display: inline-block;                                                                                      "+
	"	width: 63px;                                                                                                "+
	"	height: 38px;                                                                                               "+
	"	text-align: center;                                                                                         "+
	"	float: left;                                                                                                "+
	"	cursor: pointer;                                                                                            "+
	"	line-height: 38px;                                                                                          "+
	"}                                                                                                              "+
	"                                                                                                               "+
	".change-btn {                                                                                                  "+
	"	background-color: #75BEDA;                                                                                  "+
	"	color: #fff;                                                                                                "+
	"}                                                                                                              "+
	"                                                                                                               "+
	".lool-all-btn {                                                                                                "+
	"	width: 100px;                                                                                               "+
	"	height: 40px;                                                                                               "+
	"	border-radius: 4px;                                                                                         "+
	"	background-color: #75BEDA;                                                                                  "+
	"	color: #fff;                                                                                                "+
	"	text-align: center;                                                                                         "+
	"	line-height: 40px;                                                                                          "+
	"	cursor: pointer;                                                                                            "+
	"}                                                                                                              "+
	"                                                                                                               "+
	".list-up {                                                                                                     "+
	"	position: absolute;                                                                                         "+
	"	top: 8px;                                                                                                   "+
	"	right: 10px;                                                                                                "+
	"	cursor: pointer;                                                                                            "+
	"}                                                                                                              "+
	"                                                                                                               "+
	".list-up:after {                                                                                               "+
	"	content: '▲';                                                                                               "+
	"	color: #919191;                                                                                             "+
	"	width: 12px;                                                                                                "+
	"	height: 12px;                                                                                               "+
	"	line-height: 12px;                                                                                          "+
	"	display: block;                                                                                             "+
	"	text-align: center;                                                                                         "+
	"	font-size: 12px;                                                                                            "+
	"	transform: scale(.8, .8);                                                                                   "+
	"	-ms-transform: scale(.8, .8);                                                                               "+
	"	/* IE 9 */                                                                                                  "+
	"	-moz-transform: scale(.8, .8);                                                                              "+
	"	/* Firefox */                                                                                               "+
	"	-webkit-transform: scale(.8, .8);                                                                           "+
	"	/* Safari 和 Chrome */                                                                                      "+
	"	-o-transform: scale(.8, .8);                                                                                "+
	"	/* Opera */                                                                                                 "+
	"}                                                                                                              "+
	"                                                                                                               "+
	".list-down {                                                                                                   "+
	"	position: absolute;                                                                                         "+
	"	top: 22px;                                                                                                  "+
	"	right: 10px;                                                                                                "+
	"	cursor: pointer;                                                                                            "+
	"}                                                                                                              "+
	"                                                                                                               "+
	".list-down:after {                                                                                             "+
	"	content: '▲';                                                                                               "+
	"	color: #919191;                                                                                             "+
	"	width: 12px;                                                                                                "+
	"	height: 12px;                                                                                               "+
	"	line-height: 12px;                                                                                          "+
	"	display: block;                                                                                             "+
	"	text-align: center;                                                                                         "+
	"	font-size: 12px;                                                                                            "+
	"	transform: rotate(180deg) scale(.8, .8);                                                                    "+
	"	-ms-transform: rotate(180deg) scale(.8, .8);                                                                "+
	"	/* IE 9 */                                                                                                  "+
	"	-moz-transform: rotate(180deg) scale(.8, .8);                                                               "+
	"	/* Firefox */                                                                                               "+
	"	-webkit-transform: rotate(180deg) scale(.8, .8);                                                            "+
	"	/* Safari 和 Chrome */                                                                                      "+
	"	-o-transform: rotate(180deg) scale(.8, .8);                                                                 "+
	"	/* Opera */                                                                                                 "+
	"}                                                                                                              "+
	"                                                                                                               "+
	";                                                                                                              "+
	"                                                                                                               "+
	"}                                                                                                              "+
	".list-sort {                                                                                                   "+
	"	line-height: 42px;                                                                                          "+
	"	height: 40px;                                                                                               "+
	"}                                                                                                              "+
	".change-select {                                                                                               "+
	"	position: relative;                                                                                         "+
	"}                                                                                                              "+
	".all-Results .list-introduce-left {                                                                            "+
	"	width: 306px;                                                                                               "+
	"	height: 350px;                                                                                              "+
	"	margin-left: 30px;                                                                                          "+
	"	margin-top: 30px;                                                                                           "+
	"	position: relative;                                                                                         "+
	"	text-align: center;                                                                                         "+
	"	float: left;                                                                                                "+
	"}                                                                                                              "+
	".introduce-left-tit {                                                                                          "+
	"	display: inline-block;                                                                                      "+
	"	padding: 0px 12px;                                                                                          "+
	"	min-width: 136px;                                                                                           "+
	"	height: 48px;                                                                                               "+
	"	line-height: 48px;                                                                                          "+
	"	color: #fff;                                                                                                "+
	"	background-color: #75BEDA;                                                                                  "+
	"	border-radius: 48px;                                                                                        "+
	"	text-align: center;                                                                                         "+
	"	position: relative;                                                                                         "+
	"	top: 0px;                                                                                                   "+
	"	z-index: 111;                                                                                               "+
	"	font-size: 20px;                                                                                            "+
	"	font-weight: 600;                                                                                           "+
	"}                                                                                                              "+
	".introduce-left-content>ul>li {                                                                                "+
	"	font-size: 16px;                                                                                            "+
	"	line-height: 40px;                                                                                          "+
	"	text-align: left;                                                                                           "+
	"}                                                                                                              "+
	".introduce-left-content>ul>li>span {                                                                           "+
	"	width: 80px;                                                                                                "+
	"	text-align: right;                                                                                          "+
	"	margin-left: 40px;                                                                                          "+
	"}                                                                                                              "+
	".introduce-left-content>ul>li>i {                                                                              "+
	"	float: right;                                                                                               "+
	"	margin-right: 40px;                                                                                         "+
	"}                                                                                                              "+
	".introduce-left-content>ul {                                                                                   "+
	"	margin-top: 40px;                                                                                           "+
	"}                                                                                                              "+
	".introduce-left-content {                                                                                      "+
	"	width: 306px;                                                                                               "+
	"	height: 300px;                                                                                              "+
	"	background-color: #F8FBFF;                                                                                  "+
	"	margin-top: -20px;                                                                                          "+
	"	overflow: hidden;                                                                                           "+
	"}                                                                                                              "+
	".importantColor {                                                                                              "+
	"	color: #E9596F;                                                                                             "+
	"	font-weight: 600;                                                                                           "+
	"}                                                                                                              "+
	".positiveColor {                                                                                               "+
	"	color: #E9596F;                                                                                             "+
	"}                                                                                                              "+
	".negative {                                                                                                    "+
	"	color: #70C6C1;                                                                                             "+
	"}                                                                                                              "+
	".list-vue-right {                                                                                              "+
	"	width: calc(100% - 400px);                                                                                  "+
	"	height: 100%;                                                                                               "+
	"	float: right;                                                                                               "+
	"	position: relative;                                                                                         "+
	"}                                                                                                              "+
	".list-vue-right>div {                                                                                          "+
	"	width: 100%;                                                                                                "+
	"	height: 380px;                                                                                              "+
	"}                                                                                                              "+
	".list-vue-right>i{                                                                                             "+
	"	position: absolute;                                                                                         "+
	"	right: 35px;                                                                                                "+
	"	top: 15px;                                                                                                  "+
	"	z-index: 222;                                                                                               "+
	"    border: 1px solid #D8DCE0;                                                                                 "+
	"    height: 32px;                                                                                              "+
	"    line-height: 32px;                                                                                         "+
	"    width: 60px;                                                                                               "+
	"    text-align: center;                                                                                        "+
	"    border-radius: 16px;                                                                                       "+
	"    cursor: pointer;                                                                                           "+
	"}                                                                                                              "+
	".color3{                                                                                                       "+
	"	min-height: 742px;                                                                                          "+
	"	background-color: #fff;                                                                                     "+
	"}                                                                                                              "+
	".detailed-vue .box-content{                                                                                    "+
	"	height: 534px;                                                                                              "+
	"	width: 809px;                                                                                               "+
	"	margin: 0 auto;                                                                                             "+
	"	position: relative;                                                                                         "+
	"}                                                                                                              "+
	".color3 .box-tit{                                                                                              "+
	"	border-bottom: 1px solid #EAEAF0;                                                                           "+
	"}                                                                                                              "+
	".color3 .box-tit>i{                                                                                            "+
	"	width:24px ;                                                                                                "+
	"	height: 24px;                                                                                               "+
	"	background: url(../images/east/img/close.png) no-repeat;                                                                "+
	"	border: none;                                                                                               "+
	"	margin-top: 15px;                                                                                           "+
	"	cursor: pointer;                                                                                            "+
	"}                                                                                                              "+
	".color3 .box-tit>span{float: right;margin-right: 5px;}                                                         "+
	".color3 .box-tit>span>i.down{                                                                                  "+
	"	width:24px ;                                                                                                "+
	"	height: 24px;                                                                                               "+
	"	background: url(../images/east/img/download.png) no-repeat center #d2dbe0;                                              "+
	"	border: none;                                                                                               "+
	"	margin-top: 15px;                                                                                           "+
	"	cursor: pointer;                                                                                            "+
	"	display: inline-block;                                                                                      "+
	"	border-radius: 24px;                                                                                        "+
	"}                                                                                                              "+
	".color3 .box-tit>span>i.upload{                                                                                "+
	"	width:24px ;                                                                                                "+
	"	height: 24px;                                                                                               "+
	"	background: url(../images/east/img/up1.png) no-repeat center #d2dbe0;                                                   "+
	"	border: none;                                                                                               "+
	"	margin-top: 15px;                                                                                           "+
	"	cursor: pointer;                                                                                            "+
	"	display: inline-block;                                                                                      "+
	"	border-radius: 24px;                                                                                        "+
	"}                                                                                                              "+
	"#validate-rules-chart{width: 100%;height: 460px;}                                                              "+
	"#detail-record-chart{width: 100%;height: 460px;}                                                               "+
	".form-style .el-row{margin-top: 7px;}                                                                          "+
	".form-style .el-col{margin-top: 17px;}                                                                         "+
	".form-style .el-col>input{height: 40px;line-height: 40px;width: 320px;                                         "+
	"outline: none;border: 1px solid #D9DDE1;padding: 0px;padding-left: 5px;                                        "+
	"max-width: 320px;font-size: 14px;color: #313D49;font-family: '微软雅黑';                                       "+
	"}                                                                                                              "+
	".form-style .el-col>span{display: inline-block;width: 100px;font-size: 14px;}                                  "+
	".form-style .dateinput{position: relative;}                                                                    "+
	".form-style .dateinput:after{                                                                                  "+
	"	width: 40px;                                                                                                "+
	"	height: 40px;                                                                                               "+
	"	content: '';                                                                                                "+
	"	display: inline-block;                                                                                      "+
	"	background: url(../images/east/img/date.png) no-repeat;                                                                 "+
	"	background-position: center;                                                                                "+
	"	    position: absolute;                                                                                     "+
	"    left: 400px;                                                                                               "+
	"}                                                                                                              "+
	"                                                                                                               "+
	".input-btn{text-align: center;}                                                                                "+
	".form-style input[type='button']{                                                                              "+
	"	border: none;                                                                                               "+
	"	outline: none;                                                                                              "+
	"	background-color: #75BEDA;                                                                                  "+
	"	min-width: 80px;                                                                                            "+
	"	height: 40px;                                                                                               "+
	"	border-radius: 40px;                                                                                        "+
	"	text-align: center;                                                                                         "+
	"	font-size: 14px;                                                                                            "+
	"	color: #fff;                                                                                                "+
	"	padding: 0px 24px;                                                                                          "+
	"	margin: 10px 12px;                                                                                          "+
	"	cursor: pointer;                                                                                            "+
	"	transition: all .3s linear;                                                                                 "+
	"}                                                                                                              "+
	".form-style input[type='button']:hover{box-shadow: 0px 0px 8px #75BEDA;}                                       "+
	"table.table-detail{border-collapse: collapse;border: 2px solid #AAB8C1;width: 100%;margin-top: 15px;}          "+
	"table.table-detail>thead>tr{background-color: #AAB8C1;color: #fff;}                                            "+
	"table.table-detail tr{height: 48px;line-height: 40px;                                                          "+
	"    text-align: left;}                                                                                         "+
	"table.table-detail tbody tr{border-bottom: 1px solid #EAEAF0;transition: all .3s linear;cursor: pointer;}      "+
	"table.table-detail tr td{border: none;padding: 0px;text-align: center;font-size: 14px;}                        "+
	"table.table-detail tr td:first-child{text-align: left;padding-left: 20px;}                                     "+
	"table.table-detail tbody tr:hover{background-color: #F2F4F8;}                                                  "+
	"/*.addfocus{background-color: #F2F4F8;}*/                                                                      "+
	".table-pagebtn{text-align: right;padding: 17px 20px 26px 0px;}                                                 "+
	".table-pagebtn>input[type='button']{                                                                           "+
	"	min-width: 48px;                                                                                            "+
	"    height: 28px;                                                                                              "+
	"    border: none;                                                                                              "+
	"    outline: none;                                                                                             "+
	"    border-radius: 4px;                                                                                        "+
	"    display: inline-block;                                                                                     "+
	"    line-height: 24px;                                                                                         "+
	"    text-align: center;                                                                                        "+
	"    vertical-align: middle;                                                                                    "+
	"    margin-left: 5px;                                                                                          "+
	"    cursor: pointer;                                                                                           "+
	"    transition: all .3s linear;                                                                                "+
	"}                                                                                                              "+
	".table-pagebtn>input[type='button'].prohibitbtn{                                                               "+
	"	background-color: #eaedef;                                                                                  "+
	"	color: #667C89;                                                                                             "+
	"	cursor: not-allowed;                                                                                        "+
	"}                                                                                                              "+
	".table-pagebtn>input[type='button']:hover{background-color: #9CAAB3;                                           "+
	"	color: #fff;                                                                                                "+
	"	}                                                                                                           "+
	".table-pagebtn>input[type='button'].prohibitbtn:hover{                                                         "+
	"	background-color: #eaedef;                                                                                  "+
	"	color: #667C89;                                                                                             "+
	"	box-shadow: none;                                                                                           "+
	"	                                                                                                            "+
	"}                                                                                                              "+
	".table-pagebtn>input[type='button'].changebtn{                                                                 "+
	"	background-color: #9CAAB3;                                                                                  "+
	"	color: #fff;                                                                                                "+
	"}                                                                                                              "+
	".table-pagebtn>input[type='button'].changebtn:hover{                                                           "+
	"	box-shadow: 0px 0px 8px 0px #d9d9d9;                                                                        "+
	"}                                                                                                              "+
	".rankingicon{                                                                                                  "+
	"	content: '▲';                                                                                               "+
	"	color: #fff;                                                                                                "+
	"    width: 12px;                                                                                               "+
	"    height: 12px;                                                                                              "+
	"    line-height: 12px;                                                                                         "+
	"    display: inline-block;                                                                                     "+
	"    text-align: center;                                                                                        "+
	"    font-size: 12px;                                                                                           "+
	"    transform: rotate(180deg) scale(0.8, 0.8);                                                                 "+
	"    cursor: pointer;                                                                                           "+
	"}                                                                                                              "+
	".mustwrite{position: relative;}                                                                                "+
	".mustwrite:before{                                                                                             "+
	"	    content: '*';                                                                                           "+
	"	    display: inline-block;                                                                                  "+
	"    position: absolute;                                                                                        "+
	"    left: 455px;                                                                                               "+
	"    color: red;                                                                                                "+
	"    line-height: 40px;                                                                                         "+
	"    height: 40px;                                                                                              "+
	"}                                                                                                              "+
	"table.table-detail tbody tr td>span{                                                                           "+
	"	display: inline-block;                                                                                      "+
	"    padding: 0px 10px;                                                                                         "+
	"    border: 1px solid transparent;                                                                             "+
	"    border-radius: 6px;                                                                                        "+
	"    height: 30px;                                                                                              "+
	"    line-height: 30px;                                                                                         "+
	"    transition: all .3s linear;                                                                                "+
	"}                                                                                                              "+
	"table.table-detail tbody tr td>span:hover{                                                                     "+
	"	 border: 1px solid #AAB8C1;                                                                                 "+
	"}                                                                                                              "+

	"</style></head>"+
	"<body>"+
	"<div id='pdf' class='content_box'>                                                                                "+
	"	<div id='report-head' class='report-head'>                                                                             "+
	"		<h3>"+str_report_name+"</h3>                                                                      "+
	"		<p class='report-head-detail'>                                                                    "+
	"			<span class='left-detail'>报告编号：<i>"+str_report_id+"</i></span>                              "+
	"			<span class='middle-detail'>期数：<i>"+str_report_date+"</i></span>                             "+
	"			<span class='right-detail'>报告时间：<i>"+str_data_time+"</i></span>                        	  "+
	"		</p>                                                                                              "+
	"		<div id='report-down' class='report-down'></div>                                                                   "+
	"	</div>                                                                                                "+
	"	<div class='report-array-box all-Overview'>                                                           "+
	"		<div class='report-array-tit'>                                                                    "+
	"			<div class='report-array-tit01'>                                                              "+
	"				<h1><span>01 <i>X</i></span>报告总览</h1>                                                    "+
	"			</div>                                                                                        "+
	"			<div class='report-array-content'>                                                            "+
	"				<div class='report-vue-list'>                                                             "+
	"					<div class='list-introduce-left'>                                                     "+
	"					 <ul>                                                                                 "+
	"						<li>本期数据检核范围共包含 <i>59</i>个数据接口</li>                               			  "+
	"						<li>检核总记录数 <i>"+str_total_count+"</i>条</li>                                      "+
	"						<li>校验不通过记录数 <i>"+str_fail_count+"</i>条</li>                                    "+
	"						<li>校验不通过率为 <i>"+str_fail_rate+"</i></li>                                        "+
	"						<li>环比<b>"+str_mom_ratio+"</b>，同比<b>"+str_yoy_ratio+"</b></li>                   "+
	"					 </ul>                                                                                "+
	"					</div>                                                                                "+
	"					<div class='list-vue-right'>                                                          "+
	"						<div id='doughnut-chart01'></div>                                                 "+
	"					</div>                                                                                "+
	"				</div>                                                                                    "+
	"				<div class='report-vue-list'>                                                             "+
	"					<div class='list-introduce-left'>                                                     "+
	"					 <ul class='list-left-numberbar'>                                                     "+
	"						<li>本期数据检核执行了 <i>"+str_rule_type_count+"</i>类校验</li>                           "+
	"						<li>共计 <i>"+str_rule_count+"</i>条校验规则</li>                                      "+
	"					 </ul>                                                                                "+
	"					</div>                                                                                "+
	"					<div class='list-vue-right'>                                                          "+
	"						<div id='bar-chart01'></div>                                                      "+
	"					</div>                                                                                "+
	"				</div>                                                                                    "+
	"			</div>                                                                                        "+
	"		</div>                                                                                            "+
	"	</div>                                                                                                "+
	"	<div class='report-array-box all-Results'>                                                            "+
	"		<div class='report-array-tit'>                                                                    "+
	"			<div class='report-array-tit02'>                                                              "+
	"				<h1><span>02 <i>X</i></span>数据接口检核结果</h1>                                         "+
	"				<div class='list-sort'>                                                                   "+
	"					<div>排序：</div>                                                                     "+
	"					<div class='change-select'>                                                           "+
	"						<input type='text' value='错误记录数' />                                          "+
	"						<i class='list-up'></i>                                                           "+
	"						<i class='list-down'></i>                                                         "+
	"					</div>                                                                                "+
	//	"					<div class='list-change-btn'>                                                         "+
	//	"						<span class='change-btn'>升序</span>                                              "+
	//	"						<span>降序</span>                                                                 "+
	//	"					</div>                                                                                "+
	"					<div class='lool-all-btn'>查看全部</div>       										  "+
	"				</div>                                                                                    "+
	"			</div>                                                                                        "+
	"			<div class='report-array-content'>                                                            ";
	return str_html;
}
function openMoreColoum(tabName) {
	top.addTabs({
        id: 'e2a85310f09f4b0da42200a94ecb660f',
        title: '检核结果核对',
        close: true,
        url: '/frs/yufreejs?js=/yujs/east/result/ruleresultcheck.js&OpenPars={"data_dt":"'+str_report_date+'","tab_name":"'+tabName+'","org_ot":"'+str_org_no+'"}',
        urlType: "relative"
    });
}

function openMoreColoumAll() {
	top.addTabs({
        id: 'e2a85310f09f4b0da42200a94ecb660f',
        title: '检核结果核对',
        close: true,
        url: '/frs/yufreejs?js=/yujs/east/result/ruleresultcheck.js&OpenPars={"data_dt":"'+str_report_date+'","org_ot":"'+str_org_no+'"}',
        urlType: "relative"
    });
}

function load(jsy_tabs) {
	$("#d1").css("overflow", "auto");
 	$("#report-down").bind('click', function() {
 		html2canvas($("#pdf"), {
 		    background: "#fff",
 		    allowTaint: true,//允许跨域
 		    taintTest: false,//是否在渲染前测试图片
 		    dpi: 300,
 		    onrendered:function(canvas) {
 		        var contentWidth = canvas.width;//pdf整个的宽度
 		        var contentHeight = canvas.height;//pdf整个的高度
 		        var pageHeight = contentWidth / 592.28 * 841.89;//一页pdf显示html页面生成的canvas高度;
 		        var leftHeight = contentHeight;//未生成pdf的html页面高度
 		        var position = 0;//pdf页面偏移
 		        var imgWidth = 595.28;//html页面生成的canvas在pdf中图片的宽
 		        var imgHeight = 592.28/contentWidth * contentHeight;//html页面生成的canvas在pdf中图片的高
 		        var pageData = canvas.toDataURL('image/jpeg', 1.0);
 		        var img = new Image();
 		        img.src = pageData;
 		        var pdf = new jsPDF('p', 'pt', 'a4');
 		        img.onload = function() {
 		        	//有两个高度需要区分，一个是html页面的实际高度，和生成pdf的页面高度(841.89)
                    //当内容未超过pdf一页显示的范围，无需分页
 		            if (leftHeight < pageHeight) {
 		                pdf.addImage(pageData, 'JPEG', 0, 0, imgWidth, imgHeight );
 		            } else {
 		                while(leftHeight > 0) {
 		                    pdf.addImage(pageData, 'JPEG', 0, position, imgWidth, imgHeight)
 		                    leftHeight -= pageHeight;
 		                    position -= 841.89;
 		                    if(leftHeight > 0) {
 		                        pdf.addPage();
 		                    }
 		                }
 		            }
 		            pdf.save('east数据质量报表.pdf');
 		        }
 		    },
 		})
	});
 	setCharts();
 	setCharts1();
 	setCharts2(jsy_tabs);
 };
 
 var setCharts = function() {
 	var mycarts = document.getElementById('doughnut-chart01');
 	var myCharts1 = echarts.init(mycarts);
 	var option1 = {
 		series: [{
 			type: 'pie',
 			radius: ['50%', '70%'],
 			avoidLabelOverlap: true,
 			label: {
 				normal: {
 					show: true
 				},
 				emphasis: {
 					show: true,
 					textStyle: {
 						fontSize: '18',
 						fontWeight: 'bold'
 					}
 				}
 			},
 			labelLine: {
 				normal: {
 					show: true
 				}
 			},
 			data: [{
 					value: 110,
 					name: '不通过率'+str_fail_rate+'%',
 					itemStyle: {
 						normal: {
 							color: '#E9596F'
 						}
 					}
 				},
 				{
 					value: 310,
 					name: '通过率'+str_pass_rate+'%',
 					itemStyle: {
 						normal: {
 							color: '#ddd'
 						}
 					}
 				}
 			]
 		}]
 	};
 	myCharts1.setOption(option1);
 }
 
 var setCharts1 = function() {
 	var mycarts = document.getElementById('bar-chart01');
 	var myCharts1 = echarts.init(mycarts);
 	var option2 = {
 		tooltip: {
 			trigger: 'axis',
 			axisPointer: {
 				type: 'shadow'
 			}
 		},
 		grid: {
 			top:20,
 			left: '3%',
 			right: 70,
 			bottom: 10,
 			containLabel: true
 		},
 		xAxis: {
 			type: 'value',
 			axisTick:{
						show:false
					},
 			axisLine: {
						show: true,
						lineStyle: {
							color: '#DEDEDE',
							type: 'solid'
						}
					},
					axisLabel: {
				       show: true,
				       margin:10,
				       textStyle: {
				          color: '#313D49',  //更改坐标轴文字颜色
				          fontSize : 12      //更改坐标轴文字大小
				        }
			}
 		},
 		yAxis: {
 			type: 'category',
 			show: true,
					axisTick:{
						show:false
					},
					axisLabel: {
				       show: true,
				       margin:20,
				       textStyle: {
				          color: '#313D49',  //更改坐标轴文字颜色
				          fontSize : 12      //更改坐标轴文字大小
				        }
				    },
					axisLine: {
						show: false,
						lineStyle: {
							color: '#DEDEDE',
							type: 'solid'
						}
					},
					 splitLine:{  
				            show:false,
				            lineStyle:{
							    color: '#DEDEDE',
							    type: 'solid',
							    width: 1
							}
				   },
 			data: type_cd
 		},
 		series: [{
 				name: '',
 				type: 'bar',
 				barWidth: '12',
 				itemStyle: {
						normal: {
							color: '#75BEDA'
						}
					},
 				data: type_cd_count
 			}
 		]
 	};
 	myCharts1.setOption(option2);
 }
 
 var setCharts2 = function(js_tabs) {
	 for (var s=0;s<js_tabs.length;s++) {
		 var col_name_arr = [];
		 var col_value_arr = [];
		 
		 var tab_name = js_tabs[s].tab_name;
		 var js_tab_cols = JSPFree.getHashVOs("select * from (select tab_name, col_name, sum(fail_count) failcount, sum(total_count) totalcount from east_check_result_col where busi_type like '"+busi_type+"%' and tab_name='"+tab_name+"' and data_dt = '"+str_report_date+"' group by tab_name,col_name order by tab_name,sum(fail_count) desc)t where ROWNUM <=5");
		 for (var k=0;k<js_tab_cols.length;k++) {
			 col_name_arr.push(js_tab_cols[k].col_name);
			 col_value_arr.push(js_tab_cols[k].failcount);
		 }

		 var mycarts = document.getElementById('bar-chart'+s);
		 if(mycarts){
			 var myCharts1 = echarts.init(mycarts);
			 var option2 = {
				title: {
					show:true,
					text:"字段错误记录数TOP5",
					textStyle:{
						color:"#495A63",
						fontSize:16
					},
				left:'center',
				top:30
				},
				tooltip: {
					trigger: 'axis',
					axisPointer: {
						type: 'shadow'
					}
				},
				legend: {
					textStyle: {
						color: '#666'
					}
				},
				grid: {
					top: '70',
					left: '20',
					right: '50',
					bottom: '30',
					containLabel: true
				},
				textStyle: {
					color: '#888'
				},
				xAxis: {
					show: true,
					type: 'category',
					data: col_name_arr,
					axisLine: {
						show: false,
						lineStyle: {
							color: '#DEDEDE',
							type: 'dashed'
						}
					},
					axisLabel: {
				       show: true,
				       margin:20,
				       textStyle: {
				          color: '#495A63',  //更改坐标轴文字颜色
				          fontSize : 14      //更改坐标轴文字大小
				        }
				     }
				},
				yAxis: {
					show: true,
					type: 'value',
					name: '单位：个',
					nameTextStyle:{
						align:'right'
					},
					axisTick:{
						show:false
					},
					axisLabel: {
				       show: true,
				       margin:20,
				       textStyle: {
				          color: '#495A63',  //更改坐标轴文字颜色
				          fontSize : 14      //更改坐标轴文字大小
				        }
				    },
					axisLine: {
						show: false,
						lineStyle: {
							color: '#DEDEDE',
							type: 'dashed'
						}
					},
					 splitLine:{  
				            show:true,
				            lineStyle:{
							    color: '#DEDEDE',
							    type: 'dashed',
							    width: 1
							}
				    }
				},
				series: [{
						type: 'bar',
						barWidth: '24',
						data: col_value_arr,
						itemStyle: {
							normal: {
								color: '#3F88DE'
							}
						}
					}
				]
			};
			myCharts1.setOption(option2);
		 }
	 }
}

function dataFormat(value) {
	// 20200225
	value = value.slice(0,4)+'-'+value.slice(4,6)+'-'+value.slice(6,8);
	
 	return value;
}
