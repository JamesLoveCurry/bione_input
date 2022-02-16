
var ActivitiRest = {
	options: {},
	
	getProcessDefinitionByKey: function(processDefinitionKey, callback) {
		var url = Lang.sub(this.options.processDefinitionByKeyUrl, {processDefinitionKey: processDefinitionKey});
		$.ajax({
			url: url,
			dataType: 'jsonp',
			cache: false,
			async: true,
			success: function(data, textStatus) {
				var processDefinition = data;
				if (!processDefinition) {
					console.error("Process definition '" + processDefinitionKey + "' not found");
				} else {
				  callback.apply({processDefinitionId: processDefinition.id});
				}
			}
		}).done(function(data, textStatus) {
			console.log("ajax done");
		}).fail(function(jqXHR, textStatus, error){
			console.error('Get diagram layout['+processDefinitionKey+'] failure: ', textStatus, 'error: ', error, jqXHR);
		});
	},
	
	getProcessDefinition: function(processDefinitionId, callback) {
		var url = ctx+ "/rpt/frs/rptfill/handle/activiti";
		var data = {
				processDefinitionId : processDefinitionId,
				processInstanceId : this.options.processInstanceId
		}
		var activities = [];
		var sequenceFlows = [];
		$.ajax({
			url: url,
			dataType: 'json',
			cache: false,
			async: true,
			data : data,
			success: function(data, textStatus) {
				if(data){
					var chartParams = data.mapList;
					for(var i=0;i<chartParams.length;i++){
						var labelType = chartParams[i].label;
						if(labelType == "sequenceFlow"){
							if(chartParams[i].midX){
								var seqFlow = {
									flow : "("+chartParams[i].source+")--"+chartParams[i].flow+"-->("+chartParams[i].target+")",
									id : chartParams[i].flow,
									name : chartParams[i].name,
									xPointArray : {
										0 : parseInt(chartParams[i].startX),
										1 : parseInt(chartParams[i].endX),
										2 : parseInt(chartParams[i].midX)
									},
									yPointArray : {
										0 : parseInt(chartParams[i].startY),
										1 : parseInt(chartParams[i].endY),
										2 : parseInt(chartParams[i].midY)
									}
								}
							}else{
								var seqFlow = {
									flow : "("+chartParams[i].source+")--"+chartParams[i].flow+"-->("+chartParams[i].target+")",
									id : chartParams[i].flow,name : chartParams[i].name,
									xPointArray : {
										0 : parseInt(chartParams[i].startX),
										1 : parseInt(chartParams[i].endX)
									},
									yPointArray : {
										0 : parseInt(chartParams[i].startY),
										1 : parseInt(chartParams[i].endY)
									}
								}
							}
							sequenceFlows.push(seqFlow);
						}else{
							var chart = {
								activityId : chartParams[i].activityId,
								height : parseInt(chartParams[i].height),
								properties : {
									name : chartParams[i].name,
									type : chartParams[i].type,
									group : chartParams[i].candidateGroups,
									user : chartParams[i].candidateUsers,
									assignee : chartParams[i].assignee
								},
								width : parseInt(chartParams[i].width),
								x : parseInt(chartParams[i].xpoint),
								y : parseInt(chartParams[i].ypoint)
							};
							activities.push(chart);
							if (chartParams[i].active) {
								ActivitiRest.options.active = chartParams[i].activityId;
							}
						}
					}
				}
				
				var processDefinition = {
						id : processDefinitionId
				};
				
				var processLayout = {
						activities : activities,
						processDefinition : processDefinition,
						sequenceFlows : sequenceFlows
				}
				
				var processDefinitionDiagramLayout = processLayout;
				if (!processDefinitionDiagramLayout) {
					console.error("Process definition diagram layout '" + processDefinitionId + "' not found");
					return;
				} else {
					callback.apply({processDefinitionDiagramLayout: processDefinitionDiagramLayout});
				}
			}
		}).done(function(data, textStatus) {
			console.log("ajax done");
		}).fail(function(jqXHR, textStatus, error){
			console.log('Get diagram layout['+processDefinitionId+'] failure: ', textStatus, jqXHR);
		});
	},
	
	getHighLights: function(processInstanceId,callback) {
		if (! this.options.active) {
			console.log("highLights not found");
			return;
		}
		var activities1 = { 0 : this.options.active };
		
		var highLights = {
				processDefinitionId: this.options.processDefinitionId,
				activities : activities1
		};
		callback.apply({highLights: highLights});
	}
};