<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<%@ include file="/common/taglibs.jsp"%>
<html>
<head>
<meta name="decorator" content="/template/template14.jsp">
<script type="text/javascript">
	$(function() {
		//目标模型搜索
		$("#dst-search-btn").click(function(){
			var dstTabNm = $("#dst-search-tab").val();
			if("" == dstTabNm){
				$("#model-tab").empty();
				BIONE.tip('请输入目标模型表名');
				return false;
			}
			
			$.ajax({
			    url:"${ctx}/rpt/frame/modelcoltrans/checkDstUsed",    //请求的url地址
			    dataType:"json",   //返回格式为json
			    data:{"dstTabNm":dstTabNm},//参数值
			    type:"GET",   //请求方式
			    success:function(data){
			    	if(data.isExsi == "Y"){//目标表已经被使用
			    		BIONE.tip('此表已在配置表中，不可重复配置');
			    	}else{
						$.ajax({
						    url:"${ctx}/rpt/frame/modelcoltrans/querydstmodel",    //请求的url地址
						    dataType:"json",   //返回格式为json
						    data:{"dstTabNm":dstTabNm},    //参数值
						    type:"GET",   //请求方式
						    success:function(data){
						    	var dataArr = data.dst;
						    	if(dataArr.length == 0){
									$("#model-tab").empty();
									BIONE.tip('无此目标模型表');
						    	}else{
						    		var dsttd = '';
						    		var dstth = '<tr><th>目标模型：<span id="dstTabNm-span">'+dstTabNm+'</span></th><th>来源模型：<span id="srcTabNm-span"></span></th><th>字段转换规则</th><th>操作</th></tr>';
						    		for(var i=0;i<dataArr.length;i++){
						    			dsttd = dsttd + '<tr><td class="dst-model">目标字段：<input value="'+dataArr[i].enNm+'"></td>' +
														'<td class="src-model">来源字段：<select></select></td>' +
														'<td class="trans-rule">规则：<input></td>' +
														'<td class="oper-col"><input type="button" value="删除" onclick="javascript:$(this).parent().parent().remove();"></td></tr>';
						    		}
						    		$("#model-tab").empty();
						    		$("#model-tab").append(dstth);
						    		$("#model-tab").append(dsttd);
						    	}
						    },
						    error:function(){
						    	$("#model-tab").empty();
								BIONE.tip('请求出错');
						    }
						});
			    	}
			    },
			    error:function(){
			    	$("#model-tab").empty();
					BIONE.tip('校验请求出错');
			    }
			});
		});
		//来源模型搜索
		$("#src-search-btn").click(function(){
			var srcTabNm = $("#src-search-tab").val();
			if($("#dstTabNm-span").length == 0){
				$("#model-tab").empty();
				BIONE.tip('请先添加目标模型');
				return false;
			}
			if("" == srcTabNm){
				BIONE.tip('请输入来源模型表名');
				return false;
			}
			$.ajax({
			    url:"${ctx}/rpt/frame/modelcoltrans/querysrcmodel",    //请求的url地址
			    dataType:"json",   //返回格式为json
			    async:true,//请求是否异步，默认为异步，这也是ajax重要特性
			    data:{"srcTabNm":srcTabNm},    //参数值
			    type:"GET",   //请求方式
			    success:function(data){
			    	var dataArr = data.src;
			    	if(dataArr.length == 0){
						BIONE.tip('无此来源模型表');
			    	}else{
			    		var srcCols = '<option selected="selected">无</option>';
			    		for(var i=0;i<dataArr.length;i++){
			    			srcCols = srcCols + '<option value="'+dataArr[i].enNm+'">'+dataArr[i].enNm+'</option>';
			    		}
			    		$("#srcTabNm-span").html(srcTabNm);
			    		$(".src-model select").empty();
			    		$(".src-model select").append(srcCols);
			    	}
			    },
			    error:function(){
			    	$("#model-tab").empty();
					BIONE.tip('请求出错');
			    }
			});
		});
		//取消
		$("#submit-cancel-btn").click(function(){
			BIONE.closeDialog("addTransDialog");
		});
		//保存
		$("#submit-save-btn").click(function(){
			if($("#dstTabNm-span").length == 0){
				$("#model-tab").empty();
				BIONE.tip('请添加目标模型');
				return false;
			}
			if($("#srcTabNm-span").html() == ""){
				BIONE.tip('请添加来源模型');
				return false;
			}
			if($("#src-where-tab").val() == ""){
				BIONE.tip('where条件不可空');
				return false;
			}
			BIONE.showLoading('正在保存数据中...');
			var _dstTabNm = $("#dstTabNm-span").html();
			var _srcTabNm = $("#srcTabNm-span").html();
			var _whereSrcf = $("#src-where-tab").val();
			var dstArr = new Array();
			var srcArr = new Array();
			var ruleArr = new Array();
			$(".dst-model>input").each(function(key,value){
				dstArr[key] = $(this).val() == "" ? "null" : $(this).val();
			});
			$(".src-model>select").each(function(key,value){
				srcArr[key] = $(this).val() == "" ? "null" : $(this).val();
			});
			$(".trans-rule>input").each(function(key,value){
				ruleArr[key] = $(this).val() == "" ? "null" : $(this).val();
			});
			if(checkSelect(dstArr,srcArr,ruleArr)){//校验保存项
				$.ajax({
				    url:"${ctx}/rpt/frame/modelcoltrans/savetransmodel",    //请求的url地址
				    dataType:"json",   //返回格式为json
				    async:false,
				    data:{"dstTabNm":_dstTabNm,"srcTabNm":_srcTabNm,"dstArr":dstArr.join(","),
				    	  "whereSrcf":_whereSrcf,"srcArr":srcArr.join(","),"ruleArr":ruleArr.join(",")},    //参数值
				    type:"POST",   //请求方式
				    success:function(data){
				    	if(data.msg == "success"){
				    		parent.grid.reload();
				    		BIONE.hideLoading();
				    		BIONE.closeDialog("addTransDialog","保存成功");
				    	}else{
				    		BIONE.hideLoading();
				    		BIONE.closeDialog("addTransDialog","保存失败");
				    	}
				    },
				    error:function(){
						BIONE.tip('请求出错');
				    }
				});
			}
		});
	});
	//校验选择项
	function checkSelect(dstArr,srcArr,ruleArr){
		if(dstArr.length == 0 || srcArr.length == 0){
			BIONE.tip('没有数据');
			return false;
		}
		if(dstArr.length == srcArr.length){
			for(var i=0;i<dstArr.length;i++){
				if("null" == dstArr[i] || "null" == srcArr[i]){
					BIONE.tip('目标字段或来源字段不可为空');
					return false;
				}
			}
		}else{
			BIONE.tip('保存数据格式不正确');
			return false;
		}
		return true;
	}
</script>
<style type="text/css">
	#maingrid{
		height:100%;
	}
	.search-field{
		height:20px;
		padding:10px;
		border-bottom:2px solid gray;
	}
	.search-field div{
		float:left;
	}
	.btn-margin-left{
		margin-left:15px;
	}
	.search-text-field{
		width:150px;
	}
	.model-info table{
		width:100%;
		background:#d9e2d9;
	}
	.model-info td{
		border:1px solid gray;
		padding:5px;
	}
	.model-info th{
		border:1px solid gray;
		text-align:center;
		padding:10px 0;
	}
	.dst-model{
		width:30%;
	}
	.src-model{
		width:28%;
	}
	.trans-rule{
		width:40%;
	}
	.dst-model input{
		width:60%;
	}
	.trans-rule input{
		width:80%;
	}
	.src-model select{
		width:60%;
	}
	#model-view-info{
		height:80%;
		overflow-y:auto;
		padding:5px;
		border-bottom:2px solid gray;
	}
	#submit-btm-btn{
		float:right;
		padding-top:10px;
		padding-right:30px;
	}
	.search-field i{
		display:inline;
		float:left;
		margin-left:20px;
		width:1px;
		height:20px;
		background-color:gray;
	}
</style>
</head>
<body>
	<div id="template.center">
		<div id="maingrid">
			<div class="search-field">
				<div>
					<span>目标模型 ：</span>
					<input id="dst-search-tab" class="search-text-field" type="text" placeholder="请输入目标模型表名">
					<input id="dst-search-btn" class="btn-margin-left" type="button" value="添加">
				</div>
				<i></i>
 				<div style="margin-left:20px">
					<span>来源模型 ：</span>
					<input id="src-search-tab" class="search-text-field" type="text" placeholder="请输入来源模型表名">
					<input id="src-search-btn" class="btn-margin-left" type="button" value="添加">				
				</div>
				<i></i>
				<div style="margin-left:20px">
					<span>WHERE ：</span>
					<input id="src-where-tab" class="search-text-field" type="text" placeholder="请输入转换where条件">
				</div>
			</div>
			<div id="model-view-info" class="model-info">
				<table id="model-tab">

				</table>
			</div>
			<div id="submit-bottom">
				<div id="submit-btm-btn">
					<input id="submit-save-btn" type="button" value="保存"><input id="submit-cancel-btn" class="btn-margin-left" type="button" value="取消">
				</div>
			</div>
		</div>
	</div>
</body>
</html>