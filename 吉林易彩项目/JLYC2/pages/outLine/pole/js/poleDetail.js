/**
* 初始化电杆信息
*/
function poleInit(int_id){
	var cv = plus.webview.currentWebview();
	var poleId = cv.poleId;
	if(typeof(int_id)!="undefined"){ 
		var UID = plus.storage.getItem("uid");//获取缓存记录
		var logingUser = plus.storage.getItem("logingUser");//当前登录用户
		var areaName = plus.storage.getItem("areaName");//得到权限控制地市
		var url = plus.storage.getItem("url")+"/pdaPolelineCustom!getPoleCustom.interface";
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
				var infos = JSON.parse(data.info);
				var info = infos[0];
				$("#zh_label").val(info.zh_label);
				$("#city_id").val(info.city_id);
				changeCity(document.getElementById("city_id"));
				$("#county_id").val(info.county_id);
				$("#pole_height").val(info.pole_height);
				$("#ownership").val(info.ownership);
				$("#transfer_level").val(info.transfer_level);
				$("#purpose").val(info.purpose);
				$("#longitude").val(info.longitude);
				$("#latitude").val(info.latitude);
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
 * 得到经纬度数据
 */
function getLonLat(){
	mui.openWindow({
		url: "../../main/getLatLon.html",
		id: "getLonLat",
		extras: {
			resType:"pole",
			operate:"detail"
		}
	});
}

/**
 * 提交修改电杆
 */
function submitPole(){
	var UID = plus.storage.getItem("uid");//获取缓存记录
	var logingUser = plus.storage.getItem("logingUser");//当前登录用户
	var areaName = plus.storage.getItem("areaName");//得到权限控制地市
	var url = plus.storage.getItem("url")+"/pdaPolelineCustom!updatePoleCustom.interface";
	var jsonStr = "{";
	var city_id = $("#city_id").val();
	if(city_id != null && city_id !=''){
		jsonStr +="'city_id':"+city_id+",";
	}
	var county_id = $("#county_id").val();
	if(county_id != null && county_id !='' && county_id != "null"){
		jsonStr +="'county_id':"+county_id+",";
	}
	jsonStr = jsonStr +"'zh_label':'"+$("#zh_label").val()+"',"
		+"'int_id':'"+$("#int_id").val()+"',"
		+"'pole_height':'"+$("#pole_height").val()+"',"
		+"'ownership':'"+$("#ownership").val()+"',"
		+"'transfer_level':'"+$("#transfer_level").val()+"',"
		+"'purpose':'"+$("#purpose").val()+"',"
		+"'longitude':'"+$("#longitude").val()+"',"
		+"'latitude':'"+$("#latitude").val()+"',"
		+"'maintain_company':'"+$("#maintain_company").val()+"',"
		+"'maintain_area':'"+$("#maintain_area").val()+"',"
		+"'maintain_manager':'"+$("#maintain_manager").val()+"',"
		+"'maintain_manager_phone':'"+$("#maintain_manager_phone").val()+"',"
		+"'remark':'"+$("#remark").val()+"'"
		+"}";
	mui.ajax(url, {
		type: 'post',
		dataType: 'json',
		timeout: 10000 * 60,
		data: {
			jsonRequest:jsonStr,
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
				mui.confirm('电杆修改成功，是否返回电杆列表?','提示信息', btnArray, function(e) {
					if (e.index == 1) {
						var infos = JSON.parse(data.info);
						mui.openWindow("poleList.html");
					}
				});
				
			}else{
				mui.alert("获取数据失败！");
			}
		},
		error: function(xhr, type, errorThrown) {
			plus.nativeUI.closeWaiting();
		}

	});
}

//得到传递参数
function getQueryStr(str){
	var rs = new RegExp("(^|)" + str + "=([^&]*)(&|$)", "gi").exec(LocString), tmp; 
	var rs = new RegExp("(^|)" + str + "=([^&]*)(&|$)", "gi").exec(LocString), tmp; 
	if (tmp = rs) { 
		return tmp[2]; 
	} 
	return ""; 
}

function setLonLat(detail){
	$("#longitude").val(detail.lon);
	$("#latitude").val(detail.lat);
}

function deletePole(){
	var btnArray = ['确定', '取消'];
	mui.confirm('确定删除吗?', '提示信息', btnArray, function(e) {
		if (e.index == 0) {
			var UID = plus.storage.getItem("uid");//获取缓存记录
			var logingUser = plus.storage.getItem("logingUser");//当前登录用户
			var areaName = plus.storage.getItem("areaName");//得到权限控制地市
			var url = plus.storage.getItem("url")+"/pdaPolelineCustom!deletePoleCustom.interface";
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
						mui.openWindow("poleList.html");
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
