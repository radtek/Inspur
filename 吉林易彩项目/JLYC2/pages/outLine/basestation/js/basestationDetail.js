/**
 * 初始化基站信息
 */
function siteBaseInit(int_id){
	var UID = plus.storage.getItem("uid");//获取缓存记录
	var logingUser = plus.storage.getItem("logingUser");//当前登录用户
	var areaName = plus.storage.getItem("areaName");//得到权限控制地市
	var url = plus.storage.getItem("url")+"/pdaSiteBase!getSiteBase.interface";
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
					$("#e_nodeb_id").val(info.e_nodeb_id);
					$("#related_site_addr").val(info.related_site_addr);
					$("#related_site_addr_id").val(info.related_site_addr_id);
					$("#tower_site_addr_code").val(info.tower_site_addr_code);
					$("#tower_site_addr_id").val(info.tower_site_addr_id);
					$("#province").val(info.province);
					$("#city_id").val(info.city_id);
					changeCity(document.getElementById("city_id"));
					$("#county_id").val(info.county_id);
					$("#countryside").val(info.countryside);
					$("#unit").val(info.unit);
					$("#dept_area").val(info.dept_area);
					$("#district").val(info.district);
					$("#clusters").val(info.clusters);
					$("#reseau").val(info.reseau);
					$("#zh_label").val(info.zh_label);
					$("#enodeb_gap_name").val(info.enodeb_gap_name);
					$("#equipment_vendor").val(info.equipment_vendor);
					$("#equipment_type").val(info.equipment_type);
					$("#ipv4_addr").val(info.ipv4_addr);
					$("#subnet_mask").val(info.subnet_mask);
					$("#gateway").val(info.gateway);
					$("#bandwidth").val(info.bandwidth);
					$("#mme1").val(info.mme1);
					$("#mme2").val(info.mme2);
					$("#enodeb_version").val(info.enodeb_version);
					$("#duplex_mode").val(info.duplex_mode);
					$("#cell_num").val(info.cell_num);
					$("#omc_site_status").val(info.omc_site_status);
					$("#site_esn").val(info.site_esn);
					$("#site_type").val(info.site_type);
					$("#site_level").val(info.site_level);
					$("#site_longitude").val(info.site_longitude);
					$("#site_latitude").val(info.site_latitude);
					$("#ant_azimuth").val(info.ant_azimuth);
					$("#an_advanceangle").val(info.an_advanceangle);
					$("#project_no").val(info.project_no);
					$("#is_site_shared").val(info.is_site_shared);
					$("#omcid").val(info.omcid);
					$("#net_date").val(info.net_date);
					$("#remark").val(info.remark);
					$("#update_time").val(info.update_time);
					$("#update_person").val(info.update_person);
					getMaintainCompanyData(info.maintain_company);
					getMaintainAreaData(info.maintain_area);
					$("#maintain_manager").val(info.maintain_manager);
					$("#maintain_manager_phone").val(info.maintain_manager_phone);
					$("#maintain_area_zy").val(info.maintain_area_zy);
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

function updateBasestation(){
	var UID = plus.storage.getItem("uid");//获取缓存记录
	var logingUser = plus.storage.getItem("logingUser");//当前登录用户
	var areaName = plus.storage.getItem("areaName");//得到权限控制地市
	var url = plus.storage.getItem("url")+"/pdaSiteBase!updateSiteBase.interface";	
	var jsonStr = "{";
	var e_nodeb_id = $("#e_nodeb_id").val();
	if(e_nodeb_id != null && e_nodeb_id !=''){
		jsonStr +="'e_nodeb_id':"+e_nodeb_id+",";
	}
	var city_id = $("#city_id").val();
	if(city_id != null && city_id !=''){
		jsonStr +="'city_id':"+city_id+",";
	}
	var county_id = $("#county_id").val();
	if(county_id != null && county_id !=''){
		jsonStr +="'county_id':"+county_id+",";
	}
	var cell_num = $("#cell_num").val();
	if(cell_num != null && cell_num !=''){
		jsonStr +="'cell_num':"+cell_num+",";
	}
	var ant_azimuth = $("#ant_azimuth").val();
	if(ant_azimuth != null && ant_azimuth !=''){
		jsonStr +="'ant_azimuth':"+ant_azimuth+",";
	}
	var an_advanceangle = $("#an_advanceangle").val();
	if(an_advanceangle != null && an_advanceangle !=''){
		jsonStr +="'an_advanceangle':"+an_advanceangle+",";
	}
	jsonStr = jsonStr +"'related_site_addr':'"+$("#related_site_addr").val()+"',"
			+"'related_site_addr_id':'"+$("#related_site_addr_id").val()+"',"
			+"'tower_site_addr_code':'"+$("#tower_site_addr_code").val()+"',"
			+"'tower_site_addr_id':'"+$("#tower_site_addr_id").val()+"',"
			+"'province':'"+$("#province").val()+"',"
			+"'clusters':'"+$("#clusters").val()+"',"
			+"'countryside':'"+$("#countryside").val()+"',"
			+"'unit':'"+$("#unit").val()+"',"
			+"'dept_area':'"+$("#dept_area").val()+"',"
			+"'district':'"+$("#district").val()+"',"			
			+"'reseau':'"+$("#reseau").val()+"',"
			+"'int_id':'"+$("#int_id").val()+"',"
			+"'zh_label':'"+$("#zh_label").val()+"',"
			+"'enodeb_gap_name':'"+$("#enodeb_gap_name").val()+"',"
			+"'equipment_vendor':'"+$("#equipment_vendor").val()+"',"
			+"'equipment_type':'"+$("#equipment_type").val()+"',"
			+"'ipv4_addr':'"+$("#ipv4_addr").val()+"',"
			+"'subnet_mask':'"+$("#subnet_mask").val()+"',"
			+"'gateway':'"+$("#gateway").val()+"',"
			+"'bandwidth':'"+$("#bandwidth").val()+"',"
			+"'mme1':'"+$("#mme1").val()+"',"
			+"'mme2':'"+$("#mme2").val()+"',"
			+"'enodeb_version':'"+$("#enodeb_version").val()+"',"
			+"'duplex_mode':'"+$("#duplex_mode").val()+"',"
			+"'omc_site_status':'"+$("#omc_site_status").val()+"',"
			+"'site_esn':'"+$("#site_esn").val()+"',"
			+"'site_type':'"+$("#site_type").val()+"',"
			+"'site_level':'"+$("#site_level").val()+"',"
			+"'site_longitude':'"+$("#site_longitude").val()+"',"
			+"'site_latitude':'"+$("#site_latitude").val()+"',"
			+"'project_no':'"+$("#project_no").val()+"',"
			+"'is_site_shared':'"+$("#is_site_shared").val()+"',"
			+"'omcid':'"+$("#omcid").val()+"',"
			+"'net_date':'"+$("#net_date").val()+"',"
			+"'remark':'"+$("#remark").val()+"',"
			+"'update_time':'"+$("#update_time").val()+"',"
			+"'update_person':'"+$("#update_person").val()+"',"
			+"'maintain_company':'"+$("#maintain_company").val()+"',"
			+"'maintain_area':'"+$("#maintain_area").val()+"',"
			+"'maintain_manager':'"+$("#maintain_manager").val()+"',"
			+"'maintain_manager_phone':'"+$("#maintain_manager_phone").val()+"',"
			+"'maintain_area_zy':'"+$("#maintain_area_zy").val()+"'"
	jsonStr +="}";
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
				mui.confirm('修改成功，是否返回基站列表?', '提示信息', btnArray, function(e) {
					if (e.index == 1) {
						mui.openWindow("basestationList.html");
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

function deleteBasestation (){
	var btnArray = ['确定', '取消'];
	mui.confirm('确定删除吗?', '提示信息', btnArray, function(e) {
		if (e.index == 0) {
			var UID = plus.storage.getItem("uid");//获取缓存记录
			var logingUser = plus.storage.getItem("logingUser");//当前登录用户
			var areaName = plus.storage.getItem("areaName");//得到权限控制地市
			var url = plus.storage.getItem("url")+"/pdaSiteBase!deleteSiteBase.interface";
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
						mui.openWindow("basestationList.html");
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





/**
 * 得到经纬度数据
 */
function getLonLat(){
	mui.openWindow({
		url: "../../main/getLatLon.html",
		id: "getLonLat",
		extras: {
			resType:"siteBase",
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
	$("#site_longitude").val(detail.lon);
	$("#site_latitude").val(detail.lat);
}
/**
 * 跳转到标石页面
 */
function getStone(){
	mui.openWindow({
		url:"../stone/stoneList.html",
		id: "stoneList",
		extras: {
			basestationId:basestationId
		}
	});
}
/**
 * 得到直埋下的直埋段信息
 */
function getbasestationSeg(){
	mui.openWindow({
		url:"../basestationSeg/basestationSegList.html",
		id: "basestationSegList",
		extras: {
			basestationId:basestationId
		}
	});
}

