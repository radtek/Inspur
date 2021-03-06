function getBuriedListMore() {
	start=start+7;
	getBuriedList(start);
}

			//增加直埋
function addBuried() {
      mui.openWindow("buriedAdd.html");
}
//根据
function getBuriedList(index) {
	plus.nativeUI.showWaiting("正在加载数据......");
	var UID = plus.storage.getItem("uid"); //获取缓存记录
	var logingUser = plus.storage.getItem("logingUser"); //当前登录用户
	var areaName = plus.storage.getItem("areaName"); //得到权限控制地市
	var url = plus.storage.getItem("url") + "/pdaLineSystem!getLineSystem.interface";
	mui.ajax(url, {
		type: 'post',
		dataType: 'json',
		timeout: 10000 * 60,
		data: {
			jsonRequest: "{lineType:1}",
			UID: UID,
			start: index,
			limit: length,
			longiner: logingUser,
			areaName: areaName
		},
		success: function(response) {
			plus.nativeUI.closeWaiting();
			mCurrentWebView.endPullToRefresh();
			var result = JSON.stringify(response);
			var data = JSON.parse(result);
			var result = data.result;
			if(result == '0') {
				var infos = JSON.parse(data.info);
				if(infos.length == 0) {
					mui.alert("没有更多数据");
					return;
				}
				var cells = document.body.querySelectorAll('.borderbottom');
				var count = -1;
				for(var i = cells.length, len = i + infos.length; i < len; i++) {
					count++;
					var info = infos[count];
					totalList.push(info);
					var resStr = getNewline(info.lineName);
					$("#buriedUl").prepend("<div class=\"con borderbottom\" onclick=\"getBuriedDetail('"+ info.id+"','"+info.lineName+"');\">" +
						"<h1 class=\"con-head\">" + resStr + "</h1>" +
						"<div class=\"pr10\"><ul class=\"mui-table-view adrlist\">" +
						"<li class=\"mui-table-view-cell mapicon\" >" +
						info.lineArea +
						"</li></ul></div></div>");
				}
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

/**
 * 跳转到直埋修改详情页面
 * @param {Object} buriedId
 * @param {Object} buriedName
 */
function getBuriedDetail(buriedId, buriedName) {
	mui.openWindow({
		url:"buriedDetail.html",
		id:"buriedDetail.html",
		extras: {
			buriedId:buriedId
		}
	});
}

function getNewline(val) {
	var str = new String(val);
	var bytesCount = 0;
	var s = "";
	for(var i = 0, n = str.length; i < n; i++) {
		var c = str.charCodeAt(i);
		//统计字符串的字符长度
		if((c >= 0x0001 && c <= 0x007e) || (0xff60 <= c && c <= 0xff9f)) {
			bytesCount += 1;
		} else {
			bytesCount += 2;
		}
		//换行
		s += str.charAt(i);
		if(bytesCount >= 26) {
			s = s + ' <br> ';
			//重置
			bytesCount = 0;
		}
	}
	return s;
}