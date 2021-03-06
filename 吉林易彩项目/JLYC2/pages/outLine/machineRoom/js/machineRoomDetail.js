/**
 * 初始化机房数据
 */
function machineRoomInit(int_id){
	var UID = plus.storage.getItem("uid");//获取缓存记录
	var logingUser = plus.storage.getItem("logingUser");//当前登录用户
	var areaName = plus.storage.getItem("areaName");//得到权限控制地市
	var url = plus.storage.getItem("url")+"/pdaMachineRoom!getMachineRoom.interface";
	if(typeof(int_id)!="undefined"){ 
		mui.ajax(url, {
			type: 'post',
			dataType: 'json',
			timeout: 10000 * 60,
			data: {
				jsonRequest: "{'int_id':'"+int_id+"'}",
				UID: UID,
				longiner: logingUser,
				areaName: areaName
			},
			success: function(response) {
				var result = JSON.stringify(response);
				var data = JSON.parse(result);
				var result = data.result;
				if(result == '0') {
					infos = JSON.parse(data.info);
					var info = infos[0]; 
					$("#city_id").val(info.city_id);
					changeCity(document.getElementById("city_id"));
					$("#county_id").val(info.county_id);
					$("#zh_label").val(info.zh_label);
					$("#equiproom_type").val(info.equiproom_type);
					$("#longitude").val(info.longitude);
					$("#latitude").val(info.latitude);
					$("#address").val(info.address);
					getMaintainCompanyData(info.maintain_company);
					getMaintainAreaData(info.maintain_area);
					$("#maintain_manager").val(info.maintain_manager);
					$("#maintain_manager_phone").val(info.maintain_manager_phone);
					$("#remark").val(info.remark);
				} else {
					mui.alert("获取数据失败！");
				}
			},
			error: function(xhr, type, errorThrown) {
				plus.nativeUI.closeWaiting();
				mCurrentWebView.endPullToRefresh();
			}
	
		});
	}
}

function updateMachineRoom(){
	var UID = plus.storage.getItem("uid");//获取缓存记录
	var logingUser = plus.storage.getItem("logingUser");//当前登录用户
	var areaName = plus.storage.getItem("areaName");//得到权限控制地市
	var url = plus.storage.getItem("url")+"/pdaMachineRoom!updateMachineRoom.interface";
	var jsonStr = "{";
	var city_id = $("#city_id").val();
	if(city_id != null && city_id !=''){
		jsonStr +="'city_id':"+city_id+",";
	}
	var county_id = $("#county_id").val();
	if(county_id != null && county_id !=''){
		jsonStr +="'county_id':"+county_id+",";
	}
	jsonStr = jsonStr+"'int_id':'"+$("#int_id").val()+"',"
						+"'zh_label':'"+$("#zh_label").val()+"',"
						+"'equiproom_type':'"+$("#equiproom_type").val()+"',"
						+"'longitude':'"+$("#longitude").val()+"',"
						+"'latitude':'"+$("#latitude").val()+"',"
						+"'address':'"+$("#address").val()+"',"
						+"'maintain_company':'"+$("#maintain_company").val()+"',"
						+"'maintain_area':'"+$("#maintain_area").val()+"',"
						+"'maintain_manager':'"+$("#maintain_manager").val()+"',"
						+"'maintain_manager_phone':'"+$("#maintain_manager_phone").val()+"',"
						+"'remark':'"+$("#remark").val()+"'"
						+"}"
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
				mui.confirm('增加成功，是否返回机房列表?', '提示信息', btnArray, function(e) {
					if (e.index == 1) {
						mui.openWindow("machineRoomList.html");
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
/**
 * 得到经纬度数据
 */
function getLonLat(){
	mui.openWindow({
		url: "../../main/getLatLon.html",
		id: "getLonLat",
		extras: {
			resType:"machineRoom",
			operate:"detail"
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
	$("#longitude").val(detail.lon);
	$("#latitude").val(detail.lat);
}

function deleteMachineRoom(){
	var btnArray = ['确定', '取消'];
	mui.confirm('确定删除吗?', '提示信息', btnArray, function(e) {
		if (e.index == 0) {
			var UID = plus.storage.getItem("uid");//获取缓存记录
			var logingUser = plus.storage.getItem("logingUser");//当前登录用户
			var areaName = plus.storage.getItem("areaName");//得到权限控制地市
			var url = plus.storage.getItem("url")+"/pdaMachineRoom!deleteMachineRoom.interface";
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
						mui.openWindow("machineRoomList.html");
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
