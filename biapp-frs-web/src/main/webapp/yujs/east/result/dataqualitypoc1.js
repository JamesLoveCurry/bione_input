//初始化界面,菜单配置路径是【/frs/yufreejs?js=/yujs/east/dataqualitypoc.js】
function AfterInit(){
	document.getElementById("d1").innerHTML="<head>																"+															
"<style type='text/css'>																						"+
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
	"		<h3>xx银行全行数据质量报告</h3>                                                                   "+
	"		<p class='report-head-detail'>                                                                    "+
	"			<span class='left-detail'>报告编号：<i>201904300000125</i></span>                             "+
	"			<span class='middle-detail'>期数：<i>20190430</i></span>                                      "+
	"			<span class='right-detail'>报告时间：<i>2019-05-01 12:25:30</i></span>                        "+
	"		</p>                                                                                              "+
	"		<div id='report-down' class='report-down'></div>                                                                   "+
	"	</div>                                                                                                "+
	"	<div class='report-array-box all-Overview'>                                                           "+
	"		<div class='report-array-tit'>                                                                    "+
	"			<div class='report-array-tit01'>                                                              "+
	"				<h1><span>01 <i>X</i></span>报告总览</h1>                                                 "+
	"			</div>                                                                                        "+
	"			<div class='report-array-content'>                                                            "+
	"				<div class='report-vue-list'>                                                             "+
	"					<div class='list-introduce-left'>                                                     "+
	"					 <ul>                                                                                 "+
	"						<li>本期数据检核范围共包含 <i>58</i>个数据接口</li>                               "+
	"						<li>检核总记录数 <i>1,865,803,681</i>条</li>                                      "+
	"						<li>校验不通过记录数 <i>279,870,552</i>条</li>                                    "+
	"						<li>校验不通过率为 <i>15%</i></li>                                                "+
	"						<li>环比<b>增加<i>3%</i></b>，同比<b>下降<i>4%</i></b></li>                       "+
	"					 </ul>                                                                                "+
	"					</div>                                                                                "+
	"					<div class='list-vue-right'>                                                          "+
	"						<div id='doughnut-chart01'></div>                                                 "+
	"					</div>                                                                                "+
	"				</div>                                                                                    "+
	"				<div class='report-vue-list'>                                                             "+
	"					<div class='list-introduce-left'>                                                     "+
	"					 <ul class='list-left-numberbar'>                                                     "+
	"						<li>本区数据检核执行了 <i>9</i>类校验</li>                                        "+
	"						<li>共计 <i>2567</i>条校验规则</li>                                               "+
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
	"					<div class='list-change-btn'>                                                         "+
	"						<span class='change-btn'>升序</span>                                              "+
	"						<span>降序</span>                                                                 "+
	"					</div>                                                                                "+
	"					<div class='lool-all-btn'>查看全部</div>                                              "+
	"				</div>                                                                                    "+
	"			</div>                                                                                        "+
	"			<div class='report-array-content'>                                                            "+
	"				<div class='report-vue-list'>                                                             "+
	"					<div class='list-introduce-left'>                                                     "+
	"					  <div class='introduce-left-tit'>交易流水</div>                                      "+
	"					  <div class='introduce-left-content'>                                                "+
	"						<ul>                                                                              "+
	"							<li><span>总记录数：</span><i>1,253,013,664 </i></li>                         "+
	"							<li><span>错误记录数:</span><i class='importantColor'>15,712,791</i></li>     "+
	"							<li><span>错误率:</span><i>1.254%</i></li>                                    "+
	"							<li><span>环比:</span><i class='positiveColor'>+ 9.57%</i></li>               "+
	"							<li><span>同比:</span><i class='negative'>- 33.14%</i></li>                   "+
	"						</ul>                                                                             "+
	"					  </div>                                                                              "+
	"					</div>                                                                                "+
	"					<div class='list-vue-right'>                                                          "+
	"						<div id='bar-chart02'></div>                                                      "+
	"						<i>更多</i>                                                                       "+
	"					</div>                                                                                "+
	"				</div>                                                                                    "+
	"				<div class='report-vue-list'>                                                             "+
	"					<div class='list-introduce-left'>                                                     "+
	"					  <div class='introduce-left-tit'>内部分户账户明细记录</div>                          "+
	"					  <div class='introduce-left-content'>                                                "+
	"						<ul>                                                                              "+
	"							<li><span>总记录数：</span><i>1,253,013,664 </i></li>                         "+
	"							<li><span>错误记录数:</span><i class='importantColor'>15,712,791</i></li>     "+
	"							<li><span>错误率:</span><i>1.254%</i></li>                                    "+
	"							<li><span>环比:</span><i class='positiveColor'>+ 9.57%</i></li>               "+
	"							<li><span>同比:</span><i class='negative'>- 33.14%</i></li>                   "+
	"						</ul>                                                                             "+
	"					  </div>                                                                              "+
	"					</div>                                                                                "+
	"					<div class='list-vue-right'>                                                          "+
	"						<div id='bar-chart03'></div>                                                      "+
	"						<i>更多</i>                                                                       "+
	"					</div>                                                                                "+
	"				</div>                                                                                    "+
	"				<div class='report-vue-list'>                                                             "+
	"					<div class='list-introduce-left'>                                                     "+
	"					  <div class='introduce-left-tit'>个人活期存款分户账明细记录</div>                    "+
	"					  <div class='introduce-left-content'>                                                "+
	"						<ul>                                                                              "+
	"							<li><span>总记录数：</span><i>1,253,013,664 </i></li>                         "+
	"							<li><span>错误记录数:</span><i class='importantColor'>15,712,791</i></li>     "+
	"							<li><span>错误率:</span><i>1.254%</i></li>                                    "+
	"							<li><span>环比:</span><i class='positiveColor'>+ 9.57%</i></li>               "+
	"							<li><span>同比:</span><i class='negative'>- 33.14%</i></li>                   "+
	"						</ul>                                                                             "+
	"					  </div>                                                                              "+
	"					</div>                                                                                "+
	"					<div class='list-vue-right'>                                                          "+
	"						<div id='bar-chart04'></div>                                                      "+
	"						<i>更多</i>                                                                       "+
	"					</div>                                                                                "+
	"				</div>                                                                                    "+
	"				<div class='report-vue-list'>                                                             "+
	"					<div class='list-introduce-left'>                                                     "+
	"					  <div class='introduce-left-tit'>信用卡账户交易明细记录</div>                        "+
	"					  <div class='introduce-left-content'>                                                "+
	"						<ul>                                                                              "+
	"							<li><span>总记录数：</span><i>1,253,013,664 </i></li>                         "+
	"							<li><span>错误记录数:</span><i class='importantColor'>15,712,791</i></li>     "+
	"							<li><span>错误率:</span><i>1.254%</i></li>                                    "+
	"							<li><span>环比:</span><i class='positiveColor'>+ 9.57%</i></li>               "+
	"							<li><span>同比:</span><i class='negative'>- 33.14%</i></li>                   "+
	"						</ul>                                                                             "+
	"					  </div>                                                                              "+
	"					</div>                                                                                "+
	"					<div class='list-vue-right'>                                                          "+
	"						<div id='bar-chart05'></div>                                                      "+
	"						<i>更多</i>                                                                       "+
	"					</div>                                                                                "+
	"				</div>                                                                                    "+
	"				<div class='report-vue-list'>                                                             "+
	"					<div class='list-introduce-left'>                                                     "+
	"					  <div class='introduce-left-tit'>个人基础信息</div>                                  "+
	"					  <div class='introduce-left-content'>                                                "+
	"						<ul>                                                                              "+
	"							<li><span>总记录数：</span><i>1,253,013,664 </i></li>                         "+
	"							<li><span>错误记录数:</span><i class='importantColor'>15,712,791</i></li>     "+
	"							<li><span>错误率:</span><i>1.254%</i></li>                                    "+
	"							<li><span>环比:</span><i class='positiveColor'>+ 9.57%</i></li>               "+
	"							<li><span>同比:</span><i class='negative'>- 33.14%</i></li>                   "+
	"						</ul>                                                                             "+
	"					  </div>                                                                              "+
	"					</div>                                                                                "+
	"					<div class='list-vue-right'>                                                          "+
	"						<div id='bar-chart06'></div>                                                      "+
	"						<i>更多</i>                                                                       "+
	"					</div>                                                                                "+
	"				</div>                                                                                    "+
	"			</div>                                                                                        "+
	"		</div>                                                                                            "+
	"	</div>                                                                                                "+
	"</div>                                                                                                   "+
	"</body>";
load();
}

function load() {
	$("#d1").css("overflow", "auto");
 	setCharts();
 	setCharts1();
 	setCharts2();
 	setCharts3();
 	setCharts4();
 	setCharts5();
 	setCharts6();
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
 };
 var setCharts = function() {
 	var mycarts = document.getElementById('doughnut-chart01');
 	var myCharts1 = echarts.init(mycarts);
 	var option1 = {
 		series: [{
 			type: 'pie',
 			radius: ['50%', '75%'],
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
 					name: '不通过率 15.22%',
 					itemStyle: {
 						normal: {
 							color: '#E9596F'
 						}
 					}
 				},
 				{
 					value: 310,
 					name: '通过率 86.88%',
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
 			data: ['跨监管一致性', '总分核对', '记录数校验', '脱敏校验', '表内逻辑性', '表间逻辑性', '格式校验', '取值范围校验', '非空校验']
 		},
 		series: [{
 				name: '2011年',
 				type: 'bar',
 				barWidth: '12',
 				itemStyle: {
							normal: {
								color: '#75BEDA'
							}
					},
 				data: [120, 24, 43, 20, 223, 200,245,356,1500]
 			}
 		]
 	};
 	myCharts1.setOption(option2);
 }
 var setCharts2 = function() {
			var mycarts = document.getElementById('bar-chart02');
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
					data: ['交易介质号', '对方账号', '对方户名', '交易渠道', '对方行号'],
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
					name: '单位：万',
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
						data: [2000, 1000, 750, 1250, 900],
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
 var setCharts3 = function() {
			var mycarts = document.getElementById('bar-chart03');
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
					data: ['对方账号', '对方户名', '对方行号', '明细科目名称', '交易类型'],
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
					name: '单位：万',
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
						data: [1200, 600, 450, 750, 500],
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
 var setCharts4 = function() {
			var mycarts = document.getElementById('bar-chart04');
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
					data: ['账户名称', '交易类型', '对方户名', '业务办理机构号  ', '代办人姓名'],
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
					name: '单位：万',
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
						data: [800, 400, 300, 500, 380],
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
 var setCharts5 = function() {
			var mycarts = document.getElementById('bar-chart05');
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
					data: ['工作站编号', '交易机构号', '对方账号', '卡片交易类型', '交易柜员号'],
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
					name: '单位：万',
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
						data: [600, 300, 225, 375, 180],
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
 var setCharts6 = function() {
			var mycarts = document.getElementById('bar-chart06');
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
					data: ['来源系统', '民族', '职业', '家庭住址', '单位性质'],
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
					name: '单位：万',
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
						data: [300, 150, 110, 190, 140],
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