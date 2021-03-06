<%@ page language="java" import="java.util.*" pageEncoding="UTF-8"%>
<%
	String path = request.getContextPath();
	String basePath = request.getScheme()+"://"+request.getServerName()+":"+request.getServerPort()+path+"/";
	String frameHeight = request.getParameter("frameHeight");
%>
<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN">
<html>
	<head>
   		<meta name="viewport" content="width=device-width, initial-scale=1">
    	<base href="<%=basePath%>">
    	<title>灰名单查询</title>
    	<link rel="stylesheet" type="text/css" href="${pageContext.request.contextPath}/css/bootstrap.css"></link>
    	<link rel="stylesheet" type="text/css" href="${pageContext.request.contextPath}/css/todc-bootstrap.css">
    	<link rel="stylesheet" type="text/css" href="${pageContext.request.contextPath}/css/content.css">
		<link rel="stylesheet" type="text/css" href="${pageContext.request.contextPath}/extensions/EasyUI/icon.css">
		<link rel="stylesheet" type="text/css" href="${pageContext.request.contextPath}/extensions/jQueryUI/css/jquery-ui.css"></link>
		<link rel="stylesheet" type="text/css" href="${pageContext.request.contextPath}/extensions/datatables/css/jquery.dataTables.css"></link>
		<link rel="stylesheet" type="text/css" href="${pageContext.request.contextPath}/extensions/xcConfirm/css/xcConfirm.css">
		<link rel="stylesheet" type="text/css" href="${pageContext.request.contextPath}/extensions/zTree/css/zTreeStyle.css">	
		<link rel="stylesheet" type="text/css" href="${pageContext.request.contextPath}/extensions/topMenus/css/font-awesome.css">
		<link rel="stylesheet" type="text/css" href="${pageContext.request.contextPath}/extensions/topMenus/css/style.css">
		<script type="text/javascript" src="${pageContext.request.contextPath}/js/jquery.js"></script>
		<script type="text/javascript" src="${pageContext.request.contextPath}/js/json2.js"></script>
		<script type="text/javascript" src="${pageContext.request.contextPath}/extensions/jQueryUI/jquery-ui.js"></script>
		<script type="text/javascript" src="${pageContext.request.contextPath}/extensions/datatables/js/jquery.dataTables.js"></script>		
		<script type="text/javascript" src="${pageContext.request.contextPath}/extensions/xcConfirm/js/xcConfirm.js"></script>
		<script type="text/javascript" src="${pageContext.request.contextPath}/extensions/zTree/js/jquery.ztree.core.js"></script>
		<script type="text/javascript" src="${pageContext.request.contextPath}/extensions/zTree/js/jquery.ztree.excheck.js"></script>
		<script type="text/javascript" src="${pageContext.request.contextPath}/extensions/my97datepicker/WdatePicker.js"></script>
		<script type="text/javascript" src="${pageContext.request.contextPath}/js/plugins/checkboxes.js"></script>
		<link href="https://cdn.bootcss.com/bootstrap/3.3.7/css/bootstrap.min.css" rel="stylesheet">
		
		<script type="text/javascript">
			
		</script>
		<style type="text/css">
			.table th,.table td{
				text-align:center
			}
			.table tbody tr td{
				vertical-align:middle
			}
		</style>
  	</head>
  	<body style="width:100%;height:100%;border:solid 0px red;" id="bodyHeight">
  	<div class="container" style="width:100%;height:100%;margin-top:-20px;">
		<div class="panel panel-primary"  id="mainPanel">
			<div id="panelHeading" style="width:100%;height:50px;background-color:#337ab7;display:table;padding-left:10px">
				<div style="background-color:#337ab7;display:table-cell;vertical-align:middle;padding-left:10px">
					<font color="white" size="5">灰名单查询</font>
				</div>
			</div>
			
			<div class="panel-body">
				<div style="width:100%;height:70px;margin-top:10px;">
					<div class="col-xs-6 col-sm-6 col-md-6" style="text-align:left;width:40%;height:70px;margin-left:40px;">
						<font>地&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;市:</font>
						<select style="border-radius:7px;height:35px;width:220px;">
							<option>选择地市</option>
							<option>456</option>
							<option>789</option>
						</select>
					</div>
					<div class="col-xs-6 col-sm-6 col-md-6" style="text-align:left;width:40%;height:70px;">
						<font>灰名单类型:</font>
						<select style="border-radius:7px;height:35px;width:220px;">
							<option>选择灰名单类型</option>
							<option>456</option>
							<option>789</option>
						</select>
					</div>
					<br>
					<div class="col-xs-6 col-sm-6 col-md-6" style="text-align:left;width:40%;margin-left:40px;">
						<div style="display:inline;" id="">
							<font>站址名称:</font>
							<input type="text"  placeholder="输入站址名称" style="border-radius:7px;height:35px;width:220px;"/>
						</div>
					</div>
					<div class="col-xs-6 col-sm-6 col-md-6" style="text-align:center;">
						<div style="text-align:right;display:inline;">
							<button class="btn btn-danger" style="width:100px;">查询</button>
							<button class="btn btn-warning" style="width:100px;">导出</button>
						</div>
					</div>
				</div>
			 	<div class="col-xs-12 col-sm-12 col-md-12"
					style="margin-top:0px">
					<table id="totalData" class="table table-bordered table-hover display"
						style='vertical-align: middle; text-align: center;margin-top:5px;'>
						<div style="width:100%;height:6px"></div>
						<thead>
							<tr>
								<th>序号</th>
								<th>地市</th>
								<th>站址编号</th>
								<th>灰名单类型</th>
								<th>灰名单说明</th>
								<th>发起人</th>
								<th>进入灰名单时间</th>
								<th>灰名单有效期</th>
							</tr>
						</thead>
						<tbody>
							
						</tbody>
					</table>
				</div>
			</div>
		</div>
	</div>	
	</body>
</html>