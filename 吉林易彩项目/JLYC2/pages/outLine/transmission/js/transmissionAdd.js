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
 * 新增传输设备
 */
function insertTrans(){
	var UID = plus.storage.getItem("uid");//获取缓存记录
	var logingUser = plus.storage.getItem("logingUser");//当前登录用户
//	var areaName = plus.storage.getItem("areaName");//得到权限控制地市
	var url = plus.storage.getItem("url")+"/pdaTransmissionEquipment!insertTransmissionEquipment.interface";
	mui.ajax(url, {
		type: 'post',
		dataType: 'json',
		timeout: 10000 * 60,
		data: {
			jsonRequest:
						"{'relatedEquiproom':'"+$("#relatedEquiproom").val()+"',"
						//+"'equipmentLable':'"+$("#equipmentLable").val()+"',"
						+"'city_id':'"+$("#city_id").val()+"',"
						+"'county_id':'"+$("#county_id").val()+"',"
						+"'zh_label':'"+$("#zh_label").val()+"',"
						+"'equipmentFactory':'"+$("#equipmentFactory").val()+"',"
						+"'equipmentmodel':'"+$("#equipmentmodel").val()+"',"
						+"'equipmentType':'"+$("#equipmentType").val()+"',"
						+"'maintenanceOrg':'"+$("#maintenanceOrg").val()+"',"
						+"'maintenanceAreaId':'"+$("#maintenanceAreaId").val()+"',"
						+"'maintenanceLeader':'"+$("#maintenanceLeader").val()+"',"
						+"'leaderPhone':'"+$("#leaderPhone").val()+"',"
						+"'remark':'"+$("#remark").val()+"'"
						+"}",
			UID:UID,
			longiner:logingUser/*,
			areaName:areaName*/
		},
				success: function(response) {
			plus.nativeUI.closeWaiting();
			var result = JSON.stringify(response);
			var data = JSON.parse(result);
			var result = data.result;
			if(result =='0'){
				var btnArray = ['继续', '返回'];
				mui.confirm('增加成功，是否返回传输设备列表?', '成功', btnArray, function(e) {
					if (e.index == 1) {
						var infos = JSON.parse(data.info);
						mui.openWindow("transmissionList.html");
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
	if (tmp = rs) { 
		return tmp[2]; 
	} 
	return ""; 
}

function setLonLat(detail){
	$("#poleLongitude").val(detail.lon);
	$("#poleLatitude").val(detail.lat);
}

function getMaintainCompanyData(company) {
	var UID = plus.storage.getItem("uid");//获取缓存记录
	var logingUser = plus.storage.getItem("logingUser");//当前登录用户
	var areaName = plus.storage.getItem("areaName");//得到权限控制地市
	var url = plus.storage.getItem("url")+"/pdaLoadData!getMaintainCompanyData.interface";
	mui.ajax(url, {
		type: 'post',
		dataType: 'json',
		timeout: 10000 * 60,
		data: {
			jsonRequest:"{}",
			UID:UID,
			longiner:logingUser,
			areaName:areaName
		},
		success: function(response) {
			var result = JSON.stringify(response);
			var data = JSON.parse(result);
			var result = data.result;
			if(result =='0'){
				var infos = JSON.parse(data.info);
				for(var i=0;i<infos.length;i++){
					if(company != "" && typeof(company)!="undefined" && company == infos[i].value){
						$("#maintenanceOrg").append("<option value ="+infos[i].value+" selected=\"selected\">"+infos[i].name+"</option>");
					}else{
						$("#maintenanceOrg").append("<option value ="+infos[i].value+">"+infos[i].name+"</option>");
					}
				}
			}else{
				mui.alert(data.info);
			}
		},
		error: function(xhr, type, errorThrown) {
			plus.nativeUI.closeWaiting();
		}
	});
}

function getMaintainAreaData(area) {
	var UID = plus.storage.getItem("uid");//获取缓存记录
	var logingUser = plus.storage.getItem("logingUser");//当前登录用户
	var areaName = plus.storage.getItem("areaName");//得到权限控制地市
	var url = plus.storage.getItem("url")+"/pdaLoadData!getMaintainAreaData.interface";
	mui.ajax(url, {
		type: 'post',
		dataType: 'json',
		timeout: 10000 * 60,
		data: {
			jsonRequest:"{}",
			UID:UID,
			longiner:logingUser,
			areaName:areaName
		},
		success: function(response) {
			var result = JSON.stringify(response);
			var data = JSON.parse(result);
			var result = data.result;
			if(result =='0'){
				var infos = JSON.parse(data.info);
				for(var i=0;i<infos.length;i++){
					if(area != "" && typeof(area)!="undefined" && area == infos[i].value){
						$("#maintenanceAreaId").append("<option value ="+infos[i].value+" selected=\"selected\">"+infos[i].name+"</option>");
					}else{
						$("#maintenanceAreaId").append("<option value ="+infos[i].value+">"+infos[i].name+"</option>");
					}
				}
			}else{
				mui.alert(data.info);
			}
		},
		error: function(xhr, type, errorThrown) {
			plus.nativeUI.closeWaiting();
		}
	});
}