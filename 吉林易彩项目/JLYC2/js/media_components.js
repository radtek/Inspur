/**
 * 自定义控件 图片和录音 
 * @author 18346386@qq.com
 */

(function($, doc, w) {
	w.hasClass = function(obj, cls) {
		return obj.className.match(new RegExp('(\\s|^)' + cls + '(\\s|$)'));
	}
	w.removeClass = function(obj, cls) {
		if(hasClass(obj, cls)) {
			var reg = new RegExp('(\\s|^)' + cls + '(\\s|$)');
			obj.className = obj.className.replace(reg, ' ');
		}
	}
	/**
	 * 仅仅拍照
	 */
	$.fn.cameraListInit = function(options) {
		var imageListAll = this;
		if(imageListAll.length > 0) {
			imageListAll.each(function(e) {
				var imageList = this;
				imageList.className = "custom-media-list custom-image-list mui-row";
				imageList.options = $.extend($.ImageListOptionsDefaults, options);
				if(imageList.options.size == 1) {
					imageList.options.multiple = false
				} else if(imageList.options.size > 1) {
					imageList.options.multiple = true;
				} else {
					mui.toast("参数错误！");
				}
				var addButtonHtml = '<button class="mui-icon mui-icon-image media-item button-item mui-col-"><h6>最多' + imageList.options.size + '张</h6></button>'
				imageList.innerHTML = addButtonHtml;
				imageList.addButton = imageList.querySelector(".button-item");
				imageList.addButton.addEventListener("tap", function() {
					var nowImageLength = imageList.querySelectorAll("img.file").length; //已经有的图片张数
					if(!checkImgNum(0, nowImageLength)) {
						return;
					}
					var cmr = plus.camera.getCamera();
					cmr.captureImage(function(path) {
						addImg(path);
					}, function(err) {});

				}, false);
				/**
				 * @param {Object} p 此次添加的个数
				 */
				function checkImgNum(p, i) {
					var all = i + p;
					if((p > 0 && all > imageList.options.size) || (p == 0 && all >= imageList.options.size)) {
						mui.toast("图片不能超过" + imageList.options.size + "张");

						return false;
					} else {
						return true;
					}
				}

				//添加选中图片到回复区域--begin
				function addImg(path) {
					plus.nativeUI.showWaiting("正在处理图片......");
					//本地缓存路径
					var hb_path = '_downloads/image/' + md5(path) + '.jpg'; //HBuilder平台路径
					var sd_path = plus.io.convertLocalFileSystemURL(hb_path); //SD卡绝对路径

					plus.zip.compressImage({
							src: path,
							dst: sd_path,
							quality: 20,
							format: "jpg",
							overwrite: true
						},
						function() {
							var Img = new Image();
							Img.src = sd_path;
							Img.onload = function() {
								var myCanvas = document.createElement("canvas");
								myCanvas.width = plus.screen.resolutionWidth * plus.screen.scale;
								myCanvas.height = plus.screen.resolutionHeight * plus.screen.scale;
								var width = myCanvas.width;
								var height = myCanvas.height;
								var ctx = myCanvas.getContext("2d");
								ctx.drawImage(Img, 0, 0, width, height);
								ctx.font = "30px microsoft yahei";
								ctx.fillStyle = "rgba(255,0,0,1)";
								ctx.fillText("名称:" + options.resName, 0, height - 200, width - 10);
								ctx.fillText("经纬度:" , 0, height - 150, width - 10);
								ctx.fillText("拍照时间:" + getNowFormatDate(), 0, height - 100, width - 10);
								ctx.fillText("采集人:" + plus.storage.getItem("userName"), 0, height - 50, width - 10);
								var data = myCanvas.toDataURL();
								//TODO  bitmap 修改图片 start
								var bitmap = new plus.nativeObj.Bitmap();
								bitmap.loadBase64Data(data, function() {
									//TODO  bitmap 修改图片 end
									bitmap.save(sd_path, {
										overwrite: true
									}, function(i) {
										var result = JSON.stringify(i);
										var resultData = JSON.parse(result);
										var target = resultData.target;
										//start
										var itemPhotoPath = target.replace("file://", "");
										var imageDiv = document.createElement("div");
										imageDiv.className = "media-item image-item mui-col-"
										imageDiv.setAttribute("id", target);
										imageDiv.innerHTML = '' +
											'<img class="file mui-action-preview" data-preview-src="' + data + '" data-preview-group="1" src="' + data + '" addTime="' + (new Date()).getTime() + '" />' +
											'<span class="mui-icon mui-icon-closeempty"></span>';
										imageList.appendChild(imageDiv);

										//end
										bitmap.clear();
										plus.nativeUI.closeWaiting();
									}, function(e) {
										plus.nativeUI.closeWaiting();
										console.log('保存图片失败：' + JSON.stringify(e));
									});

								}, function() {
									plus.nativeUI.closeWaiting();
									console.log("加载图片失败");
								});
								options.fun(imageListAll);
							}
						},
						function(error) {
							alert("图片处理失败!");
						});

				}
			})

			imageListAll.on("tap", ".media-item.image-item", function(e) {
				var $this = this;
				if(e.target.className.match(new RegExp('(\\s|^)mui-icon-closeempty(\\s|$)'))) {

					var $parentNode = $this.parentNode;
					var btnArray = ['是', '否'];
					mui.confirm('确定删除？', '删除', btnArray, function(e2) {
						if(e2.index == 0) {
							$parentNode.removeChild($this);
						}
					})
				}
			})
			if(!mui.getPreviewImage()) {
				mui.previewImage();
			}
		}
		
	}
	/**
	 * 拍照和相册选择
	 */
	$.fn.imageListInit = function(options) {
		var imageListAll = this;
		if(imageListAll.length > 0) {
			imageListAll.each(function(e) {
				var imageList = this;
				imageList.className = "custom-media-list custom-image-list mui-row";
				imageList.options = $.extend($.ImageListOptionsDefaults, options);
				if(imageList.options.size == 1) {
					imageList.options.multiple = false
				} else if(imageList.options.size > 1) {
					imageList.options.multiple = true;
				} else {
					mui.toast("参数错误！");
				}
				var addButtonHtml = '<button class="mui-icon mui-icon-image media-item button-item mui-col-"><h6>最多' + imageList.options.size + '张</h6></button>'
				imageList.innerHTML = addButtonHtml;
				imageList.addButton = imageList.querySelector(".button-item");
				imageList.addButton.addEventListener("tap", function() {
					var nowImageLength = imageList.querySelectorAll("img.file").length; //已经有的图片张数
					if(!checkImgNum(0, nowImageLength)) {
						return;
					}
					var btnArray = [{
						title: "拍照"
					}, {
						title: "从相册选择"
					}];
					plus.nativeUI.actionSheet({
						title: "选择照片",
						cancel: "取消",
						buttons: btnArray
					}, function(e) {
						var index = e.index;
						switch(index) {
							case 0:
								break;
							case 1:
								var cmr = plus.camera.getCamera();
								cmr.captureImage(function(path) {
									addImg(path);
								}, function(err) {});
								break;
							case 2:
								plus.gallery.pick(function(e) {
									if(imageList.options.multiple) {
										if(!checkImgNum(e.files.length, nowImageLength)) {
											return false;
										}
										for(var i in e.files) {
											plus.io.resolveLocalFileSystemURL(e.files[i], function(entry) {
												var localURL = entry.toLocalURL();
												addImg(localURL); //处理图片的地方
											}, function(e) {
												console.log("失败：" + error.message);
											}, {
												filename: "_doc/camera/",
												index: 1
											});
										}
									} else {
										plus.io.resolveLocalFileSystemURL(e, function(entry) {
											var localURL = entry.toLocalURL();
											addImg(localURL); //处理图片的地方
										}, function(e) {
											console.log("失败：" + error.message);
										}, {
											filename: "_doc/camera/",
											index: 1
										});
									}
								}, function(e) {
									mui.toast("取消选择图片");
								}, {
									filter: "image",
									multiple: imageList.options.multiple,
									maximum: imageList.options.size - nowImageLength
								});
								break;
						}
					});
				}, false);
				/**
				 * @param {Object} p 此次添加的个数
				 */
				function checkImgNum(p, i) {
					var all = i + p;
					if((p > 0 && all > imageList.options.size) || (p == 0 && all >= imageList.options.size)) {
						mui.toast("图片不能超过" + imageList.options.size + "张");

						return false;
					} else {
						return true;
					}
				}

				//添加选中图片到回复区域--begin
				function addImg(path) {
					//本地缓存路径
					var hb_path = '_downloads/image/' + md5(path) + '.jpg'; //HBuilder平台路径
					var sd_path = plus.io.convertLocalFileSystemURL(hb_path); //SD卡绝对路径
					console.log("选择的图片sd_path路径:" + sd_path);
					console.log("选择的图片path路径:" + path);

					plus.zip.compressImage({
							src: path,
							dst: sd_path,
							quality: 20,
							format: "jpg",
							overwrite: true
						},
						function() {

							var imageDiv = document.createElement("div");
							imageDiv.className = "media-item image-item mui-col-"
							imageDiv.innerHTML = '' +
								'<img class="file mui-action-preview" data-preview-src="' + sd_path + '" data-preview-group="1" src="' + sd_path + '" addTime="' + (new Date()).getTime() + '" />' +
								'<span class="mui-icon mui-icon-closeempty"></span>';
							imageList.appendChild(imageDiv);
						},
						function(error) {
							alert("图片处理失败!");
						});

				}
			})

			imageListAll.on("tap", ".media-item.image-item", function(e) {
				var $this = this;
				if(e.target.className.match(new RegExp('(\\s|^)mui-icon-closeempty(\\s|$)'))) {

					var $parentNode = $this.parentNode;
					var btnArray = ['是', '否'];
					mui.confirm('确定删除？', '删除', btnArray, function(e2) {
						if(e2.index == 0) {
							$parentNode.removeChild($this);
						}
					})
				}
			})
			if(!mui.getPreviewImage()) {
				mui.previewImage();
			}
		}
	}

	/**
	 * 录音列表，录音弹出框
	 */
	$.fn.audioListInit = function(options) {
		var audioListAll = this;
		if(audioListAll.length > 0) {
			//			<div class="rprogress">
			//				<div class="rschedule"><img src="${base }/img/audioLoading.gif" width="46px" height="46px" /></div>
			//				<div class="r-sigh">!</div>
			//				<div class="rsalert"></div>
			//			</div>
			var audioDiv = document.createElement("div");
			audioDiv.className = "rprogress";
			audioDiv.innerHTML = '<div class="rschedule"><img src="image/audioLoading.gif" width="46px" height="46px" /></div>' +
				'<div class="r-sigh">!</div><div class="rsalert"></div>';
			$("body")[0].appendChild(audioDiv);

			var recordCancel = false; //录音结束标志位
			var recorder = null; //录音对象
			var holdData = null; //按住的时间
			var releaseData = null; //放手的时间
			var stopTimer = null; //结束计时器
			var boxSoundAlert = $(".rprogress")[0];
			var audioTips = $(".rprogress .rsalert")[0];
			var setSoundAlertVisable = function(show) {
				if(show) {
					boxSoundAlert.style.display = 'block';
					boxSoundAlert.style.opacity = 1;
				} else {
					boxSoundAlert.style.opacity = 0;
					//fadeOut 完成再真正隐藏
					setTimeout(function() {
						boxSoundAlert.style.display = 'none';
					}, 200);
				}
			};

			//上滑取消事件绑定
			mui("body")[0].addEventListener('drag', function(event) {
				if(Math.abs(event.detail.deltaY) > 50) {
					if(!recordCancel) {
						recordCancel = true;
						if(!audioTips.classList.contains("cancel")) {
							audioTips.classList.add("cancel");
						}
						audioTips.innerHTML = "松开手指，取消录音";
					}
				} else {
					if(recordCancel) {
						recordCancel = false;
						if(audioTips.classList.contains("cancel")) {
							audioTips.classList.remove("cancel");
						}
						audioTips.innerHTML = "手指上划，取消录音";
					}
				}
			}, false);

			//点击播放录音
			function playSound(soundBtn) {
				var iconDiv = soundBtn.querySelector(".audio");
				var soundPath = soundBtn.getAttribute("soundPath");
				if(iconDiv.className.match(new RegExp('(\\s|^)playing(\\s|$)'))) {
					removeClass(iconDiv, "playing");
					player.stop();
				} else {
					if(soundPath) {
						var playingBtn = document.querySelector(".playing");
						if(playingBtn) {
							removeClass(playingBtn, " playing");
							player.stop();
						}
						player = plus.audio.createPlayer(soundPath);
						iconDiv.className += " playing";
						player.play(function() {
							removeClass(iconDiv, "playing");
						}, function(e) {});
					}
				}
			}

			audioListAll.each(function(e) {
				var audioList = this;
				audioList.className = "custom-media-list custom-audio-list";
				audioList.options = $.extend($.AudioListOptionsDefaults, options);
				var addButtonHtml = '<button class="mui-icon mui-icon-mic media-item button-item"><h6>最多' + audioList.options.size + '条</h6></button>'
				audioList.innerHTML = addButtonHtml;
				audioList.addButton = audioList.querySelector(".button-item");
				//添加录音到回复区域--begin
				audioList.addButton.addEventListener("hold", function() {
					var nowAudioLength = audioList.querySelectorAll(".audio-item .audio").length; //已经有的录音条数
					if(!checkAudioNum(nowAudioLength)) {
						return;
					}
					recordCancel = false;
					if(stopTimer) clearTimeout(stopTimer);
					audioTips.innerHTML = "手指上划，取消发送";
					boxSoundAlert.classList.remove('rprogress-sigh');
					setSoundAlertVisable(true);
					recorder = plus.audio.getRecorder();
					if(recorder == null) {
						plus.nativeUI.toast("不能获取录音对象");
						return;
					}
					holdData = new Date();
					recorder.record({
						filename: "_doc/audio/"
					}, function(path) {
						if(recordCancel) return;
						addSound(path, Math.round((releaseData - holdData) / 1000));
					}, function(e) {
						plus.nativeUI.toast("录音时出现异常: " + e.message);
					});
				}, false);

				audioList.addButton.addEventListener('release', function(event) {
					if(audioTips.classList.contains("cancel")) {
						audioTips.classList.remove("cancel");
						audioTips.innerHTML = "手指上划，取消录音";
					}
					releaseData = new Date();
					if(releaseData - holdData < 1000) {
						audioTips.innerHTML = "录音时间太短";
						boxSoundAlert.classList.add('rprogress-sigh');
						recordCancel = true;
						stopTimer = setTimeout(function() {
							setSoundAlertVisable(false);
						}, 800);
					} else {
						setSoundAlertVisable(false);
					}
					recorder.stop();
				}, false);
				audioList.addButton.addEventListener("touchstart", function(e) {
					e.preventDefault();
				});

				function checkAudioNum(nowAudioLength) {
					var size = audioList.options.size;
					if(nowAudioLength >= size) {
						mui.toast("录音不能超" + size + "条");
						return false;
					} else {
						return true;
					}
				}

				function addSound(path, duration) {
					//					<button type="button" class="media-item audio-item mui-row">
					//						<div class="audio playing mui-col-"><span></span><span></span><span></span></div>
					//						<div class="time mui-col-">60s</div>
					//						 <span class="mui-icon mui-icon-closeempty"></span>
					//					</button>
					var soundBtn = document.createElement("button");
					soundBtn.className = "media-item audio-item mui-row"
					soundBtn.setAttribute("soundPath", path);
					soundBtn.setAttribute("duration", duration);
					soundBtn.setAttribute("addTime", (new Date()).getTime());
					soundBtn.innerHTML = '<div class="audio mui-col-"><span></span><span></span><span></span></div>' +
						'<div class="time mui-col-">' + duration + 's</div>' +
						'<span class="mui-icon mui-icon-closeempty"></span>'
					soundBtn.addEventListener('tap', function() {
						//playSound(this);
					});
					audioList.appendChild(soundBtn);
				}
			})

			audioListAll.on("tap", ".media-item.audio-item", function(e) {
				var $this = this;
				if(e.target.className.match(new RegExp('(\\s|^)mui-icon-closeempty(\\s|$)'))) {
					var $parentNode = $this.parentNode;
					var btnArray = ['是', '否'];
					mui.confirm('确定删除？', '删除', btnArray, function(e2) {
						if(e2.index == 0) {
							$parentNode.removeChild($this);
						}
					})
				} else {
					playSound($this);
				}
			})
		}

	}

	$.ImageListOptionsDefaults = {
		size: 1
	};
	$.AudioListOptionsDefaults = {
		maxLength: 60, //未使用
		size: 1
	};
	/*$.ready(function() {
		$('.custom-media-list').imageListInit({
			size: 5
		});
		$('.custom-media-list').audioListInit();
		$('.custom-media-list').audioListInit({
			size: 5
		});
	});*/

	/**
	 * 获取当前的日期时间 格式yyyy-MM-dd HH:MM:SS
	 */
	function getNowFormatDate() {
		var date = new Date();
		var seperator1 = "-";
		var seperator2 = ":";
		var month = date.getMonth() + 1;
		var strDate = date.getDate();
		var strMin = date.getMinutes();
		var strSecond = date.getSeconds();
		if(month >= 1 && month <= 9) {
			month = "0" + month;
		}
		if(strDate >= 0 && strDate <= 9) {
			strDate = "0" + strDate;
		}
		if(strMin >= 0 && strMin <= 9) {
			strMin = "0" + strMin;
		}

		if(strSecond >= 0 && strSecond <= 9) {
			strSecond = "0" + strSecond;
		}
		var currentdate = date.getFullYear() + seperator1 + month + seperator1 + strDate +
			" " + date.getHours() + seperator2 + strMin +
			seperator2 + strSecond;
		return currentdate;
	}

}(mui, document, window));