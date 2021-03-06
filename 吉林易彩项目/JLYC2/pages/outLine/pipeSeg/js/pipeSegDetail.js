/**
 * 初始化管道段
 */
function initPipeSeg(int_id){
	var UID = plus.storage.getItem("uid");//获取缓存记录
	var logingUser = plus.storage.getItem("logingUser");//当前登录用户
	var areaName = plus.storage.getItem("areaName");//得到权限控制地市
	var url = plus.storage.getItem("url")+"/pdaPipeCustom!getPipeSegCustom.interface";
	if(typeof(int_id)!="undefined"){ 
		mui.ajax(url, {
			type: 'post',
			async: true,
			dataType: 'json',
			timeout: 10000 * 60,
			data: {
				jsonRequest: "{'int_id':'"+int_id+"'}",
				UID:UID,
				longiner:logingUser,
				areaName:areaName
			},
			success: function(response) {
				plus.nativeUI.closeWaiting();
				var result = JSON.stringify(response);
				var data = JSON.parse(result);
				var result = data.result;
				if(result =='0'){
					infos = JSON.parse(data.info);
					var info = infos[0]; 
					$("#zh_label").val(info.zh_label);
					$("#city_id").val(info.city_id);
				changeCity(document.getElementById("city_id"));
					$("#county_id").val(info.county_id);
					$("#start_WellName").val(info.start_WellName);
					$("#dest_point_name").val(info.dest_point_name);
					$("#startLon").val(info.startLon);
					$("#startLat").val(info.startLat);
					$("#end_WellName").val(info.end_WellName);
					$("#orig_point_name").val(info.orig_point_name);
					$("#endLon").val(info.endLon);
					$("#endLat").val(info.endLat);
					$("#ownership").val(info.ownership);
					$("#length").val(info.length);
					getMaintainCompanyData(info.maintain_company);
					getMaintainAreaData(info.maintain_area);
					$("#maintain_manager").val(info.maintain_manager);
					$("#maintain_manager_phone").val(info.maintain_manager_phone);
					$("#remark").val(info.remark);
				}else{
					mui.alert("查无数据","提醒");
				}
			},
			error: function(xhr, type, errorThrown) {
				plus.nativeUI.closeWaiting();
			}
	
		});
	}
	
}

function checkWell(type){
	mui.openWindow({
		url: "../well/wellCheck.html",
		id: "wellCheck",
		extras: {
			resType:"segDetail",
			operate:type
		}
	});
}

//根据当前经纬度获取地址信息
function getAddByLonLat(lon,lat) {
	var lnglatXY = new AMap.LngLat(lon,lat);
	mapObj.plugin(["AMap.Geocoder"], function() {       
		MGeocoder = new AMap.Geocoder({
			radius: 1000,
			extensions: "all"
		});
	AMap.event.addListener(MGeocoder, "complete", geocoder_CallBack2);
	MGeocoder.getAddress(lnglatXY);
	});
}
function geocoder_CallBack2(data){
	var address;
	address = data.regeocode.formattedAddress;
	//document.getElementById("region").value=address
}

/**
 * 提交修改管道段
 */
function updatePipeSeg(){
	var UID = plus.storage.getItem("uid");//获取缓存记录
	var logingUser = plus.storage.getItem("logingUser");//当前登录用户
	var areaName = plus.storage.getItem("areaName");//得到权限控制地市
	var url = plus.storage.getItem("url")+"/pdaPipeCustom!updatePipeSegCustom.interface";
	mui.ajax(url, {
		type: 'post',
		dataType: 'json',
		timeout: 10000 * 60,
		data: {
			jsonRequest:"{'zh_label':'"+$("#zh_label").val()+"',"
						+"'int_id':'"+$("#int_id").val()+"',"
						+"'city_id':'"+$("#city_id").val()+"',"
						+"'county_id':'"+$("#county_id").val()+"',"
						+"'dest_point_name':'"+$("#dest_point_name").val()+"',"
						+"'orig_point_name':'"+$("#orig_point_name").val()+"',"
						+"'ownership':'"+$("#ownership").val()+"',"
						+"'length':'"+$("#length").val()+"',"
						+"'maintain_company':'"+$("#maintain_company").val()+"',"
						+"'maintain_area':'"+$("#maintain_area").val()+"',"
						+"'maintain_manager':'"+$("#maintain_manager").val()+"',"
						+"'maintain_manager_phone':'"+$("#maintain_manager_phone").val()+"',"
						+"'remark':'"+$("#remark").val()+"'"
						+"}",
			UID:UID,
			longiner:logingUser,
			areaName:areaName
		},
		success: function(response) {
			plus.nativeUI.closeWaiting();
			var result = JSON.stringify(response);
			var data = JSON.parse(result);
			var result = data.result;
			if(result =='0'){
				var btnArray = ['继续', '返回'];
				mui.confirm('管道段修改成功，是否返回管道段列表?','提示信息', btnArray, function(e) {
					if (e.index == 1) {
						mui.openWindow("pipeSegList.html");
					}
				});
				
			}else{
				mui.alert(data.info);
			}
		},
		error: function(xhr, type, errorThrown) {
			plus.nativeUI.closeWaiting();
		}

	});
}

function getRad(d){
	return d*PI/180.0;
}
//得到传递参数
function getQueryStr(str){
	var rs = new RegExp("(^|)" + str + "=([^&]*)(&|$)", "gi").exec(LocString), tmp; 
	if (tmp = rs) { 
		return tmp[2]; 
	} 
	return ""; 
}
/**
* approx distance between two points on earth ellipsoid
* @param {Object} lat1
* @param {Object} lng1
* @param {Object} lat2
* @param {Object} lng2
*/
function getFlatternDistance(lat1,lng1,lat2,lng2){
	var f = getRad((lat1*1 + lat2*1)/2);
	var g = getRad((lat1*1 - lat2*1)/2);
	var l = getRad((lng1*1 - lng2*1)/2);
	var sg = Math.sin(g);
	var sl = Math.sin(l);
	var sf = Math.sin(f);

	var s,c,w,r,d,h1,h2;
	var a = EARTH_RADIUS;
	var fl = 1/298.257;

	sg = sg*sg;
	sl = sl*sl;
	sf = sf*sf;

	s = sg*(1-sl) + (1-sf)*sl;
	c = (1-sg)*(1-sl) + sf*sl;

	w = Math.atan(Math.sqrt(s/c));
	r = Math.sqrt(s*c)/w;
	d = 2*w*a;
	h1 = (3*r -1)/2/c;
	h2 = (3*r +1)/2/s;
	return d*(1 + fl*(h1*sf*(1-sg) - h2*(1-sf)*sg));
}
function toDecimal(x) { 
	var f = parseFloat(x); 
	if (isNaN(f)) { 
		return; 
	} 
	f = Math.round(x*100)/100; 
	return f; 
} 

function deletePipeSeg(){
	var btnArray = ['确定', '取消'];
	mui.confirm('确定删除吗?', '提示信息', btnArray, function(e) {
		if (e.index == 0) {
			var UID = plus.storage.getItem("uid");//获取缓存记录
			var logingUser = plus.storage.getItem("logingUser");//当前登录用户
			var areaName = plus.storage.getItem("areaName");//得到权限控制地市
			var url = plus.storage.getItem("url")+"/pdaPipeCustom!deletePipeSegCustom.interface";
			mui.ajax(url, {
				type: 'post',
				dataType: 'json',
				timeout: 10000 * 60,
				data: {
					jsonRequest:"{'int_id':'"+$("#int_id").val()+"'}",
					UID:UID,
					longiner:logingUser,
					areaName:areaName
				},
				success: function(response) {
					plus.nativeUI.closeWaiting();
					var result = JSON.stringify(response);
					var data = JSON.parse(result);
					var result = data.result;
					if(result =='0'){
						mui.alert("删除成功");
						mui.openWindow("pipeSegList.html");
					}else{
						mui.alert("获取数据失败！");
					}
				},
				error: function(xhr, type, errorThrown) {
					plus.nativeUI.closeWaiting();
				}
		
			});
		}
	});
}