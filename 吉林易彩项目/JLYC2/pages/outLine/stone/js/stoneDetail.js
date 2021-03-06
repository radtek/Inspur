/**
 * 初始化标石数据
 */
function stoneInit(int_id){
	var UID = plus.storage.getItem("uid");//获取缓存记录
	var logingUser = plus.storage.getItem("logingUser");//当前登录用户
	var areaName = plus.storage.getItem("areaName");//得到权限控制地市
	var url = plus.storage.getItem("url")+"/pdaStoneCustom!getStoneCustom.interface";
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
					$("#zh_label").val(info.zh_label);
					$("#city_id").val(info.city_id);
					changeCity(document.getElementById("city_id"));
					$("#county_id").val(info.county_id);
					$("#longitude").val(info.longitude);
					$("#latitude").val(info.latitude);
					$("#ownership").val(info.ownership);
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

/**
 * 修改标石
 */
function updateStone(){
	var UID = plus.storage.getItem("uid");//获取缓存记录
	var logingUser = plus.storage.getItem("logingUser");//当前登录用户
	var areaName = plus.storage.getItem("areaName");//得到权限控制地市
	var url = plus.storage.getItem("url")+"/pdaStoneCustom!updateStoneCustom.interface";
	mui.ajax(url, {
		type: 'post',
		dataType: 'json',
		timeout: 10000 * 60,
		data: {
			jsonRequest:"{'zh_label':'"+$("#zh_label").val()+"',"
						+"'int_id':'"+$("#int_id").val()+"',"
						+"'city_id':'"+$("#city_id").val()+"',"
						+"'county_id':'"+$("#county_id").val()+"',"
						+"'longitude':'"+$("#longitude").val()+"',"
						+"'latitude':'"+$("#latitude").val()+"',"
						+"'ownership':'"+$("#ownership").val()+"',"
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
				mui.confirm('标石增加成功，是否返回标石列表?','提示信息', btnArray, function(e) {
					if (e.index == 1) {
						mui.openWindow("stoneList.html");
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

/**
 * 得到经纬度数据
 */
function getLonLat(){
	mui.openWindow({
		url: "../../main/getLatLon.html",
		id: "getLonLat",
		extras: {
			resType:"stone",
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

function deleteStone(){
	var btnArray = ['确定', '取消'];
	mui.confirm('确定删除吗?', '提示信息', btnArray, function(e) {
		if (e.index == 0) {
			var UID = plus.storage.getItem("uid");//获取缓存记录
			var logingUser = plus.storage.getItem("logingUser");//当前登录用户
			var areaName = plus.storage.getItem("areaName");//得到权限控制地市
			var url = plus.storage.getItem("url")+"/pdaStoneCustom!deleteStoneCustom.interface";
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
						mui.openWindow("stoneList.html");
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

