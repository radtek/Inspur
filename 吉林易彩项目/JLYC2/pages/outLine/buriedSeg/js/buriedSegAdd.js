
/**
 * 提交新增直埋段
 */
function insertBuriedSeg(){
	var UID = plus.storage.getItem("uid");//获取缓存记录
	var logingUser = plus.storage.getItem("logingUser");//当前登录用户
	var areaName = plus.storage.getItem("areaName");//得到权限控制地市
	var url = plus.storage.getItem("url")+"/pdaBuriedPartCustom!insertBuriedPartCustom.interface";
	mui.ajax(url, {
		type: 'post',
		dataType: 'json',
		timeout: 10000 * 60,
		data: {
			jsonRequest:"{'zh_label':'"+$("#zh_label").val()+"',"
						+"'city_id':'"+$("#city_id").val()+"',"
						+"'county_id':'"+$("#county_id").val()+"',"
						+"'start_ponit_id':'"+$("#start_ponit_id").val()+"',"
						+"'end_ponit_id':'"+$("#end_ponit_id").val()+"',"
						+"'ownership':'"+$("#ownership").val()+"',"
						+"'line_length':'"+$("#line_length").val()+"',"
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
				mui.confirm('直埋段增加成功，是否返回直埋段列表?','提示信息', btnArray, function(e) {
					if (e.index == 1) {
						mui.openWindow("buriedSegList.html");
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

/**
 * 选择标石
 * @param {Object} type
 */
function checkStone(type){
	mui.openWindow({
		url: "../stone/stoneCheck.html",
		id: "stoneCheck",
		extras: {
			resType:"segAdd",
			operate:type
		}
	});
}