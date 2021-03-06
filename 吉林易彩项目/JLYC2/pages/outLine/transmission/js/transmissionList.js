//进添加传输设备页面
function addTransmission(){
	mui.openWindow("transmissionAdd.html");
}
function getTransListMore(){
	start=start+7;
	getTransList(start);
}

function getTransList(index){
//	alert("列表")
	var UID = plus.storage.getItem("uid");//获取缓存记录
	var logingUser = plus.storage.getItem("logingUser");//当前登录用户
	/*var areaName = plus.storage.getItem("areaName");//得到权限控制地市*/
	var cv = plus.webview.currentWebview();
	var equipmentId = cv.equipmentId;
	var url = plus.storage.getItem("url")+"/pdaTransmissionEquipment!getEquipment.interface";
	
	
	
	var jsonStr = "{";
	if(equipmentId != null && equipmentId !=''){
		jsonStr +="'equipmentId':"+equipmentId+",";
	}
	if(jsonStr.charAt(jsonStr.length-1) ==','){
		jsonStr = jsonStr.substr(0,jsonStr.length-1);
	}
	jsonStr +="}";
	mui.ajax(url, {
		type: 'post',
		dataType: 'json',
		timeout: 10000 * 60,
		data: {
			jsonRequest: jsonStr,
			UID:UID,
			start:index,
			limit:length,
			longiner:logingUser/*,
			areaName:areaName*/
		},
		success: function(response) {
			plus.nativeUI.closeWaiting();
			mCurrentWebView.endPullToRefresh();
			var result = JSON.stringify(response);
			var data = JSON.parse(result);
			var result = data.result;
			if(result =='0'){
				var infos = JSON.parse(data.info);
				var cells = document.body.querySelectorAll('.borderbottom');
				var count = -1;
				for(var i = cells.length,len = i + infos.length; i < len; i++) {
					count++;
					var info = infos[count];
					totalList.push(info);
					var resStr = getNewline(info.zh_label);
						$("#poleLineUl").prepend("<div class=\"con borderbottom\" onclick=\"getTransDetail('"+info.equipmentId+"')\";>"
						+"<h1 class=\"con-head\">"+resStr+"</h1>"
						+"<div class=\"pr10\">"
						+"<ul class=\"mui-table-view adrlist\" >"
						+"<li class=\"mui-table-view-cell mapicon\">所属区域:" +
				          info.maintenanceAreaId +
				        "</li></ul></div></div>");
				}
				
				
				
				/*var infos = JSON.parse(data.info);
				for(var i=0;i<infos.length;i++){
					var info = infos[i];
					var resStr = getNewline(info.equipmentLable);
			        
			        $("#poleLineUl").append("<div class=\"con borderbottom\" onclick=\"getTransDetail('"+info.equipmentId+"')\";>" +
						"<h1 class=\"con-head\">" +resStr + "</h1>" +
						"<div class=\"pr10\">" +
						"<ul class=\"mui-table-view adrlist\" >" +
						"<li class=\"mui-table-view-cell mapicon\" >所属区域:" +
							info.maintenanceAreaId +
					"</li></ul></div></div>");
				}*/
			}else{
				mui.alert("获取数据失败！");
			}
		},
		error: function(xhr, type, errorThrown) {
			plus.nativeUI.closeWaiting();
			mCurrentWebView.endPullToRefresh();
		}

	});
}

//按名称搜索传输设备
function searchByLable(){

	var UID = plus.storage.getItem("uid");//获取缓存记录
	var logingUser = plus.storage.getItem("logingUser");//当前登录用户
/*	var areaName = plus.storage.getItem("areaName");//得到权限控制地市*/
	var url = plus.storage.getItem("url")+"/pdaTransmissionEquipment!getEquipment.interface";
	var cv = plus.webview.currentWebview();
	var equipmentId =cv.equipmentId;
	var equipmentLable=$("#equipmentLable").val();
	var jsonStr = "{";
	if(equipmentId != null && equipmentId !=''){
		jsonStr +="'equipmentId':"+equipmentId+",";
	}
	if(equipmentLable!=null&&equipmentLable!=''){
		jsonStr+="'equipmentLable':"+equipmentLable+",";
	}
	if(jsonStr.charAt(jsonStr.length-1) ==','){
		jsonStr = jsonStr.substr(0,jsonStr.length-1);
	}
	jsonStr +="}";
	mui.ajax(url, {
		type: 'post',
		dataType: 'json',
		timeout: 10000 * 60,
		data: {
			jsonRequest: jsonStr,
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
				$("#poleLineUl").empty();//清除之前的数据
				var infos = JSON.parse(data.info);
				for(var i=0;i<infos.length;i++){
					var info = infos[i];
					var resStr = getNewline(info.equipmentLable);
					  $("#poleLineUl").append("<div class=\"con borderbottom\" onclick=\"getTransDetail('"+info.equipmentId+"','"+ info.equipmentLable+"')\";>" +
						"<h1 class=\"con-head\">" +resStr + "</h1>" +
						"<div class=\"pr10\">" +
						"<ul class=\"mui-table-view adrlist\" >" +
						"<li class=\"mui-table-view-cell mapicon\" >所属区域:" +
							info.maintenanceAreaId +
					"</li></ul></div></div>");
				}
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
 * 
 * @param {Object} poleLineId
 * @param {Object} poleLineName
 */
function getTransDetail(equipmentId){
	mui.openWindow({
		url:"transmissionDetail.html",
		id:"transmissionDetail.html",
		extras: {
			equipmentId:equipmentId
		}
	});
}

function getNewline(val) {  
	var str = new String(val);  
	var bytesCount = 0;  
	var s="";
for (var i = 0 ,n = str.length; i < n; i++) {  
	var c = str.charCodeAt(i);  
	//统计字符串的字符长度
if ((c >= 0x0001 && c <= 0x007e) || (0xff60<=c && c<=0xff9f)) {  
	bytesCount += 1;  
} else {  
	bytesCount += 2;  
}
//换行
s += str.charAt(i);
if(bytesCount>=26){  
	s = s + ' <br> ';
//重置
			bytesCount=0;
		} 
	}  
	return s;  
}