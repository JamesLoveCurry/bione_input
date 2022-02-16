<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<%@ include file="/common/taglibs.jsp"%>
<html>
<head>
<link rel="stylesheet" type="text/css"
	href="${ctx}/css/classics/ligerui-custom.css" />
<link rel="stylesheet" type="text/css"
	href="${ctx}/css/classics/template/common_BS.css" />
<link rel="stylesheet" type="text/css"
	href="${ctx}/js/rcswitcher/rcswitcher.min.css" />
<link rel="stylesheet" type="text/css"
	href="${ctx}/images/classics/icons/icon/style.css" />
<link rel="stylesheet" type="text/css"
	href="${ctx}/images/classics/icons/icon/style.extend.css" />
<script type="text/javascript" src="${ctx}/js/echarts/js/jquery.min.js"></script>	
<script type="text/javascript"
	src="${ctx}/js/rcswitcher/rcswitcher.min.js"></script>
<script type="text/javascript" src="${ctx}/js/ligerUI/ligerui_BS.all.min.js"></script>
<script type="text/javascript" src="${ctx}/js/ligerUI/ligerui.expand.min.js"></script>
<script type="text/javascript" src="${ctx}/js/bione/BIONE_BS.min.js"></script>
<script type="text/javascript" src="${ctx}/js/echarts/js/echarts.min.js"></script>
<script type="text/javascript" src="${ctx}/js/format/date-format.js"></script>
<script type="text/javascript" src="${ctx}/js/html2canvas/html2canvas.min.js"></script>
<script type="text/javascript" src="${ctx}/js/jspdf/jspdf.min.js"></script>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<style type='text/css'>
	body, ul,li,p, h1,h2,h3,h4,h5 { 
		margin: 0px; 
		padding: 0px;
	}
	i {
		font-style: normal;
	}
	body {
		background-color: #EFF5F9; 
		font-weight: 400;
		font-family: '微软雅黑'; 
	}
	li { 
		list-style: none;
	}
	.content_box {
		padding: 24px; 
		color: #495A63;
	}
	.el-row { 
		margin-left: -12px;
		margin-right: -12px; 
		margin-bottom: 24px; 
		overflow: hidden;
	}
	.el-row:after { 
		clear: both; 
	}
	.el-col { 
		padding: 0px 12px; 
		display: block;
		box-sizing: border-box;
		float: left; 
	}
	.el-col-3 { 
		width: 25%;
	}
	.el-col-6 { 
		width: 50%;
	}
	.el-col-3 img { 
		width: 48px; 
		height: 48px;
		margin-top: 20px;
	}
	.el-col>div { 
		background: #fff;
		box-shadow: 0px 0px 8px 0px #d9d9d9; 
		border-radius: 4px;
		position: relative;
	}
	.el-col-3>div { 
		width: 100%; 
		height: 120px; 
		text-align: center;
	}
	.el-col-6>div { 
		width: 100%; 
	}
	.el-col-3>div>i { 
		font-size: 16px; 
		font-weight: 400;
		display: block;
		line-height: 40px; 
		height: 40px;
	}
	.col-box {
		padding: 0px 12px; 
	}
	.box-tit {
		height: 58px;
		line-height: 58px; 
		font-size: 16px; 
	}
	.color1,.color2,.color3 { 
		border-top: solid 2px; 
		box-sizing: border-box;
		
	}
	.color1 { 
		border-color: #5BC0DD; 
	}
	.color2 { 
		border-color: #1B88E5; 
	}
	.color3 { 
		border-color: #75BEDA; 
		box-shadow: 0px 0px 8px 0px #D9D9D9; 
	}
	.box-tit>i {
		float: right;
		border: 1px solid #D8DCE0; 
		height: 32px;
		line-height: 32px; 
		margin-top: 12px;
		width: 60px; 
		text-align: center;
		border-radius: 16px; 
	}
	.box-content>ul>li {
		height: 40px;
		line-height: 40px; 
		border-top: 1px solid #EAEAF0; 
		box-sizing: border-box;
		cursor: pointer; 
	}	 
	.box-content>ul>li>span { 
		width: 345px;
		height: 40px; 
		white-space: nowrap;
		text-overflow: ellipsis;
		overflow: hidden; 
		float: left;
	}
	.box-content>ul>li>b {
		color: #667C89;
		float: right; 
		margin-right: 10px; 
	}
	.box-content>ul>li>i {
		font-size: 12px;
		padding: 0px 6px; 
		background-color: #99aab4;
		color: #fff;
		vertical-align: middle; 
		margin-right: 5px;
		float: left;
		height: 20px; 
		line-height: 20px;
		margin-top: 10px; 
	}
	.box-content>ul>li>i.no-read {
		background-color: #f7b132; 
	}
	.show-box-tit { 
		padding: 0px 12px; 
		height: 24px;
		line-height: 24px; 
		vertical-align: middle;
		font-size: 16px; 
		margin-bottom: 20px; 
	}
	.show-box-tit>img { 
		vertical-align: middle;
		margin-right: 10px;
	}
	.box-content>span { 
		color: #667C89;
		font-size: 12px; 
		height: 12px;
		line-height: 12px; 
		display: block;
	}
	.show-pic { 
		font-size: 24px; 
	}
	.show-pic>p { 
		color: #667C89;
		height: 24px;
		line-height: 24px; 
		margin-top: 36px;
	}
	.show-pic>p>span {
		color: #495A63;
		display: inline-block; 
	}
	.show-pic>p>span.num-light {
		color: #75BEDA;
	}
	.show-pic>p>span.num-all {
		margin-left: 5px;
	}
	.show-pic>p>b { 
		color: #fff; 
		background-color: #75BEDA; 
		font-size: 14px; 
		padding: 3px 10px; 
		border-radius: 14px; 
		float: right;
		height: 18px;
		line-height: 18px; 
	}
	.show-pic>p>b:after { 
		content: '%';
	}
	.num-line { 
		width: 100%; 
		height: 12px;
		border-radius: 12px; 
		background-color: #f1f4f9; 
		margin-top: 15px;
	}
	.num-line>div { 
		height: 12px;
		background-color: #75BEDA; 
		border-radius: 12px; 
	}
	.compare {
		border-top: 1px solid #ececec; 
		overflow: hidden;
		height: 64px;
		position: absolute;
		bottom: 0px; 
		width: 100%; 
		margin: 0px -12px; 
	}
	.compare>div {
		float: left; 
		width: 50%;
		height: 64px;
		font-size: 14px; 
		text-align: center;
		padding-top: 15px; 
		box-sizing: border-box;
		position: relative;
		line-height: 20px; 
	}
	.compare>div:first-child {
		border-right: 1px solid #EAEAF0; 
	}
	.up {
		color: #E85168;
	}
	.down { 
		color: #63C2BC;
	}
	.compare>div:before { 
		content: ''; 
		display: inline-block; 
		height: 10px;
		width: 12px; 
		margin-right: 10px;
	}
	.compare>div.up:before {
		background: url(../images/east/img/up.png) transparent no-repeat; 
	}
	.compare>div.down:before {
		background: url(../images/east/img/down.png) transparent no-repeat;
	}
	.compare>div>span { 
		display: block;
		color: #495A63;
	}
	#doughnut-chart { 
		width: 180px;
		height: 180px; 
		float: right;
	}
	.box-content>h3 { 
		color: #E9596F;
		float: left; 
		margin-top: 70px;
		margin-left: 75px; 
		font-size: 28px; 
		font-weight: 400;
	}
	.box-content>h3>i { 
		font-size: 14px; 
	}
	.box-content.ranking>ul>li {
		height: 65px;
		line-height: 65px; 
		box-sizing: border-box;
		border-left: 2px solid transparent;
		cursor: pointer; 
	}
	.box-content.ranking>ul>li:hover {
		border-left: 2px solid #2288e4;
		box-sizing: border-box;
	}
	.box-content.ranking>ul>li>span { 
		width: 56px; 
		color: #fff; 
		height: 24px;
		background: #9CAAB3; 
		border-radius: 4px;
		display: inline-block; 
		line-height: 24px; 
		text-align: center;
		vertical-align: middle;
		margin-left: 20px; 
		margin-top: 20px;
	}
	.box-content.ranking>ul>li:first-child>span { 
		background: #E9596F; 
	}
	.box-content.ranking>ul>li:nth-child(2)>span {
		background: #F4B44B; 
	}
	.box-content.ranking>ul>li:nth-child(3)>span {
		background: #7064E6; 
	}
	.ranking-detail { 
		display: inline-block; 
		margin-left: 30px; 
		height: 60px;
		line-height: 60px; 
		position: relative;
		width: 180px;
		vertical-align: middle;
	}
	.ranking-detail>span {
		font-size: 18px; 
		display: inline-block; 
		height: 30px;
		line-height: 30px; 
		width: 300px;
		position: absolute;
		top: 5px;
	}
	.ranking-detail>i { 
		font-size: 12px; 
		display: block;
		position: absolute;
		width: 300px;
		overflow: hidden;
		text-overflow: ellipsis; 
		white-space: nowrap; 
		top: 10px; 
	}
	.ranking-num>div {
		width: 100px;
		height: 64px;
		font-size: 14px; 
	}
	.ranking-num>div:before { 
		content: ''; 
		display: inline-block; 
	}
	.ranking-num {
		width: 100px;
		display: inline-block; 
		float: right;
	}
	.ranking-num>div.up:before {
		background: url(../images/east/img/up.png) transparent no-repeat; 
		width: 12px; 
		height: 10px;
		margin-right: 10px;
	}
	.ranking-num>div.down:before {
		background: url(../images/east/img/down.png) transparent no-repeat;
		width: 12px; 
		height: 10px;
		margin-right: 10px;
	}
	.bar-pic {
		width: 100%; 
		height: 420px; 
		position: relative;
	}
	.bar-box {
		background-color: #fff;
	}
	.el-col-12 {
		width: 100%; 
	}
	.bar-detail { 
		width: 100%; 
		height: 70px;
		border-top: 1px solid #EAEAF0; 
		position: absolute;
		bottom: 0px; 
		text-align: center;
		line-height: 70px; 
		font-size: 14px; 
	}
	#bar-box {
		height: 350px; 
		width: 100%; 
	}
	.bar-detail>span {
		display: inline-block; 
		margin: 0px 10px;
	}
	.bar-detail>span:before { 
		content: ''; 
		width: 16px; 
		height: 16px;
		border-radius: 16px; 
		margin-right: 10px;
		display: inline-block; 
		vertical-align: middle;
	}
	.bar-detail>span.bar-item1:before { 
		background-color: #3F88DE; 
	}
	.bar-detail>span.bar-item2:before { 
		background-color: #5EC3D7; 
	}
	.bar-item1 {
		color: #3F88DE;
	}
	.bar-item2 {
		color: #5EC3D7;
	}
	.report-head {
		width: 100%; 
		height: 120px;
		background-color: #a7b8c2; 
		border-top-left-radius: 5px; 
		border-top-right-radius: 5px;
		text-align: center;
		position: relative;
	}
	.report-head>h3 { 
		font-size: 18px; 
		color: #fff; 
		height: 70px;
		line-height: 55px; 
		font-weight: 400;
	}
	.report-head-detail>span {
		font-size: 14px; 
		color: #fff; 
	}
	.report-head-detail { 
		height: 12px;
		line-height: 12px; 
	}
	.left-detail {
		float: left; 
		margin-left: 12px; 
	}
	.right-detail { 
		float: right;
		margin-right: 12px;
	}
	.report-down {
		width: 24px; 
		height: 24px;
		background-color: #b8c7ce; 
		border-radius: 24px; 
		top: 17px; 
		right: 12px; 
		position: absolute;
		cursor: pointer; 
	}
	.report-down:before { 
		content: ''; 
		width: 14px; 
		height: 14px;
		background: url(../../images/east/img/download.png) no-repeat; 
		display: inline-block; 
		vertical-align: middle;
	}
	 
	.report-array-tit>.report-array-tit01,
	.report-array-tit>.report-array-tit02 { 
		height: 113px;
		background-color: #f4f4f4;
		box-shadow: 0px 15px 10px -15px #ccc;
		box-sizing: content-box; 
		overflow: hidden;
		border-top: 1px solid #EAEAF0; 
	}
	.report-array-tit>div>h1 {
		font-size: 24px; 
		line-height: 64px; 
		padding-left: 10px;
		height: 64px;
		border-left: 2px solid #E9596F;
		margin-top: 15px;
		height: 110px;
		vertical-align: middle;
	}
	.report-array-tit01>h1>span,
	.report-array-tit02>h1>span { 
		color: #E9596F;
		font-size: 28px; 
		height: 64px;
		line-height: 64px; 
		display: inline-block; 
		font-weight: 400;
		vertical-align: bottom;
	}
	.report-array-tit02>h1>span { 
		color: #75BEDA;
	}
	.report-array-tit01>h1>span>i,
	.report-array-tit02>h1>span>i { 
		margin-right: 5px; 
		font-size: 20px; 
		vertical-align: bottom;
	}
	.report-array-box { 
		background-color: #fff;
	}
	.report-vue-list {
		border: 1px solid #AAB8C1; 
	}
	.all-Overview .report-array-content>div { 
		height: 400px; 
		margin: 0px 24px;
		margin-top: 24px;
	}
	.report-array-content { 
		padding-bottom: 33.6px;
		padding-top: 33.6px;
	}
	.report-vue-list>.list-introduce {
		width: 300px;
		text-align: center;
		margin-left: 63px; 
	}
	 
	.all-Overview .report-vue-list>div {
		float: left; 
	}
	.list-introduce-left{
		width: 40%;
	}
	.list-introduce-left>ul { 
		margin-left: 63px; 
		margin-top: 30px;
		font-size: 16px; 
		line-height: 30px; 
		color: #495A63;
	}
	 
	.list-introduce-left>ul>li>i {
		color: #E9596F;
	}
	 
	#partOneChart { 
		width: 100%;
		height: 300px; 
	}
	 
	.list-introduce-left>ul.list-left-numberbar { 
		margin-top: 106px; 
	}
	 
	#bar-chart01 {
		width: 100%; 
		height: 300px; 
	}
	 
	.all-Results .report-array-content>div {
		height: 380px; 
		margin: 0px 24px;
	}
	 
	.report-array-tit01,
	.report-array-tit02 { 
		position: relative;
		height: 40px;
		line-height: 40px; 
	}
	 
	.list-sort {
		position: absolute;
		top: 25px; 
		right: 12px; 
	}
	 
	.list-sort>div {
		float: left; 
		margin-right: 8px; 
		height: 40px;
		line-height: 40px; 
	}
	 
	.list-sort>div input {
		width: 123px;
		height: 40px;
		border-radius: 4px;
		border: 1px solid #D9DDE1; 
		padding: 0px;
		padding-left: 5px; 
		font-size: 16px; 
		color: #495A63;
		font-weight: 400;
		box-sizing: border-box;
	}
	 
	.list-sort>div input:focus {
		outline: none; 
	}
	 
	.list-change-btn {
		width: 128px;
		height: 40px;
		border: 1px solid #ddd;
		border-radius: 4px;
		box-sizing: border-box;
	}
	 
	.list-change-btn>span { 
		display: inline-block; 
		width: 63px; 
		height: 38px;
		text-align: center;
		float: left; 
		cursor: pointer; 
		line-height: 38px; 
	}
	 
	.change-btn { 
		background-color: #75BEDA; 
		color: #fff; 
	}
	 
	.lool-all-btn { 
		width: 100px;
		height: 40px;
		border-radius: 4px;
		background-color: #75BEDA; 
		color: #fff; 
		text-align: center;
		line-height: 40px; 
		cursor: pointer; 
	}
	 
	.list-up {
		position: absolute;
		top: 8px;
		right: 10px; 
		cursor: pointer; 
	}
	 
	.list-up:after {
		content: '▲';
		color: #919191;
		width: 12px; 
		height: 12px;
		line-height: 12px; 
		display: block;
		text-align: center;
		font-size: 12px; 
		transform: scale(.8, .8);
		-ms-transform: scale(.8, .8);
		/* IE 9 */ 
		-moz-transform: scale(.8, .8); 
		/* Firefox */
		-webkit-transform: scale(.8, .8);
		/* Safari 和 Chrome */ 
		-o-transform: scale(.8, .8); 
		/* Opera */
	}
	 
	.list-down {
		position: absolute;
		top: 22px; 
		right: 10px; 
		cursor: pointer; 
	}
	 
	.list-down:after {
		content: '▲';
		color: #919191;
		width: 12px; 
		height: 12px;
		line-height: 12px; 
		display: block;
		text-align: center;
		font-size: 12px; 
		transform: rotate(180deg) scale(.8, .8); 
		-ms-transform: rotate(180deg) scale(.8, .8);
		/* IE 9 */ 
		-moz-transform: rotate(180deg) scale(.8, .8); 
		/* Firefox */
		-webkit-transform: rotate(180deg) scale(.8, .8);
		/* Safari 和 Chrome */ 
		-o-transform: rotate(180deg) scale(.8, .8);
		/* Opera */
	}
	.list-sort {
		line-height: 42px; 
		height: 40px;
	}
	.change-select {
		position: relative;
	}
	.all-Results .list-introduce-left { 
		width: 306px;
		height: 350px; 
		margin-left: 30px; 
		margin-top: 10px;
		position: relative;
		text-align: center;
		float: left; 
	}
	.introduce-left-tit { 
		display: inline-block; 
		padding: 0px 12px; 
		min-width: 136px;
		height: 48px;
		line-height: 48px; 
		color: #fff; 
		background-color: #75BEDA; 
		border-radius: 48px; 
		text-align: center;
		position: relative;
		top: 0px;
		z-index: 111;
		font-size: 20px; 
		font-weight: 600;
		overflow: hidden;
		width : 280px;
	}
	.introduce-left-content>ul>li { 
		font-size: 16px; 
		line-height: 35px; 
		text-align: left;
	}
	.introduce-left-content>ul>li>span {
		width: 80px; 
		text-align: right; 
		margin-left: 40px; 
	}
	.introduce-left-content>ul>li>i { 
		float: right;
		margin-right: 40px;
	}
	.introduce-left-content>ul {
		margin-top: 40px;
	}
	.introduce-left-content { 
		width: 306px;
		height: 320px; 
		background-color: #F8FBFF; 
		margin-top: -20px; 
		overflow: hidden;
	}
	.importantColor { 
		color: #E9596F;
		font-weight: 600;
	}
	.positiveColor {
		color: #E9596F;
	}
	.negative { 
		color: #70C6C1;
	}
	.list-vue-right { 
		width: 58%; 
		height: 100%;
		float: right;
		position: relative;
	}
	.list-vue-right>div { 
		width: 100%; 
		height: 380px; 
	}
	.list-vue-right>i{
		position: absolute;
		right: 35px; 
		top: 15px; 
		z-index: 222;
		border: 1px solid #D8DCE0;
		height: 32px; 
		line-height: 32px;
		width: 60px;
		text-align: center; 
		border-radius: 16px;
		cursor: pointer;
	}
	.color3{
		min-height: 742px; 
		background-color: #fff;
	}
	.detailed-vue .box-content{ 
		height: 534px; 
		width: 809px;
		margin: 0 auto;
		position: relative;
	}
	.color3 .box-tit{ 
		border-bottom: 1px solid #EAEAF0;
	}
	.color3 .box-tit>i{ 
		width:24px ; 
		height: 24px;
		background: url(../images/east/img/close.png) no-repeat;
		border: none;
		margin-top: 15px;
		cursor: pointer; 
	}
	.color3 .box-tit>span{float: right;margin-right: 5px;} 
	.color3 .box-tit>span>i.down{ 
		width:24px ; 
		height: 24px;
		background: url(../images/east/img/download.png) no-repeat center #d2dbe0; 
		border: none;
		margin-top: 15px;
		cursor: pointer; 
		display: inline-block; 
		border-radius: 24px; 
	}
	.color3 .box-tit>span>i.upload{ 
		width:24px ; 
		height: 24px;
		background: url(../images/east/img/up1.png) no-repeat center #d2dbe0;
		border: none;
		margin-top: 15px;
		cursor: pointer; 
		display: inline-block; 
		border-radius: 24px; 
	}
	#validate-rules-chart{width: 100%;height: 460px;}
	#detail-record-chart{width: 100%;height: 460px;} 
	.form-style .el-row{margin-top: 7px;} 
	.form-style .el-col{margin-top: 17px;}
	.form-style .el-col>input{
		height: 40px;line-height: 40px;width: 320px;
		outline: none;border: 1px solid #D9DDE1;padding: 0px;padding-left: 5px; 
		max-width: 320px;font-size: 14px;color: #313D49;font-family: '微软雅黑';
	}
	.form-style .el-col>span{display: inline-block;width: 100px;font-size: 14px;} 
	.form-style .dateinput{position: relative;} 
	.form-style .dateinput:after{ 
		width: 40px; 
		height: 40px;
		content: ''; 
		display: inline-block; 
		background: url(../images/east/img/date.png) no-repeat;
		background-position: center; 
		position: absolute;
		left: 400px;
	}
	 
	.input-btn{text-align: center;} 
	.form-style input[type='button']{ 
		border: none;
		outline: none; 
		background-color: #75BEDA; 
		min-width: 80px; 
		height: 40px;
		border-radius: 40px; 
		text-align: center;
		font-size: 14px; 
		color: #fff; 
		padding: 0px 24px; 
		margin: 10px 12px; 
		cursor: pointer; 
		transition: all .3s linear;
	}
	.form-style input[type='button']:hover{box-shadow: 0px 0px 8px #75BEDA;}
	table.table-detail{border-collapse: collapse;border: 2px solid #AAB8C1;width: 100%;margin-top: 15px;}
	table.table-detail>thead>tr{background-color: #AAB8C1;color: #fff;} 
	table.table-detail tr{height: 48px;line-height: 40px;text-align: left;}
	table.table-detail tbody tr{border-bottom: 1px solid #EAEAF0;transition: all .3s linear;cursor: pointer;}
	table.table-detail tr td{border: none;padding: 0px;text-align: center;font-size: 14px;}
	table.table-detail tr td:first-child{text-align: left;padding-left: 20px;}
	table.table-detail tbody tr:hover{background-color: #F2F4F8;} 
	.table-pagebtn{
		text-align: right;
		padding: 17px 20px 26px 0px;
	}
	.table-pagebtn>input[type='button']{
		min-width: 48px; 
		height: 28px; 
		border: none; 
		outline: none;
		border-radius: 4px; 
		display: inline-block;
		line-height: 24px;
		text-align: center; 
		vertical-align: middle; 
		margin-left: 5px; 
		cursor: pointer;
		transition: all .3s linear; 
	}
	.table-pagebtn>input[type='button'].prohibitbtn{ 
		background-color: #eaedef; 
		color: #667C89;
		cursor: not-allowed; 
	}
	.table-pagebtn>input[type='button']:hover{background-color: #9CAAB3;
		color: #fff; 
		} 
	.table-pagebtn>input[type='button'].prohibitbtn:hover{ 
		background-color: #eaedef; 
		color: #667C89;
		box-shadow: none;
		
	}
	.table-pagebtn>input[type='button'].changebtn{
		background-color: #9CAAB3; 
		color: #fff; 
	}
	.table-pagebtn>input[type='button'].changebtn:hover{ 
		box-shadow: 0px 0px 8px 0px #d9d9d9; 
	}
	.rankingicon{ 
		content: '▲';
		color: #fff; 
		width: 12px;
		height: 12px; 
		line-height: 12px;
		display: inline-block;
		text-align: center; 
		font-size: 12px;
		transform: rotate(180deg) scale(0.8, 0.8);
		cursor: pointer;
	}
	.mustwrite{position: relative;} 
	.mustwrite:before{
		content: '*';
		display: inline-block; 
		position: absolute; 
		left: 455px;
		color: red; 
		line-height: 40px;
		height: 40px; 
	}
	table.table-detail tbody tr td>span{
		display: inline-block; 
		padding: 0px 10px;
		border: 1px solid transparent;
		border-radius: 6px; 
		height: 30px; 
		line-height: 30px;
		transition: all .3s linear; 
	}
	table.table-detail tbody tr td>span:hover{
		 border: 1px solid #AAB8C1;
	}
	
	.jloading {
	    background: rgb(98, 135, 172);
	    border-bottom-color: rgb(83, 119, 157);
	    border-bottom-style: solid;
	    border-bottom-width: 1px;
	    border-left-color: rgb(83, 119, 157);
	    border-left-style: solid;
	    border-left-width: 1px;
	    color: white;
	    filter: alpha(opacity =         70);
	    left: 45%;
	    opacity: 0.7;
	    position: absolute;
	    top: 40%;
	    z-index: 99999;
	}

</style>
<script>
	var taskId = "${taskId}";
	var taskNm = "${taskNm}";
	var dataDate = "${dataDate}";
	var orgNo = "${orgNo}";
	var orgNm = "${orgNm}";
	var defaultRate;
	var passRate;
	var warnDefaultRate;
	var warnPassRate;
	//报表过多时导出pdf会显示不全，所以要进行分割
	var divArray = [];
	var divArrayNum = 0;
	$(function(){
		$("#title").text(orgNm+"校验简报");
		$("#taskNm").text(taskNm);
		$("#dataDate").text(dataDate);
		$("#createTime").text(Date.format(new Date(), "yyyy-MM-dd : hh:mm:ss"));
		//报告总览
		initValidReportPartOne();
		//总览图
		initPartOneChart();
		//报表校验结果
		initValidReportPartTwo();
		divArray.push('#pdf1');
		$("#report-down").bind('click', function() {
			BIONE.showLoading("加载中...");
			//先把全部div隐藏了，导出ptf时，导出一个显示一个，这样保证要导出的div一直就在最上面，导出完成，整个页面也都显示出来了
			for(var j=0; j<divArray.length; j++){
				var divId = divArray[j];
				$(divId).hide();
			}
			savePdf();
		});
	});
	
	//导出pdf
	function savePdf(){
		if(divArrayNum < divArray.length){
			var divId = divArray[divArrayNum];
			$(divId).show();
	 		html2canvas($(divId), {
	 		    background: "#fff",
	 		   	dpi : 72,
	 		    //allowTaint: true,//允许跨域
	 		    //taintTest: false,//是否在渲染前测试图片
	 		   	//windowWidth: $(divId).scrollWidth,
	 		    //windowHeight: $(divId).scrollHeight,
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
	 		            pdf.save(orgNm+"校验简报"+ divArrayNum +".pdf");
	 		            divArrayNum++;
	 		            savePdf();
	 		        }
	 		    },
	 		})
		}else{
			divArrayNum = 0;
			BIONE.hideLoading();
			return;
		}
	}
	
	function initValidReportPartOne(){
		$.ajax({
			cache : false,
            async : false,
			url : "${ctx}/frs/validatereport/getValidReportPartOne", 
			type : "POST", 
			dataType : "json",
			data : {
				taskId : taskId,
				dataDate : dataDate,
				orgNo : orgNo
			},
			success : function(result) {
				defaultRate = (result.DEFAULTRATE*100).toFixed(2) + "%";
				warnDefaultRate = (result.WARNDEFAULTRATE*100).toFixed(2) + "%";
				if(result.VALIDCOUNT == "0"){
					passRate = "0.00%";
				}else{
					passRate = ((result.VALIDCOUNT-result.DEFAULTCOUNT)/result.VALIDCOUNT*100).toFixed(2) + "%";
				}
				if(result.WARNCOUNT == "0"){
					warnPassRate = "0.00%";
				}else{
					warnPassRate = ((result.WARNCOUNT-result.WARNDEFAULTCOUNT)/result.WARNCOUNT*100).toFixed(2) + "%";
				}
				$("#rptCount").text(result.RPTCOUNT);
				$("#validCount").text(result.VALIDCOUNT);
				$("#defaultCount").text(result.DEFAULTCOUNT);
				$("#defaultRate").text(defaultRate);
				$("#warnCount").text(result.WARNCOUNT);
				$("#warnDefaultCount").text(result.WARNDEFAULTCOUNT);
				$("#warnDefaultRate").text(warnDefaultRate);
			}
		});
	}
	
	function initPartOneChart() {
	 	var myCharts = echarts.init(document.getElementById('partOneChart'));
	 	var option = {
	 		series: [{
	 			type: 'pie',
	 			radius: ['50%', '60%'],
	 			avoidLabelOverlap: true,
	 			label: {
	 				normal: {
	 					show: true
	 				},
	 				emphasis: {
	 					show: true,
	 					textStyle: {
	 						fontSize: '16',
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
	 					value: $("#defaultCount").text(),
	 					name: '逻辑不通过率'+defaultRate,
	 					itemStyle: {
	 						normal: {
	 							color: '#E9596F'
	 						}
	 					}
	 				},
	 				{
	 					value: $("#validCount").text()-$("#defaultCount").text(),
	 					name: '逻辑通过率'+passRate,
	 					itemStyle: {
	 						normal: {
	 							color: '#8ab9da'
	 						}
	 					}
	 				}
	 			]
	 		},{
	 			type: 'pie',
	 			radius: ['20%', '30%'],
	 			avoidLabelOverlap: true,
	 			label: {
	 				normal: {
	 					show: true
	 				},
	 				emphasis: {
	 					show: true,
	 					textStyle: {
	 						fontSize: '16',
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
	 					value: $("#warnDefaultCount").text(),
	 					name: '预警不通过率'+warnDefaultRate,
	 					itemStyle: {
	 						normal: {
	 							color: '#e69d87'
	 						}
	 					}
	 				},
	 				{
	 					value: $("#warnCount").text()-$("#warnDefaultCount").text(),
	 					name: '预警通过率'+warnPassRate,
	 					itemStyle: {
	 						normal: {
	 							color: '#8ab9da'
	 						}
	 					}
	 				}
	 			]
	 		}]
	 	};
	 	myCharts.setOption(option);
	 }
	
	function initValidReportPartTwo(){
		$.ajax({
			cache : false,
            async : false,
			url : "${ctx}/frs/validatereport/getValidReportPartTwo", 
			type : "POST", 
			dataType : "json",
			data : {
				taskId : taskId,
				dataDate : dataDate,
				orgNo : orgNo
			},
			success : function(result) {
				var rptValidList = result.rptValidList;
				var validChartsList = result.validChartsList;
				var divNum = 1;
				var chartNum = 1;
				var chartDiv = "#partTwo";
				for(var i=0; i<rptValidList.length; i++){
					//经过测试45个图表进行分割
					if(chartNum > 45){
						divNum++;
						chartNum = 1;
						if(divNum > 1){
							chartDiv = "pdf" + divNum;
							$("#pdf").append("<div id = '"+ chartDiv +"'class='report-array-box all-Results'> </div>");
							chartDiv = "#" + chartDiv;
							divArray.push(chartDiv);
						}
					}
					var checkSts = "";
					var warnCheckSts = "";
					if(rptValidList[i].CHECK_STS == "06"){
						checkSts = "未通过";
					}else if(rptValidList[i].CHECK_STS == "05"){
						checkSts = "通过";
					}else if(rptValidList[i].CHECK_STS == "00"){
						checkSts = "未校验";
					}
					if(rptValidList[i].WARN_CHECK_STS == "06"){
						warnCheckSts = "未通过";
					}else if(rptValidList[i].WARN_CHECK_STS == "05"){
						warnCheckSts = "通过";
					}else if(rptValidList[i].WARN_CHECK_STS == "00"){
						warnCheckSts = "未校验";
					}
					var partTwoChartId = "partTwoChart" + i;
					$(chartDiv).append("<div class='report-array-content'>"
						+"	<div class='report-vue-list'>"
						+"		<div class='list-introduce-left'>"
						+"			<div class='introduce-left-tit'>"+rptValidList[i].RPT_NM+"</div>"
						+"   		<div class='introduce-left-content'>"
						+"    			<ul>"
						+"					<li><span>●&nbsp;校验状态:</span><i class='importantColor'>"+checkSts+"/"+warnCheckSts+"</i></li>"
						+"					<li><span>●&nbsp;逻辑校验总数:</span><i>"+rptValidList[i].VALIDCOUNT+"</i></li>"
						+"  				<li><span>&nbsp;&nbsp;&nbsp;表内校验不通过数:</span><i class='importantColor'>"+rptValidList[i].INDEFAULTCOUNT+"</i></li>"
						+" 					<li><span>&nbsp;&nbsp;&nbsp;表间校验不通过数:</span><i class='importantColor'>"+rptValidList[i].OUTDEFAULTCOUNT+"</i></li>"
						+"					<li><span>&nbsp;&nbsp;&nbsp;逻辑校验不通过率:</span><i class='importantColor'>"+(rptValidList[i].DEFAULTRATE*100).toFixed(2)+"%</i></li>"
						+"					<li><span>●&nbsp;预警校验总数:</span><i>"+rptValidList[i].WARNCOUNT+"</i></li>"
						+"  				<li><span>&nbsp;&nbsp;&nbsp;预警校验不通过数:</span><i class='importantColor'>"+rptValidList[i].WARNDEFAULTCOUNT+"</i></li>"
						+"					<li><span>&nbsp;&nbsp;&nbsp;不通过率:</span><i class='importantColor'>"+(rptValidList[i].WARNDEFAULTRATE*100).toFixed(2)+"%</i></li>"
						+"	  			</ul>"
						+"   		</div>"
						+"  	</div>"
						+"  	<div class='list-vue-right'>"
						+"  		<div id='"+partTwoChartId+"'></div>"
						+"		</div>"
						+"	</div>"
						+"</div>"
					);
					var rpt_id = rptValidList[i].RPT_ID;
					var dateArray = [];
					var indefaultArray = [];
					var outdefaultArray = [];
					var warnDefaultArray =[];
					for(var j=0; j<validChartsList.length; j++){
						if(rpt_id == validChartsList[j].RPT_ID){
							dateArray.push(validChartsList[j].DATADATE);
							indefaultArray.push(validChartsList[j].INDEFAULTCOUNT);
							outdefaultArray.push(validChartsList[j].OUTDEFAULTCOUNT);
							warnDefaultArray.push(validChartsList[j].WARNDEFAULTCOUNT);
						}
					}
					initPartTwoChart(partTwoChartId,dateArray,indefaultArray,outdefaultArray,warnDefaultArray);
					chartNum++;
				}
			}
		});
	}
	
	function initPartTwoChart(partTwoChartId,dateArray,indefaultArray,outdefaultArray,warnDefaultArray){
		var option = {
			    tooltip : {
			        trigger: 'axis',
			        axisPointer: {
			            label: {
			                backgroundColor: '#6a7985'
			            }
			        }
			    },
			    legend: {
			        x:'center',	//可设定图例在左、右、居中
			        y:'top',
			        padding:[30,0,0,0],
			        data:['表内校验','表间校验','预警校验']
			    },
			    toolbox: {
			        feature: {
			            saveAsImage: {}
			        }
			    },
			    grid: {
			        left: '3%',
			        right: '10%',
			        bottom: '3%',
			        containLabel: true
			    },
			    xAxis : [
			        {
			            type : 'category',
			            boundaryGap : false,
			            data : dateArray
			        }
			    ],
			    yAxis : [
			        {
			            type : 'value'
			        }
			    ],
			    series : [
			        {
			            name:'表内校验',
			            type:'line',
			            stack: '总量',
			            areaStyle: {
			            	normal: {
			            		color: '#E9596F' //改变区域颜色
			            	}
			        	},
			        	itemStyle: {
			        		normal : { 
			        			color:'#E9596F', //改变折线点的颜色
			        			lineStyle:{ 
				        			color:'#E9596F' //改变折线颜色
				        		} 
			        		} 
			        	},
			            data: indefaultArray
			        },
			        {
			            name:'表间校验',
			            type:'line',
			            stack: '总量',
			            label: {
			                normal: {
			                    show: true,
			                    position: 'top',
			                    formatter: function (obj) { 
			        				return indefaultArray[obj.dataIndex] + outdefaultArray[obj.dataIndex];
			                    }
			                }
			            },
			            areaStyle: {
			            	normal: {
			            		color: '#5299c1' //改变区域颜色
			            	}
			        	},
			        	itemStyle: {
			        		normal : { 
			        			color:'#5299c1', //改变折线点的颜色
			        			lineStyle:{ 
				        			color:'#5299c1' //改变折线颜色
				        		} 
			        		} 
			        	},
			            data: outdefaultArray
			        },
			        {
			            name:'预警校验',
			            type:'line',
			            smooth: true,
			            /* stack: '总量',
			            areaStyle: {
			            	normal: {
			            		color: '#e69d87' //改变区域颜色
			            	}
			        	}, */
			        	itemStyle: {
			        		normal : { 
			        			color:'#e69d87', //改变折线点的颜色
			        			lineStyle:{ 
				        			color:'#e69d87' //改变折线颜色
				        		} 
			        		} 
			        	},
			            data: warnDefaultArray
			        }
			    ]
			};
		 var myChart = echarts.init(document.getElementById(partTwoChartId));
		 // 使用刚指定的配置项和数据显示图表。
	     myChart.setOption(option);
	}
</script>
<title>校验简报</title>
</head>
<body>
	<div id='pdf'> 
		<div id='pdf1' class='content_box'> 
			<div id='report-head' class='report-head'>
				<h3 id="title">xx银行全行数据质量报告</h3>
				<p class='report-head-detail'> 
					<span class='left-detail'>任务名称：<i id="taskNm"></i></span>
					<span class='middle-detail'>数据日期：<i id="dataDate"></i></span> 
					<span class='right-detail'>报告时间：<i id="createTime"></i></span> 
				</p> 
				<div id='report-down' class='report-down'></div>
			</div> 
			<div class='report-array-box all-Overview'>
				<div class='report-array-tit'> 
					<div class='report-array-tit01'> 
						<h1><span>1.</span>报告总览</h1>
					</div> 
					<div class='report-array-content'> 
						<div class='report-vue-list'>
							<div class='list-introduce-left'>
							 <ul>
								<li>本期报表包含 <i id="rptCount"></i> 个报表</li>
								<li>●&nbsp;逻辑校验总数 <i id="validCount"></i> 条</li> 
								<li>&nbsp;&nbsp;&nbsp;逻辑校验不通过数 <i id="defaultCount"></i> 条</li> 
								<li>&nbsp;&nbsp;&nbsp;逻辑校验不通过率为 <i id="defaultRate"></i></li>
								<li>●&nbsp;预警校验总数 <i id="warnCount"></i> 条</li> 
								<li>&nbsp;&nbsp;&nbsp;预警校验不通过数 <i id="warnDefaultCount"></i> 条</li> 
								<li>&nbsp;&nbsp;&nbsp;预警校验不通过率为 <i id="warnDefaultRate"></i></li>
							 </ul> 
							</div> 
							<div class='list-vue-right'> 
								<div id='partOneChart'></div>
							</div> 
						</div> 
					</div> 
				</div> 
			</div> 
			<div class='report-array-box all-Results'> 
				<div class='report-array-tit' id="partTwo"> 
					<div class='report-array-tit02' style="margin-bottom: 35px;"> 
						<h1><span>2.</span>报表校验结果</h1>
					</div> 
				</div> 
			</div> 
		</div>
	</div>
</body>
</html>