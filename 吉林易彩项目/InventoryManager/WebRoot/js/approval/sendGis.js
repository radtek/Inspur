/**
 * 画点处理
 * @param points
 * @returns
 */
function drawPoints(points,lines){
	var layer = esri.layers.GraphicsLayer({id:"layer"+(500*Math.random())});
	//先画点
	for(var i=0;i<points.length;i++){
		var img = context_path+"/images/map/point.png";
		var pt = new esri.geometry.Point({
	    	type:'point',
	    	"x":points[i].longitude,
	    	"y":points[i].latitude
	    });
		var pictureMarkerSymbol = new esri.symbol.PictureMarkerSymbol(img, 20, 20);
	    var g = new esri.Graphic(pt, pictureMarkerSymbol); 
	    if(i==0){
	    	layer.add(g);
	    }
	}
	//再画线
	for(var i=0;i<lines.length;i++){
		var polyline = new esri.geometry.Polyline
		([[lines[i].startLon,lines[i].startLat],
		  [lines[i].endLon,lines[i].endLat]]);
    	var color = new dojo.Color([255,0,0]);
    	var symbol = new esri.symbol.SimpleLineSymbol(esri.symbol.SimpleLineSymbol.STYLE_SOLID, color, 2);
    	var lineGraph = new esri.Graphic(polyline, symbol);
    	layer.add(lineGraph);
	}
	return layer;
}


/**
 * 画出返回的点信息
 * @param points
 * @returns
 */
function drawResPoint(points){
	var layer = esri.layers.GraphicsLayer({id:"layer"+(500*Math.random())});
	var param = "EXAMINESTATUS+is+null&returnGeometry=true&f=json&_ts=1506580458422&returnIdsOnly=false&inSR=4326&geometryType=esriGeometryPolygon&spatialRel=esriSpatialRelIntersects" +
			"";
	var geometry ="geometry={\"rings\":[[";
	for(var i=0;i<points.length;i++){
		geometry +="";
		geometry +="["+points[i].longitude+","+points[i].latitude+"],";
	}
	geometry+="["+points[0].longitude+","+points[0].latitude+"]";
	geometry +="]]}";
	param +="&"+geometry;
	var resPoint,resLine;
	$.ajax({
		url:"http://10.224.202.65:6080/arcgis/rest/services/bjMapService1106/MapServer/0/query?"+param,
        data: {taskId:id},
        success: function (data) {
        	var pointJson = eval("("+data+")");
    		for(var i=0;i<pointJson.features.length;i++){
    			var resTemp = new esri.InfoTemplate();
    			resTemp.setTitle(pointJson.features[i].attributes.NAME);
    			resTemp.setContent("经度："+pointJson.features[i].geometry.x+";<p>" +
			    		"纬度:"+pointJson.features[i].geometry.y+";");
    			var img = context_path+"/images/map/resPoint.png";
    			var pt = new esri.geometry.Point({
    		    	type:'point',
    		    	"x":pointJson.features[i].geometry.x,
    		    	"y":pointJson.features[i].geometry.y,
    		    });
    			var pictureMarkerSymbol = new esri.symbol.PictureMarkerSymbol(img, 25,25);
    		    var g = new esri.Graphic(pt, pictureMarkerSymbol); 
    		    g.setInfoTemplate(resTemp);
    		    layer.add(g);
    		}
        },
        error: function (data) {
        	alert("查询不到数据!");
        }
     });
	$.ajax({
		url:"http://10.224.133.101/arcgis/rest/services/wx_query/MapServer/1/query?"+param,
        data: {taskId:id},
        success: function (data) {
        	var pointJson = eval("("+data+")");
    		for(var i=0;i<pointJson.features.length;i++){
    			var path= pointJson.features[i].geometry.paths;
    			var pathStr = JSON.stringify(path).replace("[[[","").replace("[","").replace("]","").replace("]]]","");
    			var paths = pathStr.split(",");
    			var polyline = new esri.geometry.Polyline
    			([[paths[0],paths[1]],
    			  [paths[2],paths[3]]]);
    	    	var color = new dojo.Color([0,0,255]);
    	    	var symbol = new esri.symbol.SimpleLineSymbol(esri.symbol.SimpleLineSymbol.STYLE_SOLID, color, 2);
    	    	var lineGraph = new esri.Graphic(polyline, symbol);
    	    	layer.add(lineGraph);
    		}
        },
        error: function (data) {
        	alert("查询不到数据!");
        }
     });
	return layer;
}


/**
 * 得到审批数据
 * @returns
 */
function approveFun(){
	 if(confirm("请确认工单中资源是否都审核!")){
		 $.ajax({
			url:context_path+"/approvalAction!examApprove.action?",
	        type: "POST",
	        data: {taskId:id},
	        dataType: "json",
	        success: function (data) {
	        	if(data == "false" || !data){
	        		alert("无审批权限");
	        	}
	        },
	        error: function (data) {
	        }
	     });
	 }
}