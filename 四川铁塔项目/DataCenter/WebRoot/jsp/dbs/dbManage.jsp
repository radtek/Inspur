<%@ page language="java" import="java.util.*" pageEncoding="UTF-8"%>
<%
	String path = request.getContextPath();
	String basePath = request.getScheme()+"://"+request.getServerName()+":"+request.getServerPort()+path+"/";
	String frameHeight = request.getParameter("frameHeight");
%>
<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN">
<html>
	<head>
    	<base href="<%=basePath%>">
    	<title>数据源管理</title>
    	<link rel="stylesheet" type="text/css" href="${pageContext.request.contextPath}/css/bootstrap.css"></link>
    	<link rel="stylesheet" type="text/css" href="${pageContext.request.contextPath}/css/todc-bootstrap.css">
    	<link rel="stylesheet" type="text/css" href="${pageContext.request.contextPath}/css/content.css">
		<link rel="stylesheet" type="text/css" href="${pageContext.request.contextPath}/extensions/EasyUI/icon.css">
		<link rel="stylesheet" type="text/css" href="${pageContext.request.contextPath}/extensions/jQueryUI/css/jquery-ui.css"></link>
		<link rel="stylesheet" type="text/css" href="${pageContext.request.contextPath}/extensions/datatables/css/jquery.dataTables.css"></link>
		<link rel="stylesheet" type="text/css" href="${pageContext.request.contextPath}/extensions/xcConfirm/css/xcConfirm.css">
		<link rel="stylesheet" type="text/css" href="${pageContext.request.contextPath}/extensions/zTree/css/zTreeStyle.css">	
		<link rel="stylesheet" type="text/css" href="${pageContext.request.contextPath}/extensions/topMenus/css/font-awesome.css"/>
		<link rel="stylesheet" type="text/css" href="${pageContext.request.contextPath}/extensions/topMenus/css/style.css"/>
		<script type="text/javascript" src="${pageContext.request.contextPath}/js/jquery.js"></script>
		<script type="text/javascript" src="${pageContext.request.contextPath}/js/json2.js"></script>
		<script type="text/javascript" src="${pageContext.request.contextPath}/js/HashMap.js"></script>
		<script type="text/javascript" src="${pageContext.request.contextPath}/extensions/jQueryUI/jquery-ui.js"></script>
		<script type="text/javascript" src="${pageContext.request.contextPath}/extensions/datatables/js/jquery.dataTables.js"></script>		
		<script type="text/javascript" src="${pageContext.request.contextPath}/extensions/xcConfirm/js/xcConfirm.js"></script>
		<script type="text/javascript" src="${pageContext.request.contextPath}/extensions/selection/jquery.fancyspinbox.js"></script>
		<script type="text/javascript" src="${pageContext.request.contextPath}/extensions/zTree/js/jquery.ztree.core.js"></script>
		<script type="text/javascript" src="${pageContext.request.contextPath}/extensions/zTree/js/jquery.ztree.excheck.js"></script>
		<%--引入已封装的工具js--%>
		<script type="text/javascript" src="${pageContext.request.contextPath}/js/plugins/checkboxes.js"></script>
		<style type="text/css">
			thead th{
				text-align:center;
			}
		</style>
		<script type="text/javascript">
			var acheItems = null;
			var oTable = null;
			var conditions = [];
			$(document).ready(function(){
				/*初始化界面高度*/
				$("#bodyHeight").css({
			    	"height":$("#hiddenOfHeight").val()
			    });
			    /*表格高度及容量*/
			    var tableHeight = parseInt($("#hiddenOfHeight").val())-$("#panelHeading").height()-75;
				var pageNumbers = parseInt(tableHeight/30);
				oTable = $("#dataTable").dataTable({
					"bSortClasses":false,
					"aLengthMenu":[10,20,30],
					"bAutoWidth":true,
					"bSort":true,
					"bProcessing":true,
					"bServerSide":true,
					"bFilter":false,
					"bLengthChange":false,
					"sPaginationType":"full_numbers",
					"bStateSave":false,
					"bScrollCollapse":true,
					"sScrollY":tableHeight+"px",
					"sScrollX":"100%",
					"aoColumns":[{
						"mData":"ID",
						"sWidth":"6%"
					},{
						"mData":"DB_NAME",
						"sWidth":"16.5%"
					},{
						"mData":"DB_TYPE",
						"sWidth":"10%"
					},{
						"mData":"IP_ADDRESS",
						"sWidth":"16%"
					},{
						"mData":"PORT",
						"sWidth":"16%"
					},{
						"mData":"SID",
						"sWidth":"11%"
					},{
						"mData":"USER_NAME",
						"sWidth":"13%"
					},{
						"mData":"USER_PASS",
						"bVisible":false
					},{
						"mData":"USE_STATE",
						"sWidth":"10.1%"
					}],
					"aoColumnDefs":[],
					"iDisplayLength":pageNumbers,
					"sDom":"<'top'>frt<'bottom'ilp><'clear'>",
					"aaSorting":[[0,"desc"]],
					"fnRowCallback":function(nRow,aData,iDisplayIndex){
						if(acheItems==null){
							acheItems = new HashMap();
						}
						acheItems.put(aData["ID"],aData);
						$("td:eq(0)",nRow).html("<center><input type='checkbox' value='"+aData["ID"]+"' name='checksRadio' onchange='javascript:uniqueCheckbox(this.checked,this.name,this.value);'></input></center>");
						if(aData["USE_STATE"]=="Y"){
							$("td:eq(7)",nRow).html("<font color='green'>在用</font>");	
						}else{
							$("td:eq(7)",nRow).html("<font color='red'>停用</font>");
						}
					},
					"fnServerData":getData,
					"sAjaxSource":"${pageContext.request.contextPath}/basicDbAction/findItems.ilf"
				});
				$("#dataTable tbody").click(function(event){
					$(oTable.fnSettings().aoData).each(function(){
						$(this.nTr).removeClass("row_selected");
					});
					if($(event.target.parentNode).attr("class")=="even" || $(event.target.parentNode).attr("class")=="odd"){
						$(event.target.parentNode).addClass("row_selected");
					}
				});
			});
			function getData(sSource,aoData,fnCallback){
				$.ajax({
					url:sSource,
					type:"POST",
					data:"tableparam="+JSON.stringify(aoData)+"&conditions="+JSON.stringify(conditions),
					dataType:"json",		
					success:function(data){
						fnCallback(data);
					}
				});
			};
			function searchData(){
				var dbName = $("#dbNameKeyInput").val();
				if(dbName!=""){
					conditions = [{ 
						name:"DB_NAME",
						value:dbName
					}];	
				}else{
					conditions = [];
				}
				oTable.fnDraw();
			}
			function initIfChecked(){
				document.getElementById("editForm").reset();
				var checkedValue = getCheckedValue("checksRadio");
				if(checkedValue!=null && acheItems!=null){
					var checkedObj = acheItems.get(checkedValue);
					if(checkedObj!=null){
						$("#hiddenOfCode").val(checkedObj["ID"]);
						$("#dbTypeSelection").val(checkedObj["DB_TYPE"]);
						$("#dbNameInput").val(checkedObj["DB_NAME"]);
						$("#ipAddrInput").val(checkedObj["IP_ADDRESS"]);
						$("#dbPortInput").val(checkedObj["PORT"]);
						$("#sidKeyInput").val(checkedObj["SID"]);
						$("#userNameInput").val(checkedObj["USER_NAME"]);
						$("#userPassInput").val(checkedObj["USER_PASS"]);
					}
				}
			}
			function validInputs(){
				var dbName = $("#dbNameInput").val();
				if(dbName==""){
					window.wxc.xcConfirm("数据源名称不能为空.",window.wxc.xcConfirm.typeEnum.info);
					return false;
				}
				var ipAddress = $("#ipAddrInput").val();
				if(ipAddress==""){
					window.wxc.xcConfirm("I.P.地址不能为空.",window.wxc.xcConfirm.typeEnum.info);
					return false;
				}
				var portName = $("#dbPortInput").val();
				if(portName==""){
					window.wxc.xcConfirm("端口号不能为空.",window.wxc.xcConfirm.typeEnum.info);
					return false;
				}
				var sidName = $("#sidKeyInput").val();
				if(sidName==""){
					window.wxc.xcConfirm("S.I.D不能为空.",window.wxc.xcConfirm.typeEnum.info);
					return false;
				}
				var userName = $("#userNameInput").val();
				if(userName==""){
					window.wxc.xcConfirm("登录用户名不能为空.",window.wxc.xcConfirm.typeEnum.info);
					return false;
				}
				var passWord = $("#userPassInput").val();
				if(passWord==""){
					window.wxc.xcConfirm("登录密码不能为空.",window.wxc.xcConfirm.typeEnum.info);
					return false;
				}
				return true;
			}
			/*
				根据配置测试数据源是否可用
			*/
			function doTest(){
				if(validInputs()){
					var requestParams = {
						dbType:$("#dbTypeSelection").val(),
						ipAddress:$("#ipAddrInput").val(),
						portName:$("#dbPortInput").val(),
						sidName:$("#sidKeyInput").val(),
						userName:$("#userNameInput").val(),
						passWord:$("#userPassInput").val()
					};
					$.ajax({
						url:"${pageContext.request.contextPath}/basicDbAction/testConnect.ilf",
						async:false,
						type:"POST",
						data:"params="+JSON.stringify(requestParams),
						dataType:"json",
						timeout:10000, 
						success:function(textStatus){
							if(textStatus["success"]){
								window.wxc.xcConfirm("测试成功.已成功连接至数据库.",window.wxc.xcConfirm.typeEnum.info);
							}else{
								window.wxc.xcConfirm(textStatus["message"],window.wxc.xcConfirm.typeEnum.error);
							}
						},
						error:function(){
							window.wxc.xcConfirm("测试失败.",window.wxc.xcConfirm.typeEnum.error);
						}
					});
				}
			}
			/*
				保存数据源
			*/
			function doSave(){
				if(validInputs()){
					var requestParams = {
						id:$("#hiddenOfCode").val(),
						dbType:$("#dbTypeSelection").val(),
						dbName:$("#dbNameInput").val(),
						ipAddress:$("#ipAddrInput").val(),
						portName:$("#dbPortInput").val(),
						sidName:$("#sidKeyInput").val(),
						userName:$("#userNameInput").val(),
						passWord:$("#userPassInput").val()
					};
					$.ajax({
						url:"${pageContext.request.contextPath}/basicDbAction/saveAudit.ilf",
						async:false,
						type:"POST",
						data:"params="+JSON.stringify(requestParams),
						dataType:"json",
						timeout:10000, 
						success:function(textStatus){
							if(textStatus["success"]){
								document.getElementById("editForm").reset();
								document.getElementById("canelButton").click();
								window.wxc.xcConfirm("数据源信息保存成功.",window.wxc.xcConfirm.typeEnum.info);
								searchData();
							}else{
								window.wxc.xcConfirm("数据源信息保存失败.",window.wxc.xcConfirm.typeEnum.error);
							}
						},
						error:function(){
							window.wxc.xcConfirm("数据源信息保存失败.",window.wxc.xcConfirm.typeEnum.error);
						}
					});
				}
			}
			/*
				删除数据源
			*/
			function deleteDb(){				
				var checkedValue = getCheckedValue("checksRadio");
				if(checkedValue!=null){
					window.wxc.xcConfirm("删除数据源会同时删除其包含的数据模型.","custom",{
						title:"警告",
						btn:parseInt("0011",2),
						onOk:function(){
							$.ajax({
								url:"${pageContext.request.contextPath}/basicDbAction/deleteItem.ilf",
								async:false,
								type:"POST",
								data:"itemCode="+checkedValue,
								dataType:"json",
								timeout:10000, 
								success:function(textStatus){
									if(textStatus["success"]){
										window.wxc.xcConfirm("数据源信息删除成功.",window.wxc.xcConfirm.typeEnum.info);
										searchData();
									}else{
										window.wxc.xcConfirm("数据源信息删除失败.",window.wxc.xcConfirm.typeEnum.error);
									}
								},
								error:function(){
									window.wxc.xcConfirm("数据源信息删除失败.",window.wxc.xcConfirm.typeEnum.error);
								}
							});
						}
					});	
				}else{
					window.wxc.xcConfirm("请先选择一个数据源.",window.wxc.xcConfirm.typeEnum.info);
				}
			}
			/*
				修改数据源状态
			*/
			function changeState(stateCode){			
				var checkedValue = getCheckedValue("checksRadio");
				if(checkedValue!=null){
					var isUsing = "Y";
					if(stateCode==1){
						isUsing = "N";
					}
					window.wxc.xcConfirm("是否确认修改此数据库的状态？.","custom",{
						title:"警告",
						btn:parseInt("0011",2),
						onOk:function(){
							$.ajax({
								url:"${pageContext.request.contextPath}/basicDbAction/updateState.ilf",
								async:false,
								type:"POST",
								data:"isUsing="+isUsing+"&checkedCode="+checkedValue,
								dataType:"json",
								timeout:10000, 
								success:function(textStatus){
									if(textStatus["success"]){
										window.wxc.xcConfirm("状态修改成功.",window.wxc.xcConfirm.typeEnum.info);
										searchData();
									}else{
										window.wxc.xcConfirm("状态修改失败.",window.wxc.xcConfirm.typeEnum.error);
									}
								},
								error:function(){
									window.wxc.xcConfirm("状态修改失败.",window.wxc.xcConfirm.typeEnum.error);
								}
							});
						}
					});		
				}else{
					window.wxc.xcConfirm("请先选择一个数据源.",window.wxc.xcConfirm.typeEnum.info);
				}
			}
		</script>
  	</head>
  	<body style="width:100%;border:solid 1px #FFF;" id="bodyHeight">
  		<input type="hidden" id="hiddenOfHeight" value="<%=frameHeight %>"></input>
		<div class="panel panel-default" style="margin-top:-20px;border:0px;">
			<div class="panel-heading" id="panelHeading">
				<span class="panel-label"></span>&nbsp;&nbsp;数据浏览&nbsp;&nbsp;>>&nbsp;&nbsp;数据源
				<form class="form-search pull-right">
					<div style="float:right;">
						<input type="text" placeholder="请输入数据源名称" style="border-radius:6px;width:160px;height:29px;font-size:12px;border:solid 1px #A3D0E3;" id="dbNameKeyInput"></input>
						<img src="${pageContext.request.contextPath}/img/icon/icon-Search.png" style='height:22px;width:22px;cursor:pointer;margin-left:5px;' onclick="javascript:searchData();"></img>
						<a class="btn btn-success" href="#myModal" role="button" data-toggle="modal" style="cursor:pointer;margin-left:10px;" onclick="javascript:initIfChecked();">
							<i class="icon-plus-sign icon-white"></i>添加 / 修改
						</a>
						<a class="btn btn-warning"  style="cursor:pointer" onclick="javascript:changeState(1);">
							<i class="icon-zoom-out icon-white"></i>停用
						</a>
						<a class="btn btn-info"  style="cursor:pointer" onclick="javascript:changeState(2);">
							<i class="icon-film icon-white"></i>启用
						</a>
						<a class="btn btn-danger"  style="cursor:pointer" onclick="javascript:deleteDb();">
							<i class="icon-trash icon-white"></i>删除数据源
						</a>	
					</div>
				</form>
			</div>
			<div class="panel-body" style="border:0px;">
				<div class="panlecontent container4 clearfix">
					<div class="div_scroll">
						<div style="height:auto;width:auto">
							<table cellpadding="0" cellspacing="0" border="0" id="dataTable" style="border:0px;">
								<thead>
									<tr>
										<th>&nbsp;</th>
										<th>数据源名称</th>
										<th>数据源类型</th>
										<th>I.P.</th>
										<th>PORT</th>
										<th>SID</th>
										<th>连接账户</th>									
										<th>连接密码</th>
										<th>状态</th>
									</tr>
								</thead>
								<tbody></tbody>
							</table>					
						</div>
					</div>
				</div>
			</div>
		</div>
		<%--窗口表单：开始--%>
		<div id="myModal" class="modal hide fade" style="width:650px;height:400px;margin-left:-300px;" tabindex="-1" role="dialog" aria-labelledby="myModalLabel" aria-hidden="true">
			<div class="modal-header">
				<button type="button" class="close" data-dismiss="modal" aria-hidden="true">×</button>
				<h3>+ 数据源信息维护</h3>
			</div>
			<div class="modal-body" style="max-height:280px;">
				<form class="l-group" id="editForm">
					<input type="hidden" value="" id="hiddenOfCode"></input>
					<table class="table table-bordered table-hover">
						<tr>
							<td><label class="l-name">数据库类型.</label></td>
							<td>
								<select class="w500" id="dbTypeSelection">
									<option value="Oracle">Oracle</option>
								</select>
							</td>
						</tr>
						<tr>
							<td><label class="l-name">数据源名称.</label></td>
							<td><input id="dbNameInput" type="text" placeholder="请输入数据源名称." style="width:500px;height:30px;margin-top:5px;"></input></td>
						</tr>
						<tr>
							<td style="text-align:center;"><label class="l-name">I.P.</label></td>
							<td><input id="ipAddrInput" type="text" placeholder="请输入数据源I.P地址." style="width:500px;height:30px;margin-top:5px;"></input></td>
						</tr>
						<tr>
							<td style="text-align:center;"><label class="l-name">Port.</label></td>
							<td><input id="dbPortInput" type="text" placeholder="请输入数据源Port端口." style="width:500px;height:30px;margin-top:5px;"></input></td>
						</tr>
						<tr>
							<td style="text-align:center;"><label class="l-name">SID.</label></td>
							<td><input id="sidKeyInput" type="text" placeholder="请输入数据库SID." style="width:500px;height:30px;margin-top:5px;"></input></td>
						</tr>
						<tr>
							<td style="text-align:center;"><label class="l-name">用户名.</label></td>
							<td><input id="userNameInput" type="text" placeholder="请输入连接用户名." style="width:500px;height:30px;margin-top:5px;"></input></td>
						</tr>
						<tr>
							<td style="text-align:center;"><label class="l-name">密码.</label></td>
							<td><input id="userPassInput" type="text" placeholder="请输入连接密码." style="width:500px;height:30px;margin-top:5px;"></input></td>
						</tr>
					</table>
				</form>
			</div>
			<div class="modal-footer tc">
				<button class="btn btn-primary" style="cursor:pointer;" onclick="javascript:doSave();">
					<i class="icon-envelope icon-white"></i>保存配置
				</button>
				<button class="btn btn-success" style="cursor:pointer;" onclick="javascript:doTest();">
					<i class="icon-film icon-white"></i>配置测试
				</button>
				<button class="btn btn-danger" data-dismiss="modal" aria-hidden="true" style="cursor:pointer;" id="canelButton">
					<i class="icon-zoom-out icon-white"></i>取消
				</button>
			</div>
		</div>
	</body>
	<script type="text/javascript" src="${pageContext.request.contextPath}/js/bootstrap.js"></script>
	<script type="text/javascript" src="${pageContext.request.contextPath}/js/jquery.slimscroll.min.js"></script>
	<script type="text/javascript" src="${pageContext.request.contextPath}/js/mousewheel.js"></script>
	<script type="text/javascript" src="${pageContext.request.contextPath}/js/easyscroll.js"></script>
	<script type="text/javascript" src="${pageContext.request.contextPath}/js/plugins.js"></script>
	<script type="text/javascript" src="${pageContext.request.contextPath}/js/jquery.easyui.min.js"></script>
</html>