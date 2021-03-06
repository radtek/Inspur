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
    	<title>灰名单到期确认</title>
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
		<style type="text/css">
			.table th,.table td{
				text-align:center
			}
			.table tbody tr td{
				vertical-align:middle
			}
		</style>
		
		<script type="text/javascript">
			var oTable = null;
			var conditions = [];
			conditions = [];
			$(document).ready(function(){
				oTable=$('#totalData').dataTable({					
					"bSortClasses":false,
					"aLengthMenu":[10,20,30],
					"bAutoWidth":true,
					"bSort":true,
					"bProcessing":true,
					"bServerSide":true,
					"bFilter":false,
					"bLengthChange":true,
					"sPaginationType":"full_numbers",
					"bStateSave":false,
					"bScrollCollapse":true,
					"sScrollX":"100%",
					"aoColumns":[{
						"mData":"id",
						"sWidth":"10%",
						"mRender": function ( data, type, full ) {
	        				return "<input type=\"checkbox\" name=\"checkId\" value=\""+data+"\"/>";}
					},{
						"mData":"city",
						"sWidth":"10%"
					},{
						"mData":"saCode",
						"sWidth":"10%"
					},{
						"mData":"glType",
						"sWidth":"10%"
					},{
						"mData":"glDescribe",
						"sWidth":"10%"
					},{
						"mData":"originator",
						"sWidth":"10%"
					},{
						"mData":"glsTime",
						"sWidth":"10%"
					},{
						"mData":"validTime",
						"sWidth":"10%"
					},{
						"mData":"expireTime",
						"sWidth":"10%"
					},{
						"mData":"expireStatus",
						"sWidth":"10%",									 
					}],
					"aoColumnDefs":[] ,
					"iDisplayLength":10,
					"sDom":"<'top'>frt<'bottom'ilp><'clear'>",
					"aaSorting":[[0,"desc"]],
					"fnRowCallback":function(nRow,aData,iDisplayIndex){
						//$("td:eq(0)",nRow).html("");
					},
					"fnServerData":function(sSource,aoData,fnCallback){						
						$.ajax({
							url:sSource,
							type:"POST",
							data:"tableparam="+JSON.stringify(aoData)+"&conditions="+JSON.stringify(conditions),
							dataType:"json",							
							success:function(data)
							{
								fnCallback(data);
							}
						});
					},
					"sAjaxSource":"${pageContext.request.contextPath}/greyListExpireAction/findGreyListOfExpireDate.ilf"
				});
			});	
			
			/*按条件查询*/
			function searchData(){
				var saName=$("#saName").val();
				
				if(saName!=null && saName!=""){
					conditions = [{
						name:"saName",
						value:saName
					}];	
				}
				var glType=$("#typeSelect").val();
				if(glType!=0 && glType!=null && glType!=""){
					conditions.push({
						name:"glType",
						value:glType
					});
				}
				oTable.fnDraw();
			}
			
			function dateExtend(){
				//暂时仅支持单条处理
				 obj = document.getElementsByName("checkId");
				    check_val = [];
				    for(k in obj){
				        if(obj[k].checked)
				            check_val.push(obj[k].value);
				    }
				 //alert(check_val[0]);
				
				//续期的数据直接跳转到新建页面
				var url="jsp/firstIndex/data/greyList/greyListExpireEdit.jsp?ID="+check_val[0];
				url=encodeURI(url);
				window.parent.parent.turnToJsp("灰名单续期填写",url);
			}
			
		   function exportExcel(){			   
		   	var saName=$("#saName").val();							
		  	var glType=$("#typeSelect").val();
			
		    window.location.href="${pageContext.request.contextPath}/greyListExpireAction/exportExcel.ilf?saName="+encodeURI(encodeURI(saName))
		    		+"&glType="+glType;
	       }
		   
		   function turnToGreyListDetail(){
				 var num=$("input:checkbox:checked").val();
			
					url = encodeURI("jsp/firstIndex/data/greyList/greyListRemoveDataDatail.jsp?ID="+num);
					window.parent.turnToJsp("灰名单列表详情",url);
			}
		</script>
  	</head>
  	<body style="width:100%;height:100%;border:solid 0px red;" id="bodyHeight">
  	<div class="container" style="width:100%;height:100%;margin-top:-20px;">
		<div class="panel panel-primary"  id="mainPanel">
			<div id="panelHeading" style="width:100%;height:50px;background-color:#337ab7;display:table;padding-left:10px">
				<div style="background-color:#337ab7;display:table-cell;vertical-align:middle;padding-left:10px">
					<font color="white" size="5">灰名单到期确认</font>
				</div>
			</div>
			
			<div class="panel-body">
				<div style="width:100%;height:50px;margin-top:10px;">
					<div class="col-xs-2 col-sm-2 col-md-2" style="text-align:center;">
						<div style="display:inline;" id="">
							<input type="text" id="saName" placeholder="输入站址名称" style="border-radius:7px;height:35px;"/>
						</div>
					</div>
					<div class="col-xs-2 col-sm-2 col-md-2" style="text-align:center;">
						<select id="typeSelect" style="border-radius:7px;height:35px;">
							<option value="0">选择灰名单类型</option>
							<option>111</option>
							<option>222</option>
							<option>333</option>
							<option>444</option>
						</select>
					</div>
					<div class="col-xs-2 col-sm-2 col-md-2" style="text-align:center;">
						<a class="btn btn-info" style="cursor:pointer;margin-bottom:10px;" onclick="javascript:searchData();">
								<span class="icon-search icon-white"></span>查询
						</a>
					</div>
					<div class="col-xs-6 col-sm-6 col-md-6" style="text-align:right;">
						<div style="text-align:right;display:inline;margin-right:30px;">
							<button class="btn btn-success" style="width:100px;" onclick="dateExtend()">续期</button>
							<button class="btn btn-danger" style="width:100px;" onclick="turnToGreyListDetail()">解除</button>
							<button class="btn btn-warning" style="width:100px;" onclick="exportExcel()">导出</button>
						</div>
					</div>
				</div>
				<div style="width:100%;">
					<HR style="FILTER: alpha(opacity=100,finishopacity=0,style=2)" width="100%" color=#8F8F8F SIZE=10>
				</div>
			 	<div class="col-xs-12 col-sm-12 col-md-12"
					style="margin-top:0px">
					<table id="totalData" class="table table-bordered table-hover display"
						style='vertical-align: middle; text-align: center;margin-top:5px;'>
						<div style="width:100%;height:6px"></div>
						<thead>
							<tr id="first_th">					   						    					    
								<th>选择</th>
								<th>地市</th>
								<th>站址编号</th>
								<th>灰名单类型</th>
								<th>灰名单说明</th>
								<th>发起人</th>
								<th>进入灰名单时间</th>
								<th>灰名单有效期</th>
								<th>到期时间</th>
								<th>是否到期</th>
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