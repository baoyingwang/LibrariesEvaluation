    //http://www.stephanimoroni.com/how-to-create-a-2d-array-in-javascript/
    Array.matrix = function(numrows, numcols, initial){
    	   var arr = [];
    	   for (var i = 0; i < numrows; ++i){
    	      var columns = [];
    	      for (var j = 0; j < numcols; ++j){
    	         columns[j] = initial;
    	      }
    	      arr[i] = columns;
    	    }
    	    return arr;
    }
    
    function addTitle(containerName,  chartTitleID, title){

    	addTitle(containerName,  chartTitleID, title, '"h2"');
    }
    
    function addTitle(containerName,  chartTitleID, title, hX){

	 	var container = document.getElementById(containerName);
	 	var chartTitle = document.createElement('h2');
	 	chartTitle.innerHTML="<span id='"+chartTitleID+"'/>";	
	 	container.appendChild(chartTitle);
	 	
	 	$("#"+chartTitleID).text(title);
    }    
    
    function addDiv(containerName, divID){

	 	var container = document.getElementById(containerName);
	 	var chartDiv = document.createElement('div');
	 	chartDiv.id=divID;
		container.appendChild(chartDiv);

    }
    
    function addParagraph(containerName, text){

	 	var container = document.getElementById(containerName);
	 	var p = document.createElement('p');
	 	p.innerHTML = text;
	 	container.appendChild(p);

    }    
   

    /**
     *
     select categraryFieldName, stackedFieldName, count(1) or sum(field3) as values
     
     from table
     group by field1, field2
     qeuryResult {
    	categraryFieldName:[,,,]
    	stackedFieldName  :[,,,]
    	valueName         :[,,,]
    }
    
    e.g.

		qeuryResult:
		{
			"intervals":["2017-01","2017-01","2017-01","2017-01","2017-01","2017-02","2017-02","2017-02","2017-02","2017-02","2017-03","2017-03","2017-03","2017-03","2017-03","2017-04","2017-04","2017-04","2017-04","2017-04","2017-05","2017-05","2017-05","2017-05","2017-05"],
			"usernames":["a","b","c","d","e","f","g","h","i","j","k","l","m","n","o","p","q","r","s","t","u","v","w","x","y"]
			"data1":["130","110","150","450","180","5","3","7","8","45","15","23","37","28","41","125","223","337","228","141","153","233","337","283","413"],
		} 
		
		categraryFieldName=intervals
		stackedFieldName  =usernames
		valueName         =data1
		result:
		            categary values
		stack_v_1  [    ,   ,   ]
		stack_v_2  [    ,   ,   ]
		stack_v_3  [    ,   ,   ]
     
     */
     baoyingLibGenerateC3ChartArrays = function(jsonQueryResult, categaryFieldName, stackedFieldName, valueName){
    	 
    	 
		var uniqCategaryArray     = Array.from(new Set(jsonQueryResult[categaryFieldName]));
		var uniqStackedGroupArray = Array.from(new Set(jsonQueryResult[stackedFieldName]));
		
		
		var categaryStackedGroupMap = new Map();
		for (var i = 0; i < jsonQueryResult[valueName].length; i++) {
			var key = jsonQueryResult[categaryFieldName][i] + ":" + jsonQueryResult[stackedFieldName][i];
			var value = jsonQueryResult[valueName][i];		    			
			categaryStackedGroupMap.set(key, value);
		} 		

	
		var matrix_without_header = Array.matrix(uniqStackedGroupArray.length,uniqCategaryArray.length,0);

		
		var row = 0;
		//forEach - https://msdn.microsoft.com/zh-cn/library/dn263016(v=vs.94).aspx
		uniqStackedGroupArray.forEach(function(stackValue, sameItem, any) {
			
			var col = 0;			
			uniqCategaryArray.forEach(function(categaryValue, sameItem, any) {
			
				var key = categaryValue + ":" + stackValue;
				if(categaryStackedGroupMap.has(key)){
					matrix_without_header[row][col] = categaryStackedGroupMap.get(key);
				}else{
					matrix_without_header[row][col] = "0";
				}
				
    		    col = col +1;
    		    
    		});
			
			row = row + 1;
		});

		$("#chart_data").text("baoyingLibGenerateC3ChartArrays - matrix_without_header:"+matrix_without_header);
		
		var chartData = matrix_without_header;
		for(var row =0; row < uniqStackedGroupArray.length; row++){
			chartData[row].unshift(uniqStackedGroupArray[row]);
		}
		//stack-xx   2017-01 2017-02  -- categary
		//[a        v       v ]
		//[b        v       v ]
		//[c         v       v ]
		
		$("#chart_data").text("baoyingLibGenerateC3ChartArrays - chartData:"+chartData);
		return [chartData, uniqCategaryArray, uniqStackedGroupArray];			
	}
     
     baoyingLibGenerateC3ChartArrays_2 = function(jsonQueryResult, categaryFieldName, stackedFieldName, valueName){
    	 
    	 
 		var uniqCategaryArray     = Array.from(new Set(jsonQueryResult[categaryFieldName]));
 		var uniqStackedGroupArray = Array.from(new Set(jsonQueryResult[stackedFieldName]));
 		
 		
 		var categaryStackedGroupMap = new Map();
 		for (var i = 0; i < jsonQueryResult[valueName].length; i++) {
 			var key = jsonQueryResult[categaryFieldName][i] + ":" + jsonQueryResult[stackedFieldName][i];
 			var value = jsonQueryResult[valueName][i];		    			
 			categaryStackedGroupMap.set(key, value);
 		} 		

 	
 		var matrix_without_header = Array.matrix(uniqStackedGroupArray.length,uniqCategaryArray.length,0);

 		
 		var row = 0;
 		//forEach - https://msdn.microsoft.com/zh-cn/library/dn263016(v=vs.94).aspx
 		uniqStackedGroupArray.forEach(function(stackValue, sameItem, any) {
 			
 			var col = 0;			
 			uniqCategaryArray.forEach(function(categaryValue, sameItem, any) {
 			
 				var key = categaryValue + ":" + stackValue;
 				if(categaryStackedGroupMap.has(key)){
 					matrix_without_header[row][col] = categaryStackedGroupMap.get(key);
 				}else{
 					matrix_without_header[row][col] = "0";
 				}
 				
     		    col = col +1;
     		    
     		});
 			
 			row = row + 1;
 		});

 		$("#chart_data").text("baoyingLibGenerateC3ChartArrays - matrix_without_header:"+matrix_without_header);
 		
 		var chartData = matrix_without_header;
 		for(var row =0; row < uniqStackedGroupArray.length; row++){
 			chartData[row].unshift(uniqStackedGroupArray[row]);
 		}
 		//stack-xxx   2017-01 2017-02  -- categary
 		//[a        v       v ]
 		//[b        v       v ]
 		//[c         v       v ]
 		
 		$("#chart_data").text("baoyingLibGenerateC3ChartArrays - chartData:"+chartData);
 		return [chartData, uniqCategaryArray, uniqStackedGroupArray];			
 	}
     /**
      * c3chartColumnMatrix:
      * e.g. [
      * 		['data1',130,10,50,450,180]
      * 	]
      * e.g.[
      * 		['data1',130,30,50,450,180]
      * 		['data2',230,10,550,50,80]
      * 	]
      */
     baoyingLibDrawLinesC3Chart=function(chartName_withSharp , c3chartColumnMatrix, uniqCategaryArray){
    	 
 		//http://c3js.org/samples/simple_xy_multiple.html
 		var chart = c3.generate({		    			
 			bindto: chartName_withSharp,		    			
 			data: {
 				columns:c3chartColumnMatrix,
 				labels: true
 			},		    		
 		    axis: {
 		        x: {
 		            type: 'category',
  		            tick: {
  		                rotate: 75,
  		                multiline: false
  		            },
 		            categories: uniqCategaryArray
 		        }
 		    }
 		});
 		
 		return chart;
     }
     
     baoyingLibDrawLinesC3Chart_height=function(chartName_withSharp , c3chartColumnMatrix, uniqCategaryArray){
    	 
  		//http://c3js.org/samples/simple_xy_multiple.html
  		var chart = c3.generate({		    			
  			bindto: chartName_withSharp,
  			size: {
  		        height: 1000,
  		        width: 1800
  		    },  			
  			data: {
  				columns:c3chartColumnMatrix,
  				labels: true
  			},		    		
  		    axis: {
  		        x: {
  		            type: 'category',
   		            tick: {
   		                rotate: 75,
   		                multiline: false
   		            },
  		            categories: uniqCategaryArray
  		        }
  		    }
  		});
  		
  		return chart;
      }     
     
     
     baoyingLibDrawBarsC3Chart=function(chartName_withSharp , c3chartColumnMatrix, uniqCategaryArray){
    	 
  		//http://c3js.org/samples/simple_xy_multiple.html
  		var chart = c3.generate({		    			
  			bindto: chartName_withSharp,		    			
  			data: {
  				columns:c3chartColumnMatrix,
  				type : 'bar',  
  				labels: true
  			},		    		
  		    axis: {
  		        x: {
  		            type: 'category',
  		            tick: {
  		                rotate: 75,
  		                multiline: false
  		            },
  		            categories: uniqCategaryArray
  		        }
  		    }
  		});
  		
  		return chart;
      }
    
     /**
      * c3chartColumnMatrix:
      * e.g. [
      * 		['data1',130,10,50,450,180]
      * 	]
      * e.g.[
      * 		['data1',130,30,50,450,180]
      * 		['data2',230,10,550,50,80]
      * 	]
      */
     baoyingLibDrawStackingBarC3Chart=function(chartName_withSharp , c3chartColumnMatrix, uniqCategaryArray, uniqStackedGroupArray){
		//http://c3js.org/samples/chart_bar_stacked.html		    			
		var chart = c3.generate({		    			
			bindto: chartName_withSharp,		    			
			data: {
				columns: c3chartColumnMatrix,
				type: 'bar',
				labels: true,
		        groups: [
					uniqStackedGroupArray
		        ]
			},	
		    axis: {
		        x: {
		            type: 'category',
		            tick: {
		                rotate: 75,
		                multiline: false
		            },
		            categories: uniqCategaryArray
		        }
		    }
		});
		
		return chart;
		
 	}
     
	function prepareChartTitleAndDiv(chartContainer, title){
		
		var random = Math.floor(Math.random() * 1000)+"_"+Math.floor(Math.random() * 1000)+"_"+Math.floor(Math.random() * 1000)+"_"+Math.floor(Math.random() * 1000);
		var chartTitleID = 'chartTitleID_'+ random;
		addTitle(chartContainer,  chartTitleID , title);
		
	 	var chartDivID   = 'chartDivID_'  + random;
		addDiv(chartContainer,  chartDivID);
		
		return chartDivID;
	}

	
	function drawMonthlyCharts_FXorMetal(month_YYYYMM, postUrl, restfulRequestParams, category_column_name, titlePrefix){
		
		var month = month_YYYYMM;

		//var postUrl = "/BaoyingDataService/query_deal_monthly_fx";
		//var restfulRequestParams = {
		//    month                       : month ,
     	//	group_by_fields_in_select   : "ext_deal_datetime_today as deal_date" ,
		//	group_by_fields_in_group_by : "ext_deal_datetime_today ",
		//	orderBy                     : "deal_date",
		//	orderDirection              : "asc"
		//};

		//var category_column_name='DEAL_DATE';
        //var titlePrefix = "FX Deals(metal excluded)";
	 	var value_1_title       = titlePrefix + " Deal Volume ( " + month + " )";
	 	var value_2_title       = titlePrefix + " Deal Count  ( " + month + " )";
	 	
	 	var value_1_column_name = 'USD_AMOUNT_IN_BILLION';
	 	var value_1_column_tip  = 'usd amount in billion';	 	
	 	var value_2_column_name = 'DEAL_COUNT';
	 	var value_2_column_tip  = 'value';	 	
	 	

    	var chartContainer = _chartContainer;
    	var value_1_ChartDivID    = prepareChartTitleAndDiv(chartContainer, value_1_title);
    	var value_2_ChartDivID    = prepareChartTitleAndDiv(chartContainer, value_2_title);
	 	
	 	

    	$.post(
    			postUrl,
    			restfulRequestParams,
		    	function(restfulResponse,status){
		    		
		    		addParagraph( chartContainer,postUrl+" status:"+status+ " response:"+restfulResponse);		    		
		    		
		    		var jsonRestfulResponse = JSON.parse(restfulResponse);
		    		var columnNames = jsonRestfulResponse['columnNames'      ];
		    		var data        = jsonRestfulResponse['columnName_values'];
		    		
		    		var categary_list   = data[category_column_name];
		    		var value_1_list    = data[value_1_column_name];
		    		var value_2_list    = data[value_2_column_name];
		    		
		    		var c3value_1_list = value_1_list.slice(0); //make a copy
		    		c3value_1_list.unshift(value_1_column_tip)    ; //add the 'value' to head of array
		    		
		    		var c3value_2_list = value_2_list.slice(0); //make a copy
		    		c3value_2_list.unshift(value_2_column_tip)    ; //add the 'value' to head of array
		    		
		    		baoyingLibDrawLinesC3Chart('#'+value_1_ChartDivID ,[c3value_1_list],categary_list); //[c2countList] means a matrix
		    		baoyingLibDrawLinesC3Chart('#'+value_2_ChartDivID,[c3value_2_list],categary_list);		    		
		    		    	
		    	}
		);      	    	

	}
	
	function drawMonthlyCharts_ONLY_count(month_YYYYMM, postUrl, restfulRequestParams, category_column_name, titlePrefix){
		
		var month = month_YYYYMM;

		//var postUrl = "/BaoyingDataService/query_deal_monthly_fx";
		//var restfulRequestParams = {
		//    month                       : month ,
     	//	group_by_fields_in_select   : "ext_deal_datetime_today as deal_date" ,
		//	group_by_fields_in_group_by : "ext_deal_datetime_today ",
		//	orderBy                     : "deal_date",
		//	orderDirection              : "asc"
		//};

		//var category_column_name='DEAL_DATE';
        //var titlePrefix = "FX Deals(metal excluded)";
		var value_1_title       = titlePrefix + " Deal Count  ( " + month + " )";
	 	//var value_2_title       = titlePrefix + " Deal Volume ( " + month + " )";		
	 	
	 	
	 	var value_1_column_name = 'DEAL_COUNT';
	 	var value_1_column_tip  = 'value';	 	
	 	//var value_2_column_name = 'USD_AMOUNT_IN_BILLION';
	 	//var value_2_column_tip  = 'usd amount in billion';	 	
	 	

    	var chartContainer = _chartContainer;
	 	var value_1_ChartDivID    = prepareChartTitleAndDiv(chartContainer, value_1_title);
	 	//var value_2_ChartDivID    = prepareChartTitleAndDiv(chartContainer, value_2_title);
	 	

    	$.post(
    			postUrl,
    			restfulRequestParams,
		    	function(restfulResponse,status){
		    		
		    		addParagraph( chartContainer,postUrl+" status:"+status+ " response:"+restfulResponse);		    		
		    		
		    		var jsonRestfulResponse = JSON.parse(restfulResponse);
		    		var columnNames = jsonRestfulResponse['columnNames'      ];
		    		var data        = jsonRestfulResponse['columnName_values'];
		    		
		    		var categary_list   = data[category_column_name];
		    		var value_1_list    = data[value_1_column_name];
		    		//var value_2_list    = data[value_2_column_name];
		    		
		    		var c3value_1_list = value_1_list.slice(0); //make a copy
		    		c3value_1_list.unshift(value_1_column_tip)    ; //add the 'value' to head of array
		    		
		    		//var c3value_2_list = value_2_list.slice(0); //make a copy
		    		//c3value_2_list.unshift(value_2_column_tip)    ; //add the 'value' to head of array
		    		
		    		baoyingLibDrawLinesC3Chart('#'+value_1_ChartDivID ,[c3value_1_list],categary_list); //[c2countList] means a matrix
		    		//baoyingLibDrawLinesC3Chart('#'+value_2_ChartDivID,[c3value_2_list],categary_list);		    		
		    		    	
		    	}
		);      	    	

	}
	
		
